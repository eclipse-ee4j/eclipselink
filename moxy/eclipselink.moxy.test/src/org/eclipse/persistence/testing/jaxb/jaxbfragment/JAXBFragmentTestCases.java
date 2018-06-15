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
//    Denise Smith - September 2012
package org.eclipse.persistence.testing.jaxb.jaxbfragment;

import javax.xml.bind.Marshaller;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class JAXBFragmentTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbfragment/address.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbfragment/address.json";

     public JAXBFragmentTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = Address.class;
        setClasses(classes);
        jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
     }

    protected Object getControlObject() {
        Address addr = new Address();
        addr.setCity("Ottawa");
        addr.setStreet("Bank st.");
        return addr;
    }
}
