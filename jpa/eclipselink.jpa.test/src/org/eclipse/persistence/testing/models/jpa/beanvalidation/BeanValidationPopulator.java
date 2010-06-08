/*******************************************************************************
 * Copyright (c) 2009 Sun Microsystems, Inc. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.beanvalidation;

import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class BeanValidationPopulator {
    public BeanValidationPopulator() {
    }
    public static final int EMPLOYEE_PK = 1;
    public static final int PROJECT_PK = 1;

    public static void createEmployeeProject(EntityManager em)
    {
        EntityTransaction tx = em.getTransaction();


        tx.begin();
        Employee e1 = new Employee(EMPLOYEE_PK, getFilledStringOfLength(Employee.NAME_MAX_SIZSE - 1), 1000);
        Project p1 = new Project(PROJECT_PK, "proj");
        Project p2 = new Project(PROJECT_PK + 1, "proj");
        em.persist(p1);
        em.persist(p2);

        Collection<Project> projects = new ArrayList<Project>();
        projects.add(p1);
        projects.add(p2);
        e1.setProjects(projects);
        e1.setManagedProject(p1);

        em.persist(e1);

        tx.commit();
    }

    public static String getFilledStringOfLength(int length) {
        char[] stringChars = new char[length];
        Arrays.fill(stringChars, 'a');
        return new String(stringChars);
    }


}