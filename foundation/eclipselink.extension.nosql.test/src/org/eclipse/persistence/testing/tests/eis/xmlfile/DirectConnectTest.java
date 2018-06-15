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
package org.eclipse.persistence.testing.tests.eis.xmlfile;

import javax.resource.*;
import javax.resource.cci.*;
import org.eclipse.persistence.internal.eis.adapters.xmlfile.*;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;

/**
 * Test conecting directly through the Attunity J2C driver. Requires Attunity installed and running
 * on the local machine.
 */
public class DirectConnectTest extends AutoVerifyTestCase {
    public XMLFileConnectionFactory connectionFactory;

    public DirectConnectTest() {
        this.setDescription("Test conecting directly through the emulated xml file adapter");
    }

    public void test() throws Exception {
        Connection connection = connect();
        connection.close();
    }

    public Connection connect() throws ResourceException {
        connectionFactory = new XMLFileConnectionFactory();

        Connection connection = connectionFactory.getConnection();
        getSession().logMessage(connection.toString());
        return connection;
    }
}
