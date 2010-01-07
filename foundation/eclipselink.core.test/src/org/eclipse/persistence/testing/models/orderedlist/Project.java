/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/05/2009 Andrei Ilitchev 
 *       - JPA 2.0 - OrderedList support.
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.orderedlist;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.helper.Helper;
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
public abstract class Project implements ChangeTracker {
    public BigDecimal id;
    public String name;
    public String description;
    public ValueHolderInterface teamLeader;
    public List<Employee> employees;
    public PropertyChangeListener listener;

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

    public void collectionChange(String propertyName, Object oldValue, Object newValue, int changeType) {
        if (listener != null) {
            listener.propertyChange(new CollectionChangeEvent(this, propertyName, oldValue, newValue, changeType));
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

    public Employee getTeamLeader() {
        return (Employee)teamLeader.getValue();
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
    
    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write(Helper.getShortClassName(this) + ": ");
        writer.write(getName());
        return writer.toString();
    }
}
