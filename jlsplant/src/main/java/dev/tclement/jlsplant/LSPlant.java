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

package dev.tclement.jlsplant;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Java interface for LSPlant
 */
@SuppressWarnings("unused")
public final class LSPlant {
    /**
     * Every created hookers are stored there.
     */
    private static final Map<String, Hooker> hookers = new HashMap<>();

    private static boolean isLibLoaded;

    private LSPlant() {}

    static {
        try {
            System.loadLibrary("java_lsplant");
            isLibLoaded = true;
        } catch (SecurityException | UnsatisfiedLinkError e) {
            Log.e("jLSPlant", "Failed to load java_lsplant native lib.", e);
            isLibLoaded = false;
        }
    }

    /**
     * Checks the current LSPlant initialization status. Causes of non-initialization may be more
     * likely device incompatibility, native lib not being loaded for some reason (see logcat),
     * or (very unlikely) unsupported abi.
     * @return If LSPlant is initialized, {@code true}, otherwise {@code false}.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isInitialized() {
        return isLibLoaded && Native.isNativeInitialized();
    }

    /**
     * Hook a Java method by providing the {@code target} method together with the context object
     * {@code owner} and its callback {@code replacement}.
     * @param owner The hooker object to store the context of the hook. Null if replacement is
     *              static. The most likely usage is, in Xposed framework, multiple modules can
     *              hook the same Java method and the {@code owner} can be used to store all the
     *              callbacks to allow multiple modules work simultaneously without conflict.
     * @param target The method you want to hook. Must not be null.
     * @param replacement The callback method to the {@code owner} object is used to replace the
     *                    {@code target} method. Whenever the {@code target} method is invoked, the
     *                    {@code replacement} method will be invoked instead of the original
     *                    {@code target} method. The return type must be the same as the original
     *                    method and the parameter type must be {@link Hooker.MethodCallback}.
     * @return If the hook succeed, {@code true}, otherwise {@code false}.
     * @see #hookMethod(Member, Method)
     * @see #hookMethodAdvanced(Object, Member, Method)
     * @see #unhookMethod(Member)
     * @implNote This function is thread safe (you can call it simultaneously from multiple thread)
     *           but it's not atomic to the same {@code target} method. That means
     *           {@link #unhookMethod} or {@link #isHooked} does not guarantee to work properly on
     *           the same {@code target} method before it returns. Also, simultaneously call on this
     *           function with the same {@code target} method does not guarantee only one will
     *           success. If you call this with different {@code owner} object on the same
     *           {@code target} method simultaneously, the behavior is undefined.
     */
    public static boolean hookMethod(Object owner, Member target, Method replacement) {
        if (!isInitialized())
            return false;
        var hooker = Hooker.hook(owner, target, replacement);
        if (hooker != null) {
            hookers.put(Utils.getMemberName(target), hooker);
            return true;
        }
        return false;
    }

    /**
     * Hook a Java method by providing the {@code target} method together with the context object
     * {@code owner} and its callback {@code replacement}.
     * @param owner The hooker object to store the context of the hook. Null if replacement is
     *              static. The most likely usage is to store the {@code backup} method into it so
     *              that when {@code replacement} is invoked, it can call the original method.
     *              Another scenario is that, for example, in Xposed framework, multiple modules can
     *              hook the same Java method and the {@code owner} can be used to store all the
     *              callbacks to allow multiple modules work simultaneously without conflict.
     * @param target The method you want to hook. Must not be null.
     * @param replacement The callback method to the {@code owner} object is used to replace the
     *                    {@code target} method. Whenever the {@code target} method is invoked, the
     *                    {@code replacement} method will be invoked instead of the original
     *                    {@code target} method. The return type must be the same as the original
     *                    method and the parameter type must be {@code Object[]}. {@code args[0]} is
     *                    the {@code this} object for non-static methods and there is NOT null this
     *                    object placeholder for static methods.
     * @return The backup of the original method. Should be stored in the {@code owner} object.
     * @see #hookMethod(Object, Member, Method)
     * @see #hookMethod(Member, Method)
     * @see #unhookMethod(Member)
     * @implNote This function is thread safe (you can call it simultaneously from multiple thread)
     *           but it's not atomic to the same {@code target} method. That means
     *           {@link #unhookMethod} or {@link #isHooked} does not guarantee to work properly on
     *           the same {@code target} method before it returns. Also, simultaneously call on this
     *           function with the same {@code target} method does not guarantee only one will
     *           success. If you call this with different {@code owner} object on the same
     *           {@code target} method simultaneously, the behavior is undefined.
     */
    public static Method hookMethodAdvanced(Object owner, Member target, Method replacement) {
        if (!isInitialized())
            return null;
        return Native.hookMethod(owner, target, replacement);
    }


