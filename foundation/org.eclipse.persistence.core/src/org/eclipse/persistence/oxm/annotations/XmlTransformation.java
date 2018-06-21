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
//     Oracle = 2.2 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Transformation is an optional annotation for org.eclipse.persistence.mappings.oxm.XMLTransformationMapping.
 * TransformationMapping allows to map an attribute to one or more database columns.
 *
 * Transformation annotation is an optional part of TransformationMapping definition.
 * Unless the TransformationMapping is write-only, it should have a ReadTransformer,
 * it defines transformation of xml data value(s)into attribute value.
 * Also unless it's a read-only mapping, either WriteTransformer annotation or WriteTransformers annotation
 * should be specified. Each WriteTransformer defines transformation of the attribute value to a single
 * xml field value (XPath is specified in the WriteTransformer).
 *
 * @see org.eclipse.persistence.oxm.annotations.XmlReadTransformer
 * @see org.eclipse.persistence.oxm.annotations.XmlWriteTransformer
 * @see org.eclipse.persistence.oxm.annotations.XmlWriteTransformers
 *
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XmlTransformation {
    /**
     * (Optional) The optional element is a hint as to whether the value
     *  of the field or property may be null. It is disregarded
     *  for primitive types, which are considered non-optional.
     */
    boolean optional() default true;
}
