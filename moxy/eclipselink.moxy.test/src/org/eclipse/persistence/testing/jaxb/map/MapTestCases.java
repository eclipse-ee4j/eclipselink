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
 *     Denise Smith  February, 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.map;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class MapTestCases extends JAXBWithJSONTestCases{
	
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/map/map.xml";
	private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/map/map.xsd";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/map/map.json";
	
	public MapTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[]{Root.class});
    	setControlDocument(XML_RESOURCE);
    	setControlJSON(JSON_RESOURCE);	
    }
	
	public void testSchemaGen() throws Exception{
		List<InputStream> controlSchemas = new ArrayList<InputStream>();
		controlSchemas.add(getClass().getClassLoader().getResourceAsStream(XSD_RESOURCE));
		super.testSchemaGen(controlSchemas);
	}

	@Override
	protected Object getControlObject() {
		Root root = new Root();
		root.stringStringMap = new HashMap<String, String>();
		root.stringStringMap.put("key1", "value1");
		
		root.integerComplexValueMap = new HashMap<Integer, ComplexValue>();
		root.integerComplexValueMap.put(10, new ComplexValue("aaa","bbb"));

	        root.stringArrayMap = new HashMap<String, String[]>();
	        root.stringArrayMap.put("abc", new String[] {"aaa","bbb"});

	        root.stringListMap = new HashMap<String, List<String>>();
	        List<String> list = new ArrayList<String>();
	        list.add("aaaa");
	        list.add("bbbb");
	        root.stringListMap.put("abcd", list);
		return root;
	}
}
