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
//     Oracle = 2.2 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <b>Purpose:</b> An annotation representing a parameter to a method. Used with
 * XmlIsSetNullPolicy to specify parameters for the isSet method.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlParameter {
    String value();
    Class type();
}
