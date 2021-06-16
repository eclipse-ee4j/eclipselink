/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
