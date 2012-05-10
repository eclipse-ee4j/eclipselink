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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import junit.framework.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.UpdateAllQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.JoinedAttributeTestHelper;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.*;
import org.eclipse.persistence.queries.ReadAllQuery;
 
public class JoinedAttributeAdvancedJunitTest extends JUnitTestCase {
        
    static protected Class[] classes = {Employee.class, Address.class, PhoneNumber.class, Project.class};
    static protected Vector[] objectVectors = {null, null, null, null};
    
    static protected EmployeePopulator populator = new EmployeePopulator();
    protected DatabaseSession dbSession;

    public JoinedAttributeAdvancedJunitTest() {
        super();
    }
    
    public JoinedAttributeAdvancedJunitTest(String name) {
        super(name);
    }
    
    // the session is cached to avoid extra dealing with SessionManager - 
    // without session caching each test caused at least 5 ClientSessins being acquired / released.
    protected DatabaseSession getDbSession() {
        if(dbSession == null) {
            dbSession = getServerSession();
        }
        return dbSession;
    }
    
    protected UnitOfWork acquireUnitOfWork() {
        return getDbSession().acquireUnitOfWork();   
    }
    
    protected void clear() {
        UnitOfWork uow = acquireUnitOfWork();

        // use alternate way for Symfoware as it doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193)
        if (!(JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            UpdateAllQuery updateEmployees = new UpdateAllQuery(Employee.class);
            updateEmployees.addUpdate("manager", null);
            updateEmployees.addUpdate("address", null);
            uow.executeQuery(updateEmployees);

            uow.executeQuery(new DeleteAllQuery(Employee.class));
        } else {
            Iterator<Employee> emps = uow.readAllObjects(Employee.class).iterator();
            while (emps.hasNext()){
              Employee emp = emps.next();
              emp.setManager(null);
              emp.setAddress((String)null);
              uow.deleteObject(emp);
            }; 
        }
        
        UpdateAllQuery updateProjects = new UpdateAllQuery(Project.class);
        updateProjects.addUpdate("teamLeader", null);
        uow.executeQuery(updateProjects);

        uow.executeQuery(new DeleteAllQuery(PhoneNumber.class));
        uow.executeQuery(new DeleteAllQuery(Address.class));
        uow.executeQuery(new DeleteAllQuery(Project.class));

        uow.commit();
        dbSessionClearCache();
    }
    
    protected void populate() {
        populator.buildExamples();
        populator.persistExample(getDbSession());
        dbSessionClearCache();
        for(int i=0; i < classes.length; i++) {
            objectVectors[i] = getDbSession().readAllObjects(classes[i]);
        }
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JoinedAttributeAdvancedJunitTest");
        
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testSetup"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectJoinTeamMembers"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectJoinTeamLeaderJoinAddressWhereTeamLeaderNotNull"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectJoinTeamLeaderJoinAddressWhereTeamLeaderNotNull_NoBase"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectJoinTeamMembersJoinAddress"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectJoinTeamMembersJoinAddress_NoBase"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectOuterJoinTeamMembersJoinAddress"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectOuterJoinTeamMembersJoinAddress_NoBase"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectOuterJoinTeamMembersOuterJoinAddress"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectOuterJoinTeamMembersOuterJoinAddress_NoBase"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectJoinTeamMembersOuterJoinAddress"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectJoinTeamMembersOuterJoinAddress_NoBase"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProblemReporterProjectJoinTeamMembersJoinAddress"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProblemReporterProjectJoinTeamMembersJoinAddress_NoBase"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testEmployeeJoinProjects"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testEmployeeJoinProjectsOnUOW"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testEmployeeJoinProjectsJoinTeamLeaderJoinAddressWhereManagerIsNull"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testEmployeeJoinProjectsJoinTeamLeaderJoinAddressWhereManagerIsNull_NoBase"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectOuterJoinTeamLeaderAddressTeamMembersAddressPhonesWhereProjectName"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectOuterJoinTeamLeaderAddressTeamMembersAddressPhonesWhereProjectName_NoBase"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testEmployeeOuterJoinAddressPhoneProjectsTeamLeaderAddressTeamMembersPhones"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testEmployeeOuterJoinAddressPhoneProjectsTeamLeaderAddressTeamMembersPhones_NoBase"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testEmployeeOuterJoinAddressPhoneProjectsTeamLeaderAddressTeamMembersPhonesOnUOW"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testEmployeeOuterJoinAddressPhoneProjectsTeamLeaderAddressTeamMembersPhonesOnUOW_NoBase"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testEmployeeJoinManagerAddressOuterJoinManagerAddress"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testEmployeeJoinManagerAddressOuterJoinManagerAddress_NoBase"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testTwoUnrelatedResultWithOneToManyJoins"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testMultipleUnrelatedResultWithOneToManyJoins"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testTwoUnrelatedResultWithOneToOneJoins"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testTwoUnrelatedResultWithOneToOneJoinsWithExtraItem"));
        // test for Bug 305713 - OuterJoinExpressionHolder order can be incorrect during ansi joins
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testOuterJoinSortingWithOneToOneJoins"));
        // test for Bug 305713 - OuterJoinExpressionHolder order can be incorrect during ansi joins
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testFetchOuterJoinSubClass"));
        
