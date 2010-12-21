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


package org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import junit.framework.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.UpdateAllQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.JoinedAttributeTestHelper;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.*;
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
            dbSession = getServerSession("fieldaccess");
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
              emp.setAddress(null);
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
        suite.setName("JoinedAttributeAdvancedJunitTest (fieldaccess)");
        
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testSetup"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectJoinTeamMembers"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectJoinTeamLeaderJoinAddressWhereTeamLeaderNotNull"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectJoinTeamMembersJoinAddress"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectOuterJoinTeamMembersJoinAddress"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectOuterJoinTeamMembersOuterJoinAddress"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectJoinTeamMembersOuterJoinAddress"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProblemReporterProjectJoinTeamMembersJoinAddress"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testEmployeeJoinProjects"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testEmployeeJoinProjectsJoinTeamLeaderJoinAddressWhereManagerIsNull"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testProjectOuterJoinTeamLeaderAddressTeamMembersAddressPhonesWhereProjectName"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testEmployeeOuterJoinAddressPhoneProjectsTeamLeaderAddressTeamMembersPhones"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testEmployeeJoinManagerAddressOuterJoinManagerAddress"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testTwoUnrelatedResultWithOneToManyJoins"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testMultipleUnrelatedResultWithOneToManyJoins"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testTwoUnrelatedResultWithOneToOneJoins"));
        suite.addTest(new JoinedAttributeAdvancedJunitTest("testTwoUnrelatedResultWithOneToOneJoinsWithExtraItem"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession("fieldaccess"));
        populate();
        clearCache("fieldaccess");
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
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Project.class);
        Expression teamLeader = query.getExpressionBuilder().get("teamLeader");
        query.setSelectionCriteria(teamLeader.notNull());
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        query.addJoinedAttribute(teamLeader);
        Expression teamLeaderAddress = teamLeader.get("address");
        query.addJoinedAttribute(teamLeaderAddress);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testProjectJoinTeamMembersJoinAddress() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Project.class);
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        Expression teamMembers = query.getExpressionBuilder().anyOf("teamMembers");
        query.addJoinedAttribute(teamMembers);
        Expression teamMembersAddress = teamMembers.get("address");
        query.addJoinedAttribute(teamMembersAddress);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testProjectOuterJoinTeamMembersJoinAddress() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Project.class);
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        Expression teamMembers = query.getExpressionBuilder().anyOfAllowingNone("teamMembers");
        query.addJoinedAttribute(teamMembers);
        Expression teamMembersAddress = teamMembers.get("address");
        query.addJoinedAttribute(teamMembersAddress);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testProjectOuterJoinTeamMembersOuterJoinAddress() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Project.class);
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        Expression teamMembers = query.getExpressionBuilder().anyOfAllowingNone("teamMembers");
        query.addJoinedAttribute(teamMembers);
        Expression teamMembersAddress = teamMembers.getAllowingNull("address");
        query.addJoinedAttribute(teamMembersAddress);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testProjectJoinTeamMembersOuterJoinAddress() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Project.class);
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        Expression teamMembers = query.getExpressionBuilder().anyOf("teamMembers");
        query.addJoinedAttribute(teamMembers);
        Expression teamMembersAddress = teamMembers.getAllowingNull("address");
        query.addJoinedAttribute(teamMembersAddress);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testProblemReporterProjectJoinTeamMembersJoinAddress() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Project.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("name").equal("Problem Reporter"));
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        Expression teamMembers = query.getExpressionBuilder().anyOf("teamMembers");
        query.addJoinedAttribute(teamMembers);
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
    
    public void testEmployeeJoinProjectsJoinTeamLeaderJoinAddressWhereManagerIsNull() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("manager").isNull());
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        Expression projects = query.getExpressionBuilder().anyOf("projects");
        query.addJoinedAttribute(projects);
        Expression teamLeader = projects.get("teamLeader");
        query.addJoinedAttribute(teamLeader);
        Expression teamLeaderAddress = teamLeader.get("address");
        query.addJoinedAttribute(teamLeaderAddress);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }
    
    public void testProjectOuterJoinTeamLeaderAddressTeamMembersAddressPhonesWhereProjectName() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Project.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("name").equal("Problem Reporting System").
            or(query.getExpressionBuilder().get("name").equal("Bleep Blob")));
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        Expression teamLeader = query.getExpressionBuilder().getAllowingNull("teamLeader");
        query.addJoinedAttribute(teamLeader);
        Expression teamLeaderAddress = teamLeader.getAllowingNull("address");
        query.addJoinedAttribute(teamLeaderAddress);
        Expression teamMembers = query.getExpressionBuilder().anyOfAllowingNone("teamMembers");
        query.addJoinedAttribute(teamMembers);
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
        query.addJoinedAttribute(projects);
        Expression teamLeader = projects.getAllowingNull("teamLeader");
        query.addJoinedAttribute(teamLeader);
        Expression teamLeaderAddress = teamLeader.getAllowingNull("address");
        query.addJoinedAttribute(teamLeaderAddress);
        Expression teamMembers = projects.anyOfAllowingNone("teamMembers");
        query.addJoinedAttribute(teamMembers);
        Expression teamMembersPhones = teamMembers.anyOfAllowingNone("phoneNumbers");
        query.addJoinedAttribute(teamMembersPhones);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

