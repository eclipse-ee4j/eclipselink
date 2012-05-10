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

import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Project;

public class ContactAsNestedCdnAddressNoRefClassTestCases extends ContactAsNestedCdnAddressTestCases {
    public ContactAsNestedCdnAddressNoRefClassTestCases(String name) throws Exception {
        super(name);
        Project p = new TypeProject();
        ((XMLCompositeObjectMapping)p.getDescriptor(Customer.class).getMappingForAttributeName("contact")).setReferenceClass(null);
        ((XMLCompositeObjectMapping)p.getDescriptor(Customer.class).getMappingForAttributeName("contact")).setReferenceClassName(null);
        setProject(p);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.typetests.ContactAsNestedCdnAddressNoRefClassTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
