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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.xmlelementref;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class AdapterWithElementRefsTestCases extends JAXBWithJSONTestCases{
	private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/xmlelementref/foobar.xml";
	private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/xmlelementref/foobar.json";    
	 
    
    public final static String FOOBAR_ITEM = "99";


	public AdapterWithElementRefsTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[]{org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.property.MyClass.class});
		setControlDocument(XML_RESOURCE);
	    setControlJSON(JSON_RESOURCE);
	}

	protected Object getControlObject() {
        Foo foo = new Foo();
        
        foo.item = FOOBAR_ITEM;

        return foo;
	}
	
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/xmlelementref/foobar-oxm.xml");

		HashMap<String, Source> properties = new HashMap<String, Source>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new StreamSource(inputStream));		
        
        return properties;
	}
}
