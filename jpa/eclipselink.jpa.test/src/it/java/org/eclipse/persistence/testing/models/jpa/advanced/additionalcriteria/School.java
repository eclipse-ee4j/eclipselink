/*
 * Copyright (c) 2011, 2020 Oracle and/or its affiliates. All rights reserved.
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
//     10/15/2010-2.2 Guy Pelletier
//       - 322008: Improve usability of additional criteria applied to queries at the session/EM
package org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.AdditionalCriteria;

import static jakarta.persistence.CascadeType.PERSIST;

@Entity
@Table(name="JPA_AC_SCHOOL")
@AdditionalCriteria("this.name LIKE 'Ottawa%'")
// A named native query will not append the additional criteria.
@NamedNativeQuery(
    name="findAllSQLSchools",
    query="select * from JPA_AC_SCHOOL",
    resultClass=org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria.School.class
)
@NamedQuery(
    name="findJPQLSchools",
    query="SELECT s from School s"
)
public class School {
    @Id
    @GeneratedValue(generator="AC_SCHOOL_SEQ")
    @SequenceGenerator(name="AC_SCHOOL_SEQ", allocationSize=25)
    public Integer id;

    @Basic
    public String name;

    @OneToMany(mappedBy="school", cascade=PERSIST)
    public List<Student> students;

    public School() {
        students = new ArrayList<Student>();
    }

    public void addStudent(Student student) {
        students.add(student);
        student.setSchool(this);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
