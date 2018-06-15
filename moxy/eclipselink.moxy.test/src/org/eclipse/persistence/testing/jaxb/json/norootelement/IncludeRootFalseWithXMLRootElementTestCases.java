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
//     Denise Smith - September 2011 - 2.4
package org.eclipse.persistence.testing.jaxb.json.norootelement;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;

public class IncludeRootFalseWithXMLRootElementTestCases extends NoRootElementTestCases{
    protected final static String JSON_RESOURCE_TYPE = "org/eclipse/persistence/testing/jaxb/json/norootelement/addressWithType.json";
    protected final static String JSON_SCHEMA = "org/eclipse/persistence/testing/jaxb/json/norootelement/addressWithTypeSchema.json";

    public IncludeRootFalseWithXMLRootElementTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE_NO_ROOT);
        setWriteControlJSON(JSON_RESOURCE_TYPE);
        setClasses(new Class[]{AddressWithRootElement.class});
    }

    public void setUp() throws Exception{
        super.setUp();
        jsonMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
      }

    public Object getControlObject() {
        AddressWithRootElement addr = new AddressWithRootElement();
        addr.setId(10);
        addr.setCity("Ottawa");
        addr.setStreet("Main street");

        return addr;
    }

    @Override
    public Class getUnmarshalClass(){
        return AddressWithRootElement.class;
    }

    public Object getReadControlObject(){
        QName name = new QName("");
        JAXBElement jbe = new JAXBElement<AddressWithRootElement>(name, AddressWithRootElement.class, (AddressWithRootElement)getControlObject());
        return jbe;
    }


    public void testJSONSchemaGeneration() throws Exception{
        generateJSONSchema(getClass().getClassLoader().getResourceAsStream(JSON_SCHEMA));
    }

    @Override
    public Map<Object, Object> getProperties() {
        HashMap m = new HashMap();

        m.put(JAXBContextProperties.JSON_INCLUDE_ROOT, new Boolean(false));
        return m;

    }
}
