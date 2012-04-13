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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;

/*
 * For a context created by class[] with a jaxb.index in the package we should process all classes in the array
 * However we should not automatically process the ObjectFactory unless they are necessary
 * In this case since there is an XmlElementRef in the bindings file we SHOULD process the 
 * ObjectFactory class and include ClassB (which is only referenced in ObjectFactory)
 * We should also NOT process those listed in the jaxb.index (in this case ClassC)
 */
public class JAXBContextByClassArrayWithRefInBindingsTestCases  extends JAXBWithJSONTestCases{
	 protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbcontext/withjaxbindex/jaxbcontextbycontextpathwithrefbindings.xml";
	 protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbcontext/withjaxbindex/jaxbcontextbycontextpathwithrefbindings.json";

		public JAXBContextByClassArrayWithRefInBindingsTestCases(String name) throws Exception {
			super(name);
		}
		
		public void setUp() throws Exception {
	        setControlDocument(XML_RESOURCE);
	        setControlJSON(JSON_RESOURCE);
		    super.setUp();
		    Class[] classes = new Class[]{ClassA.class};
		    setTypes(classes);
		}

		protected Object getControlObject() {
			ClassA classA = new ClassA();
			
			JAXBElement<String> jbe = new JAXBElement<String>(new QName("a") ,String.class ,"someValue"); 
			classA.setTheValue(jbe);
			return classA;
		}

		public void testSchemaGen() throws Exception{
			InputStream controlInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/jaxbcontext/withjaxbindex/jaxbcontextbycontextpathwithrefinbindings.xsd");		
	    	List<InputStream> controlSchemas = new ArrayList<InputStream>();    	
	    	controlSchemas.add(controlInputStream);		
			this.testSchemaGen(controlSchemas);
		}
		
		protected Map getProperties() {				
		     Map overrides = new HashMap();				
		        String overridesString =
		        	 "<?xml version='1.0' encoding='UTF-8'?>" +
		 	        "<xml-bindings xmlns='http://www.eclipse.org/eclipselink/xsds/persistence/oxm'>" +
		 	       "<java-types>" +
			        "<java-type name='org.eclipse.persistence.testing.jaxb.jaxbcontext.withjaxbindex.ClassA'>" + 
			        "<java-attributes>" +
			        "<xml-element-ref java-attribute='theValue' name='a'/>" + 
		                "</java-attributes> " +
		    	     "</java-type>" +        		    	  
		    	    "</java-types>" +
		    	 "</xml-bindings>";
		
		   DOMSource src = null;
	        try {		      
	            Document doc = parser.parse(new ByteArrayInputStream(overridesString.getBytes()));
	            src = new DOMSource(doc.getDocumentElement());
		    } catch (Exception e) {
		        e.printStackTrace();
		        fail("An error occurred during setup");
	        }
			    
	        overrides.put("org.eclipse.persistence.testing.jaxb.jaxbcontext.withjaxbindex", src);

	        Map props = new HashMap();
	        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
	        return props;
	    }	
		
}