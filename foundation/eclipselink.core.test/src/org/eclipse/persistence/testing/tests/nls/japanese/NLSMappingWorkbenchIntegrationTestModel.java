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
package org.eclipse.persistence.testing.tests.nls.japanese;

import org.eclipse.persistence.testing.framework.*;

/**
 *  This test model subclasses the EmployeeTestModel to allow testing of the integration
 *  between the Mapping Workbench and the foundation library.
 */
public class NLSMappingWorkbenchIntegrationTestModel extends TestModel {

    public NLSMappingWorkbenchIntegrationTestModel() {
        setDescription("[NLS_Japanese] This model tests mapping workbench integration with the foundation library by writing and reading projects and then running some operations on them.");
    }

    /**
     * Add the Mapping Workbench Integration test system.
     */
    public void addRequiredSystems() {
        addRequiredSystem(new NLSEmployeeWorkbenchIntegrationSystem());
        //addRequiredSystem(new InheritanceWorkbenchIntegrationSystem());
        //addRequiredSystem(new AggregateWorkbenchIntegrationSystem());

    }

    public void addTests() {
        // Employee Tests
        addTest(NLSEmployeeBasicTestModel.getReadObjectTestSuite());
        addTest(NLSEmployeeBasicTestModel.getUpdateObjectTestSuite());
        addTest(NLSEmployeeBasicTestModel.getInsertObjectTestSuite());
        addTest(NLSEmployeeBasicTestModel.getDeleteObjectTestSuite());
        addTest(NLSEmployeeBasicTestModel.getReadAllTestSuite());
        /*	
	// Inheritance Tests
	addTest(InheritanceTestModel.getDuplicateFieldTestSuite());	
	addTest(InheritanceTestModel.getReadObjectTestSuite());
	addTest(InheritanceTestModel.getReadAllTestSuite());
	addTest(InheritanceTestModel.getDeleteObjectTestSuite());
	addTest(InheritanceTestModel.getInsertObjectTestSuite());
	addTest(InheritanceTestModel.getUpdateObjectTestSuite());
	addTest(InheritanceTestModel.getUnitOfWorkTestSuite());
	addTest(InheritanceTestModel.getUnitOfWorkCommitResumeTestSuite());
	addTest(InheritanceTestModel.getDeepInheritanceTestSuite());
	addTest(InheritanceTestModel.getTranslatedKeyInheritanceTestSuite());
	
	// Aggregate Tests
	addTest(AggregateTestModel.getReadObjectTestSuite());
	addTest(AggregateTestModel.getUpdateObjectTestSuite());	
	addTest(AggregateTestModel.getReadAllTestSuite());
	addTest(AggregateTestModel.getDeleteObjectTestSuite());
	addTest(AggregateTestModel.getInsertObjectTestSuite());
	addTest(AggregateTestModel.getUnitOfWorkTestSuite());
	addTest(AggregateTestModel.getUnitOfWorkCommitResumeTestSuite());
	addTest(AggregateTestModel.getCheckForNullUnitOfWorkTestSuite());
	addTest(AggregateTestModel.getMergingUnitOfWorkTestSuite());
	addTest(AggregateTestModel.getDescriptorPropertiesTestSuite());
	addTest(AggregateTestModel.getEventTestSuite());
	addTest(AggregateTestModel.getNestedAggregateTestSuite());
	addTest(AggregateTestModel.getAggregateInheritanceTestSuite());
*/
        //UTF-8 support -was not in tl904: March 14, 2003
        //addTest(getUTF8TestSuite());

        //Query options -was in tl904, but removed in 10i_main: March 14, 2003
        //added here just for NLS_Japanese testing
        //addTest(new org.eclipse.persistence.testing.nlstests.japanese.NLSQueryOptionTestSuite());

    }
    /*
 //this was not in tl904: March 14, 2003	
public static TestSuite getUTF8TestSuite()
{
	TestSuite suite = new TestSuite();
	suite.setName("UTF8TestSuite");
	suite.setDescription("Contains tests to test only UTF-8 is supported.");
	
	suite.addTest(new ProjectXMLUTF8EncodingTest());
	suite.addTest(new ProjectXMLUTF16EncodingTest());
	return suite;	
}
*/


}
