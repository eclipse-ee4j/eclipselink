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
package org.eclipse.persistence.testing.models.inheritance;

import java.util.*;
import java.io.StringWriter;
import org.eclipse.persistence.indirection.*;

/**
 *  Designed to test cyclical relationships with inheritance.  Has two relationships
 *  to BaseProject which has a subclass.  These relationships have back pointers which
 *  create cycles.
 */
public class ProjectWorker {
    private long p_id;
    private String p_name;
    private ValueHolderInterface projects;
    private ValueHolderInterface headProject;

    public ProjectWorker() {
        this.p_name = "";
        this.headProject = new ValueHolder();
        this.projects = new ValueHolder(new Vector());
    }

    public void addProject(BaseProject project) {
        getProjects().addElement(project);
        project.setTeamLeader(this);
    }

    public BaseProject getHeadProject() {
        return (BaseProject)headProject.getValue();
    }

    public String getName() {
        return p_name;
    }

    public long getId() {
        return p_id;
    }

    public Vector getProjects() {
        return (Vector)projects.getValue();
    }

    public void removeProject(BaseProject project) {
        getProjects().removeElement(project);
    }

    public void setHeadProject(BaseProject project) {
        this.headProject.setValue(project);
    }

    public void setName(String a_name) {
        this.p_name = a_name;
    }

    public void setId(long id) {
        this.p_id = id;
    }

    public void setProjects(Vector projects) {
        this.projects.setValue(projects);
    }

    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("Person: ");
        writer.write(getName());
        return writer.toString();
    }
}
