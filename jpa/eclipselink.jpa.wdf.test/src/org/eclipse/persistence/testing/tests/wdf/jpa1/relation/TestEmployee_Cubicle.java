/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.tests.wdf.jpa1.relation;

import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Cubicle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestEmployee_Cubicle extends JPA1Base {

    @Override
    public void setup() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Department dep = new Department(9, "neun");
            Employee emp = new Employee(5, "first", "last", dep);
            Cubicle cub = new Cubicle(new Integer(3), new Integer(4), "red", emp);
            emp.setCubicle(cub);
            em.persist(dep);
            em.persist(emp);
            em.persist(cub);
            em.flush();
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testRelationToCompositeKey() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            Employee employee = em.find(Employee.class, new Integer(5));
            verify(employee.getId() == 5, "wrong employee");
            verify(employee.getCubicle() != null, "cubicle is null");
            verify(employee.getCubicle().getFloor() != null, "floor is null");
            verify(employee.getCubicle().getFloor().intValue() == 3, "wrong floor");
            verify(employee.getCubicle().getPlace() != null, "place is null");
            verify(employee.getCubicle().getPlace().intValue() == 4, "wrong place");
            verify(employee.getDepartment() != null, "department is null");
            verify(employee.getDepartment().getId() == 9, "wrong department");
        } finally {
            closeEntityManager(em);
        }
    }
}
