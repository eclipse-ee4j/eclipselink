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
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>A wrapper class for a JDK Annotation.  This implementation
 * of the TopLink JAXB 2.0 Java model simply makes reflective calls on the
 * underlying JDK object - in this case the Annotation itself is returned.
 *
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Provide access to the underlying JDK Annotation</li>
 * </ul>
 *
 * @since Oracle TopLink 11.1.1.0.0
 * @see org.eclipse.persistence.jaxb.javamodel.JavaAnnotation
 * @see java.lang.annotation.Annotation
 */
public class JavaAnnotationImpl implements JavaAnnotation {

    Annotation jAnnotation;

    public JavaAnnotationImpl(Annotation javaAnnotation) {
        jAnnotation = javaAnnotation;
    }

    public Annotation getJavaAnnotation() {
        return jAnnotation;
    }

//  ---------------- unimplemented methods ----------------//
    public Map getComponents() {
        // the reflection implementation uses the underlying
        // annotation directly - not needed
        return null;
    }

    public String getName() {
        if (jAnnotation == null) {
            return null;
        } else {
            return jAnnotation.annotationType().getName();
        }
    }

}