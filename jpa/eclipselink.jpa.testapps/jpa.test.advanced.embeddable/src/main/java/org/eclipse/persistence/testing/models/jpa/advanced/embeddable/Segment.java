/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.models.jpa.advanced.embeddable;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Issue #2245 - test entity with @Embeddable record
@Entity
@Table(name = "CMP3_EMBED_SEGMENT")
public class Segment {

    public Segment(Long id, Point a, Point b) {
        this.id = id;
        this.pointA = a;
        this.pointB = b;
    }

    public Segment() {
        this(-1L, null, null);
    }

    @GeneratedValue
    @Id
    @Column(name = "ID")
    public Long id;

    @Embedded
    @Column(nullable = false)
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "POINTAX")),
            @AttributeOverride(name = "y", column = @Column(name = "POINTAY"))})
    public Point pointA;

    @Embedded
    @Column(nullable = false)
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "POINTBX")),
            @AttributeOverride(name = "y", column = @Column(name = "POINTBY"))})
    public Point pointB;

    @Override
    public String toString() {
        return "Segment#" + id + " (" +
                pointA.x() + ", " + pointA.y() + ") -> (" +
                pointB.x() + ", " + pointB.y() + ")";
    }

}
