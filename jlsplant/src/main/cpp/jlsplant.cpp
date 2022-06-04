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
 * Copyright (C) 2022 T. Clément <https://github.com/tclement0922>
 */

#include <jni.h>
#include <string>
#include <lsplant.hpp>
#include <dobby.h>
#include <sys/mman.h>
#include "logging/logging.h"
#include "pine/elf_img.h"
#include <jni.h>

// region from official LSPosed tests
#define uintval(p)              reinterpret_cast<uintptr_t>(p)
#define ptr(p)                  (reinterpret_cast<void *>(p))
#define align_up(x, n)          (((x) + ((n) - 1)) & ~((n) - 1))
#define align_down(x, n)        ((x) & -(n))
#define page_size               4096
#define page_align(n)           align_up(static_cast<uintptr_t>(n), page_size)
#define ptr_align(x)            ptr(align_down(reinterpret_cast<uintptr_t>(x), page_size))
#define make_rwx(p, n)          ::mprotect(ptr_align(p), \
                                            page_align(uintval(p) + (n)) != page_align(uintval(p)) \
                                                ? page_align(n) + page_size : page_align(n),       \
                                            PROT_READ | PROT_WRITE | PROT_EXEC)
// endregion

bool initialized;

extern "C"
JNIEXPORT jboolean JNICALL
Java_dev_tclement_jlsplant_Native_isNativeInitialized([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jclass clazz) {
    return initialized;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_dev_tclement_jlsplant_Native_hookMethod(JNIEnv *env, [[maybe_unused]] jclass clazz, jobject owner,
                                                          jobject original, jobject replacement) {
    return lsplant::Hook(env, original, owner, replacement);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_dev_tclement_jlsplant_Native_unhookMethod(JNIEnv *env, [[maybe_unused]] jclass clazz,
                                                            jobject target) {
    return lsplant::UnHook(env, target);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_dev_tclement_jlsplant_Native_deoptimize(JNIEnv *env, [[maybe_unused]] jclass clazz,
                                                            jobject target) {
    return lsplant::Deoptimize(env, target);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_dev_tclement_jlsplant_Native_isHooked(JNIEnv *env, [[maybe_unused]] jclass clazz,
                                                      jobject target) {
    return lsplant::IsHooked(env, target);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_dev_tclement_jlsplant_Native_makeClassInheritable(JNIEnv *env, [[maybe_unused]] jclass clazz,
                                                    jclass target) {
    return lsplant::MakeClassInheritable(env, target);
}

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM* vm, [[maybe_unused]] void* reserved) {
    JNIEnv* env;
    if (vm->GetEnv((void**) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    pine::ElfImg art("libart.so");
    lsplant::v2::InitInfo initInfo{
            .inline_hooker = [](void* target, void* hooker) -> void* {
                make_rwx(target, page_size);
                void* origin_call;
                if (DobbyHook(target, hooker, &origin_call) == RS_SUCCESS) {
                    return origin_call;
                } else {
                    return nullptr;
                }
            },
            .inline_unhooker = [](void* func) -> bool {
                return DobbyDestroy(func) == RT_SUCCESS;
            },
            .art_symbol_resolver = [&art](std::string_view symbol) -> void* {
                return art.getSymbolAddress(symbol.data());
            },
            .art_symbol_prefix_resolver = [&art](std::string_view symbol) -> void* {
                return art.getSymbolAddress(symbol.data(), true);
            }
    };
    initialized = lsplant::v2::Init(env, initInfo);
    if (initialized) {
        LOGI("LSPlant initialized");
    } else {
        LOGE("LSPlant initialization failed");
    }
    return JNI_VERSION_1_6;
}
