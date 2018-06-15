/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.internal.jaxb;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;


/**
 * Utility class for Generic class hierarchy.
 *
 */
public class GenericsClassHelper {

    /**
     * A tuple consisting of a concrete class, declaring class that declares a
     * generic interface type.
     */
    private static class DeclaringClassInterfacePair {
        public final Class concreteClass;

        public final Class declaringClass;

        public final Type genericInterface;

        private DeclaringClassInterfacePair(Class concreteClass, Class declaringClass, Type genericInteface) {
            this.concreteClass = concreteClass;
            this.declaringClass = declaringClass;
            this.genericInterface = genericInteface;
        }
    }

    /**
     * Get the parameterized type arguments for a declaring class that
     * declares a generic class or interface type.
     *
     * @param concrete the concrete class than directly or indirectly
     *                 implements or extends an interface class.
     * @param classOrIface    the interface or class.
     * @return the parameterized type arguments, or null if the generic
     * interface type is not a parameterized type.
     */
    public static Type[] getParameterizedTypeArguments(Class concrete, Class classOrIface) {
        DeclaringClassInterfacePair declaringClassInterfacePair = getClass(concrete, classOrIface);
        if (null != declaringClassInterfacePair) {
            return getParameterizedTypeArguments(declaringClassInterfacePair);
        }
        return null;
    }

