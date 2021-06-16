/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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

