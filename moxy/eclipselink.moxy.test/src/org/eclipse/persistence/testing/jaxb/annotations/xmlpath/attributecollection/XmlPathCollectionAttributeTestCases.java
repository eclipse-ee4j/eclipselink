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
 *     Denise Smith - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.attributecollection;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XmlPathCollectionAttributeTestCases extends JAXBTestCases{
	 private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/listattributes.xml";

	 public XmlPathCollectionAttributeTestCases(String name) throws Exception {
	        super(name);
	        setControlDocument(XML_RESOURCE);
	        setTypes(new Class[] {TestClass.class});
	  }

	  @Override
	  protected Object getControlObject() {
	       TestClass tc = new TestClass();
	       ArrayList<String> items = new ArrayList<String>();
	       items.add("aaa");
	       items.add("bbb");
	       items.add("ccc");	       
	       tc.setItemList(items);

	       return tc;
	  }
	 
	  
	  public void testUnmarshalFromNode() throws Exception {
	        if(isUnmarshalTest()) {
                    InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setNamespaceAware(false);
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc  = builder.parse(instream);
	        	   
                    Object testObject = (TestClass) jaxbUnmarshaller.unmarshal(doc);
                    instream.close();
	            xmlToObjectTest(testObject);        	
	        	
	        }
	    }
	  
	  public void testUnmarshalFromNodeNSAware() throws Exception {
	        if(isUnmarshalTest()) {
	            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);            
	            Node node  = parser.parse(instream);
	            Object testObject = jaxbUnmarshaller.unmarshal(node);
	            instream.close();
	            xmlToObjectTest(testObject);
	        	
	        }
	    }

}
