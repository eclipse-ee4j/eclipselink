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
 *     Denise Smith - 2.3
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.annotations.xmlnametransformer;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class JAXBDefaultNameTransformerTestCases extends JAXBWithJSONTestCases {

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlnametransformer/employeeDefault.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlnametransformer/employeeDefault.json";
	private final static String CONTROL_RESPONSIBILITY1 = "Fix Bugs";
	private final static String CONTROL_RESPONSIBILITY2 = "Write JAXB2.0 Prototype";
	private final static String CONTROL_RESPONSIBILITY3 = "Write Design Spec";
	private final static String CONTROL_FIRST_NAME = "Bob";
	private final static String CONTROL_LAST_NAME = "Smith";
	private final static int CONTROL_ID = 10;

    public JAXBDefaultNameTransformerTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = Employee.class;
        setClasses(classes);
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
		
		employee.setBlah("Some String");
              	
		Address addr = new Address();
		addr.id = 10;
		addr.cityName = "Ottawa";
		employee.address = addr;
		
        return employee;
    }   
    
    public void testXMLToObjectFromXMLStreamReader() throws Exception {
        if(System.getProperty("java.version").contains("1.6")) {
        	InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        	javax.xml.stream.XMLInputFactory factory = javax.xml.stream.XMLInputFactory.newInstance();
        	javax.xml.stream.XMLStreamReader reader = factory.createXMLStreamReader(instream);
        	
        	Object obj = getJAXBUnmarshaller().unmarshal(reader);
        	this.xmlToObjectTest(obj);
        }
    }
    
    public void testXMLToObjectFromXMLEventReader() throws Exception {
        if(System.getProperty("java.version").contains("1.6")) {
        	InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        	javax.xml.stream.XMLInputFactory factory = javax.xml.stream.XMLInputFactory.newInstance();
        	javax.xml.stream.XMLEventReader reader = factory.createXMLEventReader(instream);
        	
        	Object obj = getJAXBUnmarshaller().unmarshal(reader);
        	this.xmlToObjectTest(obj);
        }
    }
    
    public void testObjectToXMLStreamWriter() throws Exception {
        if(System.getProperty("java.version").contains("1.6")) {
        	StringWriter writer = new StringWriter();
        	Object objectToWrite = getWriteControlObject();
        	javax.xml.stream.XMLOutputFactory factory = javax.xml.stream.XMLOutputFactory.newInstance();
        	javax.xml.stream.XMLStreamWriter streamWriter = factory.createXMLStreamWriter(writer);
 
 
        	getJAXBMarshaller().marshal(objectToWrite, streamWriter);
        
        
        	StringReader reader = new StringReader(writer.toString());
        	InputSource inputSource = new InputSource(reader);
        	Document testDocument = parser.parse(inputSource);
        	writer.close();
        	reader.close();

        	objectToXMLDocumentTest(testDocument);
        }
    }  

}
