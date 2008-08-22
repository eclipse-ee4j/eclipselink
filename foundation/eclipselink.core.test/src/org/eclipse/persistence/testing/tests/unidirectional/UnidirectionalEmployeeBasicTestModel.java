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
 *     ailitchev - Uni-directional OneToMany
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unidirectional;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.unidirectional.*;
import org.eclipse.persistence.testing.tests.writing.ComplexUpdateTest;

/**
 * This model tests reading/writing/deleting through using the employee demo.
 */
public class UnidirectionalEmployeeBasicTestModel extends TestModel {

    /**
     * Return the JUnit suite to allow JUnit runner to find it.
     * Unfortunately JUnit only allows suite methods to be static,
     * so it is not possible to generically do this.
     */
    public static junit.framework.TestSuite suite() {
        return new UnidirectionalEmployeeBasicTestModel();
    }

    public UnidirectionalEmployeeBasicTestModel() {
        setDescription("This model tests reading/writing/deleting using the unidirectionl employee demo.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new EmployeeSystem());
    }

    public void addTests() {
        addTest(getReadObjectTestSuite());
        addTest(getComplexUpdateObjectTestSuite());
        addTest(getInsertObjectTestSuite());
        addTest(getDeleteObjectTestSuite());
        addTest(getReadAllTestSuite());
    }

    public static TestSuite getDeleteObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeDeleteObjectTestSuite");
        suite.setDescription("This suite tests the deletion of each object in the employee demo.");

        Class employeeClass = org.eclipse.persistence.testing.models.unidirectional.Employee.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new DeleteObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new DeleteObjectTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new DeleteObjectTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new DeleteObjectTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new DeleteObjectTest(manager.getObject(employeeClass, "0005")));

