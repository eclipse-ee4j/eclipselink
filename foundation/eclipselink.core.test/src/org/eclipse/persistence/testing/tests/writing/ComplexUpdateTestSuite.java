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
package org.eclipse.persistence.testing.tests.writing;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.models.ownership.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

public class ComplexUpdateTestSuite extends TestSuite {
    public ComplexUpdateTestSuite() {
        setDescription("This suite tests updating objects with changed parts.");
    }

    public void addTests() {
        PopulationManager manager = PopulationManager.getDefaultManager();
        Employee employee = (Employee)manager.getObject(Employee.class, "0001");

        addTest(new NoIdentityUpdateTest(manager.getObject(ObjectA.class, "example1")));
        addTest(new UpdateToNullTest(employee));
        addTest(new UpdateChangeValueTest(employee));
        addTest(new UpdateChangeNothingTest(employee));
        addTest(new UpdateChangeObjectTest(employee));
        addTest(new UpdateDeepOwnershipTest((org.eclipse.persistence.testing.models.ownership.ObjectA)manager.getObject(org.eclipse.persistence.testing.models.ownership.ObjectA.class, "example1")));
        addTest(new BidirectionalInsertWithPartialRegisterationTest(true));
    }
}
