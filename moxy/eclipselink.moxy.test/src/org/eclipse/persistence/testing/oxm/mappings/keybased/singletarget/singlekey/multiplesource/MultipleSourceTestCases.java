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
package org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.multiplesource;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.persistence.testing.oxm.mappings.keybased.KeyBasedMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.Address;
import org.eclipse.persistence.testing.oxm.mappings.keybased.Root;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.Employee;

public class MultipleSourceTestCases extends KeyBasedMappingTestCases {
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/keybased/singletarget/singlekey/multiplesource/instance.xml";
    private final static String CONTROL_ID_2 = "456";
    private final static String CONTROL_NAME_2 = "Bob Jones";
    
	public MultipleSourceTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setProject(new MultipleSourceProject());
	}

	protected Object getControlObject() {
		java.util.Vector addresses = new Vector();
        Vector employees = new Vector();
        
        Address address = new Address();
		address.id = CONTROL_ADD_ID_1;
		address.street = CONTROL_ADD_STREET_1;
		address.city = CONTROL_ADD_CITY_1;
		address.country = CONTROL_ADD_COUNTRY_1;
		address.zip = CONTROL_ADD_ZIP_1;
        addresses.add(address);
        
        Address address2 = new Address();
        address2.id = CONTROL_ADD_ID_2;
        address2.street = CONTROL_ADD_STREET_2;
        address2.city = CONTROL_ADD_CITY_2;
        address2.country = CONTROL_ADD_COUNTRY_2;
        address2.zip = CONTROL_ADD_ZIP_2;
        addresses.add(address2);
        
		Employee employee = new Employee();
		employee.id = CONTROL_ID;
		employee.name = CONTROL_NAME;
		employee.address = address;
        employees.add(employee);
        
		Employee emp2 = new Employee();
        emp2.id = CONTROL_ID_2;
        emp2.name=CONTROL_NAME_2;
        emp2.address = address2;
        employees.add(emp2);
        
        MultipleSourceRoot root = new MultipleSourceRoot();
		root.addresses = addresses;
        root.employees = employees;
        
		return root;
	}

}
