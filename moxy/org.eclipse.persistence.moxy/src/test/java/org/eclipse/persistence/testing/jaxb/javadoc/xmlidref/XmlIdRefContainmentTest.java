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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlidref;

//Example 2

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlIdRefContainmentTest extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlidref/xmlidrefcontainment.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlidref/xmlidrefcontainment.json";

    public XmlIdRefContainmentTest(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[4];
        classes[0] = Customer.class;
        classes[1] = Invoice.class;
        classes[2] = Shipping.class;
        classes[3] = CustomerData.class;
        setClasses(classes);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
    }

    protected Object getControlObject() {

        CustomerData example = new CustomerData();
        Customer customer = new Customer();
        customer.setCustomerID("Alice");
        customer.setName("Alice Jane");
        Invoice invoice = new Invoice();
        invoice.customer = customer;
        Shipping shippingTo = new Shipping();
        shippingTo.customer = customer;
        example.customer = customer;
        example.invoice = invoice;
        example.shipping = shippingTo;
        return example;
    }


}
