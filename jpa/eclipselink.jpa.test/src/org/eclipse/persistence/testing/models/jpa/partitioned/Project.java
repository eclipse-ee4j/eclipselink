/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.partitioned;

import java.io.Serializable;
import javax.persistence.*;

import org.eclipse.persistence.annotations.Partitioned;
import org.eclipse.persistence.annotations.RangePartition;
import org.eclipse.persistence.annotations.RangePartitioning;

import static javax.persistence.GenerationType.*;
import static javax.persistence.InheritanceType.*;

/**
 * Employees have a many-to-many relationship with Projects through the
 * projects attribute.
 * Projects refer to Employees through the teamMembers attribute.
 * This class in used to test inheritance.
 * The field names intentionally do not match the property names to test method weaving.
 */
@Entity
@Table(name="PART_PROJECT")
@Inheritance(strategy=JOINED)
@DiscriminatorColumn(name="PROJ_TYPE")
@DiscriminatorValue("P")
@RangePartitioning(
        name="RangePartitioningByPROJ_ID",
        partitionColumn=@Column(name="PROJ_ID"),
        partitionValueType=Integer.class,
        unionUnpartitionableQueries=true,
        partitions={
            @RangePartition(connectionPool="default", startValue="0", endValue="1000"),
            @RangePartition(connectionPool="node2", startValue="1000", endValue="2000"),
            @RangePartition(connectionPool="node3", startValue="2000")
        })
@Partitioned("RangePartitioningByPROJ_ID")
public class Project implements Serializable {

    @Id
    @GeneratedValue(strategy=TABLE)
    @Column(name="PROJ_ID")
    private Integer id;

    @Version
    @Column(name="VERSION")
    private int version;

    @Column(name="PROJ_NAME")
    private String name;

    @Column(name="DESCRIP")
    private String description;

    @OneToOne
    @JoinColumns({
        @JoinColumn(name="LEADER_ID", referencedColumnName = "EMP_ID"),
        @JoinColumn(name="LEADER_LOCATION", referencedColumnName = "LOCATION")})
    protected Employee teamLeader;

    public Project () {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Employee getTeamLeader() {
        return teamLeader;
    }

    public void setTeamLeader(Employee teamLeader) {
        this.teamLeader = teamLeader;
    }

    public String displayString() {
        StringBuffer sbuff = new StringBuffer();
        sbuff.append("Project ").append(getId()).append(": ").append(getName()).append(", ").append(getDescription());

        return sbuff.toString();
    }
}
