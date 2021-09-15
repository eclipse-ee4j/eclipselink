/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jaxb.javamodel;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>A TopLink JAXB 2.0 Java model representation of a JDK Class.
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Provide information about a given implementation's underlying class, such
 * as name, package, method/field names and parameters, annotations, etc.</li>
 * </ul>
 *
 * @since Oracle TopLink 11.1.1.0.0
 * @see org.eclipse.persistence.jaxb.javamodel.JavaHasAnnotations
 * @see java.lang.Class
 */
public interface JavaClass extends JavaHasAnnotations {
    Collection getActualTypeArguments();
    JavaClass getComponentType();
    String getQualifiedName();
    String getRawName();
    boolean hasActualTypeArguments();
    Collection getDeclaredClasses();
    JavaField getDeclaredField(String arg0);
    Collection getDeclaredFields();
    JavaMethod getDeclaredMethod(String arg0, JavaClass[] arg1);
    Collection getDeclaredMethods();
    JavaMethod getMethod(String arg0, JavaClass[] arg1);
    Collection getMethods();
    JavaConstructor getConstructor(JavaClass[] parameterTypes);
    Collection getConstructors();
    JavaConstructor getDeclaredConstructor(JavaClass[] parameterTypes);
    Collection getDeclaredConstructors();
    int getModifiers();
    String getName();
    JavaPackage getPackage();
    String getPackageName();
    JavaClass getSuperclass();
    Type[] getGenericInterfaces();
    Type getGenericSuperclass();
    boolean isAbstract();
    boolean isAnnotation();
    boolean isArray();
    boolean isAssignableFrom(JavaClass arg0);
    boolean isEnum();
    boolean isFinal();
    boolean isInterface();
    boolean isMemberClass();
    boolean isPrimitive();
    boolean isPrivate();
    boolean isProtected();
    boolean isPublic();
    boolean isStatic();
    boolean isSynthetic();
    JavaClassInstanceOf instanceOf();
}
