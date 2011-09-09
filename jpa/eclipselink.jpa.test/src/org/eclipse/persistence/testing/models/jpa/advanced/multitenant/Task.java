/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     09/09/2011-2.3.1 Peter Krogh 
 *       - 356197: Add new VPD type to MultitenantType 
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.MultitenantType;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;

import static org.eclipse.persistence.annotations.MultitenantType.VPD;

/**
 * A multitenant VPD class. Do not add @Entity to this class as it will be 
 * picked up by the 'default' testing PU. We do not want this to happen
 * as any multitenant VPD entity will mark the connection mode to exclusive
 * always. We want this class to remain exclusive to the multi-tenant-vpd PU.
 * 
 * Related mapping file: 
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
