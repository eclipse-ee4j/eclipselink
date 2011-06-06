/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Rick Barkhouse - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.javamodel.xjc;

import java.lang.reflect.Modifier;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaConstructor;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

public class XJCJavaConstructorImpl implements JavaConstructor {

    private JMethod xjcConstructor;
    private JCodeModel jCodeModel;
    private DynamicClassLoader dynamicClassLoader;
    private JavaClass owningClass;

    public XJCJavaConstructorImpl(JMethod constructor, JCodeModel codeModel, DynamicClassLoader loader, JavaClass owner) {
        this.xjcConstructor = constructor;
        this.jCodeModel = codeModel;
        this.dynamicClassLoader = loader;
        this.owningClass = owner;
    }

    public int getModifiers() {
        return xjcConstructor.mods().getValue();
    }

    public String getName() {
        return xjcConstructor.name();
    }

    public JavaClass[] getParameterTypes() {
        JType[] params = xjcConstructor.listParamTypes();
        JavaClass[] paramArray = new JavaClass[params.length];

        for (int i = 0; i < params.length; i++) {
            JavaClass dc;
            if (((XJCJavaClassImpl) getOwningClass()).getJavaModel() != null) {
                dc = ((XJCJavaClassImpl) getOwningClass()).getJavaModel().getClass(params[i].fullName());
            } else {
                dc = new XJCJavaClassImpl((JDefinedClass) params[i], jCodeModel, dynamicClassLoader);
            }

            paramArray[i] = dc;
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
        throw new UnsupportedOperationException("isSynthetic");
    }

    public JavaClass getOwningClass() {
        return owningClass;
    }

    public void setOwningClass(JavaClass owningClass) {
        this.owningClass = owningClass;
    }

}