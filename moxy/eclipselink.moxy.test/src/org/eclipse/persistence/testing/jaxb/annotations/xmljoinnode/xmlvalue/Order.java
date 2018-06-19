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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;


public class Order {

    @XmlAttribute
    @XmlID
    public String id;

    @XmlElement(name="item-id")
    public String itemId;

    public boolean equals(Object obj) {
        return ((Order)obj).id.equals(id) && ((Order)obj).itemId.equals(itemId);
    }
}
