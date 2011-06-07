/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     27/07/2010 - 2.1.1 Sabine Heider 
 *          304650: fix left over entity data interfering with testSetRollbackOnly
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
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

import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.changesets.ChangeRecord;
import org.eclipse.persistence.sessions.changesets.ObjectChangeSet;
import org.eclipse.persistence.sessions.changesets.UnitOfWorkChangeSet;
import org.eclipse.persistence.sessions.server.ReadConnectionPool;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.tools.schemaframework.SequenceObjectDefinition;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.CopyGroup;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.CascadePolicy;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.PessimisticLock;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.internal.descriptors.PersistenceEntity;
import org.eclipse.persistence.internal.weaving.PersistenceWeaved;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedLazy;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.mappings.DatabaseMapping;

import org.eclipse.persistence.testing.framework.QuerySQLTracker;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.*;

/**
 * Test the EntityManager API using the advanced model.
 */
public class EntityManagerJUnitTestSuite extends JUnitTestCase {
        
    public EntityManagerJUnitTestSuite() {
        super();
    }
    
    public EntityManagerJUnitTestSuite(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("EntityManagerJUnitTestSuite (fieldaccess)");        
        suite.addTest(new EntityManagerJUnitTestSuite("testSetup"));
        
        List<String> tests = new ArrayList<String>();
        tests.add("testWeaving");
        tests.add("testClearEntityManagerWithoutPersistenceContext");
        tests.add("testUpdateAllProjects");
        tests.add("testUpdateUsingTempStorage");
        tests.add("testSequenceObjectDefinition");
        tests.add("testFindDeleteAllPersist");
        tests.add("testExtendedPersistenceContext");
        tests.add("testRemoveFlushFind");
        tests.add("testRemoveFlushPersistContains");
        tests.add("testTransactionRequired");
        tests.add("testSubString");
        tests.add("testFlushModeOnUpdateQuery");
        tests.add("testContainsRemoved");
        tests.add("testRefreshRemoved");
        tests.add("testRefreshNotManaged");
        tests.add("testDoubleMerge");
        tests.add("testDescriptorNamedQueryForMultipleQueries");
        tests.add("testDescriptorNamedQuery");
        tests.add("testClearEntityManagerWithoutPersistenceContextSimulateJTA");
        tests.add("testMultipleEntityManagerFactories");
        tests.add("testOneToManyDefaultJoinTableName");
        tests.add("testClosedEmShouldThrowException");
        tests.add("testRollbackOnlyOnException");
        tests.add("testUpdateAllProjectsWithNullTeamLeader");
        tests.add("testUpdateAllLargeProjectsWithNullTeamLeader");
        tests.add("testUpdateAllSmallProjectsWithNullTeamLeader");
        tests.add("testUpdateAllProjectsWithName");
        tests.add("testUpdateAllLargeProjectsWithName");
        tests.add("testUpdateAllSmallProjectsWithName");
        tests.add("testUpdateAllLargeProjects");
        tests.add("testUpdateAllSmallProjects");
        tests.add("testUpdateUsingTempStorageWithParameter");
        tests.add("testDeleteAllLargeProjectsWithNullTeamLeader");
        tests.add("testDeleteAllSmallProjectsWithNullTeamLeader");
        tests.add("testDeleteAllProjectsWithNullTeamLeader");
        tests.add("testDeleteAllPhonesWithNullOwner");
        tests.add("testSetFieldForPropertyAccessWithNewEM");
        tests.add("testSetFieldForPropertyAccessWithRefresh");
        tests.add("testSetFieldForPropertyAccess");
        tests.add("testInitializeFieldForPropertyAccess");
        tests.add("testCascadePersistToNonEntitySubclass");
        tests.add("testCascadeMergeManaged");
        tests.add("testCascadeMergeDetached");
        tests.add("testPrimaryKeyUpdatePKFK");
        tests.add("testPrimaryKeyUpdateSameValue");
        tests.add("testPrimaryKeyUpdate");
        tests.add("testRemoveNull");
        tests.add("testContainsNull");
        tests.add("testPersistNull");
        tests.add("testMergeNull");
        tests.add("testMergeRemovedObject");
        tests.add("testMergeDetachedObject");
        tests.add("testSerializedLazy");
        tests.add("testCloneable");
        tests.add("testLeftJoinOneToOneQuery");
        tests.add("testNullifyAddressIn");
        tests.add("testQueryOnClosedEM");
        tests.add("testIncorrectBatchQueryHint");
        tests.add("testFetchQueryHint");
        tests.add("testBatchQueryHint");
        tests.add("testQueryHints");
        tests.add("testQueryTimeOut");
        tests.add("testParallelMultipleFactories");
        tests.add("testMultipleFactories");
        tests.add("testPersistenceProperties");
        tests.add("testBeginTransactionCloseCommitTransaction");
        tests.add("testBeginTransactionClose");
        tests.add("testClose");
        tests.add("testPersistOnNonEntity");
        tests.add("testWRITELock");
        tests.add("testReadTransactionIsolation_OriginalInCache_UpdateAll_Refresh_Flush");
        tests.add("testReadTransactionIsolation_OriginalInCache_UpdateAll_Refresh");
        tests.add("testReadTransactionIsolation_OriginalInCache_UpdateAll_Flush");
        tests.add("testReadTransactionIsolation_OriginalInCache_UpdateAll");
        tests.add("testReadTransactionIsolation_OriginalInCache_CustomUpdate_Refresh_Flush");
        tests.add("testReadTransactionIsolation_OriginalInCache_CustomUpdate_Refresh");
        tests.add("testReadTransactionIsolation_OriginalInCache_CustomUpdate_Flush");
        tests.add("testReadTransactionIsolation_OriginalInCache_CustomUpdate");
        tests.add("testReadTransactionIsolation_UpdateAll_Refresh_Flush");
        tests.add("testReadTransactionIsolation_UpdateAll_Refresh");
        tests.add("testReadTransactionIsolation_UpdateAll_Flush");
        tests.add("testReadTransactionIsolation_UpdateAll");
        tests.add("testReadTransactionIsolation_CustomUpdate_Refresh_Flush");
        tests.add("testReadTransactionIsolation_CustomUpdate_Refresh");
        tests.add("testReadTransactionIsolation_CustomUpdate_Flush");
        tests.add("testReadTransactionIsolation_CustomUpdate");
        tests.add("testClearInTransaction");
        tests.add("testClearWithFlush");
        tests.add("testClear");
        tests.add("testCheckVersionOnMerge");
        tests.add("testFindWithNullPk");
        tests.add("testFindWithWrongTypePk");
        tests.add("testPersistManagedNoException");
        tests.add("testPersistManagedException");
        tests.add("testPersistRemoved");
        tests.add("testREADLock");
        tests.add("testIgnoreRemovedObjectsOnDatabaseSync");
        tests.add("testIdentityOutsideTransaction");
        tests.add("testIdentityInsideTransaction");
        tests.add("testDatabaseSyncNewObject");
        tests.add("testSetRollbackOnly");
        tests.add("testFlushModeEmCommitQueryAuto");
        tests.add("testFlushModeEmCommit");
        tests.add("testFlushModeEmCommitQueryCommit");
        tests.add("testFlushModeEmAutoQueryAuto");
        tests.add("testFlushModeEmAuto");
        tests.add("testFlushModeEmAutoQueryCommit");
        tests.add("testCacheUsage");
        tests.add("testSuperclassFieldInSubclass");
        tests.add("testCopyingAddress");
        tests.add("testSequencePreallocationUsingCallbackTest");
        tests.add("updateAttributeWithObjectTest");
        tests.add("testDeleteEmployee");
        tests.add("testDeleteMan");
        tests.add("testNullDouble");
        tests.add("testChangeRecordKeepOldValue_Simple");
        tests.add("testChangeRecordKeepOldValue_TwoStep");
        tests.add("testSetNewAggregate");
        tests.add("testSetNewNestedAggregate");
        tests.add("testObjectReferencedInBothEmAndSharedCache_AggregateObjectMapping");
        tests.add("testObjectReferencedInBothEmAndSharedCache_ObjectReferenceMappingVH");
        tests.add("testCharFieldDefaultNullValue");
        Collections.sort(tests);
        for (String test : tests) {
            suite.addTest(new EntityManagerJUnitTestSuite(test));
        }

        return suite;
    }
    
