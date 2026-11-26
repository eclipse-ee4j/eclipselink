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

package org.eclipse.persistence.jpa.collection.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.annotations.ChangeTracking;
import org.eclipse.persistence.annotations.ChangeTrackingType;

@Entity
@Table(name = "OC_CACHE_ELEMENT")
@Cache(type = CacheType.FULL, coordinationType = CacheCoordinationType.SEND_OBJECT_CHANGES)
@ChangeTracking(ChangeTrackingType.ATTRIBUTE)
public class OrderedCacheElement {

    @Id
    private int id;

    @Column(name = "ELEMENT_POSITION")
    private int position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID")
    private OrderedCacheOwner owner;

    public OrderedCacheElement() {
    }

    public OrderedCacheElement(int id) {
        this.id = id;
        this.position = id;
    }

    public int getId() {
        return id;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setOwner(OrderedCacheOwner owner) {
        this.owner = owner;
    }
}
