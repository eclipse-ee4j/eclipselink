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
 * dmccann - September 25/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementwrapper;

import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests XmlElementWrapper via eclipselink-oxm.xml
 *
 */
public class XmlElementWrapperTestCases extends JAXBWithJSONTestCases{
   
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/";
	private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/employee.xml";
	private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/employee.json";
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     * @throws Exception 
     */
    public XmlElementWrapperTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Employee.class });
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
    }
    
    public Map getProperties() {
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/eclipselink-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementwrapper",
						new StreamSource(inputStream));
		Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY,
				metadataSourceMap);

		return properties;
	}

	public void testSchemaGen() throws Exception {
		List controlSchemas = new ArrayList();
		InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/schema.xsd");
		controlSchemas.add(is);
		super.testSchemaGen(controlSchemas);

	}

	public void testInstanceDocValidation() {
		InputStream schema = ClassLoader
				.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/schema.xsd");
		StreamSource schemaSource = new StreamSource(schema);

		InputStream instanceDocStream = ClassLoader
				.getSystemResourceAsStream(XML_RESOURCE);
		String result = validateAgainstSchema(instanceDocStream, schemaSource);
		assertTrue("Instance doc validation (employee.xml) failed unxepectedly: "+ result, result == null);
	}

	@Override
	protected Object getControlObject() {
		// setup control objects
		Employee emp = new Employee();
        int[] theDigits = new int[] { 666, 999 };
        emp.digits = theDigits;
        return emp;
	}

    
    /**
     * Tests @XmlElementWrapper via eclipselink-oxm.xml.  No overrides are done,
     * so the class annotations should be used to generate the schema.
     * 
     * Positive test.
     * @throws Exception 
     */
    public void testXmlElementWrapperNoOverrideSchemaGen() throws Exception {
    	 JAXBContext ctx = JAXBContextFactory.createContext(new Class[] { Employee.class }, null);
    	
    	 MyStreamSchemaOutputResolver outputResolver = new MyStreamSchemaOutputResolver();
         ctx.generateSchema(outputResolver);

         List<Writer> generatedSchemas = outputResolver.getSchemaFiles();
         
     	List controlSchemas = new ArrayList();
		InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/schemaNoOverride.xsd");
		controlSchemas.add(is);
         compareSchemas(controlSchemas, generatedSchemas);

 		InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/schemaNoOverride.xsd");

 		StreamSource schemaSource = new StreamSource(schema);

 		InputStream instanceDocStream = ClassLoader
			.getSystemResourceAsStream(XML_RESOURCE);
 		String result = validateAgainstSchema(instanceDocStream, schemaSource);
 		assertTrue("Schema validation failed unxepectedly: " + result, result == null);

    
    }

    /**
     * Tests @XmlElementWrapper via eclipselink-oxm.xml.  Here, a number of
     * overrides are performed.
     * 
     * Here, @XmlElementWrapper.namespace() is not "##default" and different 
     * from the target namespace of the enclosing class.  An element declaration 
     * whose name is @XmlElementWrapper.name() and target namespace is
     * @XmlElementWrapper.namespace() should be generated.  Note: The element 
     * declaration is assumed to already exist and is not created.
     * 
     * Positive test.
     * @throws Exception 
     */
    public void testXmlElementWrapperNSSchemaGen() throws Exception {
    	InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/eclipselink-oxm-ns.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementwrapper",
						new StreamSource(inputStream));
		Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY,
				metadataSourceMap);
    	
    	 JAXBContext ctx = JAXBContextFactory.createContext(new Class[] { Employee.class }, properties);
    	
    	 MyStreamSchemaOutputResolver outputResolver = new MyStreamSchemaOutputResolver();
         ctx.generateSchema(outputResolver);

         List<Writer> generatedSchemas = outputResolver.getSchemaFiles();
         
     	List controlSchemas = new ArrayList();
		InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/schema_ns.xsd");
		controlSchemas.add(is);
         compareSchemas(controlSchemas, generatedSchemas);
    
    }
  

    /**
     * Tests @XmlElementWrapper via eclipselink-oxm.xml.  The instance document
     * does not contain the 'my-digits' wrapper, and is therefore invalid.  
     * 
     * Negative test.
     */
    public void testXmlElementWrapperNoWrapper() {
    	InputStream schema = ClassLoader
		.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/schema.xsd");
StreamSource schemaSource = new StreamSource(schema);

InputStream instanceDocStream = ClassLoader
		.getSystemResourceAsStream(PATH + "employee-invalid.xml");
String result = validateAgainstSchema(instanceDocStream, schemaSource);
assertTrue("Schema validation passed unxepectedly", result != null);

    }
  
}
