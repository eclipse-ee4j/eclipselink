/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     SAP - initial API and implementation
package org.eclipse.persistence.testing.framework.wdf.server;

import java.util.List;
import java.util.Map;

import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;

import org.eclipse.persistence.testing.framework.wdf.ServerInfoHolder;
import org.junit.runner.JUnitCore;

/**
 * Implementation  of the server test runner.
 */

@Stateless(name="ServerTestRunner", mappedName="ServerTestRunner")
@TransactionManagement(TransactionManagementType.BEAN)
@Remote(ServerTestRunner.class)
public class ServerTestRunnerImpl implements ServerTestRunner {

    @Override
    public List<Notification> runTestClass(String className, String dsName, Map<String, String> testProperties) {

        Class<?> clazz;
        try {
            ServerInfoHolder.setServerInfo(dsName, testProperties);
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        JUnitCore core = new JUnitCore();
        CollectNotificationsListener listener = new CollectNotificationsListener();
        core.addListener(listener);
        core.run(clazz);

        return listener.getNotifications();
    }

}
