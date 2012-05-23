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
 * dmccann - August 5/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class MethodSetOnTypeTestCases extends JAXBWithJSONTestCases{
	  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/xmltransformation/employee.xml";
	  private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/xmltransformation/employee.json";

	  private static final String EMP_NAME = "John Smith";
	  private static final String START = "9:00AM";
	  private static final String END = "5:00PM";
	    
	  public MethodSetOnTypeTestCases(String name) throws Exception {
	       super(name);
	       setControlDocument(XML_RESOURCE);
	       setControlJSON(JSON_RESOURCE);
	       setClasses(new Class[]{Employee.class});
	   }

	  public Employee getControlObject() {
	        Employee emp = new Employee();
	        emp.setName(EMP_NAME);
	        String[] hours = new String[2];
	        hours[0] = START;
	        hours[1] = END;
	        emp.setNormalHours(hours);
	        return emp;
	    }
	  
	  public Map getProperties(){
			InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/xmltransformation/method-att-xformer.xml");

			HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation", new StreamSource(inputStream));
		    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        
	        return properties;
		}	  
}
