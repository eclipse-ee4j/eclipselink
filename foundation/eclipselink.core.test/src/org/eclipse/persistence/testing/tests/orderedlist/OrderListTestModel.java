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

import org.eclipse.persistence.annotations.OrderCorrectionType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangeTrackingPolicy;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.queries.OrderedListContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.models.orderedlist.*;
import org.eclipse.persistence.testing.models.orderedlist.EmployeeSystem.ChangeTracking;
import org.eclipse.persistence.testing.models.orderedlist.EmployeeSystem.JoinFetchOrBatchRead;

import org.eclipse.persistence.testing.framework.TestProblemException;

public class OrderListTestModel extends TestModel {
    /*
     * Indicates whether to run configuration that don't use listOrderField (useListOrderField==false).
     * By default is set to false.
     * Set it to true for debugging:
     * if something fails with listOrderField (useListOrderField==true) see if it also fails without it. 
     */
    static boolean shouldRunWithoutListOrderField = false;

    /*
     * There is a single top level model (contained in TestRunModel) that contains multiple models.
     */
    boolean isTopLevel;
    
    /*
     * Top level model loops through all possible combinations of the attributes below and decides whether the combination should run.
     * Foe each combination of values that should run a model is created and added to the top level model. 
     */
    boolean useListOrderField;
    boolean isPrivatelyOwned;
    boolean useIndirection; 
    boolean useSecondaryTable;
    boolean useVarcharOrder;
    ChangeTracking changeTracking;
    OrderCorrectionType orderCorrectionType;
    boolean shouldOverrideContainerPolicy;
    JoinFetchOrBatchRead joinFetchOrBatchRead;
    
    /*
     * Variables below used by setup / reset:
     * setup saves there the original state of something, sets a new state required for testing,
     * then reset brings back the saved original state.
     */
    Map<Class, ObjectChangeTrackingPolicy> originalChangeTrackingPolicies;
        
