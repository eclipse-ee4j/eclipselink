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
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.testing.tests.proxyindirection.ProxyIndirectionTestModel;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.tests.aggregate.AggregateTestModel;
import org.eclipse.persistence.testing.tests.directmap.DirectMapMappingBatchReadTest;
import org.eclipse.persistence.testing.tests.directmap.DirectMapMappingDeleteTest;
import org.eclipse.persistence.testing.tests.directmap.DirectMapMappingIndirectionTest;
import org.eclipse.persistence.testing.tests.directmap.MergeChangeSetWithDirectMapMappingTest;
import org.eclipse.persistence.testing.tests.employee.EmployeeBasicTestModel;
import org.eclipse.persistence.testing.tests.insurance.InsuranceObjectRelationalTestModel;
import org.eclipse.persistence.testing.tests.interfaces.InterfaceWithoutTablesTestModel;
import org.eclipse.persistence.testing.tests.mapping.MappingTestModel;
import org.eclipse.persistence.testing.tests.multipletable.MultipleTableModel;


/**
 *  This model tests mapping workbench integration with the foundation library
 *  by writing and reading project .xml files and then running some operations on them.
 */
public class XMLProjectWriterTestModel extends TestModel {

    /**
     *  The constructor provides the test description.
     */
    public XMLProjectWriterTestModel() {
        setDescription("This model tests mapping workbench integration with the foundation library by writing and reading project .xml files and then running some operations on them.");
    }

    /**
     *  Add the Mapping Workbench Integration test system.
     */
    public void addRequiredSystems() {
        addRequiredSystem(new EmployeeWorkbenchIntegrationSystem());
        addRequiredSystem(new InheritanceWorkbenchIntegrationSystem());
        addRequiredSystem(new AggregateWorkbenchIntegrationSystem());
        addRequiredSystem(new InterfaceWorkbenchIntegrationSystem());
        addRequiredSystem(new DirectMapMappingMWIntergrationSystem());
        addRequiredSystem(new CMWorkbenchIntegrationSystem());
        addRequiredSystem(new ProxyIndirectionMWIntegrationSystem());
        if(this.getSession().getPlatform().isOracle9()) {
        	addRequiredSystem(new InsuranceORWorkbenchIntegrationSystem());
        }
        addRequiredSystem(new MappingModelWorkbenchIntegrationSystem());
        addRequiredSystem(new MultipleTableModelWorkbenchIntegrationSystem());
    }

