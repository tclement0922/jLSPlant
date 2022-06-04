/*
 * This file is part of jLSPlant.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>
 *
 * Copyright (C) 2019-2020 Swift Gan <https://github.com/ganyao114>
 * Copyright (C) 2020-2021 canyie <https://github.com/canyie>
 * Copyright (C) 2022 T. Clément <https://github.com/tclement0922>
 */

#include "elf_img.h"

#include <malloc.h>
#include <cstring>
#include <sys/mman.h>
#include <unistd.h>
#include <type_traits>
#include <link.h>
#include "io_wrapper.h"
#include "../logging/logging.h"
#include "macros.h"

using namespace pine;

template<typename T>
inline constexpr auto offsetOf(Elf_Ehdr *head, Elf_Off off) {
    return reinterpret_cast<std::conditional_t<std::is_pointer_v<T>, T, T *>>(
            reinterpret_cast<uintptr_t>(head) + off);
}

inline bool CanRead(const char* file) {
    return access(file, R_OK) == 0;
}

void ElfImg::Open(const char* path) {
    //load elf
    int fd = WrappedOpen(path, O_RDONLY | O_CLOEXEC);
    if (UNLIKELY(fd == -1)) {
        LOGE("failed to open %s", path);
        return;
    }

    size = lseek(fd, 0, SEEK_END);
    if (UNLIKELY(size <= 0)) {
        LOGE("lseek() failed for %s: errno %d (%s)", path, errno, strerror(errno));
    }

    header = reinterpret_cast<Elf_Ehdr*>(mmap(nullptr, size, PROT_READ, MAP_SHARED, fd, 0));

    close(fd);
    section_header = reinterpret_cast<Elf_Shdr*>(((uintptr_t) header) + header->e_shoff);

    auto shoff = reinterpret_cast<uintptr_t>(section_header);
    char* section_str = reinterpret_cast<char*>(section_header[header->e_shstrndx].sh_offset +
                                                ((uintptr_t) header));

    for (int i = 0; i < header->e_shnum; i++, shoff += header->e_shentsize) {
        auto* section_h = (Elf_Shdr*) shoff;
        char* sname = section_h->sh_name + section_str;
        Elf_Off entsize = section_h->sh_entsize;
        switch (section_h->sh_type) {
            case SHT_DYNSYM:
                if (bias == -4396) {
                    dynsym = section_h;
                    dynsym_offset = section_h->sh_offset;
                    dynsym_size = section_h->sh_size;
                    dynsym_count = dynsym_size / entsize;
                    dynsym_start = reinterpret_cast<Elf_Sym*>(((uintptr_t) header) + dynsym_offset);
                }
                break;
            case SHT_SYMTAB:
                if (strcmp(sname, ".symtab") == 0) {
                    symtab_offset = section_h->sh_offset;
                    symtab_size = section_h->sh_size;
                    symtab_count = symtab_size / entsize;
                    symtab_start = reinterpret_cast<Elf_Sym*>(((uintptr_t) header) + symtab_offset);
                }
                break;
            case SHT_STRTAB:
                if (bias == -4396) {
                    strtab = section_h;
                    symstr_offset = section_h->sh_offset;
                    strtab_start = reinterpret_cast<Elf_Sym*>(((uintptr_t) header) + symstr_offset);
                }
                if (strcmp(sname, ".strtab") == 0) {
                    symstr_offset_for_symtab = section_h->sh_offset;
                }
                break;
            case SHT_PROGBITS:
                if (strtab == nullptr || dynsym == nullptr) break;
                if (bias == -4396) {
                    bias = (off_t) section_h->sh_addr - (off_t) section_h->sh_offset;
                }
                break;
            case SHT_HASH: {
                auto *d_un = offsetOf<Elf32_Word *>(header, section_h->sh_offset);
                nbucket_ = d_un[0];
                bucket_ = d_un + 2;
                chain_ = bucket_ + nbucket_;
                break;
            }
            case SHT_GNU_HASH: {
                auto *d_buf = reinterpret_cast<Elf_Addr *>(((size_t) header) +
                                                             section_h->sh_offset);
                gnu_nbucket_ = d_buf[0];
                gnu_symndx_ = d_buf[1];
                gnu_bloom_size_ = d_buf[2];
                gnu_shift2_ = d_buf[3];
                gnu_bloom_filter_ = reinterpret_cast<decltype(gnu_bloom_filter_)>(d_buf + 4);
                gnu_bucket_ = reinterpret_cast<decltype(gnu_bucket_)>(gnu_bloom_filter_ +
                                                                      gnu_bloom_size_);
                gnu_chain_ = gnu_bucket_ + gnu_nbucket_ - gnu_symndx_;
                break;
            }
        }
    }

    if (UNLIKELY(!symtab_offset)) {
        LOGW("can't find symtab from sections in %s\n", path);
    }

    //load module base
    base = getModuleBase(path);
}

