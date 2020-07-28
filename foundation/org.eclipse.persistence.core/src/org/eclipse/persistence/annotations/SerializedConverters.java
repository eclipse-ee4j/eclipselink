/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A TypeConverters annotation allows the definition of multiple SerializedConverter.
 *
 * @see org.eclipse.persistence.annotations.SerializedConverter
 *
 * @author James Sutherland
 * @since EclipseLink 2.6
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface SerializedConverters {
    /**
     * (Required) An array of type converter.
     */
    SerializedConverter[] value();
}
