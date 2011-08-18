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
 * dmccann - August 5/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings.simple.Department;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings.simple.Employee;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings.simple.Person;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings.simple.Phone;

public class MultipleBindingsFourFilesTestCases extends JAXBWithJSONTestCases {
	  protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/multiplebindings/fourfilesmultiplebindings.xml";
	  protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/multiplebindings/fourfilesmultiplebindings.json";	  
	  XMLDescriptor employeeDesc = null;
	    public MultipleBindingsFourFilesTestCases(String name) throws Exception {
	        super(name);		
	    }
		
	    public void setUp() throws Exception {
	        setControlDocument(XML_RESOURCE);
	        setControlJSON(JSON_RESOURCE);
	        super.setUp();
	        Type[] types = new Type[2];
	        types[0] = Person.class;
	        types[1] = Employee.class;	        
	        setTypes(types);
	    }

	    public void init() throws Exception {	
	    	Type[] types = new Type[2];
		    types[0] = Person.class;
		    types[1] = Employee.class;	
	        setTypes(types);
	    }
		
	    protected Object getControlObject() {
	    	Employee emp = new Employee();
	    	emp.age = 35;
	    	emp.id = "someID";
	    	emp.name = "Bob Jones";
	    	emp.address = "123 theStreet";	  
	    	emp.jobTitle = "job3";
	    	
	    	emp.departments = new ArrayList();
	    	emp.departments.add(Department.DEV);
	    	emp.departments.add(Department.SUPPORT);
	    	
	    	Phone phone = new Phone();
	    	phone.number = "12345678";
	    	emp.phone = phone;
	        return emp;
	    }
		
	    public void testSchemaGen() throws Exception {
	        List<InputStream> controlSchemas = new ArrayList<InputStream>();		
	        InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/multiplebindings/fourfilesmultiplebindings.xsd");
	        //InputStream is2= ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/multiplebindings/fourfilesmultiplebindings2.xsd");
	        controlSchemas.add(is);
	        //controlSchemas.add(is2);
	        super.testSchemaGen(controlSchemas);	        
	    }
		
