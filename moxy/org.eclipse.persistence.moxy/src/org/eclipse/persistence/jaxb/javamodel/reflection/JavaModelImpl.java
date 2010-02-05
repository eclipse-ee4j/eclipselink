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

import java.lang.annotation.Annotation;

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

    private ClassLoader classLoader;

    public JavaModelImpl(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public JavaClass getClass(Class jClass) {
        try {
            return new JavaClassImpl(jClass);
        } catch (Exception x) {
            return null;
        }
    }

    public JavaClass getClass(String classname) {
        try {
            return new JavaClassImpl(classLoader.loadClass(classname));
        } catch (Exception x) {
            return null;
        }
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public Annotation getAnnotation(JavaAnnotation janno, Class jClass) {
        return ((JavaAnnotationImpl) janno).getJavaAnnotation();
    }

}