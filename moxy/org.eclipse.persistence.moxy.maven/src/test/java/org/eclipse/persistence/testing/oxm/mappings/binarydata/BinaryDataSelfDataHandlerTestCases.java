/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Denise Smith - June 24/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.binarydata;

import javax.activation.DataHandler;

import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentMarshaller;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentUnmarshaller;
import org.w3c.dom.Document;

public class BinaryDataSelfDataHandlerTestCases extends XMLMappingTestCases{// XMLWithJSONMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/BinaryDataSelfDataHandler.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/BinaryDataSelfDataHandler.json";
    private MyAttachmentMarshaller attachmentMarshaller;
    public BinaryDataSelfDataHandlerTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
       // setControlJSON(JSON_RESOURCE);
        Project p = new BinaryDataSelfProject();
        p.getDescriptor(Employee.class).getMappingForAttributeName("photo").setIsReadOnly(true);

        ((XMLBinaryDataMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("data")).setShouldInlineBinaryData(false);
        ((XMLBinaryDataMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("photo")).setShouldInlineBinaryData(false);


        setProject(p);
    }

    public void setUp() throws Exception {
        super.setUp();
        MyAttachmentUnmarshaller handler = new MyAttachmentUnmarshaller();
        DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text");
        handler.attachments.put(MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID, data);
        xmlUnmarshaller.setAttachmentUnmarshaller(handler);
    }

    @Override
    protected XMLMarshaller createMarshaller() {
        XMLMarshaller marshaller = super.createMarshaller();
        this.attachmentMarshaller = new MyAttachmentMarshaller();
        marshaller.setAttachmentMarshaller(this.attachmentMarshaller);
        return marshaller;
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
