/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2011, 2022 Adrian Goerler. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     01/06/2011-2.3 Guy Pelletier for Adrian Goerler
//       - 312244: can't map optional one-to-one relationship using @PrimaryKeyJoinColumn
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name="DDL_COURSE")
public class Course {
    private long courseId;
    private Material material;

    @Id
    @GeneratedValue
    @Column(name="ID")
    public long getCourseId() {
        return courseId;
    }

    @PrimaryKeyJoinColumn
    @OneToOne(cascade=CascadeType.PERSIST, optional=true)
    public Material getMaterial() {
        return material;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}

