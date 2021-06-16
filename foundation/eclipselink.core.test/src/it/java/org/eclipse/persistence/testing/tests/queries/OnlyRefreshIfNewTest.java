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
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class OnlyRefreshIfNewTest extends AutoVerifyTestCase {
    protected Employee employeeObject;
    protected String firstName;
    public ClassDescriptor empDescriptor;

    public OnlyRefreshIfNewTest() {
        setDescription("This test verifies that the shouldOnlyRefreshCacheIfNewerVersion() feature works properly.");
    }

    public void reset() {
        this.empDescriptor.dontAlwaysRefreshCache();
        this.empDescriptor.dontOnlyRefreshCacheIfNewerVersion();
        this.empDescriptor.dontDisableCacheHits();

        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() {
        this.empDescriptor = getSession().getClassDescriptor(Employee.class);
        this.empDescriptor.alwaysRefreshCache();
        this.empDescriptor.onlyRefreshCacheIfNewerVersion();
        this.empDescriptor.disableCacheHits();

        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        this.employeeObject = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").equal("Charles"));
    }

    public void test() {
        // Change the object
        this.firstName = new String("Chelmsford");
        employeeObject.setFirstName(firstName);

        // Refresh the employee using the one in the DB -- city should change
        getSession().readObject(employeeObject);
    }

    protected void verify() {
        if (!employeeObject.getFirstName().equals(this.firstName)) {
            throw new TestErrorException("The onlyRefreshIfNew test failed on first name.");
        }
    }
}
