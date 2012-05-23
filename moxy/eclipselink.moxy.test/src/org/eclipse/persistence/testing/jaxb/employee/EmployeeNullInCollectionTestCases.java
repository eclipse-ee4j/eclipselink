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
 *     Matt MacIvor - 2.3.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.employee;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class EmployeeNullInCollectionTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/employee/employee_null.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/employee/employee_null.json";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/employee/employee_null.xml";   
    private final static String CONTROL_RESPONSIBILITY1 = "Fix Bugs";
    private final static String CONTROL_RESPONSIBILITY2 = "";
    private final static String CONTROL_RESPONSIBILITY3 = null;
    private final static String CONTROL_FIRST_NAME = "Bob";
    private final static String CONTROL_LAST_NAME = "Smith";
    private final static int CONTROL_ID = 10;

    public EmployeeNullInCollectionTestCases(String name) throws Exception {
        super(name);
        
        setControlDocument(XML_RESOURCE);  
        setWriteControlDocument(XML_WRITE_RESOURCE);       
        setControlJSON(JSON_RESOURCE);
        
        Class[] classes = new Class[1];
        classes[0] = Employee_B.class;
        setClasses(classes);
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, Boolean.TRUE);
    }
    
    public void testRoundTrip(){
        
    }

    protected Object getControlObject() {
        ArrayList responsibilities = new ArrayList();
        responsibilities.add(CONTROL_RESPONSIBILITY1);
        responsibilities.add(CONTROL_RESPONSIBILITY2);
        responsibilities.add(CONTROL_RESPONSIBILITY3);

        Employee_B employee = new Employee_B();
        employee.firstName = CONTROL_FIRST_NAME;
        employee.lastName = CONTROL_LAST_NAME;
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2005,04,24,16,06,53);
      
        employee.birthday = cal;
                
        employee.id = CONTROL_ID;
        
        employee.responsibilities = responsibilities;
        
        employee.setBlah("Some String");
        
        JAXBElement<Employee_B> elem = new JAXBElement<Employee_B>(new QName("examplenamespace", "employee-data"), Employee_B.class, employee);
              
        return elem;
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
    
    public Class getUnmarshalClass(){
    	return Employee_B.class;
    }
    /*
    public void test(){
    	super.testJSONUnmarshalFromInputSource()
    	super.testJSONUnmarshalFromInputStream()
    	super.testJSONUnmarshalFromReader()
    	super.testJSONUnmarshalFromURL()
    }
    *//*
    public void testXMLToObjectFromXMLStreamReader() throws Exception {
        if(System.getProperty("java.version").contains("1.6")) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            javax.xml.stream.XMLInputFactory factory = javax.xml.stream.XMLInputFactory.newInstance();
            javax.xml.stream.XMLStreamReader reader = factory.createXMLStreamReader(instream);
            
            Object obj = getJAXBUnmarshaller().unmarshal(reader, Employee_B.class);
            this.xmlToObjectTest(obj);
        }
    }
    //Bug #283424  needs to be fixed then this test can be added 
    
    public void testXMLToObjectFromXMLEventReader() throws Exception {      
        if(System.getProperty("java.version").contains("1.6")) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            javax.xml.stream.XMLInputFactory factory = javax.xml.stream.XMLInputFactory.newInstance();
            javax.xml.stream.XMLEventReader reader = factory.createXMLEventReader(instream);
            
            Object obj = getJAXBUnmarshaller().unmarshal(reader, Employee_B.class);
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
    
    public void testXMLToObjectFromInputStream() throws Exception {
        if(isUnmarshalTest()) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            Object testObject = jaxbUnmarshaller.unmarshal(new StreamSource(instream), Employee_B.class);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }
    
    public void testXMLToObjectFromNode() throws Exception {
        if(isUnmarshalTest()) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);            
            Node node  = parser.parse(instream);
            Object testObject = jaxbUnmarshaller.unmarshal(node, Employee_B.class);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }
  
    
    public void testXMLToObjectFromURL() throws Exception {
        if(isUnmarshalTest()) {
            java.net.URL url = ClassLoader.getSystemResource(resourceName);
            Object testObject = jaxbUnmarshaller.unmarshal(new StreamSource(url.openStream()), Employee_B.class);
            xmlToObjectTest(testObject);
        }
    }
    
    public void testXMLToObjectFromXMLStreamReaderEx() throws Exception {
        if(null != XML_INPUT_FACTORY && isUnmarshalTest()) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);

            ExtendedXMLStreamReaderReader xmlStreamReaderReaderEx = new ExtendedXMLStreamReaderReader();
            XMLStreamReaderInputSource xmlStreamReaderInputSource = new XMLStreamReaderInputSource(xmlStreamReader);
            SAXSource saxSource = new SAXSource(xmlStreamReaderReaderEx, xmlStreamReaderInputSource);

            Object testObject = jaxbUnmarshaller.unmarshal(saxSource, Employee_B.class);
            instream.close();
            xmlToObjectTest(testObject);
        }
    } 
    */
    public void testUnmarshallerHandler() throws Exception {
        //Not Applicable.
    }    
}
