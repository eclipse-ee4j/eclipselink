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
 * Denise Smith - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.jaxbcontext.withjaxbindex;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/*
 * For a context created by class[] with a jaxb.index in the package we should process all classes in the array
 * However we should not automatically process the ObjectFactory unless they are necessary
 * In this case since there is an XmlElementRef we SHOULD process the ObjectFactory class and include ClassB (which is only referenced in ObjectFactory)
 * We should also NOT process those listed in the jaxb.index (in this case ClassC)
 */
public class JAXBContextByClassArrayWithRefTestCases  extends JAXBWithJSONTestCases{
	 protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbcontext/withjaxbindex/jaxbcontextbycontextpathwithref.xml";
	 protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbcontext/withjaxbindex/jaxbcontextbycontextpathwithref.json";

		public JAXBContextByClassArrayWithRefTestCases(String name) throws Exception {
			super(name);
		}
		
		public void setUp() throws Exception {
	        setControlDocument(XML_RESOURCE);
	        setControlJSON(JSON_RESOURCE);
		    super.setUp();
		    Class[] classes = new Class[]{ClassAWithElementRef.class};
		    setTypes(classes);
		}

		protected Object getControlObject() {
			ClassAWithElementRef classA = new ClassAWithElementRef();
			JAXBElement<String> jbe = new JAXBElement<String>(new QName("a") ,String.class ,"someValue"); 
			classA.setTheValue(jbe);
			
			return classA;
		}

		public void testSchemaGen() throws Exception{
			InputStream controlInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/jaxbcontext/withjaxbindex/jaxbcontextbycontextpathwithref.xsd");		
	    	List<InputStream> controlSchemas = new ArrayList<InputStream>();    	
	    	controlSchemas.add(controlInputStream);		
			this.testSchemaGen(controlSchemas);
		}
}