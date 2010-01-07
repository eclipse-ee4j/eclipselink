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


package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.util.HashMap;
import java.util.Vector;

import junit.extensions.TestSetup;
import junit.framework.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionMath;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.queries.UpdateAllQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.*;
import org.eclipse.persistence.testing.framework.UpdateAllQueryTestHelper;
 
public class UpdateAllQueryAdvancedJunitTest extends JUnitTestCase {
        
    static protected Class[] classes = {Employee.class, Address.class, PhoneNumber.class, Project.class};
    static protected Vector[] objectVectors = {null, null, null, null};
    
    static protected EmployeePopulator populator = new EmployeePopulator();

    public UpdateAllQueryAdvancedJunitTest() {
        super();
    }
    
    public UpdateAllQueryAdvancedJunitTest(String name) {
        super(name);
    }
    
    public void setUp() {
        clearCache();
        super.setUp();
        if(!compare()) {
            clear();
            populate();
        }
    }
    
    protected static DatabaseSession getDbSession() {
        return getServerSession();   
    }
    
    protected static UnitOfWork acquireUnitOfWork() {
        return getDbSession().acquireUnitOfWork();   
    }
    
    protected static void clear() {
        UnitOfWork uow = acquireUnitOfWork();

        UpdateAllQuery updateEmployees = new UpdateAllQuery(Employee.class);
        updateEmployees.addUpdate("manager", null);
        updateEmployees.addUpdate("address", null);
        uow.executeQuery(updateEmployees);
    
        UpdateAllQuery updateProjects = new UpdateAllQuery(Project.class);
        updateProjects.addUpdate("teamLeader", null);
        uow.executeQuery(updateProjects);
    
        uow.executeQuery(new DeleteAllQuery(PhoneNumber.class));
        uow.executeQuery(new DeleteAllQuery(Address.class));
        uow.executeQuery(new DeleteAllQuery(Employee.class));
        uow.executeQuery(new DeleteAllQuery(Project.class));

        uow.commit();
        clearCache();
    }
    
