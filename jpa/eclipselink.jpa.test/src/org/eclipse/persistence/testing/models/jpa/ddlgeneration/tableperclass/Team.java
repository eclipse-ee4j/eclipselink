/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     11/25/2009-2.0 Guy Pelletier
//       - 288955: EclipseLink 2.0.0.v20090821-r4934 (M7) throws EclipseLink-80/41 exceptions if InheritanceType.TABLE_PER_CLASS is used
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import static javax.persistence.GenerationType.TABLE;
import static javax.persistence.InheritanceType.TABLE_PER_CLASS;

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

