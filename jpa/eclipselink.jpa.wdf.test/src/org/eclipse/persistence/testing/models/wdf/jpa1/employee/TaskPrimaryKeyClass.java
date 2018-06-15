/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.io.Serializable;

public class TaskPrimaryKeyClass implements Serializable {

    private static final long serialVersionUID = -6089032110670569822L;
    Integer projectId;
    int taskId;

    public TaskPrimaryKeyClass() {
    }

    public TaskPrimaryKeyClass(Integer projectId, int taskId) {
        this.projectId = projectId;
        this.taskId = taskId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TaskPrimaryKeyClass)) {
            return false;
        }

        TaskPrimaryKeyClass otherKey = ((TaskPrimaryKeyClass) other);
        if (taskId == otherKey.taskId && projectId.equals(otherKey.projectId)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(taskId).hashCode() + 17 * projectId.hashCode();
    }
}
