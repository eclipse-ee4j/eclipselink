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
 *     Denise Smith - 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlIdRefTestCases extends JAXBWithJSONTestCases{
	private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlidref/root.xml";
	private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlidref/root.json";
	public XmlIdRefTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
		setClasses(new Class[] { Employee.class, Address.class, Root.class });
	}
	
	 public Map getProperties(){
			InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlidref/eclipselink-oxm.xml");

			HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref", new StreamSource(inputStream));
		    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        
	        return properties;
		}
	    
	  /**
	     * Tests @XmlID override via eclipselink-oxm.xml.  Overrides the @XmlID [city]
	     * with xml-id [id], and @XmlIDREF [homeAddress] with xml-idref [workAddress].
	     * 
	     * Positive test.
	     */
	    public void testSchemaGen() throws Exception{
	    	List controlSchemas = new ArrayList();
	    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlidref/schema.xsd");
	    	
	    	controlSchemas.add(is);
	    	
	    	super.testSchemaGen(controlSchemas);
	    	
	    	
	    }
	
	@Override
    public Object getControlObject() {
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
     * Verifies that the primary key [primary-key/@aid] was set correctly 
     * on the Address descriptor.
     */
    public void testPrimaryKeyWasSet() {
        XMLDescriptor xdesc = ((JAXBContext) jaxbContext).getXMLContext().getDescriptor(new QName("address"));
        Vector<String> pkFields = xdesc.getPrimaryKeyFieldNames();
        assertTrue("Expected [1] primary key field for Address, but was [" + pkFields.size() + "]", pkFields.size() == 1);
        assertTrue("Expected primary key field [primary-key/@aid] for Address, but was [" + pkFields.elementAt(0) + "]", pkFields.elementAt(0).equals("primary-key/@aid"));
    }
    
    
    public void xmlToObjectTest(Object testObject) throws Exception {
    	super.xmlToObjectTest(testObject);
        Root testRoot = (Root) testObject;
        assertNotNull(testRoot);
        Employee emp = testRoot.employees.get(0);
        assertNotNull(emp);
        Address workAddress = emp.workAddress;
        assertNotNull(workAddress);
        assertNotNull(workAddress.id);
        assertNotNull(workAddress.city);
        assertTrue("Expected work address id [wa111] but was [" + workAddress.id + "]", workAddress.id.equals("wa111"));
        assertTrue("Expected work address city [Ottawa] but was [" + workAddress.city + "]", workAddress.city.equals("Ottawa"));        
    }

	
}
