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

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlNamedObjectGraph;
import org.eclipse.persistence.oxm.annotations.XmlNamedObjectGraphs;
import org.eclipse.persistence.oxm.annotations.XmlNamedAttributeNode;
import org.eclipse.persistence.oxm.annotations.XmlNamedSubgraph;
import org.eclipse.persistence.oxm.annotations.XmlPath;


@XmlNamedObjectGraphs({
    @XmlNamedObjectGraph(name="basic", attributeNodes={
            @XmlNamedAttributeNode("firstName"),
            @XmlNamedAttributeNode("lastName"),
            @XmlNamedAttributeNode(value="address", subgraph="simple-address")
    },
    subgraphs={
            @XmlNamedSubgraph(name="simple-address", attributeNodes={
                    @XmlNamedAttributeNode("city"),
                    @XmlNamedAttributeNode("country")
            })
    }),
    @XmlNamedObjectGraph(name="complex", attributeNodes={
            @XmlNamedAttributeNode("firstName"),
            @XmlNamedAttributeNode("lastName"),
            @XmlNamedAttributeNode("gender"),
            @XmlNamedAttributeNode(value="address", subgraph="basic"),
            @XmlNamedAttributeNode(value="phoneNumbers", subgraph="simple-phone")
    },
    subgraphs={
            @XmlNamedSubgraph(name="simple-phone", attributeNodes={
                    @XmlNamedAttributeNode("number")
            })
    })
})
@XmlRootElement
public class Customer {

    public String firstName;

    public String lastName;

    public String age;

    public String gender;

    public Address address;

    @XmlPath("phone-numbers/phone-number")
    public List<PhoneNumber> phoneNumbers;


    public boolean equals(Object obj) {
        Customer cust = (Customer) obj;
        if(!cust.address.equals(address)) {
            return false;
        }
        if(cust.phoneNumbers != phoneNumbers) {
            if(phoneNumbers.size() != cust.phoneNumbers.size()) {
                return false;
            }
            for(int i = 0; i < phoneNumbers.size(); i++) {
                if(!phoneNumbers.get(i).equals(cust.phoneNumbers.get(i))) {
                    return false;
                }
            }
        }

        return (firstName == cust.firstName || firstName.equals(cust.firstName))
                && (lastName == cust.lastName || lastName.equals(cust.lastName))
                && (age == cust.age || age.equals(cust.age))
                && (gender == cust.gender || gender.equals(cust.gender));
    }
}
