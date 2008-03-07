/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.persistence.Column;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An optional annotation for org.eclipse.persistence.mappings.TransformationMapping.
 * WriteTransformer defines a function that applied to the attribute value produces
 * the value to be inserted in the database's column.
 * A single WriteTransformer may be specified directly on the method or field,
 * multiple WriteTransformers should be wrapped into WriteTransformers annotation.
 * No WriteTransformers specified for read-only mapping. 
 */ 
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface WriteTransformer {
    /**
     * User-defined class implementing org.eclipse.persistence.mappings.transformers.FieldTransformer interface.
     * The class will be instantiated, its buildFieldValue will be used to create the value to be written into
     * the database column.
     * Either transformerClass or method should be specified, but not both.
     */ 
    Class transformerClass() default void.class;

    /**
     * The mapped class must have a method with this name which returns a value to be written into
     * the database column.
     * Either transformerClass or method should be specified, but not both.
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
