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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;

/**
 * Test shallow refresh, non-cascaded.
 */
public class ShallowRefreshTest extends TestCase {
    protected Employee originalObject;
    protected String firstName;

    public ShallowRefreshTest() {
        setDescription("This test verifies the shallow refresh feature works properly");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void setup() {
        originalObject = (Employee)getSession().readObject(Employee.class);
    }

    public void test() {
        firstName = originalObject.getFirstName();
        originalObject.setFirstName("Godzilla");
        originalObject.getAddress().setCity("Foo Town");

        ReadObjectQuery query = new ReadObjectQuery();
        query.setSelectionObject(originalObject);
        query.refreshIdentityMapResult();
        getSession().executeQuery(query);

    }

    protected void verify() {
        if (!(originalObject.getFirstName().equals(firstName))) {
            throw new TestErrorException("The shallow refresh test failed.");
        }

        if (!(originalObject.getAddress().getCity().equals("Foo Town"))) {
            throw new TestErrorException("The shallow refresh test failed.");
        }
    }
}
