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
//     Rick Barkhouse - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmltransient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PurchaseOrderType", propOrder = {
    "shipTo",
    "billTo"
})
public class PurchaseOrder {

    @XmlTransient
    protected Object shipTo = "FOO";

    @XmlElement(required = true)
    protected Object billTo;

    @XmlElement(name="shipTo", required = true)
    public Object getShipTo() {
        return shipTo;
    }

    public void setShipTo(Object value) {
        this.shipTo = value;
    }

}
