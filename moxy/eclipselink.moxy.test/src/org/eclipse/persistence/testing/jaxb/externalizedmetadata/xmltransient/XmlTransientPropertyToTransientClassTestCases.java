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
 * Denise Smith - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient;

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

public class XmlTransientPropertyToTransientClassTestCases extends JAXBWithJSONTestCases{
	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltransient/transientProperty.xml";
	protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltransient/transientProperty.json";
		  
	public XmlTransientPropertyToTransientClassTestCases(String name)throws Exception {
		super(name);
	}
	
	public void setUp() throws Exception {
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

	    super.setUp();
	    Type[] types = new Type[1];
	    types[0] = ContactInfo.class;	        
	    setTypes(types);
	}
	
	protected Object getControlObject() {
		ContactInfo info = new ContactInfo();
		info.phoneNumber = "1234567";
		return info;
	}
	
	public Object getWriteControlObject() {
		ContactInfo info = new ContactInfo();
		info.phoneNumber = "1234567";
		List<Address> theAddresses = new ArrayList<Address>();
		Address address1 = new Address();
		address1.city = "Ottawa";
		
		theAddresses.add(address1);
				
		info.addresses = theAddresses;
		return info;
	}
	
	public void testSchemaGen() throws Exception{
		InputStream controlInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltransient/transientProperty.xsd");		
    	List<InputStream> controlSchemas = new ArrayList<InputStream>();    	
    	controlSchemas.add(controlInputStream);		
		this.testSchemaGen(controlSchemas);
	}
	
	protected Map getProperties() {
			
	        Map overrides = new HashMap();		
	
	        String overridesString =
	        "<?xml version='1.0' encoding='UTF-8'?>" +
	        "<xml-bindings xmlns='http://www.eclipse.org/eclipselink/xsds/persistence/oxm'>" +
	        "<xml-schema namespace=''/>" +       
	        "<java-types>" +
	        "<java-type name='org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.ContactInfo'>" + 
	        "<java-attributes>" +
	        "<xml-element java-attribute='phoneNumber'/>" +	        
	        "<xml-transient java-attribute='addresses'/>" +
	        "<xml-transient java-attribute='primaryAddress'/>" +	      
	        "</java-attributes> " +
	        "</java-type>" +        
	        " <java-type name='org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.Address' xml-transient='true'/>" +        
	        "</java-types>" +
	        "</xml-bindings>";
			
			
	        DOMSource src = null;
	        try {		      
	            Document doc = parser.parse(new ByteArrayInputStream(overridesString.getBytes()));
	            src = new DOMSource(doc.getDocumentElement());
		    } catch (Exception e) {
		        e.printStackTrace();
		        fail("An error occurred during setup");
	        }
			    
	        overrides.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient", src);

	        Map props = new HashMap();
	        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
	        return props;
	    }	

}
