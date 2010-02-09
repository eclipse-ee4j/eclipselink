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
import javax.mail.util.ByteArrayDataSource;

import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentMarshaller;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentUnmarshaller;

public class BinaryDataSelfTestCases extends XMLMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/BinaryDataSelf.xml";

    public BinaryDataSelfTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Project p = new BinaryDataSelfProject();
        p.getDescriptor(Employee.class).getMappingForAttributeName("data").setIsReadOnly(true);
        ((XMLBinaryDataMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("data")).setShouldInlineBinaryData(false);
        ((XMLBinaryDataMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("photo")).setShouldInlineBinaryData(true);
        setProject(p);
    }

    protected Object getControlObject() {
        Employee emp = new Employee(123);        
        emp.setPhoto(new byte[] { 0, 1, 2, 3});
        return emp;
    }
 
    public Object getReadControlObject() {
        Employee emp = new Employee(123); 
        byte[] bytes = new byte[] { 0, 1, 2, 3};
        emp.setPhoto(bytes);
 
        ByteArrayDataSource ds = new ByteArrayDataSource(bytes, "application/octet-stream");
        DataHandler dh = new DataHandler(ds);
        
        emp.setData(dh);
        return emp;
    }
    
    public void setUp() throws Exception {
    	super.setUp();
    	
    	xmlMarshaller.setAttachmentMarshaller(new MyAttachmentMarshaller());
    	xmlUnmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());
    	
    	DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text");    	
    	MyAttachmentMarshaller.attachments.put(MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID, data);
    }
}