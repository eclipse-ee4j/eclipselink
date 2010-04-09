/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.framework.wdf.server;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.eclipse.persistence.testing.framework.wdf.ServerInfoHolder;
import org.junit.runner.JUnitCore;

/**
 * Implementation  of the server test runner. 
 */

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
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
