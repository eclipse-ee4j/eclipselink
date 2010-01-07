/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - December 15, 2009
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.identifiedbyname.withgroupingelement;

import java.util.Vector;

import org.eclipse.persistence.internal.descriptors.Namespace;
import org.eclipse.persistence.internal.oxm.conversion.Base64;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.EmployeeWithByteArrayObject;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentMarshaller;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentUnmarshaller;

public class BinaryDataCollectionByteObjectArrayTestCases extends XMLMappingTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydatacollection/identifiedbyname/withgroupingelement/BinaryDataCollectionForcedInline.xml";

	public BinaryDataCollectionByteObjectArrayTestCases(String name) throws Exception {
		super(name);
		 setControlDocument(XML_RESOURCE);
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
}
