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
package org.eclipse.persistence.testing.framework;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.queries.*;

/**
 * <b>Purpose</b>: Provide write/read load builds functionalities<p>
 * <b>Description</b>:<p>
 * <b>Responsibilities</b>:<ul>
 * <li>
 * </ul>
 * @author Steven Vo
 */
public class LoadBuildSystem {
    public static LoadBuildSummary loadBuild = new LoadBuildSummary();
    public static LoadBuildSystem system;
    private DatabaseSession session;

    public static LoadBuildSummary getSummary() {
        return loadBuild;
    }

    public static LoadBuildSystem getSystem() {
        if (system == null) {
            system = new LoadBuildSystem();
            system.login();
        }
        return system;
    }

    public static void main(String[] args) {
        LoadBuildSystem system = new LoadBuildSystem();
        system.login();
        //Don't run this!
        //**system.dropTables();**
        //**system.createTables();**
        //system.populateSampleData();
        system.logout();
    }

    public LoadBuildSystem() {
        session = new LoadBuildProject().createDatabaseSession();
    }

    public DatabaseSession getSession() {
        return session;
    }

    /**
     *
     * @return boolean
     */
    public boolean isConnected() {
        return session.isConnected();
    }

    public void login() {
        session.getLogin().useNativeSQL();
        session.getLogin().useBatchWriting();
        session.getLogin().bindAllParameters();
        session.getLogin().cacheAllStatements();
        session.getLogin().setMaxBatchWritingSize(50);
        session.dontLogMessages();
        //session.logMessages();
        session.login();
    }

    public void dropTables() {
        try {
            session.executeNonSelectingCall(new SQLCall("drop table RESULT"));
            session.executeNonSelectingCall(new SQLCall("drop table SUMMARY"));
            session.executeNonSelectingCall(new SQLCall("drop table LOADBUILD"));
            session.executeNonSelectingCall(new SQLCall("drop sequence result_seq"));
            session.executeNonSelectingCall(new SQLCall("drop sequence RESULTSUM_SEQ"));
        } catch (Exception ignore) {}
    }

