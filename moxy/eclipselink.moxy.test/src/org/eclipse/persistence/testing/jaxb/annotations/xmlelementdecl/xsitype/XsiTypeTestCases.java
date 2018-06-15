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
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.xsitype;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Element;

public class XsiTypeTestCases extends JAXBWithJSONTestCases {

    private static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementdecl/xsitype.xml";
    private static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementdecl/xsitype.json";

    public XsiTypeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[] {ExampleType.class, ObjectFactory.class});
    }

    @Override
    protected JAXBElement<Object> getControlObject() {
        ExampleType exampleType = new ExampleType();

        Element elem = parser.newDocument().createElement("Content");
        exampleType.content = elem;

        JAXBElement<Object> root = new JAXBElement<Object>(ObjectFactory._SomeElement_QNAME, Object.class, exampleType);
        return root;
    }
    /*
    public void testRi() throws Exception{
        JAXBContext riContext = JAXBContext.newInstance(new Class[]{ObjectFactory.class, ExampleType.class});
        InputStream is = getClass().getClassLoader().getResourceAsStream(XML_RESOURCE);
        Object unmarshalled = riContext.createUnmarshaller().unmarshal(is);
        System.out.println(unmarshalled.getClass());
        xmlToObjectTest(unmarshalled);

        Marshaller m = riContext.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(unmarshalled, System.out);
        m.marshal(getControlObject(), System.out);
    }
*/
}