void ElfImg::RelativeOpen(const char* mElf) {
    char mBuffer[64] = {0}; // We assume that the path length doesn't exceed 64 bytes.
    if (android_get_device_api_level() >= 29) {
        // Android R: com.android.art
        strcpy(mBuffer, kApexArtLibDir);
        strcat(mBuffer, mElf);
        if (CanRead(mBuffer)) {
            Open(mBuffer);
            return;
        }

        memset(mBuffer, 0, sizeof(mBuffer));

        // Android Q: com.android.runtime
        strcpy(mBuffer, kApexRuntimeLibDir);
        strcat(mBuffer, mElf);
        if (CanRead(mBuffer)) {
            Open(mBuffer);
            return;
        }

        memset(mBuffer, 0, sizeof(mBuffer));
    }
    strcpy(mBuffer, kSystemLibDir);
    strcat(mBuffer, mElf);
    Open(mBuffer);
}


ElfImg::~ElfImg() {
    //open elf file local
    /*
    if (buffer) {
        free(buffer);
        buffer = nullptr;
    }
     */
    //use mmap
    if (header) {
        munmap(header, size);
    }
}

Elf_Addr ElfImg::getSymbolOffset(const char* name, bool match_prefix) const {
    Elf_Addr _offset;

    //search dynmtab
    if (dynsym_start != nullptr && strtab_start != nullptr) {
        Elf_Sym* sym = dynsym_start;
        char* strings = (char*) strtab_start;
        int k;
        for (k = 0; k < dynsym_count; k++, sym++) {
            char* s = strings + sym->st_name;
            if (strcmp(s, name) == 0 || (match_prefix && strncmp(s, name, strlen(name)) == 0)) {
                _offset = sym->st_value;
                return _offset;
            }
        }
    }

    //search symtab
    if (symtab_start != nullptr && symstr_offset_for_symtab != 0) {
        for (int i = 0; i < symtab_count; i++) {
            unsigned int st_type = ELF_ST_TYPE(symtab_start[i].st_info);
            char* st_name = reinterpret_cast<char*>(((uintptr_t) header) +
                                                    symstr_offset_for_symtab +
                                                    symtab_start[i].st_name);
            if (st_type == STT_FUNC && symtab_start[i].st_size) {
                if (strcmp(st_name, name) == 0 || (match_prefix && strncmp(st_name, name, strlen(name)) == 0)) {
                    _offset = symtab_start[i].st_value;
                    return _offset;
                }
            }
        }
    }
    LOGE("Symbol %s not found in elf %s", name, elf);
    return 0;
}

void* ElfImg::getSymbolAddress(const char* name, bool match_prefix) const {
    Elf_Addr offset = getSymbolOffset(name, match_prefix);
    if (LIKELY(offset > 0 && base != nullptr)) {
        return reinterpret_cast<void*>((uintptr_t) base + offset - bias);
    } else {
        return nullptr;
    }
}

void* ElfImg::getModuleBase(const char* name) {
    FILE* maps;
    char buff[256];
    off_t load_addr;
    bool found = false;
    maps = WrappedFOpen("/proc/self/maps", "re");
    while (fgets(buff, sizeof(buff), maps)) {
        if (strstr(buff, name) && (strstr(buff, "r-xp") || strstr(buff, "r--p"))) {
            found = true;
            break;
        }
    }

    if (UNLIKELY(!found)) {
        LOGE("failed to read load address for %s", name);
        fclose(maps);
        return nullptr;
    }

    if (UNLIKELY(sscanf(buff, "%lx", &load_addr) != 1))
        LOGE("failed to read load address for %s", name);

    fclose(maps);

    LOGD("get module base %s: %lu", name, load_addr);

    return reinterpret_cast<void*>(load_addr);
}
