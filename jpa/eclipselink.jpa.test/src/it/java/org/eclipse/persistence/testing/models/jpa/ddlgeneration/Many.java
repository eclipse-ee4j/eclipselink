/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     01/07/2010-2.0.1 Laird Nelson
//       - 282075: DDL generation is unpredictable
//     11/17/2010-2.2.0 Chris Delahunt
//       - 214519: Allow appending strings to CREATE TABLE statements
//     04/04/2013-2.4.3 Guy Pelletier
//       - 388564: Generated DDL does not match annotation
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;

/**
 * This class is only usable within the ddlTableSuffix persistence unit
 */
@Entity(name = "Many")
@Table(name = "m")
public class Many implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", scale = 10, precision = 0, nullable = false)
    private long id;

    @ManyToMany
    @JoinTable(name="DDL_MANY_MANY")
    private List<Many> many;

    @ManyToOne
    @JoinColumn(name = "o", referencedColumnName = "id", columnDefinition = "NUMERIC(10) NULL")
    private One one;

    public Many() {
        super();
    }

    public List<Many> getMany() {
        return many;
    }

    public void setMany(List<Many> many) {
        this.many = many;
    }

    public void setOne(final One one) {
        this.one = one;
    }

    public One getOne() {
        return this.one;
    }
}
