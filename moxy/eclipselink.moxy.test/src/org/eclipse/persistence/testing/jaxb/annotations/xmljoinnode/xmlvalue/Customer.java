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
//     Matt MacIvor - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmljoinnode.xmlvalue;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlJoinNode;

public class Customer {

    @XmlValue
    public String info;

    @XmlJoinNode(referencedXmlPath="@id", xmlPath="@order-id")
    public Order order;

    public boolean equals(Object obj) {
        return info.equals(((Customer)obj).info) && order.equals(((Customer)obj).order);
    }
}
