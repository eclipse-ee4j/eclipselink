/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     tware - initial API and implementation as part of Query Downcast feature
//     02/08/2013-2.5 Chris Delahunt
//       - 374771 - JPA 2.1 TREAT support
package org.eclipse.persistence.testing.tests.jpa.criteria.inherited;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inherited.BeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.Blue;
import org.eclipse.persistence.testing.models.jpa.inherited.BlueLight;
import org.eclipse.persistence.testing.models.jpa.inherited.InheritedTableManager;

import java.math.BigInteger;
import java.util.List;

public class CriteriaQueryCastTest extends JUnitTestCase {

    public CriteriaQueryCastTest() {
        super();
    }

    public CriteriaQueryCastTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "inherited";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CriteriaQueryCastTest");

        suite.addTest(new CriteriaQueryCastTest("testSetup"));
        suite.addTest(new CriteriaQueryCastTest("testDowncastSingleTableQueryKey"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new InheritedTableManager().replaceTables(getPersistenceUnitServerSession());
        // Force uppercase for Postgres.
        if (getPersistenceUnitServerSession().getPlatform().isPostgreSQL()) {
            getPersistenceUnitServerSession().getLogin().setShouldForceFieldNamesToUpperCase(true);
        }
    }

    public void testDowncastSingleTableQueryKey(){
        EntityManager em = createEntityManager();

        beginTransaction(em);
        try {
            BeerConsumer consumer = new BeerConsumer();
            consumer.setName("John");
            em.persist(consumer);
            Blue blue = new Blue();
            blue.setAlcoholContent(5f);
            blue.setUniqueKey(new BigInteger("4531"));
            em.persist(blue);

            BlueLight blueLight = new BlueLight();
            blueLight.setDiscount(10);
            blueLight.setUniqueKey(new BigInteger("4533"));
            em.persist(blueLight);

            consumer.addBlueBeerToConsume(blueLight);
            blueLight.setBeerConsumer(consumer);

            consumer.addBlueBeerToConsume(blue);
            consumer.addBlueBeerToConsume(blueLight);

            consumer = new BeerConsumer();
            consumer.setName("Frank");
            em.persist(consumer);

            blueLight = new BlueLight();
            blueLight.setDiscount(5);
            blueLight.setUniqueKey(new BigInteger("4532"));
            em.persist(blueLight);

            consumer.addBlueBeerToConsume(blueLight);
            blueLight.setBeerConsumer(consumer);

            em.flush();

            clearCache();
            em.clear();

            //Query query = em.createQuery("Select b from BeerConsumer b join treat(b.blueBeersToConsume as BlueLight) bl where bl.discount = 10");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<BeerConsumer> cq = qb.createQuery(BeerConsumer.class);
            Root<BeerConsumer> root = cq.from(BeerConsumer.class);
            Join blueLightJoin = qb.treat(root.join("blueBeersToConsume"), BlueLight.class);
            cq.where(qb.equal(blueLightJoin.get("discount"), 10));

            List<?> resultList = em.createQuery(cq).getResultList();

            assertEquals("Incorrect results returned", 1, resultList.size());
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
}

