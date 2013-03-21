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
 *     Andrei Ilitchev (Oracle), March 7, 2008 
 *        - New file introduced for bug 211300.  
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for org.eclipse.persistence.mappings.TransformationMapping.
 * Unless the TransformationMapping is write-only, it should have a 
 * ReadTransformer, it defines transformation of database column(s) value(s) 
 * into attribute value.
 *  
 * Also unless it's a read-only mapping, either WriteTransformer annotation or 
 * WriteTransformers annotation should be specified. Each WriteTransformer 
 * defines transformation of the attribute value to a single database column 
 * value (column is specified in the WriteTransformer). 
 *
 * @see org.eclipse.persistence.annotations.Transformation
 * @see org.eclipse.persistence.annotations.WriteTransformer
 * @see org.eclipse.persistence.annotations.WriteTransformers
 * 
 * Transformation can be specified within an Entity, MappedSuperclass 
 * and Embeddable class.
 * 
 * @author Andrei Ilitchev
 */ 
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface ReadTransformer {
    /**
     * User-defined class that must implement the 
     * org.eclipse.persistence.mappings.transformers.AttributeTransformer 
     * interface. The class will be instantiated, its buildAttributeValue will 
     * be used to create the value to be assigned to the attribute.
     * Either transformerClass or method must be specified, but not both.
     */ 
    Class transformerClass() default void.class;

    /**
     * The mapped class must have a method with this name which returns a value 
     * to be assigned to the attribute (not assigns the value to the attribute).
     * Either transformerClass or method must be specified, but not both.
     */ 
    String method() default "";
}
