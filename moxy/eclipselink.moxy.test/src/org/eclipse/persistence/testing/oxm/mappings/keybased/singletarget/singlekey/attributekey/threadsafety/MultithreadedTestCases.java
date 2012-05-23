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
* mmacivor - January 09, 2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.attributekey.threadsafety;

import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.attributekey.SingleAttributeKeyProject;

public class MultithreadedTestCases extends OXTestCase {
	public static String controlDoc1 = "<root>" +
	"<employee id=\"222\" address-id=\"99\">" +
	"  <name>Joe Smith</name>" +
	"</employee>" +
	"<address aid=\"99\">" +
	"  <street>Some St.</street>" +
	"  <city>Anytown</city>" +
	"  <country>Canada</country>" +
	"  <zip>X0X0X0</zip>" +
	"</address>" +
	"</root>";
	
	public static String controlDoc2 = "<root>" +
	"<employee id=\"222\" address-id=\"99\">" +
	"  <name>Joe Smith</name>" +
	"</employee>" +
	"<address aid=\"99\">" +
	"  <street>Blah Street</street>" +
	"  <city>Anytown</city>" +
	"  <country>Canada</country>" +
	"  <zip>X0X0X0</zip>" +
	"</address>" +
	"</root>";
	
	public static String controlStreet1 = "Some St.";
	public static String controlStreet2 = "Blah Street";
	
	public MultithreadedTestCases(String name) {
		super(name);
	}
	
	public void testMultithreaded() throws Exception {
		XMLContext context = new XMLContext(new SingleAttributeKeyProject());
		ReferenceUnmarshaller unm1 = new ReferenceUnmarshaller(context.createUnmarshaller(), controlDoc1, controlStreet1);
		ReferenceUnmarshaller unm2 = new ReferenceUnmarshaller(context.createUnmarshaller(), controlDoc2, controlStreet2);
		
		Thread thread1 = new Thread(unm1);
		Thread thread2 = new Thread(unm2);
		
		thread1.start();
		thread2.start();
		thread1.join();
		thread2.join();
	}
}
