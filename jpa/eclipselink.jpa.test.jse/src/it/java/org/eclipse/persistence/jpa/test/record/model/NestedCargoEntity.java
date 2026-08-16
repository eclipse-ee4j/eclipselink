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

package org.eclipse.persistence.jpa.test.record.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class NestedCargoEntity {

    @Id
    private Long id;

    @Embedded
    private NestedDeliveryRecord delivery;

    private String trackingId;

    protected NestedCargoEntity() {
    }

    public NestedCargoEntity(Long id, NestedDeliveryRecord delivery, String trackingId) {
        this.id = id;
        this.delivery = delivery;
        this.trackingId = trackingId;
    }

    public Long getId() {
        return id;
    }

    public NestedDeliveryRecord getDelivery() {
        return delivery;
    }

    public String getTrackingId() {
        return trackingId;
    }
}
