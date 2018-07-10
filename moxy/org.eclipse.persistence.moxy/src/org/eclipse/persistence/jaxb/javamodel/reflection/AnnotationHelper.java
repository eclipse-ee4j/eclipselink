/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// Matt MacIvor = 2.1 - Initial contribution
package org.eclipse.persistence.jaxb.javamodel.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;


/**
 * <p><b>Purpose:</b>Provide a class which is responsible for returning Annotations
 * from AnnotatedElements. This class can be extended in the case that the annotation
 * data is being provided from an external source.
 *
 * @author mmacivor
 *
 */
public class AnnotationHelper {

    /**
     * Get an annotation of type annotationClass if it's present on the AnnotatedElement
     * elem.
     */
    public Annotation getAnnotation(AnnotatedElement elem, Class annotationClass) {
       return elem.getAnnotation(annotationClass);
    }

    /**
     * Get all annotations that exist on the AnnotatedElement elem
     */
    public Annotation[] getAnnotations(AnnotatedElement elem) {
       return elem.getAnnotations();
    }

    /**
     * Return true if the annotation annotationClass exists on the annotatedElement elem.
     */
    public boolean isAnnotationPresent(AnnotatedElement elem, Class annotationClass) {
        return elem.isAnnotationPresent(annotationClass);
    }

    /**
     * Get all annotations that are defined directly on the AnnotatedElement
     * (excluding inherited annotations).
     */
    public Annotation[] getDeclaredAnnotations(AnnotatedElement elem) {
       return elem.getDeclaredAnnotations();
    }

}
