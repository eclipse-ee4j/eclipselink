/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 9/20/2012 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

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