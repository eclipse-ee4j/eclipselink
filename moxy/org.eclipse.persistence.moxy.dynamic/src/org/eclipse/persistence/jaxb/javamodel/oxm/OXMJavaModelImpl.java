/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Rick Barkhouse - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.javamodel.oxm;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaModel;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaClassImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;

public class OXMJavaModelImpl implements JavaModel {

    private ClassLoader classLoader;
    private Map<String, JavaClass> javaModelClasses = new HashMap<String, JavaClass>();

    public OXMJavaModelImpl(ClassLoader loader, JavaClass[] javaClasses) {
        this.classLoader = loader;

        for (int i = 0; i < javaClasses.length; i++) {
            this.javaModelClasses.put(javaClasses[i].getQualifiedName(), javaClasses[i]);
        }
    }

    public JavaClass getClass(Class<?> jClass) {
        if (jClass == null) {
            return null;
        }

        String className = jClass.getCanonicalName();
        JavaClass cachedClass = this.javaModelClasses.get(className);

        if (cachedClass != null) {
            return cachedClass;
        }

        // try actually finding the class, might be concrete
        try {
            Class<?> realClass = PrivilegedAccessHelper.getClassForName(className);
            if (realClass != null) {
                JavaModelImpl jm = new JavaModelImpl(this.classLoader);
                JavaClassImpl jc = new JavaClassImpl(realClass, jm);
                return jc;
            }
        } catch (Exception e) {
        }

        return new OXMJavaClassImpl(jClass.getCanonicalName());
    }

    public JavaClass getClass(String className) {
        if (className == null) {
            return null;
        }

        // javax.xml.bind.annotation.XmlElement.DEFAULT
        if (className.contains(DEFAULT)) {
        	return getClass(JAVA_LANG_OBJECT);
        }

        JavaClass cachedClass = this.javaModelClasses.get(className);

        if (cachedClass != null) {
            return cachedClass;
        }

        // try actually finding the class, might be concrete
        try {
            Class<?> realClass = PrivilegedAccessHelper.getClassForName(className);
            if (realClass != null) {
                JavaModelImpl jm = new JavaModelImpl(this.classLoader);
                JavaClassImpl jc = new JavaClassImpl(realClass, jm);
                return jc;
            }
        } catch (Exception e) {
        }

        return new OXMJavaClassImpl(className);
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public JaxbClassLoader getJaxbClassLoader() {
        ClassLoader parent = getClassLoader().getParent();
        if (parent instanceof JaxbClassLoader) {
            return (JaxbClassLoader) parent;
        }

        return null;
    }

    public Annotation getAnnotation(JavaAnnotation annotation, Class<?> jClass) {
        return null;
    }

    // ========================================================================
    
    private static String DEFAULT = "DEFAULT";
    private static String JAVA_LANG_OBJECT = "java.lang.Object";
    
    
}