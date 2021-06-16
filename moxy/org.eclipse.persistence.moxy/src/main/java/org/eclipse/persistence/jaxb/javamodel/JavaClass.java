/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
    public Collection getActualTypeArguments();
    public JavaClass getComponentType();
    public String getQualifiedName();
    public String getRawName();
    public boolean hasActualTypeArguments();
    public Collection getDeclaredClasses();
    public JavaField getDeclaredField(String arg0);
    public Collection getDeclaredFields();
    public JavaMethod getDeclaredMethod(String arg0, JavaClass[] arg1);
    public Collection getDeclaredMethods();
    public JavaMethod getMethod(String arg0, JavaClass[] arg1);
    public Collection getMethods();
    public JavaConstructor getConstructor(JavaClass[] parameterTypes);
    public Collection getConstructors();
    public JavaConstructor getDeclaredConstructor(JavaClass[] parameterTypes);
    public Collection getDeclaredConstructors();
    public int getModifiers();
    public String getName();
    public JavaPackage getPackage();
    public String getPackageName();
    public JavaClass getSuperclass();
    public Type[] getGenericInterfaces();
    public Type getGenericSuperclass();
    public boolean isAbstract();
    public boolean isAnnotation();
    public boolean isArray();
    public boolean isAssignableFrom(JavaClass arg0);
    public boolean isEnum();
    public boolean isFinal();
    public boolean isInterface();
    public boolean isMemberClass();
    public boolean isPrimitive();
    public boolean isPrivate();
    public boolean isProtected();
    public boolean isPublic();
    public boolean isStatic();
    public boolean isSynthetic();
    public JavaClassInstanceOf instanceOf();
}
