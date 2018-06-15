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
//     bdoughan - December 17/2009 - 2.0.1 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.advancedxpath;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class UnmarshalTestCases extends XMLMappingTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/advancedxpath/unmarshal.xml";

    public UnmarshalTestCases(String name) throws Exception {
        super(name);
        if (!System.getProperties().contains("platformType")) {
            System.setProperty("platformType", "DOM");
        }
        this.setProject(new CustomerProject());
        this.setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        Customer customer = new Customer();
        Address address = new Address();
        address.setStreet("Right Street");
        customer.setAddress(address);
        return customer;
    }

    @Override
    public void testObjectToContentHandler() throws Exception {
    }

    @Override
    public void testObjectToOutputStream() throws Exception {
    }

    @Override
    public void testObjectToOutputStreamASCIIEncoding() throws Exception {
    }

    @Override
    public void testObjectToXMLDocument() throws Exception {
    }

    @Override
    public void testObjectToXMLEventWriter() throws Exception {
    }

    @Override
    public void testObjectToXMLStreamWriter() throws Exception {
    }

    @Override
    public void testObjectToXMLStringWriter() throws Exception {
    }

    @Override
    public void testValidatingMarshal() throws Exception {
    }

}
