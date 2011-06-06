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
package org.eclipse.persistence.testing.tests.sessionbroker;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.platform.database.DB2Platform;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;


/**
 * Tests that the SessionBroker uses the correct platform to generate SQL.
 */
public class SessionBrokerPlatformTest extends TransactionalTestCase {
    public DatabasePlatform platform;

    public void setup() {
        super.setup();
        // CR2114; LargeProject.class passed as an argument
        this.platform = (DatabasePlatform)((AbstractSession)getSession()).getPlatform(LargeProject.class);
        DatabasePlatform newPlatform = new DB2Platform();
        newPlatform.setUsesNativeSQL(true);
        getSession().getLogin().setPlatform(newPlatform);
    }

    public void test() {
        try {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            LargeProject project = (LargeProject)uow.readObject(LargeProject.class);
            project.setDescription("Anything but what it was");
            project.setMilestoneVersion(new java.sql.Timestamp(System.currentTimeMillis()));
            uow.commit();
        } catch (DatabaseException exception) {
            throw new TestErrorException("Failed to use the Session Platform to generate SQL.  Instead used Broker platform");
        }
    }

    public void reset() {
        super.reset();
        getSession().getLogin().setPlatform(this.platform);
    }
}
