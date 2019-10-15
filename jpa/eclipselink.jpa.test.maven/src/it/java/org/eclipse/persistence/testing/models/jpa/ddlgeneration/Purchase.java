/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     07/17/2009 - tware - added tests for DDL generation of maps
//     09/15/2010-2.2 Chris Delahunt
//       - 322233 - AttributeOverrides and AssociationOverride dont change field type info
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

@Entity
public class Purchase {
    @Id
    @GeneratedValue
    private int id;

    @AttributeOverride(
            name="amount",
            column=@Column(name="FEE_AMOUNT", nullable=false))
    @AssociationOverride(
            name="currency",
            joinColumns=@JoinColumn(name="FEE_ID"))
    @Embedded
    private Money fee;

    public int getId() {
        return id;
    }

    public Money getFee() {
        return fee;
    }

    public void setFee(Money fee) {
        this.fee = fee;
    }
}
