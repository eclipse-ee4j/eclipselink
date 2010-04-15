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
*     bdoughan - April 9/2010 - 2.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.keybased.norefclass;

public class Address {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object object) {
        try {
            if(null == object) {
                return false;
            }
            Address testAddress = (Address) object;
            return id == testAddress.getId();
        } catch(ClassCastException e) {
            return false;
        }
    }

}