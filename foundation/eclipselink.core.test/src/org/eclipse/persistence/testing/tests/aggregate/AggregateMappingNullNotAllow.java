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
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.aggregate.HomeAddress;
import org.eclipse.persistence.testing.models.aggregate.WorkingAddress;
import org.eclipse.persistence.testing.models.aggregate.Employee1;

public class AggregateMappingNullNotAllow extends WriteObjectTest {
    public void reset() {
        rollbackTransaction();
    }

    public void setup() {
        beginTransaction();
    }

    public void test() {
        boolean Error = false;
        try {
            Employee1 example = new Employee1();
            example.setName("Rick");
            example.setId(23);
            example.setSalary(33000);
            example.setStatus("EclipseLink");
            example.setAddress(WorkingAddress.example2());
            example.businessAddress = HomeAddress.example1();

            UnitOfWork uow = getSession().acquireUnitOfWork();
            uow.registerObject(example);
            uow.commit();

            uow = getSession().acquireUnitOfWork();
            Employee1 empClone = (Employee1)uow.registerObject(example);
            empClone.setAddress(null);
            uow.commit();
        } catch (EclipseLinkException exp) {
            Error = true;
            if (exp.getErrorCode() == (DescriptorException.NULL_FOR_NON_NULL_AGGREGATE)) {
                return;
            }
        }
        if (Error == false) {
            throw new TestErrorException("Test Error.  No exception was thrown");
        }
    }

    public void verify() {
    }
}
