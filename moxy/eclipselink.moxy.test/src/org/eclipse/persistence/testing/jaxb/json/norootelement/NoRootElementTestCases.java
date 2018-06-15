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
//     Denise Smith - August 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.json.norootelement;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class NoRootElementTestCases extends JSONMarshalUnmarshalTestCases{
    protected final static String JSON_RESOURCE_WITH_ROOT = "org/eclipse/persistence/testing/jaxb/json/norootelement/addressWithRoot.json";
    protected final static String JSON_RESOURCE_NO_ROOT = "org/eclipse/persistence/testing/jaxb/json/norootelement/address.json";
    protected final static String JSON_SCHEMA = "org/eclipse/persistence/testing/jaxb/json/norootelement/addressSchema.json";

    public NoRootElementTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE_WITH_ROOT);
        setWriteControlJSON(JSON_RESOURCE_NO_ROOT);
        setClasses(new Class[]{Address.class});
    }

    public Object getControlObject() {
        Address addr = new Address();
        addr.setId(10);
        addr.setCity("Ottawa");
        addr.setStreet("Main street");

        return addr;
    }

    @Override
    public Class getUnmarshalClass(){
        return Address.class;
    }

    public Object getReadControlObject(){
        QName name = new QName("addressWithRootElement");
        JAXBElement jbe = new JAXBElement<Address>(name, Address.class, (Address)getControlObject());
        return jbe;
    }

    public void testJSONSchemaGeneration() throws Exception{
        generateJSONSchema(getClass().getClassLoader().getResourceAsStream(JSON_SCHEMA));
    }
}

