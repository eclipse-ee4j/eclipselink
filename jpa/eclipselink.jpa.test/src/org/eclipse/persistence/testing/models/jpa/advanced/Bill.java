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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "JPA_BILL")
public class Bill {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy="bill", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<BillLine> billLines;
    
    private String orderIdentifier;
    private String status = STATUS_NEW;
    
    public static final String STATUS_NEW = "NEW";
    public static final String STATUS_PROCESSING = "PROCESSING";

    public Bill() {
        super();
        this.billLines = new ArrayList<BillLine>();
    }

    public void addBillLine(BillLine billLine) {
        if (!this.billLines.contains(billLine)) {
            this.billLines.add(billLine);
            billLine.setBill(this);
        }
    }
    
    public void removeBillLine(BillLine billLine) {
        if (this.billLines.contains(billLine)) {
            this.billLines.remove(billLine);
            billLine.setBill(null);
        }
    }

    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderIdentifier() {
        return this.orderIdentifier;
    }
    
    public void setOrderIdentifier(String orderIdentifier) {
        this.orderIdentifier = orderIdentifier;
    }

    public List<BillLine> getBillLines() {
        return billLines;
    }
    
    public void setBillLines(List<BillLine> billLines) {
        this.billLines = billLines;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " id:[" + this.id + "] order id:[" + this.orderIdentifier + "] hashcode:[" + System.identityHashCode(this) + "]"; 
    }

}
