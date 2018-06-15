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
//     Peter Benedikovic - 2.6 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Test for bug 426744.
 * Tests XmlVariableNode processing with internal collections followed
 * by any reference. XMLVariableXPathMappingNodeValue was not resetting
 * childRecord to null when corresponding object's end tag was spotted.
 * This caused initializing new record with old data, causing failure in
 * unmarshaling.
 * @author Peter Benedikovic
 *
 */
public class XmlVariableNodeWithReferenceTestCase extends JAXBWithJSONTestCases {

    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootextended.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootextended.json";

    public XmlVariableNodeWithReferenceTestCase(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{RootWithReference.class});
    }


    @Override
    protected Object getControlObject() {
        RootWithReference r = new RootWithReference();
        r.name = "theRootName";
        r.things = new ArrayList<ThingWithCollection>();

        Reference ref = new Reference();
        ref.name = "refa";
        r.ref = ref;

        ThingWithCollection thing1 = new ThingWithCollection();
        thing1.thingName = "thinga";
        thing1.thingValue = "thingavalue";
        thing1.refs = new ArrayList<Reference>();
        thing1.refs.add(ref);

        r.things.add(thing1);
        return r;
    }

}
