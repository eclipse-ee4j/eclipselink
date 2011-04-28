/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.2.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.qualified;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.qualified.a.Customer;
import org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.qualified.b.Address;

public class QualifiedTestCases extends JAXBTestCases {

    private static final String  XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/splitpackage/qualified/input.xml";
    private static final String  XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/splitpackage/qualified/schema.xsd";

    public QualifiedTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
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
