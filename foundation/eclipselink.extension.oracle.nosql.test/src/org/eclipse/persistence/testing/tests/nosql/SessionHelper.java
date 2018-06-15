/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     03/30/2016-2.7 Tomas Kraus
//       - 490677: Initial API and implementation.
package org.eclipse.persistence.testing.tests.nosql;

import java.util.Map;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.testing.framework.TestProblemException;

/**
 * Session initialization helper methods.
 * Keeps database configuration properties in static context.
 */
public class SessionHelper {

    /** Logger. */
    private static final SessionLog LOG = AbstractSessionLog.getLog();

    /**
     * Creates {@link DatabaseLogin} with relational database connection specifications.
     * This method is based on {@link org.eclipse.persistence.testing.framework.TestSystem#loadLoginFromProperties()}
     * but uses local properties {@link Map} with NoSQL extensions.
     * @return {@link DatabaseLogin} with relational database connection specifications.
     */
    public static DatabaseLogin createDatabaseLogin() {
        final DatabaseLogin login = new DatabaseLogin();
        login.setDriverClassName(NoSQLProperties.getDBDriver());
        login.setConnectionString(NoSQLProperties.getDBURL());
        login.setUserName(NoSQLProperties.getDBUserName());
        // This avoids encrypting the password, as some tests require it non-encrypted.
        login.setEncryptedPassword(NoSQLProperties.getDBPassword());
        final String platform = NoSQLProperties.getDBPlatform();
        if (platform != null) {
            login.setPlatformClassName(platform);
        }
        return login;
    }

    /**
     * Creates EclipseLink configuration {@link Project} without testing model and with provided connection
     *  specifications.
     * @param login Database connection specifications.
     * @param loaderClass Class used to retrieve {@link ClassLoader}.
     */
    public static Project createProject(final DatasourceLogin login, final Class<?> loaderClass) {
        return new Project(login);
    }

    /**
     * Creates EclipseLink configuration {@link Project} with testing model and provided connection specifications.
     * @param login Database connection specifications.
     * @param loaderClass Class used to retrieve {@link ClassLoader}.
     */
    public static Project createModelProject(final DatasourceLogin login, final Class<?> loaderClass) {
        final Project project = XMLProjectReader.read(ModelHelper.MODEL_FILE, loaderClass.getClassLoader());
        project.setLogin(login);
        return project;
    }

    /**
     * Creates {@link DatabaseSession} from project and connect it to database.
     * {@link DatabaseSession#logout()} must be called before session instance is disposed.
     */
    public static DatabaseSession createDatabaseSession(final Project project) {
        DatabaseSession session = project.createDatabaseSession();
        session.setSessionLog(LOG);
        try {
            session.login();
        } catch (Exception ex) {
            throw new TestProblemException(
                    "Database needs to be setup for AQ, with the user " + NoSQLProperties.getDBUserName(), ex);
        }
        return session;
    }

    /**
     * Creates {@link DatabaseSession} from project and connect it to database.
     * {@link DatabaseSession#logout()} must be called before session instance is disposed.
     */
    public static Server createServerSession(final Project project) {
        Server session = project.createServerSession();
        session.setSessionLog(LOG);
        try {
            session.login();
        } catch (Exception ex) {
            throw new TestProblemException(
                    "Database needs to be setup for AQ, with the user " + NoSQLProperties.getDBUserName(), ex);
        }
        return session;
    }

    /**
     * Execute statement in provided database session.
     * @param session   Database session used to execute statement.
     * @param statement Statement to be executed.
     */
    public static void executeStatement(final DatabaseSession session, final String statement) {
        try {
            session.executeNonSelectingCall(new SQLCall(statement));
        } catch (DatabaseException de) {
            session.getSessionLog().log(SessionLog.INFO, String.format("Error executing: %s", statement));
        } catch (Exception ex) {
            session.getSessionLog().log(SessionLog.WARNING, String.format("Error executing: %s", statement));
        }
    }

}