public void testEmployeeJoinManagerAddressOuterJoinManagerAddress() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("lastName").equal("Way").
                                    or(query.getExpressionBuilder().get("lastName").equal("Jones")));
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        Expression manager = query.getExpressionBuilder().get("manager");
        query.addJoinedAttribute(manager);
        query.addJoinedAttribute(manager.get("address"));
        Expression managersManager = manager.getAllowingNull("manager");
        query.addJoinedAttribute(managersManager);

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
        query.setSelectionCriteria(query.getExpressionBuilder().get("id").equal(emp.getId()).and(eb.get("id").equal(addr.getId())));
        
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
        updall.setSelectionCriteria(updall.getExpressionBuilder().get("address").get("id").equal(addr.getId()));
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

        query.setSelectionCriteria(query.getExpressionBuilder().get("id").equal(addr.getId()));
        
        
        Vector result = (Vector)getDbSession().executeQuery(query);
        
        DeleteAllQuery deleteAll = new DeleteAllQuery(PhoneNumber.class);
        deleteAll.setSelectionCriteria(deleteAll.getExpressionBuilder().get("owner").get("id").equal(emp.getId()));
        UnitOfWork uow = getDbSession().acquireUnitOfWork();
        uow.executeQuery(deleteAll);

        UpdateAllQuery updall = new UpdateAllQuery(Employee.class);
        updall.addUpdate("address", null);
        updall.setSelectionCriteria(updall.getExpressionBuilder().get("address").get("id").equal(addr.getId()));
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
        query.setSelectionCriteria(query.getExpressionBuilder().get("id").equal(emp.getId()).and(eb.get("id").equal(addr.getId())));
        
        List list = new ArrayList();
        list.add(query.getExpressionBuilder().get("address"));
        query.addItem("employee", query.getExpressionBuilder(), list);

        list = new ArrayList();
        list.add(eb.anyOf("employees"));
        query.addItem("address", eb, list);
        
        Vector result = (Vector)getDbSession().executeQuery(query);
        
        UpdateAllQuery updall = new UpdateAllQuery(Employee.class);
        updall.addUpdate("address", null);
        updall.setSelectionCriteria(updall.getExpressionBuilder().get("id").equal(emp.getId()));
        UnitOfWork uow = getDbSession().acquireUnitOfWork();
        uow.executeQuery(updall);

        updall = new UpdateAllQuery(Employee.class);
        updall.addUpdate("address", null);
        updall.setSelectionCriteria(updall.getExpressionBuilder().get("address").get("id").equal(addr.getId()));
        uow.executeQuery(updall);
        
        uow.commit();
        
        try {
            Employee emp2 = (Employee)((Object[])result.firstElement())[0];
            Address addr2 = (Address)((Object[])result.firstElement())[1];

            assertTrue("Address were not joined correctly, emp.getAddress() = null", (emp2.getAddress() != null));
            assertTrue("Employees were not joined correctly, addr.employees.size = " + addr.getEmployees().size() + "addr2.employees.size = " + addr2.getEmployees().size(), (addr.getEmployees().size() == addr2.getEmployees().size()));

        } finally {
            testSetup();
        }
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
        query.setSelectionCriteria(query.getExpressionBuilder().get("id").equal(emp.getId()).and(eb.get("id").equal(addr.getId())));
        
        List list = new ArrayList();
        list.add(query.getExpressionBuilder().get("address"));
        query.addItem("employee", query.getExpressionBuilder(), list);
        query.addItem("employee_name", query.getExpressionBuilder().get("firstName"));

        list = new ArrayList();
        list.add(eb.anyOf("employees"));
        query.addItem("address", eb, list);
        
        Vector result = (Vector)getDbSession().executeQuery(query);
        
        UpdateAllQuery updall = new UpdateAllQuery(Employee.class);
        updall.addUpdate("address", null);
        updall.setSelectionCriteria(updall.getExpressionBuilder().get("id").equal(emp.getId()));
        UnitOfWork uow = getDbSession().acquireUnitOfWork();
        uow.executeQuery(updall);

        updall = new UpdateAllQuery(Employee.class);
        updall.addUpdate("address", null);
        updall.setSelectionCriteria(updall.getExpressionBuilder().get("address").get("id").equal(addr.getId()));
        uow.executeQuery(updall);
        
        uow.commit();
        
        Employee emp2 = (Employee)((Object[])result.firstElement())[0];
        Address addr2 = (Address)((Object[])result.firstElement())[2];
        try {
            assertTrue("Address were not joined correctly, emp.getAddress() = null", (emp2.getAddress() != null));
            assertTrue("Employees were not joined correctly, addr.employees.size = " + addr.getEmployees().size() + "addr2.employees.size = " + addr2.getEmployees().size(), (addr.getEmployees().size() == addr2.getEmployees().size()));
            if (!emp2.getFirstName().equals(((Object[])result.firstElement())[1])) {
                fail("Failed to return employee name as an separate item");
            }
    
        } finally {
            testSetup();
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
