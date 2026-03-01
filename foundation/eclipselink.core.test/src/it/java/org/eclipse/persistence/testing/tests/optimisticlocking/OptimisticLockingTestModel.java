/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.optimisticlocking;

import org.eclipse.persistence.descriptors.AllFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.ChangedFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.SelectedFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.TimestampLockingPolicy;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.models.optimisticlocking.Guitar;
import org.eclipse.persistence.testing.models.optimisticlocking.LockInAggregateObject;
import org.eclipse.persistence.testing.models.optimisticlocking.LockInCache;
import org.eclipse.persistence.testing.models.optimisticlocking.LockInObject;
import org.eclipse.persistence.testing.models.optimisticlocking.OptimisticLockingSystem;
import org.eclipse.persistence.testing.models.optimisticlocking.RockBand;
import org.eclipse.persistence.testing.models.optimisticlocking.RockMusician;
import org.eclipse.persistence.testing.models.optimisticlocking.TimestampInAggregateObject;
import org.eclipse.persistence.testing.models.optimisticlocking.TimestampInCache;
import org.eclipse.persistence.testing.models.optimisticlocking.TimestampInObject;
import org.eclipse.persistence.testing.tests.optimisticlocking.cascaded.BarSystem;
import org.eclipse.persistence.testing.tests.optimisticlocking.cascaded.Cascaded121OptimisticLockingTest;
import org.eclipse.persistence.testing.tests.optimisticlocking.cascaded.Cascaded12MInheritanceListOnSuperOptimisticLockingTest;
import org.eclipse.persistence.testing.tests.optimisticlocking.cascaded.Cascaded12MInheritanceOptimisticLockingTest;
import org.eclipse.persistence.testing.tests.optimisticlocking.cascaded.Cascaded12MOptimisticLockingTest;
import org.eclipse.persistence.testing.tests.optimisticlocking.cascaded.CascadedCollectionOptimisticLockingTest;
import org.eclipse.persistence.testing.tests.optimisticlocking.cascaded.CascadedMultiLevel121OptimisticLockingTest;
import org.eclipse.persistence.testing.tests.optimisticlocking.cascaded.CascadedMultiLevel12MOptimisticLockingTest;
import org.eclipse.persistence.testing.tests.optimisticlocking.cascaded.CascadedMultiLevelCollectionOptimisticLockingTest;
import org.eclipse.persistence.testing.tests.optimisticlocking.cascaded.CascadedVersionLockingMultiplePrivateOwnedTest;

public class OptimisticLockingTestModel extends TestModel {
    // No state.
    public OptimisticLockingTestModel() {
        setDescription("This model tests EclipseLink optimistic locking features.");
    }

    @Override
    public void addRequiredSystems() {
        addRequiredSystem(new OptimisticLockingSystem());
        addRequiredSystem(new BarSystem());
    }

    @Override
    public void addTests() {
        addTest(getOptimisticLockingTestSuite());
        addTest(getCascadeOptimisticLockingTestSuite());
        addTest(getLockingExceptionTestSuite());
        addTest(getUpdateNullValueOptimisticLockingTestSuite());
    }

    public static TestSuite getLockingExceptionTestSuite() {
        TestSuite suite;

        suite = new TestSuite();
        suite.setName("LockingExceptionTestSuite");

        suite.addTest(new OptimisticLockingPolicyDeleteRowTest(Guitar.class));
        suite.addTest(new OptimisticLockingPolicyDeleteRowTest(RockMusician.class));
        suite.addTest(new OptimisticLockingPolicyDeleteRowTest(RockBand.class));

        suite.addTest(new OptimisticLockingPolicyChangedValueUpdateTest(Guitar.class));
        suite.addTest(new OptimisticLockingPolicyChangedValueUpdateTest(RockMusician.class));
        suite.addTest(new OptimisticLockingPolicyChangedValueUpdateTest(RockBand.class));

        suite.addTest(new OptimisticLockingPolicyUpdateTest(Guitar.class));
        suite.addTest(new OptimisticLockingPolicyUpdateTest(RockMusician.class));
        suite.addTest(new OptimisticLockingPolicyUpdateTest(RockBand.class));

        return suite;
    }

