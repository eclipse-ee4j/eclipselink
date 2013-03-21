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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaModel;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>The JavaModel is the central access point to the TopLink
 * JAXB 2.0 Java model implementation's source/class files.  A JavaModel has 
 * an underlying source/classpath that defines its search path.
 * 
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Return a JavaClass based on a Class or Class name</li>
 * <li>Return a JDK Annotation for a given JavaAnnotation</li>
 * </ul>
 * 
 * @since Oracle TopLink 11.1.1.0.0
 * @see org.eclipse.persistence.jaxb.javamodel.JavaModel
 */
public class JavaModelImpl implements JavaModel {

    protected ClassLoader classLoader;
    private AnnotationHelper annotationHelper;
    private Map<String, Boolean> metadataCompletePackages;
    private Map<String, JavaClassImpl> cachedJavaClasses;
    
    public JavaModelImpl(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.annotationHelper = new AnnotationHelper();
    }
    
    public JavaModelImpl(ClassLoader classLoader, AnnotationHelper annotationHelper) {
    	this.classLoader = classLoader;
    	this.annotationHelper = annotationHelper;
    }

    public JavaClass getClass(Class<?> jClass) {
        try {
            if(null == jClass) {
                return null;
            }
            JavaClassImpl javaClass = getCachedJavaClasses().get(jClass.getName());
            if(javaClass == null) {
                javaClass = new JavaClassImpl(jClass, this);
                getCachedJavaClasses().put(jClass.getName(), javaClass);
            }
            // may need to set metadata complete indicator on the class
            if (metadataCompletePackages != null && metadataCompletePackages.containsKey(javaClass.getPackageName())) {
                javaClass.setIsMetadataComplete(metadataCompletePackages.get(javaClass.getPackageName()));
            }
            if(classLoader instanceof JaxbClassLoader) {
                ((JaxbClassLoader) classLoader).putClass(jClass.getCanonicalName(), jClass);
            }
            return javaClass;
        } catch (Exception x) {
            return null;
        }
    }

    public JavaClass getClass(String className) {
        try {
            if (className.contains("[")) {
                Class clazz = Class.forName(className);
                if (clazz != null) {
                    return getClass(clazz);
                }
            }
            Class clazz = this.classLoader.loadClass(className);
            return getClass(clazz);
        } catch(ClassNotFoundException e) {
            throw JAXBException.classNotFoundException(className);
        }
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public Annotation getAnnotation(JavaAnnotation janno, Class<?> jClass) {
        return ((JavaAnnotationImpl) janno).getJavaAnnotation();
    }

    public AnnotationHelper getAnnotationHelper() {
    	return this.annotationHelper;
    }
    
    /**
     * Set the Map of package names to metadata complete indicators for this
     * JavaModelInput.  If a given package has no entry in this map it is
     * assumed to be metadata incomplete.
     *   
     * @param metadataCompletePackageMap
     */
    public void setMetadataCompletePackageMap(Map<String, Boolean> metadataCompletePackageMap) {
        this.metadataCompletePackages = metadataCompletePackageMap;
    }
    
    public Map<String, JavaClassImpl> getCachedJavaClasses() {
        if(this.cachedJavaClasses == null) {
            this.cachedJavaClasses = new HashMap<String, JavaClassImpl>();
        }
        return this.cachedJavaClasses;
    }
}