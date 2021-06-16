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
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlVariableNodeChildTestCases extends JAXBWithJSONTestCases{
     protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootChild.xml";
        protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootChild.json";
        protected final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootChild.xsd";
    public XmlVariableNodeChildTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{Root.class, Thing.class, Child.class});
    }

    @Override
    protected Object getControlObject() {
        Root r = new Root();
        r.name = "theRootName";
        r.things = new ArrayList<Thing>();
        Thing thing1 = new Thing();
        thing1.thingName = "thinga";
        thing1.thingValue = "thingavalue";

        Thing thing2 = new Thing();
        thing2.thingName = "thingb";
        thing2.thingValue = "thingbvalue";

        Child thing3 = new Child();
        thing3.childAttribute = "childAttr";
        thing3.thingName = "thingc";
        thing3.thingValue = "thingcvalue";
        r.things.add(thing1);
        r.things.add(thing2);
        r.things.add(thing3);
        return r;
    }

    public void testSchemaGen() throws Exception{
        InputStream stream =  getClass().getClassLoader().getResourceAsStream(XSD_RESOURCE);
        List<InputStream> schemas = new ArrayList<InputStream>();
        schemas.add(stream);
        testSchemaGen(schemas);
    }

}
