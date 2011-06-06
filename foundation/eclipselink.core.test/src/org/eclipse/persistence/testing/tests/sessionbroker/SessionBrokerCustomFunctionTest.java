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
 *     dminsky - initial API and implementation
 ******************************************************************************/ 
package org.eclipse.persistence.testing.tests.sessionbroker;

import java.util.*;

import org.eclipse.persistence.testing.framework.TestCase;

import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.UpdateAllQuery;
import org.eclipse.persistence.exceptions.EclipseLinkException;

public class SessionBrokerCustomFunctionTest extends TestCase {

    protected SessionBroker sessionBroker;
    protected int testType = -1;
    
    public static final int READALLQUERY_TEST = 0;
    public static final int UPDATEALLQUERY_TEST = 1;
    public static final int DELETEALLQUERY_TEST = 2;

    public SessionBrokerCustomFunctionTest(int testType) {
        setDescription("Test using custom functions in a platform subclass using session broker");
        this.testType = testType;
    }

    public void setup() {
        Project project = new EmployeeProject();
        DatabaseLogin login = ServerBrokerTestModel.getLogin1();
        login.setPlatform(new CustomDatabasePlatform());
        project.setLogin(login);
        
        DatabaseSession aSession = project.createDatabaseSession();
        this.sessionBroker = new SessionBroker();
        this.sessionBroker.registerSession("broker1", aSession);
        this.sessionBroker.setSessionLog(getSession().getSessionLog());
        this.sessionBroker.setLogLevel(getSession().getLogLevel());
        this.sessionBroker.login();
    }

    public void test() {
        if (testType == READALLQUERY_TEST) {
            testReadAllQuery();
        } else if (testType == UPDATEALLQUERY_TEST) {
            testUpdateAllQuery(); 
        } else if (testType == DELETEALLQUERY_TEST) {
            testDeleteAllQueryTest();
        } else {
            throwError("test: Invalid test indicator passed to constructor: " + this.testType);
        }
    }
    
    public void testReadAllQuery() {
        try {
            UnitOfWork uow = this.sessionBroker.acquireUnitOfWork();
            
            ReadAllQuery query = new ReadAllQuery(Address.class); 
            ExpressionBuilder builder = query.getExpressionBuilder();
            Vector<Expression> args = new Vector<Expression>();
            args.add(builder.get("country"));
            Expression expression = builder.getFunction(CustomDatabasePlatform.OPERATOR_SELECTOR, args).equal("CANADA");
            query.setSelectionCriteria(expression);
            
            uow.executeQuery(query);
        } catch (EclipseLinkException exception) {
            throwError("testReadAllQuery: failed to use configured platform to acquire custom function: " + exception.getMessage());
        }
    }

    public void testUpdateAllQuery() {
        try {
            DatabaseSession session = (DatabaseSession)this.sessionBroker.getSessionForName("broker1");
            session.beginTransaction();
            
            UnitOfWork uow = session.acquireUnitOfWork();
            
            UpdateAllQuery query = new UpdateAllQuery(Employee.class);
            ExpressionBuilder builder = query.getExpressionBuilder();
            
            Vector<Expression> args = new Vector<Expression>();
            args.add(builder.get("lastName"));
            
            Expression expression = builder.getFunction(CustomDatabasePlatform.OPERATOR_SELECTOR, args).equal("SMITH");
            query.setSelectionCriteria(expression);
            query.addUpdate(builder.get("lastName"), "oneincrediblyunlikelylastname");
            
            uow.executeQuery(query);
            uow.commit();
            
            session.rollbackTransaction();
        } catch (EclipseLinkException exception) {
            throwError("testUpdateAllQuery: failed to use configured platform to acquire custom function: " + exception.getMessage());
        }
    }
    
    public void testDeleteAllQueryTest() {
        try {
            DatabaseSession session = (DatabaseSession)this.sessionBroker.getSessionForName("broker1");
            session.beginTransaction();
            
            UnitOfWork uow = session.acquireUnitOfWork();
            
            DeleteAllQuery query = new DeleteAllQuery(Employee.class);
            ExpressionBuilder builder = query.getExpressionBuilder();
            
            Vector<Expression> args = new Vector<Expression>();
            args.add(builder.get("lastName"));
            
            Expression expression = builder.getFunction(CustomDatabasePlatform.OPERATOR_SELECTOR, args).equal("SMITH");
            query.setSelectionCriteria(expression);
        
            uow.executeQuery(query);
            uow.commit();
            
            session.rollbackTransaction();
        } catch (EclipseLinkException exception) {
            throwError("testDeleteAllQueryTest: failed to use configured platform to acquire custom function: " + exception.getMessage());
        }
    }
    
    public void reset() {
        this.sessionBroker.logout();
    }    
    
}
