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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlNamedAttributeNode;
import org.eclipse.persistence.oxm.annotations.XmlNamedObjectGraph;

@XmlRootElement
@XmlNamedObjectGraph(
        name = "simple",
        attributeNodes = {
            @XmlNamedAttributeNode(value="contactInfo", subgraph="simple")
        }

    )
public class Employee {
    private List<ContactInfo> contactInfo = new ArrayList<ContactInfo>();

    public List<ContactInfo> getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(List<ContactInfo> contactInfo) {
        this.contactInfo = contactInfo;
    }

    public boolean equals(Object obj) {
        Employee e = (Employee)obj;
        return e.getContactInfo().equals(getContactInfo());
    }
}
