/*
 * Copyright (c) 2013, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     04/12/2013-2.5 Guy Pelletier
//       - 405640: JPA 2.1 schema generation drop operation fails to include dropping defaulted fk constraints.
package org.eclipse.persistence.testing.models.jpa21.advanced.ddl;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name="JPA21_DDL_COACH")
public class Coach {
    @Id
    @GeneratedValue
    protected Integer id;

    @ManyToMany(mappedBy="coaches")
    protected List<Runner> runners;

    public Coach() {}


    public Integer getId() {
        return id;
    }

    public List<Runner> getRunners() {
        return runners;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRunners(List<Runner> runners) {
        this.runners = runners;
    }

}
