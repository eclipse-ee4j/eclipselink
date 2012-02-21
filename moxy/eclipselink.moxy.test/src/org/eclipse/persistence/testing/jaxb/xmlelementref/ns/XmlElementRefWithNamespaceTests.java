/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 17 November 2011 - 2.3.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref.ns;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XmlElementRefWithNamespaceTests extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/ns/echobytearray.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/ns/echobytearray.json";
    private Marshaller jsonMarshaller;
    private Unmarshaller jsonUnmarshaller;
    
    public XmlElementRefWithNamespaceTests(String name) throws Exception {
        super(name);
        setClasses(new Class[]{ EchoByteArray.class, ObjectFactory.class });
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        
        jsonMarshaller = jaxbContext.createMarshaller();
        jsonUnmarshaller = jaxbContext.createUnmarshaller();
        Map<String, String> namespaces = new HashMap<String, String>();
    	namespaces.put("http://root.org/", "ns1");
    	namespaces.put("http://missing-uri.org/", "ns0");
    	
    	jsonMarshaller.setProperty(JAXBMarshaller.NAMESPACE_PREFIX_MAPPER, namespaces);        
  	 	jsonUnmarshaller.setProperty(JAXBUnmarshaller.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);

  	 	
    }
    
    protected Marshaller getJSONMarshaller() throws Exception{
    	return jsonMarshaller;
    }
    
   protected Unmarshaller getJSONUnmarshaller() throws Exception{
	   return jsonUnmarshaller;
    }

    protected Object getControlObject() {
        ObjectFactory factory = new ObjectFactory();
        EchoByteArray e = factory.createEchoByteArray();
        e.setRequest(factory.createEchoByteArrayRequest(new byte[] { 0, 1, 2, 4, 8 }));

        return e;
    }

}