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

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.annotations.ChangeTracking;
import org.eclipse.persistence.annotations.ChangeTrackingType;

@Entity
@Table(name = "OC_CACHE_OWNER")
@Cache(type = CacheType.FULL, coordinationType = CacheCoordinationType.SEND_OBJECT_CHANGES)
@ChangeTracking(ChangeTrackingType.ATTRIBUTE)
public class OrderedCacheOwner {

    @Id
    private int id;

    @Column(name = "OWNER_LABEL")
    private String label;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @OrderBy("position ASC")
    private List<OrderedCacheElement> elements = new ArrayList<>();

    public OrderedCacheOwner() {
    }

    public OrderedCacheOwner(int id) {
        this.id = id;
        this.label = "initial";
    }

    public int getId() {
        return id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<OrderedCacheElement> getElements() {
        return elements;
    }

    public void addElement(OrderedCacheElement element) {
        elements.add(element);
        element.setOwner(this);
    }
}
