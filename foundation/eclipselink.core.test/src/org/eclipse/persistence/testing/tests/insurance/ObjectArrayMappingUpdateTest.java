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
package org.eclipse.persistence.testing.tests.insurance;

import java.util.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.insurance.*;

public class ObjectArrayMappingUpdateTest extends WriteObjectTest {
    PolicyHolder holder;

    public ObjectArrayMappingUpdateTest() {
        setDescription("The test add/remove object elements from the VArray type via UnitOfWork and checks if the update works properly");
    }

    protected void removePhone(Vector phones, String type, int areaCode, int number) {
        for (Enumeration phoneEnum = phones.elements(); phoneEnum.hasMoreElements(); ) {
            Phone phone = (Phone)phoneEnum.nextElement();
            if (phone.getType().equals(type) && phone.getAreaCode() == areaCode && phone.getNumber() == number) {
                phones.removeElement(phone);
            }
        }
    }

    protected void setup() {
        beginTransaction();
    }

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
