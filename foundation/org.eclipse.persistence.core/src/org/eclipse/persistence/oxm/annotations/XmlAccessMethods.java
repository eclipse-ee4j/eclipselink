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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * <b>Purpose:</b>This annotation allows the userTo specify accessor methods for
 * a given property. The methods specified here will be used to set values in the object
 * during unmarshal and to obtain the value from the object during marshal.
 *
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlAccessMethods {
    String getMethodName() default "";
    String setMethodName() default "";
}
