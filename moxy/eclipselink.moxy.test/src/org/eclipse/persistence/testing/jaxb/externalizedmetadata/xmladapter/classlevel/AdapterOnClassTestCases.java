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
 * dmccann - July 14/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel;

import java.io.InputStream;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class AdapterOnClassTestCases extends JAXBWithJSONTestCases{

	private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/classlevel/myclass.xml";
	private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/classlevel/myclass.json";    
	 
	private final static int DAY = 12;
	private final static int MONTH = 4;
	private final static int YEAR = 1997;
	private final static int ID = 66;
	 
	
	public AdapterOnClassTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[]{org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel.MyClass.class});
		setControlDocument(XML_RESOURCE);
	    setControlJSON(JSON_RESOURCE);
	}

	protected Object getControlObject() {
		 org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel.MyClass sc = new org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel.MyClass();
	     MyCalendar mCal = new MyCalendar();
	     mCal.day = DAY;
	     mCal.month = MONTH;
	     mCal.year = YEAR;
	     sc.myCal = mCal;
	     sc.id = ID;
	     return sc;
	}
	
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/classlevel/eclipselink-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
         
        return properties;
	}
    
    public void testSchemaGen() throws Exception {
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/classlevel/schema.xsd");
    	controlSchemas.add(is);
    	super.testSchemaGen(controlSchemas);
    }
}

