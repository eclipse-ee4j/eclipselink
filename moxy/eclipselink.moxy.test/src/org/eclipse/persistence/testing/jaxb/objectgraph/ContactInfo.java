/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.5 - initial implementation
package org.eclipse.persistence.testing.jaxb.objectgraph;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.eclipse.persistence.oxm.annotations.XmlNamedAttributeNode;
import org.eclipse.persistence.oxm.annotations.XmlNamedObjectGraph;
import org.eclipse.persistence.oxm.annotations.XmlNamedSubgraph;

@XmlSeeAlso({AddressInh.class, PhoneNumberInh.class})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlNamedObjectGraph(
        name = "simple",
        attributeNodes = {
            @XmlNamedAttributeNode("contactType")
        },
        subclassSubgraphs = {
            @XmlNamedSubgraph(
                name="simple",
                type = AddressInh.class,
                attributeNodes = {
                    @XmlNamedAttributeNode("city")
                }
            ),
            @XmlNamedSubgraph(
                name = "simple",
                type = PhoneNumberInh.class,
                attributeNodes = {
                      @XmlNamedAttributeNode("number")
                }
            )
        }
    )
public abstract class ContactInfo {

    int id;

    String contactType;

}
