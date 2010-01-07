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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.tests.validation.ClassIndicatorFieldNotFoundTest;


/**
 * Code coverage test.  Tests exception code.
 */
public class ValidationModel extends TestModel {
    public ValidationModel() {
        setDescription("This model tests various EclipseLink exceptions thrown.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem());
        addRequiredSystem(new org.eclipse.persistence.testing.models.mapping.MappingSystem());
        addRequiredSystem(new org.eclipse.persistence.testing.models.interfaces.InterfaceWithoutTablesSystem());
    }

    public void addTests() {
        addTest(getClientSessionReleaseTestSuite());
        addTest(new DatabaseAccessorNotConnectedTestSuite());
        addTest(getDescriptorExceptionTestSuite());
        addTest(new ExceptionValidationTestSuit());
        addTest(new ThreeTierBehaviorTestSuite());
        addTest(getDescriptorExceptionTestSuiteSupportPhaseOne());
        addTest(getDescriptorExceptionTestSuiteSupportPhaseOne2());
        addTest(getDescriptorExceptionTestSuiteSupportPhaseOne3());
        addTest(getDescriptorExceptionTestSuiteSupportPhaseOne4());
        addTest(getDescriptorExceptionTestSuiteSupportPhaseOne5());
        addTest(getConnectionCloseAfterTxnTestSuite());
    }

    public static TestSuite getConnectionCloseAfterTxnTestSuite(){
        TestSuite suite = new TestSuite();
        suite.setName("ConnectionCloseAfterTxnTestSuite");
        suite.setDescription("This suite tests that connections are closed once txn finished.");
        suite.addTest(new CloseConnAfterDatabaseSessionTxnTest());
        return suite;
    }
    public static TestSuite getClientSessionReleaseTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("ClientSessionReleaseTestSuite");
        suite.setDescription("This suite tests that client sessions are only released once.");

        suite.addTest(new ClientServerTest(5, 1, 2));

        suite.addTest(new ClientServerTest(10, 2, 4));
        suite.addTest(new ClientServerTest(15, 2, 4));
        suite.addTest(new ClientServerTest(20, 2, 5));
        suite.addTest(new ClientServerTest(25, 4, 8));
        suite.addTest(new ClientServerTest(25, 2, 4));

        return suite;
    }

    public static TestSuite getDescriptorExceptionTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("DescriptorExceptionTestSuite");
        suite.setDescription("This suite tests that proper descriptor exceptions are thrown when appropriate.");

