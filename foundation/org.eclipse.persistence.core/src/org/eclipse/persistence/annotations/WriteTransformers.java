/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An optional annotation for org.eclipse.persistence.mappings.TransformationMapping.
 * A single WriteTransformer may be specified directly on the method or field,
 * multiple WriteTransformers should be wrapped into WriteTransformers annotation.
 * No WriteTransformers specified for read-only mapping.
 *
 * @see org.eclipse.persistence.annotations.Transformation
 * @see org.eclipse.persistence.annotations.ReadTransformer
 * @see org.eclipse.persistence.annotations.WriteTransformer
 *
 * Transformation can be specified within an Entity, MappedSuperclass
 * and Embeddable class.
 *
 * @author Andrei Ilitchev
 * @since EclipseLink 1.0
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface WriteTransformers {
    WriteTransformer[] value();
}
