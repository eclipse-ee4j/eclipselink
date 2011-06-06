/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.composite.advanced.member_3;

import java.util.*;
import java.io.Serializable;
import javax.persistence.*;
import static org.eclipse.persistence.annotations.ExistenceType.CHECK_CACHE;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.GenerationType.*;
import static javax.persistence.InheritanceType.*;

import org.eclipse.persistence.annotations.CompositeMember;
import org.eclipse.persistence.annotations.ExistenceChecking;
import org.eclipse.persistence.testing.models.jpa.composite.advanced.member_2.Employee;

/**
 * Employees have a many-to-many relationship with Projects through the
 * projects attribute.
 * Projects refer to Employees through the teamMembers attribute.
 * This class in used to test inheritance.
 * The field names intentionally do not match the property names to test method weaving.
 */
@SuppressWarnings("deprecation")
@Entity
@Table(name="MBR3_PROJECT")
@Inheritance(strategy=JOINED)
@DiscriminatorColumn(name="PROJ_TYPE")
@DiscriminatorValue("P")
@NamedQuery(
	name="findProjectByName",
	query="SELECT OBJECT(project) FROM Project project WHERE project.name = :name"
)
@ExistenceChecking(CHECK_CACHE)
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
    protected Employee m_teamLeader;
    private Collection<Employee> m_teamMembers;
    private List<String> m_properties;

    public Project () {
        m_teamMembers = new Vector<Employee>();
        m_properties = new ArrayList<String>();
    }

    @Id
    @GeneratedValue(strategy=SEQUENCE, generator="BR3_PROJECT_SEQUENCE_GENERATOR")
    @SequenceGenerator(name="BR3_PROJECT_SEQUENCE_GENERATOR", sequenceName="PROJECT_SEQ", allocationSize=10)
    @Column(name="PROJ_ID", length=37)
    public Integer getId() { 
        return m_id; 
    }
    
    public void setId(Integer id) { 
        this.m_id = id; 
    }

    @Version
    @Column(name="VERSION")
    public int getVersion() { 
        return m_version; 
    }
    
    protected void setVersion(int version) { 
        this.m_version = version; 
    }

    @Column(name="PROJ_NAME")
    public String getName() { 
        return m_name; 
    }
    
    public void setName(String name) { 
        this.m_name = name; 
    }

    @Column(name="DESCRIP")
    public String getDescription() { 
        return m_description; 
    }
    
    public void setDescription(String description) { 
        this.m_description = description; 
    }


    @ElementCollection()
    @CollectionTable(name="MBR1_PROJ_PROPS", joinColumns=@JoinColumn(name="PROJ_ID"))
    @CompositeMember("composite-advanced-member_1")
    @Column(name="PROPS")
    public List<String> getProperties() { 
        return m_properties; 
    }
    
    public void setProperties(List<String> properties) { 
        this.m_properties = properties; 
    }
    
    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name="LEADER_ID")
    public Employee getTeamLeader() {
        return m_teamLeader; 
    }
    
    public void setTeamLeader(Employee teamLeader) { 
        this.m_teamLeader = teamLeader; 
    }

    // @ManyToMany(targetEntity=Employee.class, mappedBy="projects")
    /* If a mapping is between two different composite members uses JoinTable,
     * then the relationship must be unidirectional - the "invert" mapping cannot use "mappedBy".
     * That's because JoinTable must be defined in the same composite member with target entity:
     * master mapping requires it to be in slave's member, slave mapping - in master's.
     * 
     * Workaround is to define independent invert mapping with its own JoinTable.
     * If user maintains both sides of the relationship then the two join tables will be in sync.
     * 
     * JoinTable MBR3_EMP_PROJ specified by Employee.projects defined in the same composite member with Project.
     * This invert mapping specifies its own join table MBR2_PROJ_EMP defined in the same composite member with Employee.
     */ 
    @ManyToMany()
    @JoinTable(
        name="MBR2_PROJ_EMP",
        joinColumns=@JoinColumn(name="PROJECTS_PROJ_ID", referencedColumnName="PROJ_ID")
    )
    public Collection<Employee> getTeamMembers() { 
        return m_teamMembers; 
    }
    
    public void setTeamMembers(Collection<Employee> employees) {
        this.m_teamMembers = employees;
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
}
