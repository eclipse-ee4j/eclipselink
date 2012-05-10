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
 * dmccann - August 6/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlcustomizer;

import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Test @XmlCustomizer via XML override.  The instance doc will contain 'my-first-name' 
 * and 'my-last-name' tags which are changed by the customizer from 'firstName' and
 * 'lastName' respectively.  Note that the customizer set via annotations 
 * (MyEmployeeCustomizer) will be overridden.
 * 
 * Positive test.
 */
public class XmlCustomizerWithOverrideTestCases extends JAXBWithJSONTestCases{
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlcustomizer/employee_overrides.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlcustomizer/employee_overrides.json";
    

	public XmlCustomizerWithOverrideTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[]{Employee.class});
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
		
	}

	public Map getProperties(){
	    InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlcustomizer/my-eclipselink-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlcustomizer", new StreamSource(inputStream));
		Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        
	    return properties;
	}
	
    protected Document getControlDocument() {
    	String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><employee><my-first-name>Joe</my-first-name><my-last-name>Oracle</my-last-name></employee>";      
    	
    	StringReader reader = new StringReader(contents);
    	InputSource is = new InputSource(reader);
    	Document doc = null;
		try {
			doc = parser.parse(is);
		} catch (Exception e) {
			fail("An error occurred setting up the control document");
		}
    	return doc;
    }
    
	   
	protected Object getControlObject() {
		Employee emp = new Employee();
		emp.firstName = "Joe";
		emp.lastName = "Oracle";
		return emp;
	}

}
