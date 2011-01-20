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
 *     11/17/2010-2.2 Michael O'Brien 
 *       - 325605: Do not track SQL category logs in QuerySQLTracker logged at FINEST
 *         testDeleteEmployee*() will fail on DB2 9.7 Universal because cascade deletes
 *         of an uninstantiated collection of enums must inherently be deleted even if
 *         the actual collection is empty.  DB2 warns of nothing deleted - we convert it to a FINEST log
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;
import java.util.Iterator;

import java.sql.Date;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.PessimisticLockScope;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TransactionRequiredException;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;
import javax.persistence.spi.LoadState;
import javax.persistence.spi.ProviderUtil;

import junit.framework.*;

import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.config.ExclusiveConnectionMode;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.internal.indirection.BatchValueHolder;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.queries.CursoredStreamPolicy;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.queries.ScrollableCursorPolicy;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.ConnectionPolicy;
import org.eclipse.persistence.sessions.server.ConnectionPool;
import org.eclipse.persistence.sessions.server.ReadConnectionPool;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.tools.schemaframework.SequenceObjectDefinition;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.Connector;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.DefaultConnector;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.CacheUsageIndirectionPolicy;
import org.eclipse.persistence.config.CascadePolicy;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.PessimisticLock;
import org.eclipse.persistence.config.QueryType;
import org.eclipse.persistence.config.ResultSetConcurrency;
import org.eclipse.persistence.config.ResultSetType;
import org.eclipse.persistence.config.ResultType;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.descriptors.PersistenceEntity;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.jpa.jdbc.DataSourceImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.weaving.PersistenceWeaved;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedLazy;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.platform.server.was.WebSphere_7_Platform;

