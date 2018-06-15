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
//     Denise Smith - May 2013
package org.eclipse.persistence.testing.jaxb.xmlanyelement;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlAnyJAXBElementTestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/anyJAXBElement.xml";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employeeLaxMixedWrite.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/anyJAXBElement.json";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/employeeLaxMixedWrite.json";

    public XmlAnyJAXBElementTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[3];
        classes[0] = Root.class;
        classes[1] = Thing.class;
        classes[2] = Address.class;
        setClasses(classes);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_REDUCE_ANY_ARRAYS, true);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
    }

    @Override
    protected Object getControlObject() {
        Root r = new Root();
        r.things = new ArrayList<JAXBElement>();
        Thing thing1 = new Thing();
        thing1.name = "thing1name";

        Thing thing2 = new Thing();
        thing2.name = "thing2name";

        Thing thing3 = new Thing();
        thing3.name = "thing3name";

        JAXBElement jb1 = new JAXBElement<Thing>(new QName("something"), Thing.class, thing1);
        JAXBElement jb2 = new JAXBElement<Thing>(new QName("address"), Thing.class, thing2);
        JAXBElement jb3 = new JAXBElement<Thing>(new QName("thing"), Thing.class, thing3);

        r.things.add(jb1);
        r.things.add(jb2);
        r.things.add(jb3);
        return r;
    }


    @Override
    public Object getReadControlObject() {
        Root r = new Root();
        r.things = new ArrayList();

        Thing thing3 = new Thing();
        thing3.name = "thing3name";

        Document doc = parser.newDocument();
        Element rootElem = doc.createElementNS("", "something");
        Element child = doc.createElementNS("","name");
        child.setTextContent("thing1name");
        rootElem.appendChild(child);
        doc.appendChild(rootElem);

        r.things.add(rootElem);
        r.things.add(new Address());
        r.things.add(thing3);
        return r;
    }

    public void testRoundTrip(){
    }
}
