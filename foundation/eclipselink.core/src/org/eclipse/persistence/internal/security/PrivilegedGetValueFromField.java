/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.security;

import java.lang.reflect.Field;
import java.security.PrivilegedExceptionAction;


public class PrivilegedGetValueFromField implements PrivilegedExceptionAction {

    private Field field;
    private Object object;
    
    public PrivilegedGetValueFromField(Field field, Object object) {
        this.field = field;
        this.object = object;
    }

    public Object run() throws IllegalAccessException {
        return PrivilegedAccessHelper.getValueFromField(field, object);
    }

}
