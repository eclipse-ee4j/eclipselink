/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - Nov.19/2008 - 1.1 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.defaultnamespace;

public class Address {
    public String attentionOfName;
    public String careOfName;
    public AddressLines addressLines;
    public String city;
    public String state;
    public String postalCode;
    public String countryCode;

    public boolean equals(Object obj) {
        Address objAdd;
        try {
            objAdd = (Address) obj;
        } catch (ClassCastException cce) {
            return false;
        }

        if ((!attentionOfName.equals(objAdd.attentionOfName)) ||
                (!careOfName.equals(objAdd.careOfName)) ||
                (!city.equals(objAdd.city)) ||
                (!state.equals(objAdd.state)) ||
                (!postalCode.equals(objAdd.postalCode)) ||
                (!countryCode.equals(objAdd.countryCode)) ||
                (!addressLines.equals(objAdd.addressLines))) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "Address: " + attentionOfName + ", " + careOfName + ", addressLines[" + addressLines + "], " + city + ", " + state + ", " + postalCode + ", " +countryCode;
    }
}
