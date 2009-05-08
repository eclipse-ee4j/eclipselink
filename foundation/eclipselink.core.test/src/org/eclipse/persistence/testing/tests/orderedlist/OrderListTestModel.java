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
 *     05/05/2009 Andrei Ilitchev 
 *       - JPA 2.0 - OrderedList support.
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.orderedlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangeTrackingPolicy;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.models.orderedlist.*;
import org.eclipse.persistence.testing.models.orderedlist.EmployeeSystem.ChangeTracking;
import org.eclipse.persistence.testing.models.orderedlist.EmployeeSystem.JoinFetchOrBatchRead;

import org.eclipse.persistence.testing.framework.TestProblemException;

public class OrderListTestModel extends TestModel {
    boolean isTopLevel;
    boolean useListOrderField; 
    boolean useIndirection; 
    ChangeTracking changeTracking;
    JoinFetchOrBatchRead joinFetchOrBatchRead;
    
    Map<Class, ObjectChangeTrackingPolicy> originalChangeTrackingPolicies;
    Boolean shouldPrintOuterJoinInWhereClauseOriginal;
    
    
    /**
     * Return the JUnit suite to allow JUnit runner to find it.
     * Unfortunately JUnit only allows suite methods to be static,
     * so it is not possible to generically do this.
     */
    public static junit.framework.TestSuite suite() {
        return new OrderListTestModel();
    }

    public OrderListTestModel() {
        setDescription("This model tests ordered list.");
        isTopLevel = true;

        addTest(new OrderListTestModel(false, true, ChangeTracking.DEFERRED, JoinFetchOrBatchRead.NONE));
        addTest(new OrderListTestModel(false, true, ChangeTracking.DEFERRED, JoinFetchOrBatchRead.OUTER_JOIN));
        addTest(new OrderListTestModel(false, true, ChangeTracking.DEFERRED, JoinFetchOrBatchRead.BATCH_READ));

        addTest(new OrderListTestModel(false, false, ChangeTracking.DEFERRED, JoinFetchOrBatchRead.NONE));
        addTest(new OrderListTestModel(false, false, ChangeTracking.DEFERRED, JoinFetchOrBatchRead.OUTER_JOIN));
        addTest(new OrderListTestModel(false, false, ChangeTracking.DEFERRED, JoinFetchOrBatchRead.BATCH_READ));

        addTest(new OrderListTestModel(false, true, ChangeTracking.ATTRIBUTE, JoinFetchOrBatchRead.NONE));
        addTest(new OrderListTestModel(false, true, ChangeTracking.ATTRIBUTE, JoinFetchOrBatchRead.OUTER_JOIN));
        addTest(new OrderListTestModel(false, true, ChangeTracking.ATTRIBUTE, JoinFetchOrBatchRead.BATCH_READ));

        addTest(new OrderListTestModel(true, true, ChangeTracking.DEFERRED, JoinFetchOrBatchRead.NONE));
        addTest(new OrderListTestModel(true, true, ChangeTracking.DEFERRED, JoinFetchOrBatchRead.OUTER_JOIN));
        addTest(new OrderListTestModel(true, true, ChangeTracking.DEFERRED, JoinFetchOrBatchRead.BATCH_READ));

        addTest(new OrderListTestModel(true, false, ChangeTracking.DEFERRED, JoinFetchOrBatchRead.NONE));
        addTest(new OrderListTestModel(true, false, ChangeTracking.DEFERRED, JoinFetchOrBatchRead.OUTER_JOIN));
        addTest(new OrderListTestModel(true, false, ChangeTracking.DEFERRED, JoinFetchOrBatchRead.BATCH_READ));

        addTest(new OrderListTestModel(true, true, ChangeTracking.ATTRIBUTE, JoinFetchOrBatchRead.NONE));
        addTest(new OrderListTestModel(true, true, ChangeTracking.ATTRIBUTE, JoinFetchOrBatchRead.OUTER_JOIN));
        addTest(new OrderListTestModel(true, true, ChangeTracking.ATTRIBUTE, JoinFetchOrBatchRead.BATCH_READ));

    }

