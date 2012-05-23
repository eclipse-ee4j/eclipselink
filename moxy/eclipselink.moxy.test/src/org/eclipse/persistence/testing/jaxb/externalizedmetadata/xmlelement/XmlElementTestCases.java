/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests XmlElement via eclipselink-oxm.xml
 *
 */
public class XmlElementTestCases extends JAXBWithJSONTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/employee.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/employee.json";
    private static final String XML_RESOURCE_INVALID = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/employee-invalid.xml";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     * @throws Exception 
     */
    public XmlElementTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[] { Employee.class});
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
	    	InputStream is2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/schema2.xsd");
	    	controlSchemas.add(is);
	    	controlSchemas.add(is2);	  
	    	super.testSchemaGen(controlSchemas);
	    	
	    }
	   
	 
    /**
     * Tests @XmlElement override via eclipselink-oxm.xml.  The myUtilDate 
     * element is set to 'required' in the xml file, but the instance 
     * document does not have a myUtilDate element.
     * 
     * Negative test.
     */
    public void testXmlElementOverrideInvalid() {
    	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/schema.xsd");        
        StreamSource schemaSource = new StreamSource(schema); 
                
        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_INVALID);
        String result = validateAgainstSchema(instanceDocStream, schemaSource);        
        assertTrue("Schema validation passed unxepectedly", result != null);
      	
    }
    
    public void testTeamWithListOfPlayersSchemaGen() throws Exception{
    	ClassLoader loader = getClass().getClassLoader();
        String metadataFile = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/team-oxm.xml";
        
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelement", new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        
        JAXBContext jContext = (JAXBContext) JAXBContextFactory.createContext(new Class[] { Team.class }, properties, loader);
        
        MyStreamSchemaOutputResolver outputResolver = new MyStreamSchemaOutputResolver();
        jContext.generateSchema(outputResolver);

       
        List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/team-schema.xsd");	    	
    	controlSchemas.add(is);	    	
    	
    	List<Writer> generatedSchemas = outputResolver.getSchemaFiles();
        compareSchemas(controlSchemas, generatedSchemas);
        /* 
         
    	super.testSchemaGen(controlSchemas);
        
        try {
            JAXBContext jContext = (JAXBContext) JAXBContextFactory.createContext(new Class[] { Team.class }, properties, loader);
            MySchemaOutputResolver oResolver = new MySchemaOutputResolver(); 
            jContext.generateSchema(oResolver);
            // validate schema
            String controlSchema = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/team-schema.xsd";
            compareSchemas(oResolver.schemaFiles.get(""), new File(controlSchema));
        } catch (JAXBException e) {
            fail("An exception occurred creating the JAXBContext: " + e.getMessage());
        }
        */
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
        XMLDescriptor xDesc = ((JAXBContext)jaxbContext).getXMLContext().getDescriptor(new QName("employee"));
        assertNotNull("No descriptor was generated for Employee.", xDesc);
        DatabaseMapping mapping = xDesc.getMappingForAttributeName("myEmployees");
        assertNotNull("No mapping exists on Employee for attribute [myEmployees].", mapping);
        assertTrue("Expected an XMLCompositeCollectionMapping for attribute [myEmployees], but was [" + mapping.toString() +"].", mapping instanceof XMLCompositeCollectionMapping);
        assertTrue("Expected container class [java.util.LinkedList] but was ["+((XMLCompositeCollectionMapping) mapping).getContainerPolicy().getContainerClassName()+"]", ((XMLCompositeCollectionMapping) mapping).getContainerPolicy().getContainerClassName().equals("java.util.LinkedList"));
    }

	@Override
	protected Object getControlObject() {
		Employee emp = new Employee();
		emp.firstName = "firstName";
		emp.lastName = "LastName";
		emp.id = 66;
		emp.setMyInt(66);
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(1976, Calendar.FEBRUARY, 17, 6, 15,30);
			
		emp.myUtilDate = cal;
		return emp;
	}
}
