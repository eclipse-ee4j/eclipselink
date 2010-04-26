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

import java.lang.reflect.Type;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaModel;
import org.eclipse.persistence.jaxb.javamodel.JavaModelInput;

public class XJCJavaModelInputImpl implements JavaModelInput {

    private JavaClass[] jClasses;
    private JavaModel jModel;

    public XJCJavaModelInputImpl(JavaClass[] types, JavaModel javaModel) {
        this.jModel = javaModel;
        this.jClasses = types;
    }

    public XJCJavaModelInputImpl(Type[] types, JavaModel javaModel) {
        this.jModel = javaModel;
        this.jClasses = new JavaClass[types.length];
        for (int i = 0; i < types.length; i++) {
            TypeMappingInfo typeMappingInfo = new TypeMappingInfo();
            Type type = types[i];
            typeMappingInfo.setType(type);
            jClasses[i] = jModel.getClass((Class<?>) type);
        }
    }

    public XJCJavaModelInputImpl(TypeMappingInfo[] types, JavaModel javaModel) {
        this.jModel = javaModel;
        this.jClasses = new JavaClass[types.length];
        for (int i = 0; i < types.length; i++) {
            TypeMappingInfo typeMappingInfo = types[i];
            Type type = typeMappingInfo.getType();
            jClasses[i] = jModel.getClass((Class<?>) type);
        }
    }

    public XJCJavaModelInputImpl(Class<?>[] classes, JavaModel javaModel) {
        this.jModel = javaModel;
        this.jClasses = new JavaClass[classes.length];
        for (int i=0; i<classes.length; i++) {
            jClasses[i] = jModel.getClass(classes[i]);
        }
    }

    public JavaClass[] getJavaClasses() {
        return jClasses;
    }

    public JavaModel getJavaModel() {
        return jModel;
    }

}