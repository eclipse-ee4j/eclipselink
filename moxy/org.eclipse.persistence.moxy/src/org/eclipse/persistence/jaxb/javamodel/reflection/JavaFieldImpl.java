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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jaxb.javamodel.reflection;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaField;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>A wrapper class for a JDK Field.  This implementation
 * of the TopLink JAXB 2.0 Java model simply makes reflective calls on the
 * underlying JDK object.
 *
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Provide access to the underlying field's name, type,
 * modifiers, annotations, etc.</li>
 * </ul>
 *
 * @since Oracle TopLink 11.1.1.0.0
 * @see org.eclipse.persistence.jaxb.javamodel.JavaField
 * @see java.lang.reflect.Field
 */
public class JavaFieldImpl implements JavaField {

    protected Field jField;
    private JavaModelImpl javaModelImpl;
    protected boolean isMetadataComplete;

    public JavaFieldImpl(Field javaField, JavaModelImpl javaModelImpl) {
        this(javaField, javaModelImpl, false);
    }

    public JavaFieldImpl(Field javaField, JavaModelImpl javaModelImpl, Boolean isMetadataComplete) {
        this.jField = javaField;
        this.javaModelImpl = javaModelImpl;
        if(isMetadataComplete != null){
            this.isMetadataComplete = isMetadataComplete;
        }
    }

    @Override
    public JavaAnnotation getAnnotation(JavaClass arg0) {
        if (arg0 != null && !isMetadataComplete) {
            Class annotationClass = ((JavaClassImpl) arg0).getJavaClass();
            if (javaModelImpl.getAnnotationHelper().isAnnotationPresent(getAnnotatedElement(), annotationClass)) {
                return new JavaAnnotationImpl(javaModelImpl.getAnnotationHelper().getAnnotation(getAnnotatedElement(), annotationClass));
            }
        }
        return null;
    }

    @Override
    public Collection getAnnotations() {
        ArrayList<JavaAnnotation> annotationCollection = new ArrayList<JavaAnnotation>();
        if (!isMetadataComplete) {
            Annotation[] annotations = javaModelImpl.getAnnotationHelper().getAnnotations(getAnnotatedElement());
            for (Annotation annotation : annotations) {
                annotationCollection.add(new JavaAnnotationImpl(annotation));
            }
        }
        return annotationCollection;
    }

    @Override
    public int getModifiers() {
        return jField.getModifiers();
    }

    @Override
    public String getName() {
        return jField.getName();
    }

    public JavaClass getOwningClass() {
        return javaModelImpl.getClass(jField.getDeclaringClass());
    }

    @Override
    public JavaClass getResolvedType() {
        Class fieldType = jField.getType();
        Type genericType = jField.getGenericType();

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) genericType;
            return new JavaClassImpl(pType, (Class) pType.getRawType(), javaModelImpl);
        }
        return javaModelImpl.getClass(fieldType);
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }

    @Override
    public boolean isSynthetic() {
        return jField.isSynthetic();
    }

    public AnnotatedElement getAnnotatedElement() {
        return jField;
    }
    public Object get(Object obj) throws IllegalAccessException {
        return PrivilegedAccessHelper.getValueFromField(jField, obj);
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
    public boolean isEnumConstant() {
        return jField.isEnumConstant();
    }

    //  ---------------- unimplemented methods ----------------//
    @Override
    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) {
        return null;
    }

    @Override
    public Collection getDeclaredAnnotations() {
        return null;
    }
}
