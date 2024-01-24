/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Denise Smith - May 2013
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlVariableNodeSingleTestCases extends JAXBWithJSONTestCases{
     protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootsingle.xml";
        protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootsingle.json";

    public XmlVariableNodeSingleTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class<?>[]{RootSingle.class});
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
