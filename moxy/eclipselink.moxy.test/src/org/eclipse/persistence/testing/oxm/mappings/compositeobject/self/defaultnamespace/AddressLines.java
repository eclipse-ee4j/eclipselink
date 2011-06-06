/*******************************************************************************
* Copyright (c) 1998, 2011 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* dmccann - Nov.19/2008 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.defaultnamespace;

public class AddressLines {
    public String addressLine1;
    public String addressLine2;
    public String addressLine3;
    public String addressLine4;

    public boolean equals(Object obj) {
        AddressLines objAdd;
        try {
            objAdd = (AddressLines) obj;
        } catch (ClassCastException cce) {
            return false;
        }
        
        if ((!addressLine1.equals(objAdd.addressLine1)) ||
                (!addressLine2.equals(objAdd.addressLine2)) ||
                (!addressLine3.equals(objAdd.addressLine3)) ||
                (!addressLine4.equals(objAdd.addressLine4))) {
            return false;
        }
        return true;
    }

    public String toString() {
        return addressLine1 + ", " + addressLine2 + ", " +addressLine3 + ", " +addressLine4;
    }
}