    public OrderListTestModel(boolean useListOrderField, boolean useIndirection, ChangeTracking changeTracking, JoinFetchOrBatchRead joinFetchOrBatchRead) {
        this.useListOrderField = useListOrderField;
        this.useIndirection = useIndirection; 
        this.changeTracking = changeTracking;
        this.joinFetchOrBatchRead = joinFetchOrBatchRead;
        setDescription("This model tests ordered list " + changeTracking + " " + joinFetchOrBatchRead);
        String useOrderListString = useListOrderField ? " " : " NO_ORDER_LIST "; 
        String useIndirectionString = useIndirection ? " " : " NO_INDIRECTION "; 
        setName(useOrderListString + useIndirectionString + changeTracking + " " + joinFetchOrBatchRead);
        if(!this.useIndirection && this.changeTracking.equals(ChangeTracking.ATTRIBUTE)) {
            throw new TestProblemException("ATTRIBUTE can't work with NO_INDIRECTION");
        }
    }

    public void addRequiredSystems() {
        if(!isTopLevel) {
            addRequiredSystem(new EmployeeSystem(this.useListOrderField, this.useIndirection, this.changeTracking, this.joinFetchOrBatchRead));
        }
    }

    public void addTests() {
        if(!isTopLevel) {
            addTest(new SimpleTest(useListOrderField, useIndirection, changeTracking, joinFetchOrBatchRead));
            addTest(new MultipleManagersTest(changeTracking, joinFetchOrBatchRead));
        }
    }
    
    public void setup() {
        if(!isTopLevel) {
            if(changeTracking == ChangeTracking.ATTRIBUTE) {
                // Save change policies for the all employee demo class in order to restore them at reset time.
                Map originalChangeTrackingPolicies = new HashMap<Class, ObjectChangeTrackingPolicy>();

                originalChangeTrackingPolicies.put(Employee.class, getSession().getDescriptor(Employee.class).getObjectChangePolicy());
                getSession().getDescriptor(Employee.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());
        
                originalChangeTrackingPolicies.put(Project.class, getSession().getDescriptor(Project.class).getObjectChangePolicy());
                getSession().getDescriptor(Project.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());
        
                originalChangeTrackingPolicies.put(SmallProject.class, getSession().getDescriptor(SmallProject.class).getObjectChangePolicy());
                getSession().getDescriptor(SmallProject.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());
        
                originalChangeTrackingPolicies.put(LargeProject.class, getSession().getDescriptor(LargeProject.class).getObjectChangePolicy());
                getSession().getDescriptor(LargeProject.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());
        
                // currently attribute change tracking is incompatible with AggregateCollectionMapping
                if(this.changeTracking != ChangeTracking.ATTRIBUTE) {
                    originalChangeTrackingPolicies.put(PhoneNumber.class, getSession().getDescriptor(PhoneNumber.class).getObjectChangePolicy());
                    getSession().getDescriptor(PhoneNumber.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());
                }
            }
/*            if(joinFetchOrBatchRead == JoinFetchOrBatchRead.OUTER_JOIN) {
                if(getSession().getPlatform().shouldPrintOuterJoinInWhereClause()) {
                    getSession().getPlatform().setPrintOuterJoinInWhereClause(false);
                    shouldPrintOuterJoinInWhereClauseOriginal = Boolean.TRUE;
                }
            }*/
        }
    }

    public void reset() {
        if(!isTopLevel) {
            // restore original change policies.
            if(originalChangeTrackingPolicies != null) {
                Iterator<Map.Entry<Class, ObjectChangeTrackingPolicy>> it = originalChangeTrackingPolicies.entrySet().iterator();
                while(it.hasNext()) {
                    Map.Entry<Class, ObjectChangeTrackingPolicy> entry = it.next();
                    getSession().getDescriptor(entry.getKey()).setObjectChangePolicy(entry.getValue());
                }
                originalChangeTrackingPolicies = null;
            }
            // restore original 
            if(shouldPrintOuterJoinInWhereClauseOriginal != null) {
                getSession().getPlatform().setPrintOuterJoinInWhereClause(shouldPrintOuterJoinInWhereClauseOriginal);
                shouldPrintOuterJoinInWhereClauseOriginal = null;
            }
        }
    }

