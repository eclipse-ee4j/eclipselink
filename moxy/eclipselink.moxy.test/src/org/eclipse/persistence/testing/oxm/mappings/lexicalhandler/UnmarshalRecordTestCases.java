/*******************************************************************************
* Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - December 4/2009 - 2.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.lexicalhandler;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class UnmarshalRecordTestCases extends XMLMappingTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/lexicalhandler/UnmarshalRecord.xml";

    public UnmarshalRecordTestCases(String name) throws Exception {
        super(name);
        this.setProject(new UnmarshalRecordProject());
        this.setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.setFirstName("Jane");

        Address address = new Address();
        address.setStreet("123 A Street");
        employee.setAddress(address);

        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setValue("613-555-1111");
        employee.getPhoneNumbers().add(phoneNumber);

        employee.setLastName("Doe");
        return employee;
    }

    @Override
    public void testObjectToContentHandler() throws Exception {
        // ContentHandler does not support CDATA
    }

}