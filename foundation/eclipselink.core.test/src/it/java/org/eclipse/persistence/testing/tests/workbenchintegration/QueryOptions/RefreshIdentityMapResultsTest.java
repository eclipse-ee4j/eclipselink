/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class RefreshIdentityMapResultsTest extends AutoVerifyTestCase {
    protected Employee originalObject;
    protected String firstName;
    protected ReadObjectQuery query;

    public RefreshIdentityMapResultsTest() {
        setDescription("This test verifies if the refresh identity map feature works properly");
    }

    @Override
    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    @Override
    protected void setup() {
        query =
                (ReadObjectQuery)getSession().getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class).getQueryManager().getQuery("refreshIdentityMapResultsQuery");
        originalObject = (Employee)getSession().executeQuery(query);
    }

    @Override
    public void test() {
        firstName = originalObject.getFirstName();
        originalObject.setFirstName("Godzilla");

        getSession().executeQuery(query);
    }

    @Override
    protected void verify() {
        if (!(originalObject.getFirstName().equals(firstName))) {
            throw new TestErrorException("The refresh identity map results test failed.");
        }
    }
}
