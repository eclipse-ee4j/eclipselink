/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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

        try {
            DatabaseSession session = project.createDatabaseSession();
            session.login();
        } catch (org.eclipse.persistence.exceptions.ValidationException vex) {
            caughtException = true;
        }

        assertTrue("A ValidationException was not thrown upon login as expected.", caughtException);
    }
}