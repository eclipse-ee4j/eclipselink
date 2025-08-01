/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.workbenchintegration.QueryOptions;

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;

import java.util.Vector;


public class FirstResultTest extends AutoVerifyTestCase {
    private Vector employees;

    public FirstResultTest() {
        setDescription("Test that FirstResult persists properly.");
    }

    @Override
    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    public void setup() {
    }

    @Override
    public void test() {
        employees =
                (Vector)getSession().executeQuery("firstResultQuery", org.eclipse.persistence.testing.models.employee.domain.Employee.class);
    }

    @Override
    public void verify() {
        if (employees.size() != 10) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("ReadAllQuery with setFirstResult test failed. Mismatched objects returned");
        }
    }
}
