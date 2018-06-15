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
//              dclarke - initial JPA Employee example using XML (bug 217884)
//              mbraeuer - annotated version
package org.eclipse.persistence.testing.perf.jpa.model.basic;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

/**
 * This class represent a project that a group of employees work on.
 * This demonstrates JOINED inheritance.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Project implements Serializable {
    @Id
    @Column(name = "PROJ_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    @Basic
    @Column(name = "PROJ_NAME")
    private String name;
    @Basic
    @Column(name = "DESCRIP")
    private String description;
    @Version
    private long version;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEADER_ID")
    private Employee teamLeader;

    public Project() {
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String descrip) {
        this.description = descrip;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int projId) {
        this.id = projId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String projName) {
        this.name = projName;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Employee getTeamLeader() {
        return this.teamLeader;
    }

    public void setTeamLeader(Employee employee) {
        this.teamLeader = employee;
    }
}