    /**
     * Get the parameterized type arguments for a declaring class that
     * declares a generic interface type.
     *
     * @param p the declaring class
     * @return the parameterized type arguments, or null if the generic
     * interface type is not a parameterized type.
     */
    private static Type[] getParameterizedTypeArguments(DeclaringClassInterfacePair p) {
        if (p.genericInterface instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) p.genericInterface;
            Type[] as = pt.getActualTypeArguments();
            Type[] ras = new Type[as.length];

            for (int i = 0; i < as.length; i++) {
                Type a = as[i];
                if (a instanceof Class) {
                    ras[i] = a;
                } else if (a instanceof ParameterizedType) {
                    pt = (ParameterizedType) a;
                    ras[i] = a;
                } else if (a instanceof TypeVariable) {
                    ClassTypePair ctp = resolveTypeVariable(p.concreteClass, p.declaringClass, (TypeVariable) a);
                    if (null != ctp) {
                        ras[i] = ctp.t;
                    }
                }
            }
            return ras;
        } else {
            return null;
        }
    }

    /**
     * Find the declaring class that implements or extends an interface or class.
     *
     * @param concrete the concrete class than directly or indirectly
     *                 implements or extends an interface class.
     * @param classOrIface    the interface or class.
     * @return the tuple of the declaring class and the generic interface or class
     * type.
     */
    private static DeclaringClassInterfacePair getClass(Class concrete, Class classOrIface) {
        return getClass(concrete, classOrIface, concrete);
    }

    private static DeclaringClassInterfacePair getClass(Class concrete, Class classOrIface, Class c) {
        Type[] gis = null;

        if (null != c.getGenericSuperclass()) {
            gis = new Type[] {c.getGenericSuperclass()};
        }

        if (null == gis) {
            gis = c.getGenericInterfaces();
        }

        DeclaringClassInterfacePair p = getType(concrete, classOrIface, c, gis);
        if (p != null)
            return p;

        c = c.getSuperclass();
        if (c == null || c == Object.class)
            return null;

        return getClass(concrete, classOrIface, c);
    }

    private static DeclaringClassInterfacePair getType(Class concrete, Class classOrIface, Class c, Type[] ts) {
        for (Type t : ts) {
            DeclaringClassInterfacePair p = getType(concrete, classOrIface, c, t);
            if (p != null)
                return p;
        }
        return null;
    }

    private static DeclaringClassInterfacePair getType(Class concrete, Class classOrIface, Class c, Type t) {
        if (t instanceof Class) {
            if (t == classOrIface) {
                return new DeclaringClassInterfacePair(concrete, c, t);
            } else {
                return getClass(concrete, classOrIface, (Class) t);
            }
        } else if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) t;
            if (pt.getRawType() == classOrIface) {
                return new DeclaringClassInterfacePair(concrete, c, t);
            } else {
                return getClass(concrete, classOrIface, (Class) pt.getRawType());
            }
        }
        return null;
    }

    /**
     * A tuple consisting of a class and type of the class.
     */
    private static class ClassTypePair {

        /**
         * The type of the class.
         */
        public final Type t;

        public ClassTypePair(Class c) {
            this(c, c);
        }

        public ClassTypePair(Class c, Type t) {
            this.t = t;
        }
    }

    /**
     * Given a type variable resolve the Java class of that variable.
     *
     * @param c  the concrete class from which all type variables are resolved
     * @param dc the declaring class where the type variable was defined
     * @param tv the type variable
     * @return the resolved Java class and type, otherwise null if the type variable
     * could not be resolved
     */
    private static ClassTypePair resolveTypeVariable(Class c, Class dc, TypeVariable tv) {
        return resolveTypeVariable(c, dc, tv, new HashMap<TypeVariable, Type>());
    }

    private static ClassTypePair resolveTypeVariable(Class c, Class dc, TypeVariable tv,
                                                     Map<TypeVariable, Type> map) {
        Type[] gis = c.getGenericInterfaces();
        for (Type gi : gis) {
            if (gi instanceof ParameterizedType) {
                // process pt of interface
                ParameterizedType pt = (ParameterizedType) gi;
                ClassTypePair ctp = resolveTypeVariable(pt, (Class) pt.getRawType(), dc, tv, map);
                if (ctp != null)
                    return ctp;
            }
        }

        Type gsc = c.getGenericSuperclass();
        if (gsc instanceof ParameterizedType) {
            // process pt of class
            ParameterizedType pt = (ParameterizedType) gsc;
            return resolveTypeVariable(pt, c.getSuperclass(), dc, tv, map);
        } else if (gsc instanceof Class) {
            return resolveTypeVariable(c.getSuperclass(), dc, tv, map);
        }
        return null;
    }

    private static ClassTypePair resolveTypeVariable(ParameterizedType pt, Class c, Class dc, TypeVariable tv,
                                                     Map<TypeVariable, Type> map) {
        Type[] typeArguments = pt.getActualTypeArguments();

        TypeVariable[] typeParameters = c.getTypeParameters();

        Map<TypeVariable, Type> submap = new HashMap<TypeVariable, Type>();
        for (int i = 0; i < typeArguments.length; i++) {
            // Substitute a type variable with the Java class
            if (typeArguments[i] instanceof TypeVariable) {
                Type t = map.get(typeArguments[i]);
                submap.put(typeParameters[i], t);
            } else {
                submap.put(typeParameters[i], typeArguments[i]);
            }
        }

        if (c == dc) {
            Type t = submap.get(tv);
            if (t instanceof Class) {
                return new ClassTypePair((Class) t);
            } else if (t instanceof GenericArrayType) {
                t = ((GenericArrayType) t).getGenericComponentType();
                if (t instanceof Class) {
                    c = (Class) t;
                    try {
                        return new ClassTypePair(getArrayClass(c));
                    } catch (Exception e) {
                    }
                    return null;
                } else if (t instanceof ParameterizedType) {
                    Type rt = ((ParameterizedType) t).getRawType();
                    if (rt instanceof Class) {
                        c = (Class) rt;
                    } else {
                        return null;
                    }
                    try {
                        return new ClassTypePair(getArrayClass(c), t);
                    } catch (Exception e) {
                        return null;
                    }
                } else {
                    return null;
                }
            } else if (t instanceof ParameterizedType) {
                pt = (ParameterizedType) t;
                if (pt.getRawType() instanceof Class) {
                    return new ClassTypePair((Class) pt.getRawType(), pt);
                } else
                    return null;
            } else {
                return null;
            }
        } else {
            return resolveTypeVariable(c, dc, tv, submap);
        }
    }

    protected static Class getClassOfType(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType) type;
            Type t = arrayType.getGenericComponentType();
            if (t instanceof Class) {
                return getArrayClass((Class) t);
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType subType = (ParameterizedType) type;
            Type t = subType.getRawType();
            if (t instanceof Class) {
                return (Class) t;
            }
        }
        return null;
    }

    /**
     * Get Array class of component class.
     *
     * @param c the component class of the array
     * @return the array class.
     */
    private static Class getArrayClass(Class c) {
        try {
            Object o = Array.newInstance(c, 0);
            return o.getClass();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

}
