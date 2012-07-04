/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith -  July 9, 2009 Initial test
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.singleobject;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderInputSource;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class JAXBSingleObjectIntegerNoXsiTestCases extends JAXBWithJSONTestCases {

	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/singleobject/singleObject.xml";
	protected final static String XML_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/singleobject/singleObjectWrite.xml";
	protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/singleobject/singleObjectInteger.json";

	public JAXBSingleObjectIntegerNoXsiTestCases(String name) throws Exception {
		super(name);
		init();
	}

	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		setWriteControlDocument(XML_RESOURCE_WRITE);
		setControlJSON(JSON_RESOURCE);
		Class[] classes = new Class[1];
		classes[0] = Object.class;
		setClasses(classes);
		Map namespaces = new HashMap();
    	namespaces.put("rootNamespace","ns1");
		jaxbMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
		jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
	}

   public Map getProperties(){
    	Map props = new HashMap();
	    	
    	Map namespaces = new HashMap();
    	namespaces.put("ns1", "rootNamespace");

    	props.put(JAXBContextProperties.JSON_INCLUDE_ROOT, true);

	    return props;
}
	
	public void testSchemaGen() throws Exception {
		MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
		getJAXBContext().generateSchema(outputResolver);

		assertEquals("A Schema was generated but should not have been", 0, outputResolver.getSchemaFiles().size()); 
	}
		
	public List<InputStream> getControlSchemaFiles() {
		//not applicable for this test since we override testSchemaGen
		return null;  		
	}

	protected Object getControlObject() {		
		Integer testInteger = 25;		
		QName qname = new QName("rootNamespace", "root");				
		JAXBElement jaxbElement = new JAXBElement(qname, Integer.class, testInteger);		
		return jaxbElement;
	}

	protected Type getTypeToUnmarshalTo() throws Exception {
		return Integer.class;
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE;
	}

    public void testUnmarshallerHandler() throws Exception {
    }

    public Object getWriteControlObject() {
        return getControlObject();
    }

    @Override
    public Class getUnmarshalClass() {
        return Integer.class;
    }

}
