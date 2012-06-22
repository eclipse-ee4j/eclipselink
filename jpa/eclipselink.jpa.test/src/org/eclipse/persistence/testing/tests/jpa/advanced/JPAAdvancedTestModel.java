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
package org.eclipse.persistence.testing.tests.jpa.advanced;

import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.tests.jpa.CMP3TestModel;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.testing.framework.JUnitTestCase;

/**
 * <p><b>Purpose</b>: To collect the tests that will test specifics of our
 * EJB3.0 implementation through the use of the EntityContainer.  In order for
 * this test model to work correctly the EntityContainer must be initialized
 * through the command line agent.
 */
public class JPAAdvancedTestModel extends CMP3TestModel{

    public void setup(){
        super.setup();
   		new AdvancedTableCreator().replaceTables(getServerSession());
         
        EmployeePopulator employeePopulator = new EmployeePopulator();
        employeePopulator.buildExamples();   
        //Persist the examples in the database
        employeePopulator.persistExample(((EntityManagerImpl)getEntityManager()).getServerSession());  
    }

    public void addTests(){
        addTest(getEntityManagerTestSuite());
        addTest(getAnnotationTestSuite());
	
        TestSuite tests = new TestSuite();
        tests.setName("UpdateAllQueryAdvancedJunitTest");
        tests.addTests(JUnitTestCase.suite(UpdateAllQueryAdvancedJunitTest.class));
        addTest(tests);        
        tests = new TestSuite();
        tests.setName("JoinedAttributeAdvancedJunitTest");
        tests.addTests(JUnitTestCase.suite(JoinedAttributeAdvancedJunitTest.class));
        addTest(tests);
        tests = new TestSuite();
        tests.setName("ReportQueryAdvancedJUnitTest");
        tests.addTests(JUnitTestCase.suite(ReportQueryAdvancedJUnitTest.class));
        addTest(tests);
        addTest(CascadePersistJUnitTestSuite.suite());
    }
    
    public static TestSuite getEntityManagerTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EntityManager Tests");
        suite.setDescription("This suite provides testing for EntityManager");

        suite.addTest(new EMPersistAndCommitTest());
        suite.addTest(new EMRemoveAndCommitTests());
        suite.addTest(new EMMultipleFlushTests());
        suite.addTest(new EMModifyAndCommitTest());
        suite.addTest(new EMPersistAndFlushTest());
        suite.addTest(new EMCascadingPersistAndFlushTest());
        suite.addTest(new EMCascadingPersistAndCommitTest());
        suite.addTest(new EMRemoveAndPersistTest());
        suite.addTest(new EMModifyAndMergeTest());
        suite.addTest(new EMCascadingModifyAndMergeTest());
        suite.addTest(new EMRemoveAndFlushTest());
        suite.addTest(new EMCascadingRemoveAndFlushTest());
        suite.addTest(new EMFlushBatchWritingTest());
//      Until Bug#4288681 is fixed, this test won't run
//        suite.addTest(new ModifyAndFlushTest());
//      Until Bug#4291927 is fixed, these tests won't run
        suite.addTest(new EMCascadingModifyAndRefreshTest());
        suite.addTest(new EMModifyAndRefreshTest());

        return suite;
    }
    
    public static TestSuite getAnnotationTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Annotation Tests");
        suite.setDescription("This suite provides testing for the O/R metadata annotations");

        suite.addTest(getCallbackEventTestSuite());
        suite.addTest(getPrimaryKeyTestSuite());
        
        suite.addTest(new XMLAnnotationMergingTest());
  
        return suite;
    }
    
    public static TestSuite getCallbackEventTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Callback Event Tests");
        suite.setDescription("This suite provides testing for the O/R metadata Callback event annotations");

        suite.addTest(new EntityListenerPrePersistTest());
        suite.addTest(new EntityListenerPostPersistTest());
        suite.addTest(new EntityListenerPreUpdateTest());
        suite.addTest(new EntityListenerPostUpdateTest());
        suite.addTest(new EntityListenerPreRemoveTest());
        suite.addTest(new EntityListenerPostRemoveTest());
        suite.addTest(new EntityListenerPostLoadTest());
        suite.addTest(new EntityListenerPostLoadTransactionTest());
        suite.addTest(new EntityListenerPostLoadRefreshTest());
        
        suite.addTest(new EntityMethodPrePersistTest());
        suite.addTest(new EntityMethodPostPersistTest());
        suite.addTest(new EntityMethodPreUpdateTest());
        suite.addTest(new EntityMethodPostUpdateTest());
        suite.addTest(new EntityMethodPreRemoveTest());
        suite.addTest(new EntityMethodPostRemoveTest());
        suite.addTest(new EntityMethodPostLoadTest());
        suite.addTest(new EntityMethodPostLoadTransactionTest());
        suite.addTest(new EntityMethodPostLoadRefreshTest());
  
        suite.addTest(new NonDBChangePreUpdateTest());
        
        return suite;
    }
    
    public static TestSuite getPrimaryKeyTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Primary key Tests");
        suite.setDescription("This suite provides testing for the O/R metadata primary key tests");

        suite.addTest(new PrimaryKeyClassTest());
  
        return suite;
    }
}
