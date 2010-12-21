/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.testing.tests.jpa.cascadedeletes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import junit.framework.*;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.cascadedeletes.BranchA;
import org.eclipse.persistence.testing.models.jpa.cascadedeletes.BranchB;
import org.eclipse.persistence.testing.models.jpa.cascadedeletes.CascadeDeleteTableCreator;
import org.eclipse.persistence.testing.models.jpa.cascadedeletes.LeafA;
import org.eclipse.persistence.testing.models.jpa.cascadedeletes.LeafB;
import org.eclipse.persistence.testing.models.jpa.cascadedeletes.MachineState;
import org.eclipse.persistence.testing.models.jpa.cascadedeletes.PersistentIdentity;
import org.eclipse.persistence.testing.models.jpa.cascadedeletes.RootA;
import org.eclipse.persistence.testing.models.jpa.cascadedeletes.ThreadInfo;

public class CascadeDeletesJUnitTestSuite extends JUnitTestCase {

    public CascadeDeletesJUnitTestSuite() {
        super();
    }

    public CascadeDeletesJUnitTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CascadeDeletesJUnitTestSuite");
        suite.addTest(new CascadeDeletesJUnitTestSuite("testSetup"));
        suite.addTest(new CascadeDeletesJUnitTestSuite("testDeleteWholeTree"));
        suite.addTest(new CascadeDeletesJUnitTestSuite("testRemoveMachineState"));
        suite.addTest(new CascadeDeletesJUnitTestSuite("testDeletePrivateOwned"));
        suite.addTest(new CascadeDeletesJUnitTestSuite("testDeleteBranchBTree"));
        suite.addTest(new CascadeDeletesJUnitTestSuite("testDeletePrivateOwnedChild"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow
     * execution in the server.
     */
    public void testSetup() {
        new CascadeDeleteTableCreator().replaceTables(JUnitTestCase.getServerSession("cascade-deletes"));
        clearCache("cascade-deletes");
    }
    
    public void testRemoveMachineState(){
        EntityManager em = createEntityManager("cascade-deletes");
        beginTransaction(em);
        
        MachineState ms = new MachineState();
        ms.setId(1);
        ms.setThreads(new ArrayList<ThreadInfo>());
        ms.getThreads().add(new ThreadInfo(1, "main"));
        
        em.persist(ms);
        
        commitTransaction(em);
        
        beginTransaction(em);
        
        ms = em.find(MachineState.class, 1L);
        
        em.remove(ms);
        
        commitTransaction(em);

    }

    public void testDeleteWholeTree() {
        Collection<PersistentIdentity> allEntities = new ArrayList<PersistentIdentity>();
        RootA rootA = createTree(allEntities);

        EntityManager em = createEntityManager("cascade-deletes");
        try {
            beginTransaction(em);
            rootA = em.find(RootA.class, rootA.getId());
            // :(
            for (BranchA a : rootA.getBranchAs()) {
                a.getLeafs().size();
            }
            em.remove(rootA);
            for (PersistentIdentity entity : allEntities) {
                assertNull("Contains found removed entity", em.find(entity.getClass(), entity.getId()));
            }
            commitTransaction(em);
            closeEntityManager(em);
            clearCache("cascade-deletes");

            em = createEntityManager("cascade-deletes");
            for (PersistentIdentity entity : allEntities) {
                assertNull("Failed to remove all entities, found entity class: " +  entity.getClass() + " with Id: " + entity.getId(), em.find(entity.getClass(), entity.getId()));
            }
            closeEntityManager(em);

        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }finally{
            deleteTree();
        }
    }

    public void testDeletePrivateOwned() {
        Collection<PersistentIdentity> allEntities = new ArrayList<PersistentIdentity>();
        RootA rootA = createTree(allEntities);

        EntityManager em = createEntityManager("cascade-deletes");
        try {
            beginTransaction(em);
            BranchB branchB = (BranchB) em.createQuery("Select b from BranchB b join b.branchBs bb where bb.leafBs is not empty").getResultList().get(0);
            BranchB subB = branchB.getBranchBs().get(0);
            subB.getLeafBs().size();
            branchB.getBranchBs().clear();
            commitTransaction(em);
            closeEntityManager(em);
            clearCache("cascade-deletes");
            em = createEntityManager("cascade-deletes");
            assertNull("private owned object was not deleted.", em.find(BranchB.class, subB.getId()));
            assertNotNull("child of PO object was deleted in error", em.find(LeafB.class, subB.getLeafBs().get(0).getId()));
            
            

        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }finally{
            deleteTree();
        }
    }

    public void testDeletePrivateOwnedChild() {
        Collection<PersistentIdentity> allEntities = new ArrayList<PersistentIdentity>();
        createTree(allEntities);

        EntityManager em = createEntityManager("cascade-deletes");
        try {
            beginTransaction(em);
            RootA rootA = (RootA) em.createQuery("Select r from RootA r join r.branchAs ba where ba.secondSet is not empty").getResultList().get(0);
            BranchA subA = rootA.getBranchAs().get(0);
            subA.getLeafs().size();
            rootA.getBranchAs().remove(subA);
            
            commitTransaction(em);
            closeEntityManager(em);
            clearCache("cascade-deletes");
            em = createEntityManager("cascade-deletes");
            assertNull("private owned object was not deleted.", em.find(BranchA.class, subA.getId()));
            for (LeafA leafA : subA.getLeafs()){
                assertNull("child of PO object was deleted in error", em.find(LeafA.class, leafA.getId()));
            }
            

        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }finally{
            deleteTree();
        }
    }

    public void testDeleteBranchBTree() {
        Collection<PersistentIdentity> allEntities = new ArrayList<PersistentIdentity>();
        RootA rootA = createTree(allEntities);

        EntityManager em = createEntityManager("cascade-deletes");
        try {
            beginTransaction(em);
            rootA = em.find(RootA.class, rootA.getId());
            BranchB removed = rootA.getBranchB();
            em.remove(removed);
            rootA.setBranchB(null);
            assertFalse("Failed to remove all entities in tree", removed.checkTreeForRemoval(em));
            commitTransaction(em);
            closeEntityManager(em);
            clearCache("cascade-deletes");

            em = createEntityManager("cascade-deletes");
            assertFalse("Failed to remove all entities in tree", removed.checkTreeForRemoval(em));
            closeEntityManager(em);

        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }finally{
            deleteTree();
        }
    }

    public RootA createTree(Collection<PersistentIdentity> allEntities) {
        EntityManager em = createEntityManager("cascade-deletes");
        beginTransaction(em);
        RootA rootA = new RootA();
        em.persist(rootA);
        allEntities.add(rootA);
        BranchA branchA = new BranchA();
        em.persist(branchA);
        allEntities.add(branchA);
        BranchA subBranch = new BranchA();
        em.persist(subBranch);
        allEntities.add(subBranch);
        branchA.setBranchA(subBranch);
        LeafA leafA = new LeafA();
        em.persist(leafA);
        allEntities.add(leafA);
        leafA.setBranchA(branchA);
        branchA.getLeafs().add(leafA);

        leafA = new LeafA();
        em.persist(leafA);
        allEntities.add(leafA);
        branchA.getSecondSet().add(leafA);
        leafA = new LeafA();
        em.persist(leafA);
        allEntities.add(leafA);
        branchA.getSecondSet().add(leafA);

        
        
        leafA = new LeafA();
        em.persist(leafA);
        allEntities.add(leafA);
        leafA.setBranchA(branchA);
        branchA.getLeafs().add(leafA);
        rootA.getBranchAs().add(branchA);
        branchA = new BranchA();
        em.persist(branchA);
        allEntities.add(branchA);
        subBranch = new BranchA();
        em.persist(subBranch);
        allEntities.add(subBranch);
        branchA.setBranchA(subBranch);
        leafA = new LeafA();
        em.persist(leafA);
        allEntities.add(leafA);
        leafA.setBranchA(branchA);
        branchA.getLeafs().add(leafA);
        leafA = new LeafA();
        em.persist(leafA);
        allEntities.add(leafA);
        leafA.setBranchA(branchA);
        branchA.getLeafs().add(leafA);
        rootA.getBranchAs().add(branchA);

        BranchB branchB = new BranchB();
        em.persist(branchB);
        allEntities.add(branchB);
        rootA.setBranchB(branchB);

        BranchB subbranchB = new BranchB();
        em.persist(subbranchB);
        allEntities.add(subbranchB);
        LeafB subbranchBLeafB = new LeafB();
        em.persist(subbranchBLeafB);
        subbranchB.getLeafBs().add(subbranchBLeafB);
        
        branchB.getBranchBs().add(subbranchB);
        subbranchB = new BranchB();
        em.persist(subbranchB);
        allEntities.add(subbranchB);
        branchB.getBranchBs().add(subbranchB);

        LeafB leafB = new LeafB();
        em.persist(leafB);
        allEntities.add(leafB);
        branchB.getLeafBs().add(leafB);
        leafB = new LeafB();
        em.persist(leafB);
        allEntities.add(leafB);
        branchB.getLeafBs().add(leafB);
        commitTransaction(em);
        closeEntityManager(em);
        clearCache("cascade-deletes");
        return rootA;
    }
    
    public void deleteTree() {
        EntityManager em = createEntityManager("cascade-deletes");
        beginTransaction(em);
        List<RootA> result = (List<RootA>)em.createQuery("Select r from RootA r").getResultList();
        for (RootA root : result){
            root.getBranchAs().clear();
            root.setBranchB(null);
        }
        List<BranchA> result2 = (List<BranchA>)em.createQuery("Select r from BranchA r").getResultList();
        for (BranchA root : result2){
            root.getLeafs().clear();
            root.getSecondSet().clear();
            root.setBranchA(null);
        }
        List<BranchB> result3 = (List<BranchB>)em.createQuery("Select r from BranchB r").getResultList();
        for (BranchB root : result3){
            root.getBranchBs().clear();
            root.getLeafBs().clear();
        }
        List<LeafA> result4 = (List<LeafA>)em.createQuery("Select r from LeafA r").getResultList();
        for (LeafA root : result4){
            root.setBranchA(null);
        }
        em.flush();
        em.createQuery("delete from RootA").executeUpdate();
        em.createQuery("delete from BranchA").executeUpdate();
        em.createQuery("delete from BranchB").executeUpdate();
        em.createQuery("delete from LeafA").executeUpdate();
        em.createQuery("delete from LeafB").executeUpdate();
        commitTransaction(em);
        closeEntityManager(em);
        clearCache("cascade-deletes");
    }

}
