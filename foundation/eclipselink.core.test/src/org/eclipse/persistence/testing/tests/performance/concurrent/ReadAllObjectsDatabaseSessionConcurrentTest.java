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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.performance.concurrent;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the concurrency of read all.
 * This test must be run on a multi-CPU machine to be meaningful.
 */
public class ReadAllObjectsDatabaseSessionConcurrentTest extends ConcurrentPerformanceComparisonTest {
    protected ThreadLocal session;

    public ReadAllObjectsDatabaseSessionConcurrentTest() {
        setDescription("This tests the concurrency of read-all.");
    }

    /**
     * Set thread local.
     */
    public void setup() {
        super.setup();
        session = new ThreadLocal();
    }

    /**
     * Find all employees
     */
    public void runTask() throws Exception {
        DatabaseSession dbSession = (DatabaseSession)session.get();
        if (dbSession == null) {
            dbSession = new EmployeeProject().createDatabaseSession();
            dbSession.setLogin(getSession().getLogin());
            dbSession.login();
            session.set(dbSession);
        } else {
            dbSession.readAllObjects(Employee.class);
        }
    }

    /**
     * Logout the main thread session, let the other garbage collect (hopefully).
     */
    public void reset() {
        super.reset();
        DatabaseSession dbSession = (DatabaseSession)session.get();
        dbSession.logout();
        session.set(null);
        session = null;
    }
}
