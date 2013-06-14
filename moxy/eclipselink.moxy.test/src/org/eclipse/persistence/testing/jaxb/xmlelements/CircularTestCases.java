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
 *     Denise Smith - June 14, 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelements;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class CircularTestCases extends JAXBWithJSONTestCases{
	 private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/root.xml";
	 private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/root.json";
	 private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/root.xsd";
	  
	 public CircularTestCases(String name) throws Exception {
	        super(name);
	        setControlDocument(XML_RESOURCE);   
	        setControlJSON(JSON_RESOURCE);
	        Class[] classes = new Class[1];
	        classes[0] = Root.class;	        
	        setClasses(classes);
	    }
	 
	 protected Object getControlObject() {
		 Root theRoot = new Root();
		 theRoot.name = "aName";
		 
		 Root theRoot2 = new Root();
		 theRoot2.name = "childName";
		 theRoot.children = new ArrayList<Object>();
		 theRoot.children.add(theRoot2);
		 theRoot.children.add("someString");
		 return theRoot;
	 }
	 
	 public void testSchemaGeneration() throws Exception{
		 List<InputStream> controlSchemas = new ArrayList<InputStream>();
		 controlSchemas.add(getClass().getClassLoader().getResourceAsStream(XSD_RESOURCE));
		 super.testSchemaGen(controlSchemas);
	 }
}
