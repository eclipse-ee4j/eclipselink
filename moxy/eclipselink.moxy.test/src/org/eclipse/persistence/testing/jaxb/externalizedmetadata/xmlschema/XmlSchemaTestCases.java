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
 * dmccann - June 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;


import org.eclipse.persistence.jaxb.JAXBContextFactory;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;


/** Tests package-info override via eclipselink-oxm.xml.  
* 
* The value in package-info.java for prefix 'nsx' is "http://www.example.com/xsds/fake", but 
* due to the override in the oxm.xml file it should be "http://www.example.com/xsds/real".
* 
* The value in package-info.java for target namespace is 
* "http://www.eclipse.org/eclipselink/xsds/persistence/oxm/junk", but due to the override in
* the oxm.xml file it should be "http://www.eclipse.org/eclipselink/xsds/persistence/oxm"
* 
* Also, elementForm and attributeForm are both QUALIFIED in package-info, but set to 
* UNQUALIFIED in the oxm.xml file. 
* 
* Positive test.
*/
public class XmlSchemaTestCases extends JAXBWithJSONTestCases {
    static String PREFIX = "nsx";
    static String NSX_OVERRIDE_VALUE = "http://www.example.com/xsds/real";
    static boolean FORM_DEFAULT_VALUE = false;
    static String NAMESPACE = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm";
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/employee.xml";
    private static final String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/employee-write.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/employee.json";
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlSchemaTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{Employee.class});
    }
    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/eclipselink-oxm.xml");
		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
        return properties;
	}    

	public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/employee.xsd");
    	controlSchemas.add(is);
    	
    	super.testSchemaGen(controlSchemas);
 
    	InputStream schemaInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/employee.xsd");
    	InputStream controlDocStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
    	String result = validateAgainstSchema(controlDocStream, new StreamSource(schemaInputStream));	    	
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);

    	InputStream schemaInputStream2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/employee.xsd");       
    	InputStream invalidControlDocStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/employee-invalidnamespace.xml");
    	result = validateAgainstSchema(invalidControlDocStream, new StreamSource(schemaInputStream2));
        assertTrue("Schema validation passed unxepectedly", result != null);

    	InputStream schemaInputStream3 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/employee.xsd");       

    	InputStream addressControlDocStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/address.xml");
    	result = validateAgainstSchema(addressControlDocStream, new StreamSource(schemaInputStream3));
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);

    	InputStream schemaInputStream4 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/employee.xsd");       

    	InputStream addressInvalidControlDocStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/address-invalidnamespace.xml");
    	result = validateAgainstSchema(addressInvalidControlDocStream, new StreamSource(schemaInputStream4));
        assertTrue("Schema validation passed unxepectedly", result != null);

    }

	protected Object getControlObject() {
		Employee emp = new Employee();
		emp.firstName ="firstName";
		emp.lastName = "LastName";		
		return emp;
	}
    
}
