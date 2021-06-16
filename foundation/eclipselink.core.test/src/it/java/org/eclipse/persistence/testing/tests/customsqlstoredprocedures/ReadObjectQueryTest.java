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
package org.eclipse.persistence.testing.tests.customsqlstoredprocedures;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class ReadObjectQueryTest extends AutoVerifyTestCase {
    public Employee employee;

    public ReadObjectQueryTest(Employee e) {
        super();
        this.employee = e;
    }

    public void setup() {
        beginTransaction();
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        Employee emp = (Employee)getSession().readObject(this.employee);
        emp.setFirstName("Sherry");
        emp.setFemale();
        getDatabaseSession().updateObject(emp);
        Employee emp2 = this.employee;
        emp2 = (Employee)getSession().refreshObject(emp2);
        if ((!compareObjects(emp2, emp)) || (compareObjects(emp2, this.employee))) {
            throw new TestErrorException("Object failed to refresh");
        }
    }
}
