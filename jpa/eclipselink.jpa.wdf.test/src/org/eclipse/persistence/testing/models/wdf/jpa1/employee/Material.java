/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "TMP_MATERIAL")
public class Material {
    private long m_id;

    private byte[] data;

    private Course course;

    @Id
    @Column(name = "COURSE_ID")
    public long getCourseId() {
        return m_id;
    }

    @Deprecated
    // for use by the entity manager only
    protected void setCourseId(long id) {
        m_id = id;
    }

    @Lob
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @OneToOne(mappedBy = "material")
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @PrePersist
    public void prePersist() {
        m_id = course.getCourseId();
    }

}
