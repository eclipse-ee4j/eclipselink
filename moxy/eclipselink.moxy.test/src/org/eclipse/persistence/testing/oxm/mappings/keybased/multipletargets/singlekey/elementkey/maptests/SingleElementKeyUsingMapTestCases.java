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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.elementkey.maptests;

import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.testing.oxm.mappings.keybased.KeyBasedMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.Address;
import org.eclipse.persistence.testing.oxm.mappings.keybased.Root;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class SingleElementKeyUsingMapTestCases extends KeyBasedMappingTestCases {
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/keybased/multipletargets/singlekey/elementkey/instance.xml";
	private Root controlObj;
	private Root writeControlObj;

	public SingleElementKeyUsingMapTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setProject(new SingleElementKeyUsingMapProject());
		createControlObject();
		createWriteControlObject();
	}

	private void createControlObject() {
		HashMap addresses = new HashMap();

		Address address = new Address();
		address.id = CONTROL_ADD_ID_1;
		address.street = CONTROL_ADD_STREET_1;
		address.city = CONTROL_ADD_CITY_1;
		address.country = CONTROL_ADD_COUNTRY_1;
		address.zip = CONTROL_ADD_ZIP_1;
		addresses.put(address.getKey(), address);

		address = new Address();
		address.id = CONTROL_ADD_ID_2;
		address.street = CONTROL_ADD_STREET_2;
		address.city = CONTROL_ADD_CITY_2;
		address.country = CONTROL_ADD_COUNTRY_2;
		address.zip = CONTROL_ADD_ZIP_2;
		addresses.put(address.getKey(), address);

		address = new Address();
		address.id = CONTROL_ADD_ID_3;
		address.street = CONTROL_ADD_STREET_3;
		address.city = CONTROL_ADD_CITY_3;
		address.country = CONTROL_ADD_COUNTRY_3;
		address.zip = CONTROL_ADD_ZIP_3;
		addresses.put(address.getKey(), address);

		Employee employee = new Employee();
		employee.id = CONTROL_ID;
		employee.name = CONTROL_NAME;
		employee.addresses = addresses;

		controlObj = new Root();
		controlObj.employee = employee;
	}
	
	public void createWriteControlObject() {
		ArrayList rootAddresses = new ArrayList();
		HashMap empAddresses = new HashMap();

		Address address = new Address();
		address.id = CONTROL_ADD_ID_1;
		address.street = CONTROL_ADD_STREET_1;
		address.city = CONTROL_ADD_CITY_1;
		address.country = CONTROL_ADD_COUNTRY_1;
		address.zip = CONTROL_ADD_ZIP_1;
		empAddresses.put(address.getKey(), address);
		rootAddresses.add(address);

		address = new Address();
		address.id = CONTROL_ADD_ID_2;
		address.street = CONTROL_ADD_STREET_2;
		address.city = CONTROL_ADD_CITY_2;
		address.country = CONTROL_ADD_COUNTRY_2;
		address.zip = CONTROL_ADD_ZIP_2;
		empAddresses.put(address.getKey(), address);
		rootAddresses.add(address);

		address = new Address();
		address.id = CONTROL_ADD_ID_3;
		address.street = CONTROL_ADD_STREET_3;
		address.city = CONTROL_ADD_CITY_3;
		address.country = CONTROL_ADD_COUNTRY_3;
		address.zip = CONTROL_ADD_ZIP_3;
		empAddresses.put(address.getKey(), address);
		rootAddresses.add(address);

		address = new Address();
		address.id = CONTROL_ADD_ID_4;
		address.street = CONTROL_ADD_STREET_4;
		address.city = CONTROL_ADD_CITY_4;
		address.country = CONTROL_ADD_COUNTRY_4;
		address.zip = CONTROL_ADD_ZIP_4;
		rootAddresses.add(address);
		
		Employee employee = new Employee();
		employee.id = CONTROL_ID;
		employee.name = CONTROL_NAME;
		employee.addresses = empAddresses;

		writeControlObj = new Root();
		writeControlObj.employee = employee;
		writeControlObj.addresses = rootAddresses;
	}

	public Object getControlObject() {
		return controlObj;
	}

	public Object getWriteControlObject() {
		return writeControlObj;
	}
	
    public void objectToXMLDocumentTest(Document testDocument) throws Exception {
        log("**objectToXMLDocumentTest**");
        log("Expected:");
        log(getWriteControlDocument());
        log("\nActual:");
        log(testDocument);

        // for the purpose of these tests, order is not important
        // - a successful test will result in the same number of
        // address-id elements being written out
        org.w3c.dom.NodeList controlIds = getWriteControlDocument().getElementsByTagName("address-id");
        org.w3c.dom.NodeList testIds = testDocument.getElementsByTagName("address-id");
        assertTrue(controlIds.getLength() == testIds.getLength());
    }

    public void testObjectToContentHandler() throws Exception {
        SAXDocumentBuilder builder = new SAXDocumentBuilder();
        xmlMarshaller.marshal(getWriteControlObject(), builder);

        Document controlDocument = getWriteControlDocument();
        Document testDocument = builder.getDocument();

        log("**testObjectToContentHandler**");
        log("Expected:");
        log(controlDocument);
        log("\nActual:");
        log(testDocument);
        
        // for the purpose of these tests, order is not important
        // - a successful test will result in the same number of
        // address-id elements being written out
        NodeList controlIds = controlDocument.getElementsByTagName("address-id");
        NodeList testIds = testDocument.getElementsByTagName("address-id");
        assertTrue(controlIds.getLength() == testIds.getLength());
    }
}