	    protected Map getProperties() throws Exception{
			
	        Map overrides = new HashMap();	

			
	        //DOMSource src1 = null;
	        //DOMSource src2 = null;
	        StreamSource src1 = null;
	        StreamSource src2 = null;	        
	        StreamSource src3 = null;
	        StreamSource src4 = null;	
	        
	        InputStream iStream = classLoader.getResourceAsStream("./org/eclipse/persistence/testing/jaxb/externalizedmetadata/multiplebindings/eclipselink-oxm1.xml");
	        InputStream iStream2 = classLoader.getResourceAsStream("./org/eclipse/persistence/testing/jaxb/externalizedmetadata/multiplebindings/eclipselink-oxm2.xml");
	        InputStream iStream3 = classLoader.getResourceAsStream("./org/eclipse/persistence/testing/jaxb/externalizedmetadata/multiplebindings/eclipselink-oxm3.xml");
	        InputStream iStream4 = classLoader.getResourceAsStream("./org/eclipse/persistence/testing/jaxb/externalizedmetadata/multiplebindings/eclipselink-oxm4.xml");
	      //  InputStream iStreamCopy = classLoader.getResourceAsStream("eclipselink-oxm1.xml");
	      
	        try {		      
	            //Document doc = parser.parse(new ByteArrayInputStream(bindings1.getBytes()));
	            //src1 = new DOMSource(doc.getDocumentElement());
	            //src2 = new DOMSource(doc.getDocumentElement());
	        	src1 = new StreamSource(iStream);
	        	src2 = new StreamSource(iStream2);
	        	src3 = new StreamSource(iStream3);
	        	src4 = new StreamSource(iStream4);	        	
		    } catch (Exception e) {		    	
		        e.printStackTrace();
		        fail("An error occurred during setup");
	        }
		    
		    ArrayList<Object> bindingsList = new ArrayList();
	        bindingsList.add(src1);
	        bindingsList.add(src2);
	        bindingsList.add(src3);	        
	        bindingsList.add(src4);
	        
	        overrides.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings.simple", bindingsList);

	        Map props = new HashMap();
	        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
	        return props;
	    }
	    

	    
	    public void testUserProperties(){
	    	String PROPKEY_1 ="1";
	    	String PROPKEY_2 ="2";
	    	String PROPKEY_3 ="3";
	    	
	    	String PROPVAL_1 ="A";
	    	Integer PROPVAL_2 =66;
	    	Boolean PROPVAL_3 =Boolean.TRUE; 
            XMLDescriptor employeeDesc =  this.xmlContext.getDescriptor(new QName("", "empRoot4"));

	    	DatabaseMapping projectNameMapping = employeeDesc.getMappingForAttributeName("id");
	    	
	    	Map props = projectNameMapping.getProperties();
	    	
	        assertNotNull("No user-defined properties exist on the mapping for [id]", props);	        
	        
	        assertTrue("Expected [3] user-defined properties, but there were [" + props.size() + "]", props.size() == 3);
	        // verify entries exist for each key
	        assertNotNull("No property found for key [" + PROPKEY_1 + "]", props.get(PROPKEY_1));
	        assertNotNull("No property found for key [" + PROPKEY_2 + "]", props.get(PROPKEY_2));
	        assertNotNull("No property found for key [" + PROPKEY_3 + "]", props.get(PROPKEY_3));
	        // verify value-types
	        assertTrue("Expected value-type [String] for key [" + PROPKEY_1 + "] but was [" + props.get(PROPKEY_1).getClass().getName() + "]", props.get(PROPKEY_1) instanceof String);
	        assertTrue("Expected value-type [Integer] for key [" + PROPKEY_2 + "] but was [" + props.get(PROPKEY_2).getClass().getName() + "]", props.get(PROPKEY_2) instanceof Integer);
	        assertTrue("Expected value-type [Boolean] for key [" + PROPKEY_3 + "] but was [" + props.get(PROPKEY_3).getClass().getName() + "]", props.get(PROPKEY_3) instanceof Boolean);
	        // verify values
	        assertTrue("Expected property value [" + PROPVAL_1 + "] for key [" + PROPKEY_1 + "] but was [" + props.get(PROPKEY_1) + "]", PROPVAL_1.equals(props.get(PROPKEY_1)));
	        assertTrue("Expected property value [" + PROPVAL_2 + "] for key [" + PROPKEY_2 + "] but was [" + props.get(PROPKEY_2) + "]", PROPVAL_2.equals(props.get(PROPKEY_2)));
	        assertTrue("Expected property value [" + PROPVAL_3 + "] for key [" + PROPKEY_3 + "] but was [" + props.get(PROPKEY_3) + "]", PROPVAL_3 == (Boolean) props.get(PROPKEY_3));
	        
	    }
	    
	    public void testDescriptorProperties() {
	        // create the JAXBContext
	        assertNotNull("JAXBContext creation failed", xmlContext);
	        XMLDescriptor xdesc = xmlContext.getDescriptor(new QName("", "empRoot4"));
	        assertNotNull("Employee descriptor is null", xdesc);
	        assertNotNull("No user-defined properties exist on the descriptor for Employee", xdesc.getProperties());
	        Map props = xdesc.getProperties();
	        assertEquals(8, props.size());
	        assertEquals("I", props.get("2"));
	    }

        public void testNamespaceResolver(){
            assertNotNull("JAXBContext creation failed", xmlContext);
            XMLDescriptor xdesc = xmlContext.getDescriptor(new QName("", "empRoot4"));
            assertNotNull("Employee descriptor is null", xdesc);

            // Test merged namespaces
            // Ensure that there are two namespaces for prefix "uriA" and one for "uriB"
            String uriA = "http://www.example.com/uriA";
            String uriB = "http://www.example.com/uriB";

            String prefixA = "prefixA";
            String prefixNewA = "newPrefixA";
            String prefixB = "prefixB";

            assertEquals(uriA, xdesc.getNamespaceResolver().resolveNamespacePrefix(prefixA));
            assertEquals(uriA, xdesc.getNamespaceResolver().resolveNamespacePrefix(prefixNewA));
            assertEquals(uriB, xdesc.getNamespaceResolver().resolveNamespacePrefix(prefixB));
        }

}
