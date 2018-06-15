/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.distributedservers.rcm.jms;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;

import org.eclipse.persistence.testing.tests.distributedservers.DistributedServer;
import org.eclipse.persistence.testing.tests.distributedservers.rcm.broadcast.BroadcastDistributedServersModel;
import org.eclipse.persistence.testing.framework.TestWarningException;

public class JMSRCMDistributedServersModel extends BroadcastDistributedServersModel {

    public JMSRCMDistributedServersModel() {
        setDescription("Tests cache synchronization with JMSRCM.");

        // maximum wait time to give a chance to remote command recipient to process it before verification.
        timeToWaitBeforeVerify = 10000;
    }

    /** to setup aquser in Oracle db
        1 - login as sysdba (default password is password)
        - login as scott tiger
        connect sys/password@james as sysdba

        2 - might need to install aq procesures?
        - in sqlplus - @@<orahome>\ora92\rdbms\admin\catproc.sql

        3 - create aquser with aquser password
        grant connect, resource , aq_administrator_role to aquser identified by aquser
        grant execute on dbms_aq to aquser
        grant execute on dbms_aqadm to aquser
        grant execute on dbms_aqjms to aquser
        grant execute on dbms_aqjms_internal to aquser
        connect aquser/aquser
     */
    public void setup() {
        if (!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("Supports Oracle platform only: uses Oracle AQ");
        }
        getHelper().setConnectionStringFromSession((org.eclipse.persistence.internal.sessions.AbstractSession)getSession());
        super.setup();
    }

    /**
     * Factory method for a DistributedServer.
     */
    public DistributedServer createDistributedServer(Session session) {
        return new JMSRCMDistributedServer((DatabaseSession)session);
    }

    protected JMSSetupHelper getHelper() {
        return JMSSetupHelper.getHelper();
    }
}
