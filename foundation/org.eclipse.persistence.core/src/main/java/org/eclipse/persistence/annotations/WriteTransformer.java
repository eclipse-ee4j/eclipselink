/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Andrei Ilitchev (Oracle), March 7, 2008
//        - New file introduced for bug 211300.
//     06/12/2017-2.7 Lukas Jungmann
//       - 518155: [jpa22] add support for repeatable annotations
package org.eclipse.persistence.annotations;

import jakarta.persistence.Column;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for {@linkplain org.eclipse.persistence.mappings.TransformationMapping}.
 * WriteTransformer defines transformation of the attribute value to a single
 * database column value (column is specified in the WriteTransformer).
 * <p>
 * One or more WriteTransformer annotations may be specified directly on the method or
 * attribute. Multiple occurrences of {@linkplain WriteTransformer} annotation
 * can also be wrapped into {@linkplain WriteTransformers} annotation. No WriteTransformers specified for read-only
 * mapping. Unless the {@linkplain org.eclipse.persistence.mappings.TransformationMapping} is write-only,
 * it should have a {@linkplain ReadTransformer} defining transformation of database column(s) value(s)
 * into attribute value.
 * <p>
 * Transformation can be specified within an Entity, MappedSuperclass
 * and Embeddable class.
 *
 * @see ReadTransformer
 * @see Transformation
 * @see WriteTransformers
 * @author Andrei Ilitchev
 * @since EclipseLink 1.0
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Repeatable(WriteTransformers.class)
public @interface WriteTransformer {
    /**
     * User-defined class that must implement the
     * {@linkplain org.eclipse.persistence.mappings.transformers.FieldTransformer} interface.
     * The class will be instantiated, its {@linkplain org.eclipse.persistence.mappings.transformers.FieldTransformer#buildFieldValue(Object, String, org.eclipse.persistence.sessions.Session)}
     * will be used to create the value to be written into the database column.
     * <p>
     * Note that for ddl generation and returning to be supported the method
     * {@linkplain org.eclipse.persistence.mappings.transformers.FieldTransformer#buildFieldValue(Object, String, org.eclipse.persistence.sessions.Session)}
     * in the class should be defined to return the relevant Java type, not just Object as defined in the interface,
     * for instance:
     * {@snippet :
     *  public Time buildFieldValue(Object instance, String fieldName, org.eclipse.persistence.sessions.Session session) { ... }
     * }
     * <p>
     * Either transformerClass or {@linkplain #method()}  must be specified, but not both.
     */
    Class<?> transformerClass() default void.class;

    /**
     * The mapped class must have a method with this name which returns a value
     * to be written into the database column.
     * <p>
     * Note that for ddl generation and returning to be supported the method
     * should be defined to return a particular type, not just Object, for instance:
     * {@snippet :
     *  public Time getStartTime() { ... }
     * }
     * <p>
     * The method may require an {@linkplain jakarta.persistence.Transient} annotation to avoid being mapped as
     * a Basic by default.
     * <p>
     * Either {@linkplain #transformerClass()} or method must be specified, but not both.
     */
    String method() default "";

    /**
     * Specify here the column into which the value should be written.
     * <p>
     * The only case when this could be skipped is if a single WriteTransformer
     * annotates an attribute - the attribute's name will be
     * used as a column name.
     */
    Column column() default @Column;
}
