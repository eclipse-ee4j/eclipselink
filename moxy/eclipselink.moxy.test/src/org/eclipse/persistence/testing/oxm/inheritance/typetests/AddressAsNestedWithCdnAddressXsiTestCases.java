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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.inheritance.typetests;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AddressAsNestedWithCdnAddressXsiTestCases extends XMLMappingTestCases {
    private static final String READ_DOC = "org/eclipse/persistence/testing/oxm/inheritance/typetests/employee_with_address_cdnaddressxsi.xml";
    
    public AddressAsNestedWithCdnAddressXsiTestCases(String name) throws Exception {
        super(name);
        setProject(new TypeProject());
        setControlDocument(READ_DOC);
    }

    public Object getControlObject() {
		Employee emp = new Employee();
		CanadianAddress add = new CanadianAddress();
		add.setId("123");
		add.setStreet("1 A Street");
		add.setPostalCode("A1B 2C3");
		emp.setAddress(add);
        return emp;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.typetests.AddressAsNestedWithCdnAddressXsiTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
