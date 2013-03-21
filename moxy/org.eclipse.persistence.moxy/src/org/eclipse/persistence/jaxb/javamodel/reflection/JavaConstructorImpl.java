/*******************************************************************************
* Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     mmacivor - March 12/2009 - Initial implementation
******************************************************************************/

package org.eclipse.persistence.jaxb.javamodel.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaConstructor;

/**
 * <p><b>Purpose</b>: JavaModel representation of a java.lang.reflect.Constructor object.
 * <p><b>Reponsibilities:</b>
 * <ul><li>Provide a reflection-based implementation of the JavaConstructor interface</li>
 * <li>Delegate calls to a wrapped instance of java.lang.reflect.Consturctor</li>
 * </ul>
 * @author mmacivor
 *
 */
public class JavaConstructorImpl implements JavaConstructor {

    private JavaModelImpl javaModelImpl;
    protected Constructor jConstructor;

    public JavaConstructorImpl(Constructor constructor, JavaModelImpl javaModelImpl) {
        this.javaModelImpl = javaModelImpl;
        this.jConstructor = constructor;
    }

    public int getModifiers() {
        return jConstructor.getModifiers();
    }

    public String getName() {
        return jConstructor.getName();
    }

    public JavaClass getOwningClass() {
        return javaModelImpl.getClass(jConstructor.getDeclaringClass());
    }

    public JavaClass[] getParameterTypes() {
        Class[] params = jConstructor.getParameterTypes();
        JavaClass[] paramArray = new JavaClass[params.length];
        for (int i=0; i<params.length; i++) {
            paramArray[i] = javaModelImpl.getClass(params[i]);
        }
        return paramArray;
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(getModifiers());
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(getModifiers());
    }

    public boolean isProtected() {
        return Modifier.isProtected(getModifiers());
    }

    public boolean isPublic() {
        return Modifier.isPublic(getModifiers());
    }

    public boolean isStatic() {
        return Modifier.isStatic(getModifiers());
    }

    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }

    public boolean isSynthetic() {
        return jConstructor.isSynthetic();
    }

}