    /**
     * Hook a Java method by providing the {@code target} method together with its callback
     * {@code replacement}.
     * @param target The method you want to hook. Must not be null.
     * @param replacement The callback method is used to replace the {@code target} method. Whenever
     *                    the {@code target} method is invoked, the {@code replacement} method will
     *                    be invoked instead of the original {@code target} method.
     * @return If the hook succeed, {@code true}, otherwise {@code false}.
     * @see #hookMethod(Object, Member, Method)
     * @see #unhookMethod(Member)
     * @implNote This function is thread safe (you can call it simultaneously from multiple thread)
     *           but it's not atomic to the same {@code target} method. That means
     *           {@link #unhookMethod} or {@link #isHooked} does not guarantee to work properly on
     *           the same {@code target} method before it returns. Also, simultaneously call on this
     *           function with the same {@code target} method does not guarantee only one will
     *           success.
     */
    public static boolean hookMethod(Member target, Method replacement) {
        return hookMethod(null, target, replacement);
    }

    /**
     * Unhook a Java function that is previously hooked.
     * @param target The target method that is previously hooked.
     * @return If the unhook succeed, {@code true}, otherwise {@code false}.
     * @see #hookMethod
     * @implNote Calling {@code backup} (created in {@link #hookMethod}) after unhooking
     *           is undefined behavior. Please read {@link #hookMethod}'s doc for more details.
     */
    public static boolean unhookMethod(Member target) {
        if (!isInitialized())
            return false;
        var memberName = Utils.getMemberName(target);
        if (hookers.containsKey(memberName)) {
            var hooker = hookers.get(memberName);
            if (hooker != null) {
                hookers.remove(memberName);
            }
        }
        return Native.unhookMethod(target);
    }

    /**
     * Check if a Java function is hooked by LSPlant or not.
     * @param target The method to check if it was hooked or not.
     * @return If hooking succeed, {@code true}, otherwise {@code false}.
     * @see #hookMethod(Member, Method)
     * @see #hookMethod(Object, Member, Method)
     * @see #unhookMethod(Member)
     */
    public static boolean isHooked(Member target) {
        return isInitialized() && Native.isHooked(target);
    }

    /**
     * Deoptimize a method to avoid hooked callee not being called because of inline.
     * @param target The method to deoptimize. By deoptimizing the method, the method will back all
     *               callee without inlining. For example, if you hooked a short method B that is
     *               invoked by method A, and you find that your callback to B is not invoked after
     *               hooking, then it may mean A has inlined B inside its method body. To force A to
     *               call your hooked B, you can deoptimize A and then your hook can take effect.
     *               Generally, you need to find all the callers of your hooked callee and that can
     *               be hardly achieve (but you can still search all callers by using DexHelper).
     *               Use this function if you are sure the deoptimized callers are all you need.
     *               Otherwise, it would be better to change the hook point or to deoptimize the
     *               whole app manually (by simple reinstall the app without uninstalled).
     * @return If the deoptimizing has succeed, {@code true}, otherwise {@code false}.
     * @implNote It is safe to call deoptimizing on a hooked method because the deoptimization will
     *           perform on the backup method instead.
     */
    public static boolean deoptimize(Member target) {
        return isInitialized() && Native.deoptimize(target);
    }

    /**
     * Make a class inheritable. It will make the class non-final and make all its private
     * constructors protected.
     * @param target The target class that is to make inheritable.
     * @return If the operation has succeed, {@code true}, otherwise {@code false}.
     */
    public static boolean makeClassInheritable(Class<?> target) {
        return isInitialized() && Native.makeClassInheritable(target);
    }

    // Inspired by LSPlant tests
    /**
     * Hooker class of LSPlant. Each instance of this class represents a method hook. Can't be
     * initialized manually.
     */
    public static class Hooker {
        public static class MethodCallback {
            /**
             * The original method.
             * @implNote  You can call it like this: {@code backup.invoke(args[0], args[1], ...)}
             * if the original method isn't a static method, or {@code backup.invoke(null, args[0], ...)}
             * if it's static.
             */
            public Method backup;

            /**
             * Method original arguments.
             * @implNote If the original method is not static, the first argument corresponds to
             *           {@code this} in the original method.
             */
            public Object[] args;

            MethodCallback(Method backup, Object[] args) {
                this.backup = backup;
                this.args = args;
            }
        }

        private Object owner;
        private Method backup;
        private Member target;
        private Method replacement;

        private Hooker() {}

        /**
         * Used by reflection.
         */
        public Object callback(Object[] args) throws InvocationTargetException, IllegalAccessException {
            var methodCallback = new Hooker.MethodCallback(backup, args);
            return replacement.invoke(owner, methodCallback);
        }

        private static Hooker hook(Object owner, Member target, Method replacement) {
            Hooker hooker = new Hooker();
            try {
                var callbackMethod = Hooker.class.getDeclaredMethod("callback", Object[].class);
                var result = Native.hookMethod(hooker, target, callbackMethod);
                if (result == null) throw new LSPlantHookFailed();
                hooker.owner = owner;
                hooker.backup = result;
                hooker.target = target;
                hooker.replacement = replacement;
            } catch (NoSuchMethodException e) {
                Log.e("jLSPlant", "Hooker.callback(...) method not found. Make sure jLSPlant is added to your proguard rules.", e);
                hooker = null;
            } catch (LSPlantHookFailed e) {
                Log.e("jLSPlant", "Unable to hook target.", e);
                hooker = null;
            }
            return hooker;
        }

        public static class LSPlantHookFailed extends RuntimeException {
            public LSPlantHookFailed() {
                super("LSPlant is unable to hook the target method.");
            }
        }
    }
}
