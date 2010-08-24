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

    public void createTables() {
        session.executeNonSelectingCall(new SQLCall("Create table LOADBUILD (id number(10), lbtimestamp date, lberrors number(10), fatalErrors number(10), loginChoice varchar2(100), os varchar2(100), toplink_version varchar(100), jvm varchar2(100), machine varchar2(100), numberOfTests number(10), lbuserName varchar2(50), primary key (id))"));
        session.executeNonSelectingCall(new SQLCall("Create table RESULT (id number(10), description varchar2(2000), exception varchar2(2000), name varchar2(1000), outcome varchar2(100), test_time number(10), total_time number(10), summaryId number(10), lbuildId number(10), primary key (id))"));
        session.executeNonSelectingCall(new SQLCall("Create table SUMMARY (id number(10), description varchar2(2000), setup_failures number(10), errors number(10), fatalErrors number(10), name varchar2(1000), passed number(10), problems number(10), setupException varchar2(2000), total_time number(10), totalTests number(10), warnings number(10), lbuildId number(10), parentId number(10), primary key (id))"));
        if (session.getPlatform().supportsUniqueKeyConstraints()
                && !session.getPlatform().requiresUniqueConstraintCreationOnTableCreate()) {
            session.executeNonSelectingCall(new SQLCall("ALTER TABLE RESULT ADD CONSTRAINT FK_RESULT_summaryId FOREIGN KEY (summaryId) REFERENCES SUMMARY (id)"));
            session.executeNonSelectingCall(new SQLCall("ALTER TABLE RESULT ADD CONSTRAINT FK_RESULT_lbuildId FOREIGN KEY (lbuildId) REFERENCES LOADBUILD (id)"));
            session.executeNonSelectingCall(new SQLCall("ALTER TABLE SUMMARY ADD CONSTRAINT FK_LOADBUILD_lbuildId FOREIGN KEY (lbuildId) REFERENCES LOADBUILD (id)"));
            session.executeNonSelectingCall(new SQLCall("ALTER TABLE SUMMARY ADD CONSTRAINT FK_LOADBUILD_parentId FOREIGN KEY (parentId) REFERENCES SUMMARY (id)"));
        }
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("LOADBUILD", "IX_LOADBUILD_lbtimestamp", "lbtimestamp")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("LOADBUILD", "IX_LOADBUILD_loginChoice", "loginChoice")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("LOADBUILD", "IX_LOADBUILD_toplink_version", "toplink_version")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("LOADBUILD", "IX_LOADBUILD_machine", "machine")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("LOADBUILD", "IX_LOADBUILD_lbuserName", "lbuserName")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("RESULT", "IX_RESULT_name", "name")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("RESULT", "IX_RESULT_summaryId", "summaryId")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("RESULT", "IX_RESULT_lbuildId", "lbuildId")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("SUMMARY", "IX_SUMMARY_lbuildId", "lbuildId")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("SUMMARY", "IX_SUMMARY_name", "name")));
        session.executeNonSelectingCall(new SQLCall(session.getPlatform().buildCreateIndex("SUMMARY", "IX_SUMMARY_parentId", "parentId")));
        session.executeNonSelectingCall(new SQLCall("create sequence result_seq increment by 500 start with 1000"));
        session.executeNonSelectingCall(new SQLCall("create sequence RESULTSUM_SEQ increment by 500 start with 1000"));
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
        query.addOrdering(query.getExpressionBuilder().get("loadBuildSummary").get("timestamp").ascending());
        query.addJoinedAttribute("loadBuildSummary");
        return (Vector)session.executeQuery(query);
    }

    /**
     * Read all the tests, join the load build result.
     */
    public Vector readAllTests(org.eclipse.persistence.expressions.Expression expression) {
        ReadAllQuery query = new ReadAllQuery(TestResult.class, expression);
        query.addAscendingOrdering("name");
        query.addOrdering(query.getExpressionBuilder().get("loadBuildSummary").get("timestamp").ascending());
        query.addJoinedAttribute("loadBuildSummary");
        return (Vector)session.executeQuery(query);
    }

    /**
     * Save load build  and log messages toSsystem.Out
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
     * Save load build  and log messages to a  writer
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
