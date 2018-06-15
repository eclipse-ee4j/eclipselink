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
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import java.util.Enumeration;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Test to ensure cache expiry works correctly when crossing a valueholder
 */
public class CacheExpiryValueholderTest extends CacheExpiryTest {

    protected Employee readEmployee = null;

    public CacheExpiryValueholderTest() {
        setDescription("Test cache expiry across a Valueholder");
    }

    public void test() {
        ExpressionBuilder address = new ExpressionBuilder();
        Expression streetExp = address.get("street").equal("1 Habs Place");
        Address add = (Address)getSession().readObject(Address.class, streetExp);
        CacheKey key = ((AbstractSession)getSession()).getIdentityMapAccessorInstance().getCacheKeyForObject(add);
        ((Address)key.getObject()).setStreet("Ave. Sherbrooke");

        getSession().getIdentityMapAccessor().invalidateObject(add);

        //objects expired on many side. ET
        Enumeration numbers = getSession().readAllObjects(PhoneNumber.class).elements();
        while (numbers.hasMoreElements()) {
            PhoneNumber number = (PhoneNumber)numbers.nextElement();
            number.setAreaCode("000");
            getSession().getIdentityMapAccessor().invalidateObject(number);
        }

        ExpressionBuilder employee = new ExpressionBuilder();
        Expression addressExp = employee.get("address").get("street").equal("1 Habs Place");
        readEmployee = (Employee)getSession().readObject(Employee.class, addressExp);
    }

    public void verify() {
        if (!readEmployee.getAddress().getStreet().equals("1 Habs Place")) {
            throw new TestErrorException("Invalidation is not working correctly for valueholders.");
        }
        Enumeration numbers = readEmployee.getPhoneNumbers().elements();
        while (numbers.hasMoreElements()) {
            if (((PhoneNumber)numbers.nextElement()).getAreaCode().equals("000")) {
                throw new TestErrorException("Invalidation is not working correctly for valueholders.");
            }
        }
    }
}
