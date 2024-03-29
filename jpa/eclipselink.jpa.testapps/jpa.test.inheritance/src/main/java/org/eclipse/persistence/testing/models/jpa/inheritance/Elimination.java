/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     12/12/2008-1.1 Guy Pelletier
//       - 249860: Implement table per class inheritance support.
package org.eclipse.persistence.testing.models.jpa.inheritance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.InheritanceType.TABLE_PER_CLASS;

@Entity
@Table(name="TPC_ELIMINATION")
@Inheritance(strategy=TABLE_PER_CLASS)
@IdClass(EliminationPK.class)
public class Elimination {
    @Id
    private Integer id;

    @Id
    private String name;

    @Column(name="DESCRIP")
    private String description;

    @ManyToOne(cascade=PERSIST)
    @JoinColumn(name="ASSASSIN_ID")
    private Assassin assassin;

    public Elimination () {}

    public Assassin getAssassin() {
        return assassin;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isDirectElimination() {
        return false;
    }

    public boolean isElimination() {
        return true;
    }

    public boolean isIndirectElimination() {
        return false;
    }

    public EliminationPK getPK() {
        return new EliminationPK(id, name);
    }

    public void setAssassin(Assassin assassin) {
        this.assassin = assassin;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