    /**
     * Creates MySQL tables used to store performance data.
     */
    public void createTables() {
        session.executeNonSelectingCall(new SQLCall("Create table LOADBUILD (\n" +
                "id int not null auto_increment, \n" +
                "lbtimestamp date, \n" +
                "lberrors int, \n" +
                "fatalErrors int, \n" +
                "loginChoice varchar(100), \n" +
                "os varchar(100), \n" +
                "toplink_version varchar(100), \n" +
                "jvm varchar(100), \n" +
                "machine varchar(100), \n" +
                "numberOfTests int, \n" +
                "lbuserName varchar(50), \n" +
                "primary key (id))"));

        session.executeNonSelectingCall(new SQLCall("Create table RESULT (\n" +
                "id int not null auto_increment, \n" +
                "description varchar(2000), \n" +
                "exception varchar(2000), \n" +
                "name varchar(1000), \n" +
                "outcome varchar(100), \n" +
                "test_time int, \n" +
                "total_time int, \n" +
                "summaryId int, \n" +
                "lbuildId int, \n" +
                "primary key (id))"));

        session.executeNonSelectingCall(new SQLCall("Create table SUMMARY (\n" +
                "id int not null auto_increment, \n" +
                "description varchar(2000), \n" +
                "setup_failures int, \n" +
                "errors int, \n" +
                "fatalErrors int, \n" +
                "name varchar(1000), \n" +
                "passed int, \n" +
                "problems int, \n" +
                "setupException varchar(2000), \n" +
                "total_time int, \n" +
                "totalTests int, \n" +
                "warnings int, \n" +
                "lbuildId int, \n" +
                "parentId int, \n" +
                "primary key (id))"));

        if (session.getPlatform().supportsUniqueKeyConstraints()
                && !session.getPlatform().requiresUniqueConstraintCreationOnTableCreate()) {
            session.executeNonSelectingCall(new SQLCall("ALTER TABLE RESULT ADD CONSTRAINT FK_RESULT_summaryId FOREIGN KEY (summaryId) REFERENCES SUMMARY (id)"));
            session.executeNonSelectingCall(new SQLCall("ALTER TABLE RESULT ADD CONSTRAINT FK_RESULT_lbuildId FOREIGN KEY (lbuildId) REFERENCES LOADBUILD (id)"));
            session.executeNonSelectingCall(new SQLCall("ALTER TABLE SUMMARY ADD CONSTRAINT FK_LOADBUILD_lbuildId FOREIGN KEY (lbuildId) REFERENCES LOADBUILD (id)"));
            session.executeNonSelectingCall(new SQLCall("ALTER TABLE SUMMARY ADD CONSTRAINT FK_LOADBUILD_parentId FOREIGN KEY (parentId) REFERENCES SUMMARY (id)"));
        }
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("LOADBUILD", "IX_LOADBUILD_lbtimestamp", "", false, "lbtimestamp")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("LOADBUILD", "IX_LOADBUILD_loginChoice", "", false, "loginChoice")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("LOADBUILD", "IX_LOADBUILD_toplink_version", "", false, "toplink_version")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("LOADBUILD", "IX_LOADBUILD_machine", "", false, "machine")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("LOADBUILD", "IX_LOADBUILD_lbuserName", "", false, "lbuserName")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("RESULT", "IX_RESULT_name", "", false, "name")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("RESULT", "IX_RESULT_summaryId", "", false, "summaryId")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("RESULT", "IX_RESULT_lbuildId", "", false, "lbuildId")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("SUMMARY", "IX_SUMMARY_lbuildId", "", false, "lbuildId")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("SUMMARY", "IX_SUMMARY_name", "", false, "name")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("SUMMARY", "IX_SUMMARY_parentId", "", false, "parentId")));
    }

    public void logout() {
        session.logout();
    }

    public void populateSampleData() {
        ReadAllQuery query = new ReadAllQuery(LoadBuildSummary.class);
        query.addBatchReadAttribute("results");
        query.addBatchReadAttribute("summaries");
        query.addBatchReadAttribute(query.getExpressionBuilder().get("summaries").get("results"));
        List list = (List)session.executeQuery(query);
        Iterator summaries = list.iterator();
        System.out.println("Size: " + list.size());
        UnitOfWork uow = session.acquireUnitOfWork();
        while (summaries.hasNext()) {
            LoadBuildSummary summary = (LoadBuildSummary)summaries.next();
            for (Iterator iterator = summary.getSummaries().iterator(); iterator.hasNext(); ) {
                ((TestResultsSummary)iterator.next()).getResults();
            }
            //for (int index = 0; index < 10; index++) {
                CopyGroup group = new CopyGroup();
                group.setShouldResetPrimaryKey(true);
                LoadBuildSummary summaryCopy = (LoadBuildSummary)session.copy(summary, group);
                summaryCopy.id = 0;
                uow.registerObject(summaryCopy);
            //}
        }
        uow.commit();
        list = session.readAllObjects(LoadBuildSummary.class);
        System.out.println("Size: " + list.size());
    }

    public void populate() {
        if (loadBuild == null) {
            return;
        }
        UnitOfWork uow = session.acquireUnitOfWork();
        loadBuild.timestamp = new java.sql.Timestamp(System.currentTimeMillis());
        loadBuild.initializeLoadBuild();
        loadBuild.computeNumberOfTestsAndErrors();
        uow.registerObject(loadBuild);
        uow.commit();
    }

    /**
     * Read all the test summaries, join the load build result.
     */
    public Vector readAllTestModelSummaries(org.eclipse.persistence.expressions.Expression expression) {
        ReadAllQuery query = new ReadAllQuery(TestResultsSummary.class, expression);
        query.addAscendingOrdering("name");
        //query.addOrdering(query.getExpressionBuilder().get("loadBuildSummary").get("timestamp").ascending());
        query.addJoinedAttribute("loadBuildSummary");
        return (Vector)session.executeQuery(query);
    }

    /**
     * Read all the tests, join the load build result.
     */
    public Vector readAllTests(org.eclipse.persistence.expressions.Expression expression) {
        ReadAllQuery query = new ReadAllQuery(TestResult.class, expression);
        query.addAscendingOrdering("name");
        //query.addOrdering(query.getExpressionBuilder().get("loadBuildSummary").get("timestamp").ascending());
        query.addJoinedAttribute("loadBuildSummary");
        return (Vector)session.executeQuery(query);
    }

    /**
     * Save load build  and log messages to System.out
     */
    public void saveLoadBuild() {
        boolean success = true;
        try {
            login();
            populate();
        } catch (Exception e) {
            System.out.println("Error occurred during saving of test results to database.");
            e.printStackTrace();
            System.out.println("Saving of test results failed.");
            success = false;
        }
        if (success) {
            System.out.println("Saving of test results successful.");
        }
        loadBuild = new LoadBuildSummary();
        logout();
    }

    /**
     * Save load build  and log messages to a Writer
     */
    public void saveLoadBuild(java.io.Writer log) {
        try {
            session.setLogLevel(SessionLog.FINE);
            session.setLog(log);
            login();
            populate();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        loadBuild = new LoadBuildSummary();
        logout();
    }
}
