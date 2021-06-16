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
package org.eclipse.persistence.testing.tests.identitymaps;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Bug 3310420
 * Ensure the returned object is returned from IdentityMapAccessor.removeFromIdentiytMap.
 */
public class RemoveFromIdentityMapReturnTest extends TestCase {
    protected Employee employee = null;
    protected Employee returnedEmployee = null;

    public RemoveFromIdentityMapReturnTest() {
        setDescription("Ensure the returned object is returned from IdentityMapAccessor.removeFromIdentiytMap.");
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        employee = (Employee)getSession().readObject(Employee.class);
    }

    public void test() {
        returnedEmployee = (Employee)getSession().getIdentityMapAccessor().removeFromIdentityMap(employee);
    }

    public void verify() {
        if (returnedEmployee != employee) {
            throw new TestErrorException("The incorrect employee was returned from removeFromIdentityMap.");
        }
        if (getSession().getIdentityMapAccessor().getFromIdentityMap(employee) != null) {
            throw new TestErrorException("Employee was not removed from the database in removeFromIdentityMap.");
        }
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}
