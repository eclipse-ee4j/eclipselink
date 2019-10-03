/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships;


import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.TABLE;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.config.CacheIsolationType;

@Entity(name="FieldAccessSalesPerson")
@Table(name="CMP3_FIELDACCESS_SALESPERSON")
@Cache(isolation=CacheIsolationType.ISOLATED)
public class SalesPerson implements java.io.Serializable
{
    private String name;
    @Id
    @GeneratedValue(strategy=TABLE, generator="FIELDACCESS_SALESPERSON_TABLE_GENERATOR")
    @TableGenerator(
            name="FIELDACCESS_SALESPERSON_TABLE_GENERATOR",
            table="CMP3_FIELDACCESS_CUSTOMER_SEQ",
            pkColumnName="SEQ_NAME",
            valueColumnName="SEQ_COUNT",
            pkColumnValue="SALESPERSON_SEQ"
    )
    @Column(name="ID")
    private int id;
    @OneToMany(cascade=ALL, mappedBy="salesPerson")
    private Collection<Order> orders;

    public SalesPerson() {};

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

    public Collection<Order> getOrders() {
        return orders;
    }
    public void setOrders(Collection orders) {
        this.orders = orders;
    }
}
