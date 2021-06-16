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
package org.eclipse.persistence.testing.tests.types;

import org.eclipse.persistence.platform.database.oracle.Oracle9Platform;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TestSystem;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

public class OracleTIMESTAMPTypeTestSystem extends TestSystem {

    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);

        if (session.getPlatform() instanceof Oracle9Platform) {
            schemaManager.replaceObject(TIMESTAMPDirectToFieldTester.tableDefinition(session));
            schemaManager.replaceObject(TIMESTAMPTypeConversionTester.tableDefinition(session));
            schemaManager.replaceObject(CalendarToTSTZWithoutSessionTZTest.tableDefinition(session));
            schemaManager.replaceObject(TIMESTAMPTZOwner.tableDefinition());
            schemaManager.replaceObject(CalendarToTSTZWithBindingTest.tableDefinition());
            schemaManager.replaceObject(CalendarDaylightSavingsTest.tableDefinition());
        }
    }

}
