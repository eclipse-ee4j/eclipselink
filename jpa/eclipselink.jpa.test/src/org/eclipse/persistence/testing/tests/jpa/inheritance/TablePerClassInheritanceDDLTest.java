/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.jpa.inheritance;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.PersistenceUnitProperties;

public class TablePerClassInheritanceDDLTest extends TablePerClassInheritanceJUnitTest {
    static EntityManagerFactory factory;

    public TablePerClassInheritanceDDLTest() {
        super();
    }

    public TablePerClassInheritanceDDLTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("TablePerClassInheritanceDDLTest");
        suite.addTest(new TablePerClassInheritanceDDLTest("testSetup"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testCreateAssassinWithGun"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testValidateAssassinWithGun"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testValidateGunToAssassin"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testAddDirectElimination"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testValidateDirectElimination"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testAddIndirectElimination"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testValidateIndirectElimination"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testValidateAssassinWithBombAndEliminations"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testNamedQueryFindAllWeapons"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testNamedQueryFindAllWeaponsWhereDescriptionContainsSniper"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testBatchFindAllWeapons"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testCreateNewSocialClubsWithMembers"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testValidateSocialClub1Members"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testValidateSocialClub2Members"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testValidateSocialClub3Members"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testAssassinOptimisticLocking"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testSpecialAssassinOptimisticLocking"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testAssassinOptimisticLockingUsingEntityManagerAPI"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testGunOptimisticLocking"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testUpdateAllQuery"));
        suite.addTest(new TablePerClassInheritanceDDLTest("testTeardown"));
        return suite;
    }

    /**
     * DDL should be generated when creating factory.
     */
    public void testSetup() {
        clearCache();
        clearServerSessionCache();
        createEntityManager().close();
        // Only test DDL on Oracle.
        if (!getServerSession().getPlatform().isOracle()) {
            super.testSetup();
        }
    }

    /**
     * DDL should be generated when creating factory.
     */
    public void testTeardown() {
        if (this.factory != null) {
            this.factory.close();
            this.factory = null;
        }
    }

    /**
     * Use a custom factory to generate DDL.
     */
    public EntityManagerFactory getEntityManagerFactory() {
        if (this.factory == null) {
            // Only test DDL on Oracle, otherwise will have constraint issues.
            if (!getServerSession().getPlatform().isOracle()) {
                this.factory = Persistence.createEntityManagerFactory(getPersistenceUnitName());
                return this.factory;
            }
            // Ensure real one inits first.
            super.createEntityManager().close();

            Map properties = new HashMap(getPersistenceProperties());
            properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
            properties.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_DATABASE_GENERATION);
            properties.put(PersistenceUnitProperties.SESSION_NAME, "TablePerClassInheritanceDDLTest");
            this.factory = Persistence.createEntityManagerFactory(getPersistenceUnitName(), properties);
        }
        return this.factory;
    }

    /**
     * Use a custom factory to generate DDL.
     */
    public EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }
}

