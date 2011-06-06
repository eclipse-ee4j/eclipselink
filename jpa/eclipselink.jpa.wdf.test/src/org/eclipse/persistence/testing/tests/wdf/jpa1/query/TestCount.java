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

package org.eclipse.persistence.testing.tests.wdf.jpa1.query;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.testing.framework.wdf.Bugzilla;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Cubicle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.CubiclePrimaryKeyClass;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Project;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestCount extends JPA1Base {

    private final Set<Department> ALL_DEPARTMENTS = new HashSet<Department>();
    private final Department dep10 = new Department(10, "ten");
    private final Department dep20 = new Department(20, "twenty");

    @Override
    protected void setup() {
        ALL_DEPARTMENTS.add(dep10);
        ALL_DEPARTMENTS.add(dep20);
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            em.persist(dep10);
            em.persist(dep20);
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            Project eisKratzen = new Project("Eis Kratzen");
            em.persist(eisKratzen);
            Project schneeSchieben = new Project("Schnee Schieben");
            em.persist(schneeSchieben);
            Cubicle cubicle = new Cubicle(new CubiclePrimaryKeyClass(Integer.valueOf(1), Integer.valueOf(2)), "red-green-gold",
                    null);
            em.persist(cubicle);
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    private void verifyCountResult(String txt, int expected) {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Query query = em.createQuery(txt);
            List<?> result = query.getResultList();
            verify(result.size() == 1, "wrong resultcount");
            Iterator<?> iter = result.iterator();
            verify(iter.hasNext(), "no row found");
            Number count = (Number) iter.next();
            assertEquals(expected, count.intValue());
            verify(!iter.hasNext(), "too many rows found");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testCount() {
        verifyCountResult("select count(d) from Department d", 2);
        verifyCountResult("select count(distinct d) from Department d", 2);
        verifyCountResult("select count(c) from Cubicle c", 1);
        verifyCountResult("select count(distinct d.name) from Department d", 2);
        verifyCountResult("select distinct count(d) from Department d", 2);
    }
    
    @Test
    public void testCountDistinctCompoundKey() {
        verifyCountResult("select count(distinct c) from Cubicle c", 1);
    }
    

    @Test
    @ToBeInvestigated
    public void testCount0() {
        verifyCountResult("select count(d) from Department d, Project p", 4);
    }

    @Test
    public void testCount1() {
        verifyCountResult("select count(distinct d) from Department d, Project p", 2);
    }

    @Test
    @ToBeInvestigated
    public void testCount2() {
        verifyCountResult("select count(c) from Cubicle c, Project p", 2);
    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors = { DatabaseVendor.OPEN_SQL })
    public void testCountNoOpenSQL() {
        verifyCountResult("select count(d.name) from Department d", 2);
    }
}
