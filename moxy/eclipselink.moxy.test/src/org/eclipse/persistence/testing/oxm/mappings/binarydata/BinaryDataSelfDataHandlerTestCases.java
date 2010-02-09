/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - June 24/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.binarydata;

import javax.activation.DataHandler;

import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentMarshaller;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentUnmarshaller;
import org.w3c.dom.Document;

public class BinaryDataSelfDataHandlerTestCases extends XMLMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/BinaryDataSelfDataHandler.xml";
    private MyAttachmentMarshaller attachmentMarshaller;
    public BinaryDataSelfDataHandlerTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Project p = new BinaryDataSelfProject();
        p.getDescriptor(Employee.class).getMappingForAttributeName("photo").setIsReadOnly(true);
        
        ((XMLBinaryDataMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("data")).setShouldInlineBinaryData(false);
        ((XMLBinaryDataMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("photo")).setShouldInlineBinaryData(false);
        
        
        setProject(p);
    }
    
    public void setUp() throws Exception {
    	super.setUp();
    	
    	this.attachmentMarshaller = new MyAttachmentMarshaller();
    	xmlMarshaller.setAttachmentMarshaller(this.attachmentMarshaller);
    	xmlUnmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());
    	
    	DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text");    	
    	MyAttachmentMarshaller.attachments.put(MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID, data);
    	
    }

    protected Object getControlObject() {
        Employee emp = new Employee(123);
        
        emp.setData(new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text"));        
        return emp;
    }
    
    public Object getReadControlObject() {
        Employee emp = new Employee(123);
        
        String s = "THISISATEXTSTRINGFORTHISDATAHANDLER";
       
        emp.setData(new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text"));
        emp.setPhoto(s.getBytes());
        
        return emp;
    }
    
    public void objectToXMLDocumentTest(Document testDocument) throws Exception {
        super.objectToXMLDocumentTest(testDocument);
        assertNotNull(this.attachmentMarshaller.getLocalName());
    }
    
    
}