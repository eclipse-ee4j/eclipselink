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

import javax.persistence.Column;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for org.eclipse.persistence.mappings.TransformationMapping.
 * WriteTransformer defines transformation of the attribute value to a single
 * database column value (column is specified in the WriteTransformer).
 *  
 * A single WriteTransformer may be specified directly on the method or 
 * attribute. Multiple WriteTransformers should be wrapped into 
 * WriteTransformers annotation. No WriteTransformers specified for read-only 
 * mapping. Unless the TransformationMapping is write-only, it should have a 
 * ReadTransformer, it defines transformation of database column(s) value(s) 
 * into attribute value.
 *
 * @see org.eclipse.persistence.annotations.ReadTransformer
 * @see org.eclipse.persistence.annotations.Transformation
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
public @interface WriteTransformer {
    /**
     * User-defined class that must implement the
     * org.eclipse.persistence.mappings.transformers.FieldTransformer interface.
     * The class will be instantiated, its buildFieldValue will be used to 
     * create the value to be written into the database column.
     * Note that for ddl generation and returning to be supported the method 
     * buildFieldValue in the class should be defined to return the relevant 
     * Java type, not just Object as defined in the interface,
     * for instance:
     * public Time buildFieldValue(Object instance, String fieldName, Session session).
     * Either transformerClass or method must be specified, but not both.
     */ 
    Class transformerClass() default void.class;

    /**
     * The mapped class must have a method with this name which returns a value 
     * to be written into the database column.
     * Note that for ddl generation and returning to be supported the method
     * should be defined to return a particular type, not just Object, for 
     * instance:
     * public Time getStartTime().
     * The method may require an Transient annotation to avoid being mapped as 
     * a Basic by default.
     * Either transformerClass or method must be specified, but not both.
     */ 
    String method() default "";

    /**
     * Specify here the column into which the value should be written.
     * The only case when this could be skipped is if a single WriteTransformer
     * annotates an attribute - the attribute's name will be
     * used as a column name.
     */ 
    Column column() default @Column;
}
