/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation.
package org.eclipse.persistence.testing.tests.jpa.advanced.cascadepersist;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.cascadepersist.CascadePersistTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.cascadepersist.EntityX;
import org.eclipse.persistence.testing.models.jpa.advanced.cascadepersist.EntityY;
import org.eclipse.persistence.testing.models.jpa.advanced.cascadepersist.EntityZ;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

public class CascadePersistTest extends JUnitTestCase {

    public CascadePersistTest() {
        super();
    }

    public CascadePersistTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "cascadepersist";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CascadePersistTest");
        suite.addTest(new CascadePersistTest("testSetup"));
        suite.addTest(new CascadePersistTest("testCascadePersistForUnregisteredObjects"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow
     * execution in the server.
     */
    public void testSetup() {
        new CascadePersistTableCreator().replaceTables(getPersistenceUnitServerSession());
        clearCache();
    }

    public void testCascadePersistForUnregisteredObjects(){
        List<Throwable> errorList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            try {
                persistObjects(i);
            } catch (Throwable t) {
                errorList.add(t);
            }
        }

        String errorMsg = null;
        if (!errorList.isEmpty()) {
            errorMsg = errorList.get(0).getMessage();
        }

        Assert.assertEquals(errorList.size() + " tests failed out of 10 tests with exception " + errorMsg, 0, errorList.size());
    }

    private void persistObjects(int id) {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            EntityX x = new EntityX();
            x.setId(id);
            x.setXname("Bob" + id);

            EntityY y = new EntityY();
            y.setId(100 + id);
            y.setYname("Smith" + id);

            EntityZ z = new EntityZ();
            z.setId(1000 + id);
            z.setZname("Robin" + id);

            // Register just z and x objects.
            em.persist(z);
            em.persist(x);

            // Set values as unregistered object y to reproduce the issue.
            x.setEntityY(y);
            y.setEntityX(x);
            z.addYToList(y);
            y.setYzEntityRelation(z);

            em.flush();
        } finally {
            rollbackTransaction(em);
            if (em != null && em.isOpen()) {
                closeEntityManager(em);
            }
        }
    }
}
