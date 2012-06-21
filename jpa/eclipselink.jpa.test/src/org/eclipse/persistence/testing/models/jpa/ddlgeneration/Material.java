/*******************************************************************************
 * Copyright (c) 2011 Adrian Goerler. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/06/2011-2.3 Guy Pelletier for Adrian Goerler 
 *       - 312244: can't map optional one-to-one relationship using @PrimaryKeyJoinColumn
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name="MATERIAL")
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
