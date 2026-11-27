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

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORDER_BY_GRANDCHILD")
public class OrderByGrandchild {

    @Id
    private long id;

    private String name;

    @ManyToOne
    private OrderByChild child;

    public OrderByGrandchild() {
    }

    public OrderByGrandchild(long id, String name, OrderByChild child) {
        this.id = id;
        this.name = name;
        this.child = child;
    }

    public String getName() {
        return name;
    }
}