    protected static void populate() {
        populator.buildExamples();
        populator.persistExample(getDbSession());
        clearCache();
        for(int i=0; i < classes.length; i++) {
            objectVectors[i] = getDbSession().readAllObjects(classes[i]);
        }
        clearCache();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(UpdateAllQueryAdvancedJunitTest.class);
        
        return new TestSetup(suite) {
            protected void setUp(){               
                new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());
            }

            protected void tearDown() {
                clearCache();
            }
        };
    }
    
    public static void testFirstNamePrefixBLAForAll() {
        ExpressionBuilder builder = new ExpressionBuilder();
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class);        
        updateQuery.addUpdate("firstName", Expression.fromLiteral("'BLA'", null).concat(builder.get("firstName")));
        updateAllQueryInternal(updateQuery);
    }    
    
    public static void testFirstNamePrefixBLAForSalary() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression selectionExpression = builder.get("salary").lessThan(20000);
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class, selectionExpression);
        updateQuery.addUpdate("firstName", Expression.fromLiteral("'BLA'", null).concat(builder.get("firstName")));
        updateAllQueryInternal(updateQuery);
    }    
    
    public static void testDoubleSalaryForAll() {
        ExpressionBuilder builder = new ExpressionBuilder();
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class);
        updateQuery.addUpdate("salary", ExpressionMath.multiply(builder.get("salary"), new Integer(2)));
        updateAllQueryInternal(updateQuery);
    }    
    
    public static void testDoubleSalaryForSalary() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression selectionExpression = builder.get("salary").lessThan(20000);
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class, selectionExpression);
        updateQuery.addUpdate("salary", ExpressionMath.multiply(builder.get("salary"), new Integer(2)));
        updateAllQueryInternal(updateQuery);
    }    
    
    public static void testFirstNamePrefixBLADoubleSalaryForAll() {
        ExpressionBuilder builder = new ExpressionBuilder();
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class);
        updateQuery.addUpdate("firstName", Expression.fromLiteral("'BLA'", null).concat(builder.get("firstName")));
        updateQuery.addUpdate("salary", ExpressionMath.multiply(builder.get("salary"), new Integer(2)));
        updateAllQueryInternal(updateQuery);
    }    
    
    public static void testFirstNamePrefixBLADoubleSalaryForSalary() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression selectionExpression = builder.get("salary").lessThan(20000);
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class, selectionExpression);
        updateQuery.addUpdate("firstName", Expression.fromLiteral("'BLA'", null).concat(builder.get("firstName")));
        updateQuery.addUpdate("salary", ExpressionMath.multiply(builder.get("salary"), new Integer(2)));
        updateAllQueryInternal(updateQuery);
    }    
    
    public static void testFirstNamePrefixBLADoubleSalaryForSalaryForFirstName() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression selectionExpression = builder.get("salary").lessThan(20000).and(builder.get("firstName").like("J%"));
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class, selectionExpression);
        updateQuery.addUpdate("firstName", Expression.fromLiteral("'BLA'", null).concat(builder.get("firstName")));
        updateQuery.addUpdate("salary", ExpressionMath.multiply(builder.get("salary"), new Integer(2)));
        updateAllQueryInternal(updateQuery);
    }    
    
    public static void testAssignManagerName() {
        ExpressionBuilder builder = new ExpressionBuilder();    
        Expression selectionExpression = builder.get("manager").notNull();
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class, selectionExpression);
        updateQuery.addUpdate("firstName", builder.get("manager").get("firstName"));
        updateAllQueryInternal(updateQuery);
    }    
    
    public static void testAssignNullToAddress() {
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class);
        updateQuery.addUpdate("address", null);
        updateAllQueryInternal(updateQuery);
    }    
    
    public static void testAssignObjectToAddress() {
        Address address = new Address();
        address.setCountry("Canada");
        address.setProvince("Ontario");
        address.setCity("Ottawa");
        address.setStreet("O'Connor");
        UnitOfWork uow = acquireUnitOfWork();
        uow.registerNewObject(address);
        uow.commit();
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class);
        updateQuery.addUpdate("address", address);
        updateAllQueryInternal(updateQuery);
    }    
    
    public static void testAssignExpressionToAddress() {
        ExpressionBuilder builder = new ExpressionBuilder();    
        Expression selectionExpression = builder.get("manager").notNull();
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class, selectionExpression);
        updateQuery.addUpdate("address", builder.get("manager").get("address"));
        updateAllQueryInternal(updateQuery);
    }    
    
    public static void testAggregate() {
        ExpressionBuilder builder = new ExpressionBuilder();    
        Expression selectionExpression = builder.get("manager").notNull();
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class, selectionExpression);
        updateQuery.addUpdate(builder.get("period").get("startDate"), builder.get("period").get("endDate"));
        updateQuery.addUpdate(builder.get("period").get("endDate"), builder.get("period").get("startDate"));
        updateAllQueryInternal(updateQuery);
    }
    
    protected static void updateAllQueryInternal(Class referenceClass, HashMap updateClauses, Expression selectionExpression) {
        String errorMsg = UpdateAllQueryTestHelper.execute(getDbSession(), referenceClass, updateClauses, selectionExpression);
        if(errorMsg != null) {
            fail(errorMsg);
        }
    }
    
    protected static void updateAllQueryInternal(UpdateAllQuery uq) {
        String errorMsg = UpdateAllQueryTestHelper.execute(getDbSession(), uq);
        if(errorMsg != null) {
            fail(errorMsg);
        }
    }

    protected static boolean compare() {
        for(int i=0; i < classes.length; i++) {
            if(!compare(i)) {
                return false;
            }
        }
        return true;
    }

    protected static boolean compare(int i) {
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
}
