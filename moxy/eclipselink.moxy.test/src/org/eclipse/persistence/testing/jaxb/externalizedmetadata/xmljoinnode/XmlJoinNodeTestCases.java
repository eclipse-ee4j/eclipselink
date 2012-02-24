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
 * dmccann - August 26/2009 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests relationship mapping configuration via XmlJoinNode & XmlJoinNodes.
 *
 */
public class XmlJoinNodeTestCases extends JAXBWithJSONTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmljoinnode/company.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmljoinnode/company.json";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmljoinnode/";
    private static final String OXM_DOC = PATH + "company-oxm.xml";
    private static final String INVALID_OXM_DOC = PATH + "invalid-xml-join-node-oxm.xml";
    private static final String INVALID_XPATH_OXM_DOC = PATH + "invalid-target-xpath-oxm.xml";
    private static final String INVALID_TARGET_OXM_DOC = PATH + "invalid-target-oxm.xml";
    private static final String OXM_DOC_V2 = PATH + "company2-oxm.xml";
    private static final String XSD_DOC = PATH + "company.xsd";
    private static final String WORK_ADD_XSD_DOC = PATH + "work-address.xsd";

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlJoinNodeTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[] { Company.class });
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("http://www.example.com", "x");
        jaxbUnmarshaller.setProperty(JAXBUnmarshaller.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
    }
    
    protected Marshaller getJSONMarshaller() throws Exception{
    	Marshaller m = jaxbContext.createMarshaller();
    	Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("http://www.example.com", "x");
        m.setProperty(JAXBMarshaller.NAMESPACE_PREFIX_MAPPER, namespaces);
        m.setProperty(JAXBMarshaller.MEDIA_TYPE, "application/json");
    	return m;
    }
    
  
    /**
     * Return the control Company object.
     */
    public Object getControlObject() {
        Address ottawa100 = new Address("a100", "45 O'Connor St.", "400", "Kanata", "K1P1A4");
        Address ottawa200 = new Address("a200", "1 Anystreet Rd.", "9", "Ottawa", "K4P1A2");
        Address kanata100 = new Address("a101", "99 Some St.", "1001", "Kanata", "K0A3m0");
        Employee emp101 = new Employee("e101", ottawa100);
        Employee emp102 = new Employee("e102", kanata100);
        ArrayList empList = new ArrayList();
        empList.add(emp101);
        empList.add(emp102);
        ArrayList<Address> addList = new ArrayList<Address>();
        addList.add(kanata100);
        addList.add(ottawa100);
        addList.add(ottawa200);
        return new Company(empList, addList);
    }

    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream(OXM_DOC);
		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
        return properties;
	}    

	public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream(XSD_DOC);    	
    	InputStream is2 = ClassLoader.getSystemResourceAsStream(WORK_ADD_XSD_DOC);;    	
    	controlSchemas.add(is);    	
    	
    	controlSchemas.add(is2);
    	
    	super.testSchemaGen(controlSchemas);    
    }

    /**
     * Verifies that the xml-key entries were processed and set on 
     * the Address descriptor.
     * 
     * Positive test.
     */
    public void testPrimaryKeysWereSet() {
        XMLDescriptor xdesc = xmlContext.getDescriptor(new QName("business-address"));
        Vector<String> pkFields = xdesc.getPrimaryKeyFieldNames();
        assertTrue("Expected [2] primary key fields for Address, but were [" + pkFields.size() + "]", pkFields.size() == 2);
        assertTrue("Expected primary key field [@id] for Address, but was [" + pkFields.elementAt(0) + "]", pkFields.elementAt(0).equals("@id"));
        assertTrue("Expected primary key field [city/text()] for Address, but was [" + pkFields.elementAt(1) + "]", pkFields.elementAt(1).equals("city/text()"));
    }
   

    /**
     * Tests that an exception is thrown if XmlJoinNode is set on an invalid Property,
     * as in the case where the Property type is String.
     * 
     * Negative test.
     */
    public void testInvalidXmlJoinNode() {
        boolean exception = false;
        try {        	
        	InputStream inputStream = ClassLoader.getSystemResourceAsStream(INVALID_OXM_DOC);

    		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
    	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode", new StreamSource(inputStream));
    	    Map<String, Map<String, Source>> invalidProperties = new HashMap<String, Map<String, Source>>();
    	    invalidProperties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
          
            JAXBContextFactory.createContext(new Class[] { Company.class }, invalidProperties);
        } catch (JAXBException e) {
            exception = true;
        }
        assertTrue("The excepted exception was not thrown.", exception);
    }
    
    /**
     * Tests that an exception is thrown if a target XmlPath is invalid.
     * 
     * Negative test.
     */
    public void testInvalidTargetXPath() {
        try {
        	InputStream inputStream = ClassLoader.getSystemResourceAsStream(INVALID_XPATH_OXM_DOC);

    		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
    	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode", new StreamSource(inputStream));
    	    Map<String, Map<String, Source>> invalidProperties = new HashMap<String, Map<String, Source>>();
    	    invalidProperties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
          
            JAXBContextFactory.createContext(new Class[] { Company.class }, invalidProperties);
            	
        } catch (JAXBException e) {
            return;
        } catch (Exception ex) {
            fail("An unexpected exception was thrown.");
        }
        fail("The expected JAXBException was not thrown.");
    }
    
    /**
     * Tests that an exception is thrown if the target class has no XmlID or
     * XmlKey properties.
     * 
     * Negative test.
     */
    public void testTargetWithNoKey() {
        try {

        	InputStream inputStream = ClassLoader.getSystemResourceAsStream(INVALID_TARGET_OXM_DOC);

    		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
    	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode", new StreamSource(inputStream));
    	    Map<String, Map<String, Source>> invalidProperties = new HashMap<String, Map<String, Source>>();
    	    invalidProperties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
          
            JAXBContextFactory.createContext(new Class[] { Company.class }, invalidProperties);
      
        } catch (JAXBException e) {
            return;
        } catch (Exception ex) {
            fail("An unexpected exception was thrown.");
        }
        fail("The expected JAXBException was not thrown.");
    }

    public void testContainerType() {
        JAXBContext jCtx = null;
        try {
           	InputStream inputStream = ClassLoader.getSystemResourceAsStream(OXM_DOC_V2);

    		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
    	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode", new StreamSource(inputStream));
    	    Map<String, Map<String, Source>> invalidProperties = new HashMap<String, Map<String, Source>>();
    	    invalidProperties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
          
    	    jCtx = (JAXBContext)JAXBContextFactory.createContext(new Class[] { Company.class }, invalidProperties);
      
             	
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An exception occurred while creating the JAXBContext.");
        }
        XMLDescriptor xDesc = jCtx.getXMLContext().getDescriptor(new QName("company"));
        assertNotNull("No descriptor was generated for Company.", xDesc);
        DatabaseMapping mapping = xDesc.getMappingForAttributeName("employees");
        assertNotNull("No mapping exists on Customer for attribute [employees].", mapping);
        assertTrue("Expected an XMLCollectionReferenceMapping for attribute [employees], but was [" + mapping.toString() +"].", mapping instanceof XMLCollectionReferenceMapping);
        assertTrue("Expected container class [java.util.LinkedList] but was ["+((XMLCollectionReferenceMapping) mapping).getContainerPolicy().getContainerClassName()+"]", ((XMLCollectionReferenceMapping) mapping).getContainerPolicy().getContainerClassName().equals("java.util.LinkedList"));
    }
    


}
