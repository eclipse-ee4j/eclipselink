/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     11/22/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
package org.eclipse.persistence.testing.models.jpa22.advanced.ddl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="JPA22_FORK_USER")
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
