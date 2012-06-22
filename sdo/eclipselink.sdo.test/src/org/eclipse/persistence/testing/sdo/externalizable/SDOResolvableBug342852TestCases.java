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
 *     Denise Smith - 2.3
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.externalizable;

import java.util.List;

import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

public class SDOResolvableBug342852TestCases extends SDOResolvableTestCases {

    public final String SERIALIZATION_FILE_NAME = tempFileDir + "/serialization.bin";
	
	public SDOResolvableBug342852TestCases(String name) {
		super(name);
	}
	
	public void testSerializeWithChangeSummary(){
		 String xsd = "org/eclipse/persistence/testing/sdo/externalizable/POrder.xsd";

         // Define Types so that processing attributes completes
         List types = xsdHelper.define(getSchema(xsd));
         serialize(getControlObject(), SERIALIZATION_FILE_NAME);
	}
	
	 private DataObject getControlObject(){
		 
		 Type addressType = aHelperContext.getTypeHelper().getType("http://www.example.org", "AddressType");
		
		 DataObject addrDataObject = aHelperContext.getDataFactory().create(addressType);
		 addrDataObject.set("street", "myStreet2");
		 		
		 Type porderType = aHelperContext.getTypeHelper().getType("http://www.example.org", "PurchaseOrderType");
		 DataObject theDataObject = aHelperContext.getDataFactory().create(porderType);
		 theDataObject.set("comment", "That is all.");
	
		 theDataObject.set("billTo", addrDataObject);
		          
		 return theDataObject;
	 }

}
