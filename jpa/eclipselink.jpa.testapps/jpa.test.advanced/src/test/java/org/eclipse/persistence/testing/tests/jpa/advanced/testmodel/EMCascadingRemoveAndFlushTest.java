/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.advanced.testmodel;

import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.jpa.junit.EntityContainerTestBase;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.ModelExamples;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumberPK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EMCascadingRemoveAndFlushTest extends EntityContainerTestBase  {
    public EMCascadingRemoveAndFlushTest() {
        setDescription("Test cascading remove and flush in EntityManager");
    }

    public Integer[] empIDs = new Integer[2];
    public Map<String, Object> persistedItems = new HashMap<>(4);
    public List<PhoneNumberPK> phoneIDs = new ArrayList<>();

    @Override
    public void setup (){
        super.setup();
        phoneIDs.clear();
        persistedItems.clear();

        Employee employee = ModelExamples.employeeExample1();
        employee.addPhoneNumber(ModelExamples.phoneExample4());
        employee.addPhoneNumber(ModelExamples.phoneExample8());

        try {
            beginTransaction();
            getEntityManager().persist(employee);
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            ex.printStackTrace();
            throw new TestErrorException("Exception thrown durring persist and flush" + ex);
        }

        empIDs[0] = employee.getId();
    }

    @Override
    public void test(){

        try {
            beginTransaction();
            Employee employee = getEntityManager().find(Employee.class, empIDs[0]);
            for (PhoneNumber phoneNumber : employee.getPhoneNumbers()) {
                this.phoneIDs.add(phoneNumber.buildPK());
            }
            getEntityManager().remove(employee);

            getEntityManager().flush();
            //lets initialize the identity map to make sure they were persisted
            ((JpaEntityManager)getEntityManager()).getServerSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            getEntityManager().clear();

            persistedItems.put("after flush Employee", getEntityManager().find(Employee.class, empIDs[0]));
            for (PhoneNumberPK phoneID : this.phoneIDs) {
                PhoneNumber phone = getEntityManager().find(PhoneNumber.class, phoneID);
                if (phone != null) {
                    persistedItems.put("after flush PhoneNumber", phone);
                }
            }

            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            ex.printStackTrace();
            throw new TestErrorException("Exception thrown durring persist and flush" + ex);
        }
    }

    @Override
    public void verify(){
        if(persistedItems.get("after flush Employee") != null) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " was not deleted");
        }
        if(persistedItems.get("after flush PhoneNumber") != null) {
            throw new TestErrorException("Employee ID :" + empIDs[0] + " a phone number was not deleted");
        }
    }
}
