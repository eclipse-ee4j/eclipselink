/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Iterator;

import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.tests.writing.ComplexUpdateTest;
import org.eclipse.persistence.testing.tests.writing.UpdateChangeNothingTest;
import org.eclipse.persistence.testing.tests.writing.UpdateChangeObjectTest;
import org.eclipse.persistence.testing.tests.writing.UpdateChangeValueTest;
import org.eclipse.persistence.testing.tests.writing.UpdateDeepOwnershipTest;
import org.eclipse.persistence.testing.tests.writing.UpdateToNullTest;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;


/**
 * This model is used to test the unit of work on an isolated client/server session.
 */
public class UnitOfWorkIsolatedAlwaysTestModel extends UnitOfWorkClientSessionTestModel {

    public void setup() {
        for (Iterator descriptors = getSession().getDescriptors().values().iterator(); descriptors.hasNext(); ) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_CACHE_ALWAYS);
        }
        super.setup();
    }

    public void reset() {
        for (Iterator descriptors = getSession().getDescriptors().values().iterator(); descriptors.hasNext(); ) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_NEW_DATA_AFTER_TRANSACTION);
        }
        super.reset();
    }

    /**
     * Add all unit of work test suite tests that are runnable with an isolated unit of work.
     */
    public void addTests() {
        addTest(new MergeCloneWithReferencesWithNullTest());
        addTest(new MergeCloneWithReferencesTransparentIndirectionTest());
        //bug 4518570
        //addTest(new UnitOfWorkRevertAndResumeWithNewTest()); - Requires revert from cache.
        //bug 4544221
        addTest(new UnitOfWorkRevertWithNewObjectTest());
        //bug 4569755
        addTest(new UnitOfWorkNullPrimaryKeyTest());

        PopulationManager manager = PopulationManager.getDefaultManager();
        org.eclipse.persistence.testing.models.employee.domain.Employee employee = 
            (org.eclipse.persistence.testing.models.employee.domain.Employee)manager.getObject(org.eclipse.persistence.testing.models.employee.domain.Employee.class, 
                                                                                      "0001");

        // Tests with using unit of work.
        ComplexUpdateTest test = new UpdateToNullTest(employee);
        test.usesUnitOfWork = true;
        addTest(test);

        test = new UpdateChangeValueTest(employee);
        test.usesUnitOfWork = true;
        addTest(test);

        test = new UpdateChangeNothingTest(employee);
        test.usesUnitOfWork = true;
        addTest(test);

        test = new UpdateChangeObjectTest(employee);
        test.usesUnitOfWork = true;
        addTest(test);

        test = 
new UpdateDeepOwnershipTest((org.eclipse.persistence.testing.models.ownership.ObjectA)manager.getObject(org.eclipse.persistence.testing.models.ownership.ObjectA.class, 
                                                                                               "example1"));
        test.usesUnitOfWork = true;
        addTest(test);

        //addTest(new BidirectionalInsertTest(true)); - Requires merge.
        addTest(new NestedUnitOfWorkTest(employee));
        addTest(new NestedUnitOfWorkMultipleCommitTest(employee));
        addTest(new DeepNestedUnitOfWorkTest(employee));
        addTest(new MultipleUnitOfWorkTest(employee));
        addTest(new InsertNewObjectTest());
        //addTest(new ComplexMultipleUnitOfWorkTest()); - Requires merge.
        addTest(new FaultyUnitOfWorkTest());
        addTest(new LockFailureUnitOfWorkTest());
        //addTest(new UnitOfWorkResumeTest(employee)); - Requires merge.
        addTest(new UnitOfWorkResumeOnFailureTest(employee));
        addTest(new DeletingFromParentSessionTest());
        //addTest(new org.eclipse.persistence.testing.models.mapping.EqualObjectUnitOfWorkTest()); - Requires merge.
        addTest(new NoIMWithValueHolderTest());
        addTest(new UnitOfWorkRevertTest(employee));
        //addTest(new DeepMergeCloneSerializedTest()); - Requires merge.
        //addTest(new DeepMergeCloneIndirectionTest()); - Requires merge.
        //addTest(new RegisterNewObjectTest()); - Requires merge.
        addTest(new NoIdentityMapUnitOfWorkTest());
        addTest(new RefreshObjectNoIdentityMapUnitOfWorkTest());
        addTest(new RelationshipTreeInsertTest());
        addTest(new UnitOfWorkComplexRefreshTest());
        addTest(new ViolateObjectSpaceTest());
        //code coverage
        addTest(new NoValidationWithInitIdentityMaps());
        addTest(new NoIdentityTest());
        addTest(new NoIdentityMergeCloneTest());
        addTest(new org.eclipse.persistence.testing.tests.mapping.BiDirectionInsertOrderTest());
        addTest(new UnitOfWorkCommitResumeOnFailureNoFailureTest(employee));
        addTest(new UnitOfWorkCommitAndResume(employee));
        addTest(new NestedUnitOfWorkQuery());
        addTest(new DeleteAndConform());
        //addTest(new NullAggregateTest()); - Requires merge.
        addTest(new UOWHasOnlyDeletesTest());
        //CR 2728
        addTest(new RegisterNewObjectInIdentityMapNoSeqTest(IdValidation.NULL, false, false));
        addTest(new RegisterNewObjectInIdentityMapNoSeqTest(IdValidation.ZERO, false, false));
        addTest(new RegisterNewObjectInIdentityMapNoSeqTest(IdValidation.NEGATIVE, false, false));
        addTest(new RegisterNewObjectInIdentityMapNoSeqTest(IdValidation.NULL, true, false));
        addTest(new RegisterNewObjectInIdentityMapNoSeqTest(IdValidation.ZERO, true, false));
        addTest(new RegisterNewObjectInIdentityMapNoSeqTest(IdValidation.NEGATIVE, true, false));
        addTest(new RegisterNewObjectInIdentityMapNoSeqTest(IdValidation.NULL, true, true));
        addTest(new RegisterNewObjectInIdentityMapNoSeqTest(IdValidation.ZERO, true, true));
        addTest(new RegisterNewObjectInIdentityMapNoSeqTest(IdValidation.NEGATIVE, true, true));
        //CR 2783
        addTest(new NestedUnitOfWorkDeleteNewObjectTest());
        //bug 3115160
        addTest(new NestedUnitOfWorkDeleteNestedNewObjectTest());
        //bug 3132979
        addTest(new NestedUnitOfWorkDeleteConformedNestedNewObjectTest());
        addTest(new DoubleNestedUnitOfWorkDeleteConformedNestedNewObjectTest());
        //bug 3228185
        addTest(new NestedUnitOfWorkNewObjectWithIndirectionTest());

        //CR#3216
        addTest(new UnitOfWorkDeleteNoValidationTest());
        addTest(new ConcurrentNewObjectTest());
        //addTest(new ConcurrentReadOnInsertTest()); - Test requires merge.
        addTest(new ConcurrentRefreshOnUpdateTest());
        addTest(new ConcurrentRefreshOnCloneTest());
        //CR 4094 
        addTest(new GetIdentityMapFromUOWForREADONLYClassTest());
        addTest(new UnitOfWorkCommitToDatabaseTest());
        addTest(new UnitOfWorkInitializeAllIdentityMapsTest());
        //CR#4204
        addTest(new WasTransactionBegunPrematurelyRollbackTest());

        //code coverage
        addTest(new CanChangeReadOnlySetTest());

        // code coverage
        addTest(new GetFromNewObjectWithConformTest());
        //addTest(new UOWCommitAndResumeWithPreCalcChangeSet(employee)); - Requires merge.
        addTest(new PerformDeletesFirstTest());

        // bug 3815959
        addTest(new PerformDeletesFirstIgnoreUpdateTest());
        addTest(new PerformDeletesFirstIgnoreUpdateTest2());

        // bug 2612331
        addTest(new CreateDeleteCreateTest());
        //bug 2612602
        //addTest(new WorkingCloneCopyPolicyTest()); - Requires non-isolated.
        addTest(new UnregisteredNewObjectOptimisticLockTest());

        //bug 3510459
        addTest(new DoubleNestedUnitOfWorkRegisterNewObjectTest());
        //bug 3582102
        addTest(new LockOnCloneTest());
        addTest(new LockOnCloneDeadlockAvoidanceTest());

        // bug 3287196
        addTest(new GetObjectFromIdentityMapTest());

        //bug 3656068
        //addTest(new ConcurrentReadOnUpdateWithEarlyTransTest()); - Requires non-isolated.

        addTest(new MergeDeadIndirectionTest());
        //bug 4071929
        addTest(new UnitOfWorkConcurrentRevertTest());

        //Add new tests here, if any.
        addTest(new CommitAfterExecuteModifyQueryDuringTransTest());

        //bug 4364283
        addTest(new AllChangeSetsTest());

        //bug 4438127
        //addTest(new NewObjectIdentityTest()); - Requires merge.

        //bug 4453001
        addTest(new ErrorOnInsertTest());

        addTest(new CollectionMappingMergeObjectTest());
        addTest(new ExceptionsRaisedUnitOfWorkTest());

        //bug 4736360    
        addTest(new NestedUOWWithNewObjectRegisteredTwiceTest());
        
        addTest(new NestedUnitOfWorkReadOnlyClassTest());
    }
}
