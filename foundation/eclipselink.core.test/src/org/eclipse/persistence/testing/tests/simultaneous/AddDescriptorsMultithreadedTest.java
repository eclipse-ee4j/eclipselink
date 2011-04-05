/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.simultaneous;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.logging.SessionLog;

import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.sequencing.DefaultSequence;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.interfaces.InterfaceHashtableProject;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Tests several scenarios of adding sequences and descriptors, sequence preallocation; inserting objects concurrently.
 */
public class AddDescriptorsMultithreadedTest extends MultithreadTestCase {
    
    protected static boolean allTestsShouldStop;
    
    protected AddDescriptorsMultithreadedTest() {
        super();
        setDescription("Runs provided tests concurently with adding descriptors.");
    }
    
    protected void setup() {
        super.setup();
        allTestsShouldStop = false;
    }

    protected static AddDescriptorsMultithreadedTest createMultithreadedTest(int nAddDescriptorsTests, long timeToSleepBetweenAddingDescriptors, String testName, int nTests, long timeToStopTests) {
        AddDescriptorsMultithreadedTest test = new AddDescriptorsMultithreadedTest();
        Vector tests = new Vector(nAddDescriptorsTests + nTests);
        InterfaceHashtableProject project = new InterfaceHashtableProject();
        for (int i=0; i < nAddDescriptorsTests; i++) {
            tests.add(new AddDescriptorsTest(i, nAddDescriptorsTests, timeToSleepBetweenAddingDescriptors, project));
        }
        for (int i=0; i < nTests; i++) {
            if (testName.equals("SequencePreallocationTest")) {
                tests.add(new SequencePreallocationTest(i));
            } else if (testName.equals("InsertTest")) {
                tests.add(new InsertTest(i, nTests));
            } else {
                throw new TestProblemException("Unknown test name: " + testName);
            }
        }
        if (nAddDescriptorsTests == 0 && timeToStopTests > 0) {
            tests.add(new Timer(timeToStopTests));
        }
        test.setTests(tests);
        test.generateTestName();
        return test;
    }
    
    void generateTestName() {
        ArrayList<String> testNames = new ArrayList();
        HashMap<String, Integer> map = new HashMap();
        for (int i=0; i < this.test.length; i++) {
            String testName = this.test[i].getName();
            if (testNames.contains(testName)) {
                int count = map.get(testName);
                map.put(testName, count + 1);
            } else {
                testNames.add(testName);
                map.put(testName, 1);
            }
        }
        String name = "";
        for(int k=0; k < testNames.size(); k++) {
            String testName = testNames.get(k);
            if (k > 0) {
                name += ";_";
            }
            name += testName;
            int count = map.get(testName);
            if (count > 1) {
                name += "_" + map.get(testName)+"threads";
            }
        }
        setName(name);
    }

    /*
     * If AddDescriptorsTest not used then use Timer to stop the tests after specified time.
     */
    public static class Timer extends TestCase {
        Timer(long timeToStopTests) {
            this.timeToStopTests = timeToStopTests;
            setName("Timer("+timeToStopTests+")");
        }
        long timeToStopTests;
        public void test() {
            try {
                Thread.sleep(timeToStopTests);
            } catch (InterruptedException ex) {
                throw new TestProblemException("Thread.sleep(timeToStopTests) failed ", ex);
            }
            allTestsShouldStop = true;
        }
    }
    
