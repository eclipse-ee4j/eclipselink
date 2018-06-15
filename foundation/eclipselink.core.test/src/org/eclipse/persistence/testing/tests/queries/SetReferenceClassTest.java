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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

/**
 * Test shallow refresh, non-cascaded.
 */
public class SetReferenceClassTest extends AutoVerifyTestCase {
    protected ReadObjectQuery testQuery;
    protected ClassDescriptor employeeDescriptor;

    public SetReferenceClassTest() {
        setDescription("This test verifies that the reference class on the query remains after being added to the DescriptorQueryMechanism.  Bug 3037982");
    }

    public void reset() {
        this.employeeDescriptor.getQueryManager().removeQuery(testQuery.getName());
    }

    protected void setup() {
        this.testQuery = new ReadObjectQuery(PhoneNumber.class);
        this.testQuery.setSelectionCriteria(new ExpressionBuilder().get("owner").get("firstName").equal("Bob"));
        this.testQuery.setName("bug 3037982 test Query");
        this.employeeDescriptor = getSession().getProject().getDescriptors().get(Employee.class);
    }

    public void test() {
        employeeDescriptor.getQueryManager().addQuery(testQuery.getName(), testQuery);
        PhoneNumber emp = (PhoneNumber)getSession().executeQuery(this.testQuery.getName(), Employee.class);
    }

    protected void verify() {
        if (testQuery.getReferenceClass() != PhoneNumber.class) {
            throw new TestErrorException("The reference class was overridden by addQuery");
        }
    }
}
