/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial API and implementation
package org.eclipse.persistence.testing.framework.wdf.server;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

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
