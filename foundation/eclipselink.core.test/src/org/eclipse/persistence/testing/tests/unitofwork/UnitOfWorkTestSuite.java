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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.ownership.ObjectA;
import org.eclipse.persistence.testing.tests.writing.BidirectionalInsertTest;
import org.eclipse.persistence.testing.tests.writing.ComplexUpdateTest;
import org.eclipse.persistence.testing.tests.writing.UpdateChangeNothingTest;
import org.eclipse.persistence.testing.tests.writing.UpdateChangeObjectTest;
import org.eclipse.persistence.testing.tests.writing.UpdateChangeValueTest;
import org.eclipse.persistence.testing.tests.writing.UpdateDeepOwnershipTest;
import org.eclipse.persistence.testing.tests.writing.UpdateToNullTest;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;


/**
 * Defines the main unit of work tests.
 * This suite is run under several configurations, such as three-tier and remote,
 * all tests should be compatible or throw correct warnings.
 */
public class UnitOfWorkTestSuite extends TestSuite {
    public UnitOfWorkTestSuite() {
        setDescription("This suite tests updating objects with changed parts.");
    }

    public UnitOfWorkTestSuite(boolean isSRG) {
        super(isSRG);
        setDescription("This suite tests updating objects with changed parts.");
    }

    public void addTests() {
        addTest(new MergeCloneWithReferencesWithNullTest());
        addTest(new MergeCloneWithReferencesTransparentIndirectionTest());
        //bug 4518570
        addTest(new UnitOfWorkRevertAndResumeWithNewTest());
        //bug 4544221
        addTest(new UnitOfWorkRevertWithNewObjectTest());
        //bug 4569755
        addTest(new UnitOfWorkNullPrimaryKeyTest());

        // Revived these tests as they were commented out with no reason given.
        addTest(new MergeUnitOfWorkTest(PopulationManager.getDefaultManager().getObject(Employee.class, "0001")));
        addTest(new RegisterationUnitOfWorkTest(PopulationManager.getDefaultManager().getObject(Employee.class, 
                                                                                                "0001")));
        //addTest(new UnitOfWorkConformExceptionTest()); - Test removed as conforming exceptions moved to query level setting and no longer supported correctly.
        addTest(new UnregisterUnitOfWorkTest());

        addSRGTests();
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.

    public void addSRGTests() {
        PopulationManager manager = PopulationManager.getDefaultManager();
        Employee employee = (Employee)manager.getObject(Employee.class, "0001");

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

        test = new UpdateDeepOwnershipTest((ObjectA)manager.getObject(ObjectA.class, "example1"));
        test.usesUnitOfWork = true;
        addTest(test);

        addTest(new BidirectionalInsertTest(true));
        addTest(new NestedUnitOfWorkTest(employee));
        addTest(new NestedUnitOfWorkMultipleCommitTest(employee));
        addTest(new DeepNestedUnitOfWorkTest(employee));
        addTest(new MultipleUnitOfWorkTest(employee));
        addTest(new InsertNewObjectTest());
        addTest(new ComplexMultipleUnitOfWorkTest());
        addTest(new FaultyUnitOfWorkTest());
        addTest(new LockFailureUnitOfWorkTest());
        addTest(new UnitOfWorkResumeTest(employee));
        addTest(new UnitOfWorkResumeOnFailureTest(employee));
        addTest(new DeletingFromParentSessionTest());
        addTest(new org.eclipse.persistence.testing.tests.mapping.EqualObjectUnitOfWorkTest());
        addTest(new NoIMWithValueHolderTest());
        addTest(new UnitOfWorkRevertTest(employee));
        addTest(new DeepMergeCloneSerializedTest());
        addTest(new DeepMergeCloneIndirectionTest());
        addTest(new RegisterNewObjectTest());
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
        addTest(new NullAggregateTest());
        addTest(new UOWHasOnlyDeletesTest());
        //CR 2728
        addTest(new RegisterNewObjectInIdentityMapNoSeqTest());
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

        //CR 4094 
        addTest(new GetIdentityMapFromUOWForREADONLYClassTest());
        //code coverage testing
        addTest(new UnitOfWorkCommitToDatabaseTest());
        addTest(new UnitOfWorkInitializeAllIdentityMapsTest());
        //CR#4204
        addTest(new WasTransactionBegunPrematurelyRollbackTest());

        //code coverage
        addTest(new CanChangeReadOnlySetTest());

        // code coverage
        addTest(new GetFromNewObjectWithConformTest());
        addTest(new UOWCommitAndResumeWithPreCalcChangeSet(employee));
        addTest(new PerformDeletesFirstTest());

        // bug 3815959
        addTest(new PerformDeletesFirstIgnoreUpdateTest());
        addTest(new PerformDeletesFirstIgnoreUpdateTest2());

        // bug 2612331
        addTest(new CreateDeleteCreateTest());
        //bug 2612602
        addTest(new WorkingCloneCopyPolicyTest());
        addTest(new UnregisteredNewObjectOptimisticLockTest());

        //bug 3510459
        addTest(new DoubleNestedUnitOfWorkRegisterNewObjectTest());

        // bug 3287196
        addTest(new GetObjectFromIdentityMapTest());

        addTest(new MergeDeadIndirectionTest());

        //Add new tests here, if any.
        addTest(new CommitAfterExecuteModifyQueryDuringTransTest());

        //bug 4364283
        addTest(new AllChangeSetsTest());

        //bug 5744009
        addTest(new CurrentChangeSetTest());

        //bug 4453001
        addTest(new ErrorOnInsertTest());

        addTest(new CollectionMappingMergeObjectTest());
        addTest(new ExceptionsRaisedUnitOfWorkTest());

        //bug 4736360    
        addTest(new NestedUOWWithNewObjectRegisteredTwiceTest());
    }
}
