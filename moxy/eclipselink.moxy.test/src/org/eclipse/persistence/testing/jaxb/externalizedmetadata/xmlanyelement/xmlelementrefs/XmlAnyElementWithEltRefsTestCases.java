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
 * Denise Smith - 2.4 - April 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;


public class XmlAnyElementWithEltRefsTestCases extends JAXBWithJSONTestCases{
	
	private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/xmlelementrefs/foo.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/xmlelementrefs/foo.json";
		
	
	public XmlAnyElementWithEltRefsTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{FooImplNoAnnotations.class});
    	
    }  
	
	  public Map getProperties(){
			InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/xmlelementrefs/foo-oxm.xml");

			HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs", new StreamSource(inputStream));
		    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        
	        return properties;
		}
	
		public void testSchemaGen() throws Exception{
		    java.util.List<InputStream> controlSchemas = new ArrayList<InputStream>();
			InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/xmlelementrefs/schema-oxm.xsd");
			InputStream is2 = ClassLoader.getSystemClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/xmlelementrefs/schema2.xsd");
			controlSchemas.add(is);
			controlSchemas.add(is2);
			super.testSchemaGen(controlSchemas);
	   }	  
	  
	public Object getControlObject(){
		FooImplNoAnnotations foo = new FooImplNoAnnotations();
		Bar bar = new Bar();
        bar.id = "i69";
        ObjectFactory factory = new ObjectFactory();
        
        List<Object> things = new ArrayList<Object>();
        things.add(factory.createFooA(66));
        things.add("some text");
        things.add(factory.createFooB(99));
        things.add(bar);
        foo.setOthers(things);
       
	    return foo;
	}
	
	public Object getJSONReadControlObject(){
		FooImplNoAnnotations foo = new FooImplNoAnnotations();
		Bar bar = new Bar();
        bar.id = "i69";
        ObjectFactory factory = new ObjectFactory();
        
        List<Object> things = new ArrayList<Object>();
        things.add(factory.createFooA(66));        
        things.add(factory.createFooB(99));
        things.add(bar);
        things.add("some text");
        foo.setOthers(things);
       
	    return foo;
	}
}
