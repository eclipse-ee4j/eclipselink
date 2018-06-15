/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.partitioned;

import static javax.persistence.GenerationType.TABLE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
