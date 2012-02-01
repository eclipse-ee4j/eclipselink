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
