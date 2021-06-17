/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.inheritance;

import java.util.UUID;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="CMP3_SEED")
public class Seed {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected int id;

    @Basic
    @Column(length=64, nullable=false)
    protected String name;

    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    protected SeededFruit seededFruit;

    public Seed() {
        super();
        setName(String.valueOf(UUID.randomUUID()));
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

    public SeededFruit getSeededFruit() {
        return seededFruit;
    }

    public void setSeededFruit(SeededFruit seededFruit) {
        this.seededFruit = seededFruit;
    }

}
