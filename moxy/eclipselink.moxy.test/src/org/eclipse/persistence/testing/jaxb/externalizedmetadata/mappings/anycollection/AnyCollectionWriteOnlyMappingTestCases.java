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
 * dmccann - March 24/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anycollection;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class AnyCollectionWriteOnlyMappingTestCases extends JAXBWithJSONTestCases{
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/anycollection/write-only-employee.xml";

    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/anycollection/write-only-employee.json";
    
    private static final String STUFF = "Some Stuff";
        
	public AnyCollectionWriteOnlyMappingTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);	
		setClasses(new Class[] { Employee.class });
	}
	
	 public Map getProperties(){
			InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/anycollection/write-only-employee-oxm.xml");

			HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
			metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anycollection", new StreamSource(inputStream));
			Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
			properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
		        
		    return properties;
		}
		    
	   /**
	     * Create the control Employee.
	     */
	    public Object getControlObject() {
	        Employee ctrlEmp = new Employee();

	        ctrlEmp.stuff = null;
	        return ctrlEmp;
	    }
	    
	    public Object getWriteControlObject() {
	        List<Object> stuff = new ArrayList<Object>();
	        stuff.add(STUFF);
	        
	        Employee ctrlEmp = new Employee();
	        ctrlEmp.stuff = stuff;
	        return ctrlEmp;
	    }
	    
		public void testSchemaGen() throws Exception{
		   	List controlSchemas = new ArrayList();
		   	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/anycollection/write-only-employee.xsd");
		   	controlSchemas.add(is);
		   	
		   	super.testSchemaGen(controlSchemas);	  
		}
	
		public void testRoundTrip(){
			//not applicable with write only mappings
		}
}
