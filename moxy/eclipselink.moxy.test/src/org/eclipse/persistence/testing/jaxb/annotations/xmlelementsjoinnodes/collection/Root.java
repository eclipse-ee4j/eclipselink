/*******************************************************************************
 * Copyright (c) 2011, 2014 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementsjoinnodes.collection;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.eclipse.persistence.testing.jaxb.annotations.xmlelementsjoinnodes.Address;
import org.eclipse.persistence.testing.jaxb.annotations.xmlelementsjoinnodes.PhoneNumber;

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
    
    public Root() {}
    public Root(List<Client> clients, List<Address> addresses, List<PhoneNumber> phoneNumbers) {
        this.clients = clients;
        this.addresses = addresses;
        this.phoneNumbers = phoneNumbers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Root root = (Root) o;

        if (addresses != null ? !Arrays.equals(addresses.toArray(), root.addresses.toArray()) : root.addresses != null) return false;
        if (clients != null ? !Arrays.equals(clients.toArray(), root.clients.toArray()) : root.clients != null) return false;
        if (phoneNumbers != null ? !Arrays.equals(phoneNumbers.toArray(), root.phoneNumbers.toArray()) : root.phoneNumbers != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clients != null ? Arrays.hashCode(clients.toArray()) : 0;
        result = 31 * result + (addresses != null ? Arrays.hashCode(addresses.toArray()) : 0);
        result = 31 * result + (phoneNumbers != null ? Arrays.hashCode(phoneNumbers.toArray()) : 0);
        return result;
    }
}