    @Override
    public String getPersistenceUnitName() {
        return "fieldaccess";
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(getDatabaseSession());

        // Force uppercase for Postgres.
        if (getDatabaseSession().getPlatform().isPostgreSQL()) {
            getDatabaseSession().getLogin().setShouldForceFieldNamesToUpperCase(true);
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
            
            if (getDatabaseSession().getPlatform().isSymfoware()){
                // Symfoware does not support delete all on multi table entities
                em.createNativeQuery("DELETE FROM CMP3_FA_EMPLOYEE WHERE F_NAME = '"+firstName+"'").executeUpdate();
            } else {
            // delete the Employee from the db
                em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
            }


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
        Object obj = getDatabaseSession().getIdentityMapAccessor().getFromIdentityMap(result.get(0));
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
            Query q = em.createQuery("SELECT e FROM Employee e WHERE e.firstName = '"+firstName+"'");
            for (Object oldData : q.getResultList()) {
                em.remove(oldData);
            }
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw ex;
        }
        clearCache();
        
        FlushModeType emFlushModeOriginal = em.getFlushMode();
        Employee emp = new Employee();
        emp.setFirstName(firstName);
        boolean flushed = true;
        Employee result = null;
        try {
            beginTransaction(em);            
            Query query = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.firstName like '"+firstName+"'");
            if(queryFlushMode != null) {
                query.setFlushMode(queryFlushMode);
            }
            emFlushModeOriginal = em.getFlushMode();
            em.setFlushMode(emFlushMode);
            em.persist(emp);
            result = (Employee) query.getSingleResult();
            result.toString();
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
                Query updateQuery = null;
                if (getDatabaseSession().getPlatform().isSymfoware()){
                    updateQuery = em.createNativeQuery("UPDATE CMP3_FA_EMPLOYEE SET VERSION = (VERSION + 1) WHERE F_NAME LIKE '" + firstName + "' AND EMP_ID in (SELECT EMP_ID FROM CMP3_FA_SALARY)");
                } else {
                    updateQuery = em.createQuery("UPDATE Employee e set e.salary = 100 where e.firstName like '" + firstName + "'");
                }
                updateQuery.setFlushMode(FlushModeType.AUTO);
                em.persist(emp);
                updateQuery.executeUpdate();
                if (getDatabaseSession().getPlatform().isSymfoware()){
                    updateQuery = em.createNativeQuery("UPDATE CMP3_FA_SALARY SET SALARY = 100 WHERE EMP_ID IN (SELECT EMP_ID FROM CMP3_FA_EMPLOYEE WHERE F_NAME LIKE '" + firstName + "')");
                    updateQuery.setFlushMode(FlushModeType.AUTO);
                    updateQuery.executeUpdate();
                }
                Employee result = (Employee) readQuery.getSingleResult();
                result.toString();
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
        Employee emp = null;
        Employee emp2 = null;
        beginTransaction(em);
        try{
            emp = new Employee();
            emp.setFirstName("Bob");
            emp.setLastName("Fisher");
            em.persist(emp);
            emp2 = new Employee();
            emp2.setFirstName("Anthony");
            emp2.setLastName("Walace");
            em.persist(emp2);
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
        List result = em.createQuery("SELECT e FROM Employee e where e.id = " + emp.getId() + " or e.id = " + emp2.getId()).getResultList();
        emp = (Employee)result.get(0);
        emp.toString();
        emp2 = (Employee)result.get(1);
        String newName = ""+System.currentTimeMillis();
        emp2.setFirstName(newName);
        em.flush();
        emp2.setLastName("Whatever");
        emp2.setVersion(0);
        try{
            em.flush();
        }catch (Exception ex){
            /*
			If JTA case after exception has occured the connection is immediately closed. Attempt to perform read causes a new connection to be acquired. Because the unitOfWork is no longer in transaction the connection is acquired by ServerSession. In case the ServerSession's read connection pool is JTA-managed the acquired connection that attempt fails on some app. servers (WLS) while others (GlassFish) return connection that correspond to no transaction at all (that would cause the old data to be read and the test to fail). Alternatively, if  a non-JTA-managed connection pool used for reading then the newly acquired connection is always retuns the old object (and the test would fail, again).  Note that in case of internal connection pool the connection is still kept after the exception is thrown and therefore the read returns the new data (the test passes).
			*/
			if (!isOnServer()){
				em.clear(); //prevent the flush again
				try {
					String eName = (String)em.createQuery("SELECT e.firstName FROM Employee e where e.id = " + emp2.getId()).getSingleResult();
					assertTrue("Failed to keep txn open for set RollbackOnly", eName.equals(newName));
				} catch (Exception ignore) {}
			}
        }
        try {
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
                if (ex.getCause() instanceof RollbackException) {
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
        if (getDatabaseSession().getPlatform().isSymfoware()) {
            getDatabaseSession().logMessage("Test testSubString skipped for this platform, "
                    + "Symfoware doesn't allow dynamic parameter as first argument of SUBSTRING.");
            return;
        }

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
        }catch (RuntimeException ex){
            rollbackTransaction(em);
            if (ex instanceof IllegalStateException)
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
        if (getServerSession("fieldaccess").getPlatform().isSymfoware()) {
            getServerSession("fieldaccess").logMessage("Test testIdentityInsideTransaction skipped for this platform, "
            + "The CascadeOnDelete doesn't work on a relation where CascadeType.Remove or CascadeType.All is specified on Symfoware Platform.");
            return;
        }
        EntityManager em = createEntityManager("fieldaccess");
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
        if (getServerSession("fieldaccess").getPlatform().isSymfoware()) {
            getServerSession("fieldaccess").logMessage("Test testIdentityOutsideTransaction skipped for this platform, "
            + "The CascadeOnDelete doesn't work on a relation where CascadeType.Remove or CascadeType.All is specified on Symfoware Platform.");
            return;
        }
        EntityManager em = createEntityManager("fieldaccess");
        
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
                empQuery = em.createQuery("Select e FROM Employee e where e.lastName like 'Dow%'");
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
    
    public void testREADLock() {
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
            } catch (RuntimeException exception) {
                if (exception instanceof OptimisticLockException){
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
        String firstName = "testPersistRemoved";
        Employee emp = new Employee();
        emp.setFirstName(firstName);

        // make sure no Employee with the specified firstName exists.
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            Query q = em.createQuery("SELECT e FROM Employee e WHERE e.firstName = '"+firstName+"'");
            for (Object oldData : q.getResultList()) {
                em.remove(oldData);
            }
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
                //makes debugging SQL logs a bit easier
                emp.setSalary(i*1000);
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
                    Query q = em.createQuery("SELECT e FROM Employee e WHERE e.firstName = '"+firstName+"'");
                    for (Object oldData : q.getResultList()) {
                        em.remove(oldData);
                    }
                    commitTransaction(em);
                }catch (RuntimeException ex){
                    if (isTransactionActive(em)) {
                        rollbackTransaction(em);
                    }
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
        emp.setFirstName("PersistManagedException");
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
        //bug240061: cannot operate on a closed/rolledback transaction in JBOSS - JBOSS's proxy throws away the old one and will
        // attempt to get a new EM.  An exception is thrown when this new EM tries to register with the transaction
        //emp = em.find(Employee.class, id);
        //em.remove(emp);
        rollbackTransaction(em);
        assertTrue("EntityExistsException was not thrown for an existing Employee.", caughtException);
    }
    
    public void testPersistManagedNoException(){       
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp = new Employee();
        emp.setFirstName("testPersistManagedNoException");
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
            Query q = em.createQuery("SELECT e FROM Employee e WHERE e.firstName = '"+firstName+"'");
            for (Object oldData : q.getResultList()) {
                em.remove(oldData);
            }
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
            emp = em.find(Employee.class, emp.getId());
            em.remove(emp); 
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
            Query q = em.createQuery("SELECT e FROM Employee e WHERE e.firstName = '"+firstName+"'");
            for (Object oldData : q.getResultList()) {
                em.remove(oldData);
            }
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
            Query q = em.createQuery("SELECT e FROM Employee e WHERE e.firstName = '"+firstName+"'");
            for (Object oldData : q.getResultList()) {
                em.remove(oldData);
            }
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
        try {
            Employee emp = new Employee();
            emp.setFirstName("Douglas");
            emp.setLastName("McRae");
            em.persist(emp);
            commitTransaction(em);
        } catch (RuntimeException ex){
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
        boolean cleared, updated, reset = false;
        try{
            Query query = em.createQuery("Select e FROM Employee e where e.firstName is not null");
            emp = (Employee)query.getResultList().get(0);
            originalName = emp.getFirstName();
            emp.setFirstName("Bobster");
            em.flush();
            em.clear();
            //this test is testing the cache not the database
            commitTransaction(em);
            cleared = !em.contains(emp);
            emp = em.find(Employee.class, emp.getId());
            updated = emp.getFirstName().equals("Bobster");
            closeEntityManager(em);
        } catch (RuntimeException exception) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw exception;
        } finally {
            //now clean up
            em = createEntityManager();
            beginTransaction(em);
            emp = em.find(Employee.class, emp.getId());
            emp.setFirstName(originalName);
            commitTransaction(em);
            emp = em.find(Employee.class, emp.getId());
            reset = emp.getFirstName().equals(originalName);
            closeEntityManager(em);
        }
        assertTrue("EntityManager not properly cleared", cleared);
        assertTrue("flushed data not merged", updated);
        assertTrue("unable to reset", reset);
    }
    
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
            Query q = em.createQuery("SELECT e FROM Employee e WHERE e.firstName = '"+firstName+"'");
            for (Object oldData : q.getResultList()) {
                em.remove(oldData);
            }
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
        if (shouldUpdateAll && getDatabaseSession().getPlatform().isSymfoware()) {
            getDatabaseSession().logMessage("A testReadTransactionIsolation test skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }

        //setup
        String firstName = "testReadTransactionIsolation";
        
        // make sure no Employee with the specified firstName exists.
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            Query q = em.createQuery("SELECT e FROM Employee e WHERE e.firstName = '"+firstName+"'");
            for (Object oldData : q.getResultList()) {
                em.remove(oldData);
            }
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
                nUpdated = em.createNativeQuery("UPDATE CMP3_FA_EMPLOYEE SET L_NAME = '" + lastNameNew + "', VERSION = VERSION + 1 WHERE F_NAME LIKE '" + firstName + "'").setFlushMode(FlushModeType.AUTO).executeUpdate();
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
            } else {
                throw (RuntimeException)ex;
            }
        }
        
        Employee employeeFoundAfterTransaction = em.find(Employee.class, employeeUOW.getId());
        assertTrue("employeeFoundAfterTransaction().getLastName()=="+ employeeFoundAfterTransaction.getLastName() +"; " + lastNameNew + " was expected", employeeFoundAfterTransaction.getLastName().equals(lastNameNew));
        assertTrue("employeeFoundAfterTransaction().getSalary()=="+ employeeFoundAfterTransaction.getSalary() +"; " + salaryNew + " was expected", employeeFoundAfterTransaction.getSalary() == salaryNew);
    
        // clean up
        beginTransaction(em);
        try{
            Query q = em.createQuery("SELECT e FROM Employee e WHERE e.firstName = '"+firstName+"'");
            for (Object oldData : q.getResultList()) {
                em.remove(oldData);
            }
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
        if (getDatabaseSession().getPlatform().isSymfoware()) {
            getDatabaseSession().logMessage("Test testFindDeleteAllPersist skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
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

        // make sure no Employee with the specified firstName exists.
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
            empWithAddressFound.toString();
            Employee empWithoutAddressFound = em.find(Employee.class, empWithoutAddress.getId());
            empWithoutAddressFound.toString();
            int nDeleted = em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"' and e.address IS NULL").executeUpdate();
            assertTrue(nDeleted > 0);
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        // we can no longer rely on the query above to clear the Employee from the persistence context.
        // Clearing the context to allow us to proceed.
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
        em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
        commitTransaction(em);
    }
    
    public void testWRITELock() {
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
    
    public void testPersistOnNonEntity() {
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
        if (!em.isOpen()) {
            fail("Created EntityManager is not open");
        }
        closeEntityManager(em);
        if (em.isOpen()) {
            fail("Closed EntityManager is still open");
        }
    }

    public void testBeginTransactionClose() throws Throwable {
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
        } catch (Throwable exception) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            if (em.isOpen()) {
                closeEntityManager(em);
            }
            throw exception;
        }

        rollbackTransaction(em);
        if(em.isOpen()) {
            fail("Closed EntityManager is still open after transaction rollback");
        }
    }

    public void testBeginTransactionCloseCommitTransaction() throws Throwable {
        // EntityManager is always open in server.
        if (isOnServer()) {
            return;
        }
        String firstName = "testBeginTrCloseCommitTr";
        EntityManager em = createEntityManager();

        // make sure there is no employees with this firstName
        beginTransaction(em);
        Query q = em.createQuery("SELECT e FROM Employee e WHERE e.firstName = '"+firstName+"'");
        for (Object oldData : q.getResultList()) {
            em.remove(oldData);
        }
        commitTransaction(em);
        
        // create a new Employee
        Employee employee = new Employee();
        employee.setFirstName(firstName);        
        
        // persist the new Employee and close the entity manager
        beginTransaction(em);
        try {
            em.persist(employee);
            closeEntityManager(em);
            if(em.isOpen()) {
                fail("Closed EntityManager is still open before transaction complete");
            }
        } catch (Throwable exception) {
            rollbackTransaction(em);
            if (em.isOpen()) {
                closeEntityManager(em);
            }
            throw exception;
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
            persistedEmployee.toString();

            // clean up
            beginTransaction(em);
            em.remove(persistedEmployee);
            commitTransaction(em);
        } catch (RuntimeException runtimeException) {
            exception = runtimeException;
        }

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
        ServerSession ss = ((org.eclipse.persistence.internal.jpa.EntityManagerImpl)em.getDelegate()).getServerSession();
        
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
        getEntityManagerFactory("fieldaccess");
        closeEntityManagerFactory();
        boolean isOpen = getEntityManagerFactory("fieldaccess").isOpen();
        if(!isOpen) {
            fail("Close factory 1; open factory 2 - it's not open");
        } else {
            // Get entity manager just to login back the session, then close em
            getEntityManagerFactory("fieldaccess").createEntityManager().close();
        }
    }
    
    public void testParallelMultipleFactories() {
        if (isOnServer()) {
            // Cannot connect locally on server.
            return;
        }
        EntityManagerFactory factory1 =  Persistence.createEntityManagerFactory("fieldaccess", JUnitTestCaseHelper.getDatabaseProperties());
        factory1.createEntityManager();
        EntityManagerFactory factory2 =  Persistence.createEntityManagerFactory("fieldaccess", JUnitTestCaseHelper.getDatabaseProperties());
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
        EntityManagerFactory factory3 =  Persistence.createEntityManagerFactory("fieldaccess", JUnitTestCaseHelper.getDatabaseProperties());
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
        EntityManager em = getEntityManagerFactory("fieldaccess").createEntityManager();
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

    public void testQueryTimeOut() {
        if (getDatabaseSession().getPlatform().isPostgreSQL()) {
            warning("PostgreSQL does not support query timeout.");
            return;
        }
        EntityManager em = getEntityManagerFactory("fieldaccess").createEntityManager();
        Query query = em.createQuery("SELECT d FROM Department d");
        ObjectLevelReadQuery olrQuery = (ObjectLevelReadQuery)((EJBQueryImpl)query).getDatabaseQuery();

        //testing for query timeout specified in persistence.xml
        assertTrue("Timeout overriden or not set in persistence.xml",olrQuery.getQueryTimeout() == 100);
        query.setHint(QueryHints.JDBC_TIMEOUT, 500);
        olrQuery = (ObjectLevelReadQuery)((EJBQueryImpl)query).getDatabaseQuery();
        assertTrue( olrQuery.getQueryTimeout() == 500);

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

        JpaQuery query = (JpaQuery)getEntityManagerFactory("fieldaccess").createEntityManager().createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
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
     * This test ensures that the toplink.fetch query hint works.
     * It tests two things. 
     * 
     * 1. That the joined attribute is properly added to the query
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

        JpaQuery query = (JpaQuery)getEntityManagerFactory("fieldaccess").createEntityManager().createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
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
            query.getResultList();
        } catch (QueryException exc){
            exception = exc;
        }
        assertNotNull("No exception was thrown on an incorrect BATCH query hint.", exception);
        assertTrue("Incorrect Exception thrown", exception.getErrorCode() == QueryException.QUERY_HINT_DID_NOT_CONTAIN_ENOUGH_TOKENS);
        exception = null;
        try{
            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
            query.setHint(QueryHints.BATCH, "e.abcdef");
            query.getResultList();
        } catch (QueryException exc){
            exception = exc;
        }
        assertNotNull("No exception was thrown on an incorrect BATCH query hint.", exception);
        assertTrue("Incorrect Exception thrown", exception.getErrorCode() == QueryException.QUERY_HINT_NAVIGATED_NON_EXISTANT_RELATIONSHIP);

        exception = null;
        try{
            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
            query.setHint(QueryHints.BATCH, "e.firstName");
            query.getResultList();
        } catch (QueryException exc){
            exception = exc;
        }
        assertNotNull("No exception was thrown when an incorrect relationship was navigated in a BATCH query hint.", exception);
        assertTrue("Incorrect Exception thrown", exception.getErrorCode() == QueryException.QUERY_HINT_NAVIGATED_ILLEGAL_RELATIONSHIP);

        exception = null;
        try{
            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
            query.setHint(QueryHints.FETCH, "e");
            query.getResultList();
        } catch (QueryException exc){
            exception = exc;
        }
        assertNotNull("No exception was thrown on an incorrect FETCH query hint.", exception);
        assertTrue("Incorrect Exception thrown", exception.getErrorCode() == QueryException.QUERY_HINT_DID_NOT_CONTAIN_ENOUGH_TOKENS);

        exception = null;
        try{
            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
            query.setHint(QueryHints.FETCH, "e.abcdef");
            query.getResultList();
        } catch (QueryException exc){
            exception = exc;
        }
        assertNotNull("No exception was thrown on an incorrect FETCH query hint.", exception);
        assertTrue("Incorrect Exception thrown", exception.getErrorCode() == QueryException.QUERY_HINT_NAVIGATED_NON_EXISTANT_RELATIONSHIP);

        exception = null;
        try{
            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
            query.setHint(QueryHints.FETCH, "e.firstName");
            query.getResultList();
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
        if (getDatabaseSession().getPlatform().isSymfoware()) {
            getDatabaseSession().logMessage("Test testNullifyAddressIn skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
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
        results.toString();
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
        if (isWeavingEnabled("fieldaccess")) {
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
        ServerSession ss = (ServerSession)getDatabaseSession();
        if(!ss.getLogin().getPlatform().supportsSequenceObjects() || isOnServer()) {
            // platform that supports sequence objects is required for this test
            // this test not work on server since the bug: 262251
            closeEntityManager(em);
            return;
        }
        String seqName = "testFASeqObjectsDefinition";
        try {
            // first param is preallocationSize, second is startValue
            // both should be positive
            internalTestSequenceObjectDefinition(10, 1, seqName, em, ss);
            internalTestSequenceObjectDefinition(10, 5, seqName + "1", em, ss);
            internalTestSequenceObjectDefinition(10, 15, seqName + "2", em, ss);
        } finally {
            closeEntityManager(em);
        }
    }

    protected void internalTestSequenceObjectDefinition(int preallocationSize, int startValue, String seqName, EntityManager em, ServerSession ss) {
        NativeSequence sequence = new NativeSequence(seqName, preallocationSize, startValue, false);
        sequence.onConnect(ss.getPlatform());
        SequenceObjectDefinition def = new SequenceObjectDefinition(sequence);
        try {
            // create sequence
            String createStr = def.buildCreationWriter(ss, new StringWriter()).toString();
            beginTransaction(em);
            em.createNativeQuery(createStr).executeUpdate();
            commitTransaction(em);

            // sequence value preallocated
            Vector seqValues = sequence.getGeneratedVector(null, ss);
            int firstSequenceValue = ((Number)seqValues.elementAt(0)).intValue();
            if(firstSequenceValue != startValue) {
                fail(seqName + " sequence with preallocationSize = "+preallocationSize+" and startValue = " + startValue + " produced wrong firstSequenceValue =" + firstSequenceValue);
            }
        } finally {
            sequence.onDisconnect(ss.getPlatform());
            // Symfoware doesn't allow drop while connections that performed
            // CREATE and DML on the sequence are still open.
            if (getDatabaseSession().getPlatform().isSymfoware()) return;
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
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
        Query query = em.createNamedQuery("findAllSQLDepartments");
        Collection departments = query.getResultList();
        
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
        
        // Temporarily changed until bug 264585 is fixed
        // the try/catch should be removed when the bug is fixed
        try{
            commitTransaction(em);
            
            em.createNamedQuery("findAllSQLDepartments").getResultList();
        } catch (RuntimeException e){
            getDatabaseSession().log(new SessionLogEntry(getDatabaseSession(), SessionLog.WARNING, SessionLog.TRANSACTION, e));
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
            // Remove an wrapping exceptions such as rollback, runtime, etc.
            while (persistenceException != null && !(persistenceException instanceof ValidationException)) {
                // In the server this is always a rollback exception, need to get nested exception.
                persistenceException = persistenceException.getCause();
            }
            if (persistenceException instanceof ValidationException) {
                ValidationException ve = (ValidationException) persistenceException;
                if (ve.getErrorCode() == ValidationException.PRIMARY_KEY_UPDATE_DISALLOWED) {
                    return;
                } else {
                    AssertionFailedError failure = new AssertionFailedError("Wrong error code for ValidationException: " + ve.getErrorCode());
                    failure.initCause(ve);
                    throw failure;
                }
            } else {
                AssertionFailedError failure = new AssertionFailedError("ValiationException expected, thrown: " + exception);
                failure.initCause(exception);
                throw failure;
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
        phone = em.merge(phone);
        emp2 = em.merge(emp2);
        phone.setOwner(emp2);
        
        try {
            commitTransaction(em);
        } catch (Exception exception) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            Throwable persistenceException = exception;
            // Remove an wrapping exceptions such as rollback, runtime, etc.
            while (persistenceException != null && !(persistenceException instanceof ValidationException)) {
                // In the server this is always a rollback exception, need to get nested exception.
                persistenceException = persistenceException.getCause();
            }
            if (persistenceException instanceof ValidationException) {
                ValidationException ve = (ValidationException) persistenceException;
                if (ve.getErrorCode() == ValidationException.PRIMARY_KEY_UPDATE_DISALLOWED) {
                    return;
                } else {
                    AssertionFailedError failure = new AssertionFailedError("Wrong error code for ValidationException: " + ve.getErrorCode());
                    failure.initCause(ve);
                    throw failure;
                }
            } else {
                AssertionFailedError failure = new AssertionFailedError("ValiationException expected, thrown: " + exception);
                failure.initCause(exception);
                throw failure;
            }
        } finally {
            closeEntityManager(em);
        }
        fail("No exception thrown when primary key update attempted.");        
    }
    
    // Test cascade merge on a detached entity
    public void testCascadeMergeDetached() throws Throwable {
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
        } catch (Throwable exception){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw exception;
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
        } catch (Throwable exception){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw exception;
        }
        closeEntityManager(em);
    }

    // Test cascade merge on a managed entity
    // Test for GF#1139 - Cascade doesn't work when merging managed entity
    public void testCascadeMergeManaged() throws Throwable {
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
        } catch (Throwable exception){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw exception;
        }
        closeEntityManager(em);
    }

    //Glassfish bug 1021 - allow cascading persist operation to non-entities
    public void testCascadePersistToNonEntitySubclass() {
        EntityManager em = createEntityManager();
        InheritancePolicy ip = getDatabaseSession().getDescriptor(Project.class).getInheritancePolicy();
        boolean describesNonPersistentSubclasses = ip.getDescribesNonPersistentSubclasses();
        ip.setDescribesNonPersistentSubclasses(true);

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
        	ip.setDescribesNonPersistentSubclasses(describesNonPersistentSubclasses);
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
    public void testInitializeFieldForPropertyAccess() {
        Employee employee = new Employee();
        employee.setFirstName("Andy");
        employee.setLastName("Dufresne");
        Address address = new Address();
        address.setCity("Shawshank");
        employee.setAddressField(address);
        
        EntityManager em = createEntityManager();
        beginTransaction(em);
        em.persist(employee);
        try {
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
            employee.setAddress(null);
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
    public void testSetFieldForPropertyAccess() {
        EntityManager em = createEntityManager();
        
        Employee employee = new Employee();
        employee.setFirstName("Andy");
        employee.setLastName("Dufresne");
        Address address = new Address();
        address.setCity("Shawshank");
        employee.setAddress(address);
        
        beginTransaction(em);
        em.persist(employee);
        commitTransaction(em);
        int id = employee.getId();
        int addressId = address.getId();
        
        beginTransaction(em);
        employee = em.find(Employee.class, new Integer(id));
        employee.getAddress();

        address = new Address();
        address.setCity("Metropolis");
        employee.setAddressField(address);
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
        try {
            employee = em.find(Employee.class, new Integer(id));
            address = employee.getAddress();
    
            assertTrue("The address was not persisted.", employee.getAddress() != null);
            assertTrue("The address was not correctly persisted.", employee.getAddress().getCity().equals("Metropolis"));
        } finally {    
            Address initialAddress = em.find(Address.class, new Integer(addressId));
            employee.setAddress(null);
            employee.setManager((Employee)null);
            em.remove(address);
            em.remove(employee);
            em.remove(initialAddress);
            commitTransaction(em);
        }
    }

    /**
     * Bug 801
     * Test to ensure when property access is used and the underlying variable is changed the change
     * is correctly reflected in the database
     * 
     * In this test we test making the change after the object is refreshed
     */
    public void testSetFieldForPropertyAccessWithRefresh() {
        EntityManager em = createEntityManager();
        
        Employee employee = new Employee();
        employee.setFirstName("Andy");
        employee.setLastName("Dufresne");
        Address address = new Address();
        address.setCity("Shawshank");
        employee.setAddress(address);
        
        beginTransaction(em);
        em.persist(employee);
        commitTransaction(em);
        int id = employee.getId();
        int addressId = address.getId();
        
        beginTransaction(em);
        try {
            employee = em.getReference(Employee.class, employee.getId());
            em.refresh(employee);
            employee.getAddress();
    
            address = new Address();
            address.setCity("Metropolis");
            employee.setAddressField(address);
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
        try {
            employee = em.find(Employee.class, new Integer(id));
            address = employee.getAddress();
    
            assertTrue("The address was not persisted.", employee.getAddress() != null);
            assertTrue("The address was not correctly persisted.", employee.getAddress().getCity().equals("Metropolis"));
        } finally {
            Address initialAddress = em.find(Address.class, new Integer(addressId));
            employee.setAddress(null);
            employee.setManager((Employee)null);
            em.remove(address);
            em.remove(employee);
            em.remove(initialAddress);
            commitTransaction(em);
        }
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
        Address address = new Address();
        address.setCity("Shawshank");
        employee.setAddress(address);
        
        beginTransaction(em);
        em.persist(employee);
        commitTransaction(em);
        int id = employee.getId();
        int addressId = address.getId();
        
        em = createEntityManager();
        
        beginTransaction(em);
        employee = em.find(Employee.class, new Integer(id));
        employee.getAddress();

        address = new Address();
        address.setCity("Metropolis");
        employee.setAddressField(address);
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
        try {
            employee = em.find(Employee.class, new Integer(id));
            address = employee.getAddress();
    
            assertTrue("The address was not persisted.", employee.getAddress() != null);
            assertTrue("The address was not correctly persisted.", employee.getAddress().getCity().equals("Metropolis"));
            
        } finally {
            Address initialAddress = em.find(Address.class, new Integer(addressId));
            employee.setAddress(null);
            employee.setManager((Employee)null);
            em.remove(address);
            em.remove(employee);
            em.remove(initialAddress);
            commitTransaction(em);
        }
    }
    
    // Test that deleting an employee works correctly.
    public void testDeleteEmployee() {
        // For some reason this test fails on Spring and seem to corrupt its context,
        // seems to be a Spring bug, removing from Spring run for now.
        if ((getServerPlatform() != null) && getServerPlatform().isSpring()) {
            return;
        }
        if (getServerSession("fieldaccess").getPlatform().isSymfoware()) {
            getServerSession("fieldaccess").logMessage("Test testDeleteEmployee skipped for this platform, "
            + "The CascadeOnDelete doesn't work on a relation where CascadeType.Remove or CascadeType.All is specified on Symfoware Platform.");
            return;
        }
        Employee employee = new Employee();
        employee.addPhoneNumber(new PhoneNumber("home", "123", "4567"));
        employee.addPhoneNumber(new PhoneNumber("fax", "456", "4567"));
        employee.addResponsibility("work hard");
        employee.addResponsibility("write code");
        employee.addProject(new Project());
        employee.setWorkWeek(new HashSet<Employee.Weekdays>());
        employee.getWorkWeek().add(Employee.Weekdays.MONDAY);
        employee.getWorkWeek().add(Employee.Weekdays.TUESDAY);
        EntityManager em = createEntityManager();
        QuerySQLTracker counter = null;
        try {
            beginTransaction(em);
            em.persist(employee);
            commitTransaction(em);
            closeEntityManager(em);
            clearCache();
            counter = new QuerySQLTracker(getDatabaseSession());
            em = createEntityManager();
            beginTransaction(em);
            employee = em.find(Employee.class, employee.getId());
            counter.getSqlStatements().clear();
            em.remove(employee);
            if (isWeavingEnabled("fieldaccess") && counter.getSqlStatements().size() > 3) {
                fail("Only 2 delete and 1 select should have occured.");
            }
            commitTransaction(em);
            beginTransaction(em);    
            verifyDelete(employee, "fieldaccess");
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
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
         if (getDatabaseSession().getPlatform().isSymfoware()) {
             getDatabaseSession().logMessage("Test testDeleteAllProjectsWithNullTeamLeader skipped for this platform, "
                     + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
             return;
         }
         internalDeleteAllProjectsWithNullTeamLeader("Project");
     }
     public void testDeleteAllSmallProjectsWithNullTeamLeader() {
         internalDeleteAllProjectsWithNullTeamLeader("SmallProject");
     }
     public void testDeleteAllLargeProjectsWithNullTeamLeader() {
         if (getDatabaseSession().getPlatform().isSymfoware()) {
             getDatabaseSession().logMessage("Test testDeleteAllLargeProjectsWithNullTeamLeader skipped for this platform, "
                     + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
             return;
         }
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
        if (getDatabaseSession().getPlatform().isSymfoware()) {
            getDatabaseSession().logMessage("Test testUpdateUsingTempStorage* skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }

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
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
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
        if (getDatabaseSession().getPlatform().isSymfoware()) {
            getDatabaseSession().logMessage("Test testUpdateAll*Projects skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }

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
        if (getDatabaseSession().getPlatform().isSymfoware()) {
            getDatabaseSession().logMessage("Test testUpdateAll*ProjectsWithName skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
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
        if (getDatabaseSession().getPlatform().isSymfoware()) {
            getDatabaseSession().logMessage("Test testUpdateAll*ProjectsWithNullTeamLeader skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
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
            emp.toString();
            fail("IllegalArgumentException has not been thrown");
        } catch(IllegalArgumentException ex) {
            if (isOnServer()) {
                assertTrue("Transaction is not roll back only", getRollbackOnly(em));
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
        // TODO: This does not work on the server but should.
        if (isOnServer()) {
            return;
        }
        
        // close the original factory
        closeEntityManagerFactory();
        // create the new one - not yet deployed
        EntityManagerFactory factory1 =  getEntityManagerFactory("fieldaccess");
        // create the second one
        EntityManagerFactory factory2 =  Persistence.createEntityManagerFactory("fieldaccess", JUnitTestCaseHelper.getDatabaseProperties());
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
        ServerSession ss = (ServerSession)getDatabaseSession();
        // in non-JTA case session doesn't have external transaction controller
        boolean hasExternalTransactionController = ss.hasExternalTransactionController();
        if (!hasExternalTransactionController) {
            // simulate JTA case
            ss.setExternalTransactionController(new DummyExternalTransactionController());
        }
        try {
            testClearEntityManagerWithoutPersistenceContext();
        } finally {
            if(!hasExternalTransactionController) {
                // remove the temporary set TransactionController
                ss.setExternalTransactionController(null);
            }
        }
    }
    
    public void testDescriptorNamedQuery(){
        //name needs to be unique to this test to ensure test cleanup errors do not cause extra results
        String lastNameToUse = "testDescriptorNamedQuery";
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("firstName").equal(builder.getParameter("fName"));
        exp = exp.and(builder.get("lastName").equal(builder.getParameter("lName")));
        query.setSelectionCriteria(exp);
        query.addArgument("fName", String.class);
        query.addArgument("lName", String.class);
        
        EntityManager em = createEntityManager();
        Session session = getDatabaseSession();
        ClassDescriptor descriptor = session.getDescriptor(Employee.class);
        descriptor.getQueryManager().addQuery("findByFNameLName", query);

        beginTransaction(em);
        try {
            Employee emp = new Employee();
            emp.setFirstName("Melvin");
            emp.setLastName(lastNameToUse);
            em.persist(emp);
            em.flush();
            
            Query ejbQuery = ((JpaEntityManager)em.getDelegate()).createDescriptorNamedQuery("findByFNameLName", Employee.class);
            
            List results = ejbQuery.setParameter("fName", "Melvin").setParameter("lName", lastNameToUse).getResultList();
            
            assertTrue(results.size() == 1);
            emp = (Employee)results.get(0);
            assertTrue(emp.getFirstName().equals("Melvin"));
            assertTrue(emp.getLastName().equals(lastNameToUse));
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
        Session session = getDatabaseSession();
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
            Query ejbQuery = ((JpaEntityManager)em.getDelegate()).createDescriptorNamedQuery("findEmployees", Employee.class, args);
            
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
    
    // Test the clone method works correctly with lazy attributes.
    public void testCloneable() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {
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
            em.remove(employee);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);            
        }
    }
    
    // Test copy methods work with weaving.
    public void testCopyingAddress() {
        Address address = new Address();
        address.setCity("Ottawa");
        Address copy = address.copy();
        if (!address.getCity().equals("Ottawa") || !copy.getCity().equals("Ottawa")) {
            fail("Copy method did not work."); 
        }
        address = new Address();
        address.setCity("Ottawa");
        Address.TransferAddress transfer = address.transferCopy();
        if (!address.getCity().equals("Ottawa") || !transfer.city.equals("Ottawa")) {
            fail("Transfer method did not work.");
        }
    }
    
    // This test weaving works when accessing a superclass field in a subclass.
    public void testSuperclassFieldInSubclass() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee employee = new Employee();
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
            if (project.getTeamLeader() != employee) {
                fail("Get team leader did not work, team is: " + project.getTeamLeader() + " but should be:" + employee);
            }
            em.remove(employee);
            em.remove(project);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
    
    /**
     * Test that all of the classes in the advanced model were weaved as expected.
     */
    public void testWeaving() {
        // Only test if weaving was on, test runs without weaving must set this system property.
        if (JUnitTestCase.isWeavingEnabled("fieldaccess")) {
            internalTestWeaving(new Employee(), true, true);
            internalTestWeaving(new FormerEmployment(), true, false);
            internalTestWeaving(new Address(), true, false);
            internalTestWeaving(new PhoneNumber(), true, false);
            internalTestWeaving(new EmploymentPeriod(), true, false);
            internalTestWeaving(new Buyer(), false, false);  // field-locking
            internalTestWeaving(new GoldBuyer(), false, false);  // field-locking
            internalTestWeaving(new PlatinumBuyer(), false, false);  // field-locking
            internalTestWeaving(new Department(), true, false);
            internalTestWeaving(new Golfer(), true, false);
            internalTestWeaving(new GolferPK(), true, false);
            internalTestWeaving(new SmallProject(), true, false);
            internalTestWeaving(new LargeProject(), true, false);
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
        ClassDescriptor descriptor = getDatabaseSession().getDescriptor(object);
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
    
    /**
     * Test that sequence numbers allocated but unused in the transaction
     * kept after transaction commits
     * in case SequencingCallback used (that happens if TableSequence is used without
     * using sequencing connection pool).
     */
    public void testSequencePreallocationUsingCallbackTest() {
        // setup
        ServerSession ss = (ServerSession)getDatabaseSession();
        // make sure the sequence has both preallocation and callback
        // (the latter means not using sequencing connection pool, 
        // acquiring values before insert and requiring transaction).
        //if(ss.getSequencingControl().shouldUseSeparateConnection()) {
        //    fail("setup failure: the test requires serverSession.getSequencingControl().shouldUseSeparateConnection()==false");
        //}
        String seqName = ss.getDescriptor(Employee.class).getSequenceNumberName();
        Sequence sequence = getDatabaseSession().getLogin().getSequence(seqName);
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
        getDatabaseSession().getSequencingControl().initializePreallocated(seqName);
        
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
    
    // Test for bug fix: 299637 - updateAttributeWithObjectTest with aggregate causes NPE when using field access
    public void updateAttributeWithObjectTest(){
        ServerSession session = (ServerSession)getDatabaseSession();
        ClassDescriptor descriptor = session.getDescriptor(Employee.class);
        UpdateListener listener = new UpdateListener();
        descriptor.getEventManager().addListener(listener);
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp = new Employee();
        emp.setFirstName("Mar");
        EmploymentPeriod period = new EmploymentPeriod();
        period.setStartDate(Date.valueOf("2010-11-12"));
        period.setEndDate(Date.valueOf("2010-11-13"));
        emp.setPeriod(period);
        em.persist(emp);
        em.flush();
        em.clear();
        
        emp = em.find(Employee.class, emp.getId());
        emp.setFirstName("Mark");
        em.flush();
        
        em.refresh(emp);
        
        assertTrue("The employment period was not properly updated.", emp.getPeriod().getStartDate().equals(Date.valueOf("2010-11-14")) && emp.getPeriod().getEndDate().equals(Date.valueOf("2010-11-15")));
        rollbackTransaction(em);
        descriptor.getEventManager().removeListener(listener);

    }
    
    // listener for updateAttributeWithObjectTest - Test for bug fix: 299637 - updateAttributeWithObjectTest
    private class UpdateListener extends DescriptorEventAdapter {
        public void aboutToUpdate(DescriptorEvent event) {
            EmploymentPeriod period = new EmploymentPeriod();
            period.setStartDate(Date.valueOf("2010-11-14"));
            period.setEndDate(Date.valueOf("2010-11-15"));
            event.updateAttributeWithObject("period", period);
        }
    }
        
    // Test that deleting an Man works correctly.
    public void testDeleteMan() {
        if (getServerSession("fieldaccess").getPlatform().isSymfoware()) {
            getServerSession("fieldaccess").logMessage("Test testDeleteMan skipped for this platform, "
            + "The CascadeOnDelete doesn't work on a relation where CascadeType.Remove or CascadeType.All is specified on Symfoware Platform.");
            return;
        }
        EntityManager em = createEntityManager("fieldaccess");
        QuerySQLTracker counter = null;
        try {
            beginTransaction(em);
            Man man = new Man();
            em.persist(man);
            Woman woman = new Woman();
            em.persist(woman);
            PartnerLink link = new PartnerLink();
            man.setPartnerLink(link);
            link.setMan(man);
            woman.setPartnerLink(link);
            link.setWoman(woman);                        
            commitTransaction(em);
            closeEntityManager(em);
            clearCache();
            counter = new QuerySQLTracker(getDatabaseSession());
            em = createEntityManager();
            beginTransaction(em);
            // Count SQL.
            man = em.find(Man.class, man.getId());
            woman = em.find(Woman.class, woman.getId());
            woman.setPartnerLink(null);
            counter.getSqlStatements().clear();
            em.remove(man);
            commitTransaction(em);
            if (counter.getSqlStatements().size() > 1) {
                if (getDatabaseSession().getPlatform().supportsDeleteOnCascade()) {
                    fail("Only one delete should have occured.");
                }
            }
            beginTransaction(em);
            verifyDelete(man);
            commitTransaction(em);
        } finally {
            if (counter != null) {
                counter.remove();
            }
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
    
    // Bug 325916
    public void testNullDouble(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        PlatinumBuyer buyer = new PlatinumBuyer();
        buyer.setName("Mike");
        buyer.setDescription("a buyer with no purchases.");
        em.persist(buyer);
        em.flush();
        em.createNativeQuery("update CMP3_FA_PBUYER set PURCHASES = NULL where BUYER_ID = " + buyer.getId()).executeUpdate();
        
        em.refresh(buyer);
        
        rollbackTransaction(em);
    }

    //  Bug 307433 - Regression in Auditing Support when using defaults.
    static class ChangeRecordKeepOldValueListener extends SessionEventAdapter {
        public UnitOfWorkChangeSet uowChangeSet;
        public void postCalculateUnitOfWorkChangeSet(SessionEvent event) {
            uowChangeSet = (UnitOfWorkChangeSet)event.getProperty("UnitOfWorkChangeSet");
        }
    }
    public void testChangeRecordKeepOldValue_Simple() {
        internalTestChangeRecordKeepOldValue(false);
    }
    public void testChangeRecordKeepOldValue_TwoStep() {
        internalTestChangeRecordKeepOldValue(true);
    }
    public void internalTestChangeRecordKeepOldValue(boolean addSecondStep) {
        if(isOnServer()) {
            // the test relies upon keeping attached objects between transactions.
            return;
        }
        
        ServerSession ss = (ServerSession)getDatabaseSession();
        ChangeRecordKeepOldValueListener listener = new ChangeRecordKeepOldValueListener();
        ss.getEventManager().addListener(listener);
        
        EntityManager em = createEntityManager();        
        try {
            // setup
            // create original object and referenced objects
            Employee emp = new Employee();
            emp.setFirstName("TestChangeRecordKeepOldValue");
            emp.setLastName("Manager_OriginalName");
            emp.setMale();
            emp.setSalary(35000);
            emp.setPeriod(new EmploymentPeriod(Helper.dateFromYearMonthDate(1993, 0, 1), Helper.dateFromYearMonthDate(1996, 11, 31)));
            emp.setFormerEmployment(new FormerEmployment("Original", new EmploymentPeriod(Helper.dateFromYearMonthDate(1990, 0, 1), Helper.dateFromYearMonthDate(1993, 11, 31))));
            Address address = new Address();
            address.setCountry("Original");
            address.setProvince("Original");
            address.setCity("Original");
            address.setStreet("Original");
            address.setPostalCode("Original");
            emp.setAddress(address);
            Department dep = new Department();
            dep.setName("Original");
            emp.setDepartment(dep);
            emp.addResponsibility("Original_1");
            emp.addResponsibility("Original_2");
            PhoneNumber phone1 = new PhoneNumber("Original_1", "111", "1111111"); 
            emp.addPhoneNumber(phone1);
            PhoneNumber phone2 = new PhoneNumber("Original_2", "222", "2222222"); 
            emp.addPhoneNumber(phone2);
            Employee emp1 = new Employee();
            emp1.setFirstName("TestChangeRecordKeepOldValue");
            emp1.setLastName("Original_1");
            emp.addManagedEmployee(emp1);
            Employee emp2 = new Employee();
            emp2.setFirstName("TestChangeRecordKeepOldValue");
            emp2.setLastName("Original_2");
            emp.addManagedEmployee(emp2);
            Project proj1 = new SmallProject();
            proj1.setName("Original_1");
            emp.addProject(proj1);
            Project proj2 = new LargeProject();
            proj2.setName("Original_2");
            emp.addProject(proj2);
            Project proj3 = new LargeProject();
            proj3.setName("Original_3");
            proj3.setTeamLeader(emp);
            emp1.addProject(proj3);
            emp2.addProject(proj3);

            // persist original object and all referenced objects
            beginTransaction(em);
            Equipment equipment1 = new Equipment();
            equipment1.setDescription("Original_1");
            // persist is not cascaded for department.equipment - have to explicitly persist it.
            em.persist(equipment1);
            dep.addEquipment(equipment1);
            Equipment equipment2 = new Equipment();
            equipment2.setDescription("Original_2");
            // persist is not cascaded for department.equipment - have to explicitly persist it.
            // moreover, equipment.id is used as a key in dep.equipment Map, therefore equipmet 
            // should be persisted before been added to dep.
            em.persist(equipment2);
            dep.addEquipment(equipment2);
            // persist is not cascaded for employee.department - have to explicitly persist it.
            // moreover, equipment.id is used as a key in dep.equipment Map, therefore equipmet 
            // should be persisted before been added to dep.
            em.persist(dep);
            em.persist(emp);
            commitTransaction(em);
            
            // backup original object and all referenced objects
            CopyGroup copyGroupBackup = new CopyGroup();
            copyGroupBackup.cascadeAllParts();
            ss.copy(emp, copyGroupBackup);
            // emp references (directly or through other objects) all the created objects, therefore all of them are copied.
            Map backupMap = copyGroupBackup.getCopies();
            
            // verify that backup objects are identical to originals
            Iterator<Map.Entry> it = backupMap.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry entry = it.next();
                Object original = entry.getKey();
                ClassDescriptor descriptor = ss.getDescriptor(original);
                if(!descriptor.isAggregateDescriptor()) {
                    Object backup = entry.getValue();
                    if(original == backup) {
                        fail("Test problem: backup failed: original == backup: " + original);
                    }
                    if(!ss.compareObjects(original, backup)) {
                        fail("Test problem: backup failed: compareObjects(original, backup) == false: " + original + "; " + backup);
                    }
                }
            }
            
            // change original object
            beginTransaction(em);
            // DirectToField
            emp.setLastName("Manager_NewName");
            // DirectToField
            emp.setSalary(100);
            // Aggregate
            emp.getPeriod().setStartDate(Helper.dateFromYearMonthDate(2000, 0, 1));
            emp.getPeriod().setEndDate(Helper.dateFromYearMonthDate(2002, 0, 1));
            // Aggregate
//            emp.setFormerEmployment(new FormerEmployment("New", new EmploymentPeriod(Helper.dateFromYearMonthDate(1989, 0, 1), Helper.dateFromYearMonthDate(1992, 11, 31))));
            emp.getFormerEmployment().setFormerCompany("New");
            emp.getFormerEmployment().getPeriod().setStartDate(Helper.dateFromYearMonthDate(1989, 0, 1));
            emp.getFormerEmployment().getPeriod().setStartDate(Helper.dateFromYearMonthDate(1992, 11, 31));
            //**temp for debug
            FormerEmployment fe = emp.getFormerEmployment();
            // DirectCollection
            emp.removeResponsibility("Original_1");
            emp.addResponsibility("New_1");
            // Private Collection 1 to many
            PhoneNumber phone1New = new PhoneNumber("New_1", "111", "1111111"); 
            emp.addPhoneNumber(phone1New);
            emp.removePhoneNumber(phone1);
            // Collection 1 to many
            emp.removeManagedEmployee(emp1);
            emp1.setManager(null);
            em.remove(emp1);
            Employee emp1New = new Employee();
            emp1New.setFirstName("TestChangeRecordKeepOldValue");
            emp1New.setLastName("New_1");
            emp1New.addProject(proj3);
            emp.addManagedEmployee(emp1New);
            // Collection many to many
            emp.removeProject(proj1);
            em.remove(proj1);
            Project proj1New = new LargeProject();
            proj1New.setName("New_1");
            emp.addProject(proj1New);
            // ObjectReference
            proj3.setTeamLeader(null);
            // ObjectReference
            Address addressNew = new Address();
            addressNew.setCountry("New");
            addressNew.setProvince("New");
            addressNew.setCity("New");
            addressNew.setStreet("New");
            addressNew.setPostalCode("New");
            emp.setAddress(addressNew);
            em.remove(address);
            // Map 1 to many
            dep.getEquipment().remove(equipment1.getId());
            Equipment equipment1New = new Equipment();
            equipment1New.setDescription("New_1");
            em.persist(equipment1New);
            dep.addEquipment(equipment1New);
            
            // additional change typically overrides existing aggregate or collection with the new one, then alters aggregate or adds to collection. 
            //**temp for debug
            FormerEmployment fe2 = null;
            if(addSecondStep) {
                // Aggregate
                emp.setPeriod(new EmploymentPeriod());
                emp.getPeriod().setStartDate(Helper.dateFromYearMonthDate(2001, 0, 1));
                emp.getPeriod().setEndDate(Helper.dateFromYearMonthDate(2003, 0, 1));
                // Aggregate
                //**temp for debug
                fe2 = emp.getFormerEmployment();
                emp.setFormerEmployment(new FormerEmployment("New_New", new EmploymentPeriod(Helper.dateFromYearMonthDate(1988, 0, 1), Helper.dateFromYearMonthDate(1991, 11, 31))));
/*                emp.getFormerEmployment().setPeriod(new EmploymentPeriod());
                emp.getFormerEmployment().getPeriod().setStartDate(Helper.dateFromYearMonthDate(1987, 0, 1));
                emp.getFormerEmployment().getPeriod().setEndDate(Helper.dateFromYearMonthDate(1990, 0, 1));*/
                // DirectCollection
                emp.setResponsibilities(new ArrayList());
                emp.addResponsibility("New_New_1");
                // Private Collection 1 to many
                emp.setPhoneNumbers(new HashSet());
                PhoneNumber phone1NewNew = new PhoneNumber("New_New_1", "111", "1111111"); 
                emp.addPhoneNumber(phone1NewNew);
                // Collection 1 to many
                emp1New.setManager(null);
                emp2.setManager(null);
                emp.setManagedEmployees(new Vector());
                Employee emp1NewNew = new Employee();
                emp1NewNew.setFirstName("TestChangeRecordKeepOldValue");
                emp1NewNew.setLastName("New_New_1");
                emp1NewNew.addProject(proj3);
                emp.addManagedEmployee(emp1NewNew);
                em.remove(emp1New);
                em.remove(emp2);
                // Collection many to many
                emp.setProjects(new ArrayList());
                Project proj1NewNew = new LargeProject();
                proj1NewNew.setName("New_New_1");
                emp.addProject(proj1NewNew);
                em.remove(proj1New);
                em.remove(proj2);
                // ObjectReference
                Address addressNewNew = new Address();
                addressNewNew.setCountry("New_New");
                addressNewNew.setProvince("New_New");
                addressNewNew.setCity("New_New");
                addressNewNew.setStreet("New_New");
                addressNewNew.setPostalCode("New_New");
                emp.setAddress(addressNewNew);
                em.remove(addressNew);
                // Map 1 to many
                // We are about to override equipment map.
                // It's a private OneToMany mapping so normally all the 
                // members would be removed automatically.
                // However in theis case we have explicitly persisted the the new member added in thhe current transaction
                // (because Equipment's id is used as its key in the Department.equipment Map)
                // therefore we have to explicitly em.remove it.
                em.remove(equipment1New);
                dep.setEquipment(new HashMap());
                Equipment equipment1NewNew = new Equipment();
                equipment1NewNew.setDescription("New_New_1");
                em.persist(equipment1NewNew);
                dep.addEquipment(equipment1NewNew);
            }
            commitTransaction(em);
            
            // backup updated objects 
            CopyGroup copyGroupUpdated = new CopyGroup();
            copyGroupUpdated.cascadeAllParts();
            ss.copy(emp, copyGroupUpdated);
            // copies of the updated objects will be altered to contain old values.
            // if altering the test, make sure that emp still references (directly or through other objects) all the updated objects, so that all of them are copied.
            Map oldValueMap = copyGroupUpdated.getCopies();
            // using ChangeRecords bring back the original state of the object
            Iterator itChangeSets = ((org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet)listener.uowChangeSet).getCloneToObjectChangeSet().entrySet().iterator();
            while(itChangeSets.hasNext()) {
                Map.Entry entry = (Map.Entry)itChangeSets.next();
                Object object = entry.getKey();
                ClassDescriptor descriptor = ss.getDescriptor(object);
                if(!descriptor.isAggregateDescriptor()) {
                    ObjectChangeSet changeSet = (ObjectChangeSet)entry.getValue();
                    if(!((org.eclipse.persistence.internal.sessions.ObjectChangeSet)changeSet).shouldBeDeleted() && !changeSet.isNew()) {
                        List<ChangeRecord> changes = changeSet.getChanges(); 
                        if(changes != null && !changes.isEmpty()) {
                            Object oldValueObject = oldValueMap.get(object);
                            for(ChangeRecord changeRecord : changeSet.getChanges()) {
                                Object oldValue = changeRecord.getOldValue();
                                DatabaseMapping mapping = ((org.eclipse.persistence.internal.sessions.ChangeRecord)changeRecord).getMapping(); 
                                mapping.setRealAttributeValueInObject(oldValueObject, oldValue);
                            }
                        }
                    }
                }
            }
            
            String errorMsgOldValues = "";
            // now compare oldValue objects with corresponding backup objects
            itChangeSets = ((org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet)listener.uowChangeSet).getCloneToObjectChangeSet().entrySet().iterator();
            while(itChangeSets.hasNext()) {
                Map.Entry entry = (Map.Entry)itChangeSets.next();
                Object object = entry.getKey();
                ClassDescriptor descriptor = ss.getDescriptor(object);
                if(!descriptor.isAggregateDescriptor()) {
                    ObjectChangeSet changeSet = (ObjectChangeSet)entry.getValue();
                    if(!((org.eclipse.persistence.internal.sessions.ObjectChangeSet)changeSet).shouldBeDeleted() && !changeSet.isNew()) {
                        List<ChangeRecord> changes = changeSet.getChanges(); 
                        if(changes != null && !changes.isEmpty()) {
                            Object oldValueObject = oldValueMap.get(object);
                            Object backupObject = backupMap.get(object);
                            // compare oldValue with backup object
                            if(!ss.compareObjects(oldValueObject, backupObject)) {
                                errorMsgOldValues += '\t' + object.toString() + '\n';
                            }
                        }
                    }
                }
            }
            
            // set of attached to em objects
            Set updatedObjects = new HashSet();
            for(Object object : oldValueMap.keySet()) {
                ClassDescriptor descriptor = ss.getDescriptor(object);
                if(!descriptor.isAggregateDescriptor()) {
                    updatedObjects.add(object);
                }
            }
            
            // verify that the objects were correctly written to the db
            String errorMsgDb = "";
            clearCache();
            Map updatedToReadBack = new HashMap();
            EntityManager em2 = createEntityManager();
            beginTransaction(em2);
            UnitOfWork uow2 = ((EntityManagerImpl)em2.getDelegate()).getActivePersistenceContext(null);
            // read back from the db all the attached to em objects.
            // before comparison clear all read-only mappings that we didn't set in the attached to em objects.
            for(Object object : updatedObjects) {
                Object readBack = uow2.readObject(object);
                if(readBack instanceof Project) {
                    ((Project)readBack).getTeamMembers().clear();
                } else if(readBack instanceof Address) {
                    ((Address)readBack).getEmployees().clear();
                } else if(readBack instanceof Department) {
                    ((Department)readBack).getEmployees().clear();
                }
                updatedToReadBack.put(object, readBack);
            }
            // now compare object attached to em with the object read from the db
            for(Object entryObject : updatedToReadBack.entrySet()) {
                Map.Entry entry = (Map.Entry)entryObject;
                Object object = entry.getKey();
                Object readBack = entry.getValue();
                if(!ss.compareObjects(object, readBack)) {
                    errorMsgDb += '\t' + object.toString() + '\n';
                }
            }
            rollbackTransaction(em2);
            
            // clean up
            beginTransaction(em);
            Set objectsToRemove = new HashSet();
            // remove all dependencies and add cache objects to be directly removed.
            for(Object object : updatedObjects) {
                if(object instanceof Employee) {
                    ((Employee)object).getManagedEmployees().clear();
                    ((Employee)object).setManager(null);
                    ((Employee)object).setAddress((Address)null);
                    ((Employee)object).getProjects().clear();
                    ((Employee)object).setDepartment(null);
                    objectsToRemove.add(object);
                } else if(object instanceof Project) {
                    ((Project)object).setTeamLeader(null);
                    objectsToRemove.add(object);
                } else if(object instanceof Address || object instanceof Department) {
                    objectsToRemove.add(object);
                }
            }
            // remove objects that should be removed directly.
            for(Object object : objectsToRemove) {
                em.remove(object);
            }
            commitTransaction(em);
            
            String errorMsg = "";
            if(errorMsgOldValues.length() > 0) {
                errorMsgOldValues = "Some old values and back up objects are not equal:\n" + errorMsgOldValues;
                errorMsg += errorMsgOldValues; 
            }
            if(errorMsgDb.length() > 0) {
                errorMsgDb = "\nSome values were incorrectly written into the db:\n" + errorMsgDb;
                errorMsg += errorMsgDb; 
            }
            if(errorMsg.length() > 0) {
                fail(errorMsg);
            }
        } finally {
            if(isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            ss.getEventManager().removeListener(listener);
        }
    }

    //  Bug 307433 - Regression in Auditing Support when using defaults.
    public void testSetNewAggregate() {
        // setup
        EntityManager em = createEntityManager();
        Employee emp = new Employee();
        emp.setPeriod(new EmploymentPeriod(Helper.dateFromYearMonthDate(1988, 0, 1), Helper.dateFromYearMonthDate(1991, 11, 31)));
        beginTransaction(em);
        em.persist(emp);
        commitTransaction(em);
        int id = emp.getId();
        
        // test
        beginTransaction(em);
        if(isOnServer() && isJTA()) {
            emp = em.find(Employee.class, id);
        }
        emp.getPeriod().setStartDate(Helper.dateFromYearMonthDate(1989, 0, 1));
        emp.getPeriod().setEndDate(Helper.dateFromYearMonthDate(1992, 0, 1));
        emp.setPeriod(new EmploymentPeriod(Helper.dateFromYearMonthDate(1989, 0, 1), Helper.dateFromYearMonthDate(1992, 11, 31)));
        commitTransaction(em);
        closeEntityManager(em);
        
        // verify
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        Employee empRead = em.find(Employee.class, id);
        //clean-up
        em.remove(empRead);
        commitTransaction(em);
        closeEntityManager(em);
        
        assertTrue("Wrong emp.getPeriod() inserted into db", emp.getPeriod().equals(empRead.getPeriod()));
    }
    
    //  Bug 307433 - Regression in Auditing Support when using defaults.
    public void testSetNewNestedAggregate() {
        // setup
        EntityManager em = createEntityManager();
        Employee emp = new Employee();
        emp.setFormerEmployment(new FormerEmployment("A", new EmploymentPeriod(Helper.dateFromYearMonthDate(1988, 0, 1), Helper.dateFromYearMonthDate(1991, 11, 31))));
        beginTransaction(em);
        em.persist(emp);
        commitTransaction(em);
        int id = emp.getId();
        
        // test
        beginTransaction(em);
        if(isOnServer() && isJTA()) {
            emp = em.find(Employee.class, id);
        }
        emp.setFormerEmployment(new FormerEmployment("B", new EmploymentPeriod(Helper.dateFromYearMonthDate(1987, 0, 1), Helper.dateFromYearMonthDate(1990, 11, 31))));
        commitTransaction(em);
        closeEntityManager(em);
        
        // verify
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        Employee empRead = em.find(Employee.class, id);
        //clean-up
        em.remove(empRead);
        commitTransaction(em);
        closeEntityManager(em);
        
        assertTrue("Wrong emp.getFormerEmployment().getPeriod() inserted into db", emp.getFormerEmployment().getPeriod().equals(empRead.getFormerEmployment().getPeriod()));
    }
    
    //  Bug 336280 - Same object referenced from both EM cache and shared cache
    public void testObjectReferencedInBothEmAndSharedCache_ObjectReferenceMappingVH() {
        EntityManager em = createEntityManager();
        
        Employee emp = new Employee();
        emp.setFirstName("Manager");
        
        Employee emp1 = new Employee();
        emp1.setFirstName("1");
        emp.addManagedEmployee(emp1);
        Employee emp2 = new Employee();
        emp2.setFirstName("2");
        emp.addManagedEmployee(emp2);
        
        ServerSession ss = null;
        beginTransaction(em);
        em.persist(emp);
        // in JTA case transaction required to obtain ServerSession through getServersession method.
        ss = (ServerSession)getDatabaseSession();
        commitTransaction(em);
        
        CopyGroup copyGroup = new CopyGroup();
        copyGroup.cascadeAllParts();
        ss.copy(emp, copyGroup);
        
        Set originalObjects = copyGroup.getCopies().keySet();
        // copyGroup cascades through all mappings.
        // originalObjects should consist of just three objects: emp, emp1, emp2.
        // However if manager_vh is wrapped around manager instance from the shared cache (empShared),
        // the size will be 6: emp, emp1, emp2 and empShared, emp1Shared, emp2Shared.
        assertTrue(originalObjects.size() == 3);
    }
    
    //  Bug 336280 - Same object referenced from both EM cache and shared cache
    public void testObjectReferencedInBothEmAndSharedCache_AggregateObjectMapping() {
        EntityManager em = createEntityManager();
        
        ServerSession ss = null;
        // persist a new Employee object
        Employee emp = new Employee();
        emp.setFirstName("A");
        EmploymentPeriod period = new EmploymentPeriod(Helper.dateFromYearMonthDate(1993, 0, 1), Helper.dateFromYearMonthDate(1996, 11, 31));
        emp.setPeriod(period);
        beginTransaction(em);
        em.persist(emp);
        // in JTA case transaction required to obtain ServerSession through getServersession method.
        ss = (ServerSession)getDatabaseSession();
        commitTransaction(em);
        closeEntityManager(em);
        
        // using query by example read empShared corresponding to emp in ghe share cache
        Employee empShared = (Employee)ss.readObject(emp);
        // these are really to distinct objects
        assertTrue(emp != empShared);
        // they should not share the aggragate
        assertTrue(emp.getPeriod() != empShared.getPeriod());
    }

    public void testCharFieldDefaultNullValue() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Vegetable vegetable;
        Vegetable vegetable2;
        VegetablePK pk = new VegetablePK("Eggplant", "Purple");
        VegetablePK pk2 = new VegetablePK("Capsicum", "Green");
        String[] tags = {"California", "DE"};
        String[] tags2 = {"Florida", "FD"};

        try {
            // Usecase for char with value ' ' (space)
            vegetable = new Vegetable();
            vegetable.setId(pk);
            vegetable.setCost(2.09);
            vegetable.setTags(tags);
            vegetable.setType(' ');
            em.persist(vegetable);

            // Usecase for char with value null 
            vegetable2 = new Vegetable();
            vegetable2.setId(pk2);
            vegetable2.setCost(1.35);
            vegetable2.setTags(tags2);
            em.persist(vegetable2);

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        } finally {
            closeEntityManager(em);
        }

        em = createEntityManager();
        em.clear();
        clearCache("fieldaccess");

        beginTransaction(em);
        try {
            vegetable = em.find(Vegetable.class, pk);
            vegetable2 = em.find(Vegetable.class, pk2);
            commitTransaction(em);
            assertNotNull(vegetable);
            assertNotNull(vegetable2);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }
}
