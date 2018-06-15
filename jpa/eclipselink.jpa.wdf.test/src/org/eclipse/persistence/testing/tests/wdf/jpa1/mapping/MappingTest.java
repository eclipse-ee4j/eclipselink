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

package org.eclipse.persistence.testing.tests.wdf.jpa1.mapping;

import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Project;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Task;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.TaskPrimaryKeyClass;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class MappingTest extends JPA1Base {

    @Test
    public void testColumnMappedTwice() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Project project = new Project("photo album");
            em.persist(project);
            Integer projectId = project.getId();
            int taskId = 1;
            Task task = new Task(projectId, taskId);
            project.addTask(task);
            task.setProject(project);
            env.commitTransaction(em);
            em.clear();
            env.beginTransaction(em);
            project = em.find(Project.class, projectId);
            verify(project != null, "project not found");
            List<Task> tasks = project.getTasks();
            verify(tasks.size() == 1, "wrong number of tasks:" + tasks.size());
            if (tasks.size() == 1) {
                task = tasks.get(0);
                verify(projectId.equals(task.getProjectId()), "task has wrong projectId: expected " + projectId + ", got "
                        + task.getProjectId());
                verify(task.getTaskId() == taskId, "task has wrong taskId: " + task.getTaskId());
            }
            task.setDescription("collect pictures");
            env.commitTransaction(em);
            em.clear();
            task = em.find(Task.class, new TaskPrimaryKeyClass(projectId, taskId));
            verify("collect pictures".equals(task.getDescription()), "wrong description: " + task.getDescription());
        } finally {
            closeEntityManager(em);
        }
    }
}
