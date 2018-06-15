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

/**
 * Session bean used to redirect JUnit 4 test execution from client to server
 */
public interface ServerTestRunner {

    /**
     * Redirects test execution from client to server.
     * @param className the name of test class to be executed
     * @param dsName the JNID lookup name of the data source used by the test
     * @param testProperties the test properties
     * @return a list of notifications recorded during test execution
     */
    public List<Notification> runTestClass(String className, String dsName, Map<String, String> testProperties);

}
