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

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;

@Entity
@Table(name = "ORDER_BY_FETCHED_PARENT")
public class OrderByFetchedParent {

    @Id
    private long id;

    @OneToMany
    @JoinColumn(name = "FETCHED_PARENT_ID")
    @JoinFetch(JoinFetchType.OUTER)
    @OrderBy("name DESC")
    private List<OrderByChild> children = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "OTHER_PARENT_ID")
    @OrderBy("name DESC")
    private List<OrderByChild> others = new ArrayList<>();

    public OrderByFetchedParent() {
    }

    public OrderByFetchedParent(long id) {
        this.id = id;
    }

    public List<OrderByChild> getChildren() {
        return children;
    }

    public List<OrderByChild> getOthers() {
        return others;
    }
}
