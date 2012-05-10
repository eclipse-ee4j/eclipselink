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
 * Denise Smith - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.jaxbcontext.withjaxbindex;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/*
 * For a context created by context path with a jaxb.index in the package we should process all classes referenced
 * by the ObjectFactory (in this case ClassA and ClassB) as well as those listed in the jaxb.index (in this case ClassC)
 */
public class JAXBContextByPackageWithIndexTestCases extends JAXBWithJSONTestCases{
	 protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbcontext/withjaxbindex/jaxbcontextwithjaxbindex.xml";
	 protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbcontext/withjaxbindex/jaxbcontextwithjaxbindex.json";

		public JAXBContextByPackageWithIndexTestCases(String name) throws Exception {
			super(name);
		}
		
		public void setUp() throws Exception {
	        setControlDocument(XML_RESOURCE);
	        setControlJSON(JSON_RESOURCE);
		    super.setUp();
		    	        
		    setContextPath("org.eclipse.persistence.testing.jaxb.jaxbcontext.withjaxbindex");
		}

		protected Object getControlObject() {
			ClassA classA = new ClassA();
			classA.setTheValue("someValue");
			
			return classA;
		}

		public void testSchemaGen() throws Exception{
			InputStream controlInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/jaxbcontext/withjaxbindex/jaxbcontextbycontextpath.xsd");		
	    	List<InputStream> controlSchemas = new ArrayList<InputStream>();    	
	    	controlSchemas.add(controlInputStream);		
			this.testSchemaGen(controlSchemas);
		}
}
