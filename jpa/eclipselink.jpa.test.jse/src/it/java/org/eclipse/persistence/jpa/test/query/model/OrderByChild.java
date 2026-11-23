/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORDER_BY_CHILD")
public class OrderByChild {

    @Id
    private long id;

    private String name;

    @ManyToOne
    private OrderByParent parent;

    @Embedded
    private OrderByDetail detail;

    @OneToMany(mappedBy = "child", cascade = CascadeType.PERSIST)
    @OrderBy("name DESC")
    private List<OrderByGrandchild> grandchildren = new ArrayList<>();

    public OrderByChild() {
    }

    public OrderByChild(long id, String name, OrderByParent parent) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.detail = new OrderByDetail(name.toUpperCase());
    }

    public String getName() {
        return name;
    }

    public List<OrderByGrandchild> getGrandchildren() {
        return grandchildren;
    }

    public void addGrandchild(long grandchildId, String name) {
        grandchildren.add(new OrderByGrandchild(grandchildId, name, this));
    }
}
