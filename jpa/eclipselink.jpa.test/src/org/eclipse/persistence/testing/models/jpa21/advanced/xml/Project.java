/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     01/23/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
package org.eclipse.persistence.testing.models.jpa21.advanced.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

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
    private List<String> properties;

    public Project () {
        teamMembers = new Vector<Employee>();
        properties = new ArrayList<String>();
    }

    public void addTeamMember(Employee employee) {
        getTeamMembers().add(employee);
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getProperties() {
        return properties;
    }

    public Employee getTeamLeader() {
        return teamLeader;
    }

    public Collection getTeamMembers() {
        return teamMembers;
    }

    public int getVersion() {
        return version;
    }

    public void postLoad() {
        ++post_load_count;
    }

    public void postPersist() {
        ++post_persist_count;
    }

    public void postRemove() {
        ++post_remove_count;
    }

    public void postUpdate() {
        ++post_update_count;
    }

    public void prePersist() {
        ++pre_persist_count;
    }

    public void preRemove() {
        ++pre_remove_count;
    }

    public void preUpdate() {
        ++pre_update_count;
    }

    public void removeTeamMember(Employee employee) {
        getTeamMembers().remove(employee);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    public void setTeamLeader(Employee teamLeader) {
        this.teamLeader = teamLeader;
    }

    public void setTeamMembers(Collection<Employee> employees) {
        this.teamMembers = employees;
    }

    protected void setVersion(int version) {
        this.version = version;
    }
}
