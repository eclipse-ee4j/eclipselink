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
 *     07/16/2009 Andrei Ilitchev 
 *       - Bug 282553: JPA 2.0 JoinTable support for OneToOne and ManyToOne
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.onetoonejointable;

import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionMath;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.platform.database.TimesTenPlatform;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.onetoonejointable.*;
import org.eclipse.persistence.testing.tests.expressions.ReadObjectExpressionTest;
import org.eclipse.persistence.testing.tests.writing.ComplexUpdateTest;

/**
 * This model tests reading/writing/deleting through using the employee demo.
 */
public class OneToOneJoinTableEmployeeBasicTestModel extends TestModel {

    /**
     * Return the JUnit suite to allow JUnit runner to find it.
     * Unfortunately JUnit only allows suite methods to be static,
     * so it is not possible to generically do this.
     */
    public static junit.framework.TestSuite suite() {
        return new OneToOneJoinTableEmployeeBasicTestModel();
    }

    public OneToOneJoinTableEmployeeBasicTestModel() {
        setDescription("This model tests reading/writing/deleting using the employee demo.");
    }

    public OneToOneJoinTableEmployeeBasicTestModel(boolean isSRG) {
        this();
        this.isSRG = isSRG;
    }

    public void addRequiredSystems() {
        addRequiredSystem(new EmployeeSystem());
    }

    public void addTests() {
        addTest(getReadObjectTestSuite());
        addTest(getUpdateObjectTestSuite());
        addTest(getInsertObjectTestSuite());
        addTest(getUOWInsertObjectTestSuite());
        addTest(getDeleteObjectTestSuite());
        addTest(getReadAllTestSuite());
        addTest(new ExpressionTestSuite());
    }

    public static TestSuite getDeleteObjectTestSuite() {
        TestSuite suite = getSRGDeleteObjectTestSuite();

        //Add new tests here ...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGDeleteObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeDeleteObjectTestSuite");
        suite.setDescription("This suite tests the deletion of each object in the employee demo.");

        Class employeeClass = Employee.class;
        Class largeProjectClass = LargeProject.class;
        Class smallProjectClass = SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0005")));

        suite.addTest(new ProjectDeleteTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(smallProjectClass, "0002")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(smallProjectClass, "0003")));

