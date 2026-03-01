/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle = 2.2 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
/**
 * Annotation used to wrap multiple {@linkplain XmlWriteTransformer} annotations. Used in conjunction with
 * {@linkplain XmlWriteTransformer} and {@linkplain XmlTransformation} to specify multiple Field Transformers for a given
 * property.
 *
 * @see XmlReadTransformer
 * @see XmlWriteTransformer
 * @see XmlTransformation
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XmlWriteTransformers {
    /**
     * An array of XmlWriteTransformer annotations.
     */
    XmlWriteTransformer[] value() default {};
}
