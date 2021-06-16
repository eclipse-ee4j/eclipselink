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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.setmethod;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class SetMethodTestCases extends XMLMappingTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositecollection/setmethod/customer.xml";
    private static final String CONTROL_PHONE_NUMBER_1_VALUE = "613-555-1111";
    private static final String CONTROL_PHONE_NUMBER_2_VALUE = "613-555-2222";

    public SetMethodTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new CustomerProject());
    }

    protected Object getControlObject() {
        Customer customer = new Customer();

        PhoneNumber phoneNumber1 = new PhoneNumber();
        phoneNumber1.setValue(CONTROL_PHONE_NUMBER_1_VALUE);
        customer.getPhoneNumbers().add(phoneNumber1);

        PhoneNumber phoneNumber2 = new PhoneNumber();
        phoneNumber2.setValue(CONTROL_PHONE_NUMBER_2_VALUE);
        customer.getPhoneNumbers().add(phoneNumber2);

        return customer;
    }

    public void xmlToObjectTest(Object testObject) throws Exception {
        super.xmlToObjectTest(testObject);
        Customer testCustomer = (Customer)testObject;
        this.assertEquals(2, testCustomer.getCollectionSizeWhenSetPhoneNumbersWasCalled());
    }
}
