/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     09/09/2011-2.3.1 Peter Krogh
//       - 356197: Add new VPD type to MultitenantType
//     11/15/2011-2.3.2 Guy Pelletier
//       - 363820: Issue with clone method from VPDMultitenantPolicy
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.MultitenantType;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;

import static javax.persistence.InheritanceType.SINGLE_TABLE;
import static org.eclipse.persistence.annotations.MultitenantType.VPD;

/**
 * A multitenant VPD class. Do not add @Entity to this class as it will be
 * picked up by the 'default' testing PU. We do not want this to happen
 * as any multitenant VPD entity will mark the connection mode to exclusive
 * always. We want this class to remain exclusive to the multi-tenant-vpd PU.
 *
 * Don't add an Entity annotation to this class as we don't want this class to
 * be picked up from other test persistence unit classes that do not exclude
 * unlisted classes.
 *
 * @see Related mapping file:
 * trunk\jpa\eclipselink.jpa.test\resource\eclipselink-annotation-model\multitenant-vpd.xml
 *
 * Multi-tenant to do list.
 * Each user can see its own list of tasks.
 * Each task has a set of subtasks.
 *
 * @author pkrogh
 * @since EclipseLink 2.3.1
 */
@Table(name="VPD_TASK")
@Multitenant(VPD)
@Inheritance(strategy=SINGLE_TABLE)
@DiscriminatorColumn(name="DTYPE")
@DiscriminatorValue("TASK")
@TenantDiscriminatorColumn(name = "TENANT_ID", contextProperty = "tenant.id")
@Cacheable(false)
public class Task implements Serializable {
    @Id
    @GeneratedValue
    private int id;

    @Column(name="DESCRIP")
    private String description;

    private boolean completed;

    @OneToMany(mappedBy="parent")
    private List<Task> subtasks= new ArrayList<Task>();

    @OneToOne
    @JoinColumn(name="PARENT_ID")
    public Task parent;

    public void addSubtask(Task task) {
        task.parent=this;
        this.subtasks.add(task);
    }

    public boolean getCompleted() {
        return completed;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public List<Task> getSubtasks() {
        return subtasks;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int empId) {
        this.id = empId;
    }

    public String toString() {
        String printString = "Task(id: " + getId() + " -- " + getDescription();

        if (completed) {
            return "Completed " + printString + ")";
        } else {
            return "Incomplete " + printString + ")";
        }
    }
}
