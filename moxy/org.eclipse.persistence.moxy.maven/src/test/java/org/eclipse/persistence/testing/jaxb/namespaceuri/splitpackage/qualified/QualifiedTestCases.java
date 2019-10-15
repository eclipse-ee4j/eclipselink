/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.qualified;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.qualified.a.Customer;
import org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.qualified.b.Address;

public class QualifiedTestCases extends JAXBWithJSONTestCases {

    private static final String  XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/splitpackage/qualified/input.xml";
    private static final String  JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/splitpackage/qualified/input.json";
    private static final String  XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/splitpackage/qualified/schema.xsd";

    public QualifiedTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[] {Customer.class});
    }

    @Override
    protected Object getControlObject() {
        Customer customer = new Customer();
        Address address = new Address();
        address.setStreet("A");
        customer.setAddress(address);
        return customer;
    }

    public void testSchemaGen() throws Exception {
        InputStream xsd = QualifiedTestCases.class.getClassLoader().getResourceAsStream(XSD_RESOURCE);
        List<InputStream> xsds = new ArrayList<InputStream>(1);
        xsds.add(xsd);
        testSchemaGen(xsds);
    }

}
