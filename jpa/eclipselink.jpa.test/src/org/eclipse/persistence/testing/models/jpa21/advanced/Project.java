/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/    
package org.eclipse.persistence.testing.models.jpa21.advanced;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Version;

import static javax.persistence.GenerationType.SEQUENCE;
import static javax.persistence.InheritanceType.JOINED;

@Entity
@Table(name="JPA21_PROJECT")
@Inheritance(strategy=JOINED)
@DiscriminatorColumn(name="PROJ_TYPE")
@DiscriminatorValue("P")
@SqlResultSetMapping(
    name = "ProjectResultSetMapping",
    columns = {
        @ColumnResult(name = "BUDGET_SUM")
    },
    entities = {
        @EntityResult(
            entityClass = Project.class
        ),
        @EntityResult(
            entityClass = SmallProject.class,
            fields = {
                @FieldResult(name = "id", column = "SMALL_ID"),
                @FieldResult(name = "name", column = "SMALL_NAME"),
                @FieldResult(name = "description", column = "SMALL_DESCRIPTION"),
                @FieldResult(name = "teamLeader", column = "SMALL_TEAMLEAD"),
                @FieldResult(name = "version", column = "SMALL_VERSION")
            },
            discriminatorColumn="SMALL_DESCRIM"
         )
     }
)
public class Project implements Serializable {
    public int pre_update_count = 0;
    public int post_update_count = 0;
    public int pre_remove_count = 0;
    public int post_remove_count = 0;
    public int pre_persist_count = 0;
    public int post_persist_count = 0;
    public int post_load_count = 0;
    
    private Integer m_id;
    private int m_version;
    private String m_name;
    private String m_description;
    private Employee m_teamLeader;
    private Collection<Employee> m_teamMembers;
    private List<String> m_properties;

    public Project () {
        m_teamMembers = new Vector<Employee>();
        m_properties = new ArrayList<String>();
    }

    public void addTeamMember(Employee employee) {
        getTeamMembers().add(employee);
    }
    
    @Column(name="DESCRIP")
    public String getDescription() { 
        return m_description; 
    }
    
    @Id
    @GeneratedValue(strategy=SEQUENCE, generator="PROJECT_SEQUENCE_GENERATOR")
    @SequenceGenerator(name="PROJECT_SEQUENCE_GENERATOR", sequenceName="PROJECT_SEQ", allocationSize=10)
    @Column(name="PROJ_ID", length=37)
    public Integer getId() { 
        return m_id; 
    }

    @Column(name="PROJ_NAME")
    public String getName() { 
        return m_name; 
    }
    
    @ElementCollection
    @Column(name="PROPS")
    @CollectionTable(name="JPA21_PROJ_PROPS")
    public List<String> getProperties() { 
        return m_properties; 
    }
    
    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name="LEADER_ID")
    public Employee getTeamLeader() {
        return m_teamLeader; 
    }
    
    @ManyToMany(targetEntity=Employee.class, mappedBy="projects")
    public Collection getTeamMembers() { 
        return m_teamMembers; 
    }
    
    @Version
    @Column(name="VERSION")
    public int getVersion() { 
        return m_version; 
    }

    @PostLoad
    public void postLoad() {
        ++post_load_count;
    }
    
    @PostPersist
    public void postPersist() {
        ++post_persist_count;
    }

    @PostRemove
    public void postRemove() {
        ++post_remove_count;
    }
    
    @PostUpdate
    public void postUpdate() {
        ++post_update_count;
    }
    
    @PrePersist
    public void prePersist() {
        ++pre_persist_count;
    }
    
    @PreRemove
    public void preRemove() {
        ++pre_remove_count;
    }

    @PreUpdate
    public void preUpdate() {
        ++pre_update_count;
    }
    
    public void removeTeamMember(Employee employee) {
        getTeamMembers().remove(employee);
    }
    
    public void setDescription(String description) { 
        this.m_description = description; 
    }
    
    public void setId(Integer id) { 
        this.m_id = id; 
    }
    
    public void setName(String name) { 
        this.m_name = name; 
    }
    
    public void setProperties(List<String> properties) { 
        this.m_properties = properties; 
    }
    
    public void setTeamLeader(Employee teamLeader) { 
        this.m_teamLeader = teamLeader; 
    }
    
    public void setTeamMembers(Collection<Employee> employees) {
        this.m_teamMembers = employees;
    }
    
    protected void setVersion(int version) { 
        this.m_version = version; 
    }   
}
