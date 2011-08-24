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
 * dmccann - October 7/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.adapter.MyValueClass;

/**
 * Tests XmlValue via eclipselink-oxm.xml
 *
 */
public class XmlValueTestCases extends JAXBTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlvalue/";
    private static final String ADAPTER_PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlvalue/adapter/";
    private static final String ADAPTER_OXM_DOC = ADAPTER_PATH + "oxm.xml";
    private static final String XML_RESOURCE = ADAPTER_PATH + "boolean.xml";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlValueTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setClasses(new Class[] { MyValueClass.class });
    }

    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream(ADAPTER_OXM_DOC);

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.adapter", new StreamSource(inputStream));
		Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        
	    return properties;
	}
	    
	
    public Object getControlObject() {
        MyValueClass mvc = new MyValueClass();
        mvc.blah = new Boolean("true");
        return mvc;        
    }

    /**
     * Tests @XmlValue via eclipselink-oxm.xml.
     * 
     * An exception should occur since more than one field is set as XmlValue.
     * 
     * Negative test.
     */
    public void testTwoXmlValues() {
        String metadataFile = PATH + "eclipselink-oxm-intprice-invalid.xml";
        InputStream iStream = getClass().getClassLoader().getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        boolean exceptionOccurred = false;
        
        try { 
            JAXBContextFactory.createContext(new Class[] { InternationalPriceNoAnnotation.class }, properties, getClass().getClassLoader());
        } catch (Exception x) {
            exceptionOccurred = true;
        }
        
        assertTrue("The expected exception was not thrown", exceptionOccurred);
    }

    /**
     * Tests @XmlValue via eclipselink-oxm.xml.
     * 
     * An exception should occur since only XmlAttributes are allowed when
     * there is an XmlValue.
     * 
     * Negative test.
     */
    public void testXmlValueWithElement() {
        String metadataFile = PATH + "eclipselink-oxm-intprice-invalid-2.xml";
        InputStream iStream = getClass().getClassLoader().getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        boolean exceptionOccurred = false;
        
        try { 
            JAXBContextFactory.createContext(new Class[] { InternationalPriceNoAnnotation.class }, properties, getClass().getClassLoader());
        } catch (Exception x) {
            exceptionOccurred = true;
        }
        
        assertTrue("The expected exception was not thrown", exceptionOccurred);
    }
      
}
