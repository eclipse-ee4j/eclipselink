/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     mmacivor - March 12/2009 - Initial implementation

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

    @Override
    public int getModifiers() {
        return jConstructor.getModifiers();
    }

    @Override
    public String getName() {
        return jConstructor.getName();
    }

    @Override
    public JavaClass getOwningClass() {
        return javaModelImpl.getClass(jConstructor.getDeclaringClass());
    }

    @Override
    public JavaClass[] getParameterTypes() {
        Class[] params = jConstructor.getParameterTypes();
        JavaClass[] paramArray = new JavaClass[params.length];
        for (int i=0; i<params.length; i++) {
            paramArray[i] = javaModelImpl.getClass(params[i]);
        }
        return paramArray;
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(getModifiers());
    }

    @Override
    public boolean isPrivate() {
        return Modifier.isPrivate(getModifiers());
    }

    @Override
    public boolean isProtected() {
        return Modifier.isProtected(getModifiers());
    }

    @Override
    public boolean isPublic() {
        return Modifier.isPublic(getModifiers());
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(getModifiers());
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }

    @Override
    public boolean isSynthetic() {
        return jConstructor.isSynthetic();
    }

}
