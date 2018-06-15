/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.tests.wdf.jpa1.relation;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Course;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestPrimaryKeyJoinColumn extends JPA1Base {

    @Test
    public void testPersistCourseWithoutMaterial() {

        JPAEnvironment env = getEnvironment();
        Course course = new Course();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            em.persist(course);
            env.commitTransactionAndClear(em);
            long id = course.getCourseId();

            assertNotNull(em.find(Course.class, id));
        } finally {
            if (env.isTransactionActive(em)) {
                env.rollbackTransactionAndClear(em);
            }
        }


    }

}
