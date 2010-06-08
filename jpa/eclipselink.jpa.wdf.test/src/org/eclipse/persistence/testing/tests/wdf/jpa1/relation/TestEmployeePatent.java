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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;

import org.junit.Test;

import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Patent;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.PatentId;

import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;

public class TestEmployeePatent extends JPA1Base {
    private static final Integer EDISON = new Integer(26);
    private static final Integer TESLA = new Integer(32);

    @Override
    public void setup() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Department dep = new Department(25, "R&D");
            em.persist(dep);
            em.persist(new Employee(EDISON.intValue(), "Thomas Alva", "Edison", dep));
            em.persist(new Employee(TESLA.intValue(), "Nikola", "Tesla", dep));
            for (int i = 0; i < TEST_DATA.length; i++) {
                em.persist(TEST_DATA[i]);
            }
            em.flush();
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    private static final Patent[] TEST_DATA = new Patent[] {
            new Patent("light bulb", 1879, "artificial light source", java.sql.Date.valueOf("1879-11-05")),
            new Patent("phonograph", 1877, "simple voice recorder", java.sql.Date.valueOf("1877-01-13")),
            new Patent("alternating current", 1888, "alternating current", Date.valueOf("1888-03-17")),
            new Patent("helicopter", 1922, "flying machine", Date.valueOf("1922-11-11")), };

    @Test
    public void testPatentsAsList() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        final List<Patent> testPatents = Arrays.asList(TEST_DATA);
        try {
            env.beginTransaction(em);
            final Employee employee = em.find(Employee.class, EDISON);
            employee.setPatents(testPatents);
            em.flush();
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            final Employee storedEmployee = em.find(Employee.class, EDISON);
            if (null == storedEmployee) {
                flop("test employee vanished.");
            }
            final Collection<Patent> patents = storedEmployee.getPatents();
            verify(patents != null, "patents not stored");
            verify(patents.size() == testPatents.size(), "Wrong number of patents retrieved. Expected: " + testPatents.size()
                    + ", but got: " + patents.size() + ".");
            env.commitTransactionAndClear(em);
            checkPatents(patents, testPatents);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testPatentsAsSet() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        final Set<Patent> testPatents = new HashSet<Patent>();
        testPatents.addAll(Arrays.asList(TEST_DATA));
        try {
            env.beginTransaction(em);
            final Employee employee = em.find(Employee.class, EDISON);
            employee.setPatents(testPatents);
            em.flush();
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            final Employee storedEmployee = em.find(Employee.class, EDISON);
            if (null == storedEmployee) {
                flop("test employee vanished.");
            }
            final Collection<Patent> patents = storedEmployee.getPatents();
            verify(patents != null, "patents not stored");
            verify(patents.size() == testPatents.size(), "Wrong number of patents retrieved. Expected: " + testPatents.size()
                    + ", but got: " + patents.size() + ".");
            env.commitTransactionAndClear(em);
            checkPatents(patents, testPatents);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSharedPatents() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        final Set<Patent> edisonPatents = new HashSet<Patent>();
        edisonPatents.add(TEST_DATA[0]);
        edisonPatents.add(TEST_DATA[1]);
        edisonPatents.add(TEST_DATA[3]);
        final List<Patent> teslaPatents = new ArrayList<Patent>();
        teslaPatents.add(TEST_DATA[3]);
        teslaPatents.add(TEST_DATA[2]);
        try {
            env.beginTransaction(em);
            final Employee edison = em.find(Employee.class, EDISON);
            edison.setPatents(edisonPatents);
            final Employee tesla = em.find(Employee.class, TESLA);
            tesla.setPatents(teslaPatents);
            em.flush();
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            final Employee persistedEdison = em.find(Employee.class, EDISON);
            if (null == persistedEdison) {
                flop("Edison vanished.");
            }
            final Employee persistedTesla = em.find(Employee.class, TESLA);
            if (null == persistedTesla) {
                flop("Tesla vanished.");
            }
            final Collection<Patent> persistedTeslaPatents = persistedTesla.getPatents();
            verify(persistedTeslaPatents != null, "Teslas patents not stored");
            verify(persistedTeslaPatents.size() == teslaPatents.size(), "Wrong number of patents retrieved. Expected: "
                    + teslaPatents.size() + ", but got: " + persistedTeslaPatents.size() + ".");
            final Collection<Patent> persistedEdisonPatents = persistedEdison.getPatents();
            verify(persistedEdisonPatents != null, "Edisons patents not stored");
            verify(persistedEdisonPatents.size() == edisonPatents.size(), "Wrong number of patents retrieved. Expected: "
                    + edisonPatents.size() + ", but got: " + persistedEdisonPatents.size() + ".");
            env.commitTransactionAndClear(em);
            checkPatents(persistedTeslaPatents, teslaPatents);
            checkPatents(persistedEdisonPatents, edisonPatents);
        } finally {
            closeEntityManager(em);
        }
    }

    @SuppressWarnings("unchecked")
    private void checkPatents(final Collection<Patent> patents, final Collection<Patent> testPatents) {
        final Map<PatentId, Patent> checkMap = new HashMap<PatentId, Patent>();
        for (final Patent testPatent : testPatents) {
            checkMap.put(testPatent.getId(), testPatent);
        }
        for (final Iterator iter = patents.iterator(); iter.hasNext();) {
            final Patent patent = (Patent) iter.next();
            final Patent check = checkMap.get(patent.getId());
            verify(check != null, "retrieved unknown patent with name >>" + patent.getId().getName() + "<<.");
            verify(check.getDescription().equals(patent.getDescription()), "patent with name >>" + patent.getId().getName()
                    + "<< has wrong description >>" + patent.getDescription() + "<<, expected was >>" + check.getDescription()
                    + "<<.");
            verify(check.getAssignation().equals(patent.getAssignation()), "patent with name >>" + patent.getId().getName()
                    + "<< has wrong assignation >>" + patent.getAssignation() + "<<, expected was >>" + check.getAssignation()
                    + "<<.");
        }
    }
}
