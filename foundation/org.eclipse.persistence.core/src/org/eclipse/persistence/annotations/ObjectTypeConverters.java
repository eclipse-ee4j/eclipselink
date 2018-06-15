/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     08/19/2010-2.2 Guy Pelletier
//       - 282773: Add plural converter annotations
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An ObjectTypeConverters annotation allows the definition of multiple
 * ObjectTypeConverter.
 *
 * @see org.eclipse.persistence.annotations.ObjectTypeConverter
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.2
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface ObjectTypeConverters {
    /**
     * (Required) An array of object type converter.
     */
    ObjectTypeConverter[] value();
}
