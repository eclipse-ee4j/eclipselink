/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.xsitype2;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Element;

public class FooBarXsiTypeTestCases extends JAXBWithJSONTestCases {

    private static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementdecl/foo.xml";
    private static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementdecl/foo_write.xml";
    private static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementdecl/foo.json";
    private static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementdecl/foo_write.json";

    public FooBarXsiTypeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
        setClasses(new Class[] {Foo.class, Bar.class, ObjectFactory.class});
    }

    @Override
    protected JAXBElement<Foo> getControlObject() {
        JAXBElement<Foo> foo = new ObjectFactory().createFoo(new Foo());
        return foo;
    }

    @Override
    public JAXBElement<Foo> getReadControlObject() {
        JAXBElement<Foo> foo = new ObjectFactory().createFoo(new Foo());
        return foo;
    }

    public void testRi() throws Exception{
        JAXBContext riContext = JAXBContext.newInstance(new Class[]{Foo.class, Bar.class, ObjectFactory.class});
        InputStream is = getClass().getClassLoader().getResourceAsStream(XML_RESOURCE);
        Object unmarshalled = riContext.createUnmarshaller().unmarshal(is);
        System.out.println(unmarshalled.getClass());
        xmlToObjectTest(unmarshalled);
        Marshaller m = riContext.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(unmarshalled, System.out);
        m.marshal(getControlObject(), System.out);
    }

    public void testRoundTrip(){

    }

}
