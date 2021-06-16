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
//     06/18/2009 Andrei Ilitchev
//       - JPA 2.0 - OrderedList support.
package org.eclipse.persistence.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * OrderCorrection annotation may be specified together with OrderColumn annotation.
 * Its OrderCorrectionType value defines what should be done in case
 * the order list read from the data base is invalid
 * (has nulls, duplicates, negative values, values greater/equal to list size -
 * the only valid order list of n elements is: {0, 1,..., n-1}).
 *
 * If the annotation is not specified than OrderCorrectionValue.READ_WRITE used.
 *
 * @see "org.eclipse.persistence.jpa.config.OrderColumn"
 * @see org.eclipse.persistence.annotations.OrderCorrectionType
 *
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface OrderCorrection {
    OrderCorrectionType value();
}
