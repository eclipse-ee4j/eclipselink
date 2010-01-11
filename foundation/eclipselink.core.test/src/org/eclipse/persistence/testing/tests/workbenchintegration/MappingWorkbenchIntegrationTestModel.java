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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.models.aggregate.AggregateProject;


/**
 *  This model tests the integration between the Mapping Workbench and the foundation library.
 */
public class MappingWorkbenchIntegrationTestModel extends TestModel {
    static boolean isServer = false;

    /**
     *  The constructor provides the test description.
     */
    public MappingWorkbenchIntegrationTestModel() {
        setDescription("This model tests the integration between the Mapping Workbench and the foundation library.");
    }

    public void addTests() {
        isServer = getExecutor().isServer;
        addTest(new XMLProjectWriterTestModel());
        //the following model has been commented out due to bug 5483044
        if (!isServer) {
            addTest(new ProjectClassGeneratorTestModel());
        }

        //Integration Code Coverage Tests
        addTest(getIntegrationTestSuite());

        //UTF-8 support
        addTest(getUTF8TestSuite());

        //Returning Policy support
        addTest(getReturningPolicyTestSuite());

        //Multiple sequences support
        addTest(getSequencingTestSuite());
    }

    public static TestSuite getIntegrationTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("IntegrationTestSuite");
        suite.setDescription("Contains integration tests for Code coverage");
        suite.addTest(new CopyPolicyBuildsNewInstanceTest());
        suite.addTest(new FactoryClassIsNullTest());
        suite.addTest(new FactoryMethodNameIsNotNullTest());
        suite.addTest(new FactoryMethodNameIsNullTest());
        suite.addTest(new ProjectClassGeneratorWithVariablesTest());
        suite.addTest(new ShouldAlwaysConformResultsInUnitOfWorkTest());
        suite.addTest(new ShouldAlwaysRefreshCacheOnRemoteTest());
        suite.addTest(new ShouldAlwaysRefreshCacheTest());
        suite.addTest(new ShouldBeReadOnlyTest());
        suite.addTest(new IsIsolatedTest());
        suite.addTest(new ShouldDisableCacheHitsOnRemoteTest());
        suite.addTest(new ShouldDisableCacheHitsTest());
        suite.addTest(new ShouldOnlyRefreshCacheIfNewerVersionTest());
        suite.addTest(new ShouldUseCacheIdentityMapTest());
        suite.addTest(new ShouldUseHardCacheWeakIdentityMapTest());
        suite.addTest(new ShouldUseNoIdentityMapTest());
        suite.addTest(new ShouldUseRemoteCacheIdentityMapTest());
        suite.addTest(new ShouldUseRemoteHardCacheWeakIdentityMap());
        suite.addTest(new ShouldUseRemoteNoIdentityMapTest());
        suite.addTest(new ShouldUseRemoteWeakIdentityMap());
        suite.addTest(new ShouldUseWeakIdentityMapTest());
        suite.addTest(new GetAboutToInsertSelectorIsNotNullTest());
        suite.addTest(new GetAboutToUpdateSelectorIsNotNullTest());
        suite.addTest(new GetPostBuildSelectorIsNotNullTest());
        suite.addTest(new GetPostCloneSelectorIsNotNullTest());
        suite.addTest(new GetPostDeleteSelectorIsNotNullTest());
        suite.addTest(new GetPostInsertSelectorIsNotNullTest());
        suite.addTest(new GetPostMergeSelectorIsNotNullTest());

        suite.addTest(new BuildConstructorPorjectsDefaultReadOnlyClassesIsNotEmptyTest());
        suite.addTest(new BuildDescriptorAddMultipleTableForeignKeyFieldNameTest());
        suite.addTest(new BuildDescriptorIsForInterfaceTrueTest());
        suite.addTest(new CollectionMappingIsMapPolicyTest());
        suite.addTest(new GetClassExtractionMethodNameIsNotNullTest());
        suite.addTest(new GetPostRefreshSelectorIsNotNullTest());
        suite.addTest(new GetPostUpdateSelectorIsNotNullTest());
        suite.addTest(new GetPostWriteSelectorIsNotNullTest());
        suite.addTest(new GetPreDeleteSelectorIsNotNullTest());
        suite.addTest(new GetPreInsertSelectorIsNotNullTest());
        suite.addTest(new GetPreUpdateSelectorIsNotNullTest());
        suite.addTest(new GetPreWriteSelectorIsNotNullTest());
        suite.addTest(new GetReadAllSubclassesViewIsNotNullTest());
        suite.addTest(new GetRelationshipPartnerAttributeNameIsNotNullTest());
        suite.addTest(new GettersAndSettersForProjectClassGeneratorTest());
        suite.addTest(new MappingIsReadOnlyTest());
        suite.addTest(new NamedQueryLinesGetEJBQLStringIsNotNullTest());
        suite.addTest(new NamedQueryLinesIGetSQLStringIsNotNullTest());
        suite.addTest(new NamedQueryLinesIsReadObjectQueryTest());
        suite.addTest(new NamedQueryLinesRedirectorNotNullTest());
        suite.addTest(new OneToOneMappingShouldUseJoiningTest());
        suite.addTest(new OneToOneMappingShouldVerifyDeleteTest());
        suite.addTest(new OptimisticLockingLinesSelectedFieldsLockingPolicyTest());
        suite.addTest(new QueryKeyIsAbstractQueryKeyTest());
        suite.addTest(new QueryManagerHasDeleteQueryIsSQLCallQueryTest());
        suite.addTest(new QueryManagerHasDoesExistQueryIsSQLCallQueryTest());
        suite.addTest(new QueryManagerHasInsertQueryIsSQLCallQueryTest());
        suite.addTest(new QueryManagerHasReadAllQueryIsSQLCallQueryTest());
        suite.addTest(new QueryManagerHasReadObjectQueryIsSQLCallQueryTest());
        suite.addTest(new QueryManagerHasUpdateQueryIsSQLCallQueryTest());
        suite.addTest(new QueryManagerPropertyLinesAssumeExistenceForDoesExistTest());
        suite.addTest(new QueryManagerPropertyLinesAssumeNonExistenceForDoesExistTest());
        suite.addTest(new ShouldUseBatchReadingOnMappingTest());
        suite.addTest(new ShouldUseClassNameAsIndicatorTest());
        suite.addTest(new TimestampLockingPolicyUseLocalTimeTest());
        suite.addTest(new TransformationMappingUsesBasicIndirectionTest());
        suite.addTest(new TransparentIndirectionPolicyInstanceTest());
        suite.addTest(new UseCollectionClassOnMappingTest());
        suite.addTest(new UseTransparentMapOnCollectionMapping());
        suite.addTest(new VariableOneToOneMappingClassIndicatorFieldTest());
        suite.addTest(new VariableOneToOneMappingUniquePKTest());
        suite.addTest(new VersionLockingPolicyStoreInObjectTest());
        suite.addTest(new DuplicateDescriptorNameTest());
        suite.addTest(new ProjectClassGeneratorWriteMethodTest());
        suite.addTest(new ProjectXMLSortedCollectionMapping());
        suite.addTest(new ProjectXMLSortedCollectionMappingWithInvalidComparatorTest());

