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
 *     Denise Smith - 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class XmlIdRefExceptionTestCases extends OXTestCase {
	 public XmlIdRefExceptionTestCases(String name) throws Exception {
		super(name);
		
	}
	
	 /**
	     * Tests @XmlID override via eclipselink-oxm.xml.  Here there is an xml-idref [address] on 
	     * Employee, but no corresponding xml-id set on Address.  An exception should occur.  
	     * 
	     * Negative test.
	     */
	public void testNoIdException(){
		Class[] classes = new Class[] { Employee2.class, Address2.class };
		boolean ex = false;
		try {
			InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlidref/eclipselink-oxm-no-id.xml");

			HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref", new StreamSource(inputStream));
		    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        	        
			JAXBContextFactory.createContext(classes, properties);
		} catch (JAXBException e) {
            ex = true;
        }
        assertTrue("The expected exception was not thrown.", ex);
		
	}
	
	  /**
     * Tests @XmlID override via eclipselink-oxm.xml.  Here there are two fields
     * ([city] and [id]) set as xml-id.  
     * 
     * Negative test.
     */
	public void testMultipleIdException(){
		Class[] classes = new Class[] { Employee2.class, Address2.class };
		boolean ex = false;
		try {
			InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlidref/eclipselink-oxm-multi-id.xml");

			HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref", new StreamSource(inputStream));
		    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        	        
			JAXBContextFactory.createContext(classes, properties);
		} catch (JAXBException e) {
            ex = true;
        }
        assertTrue("The expected exception was not thrown.", ex);
		
	}
	
	 /**
     * Tests @XmlID override via eclipselink-oxm.xml.  Here there is one @XmlID 
     * annotation [city] and an xml-id [id]  
     * 
     * Negative test.
     */
	public void testMultipleId2Exception(){
		Class[] classes = new Class[] { Employee2.class, Address2.class };
		boolean ex = false;
		try {
			InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlidref/eclipselink-oxm-multi-id2.xml");

			HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref", new StreamSource(inputStream));
		    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        	        
			JAXBContextFactory.createContext(classes, properties);
		} catch (JAXBException e) {
            ex = true;
        }
        assertTrue("The expected exception was not thrown.", ex);
		
	}
	
    /**
     * Tests that an exception is thrown if XmlJoinNode is set on an invalid Property,
     * as in the case where the Property type is String.
     * 
     * Negative test.
     */
	  public void testInvalidRefClass() {
		  Class[] classes = new Class[] { Employee2.class, Address2.class };
		boolean ex = false;
		try {
			InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlidref/invalid-ref-class-oxm.xml");

			HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref", new StreamSource(inputStream));
		    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        	        
			JAXBContextFactory.createContext(classes, properties);
		} catch (JAXBException e) {
          ex = true;
      }
      assertTrue("The expected exception was not thrown.", ex);
	    }
}
