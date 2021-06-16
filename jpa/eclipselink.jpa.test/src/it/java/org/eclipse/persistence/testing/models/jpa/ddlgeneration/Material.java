/*
 * Copyright (c) 2011, 2020 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name="DDL_MATERIAL")
public class Material {
    private long courseId;
    private Course course;

    @OneToOne(mappedBy="material")
    public Course getCourse() {
        return course;
    }

    @Id
    @Column(name="COURSE_ID")
    public long getCourseId() {
        return courseId;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    @PrePersist
    public void prePersist() {
        courseId = course.getCourseId();
    }
}
