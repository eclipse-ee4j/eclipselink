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
 * XmlTransformation is an optional annotation for {@linkplain org.eclipse.persistence.oxm.mappings.XMLTransformationMapping}.
 * {@linkplain org.eclipse.persistence.oxm.mappings.XMLTransformationMapping} allows to map an attribute to one or more database columns.
 * <p>
 * XmlTransformation annotation is an optional part of {@linkplain org.eclipse.persistence.oxm.mappings.XMLTransformationMapping} definition.
 * Unless the {@linkplain org.eclipse.persistence.oxm.mappings.XMLTransformationMapping} is write-only, it should have a {@linkplain XmlReadTransformer},
 * defining transformation of xml data value(s)into attribute value.
 * Also, unless it's a read-only mapping, either {@linkplain XmlWriteTransformer} annotation or {@linkplain XmlWriteTransformers} annotation
 * should be specified. Each {@linkplain XmlWriteTransformer} defines transformation of the attribute value to a single
 * xml field value (XPath is specified in the {@linkplain XmlWriteTransformer}).
 *
 * @see org.eclipse.persistence.oxm.annotations.XmlReadTransformer
 * @see org.eclipse.persistence.oxm.annotations.XmlWriteTransformer
 * @see org.eclipse.persistence.oxm.annotations.XmlWriteTransformers
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XmlTransformation {
    /**
     *  The optional element is a hint whether the value
     *  of the field or property may be null. It is disregarded
     *  for primitive types, which are considered non-optional.
     */
    boolean optional() default true;
}
