/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.lob;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.testing.framework.*;

public class LOBSessionBrokerTestModel extends TestModel {
    public LOBSessionBrokerTestModel() {
        super();
        setDescription("This suite tests TopLink LOB support with Oracle thin driver and SessionBroker.");
    }

    public void addRequiredSystems() {
        if (!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("WARNING: This model is not supposed to be run on databases other than Oracle.");
        }
        if (!(getSession().getPlatform() instanceof org.eclipse.persistence.platform.database.oracle.Oracle8Platform)) {
            throw new TestWarningException("WARNING: This model requires Oracle8Platform or higher");
        }
        addRequiredSystem(new LOBImageModelSystem());
    }

    public void addTests() {
        addTest(getLOBInsertTestSuite());
        addTest(getLOBUpdateTestSuite());
    }

    protected TestSuite getLOBInsertTestSuite() {
        TestSuite suite = new TestSuite();

        suite.setName("LOB INSERT Test Suite");
        suite.addTest(new LOBInsertTest(ImageSimulator.generateImage(100000, 80000)));
        suite.addTest(new LOBInsertTest(ImageSimulator.generateImage(100000, 80000), true));
        return suite;
    }

    protected TestSuite getLOBUpdateTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("LOB UPDATE Test Suite");
        suite.addTest(new LOBUpdateTest(ImageSimulator.generateImage(100, 100), 120000));
        suite.addTest(new LOBUpdateTest(ImageSimulator.generateImage(100, 100), 120000, true));
        return suite;
    }

    public void setup() {
        if (!((AbstractSession)getSession()).isBroker()) {
            SessionBroker broker = new SessionBroker();
            broker.registerSession(getSession().getName(), getSession());
            // because the only member-session is already connected, no broker.logout() will be required in reset
            broker.setSessionLog(getSession().getSessionLog());
            broker.login();
            getExecutor().setSession(broker);
        }
    }

    public void reset() {
        if (((AbstractSession)getSession()).isBroker()) {
            SessionBroker broker = (SessionBroker)getSession();
            AbstractSession session = broker.getSessionsByName().values().iterator().next();
            session.setBroker(null);
            // no broker.logout() required - the original session should remain connected.
            getExecutor().setSession(session);
        }
    }
}
