/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.4 - April 2012
package org.eclipse.persistence.testing.jaxb.json.attribute;

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class JSONAttributeNoXmlRootElementJAXBElementTestCases extends JAXBWithJSONTestCases {

    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/attribute/address_no_root.json";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/attribute/address_no_root.xml";

    public JSONAttributeNoXmlRootElementJAXBElementTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{AddressNoRoot.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
    }

    public Class getUnmarshalClass(){
        return AddressNoRoot.class;
    }

    public Object getReadControlObject() {
        JAXBElement jbe = new JAXBElement<AddressNoRoot>(new QName("street"), AddressNoRoot.class, new AddressNoRoot());
        return jbe;
    }


    protected Object getJSONReadControlObject() {
        JAXBElement jbe = new JAXBElement<AddressNoRoot>(new QName(""), AddressNoRoot.class, getAddress());
        return jbe;
    }

    protected Object getControlObject() {
        JAXBElement jbe = new JAXBElement<AddressNoRoot>(new QName(""), AddressNoRoot.class, getAddress());
        return jbe;


    }

    private AddressNoRoot getAddress(){
        AddressNoRoot add = new AddressNoRoot();
        add.setId(10);
        add.setCity("Ottawa");
        add.setStreet("Main street");

        return add;
    }

      public void testRoundTrip() throws Exception {}
      public void testUnmarshallerHandler() throws Exception {}
}
