/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
