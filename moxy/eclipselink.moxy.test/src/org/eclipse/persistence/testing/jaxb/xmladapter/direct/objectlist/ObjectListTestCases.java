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
 *     Blaise Doughan - 2.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.direct.objectlist;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class ObjectListTestCases extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/objectlist.xml";

    public ObjectListTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = Customer.class;
        setClasses(classes);
    }

    @Override
    protected Object getControlObject() {
        Customer customer = new Customer();
        customer.getPhoneNumbers().add(new PhoneNumber("555-WORK"));
        customer.getPhoneNumbers().add(new PhoneNumber("555-HOME"));
        return customer;
    }

}