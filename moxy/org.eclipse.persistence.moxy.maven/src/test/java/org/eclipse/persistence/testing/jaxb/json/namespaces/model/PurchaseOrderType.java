/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Radek Felcman - January 2019 - Initial implementation
package org.eclipse.persistence.testing.jaxb.json.namespaces.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PurchaseOrderType", propOrder = {
        "shipTo",
        "billTo"
})
@XmlRootElement(name = "PurchaseOrder")
public class PurchaseOrderType {

    @XmlElement(name = "ShipTo", required = true)
    protected List<USAddress> shipTo;
    @XmlElement(name = "BillTo", required = true)
    protected USAddress billTo;

    public List<USAddress> getShipTo() {
        if (shipTo == null) {
            shipTo = new ArrayList<USAddress>();
        }
        return this.shipTo;
    }

    public USAddress getBillTo() {
        return billTo;
    }

    public void setBillTo(USAddress value) {
        this.billTo = value;
    }

    public void setShipTo(List<USAddress> shipTo) {
        this.shipTo = shipTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseOrderType that = (PurchaseOrderType) o;
        return Objects.equals(getShipTo(), that.getShipTo()) &&
                Objects.equals(getBillTo(), that.getBillTo());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getShipTo(), getBillTo());
    }
}
