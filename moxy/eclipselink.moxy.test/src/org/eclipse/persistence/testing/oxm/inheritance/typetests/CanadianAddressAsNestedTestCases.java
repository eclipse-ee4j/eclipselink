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
package org.eclipse.persistence.testing.oxm.inheritance.typetests;

import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class CanadianAddressAsNestedTestCases extends XMLWithJSONMappingTestCases {
    private static final String READ_DOC = "org/eclipse/persistence/testing/oxm/inheritance/typetests/dependant_with_cdnaddress_noxsi.xml";
    private static final String JSON_READ_DOC = "org/eclipse/persistence/testing/oxm/inheritance/typetests/dependant_with_cdnaddress_noxsi.json";
    
    public CanadianAddressAsNestedTestCases(String name) throws Exception {
        super(name);
        setProject(new TypeProject());
        setControlDocument(READ_DOC);
        setControlJSON(JSON_READ_DOC);
    }

    public Object getControlObject() {
        Dependant dep = new Dependant();
        CanadianAddress cadd = new CanadianAddress();
        cadd.setId("123");
		cadd.setStreet("1 A Street");
		cadd.setPostalCode("A1B 2C3");
        dep.setAddress(cadd);
        return dep;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.typetests.CanadianAddressAsNestedTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
