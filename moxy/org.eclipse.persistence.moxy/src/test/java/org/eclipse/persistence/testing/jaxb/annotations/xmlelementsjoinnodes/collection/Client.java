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
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementsjoinnodes.collection;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlRootElement;

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
