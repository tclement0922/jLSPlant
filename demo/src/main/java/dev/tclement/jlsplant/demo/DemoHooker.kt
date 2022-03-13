package dev.tclement.jlsplant.demo

import android.content.res.Resources
import dev.tclement.jlsplant.LSPlant

object DemoHook {
    var state = false

    fun hook() {
        LSPlant.hookMethod(
            Resources::class.java.getDeclaredMethod("getColor", Int::class.java),
            DemoHook::class.java.getDeclaredMethod("getColorReplacement", LSPlant.Hooker.MethodCallback::class.java)
        )
        LSPlant.hookMethod(
            DemoHook::class.java.getDeclaredMethod("helloWorld"),
            DemoHook::class.java.getDeclaredMethod("helloReplacement", LSPlant.Hooker.MethodCallback::class.java)
        )
    }

    fun unhook() {
        LSPlant.unhookMethod(
            Resources::class.java.getDeclaredMethod("getColor", Int::class.java)
        )
        LSPlant.unhookMethod(
            DemoHook::class.java.getDeclaredMethod("helloWorld")
        )
    }

    @Suppress("Unused")
    @JvmStatic
    fun getColorReplacement(callback: LSPlant.Hooker.MethodCallback): Int {
        val resources = callback.args[0] as Resources
        if (callback.args[1] == R.color.lsplant_demo_color) {
            return callback.backup.invoke(resources, R.color.lsplant_demo_color_hook) as Int
        }
        return callback.backup.invoke(resources, callback.args[1]) as Int
    }

    @JvmStatic
    fun helloWorld() = "Hello world !"

    @Suppress("Unused", "Unused_parameter")
    @JvmStatic
    fun helloReplacement(callback: LSPlant.Hooker.MethodCallback): String {
        return "Hello LSPlant !"
    }
}