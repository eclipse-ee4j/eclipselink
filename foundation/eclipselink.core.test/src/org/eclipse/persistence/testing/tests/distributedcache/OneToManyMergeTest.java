/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.distributedcache;

import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;
import org.eclipse.persistence.sessions.UnitOfWork;

public class OneToManyMergeTest extends DistributedCacheMergeTest {
    public OneToManyMergeTest() {
        super();
    }

    protected void modifyCollection(UnitOfWork uow, Object objectToModify) {
        PhoneNumber newPhoneNumberWC = (PhoneNumber)uow.registerNewObject(newItemForCollection());
        ((Employee)objectToModify).addPhoneNumber(newPhoneNumberWC);
    }

    protected int getCollectionSize(Object rootObject) {
        return ((Employee)rootObject).getPhoneNumbers().size();
    }

    protected Object buildOriginalObject() {
        Employee emp = new Employee();
        emp.setFirstName("Sally");
        emp.setLastName("Hamilton");
        emp.setFemale();
        return emp;
    }

    protected Object newItemForCollection() {
        PhoneNumber number = new PhoneNumber();
        number.setAreaCode("111");
        number.setNumber("1111111");
        return number;
    }
}
