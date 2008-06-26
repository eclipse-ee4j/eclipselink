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
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Collection;
import java.util.Vector;
import java.util.Iterator;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;

import junit.framework.*;

import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.config.ExclusiveConnectionMode;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.ConnectionPolicy;
import org.eclipse.persistence.sessions.server.ReadConnectionPool;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.tools.schemaframework.SequenceObjectDefinition;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.CascadePolicy;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.PessimisticLock;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.internal.descriptors.PersistenceEntity;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.weaving.PersistenceWeaved;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedLazy;
import org.eclipse.persistence.queries.FetchGroupTracker;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.models.jpa.advanced.*;

/**
 * Test the EntityManager API using the advanced model.
 */
public class EntityManagerJUnitTestSuite extends JUnitTestCase {

    /** The field length for the firstname */
    public static final int MAX_FIRST_NAME_FIELD_LENGTH = 255;
	
    public EntityManagerJUnitTestSuite() {
        super();
    }
    
    public EntityManagerJUnitTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("EntityManagerJUnitTestSuite");
        
        suite.addTest(new EntityManagerJUnitTestSuite("testSetup"));
        suite.addTest(new EntityManagerJUnitTestSuite("testWeaving"));
        suite.addTest(new EntityManagerJUnitTestSuite("testClearEntityManagerWithoutPersistenceContext"));
        suite.addTest(new EntityManagerJUnitTestSuite("testUpdateAllProjects"));
        suite.addTest(new EntityManagerJUnitTestSuite("testUpdateUsingTempStorage"));
        suite.addTest(new EntityManagerJUnitTestSuite("testSequenceObjectDefinition"));
        suite.addTest(new EntityManagerJUnitTestSuite("testFindDeleteAllPersist"));
        suite.addTest(new EntityManagerJUnitTestSuite("testExtendedPersistenceContext"));
        suite.addTest(new EntityManagerJUnitTestSuite("testRemoveFlushFind"));
        suite.addTest(new EntityManagerJUnitTestSuite("testRemoveFlushPersistContains"));
        suite.addTest(new EntityManagerJUnitTestSuite("testTransactionRequired"));
        suite.addTest(new EntityManagerJUnitTestSuite("testSubString"));
        suite.addTest(new EntityManagerJUnitTestSuite("testFlushModeOnUpdateQuery"));
        suite.addTest(new EntityManagerJUnitTestSuite("testContainsRemoved"));
        suite.addTest(new EntityManagerJUnitTestSuite("testRefreshRemoved"));
        suite.addTest(new EntityManagerJUnitTestSuite("testRefreshNotManaged"));
        suite.addTest(new EntityManagerJUnitTestSuite("testDoubleMerge"));
        suite.addTest(new EntityManagerJUnitTestSuite("testDescriptorNamedQueryForMultipleQueries"));
        suite.addTest(new EntityManagerJUnitTestSuite("testDescriptorNamedQuery"));
        suite.addTest(new EntityManagerJUnitTestSuite("testClearEntityManagerWithoutPersistenceContextSimulateJTA"));
        suite.addTest(new EntityManagerJUnitTestSuite("testMultipleEntityManagerFactories"));
        suite.addTest(new EntityManagerJUnitTestSuite("testOneToManyDefaultJoinTableName"));
        suite.addTest(new EntityManagerJUnitTestSuite("testClosedEmShouldThrowException"));
        suite.addTest(new EntityManagerJUnitTestSuite("testRollbackOnlyOnException"));
        suite.addTest(new EntityManagerJUnitTestSuite("testUpdateAllProjectsWithNullTeamLeader"));
        suite.addTest(new EntityManagerJUnitTestSuite("testUpdateAllLargeProjectsWithNullTeamLeader"));
        suite.addTest(new EntityManagerJUnitTestSuite("testUpdateAllSmallProjectsWithNullTeamLeader"));
        suite.addTest(new EntityManagerJUnitTestSuite("testUpdateAllProjectsWithName"));
        suite.addTest(new EntityManagerJUnitTestSuite("testUpdateAllLargeProjectsWithName"));
        suite.addTest(new EntityManagerJUnitTestSuite("testUpdateAllSmallProjectsWithName"));
        suite.addTest(new EntityManagerJUnitTestSuite("testUpdateAllLargeProjects"));
        suite.addTest(new EntityManagerJUnitTestSuite("testUpdateAllSmallProjects"));
        suite.addTest(new EntityManagerJUnitTestSuite("testUpdateUsingTempStorageWithParameter"));
        suite.addTest(new EntityManagerJUnitTestSuite("testDeleteAllLargeProjectsWithNullTeamLeader"));
        suite.addTest(new EntityManagerJUnitTestSuite("testDeleteAllSmallProjectsWithNullTeamLeader"));
        suite.addTest(new EntityManagerJUnitTestSuite("testDeleteAllProjectsWithNullTeamLeader"));
        suite.addTest(new EntityManagerJUnitTestSuite("testDeleteAllPhonesWithNullOwner"));
        suite.addTest(new EntityManagerJUnitTestSuite("testSetFieldForPropertyAccessWithNewEM"));
        suite.addTest(new EntityManagerJUnitTestSuite("testSetFieldForPropertyAccessWithRefresh"));
        suite.addTest(new EntityManagerJUnitTestSuite("testSetFieldForPropertyAccess"));
        suite.addTest(new EntityManagerJUnitTestSuite("testInitializeFieldForPropertyAccess"));
        suite.addTest(new EntityManagerJUnitTestSuite("testCascadePersistToNonEntitySubclass"));
        suite.addTest(new EntityManagerJUnitTestSuite("testCascadeMergeManaged"));
        suite.addTest(new EntityManagerJUnitTestSuite("testCascadeMergeDetached"));
        suite.addTest(new EntityManagerJUnitTestSuite("testPrimaryKeyUpdatePKFK"));
        suite.addTest(new EntityManagerJUnitTestSuite("testPrimaryKeyUpdateSameValue"));
        suite.addTest(new EntityManagerJUnitTestSuite("testPrimaryKeyUpdate"));
        suite.addTest(new EntityManagerJUnitTestSuite("testRemoveNull"));
        suite.addTest(new EntityManagerJUnitTestSuite("testContainsNull"));
        suite.addTest(new EntityManagerJUnitTestSuite("testPersistNull"));
        suite.addTest(new EntityManagerJUnitTestSuite("testMergeNull"));
        suite.addTest(new EntityManagerJUnitTestSuite("testMergeRemovedObject"));
        suite.addTest(new EntityManagerJUnitTestSuite("testMergeDetachedObject"));
        suite.addTest(new EntityManagerJUnitTestSuite("testSerializedLazy"));
        suite.addTest(new EntityManagerJUnitTestSuite("testCloneable"));
        suite.addTest(new EntityManagerJUnitTestSuite("testLeftJoinOneToOneQuery"));
        suite.addTest(new EntityManagerJUnitTestSuite("testNullifyAddressIn"));
        suite.addTest(new EntityManagerJUnitTestSuite("testQueryOnClosedEM"));
        suite.addTest(new EntityManagerJUnitTestSuite("testIncorrectBatchQueryHint"));
        suite.addTest(new EntityManagerJUnitTestSuite("testFetchQueryHint"));
        suite.addTest(new EntityManagerJUnitTestSuite("testBatchQueryHint"));
        suite.addTest(new EntityManagerJUnitTestSuite("testQueryHints"));
        suite.addTest(new EntityManagerJUnitTestSuite("testParallelMultipleFactories"));
        suite.addTest(new EntityManagerJUnitTestSuite("testMultipleFactories"));
        suite.addTest(new EntityManagerJUnitTestSuite("testPersistenceProperties"));
        suite.addTest(new EntityManagerJUnitTestSuite("testBeginTransactionCloseCommitTransaction"));
        suite.addTest(new EntityManagerJUnitTestSuite("testBeginTransactionClose"));
        suite.addTest(new EntityManagerJUnitTestSuite("testClose"));
        suite.addTest(new EntityManagerJUnitTestSuite("testPersistOnNonEntity"));
        suite.addTest(new EntityManagerJUnitTestSuite("testWRITELock"));
        suite.addTest(new EntityManagerJUnitTestSuite("testReadTransactionIsolation_OriginalInCache_UpdateAll_Refresh_Flush"));
        suite.addTest(new EntityManagerJUnitTestSuite("testReadTransactionIsolation_OriginalInCache_UpdateAll_Refresh"));
        suite.addTest(new EntityManagerJUnitTestSuite("testReadTransactionIsolation_OriginalInCache_UpdateAll_Flush"));
        suite.addTest(new EntityManagerJUnitTestSuite("testReadTransactionIsolation_OriginalInCache_UpdateAll"));
        suite.addTest(new EntityManagerJUnitTestSuite("testReadTransactionIsolation_OriginalInCache_CustomUpdate_Refresh_Flush"));
        suite.addTest(new EntityManagerJUnitTestSuite("testReadTransactionIsolation_OriginalInCache_CustomUpdate_Refresh"));
        suite.addTest(new EntityManagerJUnitTestSuite("testReadTransactionIsolation_OriginalInCache_CustomUpdate_Flush"));
        suite.addTest(new EntityManagerJUnitTestSuite("testReadTransactionIsolation_OriginalInCache_CustomUpdate"));
        suite.addTest(new EntityManagerJUnitTestSuite("testReadTransactionIsolation_UpdateAll_Refresh_Flush"));
        suite.addTest(new EntityManagerJUnitTestSuite("testReadTransactionIsolation_UpdateAll_Refresh"));
        suite.addTest(new EntityManagerJUnitTestSuite("testReadTransactionIsolation_UpdateAll_Flush"));
        suite.addTest(new EntityManagerJUnitTestSuite("testReadTransactionIsolation_UpdateAll"));
        suite.addTest(new EntityManagerJUnitTestSuite("testReadTransactionIsolation_CustomUpdate_Refresh_Flush"));
        suite.addTest(new EntityManagerJUnitTestSuite("testReadTransactionIsolation_CustomUpdate_Refresh"));
        suite.addTest(new EntityManagerJUnitTestSuite("testReadTransactionIsolation_CustomUpdate_Flush"));
        suite.addTest(new EntityManagerJUnitTestSuite("testReadTransactionIsolation_CustomUpdate"));
        suite.addTest(new EntityManagerJUnitTestSuite("testClearInTransaction"));
        suite.addTest(new EntityManagerJUnitTestSuite("testClearWithFlush"));
        suite.addTest(new EntityManagerJUnitTestSuite("testClear"));
        suite.addTest(new EntityManagerJUnitTestSuite("testCheckVersionOnMerge"));
        suite.addTest(new EntityManagerJUnitTestSuite("testFindWithNullPk"));
        suite.addTest(new EntityManagerJUnitTestSuite("testFindWithWrongTypePk"));
        suite.addTest(new EntityManagerJUnitTestSuite("testPersistManagedNoException"));
        suite.addTest(new EntityManagerJUnitTestSuite("testPersistManagedException"));
        suite.addTest(new EntityManagerJUnitTestSuite("testPersistRemoved"));
        suite.addTest(new EntityManagerJUnitTestSuite("testREADLock"));
        suite.addTest(new EntityManagerJUnitTestSuite("testIgnoreRemovedObjectsOnDatabaseSync"));
        suite.addTest(new EntityManagerJUnitTestSuite("testIdentityOutsideTransaction"));
        suite.addTest(new EntityManagerJUnitTestSuite("testIdentityInsideTransaction"));
        suite.addTest(new EntityManagerJUnitTestSuite("testDatabaseSyncNewObject"));
        suite.addTest(new EntityManagerJUnitTestSuite("testSetRollbackOnly"));
        suite.addTest(new EntityManagerJUnitTestSuite("testFlushModeEmCommitQueryAuto"));
        suite.addTest(new EntityManagerJUnitTestSuite("testFlushModeEmCommit"));
        suite.addTest(new EntityManagerJUnitTestSuite("testFlushModeEmCommitQueryCommit"));
        suite.addTest(new EntityManagerJUnitTestSuite("testFlushModeEmAutoQueryAuto"));
        suite.addTest(new EntityManagerJUnitTestSuite("testFlushModeEmAuto"));
        suite.addTest(new EntityManagerJUnitTestSuite("testFlushModeEmAutoQueryCommit"));
        suite.addTest(new EntityManagerJUnitTestSuite("testCacheUsage"));
        suite.addTest(new EntityManagerJUnitTestSuite("testSequencePreallocationUsingCallbackTest"));
        suite.addTest(new EntityManagerJUnitTestSuite("testForceSQLExceptionFor219097"));
        suite.addTest(new EntityManagerJUnitTestSuite("testRefreshInvalidateDeletedObject"));
        suite.addTest(new EntityManagerJUnitTestSuite("testClearWithFlush2"));
        suite.addTest(new EntityManagerJUnitTestSuite("testEMFWrapValidationException"));
        suite.addTest(new EntityManagerJUnitTestSuite("testEMDefaultTxType"));
        suite.addTest(new EntityManagerJUnitTestSuite("testMergeNewObject"));
        suite.addTest(new EntityManagerJUnitTestSuite("testMergeNewObject2"));
        suite.addTest(new EntityManagerJUnitTestSuite("testMergeNewObject3_UseSequencing"));
        suite.addTest(new EntityManagerJUnitTestSuite("testMergeNewObject3_DontUseSequencing"));
        suite.addTest(new EntityManagerJUnitTestSuite("testCreateEntityManagerFactory"));
        suite.addTest(new EntityManagerJUnitTestSuite("testCreateEntityManagerFactory2"));
        suite.addTest(new EntityManagerJUnitTestSuite("testPessimisticLockHintStartsTransaction"));
        suite.addTest(new EntityManagerJUnitTestSuite("testManyToOnePersistCascadeOnFlush"));
        suite.addTest(new EntityManagerJUnitTestSuite("testDiscoverNewReferencedObject"));
        suite.addTest(new EntityManagerJUnitTestSuite("testBulkDeleteThenMerge"));
        suite.addTest(new EntityManagerJUnitTestSuite("testNativeSequences"));
        suite.addTest(new EntityManagerJUnitTestSuite("testGetReference"));
        suite.addTest(new EntityManagerJUnitTestSuite("testGetReferenceUpdate"));
        suite.addTest(new EntityManagerJUnitTestSuite("testGetReferenceUsedInUpdate"));
        suite.addTest(new EntityManagerJUnitTestSuite("testBadGetReference"));
        suite.addTest(new EntityManagerJUnitTestSuite("testClassInstanceConverter"));
        suite.addTest(new EntityManagerJUnitTestSuite("test210280EntityManagerFromPUwithSpaceInNameButNotInPath"));
        suite.addTest(new EntityManagerJUnitTestSuite("test210280EntityManagerFromPUwithSpaceInPathButNotInName"));
        suite.addTest(new EntityManagerJUnitTestSuite("test210280EntityManagerFromPUwithSpaceInNameAndPath"));
        suite.addTest(new EntityManagerJUnitTestSuite("testNewObjectNotCascadePersist"));
        suite.addTest(new EntityManagerJUnitTestSuite("testConnectionPolicy"));
        suite.addTest(new EntityManagerJUnitTestSuite("testConverterIn"));
        return suite;
    }

    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());
    }
    
    /**
     * Bug# 219097
     * This test would normally pass, but we purposely invoke an SQLException on the firstName field
     * so that we can test that an UnsupportedOperationException is not thrown as part of the 
     * roll-back exception handling code for an SQLException.
     */
    public void testForceSQLExceptionFor219097() {
    	boolean exceptionThrown = false;
    	// Set an immutable properties Map on the em to test addition of properties to this map in the roll-back exception handler
        EntityManager em = createEntityManager(Collections.emptyMap());
        beginTransaction(em);
        
        Employee emp = new Employee();
        /*
         * Provoke an SQL exception by setting a field with a value greater than field length.
         * 1 - This test will not throw an exception without the Collections$emptyMap() set on the EntityhManager
         *       or the exceeded field length set on firstName.
         * 2 - This test will throw an UnsupportedOperationException if the map on AbstractSession is not cloned when immutable - bug fix
         * 3 - This test will throw an SQLException when operating normally due to the field length exception
         */
        StringBuffer firstName = new StringBuffer("firstName_maxfieldLength_");
        for(int i=0; i<MAX_FIRST_NAME_FIELD_LENGTH + 100; i++) {
        	firstName.append("0");
        }
        emp.setFirstName(firstName.toString());        
        em.persist(emp);

        try {
        	commitTransaction(em);
        } catch (Exception e) {
        	Throwable cause = e.getCause();
        	if(cause instanceof UnsupportedOperationException) {
        		exceptionThrown = true;
        		fail(cause.getClass() + " Exception was thrown in error instead of expected SQLException.");
        	} else {
        		exceptionThrown = true;
        	}
        } finally {
        	closeEntityManager(em);
        }
        if(!exceptionThrown) {
        	fail("An expected SQLException was not thrown.");
        }
    }
    
    // JUnit framework will automatically execute all methods starting with test...    
    public void testRefreshNotManaged() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp = new Employee();
        emp.setFirstName("testRefreshNotManaged");
        try {
            em.refresh(emp);
            fail("entityManager.refresh(notManagedObject) didn't throw exception");
        } catch (IllegalArgumentException illegalArgumentException) {
            // expected behavior
        } catch (Exception exception ) {
            fail("entityManager.refresh(notManagedObject) threw a wrong exception: " + exception.getMessage());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testRefreshRemoved() {
        // find an existing or create a new Employee
        String firstName = "testRefreshRemoved";
        Employee emp;
        EntityManager em = createEntityManager();
        List result = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.firstName = '"+firstName+"'").getResultList();
        if(!result.isEmpty()) {
            emp = (Employee)result.get(0);
        } else {
            emp = new Employee();
            emp.setFirstName(firstName);
            // persist the Employee
            try{
                beginTransaction(em);
                em.persist(emp);
                commitTransaction(em);
            }catch (RuntimeException ex){
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                throw ex;
            }
        }
        
        try{
            beginTransaction(em);
            emp = em.find(Employee.class, emp.getId());
            
            // delete the Employee from the db
            em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();

            // refresh the Employee - should fail with EntityNotFoundException
            em.refresh(emp);
            fail("entityManager.refresh(removedObject) didn't throw exception");
        } catch (EntityNotFoundException entityNotFoundException) {
            rollbackTransaction(em);
            // expected behavior
        } catch (Exception exception ) {
            rollbackTransaction(em);
            fail("entityManager.refresh(removedObject) threw a wrong exception: " + exception.getMessage());
        }
    }

    //Bug5955326, refresh should invalidate the shared cached object that was deleted outside of JPA.
    public void testRefreshInvalidateDeletedObject(){
        EntityManager em1 = createEntityManager();
        EntityManager em2 = createEntityManager();
        Address address = new Address();
        address.setCity("Kanata");
        // persist the Address
        try {
            
            //Ensure shared cache being used.
            boolean isIsolated = ((EntityManagerImpl)em1).getServerSession().getClassDescriptorForAlias("Address").isIsolated();
            if(isIsolated){
                throw new Exception("This test should use non-isolated cache setting class descriptor for test.");
            }
            
            em1.getTransaction().begin();
            em1.persist(address);
            em1.getTransaction().commit();
            
            //Cache the Address
            em1 = createEntityManager();
            em1.getTransaction().begin();
            address = em1.find(Address.class, address.getId());

            // Delete Address outside of JPA so that the object still stored in the cache.
            em2 = createEntityManager();
            em2.getTransaction().begin();
            em2.createNativeQuery("DELETE FROM CMP3_ADDRESS where address_id = ?1").setParameter(1, address.getId()).executeUpdate();
            em2.getTransaction().commit();
            
            //Call refresh to invalidate the object
            em1.refresh(address);
        }catch (Exception e){
            //expected exception
        } finally{
            if (em1.getTransaction().isActive()) {
                em1.getTransaction().rollback();
            }
        }
        
        //Verify
        em1.getTransaction().begin();
        address=em1.find(Address.class, address.getId());
        em1.getTransaction().commit();
        
        assertNull("The deleted object is still valid in share cache", address);
        
    }


    public void testCacheUsage() {
        EntityManager em = createEntityManager();
        Employee emp = new Employee();
        emp.setFirstName("Mark");
        // persist the Employee
        try {
            beginTransaction(em);
            em.persist(emp);
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw ex;
        }
    	clearCache();
        // Create new entity manager to avoid extended uow of work cache hits.
        em = createEntityManager();
        beginTransaction(em);
        List result = em.createQuery("SELECT OBJECT(e) FROM Employee e").getResultList();
        commitTransaction(em);
        Object obj = ((org.eclipse.persistence.jpa.JpaEntityManager)em).getServerSession().getIdentityMapAccessor().getFromIdentityMap(result.get(0));
        assertTrue("Failed to load the object into the shared cache when there were no changes in the UOW", obj != null);
        try{
            beginTransaction(em);
            emp = em.find(Employee.class, emp.getId());
            em.remove(emp);
            commitTransaction(em);
        } catch (RuntimeException exception) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw exception;
        }
    }
    
    public void testContainsRemoved() {
        // find an existing or create a new Employee
        String firstName = "testContainsRemoved";
        Employee emp;
        EntityManager em = createEntityManager();
        List result = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.firstName = '"+firstName+"'").getResultList();
        if(!result.isEmpty()) {
            emp = (Employee)result.get(0);
        } else {
            emp = new Employee();
            emp.setFirstName(firstName);
            // persist the Employee
            try{
                beginTransaction(em);
                em.persist(emp);
                commitTransaction(em);
            }catch (RuntimeException ex){
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                throw ex;
            }
        }
        
        boolean containsRemoved = true;
        try{
            beginTransaction(em);
            emp = em.find(Employee.class, emp.getId());
            em.remove(emp);
            containsRemoved = em.contains(emp);
            commitTransaction(em);
        }catch (RuntimeException t){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw t;
        }
        
        assertFalse("entityManager.contains(removedObject)==true ", containsRemoved);
    }

    public void testFlushModeEmAutoQueryCommit() {
        internalTestFlushMode(FlushModeType.AUTO, FlushModeType.COMMIT);
    }
    
    public void testFlushModeEmAuto() {
        internalTestFlushMode(FlushModeType.AUTO, null);
    }
    
    public void testFlushModeEmAutoQueryAuto() {
        internalTestFlushMode(FlushModeType.AUTO, FlushModeType.AUTO);
    }
    
    public void testFlushModeEmCommitQueryCommit() {
        internalTestFlushMode(FlushModeType.COMMIT, FlushModeType.COMMIT);
    }
    
    public void testFlushModeEmCommit() {
        internalTestFlushMode(FlushModeType.COMMIT, null);
    }
    
    public void testFlushModeEmCommitQueryAuto() {
        internalTestFlushMode(FlushModeType.COMMIT, FlushModeType.AUTO);
    }
    
    public void internalTestFlushMode(FlushModeType emFlushMode, FlushModeType queryFlushMode) {
        // create a new Employee
        String firstName = "testFlushMode";

        // make sure no Employee with the specified firstName exists.
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
            em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw ex;
        }
        clearCache();
        
        Employee emp;
        FlushModeType emFlushModeOriginal = em.getFlushMode();
        // create a new Employee
        emp = new Employee();
        emp.setFirstName(firstName);
        boolean flushed = true;
        Employee result = null;
        try{
            beginTransaction(em);
            Query query = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.firstName like '"+firstName+"'");
            if(queryFlushMode != null) {
                query.setFlushMode(queryFlushMode);
            }
            emFlushModeOriginal = em.getFlushMode();
            em.setFlushMode(emFlushMode);
            em.persist(emp);
            result = (Employee) query.getSingleResult();
        } catch (javax.persistence.NoResultException ex) {
            // failed to flush to database
            flushed = false;
        } finally {
            rollbackTransaction(em);
            em.setFlushMode(emFlushModeOriginal);
        }
        
        boolean shouldHaveFlushed;
        if(queryFlushMode != null) {
            shouldHaveFlushed = queryFlushMode == FlushModeType.AUTO;
        } else {
            shouldHaveFlushed = emFlushMode == FlushModeType.AUTO;
        }
        if(shouldHaveFlushed != flushed) {
            if(flushed) {
                fail("Flushed to database");
            } else {
                fail("Failed to flush to database");
            }
        }
        
    }

    public void testFlushModeOnUpdateQuery() {
        // find an existing or create a new Employee
        String firstName = "testFlushModeOnUpdateQuery";
        Employee emp;
        EntityManager em = createEntityManager();
        emp = new Employee();
        emp.setFirstName(firstName);
        try{
            try{
                beginTransaction(em);
                Query readQuery = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.phoneNumbers IS EMPTY and e.firstName like '"+firstName+"'");
                Query updateQuery = em.createQuery("UPDATE Employee e set e.salary = 100 where e.firstName like '" + firstName + "'");
                updateQuery.setFlushMode(FlushModeType.AUTO);
                em.persist(emp);
                updateQuery.executeUpdate();
                Employee result = (Employee) readQuery.getSingleResult();
            }catch (javax.persistence.EntityNotFoundException ex){
                rollbackTransaction(em);
                fail("Failed to flush to database");
            }
            em.refresh(emp);
            assertTrue("Failed to flush to Database", emp.getSalary() == 100);
            em.remove(emp);
            commitTransaction(em);
        }catch(RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw ex;
        }
    }

    public void testSetRollbackOnly(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            Employee emp = new Employee();
            emp.setFirstName("Bob");
            emp.setLastName("Fisher");
            em.persist(emp);
            emp = new Employee();
            emp.setFirstName("Anthony");
            emp.setLastName("Walace");
            em.persist(emp);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        List result = em.createQuery("SELECT e FROM Employee e").getResultList();
        Employee emp = (Employee)result.get(0);
        Employee emp2 = (Employee)result.get(1);
        String newName = ""+System.currentTimeMillis();
        emp2.setFirstName(newName);
        em.flush();
        emp2.setLastName("Whatever");
        emp2.setVersion(0);
        try{
            em.flush();
        }catch (Exception ex){
            em.clear(); //prevent the flush again
            String eName = (String)em.createQuery("SELECT e.firstName FROM Employee e where e.id = " + emp2.getId()).getSingleResult();
            assertTrue("Failed to keep txn open for set RollbackOnly", eName.equals(newName));
        }
        try{
            if (isOnServer()) {
                assertTrue("Failed to mark txn rollback only", !isTransactionActive(em));
            } else {
                assertTrue("Failed to mark txn rollback only", em.getTransaction().getRollbackOnly());
            }
        } finally{
            try{
                commitTransaction(em);
            }catch (RollbackException ex){
                return;    
            }catch (RuntimeException ex){
                if (ex.getCause() instanceof javax.transaction.RollbackException) {
                    return;
                }
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                throw ex;                
            }
        }
        fail("Failed to throw rollback exception");
    }
    
    public void testSubString() {
        // find an existing or create a new Employee
        String firstName = "testSubString";
        Employee emp;
        EntityManager em = createEntityManager();
        List result = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.firstName = '"+firstName+"'").getResultList();
        if(!result.isEmpty()) {
            emp = (Employee)result.get(0);
        } else {
            emp = new Employee();
            emp.setFirstName(firstName);
            // persist the Employee
            try{
                beginTransaction(em);
                em.persist(emp);
                commitTransaction(em);
            }catch (RuntimeException ex){
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                throw ex;
            }
        }
        
        int firstIndex = 1;
        int lastIndex = firstName.length();
        List employees = em.createQuery("SELECT object(e) FROM Employee e where e.firstName = substring(:p1, :p2, :p3)").
            setParameter("p1", firstName).
            setParameter("p2", new Integer(firstIndex)).
            setParameter("p3", new Integer(lastIndex)).
            getResultList();
            
        // clean up
        try{
            beginTransaction(em);
            em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw ex;
        }

        assertFalse("employees.isEmpty()==true ", employees.isEmpty());
    }
    
    public void testNewObjectNotCascadePersist(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Golfer g = new Golfer();
        WorldRank wr = new WorldRank();
        g.setWorldRank(wr);
        em.persist(g);
        try{
            em.flush();
        }catch (PersistenceException ex){
            assertTrue("Failed to throw IllegalStateException see bug: 237279 ", ex.getCause() instanceof IllegalStateException);
        }finally{
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        
    }
    
    public void testDatabaseSyncNewObject() {
        EntityManager em = createEntityManager();

        beginTransaction(em);

        try{
            Project project = new LargeProject();
            em.persist(project);
            project.setName("Blah");
            project.setTeamLeader(new Employee());
            project.getTeamLeader().addProject(project);
            em.flush();
        }catch (PersistenceException ex){
            rollbackTransaction(em);
            if (ex.getCause() instanceof IllegalStateException)
            return;
        }
        
        fail("Failed to throw illegal argument when finding unregistered new object cascading on database sync");

    }

    public void testTransactionRequired() {
        String firstName = "testTransactionRequired";
        Employee emp = new Employee();
        emp.setFirstName(firstName);
        
        String noException = "";
        String wrongException = "";
        
        try {
            createEntityManager().flush();
            noException = noException + " flush;";
        } catch (TransactionRequiredException transactionRequiredException) {
            // expected behavior
        } catch (RuntimeException ex) {
            wrongException = wrongException + " flush: " + ex.getMessage() +";";
        }

        String errorMsg = "";
        if(noException.length() > 0) {
            errorMsg = "No exception thrown: " + noException;
        }
        if(wrongException.length() > 0) {
            if(errorMsg.length() > 0) {
                errorMsg = errorMsg + " ";
            }
            errorMsg = errorMsg + "Wrong exception thrown: " + wrongException;
        }
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testIdentityInsideTransaction() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Query query = em.createQuery("SELECT e FROM PhoneNumber e");
        List<PhoneNumber> phoneNumbers = query.getResultList();
        for (PhoneNumber phoneNumber : phoneNumbers) {
            Employee emp = phoneNumber.getOwner();
            Collection<PhoneNumber> numbers = emp.getPhoneNumbers();
            assertTrue(numbers.contains(phoneNumber));
        }
        
        commitTransaction(em);
        closeEntityManager(em);
    }

    public void testIdentityOutsideTransaction() {
        EntityManager em = createEntityManager();
        
        Query query = em.createQuery("SELECT e FROM PhoneNumber e");
        List<PhoneNumber> phoneNumbers = query.getResultList();
        for (PhoneNumber phoneNumber : phoneNumbers) {
            Employee emp = phoneNumber.getOwner();
            Collection<PhoneNumber> numbers = emp.getPhoneNumbers();
            assertTrue(numbers.contains(phoneNumber));
        }
        
        closeEntityManager(em);
    } 
    
    public void testIgnoreRemovedObjectsOnDatabaseSync() {
        EntityManager em = createEntityManager();

        beginTransaction(em);
        Query phoneQuery = em.createQuery("Select p from PhoneNumber p where p.owner.lastName like 'Dow%'");
        Query empQuery = em.createQuery("Select e FROM Employee e where e.lastName like 'Dow%'");
        //--setup
        try{
            Employee emp = new Employee();
            emp.setLastName("Dowder");
            PhoneNumber phone = new PhoneNumber("work", "613", "5555555");
            emp.addPhoneNumber(phone);
            phone = new PhoneNumber("home", "613", "4444444");
            emp.addPhoneNumber(phone);
            Address address = new Address("SomeStreet", "somecity", "province", "country", "postalcode");
            emp.setAddress(address);
            em.persist(emp);
            em.flush();
    
            emp = new Employee();
            emp.setLastName("Dows");
            phone = new PhoneNumber("work", "613", "2222222");
            emp.addPhoneNumber(phone);
            phone = new PhoneNumber("home", "613", "1111111");
            emp.addPhoneNumber(phone);
            address = new Address("street1", "city1", "province1", "country1", "postalcode1");
            emp.setAddress(address);
            em.persist(emp);
            em.flush();
            //--end setup
    
            List<Employee> emps = empQuery.getResultList();
    
            List phones = phoneQuery.getResultList();
            for (Iterator iterator = phones.iterator(); iterator.hasNext();){
                em.remove(iterator.next());
            }
            em.flush();
            
            for (Iterator<Employee> iterator = emps.iterator(); iterator.hasNext();){
                em.remove(iterator.next());
            }
        }catch (RuntimeException ex){
            rollbackTransaction(em);
            throw ex;
        }
        try{
            em.flush();
        }catch (IllegalStateException ex){
            rollbackTransaction(em);
            closeEntityManager(em);
            em = createEntityManager();
            beginTransaction(em);
            try{
                phoneQuery = em.createQuery("Select p from PhoneNumber p where p.owner.lastName like 'Dow%'");
                empQuery = em.createQuery("Select e from Employee e where e.lastName like 'Dow%'");
                List<Employee> emps =  empQuery.getResultList();
                List phones = phoneQuery.getResultList();
                for (Iterator iterator = phones.iterator(); iterator.hasNext();){
                    em.remove(iterator.next());
                }
                for (Iterator<Employee> iterator = emps.iterator(); iterator.hasNext();){
                    em.remove(iterator.next());
                }
                commitTransaction(em);
            }catch (RuntimeException re){
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                throw re;
            }
            fail("Failed to ignore the removedobject when cascading on database sync");
        }
        
        commitTransaction(em);
    }
    
    public void testREADLock(){
        // Cannot create parallel entity managers in the server.
        if (isOnServer()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee employee = null;
        try{
            employee = new Employee();
            employee.setFirstName("Mark");
            employee.setLastName("Madsen");
            em.persist(employee);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        EntityManager em2 = createEntityManager();
        Exception optimisticLockException = null;
       
        beginTransaction(em);
        try{
            employee = em.find(Employee.class, employee.getId());
            em.lock(employee, LockModeType.READ);
            em2.getTransaction().begin();
            try{
                Employee employee2 = em2.find(Employee.class, employee.getId());
                employee2.setFirstName("Michael");
                em2.getTransaction().commit();
                em2.close();
            }catch (RuntimeException ex){
                em2.getTransaction().rollback();
                em2.close();
                throw ex;
            }
        
            try{
                em.flush();
            } catch (PersistenceException exception) {
                if (exception.getCause() instanceof OptimisticLockException){
                    optimisticLockException = exception;
                }else{
                    throw exception;
                }
            }
            rollbackTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        beginTransaction(em);
        try{
            employee = em.find(Employee.class, employee.getId());
            em.remove(employee);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        
        if (optimisticLockException == null){
            fail("Proper exception not thrown when EntityManager.lock(object, READ) is used.");
        }
    }
    
    // test for bug 4676587: 
    // CTS: AFTER A REMOVE THEN A PERSIST ON THE SAME ENTITY, CONTAINS RETURNS FALSE
    // The test performs persist, remove, persist sequence on a single object
    // in different "flavours":
    // doTransaction - the first persist happens in a separate transaction;
    // doFirstFlush - perform flush after the first persist;
    // doSecondFlush - perform flush after the remove;
    // doThirdFlush - perform flush after the second persist;
    // doRollback - rollbacks transaction that contains remove and the second persist.
    public void testPersistRemoved() {
        // create an Employee
        String firstName = "testPesistRemoved";
        Employee emp = new Employee();
        emp.setFirstName(firstName);

        // make sure no Employee with the specified firstName exists.
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }

        String errorMsg = "";
        for (int i=0; i < 32; i++) {
            int j = i;
            boolean doRollback = j % 2 == 0;
            j = j/2;
            boolean doThirdFlush = j % 2 == 0;
            j = j/2;
            boolean doSecondFlush = j % 2 == 0;
            j = j/2;
            boolean doFirstFlush = j % 2 == 0;
            j = j/2;
            boolean doTransaction = j % 2 == 0;
            if(doTransaction && doFirstFlush) {
                continue;
            }
            String msg = "";
            if(doTransaction) {
                msg = "Transaction ";
            }
            if(doFirstFlush) {
                msg = msg + "firstFlush ";
            }
            if(doSecondFlush) {
                msg = msg + "secondFlush ";
            }
            if(doThirdFlush) {
                msg = msg + "thirdFlush ";
            }
            if(doRollback) {
                msg = msg + "RolledBack ";
            }

            String localErrorMsg = msg;
            boolean exceptionWasThrown = false;
            Integer empId = null;
            beginTransaction(em);            
            try {
                emp = new Employee();
                emp.setFirstName(firstName);

                // persist the Employee
                em.persist(emp);
                if(doTransaction) {
                    commitTransaction(em);
                    empId = emp.getId();
                    beginTransaction(em);
                } else {
                    if(doFirstFlush) {
                        em.flush();
                    }
                }
        
                if(doTransaction) {
                    emp = em.find(Employee.class, empId);
                }
                // remove the Employee
                em.remove(emp);
                if(doSecondFlush) {
                    em.flush();
                }
        
                // persist the Employee
                em.persist(emp);
                if(doThirdFlush) {
                    em.flush();
                }
            } catch (RuntimeException ex) {
                rollbackTransaction(em);
                localErrorMsg = localErrorMsg + " " + ex.getMessage() + ";";
                exceptionWasThrown = true;
            }
        
            boolean employeeShouldExist = doTransaction || !doRollback;
            boolean employeeExists = false;
            try{
                if(!exceptionWasThrown) {
                    if(doRollback) {
                        rollbackTransaction(em);
                    } else {
                        commitTransaction(em);
                    }
                    
                    if(doTransaction) {
                        Employee employeeReadFromCache = em.find(Employee.class, empId);
                        if(employeeReadFromCache == null) {
                            localErrorMsg = localErrorMsg + " employeeReadFromCache == null;";
                        }
                    }
                    
                    List resultList = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.firstName = '"+firstName+"'").getResultList();
                    employeeExists = resultList.size() > 0;
                    
                    if(employeeShouldExist) {
                        if(resultList.size() > 1) {
                            localErrorMsg = localErrorMsg + " resultList.size() > 1";
                        }
                        if(!employeeExists) {
                            localErrorMsg = localErrorMsg + " employeeReadFromDB == null;";
                        }
                    } else {
                        if(resultList.size() > 0) {
                            localErrorMsg = localErrorMsg + " employeeReadFromDB != null;";
                        }
                    }
                }
            }catch (RuntimeException ex){
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                throw ex;
            }
            
            // clean up
            if(employeeExists || exceptionWasThrown) {
                em = createEntityManager();
                beginTransaction(em);
                try{
                    em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
                    commitTransaction(em);
                }catch (RuntimeException ex){
                    rollbackTransaction(em);
                    throw ex;
                }
            }
            
            if(!msg.equals(localErrorMsg)) {
                errorMsg = errorMsg + "i="+Integer.toString(i)+": "+ localErrorMsg + " ";
            }
        }
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    public void testPersistManagedException(){       
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp = new Employee();
        em.persist(emp);
        em.flush();
        Integer id = emp.getId();
        emp = new Employee();
        emp.setId(id);
        boolean caughtException = false;
        try{
            em.persist(emp);
        } catch (EntityExistsException e){
            caughtException = true;
        }
        emp = em.find(Employee.class, id);
        em.remove(emp);
        rollbackTransaction(em);
        assertTrue("EntityExistsException was not thrown for an existing Employee.", caughtException);
    }
    
    public void testPersistManagedNoException(){       
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp = new Employee();
        em.persist(emp);
        em.flush();
        Integer id = emp.getId();
        Address address = new Address();
        emp.setAddress(address);
        boolean caughtException = false;
        try{
            em.persist(emp);
        } catch (EntityExistsException e){
            caughtException = true;
        }
        emp = em.find(Employee.class, id);
        em.remove(emp);
        commitTransaction(em);
        assertFalse("EntityExistsException was thrown for a registered Employee.", caughtException);
    }

    // test for bug 4676587: 
    // CTS: AFTER A REMOVE THEN A PERSIST ON THE SAME ENTITY, CONTAINS RETURNS FALSE
    public void testRemoveFlushPersistContains() {
        // create an Employee
        String firstName = "testRemoveFlushPersistContains";
        Employee emp = new Employee();
        emp.setFirstName(firstName);

        // make sure no Employee with the specified firstName exists.
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }

        // persist
        beginTransaction(em);
        try{
            em.persist(emp);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        // remove, flush, persist, contains
        boolean contains = false;
        beginTransaction(em);
        try{
            emp = em.find(Employee.class, emp.getId());
            em.remove(emp); 
            em.flush(); 
            em.persist(emp); 
            contains = em.contains(emp);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        
        // clean up
        beginTransaction(em);
        try{
            em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        
        assertTrue("contains==false", contains);
    }
    
    // test for bug 4742161: 
    // CTS: OBJECTS REMOVED AND THEN FLUSHED ARE RETURNED BY QUERIES AFTER THE FLUSH
    public void testRemoveFlushFind() {
        // create an Employee
        String firstName = "testRemoveFlushFind";
        Employee emp = new Employee();
        emp.setFirstName(firstName);

        // make sure no Employee with the specified firstName exists.
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }

        // persist
        beginTransaction(em);
        try{
            em.persist(emp);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        
        // remove, flush, persist, contains
        boolean foundAfterFlush = true;
        boolean foundBeforeFlush = true;
        beginTransaction(em);
        try{
            emp = em.find(Employee.class, emp.getId());
            em.remove(emp); 
            Employee empFound = em.find(Employee.class, emp.getId());
            foundBeforeFlush = empFound != null;
            em.flush(); 
            empFound = em.find(Employee.class, emp.getId());
             foundAfterFlush = empFound != null;
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        
        // clean up
        beginTransaction(em);
        try{
            em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        
        assertFalse("removed object found", foundBeforeFlush);
        assertFalse("removed object found after flush", foundAfterFlush);
    }
    
    // test for bug 4681287: 
    // CTS: EXCEPTION EXPECTED ON FIND() IF PK PASSED IN != ATTRIBUTE TYPE
    public void testFindWithWrongTypePk() {
        EntityManager em = createEntityManager();
        try {
            em.find(Employee.class, "1");
        } catch (IllegalArgumentException ilEx) {
            return;
        } catch (Exception ex) {
            fail("Wrong exception thrown: " + ex.getMessage());
            return;
        }finally{
            closeEntityManager(em);
        }
        fail("No exception thrown");
    }
    
    //test for gf721 - IllegalArgumentException expected for null PK
    public void testFindWithNullPk() {
        EntityManager em = createEntityManager();
        try {
            em.find(Employee.class, null);
        } catch (IllegalArgumentException iae) {
            return;
        } catch (Exception e) {
            fail("Wrong exception type thrown: " + e.getClass());
        }finally{
            closeEntityManager(em);
        }
        fail("No exception thrown when null PK used in find operation.");
    }

    public void testCheckVersionOnMerge() {
        Employee employee = new Employee();
        employee.setFirstName("Marc");
        
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
            em.persist(employee);
            commitTransaction(em);
            em.clear();
            beginTransaction(em);
            Employee empClone = em.find(Employee.class, employee.getId());
            empClone.setFirstName("Guy");
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            fail("Exception caught during test setup " + ex);
        }
        
        try {
            beginTransaction(em);
            em.merge(employee);
            commitTransaction(em);
        } catch (OptimisticLockException e) {
            rollbackTransaction(em);
            closeEntityManager(em);
            return;
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            fail("Wrong exception thrown: " + ex.getMessage());
        }
            
        fail("No exception thrown");
    }
    
    public void testClear(){
        Employee employee = new Employee();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            em.persist(employee);
            commitTransaction(em);
            em.clear();
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        boolean cleared = !em.contains(employee);
        closeEntityManager(em);
        assertTrue("EntityManager not properly cleared", cleared);
    }
    
    public void testClearWithFlush(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            Employee emp = new Employee();
            emp.setFirstName("Douglas");
            emp.setLastName("McRae");
            em.persist(emp);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        clearCache();
        EntityManager localEm = createEntityManager();
        localEm.getTransaction().begin();
        Employee emp = null;
        String originalName = "";
        boolean cleared, updated, reset = false;
        try{
            Query query = localEm.createQuery("Select e from Employee e where e.firstName is not null");
            emp = (Employee)query.getResultList().get(0);
            originalName = emp.getFirstName();
            emp.setFirstName("Bobster");
            localEm.flush();
            localEm.clear();
            //this test is testing the cache not the database
            localEm.getTransaction().commit();
            cleared = !localEm.contains(emp);
            emp = localEm.find(Employee.class, emp.getId());
            updated = emp.getFirstName().equals("Bobster");
            localEm.close();
        }catch (RuntimeException ex){
            localEm.getTransaction().rollback();
            localEm.close();
            throw ex;
        }finally{
            //now clean up
            localEm = createEntityManager();
            localEm.getTransaction().begin();
            emp = localEm.find(Employee.class, emp.getId());
            emp.setFirstName(originalName);
            localEm.getTransaction().commit();
            emp = localEm.find(Employee.class, emp.getId());
            reset = emp.getFirstName().equals(originalName);
            localEm.close();
        }
        assertTrue("EntityManager not properly cleared", cleared);
        assertTrue("flushed data not merged", updated);
        assertTrue("unable to reset", reset);
    }
    
    // gf3596: transactions never release memory until commit, so JVM eventually crashes
    // The test verifies that there's no stale data read after transaction.
    // Because there were no TopLinkProperties.FLUSH_CLEAR_CACHE property passed
    // while creating either EM or EMF the tested behavior corresponds to
    // the default property value FlushClearCache.DropInvalidate.
    // Note that the same test would pass with FlushClearCache.Merge
    // (in that case all changes are merges into the shared cache after transaction committed),
    // but the test would fail with FlushClearCache.Drop - that mode just drops em cache
    // without doing any invalidation of the shared cache.
    public void testClearWithFlush2() {
        String firstName = "testClearWithFlush2";
        
        // setup
        // create employee and manager - and then remove them from the shared cache
        EntityManager em = createEntityManager();
        int employee_1_NotInCache_id = 0;
        int employee_2_NotInCache_id = 0;
        int manager_NotInCache_id = 0;
        beginTransaction(em);
        try {
            Employee employee_1_NotInCache = new Employee();
            employee_1_NotInCache.setFirstName(firstName);
            employee_1_NotInCache.setLastName("Employee_1_NotInCache");
            
            Employee employee_2_NotInCache = new Employee();
            employee_2_NotInCache.setFirstName(firstName);
            employee_2_NotInCache.setLastName("Employee_2_NotInCache");
            
            Employee manager_NotInCache = new Employee();
            manager_NotInCache.setFirstName(firstName);
            manager_NotInCache.setLastName("Manager_NotInCache");
            // employee_1 is manager, employee_2 is not
            manager_NotInCache.addManagedEmployee(employee_1_NotInCache);
            
            // persist
            em.persist(manager_NotInCache);
            em.persist(employee_1_NotInCache);
            em.persist(employee_2_NotInCache);
            commitTransaction(em);
            
            employee_1_NotInCache_id = employee_1_NotInCache.getId();
            employee_2_NotInCache_id = employee_2_NotInCache.getId();
            manager_NotInCache_id = manager_NotInCache.getId();
        } catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        // remove both manager_NotInCache and employee_NotInCache from the shared cache
        clearCache();
        
        // setup
        // create employee and manager - and keep them in the shared cache
        em = createEntityManager();
        int employee_1_InCache_id = 0;
        int employee_2_InCache_id = 0;
        int manager_InCache_id = 0;
        beginTransaction(em);
        try {
            Employee employee_1_InCache = new Employee();
            employee_1_InCache.setFirstName(firstName);
            employee_1_InCache.setLastName("Employee_1_InCache");
            
            Employee employee_2_InCache = new Employee();
            employee_2_InCache.setFirstName(firstName);
            employee_2_InCache.setLastName("Employee_2_InCache");
            
            Employee manager_InCache = new Employee();
            manager_InCache.setFirstName(firstName);
            manager_InCache.setLastName("Manager_InCache");
            // employee_1 is manager, employee_2 is not
            manager_InCache.addManagedEmployee(employee_1_InCache);
            
            // persist
            em.persist(manager_InCache);
            em.persist(employee_1_InCache);
            em.persist(employee_2_InCache);
            commitTransaction(em);
            
            employee_1_InCache_id = employee_1_InCache.getId();
            employee_2_InCache_id = employee_2_InCache.getId();
            manager_InCache_id = manager_InCache.getId();
        } catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }

        // test
        // create new employee and manager, change existing ones, flush, clear
        em = createEntityManager();
        int employee_1_New_id = 0;
        int employee_2_New_id = 0;
        int employee_3_New_id = 0;
        int employee_4_New_id = 0;
        int manager_New_id = 0;
        beginTransaction(em);
        try {
            Employee employee_1_New = new Employee();
            employee_1_New.setFirstName(firstName);
            employee_1_New.setLastName("Employee_1_New");
            em.persist(employee_1_New);
            employee_1_New_id = employee_1_New.getId();
            
            Employee employee_2_New = new Employee();
            employee_2_New.setFirstName(firstName);
            employee_2_New.setLastName("Employee_2_New");
            em.persist(employee_2_New);
            employee_2_New_id = employee_2_New.getId();
            
            Employee employee_3_New = new Employee();
            employee_3_New.setFirstName(firstName);
            employee_3_New.setLastName("Employee_3_New");
            em.persist(employee_3_New);
            employee_3_New_id = employee_3_New.getId();
            
            Employee employee_4_New = new Employee();
            employee_4_New.setFirstName(firstName);
            employee_4_New.setLastName("Employee_4_New");
            em.persist(employee_4_New);
            employee_4_New_id = employee_4_New.getId();
            
            Employee manager_New = new Employee();
            manager_New.setFirstName(firstName);
            manager_New.setLastName("Manager_New");
            em.persist(manager_New);
            manager_New_id = manager_New.getId();

            // find and update all objects created during setup
            Employee employee_1_NotInCache = em.find(Employee.class, employee_1_NotInCache_id);
            employee_1_NotInCache.setLastName(employee_1_NotInCache.getLastName() + "_Updated");
            Employee employee_2_NotInCache = em.find(Employee.class, employee_2_NotInCache_id);
            employee_2_NotInCache.setLastName(employee_2_NotInCache.getLastName() + "_Updated");
            Employee manager_NotInCache = em.find(Employee.class, manager_NotInCache_id);
            manager_NotInCache.setLastName(manager_NotInCache.getLastName() + "_Updated");

            Employee employee_1_InCache = em.find(Employee.class, employee_1_InCache_id);
            employee_1_InCache.setLastName(employee_1_InCache.getLastName() + "_Updated");
            Employee employee_2_InCache = em.find(Employee.class, employee_2_InCache_id);
            employee_2_InCache.setLastName(employee_2_InCache.getLastName() + "_Updated");
            Employee manager_InCache = em.find(Employee.class, manager_InCache_id);
            manager_InCache.setLastName(manager_InCache.getLastName() + "_Updated");

            manager_NotInCache.addManagedEmployee(employee_1_New);
            manager_InCache.addManagedEmployee(employee_2_New);
            
            manager_New.addManagedEmployee(employee_3_New);
            manager_New.addManagedEmployee(employee_2_NotInCache);
            manager_New.addManagedEmployee(employee_2_InCache);

            // flush
            em.flush();
            
            // clear and commit
            em.clear();
            commitTransaction(em);
        } catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }

        // verify
        String errorMsg = "";
        em = createEntityManager();

        // find and verify all objects created during setup and test

        Employee manager_NotInCache = em.find(Employee.class, manager_NotInCache_id);
        if(!manager_NotInCache.getLastName().endsWith("_Updated")) {
            errorMsg = errorMsg + "manager_NotInCache lastName NOT updated; ";
        }
        Iterator it = manager_NotInCache.getManagedEmployees().iterator();
        while(it.hasNext()) {
            Employee emp = (Employee)it.next();
            if(emp.getId() == employee_1_NotInCache_id) {
                if(!emp.getLastName().endsWith("_Updated")) {
                    errorMsg = errorMsg + "employee_1_NotInCache lastName NOT updated; ";
                }
            } else if(emp.getId() == employee_1_New_id) {
                if(!emp.getLastName().endsWith("_New")) {
                    errorMsg = errorMsg + "employee_1_New lastName wrong; ";
                }
            } else {
                errorMsg = errorMsg + "manager_NotInCache has unexpected employee: lastName = " + emp.getLastName();
            }
        }
        if(manager_NotInCache.getManagedEmployees().size() != 2) {
            errorMsg = errorMsg + "manager_NotInCache.getManagedEmployees().size() != 2; size = " + manager_NotInCache.getManagedEmployees().size();
        }

        Employee manager_InCache = em.find(Employee.class, manager_InCache_id);
        if(!manager_InCache.getLastName().endsWith("_Updated")) {
            errorMsg = errorMsg + "manager_InCache lastName NOT updated; ";
        }
        it = manager_InCache.getManagedEmployees().iterator();
        while(it.hasNext()) {
            Employee emp = (Employee)it.next();
            if(emp.getId() == employee_1_InCache_id) {
                if(!emp.getLastName().endsWith("_Updated")) {
                    errorMsg = errorMsg + "employee_1_InCache lastName NOT updated; ";
                }
            } else if(emp.getId() == employee_2_New_id) {
                if(!emp.getLastName().endsWith("_New")) {
                    errorMsg = errorMsg + "employee_2_New lastName wrong; ";
                }
            } else {
                errorMsg = errorMsg + "manager_InCache has unexpected employee: lastName = " + emp.getLastName();
            }
        }
        if(manager_InCache.getManagedEmployees().size() != 2) {
            errorMsg = errorMsg + "manager_InCache.getManagedEmployees().size() != 2; size = " + manager_InCache.getManagedEmployees().size();
        }

        Employee manager_New = em.find(Employee.class, manager_New_id);
        if(!manager_New.getLastName().endsWith("_New")) {
            errorMsg = errorMsg + "manager_New lastName wrong; ";
        }
        it = manager_New.getManagedEmployees().iterator();
        while(it.hasNext()) {
            Employee emp = (Employee)it.next();
            if(emp.getId() == employee_2_NotInCache_id) {
                if(!emp.getLastName().endsWith("_Updated")) {
                    errorMsg = errorMsg + "employee_2_NotInCache_id lastName NOT updated; ";
                }
            } else if(emp.getId() == employee_2_InCache_id) {
                if(!emp.getLastName().endsWith("_Updated")) {
                    errorMsg = errorMsg + "employee_2_InCache_id lastName NOT updated; ";
                }
            } else if(emp.getId() == employee_3_New_id) {
                if(!emp.getLastName().endsWith("_New")) {
                    errorMsg = errorMsg + "employee_3_New lastName wrong; ";
                }
            } else {
                errorMsg = errorMsg + "manager_New has unexpected employee: lastName = " + emp.getLastName();
            }
        }
        if(manager_New.getManagedEmployees().size() != 3) {
            errorMsg = errorMsg + "manager_InCache.getManagedEmployees().size() != 3; size = " + manager_InCache.getManagedEmployees().size();
        }
        Employee employee_4_New = em.find(Employee.class, employee_4_New_id);
        if(!employee_4_New.getLastName().endsWith("_New")) {
            errorMsg = errorMsg + "employee_4_New lastName wrong; ";
        }
        closeEntityManager(em);
        
        // clean up
        // remove all objects created during this test and clear the cache.
        em = createEntityManager();
        try {
            beginTransaction(em);
            List<Employee> list = em.createQuery("Select e from Employee e where e.firstName = '"+firstName+"'").getResultList();
            Iterator<Employee> i = list.iterator();
            while (i.hasNext()){
            	Employee e = i.next();
            	if (e.getManager() != null){
            		e.getManager().removeManagedEmployee(e);
            		e.setManager(null);
            	}
            	em.remove(e);
            }
            commitTransaction(em);
        } catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            // ignore exception in clean up in case there's an error in test
            if(errorMsg.length() == 0) {
                throw ex;
            }
        }
        clearCache();
        
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    /** ToDo: move these to a memory test suite.
    // gf3596: transactions never release memory until commit, so JVM eventually crashes.
    // Attempts to compare memory consumption between the two FlushClearCache modes.
    // If the values changed to be big enough (in TopLink I tried nFlashes = 30 , nInsertsPerFlush = 10000)
    // internalMassInsertFlushClear(FlushClearCache.Merge, 30, 10000) will run out of memory,
    // but internalMassInsertFlushClear(null, 30, 10000) will still be ok.
    public void testMassInsertFlushClear() {
        int nFlushes = 20;
        int nPersistsPerFlush = 50;
        long[] defaultFreeMemoryDelta = internalMassInsertFlushClear(null, nFlushes, nPersistsPerFlush);
        long[] mergeFreeMemoryDelta = internalMassInsertFlushClear(FlushClearCache.Merge, nFlushes, nPersistsPerFlush);
        // disregard the flush if any of the two FreeMemoryDeltas is negative - clearly that's gc artefact.
        int nEligibleFlushes = 0;
        long diff = 0;
        for(int nFlush = 0; nFlush < nFlushes; nFlush++) {
            if(defaultFreeMemoryDelta[nFlush] >= 0 && mergeFreeMemoryDelta[nFlush] >= 0) {
                nEligibleFlushes++;
                diff = diff + mergeFreeMemoryDelta[nFlush] - defaultFreeMemoryDelta[nFlush];
            }
        }
        long lowEstimateOfBytesPerObject = 200;
        long diffPerObject = diff / (nEligibleFlushes * nPersistsPerFlush);
        if(diffPerObject < lowEstimateOfBytesPerObject) {
            fail("difference in freememory per object persisted " + diffPerObject + " is lower than lowEstimateOfBytesPerObject " + lowEstimateOfBytesPerObject);
        }
    }
    
    // memory usage with different FlushClearCache modes.
    protected long[] internalMassInsertFlushClear(String propValue, int nFlushes, int nPersistsPerFlush) {
        // set logDebug to true to output the freeMemory values after each flush/clear
        boolean logDebug = false;
        String firstName = "testMassInsertFlushClear";
        EntityManager em;
        if(propValue == null) {
            // default value FlushClearCache.DropInvalidate will be used
            em = createEntityManager();
            if(logDebug) {
                System.out.println(FlushClearCache.DEFAULT);
            }
        } else {
            HashMap map = new HashMap(1);
            map.put(PersistenceUnitProperties.FLUSH_CLEAR_CACHE, propValue);
            em = getEntityManagerFactory().createEntityManager(map);
            if(logDebug) {
                System.out.println(propValue);
            }
        }
        // For enhance accuracy of memory measuring allocate everything first:
        // make a first run and completely disregard its results - somehow
        // that get freeMemory function to report more accurate results in the second run -
        // which is the only one used to calculate results.
        if(logDebug) {
            System.out.println("The first run is ignored");
        }
        long freeMemoryOld;
        long freeMemoryNew;
        long[] freeMemoryDelta = new long[nFlushes];
        beginTransaction(em);
        try {
            // Try to force garbage collection NOW.
            System.gc();System.gc();System.gc();System.gc();System.gc();System.gc();System.gc();System.gc();
            freeMemoryOld = Runtime.getRuntime().freeMemory();
            if(logDebug) {
                System.out.println("initial freeMemory = " + freeMemoryOld);
            }
            for(int nFlush = 0; nFlush < nFlushes; nFlush++) {
                for(int nPersist = 0; nPersist < nPersistsPerFlush; nPersist++) {
                    Employee emp = new Employee();
                    emp.setFirstName(firstName);
                    int nEmployee = nFlush * nPersistsPerFlush + nPersist;
                    emp.setLastName("lastName_" + Integer.toString(nEmployee));
                    em.persist(emp);
                }
                em.flush();
                em.clear();
                // Try to force garbage collection NOW.
                System.gc();System.gc();System.gc();System.gc();System.gc();System.gc();System.gc();System.gc();
                freeMemoryNew = Runtime.getRuntime().freeMemory();
                freeMemoryDelta[nFlush] = freeMemoryOld - freeMemoryNew;
                freeMemoryOld = freeMemoryNew;
                if(logDebug) {
                    System.out.println(nFlush +": after flush/clear freeMemory = " + freeMemoryNew);
                }
            }
        } finally {
            rollbackTransaction(em);
            em = null;
        }

        if(logDebug) {
            System.out.println("The second run");
        }
        // now allocate again - with gc and memory measuring
        if(propValue == null) {
            // default value FlushClearCache.DropInvalidate will be used
            em = createEntityManager();
        } else {
            HashMap map = new HashMap(1);
            map.put(PersistenceUnitProperties.FLUSH_CLEAR_CACHE, propValue);
            em = getEntityManagerFactory().createEntityManager(map);
        }
        beginTransaction(em);
        try {
            // Try to force garbage collection NOW.
            System.gc();System.gc();System.gc();System.gc();System.gc();System.gc();System.gc();System.gc();
            freeMemoryOld = Runtime.getRuntime().freeMemory();
            if(logDebug) {
                System.out.println("initial freeMemory = " + freeMemoryOld);
            }
            for(int nFlush = 0; nFlush < nFlushes; nFlush++) {
                for(int nPersist = 0; nPersist < nPersistsPerFlush; nPersist++) {
                    Employee emp = new Employee();
                    emp.setFirstName(firstName);
                    int nEmployee = nFlush * nPersistsPerFlush + nPersist;
                    emp.setLastName("lastName_" + Integer.toString(nEmployee));
                    em.persist(emp);
                }
                em.flush();
                em.clear();
                // Try to force garbage collection NOW.
                System.gc();System.gc();System.gc();System.gc();System.gc();System.gc();System.gc();System.gc();
                freeMemoryNew = Runtime.getRuntime().freeMemory();
                freeMemoryDelta[nFlush] = freeMemoryOld - freeMemoryNew;
                freeMemoryOld = freeMemoryNew;
                if(logDebug) {
                    System.out.println(nFlush +": after flush/clear freeMemory = " + freeMemoryNew);
                }
            }
            return freeMemoryDelta;
        } finally {
            rollbackTransaction(em);
        }
    }*/

    public void testClearInTransaction(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            Employee emp = new Employee();
            emp.setFirstName("Tommy");
            emp.setLastName("Marsh");
            em.persist(emp);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        Employee emp = null;
        String originalName = "";
        try {
            Query query = em.createQuery("Select e FROM Employee e where e.firstName is not null");
            emp = (Employee)query.getResultList().get(0);
            originalName = emp.getFirstName();
            emp.setFirstName("Bobster");
            em.clear();
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        boolean cleared = !em.contains(emp);
        emp = em.find(Employee.class, emp.getId());
        closeEntityManager(em);
        assertTrue("EntityManager not properly cleared", cleared);
        assertTrue("Employee was updated although EM was cleared", emp.getFirstName().equals(originalName));
    }
    
    public void testExtendedPersistenceContext() {
        // Extended persistence context are not supported in the server.
        // TODO: make this test use an extended entity manager in the server by creating from the factory and joining transaction.
        if (isOnServer()) {
            return;
        }
        
        String firstName = "testExtendedPersistenceContext";
        int originalSalary = 0;

        Employee empNew = new Employee();
        empNew.setFirstName(firstName);
        empNew.setLastName("new");
        empNew.setSalary(originalSalary);
        
        Employee empToBeRemoved = new Employee();
        empToBeRemoved.setFirstName(firstName);
        empToBeRemoved.setLastName("toBeRemoved");
        empToBeRemoved.setSalary(originalSalary);
        
        Employee empToBeRefreshed = new Employee();
        empToBeRefreshed.setFirstName(firstName);
        empToBeRefreshed.setLastName("toBeRefreshed");
        empToBeRefreshed.setSalary(originalSalary);
        
        Employee empToBeMerged = new Employee();
        empToBeMerged.setFirstName(firstName);
        empToBeMerged.setLastName("toBeMerged");
        empToBeMerged.setSalary(originalSalary);
        
        // setup: make sure no Employee with the specified firstName exists and create the existing employees.
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
            commitTransaction(em);
            beginTransaction(em);
            em.persist(empToBeRemoved);
            em.persist(empToBeRefreshed);
            em.persist(empToBeMerged);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        closeEntityManager(em);
        clearCache();
        
        // create entityManager with extended Persistence Context.
        em = createEntityManager();
        
        try {
            // first test
            // without starting transaction persist, remove, refresh, merge
    
            em.persist(empNew);
            
            Employee empToBeRemovedExtended = em.find(Employee.class, empToBeRemoved.getId());
            em.remove(empToBeRemovedExtended);
            
            Employee empToBeRefreshedExtended = em.find(Employee.class, empToBeRefreshed.getId());
            int newSalary = 100;
            // Use another EntityManager to alter empToBeRefreshed in the db
            beginTransaction(em);
            empToBeRefreshed = em.find(Employee.class, empToBeRefreshed.getId());
            empToBeRefreshed.setSalary(newSalary);
            commitTransaction(em);
            // now refesh
            em.refresh(empToBeRefreshedExtended);
    
            Employee empToBeMergedExtended = em.find(Employee.class, empToBeMerged.getId());
            // alter empToBeRefreshed
            empToBeMerged.setSalary(newSalary);
            // now merge
            em.merge(empToBeMerged);
    
            // begin and commit transaction
            beginTransaction(em);
            commitTransaction(em);
            
            // verify objects are correct in the PersistenceContext after transaction
            if(!em.contains(empNew)) {
                fail("empNew gone from extended PersistenceContext after transaction committed");
            }
            if(em.contains(empToBeRemovedExtended)) {
                fail("empToBeRemovedExtended still in extended PersistenceContext after transaction committed");
            }
            if(!em.contains(empToBeRefreshedExtended)) {
                fail("empToBeRefreshedExtended gone from extended PersistenceContext after transaction committed");
            } else if(empToBeRefreshedExtended.getSalary() != newSalary) {
                fail("empToBeRefreshedExtended still has the original salary after transaction committed");
            }
            if(!em.contains(empToBeMergedExtended)) {
                fail("empToBeMergedExtended gone from extended PersistenceContext after transaction committed");
            } else if(empToBeMergedExtended.getSalary() != newSalary) {
                fail("empToBeMergedExtended still has the original salary after transaction committed");
            }
    
            // verify objects are correct in the db after transaction
            clearCache();
            Employee empNewFound = em.find(Employee.class, empNew.getId());
            if(empNewFound == null) {
                fail("empNew not in the db after transaction committed");
            }
            Employee empToBeRemovedFound = em.find(Employee.class, empToBeRemoved.getId());
            if(empToBeRemovedFound != null) {
                fail("empToBeRemoved is still in the db after transaction committed");
            }
            Employee empToBeRefreshedFound = em.find(Employee.class, empToBeRefreshed.getId());
            if(empToBeRefreshedFound == null) {
                fail("empToBeRefreshed not in the db after transaction committed");
            } else if(empToBeRefreshedFound.getSalary() != newSalary) {
                fail("empToBeRefreshed still has the original salary in the db after transaction committed");
            }
            Employee empToBeMergedFound = em.find(Employee.class, empToBeMerged.getId());
            if(empToBeMergedFound == null) {
                fail("empToBeMerged not in the db after transaction committed");
            } else if(empToBeMergedFound.getSalary() != newSalary) {
                fail("empToBeMerged still has the original salary in the db after transaction committed");
            }
    
            // second test
            // without starting transaction persist, remove, refresh, merge for the second time:
            // now return to the original state of the objects:
            // remove empNew, persist empToBeRemoved, set empToBeRefreshed and empToBeMerged the original salary.
            
            em.persist(empToBeRemoved);
            em.remove(empNew);
            
            // Use another EntityManager to alter empToBeRefreshed in the db
            beginTransaction(em);
            empToBeRefreshed = em.find(Employee.class, empToBeRefreshed.getId());
            empToBeRefreshed.setSalary(originalSalary);
            commitTransaction(em);
            // now refesh
            em.refresh(empToBeRefreshedExtended);
    
            // alter empToBeRefreshedFound - can't use empToBeRefreshed here because of its older version().
            empToBeMergedFound.setSalary(originalSalary);
            // now merge
            em.merge(empToBeMergedFound);
    
            // begin and commit the second transaction
            beginTransaction(em);
            commitTransaction(em);
            
            // verify objects are correct in the PersistenceContext
            if(em.contains(empNew)) {
                fail("empNew not gone from extended PersistenceContext after the second transaction committed");
            }
            if(!em.contains(empToBeRemoved)) {
                fail("empToBeRemoved is not in extended PersistenceContext after the second transaction committed");
            }
            if(!em.contains(empToBeRefreshedExtended)) {
                fail("empToBeRefreshedExtended gone from extended PersistenceContext after the second transaction committed");
            } else if(empToBeRefreshedExtended.getSalary() != originalSalary) {
                fail("empToBeRefreshedExtended still doesn't have the original salary after the second transaction committed");
            }
            if(!em.contains(empToBeMergedExtended)) {
                fail("empToBeMergedExtended gone from extended PersistenceContext after the second transaction committed");
            } else if(empToBeMergedExtended.getSalary() != originalSalary) {
                fail("empToBeMergedExtended doesn't have the original salary after the second transaction committed");
            }
    
            // verify objects are correct in the db
            clearCache();
            Employee empNewFound2 = em.find(Employee.class, empNew.getId());
            if(empNewFound2 != null) {
                fail("empNew still in the db after the second transaction committed");
            }
            Employee empToBeRemovedFound2 = em.find(Employee.class, empToBeRemoved.getId());
            if(empToBeRemovedFound2 == null) {
                fail("empToBeRemoved is not in the db after the second transaction committed");
            }
            Employee empToBeRefreshedFound2 = em.find(Employee.class, empToBeRefreshed.getId());
            if(empToBeRefreshedFound2 == null) {
                fail("empToBeRefreshed not in the db after the second transaction committed");
            } else if(empToBeRefreshedFound2.getSalary() != originalSalary) {
                fail("empToBeRefreshed doesn't have the original salary in the db after the second transaction committed");
            }
            Employee empToBeMergedFound2 = em.find(Employee.class, empToBeMerged.getId());
            if(empToBeMergedFound2 == null) {
                fail("empToBeMerged not in the db after the second transaction committed");
            } else if(empToBeMergedFound2.getSalary() != originalSalary) {
                fail("empToBeMerged doesn't have the original salary in the db after the second transaction committed");
            }
            
            // third test
            // without starting transaction persist, remove, refresh, merge
            // The same as the first test - but now we'll rollback.
            // The objects should be detached.
    
            beginTransaction(em);
            em.persist(empNew);
            em.remove(empToBeRemoved);
            
            // Use another EntityManager to alter empToBeRefreshed in the db
            EntityManager em2 = createEntityManager();
            em2.getTransaction().begin();
            try{
                empToBeRefreshed = em2.find(Employee.class, empToBeRefreshed.getId());
                empToBeRefreshed.setSalary(newSalary);
                em2.getTransaction().commit();
            }catch (RuntimeException ex){
                if (em2.getTransaction().isActive()){
                    em2.getTransaction().rollback();
                }
                throw ex;
            }finally{
                em2.close();
            }
            // now refesh
            em.refresh(empToBeRefreshedExtended);
    
            // alter empToBeRefreshed
            empToBeMergedFound2.setSalary(newSalary);
            // now merge
            em.merge(empToBeMergedFound2);
    
            // flush and ROLLBACK the third transaction
            em.flush();
            rollbackTransaction(em);
            
            // verify objects are correct in the PersistenceContext after the third transaction rolled back
            if(em.contains(empNew)) {
                fail("empNew is still in extended PersistenceContext after the third transaction rolled back");
            }
            if(em.contains(empToBeRemoved)) {
                fail("empToBeRemoved is still in extended PersistenceContext after the third transaction rolled back");
            }
            if(em.contains(empToBeRefreshedExtended)) {
                fail("empToBeRefreshedExtended is still in extended PersistenceContext after the third transaction rolled back");
            } else if(empToBeRefreshedExtended.getSalary() != newSalary) {
                fail("empToBeRefreshedExtended still has the original salary after third transaction rolled back");
            }
            if(em.contains(empToBeMergedExtended)) {
                fail("empToBeMergedExtended is still in extended PersistenceContext after the third transaction rolled back");
            } else if(empToBeMergedExtended.getSalary() != newSalary) {
                fail("empToBeMergedExtended still has the original salary after third transaction rolled back");
            }
    
            // verify objects are correct in the db after the third transaction rolled back
            clearCache();
            Employee empNewFound3 = em.find(Employee.class, empNew.getId());
            if(empNewFound3 != null) {
                fail("empNew is in the db after the third transaction rolled back");
            }
            Employee empToBeRemovedFound3 = em.find(Employee.class, empToBeRemoved.getId());
            if(empToBeRemovedFound3 == null) {
                fail("empToBeRemoved not in the db after the third transaction rolled back");
            }
            Employee empToBeRefreshedFound3 = em.find(Employee.class, empToBeRefreshed.getId());
            if(empToBeRefreshedFound3 == null) {
                fail("empToBeRefreshed not in the db after the third transaction rolled back");
            } else if(empToBeRefreshedFound3.getSalary() != newSalary) {
                fail("empToBeRefreshed has the original salary in the db after the third transaction rolled back");
            }
            Employee empToBeMergedFound3 = em.find(Employee.class, empToBeMerged.getId());
            if(empToBeMergedFound3 == null) {
                fail("empToBeMerged not in the db after the third transaction rolled back");
            } else if(empToBeMergedFound3.getSalary() != originalSalary) {
                fail("empToBeMerged still doesn't have the original salary in the db after the third transaction rolled back");
            }
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }
    
    public void testReadTransactionIsolation_CustomUpdate() {
        internalTestReadTransactionIsolation(false, false, false, false);
    }
    public void testReadTransactionIsolation_CustomUpdate_Flush() {
        internalTestReadTransactionIsolation(false, false, false, true);
    }
    public void testReadTransactionIsolation_CustomUpdate_Refresh() {
        internalTestReadTransactionIsolation(false, false, true, false);
    }
    public void testReadTransactionIsolation_CustomUpdate_Refresh_Flush() {
        internalTestReadTransactionIsolation(false, false, true, true);
    }
    public void testReadTransactionIsolation_UpdateAll() {
        internalTestReadTransactionIsolation(false, true, false, false);
    }
    public void testReadTransactionIsolation_UpdateAll_Flush() {
        internalTestReadTransactionIsolation(false, true, false, true);
    }
    public void testReadTransactionIsolation_UpdateAll_Refresh() {
        internalTestReadTransactionIsolation(false, true, true, false);
    }
    public void testReadTransactionIsolation_UpdateAll_Refresh_Flush() {
        internalTestReadTransactionIsolation(false, true, true, true);
    }
    public void testReadTransactionIsolation_OriginalInCache_CustomUpdate() {
        internalTestReadTransactionIsolation(true, false, false, false);
    }
    public void testReadTransactionIsolation_OriginalInCache_CustomUpdate_Flush() {
        internalTestReadTransactionIsolation(true, false, false, true);
    }
    public void testReadTransactionIsolation_OriginalInCache_CustomUpdate_Refresh() {
        internalTestReadTransactionIsolation(true, false, true, false);
    }
    public void testReadTransactionIsolation_OriginalInCache_CustomUpdate_Refresh_Flush() {
        internalTestReadTransactionIsolation(true, false, true, true);
    }
    public void testReadTransactionIsolation_OriginalInCache_UpdateAll() {
        internalTestReadTransactionIsolation(true, true, false, false);
    }
    public void testReadTransactionIsolation_OriginalInCache_UpdateAll_Flush() {
        internalTestReadTransactionIsolation(true, true, false, true);
    }
    public void testReadTransactionIsolation_OriginalInCache_UpdateAll_Refresh() {
        internalTestReadTransactionIsolation(true, true, true, false);
    }
    public void testReadTransactionIsolation_OriginalInCache_UpdateAll_Refresh_Flush() {
        internalTestReadTransactionIsolation(true, true, true, true);
    }
    
    protected void internalTestReadTransactionIsolation(boolean shouldOriginalBeInParentCache, boolean shouldUpdateAll, boolean shouldRefresh, boolean shouldFlush) {
        //setup
        String firstName = "testReadTransactionIsolation";
        
        // make sure no Employee with the specified firstName exists.
        EntityManager em = createEntityManager();
        Query deleteQuery = em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'");        
        beginTransaction(em);
        try{
            deleteQuery.executeUpdate();
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        clearCache();
        em.clear();
        
        // create and persist the object
        String lastNameOriginal = "Original";
        int salaryOriginal = 0;
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastNameOriginal);
        employee.setSalary(salaryOriginal);
        beginTransaction(em);
        try{
            em.persist(employee);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        
        if(!shouldOriginalBeInParentCache) {
            clearCache();
        }
        em.clear();
        
        Employee employeeUOW = null;

        int salaryNew = 100;
        String lastNameNew = "New";

        beginTransaction(em);
        Query selectQuery = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.firstName = '"+firstName+"'");
        
        try{
            if(shouldRefresh) {
                String lastNameAlternative = "Alternative";
                int salaryAlternative = 50;
                employeeUOW = (Employee)selectQuery.getSingleResult();
                employeeUOW.setLastName(lastNameAlternative);
                employeeUOW.setSalary(salaryAlternative);
            }
        
            int nUpdated;
            if(shouldUpdateAll) {
                nUpdated = em.createQuery("UPDATE Employee e set e.lastName = '" + lastNameNew + "' where e.firstName like '" + firstName + "'").setFlushMode(FlushModeType.AUTO).executeUpdate();
            } else {
                nUpdated = em.createNativeQuery("UPDATE CMP3_EMPLOYEE SET L_NAME = '" + lastNameNew + "', VERSION = VERSION + 1 WHERE F_NAME LIKE '" + firstName + "'").setFlushMode(FlushModeType.AUTO).executeUpdate();
            }
            assertTrue("nUpdated=="+ nUpdated +"; 1 was expected", nUpdated == 1);
    
            if(shouldFlush) {
                selectQuery.setFlushMode(FlushModeType.AUTO);
            } else {
                selectQuery.setFlushMode(FlushModeType.COMMIT);
            }
    
            if(shouldRefresh) {
                selectQuery.setHint("eclipselink.refresh", Boolean.TRUE);
                employeeUOW = (Employee)selectQuery.getSingleResult();
                selectQuery.setHint("eclipselink.refresh", Boolean.FALSE);
            } else {
                employeeUOW = (Employee)selectQuery.getSingleResult();
            }
            assertTrue("employeeUOW.getLastName()=="+ employeeUOW.getLastName() +"; " + lastNameNew + " was expected", employeeUOW.getLastName().equals(lastNameNew));
    
            employeeUOW.setSalary(salaryNew);
    
            employeeUOW = (Employee)selectQuery.getSingleResult();
            assertTrue("employeeUOW.getSalary()=="+ employeeUOW.getSalary() +"; " + salaryNew + " was expected", employeeUOW.getSalary() == salaryNew);
                    
            commitTransaction(em);
        }catch (Throwable ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            if (Error.class.isAssignableFrom(ex.getClass())){
                throw (Error)ex;
            }else{
                throw (RuntimeException)ex;
            }
        }
        
        Employee employeeFoundAfterTransaction = em.find(Employee.class, employeeUOW.getId());
        assertTrue("employeeFoundAfterTransaction().getLastName()=="+ employeeFoundAfterTransaction.getLastName() +"; " + lastNameNew + " was expected", employeeFoundAfterTransaction.getLastName().equals(lastNameNew));
        assertTrue("employeeFoundAfterTransaction().getSalary()=="+ employeeFoundAfterTransaction.getSalary() +"; " + salaryNew + " was expected", employeeFoundAfterTransaction.getSalary() == salaryNew);

        // The cache should be invalidated because the commit should detect a jump in the version number and invalidate the object.
        EntityManager em2 = createEntityManager();
        employeeFoundAfterTransaction = em2.find(Employee.class, employeeUOW.getId());
        assertTrue("employeeFoundAfterTransaction().getLastName()=="+ employeeFoundAfterTransaction.getLastName() +"; " + lastNameNew + " was expected", employeeFoundAfterTransaction.getLastName().equals(lastNameNew));
        assertTrue("employeeFoundAfterTransaction().getSalary()=="+ employeeFoundAfterTransaction.getSalary() +"; " + salaryNew + " was expected", employeeFoundAfterTransaction.getSalary() == salaryNew);
        em2.close();
        
        // clean up
        beginTransaction(em);
        try{
            deleteQuery.executeUpdate();
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        clearCache();
        closeEntityManager(em);
    }

    // test for bug 4755392: 
    // AFTER DELETEALL OBJECT STILL DEEMED EXISTING
    public void testFindDeleteAllPersist() {
        String firstName = "testFindDeleteAllPersist";

        // create Employees        
        Employee empWithAddress = new Employee();
        empWithAddress.setFirstName(firstName);
        empWithAddress.setLastName("WithAddress");
        empWithAddress.setAddress(new Address());

        Employee empWithoutAddress = new Employee();
        empWithoutAddress.setFirstName(firstName);
        empWithoutAddress.setLastName("WithoutAddress");

        EntityManager em = createEntityManager();
        Query deleteQuery = em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'");

        // make sure no Employee with the specified firstName exists.
        beginTransaction(em);
        try{
            deleteQuery.executeUpdate();
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }

        // persist
        beginTransaction(em);
        try{
            em.persist(empWithAddress);
            em.persist(empWithoutAddress);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        
        // clear cache
        clearCache();
        em.clear();
        
        // Find both to bring into the cache, delete empWithoutAddress.
        // Because the address VH is not triggered both objects should be invalidated.
        beginTransaction(em);
        try{
            Employee empWithAddressFound = em.find(Employee.class, empWithAddress.getId());
            Employee empWithoutAddressFound = em.find(Employee.class, empWithoutAddress.getId());
            int nDeleted = em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"' and e.address IS NULL").executeUpdate();
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        // we can no longer rely on the query above to clear the Employee from the persistence context.
        // Clearling the context to allow us to proceed.
        em.clear();
        // persist new empWithoutAddress - the one that has been deleted from the db.
        beginTransaction(em);
        try{
            Employee newEmpWithoutAddress = new Employee();
            newEmpWithoutAddress.setFirstName(firstName);
            newEmpWithoutAddress.setLastName("newWithoutAddress");
            newEmpWithoutAddress.setId(empWithoutAddress.getId());
            em.persist(newEmpWithoutAddress);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }

        // persist new empWithAddress - the one still in the db.
        beginTransaction(em);
        try{
            Employee newEmpWithAddress = new Employee();
            newEmpWithAddress.setFirstName(firstName);
            newEmpWithAddress.setLastName("newWithAddress");
            newEmpWithAddress.setId(empWithAddress.getId());
            em.persist(newEmpWithAddress);
            fail("EntityExistsException was expected");
        } catch (EntityExistsException ex) {
            // "cant_persist_detatched_object" - ignore the expected exception
        } finally {
            rollbackTransaction(em);
        }

        // clean up
        beginTransaction(em);
        deleteQuery.executeUpdate();
        commitTransaction(em);
    }
    
    public void testWRITELock(){
        // Cannot create parallel transactions.
        if (isOnServer()) {
            return;
        }
        EntityManager em = createEntityManager();
        Employee employee = new Employee();
        employee.setFirstName("Mark");
        employee.setLastName("Madsen");
        beginTransaction(em);
        try{
            em.persist(employee);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }

        EntityManager em2 = createEntityManager();
        Exception optimisticLockException = null;
        
        beginTransaction(em);
        try{
            employee = em.find(Employee.class, employee.getId());
            em.lock(employee, LockModeType.WRITE);

            em2.getTransaction().begin();
            try{
                Employee employee2 = em2.find(Employee.class, employee.getId());
                employee2.setFirstName("Michael");
                em2.getTransaction().commit();
            }catch (RuntimeException ex){
                em2.getTransaction().rollback();
                em2.close();
                throw ex;
            }
            
            commitTransaction(em);
        } catch (RollbackException exception) {
            if (exception.getCause() instanceof OptimisticLockException){
                optimisticLockException = exception;
            }
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        beginTransaction(em);
        try{
            employee = em.find(Employee.class, employee.getId());
            em.remove(employee);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        
        if (optimisticLockException == null){
            fail("Proper exception not thrown when EntityManager.lock(object, WRITE) is used.");
        }
    }
    /*This test case uses the "default2" PU defined in the persistence.xml 
    located at tltest/resource/essentials/broken-testmodels/META-INF 
    and included in essentials_testmodels_broken.jar */
    
    public void testEMFWrapValidationException() 
    {
        EntityManagerFactory factory = null;
        try {
            factory = Persistence.createEntityManagerFactory("broken-PU", JUnitTestCaseHelper.getDatabaseProperties());
            EntityManager em = factory.createEntityManager();
        } catch (javax.persistence.PersistenceException e)  {
            // Ignore - it's expected exception type
        } finally {
            factory.close();
        }
    }
    
    /**
     * At the time this test case was added, the problem it was designed to test for would cause a failure during deployement 
     * and therefore this tests case would likely always pass if there is a successful deployment.
     * But it is anticipated that that may not always be the case and therefore we are adding a test case 
     */
    public void testEMDefaultTxType() 
    {
        EntityManagerFactory factory = null;
        try {
            factory = Persistence.createEntityManagerFactory("default1", JUnitTestCaseHelper.getDatabaseProperties());
            EntityManager em = factory.createEntityManager();
        } catch (Exception e)  {   
            fail("Exception caught while creating EM with no \"transaction-type\" specified in persistence.xml");        
        } finally {
            factory.close();
        }
        Assert.assertTrue(true);        
    }
    
    public void testPersistOnNonEntity()
    {
        boolean testPass = false;
        Object nonEntity = new Object();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.persist(nonEntity);
        } catch (IllegalArgumentException e) {
            testPass = true;
        } finally {
            rollbackTransaction(em);
        }
        Assert.assertTrue(testPass);
    }

    public void testClose() {
        // Close is not used on server.
        if (isOnServer()) {
            return;
        }
        EntityManager em = createEntityManager();
        if(!em.isOpen()) {
            fail("Created EntityManager is not open");
        }
        closeEntityManager(em);
        if(em.isOpen()) {
            fail("Closed EntityManager is still open");
        }
    }

    public void testBeginTransactionClose() {
        // Close is not used on server.
        if (isOnServer()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            closeEntityManager(em);
            if(em.isOpen()) {
                fail("Closed EntityManager is still open before transaction complete");
            }
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            if(em.isOpen()) {
                closeEntityManager(em);
            }
            throw ex;
        }

        rollbackTransaction(em);
        if(em.isOpen()) {
            fail("Closed EntityManager is still open after transaction rollback");
        }
    }

    public void testBeginTransactionCloseCommitTransaction() {
        // EntityManager is always open in server.
        if (isOnServer()) {
            return;
        }
        String firstName = "testBeginTrCloseCommitTr";
        EntityManager em = createEntityManager();

        // make sure there is no employees with this firstName
        beginTransaction(em);
        em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
        commitTransaction(em);
        
        // create a new Employee
        Employee employee = new Employee();
        employee.setFirstName(firstName);        
        
        // persist the new Employee and close the entity manager
        beginTransaction(em);
        try{
            em.persist(employee);
            closeEntityManager(em);
            if(em.isOpen()) {
                fail("Closed EntityManager is still open before transaction complete");
            }
        }catch (RuntimeException ex){
            rollbackTransaction(em);
            if(em.isOpen()) {
                closeEntityManager(em);
            }
            throw ex;
        }
        commitTransaction(em);
        
        if(em.isOpen()) {
            fail("Closed EntityManager is still open after transaction commit");
        }
        
        // verify that the employee has been persisted
        em = createEntityManager();
        RuntimeException exception = null;
        try {
            Employee persistedEmployee = (Employee)em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.firstName = '"+firstName+"'").getSingleResult();
        } catch (RuntimeException runtimeException) {
            exception = runtimeException;
        }

        // clean up
        beginTransaction(em);
        em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
        commitTransaction(em);
        
        if(exception != null) {
            if(exception instanceof EntityNotFoundException) {
                fail("object has not been persisted");
            } else {
                // unexpected exception - rethrow.
                throw exception;
            }
        }
    }

    // The test removed because we moved back to binding literals 
    // on platforms other than DB2 and Derby
/*    public void testDontBindLiteral() {
        EntityManager em = createEntityManager();
        
        Query controlQuery = em.createQuery("SELECT OBJECT(p) FROM SmallProject p WHERE p.name = CONCAT(:param1, :param2)");
        controlQuery.setParameter("param1", "A").setParameter("param2", "B");
        List controlResults = controlQuery.getResultList();
        int nControlParams = ((ExpressionQueryMechanism)((EJBQueryImpl)controlQuery).getDatabaseQuery().getQueryMechanism()).getCall().getParameters().size();
        if(nControlParams != 2) {
            fail("controlQuery has wrong number of parameters = "+nControlParams+"; 2 is expected");
        }

        Query query = em.createQuery("SELECT OBJECT(p) FROM SmallProject p WHERE p.name = CONCAT('A', 'B')");
        List results = query.getResultList();
        int nParams = ((ExpressionQueryMechanism)((EJBQueryImpl)query).getDatabaseQuery().getQueryMechanism()).getCall().getParameters().size();
        if(nParams > 0) {
            fail("Query processed literals as parameters");
        }
        
        closeEntityManager(em);
    }*/
    
    public void testPersistenceProperties() {
        // Different properties are used on the server.
        if (isOnServer()) {
            return;
        }
        
        EntityManager em = createEntityManager();
        ServerSession ss = ((org.eclipse.persistence.internal.jpa.EntityManagerImpl)em).getServerSession();
        
        // these properties were set in persistence unit
        // and overridden in CMP3TestModel.setup - the values should be overridden.
        
        boolean isReadShared = (ss.getReadConnectionPool() instanceof ReadConnectionPool);
        if(isReadShared != Boolean.parseBoolean((String)JUnitTestCaseHelper.propertiesMap.get(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_SHARED))) {
            fail("isReadShared is wrong");
        }
        
        int writeMin = ss.getDefaultConnectionPool().getMinNumberOfConnections();
        if(writeMin != Integer.parseInt((String)JUnitTestCaseHelper.propertiesMap.get(PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_MIN))) {
            fail("writeMin is wrong");
        }
        
        int writeMax = ss.getDefaultConnectionPool().getMaxNumberOfConnections();
        if(writeMax != Integer.parseInt((String)JUnitTestCaseHelper.propertiesMap.get(PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_MAX))) {
            fail("writeMax is wrong");
        }

        int readMin = ss.getReadConnectionPool().getMinNumberOfConnections();
        if(readMin != Integer.parseInt((String)JUnitTestCaseHelper.propertiesMap.get(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_MIN))) {
            fail("readMin is wrong");
        }

        int readMax = ss.getReadConnectionPool().getMaxNumberOfConnections();
        if(readMax != Integer.parseInt((String)JUnitTestCaseHelper.propertiesMap.get(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_MAX))) {
            fail("readMax is wrong");
        }
        
        // these properties were set in persistence unit - the values should be the same as in persistence.xml
        /*
			<property name="eclipselink.session-name" value="default-session"/>
			<property name="eclipselink.cache.size.default" value="500"/>
			<property name="eclipselink.cache.size.Employee" value="550"/>
			<property name="eclipselink.cache.size.org.eclipse.persistence.testing.models.jpa.advanced.Address" value="555"/>
			<property name="eclipselink.cache.type.default" value="Full"/>
			<property name="eclipselink.cache.type.Employee" value="Weak"/>
			<property name="eclipselink.cache.type.org.eclipse.persistence.testing.models.jpa.advanced.Address" value="HardWeak"/>
			<property name="eclipselink.session.customizer" value="org.eclipse.persistence.testing.models.jpa.advanced.Customizer"/>
			<property name="eclipselink.descriptor.customizer.Employee" value="org.eclipse.persistence.testing.models.jpa.advanced.Customizer"/>
			<property name="eclipselink.descriptor.customizer.org.eclipse.persistence.testing.models.jpa.advanced.Address" value="org.eclipse.persistence.testing.models.jpa.advanced.Customizer"/>
        */
        
        String sessionName = ss.getName();
        if(!sessionName.equals("default-session")) {
            fail("sessionName is wrong");
        }
        
        int defaultCacheSize = ss.getDescriptor(Project.class).getIdentityMapSize();
        if(defaultCacheSize != 500) {
            fail("defaultCacheSize is wrong");
        }
        
        int employeeCacheSize = ss.getDescriptor(Employee.class).getIdentityMapSize();
        if(employeeCacheSize != 550) {
            fail("employeeCacheSize is wrong");
        }
        
        int addressCacheSize = ss.getDescriptor(Address.class).getIdentityMapSize();
        if(addressCacheSize != 555) {
            fail("addressCacheSize is wrong");
        }
        
        Class defaultCacheType = ss.getDescriptor(Project.class).getIdentityMapClass();
        if(! Helper.getShortClassName(defaultCacheType).equals("FullIdentityMap")) {
            fail("defaultCacheType is wrong");
        }
        
        Class employeeCacheType = ss.getDescriptor(Employee.class).getIdentityMapClass();
        if(! Helper.getShortClassName(employeeCacheType).equals("WeakIdentityMap")) {
            fail("employeeCacheType is wrong");
        }
        
        Class addressCacheType = ss.getDescriptor(Address.class).getIdentityMapClass();
        if(! Helper.getShortClassName(addressCacheType).equals("HardCacheWeakIdentityMap")) {
            fail("addressCacheType is wrong");
        }
        
        int numSessionCalls = Customizer.getNumberOfCallsForSession(ss.getName());
        if(numSessionCalls == 0) {
            fail("session customizer hasn't been called");
        }
        
        int numProjectCalls = Customizer.getNumberOfCallsForClass(Project.class.getName());
        if(numProjectCalls > 0) {
            fail("Project customizer has been called");
        }
        
        int numEmployeeCalls = Customizer.getNumberOfCallsForClass(Employee.class.getName());
        if(numEmployeeCalls == 0) {
            fail("Employee customizer hasn't been called");
        }
        
        int numAddressCalls = Customizer.getNumberOfCallsForClass(Address.class.getName());
        if(numAddressCalls == 0) {
            fail("Address customizer hasn't been called");
        }
        
        closeEntityManager(em);
    }

    public void testMultipleFactories() {
        getEntityManagerFactory();
        closeEntityManagerFactory();
        boolean isOpen = getEntityManagerFactory().isOpen();
        if(!isOpen) {
            fail("Close factory 1; open factory 2 - it's not open");
        } else {
            // Get entity manager just to login back the session, then close em
            getEntityManagerFactory().createEntityManager().close();
        }
    }
    
    public void testParallelMultipleFactories() {
        EntityManagerFactory factory1 =  Persistence.createEntityManagerFactory("default", JUnitTestCaseHelper.getDatabaseProperties());
        factory1.createEntityManager();
        EntityManagerFactory factory2 =  Persistence.createEntityManagerFactory("default", JUnitTestCaseHelper.getDatabaseProperties());
        factory2.createEntityManager();
        factory1.close();
        if(factory1.isOpen()) {
            fail("after factory1.close() factory1 is not closed");
        }
        if(!factory2.isOpen()) {
            fail("after factory1.close() factory2 is closed");
        }
        factory2.close();
        if(factory2.isOpen()) {
            fail("after factory2.close() factory2 is not closed");
        }
        EntityManagerFactory factory3 =  Persistence.createEntityManagerFactory("default", JUnitTestCaseHelper.getDatabaseProperties());
        if(!factory3.isOpen()) {
            fail("factory3 is closed");
        }
        factory3.createEntityManager();
        factory3.close();
        if(factory3.isOpen()) {
            fail("after factory3.close() factory3 is open");
        }
    }
    
    public void testQueryHints() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.firstName = 'testQueryHints'");
        ObjectLevelReadQuery olrQuery = (ObjectLevelReadQuery)((EJBQueryImpl)query).getDatabaseQuery();
        
        // binding
        // original state = default state
        assertTrue(olrQuery.shouldIgnoreBindAllParameters());
        // set boolean true
        query.setHint(QueryHints.BIND_PARAMETERS, true);
        // Parse cached query may be cloned when hint set, so re-get.
        olrQuery = (ObjectLevelReadQuery)((EJBQueryImpl)query).getDatabaseQuery();
        assertTrue("Binding not set.", olrQuery.shouldBindAllParameters());
        // reset to original state
        query.setHint(QueryHints.BIND_PARAMETERS, "");
        assertTrue("Binding not set.", olrQuery.shouldIgnoreBindAllParameters());
        // set "false"
        query.setHint(QueryHints.BIND_PARAMETERS, "false");
        assertFalse("Binding not set.", olrQuery.shouldBindAllParameters());
        // reset to the original state
        query.setHint(QueryHints.BIND_PARAMETERS, "");
        assertTrue("Binding not set.", olrQuery.shouldIgnoreBindAllParameters());
        
        // cache usage
        query.setHint(QueryHints.CACHE_USAGE, CacheUsage.DoNotCheckCache);
        assertTrue("Cache usage not set.", olrQuery.getCacheUsage()==ObjectLevelReadQuery.DoNotCheckCache);
        query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheOnly);
        assertTrue("Cache usage not set.", olrQuery.shouldCheckCacheOnly());
        query.setHint(QueryHints.CACHE_USAGE, CacheUsage.ConformResultsInUnitOfWork);
        assertTrue("Cache usage not set.", olrQuery.shouldConformResultsInUnitOfWork());
        // reset to the original state
        query.setHint(QueryHints.CACHE_USAGE, "");
        assertTrue("Cache usage not set.", olrQuery.shouldCheckDescriptorForCacheUsage());
        
        // pessimistic lock
        query.setHint(QueryHints.PESSIMISTIC_LOCK, PessimisticLock.Lock);
        assertTrue("Lock not set.", olrQuery.getLockMode()==ObjectLevelReadQuery.LOCK);
        query.setHint(QueryHints.PESSIMISTIC_LOCK, PessimisticLock.NoLock);
        assertTrue("Lock not set.", olrQuery.getLockMode()==ObjectLevelReadQuery.NO_LOCK);
        query.setHint(QueryHints.PESSIMISTIC_LOCK, PessimisticLock.LockNoWait);
        assertTrue("Lock not set.", olrQuery.getLockMode()==ObjectLevelReadQuery.LOCK_NOWAIT);
        // default state
        query.setHint(QueryHints.PESSIMISTIC_LOCK, "");
        assertTrue("Lock not set.", olrQuery.getLockMode()==ObjectLevelReadQuery.NO_LOCK);
        
        //refresh
        // set to original state - don't refresh.
        // the previously run LOCK and LOCK_NOWAIT have swithed it to true
        query.setHint(QueryHints.REFRESH, false);
        assertFalse("Refresh not set.", olrQuery.shouldRefreshIdentityMapResult());
        // set boolean true
        query.setHint(QueryHints.REFRESH, true);
        assertTrue("Refresh not set.", olrQuery.shouldRefreshIdentityMapResult());
        assertTrue("CascadeByMapping not set.", olrQuery.shouldCascadeByMapping()); // check if cascade refresh is enabled 
        // set "false"
        query.setHint(QueryHints.REFRESH, "false");
        assertFalse("Refresh not set.", olrQuery.shouldRefreshIdentityMapResult());
        // set Boolean.TRUE
        query.setHint(QueryHints.REFRESH, Boolean.TRUE);
        assertTrue("Refresh not set.", olrQuery.shouldRefreshIdentityMapResult());
        assertTrue("CascadeByMapping not set.", olrQuery.shouldCascadeByMapping()); // check if cascade refresh is enabled 
        // reset to original state
        query.setHint(QueryHints.REFRESH, "");
        assertFalse("Refresh not set.", olrQuery.shouldRefreshIdentityMapResult());
        
        query.setHint(QueryHints.READ_ONLY, "false");
        assertFalse("Read-only not set.", olrQuery.isReadOnly()); 
        
        query.setHint(QueryHints.READ_ONLY, Boolean.TRUE);
        assertTrue("Read-only not set.", olrQuery.isReadOnly());
        
        query.setHint(QueryHints.READ_ONLY, Boolean.FALSE);
        assertFalse("Read-only not set.", olrQuery.isReadOnly());
        
        query.setHint(QueryHints.JDBC_TIMEOUT, new Integer(100));
        assertTrue("Timeout not set.", olrQuery.getQueryTimeout() == 100);
        
        query.setHint(QueryHints.JDBC_FETCH_SIZE, new Integer(101));
        assertTrue("Fetch-size not set.", olrQuery.getFetchSize() == 101);
        
        query.setHint(QueryHints.JDBC_MAX_ROWS, new Integer(103));
        assertTrue("Max-rows not set.", olrQuery.getMaxRows() == 103); 

        query.setHint(QueryHints.REFRESH_CASCADE, CascadePolicy.NoCascading);
        assertTrue(olrQuery.getCascadePolicy()==DatabaseQuery.NoCascading);
        query.setHint(QueryHints.REFRESH_CASCADE, CascadePolicy.CascadeByMapping);
        assertTrue(olrQuery.getCascadePolicy()==DatabaseQuery.CascadeByMapping);
        query.setHint(QueryHints.REFRESH_CASCADE, CascadePolicy.CascadeAllParts);
        assertTrue(olrQuery.getCascadePolicy()==DatabaseQuery.CascadeAllParts);
        query.setHint(QueryHints.REFRESH_CASCADE, CascadePolicy.CascadePrivateParts);
        assertTrue(olrQuery.getCascadePolicy()==DatabaseQuery.CascadePrivateParts);
        // reset to the original state
        query.setHint(QueryHints.REFRESH_CASCADE, "");
        assertTrue(olrQuery.getCascadePolicy()==DatabaseQuery.CascadeByMapping);
        
        query.setHint(QueryHints.RESULT_COLLECTION_TYPE, java.util.ArrayList.class);
        assertTrue("ArrayList not set.", ((ReadAllQuery)olrQuery).getContainerPolicy().getContainerClassName().equals(java.util.ArrayList.class.getName())); 

        query.setHint(QueryHints.RESULT_COLLECTION_TYPE, "java.util.Vector");
        assertTrue("Vector not set.", ((ReadAllQuery)olrQuery).getContainerPolicy().getContainerClassName().equals(java.util.Vector.class.getName())); 

        closeEntityManager(em);
    }
    
    /**
     * This test ensures that the eclipselink.batch query hint works.
     * It tests two things. 
     * 
     * 1. That the batch read attribute is properly added to the queyr
     * 2. That the query will execute
     * 
     * It does not do any verification that the batch reading feature actually works.  That is
     * left for the batch reading testing to do.
     */
    public void testBatchQueryHint(){
        int id1 = 0;
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Employee manager = new Employee();
        manager.setFirstName("Marvin");
        manager.setLastName("Malone");
        PhoneNumber number = new PhoneNumber("cell", "613", "888-8888");
        manager.addPhoneNumber(number);
        number = new PhoneNumber("home", "613", "888-8880");
        manager.addPhoneNumber(number);
        em.persist(manager);
        id1 = manager.getId();
        
        Employee emp = new Employee();
        emp.setFirstName("Melvin");
        emp.setLastName("Malone");
        emp.setManager(manager);
        manager.addManagedEmployee(emp);
        number = new PhoneNumber("cell", "613", "888-9888");
        emp.addPhoneNumber(number);
        number = new PhoneNumber("home", "613", "888-0880");
        emp.addPhoneNumber(number);
        em.persist(emp);
        
        emp = new Employee();
        emp.setFirstName("David");
        emp.setLastName("Malone");
        emp.setManager(manager);
        manager.addManagedEmployee(emp);
        number = new PhoneNumber("cell", "613", "888-9988");
        emp.addPhoneNumber(number);
        number = new PhoneNumber("home", "613", "888-0980");
        emp.addPhoneNumber(number);
        em.persist(emp);
        
        commitTransaction(em);
        em.clear();

        org.eclipse.persistence.jpa.JpaQuery query = (org.eclipse.persistence.jpa.JpaQuery)em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
        query.setHint(QueryHints.BATCH, "e.phoneNumbers");
        query.setHint(QueryHints.BATCH, "e.manager.phoneNumbers");
        
        ReadAllQuery raq = (ReadAllQuery)query.getDatabaseQuery();
        List expressions = raq.getBatchReadAttributeExpressions();
        assertTrue(expressions.size() == 2);
        Expression exp = (Expression)expressions.get(0);
        assertTrue(exp.isQueryKeyExpression());
        assertTrue(exp.getName().equals("phoneNumbers"));
        exp = (Expression)expressions.get(1);
        assertTrue(exp.isQueryKeyExpression());
        assertTrue(exp.getName().equals("phoneNumbers"));

        List resultList = query.getResultList();
        emp = (Employee)resultList.get(0);
        emp.getPhoneNumbers().hashCode();
        
        emp.getManager().getPhoneNumbers().hashCode();

        emp = (Employee)resultList.get(1);
        emp.getPhoneNumbers().hashCode();
        
        beginTransaction(em);
        emp = em.find(Employee.class, id1);
        Iterator it = emp.getManagedEmployees().iterator();
        while (it.hasNext()){
            Employee managedEmp = (Employee)it.next();
            it.remove();
            managedEmp.setManager(null);
            em.remove(managedEmp);
        }
        em.remove(emp);
        commitTransaction(em);
    }
    
    /**
     * This test ensures that the eclipselink.fetch query hint works.
     * It tests two things. 
     * 
     * 1. That the jined attribute is properly added to the query
     * 2. That the query will execute
     * 
     * It does not do any verification that the joining feature actually works.  That is
     * left for the joining testing to do.
     */
    public void testFetchQueryHint(){
        int id1 = 0;
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Employee manager = new Employee();
        manager.setFirstName("Marvin");
        manager.setLastName("Malone");
        PhoneNumber number = new PhoneNumber("cell", "613", "888-8888");
        manager.addPhoneNumber(number);
        number = new PhoneNumber("home", "613", "888-8880");
        manager.addPhoneNumber(number);
        em.persist(manager);
        id1 = manager.getId();
        
        Employee emp = new Employee();
        emp.setFirstName("Melvin");
        emp.setLastName("Malone");
        emp.setManager(manager);
        manager.addManagedEmployee(emp);
        number = new PhoneNumber("cell", "613", "888-9888");
        emp.addPhoneNumber(number);
        number = new PhoneNumber("home", "613", "888-0880");
        emp.addPhoneNumber(number);
        em.persist(emp);
        
        emp = new Employee();
        emp.setFirstName("David");
        emp.setLastName("Malone");
        emp.setManager(manager);
        manager.addManagedEmployee(emp);
        number = new PhoneNumber("cell", "613", "888-9988");
        emp.addPhoneNumber(number);
        number = new PhoneNumber("home", "613", "888-0980");
        emp.addPhoneNumber(number);
        em.persist(emp);
        
        commitTransaction(em);
        em.clear();

        org.eclipse.persistence.jpa.JpaQuery query = (org.eclipse.persistence.jpa.JpaQuery)em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
        query.setHint(QueryHints.FETCH, "e.manager");
        ReadAllQuery raq = (ReadAllQuery)query.getDatabaseQuery();
        List expressions = raq.getJoinedAttributeExpressions();
        assertTrue(expressions.size() == 1);
        Expression exp = (Expression)expressions.get(0);
        assertTrue(exp.getName().equals("manager"));       
        query.setHint(QueryHints.FETCH, "e.manager.phoneNumbers");
        assertTrue(expressions.size() == 2);

        List resultList = query.getResultList();
        emp = (Employee)resultList.get(0);
        
        beginTransaction(em);
        emp = em.find(Employee.class, id1);
        Iterator it = emp.getManagedEmployees().iterator();
        while (it.hasNext()){
            Employee managedEmp = (Employee)it.next();
            it.remove();
            managedEmp.setManager(null);
            em.remove(managedEmp);
        }
        em.remove(emp);

        commitTransaction(em);
    }
    
    /**
     * Test that the proper exception is thrown when an incorrect batch or fetch query hint is set on the queyr.
     */
    public void testIncorrectBatchQueryHint(){
        EntityManager em = createEntityManager();
        QueryException exception = null;
        try{
            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
            query.setHint(QueryHints.BATCH, "e");
        } catch (QueryException exc){
            exception = exc;
        }
        assertNotNull("No exception was thrown on an incorrect BATCH query hint.", exception);
        assertTrue("Incorrect Exception thrown", exception.getErrorCode() == QueryException.QUERY_HINT_DID_NOT_CONTAIN_ENOUGH_TOKENS);
        exception = null;
        try{
            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
            query.setHint(QueryHints.BATCH, "e.abcdef");
        } catch (QueryException exc){
            exception = exc;
        }
        assertNotNull("No exception was thrown on an incorrect BATCH query hint.", exception);
        assertTrue("Incorrect Exception thrown", exception.getErrorCode() == QueryException.QUERY_HINT_NAVIGATED_NON_EXISTANT_RELATIONSHIP);

        exception = null;
        try{
            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
            query.setHint(QueryHints.BATCH, "e.firstName");
        } catch (QueryException exc){
            exception = exc;
        }
        assertNotNull("No exception was thrown when an incorrect relationship was navigated in a BATCH query hint.", exception);
        assertTrue("Incorrect Exception thrown", exception.getErrorCode() == QueryException.QUERY_HINT_NAVIGATED_ILLEGAL_RELATIONSHIP);

        exception = null;
        try{
            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
            query.setHint(QueryHints.FETCH, "e");
        } catch (QueryException exc){
            exception = exc;
        }
        assertNotNull("No exception was thrown on an incorrect FETCH query hint.", exception);
        assertTrue("Incorrect Exception thrown", exception.getErrorCode() == QueryException.QUERY_HINT_DID_NOT_CONTAIN_ENOUGH_TOKENS);

        exception = null;
        try{
            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
            query.setHint(QueryHints.FETCH, "e.abcdef");
        } catch (QueryException exc){
            exception = exc;
        }
        assertNotNull("No exception was thrown on an incorrect FETCH query hint.", exception);
        assertTrue("Incorrect Exception thrown", exception.getErrorCode() == QueryException.QUERY_HINT_NAVIGATED_NON_EXISTANT_RELATIONSHIP);

        exception = null;
        try{
            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
            query.setHint(QueryHints.FETCH, "e.firstName");
        } catch (QueryException exc){
            exception = exc;
        }
        assertNotNull("No exception was thrown when an incorrect relationship was navigated in a FETCH query hint.", exception);
        assertTrue("Incorrect Exception thrown", exception.getErrorCode() == QueryException.QUERY_HINT_NAVIGATED_ILLEGAL_RELATIONSHIP);

    }
    
    /*
     * Bug51411440: need to throw IllegalStateException if query executed on closed em
     */
    public void testQueryOnClosedEM() {
        // Server entity managers are not closed.
        if (isOnServer()) {
            return;
        }
        boolean exceptionWasThrown = false;
        EntityManager em = createEntityManager();
        Query q =  em.createQuery("SELECT e FROM Employee e ");
        closeEntityManager(em);
        if(em.isOpen()) {
            fail("Closed EntityManager is still open");
        }
        try{
            q.getResultList();
        }catch(java.lang.IllegalStateException e){
            exceptionWasThrown=true;
        }
        if (!exceptionWasThrown){
            fail("Query on Closed EntityManager did not throw an exception");
        }
    }
    
    public void testNullifyAddressIn() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.createQuery("UPDATE Employee e SET e.address = null WHERE e.address.country IN ('Canada', 'US')").executeUpdate();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    //test for bug 5234283: WRONG =* SQL FOR LEFT JOIN ON DERBY AND DB2 PLATFORMS
    public void testLeftJoinOneToOneQuery() {
        EntityManager em = createEntityManager();
        List results = em.createQuery("SELECT a FROM Employee e LEFT JOIN e.address a").getResultList();
        closeEntityManager(em);
    }

    // Test the clone method works correctly with lazy attributes.
    public void testCloneable() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Employee employee = new Employee();
        employee.setFirstName("Owen");
        employee.setLastName("Hargreaves");
        employee.getAddress();
        Employee clone = employee.clone();
        
        Address address = new Address();
        address.setCity("Munich");
        clone.setAddress(address);
        clone.getAddress();
        em.persist(clone);
        if (employee.getAddress() == clone.getAddress()) {
            fail("Changing clone address changed original.");
        }
        commitTransaction(em);
        clearCache();
        closeEntityManager(em);
        em = createEntityManager();
        beginTransaction(em);
        employee = em.find(Employee.class, clone.getId());
        clone = employee.clone();
        
        address = new Address();
        address.setCity("Not Munich");
        clone.setAddress(address);
        clone.getAddress();
        if (employee.getAddress() == clone.getAddress()) {
            fail("Changing clone address changed original.");
        }
        if (employee.getAddress() == null) {
            fail("Changing clone address reset original to null.");
        }
        if (clone.getAddress() != address) {
            fail("Changing clone did not work.");
        }
        commitTransaction(em);
        closeEntityManager(em);

        em = createEntityManager();
        beginTransaction(em);
        employee = em.find(Employee.class, clone.getId());
        clone = employee.clone();
        clone.setId(null);
        em.persist(clone);
        commitTransaction(em);
        if (clone.getId() == null) {
            fail("Clone was not persisted.");
        }
        beginTransaction(em);
        employee = em.find(Employee.class, clone.getId());
        em.remove(employee);
        commitTransaction(em);
        closeEntityManager(em);
    }
    
    // test for GlassFish bug 711 - throw a descriptive exception when an uninstantiated valueholder is serialized and then accessed
    public void testSerializedLazy(){
        EntityManager em = createEntityManager();      
       
        beginTransaction(em);
        
        Employee emp = new Employee();
        emp.setFirstName("Owen");
        emp.setLastName("Hargreaves");
		emp.setId(40);
        Address address = new Address();
        address.setCity("Munich");
        emp.setAddress(address);
        em.persist(emp);
        em.flush();
        commitTransaction(em);
        closeEntityManager(em);
        clearCache();
        em = createEntityManager();
        String ejbqlString = "SELECT e FROM Employee e WHERE e.firstName = 'Owen' and e.lastName = 'Hargreaves'";
        List result = em.createQuery(ejbqlString).getResultList();
        emp = (Employee)result.get(0);
        Exception exception = null;
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(byteStream);
                
            stream.writeObject(emp);
            stream.flush();
            byte arr[] = byteStream.toByteArray();
            ByteArrayInputStream inByteStream = new ByteArrayInputStream(arr);
            ObjectInputStream inObjStream = new ObjectInputStream(inByteStream);

            emp = (Employee) inObjStream.readObject();
            emp.getAddress();
        } catch (ValidationException e) {
            if (e.getErrorCode() == ValidationException.INSTANTIATING_VALUEHOLDER_WITH_NULL_SESSION){
                exception = e;
            } else {
                fail("An unexpected exception was thrown while testing serialization of ValueHolders: " + e.toString());
            }
        } catch (Exception e){
            fail("An unexpected exception was thrown while testing serialization of ValueHolders: " + e.toString());
        }

        // Only throw error if weaving was used.
        if (isWeavingEnabled()) {
            assertNotNull("The correct exception was not thrown while traversing an uninstantiated lazy relationship on a serialized object: " + exception, exception);
        }
        beginTransaction(em);
        emp = em.find(Employee.class, emp.getId());
        em.remove(emp);
        commitTransaction(em);
    }
    
    //test for bug 5170395: GET THE SEQUENCING EXCEPTION WHEN RUNNING FOR THE FIRST TIME ON A CLEAR SCHEMA
    public void testSequenceObjectDefinition() {
        EntityManager em = createEntityManager();
        ServerSession ss = ((org.eclipse.persistence.internal.jpa.EntityManagerImpl)em).getServerSession();
        if(!ss.getLogin().getPlatform().supportsSequenceObjects()) {
            // platform that supports sequence objects is required for this test
            closeEntityManager(em);
            return;
        }
        String seqName = "testSequenceObjectDefinition";
        try {
            // first param is preallocationSize, second is startValue
            // both should be positive
            internalTestSequenceObjectDefinition(10, 1, seqName, em, ss);
            internalTestSequenceObjectDefinition(10, 5, seqName, em, ss);
            internalTestSequenceObjectDefinition(10, 15, seqName, em, ss);
        } finally {
            closeEntityManager(em);
        }
    }

    protected void internalTestSequenceObjectDefinition(int preallocationSize, int startValue, String seqName, EntityManager em, ServerSession ss) {
        NativeSequence sequence = new NativeSequence(seqName, preallocationSize, startValue, false);
        sequence.onConnect(ss.getPlatform());
        SequenceObjectDefinition def = new SequenceObjectDefinition(sequence);
        // create sequence
        String createStr = def.buildCreationWriter(ss, new StringWriter()).toString();
        beginTransaction(em);
        em.createNativeQuery(createStr).executeUpdate();
        commitTransaction(em);
        try {
            // sequence value preallocated
            Vector seqValues = sequence.getGeneratedVector(null, ss);
            int firstSequenceValue = ((Number)seqValues.elementAt(0)).intValue();
            if(firstSequenceValue != startValue) {
                fail(seqName + " sequence with preallocationSize = "+preallocationSize+" and startValue = " + startValue + " produced wrong firstSequenceValue =" + firstSequenceValue);
            }
        } finally {
            sequence.onDisconnect(ss.getPlatform());
            // drop sequence
            String dropStr = def.buildDeletionWriter(ss, new StringWriter()).toString();
            beginTransaction(em);
            em.createNativeQuery(dropStr).executeUpdate();
            commitTransaction(em);
        }
    }
    
    public void testMergeDetachedObject() {
        // Step 1 - read a department and clear the cache.
        clearCache();
        EntityManager em = createEntityManager();
        EJBQueryImpl query = (EJBQueryImpl) em.createNamedQuery("findAllSQLDepartments");
        Collection departments = query.getResultCollection();
        
        Department detachedDepartment;
        
        // This test seems to get called twice. Once with departments populated
        // and a second time with the department table empty.
        if (departments.isEmpty()) {
            beginTransaction(em);
            detachedDepartment = new Department();
            detachedDepartment.setName("Department X");
            em.persist(detachedDepartment);
            commitTransaction(em);
        } else {
            detachedDepartment = (Department) departments.iterator().next();
        }
        
        closeEntityManager(em);
        clearCache();
        
        // Step 2 - create a new em, create a new employee with the 
        // detached department and then query the departments again.
        em = createEntityManager();
        beginTransaction(em);
        
        Employee emp = new Employee();
        emp.setFirstName("Crazy");
        emp.setLastName("Kid");
		emp.setId(41);
        emp.setDepartment(detachedDepartment);
            
        em.persist(emp);
        commitTransaction(em);
        
        try {
            ((EJBQueryImpl) em.createNamedQuery("findAllSQLDepartments")).getResultCollection();
        } catch (NullPointerException e) {
            assertTrue("The detached department caused a null pointer on the query execution.", false);
        }
        
        closeEntityManager(em);
    }
    
    //bug gf830 - attempting to merge a removed entity should throw an IllegalArgumentException
    public void testMergeRemovedObject() {
    	//create an Employee
        Employee emp = new Employee();
        emp.setFirstName("testMergeRemovedObjectEmployee");
		emp.setId(42);
  
        //persist the Employee
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
            em.persist(emp);
            commitTransaction(em);
        }catch (RuntimeException re){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw re;
        }
        
        beginTransaction(em);
        em.remove(em.find(Employee.class, emp.getId())); //attempt to remove the Employee
        try{  
            em.merge(emp);	//then attempt to merge the Employee
            fail("No exception thrown when merging a removed entity is attempted.");
        }catch (IllegalArgumentException iae){
        	//expected
        }catch (Exception e) {
        	fail("Wrong exception type thrown: " + e.getClass());
        }finally {
            rollbackTransaction(em);
            
            //clean up - ensure removal of employee
            beginTransaction(em);	
            em.remove(em.find(Employee.class, emp.getId())); 
            commitTransaction(em);
            closeEntityManager(em);
        } 
    }
    
    //bug6167431: tests calling merge on a new object puts it in the cache, and that all new objects in the tree get IDs generated
    public void testMergeNewObject() {
    	//create an Employee
        Employee emp = new Employee();
        emp.setFirstName("testMergeNewObjectEmployee");
        emp.setAddress(new Address("45 O'Connor", "Ottawa", "Ont", "Canada", "K1P1A4"));
  
        //persist the Employee
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
            Employee managedEmp = em.merge(emp);
            this.assertNotNull("merged Employee doesn't have its ID generated", managedEmp.getId());
            this.assertNotNull("merged Employee cannot be found using find", em.find(Employee.class, managedEmp.getId()));
			//this won't work till bug:6193761 is fixed
            //this.assertTrue("referenced Address doesn't have its ID generated", managedEmp.getAddress().getId()!=0);
        }finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
        
    }
    
    //bug6180972: Tests calling merge on a new Entity that uses int as its ID, verifying it is set and cached correctly
    public void testMergeNewObject2() {
        //create an Equipment
        Equipment equip = new Equipment();
        equip.setDescription("New Equipment");

        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
            Equipment managedEquip = em.merge(equip);
            
            this.assertTrue("merged Equipment doesn't have its ID generated", managedEquip.getId()!=0);
            this.assertNotNull("merged Equipment cannot be found using find", em.find(Equipment.class, managedEquip.getId()));
        }finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }
    
    //bug6342382: NEW OBJECT MERGE THROWS OPTIMISTICLOCKEXCEPTION
    public void testMergeNewObject3_UseSequencing() {
        internalTestMergeNewObject3(true);
    }
    
    //bug6342382: NEW OBJECT MERGE THROWS OPTIMISTICLOCKEXCEPTION
    public void testMergeNewObject3_DontUseSequencing() {
        internalTestMergeNewObject3(false);
    }
    
    // shouldUseSequencing == false indicates that PKs should be explicitly assigned to the objects
    // rather than generated by sequencing.
    protected void internalTestMergeNewObject3(boolean shouldUseSequencing) {
        int id = 0;
        if(!shouldUseSequencing) {
            // obtain the last used sequence number
            Employee emp = new Employee();
            EntityManager em = createEntityManager();
            beginTransaction(em);
            em.persist(emp);
            commitTransaction(em);
            id = emp.getId();
            beginTransaction(em);
            em.remove(emp);
            commitTransaction(em);
            closeEntityManager(em);
        }
        
    	//create two Employees:
        String firstName = "testMergeNewObjectEmployee3";
        Employee manager = new Employee();
        manager.setFirstName(firstName);
        manager.setLastName("Manager");
        if(!shouldUseSequencing) {
            manager.setId(id++);
        }
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName("Employee");
        if(!shouldUseSequencing) {
            employee.setId(id++);
        }
        manager.addManagedEmployee(employee);
    
        //persist the Employee
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            Employee managedEmp = em.merge(manager);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
    
    //merge(null) should throw IllegalArgumentException
    public void testMergeNull(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
        	em.merge(null);
        }catch (IllegalArgumentException iae){
        	return;
        }catch (Exception e) {
        	fail("Wrong exception type thrown: " + e.getClass());
        }finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        fail("No exception thrown when entityManager.merge(null) attempted.");        
    }
    
    //persist(null) should throw IllegalArgumentException
    public void testPersistNull(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
        	em.persist(null);
        }catch (IllegalArgumentException iae){
        	return;
        }catch (Exception e) {
        	fail("Wrong exception type thrown: " + e.getClass());
        }finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        fail("No exception thrown when entityManager.persist(null) attempted.");        
    }
    
    //contains(null) should throw IllegalArgumentException
    public void testContainsNull(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
        	em.contains(null);
        }catch (IllegalArgumentException iae){
        	return;
        }catch (Exception e) {
        	fail("Wrong exception type thrown: " + e.getClass());
        }finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        fail("No exception thrown when entityManager.contains(null) attempted.");        
    }

	//bug gf732 - removing null entity should throw an IllegalArgumentException
    public void testRemoveNull(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
        	em.remove(null);
        }catch (IllegalArgumentException iae){
        	return;
        }catch (Exception e) {
        	fail("Wrong exception type thrown: " + e.getClass());
        }finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        fail("No exception thrown when entityManager.remove(null) attempted.");        
    }
    
    /**
     * GlassFish Bug854, originally calling with null caused a null-pointer exception.
     */
    public void testCreateEntityManagerFactory() {
        EntityManagerFactory factory = null;

        try {
            // First call with correct properties, to ensure test does not corrupt factory.
            Persistence.createEntityManagerFactory("default", JUnitTestCaseHelper.getDatabaseProperties());
            factory = Persistence.createEntityManagerFactory("default", null);
            factory = Persistence.createEntityManagerFactory("default");
        } finally {
            if (factory != null) {
                factory.close();
            }
        }
    }
    
    //GlassFish Bug854  PU name doesn't exist or PU with the wrong name
    public void testCreateEntityManagerFactory2() {
        EntityManagerFactory emf = null;
        PersistenceProvider provider = new PersistenceProvider();
        try{
            try {
                emf = provider.createEntityManagerFactory("default123", null);
            } catch (Exception e) {
                fail("Exception is not expected, but thrown:" + e);
            }
            assertNull(emf);
            try {
                emf = Persistence.createEntityManagerFactory("default123");
                fail("PersistenceException is expected");
            } catch (Exception e) {
                assertTrue("The exception should be a PersistenceException", e instanceof PersistenceException);
            }
        } finally{
            if (emf != null) {
                emf.close();
            }
        }
    }
    
    //Glassfish bug 702 - prevent primary key updates
    public void testPrimaryKeyUpdate() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Employee emp = new Employee();
        emp.setFirstName("Groucho");
        emp.setLastName("Marx");
        em.persist(emp);

        Integer id = emp.getId();
        commitTransaction(em);
        
        beginTransaction(em);
        emp = em.merge(emp);
        emp.setId(id + 1);
        
        try {
            commitTransaction(em);
        } catch (Exception exception) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            Throwable persistenceException = exception;
            if (exception.getCause() instanceof javax.transaction.RollbackException) {
                // In the server this is always a rollback exception, need to get nested exception.
                persistenceException = exception.getCause().getCause();
            }
            if (!(persistenceException instanceof PersistenceException)) {            
                AssertionFailedError failure = new AssertionFailedError("Wrong exception type thrown: " + persistenceException.getClass());
                failure.initCause(exception);
                throw failure;
            }
            if (persistenceException.getCause() instanceof ValidationException) {
                ValidationException ve = (ValidationException) persistenceException.getCause();
                if (ve.getErrorCode() == ValidationException.PRIMARY_KEY_UPDATE_DISALLOWED) {
                    return;
                } else {
                    fail("Wrong error code for ValidationException: " + ve.getErrorCode());
                }
            } else {
                fail("ValiationException expected, thrown: " + persistenceException.getCause());
            }
        } finally {
            closeEntityManager(em);
        }
        fail("No exception thrown when primary key update attempted.");        
    }
    
    //Glassfish bug 702 - prevent primary key updates, same value is ok
    public void testPrimaryKeyUpdateSameValue() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Employee emp = new Employee();
        emp.setFirstName("Harpo");
        emp.setLastName("Marx");
        em.persist(emp);

        Integer id = emp.getId();
        commitTransaction(em);
        
        beginTransaction(em);
        emp.setId(id);
        
        try {
            commitTransaction(em);
        } catch (Exception e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            fail("Unexpected exception thrown: " + e.getClass());
        } finally {
            closeEntityManager(em);
        }
    }

    //Glassfish bug 702 - prevent primary key updates, overlapping PK/FK
    public void testPrimaryKeyUpdatePKFK() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Employee emp = new Employee();
        emp.setFirstName("Groucho");
        emp.setLastName("Marx");
        em.persist(emp);

        Employee emp2 = new Employee();
        emp2.setFirstName("Harpo");
        emp2.setLastName("Marx");
        em.persist(emp2);

        PhoneNumber phone = new PhoneNumber("home", "415", "0007");
        phone.setOwner(emp);
        em.persist(phone);
        commitTransaction(em);
        
        beginTransaction(em);
        phone.setOwner(emp2);
        
        try {
            commitTransaction(em);
        } catch (Exception exception) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            Throwable persistenceException = exception;
            if (exception.getCause() instanceof javax.transaction.RollbackException) {
                // In the server this is always a rollback exception, need to get nested exception.
                persistenceException = exception.getCause().getCause();
            }
            if (!(persistenceException instanceof PersistenceException)) {
                AssertionFailedError failure = new AssertionFailedError("Wrong exception type thrown: " + persistenceException.getClass());
                failure.initCause(exception);
                throw failure;
            }
            if (persistenceException.getCause() instanceof ValidationException) {
                ValidationException ve = (ValidationException) persistenceException.getCause();
                if (ve.getErrorCode() == ValidationException.PRIMARY_KEY_UPDATE_DISALLOWED) {
                    return;
                } else {
                    AssertionFailedError failure = new AssertionFailedError("Wrong error code for ValidationException: " + ve.getErrorCode());
                    failure.initCause(ve);
                    throw failure;
                }
            } else {
                AssertionFailedError failure = new AssertionFailedError("ValiationException expected, thrown: " + persistenceException.getCause());
                failure.initCause(persistenceException);
                throw failure;
            }
        } finally {
            closeEntityManager(em);
        }
        fail("No exception thrown when primary key update attempted.");        
    }
    
    // Test cascade merge on a detached entity
    public void testCascadeMergeDetached() {
        // setup
        Project p1 = new Project();
        p1.setName("Project1");
        Project p2 = new Project();
        p1.setName("Project2");
        Employee e1 = new Employee();
        e1.setFirstName("Employee1");
        Employee e2 = new Employee();
        e2.setFirstName("Employee2");
        
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.persist(p1);
            em.persist(p2);
            em.persist(e1);
            em.persist(e2);

            commitTransaction(em);
        } catch (RuntimeException re){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw re;
        }
        closeEntityManager(em);
        // end of setup

        //p1,p2,e1,e2 are detached
        
        // associate relationships
        //p1 -> e1 (one-to-one)
        p1.setTeamLeader(e1);
        //e1 -> e2 (one-to-many)
        e1.addManagedEmployee(e2);
        //e2 -> p2 (many-to-many)
        e2.addProject(p2);
        p2.addTeamMember(e2);

        em = createEntityManager();
        beginTransaction(em);
        try {
            Project mp1 = em.merge(p1); // cascade merge
            assertTrue(em.contains(mp1));
            assertTrue("Managed instance and detached instance must not be same", mp1 != p1);
            
            Employee me1 = mp1.getTeamLeader();
            assertTrue("Cascade merge failed", em.contains(me1));
            assertTrue("Managed instance and detached instance must not be same", me1 != e1);
            
            Employee me2 = me1.getManagedEmployees().iterator().next();
            assertTrue("Cascade merge failed", em.contains(me2));
            assertTrue("Managed instance and detached instance must not be same", me2 != e2);

            Project mp2 = me2.getProjects().iterator().next();
            assertTrue("Cascade merge failed", em.contains(mp2));
            assertTrue("Managed instance and detached instance must not be same", mp2 != p2);

            commitTransaction(em);
        } catch (RuntimeException re){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw re;
        }
        closeEntityManager(em);
    }

    // Test cascade merge on a managed entity
    // Test for GF#1139 - Cascade doesn't work when merging managed entity
    public void testCascadeMergeManaged() {
        // setup
        Project p1 = new Project();
        p1.setName("Project1");
        Project p2 = new Project();
        p1.setName("Project2");
        Employee e1 = new Employee();
        e1.setFirstName("Employee1");
        Employee e2 = new Employee();
        e2.setFirstName("Employee2");
            
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.persist(p1);
            em.persist(p2);
            em.persist(e1);
            em.persist(e2);

            commitTransaction(em);
        } catch (RuntimeException re){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw re;
        }
        closeEntityManager(em);
        // end of setup
        
        //p1,p2,e1,e2 are detached
        em = createEntityManager();
        beginTransaction(em);
        try {
            Project mp1 = em.merge(p1);
            assertTrue(em.contains(mp1));
            assertTrue("Managed instance and detached instance must not be same", mp1 != p1);

            //p1 -> e1 (one-to-one)
            mp1.setTeamLeader(e1);
            mp1 = em.merge(mp1); // merge again - trigger cascade merge

            Employee me1 = mp1.getTeamLeader();
            assertTrue("Cascade merge failed", em.contains(me1));
            assertTrue("Managed instance and detached instance must not be same", me1 != e1);

            //e1 -> e2 (one-to-many)
            me1.addManagedEmployee(e2);
            me1 = em.merge(me1); // merge again - trigger cascade merge
            
            Employee me2 = me1.getManagedEmployees().iterator().next();
            assertTrue("Cascade merge failed", em.contains(me2));
            assertTrue("Managed instance and detached instance must not be same", me2 != e2);

            //e2 -> p2 (many-to-many)
            me2.addProject(p2);
            p2.addTeamMember(me2);
            me2 = em.merge(me2); // merge again - trigger cascade merge

            Project mp2 = me2.getProjects().iterator().next();
            assertTrue("Cascade merge failed", em.contains(mp2));
            assertTrue("Managed instance and detached instance must not be same", mp2 != p2);

            commitTransaction(em);
        } catch (RuntimeException re){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw re;
        }
        closeEntityManager(em);
    }

    //Glassfish bug 1021 - allow cascading persist operation to non-entities
    public void testCascadePersistToNonEntitySubclass() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Employee emp = new Employee();
        emp.setFirstName("Albert");
        emp.setLastName("Einstein");

        SuperLargeProject s1 = new SuperLargeProject("Super 1");
        Collection projects = new ArrayList();
        projects.add(s1);
        emp.setProjects(projects);
        em.persist(emp);

        try {
            commitTransaction(em);
        } catch (Exception e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            fail("Persist operation was not cascaded to related non-entity, thrown: " + e);
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Bug 801
     * Test to ensure when property access is used and the underlying variable is changed the change
     * is correctly reflected in the database
     * 
     * In this test we test making the change before the object is managed
     */
    public void testInitializeFieldForPropertyAccess(){
        Employee employee = new Employee();
        employee.setFirstName("Andy");
        employee.setLastName("Dufresne");
        Address address = new Address();
        address.setCity("Shawshank");
        employee.setAddressField(address);
        
        EntityManager em = createEntityManager();
        beginTransaction(em);
        em.persist(employee);
        try{
            commitTransaction(em);
        } catch (RuntimeException e){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }
        int id = employee.getId();
        
        clearCache();
        
        em = createEntityManager();
        beginTransaction(em);
        try {
            employee = em.find(Employee.class, new Integer(id));
            address = employee.getAddress();
            
            assertTrue("The address was not persisted.", employee.getAddress() != null);
            assertTrue("The address was not correctly persisted.", employee.getAddress().getCity().equals("Shawshank"));
        } finally {        
            employee.setAddress((Address)null);
            em.remove(address);
            em.remove(employee);
            commitTransaction(em);
        }    
    }

    /**
     * Bug 801
     * Test to ensure when property access is used and the underlying variable is changed the change
     * is correctly reflected in the database
     * 
     * In this test we test making the change after the object is managed
     */
    public void testSetFieldForPropertyAccess(){       
        EntityManager em = createEntityManager();
        
        Employee employee = new Employee();
        employee.setFirstName("Andy");
        employee.setLastName("Dufresne");
        Address address = new Address();
        address.setCity("Shawshank");
        employee.setAddress(address);
        Employee manager = new Employee();
        manager.setFirstName("Bobby");
        manager.setLastName("Dufresne");
        employee.setManager(manager);
        
        beginTransaction(em);
        em.persist(employee);
        commitTransaction(em);
        int id = employee.getId();
        int addressId = address.getId();
        int managerId = manager.getId();
        
        beginTransaction(em);
        employee = em.find(Employee.class, new Integer(id));
        employee.getAddress();

        address = new Address();
        address.setCity("Metropolis");
        employee.setAddress(address);
        manager = new Employee();
        manager.setFirstName("Metro");
        manager.setLastName("Dufresne");
        employee.setManagerField(manager);
        try {
            commitTransaction(em);
        } catch (RuntimeException e){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }
        
        clearCache();
        
        em = createEntityManager();
        beginTransaction(em);
        
        employee = em.find(Employee.class, new Integer(id));
        address = employee.getAddress();
        manager = employee.getManager();

        assertTrue("The address was not persisted.", employee.getAddress() != null);
        assertTrue("The address was not correctly persisted.", employee.getAddress().getCity().equals("Metropolis"));

        assertTrue("The manager was not persisted.", employee.getManager() != null);
        assertTrue("The manager was not correctly persisted.", employee.getManager().getFirstName().equals("Metro"));
        
        Address initialAddress = em.find(Address.class, new Integer(addressId));
        Employee initialManager = em.find(Employee.class, new Integer(managerId));
        employee.setAddress((Address)null);
        em.remove(address);
        em.remove(employee);
        em.remove(manager);
        em.remove(initialAddress);
        em.remove(initialManager);
        commitTransaction(em);
    }

    /**
     * Bug 801
     * Test to ensure when property access is used and the underlying variable is changed the change
     * is correctly reflected in the database
     * 
     * In this test we test making the change after the object is refreshed
     */
    public void testSetFieldForPropertyAccessWithRefresh(){       
        EntityManager em = createEntityManager();
        
        Employee employee = new Employee();
        employee.setFirstName("Andy");
        employee.setLastName("Dufresne");
        Address address = new Address();
        address.setCity("Shawshank");
        employee.setAddress(address);
        Employee manager = new Employee();
        manager.setFirstName("Bobby");
        manager.setLastName("Dufresne");
        employee.setManager(manager);
        
        beginTransaction(em);
        em.persist(employee);
        commitTransaction(em);
        int id = employee.getId();
        int addressId = address.getId();
        int managerId = manager.getId();
        
        beginTransaction(em);
        employee = em.getReference(Employee.class, employee.getId());
        em.refresh(employee);
        employee.getAddress();

        address = new Address();
        address.setCity("Metropolis");
        employee.setAddress(address);
        manager = new Employee();
        manager.setFirstName("Metro");
        manager.setLastName("Dufresne");
        employee.setManagerField(manager);
        try{
            commitTransaction(em);
        } catch (RuntimeException e){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }
        
        clearCache();
        
        em = createEntityManager();
        beginTransaction(em);
        
        employee = em.find(Employee.class, new Integer(id));
        address = employee.getAddress();
        manager = employee.getManager();

        assertTrue("The address was not persisted.", employee.getAddress() != null);
        assertTrue("The address was not correctly persisted.", employee.getAddress().getCity().equals("Metropolis"));

        assertTrue("The manager was not persisted.", employee.getManager() != null);
        assertTrue("The manager was not correctly persisted.", employee.getManager().getFirstName().equals("Metro"));
        
        Address initialAddress = em.find(Address.class, new Integer(addressId));
        Employee initialManager = em.find(Employee.class, new Integer(managerId));
        employee.setAddress((Address)null);
        employee.setManager((Employee) null);
        em.remove(address);
        em.remove(employee);
        em.remove(manager);
        em.remove(initialAddress);
        em.remove(initialManager);
        commitTransaction(em);
    }
    
    /**
     * Bug 801
     * Test to ensure when property access is used and the underlying variable is changed the change
     * is correctly reflected in the database
     * 
     * In this test we test making the change when an existing object is read into a new EM
     */
    public void testSetFieldForPropertyAccessWithNewEM(){       
        EntityManager em = createEntityManager();
        
        Employee employee = new Employee();
        employee.setFirstName("Andy");
        employee.setLastName("Dufresne");
        Employee manager = new Employee();
        manager.setFirstName("Bobby");
        manager.setLastName("Dufresne");
        employee.setManager(manager);
        Address address = new Address();
        address.setCity("Shawshank");
        employee.setAddress(address);
        
        beginTransaction(em);
        em.persist(employee);
        commitTransaction(em);
        int id = employee.getId();
        int addressId = address.getId();
        int managerId = manager.getId();
        
        em = createEntityManager();
        
        beginTransaction(em);
        employee = em.find(Employee.class, new Integer(id));
        employee.getAddress();
        employee.getManager();

        address = new Address();
        address.setCity("Metropolis");
        employee.setAddress(address);
        manager = new Employee();
        manager.setFirstName("Metro");
        manager.setLastName("Dufresne");
        employee.setManagerField(manager);
        try {
            commitTransaction(em);
        } catch (RuntimeException e){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }
        
        clearCache();

        em = createEntityManager();
        beginTransaction(em);
        employee = em.find(Employee.class, new Integer(id));
        address = employee.getAddress();
        manager = employee.getManager();

        assertTrue("The address was not persisted.", employee.getAddress() != null);
        assertTrue("The address was not correctly persisted.", employee.getAddress().getCity().equals("Metropolis"));

        assertTrue("The manager was not persisted.", employee.getManager() != null);
        assertTrue("The manager was not correctly persisted.", employee.getManager().getFirstName().equals("Metro"));
        
        Address initialAddress = em.find(Address.class, new Integer(addressId));
        Employee initialManager = em.find(Employee.class, new Integer(managerId));
        
        employee.setAddress((Address)null);
        em.remove(address);
        em.remove(employee);
        em.remove(manager);
        em.remove(initialAddress);
        em.remove(initialManager);
        commitTransaction(em);
    }

    //bug gf674 - EJBQL delete query with IS NULL in WHERE clause produces wrong sql
    public void testDeleteAllPhonesWithNullOwner() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.createQuery("DELETE FROM PhoneNumber ph WHERE ph.owner IS NULL").executeUpdate();
        } catch (Exception e) {
            fail("Exception thrown: " + e.getClass());
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
     
     public void testDeleteAllProjectsWithNullTeamLeader() {
         internalDeleteAllProjectsWithNullTeamLeader("Project");
     }
     public void testDeleteAllSmallProjectsWithNullTeamLeader() {
         internalDeleteAllProjectsWithNullTeamLeader("SmallProject");
     }
     public void testDeleteAllLargeProjectsWithNullTeamLeader() {
         internalDeleteAllProjectsWithNullTeamLeader("LargeProject");
     }
     protected void internalDeleteAllProjectsWithNullTeamLeader(String className) {
         String name = "testDeleteAllProjectsWithNull";
         
         // setup
         SmallProject sp = new SmallProject();
         sp.setName(name);
         LargeProject lp = new LargeProject();
         lp.setName(name);
         EntityManager em = createEntityManager();
         try {
             beginTransaction(em);
             // make sure there are no pre-existing objects with this name
           	em.createQuery("DELETE FROM "+className+" p WHERE p.name = '"+name+"'").executeUpdate();
             em.persist(sp);
             em.persist(lp);
             commitTransaction(em);
         } catch (RuntimeException ex){
            if (isTransactionActive(em)){
                 rollbackTransaction(em);
            }
             throw ex;
         } finally {
             closeEntityManager(em);
         }
                 
         // test
         em = createEntityManager();
         beginTransaction(em);
         try {
         	em.createQuery("DELETE FROM "+className+" p WHERE p.name = '"+name+"' AND p.teamLeader IS NULL").executeUpdate();
             commitTransaction(em);
         } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                 rollbackTransaction(em);
            }
            throw e;
         } finally {
             closeEntityManager(em);
         }

         // verify
         String error = null;
         em = createEntityManager();
         List result = em.createQuery("SELECT OBJECT(p) FROM Project p WHERE p.name = '"+name+"'").getResultList();
         if(result.isEmpty()) {
             if(!className.equals("Project")) {
                 error = "Target Class " + className +": no objects left";
             }
         } else {
             if(result.size() > 1) {
                 error = "Target Class " + className +": too many objects left: " + result.size();
             } else {
                 Project p = (Project)result.get(0);
                 if(p.getClass().getName().endsWith(className)) {
                     error = "Target Class " + className +": object of wrong type left: " + p.getClass().getName();
                 }
             }
         }

         // clean up
         try {
             beginTransaction(em);
             // make sure there are no pre-existing objects with this name
           	em.createQuery("DELETE FROM "+className+" p WHERE p.name = '"+name+"'").executeUpdate();
             commitTransaction(em);
         } catch (RuntimeException ex){
            if (isTransactionActive(em)){
                 rollbackTransaction(em);
            }
             throw ex;
         } finally {
             closeEntityManager(em);
         }
         
         if(error != null) {
             fail(error);
         }
     }

    // gf1408: DeleteAll and UpdateAll queries broken on some db platforms;
    // gf1451: Complex updates to null using temporary storage do not work on Derby;
    // gf1860: TopLink provides too few values.
    // The tests forces the use of temporary storage to test null assignment to an integer field
    // on all platforms.
    public void testUpdateUsingTempStorage() {
        internalUpdateUsingTempStorage(false);
    }
    public void testUpdateUsingTempStorageWithParameter() {
        internalUpdateUsingTempStorage(true);
    }
    protected void internalUpdateUsingTempStorage(boolean useParameter) {
        String firstName = "testUpdateUsingTempStorage";
        int n = 3;
        
        // setup
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            // make sure there are no pre-existing objects with this name
           	em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
           	em.createQuery("DELETE FROM Address a WHERE a.country = '"+firstName+"'").executeUpdate();
            // populate Employees
            for(int i=1; i<=n; i++) {
                Employee emp = new Employee();
                emp.setFirstName(firstName);
                emp.setLastName(Integer.toString(i));
                emp.setSalary(i*100);
                emp.setRoomNumber(i);
                
                Address address = new Address();
                address.setCountry(firstName);
                address.setCity(Integer.toString(i));
                
                emp.setAddress(address);

                em.persist(emp);
            }
            commitTransaction(em);
        } catch (RuntimeException ex){
            if (isTransactionActive(em)){
                 rollbackTransaction(em);
            }
            throw ex;
        } finally {
            closeEntityManager(em);
        }
                
        // test
        em = createEntityManager();
        beginTransaction(em);
        int nUpdated = 0;
        try {
            if(useParameter) {
                nUpdated = em.createQuery("UPDATE Employee e set e.salary = e.roomNumber, e.roomNumber = e.salary, e.address = :address where e.firstName = '" + firstName + "'").setParameter("address", null).executeUpdate();
            } else {
                nUpdated = em.createQuery("UPDATE Employee e set e.salary = e.roomNumber, e.roomNumber = e.salary, e.address = null where e.firstName = '" + firstName + "'").executeUpdate();
            }
            commitTransaction(em);
        } catch (Exception e) {
            if (isTransactionActive(em)){
                 rollbackTransaction(em);
            }
        	fail("Exception thrown: " + e.getClass());
        } finally {
            closeEntityManager(em);
        }

        // verify
        String error = null;
        em = createEntityManager();
        List result = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.firstName = '"+firstName+"'").getResultList();
        closeEntityManager(em);
        int nReadBack = result.size();
        if(n != nUpdated) {
            error = "n = "+n+", but nUpdated ="+nUpdated+";";
        }
        if(n != nReadBack) {
            error = " n = "+n+", but nReadBack ="+nReadBack+";";
        }
        for(int i=0; i<nReadBack; i++) {
            Employee emp = (Employee)result.get(i);
            if(emp.getAddress() != null) {
                error = " Employee "+emp.getLastName()+" still has address;";
            }
            int ind = Integer.valueOf(emp.getLastName()).intValue();
            if(emp.getSalary() != ind) {
                error = " Employee "+emp.getLastName()+" has wrong salary "+emp.getSalary()+";";
            }
            if(emp.getRoomNumber() != ind*100) {
                error = " Employee "+emp.getLastName()+" has wrong roomNumber "+emp.getRoomNumber()+";";
            }
        }

        // clean up
        em = createEntityManager();
        try {
            beginTransaction(em);
            // make sure there are no objects left with this name
          	em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
           	em.createQuery("DELETE FROM Address a WHERE a.country = '"+firstName+"'").executeUpdate();
            commitTransaction(em);
        } catch (RuntimeException ex){
            if (isTransactionActive(em)){
                 rollbackTransaction(em);
            }
            throw ex;
        } finally {
            closeEntityManager(em);
        }
        
        if(error != null) {
            fail(error);
        }
    }

    protected void createProjectsWithName(String name, Employee teamLeader) {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

            SmallProject sp = new SmallProject();
            sp.setName(name);

            LargeProject lp = new LargeProject();
            lp.setName(name);

            em.persist(sp);
            em.persist(lp);
            
            if(teamLeader != null) {
                SmallProject sp2 = new SmallProject();
                sp2.setName(name);
                sp2.setTeamLeader(teamLeader);
    
                LargeProject lp2 = new LargeProject();
                lp2.setName(name);
                lp2.setTeamLeader(teamLeader);
    
                em.persist(sp2);
                em.persist(lp2);   
            }

            commitTransaction(em);
        } catch (RuntimeException ex) {
            if(isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }

    protected void deleteProjectsWithName(String name) {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

          	em.createQuery("DELETE FROM Project p WHERE p.name = '"+name+"'").executeUpdate();
            
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if(isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }

    public void testUpdateAllSmallProjects() {
        internalTestUpdateAllProjects(SmallProject.class);
    }
    public void testUpdateAllLargeProjects() {
        internalTestUpdateAllProjects(LargeProject.class);
    }
    public void testUpdateAllProjects() {
        internalTestUpdateAllProjects(Project.class);
    }
    protected void internalTestUpdateAllProjects(Class cls) {
        String className = Helper.getShortClassName(cls);
        String name = "testUpdateAllProjects";
        String newName = "testUpdateAllProjectsNEW";
        HashMap map = null;
        boolean ok = false;
        
        try {
            // setup
            // populate Projects - necessary only if no SmallProject and/or LargeProject objects already exist.
            createProjectsWithName(name, null);
            // save the original names of projects: will set them back in cleanup
            // to restore the original state.
            EntityManager em = createEntityManager();
            List projects = em.createQuery("SELECT OBJECT(p) FROM Project p").getResultList();
            map = new HashMap(projects.size());
            for(int i=0; i<projects.size(); i++) {
                Project p = (Project)projects.get(i);
                map.put(p.getId(), p.getName());
            }        
    
            // test
            beginTransaction(em);
            try {
                em.createQuery("UPDATE "+className+" p set p.name = '"+newName+"'").executeUpdate();
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if(isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                throw ex;
            } finally {
                closeEntityManager(em);
            }
            
            // verify
            em = createEntityManager();
            String errorMsg = "";
            projects = em.createQuery("SELECT OBJECT(p) FROM Project p").getResultList();
            for(int i=0; i<projects.size(); i++) {
                Project p = (Project)projects.get(i);
                String readName = p.getName();
                if(cls.isInstance(p)) {
                    if(!newName.equals(readName)) {
                        errorMsg = errorMsg + "haven't updated name: " + p + "; ";
                    }
                } else {
                    if(newName.equals(readName)) {
                        errorMsg = errorMsg + "have updated name: " + p + "; ";
                    }
                }
            }
            closeEntityManager(em);

            if(errorMsg.length() > 0) {
                fail(errorMsg);
            } else {
                ok = true;
            }
        } finally {
            // clean-up
            try {
                if(map != null) {
                    EntityManager em = createEntityManager();
                    beginTransaction(em);
                    List projects = em.createQuery("SELECT OBJECT(p) FROM Project p").getResultList();
                    try {
                        for(int i=0; i<projects.size(); i++) {
                            Project p = (Project)projects.get(i);
                            String oldName = (String)map.get(((Project)projects.get(i)).getId());
                            p.setName(oldName);
                        }
                        commitTransaction(em);
                    } catch (RuntimeException ex) {
                        if(isTransactionActive(em)) {
                            rollbackTransaction(em);
                        }
                        throw ex;
                    } finally {
                        closeEntityManager(em);
                    }
                }
                // delete projects that createProjectsWithName has created in setup
                deleteProjectsWithName(name);
            } catch (RuntimeException ex) {
                // eat clean-up exception in case the test failed
                if(ok) {
                    throw ex;
                }
            }
        }        
    }
    
    public void testUpdateAllSmallProjectsWithName() {
        internalTestUpdateAllProjectsWithName(SmallProject.class);
    }
    public void testUpdateAllLargeProjectsWithName() {
        internalTestUpdateAllProjectsWithName(LargeProject.class);
    }
    public void testUpdateAllProjectsWithName() {
        internalTestUpdateAllProjectsWithName(Project.class);
    }
    protected void internalTestUpdateAllProjectsWithName(Class cls) {
        String className = Helper.getShortClassName(cls);
        String name = "testUpdateAllProjects";
        String newName = "testUpdateAllProjectsNEW";
        boolean ok = false;
        
        try {
            // setup
            // make sure no projects with the specified names exist
            deleteProjectsWithName(name);
            deleteProjectsWithName(newName);
            // populate Projects
            createProjectsWithName(name, null);
    
            // test
            EntityManager em = createEntityManager();
            beginTransaction(em);
            try {
                em.createQuery("UPDATE "+className+" p set p.name = '"+newName+"' WHERE p.name = '"+name+"'").executeUpdate();
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if(isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                throw ex;
            } finally {
                closeEntityManager(em);
            }
            
            // verify
            em = createEntityManager();
            String errorMsg = "";
            List projects = em.createQuery("SELECT OBJECT(p) FROM Project p WHERE p.name = '"+newName+"' OR p.name = '"+name+"'").getResultList();
            for(int i=0; i<projects.size(); i++) {
                Project p = (Project)projects.get(i);
                String readName = p.getName();
                if(cls.isInstance(p)) {
                    if(!readName.equals(newName)) {
                        errorMsg = errorMsg + "haven't updated name: " + p + "; ";
                    }
                } else {
                    if(readName.equals(newName)) {
                        errorMsg = errorMsg + "have updated name: " + p + "; ";
                    }
                }
            }
            closeEntityManager(em);
            
            if(errorMsg.length() > 0) {
                fail(errorMsg);
            } else {
                ok = true;
            }
        } finally {
            // clean-up
            // make sure no projects with the specified names left
            try {
                deleteProjectsWithName(name);
                deleteProjectsWithName(newName);
            } catch (RuntimeException ex) {
                // eat clean-up exception in case the test failed
                if(ok) {
                    throw ex;
                }
            }
        }
    }
    
    public void testUpdateAllSmallProjectsWithNullTeamLeader() {
        internalTestUpdateAllProjectsWithNullTeamLeader(SmallProject.class);
    }
    public void testUpdateAllLargeProjectsWithNullTeamLeader() {
        internalTestUpdateAllProjectsWithNullTeamLeader(LargeProject.class);
    }
    public void testUpdateAllProjectsWithNullTeamLeader() {
        internalTestUpdateAllProjectsWithNullTeamLeader(Project.class);
    }
    protected void internalTestUpdateAllProjectsWithNullTeamLeader(Class cls) {
        String className = Helper.getShortClassName(cls);
        String name = "testUpdateAllProjects";
        String newName = "testUpdateAllProjectsNEW";
        Employee empTemp = null;
        boolean ok = false;
        
        try {
            // setup
            // make sure no projects with the specified names exist
            deleteProjectsWithName(name);
            deleteProjectsWithName(newName);
            EntityManager em = createEntityManager();
            Employee emp = null;
            List employees = em.createQuery("SELECT OBJECT(e) FROM Employee e").getResultList();
            if(employees.size() > 0) {
                emp = (Employee)employees.get(0);
            } else {
                beginTransaction(em);
                try {
                    emp = new Employee();
                    emp.setFirstName(name);
                    emp.setLastName("TeamLeader");
                    em.persist(emp);
                    commitTransaction(em);
                    empTemp = emp;
                } catch (RuntimeException ex) {
                    if(isTransactionActive(em)) {
                        rollbackTransaction(em);
                    }
                    closeEntityManager(em);
                    throw ex;
                }
            }
            closeEntityManager(em);
            // populate Projects
            createProjectsWithName(name, emp);
    
            // test
            em = createEntityManager();
            beginTransaction(em);
            try {
                em.createQuery("UPDATE "+className+" p set p.name = '"+newName+"' WHERE p.name = '"+name+"' AND p.teamLeader IS NULL").executeUpdate();
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if(isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                throw ex;
            } finally {
                closeEntityManager(em);
            }
            
            // verify
            em = createEntityManager();
            String errorMsg = "";
            List projects = em.createQuery("SELECT OBJECT(p) FROM Project p WHERE p.name = '"+newName+"' OR p.name = '"+name+"'").getResultList();
            for(int i=0; i<projects.size(); i++) {
                Project p = (Project)projects.get(i);
                String readName = p.getName();
                if(cls.isInstance(p) && p.getTeamLeader()==null) {
                    if(!readName.equals(newName)) {
                        errorMsg = errorMsg + "haven't updated name: " + p + "; ";
                    }
                } else {
                    if(readName.equals(newName)) {
                        errorMsg = errorMsg + "have updated name: " + p + "; ";
                    }
                }
            }
            closeEntityManager(em);
            
            if(errorMsg.length() > 0) {
                fail(errorMsg);
            } else {
                ok = true;
            }
        } finally {
            // clean-up
            // make sure no projects with the specified names exist
            try {
                deleteProjectsWithName(name);
                deleteProjectsWithName(newName);
                if(empTemp != null) {
                    EntityManager em = createEntityManager();
                    beginTransaction(em);
                    try {
                        em.createQuery("DELETE FROM Employee e WHERE e.id = '"+empTemp.getId()+"'").executeUpdate();
                        commitTransaction(em);
                    } catch (RuntimeException ex) {
                        if(isTransactionActive(em)) {
                            rollbackTransaction(em);
                        }
                        throw ex;
                    } finally {
                        closeEntityManager(em);
                    }
                }
            } catch (RuntimeException ex) {
                // eat clean-up exception in case the test failed
                if(ok) {
                    throw ex;
                }
            }
        }
    }
    
    public void testRollbackOnlyOnException() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee emp = em.find(Employee.class, "");
            fail("IllegalArgumentException has not been thrown");
        } catch(IllegalArgumentException ex) {
            if (isOnServer()) {
                assertTrue("Transaction is not roll back only", !isTransactionActive(em));
            } else {
                assertTrue("Transaction is not roll back only", em.getTransaction().getRollbackOnly());
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testClosedEmShouldThrowException() {
        // Close is not used on server.
        if (isOnServer()) {
            return;
        }
        EntityManager em = createEntityManager();
        closeEntityManager(em);
        String errorMsg = "";

        try {
            em.clear();
            errorMsg = errorMsg + "; em.clear() didn't throw exception";
        } catch(IllegalStateException ise) {
            // expected
        } catch(RuntimeException ex) {
            errorMsg = errorMsg + "; em.clear() threw wrong exception: " + ex.getMessage();
        }
        try {
            closeEntityManager(em);
            errorMsg = errorMsg + "; closeEntityManager(em) didn't throw exception";
        } catch(IllegalStateException ise) {
            // expected
        } catch(RuntimeException ex) {
            errorMsg = errorMsg + "; closeEntityManager(em) threw wrong exception: " + ex.getMessage();
        }
        try {
            em.contains(null);
            errorMsg = errorMsg + "; em.contains() didn't throw exception";
        } catch(IllegalStateException ise) {
            // expected
        } catch(RuntimeException ex) {
            errorMsg = errorMsg + "; em.contains threw() wrong exception: " + ex.getMessage();
        }
        try {
            em.getDelegate();
            errorMsg = errorMsg + "; em.getDelegate() didn't throw exception";
        } catch(IllegalStateException ise) {
            // expected
        } catch(RuntimeException ex) {
            errorMsg = errorMsg + "; em.getDelegate() threw wrong exception: " + ex.getMessage();
        }
        try {
            em.getReference(Employee.class, new Integer(1));
            errorMsg = errorMsg + "; em.getReference() didn't throw exception";
        } catch(IllegalStateException ise) {
            // expected
        } catch(RuntimeException ex) {
            errorMsg = errorMsg + "; em.getReference() threw wrong exception: " + ex.getMessage();
        }
        try {
            em.joinTransaction();
            errorMsg = errorMsg + "; em.joinTransaction() didn't throw exception";
        } catch(IllegalStateException ise) {
            // expected
        } catch(RuntimeException ex) {
            errorMsg = errorMsg + "; em.joinTransaction() threw wrong exception: " + ex.getMessage();
        }
        try {
            em.lock(null, null);
            errorMsg = errorMsg + "; em.lock() didn't throw exception";
        } catch(IllegalStateException ise) {
            // expected
        } catch(RuntimeException ex) {
            errorMsg = errorMsg + "; em.lock() threw wrong exception: " + ex.getMessage();
        }
        
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    //gf 1217 - Ensure join table defaults correctly when 'mappedby' not specified
    public void testOneToManyDefaultJoinTableName() {
        Department dept  = new Department();
        Employee manager = new Employee();
        dept.addManager(manager);
        
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            em.persist(dept);
            commitTransaction(em);
        }catch (RuntimeException e) {
            throw e;
        }finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
    
    // gf1732
    public void testMultipleEntityManagerFactories() {
        // close the original factory
        closeEntityManagerFactory();
        // create the new one - not yet deployed
        EntityManagerFactory factory1 =  getEntityManagerFactory();
        // create the second one
        EntityManagerFactory factory2 =  Persistence.createEntityManagerFactory("default", JUnitTestCaseHelper.getDatabaseProperties());
        // deploy
        factory2.createEntityManager();
        // close
        factory2.close();

        try {
            // now try to getEM from the first one - this used to throw exception
            factory1.createEntityManager();
            // don't close factory1 if all is well
        } catch (PersistenceException ex) {
            fail("factory1.createEM threw exception: " + ex.getMessage());
            factory1.close();
        }
    }

    // gf2074: EM.clear throws NPE
    public void testClearEntityManagerWithoutPersistenceContext() {
        EntityManager em = createEntityManager();
        try {
            em.clear();
        }finally {
            closeEntityManager(em);
        }
    }
    
    // Used by testClearEntityManagerWithoutPersistenceContextSimulateJTA().
    // At first tried to use JTATransactionController class, but that introduced dependencies 
    // on javax.transaction package (and therefore failed in gf entity persistence tests).
    static class DummyExternalTransactionController extends org.eclipse.persistence.transaction.AbstractTransactionController {
        public boolean isRolledBack_impl(Object status){return false;}
        protected void registerSynchronization_impl(org.eclipse.persistence.transaction.AbstractSynchronizationListener listener, Object txn) throws Exception{}
        protected Object getTransaction_impl() throws Exception {return null;}
        protected Object getTransactionKey_impl(Object transaction) throws Exception {return null;}
        protected Object getTransactionStatus_impl() throws Exception {return null;}
        protected void beginTransaction_impl() throws Exception{}
        protected void commitTransaction_impl() throws Exception{}
        protected void rollbackTransaction_impl() throws Exception{}
        protected void markTransactionForRollback_impl() throws Exception{}
        protected boolean canBeginTransaction_impl(Object status){return false;}
        protected boolean canCommitTransaction_impl(Object status){return false;}
        protected boolean canRollbackTransaction_impl(Object status){return false;}
        protected boolean canIssueSQLToDatabase_impl(Object status){return false;}
        protected boolean canMergeUnitOfWork_impl(Object status){return false;}
        protected String statusToString_impl(Object status){return "";}
    }
    // gf2074: EM.clear throws NPE (JTA case)
    public void testClearEntityManagerWithoutPersistenceContextSimulateJTA() {
        EntityManager em = createEntityManager();
        ServerSession ss = ((org.eclipse.persistence.jpa.JpaEntityManager)em).getServerSession();
        closeEntityManager(em);
        // in non-JTA case session doesn't have external transaction controller
        boolean hasExternalTransactionController = ss.hasExternalTransactionController();
        if(!hasExternalTransactionController) {
            // simulate JTA case
            ss.setExternalTransactionController(new DummyExternalTransactionController());
        }
        try {
            testClearEntityManagerWithoutPersistenceContext();
        }finally {
            if(!hasExternalTransactionController) {
                // remove the temporary set TransactionController
                ss.setExternalTransactionController(null);
            }
        }
    }
    
    public void testDescriptorNamedQuery(){
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("firstName").equal(builder.getParameter("fName"));
        exp = exp.and(builder.get("lastName").equal(builder.getParameter("lName")));
        query.setSelectionCriteria(exp);
        query.addArgument("fName", String.class);
        query.addArgument("lName", String.class);
        
        EntityManager em = createEntityManager();
        Session session = ((EntityManagerImpl)em.getDelegate()).getServerSession();
        ClassDescriptor descriptor = session.getDescriptor(Employee.class);
        descriptor.getQueryManager().addQuery("findByFNameLName", query);

        beginTransaction(em);
        try {
            Employee emp = new Employee();
            emp.setFirstName("Melvin");
            emp.setLastName("Malone");
            em.persist(emp);
            em.flush();
            
            Query ejbQuery = ((org.eclipse.persistence.jpa.JpaEntityManager)em).createDescriptorNamedQuery("findByFNameLName", Employee.class);
            
            List results = ejbQuery.setParameter("fName", "Melvin").setParameter("lName", "Malone").getResultList();
            
            assertTrue(results.size() == 1);
            emp = (Employee)results.get(0);
            assertTrue(emp.getFirstName().equals("Melvin"));
            assertTrue(emp.getLastName().equals("Malone"));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        
        descriptor.getQueryManager().removeQuery("findByFNameLName");
    }
    
    public void testDescriptorNamedQueryForMultipleQueries(){
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("firstName").equal(builder.getParameter("fName"));
        exp = exp.and(builder.get("lastName").equal(builder.getParameter("lName")));
        query.setSelectionCriteria(exp);
        query.addArgument("fName", String.class);
        query.addArgument("lName", String.class);
        
        ReadAllQuery query2 = new ReadAllQuery(Employee.class);

        
        EntityManager em = createEntityManager();
        Session session = ((EntityManagerImpl)em.getDelegate()).getServerSession();
        ClassDescriptor descriptor = session.getDescriptor(Employee.class);
        descriptor.getQueryManager().addQuery("findEmployees", query);
        descriptor.getQueryManager().addQuery("findEmployees", query2);

        beginTransaction(em);
        try {
            Employee emp = new Employee();
            emp.setFirstName("Melvin");
            emp.setLastName("Malone");
            em.persist(emp);
            em.flush();
    
            Vector args = new Vector(2);
            args.addElement(String.class);
            args.addElement(String.class);
            Query ejbQuery = ((org.eclipse.persistence.jpa.JpaEntityManager)em).createDescriptorNamedQuery("findEmployees", Employee.class, args);
            
            List results = ejbQuery.setParameter("fName", "Melvin").setParameter("lName", "Malone").getResultList();
            
            assertTrue(results.size() == 1);
            emp = (Employee)results.get(0);
            assertTrue(emp.getFirstName().equals("Melvin"));
            assertTrue(emp.getLastName().equals("Malone"));
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        
        descriptor.getQueryManager().removeQuery("findEmployees");
    }

    // GF 2621
    public void testDoubleMerge(){
        EntityManager em = createEntityManager();
        
        Employee employee = new Employee();
        employee.setId(44);
        employee.setVersion(0);
        employee.setFirstName("Alfie");

        Employee employee2 = new Employee();
        employee2.setId(44);
        employee2.setVersion(0);
        employee2.setFirstName("Phillip");        
        
        try {
            beginTransaction(em);
            em.merge(employee);
            em.merge(employee2);
            em.flush();
        } catch (PersistenceException e){
            fail("A double merge of an object with the same key, caused two inserts instead of one.");
        } finally {
            rollbackTransaction(em);
        }
    }

    // gf 3032
    public void testPessimisticLockHintStartsTransaction(){
        Assert.assertFalse("Warning: DerbyPlatform does not currently support pessimistic locking",  ((Session)JUnitTestCase.getServerSession()).getPlatform().isDerby());
        Assert.assertFalse("Warning: PostgreSQLPlatform. does not currently support pessimistic locking",  ((Session)JUnitTestCase.getServerSession()).getPlatform().isPostgreSQL());
        EntityManagerImpl em = (EntityManagerImpl)createEntityManager();
        beginTransaction(em);
        Query query = em.createNamedQuery("findAllEmployeesByFirstName");
        query.setHint("eclipselink.pessimistic-lock", PessimisticLock.Lock);
        query.setParameter("firstname", "Sarah");
        List results = query.getResultList();
        assertTrue("The extended persistence context is not in a transaction after a pessmimistic lock query", em.getActivePersistenceContext(em.getTransaction()).getParent().isInTransaction());
        
        rollbackTransaction(em);
    }
    
    /**
     * Test that all of the classes in the advanced model were weaved as expected.
     */
    public void testWeaving() {
        // Only test if weaving was on, test runs without weaving must set this system property.
        if (System.getProperty("TEST_NO_WEAVING") == null) {
            internalTestWeaving(new Employee(), true, true);
            internalTestWeaving(new FormerEmployment(), true, false);
            internalTestWeaving(new Address(), true, false);
            internalTestWeaving(new PhoneNumber(), true, false);
            internalTestWeaving(new EmploymentPeriod(), true, false);
            internalTestWeaving(new Buyer(), false, false);  // field-locking
            internalTestWeaving(new GoldBuyer(), false, false);  // field-locking
            internalTestWeaving(new PlatinumBuyer(), false, false);  // field-locking
            internalTestWeaving(new Department(), false, false);  // eager 1-m
            internalTestWeaving(new Golfer(), true, false);
            internalTestWeaving(new GolferPK(), true, false);
            internalTestWeaving(new SmallProject(), true, false);
            internalTestWeaving(new LargeProject(), true, false);
            internalTestWeaving(new SuperLargeProject(), true, false);
            internalTestWeaving(new Man(), true, false);
            internalTestWeaving(new Woman(), true, false);
            internalTestWeaving(new Vegetable(), false, false);  // serialized
            internalTestWeaving(new VegetablePK(), false, false);
            internalTestWeaving(new WorldRank(), true, false);
            internalTestWeaving(new Equipment(), true, false);
            internalTestWeaving(new EquipmentCode(), true, false);
            internalTestWeaving(new PartnerLink(), true, false);
        }
    }
    
    /**
     * Test that the object was weaved.
     */
    public void internalTestWeaving(Object object, boolean changeTracking, boolean indirection) {
        if (!(object instanceof PersistenceWeaved)) {
            fail("Object not weaved:" + object);
        }
        if (indirection && (!(object instanceof PersistenceWeavedLazy))) {
            fail("Object not weaved for indirection:" + object);
        }
        if (changeTracking && (!(object instanceof ChangeTracker))) {
            fail("Object not weaved for ChangeTracker:" + object);
        }
        ClassDescriptor descriptor = getServerSession().getDescriptor(object);
        if (!descriptor.isAggregateDescriptor()) {
            if (changeTracking != descriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy()) {
                fail("Descriptor not set to use change tracking policy correctly:" + object);
            }
            if (!(object instanceof PersistenceEntity)) {
                fail("Object not weaved for PersistenceEntity:" + object);
            }
            if (!(object instanceof FetchGroupTracker)) {
                fail("Object not weaved for FetchGroupTracker:" + object);
            }
        }
    }
        
    // this test was failing after transaction ailitche_main_6333458_070821
    public void testManyToOnePersistCascadeOnFlush() {
        boolean pass = false;
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            String firstName = "testManyToOneContains";
            Address address = new Address();
            address.setCountry(firstName);
            Employee employee = new Employee();
            employee.setFirstName(firstName);
            em.persist(employee);
            
            employee.setAddress(address);
            em.flush();

            pass = em.contains(address);
        } finally {
           rollbackTransaction(em);
           closeEntityManager(em);
        }
        if(!pass) {
            fail("em.contains(address) returned false");
        }
    }
        
    // This test weaving works with over-writing methods in subclasses, and overloading methods.
    public void testOverwrittingAndOverLoadingMethods() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Address address = new Address();
            address.setCity("Ottawa");
            Employee employee = new Employee();
            employee.setAddress(address);
            LargeProject project = new LargeProject();
            project.setTeamLeader(employee);
            em.persist(employee);
            em.persist(project);
            commitTransaction(em);
            closeEntityManager(em);
            em = createEntityManager();
            beginTransaction(em);
            employee = em.find(Employee.class, employee.getId());
            project = em.find(LargeProject.class, project.getId());
            if ((employee.getAddress("Home") == null) || (!employee.getAddress("Home").getCity().equals("Ottawa"))) {
                fail("Get address did not work.");
            }
            employee.setAddress("Toronto");
            if (!employee.getAddress().getCity().equals("Toronto")) {
                fail("Set address did not work.");
            }
            if (project.getTeamLeader() != employee) {
                fail("Get team leader did not work, team is: " + project.getTeamLeader() + " but should be:" + employee);
            }
            em.remove(employee.getAddress());
            em.remove(employee);
            em.remove(project);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    // This tests that new objects referenced by new objects are found on commit.
    public void testDiscoverNewReferencedObject() {
        String firstName = "testDiscoverNewReferencedObject";
        
        // setup: create and persist Employee
        EntityManager em = createEntityManager();
        int employeeId = 0;
        beginTransaction(em);
        try {
            Employee employee = new Employee();
            employee.setFirstName(firstName);
            employee.setLastName("Employee");
            em.persist(employee);
            commitTransaction(em);
            employeeId = employee.getId();
        } finally {
            if(isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
        
        // test: add to the exsisting Employee a new Manager with new Phones
        em = createEntityManager();
        int managerId = 0;
        beginTransaction(em);
        try {
            Employee manager = new Employee();
            manager.setFirstName(firstName);
            manager.setLastName("Manager");
            
            PhoneNumber phoneNumber1 = new PhoneNumber("home", "613", "1111111");
            manager.addPhoneNumber(phoneNumber1);
            PhoneNumber phoneNumber2 = new PhoneNumber("work", "613", "2222222");
            manager.addPhoneNumber(phoneNumber2);
            
            Employee employee = em.find(Employee.class, employeeId);
            manager.addManagedEmployee(employee);
            commitTransaction(em);

            managerId = manager.getId();
        } finally {
            if(isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
        
        // verify: were all the new objects written to the data base?
        String errorMsg = "";
        em = createEntityManager();
        try {
            Employee manager = (Employee)em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.id = "+managerId).setHint("eclipselink.refresh", "true").getSingleResult();
            if(manager == null) {
                errorMsg = "Manager hasn't been written into the db";
            } else {
                if(manager.getPhoneNumbers().size() != 2) {
                    errorMsg = "Manager has a wrong number of Phones = "+manager.getPhoneNumbers().size()+"; should be 2";
                }
            }
        } finally {
            closeEntityManager(em);
        }
        
        // clean up: delete Manager - all other object will be cascade deleted.
        em = createEntityManager();
        beginTransaction(em);
        try {
            if(managerId != 0) {
                Employee manager = em.find(Employee.class, managerId);
                em.remove(manager);
            } else if(employeeId != 0) {
                // if Manager hasn't been created - delete Employee
                Employee employee = em.find(Employee.class, employeeId);
                em.remove(employee);
            }
        } finally {
            if(isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
        
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    // bug 6006423: BULK DELETE QUERY FOLLOWED BY A MERGE RETURNS DELETED OBJECT
    public void testBulkDeleteThenMerge() {
        String firstName = "testBulkDeleteThenMerge";

        // setup - create Employee
        EntityManager em = createEntityManager(); 
        beginTransaction(em);
        Employee emp = new Employee();
        emp.setFirstName(firstName);
        emp.setLastName("Original");
        em.persist(emp); 
        commitTransaction(em);
        closeEntityManager(em);

        int id = emp.getId();
        
        // test
        // delete the Employee using bulk delete
        em = createEntityManager(); 
        beginTransaction(em);
        em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate(); 
        commitTransaction(em);
        closeEntityManager(em);

        // then re-create and merge the Employee using the same pk
        em = createEntityManager(); 
        beginTransaction(em);
        emp = new Employee();
        emp.setId(id);
        emp.setFirstName(firstName);
        emp.setLastName("New");
        em.merge(emp); 
        try {
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
        
        // verify
        String errorMsg = "";
        em = createEntityManager(); 
        // is the right Employee in the cache?
        emp = (Employee)em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.id = " + id).getSingleResult();
        if(emp == null) {
            errorMsg = "Cache: Employee is not found; ";
        } else {
            if(!emp.getLastName().equals("New")) {
                errorMsg = "Cache: wrong lastName = "+emp.getLastName()+"; should be New; ";
            }
        }
        // is the right Employee in the db?
        emp = (Employee)em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.id = " + id).setHint("eclipselink.refresh", Boolean.TRUE).getSingleResult();
        if(emp == null) {
            errorMsg = errorMsg + "DB: Employee is not found";
        } else {
            if(!emp.getLastName().equals("New")) {
                errorMsg = "DB: wrong lastName = "+emp.getLastName()+"; should be New";
            }
            // clean up in case the employee is in the db
            beginTransaction(em);
            em.remove(emp);
            commitTransaction(em);
        }
        closeEntityManager(em);

        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testNativeSequences() {
        ServerSession ss = JUnitTestCase.getServerSession();
        boolean doesPlatformSupportIdentity = ss.getPlatform().supportsIdentity();
        boolean doesPlatformSupportSequenceObjects = ss.getPlatform().supportsSequenceObjects();
        String errorMsg = "";
        
        // SEQ_GEN_IDENTITY sequence defined by
        // @GeneratedValue(strategy=IDENTITY)
        boolean isIdentity = ss.getPlatform().getSequence("SEQ_GEN_IDENTITY").shouldAcquireValueAfterInsert();
        if(doesPlatformSupportIdentity != isIdentity) {
            errorMsg = "SEQ_GEN_IDENTITY: doesPlatformSupportIdentity = " + doesPlatformSupportIdentity +", but isIdentity = " + isIdentity +"; ";
        }

        // ADDRESS_SEQ sequence defined by
        // @GeneratedValue(generator="ADDRESS_SEQ")
        // @SequenceGenerator(name="ADDRESS_SEQ", allocationSize=25)
        boolean isSequenceObject = !ss.getPlatform().getSequence("ADDRESS_SEQ").shouldAcquireValueAfterInsert();
        if(doesPlatformSupportSequenceObjects != isSequenceObject) {
            errorMsg = errorMsg +"ADDRESS_SEQ: doesPlatformSupportSequenceObjects = " + doesPlatformSupportSequenceObjects +", but isSequenceObject = " + isSequenceObject;
        }
        
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    /**
     * Test that sequence numbers allocated but unused in the transaction
     * kept after transaction commits
     * in case SequencingCallback used (that happens if TableSequence is used without
     * using sequencing connection pool).
     */
    public void testSequencePreallocationUsingCallbackTest() {
        // setup
        ServerSession ss = ((EntityManagerFactoryImpl)getEntityManagerFactory()).getServerSession();
        // make sure the sequence has both preallocation and callback
        // (the latter means not using sequencing connection pool, 
        // acquiring values before insert and requiring transaction).
        if(ss.getSequencingControl().shouldUseSeparateConnection()) {
            fail("setup failure: the test requires serverSession.getSequencingControl().shouldUseSeparateConnection()==false");
        }
        String seqName = ss.getDescriptor(Employee.class).getSequenceNumberName();
        Sequence sequence = getServerSession().getLogin().getSequence(seqName);
        if(sequence.getPreallocationSize() < 2) {
            fail("setup failure: the test requires sequence preallocation size greater than 1");
        }
        if(sequence.shouldAcquireValueAfterInsert()) {
            fail("setup failure: the test requires sequence that acquires value before insert, like TableSequence");
        }
        if(!sequence.shouldUseTransaction()) {
            fail("setup failure: the test requires sequence that uses transaction, like TableSequence");
        }
        // clear all already allocated sequencing values for seqName
        getServerSession().getSequencingControl().initializePreallocated(seqName);
        
        // test
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp1 = new Employee();
        emp1.setFirstName("testSequencePreallocation");
        emp1.setLastName("1");
        em.persist(emp1);
        int assignedSequenceNumber = emp1.getId();
        commitTransaction(em);

        // verify
        em = createEntityManager();
        beginTransaction(em);
        Employee emp2 = new Employee();
        emp2.setFirstName("testSequencePreallocation");
        emp2.setLastName("2");
        em.persist(emp2);
        int nextSequenceNumber = emp2.getId();
        // only need nextSequenceNumber, no need to commit
        rollbackTransaction(em);
        
        // cleanup
        // remove the object that has been created in setup
        em = createEntityManager();
        beginTransaction(em);
        emp1 = em.find(Employee.class, assignedSequenceNumber);
        em.remove(emp1);
        commitTransaction(em);
        
        // report result
        if(assignedSequenceNumber + 1 != nextSequenceNumber) {
            fail("Transaction that assigned sequence number committed, assignedSequenceNumber = " + assignedSequenceNumber +", but nextSequenceNumber = "+ nextSequenceNumber +"("+Integer.toString(assignedSequenceNumber+1)+" was expected)");
        }
    }

    /**
     * Create a test employee.
     */
    protected Employee createEmployee(String name) {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee employee = new Employee();
        employee.setFirstName(name);
        em.persist(employee);
        commitTransaction(em);
        return employee;
    }
    
    /**
     * Test getReference() API.
     */
    public void testGetReference() {
        Employee employee = createEmployee("testGetReference");
        int id = employee.getId();
        int version = employee.getVersion();
        
        EntityManager em = createEntityManager();
        beginTransaction(em);
        employee = em.getReference(Employee.class, id);
        if (!employee.getFirstName().equals("testGetReference")) {
            fail("getReference returned the wrong object");
        }
        commitTransaction(em);
        if (employee.getVersion() != version) {
            fail("fetched object was updated");
        }
        
        clearCache();
        
        em = createEntityManager();
        beginTransaction(em);
        employee = em.getReference(Employee.class, id);
        if (employee instanceof FetchGroupTracker) {
            if (((FetchGroupTracker)employee)._persistence_isAttributeFetched("firstName")) {
                fail("getReference fetched object.");
            }
        }
        if (!employee.getFirstName().equals("testGetReference")) {
            fail("getReference returned the wrong object");
        }
        commitTransaction(em);
        if (employee.getVersion() != version) {
            fail("fetched object was updated");
        }
        
        em = createEntityManager();
        beginTransaction(em);
        employee = em.find(Employee.class, id);
        if (!employee.getFirstName().equals("testGetReference")) {
            fail("find returned the wrong object");
        }
        commitTransaction(em);
        
        List key = new ArrayList();
        key.add(id);
        em = createEntityManager();
        beginTransaction(em);
        employee = em.getReference(Employee.class, key);
        if (!employee.getFirstName().equals("testGetReference")) {
            fail("getReference returned the wrong object");
        }
        commitTransaction(em);
        
        em = createEntityManager();
        beginTransaction(em);
        employee = em.find(Employee.class, key);
        if (!employee.getFirstName().equals("testGetReference")) {
            fail("find returned the wrong object");
        }
        commitTransaction(em);
    }
    
    /**
     * Test getReference() with update.
     */
    public void testGetReferenceUpdate() {
        int id = createEmployee("testGetReference").getId();
                
        clearCache();
        
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee employee = em.getReference(Employee.class, id);
        employee.setFirstName("changed");
        commitTransaction(em);

        verifyObjectInCacheAndDatabase(employee);
    }
    
    /**
     * Test getReference() with bad id.
     */
    public void testBadGetReference() {
                
        clearCache();
        
        EntityManager em = createEntityManager();
        Exception caught = null;
        try {
            Employee employee = em.getReference(Employee.class, -123);
            employee.getFirstName();
        } catch (EntityNotFoundException exception) {
            caught = exception;
        }
        if (caught == null) {
            fail("getReference did not throw an error for a bad id");
        }

    }
    
    /**
     * Test getReference() used in update.
     */
    public void testGetReferenceUsedInUpdate() {
        Employee employee = createEmployee("testGetReference");
        int id = employee.getId();
        int version = employee.getVersion();
        
        clearCache();

        EntityManager em = createEntityManager();
        beginTransaction(em);
        employee = em.getReference(Employee.class, id);
        if (employee instanceof FetchGroupTracker) {
            if (((FetchGroupTracker)employee)._persistence_isAttributeFetched("firstName")) {
                fail("getReference fetched object.");
            }
        }
        Employee newEmployee = new Employee();
        newEmployee.setFirstName("new");
        newEmployee.setManager(employee);
        em.persist(newEmployee);
        commitTransaction(em);
        if (employee instanceof FetchGroupTracker) {
            if (((FetchGroupTracker)employee)._persistence_isAttributeFetched("firstName")) {
                fail("commit fetched object.");
            }
        }
        if (employee.getVersion() != version) {
            fail("un-fetched object was updated");
        }

        verifyObjectInCacheAndDatabase(newEmployee);
    }

    public void testClassInstanceConverter(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Address add = new Address();
        add.setCity("St. Louis");
        add.setType(new Bungalow());
        em.persist(add);
        commitTransaction(em);
        int assignedSequenceNumber = add.getId();
        
        em.clear();
        getServerSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        add = em.find(Address.class, assignedSequenceNumber);
        
        assertTrue("Did not correctly persist a mapping using a class-instance converter", (add.getType() instanceof Bungalow));

        beginTransaction(em);
        em.remove(add);
        commitTransaction(em);
    }

    /**
     * See bug# 210280: verify that the URL encoding for spaces and multibyte chars is handled properly in the EMSetup map lookup
     * UC1 - EM has no spaces or multi-byte chars in name or path
     * UC2 - EM has spaces hex(20) in EM name but not in path
     * UC3/4 are fixed by 210280 - the other UC tests are for regression
     * UC3 - EM has spaces in path but not in the EM name
     * UC4 - EM has spaces in path and EM name
     * UC5 - EM has multi-byte hex(C3A1) chars in EM name but not in path
     * Keep resource with spaces and multibyte chars separate
     * UC6 - EM has multi-byte chars in path but not EM name
     * UC7 - EM has multi-byte chars in path and EM name
     * UC8 - EM has spaces and multi-byte chars in EM name but not in path 
     * UC9 - EM has spaces and multi-byte chars in path and EM name
     */
    // UC2 - EM has spaces in EM name but not in path
    public void test210280EntityManagerFromPUwithSpaceInNameButNotInPath() {
		// This EM is defined in a persistence.xml that is off eclipselink-advanced-properties (no URL encoded chars in path) 
    	privateTest210280EntityManagerWithPossibleSpacesInPathOrName(//
    			"A JPAADVProperties pu with spaces in the name", //
    			"with a name containing spaces was not found.");
    }

    // UC3 - EM has spaces in path but not in the EM name 
    public void test210280EntityManagerFromPUwithSpaceInPathButNotInName() {
		// This EM is defined in a persistence.xml that is off [eclipselink-pu with spaces] (with URL encoded chars in path)    	
    	privateTest210280EntityManagerWithPossibleSpacesInPathOrName(//
    			"eclipselink-pu-with-spaces-in-the-path-but-not-the-name", //
    			"with a path containing spaces was not found.");
    }

    // UC4 - EM has spaces in the path and name
    public void test210280EntityManagerFromPUwithSpaceInNameAndPath() {
		// This EM is defined in a persistence.xml that is off [eclipselink-pu with spaces] (with URL encoded chars in path)    	
    	privateTest210280EntityManagerWithPossibleSpacesInPathOrName(//
    			"eclipselink-pu with spaces in the path and name", //
    			"with a path and name both containing spaces was not found.");
    }

    private void privateTest210280EntityManagerWithPossibleSpacesInPathOrName(String puName, String failureMessagePostScript) {
        EntityManager em = null;    	
    	try {
    		em = createEntityManager(puName);
        } catch (Exception e) {
        	Throwable cause = e.getCause();
        	e.printStackTrace();
        	fail("A Persistence Unit [" + puName + "] " + failureMessagePostScript);
        } finally {
        	if(null != em) {
        		closeEntityManager(em);
        	}
        }
    }
    
    public void testConnectionPolicy() {
        // setup
        String errorMsg = "";
        
        EntityManagerFactory emFactory;
        if (isOnServer()) {
            emFactory = null;
        } else {
            emFactory = getEntityManagerFactory();
        }
        
        HashMap properties = new HashMap();
        properties.put(EntityManagerProperties.JDBC_USER, "em_user");
        properties.put(EntityManagerProperties.JDBC_PASSWORD, "em_password");
        properties.put(EntityManagerProperties.JTA_DATASOURCE, "em_jta_datasource");
        properties.put(EntityManagerProperties.NON_JTA_DATASOURCE, "em_nonjta_datasource");
        properties.put(EntityManagerProperties.EXCLUSIVE_CONNECTION_MODE, ExclusiveConnectionMode.Always);

        // test
        EntityManager em;
        if (isOnServer()) {
            em = createEntityManager();
            ((EntityManagerImpl)em.getDelegate()).setProperties(properties);
        } else {
            em = emFactory.createEntityManager(properties);
        }

        // verify
        ClientSession clientSession;
        if (isOnServer()) {
            clientSession = (ClientSession)((EntityManagerImpl)em.getDelegate()).getActivePersistenceContext(null).getParent();
        } else {
            clientSession = (ClientSession)((EntityManagerImpl)em).getActivePersistenceContext(null).getParent();
        }
        if(!clientSession.isExclusiveIsolatedClientSession()) {
            errorMsg += "ExclusiveIsolatedClientSession was expected\n";
        }
        ConnectionPolicy policy = clientSession.getConnectionPolicy();
        if(policy.isPooled()) {
            errorMsg += "NOT pooled policy was expected\n";
        }
        String user = (String)policy.getLogin().getProperty("user");
        if(!user.equals("em_user")) {
            errorMsg += "em_user was expected\n";
        }
        String password = (String)policy.getLogin().getProperty("password");
        if(!password.equals("em_password")) {
            errorMsg += "em_password was expected\n";
        }
        if(! (((DatasourceLogin)policy.getLogin()).getConnector() instanceof JNDIConnector)) {
            errorMsg += "JNDIConnector was expected\n";
        } else {
            JNDIConnector jndiConnector = (JNDIConnector)((DatasourceLogin)policy.getLogin()).getConnector();
            String dataSourceName = jndiConnector.getName();
            if(dataSourceName == null) {
                errorMsg += "NON null dataSourceName was expected\n";
            } else {
                if(clientSession.getParent().getLogin().shouldUseExternalTransactionController()) {
                    if(dataSourceName.equals("em_nonjta_datasource")) {
                        errorMsg += "em_jta_datasource was expected\n";
                    }
                } else {
                    if(dataSourceName.equals("em_jta_datasource")) {
                        errorMsg += "em_nonjta_datasource was expected\n";
                    }
                }
            }
        }
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    /*
     * Added for bug 235340 - parameters in named query are not  transformed when IN is used.
     * Before the fix this used to throw exception.
     */
    public void testConverterIn() {
        EntityManager em = createEntityManager();
        List<Employee> emps = em.createQuery("SELECT e FROM Employee e WHERE e.gender IN (:GENDER1, :GENDER2)").
                        setParameter("GENDER1", Employee.Gender.Male).
                        setParameter("GENDER2", Employee.Gender.Female).
                        getResultList();
        em.close();
    }
}