    public void addTests() {
        // Employee Tests
        TestSuite employeeSuite = new TestSuite();
        employeeSuite.setName("EmployeeModel");
        employeeSuite.addTest(EmployeeBasicTestModel.getReadObjectTestSuite());
        employeeSuite.addTest(EmployeeBasicTestModel.getUpdateObjectTestSuite());
        employeeSuite.addTest(EmployeeBasicTestModel.getInsertObjectTestSuite());
        employeeSuite.addTest(EmployeeBasicTestModel.getDeleteObjectTestSuite());
        employeeSuite.addTest(EmployeeBasicTestModel.getReadAllTestSuite());
        addTest(employeeSuite);

        // Inheritance Tests
        TestSuite inheritanceSuite = new TestSuite();
        inheritanceSuite.setName("InheritanceModel");
        inheritanceSuite.addTest(org.eclipse.persistence.testing.tests.inheritance.InheritanceTestModel.getDuplicateFieldTestSuite());
        inheritanceSuite.addTest(org.eclipse.persistence.testing.tests.inheritance.InheritanceTestModel.getReadObjectTestSuite());
        inheritanceSuite.addTest(org.eclipse.persistence.testing.tests.inheritance.InheritanceTestModel.getReadAllTestSuite());
        inheritanceSuite.addTest(org.eclipse.persistence.testing.tests.inheritance.InheritanceTestModel.getDeleteObjectTestSuite());
        inheritanceSuite.addTest(org.eclipse.persistence.testing.tests.inheritance.InheritanceTestModel.getInsertObjectTestSuite());
        inheritanceSuite.addTest(org.eclipse.persistence.testing.tests.inheritance.InheritanceTestModel.getUpdateObjectTestSuite());
        inheritanceSuite.addTest(org.eclipse.persistence.testing.tests.inheritance.InheritanceTestModel.getUnitOfWorkTestSuite());
        inheritanceSuite.addTest(org.eclipse.persistence.testing.tests.inheritance.InheritanceTestModel.getUnitOfWorkCommitResumeTestSuite());
        inheritanceSuite.addTest(org.eclipse.persistence.testing.tests.inheritance.InheritanceTestModel.getDeepInheritanceTestSuite());
        inheritanceSuite.addTest(org.eclipse.persistence.testing.tests.inheritance.InheritanceTestModel.getTranslatedKeyInheritanceTestSuite());
        addTest(inheritanceSuite);

        // Aggregate Tests
        TestSuite aggregateSuite = new TestSuite();
        aggregateSuite.setName("AggregateModel");
        boolean useNewAggregateCollectionOriginal = AggregateTestModel.useNewAggregateCollection;
        // MW doesn't support new AggregateCollection apis - temporary set the flag to false
        AggregateTestModel.useNewAggregateCollection = false;
        aggregateSuite.addTest(AggregateTestModel.getReadObjectTestSuite());
        aggregateSuite.addTest(AggregateTestModel.getUpdateObjectTestSuite());
        aggregateSuite.addTest(AggregateTestModel.getReadAllTestSuite());
        aggregateSuite.addTest(AggregateTestModel.getDeleteObjectTestSuite());
        aggregateSuite.addTest(AggregateTestModel.getInsertObjectTestSuite());
        aggregateSuite.addTest(AggregateTestModel.getUnitOfWorkTestSuite());
        aggregateSuite.addTest(AggregateTestModel.getUnitOfWorkCommitResumeTestSuite());
        aggregateSuite.addTest(AggregateTestModel.getCheckForNullUnitOfWorkTestSuite());
        aggregateSuite.addTest(AggregateTestModel.getMergingUnitOfWorkTestSuite());
        aggregateSuite.addTest(AggregateTestModel.getDescriptorPropertiesTestSuite());
        aggregateSuite.addTest(AggregateTestModel.getEventTestSuite());
        aggregateSuite.addTest(AggregateTestModel.getNestedAggregateTestSuite());
        aggregateSuite.addTest(AggregateTestModel.getAggregateInheritanceTestSuite());
        // reset the original value
        AggregateTestModel.useNewAggregateCollection = useNewAggregateCollectionOriginal;
        addTest(aggregateSuite);

        // Interface Tests
        TestSuite interfaceSuite = new TestSuite();
        interfaceSuite.setName("InterfaceModel");
        interfaceSuite.addTest(InterfaceWithoutTablesTestModel.getReadObjectTestSuite());
        interfaceSuite.addTest(InterfaceWithoutTablesTestModel.getUpdateObjectTestSuite());
        interfaceSuite.addTest(InterfaceWithoutTablesTestModel.getReadAllTestSuite());
        interfaceSuite.addTest(InterfaceWithoutTablesTestModel.getDeleteObjectTestSuite());
        interfaceSuite.addTest(InterfaceWithoutTablesTestModel.getInsertObjectTestSuite());
        addTest(interfaceSuite);

        //ExpressionPersistenceTests
        addTest(new org.eclipse.persistence.testing.tests.workbenchintegration.ExpressionPersistence.ExpressionPersistenceTestSuite());

        //ReportQuery
        addTest(new org.eclipse.persistence.testing.tests.workbenchintegration.ExpressionPersistence.ReportQueryTestSuite());

        //Query options
        addTest(new org.eclipse.persistence.testing.tests.workbenchintegration.QueryOptions.QueryOptionTestSuite());

        TestSuite readAndWriteSuite = new TestSuite();
        readAndWriteSuite.setName("XMLReadAndWriteSuite");
        readAndWriteSuite.setDescription("Tests to ensure various project options are properly read and written in XML.");
        readAndWriteSuite.addTest(new QueryShouldMaintainCacheTest());
        readAndWriteSuite.addTest(new ProjectXMLDatabaseTableNameTest());
        readAndWriteSuite.addTest(new ProjectXMLOrderByQueryKeysTest());
        readAndWriteSuite.addTest(new ProjectXMLQueryManagerQueryOrderTest());
        readAndWriteSuite.addTest(new EventListenerCollectionTest()); //bug 295383
        addTest(readAndWriteSuite);

        TestSuite mappingSuite = new TestSuite();
        mappingSuite.setName("MappingSuite");
        mappingSuite.setDescription("Tests to ensure mappings are properly written to and read from XML.");
        mappingSuite.addTest(new DirectMapMappingTest());
        mappingSuite.addTest(new MergeChangeSetWithDirectMapMappingTest());
        mappingSuite.addTest(new DirectMapMappingDeleteTest());
        mappingSuite.addTest(new DirectMapMappingBatchReadTest());
        mappingSuite.addTest(new DirectMapMappingIndirectionTest());
        addTest(mappingSuite);

        TestSuite proxyIndirectSuite = new TestSuite();
        proxyIndirectSuite.setName("ProxyIndirectionSuite");
        proxyIndirectSuite.setDescription("Tests to ensure proxy indirection is properly written to and read from XML.");
        proxyIndirectSuite.addTest(ProxyIndirectionTestModel.getDeleteTestSuite());
        proxyIndirectSuite.addTest(ProxyIndirectionTestModel.getReadTestSuite());
        proxyIndirectSuite.addTest(ProxyIndirectionTestModel.getUnitOfWorkTestSuite());
        proxyIndirectSuite.addTest(ProxyIndirectionTestModel.getWriteTestSuite());
        proxyIndirectSuite.addTest(ProxyIndirectionTestModel.getProxyObjectTestSuite());
        addTest(proxyIndirectSuite);

        //Insurance model test
        class TestSuiteOracleOnly extends TestSuite {
            public void setup() {
                if(!getSession().getPlatform().isOracle9()) {
                    throw new TestWarningException("This test suite is intended for Oracle databases only.");
                }
            }
        }
        TestSuiteOracleOnly insuranceORTestSuite = new TestSuiteOracleOnly();
        insuranceORTestSuite.setName("InsuranceORTestModel");
        insuranceORTestSuite.setDescription("Tests to enusre Insurance - object relationship descriptor - is properly written to and read from XML.");
        insuranceORTestSuite.addTest(InsuranceObjectRelationalTestModel.getDeleteObjectTestSuite());
        insuranceORTestSuite.addTest(InsuranceObjectRelationalTestModel.getInsertObjectTestSuite());
        insuranceORTestSuite.addTest(InsuranceObjectRelationalTestModel.getReadAllTestSuite());
        insuranceORTestSuite.addTest(InsuranceObjectRelationalTestModel.getReadObjectTestSuite());
        insuranceORTestSuite.addTest(InsuranceObjectRelationalTestModel.getUpdateObjectTestSuite());
        addTest(insuranceORTestSuite);
        
        // Mapping model test
        TestSuite mappingTestSuite = new TestSuite();
        mappingTestSuite.setName("MappingTestModel");
        mappingTestSuite.setDescription("Tests to enusre mapping model is properly written to and read from XML.");
        mappingTestSuite.addTest(MappingTestModel.getReadObjectTestSuite());
        mappingTestSuite.addTest(MappingTestModel.getReadAllTestSuite());
        mappingTestSuite.addTest(MappingTestModel.getDeleteObjectTestSuite());
        mappingTestSuite.addTest(MappingTestModel.getInsertObjectTestSuite());
        mappingTestSuite.addTest(MappingTestModel.getUpdateObjectTestSuite());
        mappingTestSuite.addTest(MappingTestModel.getUnitOfWorkTestSuite());
        mappingTestSuite.addTest(MappingTestModel.getPublic1MTestSuite());
        mappingTestSuite.addTest(MappingTestModel.getPrivateMMTestSuite());
        mappingTestSuite.addTest(MappingTestModel.getTransformationMappingTestSuite());
        mappingTestSuite.addTest(MappingTestModel.getUnitOfWorkCommitResumeTestSuite());
        mappingTestSuite.addTest(MappingTestModel.getBidirectionalUnitOfWorkTestSuite());
        mappingTestSuite.addTest(MappingTestModel.getAdditionalJoinTest());
        mappingTestSuite.addTest(MappingTestModel.getBuildSelectionCriteriaTestSuite());
        mappingTestSuite.addTest(MappingTestModel.getSameNameMappingTestSuite());
        addTest(mappingTestSuite);
        
        // Multiple table model test
        TestSuite multipleTableTestSuite = new TestSuite();
        multipleTableTestSuite.setName("MultipleTableTestModel");
        MultipleTableModel.addTestsToTestCollection(multipleTableTestSuite);
        addTest(multipleTableTestSuite);
    }
}
