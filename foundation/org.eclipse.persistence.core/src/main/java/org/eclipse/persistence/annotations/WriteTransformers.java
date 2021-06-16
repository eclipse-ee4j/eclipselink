/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
