/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

/* $Header: BinaryDataIdentifiedByNameXOPonNSRTestCases.java 20-oct-2006.16:41:42 mfobrien Exp $ */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    mfobrien    10/20/06 - Creation
 */

/**
 *  @version $Header: BinaryDataIdentifiedByNameXOPonNSRTestCases.java 20-oct-2006.16:41:42 mfobrien Exp $
 *  @author  mfobrien
 *  @since   11.1
 */
package org.eclipse.persistence.testing.oxm.mappings.binarydata.identifiedbyname;

import java.util.Vector;
import org.eclipse.persistence.internal.descriptors.Namespace;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydata.Employee;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentMarshaller;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentUnmarshaller;

/**
 * Not supported
 * base64Binary as attribute - no singlenode\xmlattribute
 */
public class BinaryDataIdentifiedByNameXOPonNSRTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/identifiedbyname/BinaryDataIdentifiedByNameXOPonNSR.xml";

    public BinaryDataIdentifiedByNameXOPonNSRTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        NamespaceResolver namespaceResolver = new NamespaceResolver();

        // NSR must be not be empty
        Vector namespaces = new Vector();
        namespaces.add(new Namespace(MyAttachmentUnmarshaller.XOP_NAMESPACE_PREFIX,//
                                     MyAttachmentUnmarshaller.XOP_NAMESPACE_URL));
        namespaceResolver.setNamespaces(namespaces);
        setProject(new BinaryDataIdentifiedByNameProject(namespaceResolver));
    }

    protected Object getControlObject() {
        return Employee.example1();
    }

    protected void setSession(String sessionName) {
        super.setSession(sessionName);
        // setup attachment Marshaller/Unmarshaller	  
        xmlMarshaller.setAttachmentMarshaller(new MyAttachmentMarshaller());
        xmlUnmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());
    }

    protected void setProject(Project project) {
        super.setProject(project);
        // setup attachment Marshaller/Unmarshaller
        xmlMarshaller.setAttachmentMarshaller(new MyAttachmentMarshaller());
        xmlUnmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());
    }
}