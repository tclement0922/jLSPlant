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

@SuppressWarnings("unused")
public class TestsTargets {
    public Boolean field = null;

    public TestsTargets() {
        if (field == null) {
            field = false;
        }
    }

    public String normalMethod(String a, int b, long c) {
        return a + b + c;
    }

    public String manyParametersMethod(
            String a,
            boolean b,
            byte c,
            short d,
            int e,
            long f,
            float g,
            double h,
            int i,
            long j
    ) {
        return a + b + c + d + e + f + g + h + i + j;
    }

    public static boolean staticMethod() {
        return false;
    }

    public static class NeedInitialize {
        static int x;

        static {
            x = 0;
        }

        public static boolean staticMethod() {
            try {
                return x != 0;
            } catch (Throwable e) {
                return false;
            }
        }

        public static boolean callStaticMethod() {
            try {
                return staticMethod();
            } catch (Throwable e) {
                return false;
            }
        }
    }
}