    /*
     * Constants used by WhereToAdd tests.
     */
    static final String front = "front";
    static final String middle = "middle";
    static final String end = "end";

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
    }

    void addTestModel(DatabasePlatform platform) {
        if (shouldAddModel(platform)) {            
            addTest(new OrderListTestModel(useListOrderField, useIndirection, isPrivatelyOwned, useSecondaryTable, useVarcharOrder, changeTracking, orderCorrectionType, shouldOverrideContainerPolicy, joinFetchOrBatchRead));
        }
    }
    
    /*
     * Loops through all possible model configurations and adds those for which shouldAddModel returns true.
     */
    void addModels() {
        DatabasePlatform platform = getSession().getPlatform();
        changeTracking = ChangeTracking.ATTRIBUTE;
        orderCorrectionType = OrderCorrectionType.READ_WRITE;
        joinFetchOrBatchRead = JoinFetchOrBatchRead.NONE;
        useVarcharOrder = false;
        useSecondaryTable = false;
        isPrivatelyOwned = false;
        useIndirection = true;
        useListOrderField = true;
        shouldOverrideContainerPolicy = false;
        useVarcharOrder = true;
        addTestModel(platform);
        useVarcharOrder = false;
        useSecondaryTable = true;
        addTestModel(platform);
        useSecondaryTable = false;
        useIndirection = false;
        changeTracking = ChangeTracking.DEFERRED;
        addTestModel(platform);
        changeTracking = ChangeTracking.ATTRIBUTE;
        useIndirection = true;
        shouldOverrideContainerPolicy = true;
        addTestModel(platform);
        shouldOverrideContainerPolicy = false;
        for (int i=0; i < ChangeTracking.values().length; i++) {
            changeTracking = ChangeTracking.values()[i];
            addTestModel(platform);
        }
        isPrivatelyOwned = true;
        for (int i=0; i < ChangeTracking.values().length; i++) {
            changeTracking = ChangeTracking.values()[i];
            addTestModel(platform);
        }
        changeTracking = ChangeTracking.ATTRIBUTE;
        isPrivatelyOwned = false;
        for (int j=0; j < OrderCorrectionType.values().length; j++) {
            orderCorrectionType = OrderCorrectionType.values()[j];
            addTestModel(platform);
        }
        orderCorrectionType = OrderCorrectionType.READ_WRITE;
        for (int k=0; k < JoinFetchOrBatchRead.values().length; k++) {
            joinFetchOrBatchRead = JoinFetchOrBatchRead.values()[k];
            addTestModel(platform);
        }
        joinFetchOrBatchRead = JoinFetchOrBatchRead.NONE;
        useSecondaryTable = true;
        for (int k=0; k < JoinFetchOrBatchRead.values().length; k++) {
            joinFetchOrBatchRead = JoinFetchOrBatchRead.values()[k];
            addTestModel(platform);
        }
        joinFetchOrBatchRead = JoinFetchOrBatchRead.NONE;
        useSecondaryTable = false;
        /** Un-comment to run 2^6 * 3 * 6 * 15 tests... (a lot...)
        do {
            do {
                do {
                    do {
                        do {
                            for(int i=0; i < ChangeTracking.values().length; i++) {
                                changeTracking = ChangeTracking.values()[i];
                                for(int j=0; j < OrderCorrectionType.values().length; j++) {
                                    orderCorrectionType = OrderCorrectionType.values()[j];
                                    do{ 
                                        for(int k=0; k < JoinFetchOrBatchRead.values().length; k++) {
                                            joinFetchOrBatchRead = JoinFetchOrBatchRead.values()[k];
                                            addTestModel(platform);
                                        }
                                        shouldOverrideContainerPolicy = !shouldOverrideContainerPolicy;
                                    } while(shouldOverrideContainerPolicy);
                                }
                            }
                            useVarcharOrder = !useVarcharOrder;
                        } while(useVarcharOrder);
                        useSecondaryTable = !useSecondaryTable;
                    } while(useSecondaryTable);
                    isPrivatelyOwned = !isPrivatelyOwned;
                } while(isPrivatelyOwned);
                useIndirection = !useIndirection;
            } while(useIndirection);
            useListOrderField = !useListOrderField;
        } while(useListOrderField);*/
    }

    /*
     * Verifies whether the current model configuration should be added.
     * Cuts the models with invalid configurations, configurations that don't make any difference.
     */
    boolean shouldAddModel(DatabasePlatform platform) {
        // listOrderField is not used 
        if(!useListOrderField) {
            // explicitly asked not to run the model that don't use listOrderField.
            if(!shouldRunWithoutListOrderField) {
                return false;
            }
            // the model would be identical to OrderCorrectionType.READ
            if(orderCorrectionType != OrderCorrectionType.READ) {
                return false;
            }
            // the model would be identical to useVarcharOrder==false
            if(useVarcharOrder) {
                return false;
            }
        }

        // H2 has an issue with large outer joins, causes null-pointer in driver.
        //
        // There is an Eclipselink bug: when using old Oracle-style (+) outer joins the inner joins between
        // primary and secondary tables substituted by outer joins (employee outer join manager causes manager's salary to outer join to manager).
        // If there happens to be another outer join to the secondary table (in useSecondaryTable case manager_id is in salary table)
        // then suddenly the secondary table auto joined to two tables - that causes exception: 
        // ORA-01417: a table may be outer joined to at most one other table.
        // Now by default Oracle joins in FROM clause, TimesTen can't, therefore TimesTen can't run this model.
        //
        if (useSecondaryTable && (joinFetchOrBatchRead == JoinFetchOrBatchRead.OUTER_JOIN)) {
            if (platform.isH2() || platform.isHSQL() || platform.isTimesTen()) {
                return false;
            }
        }
        if (useVarcharOrder && !platform.supportsAutoConversionToNumericForArithmeticOperations()) {
            return false;
        }
        
        return true;
    }
    
    public OrderListTestModel(boolean useListOrderField, boolean useIndirection, boolean isPrivatelyOwned, boolean useSecondaryTable, boolean useVarcharOrder, ChangeTracking changeTracking, OrderCorrectionType orderCorrectionType, boolean shouldOverrideContainerPolicy, JoinFetchOrBatchRead joinFetchOrBatchRead) {
        this.useListOrderField = useListOrderField;
        this.useIndirection = useIndirection;
        this.isPrivatelyOwned = isPrivatelyOwned;
        this.useSecondaryTable = useSecondaryTable;
        this.useVarcharOrder = useVarcharOrder;
        this.changeTracking = changeTracking;
        this.orderCorrectionType = orderCorrectionType;
        this.shouldOverrideContainerPolicy = shouldOverrideContainerPolicy;
        this.joinFetchOrBatchRead = joinFetchOrBatchRead;
        
        setDescription("This model tests ordered list");
        
        setName("");
        addToName(useListOrderField ? "" : "NO_ORDER_LIST");
        addToName(useIndirection ? "" : "NO_INDIRECTION");
        addToName(isPrivatelyOwned ? "PRIVATE" : "");
        addToName(useSecondaryTable ? "SECONDARY_TABLE" : "");
        addToName(useVarcharOrder ? "VARCHAR_ORDER" : "");
        addToName(changeTracking.toString());
        addToName(orderCorrectionType == OrderCorrectionType.READ ? "" : orderCorrectionType.toString());
        addToName(shouldOverrideContainerPolicy ? "OVERRIDE_CONTAINER_POLICY" : "");
        addToName(joinFetchOrBatchRead.toString());
    }

    void addToName(String strToAdd) {
        if(strToAdd.length() > 0) {
            setName(getName() + " " + strToAdd);
        }
    }
    
    public void addRequiredSystems() {
        if(!isTopLevel) {
            addRequiredSystem(new EmployeeSystem(useListOrderField, useIndirection, isPrivatelyOwned, useSecondaryTable, useVarcharOrder, changeTracking, orderCorrectionType, shouldOverrideContainerPolicy, joinFetchOrBatchRead));
        }
    }

    public void addTests() {
        if(!isTopLevel) {
            addTest(new CreateTest());
            addTest(new SimpleAddRemoveTest());
            // takes too long with joins
            if(joinFetchOrBatchRead != JoinFetchOrBatchRead.INNER_JOIN && joinFetchOrBatchRead != JoinFetchOrBatchRead.OUTER_JOIN) {
                addTest(new SimpleAddRemoveTest2());
            }
            // can't run with inner joins because the test sets useResponsibilities and usePhones to false and these lists are empty.
            if(joinFetchOrBatchRead != JoinFetchOrBatchRead.INNER_JOIN) {
                addTest(new AddRemoveUpdateTest());
            }
            addTest(new VerifyForeignKeyOfRemovedObject(false));
            addTest(new VerifyForeignKeyOfRemovedObject(true));
            addTest(new SimpleSetTest());
            addTest(new SimpleSetListTest());
            addTest(new SimpleSetListTest(false));
            addTest(new SimpleSetListTest(true));
            addTest(new TranspositionTest(new int[]{0, 1}, new int[]{1, 0}, false));
            addTest(new TranspositionTest(new int[]{0, 1}, new int[]{1, 0}, true));
            addTest(new TranspositionMergeTest(new int[]{0, 1}, new int[]{1, 0}));
            addTest(new TranspositionTest(new int[]{1, 3, 5}, new int[]{5, 1, 3}, false));
            addTest(new TranspositionTest(new int[]{1, 3, 5}, new int[]{5, 1, 3}, true));
            addTest(new TranspositionMergeTest(new int[]{1, 3, 5}, new int[]{5, 1, 3}));
            // currently only DirectCollectionMapping supports nulls. 
            // bug 278126: Aggregate and Direct collections containing nulls read incorrectly if join is used.
            // When the bug is fixed the tests should work and condition should be removed.
            // The bug is partially fixed - the only case result is wrong for DirectCollectionMapping
            // is a collection that has a single element - null. That collection is read in as empty.
            addTest(new AddNullTest(front));
            addTest(new AddNullTest(middle));
            addTest(new AddNullTest(end));
            // currently only DirectCollectionMapping supports duplicates.
            // Duplication doesn't work with joining because of SELECT DISTINCT (would work without DISTINCT).
            // Should DISTINCT be there?
            if(joinFetchOrBatchRead != JoinFetchOrBatchRead.INNER_JOIN && joinFetchOrBatchRead != JoinFetchOrBatchRead.OUTER_JOIN) {
                addTest(new AddDuplicateTest(front));
                addTest(new AddDuplicateTest(middle));
                addTest(new AddDuplicateTest(end));
            }
            if(joinFetchOrBatchRead == JoinFetchOrBatchRead.OUTER_JOIN) {
                addTest(new CreateEmptyTest());
                addTest(new CreateEmptyManagersTest());
            }
            if(this.useListOrderField) {
                addTest(new SimpleIndexTest(true));
                if(this.shouldOverrideContainerPolicy) {
                    addTest(new VerifyContainerPolicyClassTest());
                }
                if(orderCorrectionType == OrderCorrectionType.EXCEPTION) {
                    if(joinFetchOrBatchRead != JoinFetchOrBatchRead.INNER_JOIN) {
                        addTest(new BreakOrderExceptionTest_OneToMany());
                    }
                    addTest(new BreakOrderExceptionTest_UnidirectionalOneToMany());
                    addTest(new BreakOrderExceptionTest_ManyToMany());
                    addTest(new BreakOrderExceptionTest_DirectCollection());
                    if(this.changeTracking == ChangeTracking.DEFERRED) {
                        addTest(new BreakOrderExceptionTest_AggregateCollection());
                    }
                } else if(orderCorrectionType == OrderCorrectionType.READ_WRITE) {
                    addTest(new BreakOrderCorrectionAndRemoveTest(false));
                    addTest(new BreakOrderCorrectionAndRemoveTest(true));
                    addTest(new BreakOrderCorrectionTest(false));
                    addTest(new BreakOrderCorrectionTest(true));
                }
            }
            
            addTest(new CreateManagersTest());
        } else {
            addModels();
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
        }
    }

    /*
     * Defines operations on Lists that consist of (in that order):
     *   Employees (for empManager.managerEmployees);
     *   Children (for empManager.children);
     *   Projects (for empManager.projects);
     *   Strings (for empManager.responsibilitiesList);
     *   PhoneNumbers (for empManager.phoneNumbers).
     * That allows to easily create tests that test all mappings using list order field:
     *   OneToMany, UnidirectionalOneToMany, ManyToMany, DirectCollection, AggregateCollection.
     *   
     * Note that (for debugging purposes) some of these mappings could be "switched off" by setting the corresponding "use..." flag to false.
     * Don't do that in INNER_JOIN case - or no objects will be ever read back from db.
     */
    class BaseTest extends TestCase {
        boolean useManagedEmployees;
        boolean useChildren;
        boolean useProjects;
        boolean useResponsibilities;
        boolean usePhones;
        
        String errorMsg;
        
        BaseTest() {
            this.useManagedEmployees = true;
            this.useChildren = true;
            this.useProjects = true;
            this.useResponsibilities = true;
            this.usePhones = true;
            setValidFlags();
            
            errorMsg = "";
            
            setName(getShortClassName());
        }

        /*
         * Sets some of useManagedEmployees, useChildren, useProjects, useResponsibilities, usePhones to false
         * as required by the flags copied from OrderListTestModel.this.
         * If changing this method change validateFlags method accordingly. 
         */
        protected void setValidFlags() {
            // currently attribute change tracking is incompatible with AggregateCollectionMapping
            if(OrderListTestModel.this.changeTracking == ChangeTracking.ATTRIBUTE) {
                usePhones = false;
            }
            // managedEmployees have nothing: no managedEmployees, no children, no projects, no responsibilities, no phones -
            // can't read them using INNER_JOIN
            if(OrderListTestModel.this.joinFetchOrBatchRead == JoinFetchOrBatchRead.INNER_JOIN) {
                useManagedEmployees = false;
            }
        }
        /*
         * Validate useManagedEmployees, useChildren, useProjects, useResponsibilities, usePhones flags
         * so their value don't contradict the flags copied from OrderListTestModel.this.
         * If changing this method change setValidFlags method accordingly. 
         */
        protected void validateFlags() {
            // currently attribute change tracking is incompatible with AggregateCollectionMapping
            if(OrderListTestModel.this.changeTracking == ChangeTracking.ATTRIBUTE) {
                if(usePhones) {
                    errorMsg += "ChangeTracking.ATTRIBUTE requires usePhones==false; ";
                }
            }
            // managedEmployees have nothing: no managedEmployees, no children, no projects, no responsibilities, no phones -
            // can't read them using INNER_JOIN
            if(OrderListTestModel.this.joinFetchOrBatchRead == JoinFetchOrBatchRead.INNER_JOIN) {
                if(useManagedEmployees) {
                    errorMsg += "JoinFetchOrBatchRead.INNER_JOIN requires useManagedEmployees==false; ";
                }
            }
            if(errorMsg.length() > 0) {
                throw new TestProblemException(errorMsg);
            }
        }
        
        /*
         * Debugging: putting a breakpoint at the first line of this method is a good place to set some of 
         * useManagedEmployees, useChildren, useProjects, useResponsibilities, usePhones to false if desired.
         */
        public void setup() {
            if(!useManagedEmployees && !useChildren && !useProjects && !useResponsibilities && !usePhones) {
                throw new TestProblemException("useManagedEmployees, useChildren, useProjects, useResponsibilities, usePhones are all false - nothing to test");
            }
            validateFlags();
        }
        
        public void reset() {
            if(useManagedEmployees) {
                if(useSecondaryTable) {
                    getSession().executeNonSelectingSQL("UPDATE OL_SALARY SET MANAGER_ID = NULL");
                } else {
                    getSession().executeNonSelectingSQL("UPDATE OL_EMPLOYEE SET MANAGER_ID = NULL");
                }
            }
            if(useChildren) {
                getSession().executeNonSelectingSQL("DELETE FROM OL_ALLOWANCE");
                getSession().executeNonSelectingSQL("DELETE FROM OL_CHILD");
            }
            if(useProjects) {
                getSession().executeNonSelectingSQL("DELETE FROM OL_PROJ_EMP");
                getSession().executeNonSelectingSQL("DELETE FROM OL_LPROJECT");
                getSession().executeNonSelectingSQL("DELETE FROM OL_PROJECT");
            }
            if(useResponsibilities) {
                getSession().executeNonSelectingSQL("DELETE FROM OL_RESPONS");
            }
            if(usePhones) {
                getSession().executeNonSelectingSQL("DELETE FROM OL_PHONE");
            }

            getSession().executeNonSelectingSQL("DELETE FROM OL_SALARY");
            getSession().executeNonSelectingSQL("DELETE FROM OL_EMPLOYEE");
            
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            
            errorMsg = "";
        }

        /*
         * Creates a list of objects that could be added to manager: Employee, Child, Project, Responsibility (String), PhoneNumber.
         * Note that indexForName here is used as part of a state of the object being created - not as its position in any list.
         */
        List create(String prefix, int indexForName) {
            List list = new ArrayList();
            String iString = Integer.toString(indexForName);
            String str = prefix + iString;
            if(useManagedEmployees) {
                list.add(new Employee(iString, prefix));
            }
            if(useChildren) {
                list.add(new Child(iString, prefix));
            }
            if(useProjects) {
                if(indexForName % 2 == 0) {
                    list.add(new SmallProject(str));
                } else {
                    list.add(new LargeProject(str));
                }
            }
            if(useResponsibilities) {
                list.add(str);
            }
            if(usePhones) {
                if(prefix.length() > 3) {
                    prefix = prefix.substring(0, 3);
                }
                list.add(new PhoneNumber(prefix, iString));                
            }
            return list;
        }
        
        /*
         * Creates a list of nulls that could be added to manager: Employee, Child, Project, Responsibility (String), PhoneNumber.
         */
        List createNull() {
            List list = new ArrayList();
            if(useManagedEmployees) {
                list.add(null);
            }
            if(useChildren) {
                list.add(null);
            }
            if(useProjects) {
                list.add(null);
            }
            if(useResponsibilities) {
                list.add(null);
            }
            if(usePhones) {
                list.add(null);                
            }
            return list;
        }
        
        /*
         * Adds a list of objects to empManager: Employee, Child, Project, Responsibility (String), PhoneNumber.
         * The list should be obtained from getFrom, create, removeFrom or setInto methods.
         */
        void addTo(Employee empManager, List list) {
            int i = 0;
            if(useManagedEmployees) {
                empManager.addManagedEmployee((Employee)list.get(i++));
            }
            if(useChildren) {
                empManager.getChildren().add(list.get(i++));
            }
            if(useProjects) {
                empManager.addProject((Project)list.get(i++));
            }
            if(useResponsibilities) {
                empManager.addResponsibility((String)list.get(i++));
            }
            if(usePhones) {
                empManager.addPhoneNumber((PhoneNumber)list.get(i++));
            }
        }
        
        /*
         * Adds a list of objects to empManager at index: Employee, Child, Project, Responsibility (String), PhoneNumber.
         * The list should be obtained from getFrom, create, removeFrom or setInto methods.
         */
        void addTo(Employee empManager, int index, List list) {
            int i = 0;
            if(useManagedEmployees) {
                empManager.addManagedEmployee(index, (Employee)list.get(i++));
            }
            if(useChildren) {
                empManager.getChildren().add(index, list.get(i++));
            }
            if(useProjects) {
                empManager.addProject(index, (Project)list.get(i++));
            }
            if(useResponsibilities) {
                empManager.addResponsibility(index, (String)list.get(i++));
            }
            if(usePhones) {
                empManager.addPhoneNumber(index, (PhoneNumber)list.get(i++));
            }
        }
        
        /*
         * Adds a list of objects to empManager at index: Employee, Child, Project, Responsibility (String), PhoneNumber.
         * The list should be obtained from getFrom, create, removeFrom or setInto methods.
         */
        void addTo_NoRelMaintanence(Employee empManager, int index, List list) {
            int i = 0;
            if(useManagedEmployees) {
                empManager.getManagedEmployees().add(index, (Employee)list.get(i++));
            }
            if(useChildren) {
                empManager.getChildren().add(index, list.get(i++));
            }
            if(useProjects) {
                empManager.getProjects().add(index, (Project)list.get(i++));
            }
            if(useResponsibilities) {
                empManager.addResponsibility(index, (String)list.get(i++));
            }
            if(usePhones) {
                empManager.addPhoneNumber(index, (PhoneNumber)list.get(i++));
            }
        }
        
        /*
         * Removes a list of objects from empManager at index.
         * Returns list of removed objects: Employee, Child, Project, Responsibility (String), PhoneNumber.
         */
        List removeFrom(Employee empManager, int index) {
            List list = new ArrayList();
            if(useManagedEmployees) {
                list.add(empManager.removeManagedEmployee(index));
            }
            if(useChildren) {
                list.add(empManager.getChildren().remove(index));
            }
            if(useProjects) {
                list.add(empManager.removeProject(index));
            }
            if(useResponsibilities) {
                list.add(empManager.removeResponsibility(index));
            }
            if(usePhones) {
                list.add(empManager.removePhoneNumber(index));
            }
            return list;
        }
        
        /*
         * Removes a list of objects from empManager at index.
         * Returns list of removed objects: Employee, Child, Project, Responsibility (String), PhoneNumber.
         */
        List removeFrom_NoRelMaintanence(Employee empManager, int index) {
            List list = new ArrayList();
            if(useManagedEmployees) {
                list.add(empManager.getManagedEmployees().remove(index));
            }
            if(useChildren) {
                list.add(empManager.getChildren().remove(index));
            }
            if(useProjects) {
                list.add(empManager.getProjects().remove(index));
            }
            if(useResponsibilities) {
                list.add(empManager.removeResponsibility(index));
            }
            if(usePhones) {
                list.add(empManager.removePhoneNumber(index));
            }
            return list;
        }
        
        /*
         * Gets a list of objects from empManager at index: Employee, Child, Project, Responsibility (String), PhoneNumber.
         */
        List getFrom(Employee empManager, int index) {
            List list = new ArrayList();
            if(useManagedEmployees) {
                list.add(empManager.getManagedEmployees().get(index));
            }
            if(useChildren) {
                list.add(empManager.getChildren().get(index));
            }
            if(useProjects) {
                list.add(empManager.getProjects().get(index));
            }
            if(useResponsibilities) {
                list.add(empManager.getResponsibilitiesList().get(index));
            }
            if(usePhones) {
                list.add(empManager.getPhoneNumbers().get(index));
            }
            return list;
        }
        
        /*
         * Sets a list of objects into empManager at index.
         * Returns list of removed objects: Employee, Child, Project, Responsibility (String), PhoneNumber.
         */
        List setInto(Employee empManager, int index, List list) {
            int i = 0;
            List listOut = new ArrayList();
            if(useManagedEmployees) {
                listOut.add(empManager.setManagedEmployee(index, (Employee)list.get(i++)));
            }
            if(useChildren) {
                listOut.add(empManager.getChildren().set(index, list.get(i++)));
            }
            if(useProjects) {
                listOut.add(empManager.setProject(index, (Project)list.get(i++)));
            }
            if(useResponsibilities) {
                listOut.add(empManager.setResponsibility(index, (String)list.get(i++)));
            }
            if(usePhones) {
                listOut.add(empManager.setPhoneNumber(index, (PhoneNumber)list.get(i++)));
            }
            return listOut;
        }
        
        /*
         * Sets a list of objects into empManager at index.
         * Returns list of removed objects: Employee, Child, Project, Responsibility (String), PhoneNumber.
         */
        List setInto_NoRelMaintanence(Employee empManager, int index, List list) {
            int i = 0;
            List listOut = new ArrayList();
            if(useManagedEmployees) {
                listOut.add(empManager.getManagedEmployees().set(index, (Employee)list.get(i++)));
            }
            if(useChildren) {
                listOut.add(empManager.getChildren().set(index, list.get(i++)));
            }
            if(useProjects) {
                listOut.add(empManager.getProjects().set(index, (Project)list.get(i++)));
            }
            if(useResponsibilities) {
                listOut.add(empManager.setResponsibility(index, (String)list.get(i++)));
            }
            if(usePhones) {
                listOut.add(empManager.setPhoneNumber(index, (PhoneNumber)list.get(i++)));
            }
            return listOut;
        }
        
        /*
         * Sets lists of objects into empManager using setManagedEmployees, setChildren, setProjects, setResponsibilityList, setPhoneNumbers methods.
         * The list should be created with createList method.
         */
        void setListInto(Employee empManager, List<List> listOfLists) {
            int i = 0;
            if(useManagedEmployees) {
                List<Employee> newList = listOfLists.get(i++);
                List<Employee> oldList = empManager.getManagedEmployees();
                empManager.setManagedEmployees(newList);
                if(oldList != null) {
                    for(int j=0; j < oldList.size(); j++) {
                        oldList.get(j).setManager(null);
                    }
                }
                if(newList != null) {
                    for(int j=0; j < newList.size(); j++) {
                        newList.get(j).setManager(empManager);
                    }
                }
            }
            if(useChildren) {
                empManager.setChildren((Vector)listOfLists.get(i++));
            }
            if(useProjects) {
                List<Project> newList = listOfLists.get(i++);
                List<Project> oldList = empManager.getProjects();
                empManager.setProjects(newList);
                if(oldList != null) {
                    for(int j=0; j < oldList.size(); j++) {
                        oldList.get(j).getEmployees().remove(empManager);
                    }
                }
                if(newList != null) {
                    for(int j=0; j < newList.size(); j++) {
                        newList.get(j).getEmployees().add(empManager);
                    }
                }
            }
            if(useResponsibilities) {
                empManager.setResponsibilitiesList(listOfLists.get(i++));
            }
            if(usePhones) {
                empManager.setPhoneNumbers(listOfLists.get(i++));
            }
        }
        
        /*
         * Updates a list of objects that could be added to manager: Employee, Child, Project, Responsibility (String), PhoneNumber.
         * Note that indexForName here is used as part of a state of the object being updated - not as its position in any list.
         */
        void update( List list, String prefix, int indexForName) {
            String iString = Integer.toString(indexForName);
            int i = 0;
            if(useManagedEmployees) {
                Employee emp = (Employee)list.get(i++);
                emp.setFirstName(iString);
                emp.setLastName(prefix);
            }
            if(useChildren) {
                Child child = (Child)list.get(i++);
                child.setFirstName(iString);
                child.setLastName(prefix);
            }
            if(useProjects) {
                Project project = (Project)list.get(i++);
                project.setName(iString);
            }
            if(useResponsibilities) {
                throw new TestProblemException("Can't update a String. Set useResponsibilities to false");
            }
            if(usePhones) {
                if(prefix.length() > 3) {
                    prefix = prefix.substring(0, 3);
                }
                PhoneNumber phone = (PhoneNumber)list.get(i++);
                phone.setAreaCode(prefix);
                phone.setNumber(iString);
            }
        }
        
        /*
         * Registers in uow a list of mapped non-aggregated objects that could be added to manager: Employee, Child, Project.
         * Returns a list that contains clones that could be updated.
         */
        List register(List list, UnitOfWork uow) {
            ArrayList listClone = new ArrayList(list.size());
            int i = 0;
            if(useManagedEmployees) {
                listClone.add(uow.registerObject(list.get(i++)));
            }
            if(useChildren) {
                listClone.add(uow.registerObject(list.get(i++)));
            }
            if(useProjects) {
                listClone.add(uow.registerObject(list.get(i++)));
            }
            if(useResponsibilities) {
                throw new TestProblemException("Can't register a String in uow. Set useResponsibilities to false");
            }
            if(usePhones) {
                throw new TestProblemException("Can't register aggregate in uow. Set usePhones to false");
            }
            return listClone;
        }
        
        /*
         * Creates a list of Lists  that could be set to manager: List<Employee>, Vector, List<Project>, List<String>, List<PhoneNumber>.
         */
        List<List> createList() {
            List<List> listOfLists = new ArrayList();
            if(useManagedEmployees) {
                listOfLists.add(new ArrayList<Employee>());
            }
            if(useChildren) {
                listOfLists.add(new Vector());
            }
            if(useProjects) {
                listOfLists.add(new ArrayList<Project>());
            }
            if(useResponsibilities) {
                listOfLists.add(new ArrayList<String>());
            }
            if(usePhones) {
                listOfLists.add(new ArrayList<PhoneNumber>());
            }
            return listOfLists;
        }
        
        /*
         * Adds a list of objects to listOfLists: Employee, Child, Project, Responsibility (String), PhoneNumber.
         * listOfLists should be created with createList method.
         * list should be obtained from getFrom, create, removeFrom or setInto methods.
         */
        void addTo(List<List> listOfLists, List list) {
            int i = 0;
            if(useManagedEmployees) {
                listOfLists.get(i).add(list.get(i++));
            }
            if(useChildren) {
                listOfLists.get(i).add(list.get(i++));
            }
            if(useProjects) {
                listOfLists.get(i).add(list.get(i++));
            }
            if(useResponsibilities) {
                listOfLists.get(i).add(list.get(i++));
            }
            if(usePhones) {
                listOfLists.get(i).add(list.get(i++));
            }
        }        

        /*
         * Breaks order in the db by assigning wrong values to order fields 
         */
        String getManagegedEmployeesOrderTable() {
            return useSecondaryTable ? "OL_SALARY" : "OL_EMPLOYEE";
        }
        String getManagegedEmployeesOrderField() {
            return useVarcharOrder ? "MANAGED_ORDER_VARCHAR" : "MANAGED_ORDER";
        }
        void breakManagedEmployeesOrder() {
            executeBreak(getManagegedEmployeesOrderTable(), getManagegedEmployeesOrderField(), "1", "NULL");
        }
        String getChildrenOrderTable() {
            return useSecondaryTable ? "OL_ALLOWANCE" : "OL_CHILD";
        }
        String getChildrenOrderField() {
            return useVarcharOrder ? "CHILDREN_ORDER_VARCHAR" : "CHILDREN_ORDER";
        }
        void breakChildrenOrder() {
            executeBreak(getChildrenOrderTable(), getChildrenOrderField(), "0", "NULL");
        }
        void breakProjectsOrder() {
            String tableName = "OL_PROJ_EMP";
            String fieldName;
            if(useVarcharOrder) {
                fieldName = "PROJ_ORDER_VARCHAR";
            } else {
                fieldName = "PROJ_ORDER";
            }
            executeBreak(tableName, fieldName, "0", "5");
        }
        void breakResponsibilitiesOrder() {
            String tableName = "OL_RESPONS";
            String fieldName;
            if(useVarcharOrder) {
                fieldName = "RESPONS_ORDER_VARCHAR";
            } else {
                fieldName = "RESPONS_ORDER";
            }
            executeBreak(tableName, fieldName, "1", "0");
        }
        void breakPhonesOrder() {
            String tableName = "OL_PHONE";
            String fieldName;
            if(useVarcharOrder) {
                fieldName = "PHONE_ORDER_VARCHAR";
            } else {
                fieldName = "PHONE_ORDER";
            }
            executeBreak(tableName, fieldName, "0", "1");
        }
        void executeBreak(String tableName, String fieldName, String oldValue, String newValue) {
            getSession().executeNonSelectingSQL("UPDATE "+tableName+" SET "+fieldName+" = "+newValue+" WHERE "+fieldName+" = " + oldValue);
        }
        void breakOrder() {
            if(useManagedEmployees) {
                breakManagedEmployeesOrder();
            }
            if(useChildren) {
                breakChildrenOrder();
            }
            if(useProjects) {
                breakProjectsOrder();
            }
            if(useResponsibilities) {
                breakResponsibilitiesOrder();
            }
            if(usePhones) {
                breakPhonesOrder();
            }
        }
        
        /*
         * Set the new OrderCorrectionType, return the old one.
         * Verify that the old modes are the same for all mappings. 
         */
        OrderCorrectionType changeOrderCorrectionType(OrderCorrectionType mode) {
            OrderCorrectionType oldMode = null;
            if(useManagedEmployees) {
                oldMode = changeOrderCorrectionType("managedEmployees", mode, oldMode);
            }
            if(useChildren) {
                oldMode = changeOrderCorrectionType("children", mode, oldMode);
            }
            if(useProjects) {
                oldMode = changeOrderCorrectionType("projects", mode, oldMode);
            }
            if(useResponsibilities) {
                oldMode = changeOrderCorrectionType("responsibilitiesList", mode, oldMode);
            }
            if(usePhones) {
                oldMode = changeOrderCorrectionType("phoneNumbers", mode, oldMode);
            }
            return oldMode;
        }
        OrderCorrectionType changeOrderCorrectionType(String attribute, OrderCorrectionType mode, OrderCorrectionType oldMode) {
            ClassDescriptor desc = getSession().getDescriptor(Employee.class);
            CollectionMapping mapping = (CollectionMapping)desc.getMappingForAttributeName(attribute);
            OrderCorrectionType currOldMode = changeOrderCorrectionType(mapping, mode);
            if(oldMode != null) {
                if(oldMode != currOldMode) {
                    throw new TestProblemException("OrderCorrectionTypes for " + attribute+ " is " + currOldMode +"; for previous mapping(s) it was " + oldMode);
                }
            }
            return currOldMode;
        }
        OrderCorrectionType changeOrderCorrectionType(CollectionMapping mapping, OrderCorrectionType mode) {
            OrderedListContainerPolicy policy = (OrderedListContainerPolicy)mapping.getContainerPolicy(); 
            OrderCorrectionType oldMode = policy.getOrderCorrectionType();
            policy.setOrderCorrectionType(mode);
            
            OrderedListContainerPolicy queryPolicy = null;
            if(mapping.getSelectionQuery().isReadAllQuery()) {
                queryPolicy = (OrderedListContainerPolicy)((ReadAllQuery)mapping.getSelectionQuery()).getContainerPolicy();
            } else if(mapping.getSelectionQuery().isDataReadQuery()) {
                queryPolicy = (OrderedListContainerPolicy)((DataReadQuery)mapping.getSelectionQuery()).getContainerPolicy();
            }
            if(policy != queryPolicy) {
                OrderCorrectionType oldModeQuery = queryPolicy.getOrderCorrectionType();
                if(oldMode != oldModeQuery) {
                    throw new TestErrorException(mapping.getAttributeName() + ": OrderCorrectionTypes in container policy is " + oldMode +"; is query is " + oldModeQuery);
                }
                queryPolicy.setOrderCorrectionType(mode);
            }
            return oldMode;
        }
        
        /*
         * Verify IndirectList.isListOrderBrokenInDb flag value.
         * Throw exception if it's not expected one.
         */
        String verifyIsListOrderBrokenInDb(Employee empManager, boolean expected) {
            String localErrorMsg = "";
            if(useManagedEmployees) {
                localErrorMsg += verifyIsListOrderBrokenInDb(empManager, expected, "managedEmployees");
            }
            if(useChildren) {
                localErrorMsg += verifyIsListOrderBrokenInDb(empManager, expected, "children");
            }
            if(useProjects) {
                localErrorMsg += verifyIsListOrderBrokenInDb(empManager, expected, "projects");
            }
            if(useResponsibilities) {
                localErrorMsg += verifyIsListOrderBrokenInDb(empManager, expected, "responsibilitiesList");
            }
            if(usePhones) {
                localErrorMsg += verifyIsListOrderBrokenInDb(empManager, expected, "phoneNumbers");
            }
            if(localErrorMsg.length() > 0) {
                localErrorMsg = "isListOrderBrokenInDb expected to be " + expected + ". For the following attributes it is " + !expected +": " + localErrorMsg;
            }
            return localErrorMsg;
        }
        String verifyIsListOrderBrokenInDb(Employee empManager, boolean expected, String attribute) {
            String localErrorMsg = "";
            ClassDescriptor desc = getSession().getDescriptor(Employee.class);
            CollectionMapping mapping = (CollectionMapping)desc.getMappingForAttributeName(attribute);
            Object attributeValue = mapping.getRealAttributeValueFromObject(empManager, getAbstractSession()); 
            if(((IndirectList)attributeValue).isListOrderBrokenInDb() != expected) {
                localErrorMsg = attribute + "; ";
            }
            return localErrorMsg;
        }
        
        /*
         * Indicates whether all lists were read in
         */
        boolean isInstantiated(Employee empManager) {
            List list;
            String instantiatedStr = "";
            String notInstantiatedStr = "";
            String str;
            boolean isInstantiated;
            if(useManagedEmployees) {
                list = empManager.getManagedEmployees();
                isInstantiated = isInstantiated(list);
                str = (isInstantiated ? instantiatedStr : notInstantiatedStr);
                str += "managedEmployees; "; 
            }
            if(useChildren) {
                list = empManager.getChildren();
                isInstantiated = isInstantiated(list);
                str = (isInstantiated ? instantiatedStr : notInstantiatedStr);
                str += "children; "; 
            }
            if(useProjects) {
                list = empManager.getProjects();
                isInstantiated = isInstantiated(list);
                str = (isInstantiated ? instantiatedStr : notInstantiatedStr);
                str += "projects; "; 
            }
            if(useResponsibilities) {
                list = empManager.getResponsibilitiesList();
                isInstantiated = isInstantiated(list);
                str = (isInstantiated ? instantiatedStr : notInstantiatedStr);
                str += "responsibilities; "; 
            }
            if(usePhones) {
                list = empManager.getPhoneNumbers();
                isInstantiated = isInstantiated(list);
                str = (isInstantiated ? instantiatedStr : notInstantiatedStr);
                str += "phoneNumbers; "; 
            }
            if(instantiatedStr.length() > 0 && notInstantiatedStr.length() > 0) {
                throw new TestProblemException("Some attributes are instantiated: " + instantiatedStr + " and some are not: " + notInstantiatedStr);
            } else {
                return instantiatedStr.length() > 0;
            }
        }
        boolean isInstantiated(List list) {
            return !(list instanceof IndirectList && !((IndirectList)list).isInstantiated());
        }
        
        String getShortClassName() {
            String name = this.getClass().getName();
            int begin = name.indexOf('$') + 1;
            int end = name.length();
            return name.substring(begin, end);
        }
    }    
                
    /*
     * Base class for tests using Employee manager object.
     * createManager method creates a manager with nSize of managedEmployees, children, projects, responsibilities, phones.
     */
    class BaseManagerTest extends BaseTest {
        /*
         * Number of manager's managedEmployees, children, projects, responsibilities, phones
         * created by createManager method.
         */
        int nSize;
        
        Employee manager;
        /*
         * manager's clone in uow.
         */
        Employee managerClone;
        
        BaseManagerTest() {
            super();
            this.nSize = 2;            
        }
        
        /*
         * Creates manager with nSize members 
         */
        void createManager() {
            manager = new Employee("Manager");
            for(int i=0; i < nSize; i++) {
                this.addTo(manager, create("old", i));
            }
            UnitOfWork uow = getSession().acquireUnitOfWork();
            managerClone = (Employee)uow.registerObject(manager);
            uow.commit();
        }        
        
        protected void verify() {
            if(manager == null) {
                throw new TestErrorException("manager == null. Nothing to verify");
            }
            
            String textNameExt;
            for(int k=0; k<2; k++) {
                if(k == 0) {
                    textNameExt = "Cache";
                } else {
                    textNameExt = "DB";

                    // Read back the inserted objects, make sure the order is correct.
                    getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                    manager = (Employee)getSession().readObject(manager);
                }
                if(!getAbstractSession().compareObjects(managerClone, manager)) {
                    errorMsg += textNameExt + ": " + "objects not equal\n";
                }
            }
            if(errorMsg.length() > 0) {
                throw new TestErrorException('\n' + errorMsg);
            }
        }

        // assumes that listToVerify is in cache and contains mapped object only (can't contain, say String).
        protected void verifyList(List listToVerify, List listToCompareTo) {
            if(listToVerify == null) {
                throw new TestErrorException("listToVerify is null. Nothing to verify");
            }
            int size = listToVerify.size();
            String textNameExt;
            for(int k=0; k<2; k++) {
                if(k == 0) {
                    textNameExt = "Cache";
                } else {
                    textNameExt = "DB";

                    // Read back the objects from db
                    getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                    for(int i=0; i < size; i++) {
                        listToVerify.set(i, getSession().readObject(listToVerify.get(i)));
                    }
                }
                for(int i=0; i < size; i++) {
                    Object toVerify = listToVerify.get(i);
                    Object toCompareTo = listToCompareTo.get(i);
                    if(!getAbstractSession().compareObjects(toCompareTo, toVerify)) {
                        errorMsg += textNameExt + ": " + "objects not equal\n";
                    }
                }
            }
        }

        // verifies that listToVerify is not in cache and not in db. listToVerify contains mapped object only (can't contain, say String).
        protected void verifyListRemoved(List listToVerify) {
            if(listToVerify == null) {
                throw new TestErrorException("listToVerify is null. Nothing to verify");
            }
            int size = listToVerify.size();
            String textNameExt;
            for(int k=0; k<2; k++) {
                if(k == 0) {
                    textNameExt = "Cache";

                    for(int i=0; i < size; i++) {
                        ReadObjectQuery query = new ReadObjectQuery();
                        query.setSelectionObject(listToVerify.get(i));
                        query.checkCacheOnly();
                        Object readObject = getSession().executeQuery(query);
                        if(readObject != null) {
                            errorMsg += textNameExt + ": " + readObject + " was not removed\n";
                        }
                    }
                } else {
                    textNameExt = "DB";

                    // Read back the objects from db
                    getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                    for(int i=0; i < size; i++) {
                        Object readObject = getSession().readObject(listToVerify.get(i));
                        if(readObject != null) {
                            errorMsg += textNameExt + ": " + readObject + " was not removed\n";
                        }
                    }
                }
            }
        }

        public void reset() {
            super.reset();
            manager = null;
            managerClone = null;
        }

        String nSizeName() {
            return getShortClassName() + ": "+nSize+":";
        }
    }
    
    /*
     * Create manager, save to the db, verify that it was correctly merged into shared cache and written into the db. 
     */
    class CreateTest extends BaseManagerTest {        
        CreateTest() {
            super();
        }
        public void test() {
            createManager();
        }
    }
    
    /*
     * Create empty manager, save to the db, verify that it was correctly merged into shared cache and written into the db. 
     * For OUTER_JOIN case to verify that read back lists are empty (as opposed to having null members).
     */
    class CreateEmptyTest extends CreateTest {        
        CreateEmptyTest() {
            super();
            nSize = 0;
        }
    }
    
    /*
     * Create manager, save to the db. Meant as a base to the tests that change manager. 
     */
    class ChangeTest extends BaseManagerTest {
        ChangeTest() {
            super();
        }
        public void setup() {
            super.setup();
            createManager();
        }
    }
    
    /*
     * Switch positions of list members. 
     * [0, 2], [7, 5] means that old[0] = new[7], old[2] = new[5];
     * [0, 2, 5], [7, 5, 4] means that old[0] = new[7], old[2] = new[5], old[5] = new[4].
     * useSet indicates whether to use set(index, obj) method or remove(index)/add(index, obj).
     */
    class TranspositionTest extends ChangeTest {
        int[] oldIndexes, newIndexes;
        boolean useSet;
        /*
         * nSize is number of members in each of the lists (managedEmployees, children etc), it should be greater than any old or new index
         */
        TranspositionTest(int nSize, int[] oldIndexes, int[] newIndexes, boolean useSet) {
            super();
            this.nSize = nSize;
            this.oldIndexes = oldIndexes;
            this.newIndexes = newIndexes;
            this.useSet = useSet;
            verifyIndexes();
            for(int i=0; i<oldIndexes.length; i++) {
                if(oldIndexes[i] >= nSize) {
                    throw new TestProblemException("oldIndex["+i+"] = "+oldIndexes[i]+", which is greater than nSize ="+nSize);
                }
                if(newIndexes[i] >= nSize) {
                    throw new TestProblemException("newIndex["+i+"] = "+newIndexes[i]+", which is greater than nSize ="+nSize);
                }
            }
            setName();
        }
        /*
         * nSize is number of members in each of the lists (managedEmployees, children etc),
         * it will be set to the max index + 1.
         */
        TranspositionTest(int[] oldIndexes, int[] newIndexes, boolean useSet) {
            super();
            this.oldIndexes = oldIndexes;
            this.newIndexes = newIndexes;
            this.useSet = useSet;
            verifyIndexes();
            nSize = 0;
            for(int i=0; i<oldIndexes.length; i++) {
                if(oldIndexes[i] > nSize) {
                    nSize = oldIndexes[i]; 
                }
                if(newIndexes[i] > nSize) {
                    nSize = newIndexes[i]; 
                }
            }
            nSize++;
            setName();
        }
        public void verifyIndexes() {
            if(oldIndexes.length != newIndexes.length) {
                throw new TestProblemException("oldIndexes.length != newIndexes.length");
            }
            for(int i=0; i < oldIndexes.length; i++) {
                int oldIndex = oldIndexes[i];
                boolean found = false;
                for(int j=0; j < newIndexes.length; j++) {
                    if(oldIndex == newIndexes[j]) {
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    throw new TestProblemException("oldIndexes["+i+"] value "+oldIndex+" not found in newIndexes");
                }
            }
        }
        String toString(int[] array) {
            String str = "[";
            for(int i=0; i<array.length; i++) {
                str += Integer.toString(array[i]);
                if(i < array.length-1) {
                    str += ", ";
                } else {
                    str += "]";
                }
            }
            return str;
        }
        void setName() {
            setName(getName() + " " + toString(oldIndexes) + " -> " + toString(newIndexes) + (useSet ? " set" : " remove/add"));
        }
        public void transpose(UnitOfWork uow) {
            managerClone = (Employee)uow.registerObject(manager);
            
            int n = oldIndexes.length;
            List[] lists = new ArrayList[n];
            for(int i=0; i<n; i++) {
                lists[i] = getFrom(managerClone, oldIndexes[i]);
            }
            for(int i=0; i<n; i++) {
                int newIndex = newIndexes[i];
                if(useSet) {
                    setInto_NoRelMaintanence(managerClone, newIndex, lists[i]);
                } else {
                    removeFrom_NoRelMaintanence(managerClone, newIndex);
                    addTo_NoRelMaintanence(managerClone, newIndex, lists[i]);
                }
            }
        }
        public void test() {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            transpose(uow);
            uow.commit();
        }
    }
    
    /*
     * Same as TranspositionTest, but the owner of transposed lists is detached, then merged.
     */
    class TranspositionMergeTest extends TranspositionTest {
        // This test transposes objects in detached collection, doesn't care whether set or remove/add 
        TranspositionMergeTest(int nSize, int[] oldIndexes, int[] newIndexes) {
            super(nSize, oldIndexes, newIndexes, true);
        }
        TranspositionMergeTest(int[] oldIndexes, int[] newIndexes) {
            // This test transposes objects in detached collection, doesn't care whether set or remove/add 
            super(oldIndexes, newIndexes, true);
        }
        void setName() {
            setName(getName() + " " + toString(oldIndexes) + " -> " + toString(newIndexes));
        }
        public void test() {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            transpose(uow);
            uow.unregisterObject(this.managerClone);
            uow.release();
            
            uow = getSession().acquireUnitOfWork();
            this.managerClone = (Employee)uow.mergeCloneWithReferences(this.managerClone);
            uow.commit();
        }
    }
    
    /*
     * For each mapping: add one new object, remove one existing object
     */
    class SimpleAddRemoveTest extends ChangeTest {
        SimpleAddRemoveTest() {
            super();
        }

        public void test() {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            managerClone = (Employee)uow.registerObject(manager);
            
            addTo(managerClone, create("new", 1));
            removeFrom(managerClone, 0);
            
            uow.commit();
        }
    }
    
    /*
     * For each mapping: add one new object, remove one existing object
     */
    class AddRemoveUpdateTest extends ChangeTest {
        List removedList;
        List removedListClone;
        AddRemoveUpdateTest() {
            super();
        }
        
        public void setup() {
            // Strings are immutable - can't update.
            useResponsibilities = false;
            // Can't directly register an aggregate in uow.
            usePhones = false;
            super.setup();
        }

        public void test() {
            // create a list of objects to be added
            List newList = this.create("new", 0);
            UnitOfWork uow = getSession().acquireUnitOfWork();
            List newListClone = this.register(newList, uow);
            uow.commit();
            
            // save list to be removed
            removedList = this.getFrom(manager, 0);
            
            uow = getSession().acquireUnitOfWork();
            managerClone = (Employee)uow.registerObject(manager);
            // update new list clone
            newListClone = this.register(newList, uow);
            update(newListClone, "updated", 0);
            // add updated objects to managerClone
            addTo(managerClone, newListClone);
            
            // remove from managerClone
            removedListClone = removeFrom(managerClone, 0);
            // update removed objects
            update(removedListClone, "updated", 0);
            
            uow.commit();
        }
        
        public void verify() {
            super.verify();
            if(isPrivatelyOwned) {
                verifyListRemoved(removedList);
            } else {
                verifyList(removedList, removedListClone);
            }
            if(errorMsg.length() > 0) {
                throw new TestErrorException('\n' + errorMsg);
            }
        }
    }
    
    /*
     * For each mapping: add one new object, remove one existing object
     */
    class SimpleAddRemoveTest2 extends ChangeTest {
        SimpleAddRemoveTest2() {
            super();
            nSize = 12;
        }

        public void test() {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            managerClone = (Employee)uow.registerObject(manager);
            
            addTo(managerClone, 0, create("new", 0));
            addTo(managerClone, 4, create("new", 4));
            removeFrom(managerClone, 8);
            
            uow.commit();
        }
    }
    
    /*
     * For each mapping: set a new object
     */
    class SimpleSetTest extends ChangeTest {
        SimpleSetTest() {
            super();
        }

        public void test() {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            managerClone = (Employee)uow.registerObject(manager);
            
            setInto(managerClone, 1, create("new", 1));
            
            uow.commit();
        }
    }
    
    /*
     * For each mapping: create a new List of two objects, set the new List into managerClone;
     * if useSet is not null, before setting a new list either add or set a new object into the old list.
     */
    class SimpleSetListTest extends ChangeTest {
        Boolean useSet;
        SimpleSetListTest() {
            super();
        }
        SimpleSetListTest(boolean useSet) {
            super();
            this.useSet = useSet;
            setName(getName() + (useSet ? " set" : " remove/add"));
        }

        public void test() {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            managerClone = (Employee)uow.registerObject(manager);
            
            if(useSet != null) {
                if(useSet) {
                    setInto(managerClone, 1, create("temp", 1));
                } else {
                    addTo(managerClone, create("temp", 2));
                }
            }
            List list = createList();
            addTo(list, create("new", 0));
            addTo(list, create("new", 1));
            setListInto(managerClone, list);
            
            uow.commit();
        }
    }
    
    class SimpleIndexTest extends ChangeTest {
        int min;
        int max;
        int nExpected;
        boolean useIndex;

        SimpleIndexTest(boolean useIndex) {
            super();
            nSize = 12;
            min = 1;
            max = 4;
            nExpected = max - min + 1;
            
            this.useIndex = useIndex;
            setName(getName() + (useIndex ? " use index()" : " use getField()"));
        }
        public void test() {
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            if(useManagedEmployees) {
                test("managedEmployees");
            }
            if(useChildren) {
                test("children");
            }
            if(useProjects) {
                test("projects");
            }
            if(useResponsibilities) {
                test("responsibilitiesList");
            }
            if(usePhones) {
                test("phoneNumbers");
            }
        }
        void test(String attributeName) {
            ReportQuery query = new ReportQuery();
            query.setReferenceClass(Employee.class);
            ExpressionBuilder builder = query.getExpressionBuilder();
            Expression exp;
            Expression firstNameManager = builder.get("firstName").equal("Manager");
            Expression anyOfAttribute = builder.anyOf(attributeName);
            if(useIndex) {
                // tests index() expression
                exp = firstNameManager.and(anyOfAttribute.index().between(min, max));
            } else {
                // tests getField() expression (or getTable().getField() expression) equivalent to index().
                if(isTableExpressionRequired(attributeName)) {
                    exp = firstNameManager.and(anyOfAttribute.getTable(getTableName(attributeName)).getField(getFieldName(attributeName)).between(min, max));
                } else {
                    exp = firstNameManager.and(anyOfAttribute.getField(getFieldName(attributeName)).between(min, max));
                }
            }
            query.setSelectionCriteria(exp);
            query.addAttribute(attributeName, anyOfAttribute);
            
            ArrayList indexesRead = new ArrayList(nExpected);
            boolean error = false;
            List<ReportQueryResult> results = (List)getSession().executeQuery(query);
            for(int i=0; i < results.size(); i++) {
                ReportQueryResult result = results.get(i);
                int index = getIndex(result.getResults().get(0), attributeName);
                error |= max < index || index < min;
                indexesRead.add(index);
            }
            String localErrorMsg = "";
            if(error) {
                localErrorMsg += "Wrong index values read: " + indexesRead +"; expected all numbers between (inclusive) " + min + " and " + max;
            }
            // query with joins return Cartesian product of all rows - the same values returned more than once. 
            if(OrderListTestModel.this.joinFetchOrBatchRead != JoinFetchOrBatchRead.OUTER_JOIN && OrderListTestModel.this.joinFetchOrBatchRead != JoinFetchOrBatchRead.INNER_JOIN) {
                if(indexesRead.size() != nExpected) {
                    localErrorMsg += attributeName + " Wrong number of objects read: " + indexesRead.size() +"; expected " + nExpected;
                }
            }
            
            if(localErrorMsg.length() > 0) {
                localErrorMsg = attributeName + ": " + localErrorMsg + "\n";
                errorMsg += localErrorMsg;
            }
        }
        /*
         * all objects were constructed to incorporate the index, see createManager method.
         */
        int getIndex(Object obj, String attributeName) {
            int index = -1;
            if(attributeName.equals("managedEmployees")) {
                index = Integer.parseInt(((Employee)obj).getFirstName());
            } else if(attributeName.equals("children")) {
                index = Integer.parseInt(((Child)obj).getFirstName());
            } else if(attributeName.equals("projects")) {
                index = getNumberFromString(((Project)obj).getName());
            } else if(attributeName.equals("responsibilitiesList")) {
                index = getNumberFromString((String)obj);
            } else if(attributeName.equals("phoneNumbers")) {
                index = Integer.parseInt(((PhoneNumber)obj).getNumber());
            }
            return index;
        }
        int getNumberFromString(String str) {
            int nEnd = str.length();
            int nStart = 0;
            for(nStart=0; nStart < nEnd; nStart++) {
                char ch = str.charAt(nStart);
                if('0' <= ch && ch <='9') {
                    break;
                }
            }
            str = str.substring(nStart);
            return Integer.parseInt(str);
        }

        protected void verify() {
            if(errorMsg.length() > 0) {
                errorMsg = "\n" + errorMsg;
                throw new TestErrorException(errorMsg);
            }
        }
        
        /*
         * ManyToMany and DirectCollection mapping require table name expression.
         * Used to test getField() expression (or getTable().getField() expression) equivalent to index().
         */
        boolean isTableExpressionRequired(String attributeName) {
            return attributeName.equals("projects") || attributeName.equals("responsibilitiesList"); 
        }
        /*
         * ManyToMany and DirectCollection mapping require table name expression.
         * Used to test getTable().getField() expression equivalent to index().
         */
        String getTableName(String attributeName) {
            if(attributeName.equals("projects")) {
                return "OL_PROJ_EMP";
            } else if(attributeName.equals("responsibilitiesList")) {
                return "OL_RESPONS";
            } else {
                throw new TestProblemException(attributeName + " should not be looking for table name.");
            }
        }
        /*
         * Used to test getField() expression (or getTable().getField() expression) equivalent to index().
         */
        String getFieldName(String attributeName) {
            String fieldName = null;
            if(attributeName.equals("managedEmployees")) {
                fieldName = "MANAGED_ORDER"; 
            } else if(attributeName.equals("children")) {
                fieldName = "CHILDREN_ORDER";
            } else if(attributeName.equals("projects")) {
                fieldName = "PROJ_ORDER";
            } else if(attributeName.equals("responsibilitiesList")) {
                fieldName = "RESPONS_ORDER";
            } else if(attributeName.equals("phoneNumbers")) {
                fieldName = "PHONE_ORDER";
            }
            return fieldName;
        }
    }
    
    /*
     * For each mapping: add object in front, or in the middle, or to the end of the list.
     * Currently only DirectCollectionMapping supports duplicates and nulls.
     * The test still uses all mappings so that it could be run with INNER_JOIN.
     */
    abstract class WhereToAddTest extends ChangeTest {
        String whereToAdd;
        WhereToAddTest(String whereToAdd) {
            super();
            this.whereToAdd = whereToAdd;
            if(!front.equals(whereToAdd) && !middle.equals(whereToAdd) && !end.equals(whereToAdd)) {
                throw new TestProblemException("Wrong whereToAdd = " + whereToAdd +"; Supported values: " + front +"; " +middle +"; " + end);
            }
            setName(getName() + " " + whereToAdd);
        }
        
        public void test() {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            managerClone = (Employee)uow.registerObject(manager);
            
            if(front.equals(whereToAdd)) {
                addTo(managerClone, 0, objectToAdd());
            } else if(middle.equals(whereToAdd)) {
                addTo(managerClone, 1, objectToAdd());
            } else if(end.equals(whereToAdd)) {
                addTo(managerClone, objectToAdd());
            }
            
            uow.commit();
        }

        abstract List objectToAdd(); 
    }
    
    /*
     * For each mapping: add null.
     * Currently only DirectCollectionMapping supports nulls.
     */
    class AddNullTest extends WhereToAddTest {
        AddNullTest(String whereToAdd) {
            super(whereToAdd);
        }
        List objectToAdd() {
            List list = create("new", 0);
            for(int i=0; i < list.size(); i++) {
                if(list.get(i) instanceof String) {
                    // Currently only DirectCollectionMapping (responsibilities) supports nulls.
                    list.set(i, null);
                    break;
                }
            }
            return list;
        }
    }
    
    /*
     * For each mapping: add duplicate.
     * Currently only DirectCollectionMapping supports duplicates.
     */
    class AddDuplicateTest extends WhereToAddTest {
        AddDuplicateTest(String whereToAdd) {
            super(whereToAdd);
        }
        List objectToAdd() {
            List newList = create("new", 0);
            List oldList = getFrom(managerClone, 0);
            // the two lists are of the same size
            for(int i=0; i < newList.size(); i++) {
                if(newList.get(i) instanceof String) {
                    // Currently only DirectCollectionMapping (responsibilities) supports duplicates.
                    newList.set(i, oldList.get(i));
                    break;
                }
            }
            return newList;
        }
    }
    
    /*
     * Break the order in the db, read the lists (with broken order) back, get the expected exception.
     * Note that a separate test required for each mapping to make sure they all throw correct exception. 
     */
    abstract class BreakOrderExceptionTest extends ChangeTest {
        BreakOrderExceptionTest() {
            super();
            if(orderCorrectionType != OrderCorrectionType.EXCEPTION) {
                throw new TestProblemException("Requires OrderCorrectionType.EXCEPTION");
            }
        }
        abstract public void test();
        
        protected void verify() {
            try {
                super.verify();
                throw new TestErrorException("Expected QueryException.LIST_ORDER_FIELD_WRONG_VALUE was not thrown.");
            } catch (QueryException queryException) {
                if(queryException.getErrorCode() == QueryException.LIST_ORDER_FIELD_WRONG_VALUE) {
                    // expected query exception on attempt to read broken order list
                    return;
                } else {
                    throw queryException;
                }
            }
        }
    }    
    class BreakOrderExceptionTest_OneToMany extends BreakOrderExceptionTest {
        BreakOrderExceptionTest_OneToMany() {
            super();
        }
        public void test() {
            breakManagedEmployeesOrder();
        }
    }
    class BreakOrderExceptionTest_UnidirectionalOneToMany extends BreakOrderExceptionTest {
        BreakOrderExceptionTest_UnidirectionalOneToMany() {
            super();
        }
        public void test() {
            breakChildrenOrder();
        }
    }
    class BreakOrderExceptionTest_ManyToMany extends BreakOrderExceptionTest {
        BreakOrderExceptionTest_ManyToMany() {
            super();
        }
        public void test() {
            breakProjectsOrder();
        }
    }
    class BreakOrderExceptionTest_DirectCollection extends BreakOrderExceptionTest {
        BreakOrderExceptionTest_DirectCollection() {
            super();
        }
        public void test() {
            breakResponsibilitiesOrder();
        }
    }
    class BreakOrderExceptionTest_AggregateCollection extends BreakOrderExceptionTest {
        BreakOrderExceptionTest_AggregateCollection() {
            super();
        }
        public void test() {
            breakPhonesOrder();
        }
    }

    /*
     * bug 331144: Removing from a collection that is broken results in an 
     * ArrayIndexOutOfBoundsException when the collection is fixed.
     */
    class BreakOrderCorrectionAndRemoveTest extends BreakOrderCorrectionTest {
        BreakOrderCorrectionAndRemoveTest(boolean shoulReadManagerThroughUow) {
            super(shoulReadManagerThroughUow);
        }
        public void test() {
            breakOrder();

            // clear cache to force reading back the objects
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            if(!shoulReadManagerThroughUow) {
                // read in the object with broken order in the db
                manager = (Employee)getSession().readObject(manager);
                if(isInstantiated(manager)) {
                    // verify that all attribute values are marked as having broken order
                    errorMsg = this.verifyIsListOrderBrokenInDb(manager, true);
                    if(errorMsg.length() > 0) {
                        errorMsg = "manager in test: " + errorMsg;
                        throw new TestErrorException(errorMsg);
                    }
                }
            }

            UnitOfWork uow = getSession().acquireUnitOfWork();
            if(shoulReadManagerThroughUow) {
                managerClone = (Employee)uow.readObject(manager);            
            } else {
                managerClone = (Employee)uow.registerObject(manager);
            }
            // remove element 1
            List list1 = removeFrom(managerClone, 1);
            
            // verify that all attribute values are marked as having broken order,
            // at this point they should be instantiated
            errorMsg = this.verifyIsListOrderBrokenInDb(managerClone, true);
            if(errorMsg.length() > 0) {
                errorMsg = "managerClone in test: " + errorMsg;
                uow.release();
                throw new TestErrorException(errorMsg);
            }

            try {
                uow.commit();
            } catch (ArrayIndexOutOfBoundsException exception){
                throw new TestErrorException("Test received exception commit when fixing a broken collection", exception);
            }
        }
    }

    /*
     * Break the order in the db, read the lists (with broken order) back, update lists, write out updated lists.
     * For verification temporary switch container policy into EXCEPTION mode, read back the list -
     * exception thrown if the order is still broken. 
     */
    class BreakOrderCorrectionTest extends ChangeTest {
        boolean shoulReadManagerThroughUow;
        BreakOrderCorrectionTest(boolean shoulReadManagerThroughUow) {
            super();
            this.nSize = 4;
            this.shoulReadManagerThroughUow = shoulReadManagerThroughUow;
                        
            if(orderCorrectionType != OrderCorrectionType.READ_WRITE) {
                throw new TestProblemException("Requires OrderCorrectionType.CORRECTION");
            }
            setName(getName() + (shoulReadManagerThroughUow ? " ReadThroughUow" : " ReadThroughSession"));
        }
        public void test() {
            breakOrder();

            // clear cache to force reading back the objects
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            if(!shoulReadManagerThroughUow) {
                // read in the object with broken order in the db
                manager = (Employee)getSession().readObject(manager);
                if(isInstantiated(manager)) {
                    // verify that all attribute values are marked as having broken order
                    errorMsg = this.verifyIsListOrderBrokenInDb(manager, true);
                    if(errorMsg.length() > 0) {
                        errorMsg = "manager in test: " + errorMsg;
                        throw new TestErrorException(errorMsg);
                    }
                }
            }
            
            // change the order of elements 0 <-> 1
            UnitOfWork uow = getSession().acquireUnitOfWork();
            if(shoulReadManagerThroughUow) {
                managerClone = (Employee)uow.readObject(manager);            
            } else {
                managerClone = (Employee)uow.registerObject(manager);
            }
            List list1 = removeFrom(managerClone, 1);
            
            // verify that all attribute values are marked as having broken order,
            // at this point they should be instantiated
            errorMsg = this.verifyIsListOrderBrokenInDb(managerClone, true);
            if(errorMsg.length() > 0) {
                errorMsg = "managerClone in test: " + errorMsg;
                uow.release();
                throw new TestErrorException(errorMsg);
            }
            
            List list0 = removeFrom(managerClone, 0);
            addTo(managerClone, list1);
            addTo(managerClone, list0);

            uow.commit();
        }
        
        protected void verify() {
            OrderCorrectionType originalMode = this.changeOrderCorrectionType(OrderCorrectionType.EXCEPTION); 
            try {                
                if(shoulReadManagerThroughUow) {
                    // manager is not in shared cache - bring the one from the shared cache.
                    ReadObjectQuery query = new ReadObjectQuery();
                    query.setReferenceClass(Employee.class);
                    query.checkCacheOnly();
                    manager = (Employee)getSession().readObject(manager);
                }
                // verify that all attribute values are marked as having NOT broken order
                errorMsg = this.verifyIsListOrderBrokenInDb(manager, false);
                if(errorMsg.length() > 0) {
                    errorMsg = "manager in verify: " + errorMsg;
                }
                String localErrorMsg = this.verifyIsListOrderBrokenInDb(managerClone, false);
                if(localErrorMsg.length() > 0) {
                    localErrorMsg = "managerClone in verify: " + localErrorMsg;
                    errorMsg += localErrorMsg;
                }
                super.verify();
            } finally {
                this.changeOrderCorrectionType(originalMode); 
            }
        }
    }    

    class BaseMultipleManagersTest extends BaseTest {
        /* number of managers created by createManagers method.*/
        int nManagers;
        /*
         * Number of each manager's managedEmployees, children, projects, responsibilities, phones
         * created by createManager method.
         */
        int nSize;
        
        /* managers */
        List<Employee> managers = new ArrayList(nManagers);
        /* their uow clones */
        List<Employee> managerClones = new ArrayList(nManagers);
        
        BaseMultipleManagersTest() {
            super();
            nManagers = 2;
            nSize = 2;
        }
        
        /*
         * Creates manager with nSize members 
         */
        void createManagers() {
            UnitOfWork uow = getSession().acquireUnitOfWork();

            for(int i=0; i < nManagers; i++) {
                String iStr = Integer.toString(i);
                Employee manager = new Employee("Manager", iStr);
                for(int j=0; j < nSize; j++) {
                    addTo(manager, create(iStr, j));
                }
                Employee managerClone = (Employee)uow.registerObject(manager);

                managers.add(manager);
                managerClones.add(managerClone);
            }

            uow.commit();
        }        
        
        public void reset() {
            super.reset();
            managers.clear();
            managerClones.clear();
        }

        public void verify() {
            if(managers == null || managers.isEmpty()) {
                throw new TestErrorException("managers is null or empty. Nothing to verify");
            }
            
            String textNameExt;
            for(int k=0; k<2; k++) {
                if(k == 0) {
                    textNameExt = "Cache";
                } else {
                    textNameExt = "DB";

                    // Read back the inserted objects, make sure the order is correct.
                    getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
                    ReadAllQuery query = new ReadAllQuery();
                    query.setReferenceClass(Employee.class);
                    Expression exp = query.getExpressionBuilder().get("firstName").equal("Manager");
                    query.setSelectionCriteria(exp);
                    query.addOrdering(query.getExpressionBuilder().get("lastName"));
                    
                    managers.clear();
                    managers.addAll((List<Employee>)getSession().executeQuery(query));
                }
                if(managers.size() != managerClones.size()) {
                    errorMsg = "wrong managers size " + managers.size() + "; expected size is " + managerClones.size();  
                } else {
                    for(int i=0; i<managers.size(); i++) {
                        if(!getAbstractSession().compareObjects(managerClones.get(i), managers.get(i))) {
                            String localErrorMsg = textNameExt + ": " + "manager["+i+"] not equal\n";
                            errorMsg += localErrorMsg;
                        }
                    }
                }
            }
            if(errorMsg.length() > 0) {
                throw new TestErrorException(errorMsg);
            }
        }
    }
    
    /*
     * Creates managers, verifies that they were correctly merged into cache and written to the db.
     */
    class CreateManagersTest extends BaseMultipleManagersTest {
        CreateManagersTest() {
            super();
        }
        
        public void setup() {
            createManagers();
        }
    }

    /*
     * Creates empty managers, verifies that they were correctly merged into cache and written to the db.
     * For OUTER_JOIN case to verify that read back lists are empty (as opposed to having null members).
     */
    class CreateEmptyManagersTest extends CreateManagersTest {
        CreateEmptyManagersTest() {
            super();
            nSize = 0;
        }
    }
    
    /*
     * Verify that for each mapping both its container policy and its select query's container policy
     * are of the expected type.
     */
    class VerifyContainerPolicyClassTest extends TestCase {
        Class expectedClass;
        VerifyContainerPolicyClassTest() {
            this(NullsLastOrderedListContainerPolicy.class);
        }
        VerifyContainerPolicyClassTest(Class expectedClass) {
            super();
            this.expectedClass = expectedClass;
            setName("VerifyContainerPolicyClassTest");
        }
        public void verify() {
            String errorMsg = "";
            List<CollectionMapping> listOrderMappings = EmployeeSystem.getListOrderMappings(getDatabaseSession());
            for(int i=0; i < listOrderMappings.size(); i++) {
                CollectionMapping mapping = listOrderMappings.get(i); 
                if(!mapping.getContainerPolicy().getClass().equals(expectedClass)) {
                    errorMsg += mapping.getAttributeName() + ".containerPolicy type is wrong; ";
                }
                ReadQuery selectQuery = mapping.getSelectionQuery(); 
                if(selectQuery.isReadAllQuery()) {
                    if(!((ReadAllQuery)selectQuery).getContainerPolicy().getClass().equals(expectedClass)) {
                        errorMsg += mapping.getAttributeName() + ".queryContainerPolicy type is wrong; ";
                    }
                } else {
                    if(!((DataReadQuery)selectQuery).getContainerPolicy().getClass().equals(expectedClass)) {
                        errorMsg += mapping.getAttributeName() + ".queryContainerPolicy type is wrong; ";
                    }
                }
            }
            if(errorMsg.length() > 0) {
                throw new TestErrorException(errorMsg);
            }
        }
    }
    
    /*
     * Test for OneToMany and UnideirectionalOneToMany mappings only:
     * verify that the row in the db corresponding to the removed object
     * has its order field value set to null.
     */
    class VerifyForeignKeyOfRemovedObject extends ChangeTest {
        boolean deleteSourceObject;
        VerifyForeignKeyOfRemovedObject(boolean deleteSourceObject) {
            super();
            this.deleteSourceObject = deleteSourceObject;
            setName(getShortClassName() + (deleteSourceObject ? "_deleteSource" : "_removeTarget"));
        }
        // Only tests OneToMany and UnideirectionalOneToMany
        public void setup() {
            this.usePhones = false;
            this.useProjects = false;
            this.useResponsibilities = false;
            super.setup();
        }
        public void test() {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            managerClone = (Employee)uow.registerObject(manager);
            if(deleteSourceObject) {
                for(int i=0; i < nSize; i++) {
                    if(useManagedEmployees) {
                        managerClone.getManagedEmployees().get(i).setManager(null);
                    }
                }
                uow.deleteObject(managerClone);
            } else {
                for(int i=nSize-1; 0 <= i; i--) {
                    if(useManagedEmployees) {
                        managerClone.removeManagedEmployee(i);
                    }
                    if(useChildren) {
                        managerClone.getChildren().remove(i);
                    }
                }
            }
            uow.commit();
        }
        public void verify() {
            // assume (as usual) that there were no objects in the db when the test started,
            // then all the objects left should have their order set to null (including manager if not deleted).
            if(useManagedEmployees) {
                String sqlString = "SELECT COUNT(*) FROM "+this.getManagegedEmployeesOrderTable()+" WHERE "+this.getManagegedEmployeesOrderField()+" IS NOT NULL";
                int nonNulls = ((Number)((AbstractRecord)getSession().executeSQL(sqlString).get(0)).getValues().get(0)).intValue();
                if(nonNulls != 0) {
                    errorMsg += "useManagedEmployees has "+nonNulls+" non nulls; ";
                }
            }
            if(useChildren) {
                String sqlString = "SELECT COUNT(*) FROM "+this.getChildrenOrderTable()+" WHERE "+this.getChildrenOrderField()+" IS NOT NULL";
                int nonNulls = ((Number)((AbstractRecord)getSession().executeSQL(sqlString).get(0)).getValues().get(0)).intValue();
                if(nonNulls != 0) {
                    errorMsg += "useManagedEmployees has "+nonNulls+" non nulls; ";
                }
            }
        }
    }
}
