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
package org.eclipse.persistence.testing.oxm.inheritance.typetests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.any.ContactAsAnyNestedTestCases;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.any.collection.ContactsAsAnyNestedTestCases;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.AddressesAsNestedNoRefClassTestCases;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.AddressesAsNestedTestCases;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.AddressesAsNestedWithCdnAddressNoDefaultRootTypeXsiTestsCases;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.AddressesAsNestedWithCdnAddressXsiNoRefClassTestCases;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.AddressesAsNestedWithCdnAddressXsiTestCases;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.CanadianAddressesAsNestedNoRefClassTestCases;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.CanadianAddressesAsNestedTestCases;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.ContactsAsNestedNoRefClassTestCases;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.ContactsAsNestedTestCases;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.ContactsAsNestedWithAddressXsiNoRefClassTestCases;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.ContactsAsNestedWithAddressXsiTestCases;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.ContactsAsNestedWithCdnAddressXsiNoRefClassTestCases;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.ContactsAsNestedWithCdnAddressXsiTestCases;

public class TypeTestSuite extends TestCase {
    public TypeTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Type Test Suite");
        try {
            // --------------------- basic tests ---------------------
            // use case #1-1 
            suite.addTestSuite(ContactAsRootTestCases.class);
            // use case #1-2 
            suite.addTestSuite(ContactAsRootWithAddressXsiTestCases.class);
            // use case #2-1 
            suite.addTestSuite(ContactAsNestedTestCases.class);
            // use case #2-2 
            suite.addTestSuite(ContactAsNestedWithAddressXsiTestCases.class);
            // use case #2-3 
            suite.addTestSuite(ContactAsNestedWithCdnAddressXsiTestCases.class);
            // use case #3-1 
            suite.addTestSuite(AddressAsNestedTestCases.class);
            // use case #3-2 
            suite.addTestSuite(AddressAsNestedWithCdnAddressXsiTestCases.class);
            // use case #4-1 
            suite.addTestSuite(CanadianAddressAsNestedTestCases.class);
            // use case #5-1
            suite.addTestSuite(XmlRootWithContactXsiTestCases.class);
            // additional test #1-1
            suite.addTestSuite(ContactAsCdnAddressTestCases.class);
            // additional test #1-2
            suite.addTestSuite(ContactAsNestedCdnAddressTestCases.class);

            // --------------------- collection cases ---------------------
            suite.addTestSuite(ContactsAsNestedTestCases.class);
            suite.addTestSuite(ContactsAsNestedWithAddressXsiTestCases.class);
            suite.addTestSuite(ContactsAsNestedWithCdnAddressXsiTestCases.class);
            suite.addTestSuite(AddressesAsNestedTestCases.class);
            suite.addTestSuite(AddressesAsNestedWithCdnAddressXsiTestCases.class);
            suite.addTestSuite(CanadianAddressesAsNestedTestCases.class);

            // --------------------- any cases ---------------------
            suite.addTestSuite(ContactAsAnyNestedTestCases.class);
            suite.addTestSuite(ContactsAsAnyNestedTestCases.class);

            // --------------------- not yet supported ---------------------
            // use case #1-3
            //suite.addTestSuite(CanadianAddressAsRootTestCases.class);
            suite.addTestSuite(AddressesAsNestedWithCdnAddressNoDefaultRootTypeXsiTestsCases.class);
            //no ref class tests
            suite.addTestSuite(AddressAsNestedNoRefClassTestCases.class);
            suite.addTestSuite(AddressAsNestedWithCdnAddressXsiNoRefClassTestCases.class);
            suite.addTestSuite(CanadianAddressAsNestedNoRefClassTestCases.class);
            suite.addTestSuite(ContactAsNestedCdnAddressNoRefClassTestCases.class);
            suite.addTestSuite(ContactAsNestedNoRefClassTestCases.class);
            suite.addTestSuite(ContactAsNestedWithAddressXsiNoRefClassTestCases.class);
            suite.addTestSuite(ContactAsNestedWithCdnAddressXsiNoRefTestCases.class);

            suite.addTestSuite(AddressesAsNestedNoRefClassTestCases.class);
            suite.addTestSuite(AddressesAsNestedWithCdnAddressXsiNoRefClassTestCases.class);
            suite.addTestSuite(CanadianAddressesAsNestedNoRefClassTestCases.class);
            suite.addTestSuite(ContactsAsNestedNoRefClassTestCases.class);
            suite.addTestSuite(ContactsAsNestedWithAddressXsiNoRefClassTestCases.class);
            suite.addTestSuite(ContactsAsNestedWithCdnAddressXsiNoRefClassTestCases.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.typetests.TypeTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }
}
