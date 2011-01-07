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
 * dmccann - July 29/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.exceptions.contextfactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;

/**
 * Tests externalized metadata processor exception handling.
 *
 */
public class ExceptionHandlingTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.exceptions.contextfactory";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/exceptions/contextfactory/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public ExceptionHandlingTestCases(String name) {
        super(name);
    }
    
    /**
     * Tests an invalid parameter type by setting a Key of type Class as opposed
     * to String.
     * 
     * Negative test.
     */
    public void testInvalidMapParameterTypeBadKey() {
        Map<Class, Source> metadataSourceMap = new HashMap<Class, Source>();
        metadataSourceMap.put(JAXBContextFactory.class, new StreamSource());
        Map<String, Map<Class, Source>> properties = new HashMap<String, Map<Class, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        try {
            JAXBContextFactory.createContext(CONTEXT_PATH, loader, properties);
        } catch (JAXBException e) {
            return;
        } catch (Exception x) {
        }
        fail("The expected JAXBException was not thrown.");
    }

    /**
     * Tests an invalid parameter type by setting a null Key.
     * 
     * Negative test.
     */
    public void testInvalidMapParameterTypeNullKey() {
        Map<Class, Source> metadataSourceMap = new HashMap<Class, Source>();
        metadataSourceMap.put(null, new StreamSource());
        Map<String, Map<Class, Source>> properties = new HashMap<String, Map<Class, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        try {
            JAXBContextFactory.createContext(CONTEXT_PATH, loader, properties);
        } catch (JAXBException e) {
            return;
        } catch (Exception x) {
        }
        fail("The expected JAXBException was not thrown.");
    }

    /**
     * Tests an invalid parameter type by setting Map<String, Class> instead 
     * of Map<String, Source>.
     * 
     * Negative test.
     */
    public void testInvalidParameterTypeBadValue() {
        Map<String, Class> metadataSourceMap = new HashMap<String, Class>();
        metadataSourceMap.put(CONTEXT_PATH, this.getClass());
        Map<String, Map<String, Class>> properties = new HashMap<String, Map<String, Class>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        try {
            JAXBContextFactory.createContext(CONTEXT_PATH, loader, properties);
        } catch (JAXBException e) {
            return;
        } catch (Exception x) {
        }
        fail("The expected JAXBException was not thrown.");
    }

    /**
     * Tests setting the metadata Source in the Map to null.
     * 
     * Negative test.
     */
    public void testInvalidMapParameterTypeNullValue() {
        Map<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, null);
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        try {
            JAXBContextFactory.createContext(CONTEXT_PATH, loader, properties);
        } catch (JAXBException e) {
            return;
        } catch (Exception x) {
        }
        fail("The expected JAXBException was not thrown.");
    }

    /**
     * Tests associating something other than Map<String, Source> with the key
     * 'eclipselink-oxm-xml' in the properties map.
     *  
     * Negative test.
     */
    public void testInvalidParameterTypeBadOxmXmlValue() {
        Map<String, List<Integer>> properties = new HashMap<String, List<Integer>>();
        ArrayList<Integer> ints = new ArrayList<Integer>();
        ints.add(new Integer(666));
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, ints);
        try {
            JAXBContextFactory.createContext(CONTEXT_PATH, loader, properties);
        } catch (JAXBException e) {
            return;
        } catch (Exception x) {
        }
        fail("The expected JAXBException was not thrown.");
    }

    /**
     * Tests declaration of a non-existent class via eclipselink-oxm.xml
     * 
     * Negative test.
     */
    public void testInvalidClassName() {
        String metadataFile = PATH + "eclipselink-oxm.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        try {
            JAXBContextFactory.createContext(CONTEXT_PATH, loader, properties);
        } catch (JAXBException e) {
            return;
        } catch (Exception x) {
        }
        fail("The expected JAXBException was not thrown.");
    }

    /**
     * Tests invalid eclipselink-oxm.xml exception handling.
     * 
     * Negative test.
     */
    public void testInvalidMetadataFile() {
        String metadataFile = PATH + "eclipselink-oxm-invalid.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        try {
            JAXBContextFactory.createContext(CONTEXT_PATH, loader, properties);
        } catch (JAXBException e) {
            return;
        } catch (Exception x) {
        }
        fail("The expected JAXBException was not thrown.");
    }
    
    /**
     * Tests declaration of a non-existent package
     * 
     * Negative test.
     */
    public void testInvalidPackageAsKey() {
    	String validPath = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/jaxbcontextfactory/";
    	String contextPath = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory";
    	String metadataFile = validPath + "eclipselink-oxm.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("java.util", new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        try {
            JAXBContext ctx = JAXBContextFactory.createContext(contextPath, loader, properties);  
        } catch (javax.xml.bind.JAXBException e) {
        	assertTrue(e.getLinkedException() instanceof JAXBException);
        	assertEquals(JAXBException.JAVATYPE_NOT_ALLOWED_IN_BINDINGS_FILE, ((JAXBException)e.getLinkedException()).getErrorCode());
        	return;        	
		}
        fail("The expected JAXBException was not thrown.");
    }
}
