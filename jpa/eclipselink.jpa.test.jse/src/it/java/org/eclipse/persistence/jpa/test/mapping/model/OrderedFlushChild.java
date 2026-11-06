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

package org.eclipse.persistence.jpa.test.mapping.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Child maintained on both sides of a relationship in the flush tests. */
@Entity
@Table(name = "OF_CHILD")
public class OrderedFlushChild {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @ManyToOne
    private OrderedFlushOwner owner;
    @ManyToOne
    private OrderedFlushOwner indexedOwner;
    @ManyToOne
    private OrderedFlushOwner unorderedOwner;

    /** Constructor for persistence. */
    public OrderedFlushChild() {
    }

    /** Create a named child. */
    public OrderedFlushChild(String name) {
        this.name = name;
    }

    /** Return the name used to verify list contents. */
    public String getName() {
        return name;
    }

    /** Maintain the owning side of the selected relationship. */
    public void setOwner(String kind, OrderedFlushOwner value) {
        switch (kind) {
            case "indexedOwner" -> indexedOwner = value;
            case "unorderedOwner" -> unorderedOwner = value;
            default -> owner = value;
        }
    }
}
