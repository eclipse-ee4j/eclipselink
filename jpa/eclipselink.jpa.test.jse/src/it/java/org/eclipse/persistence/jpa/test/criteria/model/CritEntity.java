/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022 IBM Corporation. All rights reserved.
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
//     04/19/2022: Jody Grassel
//       - Issue 579726: CriteriaBuilder neg() only returns Integer type, instead of it's argument expression type.
package org.eclipse.persistence.jpa.test.criteria.model;

import java.math.BigDecimal;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CritEntity {
    @Id
    private int id;

    @Basic
    @Column(precision = 10, scale = 2)
    private BigDecimal value;

    public CritEntity() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal bigDec) {
        this.value = bigDec;
    }

    @Override
    public String toString() {
        return "CritEntity [id=" + id + ", value=" + value + "]";
    }


}


