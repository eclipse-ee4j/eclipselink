/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2011, 2015 Adrian Goerler. All rights reserved.
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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

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

