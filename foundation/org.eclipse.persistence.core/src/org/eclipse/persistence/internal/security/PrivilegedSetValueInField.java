/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.security;

import java.lang.reflect.Field;
import java.security.PrivilegedExceptionAction;

public class PrivilegedSetValueInField implements PrivilegedExceptionAction {

    private final Field field;
    private final Object object;
    private final Object value;

    public PrivilegedSetValueInField(Field field, Object object, Object value){
        this.field = field;
        this.object = object;
        this.value = value;
    }

    @Override
    public Object run() throws IllegalAccessException {
        PrivilegedAccessHelper.setValueInField(field, object, value);
        return null;
    }

}

