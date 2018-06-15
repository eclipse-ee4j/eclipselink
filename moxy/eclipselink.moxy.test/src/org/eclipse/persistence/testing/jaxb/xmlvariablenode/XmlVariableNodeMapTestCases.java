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
import java.util.TreeMap;

import javax.xml.bind.Binder;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Node;

public class XmlVariableNodeMapTestCases extends JAXBWithJSONTestCases{
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootMap.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootMap.json";

    public XmlVariableNodeMapTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{RootMap.class});
    }


    @Override
    protected Object getControlObject() {
        RootMap r = new RootMap();
        r.name = "theRootName";
        r.things = new TreeMap<String, Thing>();
        Thing thing1 = new Thing();
        thing1.thingName = "thinga";
        thing1.thingValue = "thingavalue";

        Thing thing2 = new Thing();
        thing2.thingName = "thingb";
        thing2.thingValue = "thingbvalue";

        Thing thing3 = new Thing();
        thing3.thingName = "thingc";
        thing3.thingValue = "thingcvalue";
        r.things.put("thinga",thing1);
        r.things.put("thingb",thing2);
        r.things.put("thingc",thing3);
        return r;
    }



}
