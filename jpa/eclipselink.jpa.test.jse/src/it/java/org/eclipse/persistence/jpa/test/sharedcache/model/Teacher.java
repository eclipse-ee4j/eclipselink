/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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
//     04/19/2020-3.0 Alexandre Jacob
//        - 544202: Fixed refresh of foreign key values when cacheKey was invalidated

package org.eclipse.persistence.jpa.test.sharedcache.model;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.Noncacheable;
import org.eclipse.persistence.config.CacheIsolationType;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
@Cacheable(value = true)
@Cache(isolation = CacheIsolationType.SHARED)
public class Teacher {

    @Id
    private int id;

    private String name;

    @Noncacheable
    @ManyToOne
    private Student student;

    public Teacher() {

    }

    public Teacher(int id, String name, Student student) {
        this.id = id;
        this.name = name;
        this.student = student;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
