/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
