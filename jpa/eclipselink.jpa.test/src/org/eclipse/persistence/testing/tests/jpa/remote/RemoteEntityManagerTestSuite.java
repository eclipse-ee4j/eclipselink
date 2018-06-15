/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     27/07/2010 - 2.1.1 Sabine Heider
//          304650: fix left over entity data interfering with testSetRollbackOnly
package org.eclipse.persistence.testing.tests.jpa.remote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import junit.framework.*;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.EntityManagerJUnitTestSuite;

/**
 * Test the EntityManager API using a remote EntityManager.
 */
public class RemoteEntityManagerTestSuite extends EntityManagerJUnitTestSuite {

    public RemoteEntityManagerTestSuite() {
        super();
    }

    public RemoteEntityManagerTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("RemoteEntityManagerTestSuite");
        suite.addTest(new RemoteEntityManagerTestSuite("testSetup"));

        List<String> tests = new ArrayList<String>();
        tests.add("testWeaving");
        tests.add("testClearEntityManagerWithoutPersistenceContext");
        tests.add("testUpdateAllProjects");
        tests.add("testUpdateUsingTempStorage");
        //tests.add("testSequenceObjectDefinition");
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
        tests.add("testRefreshEntityWithoutCache");
        tests.add("testDoubleMerge");
        tests.add("testDescriptorNamedQueryForMultipleQueries");
        tests.add("testDescriptorNamedQuery");
        tests.add("testClearEntityManagerWithoutPersistenceContextSimulateJTA");
        //tests.add("testMultipleEntityManagerFactories");
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
        //tests.add("testParallelMultipleFactories");
        //tests.add("testMultipleFactories");
        //tests.add("testPersistenceProperties");
        tests.add("testBeginTransactionCloseCommitTransaction");
        tests.add("testBeginTransactionClose");
        tests.add("testClose");
        tests.add("testPersistOnNonEntity");
        tests.add("testWRITELock");
        /*tests.add("testReadTransactionIsolation_OriginalInCache_UpdateAll_Refresh_Flush");
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
        tests.add("testReadTransactionIsolation_CustomUpdate");*/
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
        //tests.add("testSequencePreallocationUsingCallbackTest");
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
        tests.add("testCycleReferencesWithNonNullableField");
        Collections.sort(tests);
        for (String test : tests) {
            suite.addTest(new RemoteEntityManagerTestSuite(test));
        }

        return suite;
    }

    @Override
    public String getPersistenceUnitName() {
        return "remote";
    }

    @Override
    public Map getPersistenceProperties() {
        Map properties = new HashMap();
        properties.put(PersistenceUnitProperties.LOGGING_LEVEL, super.getPersistenceProperties().get(PersistenceUnitProperties.LOGGING_LEVEL));
        return properties;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        createEntityManager("remote-server").close();
        super.testSetup();
    }

}