    static class SimpleTest extends TestCase {
        boolean useListOrderField;
        boolean useIndirection;
        ChangeTracking changeTracking;
        JoinFetchOrBatchRead joinFetchOrBatchRead;
        
        boolean useManagedEmployees = true;
        boolean useChildren = true;
        boolean useProjects = true;
        boolean useResponsibilities = true;
        boolean usePhones = true;
        
        boolean shouldRunTest2 = true;
        boolean shouldRunTest3 = true;
        boolean shouldRunTest4 = true;
        boolean shouldRunTest5 = true;
        
        Employee manager;
        String errorMsg = "";
        
        SimpleTest(boolean useListOrderField, boolean useIndirection, ChangeTracking changeTracking, JoinFetchOrBatchRead joinFetchOrBatchRead) {
            this.useListOrderField = useListOrderField;
            this.useIndirection = useIndirection;
            this.changeTracking = changeTracking;
            this.joinFetchOrBatchRead = joinFetchOrBatchRead;
            
            // currently attribute change tracking is incompatible with AggregateCollectionMapping
            if(this.changeTracking == ChangeTracking.ATTRIBUTE) {
                usePhones = false;
            }
        }

        public void test() {            
            // test1: Insert manager with two elements in each list.
            manager = new Employee("Manager");
            if(useManagedEmployees) {
                Employee emp0 = new Employee("0");
                Employee emp1 = new Employee("1");
                manager.addManagedEmployee(emp0);
                manager.addManagedEmployee(emp1);
            }
            if(useChildren) {
                Child child0 = new Child("0");
                Child child1 = new Child("1");
                manager.getChildren().add(child0);
                manager.getChildren().add(child1);
            }
            if(useProjects) {
                SmallProject project0 = new SmallProject("0");
                LargeProject project1 = new LargeProject("1");
                manager.addProject(project0);
                manager.addProject(project1);
            }
            if(useResponsibilities) {
                manager.addResponsibility("0");
                manager.addResponsibility("1");
            }
            if(usePhones) {
                manager.addPhoneNumber(new PhoneNumber("0", "000", "0000000"));
                manager.addPhoneNumber(new PhoneNumber("1", "111", "1111111"));
            }
            UnitOfWork uow = getSession().acquireUnitOfWork();
            Employee managerClone = (Employee)uow.registerObject(manager);
            uow.commit();
//            verify("test1", new int[]{0, 1});
            verifyCompare("test1", managerClone);
            
            
            /*    if(this.changeTracking.equals(ChangeTracking.ATTRIBUTE)) {
            managerClone.getResponsibilitiesList().set(1, "new");
            managerClone.getResponsibilitiesList().add(0, "new");
            managerClone.getResponsibilitiesList().add("new");
        }*/

            Employee emp0Clone, emp1Clone;
            Child child0Clone, child1Clone;
            Project project0Clone, project1Clone;
            PhoneNumber phone0Clone;
            
            // test2: switch order of two elements
            if(shouldRunTest2) {
                uow = getSession().acquireUnitOfWork();
                managerClone = (Employee)uow.registerObject(manager);
                if(useManagedEmployees) {
                    emp1Clone = managerClone.getManagedEmployees().remove(1);
                    emp0Clone = managerClone.getManagedEmployees().remove(0);
                    managerClone.addManagedEmployee(emp1Clone);
                    managerClone.addManagedEmployee(emp0Clone);
                }
                if(useChildren) {
                    child1Clone = (Child)managerClone.getChildren().remove(1);
                    child0Clone = (Child)managerClone.getChildren().remove(0);
                    managerClone.getChildren().add(child1Clone);
                    managerClone.getChildren().add(child0Clone);
                }
                if(useProjects) {
                    project1Clone = managerClone.getProjects().remove(1);
                    project0Clone = managerClone.getProjects().remove(0);
                    managerClone.addProject(project1Clone);
                    managerClone.addProject(project0Clone);
                }
                if(useResponsibilities) {
                    managerClone.removeResponsibility("0");
                    managerClone.addResponsibility("0");
                }
                if(usePhones) {
                    phone0Clone = managerClone.removePhoneNumber(0);
                    managerClone.addPhoneNumber(phone0Clone);
                }
                uow.commit();
    //            verify("test2", new int[]{1, 0});
                verifyCompare("test2", managerClone);
            }

            // test3: add a new element and remove existing one
            if(shouldRunTest3) {
                uow = getSession().acquireUnitOfWork();
                managerClone = (Employee)uow.registerObject(manager);
                if(useManagedEmployees) {
                    Employee emp2 = new Employee("2");
                    Employee emp2Clone = (Employee)uow.registerObject(emp2);
                    managerClone.addManagedEmployee(emp2Clone);
                    emp0Clone = managerClone.removeManagedEmployee(0);
                    uow.deleteObject(emp0Clone);
                }
                if(useChildren) {
                    Child child2 = new Child("2");
                    Child child2Clone = (Child)uow.registerObject(child2);
                    managerClone.getChildren().add(child2Clone);
                    child0Clone = (Child)managerClone.getChildren().remove(0);
                }
                if(useProjects) {
                    LargeProject project2 = new LargeProject("2");
                    LargeProject project2Clone = (LargeProject)uow.registerObject(project2);
                    managerClone.addProject(project2Clone);
                    project0Clone = managerClone.removeProject(0);
                    uow.deleteObject(project0Clone);
                }
                if(useResponsibilities) {
                    managerClone.addResponsibility("2");
                    managerClone.removeResponsibility("1");
                }
                if(usePhones) {
                    managerClone.removePhoneNumber(0);
                    managerClone.addPhoneNumber(new PhoneNumber("2", "222", "2222222"));
                }    
                uow.commit();
    //            verify("test3", new int[]{0, 2});
                verifyCompare("test3", managerClone);
            }
            
            if(shouldRunTest4) {
                // test4: add a new element to collection, then set a new collections 
                uow = getSession().acquireUnitOfWork();
                managerClone = (Employee)uow.registerObject(manager);
                if(useManagedEmployees) {
                    List<Employee> oldManagedEmployees = managerClone.getManagedEmployees();
    
                    managerClone.addManagedEmployee(new Employee("temp"));
                    List newManagedEmployees = new ArrayList(2);
                    emp0Clone = new Employee("new0");
                    emp0Clone.setManager(managerClone);
                    newManagedEmployees.add(emp0Clone);
                    emp1Clone = new Employee("new1");
                    emp1Clone.setManager(managerClone);
                    newManagedEmployees.add(emp1Clone);
                    managerClone.setManagedEmployees(newManagedEmployees);
    
                    for(int i=0; i < oldManagedEmployees.size(); i++) {
                        Employee oldEmployee = oldManagedEmployees.get(i);
                        oldEmployee.setManager(null);
                    }
                }
                if(useChildren) {
                    managerClone.getChildren().add(new Child("temp"));
                    Vector newChildren = new Vector(2);
                    newChildren.add(new Child("new0"));
                    newChildren.add(new Child("new1"));
                    managerClone.setChildren(newChildren);
                }
                if(useProjects) {
                    managerClone.addProject(new LargeProject("temp"));
                    List newProjects = new ArrayList(2);
                    newProjects.add(new SmallProject("new0"));
                    newProjects.add(new LargeProject("new1"));
                    managerClone.setProjects(newProjects);
                }
                if(useResponsibilities) {
                    managerClone.addResponsibility("temp");
                    List newResponsibilities = new ArrayList(2);
                    newResponsibilities.add("new0");
                    newResponsibilities.add("new1");
                    managerClone.setResponsibilitiesList(newResponsibilities);
                }
                if(usePhones) {
                    managerClone.addPhoneNumber(new PhoneNumber("9", "tmp", "9999999"));
                    List newPhones = new ArrayList(2);
                    newPhones.add(new PhoneNumber("0", "new", "0000000"));
                    newPhones.add(new PhoneNumber("1", "new", "1111111"));
                    managerClone.setPhoneNumbers(newPhones);
                }
                uow.commit();
                verifyCompare("test4", managerClone);
            }
            
            if(shouldRunTest5) {
                // test5: the same as test4, but start with setting a new element into collection, then set a new collections 
                uow = getSession().acquireUnitOfWork();
                managerClone = (Employee)uow.registerObject(manager);
                if(useManagedEmployees) {
                    List<Employee> oldManagedEmployees = managerClone.getManagedEmployees();
    
                    Employee temp = new Employee("temp");
                    Employee removedEmployee = managerClone.getManagedEmployees().set(0, temp);
                    temp.setManager(managerClone);
                    
                    List newManagedEmployees = new ArrayList(2);
                    emp0Clone = new Employee("newer0");
                    emp0Clone.setManager(managerClone);
                    newManagedEmployees.add(emp0Clone);
                    emp1Clone = new Employee("newer1");
                    emp1Clone.setManager(managerClone);
                    newManagedEmployees.add(emp1Clone);
                    managerClone.setManagedEmployees(newManagedEmployees);
    
                    for(int i=0; i < oldManagedEmployees.size(); i++) {
                        oldManagedEmployees.get(i).setManager(null);
                    }
                    removedEmployee.setManager(null);
                }
                if(useChildren) {
                    managerClone.getChildren().set(0, new Child("temp"));
                    Vector newChildren = new Vector(2);
                    newChildren.add(new Child("newer0"));
                    newChildren.add(new Child("newer1"));
                    managerClone.setChildren(newChildren);
                }
                if(useProjects) {
                    Project temp = new LargeProject("temp");
                    managerClone.getProjects().set(0, temp);
                    
                    List newProjects = new ArrayList(2);
                    newProjects.add(new SmallProject("newer0"));
                    newProjects.add(new LargeProject("newer1"));
                    managerClone.setProjects(newProjects);
                }
                if(useResponsibilities) {
                    managerClone.getResponsibilitiesList().set(0, "temp");
                    List newResponsibilities = new ArrayList(2);
                    newResponsibilities.add("new0");
                    newResponsibilities.add("new1");
                    managerClone.setResponsibilitiesList(newResponsibilities);
                }
                if(usePhones) {
                    managerClone.getPhoneNumbers().set(0, new PhoneNumber("9", "tmp", "9999999"));
                    List newPhones = new ArrayList(2);
                    newPhones.add(new PhoneNumber("0", "nwr", "0000000"));
                    newPhones.add(new PhoneNumber("1", "nwr", "1111111"));
                    managerClone.setPhoneNumbers(newPhones);
                }
                uow.commit();
                verifyCompare("test5", managerClone);
            }
            
            //**temp
/*            uow = getSession().acquireUnitOfWork();
            managerClone = (Employee)uow.registerObject(manager);
            managerClone.addPhoneNumber(new PhoneNumber("3", "333", "3333333"));
            managerClone.addPhoneNumber(new PhoneNumber("4", "444", "4444444"));
            managerClone.addPhoneNumber(new PhoneNumber("5", "555", "5555555"));
//            managerClone.getPhoneNumbers().add(null);
            PhoneNumber phone0Clone = managerClone.removePhoneNumber(0);
            managerClone.addPhoneNumber(phone0Clone);
            PhoneNumber phone2Clone = managerClone.removePhoneNumber(0);
            managerClone.addPhoneNumber(phone2Clone);
            uow.commit();
            verifyCompare("test4", managerClone);*/

            // test4: duplicate: add object to the list for the second time
            // only ManyToMany and DirectCollectionMapping allow duplicates
/*temp            uow = getSession().acquireUnitOfWork();
            managerClone = (Employee)uow.registerObject(manager);
//            managerClone.getManagedEmployees().add(managerClone.getManagedEmployees().get(1));
//            managerClone.getChildren().add(managerClone.getChildren().get(1));
            managerClone.addProject(managerClone.getProjects().get(1));
            LargeProject project3 = new LargeProject("3");
            LargeProject project3Clone = (LargeProject)uow.registerObject(project3);
            managerClone.getProjects().add(0, project3Clone);
            managerClone.getProjects().add(2, project3Clone);
            managerClone.getProjects().add(4, project3Clone);
            project3Clone.getEmployees().add(managerClone);
            project3Clone.getEmployees().add(managerClone);
            project3Clone.getEmployees().add(managerClone);
            managerClone.addResponsibility(managerClone.getResponsibilitiesList().get(1));
            managerClone.getResponsibilitiesList().add(0, "3");
            managerClone.getResponsibilitiesList().add(2, "3");
            managerClone.getResponsibilitiesList().add(4, "3");
//            managerClone.addPhoneNumber(managerClone.getPhoneNumbers().get(1));
            uow.commit();
            verify("test4", new int[]{3, 0, 3, 2, 3, 2}, true);*/

            
            System.out.println();
        }
        protected void verify() {
            if(errorMsg.length() > 0) {
                throw new TestErrorException('\n' + errorMsg);
            }
        }
        public void reset() {
            if(manager != null) {
                getSession().executeNonSelectingSQL("DELETE FROM OL_PROJ_EMP");
                getSession().executeNonSelectingSQL("DELETE FROM OL_CHILD");
                getSession().executeNonSelectingSQL("DELETE FROM OL_PHONE");
                getSession().executeNonSelectingSQL("DELETE FROM OL_RESPONS");
                getSession().executeNonSelectingSQL("UPDATE OL_EMPLOYEE SET MANAGER_ID = NULL");
                getSession().executeNonSelectingSQL("DELETE FROM OL_EMPLOYEE");
                getSession().executeNonSelectingSQL("DELETE FROM OL_LPROJECT");
                getSession().executeNonSelectingSQL("DELETE FROM OL_PROJECT");
                
                getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                
                manager = null;
                errorMsg = "";
            }
        }
        
