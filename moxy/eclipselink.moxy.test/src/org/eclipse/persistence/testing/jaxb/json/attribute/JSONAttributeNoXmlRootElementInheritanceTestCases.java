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
//     Denise Smith - 2.4 - April 2012
package org.eclipse.persistence.testing.jaxb.json.attribute;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class JSONAttributeNoXmlRootElementInheritanceTestCases extends JSONMarshalUnmarshalTestCases {

    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/attribute/mailing_address_no_root.json";

    public JSONAttributeNoXmlRootElementInheritanceTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{MailingAddressNoRoot.class});
        setControlJSON(JSON_RESOURCE);
        jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
    }

    public Class getUnmarshalClass(){
        return AddressNoRoot.class;
    }

    protected Object getControlObject() {
        MailingAddressNoRoot add = new MailingAddressNoRoot();
        add.setId(10);
        add.setCity("Ottawa");
        add.setStreet("Main street");
        add.postalCode = "ABC123";
        return add;
    }

    public Object getReadControlObject() {
        JAXBElement jbe = new JAXBElement<AddressNoRoot>(new QName(""), AddressNoRoot.class, (AddressNoRoot)getControlObject());
        return jbe;
    }



}
