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

package dev.tclement.jlsplant.tests;

import java.lang.reflect.InvocationTargetException;

import dev.tclement.jlsplant.LSPlant;

@SuppressWarnings("unused")
public class SimpleTestsReplacements {
    public void constructorReplacement(LSPlant.Hooker.MethodCallback callback) {
        var thiz = (TestsTargets) callback.args[0];
        thiz.field = true;
    }

    public String normalMethodReplacement(LSPlant.Hooker.MethodCallback callback) throws InvocationTargetException, IllegalAccessException {
        var args = callback.args;
        var a = (String) args[1];
        var b = (int) args[2];
        var c = (long) args[3];
        return a + b + c + "replace";
    }

    public String manyParametersMethodReplacement(LSPlant.Hooker.MethodCallback callback) throws InvocationTargetException, IllegalAccessException {
        var args = callback.args;
        var a = (String) args[1];
        var b = (boolean) args[2];
        var c = (byte) args[3];
        var d = (short) args[4];
        var e = (int) args[5];
        var f = (long) args[6];
        var g = (float) args[7];
        var h = (double) args[8];
        var i = (int) args[9];
        var j = (long) args[10];
        return a + b + c + d + e + f + g + h + i + j + "replace";
    }

    public static boolean staticMethodReplacement(LSPlant.Hooker.MethodCallback callback) {
        return true;
    }
}
