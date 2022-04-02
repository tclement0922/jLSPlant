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

package dev.tclement.jlsplant;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * jLSPlant native calls
 */
final class Native {
    private Native() {}

    static native boolean isNativeInitialized();

    static native boolean isHooked(Member target);

    static native boolean deoptimize(Member target);

    static native Method hookMethod(Object owner, Member original, Method replacement);

    static native boolean unhookMethod(Member target);

    static native boolean makeClassInheritable(Class<?> target);
}
