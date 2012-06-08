/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Matt MacIvor - 2.3.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.namespaceuri.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XMLNamespaceXmlPathTestCases extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/xml/testclass.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/xml/testclass.json";
    
    public XMLNamespaceXmlPathTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{TestClass.class});
        setControlDocument(XML_RESOURCE);
        
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("http://www.w3.org/XML/1998/namespace","xml");
        namespaces.put("myns","ns0");
    }

    @Override
    protected Object getControlObject() {
        TestClass tc = new TestClass();
        tc.foo = "foo";
        tc.lang = "lang";
        
        return tc;
    }
    
    public void testSchemaGen() throws Exception{
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/namespaceuri/xml/testclass.xsd");

        controlSchemas.add(inputStream);
    
        super.testSchemaGen(controlSchemas);
    }   
}