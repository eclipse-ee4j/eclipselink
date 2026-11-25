/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.query.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "JPA_ARITHMETIC_ENTITY")
@NamedQuery(name = "ArithmeticEntity.balance",
        query = "SELECT e.amount - 0 - e.deduction - e.adjustment FROM ArithmeticEntity e WHERE e.id = :id")
@NamedQuery(name = "ArithmeticEntity.parameterBalance",
        query = "SELECT e.amount - e.deduction - :last FROM ArithmeticEntity e WHERE e.id = 1")
public class ArithmeticEntity {

    @Id
    private Integer id;

    private Integer amount;

    private Integer deduction;

    private Integer adjustment;

    @Column(name = "GROUP_ID")
    private Integer groupId;

    @Column(name = "NULLABLE_AMOUNT")
    private Integer nullableAmount;

    public ArithmeticEntity() {
    }

    public ArithmeticEntity(int id, int amount, int deduction, int adjustment, int groupId, Integer nullableAmount) {
        this.id = id;
        this.amount = amount;
        this.deduction = deduction;
        this.adjustment = adjustment;
        this.groupId = groupId;
        this.nullableAmount = nullableAmount;
    }
}