        suite.addTest(new ProjectDeleteTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(largeProjectClass, "0002")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(largeProjectClass, "0003")));

        return suite;

    }

    public static TestSuite getInsertObjectTestSuite() {
        TestSuite suite = getSRGInsertObjectTestSuite();

        //Add new tests here ...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeInsertObjectTestSuite");
        suite.setDescription("This suite tests the insertion of each object in the employee demo.");
        EmployeePopulator system = new EmployeePopulator();

        suite.addTest(new InsertObjectTest(system.basicEmployeeExample1()));
        suite.addTest(new InsertObjectTest(system.basicEmployeeExample2()));
        suite.addTest(new InsertObjectTest(system.basicEmployeeExample3()));
        suite.addTest(new InsertObjectTest(system.basicEmployeeExample4()));
        suite.addTest(new InsertObjectTest(system.basicEmployeeExample5()));

        suite.addTest(new InsertObjectTest(system.basicSmallProjectExample1()));
        suite.addTest(new InsertObjectTest(system.basicSmallProjectExample2()));
        suite.addTest(new InsertObjectTest(system.basicSmallProjectExample3()));

        suite.addTest(new InsertObjectTest(system.basicLargeProjectExample1()));
        suite.addTest(new InsertObjectTest(system.basicLargeProjectExample2()));
        suite.addTest(new InsertObjectTest(system.basicLargeProjectExample3()));

        return suite;
    }

    public static TestSuite getReadAllTestSuite() {
        TestSuite suite = getSRGReadAllTestSuite();

        //Add new tests here ...
        Expression orderBy = new ExpressionBuilder().get("firstName").ascending();
        Call call = new SQLCall("SELECT t0.VERSION, t1.EMP_ID, t0.L_NAME, t0.F_NAME, t1.SALARY, t0.EMP_ID, t0.GENDER FROM OTOJT_EMPLOYEE t0, OTOJT_SALARY t1 WHERE t1.EMP_ID = t0.EMP_ID");
        suite.addTest(new ReadAllCallWithOrderingTest(Employee.class, 12, call, orderBy));

        suite.addTest(new JoinTest());
        suite.addTest(new JoinTest_SelectByFirstName());
        suite.addTest(new BatchReadingTest());
        suite.addTest(new BatchReadingTest_SelectByFirstName());

        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeReadAllTestSuite");
        suite.setDescription("This suite tests the reading of all the objects of each class in the employee demo.");

        suite.addTest(new ReadAllTest(Employee.class, 12));
        suite.addTest(new ReadAllTest(Project.class, 15));
        suite.addTest(new ReadAllTest(LargeProject.class, 5));
        suite.addTest(new ReadAllTest(SmallProject.class, 10));

        suite.addTest(new ReadAllCallTest(Employee.class, 12, new SQLCall("SELECT  t0.VERSION, t1.EMP_ID, t0.L_NAME, t0.F_NAME, t1.SALARY, t0.EMP_ID, t0.GENDER FROM OTOJT_EMPLOYEE t0, OTOJT_SALARY t1 WHERE t1.EMP_ID = t0.EMP_ID")));

        return suite;
    }

    public static TestSuite getReadObjectTestSuite() {
        TestSuite suite = getSRGReadObjectTestSuite();

        //Add new tests here ...
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeReadObjectTestSuite");
        suite.setDescription("This suite test the reading of each object in the employee demo.");

        Class employeeClass = Employee.class;
        Class largeProjectClass = LargeProject.class;
        Class smallProjectClass = SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0005")));
        Employee employee = (Employee)manager.getObject(employeeClass, "0001");

		suite.addTest(new ReadObjectCallTest(employeeClass, new SQLCall("SELECT t0.VERSION, t1.EMP_ID, t0.L_NAME, t0.F_NAME, t1.SALARY, t0.EMP_ID, t0.GENDER FROM OTOJT_EMPLOYEE t0, OTOJT_SALARY t1 WHERE t1.EMP_ID = t0.EMP_ID AND t0.F_NAME = '"+employee.getFirstName()+"' AND t0.L_NAME = '"+employee.getLastName()+"'")));
        employee = (Employee)manager.getObject(employeeClass, "0002");
		suite.addTest(new ReadObjectCallTest(employeeClass, new SQLCall("SELECT  t0.VERSION, t1.EMP_ID, t0.L_NAME, t0.F_NAME, t1.SALARY, t0.EMP_ID, t0.GENDER FROM OTOJT_EMPLOYEE t0, OTOJT_SALARY t1 WHERE t1.EMP_ID = t0.EMP_ID AND t0.F_NAME = '"+employee.getFirstName()+"' AND t0.L_NAME = '"+employee.getLastName()+"'")));
        employee = (Employee)manager.getObject(employeeClass, "0003");
		suite.addTest(new ReadObjectCallTest(employeeClass, new SQLCall("SELECT  t0.VERSION, t1.EMP_ID, t0.L_NAME, t0.F_NAME, t1.SALARY, t0.EMP_ID, t0.GENDER FROM OTOJT_EMPLOYEE t0, OTOJT_SALARY t1 WHERE t1.EMP_ID = t0.EMP_ID AND t0.F_NAME = '"+employee.getFirstName()+"' AND t0.L_NAME = '"+employee.getLastName()+"'")));

        Project project = (Project)manager.getObject(largeProjectClass, "0001");
        ReadObjectTest test = new ReadObjectTest(project);
        test.setQuery(new org.eclipse.persistence.queries.ReadObjectQuery(Project.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("id").equal(project.getId())));
        suite.addTest(test);

        suite.addTest(new ReadObjectTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(smallProjectClass, "0002")));
        suite.addTest(new ReadObjectTest(manager.getObject(smallProjectClass, "0003")));

        suite.addTest(new ReadObjectTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(largeProjectClass, "0002")));
        suite.addTest(new ReadObjectTest(manager.getObject(largeProjectClass, "0003")));

        return suite;
    }

    public static TestSuite getUpdateObjectTestSuite() {
        TestSuite suite = getSRGUpdateObjectTestSuite();

        //Add new tests here ...
        suite.addTest(new ManyToManyReadOnlyMappingUpdateTest());
        suite.addTest(new AddRemoveTest());
        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGUpdateObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeUpdateObjectTestSuite");
        suite.setDescription("This suite tests the updating of each object in the employee demo.");

        Class employeeClass = Employee.class;
        Class largeProjectClass = LargeProject.class;
        Class smallProjectClass = SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(employeeClass, "0005")));

        suite.addTest(new WriteObjectTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(smallProjectClass, "0002")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(smallProjectClass, "0003")));

        suite.addTest(new WriteObjectTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(largeProjectClass, "0002")));
        suite.addTest(new UnitOfWorkBasicUpdateObjectTest(manager.getObject(largeProjectClass, "0003")));

        return suite;
    }
    
    static class ManyToManyReadOnlyMappingUpdateTest extends ComplexUpdateTest {
        ManyToManyReadOnlyMappingUpdateTest() {
            super();
            usesUnitOfWork = true;
            setName("ManyToManyReadOnlyMappingUpdateTest");
        }
        public void setup() {
            // find Project with at least one Employee
            List<Project> projects = getSession().readAllObjects(Project.class);
            for(int i=0; i < projects.size(); i++) {
                if(projects.get(i).getEmployees().size() > 0) {
                    originalObject = projects.get(i);
                    break;
                }
            }
            
            super.setup();
        }
        // remove all employees
        protected void changeObject() {
            Project project = (Project)this.workingCopy;
            int size = project.getEmployees().size();
            if(size == 0) {
                throw new TestProblemException("Project was supposed to have Employees, but doesn't have any.");
            } else {
                for(int i = size-1; 0 <= i; i--) {
                    project.removeEmployee(project.getEmployees().get(i));
                }
            }
        }
    }
    
    static class AddRemoveTest extends TestCase {
        Employee employee;
        Address address1, address2;
        Project project1, project2;
        boolean useAddress;
        boolean useProjects;
        AddRemoveTest() {
            super();
            setName("AddRemoveTest");
        }
        public void setup() {
            useAddress = true;
            useProjects = true;
            
        }
        public void test() {
            employee = new Employee();
            employee.setFirstName("AddRemoveTest");
            if(useAddress) {
                address1 = new Address();
                address1.setCity("city1");
            }
            if(useProjects) {
                project1 = new SmallProject("project1");
            }
            
            // insert employee with address/project
            UnitOfWork uow = getSession().acquireUnitOfWork();
            Employee employeeClone = (Employee)uow.registerObject(employee);
            if(useAddress) {
                Address address1Clone = (Address)uow.registerObject(address1);
                employeeClone.setAddress(address1Clone);
            }
            if(useProjects) {
                Project project1Clone = (Project)uow.registerObject(project1);
                employeeClone.addProject(project1Clone);
            }
            uow.commit();
            
            // remove address/project from employee
            uow = getSession().acquireUnitOfWork();
            employeeClone = (Employee)uow.registerObject(employee);
            if(useAddress) {
                employeeClone.setAddress(null);
            }
            if(useProjects) {
                employeeClone.removeProject(employeeClone.getProjects().get(0));
            }
            uow.commit();
            
            // set address/project into employee
            uow = getSession().acquireUnitOfWork();
            employeeClone = (Employee)uow.registerObject(employee);
            if(useAddress) {
                Address address1Clone = (Address)uow.registerObject(address1);
                employeeClone.setAddress(address1Clone);
            }
            if(useProjects) {
                Project project1Clone = (Project)uow.registerObject(project1);
                employeeClone.addProject(project1Clone);
            }
            uow.commit();
            
            // override address/project in employee
            uow = getSession().acquireUnitOfWork();
            employeeClone = (Employee)uow.registerObject(employee);
            if(useAddress) {
                address2 = new Address();
                address2.setCity("city2");
                Address address2Clone = (Address)uow.registerObject(address2);
                employeeClone.setAddress(address2Clone);
            }
            if(useProjects) {
                project2 = new LargeProject("project2");
                Project project2Clone = (Project)uow.registerObject(project2);
                employeeClone.addProject(project2Clone);
            }
            uow.commit();
            
            // delete employee
            uow = getSession().acquireUnitOfWork();
            uow.deleteObject(employee);
            uow.commit();
            employee = null;
        }
        public void reset() {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            if(employee != null) {
                uow.deleteObject(employee);
            }
            if(useAddress) {
                if(address1 != null) {
                    uow.deleteObject(address1);
                }
                if(address2 != null) {
                    uow.deleteObject(address2);
                }
            }
            if(useProjects) {
                if(project1 != null) {
                    uow.deleteObject(project1);
                }
                if(project2 != null) {
                    uow.deleteObject(project2);
                }
            }
            uow.commit();
        }
    }

    static class JoinTest extends TestCase {
        public JoinTest() {
            super();
            setName("JoinTest - no selection criteria");
            setDescription("Tests reading of Employees with join expressions.");
        }
        void setSelectionCriteria(ReadAllQuery query) {
        }
        public void test() {
            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Employee.class);
            setSelectionCriteria(query);
            
            ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
            
            Expression address = query.getExpressionBuilder().getAllowingNull("address");
            query.addJoinedAttribute(address);
            Expression projectLed = query.getExpressionBuilder().getAllowingNull("projectLed");
            query.addJoinedAttribute(projectLed);
            Expression manager = query.getExpressionBuilder().getAllowingNull("manager");
            query.addJoinedAttribute(manager);
            Expression managerProjectLed = manager.getAllowingNull("projectLed");
            query.addJoinedAttribute(managerProjectLed);

            String errorMsg = JoinedAttributeTestHelper.executeQueriesAndCompareResults(controlQuery, query, (AbstractSession)getSession());
            if(errorMsg.length() > 0) {
                throw new TestErrorException(errorMsg);
            }
        }
    }
    static class JoinTest_SelectByFirstName extends JoinTest {
        public JoinTest_SelectByFirstName() {
            super();
            setName("JoinTest - select by first name");
        }
        void setSelectionCriteria(ReadAllQuery query) {
            query.setSelectionCriteria(query.getExpressionBuilder().get("firstName").like("J%"));
        }
    }

    static class BatchReadingTest extends TestCase {
        boolean shouldPrintDebugOutput = false;
        public BatchReadingTest() {
            setName("EmployeeBatchReadingTest - no selection criteria");
            setDescription("Tests batch reading of Employees with batch expression address");
        }
        void setSelectionCriteria(ReadAllQuery query) {
        }
        public void test() {
            // clear cache
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            // create batch read query, set its selectionCriteria
            ReadAllQuery query = new ReadAllQuery(Employee.class);
            setSelectionCriteria(query);
            // before adding batch read attributes clone the query to create control query
            ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
            // add batch read attributes
            Expression addressExp = query.getExpressionBuilder().get("address");
            query.addBatchReadAttribute(addressExp);
            // execute the query
            List employees = (List)getSession().executeQuery(query);
            if(employees.isEmpty()) {
                throw new TestProblemException("No Employees were read");
            }
            // need to instantiate only a single Address to trigger sql that reads data from the db for all.
            // still need to trigger all the indirections - but (except the first one) they are not accessing the db 
            // (the data is already cached in the value holders).  
            printDebug("Trigger batch reading results");
            boolean isConnected = true;
            for(int i=0; i < employees.size(); i++) {
                Address address = ((Employee)employees.get(i)).getAddress();
                if(isConnected) {
                    // need to instantiate only a single Address to trigger sql that reads data from the db for all Addresses.
                    // to ensure that no other sql is issued close connection.
                    ((AbstractSession)getSession()).getAccessor().closeConnection();
                    isConnected = false;
                }
            }
            if(!isConnected) {
                // reconnect connection
                ((AbstractSession)getSession()).getAccessor().reestablishConnection((AbstractSession)getSession());
            }
            printDebug("");

            // obtain control results
            // clear cache
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            // execute control query
            List controlEmployees = (List)getSession().executeQuery(controlQuery);
            // instantiate all value holders that the batch query expected to instantiate            
            printDebug("Trigger control results");
            for(int i=0; i < controlEmployees.size(); i++) {
                Address address = ((Employee)controlEmployees.get(i)).getAddress();
            }
            
            // compare results
            String errorMsg = JoinedAttributeTestHelper.compareCollections(employees, controlEmployees, getSession().getClassDescriptor(Employee.class), ((AbstractSession)getSession()));
            if(errorMsg.length() > 0) {
                throw new TestErrorException(errorMsg);
            }
        }
        void printDebug(String msg) {
            if(this.shouldPrintDebugOutput) {
                System.out.println(msg);
            }
        }
    }
    static class BatchReadingTest_SelectByFirstName extends BatchReadingTest {
        public BatchReadingTest_SelectByFirstName() {
            super();
            setName("EmployeeBatchReadingTest - select by first name");
        }
        void setSelectionCriteria(ReadAllQuery query) {
            query.setSelectionCriteria(query.getExpressionBuilder().get("firstName").like("J%"));
        }
    }

    // copied 6 methods from org.eclipse.persistence.testing.tests.expressions.ExpressionTestSuite
    static class ExpressionTestSuite extends TestSuite {
        public ExpressionTestSuite() {
            setName("ExpressionTestSuite");
            setDescription("This suite tests expressions.");
        }
        public void addTests() {
            addMultipleAndsTest();
            addMultipleAndsTest2();
            addMultipleAndsTest3();
            addMultipleAndsTest4();
            addMultipleAndsTest5();
            addMultipleAndsTest6();
            addEqualUnneccessaryJoinTest();
            addManagersOfWealthyMarriedAtWorkEmployeesTest();
            addOneToManyJoin2WithBatchReadTest();
        }
        private void addMultipleAndsTest() {
            ExpressionBuilder builder = new ExpressionBuilder();
    
            //this is a bug, wrong generated SQL like... ADDRESS.ADDRESS_ID = EMPLOYEE.ADDR_ID...
            //however, it should be ... ADDRESS.ADDRESS_ID = 0...
            Expression expression = builder.get("address").equal(new Address()).and(builder.get("lastName").notEqual("foopoyp"));
    
            ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
            test.setExpression(expression);
            test.setName("MultipleAndsExpressionTest");
            test.setDescription("Test object equality with object will null primary key");
            addTest(test);
        }
    
        private void addMultipleAndsTest2() {
            //there is a bug, generated SQL looks like ... (ADDRESS.ADDRESS_ID = 123456)) AND (ADDRESS.ADDRESS_ID = EMPLOYEE.ADDR_ID)...
            //should be: ...EMPLOYEE.ADDR_ID = 123456...  no join needed!
            ExpressionBuilder builder = new ExpressionBuilder();
            Address a = new Address();
            a.setId(new java.math.BigDecimal(123456));
    
            Expression expression = builder.get("address").equal(a).and(builder.get("id").greaterThan(800));
    
            ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
            test.setQuery(new ReadAllQuery(Employee.class, expression));
            test.getQuery().addAscendingOrdering("id");
            test.setExpression(expression);
            test.setName("MultipleAndsExpressionTest2");
            test.setDescription("Test multiple ands with object equality");
            addTest(test);
        }
    
        private void addMultipleAndsTest3() {
            //there is a bug, generated wrong SQL looks like ...  FROM ADDRESS t3, SALARY t2, EMPLOYEE t1 WHERE (((((t1.EMP_ID > '800') AND ) AND (t1.EMP_ID = t2.EMP_ID)) AND (t3.ADDRESS_ID = 123456))...
            ExpressionBuilder builder = new ExpressionBuilder();
    
            Address a = new Address();
            a.setId(new java.math.BigDecimal(123456));
    
            Expression expression = builder.get("id").greaterThan(800).and(builder.get("address").equal(a));
            ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
            test.setQuery(new ReadAllQuery(Employee.class, expression));
            test.getQuery().addAscendingOrdering("id");
            test.setExpression(expression);
            test.setName("MultipleAndsExpressionTest3");
            test.setDescription("Test multiple ands with object equality");
            addTest(test);
        }
    
        private void addMultipleAndsTest4() {
            // This tests the case of a tree where the top-level AND should print, even though
            // both branches underneath it are partly suppressed.
            ExpressionBuilder builder = new ExpressionBuilder();
            Address address1 = new Address();
            address1.setId(new java.math.BigDecimal(999999876));
    
            Address address2 = new Address();
            address2.setId(new java.math.BigDecimal(999999877));
    
            Expression expression1 = builder.get("address").equal(address1).or(builder.get("lastName").equal("Smith"));
            Expression expression2 = builder.get("address").equal(address2).or(builder.get("firstName").equal("Bob"));
    
            ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
            test.setExpression(expression1.and(expression2));
            test.setName("MultipleAndsExpressionTest4");
            test.setDescription("Test multiple booleans with supression in each branch");
            addTest(test);
        }
    
        private void addMultipleAndsTest5() {
            ExpressionBuilder builder = new ExpressionBuilder();
    
            //this is a bug, Ill-formed expression in query, attempting to print an object reference into a
            //SQL statement for Query Key address{DatabaseTable(t1)=DatabaseTable(ADDRESS)}
            //however, it should be ... EMPLOYEE.ADDRESS_ID = 0 (null)...
            Expression expression = builder.get("address").equal(null).and(builder.get("lastName").notEqual("foopoyp"));
    
            ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
            test.setExpression(expression);
            test.setName("MultipleAndsExpressionTest5");
            test.setDescription("Test multiple ands expression");
            addTest(test);
        }
    
        private void addMultipleAndsTest6() {
            ExpressionBuilder builder = new ExpressionBuilder();
    
            //this is a bug, Ill-formed expression in query, attempting to print an object reference into a
            //SQL statement for Query Key address{DatabaseTable(t1)=DatabaseTable(ADDRESS)}
            //however, it should be ... EMPLOYEE.ADDRESS_ID = 0 (null)...
            Expression expression = builder.get("address").isNull().and(builder.get("lastName").notEqual("foopoyp"));
    
            ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
            test.setExpression(expression);
            test.setName("MultipleAndsExpressionTest6");
            test.setDescription("Test multiple ands expression");
            addTest(test);
        }

        /*
         * bug 5683148/2380: Reducing unnecessary joins on an equality check between the a statement
         *   and itself
         */
        private void addEqualUnneccessaryJoinTest() {
            Employee employee = (Employee)PopulationManager.getDefaultManager().getObject(new Employee().getClass(), "0008");

            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.get("firstName").equal("Fred").or(builder.get("manager").notEqual(builder.get("manager")));

            ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
            test.setName("EqualUnneccessaryJoinTest");
            test.setDescription("Test = expression does not create an extra unneccessary join");
            addTest(test);
        }
        
        /**
         *  @bug 2612185 Support ReportItems,OrderBy Expressions from Parallel Builders.
         *  Find all managers of employees who have a spouse at work and a family
         *  income in excess of 100,000.
         */
        private void addManagersOfWealthyMarriedAtWorkEmployeesTest() {
            ExpressionBuilder builder = new ExpressionBuilder();
            ExpressionBuilder innerBuilder = new ExpressionBuilder(Employee.class);
            ExpressionBuilder innerSpouses = new ExpressionBuilder(Employee.class);

            Expression innerExpression = innerBuilder.get("manager").equal(builder);
            innerExpression = innerExpression.and(innerBuilder.get("lastName").equal(innerSpouses.get("lastName")));
            innerExpression = innerExpression.and(innerBuilder.get("gender").notEqual(innerSpouses.get("gender")));

            ReportQuery subquery = new ReportQuery(Employee.class, innerBuilder);
            subquery.addAverage("family income", ExpressionMath.add(innerBuilder.get("salary"), innerSpouses.get("salary")));
            subquery.setSelectionCriteria(innerExpression);

            Expression expression = builder.subQuery(subquery).equal(140000);

            ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
            test.setExpression(expression);
            test.testBatchAttributesOnEmployee();
            test.setName("ManagersOfWealthyMarriedAtWorkEmployeesTest");
            test.setDescription("Test executing query where subselect is a ReportQuery with ReportItems from multiple builders.  Does not tests batch reading.  For 2612185.");
            test.addUnsupportedPlatform(TimesTenPlatform.class);
            addTest(test);
        }
        /**
         * @bug 2720149 INVALID SQL WHEN USING BATCH READS AND MULTIPLE ANYOFS
         */
        private void addOneToManyJoin2WithBatchReadTest() {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression expression = builder.anyOf("managedEmployees").get("lastName").like("Sa%");
            Expression exp2 = builder.anyOf("managedEmployees").get("firstName").like("B%");

            expression = expression.and(exp2);

            ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
            test.setExpression(expression);
            test.testBatchAttributesOnEmployee();
            test.setName("OneToManyJoin2WithBatchReadTest");
            test.setDescription("Test a join across a 1:many relation with 2 anyOf clauses, and test again as part of a batch read.");
            addTest(test);
        }
    }

    public static TestSuite getUOWInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeUOWBasicInsertObjectTestSuite");
        suite.setDescription("This suite tests the insertion of each object in the employee demo using uow.");
        EmployeePopulator populator = new EmployeePopulator();

        suite.addTest(new UnitOfWorkBasicInsertObjectTest(populator.basicEmployeeExample1()));
        suite.addTest(new UnitOfWorkBasicInsertObjectTest(populator.basicEmployeeExample2()));
        suite.addTest(new UnitOfWorkBasicInsertObjectTest(populator.basicEmployeeExample3()));
        suite.addTest(new UnitOfWorkBasicInsertObjectTest(populator.basicEmployeeExample4()));
        suite.addTest(new UnitOfWorkBasicInsertObjectTest(populator.basicEmployeeExample5()));

        return suite;
    }

    static class EmployeeDeleteTest extends DeleteObjectTest {
        public EmployeeDeleteTest(Object originalObject) {
            super(originalObject);
        }

        protected void setup() {
            super.setup();
            // Must drop references first to appease constraints.
            getSession().executeNonSelectingCall(new SQLCall("delete from OTOJT_PROJ_LEADER where EMP_ID = " + ((Employee)getOriginalObject()).getId()));
            getSession().executeNonSelectingCall(new SQLCall("delete from OTOJT_EMP_MANAGER where MANAGER_ID = " + ((Employee)getOriginalObject()).getId()));
        }
    }
    static class ProjectDeleteTest extends DeleteObjectTest {
        public ProjectDeleteTest(Object originalObject) {
            super(originalObject);
        }

        protected void setup() {
            super.setup();
            // Must drop references first to appease constraints.
            getSession().executeNonSelectingCall(new SQLCall("delete from OTOJT_PROJ_EMP where PROJ_ID = " + ((Project)getOriginalObject()).getId()));
        }
    }

    static class ReadAllExpressionTest extends org.eclipse.persistence.testing.tests.expressions.ReadAllExpressionTest {
        public ReadAllExpressionTest(Class referenceClass, int originalObjectsSize) {
            super(referenceClass, originalObjectsSize);
        }

        protected void setupBatchAttributes() {
            getQuery().addBatchReadAttribute("children");
            getQuery().addBatchReadAttribute("address");
            getQuery().addBatchReadAttribute("responsibilitiesList");
            getQuery().addBatchReadAttribute("projects");
        }
        
        protected void testBatchAttributes() {
            Vector result = (Vector) this.objectsFromDatabase;
            List children = ((Employee) result.elementAt(0)).getChildren();
            ((Employee) result.elementAt(0)).getResponsibilitiesList().size();
            ((Employee) result.elementAt(0)).getProjects().size();
            if ((children == null) || (children.size() == 0)) {
                    throw new TestErrorException("The original query was corrupted when made part of a batch query.");
            }
            if (((Employee) result.elementAt(0)).getAddress() == null) {
                    throw new TestErrorException("The original query was corrupted when made part of a batch query.");
            }
        }
    }
}
