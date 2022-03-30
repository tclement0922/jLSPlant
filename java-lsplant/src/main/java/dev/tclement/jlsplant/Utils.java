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
 * Copyright (C) 2022 T. Clément <https://github.com/tclement0922>
 */

package dev.tclement.jlsplant;

import android.util.Log;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * Java-LSPlant internal utils
 */
final class Utils {
    // Edit of Arrays.toString that removes braces.
    static String arrayToString(Object[] a) {
        if (a == null)
            return "null";

        int iMax = a.length - 1;
        if (iMax == -1)
            return "";

        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax)
                return b.toString();
            b.append(", ");
        }
    }

    static String getMemberName(Member member) {
        var declaringClassName = member.getDeclaringClass().getCanonicalName();
        var memberName = member.getName();
        String memberParameters;
        if (member instanceof Method) {
            memberParameters = arrayToString(((Method) member).getParameterTypes());
        } else {
            memberParameters = "";
        }
        var formattedName = declaringClassName + "$" + memberName + "(" + memberParameters + ")";
        Log.d("LSPlant", "Method name is " + formattedName);
        return formattedName;
    }
}
