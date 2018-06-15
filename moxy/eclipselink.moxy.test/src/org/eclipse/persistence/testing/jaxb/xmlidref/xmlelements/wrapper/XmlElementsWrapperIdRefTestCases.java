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
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements.wrapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementsWrapperIdRefTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/xmlelements/wrapper.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/xmlelements/wrapper.json";
    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/xmlelements/wrapper.xsd";

    public XmlElementsWrapperIdRefTestCases(String name) throws Exception {
        super(name);
        Class[] classes = new Class[1];
        classes[0] = Foo.class;
        setClasses(classes);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    protected Object getControlObject() {
        Foo foo = new Foo();

        AttributeImpl attr1 = new AttributeImpl();
        attr1.setId("1");
        AttributeImpl2 attr2 = new AttributeImpl2();
        attr2.setId("2");
        AttributeImpl attr3 = new AttributeImpl();
        attr3.setId("3");
        foo.attributes.add(attr1);
        foo.attributes.add(attr2);
        foo.attributes.add(attr3);

        foo.attributeRefs.add(attr1);
        foo.attributeRefs.add(attr2);

        foo.attributeImplRefs.add(attr1);
        foo.attributeImplRefs.add(attr3);

        return foo;
    }

    protected Object getJSONReadControlObject() {
        Foo foo = (Foo)getControlObject();
        Attribute removed = foo.attributes.remove(1);
        foo.attributes.add(removed);
        return foo;
    }
    public void testSchemaGen() throws Exception {
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        controlSchemas.add(ClassLoader.getSystemResourceAsStream(XSD_RESOURCE));

        this.testSchemaGen(controlSchemas);

    }
}
