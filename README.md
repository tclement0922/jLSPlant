# jLSPlant

[![Android CI](https://github.com/tclement0922/jLSPlant/actions/workflows/android.yml/badge.svg?branch=main)](https://github.com/tclement0922/jLSPlant/actions/workflows/android.yml)

An unofficial Java implementation of the LSPlant hooking framework.

The aim of this library is providing a easier way to use the LSPlant framework, without the need to 
provide an ART symbol resolver and a inline hook framework.

### Support
jLSPlant has the same support as LSPlant:
> Support Android 5.0 - 13 (API level 21 - 33)
> 
> Support armeabi-v7a, arm64-v8a, x86, x86-64

Compatibility isn't guaranteed for every device. Some OEM system modifications, abis or something
else can break LSPlant, so if you plan to publish an app using this framework, do not forget to
support the case where `LSPlant.isInitialized()` returns false.

### Example usage
```gradle
repositories {
    maven { url = "https://github.com/tclement0922/jLSPlant/raw/maven" }
}

dependencies {
    implementation("dev.tclement.jlsplant:jlsplant:3.1-01")
}
```

To hook the method ```exampleMethod(int param)``` from the class ```com.example.Example``` with the method ```exampleMethodReplacement(LSPlant.Hooker.MethodCallback callback)``` from the class ```com.example.Hook```, do:
```java
package com.example;

import dev.tclement.jlsplant.LSPlant;

public class Hook {

    public /* maybe static */ Object exampleMethodReplacement(LSPlant.Hooker.MethodCallback callback) {
        // To use original method
        callback.backup.invoke(/* params */);
        
        // To access original method's 'this', if original method isn't static
        callback.args[0]
        
        // To access original method's 'param' parameter, if original method isn't static
        callback.args[1]
        // or if original method is static
        callback.args[0]
        
        return callback.backup.invoke(callback.args[0], 0);;
    }

    public void hookExampleMethod() {
        // If exampleMethodReplacement is static.
        LSPlant.hookMethod(
            Example.class.getDeclaredMethod("exampleMethod", int.class),
            Hook.class.getDeclaredMethod("exampleMethodReplacement", LSPlant.Hooker.MethodCallback.class)
        );

        // If exampleMethodReplacement isn't static.
        LSPlant.hookMethod(
            this,
            Example.class.getDeclaredMethod("exampleMethod", int.class),
            Hook.class.getDeclaredMethod("exampleMethodReplacement", LSPlant.Hooker.MethodCallback.class)
        );
    }
}
```
For more informations and advanced usage, see demo module or read [documentation](https://tclement0922.github.io/jLSPlant).

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
jLSPlant is licensed under the [GNU Lesser General Public License v3.0](LICENSE), Copyright (C) 2022 T. Clément.
