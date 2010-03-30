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
 * dmccann - March 25/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.collectionreference;

import java.util.List;

public class Employee {
    public List<Address> workAddresses;
    
    public boolean equals(Object obj) {
        if (obj == null) { return false; }

        Employee theObj;
        try {
            theObj = (Employee) obj;
        } catch (ClassCastException e) {
            return false;
        }
        
        if (workAddresses == null) {
            return theObj.workAddresses == null;
        }
        
        if (theObj.workAddresses == null) { 
            return false; 
        }
        
        if (workAddresses.size() != theObj.workAddresses.size()) {
            return false;
        }
        
        for (Address add : workAddresses) {
            if (!addExistsInList(add, theObj.workAddresses)) {
                return false;
            }
        }
        return true;
    }   
    
    private boolean addExistsInList(Address add, List<Address> addList) {
        for (Address listAdd : addList) {
            if (listAdd.equals(add)) {
                return true;
            }
        }
        return false;
    }
}