/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-10-02 16:25:00 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.reuse;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentMarshaller;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentUnmarshaller;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.identifiedbyname.withgroupingelement.BinaryDataCollectionWithGroupingElementIdentifiedByNameProject;

public class BinaryDataCollectionReuseTestCases extends XMLMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydatacollection/identifiedbyname/withgroupingelement/BinaryDataCollectionWithGroupElemIdentifiedByNameEmptyNSR.xml";

    public BinaryDataCollectionReuseTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);

        // NSR must be empty
        NamespaceResolver namespaceResolver = new NamespaceResolver();
        Vector namespaces = new Vector();
        namespaceResolver.setNamespaces(namespaces);
        setProject(new BinaryDataCollectionReuseProject(namespaceResolver));
    }

    protected Object getControlObject() {
        return Employee.example1();
    }

    public void setUp() throws Exception {
        super.setUp();
        xmlUnmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());
    }

    
    @Override
    protected XMLMarshaller createMarshaller() {
        XMLMarshaller marshaller = super.createMarshaller();
        marshaller.setAttachmentMarshaller(new MyAttachmentMarshaller());
        return marshaller;
    }

    @Override
    public void testObjectToContentHandler() throws Exception {
        // Override this test method here so that it gets run before testContainerReused.
        // A marshal test must occur before any unmarshall tests, because it sets up
        // the AttachmentMarshaller.
        super.testObjectToContentHandler();
    }

    public void testContainerReused() throws Exception {
        URL url = ClassLoader.getSystemResource(resourceName);
        Employee testObject = (Employee) xmlUnmarshaller.unmarshal(url);

        assertEquals("This mapping's container was not reused.", Stack.class, testObject.getPhotos().getClass());
    }

}