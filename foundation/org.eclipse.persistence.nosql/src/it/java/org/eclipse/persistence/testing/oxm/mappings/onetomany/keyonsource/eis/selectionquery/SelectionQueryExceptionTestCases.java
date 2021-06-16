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
package org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.selectionquery;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.selectionquery.SelectionQueryBadProject;

public class SelectionQueryExceptionTestCases extends OXTestCase {
    public SelectionQueryExceptionTestCases(String name) throws Exception {
        super(name);
    }

    public void testCreateSessionException() {
        SelectionQueryBadProject project = new SelectionQueryBadProject();
        boolean caughtException = false;

        DatabaseSession session = project.createDatabaseSession();
        session.login();

        // This is now valid, as EIS now support query generation from expressions (dependent on the platform).
        //assertTrue("A ValidationException was not thrown upon login as expected.", caughtException);
    }
}
