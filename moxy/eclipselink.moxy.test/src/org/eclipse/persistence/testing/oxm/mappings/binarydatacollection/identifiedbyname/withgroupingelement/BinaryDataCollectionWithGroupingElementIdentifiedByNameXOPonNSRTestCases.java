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

import java.util.Vector;

import javax.activation.DataHandler;

import org.eclipse.persistence.internal.descriptors.Namespace;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
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
public class BinaryDataCollectionWithGroupingElementIdentifiedByNameXOPonNSRTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydatacollection/identifiedbyname/withgroupingelement/BinaryDataCollectionWithGroupElemIdentifiedByNameXOPinNSR.xml";

    //private final static String PHOTO_PATH1 = "http://www.example.com/admin/images/ocom/oralogo_small.gif";
    //private final static String PHOTO_PATH2 = "http://www.example.com/admin/images/ocom/oralogo_small.gif";
    //private final static String PHOTO_PATH3 = "http://www.example.com/admin/images/ocom/oralogo_small.gif";
    public BinaryDataCollectionWithGroupingElementIdentifiedByNameXOPonNSRTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        NamespaceResolver namespaceResolver = new NamespaceResolver();
        Vector namespaces = new Vector();
        namespaces.add(new Namespace(MyAttachmentUnmarshaller.XOP_NAMESPACE_PREFIX,//
                                     MyAttachmentUnmarshaller.XOP_NAMESPACE_URL));
        namespaceResolver.setNamespaces(namespaces);
        setProject(new BinaryDataCollectionWithGroupingElementIdentifiedByNameProject(namespaceResolver));
    }

    protected Object getControlObject() {
        Employee employee = null;

        //	  	try {
        Vector photos = new Vector();
        photos.addElement(MyAttachmentUnmarshaller.PHOTO_BASE64.getBytes());
        photos.addElement(MyAttachmentUnmarshaller.PHOTO_BASE64.getBytes());
        photos.addElement(MyAttachmentUnmarshaller.PHOTO_BASE64.getBytes());
        //photos.addElement(Toolkit.getDefaultToolkit().getImage(new URL(PHOTO_PATH1)));
        //photos.addElement(Toolkit.getDefaultToolkit().getImage(new URL(PHOTO_PATH2)));
        //photos.addElement(Toolkit.getDefaultToolkit().getImage(new URL(PHOTO_PATH3)));
        employee = new Employee();
        employee.setID(Employee.DEFAULT_ID);
        employee.setPhotos(photos);

        //	  	} catch (MalformedURLException mue) {
        //	  		System.out.println("BinaryDataCollection: Exception: " + mue.getMessage());
        //	  		mue.printStackTrace();
        //	  	}
        return employee;
    }

    public void setUp() throws Exception {
        super.setUp();
        xmlUnmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());
        
    	MyAttachmentMarshaller.attachments.put(MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID, MyAttachmentUnmarshaller.PHOTO_BASE64.getBytes());
        
    }

    @Override
    protected XMLMarshaller createMarshaller() {
        XMLMarshaller marshaller = super.createMarshaller();
        marshaller.setAttachmentMarshaller(new MyAttachmentMarshaller());
        return marshaller;
    }

}
