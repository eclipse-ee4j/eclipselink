/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Matt MacIvor = 2.1 - Initial contribution
 ******************************************************************************/
package org.eclipse.persistence.jaxb.javamodel.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.HashMap;

/**
 * <p><b>Purpose:</b>Provide a class which is responsible for returning Annotations
 * from AnnotatedElements. This class can be extended in the case that the annotation 
 * data is being provided from an external source.
 * 
 * @author mmacivor
 *
 */
public class AnnotationHelper {

    private HashMap<AnnotatedElement, HashMap<Class, Annotation>> allAnnotationsMap =
            new HashMap<AnnotatedElement, HashMap<Class, Annotation>>();

    /**
     * Get an annotation of type annotationClass if it's present on the AnnotatedElement
     * elem.
     */
    public Annotation getAnnotation(AnnotatedElement elem, Class annotationClass) {
        if (allAnnotationsMap.get(elem) == null) {
            getAnnotationsFromElement(elem);
        }
        return allAnnotationsMap.get(elem).get(annotationClass);
    }

    /**
     * Get all annotations that exist on the AnnotatedElement elem
     */
    public Annotation[] getAnnotations(AnnotatedElement elem) {
        if (allAnnotationsMap.get(elem) == null) {
            getAnnotationsFromElement(elem);
        }
        Collection<Annotation> annos = allAnnotationsMap.get(elem).values();
        return (Annotation[]) annos.toArray(new Annotation[annos.size()]);
    }

    /**
     * Return true if the annotation annotationClass exists on the annotatedElement elem.
     */
    public boolean isAnnotationPresent(AnnotatedElement elem, Class annotationClass) {
        if (allAnnotationsMap.get(elem) == null) {
            getAnnotationsFromElement(elem);
        }
        return allAnnotationsMap.get(elem).keySet().contains(annotationClass);
    }

    private void getAnnotationsFromElement(AnnotatedElement elem) {
        HashMap<Class, Annotation> annotationsMap = new HashMap<Class, Annotation>();
        Annotation[] annotations = elem.getAnnotations();
        for (Annotation annotation : annotations) {
            annotationsMap.put(annotation.annotationType(), annotation);
        }
        allAnnotationsMap.put(elem, annotationsMap);
    }

}
