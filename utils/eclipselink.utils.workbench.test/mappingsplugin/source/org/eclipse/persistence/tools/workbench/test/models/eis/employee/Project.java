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
package org.eclipse.persistence.tools.workbench.test.models.eis.employee;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;


public class Project implements Serializable
{
    public BigDecimal id;
    public String name;
    public String description;
    public String version;
    public Date endDate;
    public ValueHolderInterface teamLeader;
    public Collection teamMembers;

    public Project() {
        this.name = "";
        this.description = "";
        this.teamLeader = new ValueHolder();
        this.teamMembers = new Vector();
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
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

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date date) {
        this.endDate = date;
    }

    public Employee getTeamLeader() {
        return (Employee) this.teamLeader.getValue();
    }

    public void setTeamLeader(Employee teamLeader) {
        this.teamLeader.setValue(teamLeader);
    }

    public Iterator teamMembers() {
        return this.teamMembers.iterator();
    }

    public void addTeamMember(Employee teamMember) {
        this.teamMembers.add(teamMember);
    }

    public void removeTeamMember(Employee teamMember) {
        this.teamMembers.remove(teamMember);
    }
}
