/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.relationships;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

import java.util.Collection;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.TABLE;

@Entity(name="SalesPerson")
@Table(name="CMP3_SALESPERSON")
public class SalesPerson
{
    private String name;
    private int id;
    private Collection<Order> orders;

    public SalesPerson() {}

    @Id
    @GeneratedValue(strategy=TABLE, generator="SALESPERSON_TABLE_GENERATOR")
    @TableGenerator(
        name="SALESPERSON_TABLE_GENERATOR",
        table="CMP3_CUSTOMER_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="SALESPERSON_SEQ"
    )
    @Column(name="ID")
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(cascade=ALL, mappedBy="salesPerson")
    public Collection<Order> getOrders() {
        return orders;
    }
    public void setOrders(Collection<Order> orders) {
        this.orders = orders;
    }
}
