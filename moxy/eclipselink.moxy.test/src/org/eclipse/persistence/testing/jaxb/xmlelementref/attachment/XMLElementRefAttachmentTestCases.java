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

import java.util.ArrayList;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XMLElementRefAttachmentTestCases extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/attachment/employee.xml";

    public XMLElementRefAttachmentTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        
        Class[] classes = new Class[2];
        classes[0] = ObjectFactory.class;
        classes[1] = Employee.class;
        setClasses(classes);
    }

    public void setUp() throws Exception{
        super.setUp();          
        jaxbMarshaller.setAttachmentMarshaller(new AttachmentMarshaller() {
            
            @Override
            public String addSwaRefAttachment(DataHandler arg0) {
                return "cid1234567";
            }
            
            @Override
            public String addMtomAttachment(byte[] arg0, int arg1, int arg2, String arg3, String arg4, String arg5) {
                return "cid1234567";
            }
            
            @Override
            public String addMtomAttachment(DataHandler arg0, String arg1, String arg2) {
                return "cid1234567";
            }
            
            @Override
            public boolean isXOPPackage() {
                return true;
            }
        });
        jaxbUnmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshaller() {
            
            @Override
            public DataHandler getAttachmentAsDataHandler(String arg0) {
                return null;
            }
            
            @Override
            public byte[] getAttachmentAsByteArray(String arg0) {
                // TODO Auto-generated method stub
                return new byte[] {1, 2, 3, 4, 5};
            }
            
            @Override
            public boolean isXOPPackage() {
                return true;
            }
        });
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
    

}
