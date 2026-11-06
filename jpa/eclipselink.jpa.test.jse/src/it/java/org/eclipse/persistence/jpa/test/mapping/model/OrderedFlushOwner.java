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

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.ChangeTracking;
import org.eclipse.persistence.annotations.ChangeTrackingType;

/** Owner with deferred collection tracking for issue #2899. */
@Entity
@Table(name = "OF_OWNER")
@ChangeTracking(ChangeTrackingType.DEFERRED)
public class OrderedFlushOwner {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "owner")
    @OrderBy("name ASC")
    private List<OrderedFlushChild> children = new ArrayList<>();

    @OneToMany(mappedBy = "indexedOwner")
    @OrderColumn(name = "LIST_POSITION")
    private List<OrderedFlushChild> indexedChildren = new ArrayList<>();

    @OneToMany(mappedBy = "unorderedOwner")
    private List<OrderedFlushChild> unorderedChildren = new ArrayList<>();

    /** Return the generated identifier. */
    public Long getId() {
        return id;
    }

    /** Return the collection for the requested mapping. */
    public List<OrderedFlushChild> children(String kind) {
        return switch (kind) {
            case "indexedOwner" -> indexedChildren;
            case "unorderedOwner" -> unorderedChildren;
            default -> children;
        };
    }
}