    /*
     * Adds descriptors, if timeToSleepBetweenAddingDescriptors then sleeps between adding descriptors.
     */
    public static class AddDescriptorsTest extends TestCase {
        public AddDescriptorsTest(int testNumber, int numberOfTests, long timeToSleepBetweenAddingDescriptors, InterfaceHashtableProject project) {
            this.testNumber = testNumber;
            this.numberOfTests = numberOfTests;
            this.timeToSleepBetweenAddingDescriptors = timeToSleepBetweenAddingDescriptors;
            this.project = project;
            String name = "AddDescriptorsTest";
            if (timeToSleepBetweenAddingDescriptors > 0) {
                name += "(" + timeToSleepBetweenAddingDescriptors + ")";
            }
            setName(name);
        }
        int testNumber;
        int numberOfTests;
        long timeToSleepBetweenAddingDescriptors;
        InterfaceHashtableProject project;
        static int numberOfCompletedTests = 0;
        static Object lock = new Boolean(true);
        public void test() {
            DatabaseSession dbSession;
            if (getSession().isDatabaseSession()) {
                dbSession = (DatabaseSession)getSession();
            } else {
                // must be ClientSession
                dbSession = (DatabaseSession)(getAbstractSession().getParent());
            }
            int nSize = project.getOrderedDescriptors().size();
            // if numberOfTests = 10 then the first test uses k = 0, 10, 20 etc; the second k = 1, 11, 21 etc. 
            for (int k = testNumber; k < nSize; k = k + numberOfTests) {
                ClassDescriptor descriptor = project.getOrderedDescriptors().get(k);
                getAbstractSession().log(SessionLog.FINEST, "AddDescriptorsTest adding descriptor for class = " + Helper.getShortClassName(descriptor.getJavaClass()), new Object[]{}, null, false);
                DatabaseField sequenceNumberField = descriptor.getMappingForAttributeName("id").getField();
                descriptor.setSequenceNumberField(sequenceNumberField);
                String seqName = "SEQ_" + sequenceNumberField.getTableName();
                descriptor.setSequenceNumberName(seqName);
                int k3 = k % 3;
                // try adding different sequence types
                if (k3 == 0) {
                    dbSession.addSequence(new NativeSequence(seqName));
                } else if (k == 1) {
                    dbSession.addSequence(new TableSequence(seqName));
                } else {
                    // use default sequence - nothing to do
                }
                // try using both addDescriptor and addDescriptors methods
                if (k % 2 == 0) {
                    dbSession.addDescriptor(descriptor);
                } else {
                    ArrayList descriptors = new ArrayList();
                    descriptors.add(descriptor);
                    dbSession.addDescriptors(descriptors);
                }
                if (timeToSleepBetweenAddingDescriptors > 0) {
                    try {
                        Thread.sleep(timeToSleepBetweenAddingDescriptors);
                    } catch (InterruptedException ex) {
                        throw new TestProblemException("Thread.sleep(timeToSleepBetweenAddingDescriptors) failed ", ex);
                    }
                }
            }
            synchronized(lock) {
                numberOfCompletedTests++;
                if (numberOfCompletedTests == numberOfTests) {
                    allTestsShouldStop = true;

                    // get ready for the next run
                    numberOfCompletedTests = 0;
                }
            }
        }
        protected void verify() {
            DatabaseSession dbSession;
            if (getSession().isDatabaseSession()) {
                dbSession = (DatabaseSession)getSession();
            } else {
                // must be ClientSession
                dbSession = (DatabaseSession)(getAbstractSession().getParent());
            }
            int nSize = project.getOrderedDescriptors().size();
            // if numberOfTests = 10 then the first test uses k = 0, 10, 20 etc; the second k = 1, 11, 21 etc. 
            for (int k = testNumber; k < nSize; k = k + numberOfTests) {
                ClassDescriptor descriptor = project.getOrderedDescriptors().get(k);
                descriptor = dbSession.getDescriptor(descriptor.getJavaClass());
                if (descriptor == null) {
                    throw new TestErrorException(descriptor + " is not found in the session");
                }
                DatabaseField sequenceNumberField = descriptor.getMappingForAttributeName("id").getField();
                String seqName = "SEQ_" + sequenceNumberField.getTableName();
                
                Sequence sequence = dbSession.getPlatform().getSequence(seqName);
                if (sequence == null) {
                    throw new TestErrorException("Not found sequence " + seqName + " defined for class " + descriptor.getAlias());
                }
                int k3 = k % 3;
                // try adding different sequence types
                if (k3 == 0) {
                    if (!sequence.isNative()) {
                        throw new TestErrorException(sequence + " defined for class " + descriptor.getAlias() + " is wrong. NativeSequence was expected");
                    }
                } else if (k == 1) {
                    if (!sequence.isTable()) {
                        throw new TestErrorException(sequence + " defined for class " + descriptor.getAlias() + " is wrong. TableSequence was expected");
                    }
                } else {
                    if (!(sequence instanceof DefaultSequence)) {
                        throw new TestErrorException(sequence + " defined for class " + descriptor.getAlias() + " is wrong. DefaultSequence was expected");
                    }
                }
            }
        }
        /*
         * Concurrently runs nAddDescriptorsTests AddDescriptorsTests. 
         * If timeToSleepBetweenAddingDescriptors > 0 then each AddDescriptorTest sleeps after adding each descriptor.
         */
        public static AddDescriptorsMultithreadedTest createMultithreadedTest(int nAddDescriptorsTests, long timeToSleepBetweenAddingDescriptors) {
            return AddDescriptorsMultithreadedTest.createMultithreadedTest(nAddDescriptorsTests, timeToSleepBetweenAddingDescriptors, "", 0, 0);
        }
    }

