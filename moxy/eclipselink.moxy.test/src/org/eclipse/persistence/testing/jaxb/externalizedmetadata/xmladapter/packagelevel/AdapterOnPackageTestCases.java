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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.someotherpackage.SomeLameClass;

public class AdapterOnPackageTestCases extends JAXBWithJSONTestCases{

	private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/packagelevel/myclass.xml";
	private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/packagelevel/myclass.json";    
    private final static int DAY = 12;
    private final static int MONTH = 4;
    private final static int YEAR = 1997; 
    private static Calendar CALENDAR = new GregorianCalendar(YEAR, MONTH, DAY);
    private final static int ID = 66;
	 
	
	public AdapterOnPackageTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[]{MyClass.class, SomeLameClass.class});
		setControlDocument(XML_RESOURCE);
	    setControlJSON(JSON_RESOURCE);
	}

	protected Object getControlObject() {
	    org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.MyClass sc = new org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.MyClass();
        sc.cal = CALENDAR;
        sc.id = ID;
        return sc;
	}
	
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/packagelevel/eclipselink-oxm.xml");
		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
         
        return properties;
	}
    
    public void testSchemaGen() throws Exception {
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/packagelevel/schema.xsd");
    	controlSchemas.add(is);
    	super.testSchemaGen(controlSchemas);
    }

}
