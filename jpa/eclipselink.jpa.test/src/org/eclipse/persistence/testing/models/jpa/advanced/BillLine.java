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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "JPA_BILL_LINE")
public class BillLine {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Bill bill;

    @OneToMany(mappedBy="billLine", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<BillLineItem> billLineItems;

    private int quantity;

    public BillLine() {
        super();
        this.billLineItems = new ArrayList<BillLineItem>();
    }

    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public Bill getBill() {
        return this.bill;
    }
    
    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public int getQuantity() {
        return this.quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<BillLineItem> getBillLineItems() {
        return billLineItems;
    }
    
    public void setBillLineItems(List<BillLineItem> billLineItems) {
        this.billLineItems = billLineItems;
    }
    
    public void addBillLineItem(BillLineItem item) {
        if (!getBillLineItems().contains(item)) {
            getBillLineItems().add(item);
            item.setBillLine(this);
        }
    }
    
    public void removeBillLineItem(BillLineItem item) {
        if (getBillLineItems().contains(item)) {
            getBillLineItems().remove(item);
            item.setBillLine(null);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " id:[" + this.id + "] quantity:[" + this.quantity + "] hashcode:[" + System.identityHashCode(this) + "]"; 
    }

}
