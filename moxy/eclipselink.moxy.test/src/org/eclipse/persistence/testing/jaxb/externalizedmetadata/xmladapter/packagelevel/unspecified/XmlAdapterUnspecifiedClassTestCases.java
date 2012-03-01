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
 *     Denise Smith - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.unspecified;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.dom.DOMSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel.MyCalendar;
import org.w3c.dom.Document;

public class XmlAdapterUnspecifiedClassTestCases extends JAXBWithJSONTestCases{
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/packagelevel/unspecified/employee.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/packagelevel/unspecified/employee.json";
    
    public XmlAdapterUnspecifiedClassTestCases(String name) throws Exception {
        super(name);		
    }
    public void setUp() throws Exception {
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        super.setUp();
        Type[] types = new Type[1];
        types[0] = Employee.class;
        setTypes(types);
    }

	protected Object getControlObject() {
		MyCalendar myCalendar = new MyCalendar();
		myCalendar.day = 1;
		myCalendar.month = 1;
		myCalendar.year = 2011;
		
		Employee emp = new Employee();
		Address addr = new Address();
		addr.id = new BigDecimal("1");
		addr.cityName = "Ottawa";
		addr.effectiveDate = myCalendar;
		emp.id = new BigDecimal("66");	
		emp.firstName = "Joe";
		emp.birthday = myCalendar;
		emp.lastName = "Smith";
		emp.address = addr;
		return emp;
	}
	
	 protected Map getProperties() throws Exception{
			
	        Map overrides = new HashMap();		
	        String overridesString = 
	        "<?xml version='1.0' encoding='UTF-8'?>" +
	        "<xml-bindings xmlns='http://www.eclipse.org/eclipselink/xsds/persistence/oxm'>" +	               
	        "<xml-schema-types>" + 
            "<xml-schema-type name='int' type='java.math.BigDecimal'/>" +
            "</xml-schema-types> " +
            "<xml-java-type-adapters> " +
            "<xml-java-type-adapter value='org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel.MyCalendarAdapter' type='org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel.MyCalendar'/>" + 
            "</xml-java-type-adapters>" +	        
	        "</xml-bindings>";
	        	       
	        DOMSource src = null;
	        try {		      
	            Document doc = parser.parse(new ByteArrayInputStream(overridesString.getBytes()));
	            src = new DOMSource(doc.getDocumentElement());
		    } catch (Exception e) {
		        e.printStackTrace();
		        fail("An error occurred during setup");
	        }
			    
	        overrides.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.unspecified", src);

	        Map props = new HashMap();
	        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
	        return props;
	    }		
	 
	  public void testSchemaGen() throws Exception {
	        List<InputStream> controlSchemas = new ArrayList<InputStream>();		
	        InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/packagelevel/unspecified/schema.xsd");
	        controlSchemas.add(is);		
	        super.testSchemaGen(controlSchemas);
	    }
}