    public static TestSuite getOptimisticLockingTestSuite() {
        TestSuite suite;

        suite = new TestSuite();
        suite.setName("OptimisticLockingTestSuite");
        suite.setDescription("This suite tests the functionality of the optimistic locking policy.");

        suite.addTest(new OptimisticLockingUpdateTest(LockInCache.class, true));
        suite.addTest(new OptimisticLockingDeleteTest(LockInCache.class, true));
        suite.addTest(new OptimisticLockingInsertTest(new LockInCache()));
        suite.addTest(new OptimisticLockingUpdateTest(LockInObject.class, true));
        suite.addTest(new OptimisticLockingDeleteTest(LockInObject.class, true));
        suite.addTest(new OptimisticLockingInsertTest(new LockInObject()));
        suite.addTest(new OptimisticLockingUpdateTest(TimestampInCache.class, true));
        suite.addTest(new OptimisticLockingDeleteTest(TimestampInCache.class, true));
        suite.addTest(new OptimisticLockingInsertTest(new TimestampInCache()));
        suite.addTest(new OptimisticLockingUpdateTest(TimestampInObject.class, true));
        suite.addTest(new OptimisticLockingDeleteTest(TimestampInObject.class, true));
        suite.addTest(new OptimisticLockingInsertTest(new TimestampInObject()));
        suite.addTest(new OptimisticLockingPolicyUpdateWithUOWTest());

        suite.addTest(new OptimisticLockingUpdateTest(TimestampInAggregateObject.class, true));
        suite.addTest(new OptimisticLockingDeleteTest(TimestampInAggregateObject.class, true));
        suite.addTest(new OptimisticLockingInsertTest(new TimestampInAggregateObject()));

        suite.addTest(new OptimisticLockingUpdateTest(LockInAggregateObject.class, true));
        suite.addTest(new OptimisticLockingDeleteTest(LockInAggregateObject.class, true));
        suite.addTest(new OptimisticLockingInsertTest(new LockInAggregateObject()));
        suite.addTest(new TimestampNewObjectInCache(LockInObject.example1()));
        suite.addTest(new TimestampNewObjectInCache(TimestampInObject.example1()));
        suite.addTest(new ChangeSetOptimisticLockingUpdateTest(TimestampInAggregateObject.class));
        suite.addTest(new ChangeSetOptimisticLockingUpdateTest(LockInAggregateObject.class));
        suite.addTest(new ChangeSetOptimisticLockingUpdateTest(TimestampInCache.class));
        suite.addTest(new ChangeSetOptimisticLockingUpdateTest(LockInCache.class));
        suite.addTest(new ChangeSetOptimisticLockingUpdateTest(TimestampInObject.class));
        suite.addTest(new ChangeSetOptimisticLockingUpdateTest(LockInObject.class));
        suite.addTest(new ChangeSetOptimisticLockingInsertTest(TimestampInAggregateObject.class));
        suite.addTest(new ChangeSetOptimisticLockingInsertTest(LockInAggregateObject.class));
        suite.addTest(new ChangeSetOptimisticLockingInsertTest(TimestampInCache.class));
        suite.addTest(new ChangeSetOptimisticLockingInsertTest(LockInCache.class));
        suite.addTest(new ChangeSetOptimisticLockingInsertTest(TimestampInObject.class));
        suite.addTest(new ChangeSetOptimisticLockingInsertTest(LockInObject.class));
        suite.addTest(new WriteLockValueSerializationTest());

        suite.addTest(new FieldsLockingCachedUpdateCallsTest());

        // EL bug 247884 - NullPointerException using Timestamp (server) based optimistic locking and UpdateAllQuery
        suite.addTest(new UpdateAllWithTimestampLockingTest());
        // EL bug 422610 - Null aggregate with lock field using TimestampLockingPolicy causes NPE on change calculation
        suite.addTest(new WriteNullAggregateWithTimestampLockingTest());

        return suite;
    }

    public static TestSuite getCascadeOptimisticLockingTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("CascadeOptimisticLockingTestSuite");
        suite.setDescription("This suite tests the functionality of the cascade optimistic locking policy.");

        suite.addTest(new Cascaded121OptimisticLockingTest());
        suite.addTest(new Cascaded12MOptimisticLockingTest());
        suite.addTest(new CascadedCollectionOptimisticLockingTest());
        suite.addTest(new CascadedMultiLevel121OptimisticLockingTest());
        suite.addTest(new CascadedMultiLevel12MOptimisticLockingTest());
        suite.addTest(new CascadedMultiLevelCollectionOptimisticLockingTest());
        suite.addTest(new Cascaded12MInheritanceOptimisticLockingTest());
        suite.addTest(new Cascaded12MInheritanceListOnSuperOptimisticLockingTest());
        // EL bug 342632
        suite.addTest(new CascadedVersionLockingMultiplePrivateOwnedTest());

        return suite;
    }

    // EL bug 319759
    public static TestSuite getUpdateNullValueOptimisticLockingTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("UpdateNullValueOptimisticLockingTestSuite");
        suite.setDescription("Tests the functionality of updating fields with a null database value, with optimistic locking");

        final Class<?>[] policies = { AllFieldsLockingPolicy.class, SelectedFieldsLockingPolicy.class,
                ChangedFieldsLockingPolicy.class, VersionLockingPolicy.class, TimestampLockingPolicy.class };
        // done this way for test ordering
        for (Class<?> policy3 : policies) {
            suite.addTest(new UpdateNullDirectToFieldValueTest(policy3));
        }
        for (Class<?> policy2 : policies) {
            suite.addTest(new UpdateNullOneToOneValueTest(policy2));
        }
        for (Class<?> policy1 : policies) {
            suite.addTest(new UpdateNullAggregateValueTest(policy1));
        }
        for (Class<?> element : policies) {
            suite.addTest(new UpdateNullOneToManyValueTest(element));
        }
        for (Class<?> item : policies) {
            suite.addTest(new UpdateNullOneToManyValueTest2(item));
        }
        for (Class<?> value : policies) {
            suite.addTest(new UpdateNullTransformationValueTest(value));
        }
        for (Class<?> aClass : policies) {
            suite.addTest(new UpdateNullVariableOneToOneValueTest(aClass));
        }
        for (Class<?> policy : policies) {
            suite.addTest(new UpdateNullManyToManyValueTest(policy));
        }

        return suite;
    }

}
