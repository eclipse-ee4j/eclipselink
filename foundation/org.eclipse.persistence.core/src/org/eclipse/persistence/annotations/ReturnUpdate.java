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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A ReturnUpdate annotation allows for UPDATE operations to return values back
 * into the object being written. This allows for table default values, trigger
 * or stored procedures computed values to be set back into the object.
 *
 * A ReturnUpdate annotation can only be specified on a Basic mapping.
 *
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface ReturnUpdate {}
