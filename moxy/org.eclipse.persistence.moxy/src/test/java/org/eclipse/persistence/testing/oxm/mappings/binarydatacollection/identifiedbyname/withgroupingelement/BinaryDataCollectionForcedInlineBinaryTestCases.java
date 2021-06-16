/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     mmacivor - July 16/2009 - 2.0 - Initial implementation

/* $Header: BinaryDataCollectionWithGroupingElementIdentifiedByNameXOPonNSRTestCases.java 19-oct-2006.17:10:30 mfobrien Exp $ */

/**
 *  @version $Header: BinaryDataCollectionWithGroupingElementIdentifiedByNameXOPonNSRTestCases.java 19-oct-2006.17:10:30 mfobrien Exp $
 *  @author  mfobrien
 *  @since   11.1
 */
package org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.identifiedbyname.withgroupingelement;

import java.util.Vector;

import org.eclipse.persistence.internal.oxm.Namespace;
import org.eclipse.persistence.internal.oxm.conversion.Base64;
import org.eclipse.persistence.oxm.NamespaceResolver;
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
public class BinaryDataCollectionForcedInlineBinaryTestCases extends XMLWithJSONMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydatacollection/identifiedbyname/withgroupingelement/BinaryDataCollectionForcedInline.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydatacollection/identifiedbyname/withgroupingelement/BinaryDataCollectionForcedInline.json";

    //private final static String PHOTO_PATH1 = "http://www.example.com/admin/images/ocom/oralogo_small.gif";
    //private final static String PHOTO_PATH2 = "http://www.example.com/admin/images/ocom/oralogo_small.gif";
    //private final static String PHOTO_PATH3 = "http://www.example.com/admin/images/ocom/oralogo_small.gif";
    public BinaryDataCollectionForcedInlineBinaryTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        NamespaceResolver namespaceResolver = new NamespaceResolver();
        Vector namespaces = new Vector();
        namespaces.add(new Namespace(MyAttachmentUnmarshaller.XOP_NAMESPACE_PREFIX,//
                                     MyAttachmentUnmarshaller.XOP_NAMESPACE_URL));
        namespaceResolver.setNamespaces(namespaces);
        setProject(new BinaryDataCollectionWithGroupingElementIdentifiedByNameProject(namespaceResolver));
    }

    protected Object getControlObject() {
        Employee employee = null;

        //      try {
        Vector photos = new Vector();
        String base64 = MyAttachmentUnmarshaller.PHOTO_BASE64;
        byte[] bytes = Base64.base64Decode(base64.getBytes());
        photos.addElement(bytes);
        photos.addElement(bytes);
        employee = new Employee();
        employee.setID(Employee.DEFAULT_ID);
        employee.setPhotos(photos);

        //      } catch (MalformedURLException mue) {
        //          System.out.println("BinaryDataCollection: Exception: " + mue.getMessage());
        //          mue.printStackTrace();
        //      }
        return employee;
    }

    public void setUp() throws Exception {
        super.setUp();
        MyAttachmentMarshaller marshaller = new MyAttachmentMarshaller();
        marshaller.setReturnNull(true);
        xmlMarshaller.setAttachmentMarshaller(marshaller);
        xmlUnmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());
    }
}
