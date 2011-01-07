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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlmarshaller.twoprojects;

import java.util.Vector;

public class Customer {
    private Address address;
    private Vector addresses;
    private Object any;
    private Vector anyCollection;

    public Customer() {
        super();
        addresses = new Vector();
        anyCollection = new Vector();
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Vector getAddresses() {
        return addresses;
    }

    public void setAddresses(Vector addresses) {
        this.addresses = addresses;
    }

    public Object getAny() {
        return any;
    }

    public void setAny(Object any) {
        this.any = any;
    }

    public Vector getAnyCollection() {
        return anyCollection;
    }

    public void setAnyCollection(Vector anyCollection) {
        this.anyCollection = anyCollection;
    }

    public boolean equals(Object object) {
        try {
            if (null == object) {
                return false;
            }
            Customer customer = (Customer)object;

            if (null == address) {
                if (null != customer.getAddress()) {
                    return false;
                }
            } else {
                if (!address.equals(customer.getAddress())) {
                    return false;
                }
            }

            if (null == addresses) {
                if (null != customer.getAddresses()) {
                    return false;
                }
            } else {
                if (null == customer.getAddresses()) {
                    return false;
                }
                if (addresses.size() != customer.getAddresses().size()) {
                    return false;
                }
                int addressesSize = addresses.size();
                for (int x = 0; x < addressesSize; x++) {
                    if (!addresses.get(x).equals(customer.getAddresses().get(x))) {
                        return false;
                    }
                }
            }

            if (null == any) {
                if (null != customer.getAny()) {
                    return false;
                }
            } else {
                if (!any.equals(customer.getAny())) {
                    return false;
                }
            }

            if (null == anyCollection) {
                if (null != customer.getAnyCollection()) {
                    return false;
                }
            } else {
                if (null == customer.getAnyCollection()) {
                    return false;
                }
                if (anyCollection.size() != customer.getAnyCollection().size()) {
                    return false;
                }
                int anyCollectionSize = anyCollection.size();
                for (int x = 0; x < anyCollectionSize; x++) {
                    if (!anyCollection.get(x).equals(customer.getAnyCollection().get(x))) {
                        return false;
                    }
                }
            }

            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        String string = "Customer(address=";
        string += address;
        string += " addresses=";
        string += addresses;
        string += " any=";
        string += any;
        string += " anyCollection=";
        string += anyCollection;
        string += ")";
        return string;
    }
}
