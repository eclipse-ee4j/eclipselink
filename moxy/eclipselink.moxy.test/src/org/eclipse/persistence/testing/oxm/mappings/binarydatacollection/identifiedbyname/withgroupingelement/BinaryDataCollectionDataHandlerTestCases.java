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
 *     Denise Smith - January 6th, 2010 - 2.0.1
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.identifiedbyname.withgroupingelement;

import java.util.Vector;

import javax.activation.DataHandler;

import org.eclipse.persistence.internal.descriptors.Namespace;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.EmployeeWithByteArrayObject;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentMarshallerDataHandler;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentUnmarshaller;

public class BinaryDataCollectionDataHandlerTestCases extends XMLWithJSONMappingTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydatacollection/identifiedbyname/withgroupingelement/BinaryDataCollectionDataHandler.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydatacollection/identifiedbyname/withgroupingelement/BinaryDataCollectionDataHandler.json";

	public BinaryDataCollectionDataHandlerTestCases(String name) throws Exception {
		super(name);
		 setControlDocument(XML_RESOURCE);
		 setControlJSON(JSON_RESOURCE);
	        NamespaceResolver namespaceResolver = new NamespaceResolver();
	        Vector namespaces = new Vector();
	        namespaces.add(new Namespace(MyAttachmentUnmarshaller.XOP_NAMESPACE_PREFIX,//
	                                     MyAttachmentUnmarshaller.XOP_NAMESPACE_URL));
	        namespaceResolver.setNamespaces(namespaces);
	        setProject(new BinaryDataCollectionDataHandlerProject(namespaceResolver));
	    }

	    protected Object getControlObject() {
	    	EmployeeWithByteArrayObject employee = new EmployeeWithByteArrayObject();
	   
	        Vector photos = new Vector();

	        DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text");
	        DataHandler data2 = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text");
	        
	        photos.add(data);
	        photos.add(data2);
	        
	        employee.setID(EmployeeWithByteArrayObject.DEFAULT_ID);
	        employee.setPhotos(photos);

	        return employee;
	    }

	    public void setUp() throws Exception {
	        super.setUp();
	        xmlUnmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());
	        
	        DataHandler data = new DataHandler("THISISATEXTSTRINGFORTHISDATAHANDLER", "text");    	
	        MyAttachmentMarshallerDataHandler.attachments.put(MyAttachmentUnmarshaller.ATTACHMENT_TEST_ID, data);
	    	
	    }

        @Override
        protected XMLMarshaller createMarshaller() {
            XMLMarshaller marshaller = super.createMarshaller();
            marshaller.setAttachmentMarshaller(new MyAttachmentMarshallerDataHandler());
            return marshaller;
        }

}
