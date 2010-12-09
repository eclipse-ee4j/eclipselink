/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice.reference;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;

import org.eclipse.persistence.oxm.annotations.XmlJoinNode;
import org.eclipse.persistence.oxm.annotations.XmlJoinNodes;
import org.eclipse.persistence.oxm.annotations.XmlElementsJoinNodes;

public class Client {
    @XmlAttribute
    @XmlID
    public String id;
    
    //@XmlElements({
    //    @XmlElement(name="mail", type=Address.class),
    //    @XmlElement(name="phone", type=PhoneNumber.class)
    //})
    //@XmlElementsJoinNodes({
    //    @XmlJoinNodes({
    //        @XmlJoinNode(xmlPath="mail/@id", referencedXmlPath="@aid"),
    //        @XmlJoinNode(xmlPath="mail/type/text()", referencedXmlPath="@type"),
    //    }),
    //    @XmlJoinNodes({
    //        @XmlJoinNode(xmlPath="phone/@id", referencedXmlPath="@pid"),
    //        @XmlJoinNode(xmlPath="phone/type/text()", referencedXmlPath="@type"),
    //    })
    //})
    public Object preferredContactMethod;
    
    public Client() {}
    public Client(String id, Object preferredContactMethod) {
        this.id = id;
        this.preferredContactMethod = preferredContactMethod;
    }
    
    public boolean equals(Object o) {
        Client c;
        try {
            c = (Client) o;
        } catch (ClassCastException cce) {
            return false;
        }
        try {
            return this.id.equals(c.id) && this.preferredContactMethod.equals(c.preferredContactMethod);
        } catch (Exception x) {
            return false;
        }
    }
}
