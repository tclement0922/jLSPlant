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
 * Copyright (C)  T. Clément <https://github.com/tclement0922>
 */

package dev.tclement.jlsplant

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.tclement.jlsplant.LSPlant.Hooker.MethodCallback
import dev.tclement.jlsplant.tests.SimpleTestsReplacements
import dev.tclement.jlsplant.tests.TestsReplacements
import dev.tclement.jlsplant.tests.TestsTargets
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*


/**
 * Unit tests for jLSPlant. Some parts of these tests are based on official LSPlant tests, which are
 * licensed under LGPLv3.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UnitTests {

    @Test
    fun test_00_initLSPlant() {
        Assert.assertTrue(LSPlant.isInitialized())
    }

    @Test
    @Throws(
        NoSuchMethodException::class,
        InvocationTargetException::class,
        IllegalAccessException::class
    )
    fun test_01_staticMethod() {
        val staticMethod: Method = TestsTargets::class.java.getDeclaredMethod("staticMethod")
        val staticMethodReplacement: Method =
            TestsReplacements::class.java.getDeclaredMethod(
                "staticMethodReplacement",
                Array<Any>::class.java
            )
        Assert.assertFalse(TestsTargets.staticMethod())
        val backup = LSPlant.hookMethodAdvanced(TestsReplacements(), staticMethod, staticMethodReplacement)
        Assert.assertNotNull(backup)
        Assert.assertTrue(TestsTargets.staticMethod())
        Assert.assertFalse(backup.invoke(null) as Boolean)
        Assert.assertTrue(LSPlant.unhookMethod(staticMethod))
        Assert.assertFalse(TestsTargets.staticMethod())
    }

    @Test
    @Throws(
        NoSuchMethodException::class,
        InvocationTargetException::class,
        IllegalAccessException::class
    )
    fun test_02_normalMethod() {
        val normalMethod: Method = TestsTargets::class.java.getDeclaredMethod(
            "normalMethod",
            String::class.java,
            Int::class.javaPrimitiveType,
            Long::class.javaPrimitiveType
        )
        val normalMethodReplacement: Method =
            TestsReplacements::class.java.getDeclaredMethod(
                "normalMethodReplacement",
                Array<Any>::class.java
            )
        val a = "test"
        val b = 114514
        val c = 1919810L
        val o = a + b + c
        val r = a + b + c + "replace"
        val test = TestsTargets()
        Assert.assertEquals(o, test.normalMethod(a, b, c))
        val backup = LSPlant.hookMethodAdvanced(TestsReplacements(), normalMethod, normalMethodReplacement)
        Assert.assertNotNull(backup)
        Assert.assertEquals(r, test.normalMethod(a, b, c))
        Assert.assertEquals(o, backup.invoke(test, a, b, c))
        Assert.assertTrue(LSPlant.unhookMethod(normalMethod))
        Assert.assertEquals(o, test.normalMethod(a, b, c))
    }

    @Test
    @Throws(
        NoSuchMethodException::class,
        InvocationTargetException::class,
        IllegalAccessException::class,
        InstantiationException::class
    )
    fun test_03_constructor() {
        val constructor: Constructor<TestsTargets> = TestsTargets::class.java.getDeclaredConstructor()
        val constructorReplacement: Method = TestsReplacements::class.java.getDeclaredMethod(
            "constructorReplacement",
            Array<Any>::class.java
        )
        Assert.assertFalse(TestsTargets().field)
        Assert.assertFalse(constructor.newInstance().field)
        val backup = LSPlant.hookMethodAdvanced(TestsReplacements(), constructor, constructorReplacement)
        Assert.assertNotNull(backup)
        Assert.assertTrue(TestsTargets().field)
        Assert.assertTrue(constructor.newInstance().field)
        Assert.assertTrue(LSPlant.unhookMethod(constructor))
        Assert.assertFalse(TestsTargets().field)
        Assert.assertFalse(constructor.newInstance().field)
    }

    @Test
    @Throws(
        NoSuchMethodException::class,
        InvocationTargetException::class,
        IllegalAccessException::class
    )
    fun test_04_manyParametersMethod() {
        val manyParametersMethod: Method = TestsTargets::class.java.getDeclaredMethod(
            "manyParametersMethod",
            String::class.java,
            Boolean::class.javaPrimitiveType,
            Byte::class.javaPrimitiveType,
            Short::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
            Long::class.javaPrimitiveType,
            Float::class.javaPrimitiveType,
            Double::class.javaPrimitiveType,
            Int::class.java,
            Long::class.java
        )
        val manyParametersReplacement: Method =
            TestsReplacements::class.java.getDeclaredMethod(
                "manyParametersMethodReplacement",
                Array<Any>::class.java
            )
        val a = "test"
        val b = true
        val c = 114.toByte()
        val d = 514.toShort()
        val e = 19
        val f = 19L
        val g = 810f
        val h = 12345.0
        val o = a + b + c + d + e + f + g + h + e + f
        val r = a + b + c + d + e + f + g + h + e + f + "replace"
        val test = TestsTargets()
        Assert.assertEquals(o, test.manyParametersMethod(a, b, c, d, e, f, g, h, e, f))
        val backup = LSPlant.hookMethodAdvanced(TestsReplacements(), manyParametersMethod, manyParametersReplacement)
        Assert.assertNotNull(backup)
        Assert.assertEquals(r, test.manyParametersMethod(a, b, c, d, e, f, g, h, e, f))
        Assert.assertEquals(o, backup.invoke(test, a, b, c, d, e, f, g, h, e, f))
        Assert.assertTrue(LSPlant.unhookMethod(manyParametersMethod))
        Assert.assertEquals(o, test.manyParametersMethod(a, b, c, d, e, f, g, h, e, f))
    }

    @Test
    @Throws(
        NoSuchMethodException::class,
        InvocationTargetException::class,
        IllegalAccessException::class,
        ClassNotFoundException::class
    )
    fun test_05_uninitializedStaticMethod() {
        val uninitializedClass = Class.forName(
            "dev.tclement.jlsplant.tests.TestsTargets\$NeedInitialize", false,
            TestsTargets::class.java.classLoader
        )
        val staticMethod = uninitializedClass.getDeclaredMethod("staticMethod")
        val callStaticMethod = uninitializedClass.getDeclaredMethod("callStaticMethod")
        val staticMethodReplacement: Method =
            TestsReplacements::class.java.getDeclaredMethod(
                "staticMethodReplacement",
                Array<Any>::class.java
            )
        val backup = LSPlant.hookMethodAdvanced(TestsReplacements(), staticMethod, staticMethodReplacement)
        Assert.assertNotNull(backup)
        for (i in 0..4999) {
            Assert.assertTrue("Iter $i", callStaticMethod.invoke(null) as Boolean)
            Assert.assertFalse("Iter $i", backup.invoke(null) as Boolean)
        }
        Assert.assertTrue(LSPlant.unhookMethod(staticMethod))
        Assert.assertFalse(callStaticMethod.invoke(null) as Boolean)
    }

    @Test
    @Throws(
        NoSuchMethodException::class,
        InvocationTargetException::class,
        IllegalAccessException::class
    )
    fun test_06_staticMethod() {
        val staticMethod: Method = TestsTargets::class.java.getDeclaredMethod("staticMethod")
        val staticMethodReplacement: Method =
            SimpleTestsReplacements::class.java.getDeclaredMethod(
                "staticMethodReplacement",
                MethodCallback::class.java
            )
        Assert.assertFalse(TestsTargets.staticMethod())
        Assert.assertTrue(LSPlant.hookMethod(null, staticMethod, staticMethodReplacement))
        Assert.assertTrue(TestsTargets.staticMethod())
        Assert.assertTrue(LSPlant.unhookMethod(staticMethod))
        Assert.assertFalse(TestsTargets.staticMethod())
    }

    @Test
    @Throws(
        NoSuchMethodException::class,
        InvocationTargetException::class,
        IllegalAccessException::class
    )
    fun test_07_normalMethod() {
        val normalMethod: Method = TestsTargets::class.java.getDeclaredMethod(
            "normalMethod",
            String::class.java,
            Int::class.javaPrimitiveType,
            Long::class.javaPrimitiveType
        )
        val normalMethodReplacement: Method =
            SimpleTestsReplacements::class.java.getDeclaredMethod(
                "normalMethodReplacement",
                MethodCallback::class.java
            )
        val a = "test"
        val b = 114514
        val c = 1919810L
        val o = a + b + c
        val r = a + b + c + "replace"
        val test = TestsTargets()
        Assert.assertEquals(o, test.normalMethod(a, b, c))
        Assert.assertTrue(LSPlant.hookMethod(SimpleTestsReplacements(), normalMethod, normalMethodReplacement))
        Assert.assertEquals(r, test.normalMethod(a, b, c))
        Assert.assertTrue(LSPlant.unhookMethod(normalMethod))
        Assert.assertEquals(o, test.normalMethod(a, b, c))
    }

    @Test
    @Throws(
        NoSuchMethodException::class,
        InvocationTargetException::class,
        IllegalAccessException::class,
        InstantiationException::class
    )
    fun test_08_constructor() {
        val constructor: Constructor<TestsTargets> = TestsTargets::class.java.getDeclaredConstructor()
        val constructorReplacement: Method = SimpleTestsReplacements::class.java.getDeclaredMethod(
            "constructorReplacement",
            MethodCallback::class.java
        )
        Assert.assertFalse(TestsTargets().field)
        Assert.assertFalse(constructor.newInstance().field)
        Assert.assertTrue(LSPlant.hookMethod(SimpleTestsReplacements(), constructor, constructorReplacement))
        Assert.assertTrue(TestsTargets().field)
        Assert.assertTrue(constructor.newInstance().field)
        Assert.assertTrue(LSPlant.unhookMethod(constructor))
        Assert.assertFalse(TestsTargets().field)
        Assert.assertFalse(constructor.newInstance().field)
    }

    @Test
    @Throws(
        NoSuchMethodException::class,
        InvocationTargetException::class,
        IllegalAccessException::class
    )
    fun test_09_manyParametersMethod() {
        val manyParametersMethod: Method = TestsTargets::class.java.getDeclaredMethod(
            "manyParametersMethod",
            String::class.java,
            Boolean::class.javaPrimitiveType,
            Byte::class.javaPrimitiveType,
            Short::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
            Long::class.javaPrimitiveType,
            Float::class.javaPrimitiveType,
            Double::class.javaPrimitiveType,
            Int::class.java,
            Long::class.java
        )
        val manyParametersReplacement: Method =
            SimpleTestsReplacements::class.java.getDeclaredMethod(
                "manyParametersMethodReplacement",
                MethodCallback::class.java
            )
        val a = "test"
        val b = true
        val c = 114.toByte()
        val d = 514.toShort()
        val e = 19
        val f = 19L
        val g = 810f
        val h = 12345.0
        val o = a + b + c + d + e + f + g + h + e + f
        val r = a + b + c + d + e + f + g + h + e + f + "replace"
        val test = TestsTargets()
        Assert.assertEquals(o, test.manyParametersMethod(a, b, c, d, e, f, g, h, e, f))
        Assert.assertTrue(LSPlant.hookMethod(SimpleTestsReplacements(), manyParametersMethod, manyParametersReplacement))
        Assert.assertEquals(r, test.manyParametersMethod(a, b, c, d, e, f, g, h, e, f))
        Assert.assertTrue(LSPlant.unhookMethod(manyParametersMethod))
        Assert.assertEquals(o, test.manyParametersMethod(a, b, c, d, e, f, g, h, e, f))
    }

    @Test
    @Throws(
        NoSuchMethodException::class,
        InvocationTargetException::class,
        IllegalAccessException::class,
        ClassNotFoundException::class
    )
    fun test_10_uninitializedStaticMethod() {
        val uninitializedClass = Class.forName(
            "dev.tclement.jlsplant.tests.TestsTargets\$NeedInitialize", false,
            TestsTargets::class.java.classLoader
        )
        val staticMethod = uninitializedClass.getDeclaredMethod("staticMethod")
        val callStaticMethod = uninitializedClass.getDeclaredMethod("callStaticMethod")
        val staticMethodReplacement: Method =
            SimpleTestsReplacements::class.java.getDeclaredMethod(
                "staticMethodReplacement",
                MethodCallback::class.java
            )
        Assert.assertTrue(LSPlant.hookMethod(SimpleTestsReplacements(), staticMethod, staticMethodReplacement))
        for (i in 0..4999) {
            Assert.assertTrue("Iter $i", callStaticMethod.invoke(null) as Boolean)
        }
        Assert.assertTrue(LSPlant.unhookMethod(staticMethod))
        Assert.assertFalse(callStaticMethod.invoke(null) as Boolean)
    }
}