        //following test has been commented out due to bug 5483044
        if (!isServer) {
            suite.addTest(new ProjectClassGeneratorWithCMPDescriptorTest());
        }

        ProjectClassGeneratorResultFileTest test = 
            new ProjectClassGeneratorResultFileTest(new AggregateProject(), ".addFieldNameTranslation");
        test.setName("AddFieldNameTranslationTest");
        suite.addTest(test);

        test = 
new ProjectClassGeneratorResultFileTest(new org.eclipse.persistence.testing.models.relationshipmaintenance.RelationshipsProject(), 
                                        ".useTransparentCollection();");
        test.setName("UseTransparentCollectionTest");
        suite.addTest(test);

        test = 
new ProjectClassGeneratorResultFileTest(new org.eclipse.persistence.testing.models.mapping.MappingProject(), "SerializedObjectConverter");
        test.setName("SerializedObjectMappingTest");
        suite.addTest(test);

        test = new ProjectClassGeneratorResultFileTest(new EmployeeSubProject(), "addAscendingOrdering(\"id\")");
        test.setName("AscendingOrderingTest");
        suite.addTest(test);

        test = new ProjectClassGeneratorResultFileTest(new EmployeeSubProject(), "addDescendingOrdering(\"id\")");
        test.setName("DescendingOrderingTest");
        suite.addTest(test);

        test = new ProjectClassGeneratorResultFileTest(new EmployeeSubProject(), "new java.util.Date");
        test.setName("DatePrintTest");
        suite.addTest(test);

        test = new ProjectClassGeneratorResultFileTest(new EmployeeSubProject(), ".useContainerIndirection(");
        test.setName("ContainerIndirectionTransformationMappingTest");
        suite.addTest(test);

        test = 
new ProjectClassGeneratorResultFileTest(new org.eclipse.persistence.testing.models.transparentindirection.CustomIndirectContainerProject(), 
                                        ".useContainerIndirection(");
        test.setName("ContainerIndirectionForeignReferenceMappingTest");
        suite.addTest(test);

        suite.addTest(new RuntimeCustomSQLQueriesTest());
        suite.addTest(new CMPDescriptorPessimisticLockingTest());
        suite.addTest(new MapPolicyIndirectionTest());

        // Bug 5170735 - PROJECTCLASSGENERATOR GENERATES NON-COMPILING CODE FOR TYPECONVERSIONCONVERTER
        suite.addTest(new TypeConversionConverterDataClassIsArrayTest(byte[].class));
        suite.addTest(new TypeConversionConverterDataClassIsArrayTest(Byte[].class));
        suite.addTest(new TypeConversionConverterDataClassIsArrayTest(char[].class));
        suite.addTest(new TypeConversionConverterDataClassIsArrayTest(Character[].class));
         
        suite.addTest(new TypeConversionConverterObjectClassIsArrayTest(byte[].class));
        suite.addTest(new TypeConversionConverterObjectClassIsArrayTest(Byte[].class));
        suite.addTest(new TypeConversionConverterObjectClassIsArrayTest(char[].class));
        suite.addTest(new TypeConversionConverterObjectClassIsArrayTest(Character[].class));

        return suite;
    }

    public static TestSuite getUTF8TestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("UTF8TestSuite");
        suite.setDescription("Contains tests to test only UTF-8 is supported.");

        suite.addTest(new ProjectXMLUTF8EncodingTest());
        suite.addTest(new ProjectXMLUTF16EncodingTest());
        return suite;
    }

    public static TestSuite getReturningPolicyTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("ReturningPolicyTestSuite");
        suite.addTest(ReturningPolicyWorkbenchIntegrationTest.projectXML());
        //following model has been commented out due to bug 5483044
        if (!isServer) {
            suite.addTest(ReturningPolicyWorkbenchIntegrationTest.projectClassGenerated());
        }

        return suite;
    }

    public static TestSuite getSequencingTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("SequencingTestSuite");
        suite.addTest(SequencingWorkbenchIntegrationTest.projectXML());
        //following model has been commented out due to bug 5483044
        if (!isServer) {
            suite.addTest(SequencingWorkbenchIntegrationTest.projectClassGenerated());
        }

        return suite;
    }
}
