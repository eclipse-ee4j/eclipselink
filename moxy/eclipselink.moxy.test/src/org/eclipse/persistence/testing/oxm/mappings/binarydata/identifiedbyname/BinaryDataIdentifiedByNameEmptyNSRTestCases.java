/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

/* $Header: BinaryDataIdentifiedByNameEmptyNSRTestCases.java 20-oct-2006.16:41:05 mfobrien Exp $ */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    mfobrien    10/20/06 - Creation
 */

/**
 *  @version $Header: BinaryDataIdentifiedByNameEmptyNSRTestCases.java 20-oct-2006.16:41:05 mfobrien Exp $
 *  @author  mfobrien
 *  @since  11.1
 */
package org.eclipse.persistence.testing.oxm.mappings.binarydata.identifiedbyname;

import java.util.Vector;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydata.Employee;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentMarshaller;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentUnmarshaller;

/**
 * Not supported
 * base64Binary as attribute - no singlenode\xmlattribute
 */
public class BinaryDataIdentifiedByNameEmptyNSRTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/identifiedbyname/BinaryDataIdentifiedByNameEmptyNSR.xml";

    public BinaryDataIdentifiedByNameEmptyNSRTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        NamespaceResolver namespaceResolver = new NamespaceResolver();

        // NSR must be empty
        Vector namespaces = new Vector();

        //namespaces.add(new Namespace(MyAttachmentUnmarshaller.XOP_NAMESPACE_PREFIX,//
        //		MyAttachmentUnmarshaller.XOP_NAMESPACE_URL));
        namespaceResolver.setNamespaces(namespaces);
        setProject(new BinaryDataIdentifiedByNameProject(namespaceResolver));
    }

    protected Object getControlObject() {
        return Employee.example1();
    }

    public void setUp() throws Exception {
        super.setUp();
        xmlUnmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());
        
        byte[] bytes = MyAttachmentUnmarshaller.PHOTO_BASE64.getBytes();
    	MyAttachmentMarshaller.attachments.put(MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID, bytes);        
    }

    @Override
    protected XMLMarshaller createMarshaller() {
        XMLMarshaller marshaller = super.createMarshaller();
        marshaller.setAttachmentMarshaller(new MyAttachmentMarshaller());
        return marshaller;
    }

}