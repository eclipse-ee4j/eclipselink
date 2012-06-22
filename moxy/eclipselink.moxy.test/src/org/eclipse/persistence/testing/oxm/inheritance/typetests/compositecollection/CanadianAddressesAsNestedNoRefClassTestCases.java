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

import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.sessions.Project;

public class CanadianAddressesAsNestedNoRefClassTestCases extends CanadianAddressesAsNestedTestCases {
    public CanadianAddressesAsNestedNoRefClassTestCases(String name) throws Exception {
        super(name);
        Project p = new COMCollectionTypeProject();
        ((XMLCompositeCollectionMapping)p.getDescriptor(Dependant.class).getMappingForAttributeName("addresses")).setReferenceClass(null);
        ((XMLCompositeCollectionMapping)p.getDescriptor(Dependant.class).getMappingForAttributeName("addresses")).setReferenceClassName(null);
        setProject(p);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.CanadianAddressesAsNestedNoRefClassTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
