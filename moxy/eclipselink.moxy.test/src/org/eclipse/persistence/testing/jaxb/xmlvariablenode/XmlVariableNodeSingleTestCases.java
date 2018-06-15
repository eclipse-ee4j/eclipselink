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

import javax.xml.bind.Binder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Node;

public class XmlVariableNodeSingleTestCases extends JAXBWithJSONTestCases{
     protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootsingle.xml";
        protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootsingle.json";

    public XmlVariableNodeSingleTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{RootSingle.class});
    }

    @Override
    protected Object getControlObject() {
        RootSingle r = new RootSingle();
        r.name = "theRootName";

        Thing thing1 = new Thing();
        thing1.thingName = "thinga";
        thing1.thingValue = "thingavalue";
        r.thing = thing1;
        return r;
    }

    /*
    public void testBinder() throws Exception{
        Binder<Node> binder = jaxbContext.createBinder();
        InputStream is = getClass().getClassLoader().getResourceAsStream(XML_RESOURCE);
        Node doc = parser.parse(is);
        RootSingle unmarshalledObject = (RootSingle)binder.unmarshal(doc);

        ((Thing)unmarshalledObject.thing).thingValue= "NEWTHINGVALUE";
        ((Thing)unmarshalledObject.thing).thingName = "NEWTHINGNAME";
        binder.updateXML(unmarshalledObject);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();

        t.transform(new DOMSource(doc), new StreamResult(System.out));

    }*/
}
