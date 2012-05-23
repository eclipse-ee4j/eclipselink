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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;
import org.eclipse.persistence.sessions.*;

/**
 * This test is created for bug 2979118: INITIALIZEDESCRIPTORS() THROWS NPE IF CALLED BEFORE LOGIN(),
 * which is a spin-off of bug 2964374: 904 BETA2: IMPORT OF 2.5 PROJECT CAUSES NPE DURING INITIALIZATION.
 * The customer (EMC) would call initializeDescriptors() (though it is marked *INTERNAL*), their project using sequencing,
 * but sequencing used to be initialized later (during login()), so they would get NPE (getSequencing()==null).
 * The cure was to move sequencing initialization to the very beginning of initializeDescriptors() call.
 * This test verifies two things:
 * 1. That the aforementioned NPE doesn't happen any more;
 * 2. That it's possible to change sequencing settings between initializeDescriptors() and login() calls
 * and the change would be used.
 */
public class InitializeDescriptorsBeforeLoginTest extends AutoVerifyTestCase {
    DatabaseSession databaseSession;
    Exception exception;

    public InitializeDescriptorsBeforeLoginTest() {
        setDescription("Verifies that initializeDescriptors() may be called before login() and that sequencing settings may be altered in betwwen these two calls");
    }

    public void setup() {
        DatabaseLogin login = (DatabaseLogin)(getSession().getLogin().clone());

        // make sure we are using table sequencing
        TableSequence ts = new TableSequence();

        // override default table name with platform's, in case current one
        // is not legal for this platform (e.g. SEQUENCE for Symfoware)
        Sequence seq = getSession().getDatasourcePlatform().getDefaultSequence();
        if (seq instanceof TableSequence) {
            ts.setTableName(((TableSequence)seq).getTableName());
        }

        login.setDefaultSequence(ts);
        // any project that uses sequencing will do
        org.eclipse.persistence.sessions.Project project = new EmployeeProject();
        project.setLogin(login);
        databaseSession = project.createDatabaseSession();
        if (!databaseSession.getProject().usesSequencing()) {
            throw new TestWarningException("project doesn't use sequencing");
        }
        databaseSession.setSessionLog(getSession().getSessionLog());
    }

    public void test() {
        try {
            ((DatabaseSessionImpl)databaseSession).initializeDescriptors();
        } catch (Exception ex) {
            exception = ex;
        }
        databaseSession.getSequencingControl().setShouldUseSeparateConnection(true);
        databaseSession.login();
    }

    public void verify() {
        if (exception != null) {
            throw new TestErrorException("initializeDescriptors() failed");
        }
        if (!databaseSession.getSequencingControl().isConnectedUsingSeparateConnection()) {
            throw new TestErrorException("sequencing doesn't use separate connection");
        }
    }

    public void reset() {
        if (databaseSession != null) {
            if (databaseSession.isConnected()) {
                databaseSession.logout();
            }
            databaseSession = null;
        }
    }
}
