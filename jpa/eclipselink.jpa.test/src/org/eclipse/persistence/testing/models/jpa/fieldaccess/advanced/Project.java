/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.util.*;
import java.io.Serializable;
import javax.persistence.*;
import static javax.persistence.GenerationType.*;
import static javax.persistence.InheritanceType.*;

/**
 * Employees have a many-to-many relationship with Projects through the
 * projects attribute.
 * Projects refer to Employees through the teamMembers attribute.
 */
@Entity(name="Project")
@Table(name="CMP3_FA_PROJECT")
@Inheritance(strategy=JOINED)
@DiscriminatorColumn(name="PROJ_TYPE")
@DiscriminatorValue("P")
@NamedQuery(
	name="findFieldAccessProjectByName",
	query="SELECT OBJECT(project) FROM Project project WHERE project.name = :name"
)
public class Project implements Serializable {
    @Transient
    public int pre_update_count = 0;
    @Transient
    public int post_update_count = 0;
    @Transient
    public int pre_remove_count = 0;
    @Transient
    public int post_remove_count = 0;
    @Transient
    public int pre_persist_count = 0;
    @Transient
    public int post_persist_count = 0;
    @Transient
    public int post_load_count = 0;
    
    @Transient
    public int fieldOnlySetThroughConstructor=0;
    
    @Id
    @GeneratedValue(strategy=SEQUENCE, generator="FA_PROJ_SEQ_GENERATOR")
    @SequenceGenerator(name="FA_PROJ_SEQ_GENERATOR", sequenceName="PROJECT_SEQ", allocationSize=10)
    @Column(name="PROJ_ID")
    private Integer id;
    @Version
    @Column(name="VERSION")
    private int version;
    @Column(name="PROJ_NAME")
    private String name;
    @Column(name="DESCRIP")
    private String description;
    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name="LEADER_ID")
    protected Employee teamLeader;
    @ManyToMany(
        targetEntity=org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee.class,
        mappedBy="projects")
    private Collection<Employee> teamMembers;

    public Project () {
        fieldOnlySetThroughConstructor = 1;
        //setting collections is bad practice as it reduces performance (see 292385)
        this.teamMembers = new Vector<Employee>();
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
    
    public Collection getTeamMembers() { 
        return teamMembers; 
    }
    
    public void setTeamMembers(Collection<Employee> employees) {
            this.teamMembers = employees;
    }

    public void addTeamMember(Employee employee) {
        getTeamMembers().add(employee);
    }

    public void removeTeamMember(Employee employee) {
        getTeamMembers().remove(employee);
    }

    public String displayString() {
        StringBuffer sbuff = new StringBuffer();
        sbuff.append("Project ").append(getId()).append(": ").append(getName()).append(", ").append(getDescription());
        return sbuff.toString();
    }    
   
    @PrePersist
    public void prePersist() {
        ++pre_persist_count;
    }

    @PostPersist
    public void postPersist() {
        ++post_persist_count;
    }

    @PreRemove
    public void preRemove() {
        ++pre_remove_count;
    }

    @PostRemove
    public void postRemove() {
        ++post_remove_count;
    }

    @PreUpdate
    public void preUpdate() {
        ++pre_update_count;
    }

    @PostUpdate
    public void postUpdate() {
        ++post_update_count;
    }

    @PostLoad
    public void postLoad() {
        ++post_load_count;
    }
    
    public int getFieldOnlySetThroughConstructor(){
        return fieldOnlySetThroughConstructor;
    }
}
