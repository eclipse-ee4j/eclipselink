/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.partitioned;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.eclipse.persistence.annotations.HashPartitioning;
import org.eclipse.persistence.annotations.Partitioned;

@Entity
@Table(name = "PART_OFFICE")
@HashPartitioning(
    name="HashPartitioningByNAME",
    partitionColumn=@Column(name="OFF_NAME"),
    connectionPools={"node2", "node3"})
@Partitioned("HashPartitioningByNAME")
public class Office {

    @Column(name="OFF_NUMBER")
    protected int number;

    @Id
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
