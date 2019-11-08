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
package org.eclipse.persistence.testing.tests.nls.japanese;

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import java.util.*;

public class NLSMaxRowsTest extends AutoVerifyTestCase {
    private Vector employees;

    public NLSMaxRowsTest() {
        setDescription("[NLS_Japanese] Set up the limit for the maximum number of rows the query returns");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
    }

    public void test() {
        employees = (Vector)getSession().executeQuery("maxRowsQuery", org.eclipse.persistence.testing.tests.nls.japanese.NLSEmployee.class);
    }

    public void verify() {
        if (employees.size() != 4) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("ReadAllQuery with setMaxRows test failed. Mismatched objects returned");
        }
    }
}