        protected void verify(String testName, int[] expected) {
            verify(testName, expected, false);
        }
        // only ManyToMany and DirectCollectionMapping allow duplicates
        protected void verify(String testName, int[] expected, boolean hasDuplicates) {
            String textNameExt;
            for(int k=0; k<2; k++) {
                if(k == 0) {
                    textNameExt = testName+"(Cache)";
                } else {
                    textNameExt = testName+"(DB)";
                    // Read back the inserted objects, make sure the order is correct.
                    getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                    ReadObjectQuery query = new ReadObjectQuery();
                    query.setReferenceClass(Employee.class);
                    Expression exp = query.getExpressionBuilder().get("id").equal(manager.getId());
                    query.setSelectionCriteria(exp);
                    manager = (Employee)getSession().executeQuery(query);
                }
    
                if(!hasDuplicates) {
                    // check managedEmployees
                    int size = manager.getManagedEmployees().size();
                    if(size != expected.length) {
                        String localErrorMsg = "wrong managedEmployees size " + size +"; expected " + expected.length;
                        errorMsg += textNameExt + ": " + localErrorMsg + "\n";
                    } else {
                        String localErrorMsg = "";
                        for(int i=0; i < size; i++) {
                            Employee emp = manager.getManagedEmployees().get(i);
                            if(Integer.parseInt(emp.getFirstName()) != expected[i]) {
                                localErrorMsg += "wrong managedEmployee("+i+").firstName() == "+emp.getFirstName()+", expected " + expected[i] + "; ";
                            }
                        }
                        if(localErrorMsg.length() > 0) {
                            errorMsg += textNameExt + ": " + localErrorMsg + "\n";
                        }
                    }
        
                    // check children
                    size = manager.getChildren().size();
                    if(size != expected.length) {
                        String localErrorMsg = "wrong children size " + size +"; expected " + expected.length;
                        errorMsg += textNameExt + ": " + localErrorMsg + "\n";
                    } else {
                        String localErrorMsg = "";
                        for(int i=0; i < size; i++) {
                            Child child = (Child)manager.getChildren().get(i);
                            if(Integer.parseInt(child.getFirstName()) != expected[i]) {
                                localErrorMsg += "wrong child("+i+").firstName() == "+child.getFirstName()+", expected " + expected[i] + "; ";
                            }
                        }
                        if(localErrorMsg.length() > 0) {
                            errorMsg += textNameExt + ": " + localErrorMsg + "\n";
                        }
                    }
                    
                    // check phone number
                    size = manager.getPhoneNumbers().size();
                    if(size != expected.length) {
                        String localErrorMsg = "wrong phoneNumbers size " + size +", expected " + expected.length;
                        errorMsg += textNameExt + ": " + localErrorMsg + "\n";
                    } else {
                        String localErrorMsg = "";
                        for(int i=0; i < size; i++) {
                            PhoneNumber phone = manager.getPhoneNumbers().get(i);
                            if(Integer.parseInt(phone.getType()) != expected[i]) {
                                localErrorMsg += "wrong phone("+i+") == "+phone.getType()+", expected " + expected[i] + "; ";
                            }
                        }
                        if(localErrorMsg.length() > 0) {
                            errorMsg += textNameExt + ": " + localErrorMsg + "\n";
                        }
                    }
                }
    
                // check projects
                int size = manager.getProjects().size();
                if(size != expected.length) {
                    String localErrorMsg = "wrong projects size " + size +", expected " + expected.length;
                    errorMsg += textNameExt + ": " + localErrorMsg + "\n";
                } else {
                    String localErrorMsg = "";
                    for(int i=0; i < size; i++) {
                        Project project = manager.getProjects().get(i);
                        if(Integer.parseInt(project.getName()) != expected[i]) {
                            localErrorMsg += "wrong project("+i+").name() == "+project.getName()+", expected " + expected[i] + "; ";
                        }
                    }
                    if(localErrorMsg.length() > 0) {
                        errorMsg += textNameExt + ": " + localErrorMsg + "\n";
                    }
                }
    
                // check responsibilities
                size = manager.getResponsibilitiesList().size();
                if(size != expected.length) {
                    String localErrorMsg = "wrong responsibilitiesList size " + size +", expected " + expected.length;
                    errorMsg += textNameExt + ": " + localErrorMsg + "\n";
                } else {
                    String localErrorMsg = "";
                    for(int i=0; i < size; i++) {
                        String resp = (String)manager.getResponsibilitiesList().get(i);
                        if(Integer.parseInt(resp) != expected[i]) {
                            localErrorMsg += "wrong responsibility("+i+") == "+resp+", expected " + expected[i] + "; ";
                        }
                    }
                    if(localErrorMsg.length() > 0) {
                        errorMsg += textNameExt + ": " + localErrorMsg + "\n";
                        localErrorMsg = "";
                    }
                }
            }
        }
        
