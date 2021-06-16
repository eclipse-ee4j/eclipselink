/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Denise Smith - May 2013
package org.eclipse.persistence.testing.jaxb.xmlvariablenode.method;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlVariableNodeMethodGetOnlyTestCases extends JAXBWithJSONTestCases{
     protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/root.xml";
        protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/root.json";

    public XmlVariableNodeMethodGetOnlyTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{RootGetOnly.class});
    }

    @Override
    protected Object getControlObject() {
        RootGetOnly r = new RootGetOnly();
        r.name = "theRootName";
        r.things = new ArrayList<ThingGetOnly>();
        ThingGetOnly thing1 = new ThingGetOnly("thinga");
        thing1.setThingValue("thingavalue");

        ThingGetOnly thing2 = new ThingGetOnly("thingb");
        thing2.setThingValue("thingbvalue");

        ThingGetOnly thing3 = new ThingGetOnly("thingc");
        thing3.setThingValue("thingcvalue");
        r.things.add(thing1);
        r.things.add(thing2);
        r.things.add(thing3);
        return r;
    }

    @Override
    public Object getReadControlObject() {
        RootGetOnly r = new RootGetOnly();
        r.name = "theRootName";
        r.things = new ArrayList<ThingGetOnly>();
        ThingGetOnly thing1 = new ThingGetOnly();
        thing1.setThingValue("thingavalue");

        ThingGetOnly thing2 = new ThingGetOnly();
        thing2.setThingValue("thingbvalue");

        ThingGetOnly thing3 = new ThingGetOnly();
        thing3.setThingValue("thingcvalue");
        r.things.add(thing1);
        r.things.add(thing2);
        r.things.add(thing3);
        return r;
    }

    public void testRoundTrip(){

    }
}
