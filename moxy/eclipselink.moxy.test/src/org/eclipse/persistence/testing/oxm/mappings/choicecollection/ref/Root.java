/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Oracle = 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.choicecollection.ref;

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
