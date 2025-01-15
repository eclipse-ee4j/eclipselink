/*
 * Copyright (c) 2012, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     11/22/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
package org.eclipse.persistence.testing.models.jpa21.advanced.ddl;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="JPA21_FORK_USER")
public class ForkUser {
    @Id
    @GeneratedValue
    public Integer id;

    public String name;

    @ManyToMany(mappedBy="users")
    public List<Fork> forks;

    public ForkUser() {
        forks = new ArrayList<>();
    }

    public void addFork(Fork fork) {
        forks.add(fork);
    }

    public List<Fork> getForks() {
        return forks;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setForks(List<Fork> forks) {
        this.forks = forks;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