        void verifyCompare(String testName, Object managerClone) {
            String textNameExt;
            Object objectToCompare;
            for(int k=0; k<2; k++) {
                if(k == 0) {
                    textNameExt = testName+"(Cache)";
                } else {
                    textNameExt = testName+"(DB)";

                    // Read back the inserted objects, make sure the order is correct.
                    getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                    manager = (Employee)getSession().readObject(manager);
                }
                if(!getAbstractSession().compareObjects(managerClone, manager)) {
                    String localErrorMsg = textNameExt + ": " + "objects not equal\n";
                    errorMsg += localErrorMsg;
                }
            }
        }
    }
    
    static class MultipleManagersTest extends TestCase {
        ChangeTracking changeTracking;
        JoinFetchOrBatchRead joinFetchOrBatchRead;
        
        MultipleManagersTest(ChangeTracking changeTracking, JoinFetchOrBatchRead joinFetchOrBatchRead) {
            this.changeTracking = changeTracking;
            this.joinFetchOrBatchRead = joinFetchOrBatchRead;
        }
        
        int nManagers = 5;
        List<Employee> managers = new ArrayList(nManagers);
        List<Employee> managerClones = new ArrayList(nManagers);
        int nProjects = 4;
        List<Project> projects = new ArrayList(nProjects);
        int nManagedEmployees = 2;
        int nChildren = 2;
        // should be <= 9
        int nPhones = 2;
        int nResponsibilities = 2;
        String errorMsg = "";

