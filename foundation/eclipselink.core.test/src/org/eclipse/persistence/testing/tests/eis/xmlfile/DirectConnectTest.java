/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
