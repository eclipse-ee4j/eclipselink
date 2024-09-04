/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.models.jpa.weave;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name="JPA21_ORDER_1")
public class Order {

    @Id
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    protected Node node;

    @Version
    protected Integer version;

    protected Order() {
    }

    public Order(long id) {

        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node destinationNode) {
        this.node = destinationNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(node, order.node) && Objects.equals(version, order.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, node, version);
    }
}