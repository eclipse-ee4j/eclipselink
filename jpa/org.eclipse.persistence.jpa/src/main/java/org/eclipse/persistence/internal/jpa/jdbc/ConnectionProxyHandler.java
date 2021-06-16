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
package org.eclipse.persistence.internal.jpa.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * A simple invocation handler for the proxied connection.
 *
 * Connections are proxied only when they are obtained from a
 * transactional data source and within the context of a JTA
 * transaction.
 */
public class ConnectionProxyHandler implements InvocationHandler {
    Connection connection;

    /************************/
    /***** Internal API *****/
    /************************/
    private void debug(String s) {
        System.out.println(s);
    }

    /*
     * Use this constructor
     */
    public ConnectionProxyHandler(Connection connection) {
        this.connection = connection;
    }

    /*********************************/
    /***** InvocationHandler API *****/
    /*********************************/

    /*
     * Gateway for method interception
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        debug("PROXY method: " + methodName);
        // No-op if any of the following calls
        if (methodName.equals("close") || methodName.equals("commit") || methodName.equals("rollback")) {
            return null;
        }

        // Normal case is just to forward on to the real connection
        return method.invoke(connection, args);
    }
}
