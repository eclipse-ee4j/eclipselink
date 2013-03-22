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

import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaPackage;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>A wrapper class for a JDK Package.  This implementation
 * of the TopLink JAXB 2.0 Java model simply makes reflective calls on the
 * underlying JDK object.
 *
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Provide access to the underlying package's qualified name, annotations, etc.</li>
 * </ul>
 *
 * @since Oracle TopLink 11.1.1.0.0
 * @see org.eclipse.persistence.jaxb.javamodel.JavaPackage
 * @see java.lang.Package
 */
public class JavaPackageImpl implements JavaPackage {

    protected Package jPkg;
    protected JavaModelImpl jModelImpl;
    protected Boolean isMetadataComplete;
    
    public JavaPackageImpl(Package javaPackage, JavaModelImpl javaModelImpl) {
        this(javaPackage, javaModelImpl, false);
    }

    public JavaPackageImpl(Package javaPackage, JavaModelImpl javaModelImpl, Boolean isMetadataComplete) {
        jPkg = javaPackage;
        jModelImpl = javaModelImpl;
        this.isMetadataComplete = isMetadataComplete;
    }

    /**
     * Assumes JavaType is a JavaClassImpl instance
     */
    public JavaAnnotation getAnnotation(JavaClass arg0) {
        if (arg0 != null && !isMetadataComplete) {
            Class annotationClass = ((JavaClassImpl) arg0).getJavaClass();
            if (jPkg != null && jModelImpl.getAnnotationHelper().isAnnotationPresent(getAnnotatedElement(), annotationClass)) {
               return new JavaAnnotationImpl(jModelImpl.getAnnotationHelper().getAnnotation(getAnnotatedElement(), annotationClass));
            }
        }
        return null;
    }

    public Collection getAnnotations() {
        ArrayList<JavaAnnotation> annotationCollection = new ArrayList<JavaAnnotation>();
        if(jPkg != null && !isMetadataComplete){
            Annotation[] annotations = jModelImpl.getAnnotationHelper().getAnnotations(getAnnotatedElement());
            for (Annotation annotation : annotations) {
                annotationCollection.add(new JavaAnnotationImpl(annotation));
            }
        }
        return annotationCollection;
    }

    public String getName() {
        if(jPkg != null){
            return jPkg.getName();
        }else{
            return null;
        }
    }

    public String getQualifiedName() {
        if(jPkg != null){
            return jPkg.getName();
        }else{
            return null;
        }
    }
    
    public AnnotatedElement getAnnotatedElement() {
    	return jPkg;
    }

//  ---------------- unimplemented methods ----------------//
    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) {
        return null;
    }

    public Collection getDeclaredAnnotations() {
        return null;
    }

}