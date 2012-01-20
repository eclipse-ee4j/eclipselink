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
 *     Blaise Doughan - 2.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref.inheritance1;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.dynamic.util.SecondFieldTransformer;
import org.eclipse.persistence.testing.jaxb.xmlelementref.TestObjectFactory;

public class Inheritance1TestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/inheritance/inheritance.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/inheritance/inheritance.json";
    
    public Inheritance1TestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = Customer.class;
        classes[1] = Address.class;
        setClasses(classes);
    }

    @Override
    protected Object getControlObject() {
        Customer customer = new Customer();
        Address address = new Address();
        address.setStreet("test street");
        customer.setContactInfo(address);
        return customer;
    }

}