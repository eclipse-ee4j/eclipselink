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
*     bdoughan - December 17/2009 - 2.0.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.advancedxpath;

public class Address {

    private String street;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public boolean equals(Object object) {
        try {
            if(null == object) {
                return false;
            }
            Address testAddress = (Address) object;
            if(null == street) {
                return null == testAddress.getStreet();
            } else {
                return street.equals(testAddress.getStreet());
            }
        } catch(ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        String string = "Address(street=";
        if(null == street) {
            string += null;
        } else {
            string += "'" + street + "'";
        }
        return string += ")";
    }

}