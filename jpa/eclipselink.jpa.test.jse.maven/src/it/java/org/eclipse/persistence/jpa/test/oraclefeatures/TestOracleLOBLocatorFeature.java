/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     IBM - initial drop
//     05/06/2019 - Jody Grassel
//       - 547023 : Add LOB Locator support for core Oracle platform.

package org.eclipse.persistence.jpa.test.oraclefeatures;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.oraclefeatures.model.OracleLobEntity;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.OraclePlatform;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestOracleLOBLocatorFeature {
    private final static char[] alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghikjlmnopqrstuvwxyz".toCharArray();
    private final static int alphaLen = alphabet.length;

    @Emf(name = "emfWithSessionCustomizer",createTables = DDLGen.DROP_CREATE, classes = { OracleLobEntity.class }, properties = {
            @Property(name = "eclipselink.session.customizer", value="org.eclipse.persistence.jpa.test.oraclefeatures.OracleLOBLocatorSessionCustomizer")
//            @Property(name = "eclipselink.logging.level", value = "FINEST"),
    })
    private EntityManagerFactory emf;

    @Emf(name = "emfNoSessionCustomizer", createTables = DDLGen.DROP_CREATE, classes = { OracleLobEntity.class })
    private EntityManagerFactory emfNoSessionCustomizer;

    
    private SecureRandom sr = new SecureRandom();

    @Test
    public void testOracleLOBLocator() throws Exception {
        if (!checkIsOracle()) {
            // Skip if not testing against Oracle
            return;
        }

        System.out.println("***** Begin testOracleLOBLocator");

        EntityManager em = emf.createEntityManager();
        try {
            OracleLobEntity blobEntity = new OracleLobEntity();
            blobEntity.setStrData("Some Data");

            int datalen = 50000;
            byte[] data = new byte[datalen];
            sr.nextBytes(data);
            blobEntity.setBlobData(data);

            char[] cdata = new char[datalen];
            for (int i = 0; i < datalen; i++) {
                cdata[i] = alphabet[Math.abs((sr.nextInt() % alphaLen))];
            }
            blobEntity.setClobData(new String(cdata));

            em.getTransaction().begin();
            em.persist(blobEntity);
            em.getTransaction().commit();

            em.clear();

            OracleLobEntity findEntity = em.find(OracleLobEntity.class, blobEntity.getId());
            Assert.assertNotNull(findEntity);
            Assert.assertEquals(blobEntity.getStrData(), findEntity.getStrData());
            Assert.assertEquals(blobEntity.getBlobData(), findEntity.getBlobData());
            Assert.assertEquals(blobEntity.getClobData(), findEntity.getClobData());
        } finally {
            if(em != null) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                if(em.isOpen()) {
                    em.close();
                 }
            }
            System.out.println("***** End testOracleLOBLocator");
        }
    }

    @Test
    public void testOracleLOBLocatorWithEmptyClob() throws Exception {
        if (!checkIsOracle()) {
            // Skip if not testing against Oracle
            return;
        }

        System.out.println("***** Begin testOracleLOBLocatorWithEmptyClob");

        EntityManager em = emf.createEntityManager();
        try {
            OracleLobEntity blobEntity = new OracleLobEntity();
            blobEntity.setStrData("Some Data");

            int datalen = 50000;
            byte[] data = new byte[datalen];
            sr.nextBytes(data);
            blobEntity.setBlobData(data);

            blobEntity.setClobData("");

            em.getTransaction().begin();
            em.persist(blobEntity);
            em.getTransaction().commit();

            em.clear();

            OracleLobEntity findEntity = em.find(OracleLobEntity.class, blobEntity.getId());
            Assert.assertNotNull(findEntity);
            Assert.assertEquals(blobEntity.getStrData(), findEntity.getStrData());
            Assert.assertEquals(blobEntity.getBlobData(), findEntity.getBlobData());
            Assert.assertEquals(blobEntity.getClobData(), findEntity.getClobData());
        } finally {
            if(em != null) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                if(em.isOpen()) {
                    em.close();
                 }
            }
            System.out.println("***** End testOracleLOBLocatorWithEmptyClob");
        }
    }
    
    @Test
    public void testOracleWithoutLOBLocatorWithEmptyClob() throws Exception {
        // After Oracle 11, the lob locator is disabled by default (requiring a Session Customizer to reenable it)
        // So the test should fail because Eclipselink will try store a null value instead of an empty_blob()/empty_clob()
        // and violate the NOT NULL constraint.
        Set<String> notAllowedPlatforms = new HashSet<String>();
        notAllowedPlatforms.add("org.eclipse.persistence.platform.database.Oracle8Platform");
        notAllowedPlatforms.add("org.eclipse.persistence.platform.database.Oracle9Platform");
        notAllowedPlatforms.add("org.eclipse.persistence.platform.database.Oracle10Platform");
        
        
        if (!checkIsOracle() || notAllowedPlatforms.contains(getPlatform(emfNoSessionCustomizer).getClass().getName())) {
            // Skip if not testing against Oracle
            return;
        }

        System.out.println("***** Begin testOracleWithoutLOBLocatorWithEmptyClob");

        EntityManager em = emfNoSessionCustomizer.createEntityManager();
        try {
            OracleLobEntity blobEntity = new OracleLobEntity();
            blobEntity.setStrData("Some Data");

            int datalen = 50000;
            byte[] data = new byte[datalen];
            sr.nextBytes(data);
            blobEntity.setBlobData(data);

            blobEntity.setClobData("");
            
            try {
                em.getTransaction().begin();
                em.persist(blobEntity);
                em.getTransaction().commit();
            } catch (javax.persistence.RollbackException re) {
                // Expected
                Assert.assertThat(re, getExceptionChainMatcher(java.sql.SQLIntegrityConstraintViolationException.class));
            }

            
        } finally {
            if(em != null) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                if(em.isOpen()) {
                    em.close();
                 }
            }
            System.out.println("***** End testOracleWithoutLOBLocatorWithEmptyClob");
        }
    }
    

    private boolean checkIsOracle() {
        return (emf != null && getPlatform(emf) instanceof OraclePlatform);
    }

    private DatabasePlatform getPlatform(EntityManagerFactory emf) {
        DatabasePlatform platform = ((EntityManagerFactoryImpl) emf).getServerSession().getPlatform();
        return platform;
    }
    
    @SuppressWarnings("rawtypes")
    protected Matcher getExceptionChainMatcher(final Class t) {
        return new BaseMatcher() {
            final protected Class<?> expected = t;

            @Override
            public boolean matches(Object obj) {
                if (obj == null) {
                    return (expected == null);
                }

                if (!(obj instanceof Throwable)) {
                    return false;
                }

                final ArrayList<Throwable> tList = new ArrayList<Throwable>();

                Throwable t = (Throwable) obj;
                while (t != null) {
                    tList.add(t);
                    if (expected.equals(t.getClass())) {
                        return true;
                    }

                    if (expected.isAssignableFrom(t.getClass())) {
                        return true;
                    }

                    t = t.getCause();
                }

                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(expected.toString());
            }

        };
    }

}
