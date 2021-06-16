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
package org.eclipse.persistence.testing.tests.remote;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.tests.queries.options.QueryOptionEmployee;

public class RefreshRemoteIdentityMapResultsTest extends TestCase {
    protected QueryOptionEmployee originalObject;
    protected String firstName;

    public RefreshRemoteIdentityMapResultsTest() {
        setDescription("This test verifies if the refresh remote identity map feature works properly");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void setup() {
        originalObject =
                (QueryOptionEmployee)getSession().executeQuery("refreshRemoteIdentityMapResultsQuery", QueryOptionEmployee.class);
    }

    public void test() {
        firstName = originalObject.getName();
        originalObject.setName("Godzilla");

        //      ((ReadObjectQuery)getSession().getDescriptor(org.eclipse.persistence.demos.employee.domain.Employee.class).getQueryManager().getQuery("refreshRemoteIdentityMapResultsQuery")).setSelectionObject(originalObject);
        getSession().executeQuery("refreshRemoteIdentityMapResultsQuery", QueryOptionEmployee.class);
    }

    protected void verify() {
        if (!(originalObject.getName().equals(firstName))) {
            throw new TestErrorException("The refresh remote identity map results test failed.");
        }
    }
}
