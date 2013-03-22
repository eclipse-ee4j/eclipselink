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

import javax.persistence.FetchType;

import static javax.persistence.FetchType.EAGER;

/**
 * Transformation is an optional annotation for org.eclipse.persistence.mappings.TransformationMapping.
 * TransformationMapping allows to map an attribute to one or more database columns.
 * 
 * Transformation annotation is an optional part of TransformationMapping definition.
 * Unless the TransformationMapping is write-only, it should have a ReadTransformer,
 * it defines transformation of database column(s) value(s)into attribute value.
 * Also unless it's a read-only mapping, either WriteTransformer annotation or WriteTransformers annotation
 * should be specified. Each WriteTransformer defines transformation of the attribute value to a single
 * database column value (column is specified in the WriteTransformer). 
 * 
 * @see org.eclipse.persistence.annotations.ReadTransformer
 * @see org.eclipse.persistence.annotations.WriteTransformer
 * @see org.eclipse.persistence.annotations.WriteTransformers
 * 
 * Transformation can be specified within an Entity, MappedSuperclass 
 * and Embeddable class.
 * 
 * @author Andrei Ilitchev
 * @since EclipseLink 1.0 
 */ 
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Transformation {
    /**
     * (Optional) Defines whether the value of the field or property should
     * be lazily loaded or must be eagerly fetched. The EAGER strategy is a 
     * requirement on the persistence provider runtime that the value must be 
     * eagerly fetched. The LAZY strategy is a hint to the persistence provider 
     * runtime. If not specified, defaults to EAGER.
     */
    FetchType fetch() default EAGER;
    
    /**
     * (Optional) The optional element is a hint as to whether the value
     *  of the field or property may be null. It is disregarded
     *  for primitive types, which are considered non-optional.
     */
    boolean optional() default true;
}
