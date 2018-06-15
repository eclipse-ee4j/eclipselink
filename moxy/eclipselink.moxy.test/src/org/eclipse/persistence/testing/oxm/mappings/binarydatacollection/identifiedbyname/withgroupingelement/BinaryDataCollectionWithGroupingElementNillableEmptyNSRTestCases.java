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
//     Oracle - initial API and implementation from Oracle TopLink

/* $Header: BinaryDataCollectionWithGroupingElementIdentifiedByNameXOPonNSRTestCases.java 19-oct-2006.17:10:30 mfobrien Exp $ */

/**
 *  @version $Header: BinaryDataCollectionWithGroupingElementIdentifiedByNameXOPonNSRTestCases.java 19-oct-2006.17:10:30 mfobrien Exp $
 *  @author  mfobrien
 *  @since   11.1
 */
package org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.identifiedbyname.withgroupingelement;

import java.util.Vector;


import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.Employee;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentMarshaller;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentUnmarshaller;

/**
 * Not supported
 * base64Binary as attribute - no singlenode\xmlattribute
 *
 * @author mfobrien
 *
 */
public class BinaryDataCollectionWithGroupingElementNillableEmptyNSRTestCases extends XMLWithJSONMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydatacollection/identifiedbyname/withgroupingelement/BinaryDataCollectionWithGroupElemNillableEmptyNSR.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydatacollection/identifiedbyname/withgroupingelement/BinaryDataCollectionWithGroupElemNillableEmptyNSR.json";

    public BinaryDataCollectionWithGroupingElementNillableEmptyNSRTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        NamespaceResolver namespaceResolver = new NamespaceResolver();

        // NSR must be empty
        Vector namespaces = new Vector();

        //namespaces.add(new Namespace(MyAttachmentUnmarshaller.XOP_NAMESPACE_PREFIX,//
        //        MyAttachmentUnmarshaller.XOP_NAMESPACE_URL));
        namespaceResolver.setNamespaces(namespaces);
        Project p = new BinaryDataCollectionWithGroupingElementIdentifiedByNameProject(namespaceResolver);
        ((XMLBinaryDataCollectionMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("photos")).getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
        ((XMLBinaryDataCollectionMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("photos")).getNullPolicy().setNullRepresentedByXsiNil(true);
        setProject(p);
    }

    protected Object getControlObject() {
        Vector photos = new Vector();
        photos.addElement(MyAttachmentUnmarshaller.PHOTO_BASE64.getBytes());
        photos.addElement(null);
        photos.addElement(MyAttachmentUnmarshaller.PHOTO_BASE64.getBytes());
        return new Employee(123, photos);
    }

    public void setUp() throws Exception {
        super.setUp();

        MyAttachmentUnmarshaller handler = new MyAttachmentUnmarshaller();

        handler.attachments.put(MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID,MyAttachmentUnmarshaller.PHOTO_BASE64.getBytes());
        xmlUnmarshaller.setAttachmentUnmarshaller(handler);
    }

    @Override
    protected XMLMarshaller createMarshaller() {
        XMLMarshaller marshaller = super.createMarshaller();
        marshaller.setAttachmentMarshaller(new MyAttachmentMarshaller());
        return marshaller;
    }

}
