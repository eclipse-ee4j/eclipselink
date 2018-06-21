/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directcollection.singlenode.xmlattribute;

import junit.textui.TestRunner;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.arraylist.Employee;

public class DirectCollectionSingleNodeXMLAttributeMethodAccessingTestCases extends DirectCollectionSingleNodeXMLAttributeTestCases {
    public DirectCollectionSingleNodeXMLAttributeMethodAccessingTestCases(String name) throws Exception {
        super(name);
        Project p = new DirectCollectionSingleNodeXMLAttributeProject();
        p.getDescriptor(Employee.class).getMappingForAttributeName("id").setGetMethodName("getID");
        p.getDescriptor(Employee.class).getMappingForAttributeName("id").setSetMethodName("setID");

        p.getDescriptor(Employee.class).getMappingForAttributeName("responsibilities").setGetMethodName("getResponsibilities");
        p.getDescriptor(Employee.class).getMappingForAttributeName("responsibilities").setSetMethodName("setResponsibilities");
        setProject(p);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.directcollection.singlenode.xmlattribute.DirectCollectionSingleNodeXMLAttributeMethodAccessingTestCases" };
        TestRunner.main(arguments);
    }

     public void xmlToObjectTest(Object testObject) throws Exception {
        super.xmlToObjectTest(testObject);
        assertEquals(1, ((Employee)testObject).getIdSetCounter());
        assertEquals(1, ((Employee)testObject).getResponsibilitiesSetCounter());
    }

}
