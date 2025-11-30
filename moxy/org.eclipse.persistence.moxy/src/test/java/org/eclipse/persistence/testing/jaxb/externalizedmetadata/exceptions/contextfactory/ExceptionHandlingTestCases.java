/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// dmccann - July 29/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.exceptions.contextfactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.oxm.OXTestCase;

/**
 * Tests externalized metadata processor exception handling.
 *
 */
public class ExceptionHandlingTestCases extends OXTestCase {

    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.exceptions.contextfactory";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/exceptions/contextfactory/";

    /**
     * This is the preferred (and only) constructor.
     *
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
        Map<Class<?>, Source> metadataSourceMap = new HashMap<>();
        metadataSourceMap.put(JAXBContextFactory.class, new StreamSource());
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);

        try {
            JAXBContextFactory.createContext(CONTEXT_PATH, getClass().getClassLoader(), properties);
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
        Map<Class<?>, Source> metadataSourceMap = new HashMap<>();
        metadataSourceMap.put(null, new StreamSource());
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);

        try {
            JAXBContextFactory.createContext(CONTEXT_PATH, getClass().getClassLoader(), properties);
        } catch (JAXBException e) {
            return;
        } catch (Exception x) {
        }
        fail("The expected JAXBException was not thrown.");
    }

    /**
     * Tests an invalid parameter type by setting {@code Map<String, Class>} instead
     * of {@code Map<String, Source>}.
     *
     * Negative test.
     */
    public void testInvalidParameterTypeBadValue() {
        Map<String, Class<?>> metadataSourceMap = new HashMap<>();
        metadataSourceMap.put(CONTEXT_PATH, this.getClass());
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);

        try {
            JAXBContextFactory.createContext(CONTEXT_PATH, getClass().getClassLoader(), properties);
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
        Map<String, Source> metadataSourceMap = new HashMap<>();
        metadataSourceMap.put(CONTEXT_PATH, null);
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);

        try {
            JAXBContextFactory.createContext(CONTEXT_PATH, getClass().getClassLoader(), properties);
        } catch (JAXBException e) {
            return;
        } catch (Exception x) {
        }
        fail("The expected JAXBException was not thrown.");
    }

    /**
     * Tests associating something other than {@code Map<String, Source>} with the key
     * 'eclipselink-oxm-xml' in the properties map.
     *
     * Negative test.
     */
    public void testInvalidParameterTypeBadOxmXmlValue() {
        Map<String, Object> properties = new HashMap<>();
        ArrayList<Integer> ints = new ArrayList<>();
        ints.add(666);
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, ints);
        try {
            JAXBContextFactory.createContext(CONTEXT_PATH, getClass().getClassLoader(), properties);
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
        InputStream iStream = getClass().getClassLoader().getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }

        HashMap<String, Source> metadataSourceMap = new HashMap<>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);

        try {
            JAXBContextFactory.createContext(CONTEXT_PATH, getClass().getClassLoader(), properties);
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
        InputStream iStream = getClass().getClassLoader().getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }

        HashMap<String, Source> metadataSourceMap = new HashMap<>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);

        try {
            JAXBContextFactory.createContext(CONTEXT_PATH, getClass().getClassLoader(), properties);
        } catch (JAXBException e) {
            return;
        } catch (Exception x) {
            x.printStackTrace();
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
        InputStream iStream = getClass().getClassLoader().getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }

        HashMap<String, Source> metadataSourceMap = new HashMap<>();
        metadataSourceMap.put("java.util", new StreamSource(iStream));
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);

        try {
            JAXBContextFactory.createContext(contextPath, getClass().getClassLoader(), properties);
        } catch (jakarta.xml.bind.JAXBException e) {
            assertTrue(e.getLinkedException() instanceof JAXBException);
            assertEquals(JAXBException.JAVATYPE_NOT_ALLOWED_IN_BINDINGS_FILE, ((JAXBException)e.getLinkedException()).getErrorCode());
            return;
        }
        fail("The expected JAXBException was not thrown.");
    }

    /**
     * Tests declaration of a non-existent class via eclipselink-oxm.xml
     *
     * Negative test.
     */
    public void testInvalidLocation() {
        String metadataFile = PATH + "eclipselink_doesnt_exist-oxm.xml";
        InputStream iStream = getClass().getClassLoader().getResourceAsStream(metadataFile);

        HashMap<String, Source> metadataSourceMap = new HashMap<>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);

        try {
            JAXBContextFactory.createContext(CONTEXT_PATH, getClass().getClassLoader(), properties);
        } catch (JAXBException e) {
            return;
        } catch (Exception x) {
            x.printStackTrace();
        }
        fail("The expected JAXBException was not thrown.");
    }

    /**
     * Tests declaration of a non-existent class via eclipselink-oxm.xml
     *
     * Negative test.
     */
    public void testInvalidLocation2() {
        String metadataFile = PATH + "eclipselink_doesnt_exist-oxm.xml";

        HashMap<String, Source> metadataSourceMap = new HashMap<>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(metadataFile));
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);

        try {
            JAXBContextFactory.createContext(CONTEXT_PATH, getClass().getClassLoader(), properties);
        } catch (JAXBException e) {
            return;
        } catch (Exception x) {
            x.printStackTrace();
        }
        fail("The expected JAXBException was not thrown.");
    }
}
