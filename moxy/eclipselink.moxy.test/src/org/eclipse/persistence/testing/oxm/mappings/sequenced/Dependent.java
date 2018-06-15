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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.sequenced;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.sequenced.*;

public class Dependent implements SequencedObject {

    private String firstName;
    private String lastName;
    private Address address;
    private List<Setting> settings = new ArrayList<Setting>();

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public boolean equals(Object object) {
        try {
            Dependent testDependent = (Dependent) object;
            if(!Comparer.equals(firstName, testDependent.getFirstName())) {
                return false;
            }
            if(!Comparer.equals(lastName, testDependent.getLastName())) {
                return false;
            }
            /*
            if(!Comparer.equals(address, testDependent.getAddress())) {
                return false;
            }
            */
            if(!Comparer.equals(settings, testDependent.getSettings())) {
                return false;
            }
            return true;
        } catch(ClassCastException e) {
            return false;
        }
    }

}
