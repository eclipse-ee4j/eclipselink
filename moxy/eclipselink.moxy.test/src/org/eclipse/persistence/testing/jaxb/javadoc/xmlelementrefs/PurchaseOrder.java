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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelementrefs;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PurchaseOrder {

    @XmlElementRefs({
        @XmlElementRef(name="air-transport", type=ViaAir.class),
        @XmlElementRef(name="land-transport", type=ViaLand.class)})
    public TransportType shipBy;

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof PurchaseOrder)) {
            return false;
        }
        PurchaseOrder order = (PurchaseOrder) obj;

        return order.shipBy.equals(this.shipBy);
    }
}
