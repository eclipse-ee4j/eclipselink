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
package org.eclipse.persistence.testing.tests.inheritance;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier; 
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.UpdateAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.queries.ReadAllQuery;

import org.eclipse.persistence.testing.framework.JoinedAttributeTestHelper;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestSuite;

import org.eclipse.persistence.testing.models.inheritance.*;

public class STI_JoinedAttributeTest extends TestCase {
        
    // If the flag is set to true then setup method ensures
    // that the objects in db are exactly those created by
    // STI_EmployeePopulator, and nothing else.
    static protected boolean eachTestShouldEnsurePopulation = false;
    
    // the following static variables are used only in case eachTestShouldEnsurePopulation==true
    static protected Class[] classes = {STI_Employee.class, STI_Project.class};
    static protected Vector[] objectVectors = {null, null};
    static protected STI_EmployeePopulator populator = new STI_EmployeePopulator();

    // The name should start with "test" prefix,
    // it coinsides with the method name the test should use.
    // For instance "testProjectJoinTeamMembers" causes the test's test
    // method to invoke testProjectJoinTeamMembers method.
    public STI_JoinedAttributeTest(String name) {
        super();
        setName(name);
    }
    
    // Junit-style trick: this method creates a TestSuite which contains
    // a TestCase for each method with the name starting with "test".
    public static TestSuite getTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("STI_JoinedAttributeTest");
        suite.setDescription("This suite tests join attribute on a Single Table Inheritance version of Employee/Project.");
        Method[] methods= STI_JoinedAttributeTest.class.getDeclaredMethods();
        for (int i= 0; i < methods.length; i++) {
            if(isPublicTestMethod(methods[i])) {
                suite.addTest(new STI_JoinedAttributeTest(methods[i].getName()));
            }
        }
        return suite;
    }
    
	// stolen from junit.framework.TestSuite
    private static boolean isPublicTestMethod(Method m) {
		return isTestMethod(m) && Modifier.isPublic(m.getModifiers());
	 }
	 
	// stolen from junit.framework.TestSuite
	private static boolean isTestMethod(Method m) {
		String name= m.getName();
		Class[] parameters= m.getParameterTypes();
		Class returnType= m.getReturnType();
		return parameters.length == 0 && name.startsWith("test") && returnType.equals(Void.TYPE);
	 }
     
    // This method is designed to make sure that the tests always work in the same environment:
    // db has all the objects produced by populate method - and no other objects of the relevant classes.
    // In order to enforce that the first test populates the db and caches the objects in static collections,
    // the following test reads all the objects from the db, compares them with the cached ones - if they are the
    // same (the case if the tests run directly one after another) then no population occurs.
    // Switched on/off with eachTestShouldEnsurePopulation flag.
    protected void setup() throws Throwable {
        clearCache();
        if(eachTestShouldEnsurePopulation) {
            if(!compare()) {
                clear();
                populate();
            }
            clearCache();
        }
    }
    
    // executes the method with the same name as the test
    protected void test() throws Exception {
        Method method = this.getClass().getDeclaredMethod(getName(), new Class[]{});
        method.invoke(this, new Object[] {});
    }
    
    public void reset() {
        clearCache();
    }
        
    protected void clear() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        UpdateAllQuery updateEmployees = new UpdateAllQuery(STI_Employee.class);
        updateEmployees.addUpdate("manager", null);
        uow.executeQuery(updateEmployees);
    
        UpdateAllQuery updateProjects = new UpdateAllQuery(STI_Project.class);
        updateProjects.addUpdate("teamLeader", null);
        uow.executeQuery(updateProjects);
    
        uow.executeQuery(new DeleteAllQuery(STI_Employee.class));
        uow.executeQuery(new DeleteAllQuery(STI_Project.class));

        uow.commit();
        clearCache();
    }
    
    protected void populate() {
        populator.buildExamples();
        populator.persistExample(getSession());
        clearCache();
        for(int i=0; i < classes.length; i++) {
            objectVectors[i] = getSession().readAllObjects(classes[i]);
        }
    }
    
    public void testProjectJoinTeamMembers() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(STI_Project.class);
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        query.addJoinedAttribute(query.getExpressionBuilder().anyOf("teamMembers"));

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            failTest(errorMsg);
        }
    }
    
    public void testProjectJoinTeamLeaderWhereTeamLeaderNotNull() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(STI_Project.class);
        Expression teamLeader = query.getExpressionBuilder().get("teamLeader");
        query.setSelectionCriteria(teamLeader.notNull());
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        query.addJoinedAttribute(teamLeader);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            failTest(errorMsg);
        }
    }
    
    public void testProjectOuterJoinTeamMembers() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(STI_Project.class);
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        Expression teamMembers = query.getExpressionBuilder().anyOfAllowingNone("teamMembers");
        query.addJoinedAttribute(teamMembers);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            failTest(errorMsg);
        }
    }
    
    public void testProblemReporterProjectJoinTeamMembers() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(STI_Project.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("name").equal("Problem Reporter"));
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        Expression teamMembers = query.getExpressionBuilder().anyOf("teamMembers");
        query.addJoinedAttribute(teamMembers);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            failTest(errorMsg);
        }
    }
    
    public void testEmployeeJoinProjects() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(STI_Employee.class);
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        query.addJoinedAttribute(query.getExpressionBuilder().anyOf("projects"));

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            failTest(errorMsg);
        }
    }
    
    public void testEmployeeJoinProjectsJoinTeamLeaderWhereManagerIsNull() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(STI_Employee.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("manager").isNull());
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        Expression projects = query.getExpressionBuilder().anyOf("projects");
        query.addJoinedAttribute(projects);
        Expression teamLeader = projects.get("teamLeader");
        query.addJoinedAttribute(teamLeader);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            failTest(errorMsg);
        }
    }
    
    public void testProjectOuterJoinTeamLeaderTeamMembersWhereProjectName() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(STI_Project.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("name").equal("Problem Reporting System").
            or(query.getExpressionBuilder().get("name").equal("Bleep Blob")));
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        Expression teamLeader = query.getExpressionBuilder().getAllowingNull("teamLeader");
        query.addJoinedAttribute(teamLeader);
        Expression teamMembers = query.getExpressionBuilder().anyOfAllowingNone("teamMembers");
        query.addJoinedAttribute(teamMembers);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            failTest(errorMsg);
        }
    }
    
    public void testEmployeeOuterJoinProjectsTeamLeader() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(STI_Employee.class);
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        Expression projects = query.getExpressionBuilder().anyOfAllowingNone("projects");
        query.addJoinedAttribute(projects);
        Expression teamLeader = projects.getAllowingNull("teamLeader");
        query.addJoinedAttribute(teamLeader);
        Expression teamMembers = projects.anyOfAllowingNone("teamMembers");
        query.addJoinedAttribute(teamMembers);

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            failTest(errorMsg);
        }
    }
    
    protected String executeQueriesAndCompareResults(ObjectLevelReadQuery controlQuery, ObjectLevelReadQuery queryWithJoins) {
        return JoinedAttributeTestHelper.executeQueriesAndCompareResults(controlQuery, queryWithJoins, (AbstractSession)getSession());
    }

    protected void failTest(String errorMsg) {
        throw new TestErrorException(errorMsg);        
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
        Vector currentVector = getSession().readAllObjects(classes[i]);
        
        if(currentVector.size() != objectVectors[i].size()) {
            return false;
        }
        ClassDescriptor descriptor = getSession().getDescriptor(classes[i]);
        for(int j=0; j < currentVector.size(); j++) {
            Object obj1 = objectVectors[i].elementAt(j);
            Object obj2 = currentVector.elementAt(j);
            if(!descriptor.getObjectBuilder().compareObjects(obj1, obj2, (org.eclipse.persistence.internal.sessions.AbstractSession)getSession())) {
                return false;
            }
        }
        return true;
    }

    protected void clearCache() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
