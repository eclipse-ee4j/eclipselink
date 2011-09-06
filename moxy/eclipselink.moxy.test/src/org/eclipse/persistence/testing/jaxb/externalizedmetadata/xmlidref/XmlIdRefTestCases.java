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
 * dmccann - July 14/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlID and XmlIDREF via eclipselink-oxm.xml
 *
 */
public class XmlIdRefTestCases extends ExternalizedMetadataTestCases {
    private boolean shouldGenerateSchema = true;
    private MySchemaOutputResolver outputResolver;
    private JAXBContext jCtx;
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlidref/";
    private static final String INSTANCE_DOC = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlidref/root.xml";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlIdRefTestCases(String name) {
        super(name);
    }
    
    public void setUp() throws Exception {
        super.setUp();
        outputResolver = generateSchema(new Class[] { Employee.class, Address.class, Root.class }, CONTEXT_PATH, PATH, 1);
        jCtx = getJAXBContext();
    }
    
    public Root getControlRoot() {
        Address home123 = new Address();
        home123.city = "Woodlawn";
        home123.id = "ha123";
        Address work111 = new Address();
        work111.city = "Ottawa";
        work111.id = "wa111";
        Address work112 = new Address();
        work112.city = "Kanata";
        work112.id = "wa112";
        Address work113 = new Address();
        work113.city = "Orleans";
        work113.id = "wa113";
        Employee emp = new Employee();
        emp.name = "Joe Black";
        emp.homeAddress = home123;
        emp.workAddress = work111;
        Root root = new Root();
        root.employees = new ArrayList<Employee>();
        root.addresses = new ArrayList<Address>();
        root.employees.add(emp);
        root.addresses.add(work111);
        root.addresses.add(work112);
        root.addresses.add(work113);
        return root;
    }
    
    /**
     * Tests @XmlID override via eclipselink-oxm.xml.  Overrides the @XmlID [city]
     * with xml-id [id], and @XmlIDREF [homeAddress] with xml-idref [workAddress].
     * 
     * Positive test.
     */
    public void testXmlIDAndXmlIDREFOverride() {
        // validate schema
        String controlSchema = PATH + "schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }
    
    /**
     * Verifies that the primary key [primary-key/@aid] was set correctly 
     * on the Address descriptor.
     */
    public void testPrimaryKeyWasSet() {
        XMLDescriptor xdesc = jCtx.getXMLContext().getDescriptor(new QName("address"));
        Vector<String> pkFields = xdesc.getPrimaryKeyFieldNames();
        assertTrue("Expected [1] primary key field for Address, but was [" + pkFields.size() + "]", pkFields.size() == 1);
        assertTrue("Expected primary key field [primary-key/@aid] for Address, but was [" + pkFields.elementAt(0) + "]", pkFields.elementAt(0).equals("primary-key/@aid"));
    }
    
    public void testUnmarshal() throws JAXBException {
        // load instance doc
        InputStream iDocStream = loader.getResourceAsStream(INSTANCE_DOC);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + INSTANCE_DOC + "]");
        }
        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            Root testRoot = (Root) unmarshaller.unmarshal(iDocStream);
            assertNotNull(testRoot);
            Employee emp = testRoot.employees.get(0);
            assertNotNull(emp);
            Address workAddress = emp.workAddress;
            assertNotNull(workAddress);
            assertNotNull(workAddress.id);
            assertNotNull(workAddress.city);
            assertTrue("Expected work address id [wa111] but was [" + workAddress.id + "]", workAddress.id.equals("wa111"));
            assertTrue("Expected work address city [Ottawa] but was [" + workAddress.city + "]", workAddress.city.equals("Ottawa"));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }
    }
    
    public void testMarshal() throws JAXBException {
        // setup control document
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(INSTANCE_DOC);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + INSTANCE_DOC + "].");
        }

        Marshaller marshaller = jCtx.createMarshaller();
        try {
            marshaller.marshal(getControlRoot(), testDoc);
            //marshaller.marshal(getControlRoot(), System.out);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }
    }
    
    /**
     * Tests @XmlID override via eclipselink-oxm.xml.  Here there is an xml-idref [address] on 
     * Employee, but no corresponding xml-id set on Address.  An exception should occur.  
     * 
     * Negative test.
     */
    public void testXmlIDREFWithNOXmlID() {
        String metadataFile = PATH + "eclipselink-oxm-no-id.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        boolean ex = false;
        try {
            JAXBContextFactory.createContext(new Class[] { Employee2.class, Address2.class }, properties, loader);
        } catch (JAXBException e) {
            //e.printStackTrace();
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
    public void testMultipleXmlIDs() {
        String metadataFile = PATH + "eclipselink-oxm-multi-id.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        boolean ex = false;
        try {
            JAXBContextFactory.createContext(new Class[] { Address2.class }, properties, loader);
        } catch (JAXBException e) {
            //e.printStackTrace();
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
    public void testMultipleXmlIDs2() {
        String metadataFile = PATH + "eclipselink-oxm-multi-id2.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        boolean ex = false;
        try {
            JAXBContextFactory.createContext(new Class[] { Address.class }, properties, loader);
        } catch (JAXBException e) {
            //e.printStackTrace();
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
        try {
            createContext(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "invalid-ref-class-oxm.xml");
        } catch (JAXBException e) {
            return;
        }
        fail("The excepted exception was not thrown.");
    }
}
