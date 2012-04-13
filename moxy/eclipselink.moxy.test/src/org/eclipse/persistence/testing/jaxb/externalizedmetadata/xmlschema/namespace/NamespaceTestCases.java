/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Denise Smith - March 4/2010 - 2.0.2
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.namespace;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.dom.DOMSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;

public class NamespaceTestCases extends JAXBWithJSONTestCases{
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/namespace/namespace.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/namespace/namespace.json";
    public NamespaceTestCases(String name) throws Exception {
        super(name);		
    }
	
    public void setUp() throws Exception {
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        super.setUp();
        Type[] types = new Type[1];
        types[0] = Address.class;
        setTypes(types);
    }

    public void init() throws Exception {	
        Type[] types = new Type[1];
        types[0] = Address.class;
        setTypes(types);
    }
	
    protected Object getControlObject() {
        Address address = new Address();
        address.city = "Ottawa";
        address.street = "Main Street";
        return address;
    }
	
    public void testSchemaGen() throws Exception {
        List<InputStream> controlSchemas = new ArrayList<InputStream>();		
        InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/namespace/namespace.xsd");
        controlSchemas.add(is);		
        super.testSchemaGen(controlSchemas);
    }
	
    protected Map getProperties() {
		
        Map overrides = new HashMap();		
        String overridesString = 
        "<?xml version='1.0' encoding='UTF-8'?>" +
        "<xml-bindings xmlns='http://www.eclipse.org/eclipselink/xsds/persistence/oxm'>" +
        "<xml-schema namespace='myuri'/>" +
        "<java-types/>" +
        "</xml-bindings>";
		
        DOMSource src = null;
        try {		      
            Document doc = parser.parse(new ByteArrayInputStream(overridesString.getBytes()));
            src = new DOMSource(doc.getDocumentElement());
	    } catch (Exception e) {
	        e.printStackTrace();
	        fail("An error occurred during setup");
        }
		    
        overrides.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.namespace", src);

        Map props = new HashMap();
        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
        return props;
    }	
}