        public void setup() {
            // create Projects
            for(int i=0; i < nProjects; i++) {
                if(i % 2 == 0) {
                    projects.add(new SmallProject(Integer.toString(i)));
                } else {
                    projects.add(new LargeProject(Integer.toString(i)));
                }
            }

            // create managers
            for(int i=0; i < nManagers; i++) {
                String iStr = Integer.toString(i);
                Employee manager = new Employee("Manager", iStr);
                for(int j=0; j < nManagedEmployees; j++) {
                    manager.addManagedEmployee(new Employee(Integer.toString(j), iStr));
                }
                for(int j=0; j < nChildren; j++) {
                    manager.getChildren().add(new Child(Integer.toString(j), iStr));
                }
                String areaCode = iStr + iStr + iStr;
                for(int j=0; j < nPhones; j++) {
                    String jStr = Integer.toString(j);
                    String number = jStr + jStr + jStr + jStr + jStr + jStr + jStr; 
                    manager.addPhoneNumber(new PhoneNumber(jStr, areaCode, number));
                }
                for(int j=0; j < nResponsibilities; j++) {
                    manager.addResponsibility(Integer.toString(j));
                }
                // let's the same project have different order indexes for different managers
                for(int j=i; j < nProjects+i; j++) {
                    int k = j % nProjects;
                    manager.addProject(projects.get(k));
                }
                managers.add(manager);
            }
        }

