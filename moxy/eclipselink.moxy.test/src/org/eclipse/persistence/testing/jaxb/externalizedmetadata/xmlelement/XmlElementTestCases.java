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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelement;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;

/**
 * Tests XmlElement via eclipselink-oxm.xml
 *
 */
public class XmlElementTestCases extends JAXBTestCases {
    private boolean shouldGenerateSchema = true;
    private MyStreamSchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelement";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/";
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/employee.xml";
    private static final String XML_INVALID_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/employee-invalid.xml";
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlElementTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_RESOURCE);
        setClasses(new Class[] { Employee.class});
        outputResolver = new MyStreamSchemaOutputResolver();
    }
    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/eclipselink-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelement", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
        return properties;
	}
    
    public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/schema.xsd");
    	InputStream is2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/schema.xsd");
    	controlSchemas.add(is);
    	controlSchemas.add(is2);
    	super.testSchemaGen(controlSchemas);
    	
    	
    }
    /*
    private void doGenerateSchema() {
        if (shouldGenerateSchema) {
            generateSchemaWithFileName(new Class[] { Employee.class} , CONTEXT_PATH, PATH + "eclipselink-oxm.xml", 2, outputResolver);
            // validate schema
            String controlSchema = PATH + "schema.xsd";
            compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE).toString(), new File(controlSchema));
            shouldGenerateSchema = false;
        }
    }
    */
    /**
     * Tests @XmlElement override via eclipselink-oxm.xml.  
     * 
     * Positive test.
     */
    /*
    public void testXmlElementOverride() {
        doGenerateSchema();
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
*/
    /**
     * Tests @XmlElement override via eclipselink-oxm.xml.  The myUtilDate 
     * element is set to 'required' in the xml file, but the instance 
     * document does not have a myUtilDate element.
     * 
     * Negative test.
     */
    public void testXmlElementOverrideInvalid() {
      //  doGenerateSchema();
        //String src = PATH + "employee-invalid.xml";
        //String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        InputStream is = getClass().getClassLoader().getResourceAsStream(XML_INVALID_RESOURCE);
        InputStream xsd_is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/schema.xsd");
        StreamSource schemaSource = new StreamSource(xsd_is);
        String result = super.validateAgainstSchema(is, schemaSource);
        assertTrue("Schema validation passed unxepectedly", result != null);
    }
    /*
    public void testTeamWithListOfPlayersSchemaGen() {
        String metadataFile = PATH + "team-oxm.xml";
        
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        
        try {
            JAXBContext jContext = (JAXBContext) JAXBContextFactory.createContext(new Class[] { Team.class }, properties, loader);
            MySchemaOutputResolver oResolver = new MySchemaOutputResolver(); 
            jContext.generateSchema(oResolver);
            // validate schema
            String controlSchema = PATH + "team-schema.xsd";
            compareSchemas(oResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
        } catch (JAXBException e) {
            fail("An exception occurred creating the JAXBContext: " + e.getMessage());
        }
    }
    */
    /**
     * Test setting the container class via container-type attribute.
     * 
     * Positive test.
     */
    public void testContainerType() {
        XMLDescriptor xDesc = xmlContext.getDescriptor(new QName("employee"));
        assertNotNull("No descriptor was generated for Employee.", xDesc);
        DatabaseMapping mapping = xDesc.getMappingForAttributeName("myEmployees");
        assertNotNull("No mapping exists on Employee for attribute [myEmployees].", mapping);
        assertTrue("Expected an XMLCompositeCollectionMapping for attribute [myEmployees], but was [" + mapping.toString() +"].", mapping instanceof XMLCompositeCollectionMapping);
        assertTrue("Expected container class [java.util.LinkedList] but was ["+((XMLCompositeCollectionMapping) mapping).getContainerPolicy().getContainerClassName()+"]", ((XMLCompositeCollectionMapping) mapping).getContainerPolicy().getContainerClassName().equals("java.util.LinkedList"));
    }

	protected Object getControlObject() {
		Employee emp = new Employee();
		emp.firstName = "firstName";
		emp.lastName = "LastName";
		emp.id = 66;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 1976);
		cal.set(Calendar.MONTH, 1);		
		cal.set(Calendar.DAY_OF_MONTH, 17);
		
		cal.set(Calendar.HOUR_OF_DAY, 6);
		cal.set(Calendar.MINUTE, 15);
		cal.set(Calendar.SECOND, 30);
		cal.set(Calendar.MILLISECOND, 0);
		
		
		
		emp.myUtilDate = cal;
		return emp;
	}
}
