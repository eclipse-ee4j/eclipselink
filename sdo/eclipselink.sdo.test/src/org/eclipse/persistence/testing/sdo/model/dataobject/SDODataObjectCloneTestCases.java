/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.model.dataobject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.helper.ListWrapper;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class SDODataObjectCloneTestCases extends SDOTestCase {

	private SDODataObject purchaseOrder;
	
	public SDODataObjectCloneTestCases(String name) {
		super(name);
	}
	
	public static void main(String[] args) {
		 String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectCloneTestCases" };
	     TestRunner.main(arguments);
	}
	
	 public void setUp() {
	      super.setUp();	      
	      String xsdString = getXSDString("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeep.xsd");

	        // Generate a list of sdo types based on the purchaseOrder.xsd and print
	        // out info about them
	        List types = xsdHelper.define(xsdString);

	        // create a Purchase Order dataObject and write it to XML called
	        try {
	            FileInputStream inStream = new FileInputStream("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderNSDeep.xml");
	            XMLDocument document = xmlHelper.load(inStream);
	            purchaseOrder = (SDODataObject)document.getRootObject();
	            inStream.close();
	        } catch (IOException e) {
	            fail("An IOException occurred during setup.");
	        }
	 }
	
	 
	 private String getXSDString(String filename) {
	        try {
	            FileInputStream inStream = new FileInputStream(filename);
	            byte[] bytes = new byte[inStream.available()];
	            inStream.read(bytes);
	            return new String(bytes);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	 
	public void testNotSameObject(){
		Object purchaseOrderClone = purchaseOrder.clone();
		assertFalse(purchaseOrder == purchaseOrderClone);
	}
	
	public void testCloneIsDeepCopy() {
		SDODataObject purchaseOrderClone = (SDODataObject)purchaseOrder.clone();
		assertTrue(equalityHelper.equalShallow(purchaseOrder, purchaseOrderClone));
		assertTrue(equalityHelper.equal(purchaseOrder, purchaseOrderClone));
		
		DataObject shipTo = purchaseOrder.getDataObject("shipTo");
		DataObject shipToClone = purchaseOrderClone.getDataObject("shipTo");
		assertFalse(shipTo == shipToClone);
		
		assertEquals(shipTo.get("name"), shipToClone.get("name"));
		
		Type poType =purchaseOrder.getType();
		
		DataObject items = purchaseOrder.getDataObject("items");
		DataObject itemsClone = purchaseOrderClone.getDataObject("items");		
		assertFalse(items == itemsClone);
		
		DataObject item2 = (DataObject)items.getList("item").get(1);
		DataObject item2Clone = (DataObject)itemsClone.getList("item").get(1);
		assertFalse(item2 == item2Clone);
		
	}	

}
