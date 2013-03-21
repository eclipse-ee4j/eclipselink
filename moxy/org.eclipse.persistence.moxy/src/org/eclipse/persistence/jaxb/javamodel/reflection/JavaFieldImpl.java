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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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

    public JavaAnnotation getAnnotation(JavaClass arg0) {
        if (arg0 != null && !isMetadataComplete) {
            Class annotationClass = ((JavaClassImpl) arg0).getJavaClass();
            if (javaModelImpl.getAnnotationHelper().isAnnotationPresent(getAnnotatedElement(), annotationClass)) {
                return new JavaAnnotationImpl(javaModelImpl.getAnnotationHelper().getAnnotation(getAnnotatedElement(), annotationClass));
            }
        }
        return null;
    }

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

    public int getModifiers() {
        return jField.getModifiers();
    }

    public String getName() {
        return jField.getName();
    }

    public JavaClass getOwningClass() {
        return javaModelImpl.getClass(jField.getDeclaringClass());
    }

    public JavaClass getResolvedType() {
        Class fieldType = jField.getType();
        Type genericType = jField.getGenericType();

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) genericType;
            return new JavaClassImpl(pType, (Class) pType.getRawType(), javaModelImpl);
        }
        return javaModelImpl.getClass(fieldType);
    }

    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }

    public boolean isSynthetic() {
        return jField.isSynthetic();
    }
    
    public AnnotatedElement getAnnotatedElement() {
    	return jField;
    }
    public Object get(Object obj) throws IllegalAccessException {    	
    	return PrivilegedAccessHelper.getValueFromField(jField, obj);
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

    public boolean isEnumConstant() {
        return jField.isEnumConstant();
    }

    //  ---------------- unimplemented methods ----------------//
    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) {
        return null;
    }

    public Collection getDeclaredAnnotations() {
        return null;
    }
}