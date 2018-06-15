/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - 2.4 - February 2012

package org.eclipse.persistence.testing.jaxb.uri;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ChildURITestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/uri/childuri.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/uri/childuri.json";

    public ChildURITestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{TestObject.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    public Object getControlObject(){
        try{
            TestObject testObject = new TestObject();
            testObject.theURI =  new URI("uri1");
            testObject.theURIs = new ArrayList<URI>();
            testObject.theURIs.add(new URI("uri2"));
            testObject.theURIs.add(new URI("uri3"));
            testObject.theURIs.add(new URI("uri4"));
            return testObject;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
        }

        return null;
    }

    public void testSchemaGen() throws Exception{
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/uri/childuri.xsd");
        controlSchemas.add(inputStream);
        super.testSchemaGen(controlSchemas);
    }

}
