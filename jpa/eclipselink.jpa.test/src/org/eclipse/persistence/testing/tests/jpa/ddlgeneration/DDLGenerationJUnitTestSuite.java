/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     08/20/2008-1.0.1 Nathan Beyer (Cerner) 
 *       - 241308: Primary key is incorrectly assigned to embeddable class 
 *                 field with the same name as the primary key field's name
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.ddlgeneration;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

/**
 * JUnit test case(s) for DDL generation.
 */
public class DDLGenerationJUnitTestSuite extends JUnitTestCase {
    // the persistence unit name which is used in this test suite
    private static final String DDL_PU = "ddlGeneration";

    public DDLGenerationJUnitTestSuite() {
        super();
    }

    public DDLGenerationJUnitTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(DDLGenerationJUnitTestSuite.class);
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        // Trigger DDL generation
        //TODO: Let's add a flag which do not disregard DDL generation errors.
        //TODO: This is required to ensure that DDL generation has succeeded.
        EntityManager em = createEntityManager(DDL_PU);
        //em.close();
        clearCache(DDL_PU);
    }

    // Test for GF#1392
    // If there is a same name column for the entity and many-to-many table, wrong pk constraint generated.
    public void testDDLPkConstraintErrorIncludingRelationTableColumnName() {
        EntityManager em = createEntityManager(DDL_PU);
        beginTransaction(em);
        try {

            CKeyEntityC c = new CKeyEntityC(new CKeyEntityCPK("Manager"));
            em.persist(c);

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            fail("DDL generation may generate wrong Primary Key constraint, thrown:" + e);
        } finally {
            closeEntityManager(em);
        }
    }

    // Test for relationships using candidate(unique) keys
    public void testDDLUniqueKeysAsJoinColumns() {
        CKeyEntityAPK aKey;
        CKeyEntityBPK bKey;
        
        EntityManager em = createEntityManager(DDL_PU);
        beginTransaction(em);
        try {
            CKeyEntityA a = new CKeyEntityA("Wonseok", "Kim");
            long seq = System.currentTimeMillis(); // just to get unique value :-)
            CKeyEntityB b = new CKeyEntityB(new CKeyEntityBPK(seq, "B1209"));
            //set unique keys
            b.setUnq1("u0001");
            b.setUnq2("u0002");

            a.setUniqueB(b);
            b.setUniqueA(a);
            
            em.persist(a);
            em.persist(b);

            commitTransaction(em);
            
            aKey = a.getKey();
            bKey = b.getKey();
        } catch (RuntimeException e) {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            throw e;
        } finally {
            closeEntityManager(em);
        }
        //clearCache(DDL_PU);

        em = createEntityManager(DDL_PU);
        beginTransaction(em);
        try {
            CKeyEntityA a = em.find(CKeyEntityA.class, aKey);
            assertNotNull(a);

            CKeyEntityB b = a.getUniqueB();
            assertNotNull(b);

            assertEquals(b.getUnq1(), "u0001");
            assertEquals(b.getUnq2(), "u0002");

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            throw e;
        } finally {
            closeEntityManager(em);
        }
        //clearCache(DDL_PU);

        em = createEntityManager(DDL_PU);
        beginTransaction(em);
        try {

            CKeyEntityB b = em.find(CKeyEntityB.class, bKey);
            assertNotNull(b);

            CKeyEntityA a = b.getUniqueA();
            assertNotNull(a);
            assertEquals(a.getKey(), aKey);

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            throw e;
        } finally {
            closeEntityManager(em);
        }

    }

    // Test to check if unique constraints are generated correctly
    public void testDDLUniqueConstraintsByAnnotations() {
        if(!getServerSession(DDL_PU).getPlatform().supportsUniqueKeyConstraints()) {
            return;
        }
        UniqueConstraintsEntity1 ucEntity;
        
        EntityManager em = createEntityManager(DDL_PU);
        beginTransaction(em);
        try {
            ucEntity = em.find(UniqueConstraintsEntity1.class, 1);
            if(ucEntity == null) {
                ucEntity = new UniqueConstraintsEntity1(1);
                ucEntity.setColumns(1, 1, 1, 1);
                em.persist(ucEntity);
            }
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
            throw e;
        }
        
        beginTransaction(em);
        try {
            ucEntity = new UniqueConstraintsEntity1(2);
            ucEntity.setColumns(1, 2, 2, 2);
            em.persist(ucEntity);
            em.flush();
            
            fail("Unique constraint violation is expected");
        } catch (PersistenceException e) {
            //expected
        } finally {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
        }

        em = createEntityManager(DDL_PU);
        beginTransaction(em);
        try {
            ucEntity = new UniqueConstraintsEntity1(2);
            ucEntity.setColumns(2, 1, 2, 2);
            em.persist(ucEntity);
            em.flush();
            
            fail("Unique constraint violation is expected");
        } catch (PersistenceException e) {
            //expected
        } finally {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
        }
        
        em = createEntityManager(DDL_PU);
        beginTransaction(em);
        try {
            ucEntity = new UniqueConstraintsEntity1(2);
            ucEntity.setColumns(2, 2, 1, 1);
            em.persist(ucEntity);
            em.flush();
            
            fail("Unique constraint violation is expected");
        } catch (PersistenceException e) {
            //expected
        } finally {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
        }

        em = createEntityManager(DDL_PU);
        beginTransaction(em);
        try {
            ucEntity = new UniqueConstraintsEntity1(2);
            ucEntity.setColumns(2, 2, 1, 2);
            em.persist(ucEntity);
            em.flush();
        } catch (PersistenceException e) {
            fail("Unique constraint violation is not expected, thrown:" + e);
        } finally {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
        }
    }
    
    // Test to check if unique constraints are generated correctly
    public void testDDLUniqueConstraintsByXML() {
        if(!getServerSession(DDL_PU).getPlatform().supportsUniqueKeyConstraints()) {
            return;
        }
        UniqueConstraintsEntity2 ucEntity;
        
        EntityManager em = createEntityManager(DDL_PU);
        beginTransaction(em);
        try {
            ucEntity = em.find(UniqueConstraintsEntity2.class, 1);
            if(ucEntity == null) {
                ucEntity = new UniqueConstraintsEntity2(1);
                ucEntity.setColumns(1, 1, 1, 1);
                em.persist(ucEntity);
            }
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
            throw e;
        }
        
        beginTransaction(em);
        try {
            ucEntity = new UniqueConstraintsEntity2(2);
            ucEntity.setColumns(1, 2, 2, 2);
            em.persist(ucEntity);
            em.flush();
            
            fail("Unique constraint violation is expected");
        } catch (PersistenceException e) {
            //expected
        } finally {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
        }

        em = createEntityManager(DDL_PU);
        beginTransaction(em);
        try {
            ucEntity = new UniqueConstraintsEntity2(2);
            ucEntity.setColumns(2, 1, 2, 2);
            em.persist(ucEntity);
            em.flush();
            
            fail("Unique constraint violation is expected");
        } catch (PersistenceException e) {
            //expected
        } finally {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
        }
        
        em = createEntityManager(DDL_PU);
        beginTransaction(em);
        try {
            ucEntity = new UniqueConstraintsEntity2(2);
            ucEntity.setColumns(2, 2, 1, 1);
            em.persist(ucEntity);
            em.flush();
            
            fail("Unique constraint violation is expected");
        } catch (PersistenceException e) {
            //expected
        } finally {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
        }

        em = createEntityManager(DDL_PU);
        beginTransaction(em);
        try {
            ucEntity = new UniqueConstraintsEntity2(2);
            ucEntity.setColumns(2, 2, 1, 2);
            em.persist(ucEntity);
            em.flush();
        } catch (PersistenceException e) {
            fail("Unique constraint violation is not expected, thrown:" + e);
        } finally {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    // test if the primary key columns of subclass entity whose root entity has EmbeddedId 
    // are generated properly in joined inheritance strategy
    // Issue: GF#1391
    public void testDDLSubclassEmbeddedIdPkColumnsInJoinedStrategy() {
        EntityManager em = createEntityManager(DDL_PU);
        beginTransaction(em);
        // let's see if a subclass entity is persisted and found well
        try {
            long seq = System.currentTimeMillis(); // just to get unique value :-)
            String code = "B1215";
            CKeyEntityB2 b = new CKeyEntityB2(new CKeyEntityBPK(seq, code));
            //set unique keys
            b.setUnq1(String.valueOf(seq));
            b.setUnq2(String.valueOf(seq));

            em.persist(b);
            em.flush();
            String query = "SELECT b FROM CKeyEntityB2 b WHERE b.key.seq = :seq AND b.key.code = :code";
            Object result = em.createQuery(query).setParameter("seq", seq).setParameter("code", code)
                            .getSingleResult();
            assertNotNull(result);
            
            rollbackTransaction(em);
            
        } catch (RuntimeException e) {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    // Bug 241308 - Primary key is incorrectly assigned to embeddable class 
    // field with the same name as the primary key field's name
    public void testBug241308() {
        EntityManager em = createEntityManager(DDL_PU);
        beginTransaction(em);
     
        try {
            ThreadInfo threadInfo1 = new ThreadInfo();
            threadInfo1.setId(0);
            threadInfo1.setName("main");
            
            MachineState machineState1 = new MachineState();
            machineState1.setId(0);
            machineState1.setThread(threadInfo1);
            
            em.persist(machineState1);
            
            ThreadInfo threadInfo2 = new ThreadInfo();
            threadInfo2.setId(0);
            threadInfo2.setName("main");
            
            MachineState machineState2 = new MachineState();
            machineState2.setId(1);
            machineState2.setThread(threadInfo2);
            
            em.persist(machineState2);
            
            commitTransaction(em);
            
        } catch (RuntimeException e) {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}
