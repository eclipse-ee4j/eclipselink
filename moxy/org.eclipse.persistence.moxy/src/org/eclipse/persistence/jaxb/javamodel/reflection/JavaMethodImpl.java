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
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>A wrapper for a JDK Method.  This implementation
 * of the EclipseLink JAXB 2.X Java model simply makes reflective calls on the
 * underlying JDK object.
 *
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Provide access to the underlying method's name, parameters,
 * modifiers, annotations, etc.</li>
 * </ul>
 *
 * @since Oracle TopLink 11.1.1.0.0
 * @see org.eclipse.persistence.jaxb.javamodel.JavaMethod
 * @see java.lang.reflect.Method
 */
public class JavaMethodImpl implements JavaMethod {

    protected Method jMethod;
    private JavaModelImpl javaModelImpl;
    protected boolean isMetadataComplete;

    public JavaMethodImpl(Method javaMethod, JavaModelImpl javaModelImpl) {
        this(javaMethod, javaModelImpl, false);
    }

    public JavaMethodImpl(Method javaMethod, JavaModelImpl javaModelImpl, Boolean isMetadataComplete) {
        this.jMethod = javaMethod;
        this.javaModelImpl = javaModelImpl;
        if(isMetadataComplete != null){
            this.isMetadataComplete = isMetadataComplete;
        }
    }

    public Collection getActualTypeArguments() {
        ArrayList<JavaClass> argCollection = new ArrayList<JavaClass>();
        Type[] params = jMethod.getGenericParameterTypes();
        for (Type type : params) {
            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) type;
                argCollection.add(new JavaClassImpl(pType, pType.getClass(), javaModelImpl));
            } else if (type instanceof Class) {
                argCollection.add(javaModelImpl.getClass((Class) type));
            }
        }
        return argCollection;
    }

    @Override
    public JavaAnnotation getAnnotation(JavaClass arg0) {
        if (arg0 != null && !isMetadataComplete) {
            Class annotationClass = ((JavaClassImpl) arg0).getJavaClass();
            Annotation anno = javaModelImpl.getAnnotationHelper().getAnnotation(getAnnotatedElement(), annotationClass);
            if (anno != null) {
                return new JavaAnnotationImpl(anno);
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
    public String getName() {
        return jMethod.getName();
    }

    @Override
    public JavaClass[] getParameterTypes() {
        Class[] params = PrivilegedAccessHelper.getMethodParameterTypes(jMethod);
        JavaClass[] paramArray = new JavaClass[params.length];
        for (int i=0; i<params.length; i++) {
            paramArray[i] = javaModelImpl.getClass(params[i]);
        }
        return paramArray;
    }

    public JavaClass getResolvedType() {
        Class returnType = PrivilegedAccessHelper.getMethodReturnType(jMethod);
        return javaModelImpl.getClass(returnType);
    }

    @Override
    public JavaClass getReturnType() {
        Type type = jMethod.getGenericReturnType();
        Class returnType = PrivilegedAccessHelper.getMethodReturnType(jMethod);
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            return new JavaClassImpl(pType, returnType, javaModelImpl);
        }
        return javaModelImpl.getClass(returnType);
    }

    public boolean hasActualTypeArguments() {
        Type[] params = jMethod.getGenericParameterTypes();
        for (Type type : params) {
            if (type instanceof ParameterizedType) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getModifiers() {
        return jMethod.getModifiers();
    }

    @Override
    public JavaClass getOwningClass() {
        return javaModelImpl.getClass(jMethod.getDeclaringClass());
    }

    public AnnotatedElement getAnnotatedElement() {
        return jMethod;
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
        return jMethod.isSynthetic();
    }

    @Override
    public boolean isBridge() {
        return jMethod.isBridge();
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
