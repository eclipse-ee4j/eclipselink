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
package org.eclipse.persistence.testing.models.onetoonejointable;

import java.math.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.descriptors.changetracking.*;

/**
 * <b>Purpose</b>: Abstract superclass for Large & Small projects in Employee Demo
 * <p><b>Description</b>:     Project is an example of an abstract superclass. It demonstrates how class inheritance can be mapped to database tables.
 * It's subclasses are concrete and may or may not add columns through additional tables. The PROJ_TYPE field in the
 * database table indicates which subclass to instantiate. Projects are involved in a M:M relationship with employees.
 * The Employee classs maintains the definition of the relation table.
 * @see LargeProject
 * @see SmallProject
 */
public abstract class Project implements Serializable, ChangeTracker {
    // implements ChangeTracker for testing
    public BigDecimal id;
    public String name;
    public String description;
    public ValueHolderInterface teamLeader;
    public PropertyChangeListener listener;
    public List<Employee> employees;

    public PropertyChangeListener _persistence_getPropertyChangeListener() {
        return listener;
    }

    public void _persistence_setPropertyChangeListener(PropertyChangeListener listener) {
        this.listener = listener;
    }

    public void propertyChange(String propertyName, Object oldValue, Object newValue) {
        if (listener != null) {
            if (oldValue != newValue) {
                listener.propertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
            }
        }
    }

    public void collectionChange(String propertyName, Object oldValue, Object newValue, int changeType, boolean isChangeApplied) {
        if (listener != null) {
            listener.propertyChange(new CollectionChangeEvent(this, propertyName, oldValue, newValue, changeType, isChangeApplied));
        }
    }

    public Project() {
        this.name = "";
        this.description = "";
        this.teamLeader = new ValueHolder();
        this.employees = new ArrayList();
    }

    public Project(String name) {
        this();
        this.name = name;
    }

    public void addEmployee(Employee employee) {
        getEmployees().add(employee);
        employee.getProjects().add(this);
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

    public List<Employee> getEmployees() {
        return employees;
    }

    public String getName() {
        return name;
    }

    public Employee getTeamLeader() {
        return (Employee)teamLeader.getValue();
    }

    public void removeEmployee(Employee employee) {
        getEmployees().remove(employee);
        employee.getProjects().remove(this);
    }

    public void setDescription(String description) {
        propertyChange("description", this.description, description);
        this.description = description;
    }

    /**
     * Set the persistent identifier of the receiver.
     */
    public void setId(BigDecimal id) {
        propertyChange("id", this.id, id);
        this.id = id;
    }

    public void setName(String name) {
        propertyChange("name", this.name, name);
        this.name = name;
    }

    public void setTeamLeader(Employee teamLeader) {
        propertyChange("teamLeader", this.teamLeader.getValue(), teamLeader);
        this.teamLeader.setValue(teamLeader);
    }

    public void setEmployees(List<Employee> employees) {
        propertyChange("employees", this.employees, employees);
        this.employees = employees;
    }
}