        // tests for Bug 274436: Custom QueryKeys fail to auto join.
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testAddressQK"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testManagedProjects"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testManagedLargeProjects"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectsQK"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testLargeProjects"));
//        suite.addTest(new JoinedAttributeAdvancedJunitTest("testResponsibilitiesQK"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testOwner"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testEmployees"));

        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        clearCache();
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());
        populate();
        clearCache();
    }
    
    public void tearDown() {
        dbSessionClearCache();
        dbSession = null;
        super.tearDown();
    }
        
    public void testProjectJoinTeamMembers() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Project.class);
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        query.addJoinedAttribute(query.getExpressionBuilder().anyOf("teamMembers"));

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testProjectJoinTeamLeaderJoinAddressWhereTeamLeaderNotNull() {
        internalProjectJoinTeamLeaderJoinAddressWhereTeamLeaderNotNull(false);
    }
    public void testProjectJoinTeamLeaderJoinAddressWhereTeamLeaderNotNull_NoBase() {
        internalProjectJoinTeamLeaderJoinAddressWhereTeamLeaderNotNull(true);
    }
    protected void internalProjectJoinTeamLeaderJoinAddressWhereTeamLeaderNotNull(boolean noBase) {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Project.class);
        Expression teamLeader = query.getExpressionBuilder().get("teamLeader");
        query.setSelectionCriteria(teamLeader.notNull());
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        if(!noBase) {
            query.addJoinedAttribute(teamLeader);
        }
        Expression teamLeaderAddress = teamLeader.get("address");
        query.addJoinedAttribute(teamLeaderAddress);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testProjectJoinTeamMembersJoinAddress() {
        internalProjectJoinTeamMembersJoinAddress(false);
    }
    public void testProjectJoinTeamMembersJoinAddress_NoBase() {
        internalProjectJoinTeamMembersJoinAddress(true);
    }
    protected void internalProjectJoinTeamMembersJoinAddress(boolean noBase) {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Project.class);
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        Expression teamMembers = query.getExpressionBuilder().anyOf("teamMembers");
        if(!noBase) {
            query.addJoinedAttribute(teamMembers);
        }
        Expression teamMembersAddress = teamMembers.get("address");
        query.addJoinedAttribute(teamMembersAddress);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testProjectOuterJoinTeamMembersJoinAddress() {
        internalProjectOuterJoinTeamMembersJoinAddress(false);
    }    
    public void testProjectOuterJoinTeamMembersJoinAddress_NoBase() {
        internalProjectOuterJoinTeamMembersJoinAddress(true);
    }
    protected void internalProjectOuterJoinTeamMembersJoinAddress(boolean noBase) {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Project.class);
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        Expression teamMembers = query.getExpressionBuilder().anyOfAllowingNone("teamMembers");
        if(!noBase) {
            query.addJoinedAttribute(teamMembers);
        }
        Expression teamMembersAddress = teamMembers.get("address");
        query.addJoinedAttribute(teamMembersAddress);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testProjectOuterJoinTeamMembersOuterJoinAddress() {
        internalProjectOuterJoinTeamMembersOuterJoinAddress(false);
    }    
    public void testProjectOuterJoinTeamMembersOuterJoinAddress_NoBase() {
        internalProjectOuterJoinTeamMembersOuterJoinAddress(true);
    }
    protected void internalProjectOuterJoinTeamMembersOuterJoinAddress(boolean noBase) {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Project.class);
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        Expression teamMembers = query.getExpressionBuilder().anyOfAllowingNone("teamMembers");
        if(!noBase) {
            query.addJoinedAttribute(teamMembers);
        }
        Expression teamMembersAddress = teamMembers.getAllowingNull("address");
        query.addJoinedAttribute(teamMembersAddress);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testProjectJoinTeamMembersOuterJoinAddress() {
        internalProjectJoinTeamMembersOuterJoinAddress(false);
    }
    public void testProjectJoinTeamMembersOuterJoinAddress_NoBase() {
        internalProjectJoinTeamMembersOuterJoinAddress(true);
    }
    protected void internalProjectJoinTeamMembersOuterJoinAddress(boolean noBase) {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Project.class);
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        Expression teamMembers = query.getExpressionBuilder().anyOf("teamMembers");
        if(!noBase) {
            query.addJoinedAttribute(teamMembers);
        }
        Expression teamMembersAddress = teamMembers.getAllowingNull("address");
        query.addJoinedAttribute(teamMembersAddress);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testProblemReporterProjectJoinTeamMembersJoinAddress() {
        internalProblemReporterProjectJoinTeamMembersJoinAddress(false);
    }    
    public void testProblemReporterProjectJoinTeamMembersJoinAddress_NoBase() {
        internalProblemReporterProjectJoinTeamMembersJoinAddress(true);
    }
    protected void internalProblemReporterProjectJoinTeamMembersJoinAddress(boolean noBase) {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Project.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("name").equal("Problem Reporter"));
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        Expression teamMembers = query.getExpressionBuilder().anyOf("teamMembers");
        if(!noBase) {
            query.addJoinedAttribute(teamMembers);
        }
        Expression teamMembersAddress = teamMembers.get("address");
        query.addJoinedAttribute(teamMembersAddress);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testEmployeeJoinProjects() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        query.addJoinedAttribute(query.getExpressionBuilder().anyOf("projects"));

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    //bug 6130550: test fetch join works in a uow as well as the session.
    public void testEmployeeJoinProjectsOnUOW() {
        ReadAllQuery controlQuery = new ReadAllQuery();
        controlQuery.setReferenceClass(Employee.class);
        
        ReadAllQuery queryWithJoins = (ReadAllQuery)controlQuery.clone();
        queryWithJoins.addJoinedAttribute(queryWithJoins.getExpressionBuilder().anyOf("projects"));
        
        UnitOfWork uow = acquireUnitOfWork();
        //loads objects into cache without using joins
        uow.executeQuery(controlQuery);
        uow.commit();
        ((UnitOfWorkImpl)uow).setShouldCascadeCloneToJoinedRelationship(true);
        Collection results = (Collection)uow.executeQuery(queryWithJoins);
        getDbSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        Collection controlledResults = (Collection)getDbSession().executeQuery(queryWithJoins);
        String errorMsg = JoinedAttributeTestHelper.compareCollections(controlledResults, results, controlQuery.getDescriptor(), (AbstractSession)getDbSession());
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testEmployeeJoinProjectsJoinTeamLeaderJoinAddressWhereManagerIsNull() {
        internalEmployeeJoinProjectsJoinTeamLeaderJoinAddressWhereManagerIsNull(false);
    }
    
    public void testEmployeeJoinProjectsJoinTeamLeaderJoinAddressWhereManagerIsNull_NoBase() {
        internalEmployeeJoinProjectsJoinTeamLeaderJoinAddressWhereManagerIsNull(true);
    }
    protected void internalEmployeeJoinProjectsJoinTeamLeaderJoinAddressWhereManagerIsNull(boolean noBase) {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("manager").isNull());
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        Expression projects = query.getExpressionBuilder().anyOf("projects");
        if(!noBase) {
            query.addJoinedAttribute(projects);
        }
        Expression teamLeader = projects.get("teamLeader");
        if(!noBase) {
            query.addJoinedAttribute(teamLeader);
        }
        Expression teamLeaderAddress = teamLeader.get("address");
        query.addJoinedAttribute(teamLeaderAddress);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testProjectOuterJoinTeamLeaderAddressTeamMembersAddressPhonesWhereProjectName() {
        internalProjectOuterJoinTeamLeaderAddressTeamMembersAddressPhonesWhereProjectName(false);
    }    
    public void testProjectOuterJoinTeamLeaderAddressTeamMembersAddressPhonesWhereProjectName_NoBase() {
        internalProjectOuterJoinTeamLeaderAddressTeamMembersAddressPhonesWhereProjectName(true);
    }
    protected void internalProjectOuterJoinTeamLeaderAddressTeamMembersAddressPhonesWhereProjectName(boolean noBase) {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Project.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("name").equal("Problem Reporting System").
            or(query.getExpressionBuilder().get("name").equal("Bleep Blob")));
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        Expression teamLeader = query.getExpressionBuilder().getAllowingNull("teamLeader");
        if(!noBase) {
            query.addJoinedAttribute(teamLeader);
        }
        Expression teamLeaderAddress = teamLeader.getAllowingNull("address");
        query.addJoinedAttribute(teamLeaderAddress);
        Expression teamMembers = query.getExpressionBuilder().anyOfAllowingNone("teamMembers");
        if(!noBase) {
            query.addJoinedAttribute(teamMembers);
        }
        Expression teamMembersAddress = teamMembers.getAllowingNull("address");
        query.addJoinedAttribute(teamMembersAddress);
        Expression teamMembersPhones = teamMembers.anyOfAllowingNone("phoneNumbers");
        query.addJoinedAttribute(teamMembersPhones);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testEmployeeOuterJoinAddressPhoneProjectsTeamLeaderAddressTeamMembersPhones() {
        internalEmployeeOuterJoinAddressPhoneProjectsTeamLeaderAddressTeamMembersPhones(false);
    }
    public void testEmployeeOuterJoinAddressPhoneProjectsTeamLeaderAddressTeamMembersPhones_NoBase() {
        internalEmployeeOuterJoinAddressPhoneProjectsTeamLeaderAddressTeamMembersPhones(true);
    }
    protected void internalEmployeeOuterJoinAddressPhoneProjectsTeamLeaderAddressTeamMembersPhones(boolean noBase) {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        // Note that without the following two lines address and phones are not read not for all Employees:
        // once an Employee is built (without Address and Phones)
        // it's not going to be rebuilt (get Address and Phones) when it's
        // up again either as a teamLeader or teamMember.
        // That means that only Employees read first indirectly (either as teamLeaders or
        // teamMembers would've got Phones and Addresses).
        query.addJoinedAttribute(query.getExpressionBuilder().getAllowingNull("address"));
        query.addJoinedAttribute(query.getExpressionBuilder().anyOfAllowingNone("phoneNumbers"));
        
        Expression projects = query.getExpressionBuilder().anyOfAllowingNone("projects");
        if(!noBase) {
            query.addJoinedAttribute(projects);
        }
        Expression teamLeader = projects.getAllowingNull("teamLeader");
        if(!noBase) {
            query.addJoinedAttribute(teamLeader);
        }
        Expression teamLeaderAddress = teamLeader.getAllowingNull("address");
        query.addJoinedAttribute(teamLeaderAddress);
        Expression teamMembers = projects.anyOfAllowingNone("teamMembers");
        if(!noBase) {
            query.addJoinedAttribute(teamMembers);
        }
        Expression teamMembersPhones = teamMembers.anyOfAllowingNone("phoneNumbers");
        query.addJoinedAttribute(teamMembersPhones);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    //bug 6130550: test fetch join works in a uow as well as the session.
    public void testEmployeeOuterJoinAddressPhoneProjectsTeamLeaderAddressTeamMembersPhonesOnUOW() {
        internalEmployeeOuterJoinAddressPhoneProjectsTeamLeaderAddressTeamMembersPhonesOnUOW(false);
    }
    public void testEmployeeOuterJoinAddressPhoneProjectsTeamLeaderAddressTeamMembersPhonesOnUOW_NoBase() {
        internalEmployeeOuterJoinAddressPhoneProjectsTeamLeaderAddressTeamMembersPhonesOnUOW(true);
    }
    protected void internalEmployeeOuterJoinAddressPhoneProjectsTeamLeaderAddressTeamMembersPhonesOnUOW(boolean noBase) {
        ReadAllQuery controlQuery = new ReadAllQuery();
        controlQuery.setReferenceClass(Employee.class);
        
        ReadAllQuery queryWithJoins = (ReadAllQuery)controlQuery.clone();
        
        // Note that without the following two lines address and phones are not read not for all Employees:
        // once an Employee is built (without Address and Phones)
        // it's not going to be rebuilt (get Address and Phones) when it's
        // up again either as a teamLeader or teamMember.
        // That means that only Employees read first indirectly (either as teamLeaders or
        // teamMembers would've got Phones and Addresses).
        queryWithJoins.addJoinedAttribute(queryWithJoins.getExpressionBuilder().getAllowingNull("address"));
        queryWithJoins.addJoinedAttribute(queryWithJoins.getExpressionBuilder().anyOfAllowingNone("phoneNumbers"));
        
        Expression projects = queryWithJoins.getExpressionBuilder().anyOfAllowingNone("projects");
        if(!noBase) {
            queryWithJoins.addJoinedAttribute(projects);
        }
        Expression teamLeader = projects.getAllowingNull("teamLeader");
        if(!noBase) {
            queryWithJoins.addJoinedAttribute(teamLeader);
        }
        Expression teamLeaderAddress = teamLeader.getAllowingNull("address");
        queryWithJoins.addJoinedAttribute(teamLeaderAddress);
        Expression teamMembers = projects.anyOfAllowingNone("teamMembers");
        if(!noBase) {
            queryWithJoins.addJoinedAttribute(teamMembers);
        }
        Expression teamMembersPhones = teamMembers.anyOfAllowingNone("phoneNumbers");
        queryWithJoins.addJoinedAttribute(teamMembersPhones);

        UnitOfWork uow = acquireUnitOfWork();
        //loads objects into cache without using joins
        uow.commit();
        uow.executeQuery(controlQuery);
        ((UnitOfWorkImpl)uow).setShouldCascadeCloneToJoinedRelationship(true);
        Collection results = (Collection)uow.executeQuery(queryWithJoins);
        getDbSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        Collection controlledResults = (Collection)JoinedAttributeTestHelper.getControlResultsFromControlQuery(controlQuery, queryWithJoins, (AbstractSession)uow);
        
        String errorMsg = JoinedAttributeTestHelper.compareCollections(controlledResults, results, controlQuery.getDescriptor(), (AbstractSession)getDbSession());
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    public void testEmployeeJoinManagerAddressOuterJoinManagerAddress() {
        internalEmployeeJoinManagerAddressOuterJoinManagerAddress(false);
    }
    public void testEmployeeJoinManagerAddressOuterJoinManagerAddress_NoBase() {
        internalEmployeeJoinManagerAddressOuterJoinManagerAddress(true);
    }
    protected void internalEmployeeJoinManagerAddressOuterJoinManagerAddress(boolean noBase) {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("lastName").equal("Way").
                                    or(query.getExpressionBuilder().get("lastName").equal("Jones")));
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        Expression manager = query.getExpressionBuilder().get("manager");
        if(!noBase) {
            query.addJoinedAttribute(manager);
        }
        query.addJoinedAttribute(manager.get("address"));
        Expression managersManager = manager.getAllowingNull("manager");
        if(!noBase) {
            query.addJoinedAttribute(managersManager);
        }
        query.addJoinedAttribute(managersManager.get("address"));

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testTwoUnrelatedResultWithOneToManyJoins() {
        if (getServerSession().getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testTwoUnrelatedResultWithOneToManyJoins skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        ReadAllQuery raq = new ReadAllQuery(Employee.class);
        raq.setSelectionCriteria(raq.getExpressionBuilder().get("lastName").equal("Way").or(raq.getExpressionBuilder().get("lastName").equal("Jones")));
        Employee emp = (Employee)((Vector)getDbSession().executeQuery(raq)).firstElement();
        emp.getPhoneNumbers();
        for (Iterator iterator = emp.getPhoneNumbers().iterator(); iterator.hasNext();){
            ((PhoneNumber)iterator.next()).getOwner();
        }
        
        raq = new ReadAllQuery(Address.class);
        raq.setSelectionCriteria(raq.getExpressionBuilder().get("city").like("%ttawa%"));
        Address addr = (Address)((Vector)getDbSession().executeQuery(raq)).firstElement();
        addr.getEmployees();
        for (Iterator iterator = addr.getEmployees().iterator(); iterator.hasNext();){
            ((Employee)iterator.next()).getAddress();
        }
        
        getDbSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        
        ReportQuery query = new ReportQuery();
        query.setShouldReturnWithoutReportQueryResult(true);
        query.setReferenceClass(Employee.class);

        ExpressionBuilder eb = new ExpressionBuilder(Address.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("id").equal(emp.getId()).and(eb.get("ID").equal(addr.getID())));
        
        List list = new ArrayList();
        list.add(query.getExpressionBuilder().anyOf("phoneNumbers"));
        query.addItem("employee", query.getExpressionBuilder(), list);

        list = new ArrayList();
        list.add(eb.anyOf("employees"));
        query.addItem("address", eb, list);
        
        Vector result = (Vector)getDbSession().executeQuery(query);
        
        DeleteAllQuery deleteAll = new DeleteAllQuery(PhoneNumber.class);
        deleteAll.setSelectionCriteria(deleteAll.getExpressionBuilder().get("owner").get("id").equal(emp.getId()));
        UnitOfWork uow = getDbSession().acquireUnitOfWork();
        uow.executeQuery(deleteAll);

        UpdateAllQuery updall = new UpdateAllQuery(Employee.class);
        updall.addUpdate("address", null);
        updall.setSelectionCriteria(updall.getExpressionBuilder().get("address").get("ID").equal(addr.getID()));
        uow.executeQuery(updall);
        
        uow.commit();
        
        try {
            Employee emp2 = (Employee)((Object[])result.firstElement())[0];
            Address addr2 = (Address)((Object[])result.firstElement())[1];
    
            assertTrue("PhoneNumbers were not joined correctly, emp.getPhoneNumbers().size = " + emp.getPhoneNumbers().size() + " emp2.getPhoneNumbers().size = " + emp2.getPhoneNumbers().size(), (emp.getPhoneNumbers().size() == emp2.getPhoneNumbers().size()));
            assertTrue("Employees were not joined correctly, addr.employees.size = " + addr.getEmployees().size() + "addr2.employees.size = " + addr2.getEmployees().size(), (addr.getEmployees().size() == addr2.getEmployees().size()));
        } finally {
            testSetup();
        }

    }
    
    public void testMultipleUnrelatedResultWithOneToManyJoins() {
        if (getServerSession().getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testMultipleUnrelatedResultWithOneToManyJoins skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        ReadAllQuery raq = new ReadAllQuery(Employee.class);
        raq.setSelectionCriteria(raq.getExpressionBuilder().notEmpty("phoneNumbers"));
        Employee emp = (Employee)((Vector)getDbSession().executeQuery(raq)).firstElement();
        emp.getPhoneNumbers();
        for (Iterator iterator = emp.getPhoneNumbers().iterator(); iterator.hasNext();){
            ((PhoneNumber)iterator.next()).getOwner();
        }
        
        raq = new ReadAllQuery(Address.class);
        raq.setSelectionCriteria(raq.getExpressionBuilder().get("city").like("%ttawa%"));
        Address addr = (Address)((Vector)getDbSession().executeQuery(raq)).firstElement();
        addr.getEmployees();
        for (Iterator iterator = addr.getEmployees().iterator(); iterator.hasNext();){
            Employee addrEmp = (Employee)iterator.next();
            addrEmp.getAddress();
            addrEmp.getPhoneNumbers().size(); // as the report query will join in all phones to all emps, make sure we can compare.
        }
        
        getDbSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        
        ReportQuery query = new ReportQuery();
        query.setShouldReturnWithoutReportQueryResult(true);
        query.setReferenceClass(Address.class);
        
        ExpressionBuilder eb = new ExpressionBuilder(Employee.class);

        List list = new ArrayList();
        list.add(eb.anyOf("phoneNumbers"));
        query.addItem("employee", eb, list);

        list = new ArrayList();
        list.add(query.getExpressionBuilder().anyOf("employees"));
        query.addItem("address", query.getExpressionBuilder(), list);

        query.setSelectionCriteria(query.getExpressionBuilder().get("ID").equal(addr.getID()));
        
        
        Vector result = (Vector)getDbSession().executeQuery(query);
        
        DeleteAllQuery deleteAll = new DeleteAllQuery(PhoneNumber.class);
        deleteAll.setSelectionCriteria(deleteAll.getExpressionBuilder().get("owner").get("id").equal(emp.getId()));
        UnitOfWork uow = getDbSession().acquireUnitOfWork();
        uow.executeQuery(deleteAll);

        UpdateAllQuery updall = new UpdateAllQuery(Employee.class);
        updall.addUpdate("address", null);
        updall.setSelectionCriteria(updall.getExpressionBuilder().get("address").get("ID").equal(addr.getID()));
        uow.executeQuery(updall);
        
        uow.commit();
        
        try {
            Employee emp2 = null;
            Address addr2 = null;
            for (Iterator iterator = result.iterator(); iterator.hasNext();){
                Object [] items = (Object[])iterator.next();
                emp2 = (Employee)items[0];
                if (emp2.getId().equals(emp.getId())){
                    addr2 = (Address)items[1];
                    break;
                }
            }
            assertTrue("PhoneNumbers were not joined correctly, emp.getPhoneNumbers().size = " + emp.getPhoneNumbers().size() + " emp2.getPhoneNumbers().size = " + emp2.getPhoneNumbers().size(), (emp.getPhoneNumbers().size() == emp2.getPhoneNumbers().size()));
            assertTrue("Employees were not joined correctly, addr.employees.size = " + addr.getEmployees().size() + "addr2.employees.size = " + addr2.getEmployees().size(), (addr.getEmployees().size() == addr2.getEmployees().size()));
        } finally {
            testSetup();
        }

    }
    
    public void testTwoUnrelatedResultWithOneToOneJoins() {
        if (getServerSession().getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testTwoUnrelatedResultWithOneToOneJoins skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        ReadAllQuery raq = new ReadAllQuery(Employee.class);
        raq.setSelectionCriteria(raq.getExpressionBuilder().get("lastName").equal("Way").or(raq.getExpressionBuilder().get("lastName").equal("Jones")));
        Employee emp = (Employee)((Vector)getDbSession().executeQuery(raq)).firstElement();
        emp.getAddress();
        
        raq = new ReadAllQuery(Address.class);
        raq.setSelectionCriteria(raq.getExpressionBuilder().get("city").like("%ttawa%"));
        Address addr = (Address)((Vector)getDbSession().executeQuery(raq)).firstElement();
        addr.getEmployees();
        for (Iterator iterator = addr.getEmployees().iterator(); iterator.hasNext();){
            ((Employee)iterator.next()).getAddress();
        }
        
        getDbSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        
        ReportQuery query = new ReportQuery();
        query.setShouldReturnWithoutReportQueryResult(true);
        query.setReferenceClass(Employee.class);

        ExpressionBuilder eb = new ExpressionBuilder(Address.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("id").equal(emp.getId()).and(eb.get("ID").equal(addr.getID())));
        
        List list = new ArrayList();
        list.add(query.getExpressionBuilder().get("address"));
        query.addItem("employee", query.getExpressionBuilder(), list);

        list = new ArrayList();
        list.add(eb.anyOf("employees"));
        query.addItem("address", eb, list);
        
        List result = (List)getDbSession().executeQuery(query);
        
        UpdateAllQuery updall = new UpdateAllQuery(Employee.class);
        updall.addUpdate("address", null);
        updall.setSelectionCriteria(updall.getExpressionBuilder().get("id").equal(emp.getId()));
        UnitOfWork uow = getDbSession().acquireUnitOfWork();
        uow.executeQuery(updall);

        updall = new UpdateAllQuery(Employee.class);
        updall.addUpdate("address", null);
        updall.setSelectionCriteria(updall.getExpressionBuilder().get("address").get("ID").equal(addr.getID()));
        uow.executeQuery(updall);
        
        uow.commit();
        
        try {
            Employee emp2 = (Employee)((Object[])result.get(0))[0];
            Address addr2 = (Address)((Object[])result.get(0))[1];

            assertTrue("Address were not joined correctly, emp.getAddress() = null", (emp2.getAddress() != null));
            assertTrue("Employees were not joined correctly, addr.employees.size = " + addr.getEmployees().size() + "addr2.employees.size = " + addr2.getEmployees().size(), (addr.getEmployees().size() == addr2.getEmployees().size()));

        } finally {
            testSetup();
        }
    }
    
    // Bug 305713 - OuterJoinExpressionHolder linearization order can be incorrect during ansi joins 
    public void testOuterJoinSortingWithOneToOneJoins() {
    	ReadAllQuery controlQuery = new ReadAllQuery(Employee.class);
    	ExpressionBuilder controlBuilder = controlQuery.getExpressionBuilder();
    	Expression dept = controlBuilder.get("department");
    	controlQuery.setSelectionCriteria(dept.notNull().and(dept.get("departmentHead").isNull()));
    	List<Employee> controlResults = (List)getDbSession().executeQuery(controlQuery);
    	
    	assertNotNull("Control query results should be non-null", controlResults);
    	assertFalse("Control query results should be non-empty", controlResults.isEmpty());
    	
    	ReadAllQuery query = new ReadAllQuery(Employee.class);
    	ExpressionBuilder builder = query.getExpressionBuilder();
    	
    	Expression expression = builder.get("department");
    	query.addJoinedAttribute(expression);
    	
    	expression = expression.getAllowingNull("departmentHead");
    	query.addJoinedAttribute(expression);
    	
    	expression = expression.getAllowingNull("manager");
    	query.addJoinedAttribute(expression);
    	
    	expression = expression.getAllowingNull("address");
    	query.addJoinedAttribute(expression);

    	List<Employee> actualResults = (List)getDbSession().executeQuery(query);
    	
    	assertNotNull("Query results should be non-null.", actualResults);
    	assertFalse("Query results should be non-empty.", actualResults.isEmpty());
    	
    	assertEquals("Query results should be the same.", controlResults.size(), actualResults.size());
    	
    	for (Employee emp : controlResults) {
    		if (!actualResults.contains(emp)) {
    			fail("Actual results do not contain employee with id " + emp.getId());
    		}
    	}
    }
    
    // Bug 305713 - OuterJoinExpressionHolder linearization order can be incorrect during ansi joins 
    // Before the fix generated wrong sql: 
    //   SELECT ... FROM CMP3_EMPLOYEE t1 
    //   LEFT OUTER JOIN CMP3_ADDRESS t0 ON (t0.ADDRESS_ID = t1.ADDR_ID) 
    //   LEFT OUTER JOIN (CMP3_PROJECT t6 JOIN CMP3_HPROJECT t8 ON (t8.PROJ_ID = t6.PROJ_ID)) ON (t6.PROJ_ID = t1.HUGE_PROJ_ID) 
    //   LEFT OUTER JOIN CMP3_DEPT t11 ON (t11.ID = t1.DEPT_ID),
    //   CMP3_EMPLOYEE t3 
    //   LEFT OUTER JOIN CMP3_ADDRESS t5 ON (t5.ADDRESS_ID = t3.ADDR_ID) 
    //   LEFT OUTER JOIN (CMP3_EMPLOYEE t9 JOIN CMP3_SALARY t10 ON (t10.EMP_ID = t9.EMP_ID)) ON (t9.EMP_ID = t8.EVANGELIST_ID), 
    //   CMP3_LPROJECT t7, CMP3_SALARY t4, CMP3_SALARY t2 
    //   WHERE ((t2.EMP_ID = t1.EMP_ID) AND ((t3.EMP_ID = t1.MANAGER_EMP_ID) AND (t4.EMP_ID = t3.EMP_ID)))
    // that failed with: java.sql.SQLSyntaxErrorException: ORA-00904: "T8"."EVANGELIST_ID": invalid identifier.
    //
    // After the fix generates correct sql (the only difference is order: LEFT OUTER JOIN on t8.EVANGELIST_ID has moved up):
    //   SELECT ... FROM CMP3_EMPLOYEE t1
    //   LEFT OUTER JOIN CMP3_ADDRESS t0 ON (t0.ADDRESS_ID = t1.ADDR_ID)
    //   LEFT OUTER JOIN (CMP3_PROJECT t6 JOIN CMP3_HPROJECT t8 ON (t8.PROJ_ID = t6.PROJ_ID)) ON (t6.PROJ_ID = t1.HUGE_PROJ_ID) 
    //   LEFT OUTER JOIN (CMP3_EMPLOYEE t9 JOIN CMP3_SALARY t10 ON (t10.EMP_ID = t9.EMP_ID)) ON (t9.EMP_ID = t8.EVANGELIST_ID) 
    //   LEFT OUTER JOIN CMP3_DEPT t11 ON (t11.ID = t1.DEPT_ID),
    //   CMP3_EMPLOYEE t3 
    //   LEFT OUTER JOIN CMP3_ADDRESS t5 ON (t5.ADDRESS_ID = t3.ADDR_ID), 
    //   CMP3_LPROJECT t7, CMP3_SALARY t4, CMP3_SALARY t2 
    //   WHERE ((t2.EMP_ID = t1.EMP_ID) AND ((t3.EMP_ID = t1.MANAGER_EMP_ID) AND (t4.EMP_ID = t3.EMP_ID)))
    public void testFetchOuterJoinSubClass() {
        // TODO: currently the test verifies that the query doesn't blow up.
        // Add control query so that the results could be verified.
        // Unfortunately executeQueriesAndCompareResults method doesn't work correctly when outer joins mixed with inner joins
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        ExpressionBuilder eb = query.getExpressionBuilder();        
        Expression expression = eb.getAllowingNull("address");
        query.addJoinedAttribute(expression);

        expression = eb.get("manager");
        query.addJoinedAttribute(expression);
        expression = expression.getAllowingNull("address");
        query.addJoinedAttribute(expression);

        expression = eb.getAllowingNull("hugeProject");
        query.addJoinedAttribute(expression);
        expression = expression.getAllowingNull("evangelist");
        query.addJoinedAttribute(expression);

        List results = (List)getDbSession().executeQuery(query);
        System.out.println();
    }
    
    public void testTwoUnrelatedResultWithOneToOneJoinsWithExtraItem() {
        if (getServerSession().getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testTwoUnrelatedResultWithOneToOneJoinsWithExtraItem skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        ReadAllQuery raq = new ReadAllQuery(Employee.class);
        raq.setSelectionCriteria(raq.getExpressionBuilder().get("lastName").equal("Way").or(raq.getExpressionBuilder().get("lastName").equal("Jones")));
        Employee emp = (Employee)((Vector)getDbSession().executeQuery(raq)).firstElement();
        emp.getAddress();
        
        raq = new ReadAllQuery(Address.class);
        raq.setSelectionCriteria(raq.getExpressionBuilder().get("city").like("%ttawa%"));
        Address addr = (Address)((Vector)getDbSession().executeQuery(raq)).firstElement();
        addr.getEmployees();
        for (Iterator iterator = addr.getEmployees().iterator(); iterator.hasNext();){
            ((Employee)iterator.next()).getAddress();
        }
        
        getDbSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        
        ReportQuery query = new ReportQuery();
        query.setShouldReturnWithoutReportQueryResult(true);
        query.setReferenceClass(Employee.class);

        ExpressionBuilder eb = new ExpressionBuilder(Address.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("id").equal(emp.getId()).and(eb.get("ID").equal(addr.getID())));
        
        List list = new ArrayList();
        list.add(query.getExpressionBuilder().get("address"));
        query.addItem("employee", query.getExpressionBuilder(), list);
        query.addItem("employee_name", query.getExpressionBuilder().get("firstName"));

        list = new ArrayList();
        list.add(eb.anyOf("employees"));
        query.addItem("address", eb, list);
        
        List result = (List)getDbSession().executeQuery(query);
        
        UpdateAllQuery updall = new UpdateAllQuery(Employee.class);
        updall.addUpdate("address", null);
        updall.setSelectionCriteria(updall.getExpressionBuilder().get("id").equal(emp.getId()));
        UnitOfWork uow = getDbSession().acquireUnitOfWork();
        uow.executeQuery(updall);

        updall = new UpdateAllQuery(Employee.class);
        updall.addUpdate("address", null);
        updall.setSelectionCriteria(updall.getExpressionBuilder().get("address").get("ID").equal(addr.getID()));
        uow.executeQuery(updall);
        
        uow.commit();

        try {
            Employee emp2 = (Employee)((Object[])result.get(0))[0];
            Address addr2 = (Address)((Object[])result.get(0))[2];
            assertTrue("Address were not joined correctly, emp.getAddress() = null", (emp2.getAddress() != null));
            assertTrue("Employees were not joined correctly, addr.employees.size = " + addr.getEmployees().size() + "addr2.employees.size = " + addr2.getEmployees().size(), (addr.getEmployees().size() == addr2.getEmployees().size()));
            if (!emp2.getFirstName().equals(((Object[])result.get(0))[1])) {
                fail("Failed to return employee name as an separate item");
            }
    
        } finally {
            testSetup();
        }
    }
    
    public void testAddressQK() {
        // control query - it's results should be the same as results of the tested query
        ReadAllQuery controlQuery = new ReadAllQuery(Employee.class);
        ExpressionBuilder controlEb = controlQuery.getExpressionBuilder();
        controlQuery.setSelectionCriteria(controlEb.getAllowingNull("address").get("city").like("O%"));
        List<Employee> controlEmps = (List)getDbSession().executeQuery(controlQuery);

        // choose example that actually selects something
        if(controlEmps.isEmpty()) {
            fail("Test setup problem: control query result is empty. Choose selection criteria such that something is actually selected");
        }
        
        // clear cache
        getDbSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        
        // execute the tested query
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder eb = query.getExpressionBuilder();
        query.setSelectionCriteria(eb.getAllowingNull("addressQK").get("city").like("O%"));
        List<Employee> emps = (List)getDbSession().executeQuery(query);
        
        // compare results with control query
        String errorMsg = JoinedAttributeTestHelper.compareCollections(controlEmps, emps, getDbSession().getDescriptor(Employee.class), (AbstractSession)getDbSession());
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testManagedProjects() {
        // TODO: currently the test verifies that the query doesn't blow up.
        // Add control query so that the results could be verified.
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder eb = query.getExpressionBuilder();
        query.setSelectionCriteria(eb.anyOfAllowingNone("managedProjects").get("name").containsSubstring("Enterprise"));
        List<Employee> emps = (List)getDbSession().executeQuery(query);

        // choose example that actually selects something
        if(emps.isEmpty()) {
            fail();
        }
    }
            
    public void testManagedLargeProjects() {
        // TODO: currently the test verifies that the query doesn't blow up.
        // Add control query so that the results could be verified.
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder eb = query.getExpressionBuilder();
        query.setSelectionCriteria(eb.anyOfAllowingNone("managedLargeProjects").get("name").containsSubstring("Enterprise"));
        List<Employee> emps = (List)getDbSession().executeQuery(query);

        // choose example that actually selects something
        if(emps.isEmpty()) {
            fail();
        }
    }
            
    public void testProjectsQK() {
        // control query - it's results should be the same as results of the tested query
        ReadAllQuery controlQuery = new ReadAllQuery(Employee.class);
        ExpressionBuilder controlEb = controlQuery.getExpressionBuilder();
        controlQuery.setSelectionCriteria(controlEb.anyOfAllowingNone("projects").get("name").containsSubstring("Enterprise"));
        List<Employee> controlEmps = (List)getDbSession().executeQuery(controlQuery);

        // choose example that actually selects something
        if(controlEmps.isEmpty()) {
            fail("Test setup problem: control query result is empty. Choose selection criteria such that something is actually selected");
        }
                
        // clear cache
        getDbSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        
        // execute the tested query
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder eb = query.getExpressionBuilder();
        query.setSelectionCriteria(eb.anyOfAllowingNone("projectsQK").get("name").containsSubstring("Enterprise"));
        List<Employee> emps = (List)getDbSession().executeQuery(query);

        // choose example that actually selects something
        if(emps.isEmpty()) {
            fail();
        }
    }
            
    public void testLargeProjects() {
        // TODO: currently the test verifies that the query doesn't blow up.
        // Add control query so that the results could be verified.
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder eb = query.getExpressionBuilder();
        query.setSelectionCriteria(eb.anyOfAllowingNone("largeProjects").get("name").containsSubstring("Enterprise"));
        List<Employee> emps = (List)getDbSession().executeQuery(query);

        // choose example that actually selects something
        if(emps.isEmpty()) {
            fail();
        }
    }
            
    // doesn't work yet
    public void testResponsibilitiesQK() {
        ReadAllQuery controlQuery = new ReadAllQuery(Employee.class);
        ExpressionBuilder controlEb = controlQuery.getExpressionBuilder();
        controlQuery.setSelectionCriteria(controlEb.anyOfAllowingNone("responsibilities").equal("Make the coffee."));
        List<Employee> controlEmps = (List)getDbSession().executeQuery(controlQuery);

        // choose example that actually selects something
        if(controlEmps.isEmpty()) {
            fail("Test setup problem: control query result is empty. Choose selection criteria such that something is actually selected");
        }
        
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder eb = query.getExpressionBuilder();
        query.setSelectionCriteria(eb.anyOfAllowingNone("responsibilitiesQK").equal("Make the coffee."));
        List<Employee> emps = (List)getDbSession().executeQuery(query);

        String errorMsg = JoinedAttributeTestHelper.compareCollections(controlEmps, emps, getDbSession().getDescriptor(Employee.class), (AbstractSession)getDbSession());
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
            
    public void testOwner() {
        // TODO: currently the test verifies that the query doesn't blow up.
        // Add control query so that the results could be verified.
        ReadAllQuery query = new ReadAllQuery(Address.class);
        ExpressionBuilder eb = query.getExpressionBuilder();
        query.setSelectionCriteria(eb.getAllowingNull("owner").get("firstName").like("J%"));
        List<Address> addresses = (List)getDbSession().executeQuery(query);
        
        // choose example that actually selects something
        if(addresses.isEmpty()) {
            fail();
        }
    }
    
    public void testEmployees() {
        // TODO: currently the test verifies that the query doesn't blow up.
        // Add control query so that the results could be verified.
        ReadAllQuery query = new ReadAllQuery(Project.class);
        ExpressionBuilder eb = query.getExpressionBuilder();
        query.setSelectionCriteria(eb.anyOfAllowingNone("employees").get("firstName").equal("John"));
        List<Project> projects = (List)getDbSession().executeQuery(query);
        
        // choose example that actually selects something
        if(projects.isEmpty()) {
            fail();
        }
    }
    
    protected String executeQueriesAndCompareResults(ObjectLevelReadQuery controlQuery, ObjectLevelReadQuery queryWithJoins) {
        return JoinedAttributeTestHelper.executeQueriesAndCompareResults(controlQuery, queryWithJoins, (AbstractSession)getDbSession());
    }

    protected boolean compare() {
        for(int i=0; i < classes.length; i++) {
            if(!compare(i)) {
                return false;
            }
        }
        return true;
    }

    protected boolean compare(int i) {
        if(objectVectors[i] == null) {
            return false;
        }
        Vector currentVector = getDbSession().readAllObjects(classes[i]);
        
        if(currentVector.size() != objectVectors[i].size()) {
            return false;
        }
        ClassDescriptor descriptor = getDbSession().getDescriptor(classes[i]);
        for(int j=0; j < currentVector.size(); j++) {
            Object obj1 = objectVectors[i].elementAt(j);
            Object obj2 = currentVector.elementAt(j);
            if(!descriptor.getObjectBuilder().compareObjects(obj1, obj2, (org.eclipse.persistence.internal.sessions.AbstractSession)getDbSession())) {
                return false;
            }
        }
        return true;
    }

    public void dbSessionClearCache() {
        getDbSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
