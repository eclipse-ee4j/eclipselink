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
package org.eclipse.persistence.testing.tests.writing;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.tests.writing.ComplexUpdateTest;

/**
 * Test changing private parts of an object.
 */
public class UpdateToNullTest extends ComplexUpdateTest {
    public UpdateToNullTest() {
        super();
    }

    public UpdateToNullTest(Employee originalObject) {
        super(originalObject);
    }

    protected void changeObject() {
        Employee employee = (Employee)this.workingCopy;

        // Direct to field
        employee.setFirstName(null);
        // Object type
        employee.setGender(null);
        // Transformation
        employee.setStartTime(null);
        employee.setEndTime(null);
        // Aggregate
        employee.setPeriod(null);
        // One to many
        employee.setPhoneNumbers(null);
        // Many to many
        employee.setProjects(null);
        // One to one private/public
        employee.setAddress(null);
        employee.setManager(null);
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        this.objectFromDatabase = getSession().executeQuery(this.query);

        if (!(compareObjects(this.objectToBeWritten, this.objectFromDatabase))) {
            if (usesUnitOfWork) {
                try {
                    Class.forName("com.ibm.uvm.tools.DebugSupport");
                    throw new TestWarningException("This fails on VA because VA has bug for setting to null.");
                } catch (ClassNotFoundException ce) {
                }
            }
            throw new TestErrorException("The object inserted into the database, '" + this.objectFromDatabase + "' does not match the original, '" + this.objectToBeWritten + ".");
        }
    }
}
