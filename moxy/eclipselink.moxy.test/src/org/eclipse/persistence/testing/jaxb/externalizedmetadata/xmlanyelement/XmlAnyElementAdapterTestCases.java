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
 * Denise Smith - 2.4 - April 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlAnyElementAdapterTestCases extends JAXBWithJSONTestCases{
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/employee-default-ns.xml";
	private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/employee-default-ns.json";
	
	public XmlAnyElementAdapterTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{Employee.class});
        
    }  
	
	  public Map getProperties(){
			InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/eclipselink-oxm-xml-adapter.xml");

			HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement", new StreamSource(inputStream));
		    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        
	        return properties;
		}
	  
	  protected Object getControlObject() {
		  Employee ctrlEmp = new Employee();
          ctrlEmp.a = 1;
          ctrlEmp.b = "3";
          Dom stuff = new Dom();
          stuff.stuffStr = "This is some stuff";
          ctrlEmp.stuff = stuff;
          return ctrlEmp;
	  }
	  	
}
