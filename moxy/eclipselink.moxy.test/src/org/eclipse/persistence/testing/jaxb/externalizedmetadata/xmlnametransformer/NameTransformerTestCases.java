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
 * Denise Smith - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.dom.DOMSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Document;

public class NameTransformerTestCases extends JAXBTestCases{
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlnametransformer/nametransformerupper.xml";
    
	private final static String CONTROL_RESPONSIBILITY1 = "Fix Bugs";
	private final static String CONTROL_RESPONSIBILITY2 = "Write JAXB2.0 Prototype";
	private final static String CONTROL_RESPONSIBILITY3 = "Write Design Spec";
	private final static String CONTROL_FIRST_NAME = "Bob";
	private final static String CONTROL_LAST_NAME = "Smith";
	private final static int CONTROL_ID = 10;

    public NameTransformerTestCases(String name) throws Exception {
        super(name);		
    }
	
    public void setUp() throws Exception {
        setControlDocument(XML_RESOURCE);
        super.setUp();
        Type[] types = new Type[3];
        types[0] = Employee.class;
        types[1] = Address.class;
        types[2] = Phone.class;
        setTypes(types);
    }

    public void init() throws Exception {	
        Type[] types = new Type[2];
        types[0] = Employee.class;
        types[1] = Address.class;       
        setTypes(types);
    }
	
    protected Object getControlObject() {
        ArrayList responsibilities = new ArrayList();
        responsibilities.add(CONTROL_RESPONSIBILITY1);
        responsibilities.add(CONTROL_RESPONSIBILITY2);
        responsibilities.add(CONTROL_RESPONSIBILITY3);

        Employee employee = new Employee();
		employee.firstName = CONTROL_FIRST_NAME;
		employee.lastName = CONTROL_LAST_NAME;
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2005,04,24,16,06,53);
			
		employee.birthday = cal;
				
		employee.id = CONTROL_ID;
		
		employee.responsibilities = responsibilities;
		
		employee.responsibilities2 = responsibilities;
		
		employee.setBlah("Some String");
             
		Phone p = new Phone();
		p.number = "1234567";
		employee.phone = p;
		
		Address addr = new Address();		
		addr.id = 1;
		addr.cityName = "Dartmouth";
		employee.address = addr;
		
        return employee;
    }
    
	
    public void testSchemaGen() throws Exception {
        List<InputStream> controlSchemas = new ArrayList<InputStream>();		
        InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlnametransformer/nametransformerupper.xsd");
        controlSchemas.add(is);		
        super.testSchemaGen(controlSchemas);
    }
	
    protected Map getProperties() throws Exception{
		
        Map overrides = new HashMap();		
        String overridesString = 
        "<?xml version='1.0' encoding='UTF-8'?>" +
        "<xml-bindings xmlns='http://www.eclipse.org/eclipselink/xsds/persistence/oxm' xml-name-transformer='org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer.MyUpperTransformer'>" +
        "<xml-schema namespace='myuri'/>" +
    	"<java-types>" +
		"<java-type name='org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer.Address' xml-name-transformer='org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer.MyDoubleTransformer'>" +
		"</java-type>" +
	    "</java-types>" +
        "</xml-bindings>";

        DOMSource src = null;
        try {		      
            Document doc = parser.parse(new ByteArrayInputStream(overridesString.getBytes()));
            src = new DOMSource(doc.getDocumentElement());
	    } catch (Exception e) {
	        e.printStackTrace();
	        fail("An error occurred during setup");
        }
		    
        overrides.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer", src);

        Map props = new HashMap();
        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
        return props;
    }	
    
   
    //package-info has MyLowerTransformer
    //address annotation has MyLowerTransformer
    //bindings xml-bindings has MyUpperTransformer
    //bindings address has MyDoubleTransformer
    //phone should get MyUpperTransformer from bindings packagelevel
    
    //Employee - MyUpperTransformer
    //Address - MyDoubleTransformer
    //Phone - MyUpperTransformer
}
