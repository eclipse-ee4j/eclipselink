/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.partitioned;

import static jakarta.persistence.GenerationType.TABLE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.HashPartitioning;
import org.eclipse.persistence.annotations.Partitioned;
import org.eclipse.persistence.annotations.UnionPartitioning;

@Entity
@Table(name = "PART_OFFICE")
@HashPartitioning(
    name="HashPartitioningByNAME",
    partitionColumn=@Column(name="OFF_NAME"),
    connectionPools={"node2", "node3"})
@Partitioned("HashPartitioningByNAME")
public class Office {

    @Id
    @Column(name="OFF_ID")
    @GeneratedValue(strategy=TABLE, generator="OFFICE_TABLE_GENERATOR")
    protected int id;
    @Column(name="OFF_NUMBER")
    protected int number;
    @Column(name="OFF_NAME")
    protected String name;

    public Office() {
        this(null, 0);
    }

    public Office(String name, int number) {
        super();
        setName(name);
        setNumber(number);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
