/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.dbchangenotification;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.framework.oracle.OracleAqHelper;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.tests.returning.TestSystemAdapted;

/**
 * Test Database change notification using JMS on top of Oracle AQ.
 */
public class DbChangeNotificationInternalTestModel extends TestModel {


    protected Session oldSession;

    protected String queueName;
    protected String queueTableName;
    protected boolean useMultipleConsumers;
    protected String aqUser;
    protected String aqPassword;

    public DbChangeNotificationInternalTestModel(boolean useMultipleConsumers) {
        this(useMultipleConsumers ? "notify_topic" : "notify_queue", useMultipleConsumers);
    }

    public DbChangeNotificationInternalTestModel(String queueName, boolean useMultipleConsumers) {
        this(queueName, queueName + "_table", useMultipleConsumers, OracleAqHelper.getAqUser(), OracleAqHelper.getAqPassword());
    }

    public DbChangeNotificationInternalTestModel(String queueName, String queueTableName,
            boolean useMultipleConsumers, String aqUser, String aqPassword) {
        super();
        String type;
        if (useMultipleConsumers) {
            type = " broadcast";
        } else {
            type = " point to point";
        }
        setName(getName() + type);
        setDescription("Test Database change notification using JMS on top of Oracle AQ.");
        this.queueName = queueName;
        this.queueTableName = queueTableName;
        this.useMultipleConsumers = useMultipleConsumers;
        this.aqUser = aqUser;
        this.aqPassword = aqPassword;
    }

    public void addTests() {
        addTest(new DbChangeNotificationTest(aqUser, aqPassword, queueName, useMultipleConsumers, false));
        addTest(new DbChangeNotificationTest(aqUser, aqPassword, queueName, useMultipleConsumers, true));
    }

    public void addRequiredSystems() {
        setupUser();

        addRequiredSystem(new TestSystemAdapted(new EmployeeSystem(), new DbChangeNotificationAdapter(queueName, queueTableName, useMultipleConsumers)));
    }

    protected void setupUser() {
        if (!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("Supports Oracle platform only");
        }

        oldSession = getSession();
        if (!oldSession.getLogin().getUserName().equalsIgnoreCase(aqUser)) {
            // 1 - might need to install aq procesures?
            //     in sqlplus - @@<orahome>\ora92\rdbms\admin\catproc.sql
            //                - as SYSDBA
            // 2 - create a AQ user <aquser> with password <aqpassword>
            //     grant connect, resource , aq_administrator_role to <aquser> identified by <aqpassword>
            //     grant execute on dbms_aq to <aquser>
            //     grant execute on dbms_aqadm to <aquser>
            //     grant execute on dbms_lock to <aquser>
            DatabaseLogin login = (DatabaseLogin)oldSession.getLogin().clone();
            login.setUserName(aqUser);
            login.setPassword(aqPassword);
            DatabaseSession session = new Project(login).createDatabaseSession();
            session.setSessionLog(getSession().getSessionLog());
            session.setLogLevel(getSession().getLogLevel());
            try {
                session.login();
            } catch (Exception exception) {
                throw new TestProblemException(
                        "Database needs to be setup for AQ, needs user " + OracleAqHelper.getAqUser());
            }
            getExecutor().setSession(session);
        }
    }

    public void reset() {
        getExecutor().removeConfigureSystem(new TestSystemAdapted());
        if (oldSession != getSession()) {
            ((DatabaseSession)getSession()).logout();
            getExecutor().setSession(oldSession);
        }
    }
}
