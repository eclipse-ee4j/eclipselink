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
 * dmccann - July 14/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.hexbinary;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class AdapterHexBinaryTestCases extends JAXBWithJSONTestCases{

	private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/hexbinary/hexbinary.xml";
	private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/hexbinary/hexbinary.json";
        	 
	
	public AdapterHexBinaryTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[]{Customer.class});
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
	}

	protected Object getControlObject() {
        byte[] bytes = new byte[] {30,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4};
	    Customer customer = new Customer();
	    customer.hexBytes = bytes;
	    customer.base64Bytes = bytes;
	    return customer;
	}
	
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/hexbinary/hexbinary-oxm.xml");
		HashMap<String, Source> properties = new HashMap<String, Source>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new StreamSource(inputStream));		
         
        return properties;
	}
    
    public void testSchemaGen() throws Exception {
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/hexbinary/schema.xsd");
    	controlSchemas.add(is);
    	super.testSchemaGen(controlSchemas);
    }

}
