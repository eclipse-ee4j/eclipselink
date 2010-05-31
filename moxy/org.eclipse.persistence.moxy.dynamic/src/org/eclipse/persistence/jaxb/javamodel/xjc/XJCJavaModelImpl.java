/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-12-18 13:04:58 - EclipseLink 2.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.javamodel.xjc;

import java.lang.annotation.Annotation;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaModel;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;

public class XJCJavaModelImpl implements JavaModel {

    private JCodeModel jCodeModel;
    private ClassLoader classLoader;
    private DynamicClassLoader dynamicClassLoader;

    public XJCJavaModelImpl(ClassLoader loader, JCodeModel codeModel, DynamicClassLoader dynLoader) {
        this.jCodeModel = codeModel;
        this.classLoader = loader;
        this.dynamicClassLoader = dynLoader;
    }

    public JavaClass getClass(Class<?> jClass) {
        if (jClass == null) {
            return null;
        }

        try {
            return new XJCJavaClassImpl(jCodeModel._class(jClass.getCanonicalName()), jCodeModel, dynamicClassLoader);
        } catch (JClassAlreadyExistsException ex) {
            return new XJCJavaClassImpl(jCodeModel._getClass(jClass.getCanonicalName()), jCodeModel, dynamicClassLoader);
        }
    }

    public JavaClass getClass(String className) {
        try {
            return new XJCJavaClassImpl(jCodeModel._class(className), jCodeModel, dynamicClassLoader);
        } catch (JClassAlreadyExistsException ex) {
            return new XJCJavaClassImpl(jCodeModel._getClass(className), jCodeModel, dynamicClassLoader);
        }
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public Annotation getAnnotation(JavaAnnotation annotation, Class<?> jClass) {
        return ((XJCJavaAnnotationImpl) annotation).getJavaAnnotation();
    }

}