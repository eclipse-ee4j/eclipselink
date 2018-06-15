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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="JPA_BILL_ACTION")
public class BillAction {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private BillLine billLine;

    private int priority;

    public BillAction() {
        super();
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BillLine getBillLine() {
        return billLine;
    }
    
    public void setBillLine(BillLine billLine) {
        this.billLine = billLine;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " id:[" + this.id + "] priority:[" + this.priority + "] hashcode:[" + System.identityHashCode(this) + "]"; 
    }

}
