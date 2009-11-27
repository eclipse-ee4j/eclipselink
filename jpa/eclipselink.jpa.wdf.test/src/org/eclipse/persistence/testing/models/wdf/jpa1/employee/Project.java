/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Cacheable(true)
@Entity
@Table(name = "TMP_PROJECT")
@SecondaryTable(name = "TMP_PROJECT_DETAILS", pkJoinColumns = @PrimaryKeyJoinColumn(name = "PROJECT_ID", referencedColumnName = "ID"))
@TableGenerator(name = "IdGenerator", table = "TMP_GENERATOR", pkColumnName = "BEAN_NAME", valueColumnName = "MAX_ID")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    public Project(String aName) {
        // generated id
        projId = null;
        name = aName;
        plannedDays = 1;
        usedDays = 0;
    }

    private Integer projId;

    // private int version;

    private String name;

    private int plannedDays;

    private int usedDays;

    @SuppressWarnings("unchecked")
    private Set employees;

    private List<Task> tasks;

    public Project() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "IdGenerator")
    @Column(name = "ID")
    public Integer getId() {
        return projId;
    }

    protected void setId(Integer id) {
        projId = id;
    }

    // // @Version
    // public int getVersion() {
    // return version;
    // }
    //
    // protected void setVersion(int version) {
    // this.version = version;
    // }

    @Basic
    @Column(name = "PROJ_NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "PLANNED_DAYS", table = "TMP_PROJECT_DETAILS")
    public int getPlannedDays() {
        return plannedDays;
    }

    public void setPlannedDays(int plannedDays) {
        this.plannedDays = plannedDays;
    }

    @Basic
    @Column(name = "USED_DAYS", table = "TMP_PROJECT_DETAILS")
    public int getUsedDays() {
        return usedDays;
    }

    public void setUsedDays(int usedDays) {
        this.usedDays = usedDays;
    }

    @SuppressWarnings("unchecked")
    @ManyToMany(mappedBy = "projects", targetEntity = Employee.class)
    public Set getEmployees() {
        return employees;
    }

    @SuppressWarnings("unchecked")
    public void setEmployees(Set employees) {
        this.employees = employees;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Project) {
            Project other = (Project) obj;
            return other.name.equals(name);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (name == null) {
            return 0;
        }
        return name.hashCode();
    }

    @SuppressWarnings("unchecked")
    public void addEmployee(Employee employee) {
        if (employees == null) {
            employees = new HashSet();
        }

        if (employee.projects == null) {
            employee.projects = new HashSet<Project>();
        }
        employee.projects.add(this);
    }

    public void addTask(Task task) {
        if (tasks == null) {
            tasks = new ArrayList<Task>();
        }
        tasks.add(task);
    }
}
