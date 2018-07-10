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
package org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.identifiedbyname.withgroupingelement;

import java.util.Vector;

import org.eclipse.persistence.internal.oxm.Namespace;
import org.eclipse.persistence.internal.oxm.conversion.Base64;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.EmployeeWithByteArrayObject;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentMarshaller;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentUnmarshaller;

public class BinaryDataCollectionByteObjectArrayTestCases extends XMLWithJSONMappingTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydatacollection/identifiedbyname/withgroupingelement/BinaryDataCollectionForcedInline.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydatacollection/identifiedbyname/withgroupingelement/BinaryDataCollectionForcedInline.json";

    public BinaryDataCollectionByteObjectArrayTestCases(String name) throws Exception {
        super(name);
         setControlDocument(XML_RESOURCE);
         setControlJSON(JSON_RESOURCE);
            NamespaceResolver namespaceResolver = new NamespaceResolver();
            Vector namespaces = new Vector();
            namespaces.add(new Namespace(MyAttachmentUnmarshaller.XOP_NAMESPACE_PREFIX,//
                                         MyAttachmentUnmarshaller.XOP_NAMESPACE_URL));
            namespaceResolver.setNamespaces(namespaces);
            setProject(new BinaryDataCollectionByteObjectArrayProject(namespaceResolver));
        }

        protected Object getControlObject() {
            EmployeeWithByteArrayObject employee = null;


            Vector photos = new Vector();
            String base64 = MyAttachmentUnmarshaller.PHOTO_BASE64;
            byte[] bytes = Base64.base64Decode(base64.getBytes());

            Byte[] objectBytes = new Byte[bytes.length];
            for (int index = 0; index < bytes.length; index++) {
                objectBytes[index] = new Byte(bytes[index]);
            }

            photos.addElement(objectBytes);
            photos.addElement(objectBytes);
            employee = new EmployeeWithByteArrayObject();
            employee.setID(EmployeeWithByteArrayObject.DEFAULT_ID);
            employee.setPhotos(photos);

            return employee;
        }

        public void setUp() throws Exception {
            super.setUp();
            MyAttachmentMarshaller marshaller = new MyAttachmentMarshaller();
            marshaller.setReturnNull(true);
            xmlMarshaller.setAttachmentMarshaller(marshaller);
            xmlUnmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());
        }

    //@Override
    //public void testUnmarshallerHandler() throws Exception {
    //}

}
