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
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Binder;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Node;

public class XmlVariableNodeXmlValueCollectionTestCases extends JAXBWithJSONTestCases{
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootXmlValueCollection.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootXmlValueCollection.json";

    public XmlVariableNodeXmlValueCollectionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{RootXmlValueCollection.class});
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
    }


    @Override
    protected Object getControlObject() {
        RootXmlValueCollection r = new RootXmlValueCollection();
        r.name = "theRootName";

        ThingXmlValue thing = new ThingXmlValue();
        thing.thingName = "thinga";
        thing.thingValue = "thingavalue";

        ThingXmlValue thingb = new ThingXmlValue();
        thingb.thingName = "thingb";
        thingb.thingValue = "thingbvalue";

        ThingXmlValue thingc = new ThingXmlValue();
        thingc.thingName = "thingc";
        thingc.thingValue = "thingcvalue";

        r.things= new ArrayList<ThingXmlValue>();
        r.things.add(thing);
        r.things.add(thingb);
        r.things.add(thingc);
        return r;
    }



}
