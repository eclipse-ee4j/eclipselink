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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.inheritance;

import java.math.*;
import java.io.*;
import org.eclipse.persistence.indirection.*;
import java.util.Vector;

/**
 * STI stands for Single Table Inheritance.
 * STI_Project references and referenced by STI_Employee class,
 * STI_Project is mapped with its subclasses STI_SmallProject and STI_LargeProject
 * to a single table.
 */
public abstract class STI_Project implements Serializable {
    public BigDecimal id;
    public String name;
    public String description;
    public ValueHolderInterface teamLeader;
    public ValueHolderInterface teamMembers;

    public STI_Project() {
        this.name = "";
        this.description = "";
        this.teamLeader = new ValueHolder();
        this.teamMembers = new ValueHolder(new Vector());
    }

    public String getDescription() {
        return description;
    }

    /**
     * Return the persistent identifier of the receiver.
     */
    public BigDecimal getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public STI_Employee getTeamLeader() {
        return (STI_Employee)teamLeader.getValue();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the persistent identifier of the receiver.
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeamLeader(STI_Employee teamLeader) {
        this.teamLeader.setValue(teamLeader);
    }

    public Vector getTeamMembers() {
        return (Vector)teamMembers.getValue();
    }

    public void setTeamMembers(Vector employees) {
        this.teamMembers.setValue(employees);
    }

    public void addTeamMember(STI_Employee employee) {
        getTeamMembers().add(employee);
    }

    public void removeTeamMember(STI_Employee employee) {
        getTeamMembers().remove(employee);
    }
}
