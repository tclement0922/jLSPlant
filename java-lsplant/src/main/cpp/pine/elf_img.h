/*
 * This file is part of Java-LSPlant.
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

#ifndef PINE_ELF_IMG_H
#define PINE_ELF_IMG_H

#include <linux/elf.h>
#include <stdio.h>
#include "macros.h"

#if defined(__LP64__)
typedef Elf64_Ehdr Elf_Ehdr;
typedef Elf64_Shdr Elf_Shdr;
typedef Elf64_Addr Elf_Addr;
typedef Elf64_Dyn Elf_Dyn;
typedef Elf64_Rela Elf_Rela;
typedef Elf64_Sym Elf_Sym;
typedef Elf64_Off Elf_Off;

#define ELF_R_SYM(i) ELF64_R_SYM(i)
#else
typedef Elf32_Ehdr Elf_Ehdr;
typedef Elf32_Shdr Elf_Shdr;
typedef Elf32_Addr Elf_Addr;
typedef Elf32_Dyn Elf_Dyn;
typedef Elf32_Rel Elf_Rela;
typedef Elf32_Sym Elf_Sym;
typedef Elf32_Off Elf_Off;

#define ELF_R_SYM(i) ELF32_R_SYM(i)
#endif

namespace pine {
    class ElfImg {
    public:
        ElfImg(const char* elf) {
            this->elf = elf;
            if (elf[0] == '/') {
                Open(elf);
            } else {
                // Relative path
                RelativeOpen(elf);
            }
        }
        Elf_Addr getSymbolOffset(const char* name) const;
        void* getSymbolAddress(const char* name) const;

        ~ElfImg();

    private:
        void Open(const char* path);
        void RelativeOpen(const char* mElf);
        static void* getModuleBase(const char* name);

#ifdef __LP64__
        static constexpr const char* kSystemLibDir = "/system/lib64/";
        static constexpr const char* kApexRuntimeLibDir = "/apex/com.android.runtime/lib64/";
        static constexpr const char* kApexArtLibDir = "/apex/com.android.art/lib64/";
#else
        static constexpr const char* kSystemLibDir = "/system/lib/";
        static constexpr const char* kApexRuntimeLibDir = "/apex/com.android.runtime/lib/";
        static constexpr const char* kApexArtLibDir = "/apex/com.android.art/lib/";
#endif

        const char* elf = nullptr;
        void* base = nullptr;
        char* buffer = nullptr;
        off_t size = 0;
        off_t bias = -4396;
        Elf_Ehdr* header = nullptr;
        Elf_Shdr* section_header = nullptr;
        Elf_Shdr* symtab = nullptr;
        Elf_Shdr* strtab = nullptr;
        Elf_Shdr* dynsym = nullptr;
        Elf_Off dynsym_count = 0;
        Elf_Sym* symtab_start = nullptr;
        Elf_Sym* dynsym_start = nullptr;
        Elf_Sym* strtab_start = nullptr;
        Elf_Off symtab_count = 0;
        Elf_Off symstr_offset = 0;
        Elf_Off symstr_offset_for_symtab = 0;
        Elf_Off symtab_offset = 0;
        Elf_Off dynsym_offset = 0;
        Elf_Off symtab_size = 0;
        Elf_Off dynsym_size = 0;
    };
}

#endif //PINE_ELF_IMG_H
