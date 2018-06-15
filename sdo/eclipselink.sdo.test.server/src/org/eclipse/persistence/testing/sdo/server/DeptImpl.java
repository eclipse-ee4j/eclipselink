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
//     etang - April 12/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.sdo.server;

import org.eclipse.persistence.sdo.SDODataObject;

public class DeptImpl extends SDODataObject implements Dept {

    public static final int START_PROPERTY_INDEX = 0;

    public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 3;

    public DeptImpl() {
    }

    public java.lang.Integer getDeptno() {
        return new Integer(getInt(START_PROPERTY_INDEX + 0));
    }

    public void setDeptno(java.lang.Integer value) {
        set(START_PROPERTY_INDEX + 0, value);
    }

    public java.lang.String getDname() {
        return getString(START_PROPERTY_INDEX + 1);
    }

    public void setDname(java.lang.String value) {
        set(START_PROPERTY_INDEX + 1, value);
    }

    public java.lang.String getLoc() {
        return getString(START_PROPERTY_INDEX + 2);
    }

    public void setLoc(java.lang.String value) {
        set(START_PROPERTY_INDEX + 2, value);
    }

}
