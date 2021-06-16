/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
// dmccann - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementsjoinnodes;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement
public class Root {
    @XmlElement
    @XmlPath("client")
    public List<Client> clients;

    @XmlElement
    @XmlPath("address")
    public List<Address> addresses;

    @XmlElement
    @XmlPath("phone-number")
    public List<PhoneNumber> phoneNumbers;

    @XmlElements({
        @XmlElement(name="publicCompany", type=PublicCompany.class)
    })
    private List<Company> companies;

    public Root() {}
    public Root(List<Client> clients, List<Address> addresses, List<PhoneNumber> phoneNumbers, List<Company> companies) {
        this.clients = clients;
        this.addresses = addresses;
        this.phoneNumbers = phoneNumbers;
        this.companies = companies;
    }

    @Override
    public int hashCode() {
        int result = clients != null ? clients.hashCode() : 0;
        result = 31 * result + (addresses != null ? addresses.hashCode() : 0);
        result = 31 * result + (phoneNumbers != null ? phoneNumbers.hashCode() : 0);
        result = 31 * result + (companies != null ? companies.hashCode() : 0);
        return result;
    }

    public boolean equals(Object o) {
        Root r;
        try {
            r = (Root) o;
        } catch (ClassCastException cce) {
            return false;
        }
        try {
            if (this.clients.size() != r.clients.size()) {
                return false;
            }
            if (this.addresses.size() != r.addresses.size()) {
                return false;
            }
            if (this.phoneNumbers.size() != r.phoneNumbers.size()) {
                return false;
            }
            boolean foundIt;
            for (Client c : this.clients) {
                foundIt = false;
                for (Client c1 : r.clients) {
                    if (c1.equals(c)) {
                        foundIt = true;
                    }
                }
                if (!foundIt) {
                    return false;
                }
            }
            for (Address a : this.addresses) {
                foundIt = false;
                for (Address a1 : r.addresses) {
                    if (a1.equals(a)) {
                        foundIt = true;
                    }
                }
                if (!foundIt) {
                    return false;
                }
            }
            for (PhoneNumber p : this.phoneNumbers) {
                foundIt = false;
                for (PhoneNumber p1 : r.phoneNumbers) {
                    if (p1.equals(p)) {
                        foundIt = true;
                    }
                }
                if (!foundIt) {
                    return false;
                }
            }
        } catch (Exception x) {
            return false;
        }
        return true;
    }
}
