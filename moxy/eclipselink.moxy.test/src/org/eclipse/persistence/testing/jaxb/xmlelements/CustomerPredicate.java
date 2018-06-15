/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.4.2 - Initial Implementation
package org.eclipse.persistence.testing.jaxb.xmlelements;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.eclipse.persistence.oxm.annotations.XmlPaths;

@XmlRootElement(name="customer")
public class CustomerPredicate {
    @XmlElements({
        @XmlElement(type=Address.class),
        @XmlElement(type=Link.class)
    })
    @XmlPaths({
        @XmlPath("address"),
        @XmlPath("address[@rel=\"self\"]")
    })
    public Object address;

    public boolean equals(Object obj) {
        CustomerPredicate cust = (CustomerPredicate)obj;
        return address == cust.address || address.equals(address);
    }
}
