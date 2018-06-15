/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - January 18/2010 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.binarydata;


import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentMarshaller;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentUnmarshaller;
import org.w3c.dom.Document;

public class BinaryDataCompositeSelfNillableTestCases extends XMLWithJSONMappingTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/BinaryDataCompositeSelfNillable.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/BinaryDataCompositeSelfNillable.json";
    private MyAttachmentMarshaller attachmentMarshaller;

    public BinaryDataCompositeSelfNillableTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Project p = new BinaryDataCompositeSelfProject();
        ((XMLCompositeObjectMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("myImage")).getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
        ((XMLCompositeObjectMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("myImage")).getNullPolicy().setNullRepresentedByXsiNil(true);
        setProject(p);
    }

    public void setUp() throws Exception {
        super.setUp();
        MyAttachmentUnmarshaller handler = new MyAttachmentUnmarshaller();
        byte[] bytes = new byte[] {1, 2, 3, 4, 5, 6};
        handler.attachments.put(MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID, bytes);
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
        emp.setMyImage(null);
        return emp;
    }

    public void objectToXMLDocumentTest(Document testDocument) throws Exception {
        super.objectToXMLDocumentTest(testDocument);
        assertNull(this.attachmentMarshaller.getLocalName());
    }
}
