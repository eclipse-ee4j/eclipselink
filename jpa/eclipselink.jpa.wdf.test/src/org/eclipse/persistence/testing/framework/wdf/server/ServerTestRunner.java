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
