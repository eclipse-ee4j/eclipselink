/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     etang - April 12/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.sdo.server;

import org.eclipse.persistence.sdo.SDODataObject;

public class DeptImpl extends SDODataObject implements Dept {

    public static final int START_PROPERTY_INDEX = 0;

    public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 3;

    public DeptImpl() {
    }

    @Override
    public java.lang.Integer getDeptno() {
        return getInt(START_PROPERTY_INDEX + 0);
    }

    @Override
    public void setDeptno(java.lang.Integer value) {
        set(START_PROPERTY_INDEX + 0, value);
    }

    @Override
    public java.lang.String getDname() {
        return getString(START_PROPERTY_INDEX + 1);
    }

    @Override
    public void setDname(java.lang.String value) {
        set(START_PROPERTY_INDEX + 1, value);
    }

    @Override
    public java.lang.String getLoc() {
        return getString(START_PROPERTY_INDEX + 2);
    }

    @Override
    public void setLoc(java.lang.String value) {
        set(START_PROPERTY_INDEX + 2, value);
    }

}
