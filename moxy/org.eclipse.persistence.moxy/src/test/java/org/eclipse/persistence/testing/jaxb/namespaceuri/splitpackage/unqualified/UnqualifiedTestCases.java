/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.unqualified;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.qualified.QualifiedTestCases;
import org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.unqualified.a.Customer;
import org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.unqualified.b.Address;

public class UnqualifiedTestCases extends JAXBWithJSONTestCases {

    private static final String  XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/splitpackage/unqualified/input.xml";
    private static final String  JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/splitpackage/unqualified/input.json";
    private static final String  XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/splitpackage/unqualified/schema.xsd";

    public UnqualifiedTestCases(String name) throws Exception {
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