        return suite;
    }

    public static TestSuite getInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeUOWBasicInsertObjectTestSuite");
        suite.setDescription("This suite tests the insertion of each object in the employee demo using uow.");
        EmployeePopulator system = new EmployeePopulator();

        suite.addTest(new UnitOfWorkBasicInsertObjectTest(system.basicEmployeeExample1()));
        suite.addTest(new UnitOfWorkBasicInsertObjectTest(system.basicEmployeeExample2()));
        suite.addTest(new UnitOfWorkBasicInsertObjectTest(system.basicEmployeeExample3()));
        suite.addTest(new UnitOfWorkBasicInsertObjectTest(system.basicEmployeeExample4()));
        suite.addTest(new UnitOfWorkBasicInsertObjectTest(system.basicEmployeeExample5()));

        return suite;
    }

    public static TestSuite getReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeReadAllTestSuite");
        suite.setDescription("This suite tests the reading of all the objects of each class in the employee demo.");

        suite.addTest(new ReadAllTest(Employee.class, 12));

        suite.addTest(new ReadAllCallTest(Employee.class, 12, new SQLCall("SELECT VERSION, EMP_ID, L_NAME, F_NAME FROM UNIDIR_EMPLOYEE")));

        //Add new tests here ...
        Expression orderBy = new ExpressionBuilder().get("firstName").ascending();
        Call call = new SQLCall("SELECT VERSION, EMP_ID, L_NAME, F_NAME FROM UNIDIR_EMPLOYEE");
        suite.addTest(new ReadAllCallWithOrderingTest(Employee.class, 12, call, orderBy));

        return suite;
    }

    public static TestSuite getReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeReadObjectTestSuite");
        suite.setDescription("This suite test the reading of each object in the employee demo.");

        Class employeeClass = Employee.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0005")));

        Employee employee = (Employee)manager.getObject(employeeClass, "0001");
        suite.addTest(new ReadObjectCallTest(employeeClass, new SQLCall("SELECT VERSION, EMP_ID, L_NAME, F_NAME FROM UNIDIR_EMPLOYEE WHERE F_NAME = '"+employee.getFirstName()+"' AND L_NAME = '"+employee.getLastName()+"'")));
        employee = (Employee)manager.getObject(employeeClass, "0002");
        suite.addTest(new ReadObjectCallTest(employeeClass, new SQLCall("SELECT VERSION, EMP_ID, L_NAME, F_NAME FROM UNIDIR_EMPLOYEE WHERE F_NAME = '"+employee.getFirstName()+"' AND L_NAME = '"+employee.getLastName()+"'")));
        employee = (Employee)manager.getObject(employeeClass, "0003");
        suite.addTest(new ReadObjectCallTest(employeeClass, new SQLCall("SELECT VERSION, EMP_ID, L_NAME, F_NAME FROM UNIDIR_EMPLOYEE WHERE F_NAME = '"+employee.getFirstName()+"' AND L_NAME = '"+employee.getLastName()+"'")));

        Employee emp1 = (Employee)manager.getObject(employeeClass, "0001");
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("id").equal(emp1.getId()));
        Expression employees = query.getExpressionBuilder().anyOf("managedEmployees");
        query.addJoinedAttribute(employees);
        Expression phones = employees.anyOf("phoneNumbers");
        query.addJoinedAttribute(phones);
        ReadObjectTest test1 = new ReadObjectTest(emp1);
        test1.setQuery(query);
        test1.setName("JoinTest: " + test1.getName());
        suite.addTest(test1);

        return suite;
    }

    public static TestSuite getComplexUpdateObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeComplexUpdateTestSuite");
        suite.setDescription("This suite tests the updating of each an employee by adding and/or removing managed employees and/or phones.");

        Class employeeClass = Employee.class;
        PopulationManager manager = PopulationManager.getDefaultManager();
        Employee originalEmployee = (Employee)manager.getObject(employeeClass, "0001");
        Employee otherEmployee = (Employee)manager.getObject(employeeClass, "0002");

        // add a managed Employee from other Employee managed List; remove the first managed Employee.
        suite.addTest(new EmployeeComplexUpdateTest(originalEmployee, otherEmployee.getManagedEmployees().get(0), originalEmployee.getManagedEmployees().get(0)));
        // remove the first Phone.
        suite.addTest(new EmployeeComplexUpdateTest(originalEmployee, (Object)null, originalEmployee.getPhoneNumbers().get(0)));
        suite.addTest(new CascadeLockingTest());

        return suite;
    }
    
    /**
     * Tests adding/removing of target object to/from the source for UnidirectionalOneToManyMapping.
     * Derived from ComplexUpdateTest, this test expects an instance of Employee as original object,
     * as well as object (or array, or list) of objects to be added to the original objects as the second parameter, 
     * as well as object (or array, or list) of objects to be removed from the original objects as the third parameter.
     * These objects should be of type either Employee or PhoneNumber. 
     */
    static class EmployeeComplexUpdateTest extends ComplexUpdateTest {
        List<Employee> managedEmployeesToAdd = new ArrayList<Employee>();
        List<Employee> managedEmployeesToRemove = new ArrayList<Employee>();
        List<PhoneNumber> phonesToAdd = new ArrayList<PhoneNumber>();
        List<PhoneNumber> phonesToRemove = new ArrayList<PhoneNumber>();
        public EmployeeComplexUpdateTest(Employee originalEmployee, List objectsToAdd, Object objectToRemove) {
            this(originalEmployee, (objectsToAdd != null ? objectsToAdd.toArray() : null), (objectToRemove != null ? new Object[]{objectToRemove} : null));
        }
        public EmployeeComplexUpdateTest(Employee originalEmployee, Object objectToAdd, List objectsToRemove) {
            this(originalEmployee, (objectToAdd != null ? new Object[]{objectToAdd} : null), (objectsToRemove != null ? objectsToRemove.toArray() : null));
        }
        public EmployeeComplexUpdateTest(Employee originalEmployee, List objectsToAdd, List objectsToRemove) {
            this(originalEmployee, (objectsToAdd != null ? objectsToAdd.toArray() : null), (objectsToRemove != null ? objectsToRemove.toArray() : null));
        }
        public EmployeeComplexUpdateTest(Employee originalEmployee, Object objectToAdd, Object objectToRemove) {
            this(originalEmployee, (objectToAdd != null ? new Object[]{objectToAdd} : null), (objectToRemove != null ? new Object[]{objectToRemove} : null));
        }
        public EmployeeComplexUpdateTest(Employee originalEmployee, Object[] objectsToAdd, Object objectToRemove) {
            this(originalEmployee, objectsToAdd, (objectToRemove != null ? new Object[]{objectToRemove} : null));
        }
        public EmployeeComplexUpdateTest(Employee originalEmployee, Object objectToAdd, Object[] objectsToRemove) {
            this(originalEmployee, (objectToAdd != null ? new Object[]{objectToAdd} : null), objectsToRemove);
        }
        public EmployeeComplexUpdateTest(Employee originalEmployee, Object[] objectsToAdd, Object[] objectsToRemove) {
            super(originalEmployee);
            this.usesUnitOfWork = true;
            if(objectsToAdd != null) {
                for(int i=0; i < objectsToAdd.length; i++) {
                    Object objectToAdd = objectsToAdd[i];
                    if(objectToAdd instanceof Employee) {
                        if(!originalEmployee.getManagedEmployees().contains(objectToAdd)) {
                            managedEmployeesToAdd.add((Employee)objectToAdd);
                        } else {
                            throw new TestWarningException("OriginalEmployee: " + originalEmployee + " already manages employee to be added: " + objectToAdd);
                        }
                    } else {
                        // must be Phone
                        if(!originalEmployee.getPhoneNumbers().contains(objectToAdd)) {
                            phonesToAdd.add((PhoneNumber)objectToAdd);
                        } else {
                            throw new TestWarningException("OriginalEmployee: " + originalEmployee + " already has the phonee to be added: " + objectToAdd);
                        }
                    }
                }
            }
            if(objectsToRemove != null) {
                for(int i=0; i < objectsToRemove.length; i++) {
                    Object objectToRemove = objectsToRemove[i];
                    if(objectToRemove instanceof Employee) {
                        if(originalEmployee.getManagedEmployees().contains(objectToRemove)) {
                            managedEmployeesToRemove.add((Employee)objectToRemove);
                        } else {
                            throw new TestWarningException("OriginalEmployee: " + originalEmployee + " doesn't manage employee to be removed: " + objectToRemove);
                        }
                    } else {
                        // must be Phone
                        if(originalEmployee.getPhoneNumbers().contains(objectToRemove)) {
                            phonesToRemove.add((PhoneNumber)objectToRemove);
                        } else {
                            throw new TestWarningException("OriginalEmployee: " + originalEmployee + " doesn't have the phonee to be removed: " + objectToRemove);
                        }
                    }
                }
            }
            // generate a meaningful test name
            String employeeString = "";
            if(managedEmployeesToAdd.size() > 0 || managedEmployeesToRemove.size() > 0 ) {
                String addString = "";
                if(managedEmployeesToAdd.size() > 0) {
                    addString = "add "+ managedEmployeesToAdd.size();
                }
                String removeString = "";
                if(managedEmployeesToRemove.size() > 0) {
                    removeString = "remove "+ managedEmployeesToRemove.size();
                }
                employeeString = addString +(addString.length()>0 && removeString.length()>0 ? " and " : " ") + removeString + " Employees";
            }
            String phoneString = "";
            if(phonesToAdd.size() > 0 || phonesToRemove.size() > 0 ) {
                String addString = "";
                if(phonesToAdd.size() > 0) {
                    addString = "add "+ phonesToAdd.size();
                }
                String removeString = "";
                if(phonesToRemove.size() > 0) {
                    removeString = "remove "+ phonesToRemove.size();
                }
                phoneString = addString +(addString.length()>0 && removeString.length()>0 ? " and " : "") + removeString + " Phones";
            }
            setName("EmployeeComplexUpdateTest: " + employeeString +(employeeString.length()>0 && phoneString.length()>0 ? "; " : "")+ phoneString+";");
            setDescription("The test updates original Employee object: " +originalObject.toString()+ " from the database by adding and/or removing managedEmployees and/or PhoneNumbers and verifies that the object updated correctly.");
        }
        public String getName() {
            String testName = super.getName();
            int lastIndex = testName.lastIndexOf(";");
            if(lastIndex > 0) {
                testName = testName.substring(0, lastIndex); 
            }
            return testName;
        }
        protected void changeObject() {
            UnitOfWork uow = (UnitOfWork)getSession();
            Employee cloneEmployee = (Employee)workingCopy;
            for(int i=0; i < managedEmployeesToAdd.size(); i++) {
                Employee cloneEmployeeToAdd = (Employee)uow.registerObject(managedEmployeesToAdd.get(i));
                cloneEmployee.addManagedEmployee(cloneEmployeeToAdd);
            }
            for(int i=0; i < managedEmployeesToRemove.size(); i++) {
                Employee cloneEmployeeToRemove = (Employee)uow.registerObject(managedEmployeesToRemove.get(i));
                cloneEmployee.removeManagedEmployee(cloneEmployeeToRemove);
            }
            for(int i=0; i < phonesToRemove.size(); i++) {
                PhoneNumber clonePhoneToRemove = (PhoneNumber)uow.registerObject(phonesToRemove.get(i));
                cloneEmployee.removePhoneNumber(clonePhoneToRemove);
            }
            for(int i=0; i < phonesToAdd.size(); i++) {
                PhoneNumber clonePhoneToAdd = (PhoneNumber)uow.registerObject(phonesToAdd.get(i));
                cloneEmployee.addPhoneNumber(clonePhoneToAdd);
            }
        }
        protected void setup() {
            super.setup();

            for(int i=0; i < managedEmployeesToAdd.size(); i++) {
                Employee readEmployeeToAdd = (Employee)readObject(managedEmployeesToAdd.get(i));
                if(readEmployeeToAdd != null) {
                    managedEmployeesToAdd.set(i, readEmployeeToAdd);
                } else {
                    // it's a new object
                }
            }
            for(int i=0; i < managedEmployeesToRemove.size(); i++) {
                Employee readEmployeeToRemove = (Employee)readObject(managedEmployeesToRemove.get(i));
                if(readEmployeeToRemove != null) {
                    managedEmployeesToRemove.set(i, readEmployeeToRemove);
                } else {
                    throw new TestWarningException("Employee to be removed: " + managedEmployeesToRemove.get(i) + " is not in the db");
                }
            }
            for(int i=0; i < phonesToRemove.size(); i++) {
                PhoneNumber readPhoneToRemove = (PhoneNumber)readObject(phonesToRemove.get(i));
                if(readPhoneToRemove != null) {
                    phonesToRemove.set(i, readPhoneToRemove);
                } else {
                    throw new TestWarningException("Phone to be removed: " + phonesToRemove.get(i) + " is not in the db");
                }
            }
            for(int i=0; i < phonesToAdd.size(); i++) {
                PhoneNumber readPhoneToAdd = (PhoneNumber)readObject(phonesToAdd.get(i));
                if(readPhoneToAdd != null) {
                    phonesToAdd.set(i, readPhoneToAdd);
                } else {
                    // it's a new object
                }
            }
        }
        protected Object readObject(Object object) {
            ReadObjectQuery query = new ReadObjectQuery();
            query.setSelectionObject(object);
            return getSession().executeQuery(query);
        }
    }

    /**
     * Tests cascade locking of the source Employee object for adding, modifying and removing the target object (Employee and Phone).
     * The version of he source should be always incremented on either adding or removing any target.
     * Also currently source's version incremented on changing of the privately owned target (Phone),
     * but on change of non private target (managed Employee) stays unchanged.  
     */
    static class CascadeLockingTest extends TransactionalTestCase {
        long version[] = new long[7];
        long versionExpected[] = new long[] {1, 2, 2, 3, 4, 5, 6};
        public void setup() {
            super.setup();
            for(int i=0; i<version.length; i++) {
                version[i] = 0;
            }
        }
        public void test() {
            // setup
            Employee manager = new Employee();
            manager.setFirstName("Manager");
            Employee employee = new Employee();
            employee.setFirstName("Employee");
            
            UnitOfWork uow = getSession().acquireUnitOfWork();
            uow.registerObject(manager);
            uow.registerObject(employee);
            uow.commit();
            
            Vector pk = new Vector(1);
            pk.add(manager.getId());
            
            version[0] = ((Long)getSession().getDescriptor(Employee.class).getOptimisticLockingPolicy().getWriteLockValue(manager, pk, getAbstractSession())).longValue();
            
            // test1 - add managed employee, manager's version should increment.
            uow = getSession().acquireUnitOfWork();
            Employee managerClone = (Employee)uow.registerObject(manager);
            Employee employeeClone = (Employee)uow.registerObject(employee);
            managerClone.addManagedEmployee(employeeClone);
            uow.commit();
            version[1] = ((Long)getSession().getDescriptor(Employee.class).getOptimisticLockingPolicy().getWriteLockValue(manager, pk, getAbstractSession())).longValue();
            
            // test2 - alter managed employee, manager's version should NOT increment.
            uow = getSession().acquireUnitOfWork();
            employeeClone = (Employee)uow.registerObject(employee);
            employeeClone.setFirstName("Altered");
            uow.commit();
            version[2] = ((Long)getSession().getDescriptor(Employee.class).getOptimisticLockingPolicy().getWriteLockValue(manager, pk, getAbstractSession())).longValue();
            
            // test3- remove managed employee, manager's version should increment.
            uow = getSession().acquireUnitOfWork();
            managerClone = (Employee)uow.registerObject(manager);
            employeeClone = (Employee)uow.registerObject(employee);
            managerClone.removeManagedEmployee(employeeClone);
            uow.commit();
            version[3] = ((Long)getSession().getDescriptor(Employee.class).getOptimisticLockingPolicy().getWriteLockValue(manager, pk, getAbstractSession())).longValue();
            
            PhoneNumber phone = new PhoneNumber("home", "613", "1111111");
            
            // test4 - add phone, manager's version should increment.
            uow = getSession().acquireUnitOfWork();
            managerClone = (Employee)uow.registerObject(manager);
            PhoneNumber phoneClone = (PhoneNumber)uow.registerObject(phone);
            managerClone.addPhoneNumber(phoneClone);
            uow.commit();
            version[4] = ((Long)getSession().getDescriptor(Employee.class).getOptimisticLockingPolicy().getWriteLockValue(manager, pk, getAbstractSession())).longValue();
            
            // test5- alter phone, manager's version should increment.
            uow = getSession().acquireUnitOfWork();
            phoneClone = (PhoneNumber)uow.registerObject(phone);
            phoneClone.setType("work");
            uow.commit();
            version[5] = ((Long)getSession().getDescriptor(Employee.class).getOptimisticLockingPolicy().getWriteLockValue(manager, pk, getAbstractSession())).longValue();
            
            // test6- remove phone, manager's version should increment.
            uow = getSession().acquireUnitOfWork();
            managerClone = (Employee)uow.registerObject(manager);
            phoneClone = (PhoneNumber)uow.registerObject(phone);
            managerClone.removePhoneNumber(phoneClone);
            uow.commit();
            version[6] = ((Long)getSession().getDescriptor(Employee.class).getOptimisticLockingPolicy().getWriteLockValue(manager, pk, getAbstractSession())).longValue();
            System.out.println(version);
        }
        public void verify() {
            int numTestsFailed = 0;
            String errorMsg = "";
            for(int i=0; i<version.length; i++) {
                if(version[i] + numTestsFailed != versionExpected[i]) {
                    numTestsFailed++;
                    errorMsg += "test" + i +" failed; ";
                }
            }
            if(numTestsFailed > 0) {
                throw new TestErrorException(errorMsg);
            }
        }
    }
}
