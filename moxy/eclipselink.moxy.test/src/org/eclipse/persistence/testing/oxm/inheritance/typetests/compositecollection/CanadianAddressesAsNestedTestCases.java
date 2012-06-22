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

import org.eclipse.persistence.testing.oxm.inheritance.typetests.CanadianAddress;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class CanadianAddressesAsNestedTestCases extends XMLMappingTestCases {
    private static final String READ_DOC = "org/eclipse/persistence/testing/oxm/inheritance/typetests/dependant_with_cdnaddresses_noxsi.xml";
    
    public CanadianAddressesAsNestedTestCases(String name) throws Exception {
        super(name);
        setProject(new COMCollectionTypeProject());
        setControlDocument(READ_DOC);
    }

    public Object getControlObject() {
        Dependant dep = new Dependant();
        ArrayList adds = new ArrayList();
        CanadianAddress cadd = new CanadianAddress();
        cadd.setId("123");
		cadd.setStreet("1 A Street");
		cadd.setPostalCode("A1B 2C3");
        adds.add(cadd);
        cadd = new CanadianAddress();
        cadd.setId("456");
        cadd.setStreet("2 A Street");
        cadd.setPostalCode("A1B 2C3");
        adds.add(cadd);
        cadd = new CanadianAddress();
        cadd.setId("789");
        cadd.setStreet("3 A Street");
        cadd.setPostalCode("A1B 2C3");
        adds.add(cadd);

        dep.setAddresses(adds);
        return dep;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.CanadianAddressesAsNestedTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
