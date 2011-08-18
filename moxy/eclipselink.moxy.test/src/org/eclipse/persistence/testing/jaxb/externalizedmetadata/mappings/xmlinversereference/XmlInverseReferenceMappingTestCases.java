/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * mmacivor - April 20/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmlinversereference;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLInverseReferenceMapping;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

/**
 *
 */
public class XmlInverseReferenceMappingTestCases extends JAXBTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/xmlinversereference/root.xml";
    private static final String CONTROL_ID = "a222";
    private static final String CONTROL_NAME = "Joe Smith";
    private static final String CONTROL_ADD_ID_1 = "a199";
    private static final String CONTROL_ADD_STREET_1 = "Some Other St.";
    private static final String CONTROL_ADD_CITY_1 = "Anyothertown";
    private static final String CONTROL_ADD_COUNTRY_1 = "Canada";
    private static final String CONTROL_ADD_ZIP_1 = "X0X0X0";
    private static final String CONTROL_ADD_ID_2 = "a99";
    private static final String CONTROL_ADD_STREET_2 = "Some St.";
    private static final String CONTROL_ADD_CITY_2 = "Anytown";
    private static final String CONTROL_ADD_COUNTRY_2 = "Canada";
    private static final String CONTROL_ADD_ZIP_2 = "X0X0X0";
    private static final String CONTROL_PHONE_ID_1 = "a123";
    private static final String CONTROL_PHONE_NUM_1 = "613-123-4567";
    private static final String CONTROL_PHONE_ID_2 = "a456";
    private static final String CONTROL_PHONE_NUM_2 = "613-234-5678";
    
    public XmlInverseReferenceMappingTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setClasses(new Class[]{Root.class});
    }

    protected Root getControlObject() {
        Employee employee = new Employee();
        employee.id = CONTROL_ID;
        employee.name = CONTROL_NAME;
        
        Address address = new Address();
        address.id = CONTROL_ADD_ID_1;
        address.street = CONTROL_ADD_STREET_1;
        address.city = CONTROL_ADD_CITY_1;
        address.country = CONTROL_ADD_COUNTRY_1;
        address.zip = CONTROL_ADD_ZIP_1;
        address.emp = new Vector<Object>();
        address.emp.add(employee);
        
        employee.address = address;
        
        employee.phones = new ArrayList();
        
        PhoneNumber num = new PhoneNumber();
        num.id = CONTROL_PHONE_ID_1;
        num.number = CONTROL_PHONE_NUM_1;
        num.emp = employee;
        employee.phones.add(num);
        
        num = new PhoneNumber();
        num.id = CONTROL_PHONE_ID_2;
        num.number = CONTROL_PHONE_NUM_2;
        num.emp = employee;
        employee.phones.add(num);
        
        Root root = new Root();
        root.employee = employee;
        return root;
    }

    public Root getWriteControlObject() {
        ArrayList rootAddresses = new ArrayList();
        ArrayList rootPhones = new ArrayList();

        Employee employee = new Employee();
        employee.id = CONTROL_ID;
        employee.name = CONTROL_NAME;
        
        Address address = new Address();
        address.id = CONTROL_ADD_ID_1;
        address.street = CONTROL_ADD_STREET_1;
        address.city = CONTROL_ADD_CITY_1;
        address.country = CONTROL_ADD_COUNTRY_1;
        address.zip = CONTROL_ADD_ZIP_1;
        address.emp = new Vector<Object>();
        address.emp.add(employee);
        rootAddresses.add(address);
        
        employee.address = address;
        
        address = new Address();
        address.id = CONTROL_ADD_ID_2;
        address.street = CONTROL_ADD_STREET_2;
        address.city = CONTROL_ADD_CITY_2;
        address.country = CONTROL_ADD_COUNTRY_2;
        address.zip = CONTROL_ADD_ZIP_2;
        rootAddresses.add(address);
        employee.phones = new ArrayList();
        
        PhoneNumber num = new PhoneNumber();
        num.id = CONTROL_PHONE_ID_1;
        num.number = CONTROL_PHONE_NUM_1;
        num.emp = employee;
        employee.phones.add(num);
        rootPhones.add(num);
        
        num = new PhoneNumber();
        num.id = CONTROL_PHONE_ID_2;
        num.number = CONTROL_PHONE_NUM_2;
        num.emp = employee;
        employee.phones.add(num);
        rootPhones.add(num);
        
        Root root = new Root();
        root.employee = employee;
        root.addresses = rootAddresses;
        root.phoneNumbers = rootPhones;
        return root;
    }
    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/xmlinversereference/root-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmlinversereference", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
        
        return properties;
	}
    
    public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/xmlinversereference/root.xsd");
    	controlSchemas.add(is);
    	super.testSchemaGen(controlSchemas);
    }

    public void testAddressContainerType() {    	
        XMLDescriptor xDesc = xmlContext.getDescriptor(new QName("address"));
        assertNotNull("No descriptor was generated for Address.", xDesc);
        DatabaseMapping mapping = xDesc.getMappingForAttributeName("emp");
        assertNotNull("No mapping exists on Address for attribute [emp].", mapping);
        assertTrue("Expected an XMLInverseReferenceMapping for attribute [emp], but was [" + mapping.toString() +"].", mapping instanceof XMLInverseReferenceMapping);
        assertTrue("Expected container class [java.util.LinkedList] but was ["+((XMLInverseReferenceMapping) mapping).getContainerPolicy().getContainerClassName()+"]", ((XMLInverseReferenceMapping) mapping).getContainerPolicy().getContainerClassName().equals("java.util.LinkedList"));
    }
}
