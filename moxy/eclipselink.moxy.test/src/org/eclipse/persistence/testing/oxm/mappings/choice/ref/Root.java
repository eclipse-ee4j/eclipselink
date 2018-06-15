/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - December 01/2010 - 2.3 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.choice.ref;

import java.util.Iterator;
import java.util.List;

public class Root {

    public List<Employee> employees;
    public List<Address> addresses;
    public List<PhoneNumber> phones;

    public boolean equals(Object obj) {
        Root root = (Root)obj;

        return employees.equals(root.employees) && addresses.equals(root.addresses) && phones.equals(root.phones);
    }
}
