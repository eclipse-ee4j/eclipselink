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
//     Denise Smith - December 15, 2009
package org.eclipse.persistence.testing.oxm.mappings.binarydata.identifiedbyname;

import java.util.Vector;

import javax.activation.DataHandler;

import org.eclipse.persistence.internal.oxm.Namespace;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentMarshaller;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentUnmarshaller;

public class BinaryDataByteObjectArrayTestCases extends XMLWithJSONMappingTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/identifiedbyname/BinaryDataIdentifiedByNameXOPonNSR.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/identifiedbyname/BinaryDataIdentifiedByNameXOPonNSR.json";
    private MyAttachmentUnmarshaller attachmentUnmarshaller;

    public BinaryDataByteObjectArrayTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        NamespaceResolver namespaceResolver = new NamespaceResolver();

        // NSR must be not be empty
        Vector namespaces = new Vector();
        namespaces.add(new Namespace(MyAttachmentUnmarshaller.XOP_NAMESPACE_PREFIX,//
                                     MyAttachmentUnmarshaller.XOP_NAMESPACE_URL));
        namespaceResolver.setNamespaces(namespaces);
        setProject(new BinaryDataByteObjectArrayProject(namespaceResolver));
    }

     protected Object getControlObject() {
            EmployeeWithByteObjectArray emp = EmployeeWithByteObjectArray.example1();
            String s = "THISISATEXTSTRINGFORTHISDATAHANDLER";
            byte[] bytes = s.getBytes();

            Byte[] objectBytes = new Byte[bytes.length];
            for (int index = 0; index < bytes.length; index++) {
                objectBytes[index] = new Byte(bytes[index]);
            }
            emp.setPhoto(objectBytes);
            emp.setData(new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text"));
            return emp;
        }

      public void setUp() throws Exception {
            super.setUp();
            attachmentUnmarshaller = new MyAttachmentUnmarshaller();
            DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text");

            attachmentUnmarshaller.attachments.put(attachmentUnmarshaller.ATTACHMENT_TEST_ID, data);
            xmlUnmarshaller.setAttachmentUnmarshaller(attachmentUnmarshaller);

        }

    @Override
    protected XMLMarshaller createMarshaller() {
        XMLMarshaller marshaller = super.createMarshaller();
        marshaller.setAttachmentMarshaller(new MyAttachmentMarshaller());
        return marshaller;
    }

        public void xmlToObjectTest(Object testObject) throws Exception {
            super.xmlToObjectTest(testObject);

            assertTrue(attachmentUnmarshaller.getAttachmentAsDataHandlerWasCalled());
        }

}
