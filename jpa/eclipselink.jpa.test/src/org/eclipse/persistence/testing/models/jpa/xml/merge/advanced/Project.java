/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     07/15/2010-2.2 Guy Pelletier
//       -311395 : Multiple lifecycle callback methods for the same lifecycle event
package org.eclipse.persistence.testing.models.jpa.xml.merge.advanced;

import java.util.*;
import java.io.Serializable;
import javax.persistence.*;
import static javax.persistence.GenerationType.*;
import static javax.persistence.InheritanceType.*;
import static javax.persistence.DiscriminatorType.*;

/**
 * This class is used to test XML and annotation merging. This class is mapped
 * in:
 * eclipselink-xml-merge-model/orm-annotation-merge-advanced-entity-mappings.xml
 *
 * Also there are no automated tests that go along with these models, see the
 * test suite: EntityMappingsMergeAdvancedJUnitTestCase. It tests through
 * inspecting descriptor settings only and by no means does extensive validation
 * of all the metadata and defaults.
 */
@Entity(name = "AnnMergeProject")
@Table(name = "CMP3_ANN_MERGE_PROJECT")
@Inheritance(strategy = JOINED)
@DiscriminatorColumn(name = "ANN_MERGE_PROJ_TYPE", discriminatorType = INTEGER)
@DiscriminatorValue("0")
@NamedQuery(name = "ann_merge_findProjectByName", query = "SELECT OBJECT(p) FROM Project p WHERE p.name = :name")
public class Project implements Serializable {
    public int pre_update_count = 0;
    public int post_update_count = 0;
    public int pre_remove_count = 0;
    public int post_remove_count = 0;
    public int pre_persist_count = 0;
    public int post_persist_count = 0;
    public int post_load_count = 0;

    private Integer id;
    private int version;
    private String name;
    private String description;
    private Employee teamLeader;
    private Collection<Employee> teamMembers;

    public Project() {
        this.teamMembers = new Vector<Employee>();
    }

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "ANN_MERGE_PROJECT_SEQUENCE_GENERATOR")
    @SequenceGenerator(name = "ANN_MERGE_PROJECT_SEQUENCE_GENERATOR", sequenceName = "ANN_MERGE_PROJECT_SEQ", allocationSize = 10)
    @Column(name = "ANN_MERGE_PROJ_ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Version
    @Column(name = "ANN_MERGE_VERSION")
    public int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }

    @Column(name = "ANN_MERGE_PROJ_NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "ANN_MERGE_DESCRIP")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToOne
    @JoinColumn(name = "ANN_MERGE_LEADER_ID")
    public Employee getTeamLeader() {
        return teamLeader;
    }

    public void setTeamLeader(Employee teamLeader) {
        this.teamLeader = teamLeader;
    }

    @ManyToMany(mappedBy = "projects")
    public Collection<Employee> getTeamMembers() {
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

    public void prePersist() {
        ++pre_persist_count;
    }

    @PrePersist
    public void ignoredPrePersistMethod() {
        // This should not throw an exception (multiple pre persist methods)
        // rather this method should just be ignored since the method above
        // prePersist is defined as such in XML.
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
