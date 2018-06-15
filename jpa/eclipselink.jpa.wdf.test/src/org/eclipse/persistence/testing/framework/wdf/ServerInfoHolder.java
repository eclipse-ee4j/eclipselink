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
package org.eclipse.persistence.testing.framework.wdf;

import java.util.Collections;
import java.util.Map;

/**
 * Static class to hold state shared between ServerTestRunner and tests
 */
public class ServerInfoHolder {

    @SuppressWarnings("unchecked")
    private static Map<String, String> testProperties = Collections.EMPTY_MAP;
    private static String dataSourceName;
    private static boolean isOnServer = false;

    /**
     * Get the set of test properties
     * @return the set of test properties
     */
    public static synchronized Map<String, String> getTestProperties() {
        return testProperties;
    }



    // prevent instantiation
    private ServerInfoHolder() {

    }

    /**
     * @return if the test is running on a JEE server, or in JSE.
     */
    public static boolean isOnServer() {
        return isOnServer;
    }


    /**
     * Get the JNDI lookup name of the data source used by the tests
     * @return the JNDI lookup name of the data source used by the tests
     */
    public synchronized static String getDataSourceName() {
        return dataSourceName;
    }

    /**
     * set info needed to execute the tests on the server
     * @param dsName the JNDI lookup name of the data source used by the tests
     * @param properties the test properties
     */
    public static synchronized void setServerInfo(String dsName, Map<String, String> properties) {
        isOnServer = true;
        dataSourceName = dsName;
        testProperties = properties;
    }

}
