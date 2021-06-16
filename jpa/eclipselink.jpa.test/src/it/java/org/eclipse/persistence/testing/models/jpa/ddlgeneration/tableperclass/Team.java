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
//     11/25/2009-2.0 Guy Pelletier
//       - 288955: EclipseLink 2.0.0.v20090821-r4934 (M7) throws EclipseLink-80/41 exceptions if InheritanceType.TABLE_PER_CLASS is used
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass;

import java.util.ArrayList;
import java.util.Collection;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import static jakarta.persistence.GenerationType.TABLE;
import static jakarta.persistence.InheritanceType.TABLE_PER_CLASS;

@Entity
@Inheritance(strategy=TABLE_PER_CLASS)
@Table(name="JPA_TEAM")
public class Team {
    @Id
    @GeneratedValue(strategy=TABLE)
    public int id;

    public String name;

    @ManyToMany(mappedBy="teams")
    private Collection<Player> players;

    public Team(){
        players = new ArrayList<Player>();
    }
}

