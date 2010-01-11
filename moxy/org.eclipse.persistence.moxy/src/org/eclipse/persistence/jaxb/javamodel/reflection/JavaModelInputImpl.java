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

import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaModel;
import org.eclipse.persistence.jaxb.javamodel.JavaModelInput;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide access to an array of JavaClass instances 
 * and their associated JavaModel.  This class will transform an array 
 * of Class objects to an array of JavaClasses.
 * 
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Create an array of JavaClass instances from an array of Classes</li>
 * <li>Return an array of JavaClass objects to be used by the generator</li>
 * <li>Return the JavaModel to be used during generation</li>
 * </ul>
 * 
 * @since Oracle TopLink 11.1.1.0.0
 * @see org.eclipse.persistence.jaxb.javamodel.JavaClass 
 * @see org.eclipse.persistence.jaxb.javamodel.JavaModel 
 * @see org.eclipse.persistence.jaxb.javamodel.JavaModelInput 
 */
public class JavaModelInputImpl implements JavaModelInput {
    private JavaClass[] jClasses;
    private JavaModel jModel;
    
    public JavaModelInputImpl(Class[] classes, JavaModel javaModel) {
        jClasses = new JavaClass[classes.length];
        for (int i=0; i<classes.length; i++) {
            jClasses[i] = new JavaClassImpl(classes[i]);            
        }
        jModel = javaModel;
    }
    
    public JavaClass[] getJavaClasses() {
        return jClasses;
    }
    
    public JavaModel getJavaModel() {
        return jModel;
    }
}
