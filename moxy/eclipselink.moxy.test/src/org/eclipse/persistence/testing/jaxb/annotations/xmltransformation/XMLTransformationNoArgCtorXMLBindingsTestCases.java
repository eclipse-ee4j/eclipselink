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
 * Denise Smith - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmltransformation;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.dom.DOMSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;

public class XMLTransformationNoArgCtorXMLBindingsTestCases extends JAXBWithJSONTestCases {
    public XMLTransformationNoArgCtorXMLBindingsTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {EmployeeWithAddress.class});
        setControlDocument("org/eclipse/persistence/testing/jaxb/annotations/xmltransformation/employee.xml");
        setControlJSON("org/eclipse/persistence/testing/jaxb/annotations/xmltransformation/employee.json");
    }
    
    public Object getControlObject() {
    	EmployeeWithAddress emp = new EmployeeWithAddress();
        emp.name = "John Smith";
        AddressNoCtor address = new AddressNoCtor("theStreet", "theCity");        
        emp.address = address;
        
        return emp;
    }
    
    protected Map getProperties() {
		
	     Map overrides = new HashMap();				
	        String overridesString =
	        "<?xml version='1.0' encoding='UTF-8'?>" +
	 	        "<xml-bindings xmlns='http://www.eclipse.org/eclipselink/xsds/persistence/oxm'>" +
	 	       "<java-types>" +
		        "<java-type name='org.eclipse.persistence.testing.jaxb.annotations.xmltransformation.EmployeeWithAddress'>" + 
		        "<java-attributes>" +
                "<xml-transformation java-attribute='address' optional='true'>" +
                "<xml-read-transformer transformer-class='org.eclipse.persistence.testing.jaxb.annotations.xmltransformation.AddressTransformer'/>" +
                "<xml-write-transformer transformer-class='org.eclipse.persistence.testing.jaxb.annotations.xmltransformation.AddressTransformer' xml-path='address/street/text()'/>" +
                "<xml-write-transformer transformer-class='org.eclipse.persistence.testing.jaxb.annotations.xmltransformation.AddressTransformer' xml-path='address/city/text()'/>" +
                "</xml-transformation>" + 
	                "</java-attributes> " +
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
		    
       overrides.put("org.eclipse.persistence.testing.jaxb.annotations.xmltransformation", src);

       Map props = new HashMap();
       props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
       return props;
   }	
    
}