    /*
     * In a loop assigns sequence number for different types 
     * (which causes sequence number preallocation) then rolls back throwing all the preallocated numbers away.
     * Does not have verify method - tests for deadlocks.
     * Stops when either AddDescriptorsTest or Timer set allTestsShouldStop flag to false. 
     */
    public static class SequencePreallocationTest extends TestCase {
        public SequencePreallocationTest(int testNumber) {
            this.testNumber = testNumber;
            setName("SequencePreallocationTest");
        }
        int testNumber;
        public void test() {
            int index = 0;
            while (!allTestsShouldStop) {
                getAbstractSession().beginTransaction();
                try {
                    UnitOfWork uow = getSession().acquireUnitOfWork();
                    Object obj;
                    if (index == 0) {
                        obj = new Address();
                        index ++;
                    } else if (index == 1) {
                        obj = new Employee();
                        index ++;
                    } else {
                        obj = new SmallProject();
                        index = 0;
                    }
                    uow.assignSequenceNumber(obj);
                } finally {
                    getAbstractSession().rollbackTransaction();
                }
            }
        }
        /*
         * Concurrently runs nAddDescriptorsTests AddDescriptorTests and nTests SequencePreallocationTests. 
         * If timeToSleepBetweenAddingDescriptors > 0 then each AddDescriptorTest sleep afters adding each descriptor.
         */
        public static AddDescriptorsMultithreadedTest createMultithreadedTestWithAddDescriptors(int nAddDescriptorsTests, long timeToSleepBetweenAddingDescriptors, int nTests) {
            return AddDescriptorsMultithreadedTest.createMultithreadedTest(nAddDescriptorsTests, timeToSleepBetweenAddingDescriptors, "SequencePreallocationTest", nTests, 0);
        }
        /*
         * Concurrently runs nTests SequencePreallocationTests. 
         * timeToStopTest > 0 must be specified, or the test will run forever.
         */
        public static AddDescriptorsMultithreadedTest createMultithreadedTest(int nTests, long timeToStopTests) {
            return AddDescriptorsMultithreadedTest.createMultithreadedTest(0, 0, "SequencePreallocationTest", nTests, timeToStopTests);
        }
    }

    /*
     * In a loop inserts objects of different types. 
     * Stops when either AddDescriptorsTest or Timer set allTestsShouldStop flag to false.
     * Does not have verify method - tests for deadlocks.
     * The last test to reset deletes all the inserted objects. 
     */
    public static class InsertTest extends TestCase {
        public InsertTest(int testNumber, int numberOfTests) {
            this.testNumber = testNumber;
            this.numberOfTests = numberOfTests;
            setName("InsertTest");
        }
        int testNumber;
        int numberOfTests;
        static int numberOfCompletedTests = 0;
        static Object lock = new Boolean(true);
        public void test() {
            int index3 = testNumber % 3;
            String strTestNumber = Integer.toString(testNumber);
            while (!allTestsShouldStop) {
                UnitOfWork uow = getSession().acquireUnitOfWork();
                Object obj;
                if (index3 == 0) {
                    Address address;
                    address = new Address();
                    address.setCity(strTestNumber);
                    address.setCountry("InsertTest");
                    obj = address;
                    index3 = 1;
                } else if (index3 == 1) {
                    Employee emp = new Employee();
                    emp.setFirstName(strTestNumber);
                    emp.setLastName("InsertTest");
                    obj = emp;
                    index3 = 2;
                } else {
                    SmallProject project = new SmallProject();
                    project.setName(strTestNumber);
                    project.setDescription("InsertTest");
                    obj = project;
                    index3 = 0;
                }
                uow.registerObject(obj);
                uow.commit();

            }
        }
        public void reset() {
            // the last test deletes the objects created by all tests
            synchronized(lock) {
                numberOfCompletedTests++;
                if (numberOfCompletedTests == numberOfTests) {
                    // delete all created objects
                    UnitOfWork uow = getSession().acquireUnitOfWork();
                    
                    DeleteAllQuery deleteAddresses = new DeleteAllQuery(Address.class);
                    deleteAddresses.setSelectionCriteria(deleteAddresses.getExpressionBuilder().get("country").equal("InsertTest"));
                    uow.executeQuery(deleteAddresses);

                    DeleteAllQuery deleteEmployees = new DeleteAllQuery(Employee.class);
                    deleteEmployees.setSelectionCriteria(deleteEmployees.getExpressionBuilder().get("lastName").equal("InsertTest"));
                    uow.executeQuery(deleteEmployees);
                    
                    DeleteAllQuery deleteProjects = new DeleteAllQuery(SmallProject.class);
                    deleteProjects.setSelectionCriteria(deleteProjects.getExpressionBuilder().get("description").equal("InsertTest"));
                    uow.executeQuery(deleteProjects);
                    
                    uow.commit();
                    
                    // get ready for the next run
                    numberOfCompletedTests = 0;
                }
            }
        }
        /*
         * Concurrently runs nAddDescriptorsTests AddDescriptorTests and nTests InsertTests. 
         * If timeToSleepBetweenAddingDescriptors > 0 then each AddDescriptorTest sleep afters adding each descriptor.
         */
        public static AddDescriptorsMultithreadedTest createMultithreadedTestWithAddDescriptors(int nAddDescriptorsTests, long timeToSleepBetweenAddingDescriptors, int nTests) {
            return AddDescriptorsMultithreadedTest.createMultithreadedTest(nAddDescriptorsTests, timeToSleepBetweenAddingDescriptors, "InsertTest", nTests, 0);
        }
        /*
         * Concurrently runs nTests InsertTests. 
         * timeToStopTest > 0 must be specified, or the test will run forever.
         */
        public static AddDescriptorsMultithreadedTest createMultithreadedTest(int nTests, long timeToStopTests) {
            return AddDescriptorsMultithreadedTest.createMultithreadedTest(0, 0, "InsertTest", nTests, timeToStopTests);
        }
    }
}
