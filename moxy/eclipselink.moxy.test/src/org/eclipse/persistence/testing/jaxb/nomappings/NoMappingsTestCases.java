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
//     Denise Smith - 2.4
package org.eclipse.persistence.testing.jaxb.nomappings;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class NoMappingsTestCases extends JAXBWithJSONTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/nomappings/test.xml";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/nomappings/testWrite.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/nomappings/test.json";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/nomappings/testWrite.json";

    public NoMappingsTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{SomeClass.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
    }

    @Override
    protected Object getControlObject() {
        return new SomeClass();
    }

    @Override
    public Object getJSONReadControlObject() {
        JAXBElement jbe = new JAXBElement<SomeClass>(new QName(""), SomeClass.class, new SomeClass());
        return jbe;
    }

    @Override
    public Object getReadControlObject() {
        JAXBElement jbe = new JAXBElement<SomeClass>(new QName("someClass"), SomeClass.class, new SomeClass());
        return jbe;
    }

    @Override
    public Class getUnmarshalClass(){
        return SomeClass.class;
    }

    public void testUnmarshallerHandler(){};

}
