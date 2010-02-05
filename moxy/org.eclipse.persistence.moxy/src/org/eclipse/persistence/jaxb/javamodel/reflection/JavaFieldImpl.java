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

import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaField;

import java.lang.annotation.Annotation;
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

    public JavaFieldImpl(Field javaField) {
        jField = javaField;
    }

    public JavaAnnotation getAnnotation(JavaClass arg0) {
        if (arg0 != null) {
            Class annotationClass = ((JavaClassImpl) arg0).getJavaClass();
            if (jField.isAnnotationPresent(annotationClass)) {
                return new JavaAnnotationImpl(jField.getAnnotation(annotationClass));
            }
        }
        return null;
    }

    public Collection getAnnotations() {
        ArrayList<JavaAnnotation> annotationCollection = new ArrayList<JavaAnnotation>();
        Annotation[] annotations = jField.getAnnotations();
        for (Annotation annotation : annotations) {
            annotationCollection.add(new JavaAnnotationImpl(annotation));
        }
        return annotationCollection;
    }

    public int getModifiers() {
        return jField.getModifiers();
    }

    public String getName() {
        return jField.getName();
    }

    public JavaClass getOwningClass() {
        return new JavaClassImpl(jField.getDeclaringClass());
    }

    public JavaClass getResolvedType() {
        Class fieldType = jField.getType();
        Type genericType = jField.getGenericType();

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) genericType;
            return new JavaClassImpl(pType, (Class) pType.getRawType());
        }
        return new JavaClassImpl(fieldType);
    }

    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }

    public boolean isSynthetic() {
        return jField.isSynthetic();
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

//  ---------------- unimplemented methods ----------------//
    public boolean isEnumConstant() {
        return jField.isEnumConstant();
    }

    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) {
        return null;
    }

    public Collection getDeclaredAnnotations() {
        return null;
    }

}