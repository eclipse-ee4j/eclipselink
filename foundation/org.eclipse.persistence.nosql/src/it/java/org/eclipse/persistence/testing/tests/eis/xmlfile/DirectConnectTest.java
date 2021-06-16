/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.eis.xmlfile;

import jakarta.resource.*;
import jakarta.resource.cci.*;
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
