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
