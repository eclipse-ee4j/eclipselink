/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - November 03/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlbidirectional;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;

/**
 * Tests XmlBidirectional via eclipselink-oxm.xml
 *
 */
public class XmlBidirectionalTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlbidirectional";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlbidirectional/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlBidirectionalTestCases(String name) {
        super(name);
    }
    
    /**
     * Tests @XmlBidirectional override via eclipselink-oxm.xml.
     *  
     * Positive test.
     */
    public void testXmlBidirectional() {
        String metadataFile = PATH + "eclipselink-oxm.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        JAXBContext jCtx = null;
        try {
            jCtx = (JAXBContext) JAXBContextFactory.createContext(new Class[] { Root.class, Employee.class, Address.class }, properties, loader);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred creating the JAXBContext.");
        }
        
        Root testRoot = null;
        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        String src = PATH + "root.xml";
        try {
            testRoot = (Root) unmarshaller.unmarshal(getControlDocument(src));
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred unmarshalling the document [" + src + "].");
        }

        assertNotNull("The unmarshalled Root is null.", testRoot);
        Employee testEmp = testRoot.employee;
        assertNotNull("The unmarshalled Employee is null.", testEmp);
        Address testAdd = testRoot.employee.address;
        assertNotNull("The unmarshalled Address is null.", testAdd);
        Employee testAddEmp = testAdd.emp;
        assertNotNull("The unmarshalled Address' Employee is null.", testAddEmp);
        assertEquals("The Address' Employee should be [" + testEmp + "] but was [" + testAddEmp + "]", testEmp, testAddEmp);
    }
}
