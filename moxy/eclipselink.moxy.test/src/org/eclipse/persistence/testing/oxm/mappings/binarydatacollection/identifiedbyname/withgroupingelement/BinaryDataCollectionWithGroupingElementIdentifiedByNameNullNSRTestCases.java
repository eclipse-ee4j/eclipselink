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

/* $Header: BinaryDataCollectionWithGroupingElementIdentifiedByNameXOPonNSRTestCases.java 19-oct-2006.17:10:30 mfobrien Exp $ */

/**
 *  @version $Header: BinaryDataCollectionWithGroupingElementIdentifiedByNameXOPonNSRTestCases.java 19-oct-2006.17:10:30 mfobrien Exp $
 *  @author  mfobrien
 *  @since   11.1
 */
package org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.identifiedbyname.withgroupingelement;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLMarshaller;

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
public class BinaryDataCollectionWithGroupingElementIdentifiedByNameNullNSRTestCases extends XMLWithJSONMappingTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydatacollection/identifiedbyname/withgroupingelement/BinaryDataCollectionWithGroupElemIdentifiedByNameNullNSR.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydatacollection/identifiedbyname/withgroupingelement/BinaryDataCollectionWithGroupElemIdentifiedByNameNullNSR.json";

    public BinaryDataCollectionWithGroupingElementIdentifiedByNameNullNSRTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        // NSR must be null
        NamespaceResolver namespaceResolver = null;//new NamespaceResolver();

        setProject(new BinaryDataCollectionWithGroupingElementIdentifiedByNameProject(namespaceResolver));
    }

    protected Object getControlObject() {
        return Employee.example1();
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
