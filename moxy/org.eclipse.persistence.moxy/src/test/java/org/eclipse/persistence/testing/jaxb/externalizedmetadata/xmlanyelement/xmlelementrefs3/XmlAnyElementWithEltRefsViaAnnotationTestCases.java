/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
// Denise Smith - 2.4 - April 2012
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs3;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs.Bar;

public class XmlAnyElementWithEltRefsViaAnnotationTestCases extends JAXBWithJSONTestCases{

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/xmlelementrefs/foo3.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/xmlelementrefs/foo.json";


    public XmlAnyElementWithEltRefsViaAnnotationTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{FooImpl.class, Bar.class, ObjectFactory3.class});
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
    }


    public void testSchemaGen() throws Exception{
        java.util.List<InputStream> controlSchemas = new ArrayList<InputStream>();
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/xmlelementrefs/schema.xsd");
        controlSchemas.add(is);
        InputStream is2 = ClassLoader.getSystemClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/xmlelementrefs/schema2.xsd");
        controlSchemas.add(is2);
        super.testSchemaGen(controlSchemas);
    }

    public Object getControlObject(){
        FooImpl foo = new FooImpl();
        Bar bar = new Bar();
        bar.id = "i69";
        ObjectFactory3 factory = new ObjectFactory3();

        List<Object> things = new ArrayList<Object>();
        things.add(factory.createFooA(66));
        things.add("some text");
        things.add(factory.createFooB(99));
        things.add(bar);
        foo.setOthers(things);
        return foo;
    }

    public Object getJSONReadControlObject(){
        FooImpl foo = new FooImpl();
        Bar bar = new Bar();
        bar.id = "i69";
        ObjectFactory3 factory = new ObjectFactory3();

        List<Object> things = new ArrayList<Object>();
        things.add(factory.createFooA(66));
        things.add(factory.createFooB(99));
        things.add(bar);
        things.add("some text");
        foo.setOthers(things);
        return foo;
    }
}
