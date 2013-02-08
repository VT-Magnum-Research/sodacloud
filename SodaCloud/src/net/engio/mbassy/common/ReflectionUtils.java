/******************************************************************************************
MIT License

Copyright (c) 2012 Benjamin Diedrichsen

Permission is hereby granted, free of charge, to any person obtaining a copy of this
software and associated documentation files (the "Software"), to deal in the Software
without restriction, including without limitation the rights to use, copy, modify, merge,
publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
DEALINGS IN THE SOFTWARE.
********************************************************************************************/
package net.engio.mbassy.common;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author bennidi
 *         Date: 2/16/12
 *         Time: 12:14 PM
 */
public class ReflectionUtils {

    public static List<Method> getMethods(IPredicate<Method> condition, Class<?> target) {
        List<Method> methods = new LinkedList<Method>();
        try {
            for (Method method : target.getDeclaredMethods()) {
                if (condition.apply(method)) {
                    methods.add(method);
                }
            }
        } catch (Exception e) {
            //nop
        }
        if (!target.equals(Object.class)) {
            methods.addAll(getMethods(condition, target.getSuperclass()));
        }
        return methods;
    }

    /**
     * Traverses the class hierarchy upwards, starting at the given subclass, looking
     * for an override of the given methods -> finds the bottom most override of the given
     * method if any exists
     *
     * @param overridingMethod
     * @param subclass
     * @return
     */
    public static Method getOverridingMethod(Method overridingMethod, Class subclass) {
        Class current = subclass;
        while(!current.equals(overridingMethod.getDeclaringClass())){
            try {
                Method overridden = current.getDeclaredMethod(overridingMethod.getName(), overridingMethod.getParameterTypes());
                return overridden;
            } catch (NoSuchMethodException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    public static List<Method> withoutOverridenSuperclassMethods(List<Method> allMethods) {
        List<Method> filtered = new LinkedList<Method>();
        for (Method method : allMethods) {
            if (!containsOverridingMethod(allMethods, method)) filtered.add(method);
        }
        return filtered;
    }

    public static Collection<Class> getSuperclasses(Class from) {
        Collection<Class> superclasses = new LinkedList<Class>();
        while (!from.equals(Object.class)) {
            superclasses.add(from.getSuperclass());
            from = from.getSuperclass();
        }
        return superclasses;
    }

    public static boolean containsOverridingMethod(List<Method> allMethods, Method methodToCheck) {
        for (Method method : allMethods) {
            if (isOverriddenBy(methodToCheck, method)) return true;
        }
        return false;
    }

    private static boolean isOverriddenBy(Method superclassMethod, Method subclassMethod) {
        // if the declaring classes are the same or the subclass method is not defined in the subclass
        // hierarchy of the given superclass method or the method names are not the same then
        // subclassMethod does not override superclassMethod
        if (superclassMethod.getDeclaringClass().equals(subclassMethod.getDeclaringClass())
                || !superclassMethod.getDeclaringClass().isAssignableFrom(subclassMethod.getDeclaringClass())
                || !superclassMethod.getName().equals(subclassMethod.getName())) {
            return false;
        }

        Class[] superClassMethodParameters = superclassMethod.getParameterTypes();
        Class[] subClassMethodParameters = superclassMethod.getParameterTypes();
        // method must specify the same number of parameters
        if(subClassMethodParameters.length != subClassMethodParameters.length){
            return false;
        }
        //the parameters must occur in the exact same order
        for(int i = 0 ; i< subClassMethodParameters.length; i++){
           if(!superClassMethodParameters[i].equals(subClassMethodParameters[i])){
               return false;
           }
        }
        return true;
    }

}
