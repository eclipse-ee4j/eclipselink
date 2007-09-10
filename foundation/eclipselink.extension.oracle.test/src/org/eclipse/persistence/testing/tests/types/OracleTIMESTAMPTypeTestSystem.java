/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
        }
    }
    
}
