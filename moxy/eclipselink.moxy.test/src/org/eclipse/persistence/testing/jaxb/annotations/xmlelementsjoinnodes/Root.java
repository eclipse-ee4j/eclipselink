/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementsjoinnodes;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

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
