/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// dmccann - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementsjoinnodes.collection;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlJoinNode;
import org.eclipse.persistence.oxm.annotations.XmlJoinNodes;
import org.eclipse.persistence.oxm.annotations.XmlElementsJoinNodes;
import org.eclipse.persistence.testing.jaxb.annotations.xmlelementsjoinnodes.Address;
import org.eclipse.persistence.testing.jaxb.annotations.xmlelementsjoinnodes.PhoneNumber;

@XmlRootElement
public class Client {
    @XmlAttribute
    @XmlID
    public String id;

    @XmlElements({
        @XmlElement(name="mail", type=Address.class),
        @XmlElement(name="phone", type=PhoneNumber.class)
    })
    @XmlElementsJoinNodes({
        @XmlJoinNodes({
            @XmlJoinNode(xmlPath="mail/@id", referencedXmlPath="@aid"),
            @XmlJoinNode(xmlPath="mail/type/text()", referencedXmlPath="@type")
        }),
        @XmlJoinNodes({
            @XmlJoinNode(xmlPath="phone/@id", referencedXmlPath="@pid"),
            @XmlJoinNode(xmlPath="phone/type/text()", referencedXmlPath="@type")
        })
    })
    public List<Object> preferredContactMethods;

    public Client() {}
    public Client(String id, List<Object> preferredContactMethods) {
        this.id = id;
        this.preferredContactMethods = preferredContactMethods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;

        if (id != null ? !id.equals(client.id) : client.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        return result;
    }
}
