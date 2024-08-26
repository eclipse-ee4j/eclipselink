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
@Table(name="JPA21_LOCATION")
public class Location {

    @Id
    protected Long id;

    protected String locationId;

    @ManyToOne(fetch = FetchType.LAZY)
    protected Node node;

    @Version
    protected Integer version;

    public Location(long id, String locationId) {
        this.id = id;
        this.locationId = locationId;
    }

    protected Location() {
    }

    public Long getId() {
        return this.id;
    }

    public String getLocationId() {
        return this.locationId;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(id, location.id) && Objects.equals(locationId, location.locationId) && Objects.equals(node, location.node) && Objects.equals(version, location.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, locationId, node, version);
    }
}