/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.proxyauthentication;

import org.eclipse.persistence.testing.framework.TestModel;

/**
 * Test Database change notification using JMS on top of Oracle AQ.
 */
public class ProxyAuthenticationTestModel extends TestModel {
    public ProxyAuthenticationTestModel() {
        super();
        addTest(new ProxyAuthenticationInternalTestModel("org.eclipse.persistence.testing.tests.proxyauthentication.JDBC10_1_0_2ProxyTestHelper"));
        //		addTest(new ProxyAuthenticationInternalTestModel("org.eclipse.persistence.testing.tests.proxyauthentication.OCIProxyTestHelper"));
    }
}
