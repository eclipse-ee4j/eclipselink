/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.nosql.sdk;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
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

    /** Database model descriptor file. */
    public static final String MODEL_FILE = "org/eclipse/persistence/testing/models/order/eis/nosql/sdk/order-project.xml";

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
        final Project project = XMLProjectReader.read(MODEL_FILE, loaderClass.getClassLoader());
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
                    "Database needs to be setup for Oracle NoSQL database.", ex);
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
                    "Database needs to be setup for Oracle NoSQL database.", ex);
        }
        return session;
    }

}