        suite.addTest(new GetTableNameTest());
        suite.addTest(new CatchMultilpeSetTableNameTest());
        suite.addTest(new GetMethodReturnsValueHolderTest());
        suite.addTest(new SequenceFieldNameNotSetTest());
        suite.addTest(new ConnectionSizeChangedAfterLogin());
        suite.addTest(new NewObjectRegisteration());
        suite.addTest(new NoFieldConversionValueInObjectTypeMapping());
        suite.addTest(new NoAttributeConversionValueInObjectTypeMapping());
        suite.addTest(new KeyFromObjecWithoutDescriptor());
        suite.addTest(new ConstructorTest());
        suite.addTest(new UOWWithoutDescriptorTest());
        suite.addTest(new BidirectionWithHashtableTest());
        return suite;
    }

    public static TestSuite getDescriptorExceptionTestSuiteSupportPhaseOne() {
        TestSuite suite = new TestSuite();
        suite.setName("DescriptorExceptionTestSuite(SupportPhaseOne)");
        suite.setDescription("This suite tests that proper descriptor exceptions are thrown when appropriate.");
        suite.addTest(new NoMappingForPrimaryKeyTest()); //ian added
        suite.addTest(new DirectFieldNameNotSetTest()); //ian added
        suite.addTest(new NoForeighKeysAreSpecifiedTest()); //ian added
        suite.addTest(new ForeignKeysDefinedIncorrectlyTest()); //ian added
        suite.addTest(new ReferenceKeyFieldNotProperlySpecifiedTest()); //ian added
        suite.addTest(new NoReferenceKeyIsSpecifiedTest()); //ian added
        suite.addTest(new NoRelationTableTest()); //ian added
        suite.addTest(new NoSourceRelationKeysSpecifiedTest()); //ian added
        suite.addTest(new NoSuchMethodWhileInitializingAttributesInMethodAccessor_SetTest()); //ian added
        suite.addTest(new NoSuchMethodWhileInitializingAttributesInMethodAccessor_GetTest()); //ian added  
        suite.addTest(new NoSuchMethodWhileInitializingAttributesInMethodAccessorTest()); //ian added    
        suite.addTest(new NoTargetForeignKeysSpecifiedTest()); //ian added
        suite.addTest(new NoTargetRelationKeysSpecifiedTest()); //ian added
        suite.addTest(new ClassIndicatorFieldNotFoundTest()); //ian added
        suite.addTest(new ValueNotFoundInClassIndicatorMappingTest()); //ian added
        suite.addTest(new DescriptorForInterfaceIsMissingTest()); //ian added
        suite.addTest(new DescriptorIsMissingTest()); //ian added
        suite.addTest(new ParentDescriptorNotSpecifiedTest()); //ian added
        suite.addTest(new TableNotSpecifiedTest()); //ian added
        suite.addTest(new ReferenceTableNotSpecifiedTest()); //ian added
        suite.addTest(new RelationKeyFieldNotProperlySpecifiedTest()); //ian added
        suite.addTest(new ReturnTypeInGetAttributeAccessorTest()); //ian added
        suite.addTest(new SecurityOnFindMethodTest());
        suite.addTest(new NoSuchMethodOnInitializingAttributeMethodTest()); //ian added
        suite.addTest(new MultipleWriteMappingsForFieldTest()); //ian added
        suite.addTest(new AttributeNameNotSpecifiedTest()); //vesna added
        suite.addTest(new IllegalArgumentWhileGettingValueThruMethodAccessorTest()); //vesna added
        suite.addTest(new MultipleTablePrimaryKeyMustBeFullyQualifiedTest()); //vesna added
        suite.addTest(new NullPointerWhileGettingValueThruInstanceVariableAccessorTest()); //vesna added
        suite.addTest(new NullPointerWhileGettingValueThruMethodAccessorTest()); //vesna added
        suite.addTest(new IllegalArgumentWhileSettingValueThruMethodAccessorTest()); //vesna added  
        suite.addTest(new ParentClassIsSelfTest()); //vesna added
        suite.addTest(new IllegalArgumentWhileGettingValueThruInstanceVariableAccessorTest()); //vesna aded
        suite.addTest(new IllegalArgumentWhileSettingValueThruInstanceVariableAccessorTest()); //vesna added

        return suite;
    }

    public static TestSuite getDescriptorExceptionTestSuiteSupportPhaseOne2() {
        TestSuite suite = new TestSuite();
        suite.setName("DescriptorExceptionTestSuite(SupportPhaseOne.2)");
        suite.setDescription("This suite tests that proper descriptor exceptions are thrown when appropriate.");
        suite.addTest(new NoFieldNameForMappingTest()); //ian added
        suite.addTest(new NoAttributeTransformationMethodTest()); //ian added
        suite.addTest(new NoSuchMethodWhileConvertingToMethodTest()); //ian added
        suite.addTest(new NoAttributeValueConversionToFieldValueProvidedTest()); //ian added
        suite.addTest(new SetExistanceCheckingNotUnderstoodTest()); //ian added
        suite.addTest(new ValueHolderInstantiationMismatchTest()); //ian added
        suite.addTest(new TableIsNotPresentInDatabaseTest()); //ian added
        suite.addTest(new InvalidIdentityMapTest()); //ian added
        suite.addTest(new MissingMappingForFieldTest()); //ian added **
        suite.addTest(new InvalidUseOfTransparentIndirectionTest_extractPrimaryKeyForReferenceObject()); //ian added
        suite.addTest(new InvalidUseOfTransparentIndirectionTest_nullValueFromRow()); //ian added
        suite.addTest(new InvalidUseOfTransparentIndirectionTest_valueFromMethod()); //ian added  
        suite.addTest(new ReferenceDescriptorIsNotAggreagteCollectionTest()); //ian added
        suite.addTest(new ReferenceDescriptorIsNotAggregateTest()); //ian added
        suite.addTest(new SetMethodParameterTypeNotValidTest()); //ian added
        suite.addTest(new IllegalTableNameInMultipleTableForeignKeyTest_Source()); //ian added
        suite.addTest(new IllegalTableNameInMultipleTableForeignKeyTest_Target()); //ian added  
        suite.addTest(new NormalDescriptorsDoNotSupportNonRelationalExtensionsTest("buildDirectValuesFromFieldValue")); //ian added
        suite.addTest(new NormalDescriptorsDoNotSupportNonRelationalExtensionsTest("buildFieldValueFromDirectValues")); //ian added
        suite.addTest(new NormalDescriptorsDoNotSupportNonRelationalExtensionsTest("buildFieldValueFromForeignKeys")); //ian added
        suite.addTest(new NormalDescriptorsDoNotSupportNonRelationalExtensionsTest("buildFieldValueFromNestedRow")); //ian added
        suite.addTest(new NormalDescriptorsDoNotSupportNonRelationalExtensionsTest("buildFieldValueFromNestedRows")); //ian added
        suite.addTest(new NormalDescriptorsDoNotSupportNonRelationalExtensionsTest("buildNestedRowFromFieldValue")); //ian added
        suite.addTest(new NormalDescriptorsDoNotSupportNonRelationalExtensionsTest("buildNestedRowsFromFieldValue")); //ian added
        suite.addTest(new InvalidDataModificationEventTest()); //ian added
        suite.addTest(new InvalidDataModificationEventCodeTest_DirectCollectionMapping()); //ian added
        suite.addTest(new InvalidDataModificationEventCodeTest_ManyToManyMapping()); //ian added  
        suite.addTest(new InvalidDescriptorEventCodeTest()); //ian added
        suite.addTest(new MultipleTablePrimaryKeyNotSpecifiedTest()); //ian added
        suite.addTest(new NoSuchMethodOnFindObsoleteMethodTest()); //ian added
        suite.addTest(new NoSuchMethodWhileInitializingClassExtractionMethodTest()); //ian added
        suite.addTest(new NoSuchMethodWhileInitializingCopyPolicyTest()); //ian added
        suite.addTest(new MappingCanNotBeReadOnlyTest()); //ian added
        suite.addTest(new MappingMustBeReadOnlyWhenStoredInCacheTest()); //ian added 
        suite.addTest(new ParameterAndMappingWithoutIndirectionMismatchTest()); //vesna added *
        suite.addTest(new ReturnAndMappingWithoutIndirectionMismatchTest()); //vesna added
        suite.addTest(new ParameterAndMappingWithIndirectionMismatchTest()); //vesna added
        suite.addTest(new IncorrectCollectionPolicyTest()); //vesna added
        suite.addTest(new InvalidIndirectionContainerClassTest()); //vesna added
        suite.addTest(new StructureNameNotSetInMappingTest()); //vesna added
        return suite;
    }

    public static TestSuite getDescriptorExceptionTestSuiteSupportPhaseOne3() {
        TestSuite suite = new TestSuite();
        suite.setName("DescriptorExceptionTestSuite(SupportPhaseOne.3)");
        suite.setDescription("This suite tests that proper descriptor exceptions are thrown when appropriate.");
        suite.addTest(new NoSubClassMatchTest_AggregateCollection()); //ian added
        suite.addTest(new NoSubClassMatchTest_AggregateObject()); //ian added  
        suite.addTest(new ReturnAndMappingWithTransparentIndirectionMismatchTest()); //vesna added
        suite.addTest(new ParameterAndMappingWithTransparentIndirectionMismatchTest()); //vesna added
        suite.addTest(new GetMethodReturnTypeNotValidTest()); //vesna added
        suite.addTest(new AttributeAndMappingWithTransparentIndirectionMismatchTest()); //ian added
        //The following was untestable, but using a non-standard verify it can be tested
        suite.addTest(new InvalidContainerPolicyTest()); //ian added -- changed verify method as error is never thrown
        suite.addTest(new ErrorOccuredInAmendmentMethodTest()); //vesna added
        suite.addTest(new InvalidAmendmentMethodTest()); //vesna added
        suite.addTest(new InvalidContainerPolicyWithTransparentIndirectionTest()); //ian added
        suite.addTest(new ProxyIndirectionNotAvailableTest()); //ian added -- non standard test for JDK 1.2 or JDK 1.3+
        suite.addTest(new InvalidAttributeTypeForProxyIndirectionTest()); //ian added
        suite.addTest(new InvalidGetMethodReturnTypeForProxyIndirectionTest()); //ian added
        suite.addTest(new InvalidSetMethodParameterTypeForProxyIndirectionTest()); //ian added  
        suite.addTest(new InvalidMappingOperationTest("buildBackupCloneForPartObject")); //ian added
        suite.addTest(new InvalidMappingOperationTest("buildCloneForPartObject")); //ian added
        suite.addTest(new InvalidMappingOperationTest("cascadeMerge")); //ian added
        suite.addTest(new InvalidMappingOperationTest("createUnitOfWorkValueHolder")); //ian added
        suite.addTest(new InvalidMappingOperationTest("getContainerPolicy")); //ian added
        suite.addTest(new InvalidMappingOperationTest("getRealCollectionAttributeValueFromObject")); //ian added
        suite.addTest(new InvalidMappingOperationTest("getValueFromRemoteValueHolder")); //ian added  
        suite.addTest(new InvalidMappingOperationTest("iterateOnRealAttributeValue")); //ian added
        suite.addTest(new InvalidMappingOperationTest("simpleAddToCollectionChangeRecord")); //ian added
        suite.addTest(new InvalidMappingOperationTest("simpleRemoveFromCollectionChangeRecord")); //ian added
        suite.addTest(new InvalidIndirectionPolicyOperationTest("NoIndirectionPolicy.getValueFromRemoteValueHolder")); //ian added
        suite.addTest(new InvalidIndirectionPolicyOperationTest("NoIndirectionPolicy.mergeRemoteValueHolder")); //ian added 
        suite.addTest(new InvalidIndirectionPolicyOperationTest("ContainerIndirectionPolicy.nullValueFromRow")); //ian added
        suite.addTest(new MissingForeignKeyTranslationTest()); //ian added
        suite.addTest(new TargetInvocationWhileInvokingFieldToMethodTest()); //vesna added 
        return suite;
    }

    public static TestSuite getDescriptorExceptionTestSuiteSupportPhaseOne4() {
        TestSuite suite = new TestSuite();
        suite.setName("DescriptorExceptionTestSuite(SupportPhaseOne.4)");
        suite.setDescription("This suite tests that proper descriptor exceptions are thrown when appropriate.");
        suite.addTest(new TargetInvocationWhileGettingValueThruMethodAccessorTest()); //ian added
        suite.addTest(new TargetInvocationWhileSettingValueThruMethodAccessorTest()); //ian added
        suite.addTest(new TargetInvocationWhileCloningTest()); //ian added
        suite.addTest(new TargetInvocationWhileEventExecutionTest()); //ian added
        suite.addTest(new NullPointerWhileMethodInstantiationTest()); //ian added
        suite.addTest(new TargetInvocationWhileMethodInstantiationTest()); //ian added
        suite.addTest(new TargetInvocationWhileInvokingRowExtractionMethodTest()); //ian added
        suite.addTest(new TargetInvocationWhileInvokingAttributeMethodTest()); //ian added
        suite.addTest(new TargetInvocationWhileInstantiatingMethodBasedProxyTest()); //ian added
        suite.addTest(new TargetInvocationWhileConstructorInstantiationTest()); //ian added
        suite.addTest(new IllegalArgumentWhileInstantiatingMethodBasedProxyTest()); //ian added
        suite.addTest(new IllegalArgumentWhileInvokingAttributeMethodTest()); //ian added
        suite.addTest(new IllegalArgumentWhileInvokingFieldToMethodTest()); //ian added
        suite.addTest(new TargetInvocationWhileConstructorInstantiationOfFactoryTest()); //ian added
        suite.addTest(new InstantiationWhileConstructorInstantiationOfFactoryTest()); //ian added
        suite.addTest(new NoConstructorIndirectionContainerClassTest()); //ian added
        return suite;
    }

    public static TestSuite getDescriptorExceptionTestSuiteSupportPhaseOne5() {
        TestSuite suite = new TestSuite();
        suite.setName("DescriptorExceptionTestSuite(SupportPhaseOne.5)");
        suite.setDescription("This suite tests that proper descriptor exceptions are thrown when appropriate.");
        suite.addTest(new ConstructingDescriptorExceptionTests()); //ian added -- non-standard test -- catch all
        suite.addTest(new ChildDoesNotDefineAbstractQueryKeyOfParentTest()); //ian added
        suite.addTest(new NullPointerWhileConstructorInstantiationTest()); //ian added
        suite.addTest(new VariableOneToOneMappingIsNotDefinedProperlyTest("writeFromObjectIntoRow")); //ian added
        suite.addTest(new VariableOneToOneMappingIsNotDefinedProperlyTest("writeFromObjectIntoRowWithChangeRecord")); //ian added
        suite.addTest(new VariableOneToOneMappingIsNotDefinedProperlyTest("writeFromObjectIntoRowForWhereClause")); //ian added  im not
        suite.addTest(new NullPointerWhileSettingValueThruInstanceVariableAccessorTest());
        suite.addTest(new GetClassDescriptorWithNullTest());
        return suite;
    }
}
