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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jaxb.javamodel.reflection;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;

import java.lang.annotation.Annotation;
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

    public JavaMethodImpl(Method javaMethod) {
        jMethod = javaMethod;
    }

    public Collection getActualTypeArguments() {
        ArrayList<JavaClassImpl> argCollection = new ArrayList<JavaClassImpl>();
        Type[] params = jMethod.getGenericParameterTypes();
        for (Type type : params) {
            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) type;
                argCollection.add(new JavaClassImpl(pType, pType.getClass()));
            } else if (type instanceof Class) {
                argCollection.add(new JavaClassImpl((Class) type));
            }
        }
        return argCollection;
    }

    public JavaAnnotation getAnnotation(JavaClass arg0) {
        if (arg0 != null) {
            Class annotationClass = ((JavaClassImpl) arg0).getJavaClass();
            if (jMethod.isAnnotationPresent(annotationClass)) {
                return new JavaAnnotationImpl(jMethod.getAnnotation(annotationClass));
            }
        }
        return null;
    }

    public Collection getAnnotations() {
        ArrayList<JavaAnnotation> annotationCollection = new ArrayList<JavaAnnotation>();
        Annotation[] annotations = jMethod.getAnnotations();
        for (Annotation annotation : annotations) {
            annotationCollection.add(new JavaAnnotationImpl(annotation));
        }
        return annotationCollection;
    }

    public String getName() {
        return jMethod.getName();
    }

    public JavaClass[] getParameterTypes() {
        Class[] params = PrivilegedAccessHelper.getMethodParameterTypes(jMethod);
        JavaClass[] paramArray = new JavaClass[params.length];
        for (int i=0; i<params.length; i++) {
            paramArray[i] = new JavaClassImpl(params[i]);
        }
        return paramArray;
    }

    public JavaClass getResolvedType() {
        Class returnType = PrivilegedAccessHelper.getMethodReturnType(jMethod);
        return new JavaClassImpl(returnType);
    }

    public JavaClass getReturnType() {
        Type type = jMethod.getGenericReturnType();
        Class returnType = PrivilegedAccessHelper.getMethodReturnType(jMethod);
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            return new JavaClassImpl(pType, returnType);
        }
        return new JavaClassImpl(returnType);
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

    public int getModifiers() {
        return jMethod.getModifiers();
    }

    public JavaClass getOwningClass() {
        return new JavaClassImpl(jMethod.getDeclaringClass());
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
        return jMethod.isSynthetic();
    }

//  ---------------- unimplemented methods ----------------//
    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) {
        return null;
    }

    public Collection getDeclaredAnnotations() {
        return null;
    }

}