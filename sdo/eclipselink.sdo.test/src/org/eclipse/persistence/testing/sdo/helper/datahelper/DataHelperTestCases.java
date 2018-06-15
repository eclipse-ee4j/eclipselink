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
package org.eclipse.persistence.testing.sdo.helper.datahelper;

import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class DataHelperTestCases extends SDOTestCase {
    protected StringBuffer sb;

    public DataHelperTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        sb = new StringBuffer();
    }

    public void makeString(String s) {
        sb.append(s);
    }

    public void clear() {
        sb.delete(0, sb.length());
    }
}