        public void test() {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            for(int i=0; i < nManagers; i++) {
                managerClones.add((Employee)uow.registerObject(managers.get(i)));
            }
            uow.commit();
        }
        
        public void verify() {
            String textNameExt;
            Object objectToCompare;
            for(int k=0; k<2; k++) {
                if(k == 0) {
                    textNameExt = "(Cache)";
                } else {
                    textNameExt = "(DB)";

                    // Read back the inserted objects, make sure the order is correct.
                    getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                    ReadAllQuery query = new ReadAllQuery();
                    query.setReferenceClass(Employee.class);
                    Expression exp = query.getExpressionBuilder().get("firstName").equal("Manager");
                    query.setSelectionCriteria(exp);
                    query.addOrdering(query.getExpressionBuilder().get("lastName"));
                    
                    managers.clear();
                    managers.addAll((List<Employee>)getSession().executeQuery(query));
                    System.out.println();
                }
                for(int i=0; i<nManagers; i++) {
                    if(!getAbstractSession().compareObjects(managerClones.get(i), managers.get(i))) {
                        String localErrorMsg = textNameExt + ": " + "objects not equal\n";
                        errorMsg += localErrorMsg;
                    }
                }
            }
            if(errorMsg.length() > 0) {
                throw new TestErrorException(errorMsg);
            }
        }
        
