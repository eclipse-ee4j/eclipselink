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
//  - Denise Smith February 12, 2013
package org.eclipse.persistence.testing.jaxb.xmlelementref.prefix;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelementref.prefix2.Child;
import org.eclipse.persistence.testing.jaxb.xmlelementref.prefix3.Other;

public class XmlElementRefPrefixesTestCases extends JAXBWithJSONTestCases{
    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/prefixes.xsd";
    private final static String XSD_RESOURCE2 = "org/eclipse/persistence/testing/jaxb/xmlelementref/prefixes2.xsd";
    private final static String XSD_RESOURCE3 = "org/eclipse/persistence/testing/jaxb/xmlelementref/prefixes3.xsd";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/prefixes.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/prefixes.json";

    public XmlElementRefPrefixesTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Root.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    protected Object getControlObject() {
        Root root = new Root();
        root.name = "theName";
        Child child = new Child();
        child.id = "theId";
        child.href = "this is the href";
        Other other = new Other();
        other.thing = "the other thing";
        child.otherThing = other;
        root.child = child;

        return root;
    }

    public void testSchemaGen() throws Exception{
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        InputStream is = getClass().getClassLoader().getResourceAsStream(XSD_RESOURCE);
        InputStream is2 = getClass().getClassLoader().getResourceAsStream(XSD_RESOURCE2);
        InputStream is3 = getClass().getClassLoader().getResourceAsStream(XSD_RESOURCE3);
        controlSchemas.add(is);
        controlSchemas.add(is2);
        controlSchemas.add(is3);
        testSchemaGen(controlSchemas);
    }
}
