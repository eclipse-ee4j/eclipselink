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
package org.eclipse.persistence.testing.jaxb.xmlvariablenode.method;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlVariableNodeMethodSetOnlyTestCases extends JAXBWithJSONTestCases{
     protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/root.xml";
        protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/root.json";

        protected final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootSetOnly.xml";
        protected final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootSetOnly.json";
    public XmlVariableNodeMethodSetOnlyTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setClasses(new Class[]{RootSetOnly.class});
    }

    @Override
    protected Object getControlObject() {
        RootSetOnly r = new RootSetOnly();
        r.name = "theRootName";
        r.things = new ArrayList<ThingSetOnly>();
        ThingSetOnly thing1 = new ThingSetOnly();
        thing1.setThingName("thinga");
        thing1.setThingValue("thingavalue");

        ThingSetOnly thing2 = new ThingSetOnly();
        thing2.setThingName("thingb");
        thing2.setThingValue("thingbvalue");

        ThingSetOnly thing3 = new ThingSetOnly();
        thing3.setThingName("thingc");
        thing3.setThingValue("thingcvalue");
        r.things.add(thing1);
        r.things.add(thing2);
        r.things.add(thing3);
        return r;
    }

    public void testRoundTrip(){}

}
