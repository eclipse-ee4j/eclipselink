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
import javax.mail.util.ByteArrayDataSource;

import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentMarshaller;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentUnmarshaller;

public class BinaryDataSelfTestCases extends XMLMappingTestCases{ //XMLWithJSONMappingTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/BinaryDataSelf.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/BinaryDataSelf.json";

    public BinaryDataSelfTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
       // setControlJSON(JSON_RESOURCE);
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

           MyAttachmentUnmarshaller handler = new MyAttachmentUnmarshaller();
        DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text");
        handler.attachments.put(MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID, data);

        MyAttachmentMarshaller marshalHandler = new MyAttachmentMarshaller();
        marshalHandler.attachments.put(MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID, data);

        xmlMarshaller.setAttachmentMarshaller(marshalHandler);
        xmlUnmarshaller.setAttachmentUnmarshaller(handler);
    }
}
