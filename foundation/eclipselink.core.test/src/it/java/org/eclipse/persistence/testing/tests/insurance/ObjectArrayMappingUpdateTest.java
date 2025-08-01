/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.insurance;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestException;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.insurance.Phone;
import org.eclipse.persistence.testing.models.insurance.PolicyHolder;

import java.util.Enumeration;
import java.util.Vector;

public class ObjectArrayMappingUpdateTest extends WriteObjectTest {
    PolicyHolder holder;

    public ObjectArrayMappingUpdateTest() {
        setDescription("The test add/remove object elements from the VArray type via UnitOfWork and checks if the update works properly");
    }

    protected void removePhone(Vector phones, String type, int areaCode, int number) {
        for (Enumeration phoneEnum = phones.elements(); phoneEnum.hasMoreElements(); ) {
            Phone phone = (Phone)phoneEnum.nextElement();
            if (phone.getType().equals(type) && phone.getAreaCode() == areaCode && phone.getNumber() == number) {
                phones.remove(phone);
            }
        }
    }

    @Override
    protected void setup() {
        beginTransaction();
    }

    @Override
    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holder = (PolicyHolder)uow.readObject(PolicyHolder.class);
        Phone newPhone = new Phone();
        newPhone.setType("Work");
        newPhone.setAreaCode(613);
        newPhone.setNumber(5698855);
        holder.addPhone(newPhone);
        removePhone(holder.getPhones(), "fax", 123, 23456789);
        uow.commit();
    }

    @Override
    protected void verify() {
        Vector phones = holder.getPhones();
        for (Enumeration phoneEnum = phones.elements(); phoneEnum.hasMoreElements(); ) {
            Phone phone = (Phone)phoneEnum.nextElement();
            if ((phone.getAreaCode() == 613) && (phone.getNumber() == 5698855) && phone.getType().equals("Work")) {
                return; //insert the new phone successfully
            } else if (phone.getType().equals("fax") && (phone.getAreaCode() == 123) && (phone.getNumber() == 23456789)) {
                throw new TestException("Remove on ObjectArrayMapping fails!");
            }
        }
        //the inserted phone was not found, insert fails
        throw new TestException("Insert on ObjectArrayMapping fails!");
    }
}
