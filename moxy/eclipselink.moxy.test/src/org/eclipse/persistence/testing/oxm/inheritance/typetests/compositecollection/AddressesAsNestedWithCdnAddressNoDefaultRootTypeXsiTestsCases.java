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
package org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection;

import java.util.ArrayList;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.CanadianAddress;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AddressesAsNestedWithCdnAddressNoDefaultRootTypeXsiTestsCases extends XMLMappingTestCases {
    private static final String READ_DOC = "org/eclipse/persistence/testing/oxm/inheritance/typetests/ns_employee_with_addresses_cdnaddressxsi.xml";

    public AddressesAsNestedWithCdnAddressNoDefaultRootTypeXsiTestsCases(String name) throws Exception {
        super(name);
        Project p =new COMCollectionTypeProject(); 
        
        ((XMLDescriptor)p.getDescriptor(CanadianAddress.class)).setDefaultRootElementType(null);
        ((XMLDescriptor)p.getDescriptor(CanadianAddress.class)).setDefaultRootElementField(null);
        ((XMLDescriptor)p.getDescriptor(CanadianAddress.class)).setDefaultRootElement(null);
        
        ((XMLCompositeCollectionMapping)((XMLDescriptor)p.getDescriptor(Employee.class)).getMappingForAttributeName("addresses")).setReferenceClass(null);        
        ((XMLField)((XMLCompositeCollectionMapping)((XMLDescriptor)p.getDescriptor(Employee.class)).getMappingForAttributeName("addresses")).getField()).setLeafElementType(null);
        setProject(p);
        setControlDocument(READ_DOC);
    }

    public Object getControlObject() {
        Employee emp = new Employee();
        ArrayList adds = new ArrayList();
        CanadianAddress add = new CanadianAddress();
        add.setId("123");
        add.setStreet("1 A Street");
        add.setPostalCode("A1B 2C3");
        adds.add(add);
        add = new CanadianAddress();
        add.setId("456");
        add.setStreet("2 A Street");
        add.setPostalCode("A1B 2C3");
        adds.add(add);
        add = new CanadianAddress();
        add.setId("789");
        add.setStreet("3 A Street");
        add.setPostalCode("A1B 2C3");
        adds.add(add);

        emp.setAddresses(adds);
        return emp;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.AddressesAsNestedWithCdnAddressNoDefaultRootTypeXsiTestsCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