import org.eclipse.persistence.testing.framework.ConnectionWrapper;
import org.eclipse.persistence.testing.framework.DriverWrapper;
import org.eclipse.persistence.testing.framework.QuerySQLTracker;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.jpa.advanced.*;
import org.eclipse.persistence.testing.models.jpa.relationships.CustomerCollection;

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
        List<String> tests = new ArrayList<String>();
        tests.add("testClearEntityManagerWithoutPersistenceContext");
        tests.add("testDeadConnectionFailover");
        tests.add("testDeadPoolFailover");
        tests.add("testDeleteEmployee");
        tests.add("testDeleteEmployee_with_status_enum_collection_instantiated");
        tests.add("testDeleteMan");
        tests.add("testFindDeleteAllPersist");
        tests.add("testExtendedPersistenceContext");
        tests.add("testRemoveFlushFind");
        tests.add("testRemoveFlushPersistContains");
        tests.add("testTransactionRequired");
        tests.add("testSubString");
        tests.add("testFlushModeOnUpdateQuery");
        tests.add("testAnnotationDefaultLockModeNONEOnUpdateQuery");
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
        tests.add("testParallelMultipleFactories");
        tests.add("testMultipleFactories");
        tests.add("testPersistenceProperties");
        tests.add("testBeginTransactionCloseCommitTransaction");
        tests.add("testBeginTransactionClose");
        tests.add("testClose");
        tests.add("testPersistOnNonEntity");
        tests.add("testWRITELock");
        tests.add("testOPTIMISTIC_FORCE_INCREMENTLock");
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
        tests.add("testEMFClose");
        tests.add("testCheckVersionOnMerge");
        tests.add("testFindWithNullPk");
        tests.add("testFindWithWrongTypePk");
        tests.add("testPersistManagedNoException");
        tests.add("testPersistManagedException");
        tests.add("testPersistRemoved");
        tests.add("testREADLock");
        tests.add("testOPTIMISTICLock");
        tests.add("testPESSIMISTIC_READLock");
        tests.add("testPESSIMISTIC_WRITELock");
        tests.add("testPESSIMISTIC_READLockWithNoChanges");
        tests.add("testPESSIMISTIC_WRITELockWithNoChanges");
        tests.add("testPESSIMISTIC_READ_TIMEOUTLock");
        tests.add("testPESSIMISTIC_WRITE_TIMEOUTLock");
        tests.add("testPESSIMISTIC_ExtendedScope");
        tests.add("testRefreshOPTIMISTICLock");
        tests.add("testRefreshPESSIMISTIC_READLock");
        tests.add("testRefreshPESSIMISTIC_WRITELock");
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
        tests.add("testSequencePreallocationUsingCallbackTest");
        tests.add("testForceSQLExceptionFor219097");
        tests.add("testRefreshInvalidateDeletedObject");
        tests.add("testClearWithFlush2");
        tests.add("testEMFWrapValidationException");
        tests.add("testEMDefaultTxType");
        tests.add("testMergeNewObject");
        tests.add("testMergeNewObject2");
        tests.add("testMergeNewObject3_UseSequencing");
        tests.add("testMergeNewObject3_DontUseSequencing");
        tests.add("testCreateEntityManagerFactory");
        tests.add("testCreateEntityManagerFactory2");
        tests.add("testPessimisticLockHintStartsTransaction");
        tests.add("testManyToOnePersistCascadeOnFlush");
        tests.add("testDiscoverNewReferencedObject");
        tests.add("testBulkDeleteThenMerge");
        tests.add("testNativeSequences");
        tests.add("testGetReference");
        tests.add("testGetReferenceUpdate");
        tests.add("testGetReferenceUsedInUpdate");
        tests.add("testBadGetReference");
        tests.add("testClassInstanceConverter");
        tests.add("test210280EntityManagerFromPUwithSpaceInNameButNotInPath");
        tests.add("test210280EntityManagerFromPUwithSpaceInPathButNotInName");
        tests.add("test210280EntityManagerFromPUwithSpaceInNameAndPath");
        tests.add("testNewObjectNotCascadePersist");
        tests.add("testConnectionPolicy");
        tests.add("testConverterIn");
        tests.add("testExceptionForPersistNonEntitySubclass");
        tests.add("testEnabledPersistNonEntitySubclass");
        tests.add("testCloneEmbeddable");
        tests.add("testCloseOnCommit");
        tests.add("testPersistOnCommit");
        tests.add("testFlushMode");
        tests.add("testEmbeddedNPE");
        tests.add("testCollectionAddNewObjectUpdate");
        tests.add("testEMCloseAndOpen");
        tests.add("testEMFactoryCloseAndOpen");
        tests.add("testPostAcquirePreReleaseEvents_InternalConnectionPool");
        tests.add("testPostAcquirePreReleaseEvents_ExternalConnectionPool");
        tests.add("testNoPersistOnCommit");
        tests.add("testNoPersistOnCommitProperties");
        tests.add("testForUOWInSharedCacheWithBatchQueryHint");
        tests.add("testNoPersistOnFlushProperties");
        tests.add("testUOWReferenceInExpressionCache");
        tests.add("testIsLoadedWithReference");
        tests.add("testIsLoadedWithoutReference");
        tests.add("testIsLoadedWithoutReferenceAttribute");
        tests.add("testGenerateSessionNameFromConnectionProperties");
        tests.add("testLockWithJoinedInheritanceStrategy");
        tests.add("testPreupdateEmbeddable");
        tests.add("testFindReadOnlyIsolated");
        tests.add("testInheritanceQuery");
        tests.add("testNullBasicMap");
        tests.add("testFlushClearFind");
        tests.add("testFlushClearQueryPk");
        tests.add("testFlushClearQueryNonPK");
        tests.add("testNestedBatchQueryHint");
        tests.add("testSequenceObjectDefinition");
        tests.add("testTemporalOnClosedEm");
        tests.add("testTransientMapping");
        tests.add("testUpdateAllProjects");
        tests.add("testUpdateUsingTempStorage");
        tests.add("testWeaving");
        if (!isJPA10()) {
            tests.add("testDetachNull");
            tests.add("testDetachRemovedObject");
            tests.add("testLockingLeftJoinOneToOneQuery");
            tests.add("testLockingLeftJoinOneToOneQuery2");
            tests.add("testGetProperties");
            tests.add("testDetachNonEntity");
            tests.add("testFindWithProperties");
            tests.add("testDetachManagedObject");
            tests.add("testDetachNonManagedObject");
            tests.add("testPESSIMISTIC_FORCE_INCREMENTLock");
            tests.add("testGetLockModeType");
            tests.add("testGetEntityManagerFactory");
            tests.add("testConnectionPolicySetProperty");
            tests.add("testUnWrapClass");
            tests.add("testIsLoaded");
            tests.add("testIsLoadedAttribute");
            tests.add("testGetIdentifier");
            tests.add("testGetHints");
            tests.add("testPESSIMISTIC_FORCE_INCREMENTLockOnNonVersionedEntity");
            tests.add("testSelectEmbeddable");
            tests.add("testNonPooledConnection");
            tests.add("testExclusiveIsolatedLeaksConnectionOnClear");
            tests.add("testSetTargetQueryOneToMany");
        }
        Collections.sort(tests);
        for (String test : tests) {
            suite.addTest(new EntityManagerJUnitTestSuite(test));
        }
        if (!isJPA10()) {
            suite.addTest(new EntityManagerJUnitTestSuite("testCascadeDetach"));
        }
        return suite;
    }

    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());

        // Force uppercase for Postgres.
        if (getServerSession().getPlatform().isPostgreSQL()) {
            getServerSession().getLogin().setShouldForceFieldNamesToUpperCase(true);
        }
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

    public void testEMFClose() {
        // This test tests the bug fix for 260511
    	// The NPE would be thrown if the EnityManager 
    	// was created through the constructor
    	String errorMsg = "";
    	EntityManagerFactory em = new EntityManagerFactoryImpl(JUnitTestCase.getServerSession());
        try {
        	em.close();
        } catch (RuntimeException ex) {
        	errorMsg ="EMFClose: " + ex.getMessage() +";";
        }

        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
 
    public void testRefreshOPTIMISTICLock(){
        // Cannot create parallel entity managers in the server.
        if (! isOnServer()) {
            EntityManager em = createEntityManager();
            beginTransaction(em);
            Employee employee = null;
            
            try {
                employee = new Employee();
                employee.setFirstName("Billy");
                employee.setLastName("Madsen");
                em.persist(employee);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw ex;
            }
          
            EntityManager em2 = createEntityManager();
            Exception optimisticLockException = null;
            beginTransaction(em);
            
            try {
                em.refresh(employee, LockModeType.OPTIMISTIC);
                beginTransaction(em2);
                
                try {
                    Employee employee2 = em2.find(Employee.class, employee.getId());
                    employee2.setFirstName("Tilly");
                    commitTransaction(em2);
                } catch (RuntimeException ex) {
                    if (isTransactionActive(em2)) {
                        rollbackTransaction(em2);
                    }
                    
                    throw ex;
                } finally {
                    closeEntityManager(em2);
                }
            
                try {
                    em.flush();
                } catch (PersistenceException exception) {
                    if (exception instanceof OptimisticLockException){
                        optimisticLockException = exception;
                    } else {
                        throw exception;
                    }
                }
                
                rollbackTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw ex;
            }
            
            try {
                beginTransaction(em);
                employee = em.find(Employee.class, employee.getId());
                em.remove(employee);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw ex;
            }
            
            assertFalse("Proper exception not thrown when EntityManager.lock(object, OPTIMISTIC) is used.", optimisticLockException == null);
        }
    }       
    
    public void testRefreshPESSIMISTIC_READLock() {
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateNoWaitSupported()) {
            EntityManager em = createEntityManager();
            Department dept = null;
            
            try {
                beginTransaction(em);
                dept = new Department();
                dept.setName("Pessimistic Department");
                em.persist(dept);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw ex;
            }
            
            Exception pessimisticLockException = null;
            try {
                beginTransaction(em);
                dept = em.find(Department.class, dept.getId());
                em.lock(dept, LockModeType.PESSIMISTIC_READ);
                dept.setName("New Pessimistic Department");
                
                EntityManager em2 = createEntityManager();
                
                try {
                    beginTransaction(em2);
                    Department dept2 = em2.find(Department.class, dept.getId());
                    HashMap properties = new HashMap();
                    // According to the spec a 0 indicates a NOWAIT clause.
                    properties.put(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 0);
                    em2.refresh(dept2, LockModeType.PESSIMISTIC_READ, properties);
                } catch (PersistenceException ex) {
                    if (ex instanceof javax.persistence.PessimisticLockException) {
                        pessimisticLockException = ex;
                    } else {
                        throw ex;
                    } 
                } finally {
                    closeEntityManager(em2);
                }
            
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                throw ex;
            } finally {
                closeEntityManager(em);
            }
            
            assertFalse("Proper exception not thrown when EntityManager.lock(object, PESSIMISTIC) is used.", pessimisticLockException == null);
        }
    }
    
    public void testRefreshPESSIMISTIC_WRITELock() {
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateNoWaitSupported()) {
            EntityManager em = createEntityManager();
            Department dept = null;
            
            try {
                beginTransaction(em);
                dept = new Department();
                dept.setName("Pessimistic Department");
                em.persist(dept);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw ex;
            }
            
            Exception pessimisticLockException = null;
            try {
                beginTransaction(em);
                dept = em.find(Department.class, dept.getId());
                em.lock(dept, LockModeType.PESSIMISTIC_WRITE);
                dept.setName("New Pessimistic Department");
                
                EntityManager em2 = createEntityManager();
                
                try {
                    beginTransaction(em2);
                    Department dept2 = em2.find(Department.class, dept.getId());
                    HashMap properties = new HashMap();
                    // According to the spec a 0 indicates a NOWAIT clause.
                    properties.put(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 0);
                    em2.refresh(dept2, LockModeType.PESSIMISTIC_WRITE, properties);
                } catch (PersistenceException ex) {
                    if (ex instanceof javax.persistence.PessimisticLockException) {
                        pessimisticLockException = ex;
                    } else {
                        throw ex;
                    } 
                } finally {
                    closeEntityManager(em2);
                }
            
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                throw ex;
            } finally {
                closeEntityManager(em);
            }
            
            assertFalse("Proper exception not thrown when EntityManager.lock(object, PESSIMISTIC) is used.", pessimisticLockException == null);
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
            
            if (!getServerSession().getPlatform().isSymfoware()){
                // Symfoware does not support delete all queries on multi-table entities
                // use a native query to delete instead 
                em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
            } else {
                // delete the Employee from the db
                em.createNativeQuery("DELETE FROM CMP3_EMPLOYEE WHERE F_NAME = '"+firstName+"'").executeUpdate();
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
            
            beginTransaction(em1);
            em1.persist(address);
            commitTransaction(em1);
            
            //Cache the Address
            em1 = createEntityManager();
            beginTransaction(em1);
            address = em1.find(Address.class, address.getID());

            // Delete Address outside of JPA so that the object still stored in the cache.
            em2 = createEntityManager();
            beginTransaction(em2);
            em2.createNativeQuery("DELETE FROM CMP3_ADDRESS where address_id = ?1").setParameter(1, address.getID()).executeUpdate();
            commitTransaction(em2);
            
            //Call refresh to invalidate the object
            em1.refresh(address);
        }catch (Exception e){
            //expected exception
        } finally{
            if (isTransactionActive(em1)) {
                rollbackTransaction(em1);
            }
        }
        
        //Verify
        beginTransaction(em1);
        address=em1.find(Address.class, address.getID());
        commitTransaction(em1);
        
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
        Object obj = getServerSession().getIdentityMapAccessor().getFromIdentityMap(result.get(0));
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
                
                if (getServerSession().getPlatform().isSymfoware()){
                    updateQuery = em.createNativeQuery("UPDATE CMP3_EMPLOYEE SET VERSION = (VERSION + 1) WHERE F_NAME LIKE '" + firstName + "' AND EMP_ID in (SELECT EMP_ID FROM CMP3_SALARY)");
                } else {
                    updateQuery = em.createQuery("UPDATE Employee e set e.salary = 100 where e.firstName like '" + firstName + "'");
                }
                updateQuery.setFlushMode(FlushModeType.AUTO);
                em.persist(emp);
                updateQuery.executeUpdate();
                if (getServerSession().getPlatform().isSymfoware()){
                    updateQuery = em.createNativeQuery("UPDATE CMP3_SALARY SET SALARY = 100 WHERE EMP_ID IN (SELECT EMP_ID FROM CMP3_EMPLOYEE WHERE F_NAME LIKE '" + firstName + "')");
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
    
    public void testAnnotationDefaultLockModeNONEOnUpdateQuery() {
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testAnnotationDefaultLockModeNONEOnUpdateQuery skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        
        try {
            beginTransaction(em);
            em.createNamedQuery("UpdateEmployeeQueryWithLockModeNONE").executeUpdate();
            commitTransaction(em);
        } catch (Exception e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            fail("Update query failed: " + e.getMessage());
        } finally {
            closeEntityManager(em);
        }
    }

    public void testSetRollbackOnly(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp = null; 
        Employee emp2 = null;
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
                if (ex.getCause() instanceof javax.persistence.RollbackException) {
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
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testSubString skipped for this platform, "
                    + "Symfoware doesn't allow dynamic parameter as first argument of SUBSTRING. (bug 304897)");
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
    
    public void testNewObjectNotCascadePersist(){
        IllegalStateException exception = null;
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Golfer g = new Golfer();
        WorldRank wr = new WorldRank();
        g.setWorldRank(wr);
        em.persist(g);
        try{
            em.flush();
        }catch (IllegalStateException ex){
            exception = ex;
        }finally{
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        assertNotNull("Failed to throw IllegalStateException see bug: 237279 ", exception);
        
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
        
        try {
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
    
    public void testOPTIMISTIC_FORCE_INCREMENTLock(){
        // Cannot create parallel transactions.
        if (! isOnServer()) {
            EntityManager em = createEntityManager();
            Employee employee;
            
            try {
                beginTransaction(em);
                employee = new Employee();
                employee.setFirstName("Philip");
                employee.setLastName("Madsen");
                em.persist(employee);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw ex;
            }
                
            Exception optimisticLockException = null;

            try {
                beginTransaction(em);
                employee = em.find(Employee.class, employee.getId(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                EntityManager em2 = createEntityManager();

                try {
                    beginTransaction(em2);
                    Employee employee2 = em2.find(Employee.class, employee.getId());
                    employee2.setFirstName("Tulip");
                    commitTransaction(em2);
                } catch (RuntimeException ex) {
                    if (isTransactionActive(em2)) {
                        rollbackTransaction(em2);
                    }
                    
                    throw ex;
                } finally {
                    closeEntityManager(em2);
                }
                
                commitTransaction(em);
            } catch (RollbackException exception) {
                if (exception.getCause() instanceof OptimisticLockException){
                    optimisticLockException = exception;
                }
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw ex;
            }
            
            beginTransaction(em);
            
            try {
                employee = em.find(Employee.class, employee.getId());
                em.remove(employee);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                throw ex;
            } finally {
                closeEntityManager(em);
            }
            
            assertFalse("Proper exception not thrown when EntityManager.lock(object, WRITE) is used.", optimisticLockException == null);
        }
    }
    
    public void testOPTIMISTICLock(){
        // Cannot create parallel entity managers in the server.
        if (! isOnServer()) {
            EntityManager em = createEntityManager();
            beginTransaction(em);
            Employee employee = null;
            
            try {
                employee = new Employee();
                employee.setFirstName("Harry");
                employee.setLastName("Madsen");
                em.persist(employee);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw ex;
            }
            
            EntityManager em2 = createEntityManager();
            Exception optimisticLockException = null;
            beginTransaction(em);
            
            try {
                employee = em.find(Employee.class, employee.getId());
                em.lock(employee, LockModeType.OPTIMISTIC);
                beginTransaction(em2);
                
                try {
                    Employee employee2 = em2.find(Employee.class, employee.getId());
                    employee2.setFirstName("Michael");
                    commitTransaction(em2);
                } catch (RuntimeException ex) {
                    if (isTransactionActive(em2)) {
                        rollbackTransaction(em2);
                    }
                    
                    throw ex;
                } finally {
                    closeEntityManager(em2);
                }
            
                try {
                    em.flush();
                } catch (PersistenceException exception) {
                    if (exception instanceof OptimisticLockException){
                        optimisticLockException = exception;
                    } else {
                        throw exception;
                    }
                }
                
                rollbackTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw ex;
            }
            
            try {
                beginTransaction(em);
                employee = em.find(Employee.class, employee.getId());
                em.remove(employee);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw ex;
            }
            
            assertFalse("Proper exception not thrown when EntityManager.lock(object, OPTIMISTIC) is used.", optimisticLockException == null);
        }
    }

    // This test issues a LOCK and a LOCK NOWAIT.
    public void testPESSIMISTIC_READLock() {
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateNoWaitSupported()) {
            EntityManager em = createEntityManager();
            Department dept = null;
            
            try {
                beginTransaction(em);
                dept = new Department();
                dept.setName("Pessimistic Department");
                em.persist(dept);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw ex;
            }
            
            Exception pessimisticLockException = null;
            
            try {
                beginTransaction(em);
                dept = em.find(Department.class, dept.getId());
                em.lock(dept, LockModeType.PESSIMISTIC_READ);
                dept.setName("New Pessimistic Department");
                
                EntityManager em2 = createEntityManager();
                
                try {
                    beginTransaction(em2);
                    Department dept2 = em2.find(Department.class, dept.getId());
                    HashMap properties = new HashMap();
                    // According to the spec a 0 indicates a NOWAIT clause.
                    properties.put(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 0);
                    em2.lock(dept2, LockModeType.PESSIMISTIC_READ, properties);
                } catch (PersistenceException ex) {
                    if (ex instanceof javax.persistence.PessimisticLockException) {
                        pessimisticLockException = ex;
                    } else {
                        throw ex;
                    } 
                } finally {
                    rollbackTransaction(em2);
                    closeEntityManager(em2);
                }
            
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                throw ex;
            } finally {
                closeEntityManager(em);
            }
            
            assertFalse("Proper exception not thrown when EntityManager.lock(object, PESSIMISTIC) is used.", pessimisticLockException == null);
        }
    }
    
    public void testPESSIMISTIC_WRITELock() {
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateNoWaitSupported()) {
            EntityManager em = createEntityManager();
            Department dept = null;
            
            try {
                beginTransaction(em);
                dept = new Department();
                dept.setName("Pessimistic Department");
                em.persist(dept);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                closeEntityManager(em);
                throw ex;
            }
            
            Exception pessimisticLockException = null;
            
            try {
                beginTransaction(em);
                dept = em.find(Department.class, dept.getId());
                em.lock(dept, LockModeType.PESSIMISTIC_WRITE);
                dept.setName("New Pessimistic Department");
                
                EntityManager em2 = createEntityManager();
                
                try {
                    beginTransaction(em2);
                    Department dept2 = em2.find(Department.class, dept.getId());
                    HashMap properties = new HashMap();
                    // According to the spec a 0 indicates a NOWAIT clause.
                    properties.put(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 0);
                    em2.lock(dept2, LockModeType.PESSIMISTIC_WRITE, properties);
                } catch (PersistenceException ex) {
                    if (ex instanceof javax.persistence.PessimisticLockException) {
                        pessimisticLockException = ex;
                    } else {
                        throw ex;
                    } 
                } finally {
                    rollbackTransaction(em2);
                    closeEntityManager(em2);
                }
            
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                throw ex;
            } finally {
                closeEntityManager(em);
            }
            
            assertFalse("Proper exception not thrown when EntityManager.lock(object, PESSIMISTIC) is used.", pessimisticLockException == null);
        }
    }
    
    public void testPESSIMISTIC_FORCE_INCREMENTLock() {
        if (isSelectForUpateSupported()) {
            Employee employee = null;
            Integer version1;

            EntityManager em = createEntityManager();
            
            try {
                beginTransaction(em);
                employee = new Employee();
                employee.setFirstName("Guillaume");
                employee.setLastName("Aujet");
                em.persist(employee);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
             
                closeEntityManager(em);
                throw ex;
            }
            
            version1 = employee.getVersion();
            
            try {
                beginTransaction(em);
                employee = em.find(Employee.class, employee.getId(), LockModeType.PESSIMISTIC_FORCE_INCREMENT);
                commitTransaction(em);
                
                assertTrue("The version was not updated on the pessimistic lock.", version1.intValue() < employee.getVersion().intValue());
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                throw ex;
            } finally {
                closeEntityManager(em);
            }
        }
    }
    
    public void testPESSIMISTIC_READLockWithNoChanges() {
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateSupported()) {             
            Employee employee = null;
            Integer version1;

            EntityManager em = createEntityManager();
            
            try {
                beginTransaction(em);
                employee = new Employee();
                employee.setFirstName("Black");
                employee.setLastName("Crappie");
                em.persist(employee);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
             
                closeEntityManager(em);
                throw ex;
            }
            
            version1 = employee.getVersion();
            
            try {
                beginTransaction(em);
                employee = em.find(Employee.class, employee.getId(), LockModeType.PESSIMISTIC_READ);
                commitTransaction(em);
                
                assertTrue("The version was updated on the pessimistic lock.", version1.intValue() == employee.getVersion().intValue());
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                throw ex;
            } finally {
                closeEntityManager(em);
            }
        }
    }
    
    public void testPESSIMISTIC_WRITELockWithNoChanges() {
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateSupported()) {
            Employee employee = null;
            Integer version1;
    
            EntityManager em = createEntityManager();
            
            try {
                beginTransaction(em);
                employee = new Employee();
                employee.setFirstName("Black");
                employee.setLastName("Crappie");
                em.persist(employee);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
             
                closeEntityManager(em);
                throw ex;
            }
            
            version1 = employee.getVersion();
            
            try {
                beginTransaction(em);
                employee = em.find(Employee.class, employee.getId(), LockModeType.PESSIMISTIC_WRITE);
                commitTransaction(em);
                
                assertTrue("The version was updated on the pessimistic lock.", version1.intValue() == employee.getVersion().intValue());
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                throw ex;
            } finally {
                closeEntityManager(em);
            }
        }
    }
    
    public void testPESSIMISTIC_READ_TIMEOUTLock() {
        ServerSession session = JUnitTestCase.getServerSession();
        
        // Cannot create parallel entity managers in the server.
        // Lock timeout is only supported on Oracle.
        if (! isOnServer() && session.getPlatform().isOracle()) {
            EntityManager em = createEntityManager();
            List result = em.createQuery("Select employee from Employee employee").getResultList();
            Employee employee = (Employee) result.get(0);
            Exception lockTimeOutException = null;
           
            try {
                beginTransaction(em);
                employee = em.find(Employee.class, employee.getId(), LockModeType.PESSIMISTIC_READ);
            
                EntityManager em2 = createEntityManager();
            
                try {
                    beginTransaction(em2);
                    
                    HashMap<String, Object> properties = new HashMap<String, Object>();
                    properties.put(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 5);
                    Employee employee2 = (Employee)em2.find(Employee.class, employee.getId(), LockModeType.PESSIMISTIC_READ, properties);
                    employee2.setFirstName("Invalid Lock Employee");
                    commitTransaction(em2);
                } catch (PersistenceException ex) {
                    if (isTransactionActive(em2)) {
                        rollbackTransaction(em2);
                    }
                    if (ex instanceof javax.persistence.LockTimeoutException) {
                        lockTimeOutException = ex;
                    } else {
                        throw ex;
                    } 
                } finally {
                    closeEntityManager(em2);
                }
                
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                throw ex;
            } finally {
                closeEntityManager(em);
            }
        
            assertFalse("Proper exception not thrown when Query with LockModeType.PESSIMISTIC is used.", lockTimeOutException == null);
        }
    }
    
    public void testPESSIMISTIC_WRITE_TIMEOUTLock() {
        ServerSession session = JUnitTestCase.getServerSession();
        
        // Cannot create parallel entity managers in the server.
        // Lock timeout is only supported on Oracle.
        if (! isOnServer() && session.getPlatform().isOracle()) {
            EntityManager em = createEntityManager();
            List result = em.createQuery("Select employee from Employee employee").getResultList();
            Employee employee = (Employee) result.get(0);
            Exception lockTimeOutException = null;
           
            try {
                beginTransaction(em);
                employee = em.find(Employee.class, employee.getId(), LockModeType.PESSIMISTIC_WRITE);
            
                EntityManager em2 = createEntityManager();
            
                try {
                    beginTransaction(em2);
                    
                    HashMap<String, Object> properties = new HashMap<String, Object>();
                    properties.put(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 5);
                    Employee employee2 = (Employee)em2.find(Employee.class, employee.getId(), LockModeType.PESSIMISTIC_WRITE, properties);
                    employee2.setFirstName("Invalid Lock Employee");
                    commitTransaction(em2);
                } catch (PersistenceException ex) {
                    if (isTransactionActive(em2)) {
                        rollbackTransaction(em2);
                    }
                    if (ex instanceof javax.persistence.LockTimeoutException) {
                        lockTimeOutException = ex;
                    } else {
                        throw ex;
                    } 
                } finally {
                    closeEntityManager(em2);
                }
                
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                throw ex;
            } finally {
                closeEntityManager(em);
            }
        
            assertFalse("Proper exception not thrown when Query with LockModeType.PESSIMISTIC is used.", lockTimeOutException == null);
        }
    }

    public void testPESSIMISTIC_FORCE_INCREMENTLockOnNonVersionedEntity() {
        Department dept = null;

        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            dept = new Department();
            em.persist(dept);
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        } 

        try {
            beginTransaction(em);
            dept = em.find(Department.class, dept.getId(), LockModeType.PESSIMISTIC_FORCE_INCREMENT);
            rollbackTransaction(em);
            fail("An Expected javax.persistence.PersistenceException was not thrown");
        } catch (PersistenceException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testLockWithJoinedInheritanceStrategy () throws InterruptedException {
        if(getServerSession().getPlatform().isMaxDB()) {
            // skip this test (bug 326799)
            return;
        }
        
        // Cannot create parallel entity managers in the server.
        if (! isOnServer() && isSelectForUpateSupported()) {
            Employee emp = null;
            LargeProject largeProject = null;

            EntityManager em = createEntityManager();
            
            try {
                beginTransaction(em);
                emp = new Employee();
                largeProject = new LargeProject();
                largeProject.setName("Large Project");
                largeProject.setBudget(50000);
                emp.addProject(largeProject);
                em.persist(emp);
                commitTransaction(em);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
                throw ex;
            }

            try {
                beginTransaction(em);
                emp = em.find(Employee.class, emp.getId());
                final Project lp1 = emp.getProjects().iterator().next();
                em.lock(lp1, LockModeType.PESSIMISTIC_WRITE);
                lp1.setName("Lock In Additional Table ");
                
                Runnable runnable = new Runnable() {

                    public void run() {
                        EntityManager em2 = createEntityManager();
                        
                        try {
                            beginTransaction(em2);
                            LargeProject lp2 = em2.find(LargeProject.class, lp1.getId());
                            HashMap properties = new HashMap();
                            // According to the spec a 0 indicates a NOWAIT clause.
                            properties.put(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 0);
                            em2.lock(lp2, LockModeType.PESSIMISTIC_WRITE, properties);
                        } catch (PersistenceException ex) {
                            if (!(ex instanceof javax.persistence.PessimisticLockException)) {
                                throw ex;
                            } 
                        } finally {
                            rollbackTransaction(em2);
                            closeEntityManager(em2);
                        }

                    }
                    
                };
                
                Thread t2 = new Thread(runnable);
                t2.start();
                
                Thread.sleep(2000);
                
                // t2 should have failed to get a lock with NOWAIT and hence should have finished by now
                boolean hanging = t2.isAlive();
                
                if (hanging) {
                    t2.interrupt();
                }
                
                commitTransaction(em);
                
                assertFalse("pessimistic lock with nowait on entity with joined inheritance causes concurrent thread to wait", hanging);
            } catch (RuntimeException ex) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                
                throw ex;
            } finally {
                closeEntityManager(em);
            }
            
        }
    }
    
    /*
     * Helper class for testPESSIMISTIC_ExtendedScope.
     * Kills transaction that is holding the test for too long.
     */
    class TransactionKiller extends Thread {
        EntityManager em;
        long timeToWait;
        boolean shouldKillTransaction = true;
        boolean isWaiting;
        boolean hasKilledTransaction;
        TransactionKiller(EntityManager em, long timeToWait) {
            this.em = em;
            this.timeToWait = timeToWait;
        }
        public void run() {
            try {
                isWaiting = true;
                Thread.sleep(timeToWait);
            } catch (InterruptedException ex) {
                throw new TestProblemException("TestProblem: TransactionKiller.run: wait failed: " + ex);
            } finally {
                isWaiting = false;
            }
            if (shouldKillTransaction && isTransactionActive(em)) {
                hasKilledTransaction = true;
                rollbackTransaction(em);
            }
        }
    }
    public void testPESSIMISTIC_ExtendedScope() {
        ServerSession session = JUnitTestCase.getServerSession();
        // Cannot create parallel entity managers in the server. Uses FOR UPDATE clause which SQLServer doesn't support.
        if (isOnServer() || !isSelectForUpateSupported() || !isPessimisticWriteLockSupported()) {
            return;
        }
            
        ServerSession ss = ((EntityManagerFactoryImpl)getEntityManagerFactory()).getServerSession();
        
        // If FOR UPDATE NOWAIT is not supported then FOR UPDATE is used - and that could lock the test (em2) for a long time (depending on db setting). 
        boolean shouldSpawnThread = !isSelectForUpateNoWaitSupported();
        // To avoid that a separate thread is spawned that - after waiting the specified time -
        // completes the locking transaction (em1) and therefore clears the way to em2 go ahead.
        long timeToWait = 1000;
        
        String errorMsg = "";
        LockModeType lockMode = LockModeType.PESSIMISTIC_WRITE;
        
        // create Employee with Projects and Responsibilities
        Employee emp = new Employee();
        emp.setFirstName("PESSIMISTIC");
        emp.setLastName("ExtendedScope");
        emp.addResponsibility("0");
        emp.addResponsibility("1");
        SmallProject smallProject = new SmallProject();
        smallProject.setName("SmallExtendedScope");
        emp.addProject(smallProject);
        LargeProject largeProject = new LargeProject();
        largeProject.setName("LargeExtendedScope");
        largeProject.setBudget(5000);
        emp.addProject(largeProject);            
        // persist
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            em.persist(emp);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }                
            closeEntityManager(em);
        }
        // cache ids
        int id = emp.getId();
        int smallProjId = smallProject.getId();
        
        clearCache();

        
        // properties to be passed to find, lock, refresh methods
        Map<String, Object> properties = new HashMap();
        properties.put(QueryHints.PESSIMISTIC_LOCK_SCOPE, PessimisticLockScope.EXTENDED);
        String forUpdateClause = session.getPlatform().getSelectForUpdateString();
        if(isSelectForUpateNoWaitSupported()) {
            properties.put(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 0);
            forUpdateClause = session.getPlatform().getSelectForUpdateNoWaitString();
        }
        String lockingClauseAfterWhereClause = "";
        String lockingClauseBeforeWhereClause = "";
        if(session.getPlatform().shouldPrintLockingClauseAfterWhereClause()) {
            lockingClauseAfterWhereClause = forUpdateClause;
        } else {
            lockingClauseBeforeWhereClause = forUpdateClause;
        }
                    
        // indicates whether the object to be locked is already in cache.
        boolean[] isObjectCached = {false, true};
        // indicates which method on the first entity manager is used to lock the object.
        String[] testModeArray1 = {"query", "find", "lock", "refresh"};
        // indicates which method on the second entity manager is used to test the lock. 
        String[] testModeArray2 = {"query", "find", "update_name", "update_salary", "remove_project", "remove_respons", "update_project", "update_respons", "lock", "refresh"};
/* test runs all combinations of elements of the above three arrays. To limit the number of configuration for debugging override these array, for instance:
        boolean[] isObjectCached = {false};
        String[] testModeArray1 = {"lock"};
        String[] testModeArray2 = {"find"};
*/
        // testMode1 loop
        for(int i=0; i < testModeArray1.length; i++) {
            String testMode1 = testModeArray1[i];            
            // isObjectCached loop
            for(int k=0; k < isObjectCached.length; k++) {
                boolean isObjCached = isObjectCached[k];
                // testMode2 loop
                for(int j=0; j < testModeArray2.length; j++) {
                    String testMode2 = testModeArray2[j];
                    boolean isExceptionExpected = !testMode2.equals("update_project");
                    
                    // lock emp using em1
                    EntityManager em1= createEntityManager();       
                    // bring object into cache if required
                    if(isObjCached) {
                        ss.log(SessionLog.FINEST, SessionLog.QUERY, "testPESSIMISTIC_ExtendedScope: bring object into cache", (Object[])null, null, false);
                        em1.find(Employee.class, id);
                    }                    
                    Employee emp1;
                    try {
                        beginTransaction(em1);

                        ss.log(SessionLog.FINEST, SessionLog.QUERY, "testPESSIMISTIC_ExtendedScope: testMode1 = " + testMode1, (Object[])null, null, false);
                        
                        if(testMode1.equals("query")) {
                            Query query1 = em1.createQuery("SELECT emp FROM Employee emp WHERE emp.id = "+id).setLockMode(lockMode).
                                    setHint(QueryHints.PESSIMISTIC_LOCK_SCOPE, PessimisticLockScope.EXTENDED);
                            if(isSelectForUpateNoWaitSupported()) {                
                                query1.setHint(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 0);
                            }
                            emp1 = (Employee)query1.getSingleResult();
                        } else if(testMode1.equals("find")) {
                            emp1 = em1.find(Employee.class, id, lockMode, properties);
                        } else {
                            emp1 = em1.find(Employee.class, id);
                            if(testMode1.equals("lock")) {
                                em1.lock(emp1, lockMode, properties);
                            } else if(testMode1.equals("refresh")) {
                                em1.refresh(emp1, lockMode, properties);
                            } else {
                                fail("Unknown testMode1 = " + testMode1);
                            }
                        }
                    
                        TransactionKiller transactionKiller = null;
                        // try to update emp using em2
                        EntityManager em2 = createEntityManager();
                        Employee emp2;
                        try {
                            beginTransaction(em2);
                            
                            ss.log(SessionLog.FINEST, SessionLog.QUERY, "testPESSIMISTIC_ExtendedScope: testMode2 = " + testMode2, (Object[])null, null, false);
                            
                            if(shouldSpawnThread) {
                                // after waiting TransactionKiller rollback em1 transaction unlocking way for em2 to proceed.
                                // the test assumes that em2 waiting for timeToWait means em2 waiting on the lock acquired by em1.
                                transactionKiller = new TransactionKiller(em1, timeToWait);
                                transactionKiller.start();
                            }
            
                            if(testMode2.equals("query")) {
                                Query query2 = em2.createQuery("SELECT emp FROM Employee emp WHERE emp.id = "+id).setLockMode(lockMode).
                                setHint(QueryHints.PESSIMISTIC_LOCK_SCOPE, PessimisticLockScope.EXTENDED);
                                if(isSelectForUpateNoWaitSupported()) {                
                                    query2.setHint(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, 0);
                                }
                                emp2 = (Employee)query2.getSingleResult();
                            } else if(testMode2.equals("find")) {
                                emp2 = em2.find(Employee.class, id, lockMode, properties);
                            } else if(testMode2.equals("update_name")) {
                                em2.createNativeQuery("SELECT L_NAME FROM CMP3_EMPLOYEE"+lockingClauseBeforeWhereClause+" WHERE EMP_ID = "+id+lockingClauseAfterWhereClause).getSingleResult();
//                                em2.createNativeQuery("UPDATE CMP3_EMPLOYEE SET L_NAME = 'NEW' WHERE EMP_ID = "+id).executeUpdate();
                            } else if(testMode2.equals("update_salary")) {
                                em2.createNativeQuery("SELECT SALARY FROM CMP3_SALARY"+lockingClauseBeforeWhereClause+" WHERE EMP_ID = "+id+lockingClauseAfterWhereClause).getSingleResult();
//                                em2.createNativeQuery("UPDATE CMP3_SALARY SET SALARY = 1000 WHERE EMP_ID = "+id).executeUpdate();
                            } else if(testMode2.equals("remove_project")) {
                                em2.createNativeQuery("SELECT PROJECTS_PROJ_ID FROM CMP3_EMP_PROJ"+lockingClauseBeforeWhereClause+" WHERE EMPLOYEES_EMP_ID = "+id+lockingClauseAfterWhereClause).getResultList();
//                                em2.createNativeQuery("DELETE FROM CMP3_EMP_PROJ WHERE EMPLOYEES_EMP_ID = "+id).executeUpdate();
                            } else if(testMode2.equals("remove_respons")) {
                                em2.createNativeQuery("SELECT EMP_ID FROM CMP3_RESPONS"+lockingClauseBeforeWhereClause+" WHERE EMP_ID = "+id+lockingClauseAfterWhereClause).getResultList();
//                                em2.createNativeQuery("DELETE FROM CMP3_RESPONS WHERE EMP_ID = "+id).executeUpdate();
                            } else if(testMode2.equals("update_project")) {
                                em2.createNativeQuery("SELECT PROJ_NAME FROM CMP3_PROJECT"+lockingClauseBeforeWhereClause+" WHERE PROJ_ID = "+smallProjId+lockingClauseAfterWhereClause).getSingleResult();
//                                em2.createNativeQuery("UPDATE CMP3_PROJECT SET PROJ_NAME = 'NEW' WHERE PROJ_ID = "+smallProjId).executeUpdate();
                            } else if(testMode2.equals("update_respons")) {
                                em2.createNativeQuery("SELECT DESCRIPTION FROM CMP3_RESPONS"+lockingClauseBeforeWhereClause+" WHERE EMP_ID = "+id+lockingClauseAfterWhereClause).getResultList();
//                                em2.createNativeQuery("UPDATE CMP3_RESPONS SET DESCRIPTION = 'NEW' WHERE EMP_ID = "+id).executeUpdate();
                            } else {
                                emp2 = em2.find(Employee.class, id);
                                if(testMode2.equals("lock")) {
                                    em2.lock(emp2, lockMode, properties);
                                } else if(testMode2.equals("refresh")) {
                                    em2.refresh(emp2, lockMode, properties);
                                } else {
                                    fail("Unknown testMode2 = " + testMode2);
                                }
                            }
                            
            //                commitTransaction(em2);
                            boolean hasKilledTransaction = false;
                            if(transactionKiller != null) {
                                transactionKiller.shouldKillTransaction = false;
                                try {
                                    transactionKiller.join();
                                } catch(InterruptedException intEx) {
                                    // Ignore
                                }
                                hasKilledTransaction = transactionKiller.hasKilledTransaction;
                            }
                            // transaction killed by TransactionKiller is treated as PessimisticLockException 
                            if(isExceptionExpected && !hasKilledTransaction) {
                                String localErrorMsg = testMode1 + (isObjCached ? " cached " : " ") + testMode2 + ": Exception was expected."; 
                                ss.log(SessionLog.FINEST, SessionLog.QUERY, localErrorMsg, (Object[])null, null, false);
                                errorMsg += '\n' + localErrorMsg; 
                            }
                        } catch (Exception ex) {
                            if(transactionKiller != null) {
                                transactionKiller.shouldKillTransaction = false;
                                try {
                                    transactionKiller.join();
                                } catch(InterruptedException intEx) {
                                    // Ignore
                                }
                            }
                            if(!isExceptionExpected) {
                                String localErrorMsg = testMode1 + (isObjCached ? " cached " : " ") + testMode2 + ": Unexpected exception: " + ex.getMessage();
                                ss.log(SessionLog.FINEST, SessionLog.QUERY, localErrorMsg, (Object[])null, null, false);
                                errorMsg += '\n' + localErrorMsg;
                            }
                        } finally {
                            if (isTransactionActive(em2)) {
                                rollbackTransaction(em2);
                            }
                            closeEntityManager(em2);
                        }
                        
            //            commitTransaction(em1);
                    } finally {
                        if (isTransactionActive(em1)) {
                            rollbackTransaction(em1);
                        }
                        closeEntityManager(em1);
                    }
                    clearCache();
                }  // testModel2 loop
            }  // isObjectCached loop
        }  // testMode1 loop
        
        // clean up
        em = createEntityManager();
        emp = em.find(Employee.class, id);
        try {
            beginTransaction(em);
            Iterator<Project> it = emp.getProjects().iterator(); 
            while(it.hasNext()) {
                Project project = it.next();
                it.remove();
                em.remove(project);
            }
            em.remove(emp);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }                
            closeEntityManager(em);
        }
        
        if(errorMsg.length() > 0) {
            fail(errorMsg);
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

    public void testDetachManagedObject() {
        EntityManager em = createEntityManager();

        // create test data
        beginTransaction(em);
        Employee emp = new Employee();
        emp.setFirstName("beforePersist");
        em.persist(emp);
        Integer id = emp.getId();
        commitTransaction(em);
    
        // Test that 'detach()' removes 'emp' from the persistence context
        beginTransaction(em);
        em.detach(emp);
        assertFalse("could not detach managed object", em.contains(emp));
    
        // clean up 
        emp = em.find(Employee.class, id);
        em.remove(emp);
        commitTransaction(em);
    }

    public void testCascadeDetach() {
        EntityManager em = createEntityManager();
        Employee emp = (Employee)em.createQuery("Select e from Employee e where e.managedEmployees is not empty").getResultList().get(0);
        emp.getManagedEmployees().size();
        em.detach(emp);
        assertFalse("Did not cascade detach", em.contains(emp.getManagedEmployees().iterator().next()));
    }

    //detaching an non-managed object should not throw any exception.
    public void testDetachNonManagedObject() {
        EntityManager em = createEntityManager();
    
        // Create test data
        beginTransaction(em);
        Employee emp = new Employee();
        emp.setFirstName("beforePersist");
        boolean caughtException = false;
    
        // detach the object
        em.detach(emp);
        em.persist(emp);
    
        // Deleting the object
        em.remove(emp);
        commitTransaction(em);
        assertFalse("Cannot_detach_Object Exception was thrown for a non-managed Entity", caughtException);
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

    // Test that deleting an employee works correctly.
    public void testDeleteEmployee() {        
        Employee employee = new Employee();
        employee.addPhoneNumber(new PhoneNumber("home", "123", "4567"));
        employee.addPhoneNumber(new PhoneNumber("fax", "456", "4567"));
        employee.addResponsibility("work hard");
        employee.addResponsibility("write code");
        employee.addProject(new Project());
        employee.setWorkWeek(new HashSet<Employee.Weekdays>());
        employee.getWorkWeek().add(Employee.Weekdays.MONDAY);
        employee.getWorkWeek().add(Employee.Weekdays.TUESDAY);
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            em.persist(employee);
            commitTransaction(em);
            closeEntityManager(em);
            clearCache();
            em = createEntityManager();
            beginTransaction(em);
            employee = em.find(Employee.class, employee.getId());
            counter.getSqlStatements().clear();
            em.remove(employee);
            commitTransaction(em);
            if (isWeavingEnabled() && counter.getSqlStatements().size() > 13) {
                fail("Only 13 sql statements should have occured:" + counter.getSqlStatements().size());
            }
            beginTransaction(em);    
            verifyDelete(employee);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
    
    /**
     *     Test that deleting an employee works correctly.
     *     This test case was added in 8177 for 324321 and modified for 325605.
     *     The issue is that the status enum will be cascade deleted even if it is not
     *     instantiated (lazy) because the owning object does not know if the collection is empty
     *     without instantiating it.
     *     DB2 will therefore emit warning logs that are printed at FINEST in this lazy case.
     *     This test is a modification of testDeleteEmployee() that verifies instantiated lists are also ok
     *     
     *     11/17/2010-2.2 Michael O'Brien 
     *       - 325605: Filter out SQL warnings that are not SQL statements but are 
     *       logged at a non-warning level.  This affects only implementors of SessionLog that
     *       perform log diagnostics/tracking in addition to logging.
     */
    public void testDeleteEmployee_with_status_enum_collection_instantiated() {        
        Employee employee = new Employee();
        PhoneNumber homePhone = new PhoneNumber("home", "123", "4567");
        PhoneNumber faxPhone = new PhoneNumber("fax", "456", "4567");
        employee.addPhoneNumber(homePhone);
        employee.addPhoneNumber(faxPhone);
        employee.addResponsibility("work hard"); 
        employee.addResponsibility("write code");
        employee.addProject(new Project());
        employee.setWorkWeek(new HashSet<Employee.Weekdays>());
        employee.getWorkWeek().add(Employee.Weekdays.MONDAY);
        employee.getWorkWeek().add(Employee.Weekdays.TUESDAY);
        // set enums
        employee.setStatus(Employee.EmployeeStatus.PART_TIME); // enum index is 1
        // set enum on 1 of the 2 phones, leave the other Collection of enums unset - but do a later find to instantiate the Collection
        homePhone.addStatus(PhoneNumber.PhoneStatus.ASSIGNED);
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            em.persist(employee);
            commitTransaction(em);
            closeEntityManager(em);
            clearCache();
            em = createEntityManager();
            beginTransaction(em);
            employee = em.find(Employee.class, employee.getId());
            // instantiate the empty Collection of enums to verify we do not cascade delete if we "know" the Collection is empty
            employee.getPhoneNumbers();
            counter.getSqlStatements().clear();
            em.remove(employee);
            commitTransaction(em);
            // We do not count any SQL warnings that may occur (DB2 may have 3) at the FINEST level
            if (isWeavingEnabled() && counter.getSqlStatements().size() > 13) {
                fail("Only 13 sql statements should have occured: " + counter.getSqlStatements().size());
            }
            beginTransaction(em);    
            verifyDelete(employee);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
        
    // Test that deleting an Man works correctly.
    public void testDeleteMan() {
        EntityManager em = createEntityManager();
        QuerySQLTracker counter = null;
        try {
            beginTransaction(em);
            Man man = new Man();
            em.persist(man);
            Woman woman = new Woman();
            em.persist(woman);
            PartnerLink link = new PartnerLink();
            em.persist(link);
            man.setPartnerLink(link);
            link.setMan(man);
            woman.setPartnerLink(link);
            link.setWoman(woman);                        
            commitTransaction(em);
            closeEntityManager(em);
            clearCache();
            counter = new QuerySQLTracker(getServerSession());
            em = createEntityManager();
            beginTransaction(em);
            // Count SQL.
            man = em.find(Man.class, man.getId());
            woman = em.find(Woman.class, woman.getId());
            woman.setPartnerLink(null);
            counter.getSqlStatements().clear();
            em.remove(man);
            commitTransaction(em);
            if (counter.getSqlStatements().size() > 2) {
                fail("Only 2 delete should have occured.");
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

    public void testFindWithProperties(){
        Employee employee = new Employee();
        employee.setFirstName("Marc");
        HashMap<String, Object> queryhints=new  HashMap<String, Object>();
        EntityManager em = createEntityManager();
        try {
          beginTransaction(em);
          em.persist(employee);
          commitTransaction(em);
          beginTransaction(em);
          int empId=employee.getId();
          Employee e1=em.find(Employee.class,empId);
          e1.setFirstName("testfind");
          queryhints.put(QueryHints.REFRESH, "TRUE");
          Employee e2= (Employee)em.find(Employee.class,empId ,queryhints);
          assertFalse(e2.getFirstName().equals("testfind"));
          commitTransaction(em);
        } catch (IllegalArgumentException iae) {
            return;
        } catch (Exception e) {
            fail("Wrong exception type thrown: " + e.getClass());
        }finally{
            closeEntityManager(em);
        }
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

    // Test using PERSISTENCE_CONTEXT_CLOSE_ON_COMMIT option to avoid the resume on commit.
    public void testCloseOnCommit() {
        // Properties only works in jse.
        if (isOnServer()) {
            return;
        }
        Map properties = new HashMap();
        properties.put(PersistenceUnitProperties.PERSISTENCE_CONTEXT_CLOSE_ON_COMMIT, "true");
        EntityManager em = createEntityManager(properties);
        beginTransaction(em);
        try {
            Employee emp = new Employee();
            emp.setFirstName("Douglas");
            emp.setLastName("McRae");
            em.persist(emp);
            commitTransaction(em);
            verifyObjectInCacheAndDatabase(emp);
            closeEntityManager(em);
            em = createEntityManager(properties);
            beginTransaction(em);
            emp = em.find(Employee.class, emp.getId());
            emp.setFirstName("Joe");
            commitTransaction(em);
            verifyObjectInCacheAndDatabase(emp);
            em = createEntityManager(properties);
            beginTransaction(em);
            emp = em.find(Employee.class, emp.getId());
            em.remove(emp);
            commitTransaction(em);
            closeEntityManager(em);            
        } catch (RuntimeException exception) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw exception;
        }
    }

    // Test using PERSISTENCE_CONTEXT_PERSIST_ON_COMMIT option to avoid the discover on commit.
    public void testPersistOnCommit() {
        // Properties only works in jse.
        if (isOnServer()) {
            return;
        }
        Map properties = new HashMap();
        properties.put(PersistenceUnitProperties.PERSISTENCE_CONTEXT_PERSIST_ON_COMMIT, "false");
        EntityManager em = createEntityManager(properties);
        beginTransaction(em);
        try {
            Employee emp = new Employee();
            emp.setFirstName("Douglas");
            emp.setLastName("McRae");
            em.persist(emp);
            commitTransaction(em);
            verifyObjectInCacheAndDatabase(emp);
            closeEntityManager(em);
            em = createEntityManager(properties);
            beginTransaction(em);
            Address address = new Address();
            emp = em.find(Employee.class, emp.getId());
            emp.setAddress(address);
            em.persist(address);
            commitTransaction(em);
            verifyObjectInCacheAndDatabase(emp);
            em = createEntityManager(properties);
            beginTransaction(em);
            emp = em.find(Employee.class, emp.getId());
            em.remove(emp);
            commitTransaction(em);
            closeEntityManager(em);            
        } catch (RuntimeException exception) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw exception;
        }
    }

    // Test Not using the persist operation on commit.
    public void testNoPersistOnCommit() {
        // Properties only works in jse.
        if (isOnServer()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee employee = new Employee();
        employee.setLastName("SomeName");
        Address addr = new Address();
        addr.setCity("Douglas");
        try {
            em.persist(employee);
            commitTransaction(em);
            verifyObjectInCacheAndDatabase(employee);
            closeEntityManager(em);

            em = createEntityManager();
            beginTransaction(em);
            ((RepeatableWriteUnitOfWork)JpaHelper.getEntityManager(em).getUnitOfWork()).setDiscoverUnregisteredNewObjectsWithoutPersist(true);
            employee = em.find(Employee.class, employee.getId());
            employee.setAddress(addr);
            addr.getEmployees().add(employee);
            commitTransaction(em);
            verifyObjectInCacheAndDatabase(employee);
            closeEntityManager(em);

            em = createEntityManager();
            clearCache();
            beginTransaction(em);
            ((RepeatableWriteUnitOfWork)JpaHelper.getEntityManager(em).getUnitOfWork()).setDiscoverUnregisteredNewObjectsWithoutPersist(true);
            employee = em.find(Employee.class, employee.getId());
            employee.getAddress().setCountry("country");
            employee.getAddress().getEmployees().size();
            employee.setAddress((Address)null);
            em.remove(employee);
            commitTransaction(em);

            em = createEntityManager();
            beginTransaction(em);
            employee = em.find(Employee.class, employee.getId());
            assertNull("Employee Not Deleted", employee);
            commitTransaction(em);
            closeEntityManager(em);
        } catch (RuntimeException exception) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw exception;
        } finally {
            try {
                em = createEntityManager();
                clearCache();
                beginTransaction(em);
                em.remove(em.find(Address.class, addr.getID()));
                commitTransaction(em);
            } catch (RuntimeException ex) {
                // ignore

            }
        }
    }

    // Test Not using the persist operation on commit.
    public void testNoPersistOnCommitProperties() {
        // Properties only works in jse.
        if (isOnServer()) {
            return;
        }
        Map properties = new HashMap();
        properties.putAll(JUnitTestCaseHelper.getDatabaseProperties());
        properties.put(EntityManagerProperties.PERSISTENCE_CONTEXT_COMMIT_WITHOUT_PERSIST_RULES, "true");
        
        EntityManager em = createEntityManager(properties);
        beginTransaction(em);
        Employee employee = new Employee();
        employee.setLastName("SomeName");
        Address addr = new Address();
        addr.setCity("Douglas");
        try {
            em.persist(employee);
            commitTransaction(em);
            verifyObjectInCacheAndDatabase(employee);
            closeEntityManager(em);

            em = createEntityManager(properties);
            beginTransaction(em);
            employee = em.find(Employee.class, employee.getId());
            employee.setAddress(addr);
            addr.getEmployees().add(employee);
            commitTransaction(em);
            verifyObjectInCacheAndDatabase(employee);
            closeEntityManager(em);

            em = createEntityManager(properties);
            clearCache();
            beginTransaction(em);
            employee = em.find(Employee.class, employee.getId());
            employee.getAddress().setCountry("country");
            employee.getAddress().getEmployees().size();
            employee.setAddress((Address)null);
            em.remove(employee);
            commitTransaction(em);

            em = createEntityManager(properties);
            beginTransaction(em);
            employee = em.find(Employee.class, employee.getId());
            assertNull("Employee Not Deleted", employee);
            commitTransaction(em);
            closeEntityManager(em);
        } catch (RuntimeException exception) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw exception;
        } finally {
            try {
                em = createEntityManager();
                clearCache();
                beginTransaction(em);
                em.remove(em.find(Address.class, addr.getID()));
                commitTransaction(em);
            } catch (RuntimeException ex) {
                // ignore

            }
        }
    }

    // Test Not using the persist operation on commit.
    public void testNoPersistOnFlushProperties() {
        // Properties only works in jse.
        if (isOnServer()) {
            return;
        }
        Map properties = new HashMap();
        properties.putAll(JUnitTestCaseHelper.getDatabaseProperties());
        properties.put(EntityManagerProperties.PERSISTENCE_CONTEXT_COMMIT_WITHOUT_PERSIST_RULES, "true");
        
        EntityManager em = createEntityManager(properties);
        beginTransaction(em);
        Employee employee = new Employee();
        employee.setLastName("SomeName");
        Address addr = new Address();
        addr.setCity("Douglas");
        try {
            em.persist(employee);
            em.flush();
            commitTransaction(em);
            verifyObjectInCacheAndDatabase(employee);
            closeEntityManager(em);

            em = createEntityManager(properties);
            beginTransaction(em);
            employee = em.find(Employee.class, employee.getId());
            employee.setAddress(addr);
            addr.getEmployees().add(employee);
            em.flush();
            commitTransaction(em);
            verifyObjectInCacheAndDatabase(employee);
            closeEntityManager(em);

            em = createEntityManager(properties);
            clearCache();
            beginTransaction(em);
            employee = em.find(Employee.class, employee.getId());
            employee.getAddress().setCountry("country");
            employee.getAddress().getEmployees().size();
            employee.setAddress((Address)null);
            em.remove(employee);
            commitTransaction(em);

            em = createEntityManager(properties);
            beginTransaction(em);
            employee = em.find(Employee.class, employee.getId());
            assertNull("Employee Not Deleted", employee);
            commitTransaction(em);
            closeEntityManager(em);
        } catch (RuntimeException exception) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw exception;
        } finally {
            try {
                em = createEntityManager();
                clearCache();
                beginTransaction(em);
                em.remove(em.find(Address.class, addr.getID()));
                commitTransaction(em);
            } catch (RuntimeException ex) {
                // ignore

            }
        }
    }

    // Test using PERSISTENCE_CONTEXT_FLUSH_MODE option.
    public void testFlushMode() {
        // Properties only works in jse.
        if (isOnServer()) {
            return;
        }
        Map properties = new HashMap();
        properties.put(PersistenceUnitProperties.PERSISTENCE_CONTEXT_FLUSH_MODE, "COMMIT");
        EntityManager em = createEntityManager(properties);
        beginTransaction(em);
        try {
            Employee emp = new Employee();
            emp.setFirstName("testFlushMode");
            emp.setLastName("McRae");
            em.persist(emp);
            Query query = em.createQuery("Select e from Employee e where e.firstName = 'testFlushMode'");
            if (query.getResultList().size() > 0) {
                fail("Query triggered flush.");
            }
            rollbackTransaction(em);
            closeEntityManager(em);            
        } catch (RuntimeException exception) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw exception;
        }
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
        beginTransaction(localEm);
        Employee emp = null;
        String originalName = "";
        boolean cleared, updated, reset = false;
        try{
            Query query = localEm.createQuery("Select e FROM Employee e where e.firstName is not null");
            emp = (Employee)query.getResultList().get(0);
            originalName = emp.getFirstName();
            emp.setFirstName("Bobster");
            localEm.flush();
            localEm.clear();
            //this test is testing the cache not the database
            commitTransaction(localEm);
            cleared = !localEm.contains(emp);
            emp = localEm.find(Employee.class, emp.getId());
            updated = emp.getFirstName().equals("Bobster");
            closeEntityManager(localEm);
        } catch (RuntimeException exception) {
            if (isTransactionActive(localEm)) {
                rollbackTransaction(localEm);
            }
            closeEntityManager(localEm);
            throw exception;
        } finally {
            //now clean up
            localEm = createEntityManager();
            beginTransaction(localEm);
            emp = localEm.find(Employee.class, emp.getId());
            emp.setFirstName(originalName);
            commitTransaction(localEm);
            emp = localEm.find(Employee.class, emp.getId());
            reset = emp.getFirstName().equals(originalName);
            closeEntityManager(localEm);
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
        if (shouldUpdateAll && (JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("A testReadTransactionIsolation test skipped for this platform, "
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
        closeEntityManager(em2);
        
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
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testFindDeleteAllPersist skipped for this platform, "
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
        try {
            Employee empWithAddressFound = em.find(Employee.class, empWithAddress.getId());
            empWithAddressFound.toString();
            Employee empWithoutAddressFound = em.find(Employee.class, empWithoutAddress.getId());
            empWithoutAddressFound.toString();
            em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"' and e.address IS NULL").executeUpdate();
            commitTransaction(em);
        } catch (RuntimeException ex){
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
        em.createQuery("DELETE FROM Employee e WHERE e.firstName = '"+firstName+"'").executeUpdate();
        commitTransaction(em);
        closeEntityManager(em);
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
    
    /*This test case uses the "broken-PU" PU defined in the persistence.xml 
    located at eclipselink.jpa.test/resource/eclipselink-validation-failed-model/ 
    and included in eclipselink-validation-failed-model.jar */
    
    public void testEMFWrapValidationException() 
    {
        // Comment out because it's not relevant for getDatabaseProperties() when running in server
        if (isOnServer()) {
            return;
        }
        EntityManagerFactory factory = null;
        try {
            factory = Persistence.createEntityManagerFactory("broken-PU", JUnitTestCaseHelper.getDatabaseProperties());
            EntityManager em = factory.createEntityManager();
            em.close();
        } catch (javax.persistence.PersistenceException e)  {
            ArrayList expectedExceptions = new ArrayList();
            expectedExceptions.add(48);
            expectedExceptions.add(48);
            IntegrityException integrityException = (IntegrityException)e.getCause();
            Iterator i = integrityException.getIntegrityChecker().getCaughtExceptions().iterator();
            while (i.hasNext()){
                expectedExceptions.remove((Object)((EclipseLinkException)i.next()).getErrorCode());
            }
            if (expectedExceptions.size() > 0){
                fail("Not all expected exceptions were caught");
            }
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
        // Comment out because it's not relevant for getDatabaseProperties() when running in server
        if (isOnServer()) {
            return;
        }
        EntityManagerFactory factory = null;
        try {
            factory = Persistence.createEntityManagerFactory("default1", JUnitTestCaseHelper.getDatabaseProperties());
            EntityManager em = factory.createEntityManager();
            em.close();
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

    //detach(nonentity) throws illegalArgumentException
    public void testDetachNonEntity() {
        boolean testPass = false;
        Object nonEntity = new Object();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.detach(nonEntity);
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
        Employee persistedEmployee = null;
        try {
            persistedEmployee = (Employee)em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.firstName = '"+firstName+"'").getSingleResult();
            persistedEmployee.toString();
        } catch (RuntimeException runtimeException) {
            exception = runtimeException;
        }

        // clean up
        beginTransaction(em);
        em.remove(persistedEmployee);
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
        
        int writeInitial = ss.getDefaultConnectionPool().getInitialNumberOfConnections();
        if(writeInitial != Integer.parseInt((String)JUnitTestCaseHelper.propertiesMap.get(PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_INITIAL))) {
            fail("writeInitial is wrong");
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
        
        int batchSize = ss.getPlatform().getMaxBatchWritingSize();
        if (batchSize != Integer.parseInt((String)JUnitTestCaseHelper.propertiesMap.get(PersistenceUnitProperties.BATCH_WRITING_SIZE))) {
            fail("batchSize is wrong");
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
            <property name="eclipselink.descriptor.customizer.Project" value="org.eclipse.persistence.testing.models.jpa.advanced.Customizer"/>
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
        
        // Department cache size specified in @Cache annotation - that should override default property
        int departmentCacheSize = ss.getDescriptor(Department.class).getIdentityMapSize();
        if(departmentCacheSize != 777) {
            fail("departmentCacheSize is wrong");
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
        
        // Department cache type specified in @Cache annotation - that should override default property
        Class departmentCacheType = ss.getDescriptor(Department.class).getIdentityMapClass();
        if(! Helper.getShortClassName(departmentCacheType).equals("SoftCacheWeakIdentityMap")) {
            fail("departmentCacheType is wrong");
        }
        
        int numSessionCalls = Customizer.getNumberOfCallsForSession(ss.getName());
        if(numSessionCalls == 0) {
            fail("session customizer hasn't been called");
        }
        
        int numProjectCalls = Customizer.getNumberOfCallsForClass(Project.class.getName());
        if(numProjectCalls == 0) {
            fail("Project customizer hasn't been called");
        }
        
        int numEmployeeCalls = Customizer.getNumberOfCallsForClass(Employee.class.getName());
        if(numEmployeeCalls == 0) {
            fail("Employee customizer hasn't been called");
        }
        
        int numAddressCalls = Customizer.getNumberOfCallsForClass(Address.class.getName());
        if(numAddressCalls == 0) {
            fail("Address customizer hasn't been called");
        }
        
        IdValidation employeeIdValidation = ss.getDescriptor(Employee.class).getIdValidation();
        if(employeeIdValidation != IdValidation.ZERO) {
            fail("employeeIdValidation is wrong, IdValidation.ZERO assigned through PrimaryKey annotation was expected");
        }
        
        IdValidation addressIdValidation = ss.getDescriptor(Address.class).getIdValidation();
        if(addressIdValidation != IdValidation.NEGATIVE) {
            fail("addressIdValidation is wrong, IdValidation.NEGATIVE set as a default value in persistence.xml was expected");
        }
        
        closeEntityManager(em);
    }

    public void testGetLockModeType() {
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testGetLockModeType skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        if (isSelectForUpateSupported()) {
            EntityManager em = createEntityManager();
            try {
                beginTransaction(em);
                Employee emp = new Employee();
                Employee emp1 = new Employee();
                Employee emp2 = new Employee();
                Employee emp3 = new Employee();
                Employee emp4 = new Employee();
                Employee emp5 = new Employee();
                Employee emp6= new Employee();
                Employee emp7= new Employee();
                emp.setFirstName("Douglas");
                emp.setLastName("McRae");
                emp1.setFirstName("kaul");
                emp1.setLastName("Jeet");
                emp2.setFirstName("Schwatz");
                emp2.setLastName("Jonathan");
                emp3.setFirstName("Anil");
                emp3.setLastName("Gadre");
                emp4.setFirstName("Anil");
                emp4.setLastName("Gaur");
                emp5.setFirstName("Eliot");
                emp5.setLastName("Morrison");
                emp6.setFirstName("Edward");
                emp6.setLastName("Bratt");
                emp7.setFirstName("TJ");
                emp7.setLastName("Thomas");
                em.persist(emp);
                em.persist(emp1);
                em.persist(emp2);
                em.persist(emp3);
                em.persist(emp4);
                em.persist(emp5);
                em.persist(emp6);
                em.persist(emp7);
                commitTransaction(em);
                beginTransaction(em);
                emp = em.find(Employee.class, emp.getId());
                emp1 = em.find(Employee.class, emp1.getId());
                emp2 = em.find(Employee.class, emp2.getId());
                emp3 = em.find(Employee.class, emp3.getId());
                emp4 = em.find(Employee.class, emp4.getId());
                emp5 = em.find(Employee.class, emp5.getId());
                emp6 = em.find(Employee.class, emp6.getId());
                emp7 = em.find(Employee.class, emp7.getId());
                em.lock(emp, LockModeType.OPTIMISTIC);
                LockModeType lt = em.getLockMode(emp);
                em.lock(emp1, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                LockModeType lt1 = em.getLockMode(emp1);
                em.lock(emp2, LockModeType.PESSIMISTIC_READ);
                LockModeType lt2 = em.getLockMode(emp2);
                em.lock(emp3, LockModeType.PESSIMISTIC_WRITE);
                LockModeType lt3 = em.getLockMode(emp3);
                em.lock(emp4, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
                LockModeType lt4 = em.getLockMode(emp4);
                em.lock(emp5, LockModeType.READ);
                LockModeType lt5 = em.getLockMode(emp5);
                em.lock(emp6, LockModeType.WRITE);
                LockModeType lt6 = em.getLockMode(emp6);
                em.lock(emp7, LockModeType.NONE);
                LockModeType lt7 = em.getLockMode(emp7);
                assertEquals("Did not return correct LockModeType", LockModeType.OPTIMISTIC, lt);
                assertEquals("Did not return correct LockModeType", LockModeType.OPTIMISTIC_FORCE_INCREMENT, lt1);
    
                // Note: On some databases EclipseLink automatically upgrade LockModeType to PESSIMSITIC_WRITE
                assertTrue("Did not return correct LockModeType", lt2 == LockModeType.PESSIMISTIC_WRITE || lt2 == LockModeType.PESSIMISTIC_READ);
                
                assertEquals("Did not return correct LockModeType", LockModeType.PESSIMISTIC_WRITE, lt3);
                assertEquals("Did not return correct LockModeType", LockModeType.PESSIMISTIC_FORCE_INCREMENT, lt4);
                assertEquals("Did not return correct LockModeType", LockModeType.OPTIMISTIC, lt5);
                assertEquals("Did not return correct LockModeType", LockModeType.OPTIMISTIC_FORCE_INCREMENT, lt6);
                assertEquals("Did not return correct LockModeType", LockModeType.NONE, lt7);
            } catch (UnsupportedOperationException use) {
                return;
            } finally {
                rollbackTransaction(em);
                closeEntityManager(em);
            }
        }
    }

    //'getProperties()' returns map that throws exception when tried to modify.
    public void testGetProperties() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Map m1 = em.getProperties();
            m1.remove("eclipselink.weaving");
        } catch (UnsupportedOperationException use) {
            return;
        } catch (Exception e) {
            fail("Wrong exception type thrown: " + e.getClass());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        fail("No exception thrown when entityManager's properties are attempted to change.");
    }

    public void testUOWReferenceInExpressionCache() {
        // Properties only works in jse.
        if (isOnServer()) {
            return;
        }
        EntityManager manager = createEntityManager();
        ((JpaEntityManager)manager).getUnitOfWork().getParent().getIdentityMapAccessor().initializeAllIdentityMaps();
        DescriptorQueryManager queryManager = ((JpaEntityManager)manager).getUnitOfWork().getDescriptor(Employee.class).getQueryManager();
        queryManager.setExpressionQueryCacheMaxSize(queryManager.getExpressionQueryCacheMaxSize());
        ReadAllQuery query = new ReadAllQuery();
        query.setIsExecutionClone(true);
        query.setReferenceClass(Employee.class);
        ((JpaEntityManager)manager).getUnitOfWork().executeQuery(query);
        closeEntityManager(manager);

        assertNull("ExpressionCache has query that references a RWUOW", queryManager.getCachedExpressionQuery(query).getSession());
    }

    public void testUnWrapClass() {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            EntityManagerImpl emi = (EntityManagerImpl) em.getDelegate();
            ServerSession session = emi.getServerSession();
            UnitOfWork uow = emi.getUnitOfWork();
            JpaEntityManager jem = emi;
            org.eclipse.persistence.sessions.server.ServerSession session1;
            session1 = (ServerSession) em.unwrap(org.eclipse.persistence.sessions.Session.class);
            assertEquals("Does not return server session", session, session1);
            UnitOfWork uow1;
            uow1 = em.unwrap(org.eclipse.persistence.sessions.UnitOfWork.class);
            assertEquals("Does not return unit of work", uow, uow1);
            JpaEntityManager jem1;
            jem1 = em.unwrap(org.eclipse.persistence.jpa.JpaEntityManager.class);
            assertEquals("Does not return underlying entitymanager", jem, jem1);
            // TODO: This should be supported.
            /*Connection conn1;
            conn1 = em.unwrap(java.sql.Connection.class);
            Connection conn = uowImpl.getAccessor().getConnection();
            assertEquals("Does not return underlying connection", conn, conn1);*/
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
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
        if (isOnServer()) {
            // Cannot connect locally on server.
            return;
        }
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
        EntityManager em = (EntityManager)getEntityManagerFactory().createEntityManager().getDelegate();
        Query query = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.firstName = 'testQueryHints'");
        // Set a hint first to trigger query clone (because query is accessed before other hints are set).
        query.setHint(QueryHints.READ_ONLY, false);
        ObjectLevelReadQuery olrQuery = (ObjectLevelReadQuery)((JpaQuery)query).getDatabaseQuery();

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
        
        query.setHint(QueryHints.INDIRECTION_POLICY, CacheUsageIndirectionPolicy.Trigger);
        assertTrue("INDIRECTION_POLICY not set.", olrQuery.getInMemoryQueryIndirectionPolicyState() == InMemoryQueryIndirectionPolicy.SHOULD_TRIGGER_INDIRECTION);
        
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
        
        // Read-only
        query.setHint(QueryHints.READ_ONLY, "false");
        assertFalse("Read-only not set.", olrQuery.isReadOnly()); 
        
        query.setHint(QueryHints.READ_ONLY, Boolean.TRUE);
        assertTrue("Read-only not set.", olrQuery.isReadOnly());
        
        query.setHint(QueryHints.READ_ONLY, Boolean.FALSE);
        assertFalse("Read-only not set.", olrQuery.isReadOnly());        

        // Maintain cache
        query.setHint(QueryHints.MAINTAIN_CACHE, true);
        assertTrue("MAINTAIN_CACHE set.", olrQuery.shouldMaintainCache());
        
        query.setHint(QueryHints.MAINTAIN_CACHE, "false");
        assertFalse("MAINTAIN_CACHE not set.", olrQuery.shouldMaintainCache());
        
        query.setHint(QueryHints.MAINTAIN_CACHE, Boolean.TRUE);
        assertTrue("MAINTAIN_CACHE not set.", olrQuery.shouldMaintainCache());
        
        query.setHint(QueryHints.MAINTAIN_CACHE, Boolean.FALSE);
        assertFalse("MAINTAIN_CACHE not set.", olrQuery.shouldMaintainCache());

        // Prepare
        query.setHint(QueryHints.PREPARE, true);
        assertTrue("PREPARE set.", olrQuery.shouldPrepare());
        
        query.setHint(QueryHints.PREPARE, "false");
        assertFalse("PREPARE not set.", olrQuery.shouldPrepare());
        
        query.setHint(QueryHints.PREPARE, Boolean.TRUE);
        assertTrue("PREPARE not set.", olrQuery.shouldPrepare());
        
        query.setHint(QueryHints.PREPARE, Boolean.FALSE);
        assertFalse("PREPARE not set.", olrQuery.shouldPrepare());

        // Cache statement
        query.setHint(QueryHints.CACHE_STATMENT, true);
        assertTrue("CACHE_STATMENT set.", olrQuery.shouldCacheStatement());
        
        query.setHint(QueryHints.CACHE_STATMENT, "false");
        assertFalse("CACHE_STATMENT not set.", olrQuery.shouldCacheStatement());
        
        query.setHint(QueryHints.CACHE_STATMENT, Boolean.TRUE);
        assertTrue("CACHE_STATMENT not set.", olrQuery.shouldCacheStatement());
        
        query.setHint(QueryHints.CACHE_STATMENT, Boolean.FALSE);
        assertFalse("CACHE_STATMENT not set.", olrQuery.shouldCacheStatement());

        // Flush
        query.setHint(QueryHints.FLUSH, true);
        assertTrue("FLUSH set.", olrQuery.getFlushOnExecute());
        
        query.setHint(QueryHints.FLUSH, "false");
        assertFalse("FLUSH not set.", olrQuery.getFlushOnExecute());
        
        query.setHint(QueryHints.FLUSH, Boolean.TRUE);
        assertTrue("FLUSH not set.", olrQuery.getFlushOnExecute());
        
        query.setHint(QueryHints.FLUSH, Boolean.FALSE);
        assertFalse("FLUSH not set.", olrQuery.getFlushOnExecute());

        // Native connection
        query.setHint(QueryHints.NATIVE_CONNECTION, true);
        assertTrue("NATIVE_CONNECTION set.", olrQuery.isNativeConnectionRequired());
        
        query.setHint(QueryHints.NATIVE_CONNECTION, "false");
        assertFalse("NATIVE_CONNECTION not set.", olrQuery.isNativeConnectionRequired());
        
        query.setHint(QueryHints.NATIVE_CONNECTION, Boolean.TRUE);
        assertTrue("NATIVE_CONNECTION not set.", olrQuery.isNativeConnectionRequired());
        
        query.setHint(QueryHints.NATIVE_CONNECTION, Boolean.FALSE);
        assertFalse("NATIVE_CONNECTION not set.", olrQuery.isNativeConnectionRequired());
        
        // Hint
        query.setHint(QueryHints.HINT, "/* use the index man */");
        assertTrue("HINT not set.", olrQuery.getHintString().equals("/* use the index man */"));

        // Cursor
        query.setHint(QueryHints.CURSOR, Boolean.TRUE);
        assertTrue("CURSOR not set.", ((ReadAllQuery)olrQuery).getContainerPolicy().isCursoredStreamPolicy());
        
        query.setHint(QueryHints.CURSOR, Boolean.FALSE);
        assertFalse("CURSOR set.", ((ReadAllQuery)olrQuery).getContainerPolicy().isCursoredStreamPolicy());
        
        query.setHint(QueryHints.CURSOR_INITIAL_SIZE, "100");
        assertTrue("CURSOR not set.", ((ReadAllQuery)olrQuery).getContainerPolicy().isCursoredStreamPolicy());
        assertTrue("CURSOR_INITIAL_SIZE not set.", ((CursoredStreamPolicy)((ReadAllQuery)olrQuery).getContainerPolicy()).getInitialReadSize() == 100);

        query.setHint(QueryHints.CURSOR_INITIAL_SIZE, 200);
        assertTrue("CURSOR_INITIAL_SIZE not set.", ((CursoredStreamPolicy)((ReadAllQuery)olrQuery).getContainerPolicy()).getInitialReadSize() == 200);

        query.setHint(QueryHints.CURSOR, Boolean.FALSE);
        
        query.setHint(QueryHints.CURSOR_PAGE_SIZE, "100");
        assertTrue("CURSOR not set.", ((ReadAllQuery)olrQuery).getContainerPolicy().isCursoredStreamPolicy());
        assertTrue("CURSOR_PAGE_SIZE not set.", ((CursoredStreamPolicy)((ReadAllQuery)olrQuery).getContainerPolicy()).getPageSize() == 100);

        query.setHint(QueryHints.CURSOR_PAGE_SIZE, 200);
        assertTrue("CURSOR_PAGE_SIZE not set.", ((CursoredStreamPolicy)((ReadAllQuery)olrQuery).getContainerPolicy()).getPageSize() == 200);

        query.setHint(QueryHints.CURSOR, Boolean.FALSE);
        
        query.setHint(QueryHints.CURSOR_SIZE, "Select Count(*) from Employee");
        assertTrue("CURSOR_SIZE not set.", ((CursoredStreamPolicy)((ReadAllQuery)olrQuery).getContainerPolicy()).getSizeQuery().getSQLString().equals("Select Count(*) from Employee"));

        // Scrollable cursor
        query.setHint(QueryHints.SCROLLABLE_CURSOR, Boolean.TRUE);
        assertTrue("SCROLLABLE_CURSOR not set.", ((ReadAllQuery)olrQuery).getContainerPolicy().isScrollableCursorPolicy());
        
        query.setHint(QueryHints.SCROLLABLE_CURSOR, Boolean.FALSE);
        assertFalse("SCROLLABLE_CURSOR set.", ((ReadAllQuery)olrQuery).getContainerPolicy().isScrollableCursorPolicy());

        query.setHint(QueryHints.RESULT_SET_TYPE, ResultSetType.Reverse);
        assertTrue("RESULT_SET_TYPE not set.", ((ScrollableCursorPolicy)((ReadAllQuery)olrQuery).getContainerPolicy()).getResultSetType() == ScrollableCursorPolicy.FETCH_REVERSE);

        query.setHint(QueryHints.RESULT_SET_CONCURRENCY, ResultSetConcurrency.Updatable);
        assertTrue("RESULT_SET_CONCURRENCY not set.", ((ScrollableCursorPolicy)((ReadAllQuery)olrQuery).getContainerPolicy()).getResultSetConcurrency() == ScrollableCursorPolicy.CONCUR_UPDATABLE);
      
        // Exclusive connection
        query.setHint(QueryHints.EXCLUSIVE_CONNECTION, Boolean.TRUE);
        assertTrue("EXCLUSIVE_CONNECTION not set.", olrQuery.shouldUseExclusiveConnection());

        // Inheritance
        query.setHint(QueryHints.INHERITANCE_OUTER_JOIN, Boolean.TRUE);
        assertTrue("INHERITANCE_OUTER_JOIN not set.", olrQuery.shouldOuterJoinSubclasses());
        
        // History
        query.setHint(QueryHints.AS_OF, "1973/10/11 12:00:00");
        assertTrue("AS_OF not set.", olrQuery.getAsOfClause() != null);
        
        query.setHint(QueryHints.AS_OF_SCN, "12345");
        assertTrue("AS_OF_SCN not set.", ((Number)olrQuery.getAsOfClause().getValue()).intValue() == 12345);
        
        // Fetch groups
        query.setHint(QueryHints.FETCH_GROUP_DEFAULT, Boolean.FALSE);
        assertFalse("FETCH_GROUP_DEFAULT not set.", olrQuery.shouldUseDefaultFetchGroup());

        query.setHint(QueryHints.FETCH_GROUP_NAME, "nameAndCity");
        assertTrue("FETCH_GROUP_NAME not set.", olrQuery.getFetchGroupName().equals("nameAndCity"));
        
        query.setHint(QueryHints.FETCH_GROUP_ATTRIBUTE, "firstName");
        query.setHint(QueryHints.FETCH_GROUP_ATTRIBUTE, "lastName");
        assertTrue("FETCH_GROUP_ATTRIBUTE not set.", olrQuery.getFetchGroup().containsAttribute("firstName"));
        assertTrue("FETCH_GROUP_ATTRIBUTE not set.", olrQuery.getFetchGroup().containsAttribute("lastName"));

        FetchGroup fetchGroup = new FetchGroup();
        fetchGroup.addAttribute("id");
        query.setHint(QueryHints.FETCH_GROUP, fetchGroup);
        assertTrue("FETCH_GROUP not set.", olrQuery.getFetchGroup() == fetchGroup);
        
        // Timeout
        query.setHint(QueryHints.JDBC_TIMEOUT, new Integer(100));
        assertTrue("Timeout not set.", olrQuery.getQueryTimeout() == 100);
        
        // JDBC
        query.setHint(QueryHints.JDBC_FETCH_SIZE, new Integer(101));
        assertTrue("Fetch-size not set.", olrQuery.getFetchSize() == 101);
        
        query.setHint(QueryHints.JDBC_MAX_ROWS, new Integer(103));
        assertTrue("Max-rows not set.", olrQuery.getMaxRows() == 103);
        
        query.setHint(QueryHints.JDBC_FIRST_RESULT, new Integer(123));
        assertTrue("JDBC_FIRST_RESULT not set.", olrQuery.getFirstResult() == 123); 

        // Refresh
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
        
        // Result collection
        query.setHint(QueryHints.RESULT_COLLECTION_TYPE, java.util.ArrayList.class);
        assertTrue("ArrayList not set.", ((ReadAllQuery)olrQuery).getContainerPolicy().getContainerClass().equals(java.util.ArrayList.class)); 

        query.setHint(QueryHints.RESULT_COLLECTION_TYPE, "java.util.Vector");
        assertTrue("Vector not set.", ((ReadAllQuery)olrQuery).getContainerPolicy().getContainerClass().equals(java.util.Vector.class)); 

        query.setHint(QueryHints.RESULT_COLLECTION_TYPE, "org.eclipse.persistence.testing.models.jpa.relationships.CustomerCollection");
        assertTrue("CustomerCollection not set.", ((ReadAllQuery)olrQuery).getContainerPolicy().getContainerClass().equals(CustomerCollection.class)); 
        
        // Query type
        query.setHint(QueryHints.QUERY_TYPE, QueryType.ReadObject);
        assertTrue("QUERY_TYPE not set.", ((JpaQuery)query).getDatabaseQuery().getClass().equals(ReadObjectQuery.class)); 

        query.setHint(QueryHints.QUERY_TYPE, QueryType.Report);
        assertTrue("QUERY_TYPE not set.", ((JpaQuery)query).getDatabaseQuery().getClass().equals(ReportQuery.class));
        
        query.setHint(QueryHints.QUERY_TYPE, QueryType.DataModify);
        assertTrue("QUERY_TYPE not set.", ((JpaQuery)query).getDatabaseQuery().getClass().equals(DataModifyQuery.class));

        query.setHint(QueryHints.QUERY_TYPE, "org.eclipse.persistence.queries.ValueReadQuery");
        assertTrue("QUERY_TYPE not set.", ((JpaQuery)query).getDatabaseQuery().getClass().equals(ValueReadQuery.class));
        
        query.setHint(QueryHints.QUERY_TYPE, QueryType.ReadAll);
        assertTrue("QUERY_TYPE not set.", ((JpaQuery)query).getDatabaseQuery().getClass().equals(ReadAllQuery.class)); 

        // Result type
        query.setHint(QueryHints.QUERY_TYPE, QueryType.Report);
        query.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
        assertTrue("RESULT_TYPE not set.", ((ReportQuery)((JpaQuery)query).getDatabaseQuery()).getReturnType() == ReportQuery.ShouldReturnReportResult);

        query.setHint(QueryHints.RESULT_TYPE, ResultType.Array);
        assertTrue("RESULT_TYPE not set.", ((ReportQuery)((JpaQuery)query).getDatabaseQuery()).getReturnType() == ReportQuery.ShouldReturnArray);

        query.setHint(QueryHints.RESULT_TYPE, ResultType.Value);
        assertTrue("RESULT_TYPE not set.", ((ReportQuery)((JpaQuery)query).getDatabaseQuery()).getReturnType() == ReportQuery.ShouldReturnSingleValue);
        
        query.setHint(QueryHints.QUERY_TYPE, QueryType.DataRead);
        query.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
        query.setHint(QueryHints.RESULT_TYPE, ResultType.Array);
        query.setHint(QueryHints.RESULT_TYPE, ResultType.Value);
   
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

        //org.eclipse.persistence.jpa.JpaQuery query = (org.eclipse.persistence.jpa.JpaQuery)em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
        org.eclipse.persistence.jpa.JpaQuery query = (org.eclipse.persistence.jpa.JpaQuery)getEntityManagerFactory().createEntityManager().createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
        query.setHint(QueryHints.BATCH, "e.phoneNumbers");
        query.setHint(QueryHints.BATCH, "e.manager.phoneNumbers");
        query.setHint(QueryHints.BATCH_TYPE, "IN");
        query.setHint(QueryHints.BATCH_SIZE, "10");
        
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

        //org.eclipse.persistence.jpa.JpaQuery query = (org.eclipse.persistence.jpa.JpaQuery)em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
        org.eclipse.persistence.jpa.JpaQuery query = (org.eclipse.persistence.jpa.JpaQuery)getEntityManagerFactory().createEntityManager().createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
        query.setHint(QueryHints.LEFT_FETCH, "e.manager");
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
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testNullifyAddressIn skipped for this platform, " +
                     "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
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
           
    // Test multiple items from a report query. Will verify the version on
    // only one of the results.
    public void testLockingLeftJoinOneToOneQuery() {        
        // Grab a copy of the results that we will lock then verify.
        List<Object[]> results = createEntityManager().createQuery("SELECT m, e FROM Employee e LEFT JOIN e.manager m").getResultList();
        
        if (! results.isEmpty()) {
            // Issuing the force increment locking query.
            EntityManager em = createEntityManager();
            beginTransaction(em);
            em.createQuery("SELECT m, e FROM Employee e LEFT JOIN e.manager m").setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT).getResultList();
            commitTransaction(em);
        
            // Verify the items of the result list all had a version increment.
            beginTransaction(em);
            try{
                for (Object[] result : results) {
                    Employee managerBefore = (Employee) result[0];
                    if (managerBefore != null) {
                        int managerVersionBefore = managerBefore.getVersion();
                        Employee managerAfter = em.find(Employee.class, managerBefore.getId());
                        int managerVersionAfter = managerAfter.getVersion();
                        assertTrue("The manager version was not updated on the locking query.", (managerVersionAfter - managerVersionBefore) == 1);
                    }
                    
                    Employee employeeBefore = (Employee) result[1];
                    if (employeeBefore != null) {
                        int employeeVersionBefore = employeeBefore.getVersion();
                        Employee employeeAfter = em.find(Employee.class, employeeBefore.getId());
                        int employeeVersionAfter = employeeAfter.getVersion();
                        assertTrue("The manager version was not updated on the locking query.", (employeeVersionAfter - employeeVersionBefore) == 1);
                    }
                }
            } finally {
               if(this.isTransactionActive(em)) {
                   rollbackTransaction(em);
                  }
                   closeEntityManager(em);
            }
        }
    }
    
    // Test single item from a report query.
    public void testLockingLeftJoinOneToOneQuery2() {        
        // Grab a copy of the results that we will lock then verify.
        List<Address> results = createEntityManager().createQuery("SELECT a FROM Employee e LEFT JOIN e.address a").getResultList();
        
        if (results != null) {
            // Issuing the force increment locking query.
            EntityManager em = createEntityManager();
            beginTransaction(em);
            em.createQuery("SELECT a FROM Employee e LEFT JOIN e.address a").setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT).getResultList();
            commitTransaction(em);
            
            // Verify the items of the result list all had a version increment.
            beginTransaction(em);
            try{
                for (Address address : results) {
                    if (address != null) {
                        int versionBefore = address.getVersion();    
                        Address addressAfter = em.find(Address.class, address.getID());
                        int versionAfter = addressAfter.getVersion();
                        assertTrue("The version on an address was not updated on the locking query.", (versionAfter - versionBefore) == 1);
                    }
                }
            } finally {
               if(this.isTransactionActive(em)) {
                   rollbackTransaction(em);
                  }
                   closeEntityManager(em);
            }
        }
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
        ServerSession ss = getServerSession();
        if(!ss.getLogin().getPlatform().supportsSequenceObjects() || isOnServer()) {
            // platform that supports sequence objects is required for this test
            // this test not work on server since the bug: 262251
            closeEntityManager(em);
            return;
        }
        String seqName = "testSequenceObjectDefinition";
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
            if (JUnitTestCase.getServerSession().getPlatform().isSymfoware()) return;
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
        commitTransaction(em);
        
        try {
            em.createNamedQuery("findAllSQLDepartments").getResultList();
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
            em.merge(emp);    //then attempt to merge the Employee
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

    //detach(object) on a removed object does not throw exception. This test only
    //checks whether an removed object is completely deleted from the
    //getDeletedObject()Map after 'detach(removedobject)' is invoked
    public void testDetachRemovedObject() {
        //create an Employee
        Employee emp = new Employee();
        emp.setFirstName("testDetachRemovedObjectEmployee");
        emp.setId(71);
    
        //persist the Employee
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            em.persist(emp);
            commitTransaction(em);
        } catch (RuntimeException re){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw re;
        }
    
        beginTransaction(em);
        em.remove(em.find(Employee.class, emp.getId())); //attempt to remove the Employee
        commitTransaction(em);
        beginTransaction(em);
        EntityManagerImpl em1 = (EntityManagerImpl)em.getDelegate();
        try {  
            em.detach(emp);    //attempt to detach the Employee
            UnitOfWork uow = em1.getUnitOfWork();
            UnitOfWorkImpl uowImpl = (UnitOfWorkImpl)uow;
            boolean afterClear = uowImpl.getDeletedObjects().containsKey(emp);
            assertFalse("exception thrown when detaching a removed entity is attempted.", afterClear);
        } catch (IllegalArgumentException iae){
            return;
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
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
            emp = em.find(Employee.class, emp.getId());
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
            managedEmp.toString();
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

    //detach(null) should throw IllegalArgumentException
    public void testDetachNull() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.detach(null);
        } catch (IllegalArgumentException iae) {
            return;
        } catch (Exception e) {
            fail("Wrong exception type thrown: " + e.getClass());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        fail("No exception thrown when entityManager.detach(null) attempted.");
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
        if (isOnServer() && JUnitTestCase.isWeavingEnabled()) {
            // Bug 297628 - jpa.advanced.EntityManagerJUnitTestSuite.testCreateEntityManagerFactory failed on WLS for only dynamic weaving
            // Dynamic weaving for Persistence.createEntityManagerFactory can't be done on app. server.
            return;
        }
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
        if (getServerSession().getServerPlatform() != null && getServerSession().getServerPlatform() instanceof WebSphere_7_Platform){
            warning("The test <testCreateEntityManagerFactory2> is not supported on WebSphere, because the ejb3.0 spec doesn't say that the PersistenceException must throw at this situation, just EclipseLink's implementation beyond the spec. So this test should not run on WebSphere 7.0.0.5");
            return;
        }
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
        // added new setting for bug 237281
        InheritancePolicy ip = getServerSession().getDescriptor(Project.class).getInheritancePolicy();
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
        int addressId = address.getID();
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
        employee.setManager((Employee)null);
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
        int addressId = address.getID();
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
        employee.setManager((Employee)null);
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
        int addressId = address.getID();
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
        employee.setManager((Employee)null);
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
         if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
             getServerSession().logMessage("Test testDeleteAllProjectsWithNullTeamLeader skipped for this platform, "
                     + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
             return;
         }
         internalDeleteAllProjectsWithNullTeamLeader("Project");
     }
     public void testDeleteAllSmallProjectsWithNullTeamLeader() {
         internalDeleteAllProjectsWithNullTeamLeader("SmallProject");
     }
     public void testDeleteAllLargeProjectsWithNullTeamLeader() {
         if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
             getServerSession().logMessage("Test testDeleteAllLargeProjectsWithNullTeamLeader skipped for this platform, "
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
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testUpdateUsingTempStorage* skipped for this platform, "
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
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testUpdateAll*Projects skipped for this platform, "
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
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testUpdateAll*ProjectsWithName skipped for this platform, "
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
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testUpdateAll*ProjectsWithNullTeamLeader skipped for this platform, "
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
        ServerSession ss = getServerSession();
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
        Session session = getServerSession();
        ClassDescriptor descriptor = session.getDescriptor(Employee.class);
        descriptor.getQueryManager().addQuery("findByFNameLName", query);

        beginTransaction(em);
        try {
            Employee emp = new Employee();
            emp.setFirstName("Melvin");
            emp.setLastName("Malone");
            em.persist(emp);
            em.flush();
            
            Query ejbQuery = ((org.eclipse.persistence.jpa.JpaEntityManager)em.getDelegate()).createDescriptorNamedQuery("findByFNameLName", Employee.class);
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
        Session session = getServerSession();
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
            Query ejbQuery = ((org.eclipse.persistence.jpa.JpaEntityManager)em.getDelegate()).createDescriptorNamedQuery("findEmployees", Employee.class, args);
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
        Employee result = null;
        try {
            beginTransaction(em);
            result = em.merge(employee);
            result = em.merge(employee2);
            assertTrue("The firstName was not merged properly", result.getFirstName().equals(employee2.getFirstName()));
            em.flush();
        } catch (PersistenceException e){
            fail("A double merge of an object with the same key, caused two inserts instead of one.");
        } finally {
            rollbackTransaction(em);
        }
    }

    // gf 3032
    public void testPessimisticLockHintStartsTransaction(){
        if (isOnServer()) {
            // Extended persistence context are not supported in the server.
            return;
        }
        if (!isSelectForUpateSupported()) {
            return;
        }
        EntityManagerImpl em = (EntityManagerImpl)createEntityManager();
        beginTransaction(em);
        Query query = em.createNamedQuery("findAllEmployeesByFirstName");
        query.setHint("eclipselink.pessimistic-lock", PessimisticLock.Lock);
        query.setParameter("firstname", "Sarah");
        List results = query.getResultList();
        results.toString();
        assertTrue("The extended persistence context is not in a transaction after a pessmimistic lock query", em.getActivePersistenceContext(em.getTransaction()).getParent().isInTransaction());
        rollbackTransaction(em);
    }
    
    /**
     * Test that all of the classes in the advanced model were weaved as expected.
     */
    public void testWeaving() {
        // Only test if weaving was on, test runs without weaving must set this system property.
        if (JUnitTestCase.isWeavingEnabled()) {
            internalTestWeaving(new Employee(), true, true);
            internalTestWeaving(new FormerEmployment(), true, false);
            internalTestWeaving(new Address(), true, false);
            internalTestWeaving(new PhoneNumber(), true, false);
            internalTestWeaving(new EmploymentPeriod(), true, false);
            internalTestWeaving(new Buyer(), false, false);  // field-locking
            internalTestWeaving(new GoldBuyer(), false, false);  // field-locking
            internalTestWeaving(new PlatinumBuyer(), false, false);  // field-locking
            internalTestWeaving(new Department(), true, false);  // eager 1-m
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
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testBulkDeleteThenMerge skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        if (isOnServer()) {
            // Got transaction timeout on server.
            return;
        }
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
        ServerSession ss = getServerSession();
        // make sure the sequence has both preallocation and callback
        // (the latter means not using sequencing connection pool, 
        // acquiring values before insert and requiring transaction).
        //if(ss.getSequencingControl().shouldUseSeparateConnection()) {
        //    fail("setup failure: the test requires serverSession.getSequencingControl().shouldUseSeparateConnection()==false");
        //}
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
     * Test getEntityManagerFactory() API
     * tests whether illegalArgumentException is thrown when
     * an entity manager is closed.
     */
    public void testGetEntityManagerFactory() {
        Employee emp = new Employee();
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            emp.setFirstName("test");
            em.persist(emp);
            commitTransaction(em);
            EntityManagerFactory emf = em.getEntityManagerFactory();
            if (emf == null) {
                fail("Factory is null.");
            }
        } finally {
            closeEntityManager(em);
        }
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

        clearCache();
        
        em = createEntityManager();
        beginTransaction(em);
        employee = em.find(Employee.class, id);
        if (!employee.getFirstName().equals("testGetReference")) {
            fail("find returned the wrong object");
        }
        commitTransaction(em);

        clearCache();
        
        List key = new ArrayList();
        key.add(id);
        em = createEntityManager();
        beginTransaction(em);
        employee = em.getReference(Employee.class, key);
        if (!employee.getFirstName().equals("testGetReference")) {
            fail("getReference returned the wrong object");
        }
        commitTransaction(em);

        clearCache();
        
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
     * Test adding a new object to a collection.
     */
    public void testCollectionAddNewObjectUpdate() {
        int id = createEmployee("testCollectionAddNew").getId();
                
        clearCache();
        
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee employee = em.find(Employee.class, id);
        SmallProject project = new SmallProject();
        employee.getProjects().add(project);
        project.getTeamMembers().add(employee);
        commitTransaction(em);


        // 264768: Certain application servers like WebSphere 7 will call em.clear() during commit()
        // in order that the entityManager is cleared before returning the em to their server pool.
        // We will want to verify that the entity is managed before accessing its properties and causing 
        // the object to be rebuilt with any non-direct fields uninstantiated.
        // Note: even in this case the entity should still be in the shared cache and database below
        if(em.contains(employee)) {
            verifyObject(project);
            verifyObject(employee);
        }
        
        clearCache();
        if(em.contains(employee)) {
            verifyObject(project);
            verifyObject(employee);
        }
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
        
        // 264768: Certain application servers like WebSphere 7 will call em.clear() during commit()
        // in order that the entityManager is cleared before returning the em to their server pool.
        // We will want to verify that the entity is managed before accessing its properties and causing 
        // the object to be rebuilt with any non-direct fields uninstantiated.
        // Note: even in this case the entity should still be in the shared cache and database below
        if(em.contains(employee)) {
            if (employee.getVersion() != version) {
                fail("un-fetched object was updated");
            }
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
        int assignedSequenceNumber = add.getID();
        
        em.clear();
        getServerSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        add = em.find(Address.class, assignedSequenceNumber);
        
        assertTrue("Did not correctly persist a mapping using a class-instance converter", (add.getType() instanceof Bungalow));

        beginTransaction(em);
        add = em.find(Address.class, assignedSequenceNumber);
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
        } catch (Exception exception) {
            throw new RuntimeException("A Persistence Unit [" + puName + "] " + failureMessagePostScript, exception);
        } finally {
            if (null != em) {
                closeEntityManager(em);
            }
        }
    }
    
    public void testConnectionPolicy() {
        internalTestConnectionPolicy(false);
    }
    
    public void testConnectionPolicySetProperty() {
        internalTestConnectionPolicy(true);
    }
    
    public void internalTestConnectionPolicy(boolean useSetProperty) {
        // setup
        String errorMsg = "";

        HashMap properties = null;
        if(!useSetProperty) { 
            properties = new HashMap();
            properties.put(EntityManagerProperties.JDBC_USER, "em_user");
            properties.put(EntityManagerProperties.JDBC_PASSWORD, "em_password");
            properties.put(EntityManagerProperties.JTA_DATASOURCE, "em_jta_datasource");
            properties.put(EntityManagerProperties.NON_JTA_DATASOURCE, "em_nonjta_datasource");
            properties.put(EntityManagerProperties.EXCLUSIVE_CONNECTION_MODE, ExclusiveConnectionMode.Always);
        }

        // test
        EntityManager em = null;
        boolean isInTransaction = false;
        try {
            // assume that if JTA is used on server then EntityManager is always injected.
            boolean isEmInjected = isOnServer() && getServerSession().getLogin().shouldUseExternalTransactionController();
            if (isEmInjected) {
                em = createEntityManager();
                // In server jta case need a transaction - otherwise the wrapped EntityManagerImpl is not kept.
                beginTransaction(em);
                isInTransaction = true;
                ((EntityManagerImpl)em.getDelegate()).setProperties(properties);
            } else {
                EntityManagerFactory emFactory = getEntityManagerFactory();
                em = emFactory.createEntityManager(properties);
            }
            if(useSetProperty) { 
                em.setProperty(EntityManagerProperties.JDBC_USER, "em_user");
                em.setProperty(EntityManagerProperties.JDBC_PASSWORD, "em_password");
                em.setProperty(EntityManagerProperties.JTA_DATASOURCE, "em_jta_datasource");
                em.setProperty(EntityManagerProperties.NON_JTA_DATASOURCE, "em_nonjta_datasource");
                em.setProperty(EntityManagerProperties.EXCLUSIVE_CONNECTION_MODE, ExclusiveConnectionMode.Always);
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
        } finally {
            // clean-up
            if (isInTransaction) {
                rollbackTransaction(em);
            }
            if(em != null) {
                closeEntityManager(em);
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
        emps.toString();
        closeEntityManager(em);
    }
    
    
    // Bug 237281 - ensure we throw the correct exception when trying to persist a non-entity subclass of an entity
    public void testExceptionForPersistNonEntitySubclass(){
        EntityManager em = createEntityManager();
        Exception caughtException = null;
        try{
            beginTransaction(em);
            em.persist(new SuperLargeProject());
        } catch (IllegalArgumentException e){
            caughtException = e;
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
        if (caughtException == null){
            fail("Caught an incorrect exception when persisting a non entity.");
        }
    }
    
    // bug 237281 - ensure seeting InheritancePolicy to allow non-entity subclasses to be persisted as their
    // superclass works
    public void testEnabledPersistNonEntitySubclass() {
        EntityManager em = createEntityManager();
        InheritancePolicy ip = getServerSession().getDescriptor(Project.class).getInheritancePolicy();
        boolean describesNonPersistentSubclasses = ip.getDescribesNonPersistentSubclasses();
        ip.setDescribesNonPersistentSubclasses(true);

        beginTransaction(em);
        SuperLargeProject s1 = new SuperLargeProject("Super 1");
        try {
            em.persist(s1);
        } catch (Exception e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            fail("Persist operation was not cascaded to related non-entity, thrown: " + e);
        } finally {
            rollbackTransaction(em);
            ip.setDescribesNonPersistentSubclasses(describesNonPersistentSubclasses);
            closeEntityManager(em);
        }
    }
    
    // bug 239505 - Ensure our Embeddables succeed a call to clone
    public void testCloneEmbeddable(){
        EmployeePopulator populator = new EmployeePopulator();
        EmploymentPeriod period = populator.employmentPeriodExample1();

        try {
            period.clone();
        } catch (Exception e){
            fail("Exception thrown when trying to clone an Embeddable: " + e.toString());
        }
    }

    // bug 232555 - NPE setting embedded attributes after persist in woven classes
    public void testEmbeddedNPE() {
        if (isOnServer()) {
            // Close is not used on server.
            return;
        }
        // setup
        // create and persist an Employee
        Employee emp = new Employee();
        emp.setFirstName("testEmbeddedNPE");
        EntityManager em = createEntityManager();
        beginTransaction(em);
        em.persist(emp);
        commitTransaction(em);
        
        // test
        // add an embedded to the persisted Employee
        EmploymentPeriod period = new EmploymentPeriod(Date.valueOf("2007-01-01"), Date.valueOf("2007-12-31")); 
        try {
            // that used to cause NPE
            emp.setPeriod(period);
        } catch (NullPointerException npe) {
            // clean-up
            beginTransaction(em);
            em.remove(emp);
            commitTransaction(em);
            em.close();
            
            throw npe;
        }
        
        // update the Employee in the db
        beginTransaction(em);
        em.merge(emp);
        commitTransaction(em);
        
        // verify
        // make sure that the Employee has been written into the db correctly:
        // clear both em and shared cache
        em.clear();
        clearCache();
        // and re-read it
        Employee readEmp = em.find(Employee.class, emp.getId());
        // should be the same as the original one
        boolean equal = getServerSession().compareObjects(emp, readEmp);
        
        // clean-up
        if(readEmp != null) {
            beginTransaction(em);
            //readEmp = em.find(Employee.class, emp.getId());
            em.remove(readEmp);
            commitTransaction(em);
        }
        em.close();
        
        if(!equal) {
            fail("The Employee wasn't updated correctly in the db");
        }
    }
    
    // Bug 256296: Reconnect fails when session loses connectivity
    static class AcquireReleaseListener extends SessionEventAdapter {
        HashSet<Accessor> acquiredReadConnections = new HashSet(); 
        HashSet<Accessor> acquiredWriteConnections = new HashSet(); 
        public void postAcquireConnection(SessionEvent event) {
            Accessor accessor = (Accessor)event.getResult();
            Session session = event.getSession();
            if(session.isServerSession()) {
                acquiredReadConnections.add(accessor);
                ((ServerSession)session).log(SessionLog.FINEST, SessionLog.CONNECTION, "AcquireReleaseListener.acquireReadConnection: " + nAcquredReadConnections(), (Object[])null, accessor, false);
            } else {
                acquiredWriteConnections.add(accessor);
                ((ClientSession)session).log(SessionLog.FINEST, SessionLog.CONNECTION, "AcquireReleaseListener.acquireWriteConnection: " + nAcquredWriteConnections(), (Object[])null, accessor, false);
            }
        }
        public void preReleaseConnection(SessionEvent event) {
            Accessor accessor = (Accessor)event.getResult();
            Session session = event.getSession();
            if(session.isServerSession()) {
                acquiredReadConnections.remove(accessor);
                ((ServerSession)session).log(SessionLog.FINEST, SessionLog.CONNECTION, "AcquireReleaseListener.releaseReadConnection: " + nAcquredReadConnections(), (Object[])null, accessor, false);
            } else {
                acquiredWriteConnections.remove(accessor);
                ((ClientSession)session).log(SessionLog.FINEST, SessionLog.CONNECTION, "AcquireReleaseListener.releaseWriteConnection: " + nAcquredWriteConnections(), (Object[])null, accessor, false);
            }
        }
        int nAcquredReadConnections() {
            return acquiredReadConnections.size(); 
        }
        int nAcquredWriteConnections() {
            return acquiredWriteConnections.size(); 
        }
        void clear() {
            acquiredReadConnections.clear(); 
            acquiredWriteConnections.clear(); 
        }
    }
    public void testEMCloseAndOpen(){
        Assert.assertFalse("Warning Sybase Driver does not work with DriverWrapper, testEMCloseAndOpen can't run on this platform.",  JUnitTestCase.getServerSession().getPlatform().isSybase());
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testEMCloseAndOpen skipped for this platform, "
                            + "Symfoware platform doesn't support failover.");
            return;
        }
        
        if (isOnServer()) {
            // Uses DefaultConnector.
            return;
        }
        
        // normally false; set to true for debug output for just this single test
        boolean shouldForceFinest = false;
        int originalLogLevel = -1; 
        
        ServerSession ss = ((EntityManagerFactoryImpl)getEntityManagerFactory()).getServerSession();
        
        // make sure the id hasn't been already used - it will be assigned to a new object (in case sequencing is not used). 
        int id = (ss.getNextSequenceNumberValue(Employee.class)).intValue();
        
        // cache the original driver name and connection string.
        String originalDriverName = ss.getLogin().getDriverClassName();
        String originalConnectionString = ss.getLogin().getConnectionString();
        
        // the new driver name and connection string to be used by the test
        String newDriverName = DriverWrapper.class.getName();
        String newConnectionString = DriverWrapper.codeUrl(originalConnectionString);
        
        // setup the wrapper driver
        DriverWrapper.initialize(originalDriverName);
        
        // The test need to connect with the new driver and connection string.
        // That could be done in JPA:
        //    // close the existing emf
        //    closeEntityManagerFactory();
        //    HashMap properties = new HashMap(JUnitTestCaseHelper.getDatabaseProperties());
        //    properties.put(PersistenceUnitProperties.JDBC_DRIVER, newDriverName);
        //    properties.put(PersistenceUnitProperties.JDBC_URL, newConnectionString);
        //    emf = getEntityManagerFactory(properties);
        // However this only works in case closeEntityManagerFactory disconnects the original ServerSession,
        // which requires the factory to be the only one using the persistence unit.
        // Alternative - and faster - approach is to disconnect the original session directly
        // and then reconnected it with the new driver and connection string.        
        ss.logout();
        ss.getLogin().setDriverClassName(newDriverName);
        ss.getLogin().setConnectionString(newConnectionString);
        AcquireReleaseListener listener = new AcquireReleaseListener();  
        ss.getEventManager().addListener(listener);
        if(shouldForceFinest) {
            if(ss.getLogLevel() != SessionLog.FINEST) {
                originalLogLevel = ss.getLogLevel();
                ss.setLogLevel(SessionLog.FINEST);
            }
        }
        ss.login();
        
        String errorMsg = "";
        // test several configurations:
        // all exclusive connection modes
        String[] exclusiveConnectionModeArray = new String[]{ExclusiveConnectionMode.Transactional, ExclusiveConnectionMode.Isolated, ExclusiveConnectionMode.Always};
        
        // Workaround for Bug 309881 - problems with CallQueryMechanism.prepareCall method
        // Because of this bug em.find ignores QueryHints.JDBC_TIMEOUT,
        // have to set it directly into the Descriptor's ReadObjectQuery.
        // This should be removed from the test after the bug is fixed.
        ReadObjectQuery employeeFindQuery = ss.getDescriptor(Employee.class).getQueryManager().getReadObjectQuery(); 
        int originalQueryTimeout = employeeFindQuery.getQueryTimeout();
        if(originalQueryTimeout > 0) {
            ss.setQueryTimeoutDefault(0);
            employeeFindQuery.setQueryTimeout(0);
            // The same bug 309881 requires the query to be reprepaired for queryTimeOut to be set on its call
            employeeFindQuery.setIsPrepared(false);
            employeeFindQuery.checkPrepare(ss, null);
        }
        
        // currently reconnection is not attempted if query time out is not zero.
        HashMap noTimeOutHint = new HashMap(1);
        noTimeOutHint.put(QueryHints.JDBC_TIMEOUT, 0);
        try {
            
            for(int i=0; i<3; i++) {
                String exclusiveConnectionMode = exclusiveConnectionModeArray[i];                
                for(int j=0; j<2; j++) {
                    // either using or not using sequencing 
                    boolean useSequencing = (j==0);                   
                    ss.log(SessionLog.FINEST, SessionLog.CONNECTION, "testEMCloseAndOpen: " + (useSequencing ? "sequencing" : "no sequencing"), (Object[])null, null, false);
                    HashMap emProperties = new HashMap(1);
                    emProperties.put(EntityManagerProperties.EXCLUSIVE_CONNECTION_MODE, exclusiveConnectionMode);
                    EntityManager em = createEntityManager(emProperties);
                    
                    em.find(Employee.class, 1, noTimeOutHint);
                    Employee emp = null;
                    boolean hasUnexpectedlyCommitted = false;
                    try{
                        em.getTransaction().begin();
    
                        // imitate disconnecting from network:
                        // driver's connect method and any method on any connection will throw SQLException
                        ss.log(SessionLog.FINEST, SessionLog.CONNECTION, "testEMCloseAndOpen: DriverWrapper.breakDriver(); DriverWrapper.breakOldConnections();", (Object[])null, null, false);
                        DriverWrapper.breakDriver();
                        DriverWrapper.breakOldConnections();
                        
                        emp = new Employee();
                        if(!useSequencing) {
                            emp.setId(id);
                        }
                        em.persist(emp);
                        em.getTransaction().commit();
    
                        // should never get here - all connections should be broken.
                        hasUnexpectedlyCommitted = true;
                        errorMsg += "useSequencing = " + useSequencing + "; exclusiveConnectionMode = " + exclusiveConnectionMode + ": Commit has unexpectedly succeeded - should have failed because all connections broken. driver = " + ss.getLogin().getDriverClassName() + "; url = " + ss.getLogin().getConnectionString();
                    } catch (Exception e){
                        // expected exception - connection is invalid and cannot be reconnected.
                        if(em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }            
                    }
                    closeEntityManager(em);
                    
                    // verify - all connections should be released
                    String localErrorMsg = "";
                    if(listener.nAcquredWriteConnections() > 0) {
                        localErrorMsg += "writeConnection not released; ";
                    }
                    if(listener.nAcquredReadConnections() > 0) {
                        localErrorMsg += "readConnection not released; ";
                    }
                    if(localErrorMsg.length() > 0) {
                        localErrorMsg = exclusiveConnectionMode + " useSequencing="+useSequencing + ": " + localErrorMsg;
                        errorMsg += localErrorMsg;
                        listener.clear();
                    }
                    
                    // imitate  reconnecting to network:
                    // driver's connect method will now work, all newly acquired connections will work, too;
                    // however the old connections cached in the connection pools are still invalid.
                    DriverWrapper.repairDriver();
                    ss.log(SessionLog.FINEST, SessionLog.CONNECTION, "testEMCloseAndOpen: DriverWrapper.repairDriver();", (Object[])null, null, false);
                    
                    boolean failed = true;
                    try {
                        em = createEntityManager();
                        em.find(Employee.class, 1);
                        if(!hasUnexpectedlyCommitted) {
                            em.getTransaction().begin();
                            emp = new Employee();
                            if(!useSequencing) {
                                emp.setId(id);
                            }
                            em.persist(emp);
                            em.getTransaction().commit();
                            failed = false;
                        }
                    } finally {
                        if(failed) {
                            // This should not happen
                            if(em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            closeEntityManager(em);
                            
                            if(errorMsg.length() > 0) {
                                ss.log(SessionLog.FINEST, SessionLog.CONNECTION, "testEMCloseAndOpen: errorMsg: " + "\n" + errorMsg, (Object[])null, null, false);
                            }
                        }
                    }
    
                    // clean-up
                    // remove the inserted object
                    em.getTransaction().begin();
                    em.remove(emp);
                    em.getTransaction().commit();
                    closeEntityManager(em);                
                }
            }

        } finally {

            // Workaround for Bug 309881 - problems with CallQueryMechanism.prepareCall method
            // Because of this bug em.find ignores QueryHints.JDBC_TIMEOUT,
            // have to set it directly into the Descriptor's ReadObjectQuery.
            // This should be removed from the test after the bug is fixed.
            if(originalQueryTimeout > 0) {
                ss.setQueryTimeoutDefault(originalQueryTimeout);
                employeeFindQuery.setQueryTimeout(originalQueryTimeout);
                // The same bug 309881 requires the query to be reprepaired for queryTimeOut to be set on its call
                employeeFindQuery.setIsPrepared(false);
                employeeFindQuery.checkPrepare(ss, null);
            }
            // clear the driver wrapper
            DriverWrapper.clear();
    
            // reconnect the session using the original driver and connection string
            ss.getEventManager().removeListener(listener);
            ss.logout();
            if(originalLogLevel >= 0) {
                ss.setLogLevel(originalLogLevel);
            }
            ss.getLogin().setDriverClassName(originalDriverName);
            ss.getLogin().setConnectionString(originalConnectionString);
            ss.login();
            
            if(errorMsg.length() > 0) {
                fail(errorMsg);
            }
        }
    }

    // Bug 256284: Closing an EMF where the database is unavailable results in deployment exception on redeploy
    public void testEMFactoryCloseAndOpen(){
        if (isOnServer()) {
            // Uses DefaultConnector.
            return;
        }
        Assert.assertFalse("Warning Sybase Driver does not work with DriverWrapper, testEMCloseAndOpen can't run on this platform.",  JUnitTestCase.getServerSession().getPlatform().isSybase());
        
        // cache the driver name
        String driverName = ((EntityManagerFactoryImpl)getEntityManagerFactory()).getServerSession().getLogin().getDriverClassName();
        String originalConnectionString = ((EntityManagerFactoryImpl)getEntityManagerFactory()).getServerSession().getLogin().getConnectionString();
        
        // disconnect the session
        closeEntityManagerFactory();
        
        // setup the wrapper driver
        DriverWrapper.initialize(driverName);
        
        // connect the session using the wrapper driver
        HashMap properties = new HashMap(JUnitTestCaseHelper.getDatabaseProperties());
        properties.put(PersistenceUnitProperties.JDBC_DRIVER, DriverWrapper.class.getName());
        properties.put(PersistenceUnitProperties.JDBC_URL, DriverWrapper.codeUrl(originalConnectionString));
        getEntityManagerFactory(properties);

        // this connects the session
        EntityManager em = createEntityManager();
        
        // imitate disconnecting from network:
        // driver's connect method and any method on any connection will throw SQLException
        DriverWrapper.breakDriver();
        DriverWrapper.breakOldConnections();

        // close factory
        try {
            closeEntityManagerFactory();
        } finally {
            // clear the driver wrapper
            DriverWrapper.clear();
        }

        String errorMsg = "";
        //reconnect the session
        em = createEntityManager();
        //verify connections
        Iterator<ConnectionPool> itPools = ((EntityManagerImpl)em).getServerSession().getConnectionPools().values().iterator();
        while (itPools.hasNext()) {
            ConnectionPool pool = itPools.next();
            int disconnected = 0;
            for (int i=0; i < pool.getConnectionsAvailable().size(); i++) {
                if (!(pool.getConnectionsAvailable().get(i)).isConnected()) {
                    disconnected++;
                }
            }
            if (disconnected > 0) {
                errorMsg += pool.getName() + " has " + disconnected + " connections; ";
            }
        }
        if (errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    // Bug 332683 - Problems with ClientSession connections
    // testPostAcquirePreReleaseEvents tests verifies that postAcquireConnection and preReleaseConnection events are risen correctly.
    // The test is run in two configurations: with Internal and External connection pools.
    // Each test loops through several modes, the mode is a cartesian product of the following 3 choices: 
    //    pooled  vs  non-pooled modes;
    //    ExclusiveConnectionModes: Transactional  vs  Isolated  vs  Always;
    //    persist and commit  vs   read, begin transaction, persist, commit  vs  uow.beginEarlyTransaction, persist, commit.  
    static class AcquireRepair_ReleaseBreak_Listener extends SessionEventAdapter {
        HashSet<Accessor> acquiredConnections = new HashSet();
        public void postAcquireConnection(SessionEvent event) {
            Accessor accessor = (Accessor)event.getResult();
            if(acquiredConnections.contains(accessor)) {
                ((AbstractSession)event.getSession()).log(SessionLog.FINEST, SessionLog.CONNECTION, "AcquireRepair_ReleaseBreak_Listener.postAcquireConnection: risen two or more times in a row;", (Object[])null, accessor, false);
                throw new RuntimeException("AcquireRepair_ReleaseBreak_Listener.postAcquireConnection: risen two or more times in a row");
            } else {
                acquiredConnections.add(accessor);
            }
            ((AbstractSession)event.getSession()).log(SessionLog.FINEST, SessionLog.CONNECTION, "AcquireRepair_ReleaseBreak_Listener.postAcquireConnection: repairConnection;", (Object[])null, accessor, false);
            ((ConnectionWrapper)accessor.getConnection()).repairConnection();
        }
        public void preReleaseConnection(SessionEvent event) {
            Accessor accessor = (Accessor)event.getResult();
            if(!acquiredConnections.contains(accessor)) {
                ((AbstractSession)event.getSession()).log(SessionLog.FINEST, SessionLog.CONNECTION, "AcquireRepair_ReleaseBreak_Listener.preReleaseConnection: postAcquireConnection has not been risen;", (Object[])null, accessor, false);
                throw new RuntimeException("AcquireRepair_ReleaseBreak_Listener.preReleaseConnection: postAcquireConnection has not been risen");
            } else {
                acquiredConnections.remove(accessor);
            }
            ((AbstractSession)event.getSession()).log(SessionLog.FINEST, SessionLog.CONNECTION, "AcquireRepair_ReleaseBreak_Listener.preReleaseConnection: breakConnection;", (Object[])null, accessor, false);
            ((ConnectionWrapper)accessor.getConnection()).breakConnection();
        }
        public boolean hasAcquiredConnections() {
            return !acquiredConnections.isEmpty();
        }
    }
    public void testPostAcquirePreReleaseEvents_InternalConnectionPool() {
        internalTestPostAcquirePreReleaseEvents(false);
    }
    public void testPostAcquirePreReleaseEvents_ExternalConnectionPool() {
        internalTestPostAcquirePreReleaseEvents(true);
    }
    public void internalTestPostAcquirePreReleaseEvents(boolean useExternalConnectionPool){
        if (isOnServer()) {
            // Uses DefaultConnector.
            return;
        }
        
        ServerSession ss = ((EntityManagerFactoryImpl)getEntityManagerFactory()).getServerSession();
        
        Assert.assertFalse("Warning Sybase Driver does not work with DriverWrapper, testPostAcquirePreReleaseEvents can't run on this platform.",  ss.getPlatform().isSybase());
        if (ss.getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testPostAcquirePreReleaseEvents skipped for this platform, "
                            + "Symfoware platform doesn't support failover.");
            return;
        }
        
        // normally false; set to true for debug output for just this single test
        boolean shouldForceFinest = false;
        int originalLogLevel = -1; 
        
        // cache the original driver name and connection string.
        String originalDriverName = ss.getLogin().getDriverClassName();
        String originalConnectionString = ss.getLogin().getConnectionString();
        // cache original connector for external connection pool case
        Connector originalConnector = ss.getLogin().getConnector();
        
        // the new driver name and connection string to be used by the test
        String newDriverName = DriverWrapper.class.getName();
        String newConnectionString = DriverWrapper.codeUrl(originalConnectionString);
        
        // setup the wrapper driver
        DriverWrapper.initialize(originalDriverName);
        
        // The test need to connect with the new driver and connection string.
        // That could be done in JPA:
        //    // close the existing emf
        //    closeEntityManagerFactory();
        //    HashMap properties = new HashMap(JUnitTestCaseHelper.getDatabaseProperties());
        //    properties.put(PersistenceUnitProperties.JDBC_DRIVER, newDriverName);
        //    properties.put(PersistenceUnitProperties.JDBC_URL, newConnectionString);
        //    emf = getEntityManagerFactory(properties);
        // However this only works in case closeEntityManagerFactory disconnects the original ServerSession,
        // which requires the factory to be the only one using the persistence unit.
        // Alternative - and faster - approach is to disconnect the original session directly
        // and then reconnected it with the new driver and connection string.        
        ss.logout();
        if(useExternalConnectionPool) {
            ss.getLogin().setConnector(new JNDIConnector(new DataSourceImpl(null, newConnectionString, null, null)));
            ss.getLogin().useExternalConnectionPooling();
        } else {
            ss.getLogin().setDriverClassName(newDriverName);
            ss.getLogin().setConnectionString(newConnectionString);
        }
        if(shouldForceFinest) {
            if(ss.getLogLevel() != SessionLog.FINEST) {
                originalLogLevel = ss.getLogLevel();
                ss.setLogLevel(SessionLog.FINEST);
            }
        }
        // switch off reconnection
        boolean originalIsConnectionHealthValidatedOnError = ss.getLogin().isConnectionHealthValidatedOnError();
        ss.getLogin().setConnectionHealthValidatedOnError(false);

        // Using DriverWrapper the listener will repair connection on postAcquireConnection and break it on preReleaseConnection event.
        // Also the listener will verify that neither postAcquireConnection nor preReleaseConnection events not called two in a row.
        AcquireRepair_ReleaseBreak_Listener listener = new AcquireRepair_ReleaseBreak_Listener();  
        ss.getEventManager().addListener(listener);
        
        // Driver's connect method will still work, however any method called on any acquired connection will throw SQLException.
        // On postAcquireConnection connection will be repaired; on preReleaseConnection - broken again.
        ss.log(SessionLog.FINEST, SessionLog.CONNECTION, "testPostAcquirePreReleaseEvents: DriverWrapper.breakOldConnections(); DriverWrapper.breakNewConnections();", (Object[])null, null, false);
        DriverWrapper.breakOldConnections();
        DriverWrapper.breakNewConnections();
        
        ss.login();
        
        // test several configurations:
        // all exclusive connection modes
        String[] exclusiveConnectionModeArray = new String[]{ExclusiveConnectionMode.Transactional, ExclusiveConnectionMode.Isolated, ExclusiveConnectionMode.Always};

        // Normally the user wishing to use not pooled connection would specify user and password properties (and possibly db url, too).
        // However if these properties have the same values that those in the logging then no non pooled connection is created and the pooled one used instead.
        // In the test we must use the same user as already in the session login (don't know any others) that forces usage of ConnectionPolicy property.
        ConnectionPolicy connectionPolicy = (ConnectionPolicy)ss.getDefaultConnectionPolicy().clone();
        connectionPolicy.setLogin(ss.getLogin());
        connectionPolicy.setPoolName(null);
                
        try {
            HashMap emProperties = new HashMap();
            String mode, pooled = "", exclusiveConnectionMode;
            for(int k=0; k<2; k++) {
                if(k==1) {
                    //use non pooled connections
                    pooled = "non pooled; ";
                    emProperties.put(EntityManagerProperties.CONNECTION_POLICY, connectionPolicy);
                }
                for(int i=0; i<exclusiveConnectionModeArray.length; i++) {
                    exclusiveConnectionMode = exclusiveConnectionModeArray[i];
                    for(int j=0; j<3; j++) {
                        // either beginning early transaction or not 
                        boolean shouldBeginEarlyTransaction = (j==2);
                        boolean shouldReadBeforeTransaction = (j==1);
                        mode = pooled + exclusiveConnectionMode + (shouldBeginEarlyTransaction ? "; beginEarlyTransaction" : "") + (shouldReadBeforeTransaction ? "; readBeforeTransaction" : "");
                        ss.log(SessionLog.FINEST, SessionLog.CONNECTION, "testPostAcquirePreReleaseEvents: " + mode, (Object[])null, null, false);
                        emProperties.put(EntityManagerProperties.EXCLUSIVE_CONNECTION_MODE, exclusiveConnectionMode);
                        EntityManager em = createEntityManager(emProperties);
                        
                        if(shouldReadBeforeTransaction) {
                            em.find(Employee.class, 1);
                        }
                        Employee emp = null;
                        try{
                            em.getTransaction().begin();
                            
                            if(shouldBeginEarlyTransaction) {
                                em.unwrap(UnitOfWorkImpl.class).beginEarlyTransaction();
                            }
        
                            emp = new Employee();
                            emp.setFirstName("testPostAcquirePreReleaseEvents");
                            em.persist(emp);
                            em.getTransaction().commit();    
                        } finally {
                            // expected exception - connection is invalid and cannot be reconnected.
                            if(em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }    
                            closeEntityManager(em);
                        }
                        
                        if(listener.hasAcquiredConnections()) {
                            fail(mode + " connection was not passed to preReleaseConnection event");
                        }                    
                    }
                }
            }

        } finally {
            // clear the driver wrapper
            DriverWrapper.clear();
    
            // reconnect the session using the original driver and connection string
            ss.getEventManager().removeListener(listener);
            // clean-up
            // remove the inserted object
            EntityManager em = createEntityManager(); 
            em.getTransaction().begin();
            try {
                em.createQuery("DELETE FROM Employee e WHERE e.firstName = 'testPostAcquirePreReleaseEvents'");
                em.getTransaction().commit();
            } finally {
                if(em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }            
                closeEntityManager(em);

                ss.logout();
                if(originalLogLevel >= 0) {
                    ss.setLogLevel(originalLogLevel);
                }
                if(useExternalConnectionPool) {
                    ss.getLogin().setConnector(originalConnector);
                    ss.getLogin().dontUseExternalConnectionPooling();
                } else {
                    ss.getLogin().setDriverClassName(originalDriverName);
                    ss.getLogin().setConnectionString(originalConnectionString);
                }
                ss.getLogin().setConnectionHealthValidatedOnError(originalIsConnectionHealthValidatedOnError);
                ss.login();
            }
        }
    }

    /**
     * Tests that queries will be re-executed if a query fails from a dead connection. 
     */
    public void testDeadConnectionFailover(){
        if (JUnitTestCase.getServerSession().getPlatform().isSymfoware() || JUnitTestCase.getServerSession().getPlatform().isSybase()) {
            warning("Test testDeadConnectionFailover skipped for this platform, Sybase, Symfoware platform doesn't support failover.");
            return;
        }
        
        if (isOnServer()) {
            // Uses DefaultConnector.
            return;
        }
        
        ServerSession server = ((EntityManagerFactoryImpl)getEntityManagerFactory()).getServerSession();
                
        // cache the original driver name and connection string.
        String originalDriverName = server.getLogin().getDriverClassName();
        String originalConnectionString = server.getLogin().getConnectionString();
        
        // the new driver name and connection string to be used by the test
        String newDriverName = DriverWrapper.class.getName();
        String newConnectionString = DriverWrapper.codeUrl(originalConnectionString);
        
        // setup the wrapper driver
        DriverWrapper.initialize(originalDriverName);
        
        server.logout();
        server.getLogin().setDriverClassName(newDriverName);
        server.getLogin().setConnectionHealthValidatedOnError(true);
        server.getLogin().setConnectionString(newConnectionString);
        server.login();        
        try {
            EntityManager em = createEntityManager();
            em.createQuery("Select e from Employee e").getResultList();
            em.getTransaction().begin();
            em.persist(new Employee());
            em.getTransaction().commit();
            // test several configurations:
            // all exclusive connection modes
            String[] exclusiveConnectionModes = new String[]{ExclusiveConnectionMode.Transactional, ExclusiveConnectionMode.Isolated, ExclusiveConnectionMode.Always};
            for (String exclusiveConnectionMode : exclusiveConnectionModes) {
                try {
                    HashMap emProperties = new HashMap(1);
                    emProperties.put(EntityManagerProperties.EXCLUSIVE_CONNECTION_MODE, exclusiveConnectionMode);
                    em = createEntityManager(emProperties);
                    List<Employee> employees = em.createQuery("Select e from Employee e").getResultList();
                    DriverWrapper.breakOldConnections();
                    List<Employee> employees2 = em.createQuery("Select e from Employee e").getResultList();
                    if (employees.size() != employees2.size()) {
                        fail("Query results not the same after failure.");
                    }
                    DriverWrapper.breakOldConnections();
                    em.getTransaction().begin();
                    em.persist(new Employee());
                    DriverWrapper.breakOldConnections();
                    em.getTransaction().commit();
                    em.getTransaction().begin();
                    em.persist(new Employee());
                    em.flush();
                    DriverWrapper.breakOldConnections();
                    boolean failed = false;
                    try {
                        em.getTransaction().commit();
                    } catch (Exception shouldFail) {
                        failed = true;
                    }
                    if (!failed) {
                        fail("Retry should not work in a transaction.");
                    }
                } catch (Exception failed) {
                    fail("Retry did not work, mode:" + exclusiveConnectionMode + " error:" + failed);
                } finally {
                    try {
                        em.close();
                    } catch (Exception ignore) {}
                }
            }
        } finally {
            // clear the driver wrapper
            DriverWrapper.clear();
    
            // reconnect the session using the original driver and connection string
            server.logout();
            server.getLogin().setDriverClassName(originalDriverName);
            server.getLogin().setConnectionString(originalConnectionString);
            server.login();
        }
    }


    /**
     * Tests that a dead connection pool can fail over. 
     */
    public void testDeadPoolFailover(){
        if (JUnitTestCase.getServerSession().getPlatform().isSymfoware() || JUnitTestCase.getServerSession().getPlatform().isSybase()) {
            warning("Test testDeadConnectionFailover skipped for this platform, Sybase, Symfoware platform doesn't support failover.");
            return;
        }
        
        if (isOnServer()) {
            // Uses DefaultConnector.
            return;
        }
        
        ServerSession server = ((EntityManagerFactoryImpl)getEntityManagerFactory()).getServerSession();
                
        // cache the original driver name and connection string.
        DatabaseLogin originalLogin = (DatabaseLogin)server.getLogin().clone();
        
        // the new driver name and connection string to be used by the test
        String newDriverName = DriverWrapper.class.getName();
        String newConnectionString = DriverWrapper.codeUrl(originalLogin.getConnectionString());
        
        // setup the wrapper driver
        DriverWrapper.initialize(originalLogin.getDriverClassName());
        
        server.logout();
        server.getLogin().setDriverClassName(newDriverName);
        server.getLogin().setConnectionHealthValidatedOnError(true);
        server.getLogin().setConnectionString(newConnectionString);
        server.addConnectionPool("backup", originalLogin, 2, 4);
        server.getDefaultConnectionPool().addFailoverConnectionPool("backup");
        server.getReadConnectionPool().addFailoverConnectionPool("backup");
        server.login();
        try {
            EntityManager em = createEntityManager();
            em.createQuery("Select e from Employee e").getResultList();
            em.getTransaction().begin();
            em.persist(new Employee());
            em.getTransaction().commit();
            // test several configurations:
            // all exclusive connection modes
            String[] exclusiveConnectionModes = new String[]{ExclusiveConnectionMode.Transactional, ExclusiveConnectionMode.Isolated, ExclusiveConnectionMode.Always};
            for (String exclusiveConnectionMode : exclusiveConnectionModes) {
                try {
                    HashMap emProperties = new HashMap(1);
                    emProperties.put(EntityManagerProperties.EXCLUSIVE_CONNECTION_MODE, exclusiveConnectionMode);
                    em = createEntityManager(emProperties);
                    List<Employee> employees = em.createQuery("Select e from Employee e").getResultList();
                    DriverWrapper.breakAll();
                    List<Employee> employees2 = em.createQuery("Select e from Employee e").getResultList();
                    if (employees.size() != employees2.size()) {
                        fail("Query results not the same after failure.");
                    }
                    em.getTransaction().begin();
                    em.persist(new Employee());
                    em.getTransaction().commit();
                    em.getTransaction().begin();
                    em.persist(new Employee());
                    em.flush();
                    em.getTransaction().commit();
                } catch (Exception failed) {
                    fail("Retry did not work, mode:" + exclusiveConnectionMode + " error:" + failed);
                } finally {
                    try {
                        em.close();
                    } catch (Exception ignore) {}
                }
            }
        } finally {
            // clear the driver wrapper
            DriverWrapper.clear();
    
            // reconnect the session using the original driver and connection string
            server.logout();
            server.getConnectionPools().remove("backup");
            server.getDefaultConnectionPool().setFailoverConnectionPools(new ArrayList());
            server.getReadConnectionPool().setFailoverConnectionPools(new ArrayList());
            server.getLogin().setDriverClassName(originalLogin.getDriverClassName());
            server.getLogin().setConnectionString(originalLogin.getConnectionString());
            server.login();
        }
    }
    
    /**
     * This test ensures that the eclipselink.batch query hint works. It tests
     * two things.
     * 
     * 1. That the batch read attribute is properly added to the queyr 2. That
     * the query will execute
     * 
     * It does not do any verification that the batch reading feature actually
     * works. That is left for the batch reading testing to do.
     */
    public void testForUOWInSharedCacheWithBatchQueryHint() {
        if (isOnServer()) {
            // Can not unwrap on WLS.
            return;
        }

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
        closeEntityManager(em);
        clearCache();

        // org.eclipse.persistence.jpa.JpaQuery query =
        // (org.eclipse.persistence.jpa.JpaQuery)em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
        em = createEntityManager();
        beginTransaction(em);
        org.eclipse.persistence.jpa.JpaQuery query = (org.eclipse.persistence.jpa.JpaQuery) em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone' order by e.firstName");
        query.setHint(QueryHints.BATCH, "e.phoneNumbers");

        List resultList = query.getResultList();
        emp = (Employee) resultList.get(0);
        emp.setFirstName("somethingelse" + System.currentTimeMillis());

        // execute random other query
        em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Malone'").getResultList();
        ((Employee) resultList.get(1)).getPhoneNumbers().hashCode();

        commitTransaction(em);
        try {
            emp = (Employee) JpaHelper.getEntityManager(em).getServerSession().getIdentityMapAccessor().getFromIdentityMap(emp);
            assertNotNull("Error, phone numbers is empty.  Not Batch Read", emp.getPhoneNumbers());
            assertFalse("PhoneNumbers was empty.  This should not be the case as the test created phone numbers", emp.getPhoneNumbers().isEmpty());
            assertTrue("Phonee numbers was not an indirectList", emp.getPhoneNumbers() instanceof IndirectList);
            assertNotNull("valueholder was null in triggered batch attribute", ((IndirectList) emp.getPhoneNumbers()).getValueHolder());
            BatchValueHolder bvh = (BatchValueHolder) ((IndirectList) emp.getPhoneNumbers()).getValueHolder();
            if (bvh.getQuery() != null && bvh.getQuery().getSession() != null && bvh.getQuery().getSession().isUnitOfWork()) {
                fail("In Shared Cache a UOW was set within a BatchValueHolder's query object");
            }
            closeEntityManager(em);
        } finally {
            em = createEntityManager();
            beginTransaction(em);
            emp = em.find(Employee.class, id1);
            Iterator it = emp.getManagedEmployees().iterator();
            while (it.hasNext()) {
                Employee managedEmp = (Employee) it.next();
                it.remove();
                managedEmp.setManager(null);
                em.remove(managedEmp);
            }
            em.remove(emp);
            commitTransaction(em);
        }
    }
    
    public void testIsLoaded(){
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            Employee emp = new Employee();
            emp.setFirstName("Abe");
            emp.setLastName("Jones");
            Address addr = new Address();
            addr.setCity("Palo Alto");
            emp.setAddress(addr);
            
            PhoneNumber pn = new PhoneNumber();
            pn.setNumber("1234456");
            pn.setType("Home");
            emp.addPhoneNumber(pn);
            pn.setOwner(emp);
            em.persist(emp);
            em.flush();
            
            em.clear();
            clearCache();
        
            emp = em.find(Employee.class, emp.getId());
            PersistenceUnitUtil util = emf.getPersistenceUnitUtil();
            assertTrue("PersistenceUnitUtil says employee is not loaded when it is.", util.isLoaded(emp));
        } finally {
            rollbackTransaction(em);
        }
    }
    
    public void testIsLoadedAttribute(){
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            Employee emp = new Employee();
            emp.setFirstName("Abe");
            emp.setLastName("Jones");
            Address addr = new Address();
            addr.setCity("Palo Alto");
            emp.setAddress(addr);
            
            PhoneNumber pn = new PhoneNumber();
            pn.setNumber("1234456");
            pn.setType("Home");
            emp.addPhoneNumber(pn);
            pn.setOwner(emp);
            
            SmallProject project = new SmallProject();
            project.setName("Utility Testing");
            project.addTeamMember(emp);
            emp.addProject(project);
            em.persist(emp);
            em.flush();
            
            em.clear();
            clearCache();
            
            emp = em.find(Employee.class, emp.getId());
            PersistenceUnitUtil util = emf.getPersistenceUnitUtil();
            if (emp instanceof PersistenceWeaved){
                assertFalse("PersistenceUnitUtil says address is loaded when it is not", util.isLoaded(emp, "address"));
            } else {
                assertTrue("Non-weaved PersistenceUnitUtil says address is not loaded", util.isLoaded(emp, "address"));
            }
            assertFalse("PersistenceUnitUtil says projects is loaded when it is not", util.isLoaded(emp, "projects"));
            emp.getPhoneNumbers().size();
            assertTrue("PersistenceUnitUtil says phoneNumbers is not loaded when it is", util.isLoaded(emp, "phoneNumbers"));
            assertTrue("PersistenceUnitUtil says firstName is not loaded when it is", util.isLoaded(emp, "firstName"));
        } finally {
            rollbackTransaction(em);
        }
    }
    
    public void testGetIdentifier(){
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            Employee emp = new Employee();
            emp.setFirstName("Abe");
            emp.setLastName("Jones");
            Address addr = new Address();
            addr.setCity("Palo Alto");
            emp.setAddress(addr);
            
            PhoneNumber pn = new PhoneNumber();
            pn.setNumber("1234456");
            pn.setType("Home");
            emp.addPhoneNumber(pn);
            pn.setOwner(emp);
            em.persist(emp);
            em.flush();
            Integer id = emp.getId();
            
            em.clear();
            clearCache();
            
            emp = em.find(Employee.class, emp.getId());
            PersistenceUnitUtil util = emf.getPersistenceUnitUtil();
            Object retrievedId = util.getIdentifier(emp);
            assertTrue("Got an incorrect id from persistenceUtil.getIdentifier()", id.equals(retrievedId));
        } finally {
            rollbackTransaction(em);
        }
    }
    
    public void testIsLoadedWithReference(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            Employee emp = new Employee();
            emp.setFirstName("Abe");
            emp.setLastName("Jones");
            Address addr = new Address();
            addr.setCity("Palo Alto");
            emp.setAddress(addr);
            
            PhoneNumber pn = new PhoneNumber();
            pn.setNumber("1234456");
            pn.setType("Home");
            emp.addPhoneNumber(pn);
            pn.setOwner(emp);
            
            Employee manager = new Employee();
            manager.addManagedEmployee(emp);
            emp.setManager(manager);
            
            
            em.persist(emp);
            em.flush();
            
            em.clear();
            clearCache();
            
            emp = em.find(Employee.class, emp.getId());
            emp.getAddress().getCity();
            emp.getPhoneNumbers().size();
            
            ProviderUtil util = (new PersistenceProvider()).getProviderUtil();
            assertTrue("ProviderUtil did not return LOADED for isLoaded for address when it should.", util.isLoadedWithReference(emp, "address").equals(LoadState.LOADED));
            assertTrue("ProviderUtil did not return LOADED for isLoaded for phoneNumbers when it should.", util.isLoadedWithReference(emp, "phoneNumbers").equals(LoadState.LOADED));
            if (emp instanceof PersistenceWeaved){
                assertTrue("ProviderUtil did not return NOT_LOADED for isLoaded for manager when it should.", util.isLoadedWithReference(emp, "manager").equals(LoadState.NOT_LOADED));
            } else {
                assertTrue("(NonWeaved) ProviderUtil did not return LOADED for isLoaded for manager when it should.", util.isLoadedWithReference(emp, "manager").equals(LoadState.LOADED));
            }
            assertTrue("ProviderUtil did not return NOT_LOADED for isLoaded for projects when it should.", util.isLoadedWithReference(emp, "projects").equals(LoadState.NOT_LOADED));
        } finally {
            rollbackTransaction(em);
        }
    }
    
    public void testIsLoadedWithoutReference(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            Employee emp = new Employee();
            emp.setFirstName("Abe");
            emp.setLastName("Jones");
            Address addr = new Address();
            addr.setCity("Palo Alto");
            emp.setAddress(addr);
            
            PhoneNumber pn = new PhoneNumber();
            pn.setNumber("1234456");
            pn.setType("Home");
            emp.addPhoneNumber(pn);
            pn.setOwner(emp);
            
            Employee manager = new Employee();
            manager.addManagedEmployee(emp);
            emp.setManager(manager);
            
            em.persist(emp);
            em.flush();
            
            em.clear();
            clearCache();
            
            ProviderUtil util = (new PersistenceProvider()).getProviderUtil();
            
            if (emp instanceof PersistenceWeaved){
                assertTrue("ProviderUtil did not return LOADED for isLoaded when it should.", util.isLoaded(emp).equals(LoadState.LOADED));
                
                emp = em.getReference(Employee.class, emp.getId());
                assertTrue("ProviderUtil did not return NOT_LOADED for isLoaded when it should.", util.isLoaded(emp).equals(LoadState.NOT_LOADED));
            } else {
                assertTrue("(NonWeaved) ProviderUtil did not return UNKNOWN for isLoaded when it should.", util.isLoaded(emp).equals(LoadState.UNKNOWN));
                
                emp = em.getReference(Employee.class, emp.getId());
                assertTrue("(NonWeaved)  ProviderUtil did not return UNKNOWN for isLoaded when it should.", util.isLoaded(emp).equals(LoadState.UNKNOWN));
            }
            assertTrue("ProviderUtil did not return UNKNOWN for isLoaded when it should.", util.isLoaded(new NonEntity()).equals(LoadState.UNKNOWN));

        } finally {
            rollbackTransaction(em);
        }
    }
    
    public void testIsLoadedWithoutReferenceAttribute(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            Employee emp = new Employee();
            emp.setFirstName("Abe");
            emp.setLastName("Jones");
            Address addr = new Address();
            addr.setCity("Palo Alto");
            emp.setAddress(addr);
            
            PhoneNumber pn = new PhoneNumber();
            pn.setNumber("1234456");
            pn.setType("Home");
            emp.addPhoneNumber(pn);
            pn.setOwner(emp);
            
            Employee manager = new Employee();
            manager.addManagedEmployee(emp);
            emp.setManager(manager);
            
            em.persist(emp);
            em.flush();
            
            em.clear();
            clearCache();
            
            emp = em.find(Employee.class, emp.getId());
            emp.getAddress().getCity();
            emp.getPhoneNumbers().size();
            
            ProviderUtil util = (new PersistenceProvider()).getProviderUtil();
            if (emp instanceof PersistenceWeaved){
                assertTrue("ProviderUtil did not return LOADED for isLoaded for address when it should.", util.isLoadedWithReference(emp, "address").equals(LoadState.LOADED));
                assertTrue("ProviderUtil did not return NOT_LOADED for isLoaded for manager when it should.", util.isLoadedWithReference(emp, "manager").equals(LoadState.NOT_LOADED));
            } else {
                assertTrue("(Unweaved) ProviderUtil did not return LOADED for isLoaded for address when it should.", util.isLoadedWithReference(emp, "address").equals(LoadState.LOADED));
                assertTrue("(Unweaved) ProviderUtil did not return LOADED for isLoaded for manager when it should.", util.isLoadedWithReference(emp, "manager").equals(LoadState.LOADED));
            }
            assertTrue("ProviderUtil did not return LOADED for isLoaded for phoneNumbers when it should.", util.isLoadedWithReference(emp, "phoneNumbers").equals(LoadState.LOADED));
            assertTrue("ProviderUtil did not return NOT_LOADED for isLoaded for projects when it should.", util.isLoadedWithReference(emp, "projects").equals(LoadState.NOT_LOADED));

            assertTrue("ProviderUtil did not return UNKNOWN for isLoaded when it should.", util.isLoaded(new NonEntity()).equals(LoadState.UNKNOWN));
        } finally {
            rollbackTransaction(em);
        }
    }
    
    public void testGetHints(){
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("Select e from Employee e");
        query.setHint(QueryHints.CACHE_USAGE, CacheUsage.DoNotCheckCache);
        query.setHint(QueryHints.BIND_PARAMETERS, "");
        Map<String, Object> hints = query.getHints();
        assertTrue("Incorrect number of hints.", hints.size() == 2);
        assertTrue("CacheUsage hint missing.", hints.get(QueryHints.CACHE_USAGE).equals(CacheUsage.DoNotCheckCache));
        assertTrue("Bind parameters hint missing.", hints.get(QueryHints.BIND_PARAMETERS) != null);
        
        query = em.createQuery("Select a from Address a");
        hints = query.getHints();
        assertTrue("Hints is not null when it should be.", hints == null);
    }

    
    public void testTemporalOnClosedEm(){
        if (isOnServer()) {
            // Don't run this test on server.
            return;
        }

        EntityManager em = createEntityManager();
        Query numericParameterQuery = em.createQuery("Select e from Employee e where e.period.startDate = ?1");
        Query namedParameterQuery = em.createQuery("Select e from Employee e where e.period.startDate = :date");
        closeEntityManager(em);
        Exception caughtException = null;
        try{
            numericParameterQuery.setParameter(1, new Date(System.currentTimeMillis()), TemporalType.DATE);
        } catch (Exception e){
            caughtException = e;
        }
        assertTrue("Wrong Exception was caught when setting a numeric temporal Date parameter on a query with a closed em.", caughtException instanceof IllegalStateException);

        try{
            numericParameterQuery.setParameter(1, Calendar.getInstance(), TemporalType.DATE);
        } catch (Exception e){
            caughtException = e;
        }
        assertTrue("Wrong Exception was caught when setting a numeric temporal Calendar parameter on a query with a closed em.", caughtException instanceof IllegalStateException);

        try{
        	namedParameterQuery.setParameter("date", new Date(System.currentTimeMillis()), TemporalType.DATE);
        } catch (Exception e){
            caughtException = e;
        }
        assertTrue("Wrong Exception was caught when setting a named temporal Date parameter on a query with a closed em.", caughtException instanceof IllegalStateException);

        try{
        	namedParameterQuery.setParameter("date", Calendar.getInstance(), TemporalType.DATE);
        } catch (Exception e){
            caughtException = e;
        }
        assertTrue("Wrong Exception was caught when setting a named temporal Calendar parameter on a query with a closed em.", caughtException instanceof IllegalStateException);
    }
    
    public void testTransientMapping(){
        ServerSession session = getServerSession();
        ClassDescriptor descriptor = session.getClassDescriptor(Customer.class);
        assertTrue("There should not be a mapping for transientField.", descriptor.getMappingForAttributeName("transientField") == null);
    }

    public static class SessionNameCustomizer implements SessionCustomizer {
        static ServerSession ss;
        public void customize(Session session) throws Exception {
            ss = (ServerSession)session;
        }
    }
    public void testGenerateSessionNameFromConnectionProperties() {
        // the test requires passing properties to createEMF or createContainerEMF method.
        if (isOnServer()) {
            return;
        }
        
        String errorMsg = "";
        EntityManagerFactory emf;
        Map properties;
        String puName = "default";
        String customizer = "org.eclipse.persistence.testing.tests.jpa.advanced.EntityManagerJUnitTestSuite$SessionNameCustomizer";
        
        String myJtaDS = "myJtaDS";
        String myNonJtaDS = "myNonJtaDS";
        String myUrl = "myUrl";
        String myUser = "myUser";
        String myDriver = "myDriver";
        String myPassword = "myPassword";
        
        ServerSession originalSession = JUnitTestCase.getServerSession();
        String loggingLevel = originalSession.getSessionLog().getLevelString();
        SessionNameCustomizer.ss = null;
        
        // 0: The session name specified in persistence.xml is NOT overridden -> the existing session is used by emf.
        // If persistence.xml no longer specifies session name then comment out this part of the test.
        properties = new HashMap();
        // required by the test
        properties.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, customizer);
        // log on the same level as original session
        properties.put(PersistenceUnitProperties.LOGGING_LEVEL, loggingLevel);
        properties.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");
        // to override data source if one is specified in persistence.xml
        properties.put(PersistenceUnitProperties.JTA_DATASOURCE, "");
        // to override data source if one is specified in persistence.xml
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, "");
        properties.put(PersistenceUnitProperties.JDBC_URL, myUrl);
        properties.put(PersistenceUnitProperties.JDBC_USER, myUser);
        properties.put(PersistenceUnitProperties.JDBC_DRIVER, myDriver);
        properties.put(PersistenceUnitProperties.JDBC_PASSWORD, myPassword);
        emf = Persistence.createEntityManagerFactory(puName, properties);
        try {
            emf.createEntityManager();
        } catch (Exception ex) {
            // Ignore exception that probably caused by attempt to connect the session with invalid connection data provided in the properties.
        } finally {
            emf.close();
            if(SessionNameCustomizer.ss != null && SessionNameCustomizer.ss != originalSession) {
                errorMsg += "0: Session name NOT overridden by an empty string - original session expected; ";
            }
            // clear for the next test
            SessionNameCustomizer.ss = null;
        }
        
        // 1: New session with DefaultConnector with myUrl, myDriver, myUser, myPassword
        properties = new HashMap();
        // required by the test
        properties.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, customizer);
        // log on the same level as original session
        properties.put(PersistenceUnitProperties.LOGGING_LEVEL, loggingLevel);
        // to override SESSION_NAME if one is specified in persistence.xml
        properties.put(PersistenceUnitProperties.SESSION_NAME, "");
        properties.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");
        // to override data source if one is specified in persistence.xml
        properties.put(PersistenceUnitProperties.JTA_DATASOURCE, "");
        // to override data source if one is specified in persistence.xml
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, "");
        properties.put(PersistenceUnitProperties.JDBC_URL, myUrl);
        properties.put(PersistenceUnitProperties.JDBC_USER, myUser);
        properties.put(PersistenceUnitProperties.JDBC_DRIVER, myDriver);
        properties.put(PersistenceUnitProperties.JDBC_PASSWORD, myPassword);
        emf = Persistence.createEntityManagerFactory(puName, properties);
        try {
            emf.createEntityManager();
        } catch (Exception ex) {
            // Ignore exception that probably caused by attempt to connect the session with invalid connection data provided in the properties.
        } finally {
            emf.close();
            if(SessionNameCustomizer.ss == null && SessionNameCustomizer.ss == originalSession) {
                errorMsg += "1: New session expected; ";
            } else {
                if(SessionNameCustomizer.ss.getLogin().shouldUseExternalConnectionPooling()) {
                    errorMsg += "1: internal connection pooling expected; ";
                }
                if(SessionNameCustomizer.ss.getLogin().shouldUseExternalTransactionController()) {
                    errorMsg += "1: no externalTransactionController expected; ";
                }
                if(! myUser.equals(SessionNameCustomizer.ss.getLogin().getUserName())) {
                    errorMsg += "1: myUser expected; ";
                }
                Connector connector = SessionNameCustomizer.ss.getLogin().getConnector();
                if(connector instanceof DefaultConnector) {
                    if(! myUrl.equals(((DefaultConnector)connector).getDatabaseURL())) {
                        errorMsg += "1: myUrl expected; ";
                    }
                    if(! myDriver.equals(((DefaultConnector)connector).getDriverClassName())) {
                        errorMsg += "1: myDriver expected; ";
                    }
                } else {
                    errorMsg += "1: DefaultConnector expected; ";
                }
            }                
            // clear for the next test
            SessionNameCustomizer.ss = null;
        }

        
        // 2: New session with JNDIConnector with myJtaDs, myNonJtaDs
        properties = new HashMap();
        // required by the test
        properties.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, customizer);
        // log on the same level as original session
        properties.put(PersistenceUnitProperties.LOGGING_LEVEL, loggingLevel);
        // to override SESSION_NAME if one is specified in persistence.xml
        properties.put(PersistenceUnitProperties.SESSION_NAME, "");
        properties.put(PersistenceUnitProperties.TRANSACTION_TYPE, "JTA");
        properties.put(PersistenceUnitProperties.JTA_DATASOURCE, myJtaDS);
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, myNonJtaDS);
        // to override user if one is specified in persistence.xml
        properties.put(PersistenceUnitProperties.JDBC_USER, "");
        // note that there is no need to override JDBC_URL, JDBC_DRIVER, JDBC_PASSWORD with an empty string, they will be ignored.
        // JDBC_URL, JDBC_DRIVER - because data source(s) are specified; JDBC_PASSWORD - because JDBC_USER is not specified 
        emf = Persistence.createEntityManagerFactory(puName, properties);
        try {
            emf.createEntityManager();
        } catch (Exception ex) {
            // Ignore exception that probably caused by attempt to connect the session with invalid connection data provided in the properties.
        } finally {
            emf.close();
            if(SessionNameCustomizer.ss == null && SessionNameCustomizer.ss == originalSession) {
                errorMsg += "2: New session expected; ";
            } else {
                if(! SessionNameCustomizer.ss.getLogin().shouldUseExternalConnectionPooling()) {
                    errorMsg += "2: external connection pooling expected; ";
                }
                if(! SessionNameCustomizer.ss.getLogin().shouldUseExternalTransactionController()) {
                    errorMsg += "2: externalTransactionController expected; ";
                }
                if(SessionNameCustomizer.ss.getLogin().getUserName().length() > 0) {
                    errorMsg += "2: empty string user expected; ";
                }
                Connector connector = SessionNameCustomizer.ss.getLogin().getConnector();
                if(connector instanceof JNDIConnector) {
                    if(! myJtaDS.equals(((JNDIConnector)connector).getName())) {
                        errorMsg += "2: myJtaDS expected; ";
                    }
                } else {
                    errorMsg += "2: JNDIConnector expected; ";
                }

                if(! SessionNameCustomizer.ss.getReadConnectionPool().getLogin().shouldUseExternalConnectionPooling()) {
                    errorMsg += "2: resding: external connection pooling expected; ";
                }
                if(SessionNameCustomizer.ss.getReadConnectionPool().getLogin().shouldUseExternalTransactionController()) {
                    errorMsg += "2: reading no externalTransactionController expected; ";
                }
                if(SessionNameCustomizer.ss.getReadConnectionPool().getLogin().getUserName().length() > 0) {
                    errorMsg += "2: reading: empty string user expected; ";
                }
                Connector readConnector = ((DatasourceLogin)SessionNameCustomizer.ss.getReadConnectionPool().getLogin()).getConnector();
                if(readConnector instanceof JNDIConnector) {
                    if(! myNonJtaDS.equals(((JNDIConnector)readConnector).getName())) {
                        errorMsg += "2: reading: myNonJtaDS expected; ";
                    }
                } else {
                    errorMsg += "2: reading: JNDIConnector expected; ";
                }
            }                
            // clear for the next test
            SessionNameCustomizer.ss = null;
        }

        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testPreupdateEmbeddable(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp = new Employee();
        emp.setFirstName("testPreupdateEmbeddable");
        emp.setLastName("testPreupdateEmbeddable");
        EmploymentPeriod period = new EmploymentPeriod();
        period.setStartDate(Date.valueOf("2002-01-01"));
        emp.setPeriod(period);
        em.persist(emp);
        commitTransaction(em);
        closeEntityManager(em);
        clearCache();
        
        em = createEntityManager();
        beginTransaction(em);
        
        emp = em.find(Employee.class, emp.getId());
        emp.setFirstName("testPreupdateEmbeddable1");
        

        emp = em.merge(emp);

        em.flush();
        
        clearCache();
        emp = em.find(Employee.class, emp.getId());
        assertTrue("The endDate was not updated.", emp.getPeriod().getEndDate().equals(EmployeeListener.UPDATE_DATE));
        
        em.remove(emp);
        commitTransaction(em);
    }

    /**
     * Test for Bug 299147 - em.find isolated read-only entity throws exception 
     */
    public void testFindReadOnlyIsolated() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            // test query
            em.createQuery("SELECT ro FROM ReadOnlyIsolated ro").getResultList();
            // test find - this used to throw exception
            em.find(ReadOnlyIsolated.class, 1);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    // bug 274975
    public void testInheritanceQuery(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        LargeProject project = new LargeProject();
        em.persist(project);
        commitTransaction(em);
        int id = project.getId();
        clearCache();
        
        ClassDescriptor descriptor = getServerSession().getClassDescriptor(Project.class);
        ReadAllQuery query = new ReadAllQuery(Project.class);
        ExpressionBuilder b = query.getExpressionBuilder();
        query.addArgument("id", Integer.class); // declare bind variable in (parent) query

        ReportQuery subQuery = new ReportQuery();
        subQuery.setReferenceClass(Project.class); // use any dummy mapped class...
        SQLCall selectIdsCall = new SQLCall();
        String subSelect = "select p.PROJ_ID from CMP3_PROJECT p where p.PROJ_ID = #id"; // <= re-use bind variable in child query 
        selectIdsCall.setSQLString(subSelect);
        subQuery.setCall(selectIdsCall);

        Expression expr = b.get("id").in(subQuery);
        query.setSelectionCriteria(expr);

        // Now execute query with bind variable   
        // (using setShouldBindAllParameters(false) makes it work... 
        // but this is not good, in particular it prevents use of advanced queries with Oraclearrays as bind-variables!!!) 
        Vector params = new Vector(1);
        params.add(id);
        List res = (List) getServerSession().executeQuery(query, params);
        assertTrue(res.size() == 1);
    }
    
    public void testNullBasicMap(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            Buyer buyer = new Buyer();
            buyer.setName("Joe");
            buyer.setDescription("Test Buyer");
            buyer.setCreditCards(null);
            em.persist(buyer);
            em.flush();
            clearCache();
            buyer = em.find(Buyer.class, buyer.getId());
            assertTrue("Buyer was not properly persisted", buyer != null);
        } catch (NullPointerException ex){
            fail("NPE caught when persisting a null Map.");
        } finally {
            rollbackTransaction(em);
        }
        
    }
    
    public void testFlushClearFind(){
        Map properties = new HashMap();
        
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp = new Employee();
        emp.setFirstName("Cesc");
        emp.setLastName("Fabergass");
        em.persist(emp);
        commitTransaction(em);
           
        beginTransaction(em);
        emp = em.find(Employee.class, emp.getId());
        emp.setLastName("Fabregas");
        em.flush();
        em.clear();
    
        emp = em.find(Employee.class, emp.getId());
        commitTransaction(em);
        try{
            assertTrue("Employees name was returned from server cache, when it should not have been", emp.getLastName().equals("Fabregas"));
        } finally {
            beginTransaction(em);
            emp = em.find(Employee.class, emp.getId());
            em.remove(emp);
            commitTransaction(em);
        }
    }
    
    public void testFlushClearQueryPk(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp = new Employee();
        emp.setFirstName("Cesc");
        emp.setLastName("Fabergass");
        em.persist(emp);
        commitTransaction(em);
        
        beginTransaction(em);
        emp = em.find(Employee.class, emp.getId());
        emp.setLastName("Fabregas");
        em.flush();
        em.clear();

        emp = (Employee)em.createQuery("select e from Employee e where e.id = :id").setParameter("id", emp.getId()).getSingleResult();
        commitTransaction(em);
        try{
            assertTrue("Employees name was returned from server cache, when it should not have been", emp.getLastName().equals("Fabregas"));
        } finally {
            beginTransaction(em);
            emp = em.find(Employee.class, emp.getId());
            em.remove(emp);
            commitTransaction(em);
        }
    }
    
    public void testFlushClearQueryNonPK(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp = new Employee();
        emp.setFirstName("Cesc");
        emp.setLastName("Fabergass");
        em.persist(emp);
        commitTransaction(em);
        
        beginTransaction(em);
        emp = em.find(Employee.class, emp.getId());
        emp.setLastName("Fabregas");
        em.flush();
        em.clear();

        emp = (Employee)em.createQuery("select e from Employee e where e.firstName = :name").setParameter("name", emp.getFirstName()).getSingleResult();
        commitTransaction(em);
        try{
            assertTrue("Employees name was returned from server cache, when it should not have been", emp.getLastName().equals("Fabregas"));
        } finally {
            beginTransaction(em);
            emp = em.find(Employee.class, emp.getId());
            em.remove(emp);
            commitTransaction(em);
        }
    }
    
    //  Bug 324406 - Wrong Index in ReportItem when @Embeddable Objects are used in ReportQuery 
    public void testSelectEmbeddable(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp = new Employee();
        emp.setFirstName("Robin");
        emp.setLastName("Van Persie");
        EmploymentPeriod period = new EmploymentPeriod();
        ;
       
        period.setStartDate(new Date((new GregorianCalendar(2009, 1, 1)).getTimeInMillis()));
        period.setEndDate(new Date((new GregorianCalendar(2010, 1, 1)).getTimeInMillis()));
        emp.setPeriod(period);
        em.persist(emp);
        em.flush();
        em.clear();
        clearCache();
        
        EmployeeHolder results = (EmployeeHolder)em.createQuery("select new org.eclipse.persistence.testing.models.jpa.advanced.EmployeeHolder(e.id, e.period, e.firstName) from Employee e where e.id = :id").setParameter("id", emp.getId()).getSingleResult();
        assertTrue("Incorrect id", emp.getId().equals(results.getId()));
        assertTrue("Incorrect period start date", emp.getPeriod().getStartDate().equals(results.getPeriod().getStartDate()));
        assertTrue("Incorrect period end date", emp.getPeriod().getEndDate().equals(results.getPeriod().getEndDate()));
        assertTrue("Incorrect name", emp.getFirstName().equals(results.getName()));
        
        rollbackTransaction(em);
    }
    
    // Bug 320254 - Ensure we do not get an exception when using a batch hint that navigates through more than one descriptor
    public void testNestedBatchQueryHint(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Department dept = new Department();
        dept.setName("Parents");
        Employee emp = new Employee();
        emp.setFirstName("Dave");
        emp.setLastName("Daddy");
        dept.setDepartmentHead(emp);
        emp.setDepartment(dept);
        PhoneNumber pn = new PhoneNumber();
        pn.setNumber("1234567");
        pn.setAreaCode("613");
        pn.setType("Home");
        emp.addPhoneNumber(pn);
        em.persist(emp);
        em.persist(dept);
        em.persist(pn);
        em.flush();
        em.clear();
        clearCache();
        
        List results = em.createQuery("select d from ADV_DEPT d").setHint(QueryHints.FETCH, "d.departmentHead").setHint(QueryHints.BATCH, "d.departmentHead.phoneNumbers").getResultList();
        assertTrue("Wrong results returned.", results.size() == 1);
        
        dept = (Department)results.get(0);
        dept.getDepartmentHead().getPhoneNumbers().hashCode();
        rollbackTransaction(em);
    }
    
    // Bug 332683 - Problems with ClientSession connections
    // This test verifies that non pooled connection case works.
    public void testNonPooledConnection() {
        ServerSession ss = getServerSession();
        // Normally the user wishing to use not pooled connection would specify user and password properties (and possibly db url, too).
        // However if these properties have the same values that those in the logging then no non pooled connection is created and the pooled one used instead.
        // In the test we must use the same user as already in the session login (don't know any others) that forces usage of ConnectionPolicy property.
        ConnectionPolicy connectionPolicy = (ConnectionPolicy)ss.getDefaultConnectionPolicy().clone();
        connectionPolicy.setLogin(ss.getLogin());
        connectionPolicy.setPoolName(null);
        EntityManager em;
        boolean isEmInjected = isOnServer() && ss.getLogin().shouldUseExternalTransactionController();
        boolean isSpring = isOnServer() && getServerPlatform().isSpring();
        if (isEmInjected) {
            em = createEntityManager();
            // In server jta case need a transaction - otherwise the wrapped EntityManagerImpl is not kept.
            beginTransaction(em);
            em.setProperty(EntityManagerProperties.CONNECTION_POLICY, connectionPolicy);
        } else {
            EntityManagerFactory emFactory = getEntityManagerFactory();
            Map properties = new HashMap(1);
            properties.put(EntityManagerProperties.CONNECTION_POLICY, connectionPolicy);
            em = emFactory.createEntityManager(properties);
            if(isSpring) {
                em.getTransaction().begin();
            } else {
                beginTransaction(em);
            }
        }
        try {
            // native query triggers early begin transaction
            em.createNativeQuery("SELECT F_NAME FROM CMP3_EMPLOYEE").getResultList();
            // verify that the connection is really not pooled.
            assertTrue("Test problem: connection should be not pooled", em.unwrap(UnitOfWork.class).getParent().getAccessor().getPool() == null);
        } finally {
            if(isSpring) {
                em.getTransaction().rollback();
            } else {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
    
    //  Bug 332464 - EntityManager running out of connections in exclusive isolated client session mode 
    public void testExclusiveIsolatedLeaksConnectionOnClear() {
        ServerSession ss = getServerSession();
        EntityManager em;
        boolean isEmInjected = isOnServer() && ss.getLogin().shouldUseExternalTransactionController();
        boolean isSpring = isOnServer() && getServerPlatform().isSpring();
        if (isEmInjected) {
            em = createEntityManager();
            // In server jta case need a transaction - otherwise the wrapped EntityManagerImpl is not kept.
            beginTransaction(em);
            em.setProperty(EntityManagerProperties.EXCLUSIVE_CONNECTION_MODE, "Always");
        } else {
            EntityManagerFactory emFactory = getEntityManagerFactory();
            Map properties = new HashMap(1);
            properties.put(EntityManagerProperties.EXCLUSIVE_CONNECTION_MODE, "Always");
            em = emFactory.createEntityManager(properties);
            if(isSpring) {
                em.getTransaction().begin();
            } else {
                beginTransaction(em);
            }
        }

        // any query on ExclusiveIsolated session triggers the exclusive connection to be acquired.
        em.createQuery("SELECT e FROM Employee e").getResultList();
        // the exclusive connection
        Accessor accessor = em.unwrap(UnitOfWork.class).getParent().getAccessor(); 
        // connection is still held because it's an ExclusiveIsolatedSession
        if(isSpring) {
            em.getTransaction().commit();
        } else {
            commitTransaction(em);
        }
        // before the bug was fixed that used to simply nullify the uow without releasing it and its parent ExclusiveIsolatedSession.
        em.clear();
        // closing EntityManager should release the exclusive connection.
        closeEntityManager(em);
        
        // verify that the connection has been released
        boolean released;
        ConnectionPool pool = ss.getDefaultConnectionPool();
        if(ss.getLogin().shouldUseExternalConnectionPooling()) {
            // external pool case
            released = accessor.getConnection() == null;
        } else {
            // intrenal pool case
            released = pool.getConnectionsAvailable().contains(accessor);
        }
        
        if(!released) {
            // release connection
            pool.releaseConnection(accessor);
            fail("Exclusive connection has not been released after the EntityManager has been closed.");
        }
    }
    
    // Bug 331692
    public void testSetTargetQueryOneToMany(){
		if (isOnServer()) {
            return;
        }
        
        EntityManager em = createEntityManager("customizeAddTarget");
        beginTransaction(em);
        Employee emp = new Employee();
        emp.setFirstName("Nick");
        em.persist(emp);
        em.flush();
        emp.addDealer(new Dealer());
        em.flush();
        em.clear();
        clearCache("customizeAddTarget");
        
        emp = em.find(Employee.class, emp.getId());
        
        assertTrue("The add Target Query was not correctly customized", emp.getFirstName() == null);
        
        rollbackTransaction(em);
    }
}

