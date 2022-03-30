# Java-LSPlant

[![Android CI](https://github.com/tclement0922/Java-LSPlant/actions/workflows/android.yml/badge.svg?branch=main)](https://github.com/tclement0922/Java-LSPlant/actions/workflows/android.yml)

An unofficial Java implementation of the LSPlant hooking framework.

The aim of this library is providing a easier way to use the LSPlant framework, without the need to 
provide an ART symbol resolver and a inline hook framework.

### Support
Java-LSPlant has the same support as LSPlant:
> Support Android 5.0 - 13 (API level 21 - 33)
> 
> Support armeabi-v7a, arm64-v8a, x86, x86-64

Compatibility isn't guaranteed for every device. Some OEM system modifications, abis or something
else can break LSPlant, so if you plan to publish an app using this framework, do not forget to
support the case where `LSPlant.isInitialized()` returns false.

### Credits
 - [LSPlant](https://github.com/LSPosed/LSPlant) used as core framework:
   > LSPlant is an Android ART hook library, providing Java method hook/unhook and inline deoptimization.
   > Licensed under the [GNU Lesser General Public License v3.0](https://github.com/LSPosed/LSPlant/blob/master/LICENSE), Copyright (C) 2022 [LSPosed](https://github.com/LSPosed)
   
   Note: Parts of this library is inspired by official LSPlant tests.
 - [Dobby](https://github.com/jmpews/Dobby) used as inline hook framework:
   > Dobby is a lightweight, multi-platform, multi-architecture exploit hook framework.
   > Licensed under the [Apache License 2.0](https://github.com/jmpews/Dobby/blob/master/LICENSE), Copyright (C) [jmpews](https://github.com/jmpews)

 - [Pine](https://github.com/canyie/pine) for its elf image parser:
   > Pine is a dynamic java method hook framework on ART runtime, it can intercept almost all java method calls in this process.
   > Licensed under the [Anti 996 License v1.0](https://github.com/996icu/996.ICU/blob/master/LICENSE), Copyright (C) [canyie](https://github.com/canyie)
 
### License
Java-LSPlant is licensed under the [GNU Lesser General Public License v3.0](LICENSE), Copyright (C) 2022 T. Clément.
