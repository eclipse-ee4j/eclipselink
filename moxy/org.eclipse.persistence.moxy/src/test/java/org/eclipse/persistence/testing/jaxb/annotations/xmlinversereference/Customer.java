/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 9/20/2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference;

import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="CUSTOMER")
public class Customer implements Linkable {

    private Address address;
    private List<PhoneNumber> phoneNumber;

    public List<PhoneNumber> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(List<PhoneNumber> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    private String link;

    @Override
    public String getLink() {
        // TODO Auto-generated method stub
        return link;
    }

    @Override
    public void setLink(String link) {
        // TODO Auto-generated method stub
        this.link = link;
    }


}
