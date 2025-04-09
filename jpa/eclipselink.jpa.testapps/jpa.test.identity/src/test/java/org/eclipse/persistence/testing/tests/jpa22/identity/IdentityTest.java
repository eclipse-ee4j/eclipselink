/*
 * Copyright (c) 2016, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     09/29/2016-2.7 Tomas Kraus
//       - 426852: @GeneratedValue(strategy=GenerationType.IDENTITY) support in Oracle 12c
package org.eclipse.persistence.testing.tests.jpa22.identity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import junit.framework.TestSuite;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa22.identity.Person;
import junit.framework.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test proper identity column value generation.
 */
public class IdentityTest extends JUnitTestCase{

    public IdentityTest() {
        super();
    }

    public IdentityTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "identity-pu";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("IdentityTest");
        suite.addTest(new IdentityTest("testIdentity"));
        return suite;
    }

    /**
     * Test identity column value generation.
     */
    public void testIdentity() {
        EntityManager em = createEntityManager();
        DatabaseSession session = getPersistenceUnitServerSession();

        if (!session.getPlatform().supportsIdentity()) {
            return;
        }
        final Person p1 = new Person("John", "Smith");
        final Person p2 = new Person("Bob", "Brown");
        beginTransaction(em);
        try {
            em.persist(p1);
            em.persist(p2);
            commitTransaction(em);
        } catch (PersistenceException | IllegalArgumentException ex) {
            rollbackTransaction(em);
            ex.printStackTrace();
            throw ex;
        }
        final Map<String, Person> pMap = new HashMap<>(2);
        pMap.put(p1.getSecondName(), p1);
        pMap.put(p2.getSecondName(), p2);
        final TypedQuery<Person> pQuery = em.createQuery("SELECT p FROM Person p", Person.class);
        final List<Person> pList = pQuery.getResultList();
        for (final Person p : pList) {
            final Person pV = pMap.get(p.getSecondName());
            assertEquals(p.getId(), pV.getId());
        }
    }
}
