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
package org.eclipse.persistence.testing.tests.xdb;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;

public class InsertWithNullTest extends TestCase {

    public InsertWithNullTest() {
        setDescription("Tests inserting an object with NULL XMLType fields.");
    }

    public void setup() {
    }

    public void reset() {
    }

    public void test() {
        Employee_XML emp = new Employee_XML();
        emp.firstName = "Fred";
        emp.lastName = "Flintstone";
        emp.gender = "Male";

        UnitOfWork uow = this.getSession().acquireUnitOfWork();
        uow.registerObject(emp);
        uow.commit();
    }

    public void verify() {
    }
}

