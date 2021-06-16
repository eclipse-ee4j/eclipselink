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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A TimeOfDay annotation is used to specify a specific time of day using a
 * Calendar instance which is to be used within an OptimisticLocking annotation.
 *
 * @see org.eclipse.persistence.annotations.OptimisticLocking
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0
 */
@Target({})
@Retention(RUNTIME)
public @interface TimeOfDay {
    /**
     * (Optional) Hour of the day.
     */
    int hour() default 0;

    /**
     * (Optional) Minute of the day.
     */
    int minute() default 0;

    /**
     * (Optional) Second of the day.
     */
    int second() default 0;

    /**
     * (Optional) Millisecond of the day.
     */
    int millisecond() default 0;

    /**
     * Internal use. Do not modify.
     */
    boolean specified() default true;
}
