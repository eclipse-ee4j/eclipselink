/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     04/12/2013-2.5 Guy Pelletier
//       - 405640: JPA 2.1 schema generation drop operation fails to include dropping defaulted fk constraints.
package org.eclipse.persistence.testing.models.jpa21.advanced.ddl;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

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
