/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Denise Smith - 2.4
 ******************************************************************************/

package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementwrapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementWrapperElementOverrideTestCases extends JAXBWithJSONTestCases{
   
	private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/employee-xmlelement.xml";
	private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/employee-xmlelement.json";
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     * @throws Exception 
     */
    public XmlElementWrapperElementOverrideTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Employee.class });
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
    }
    
    public Map getProperties() {
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/eclipselink-oxm-xmlelement.xml");

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
		InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/schema_xmlelement.xsd");
		controlSchemas.add(is);
		super.testSchemaGen(controlSchemas);

	}

	public void testInstanceDocValidation() {
		InputStream schema = ClassLoader
				.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/schema_xmlelement.xsd");
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

}
