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
 * Oracle = 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref.attachment;

import java.io.InputStream;
import java.util.ArrayList;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XMLElementRefTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/attachment/employee.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/attachment/employee.json";
    private final static String JSON_SCHEMA_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/attachment/employeeschema.json";

    public XMLElementRefTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE); 
        Class[] classes = new Class[2];
        classes[0] = ObjectFactory.class;
        classes[1] = Employee.class;
        setClasses(classes);
    }
  
    @Override
    protected Object getControlObject() {
        ObjectFactory factory = new ObjectFactory();
        Employee employee = new Employee();
        
        employee.ref1 = factory.createFooA();        
        
        employee.ref2 = new ArrayList<JAXBElement>();
        employee.ref2.add(factory.createFooC());
        employee.ref2.add(factory.createFooB());
        employee.ref2.add(factory.createFooB());
        employee.ref2.add(factory.createFooC());
        return employee;
    }
    
    @Override
    protected Object getJSONReadControlObject(){    
        ObjectFactory factory = new ObjectFactory();
        Employee employee = new Employee();
        
        employee.ref1 = factory.createFooA();        
        
        employee.ref2 = new ArrayList<JAXBElement>();
        employee.ref2.add(factory.createFooC());
        employee.ref2.add(factory.createFooC());
        employee.ref2.add(factory.createFooB());
        employee.ref2.add(factory.createFooB());        
        return employee;
    }
    
    public void testJSONSchemaGen() throws Exception{
        InputStream controlSchema = classLoader.getResourceAsStream(JSON_SCHEMA_RESOURCE);
        super.generateJSONSchema(controlSchema);
    }

  
}