        public void reset() {
            getSession().executeNonSelectingSQL("DELETE FROM OL_PROJ_EMP");
            getSession().executeNonSelectingSQL("DELETE FROM OL_CHILD");
            getSession().executeNonSelectingSQL("DELETE FROM OL_PHONE");
            getSession().executeNonSelectingSQL("DELETE FROM OL_RESPONS");
            getSession().executeNonSelectingSQL("UPDATE OL_EMPLOYEE SET MANAGER_ID = NULL");
            getSession().executeNonSelectingSQL("DELETE FROM OL_EMPLOYEE");
            getSession().executeNonSelectingSQL("DELETE FROM OL_LPROJECT");
            getSession().executeNonSelectingSQL("DELETE FROM OL_PROJECT");
            
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            
            managers.clear();
            managerClones.clear();
            projects.clear();

            errorMsg = "";
        }
    }
/*    static class IndexTest extends TestCase {
        public void test() {
            ReadObjectQuery query = new ReadObjectQuery();
            ExpressionBuilder builder = query.getExpressionBuilder();
            query.setReferenceClass(Employee.class);
            Expression phoneNumbers = builder.get("phoneNumbers");
            Expression exp = phoneNumbers.getField("ORDER_PHONE").between(0, 1).and(phoneNumbers.type)
            Expression exp = builder.getField("phoneNumbers").get
        }
    }*/
}
