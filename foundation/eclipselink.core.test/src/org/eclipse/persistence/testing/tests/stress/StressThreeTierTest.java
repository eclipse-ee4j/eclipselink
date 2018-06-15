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
package org.eclipse.persistence.testing.tests.stress;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;

/**
 * Test many client sessions.
 */

public class StressThreeTierTest extends AutoVerifyTestCase {
    public int stressLevel;
public StressThreeTierTest(int stressLevel)
{
    this.stressLevel = stressLevel;
}
public void test( )
{
    Server server = null;
    try {
    server = new Project(getSession().getDatasourceLogin()).createServerSession();
    new EmployeeSystem().addDescriptors(server);
    server.addConnectionPool("default", getSession().getDatasourceLogin(), 3, 5);
    server.useReadConnectionPool(3, 3);
    server.login();

    long startTime = System.currentTimeMillis();
    ClientThread client = new ClientThread(server);
    client.start();
    try {
        client.join();
    } catch (InterruptedException exception) {
        throw new TestErrorException(exception.toString());
    }
    long endTime = System.currentTimeMillis();
    System.out.println("Single thread total time -> " + (endTime - startTime));

    ClientThread[] clients = new ClientThread[stressLevel];
    for (int i = 0; i < stressLevel; i++) {
        clients[i] = new ClientThread(server);
    }

    startTime = System.currentTimeMillis();
    for (int i = 0; i < stressLevel; i++) {
        clients[i].start();
    }

    for (int i = 0; i < stressLevel; i++) {
        try {
            clients[i].join();
        } catch (InterruptedException exception) {
            throw new TestErrorException(exception.toString());
        }
    }
    endTime = System.currentTimeMillis();

    System.out.println("Concurrent thread total time -> " + (endTime - startTime));
    } finally {
        server.logout();
    }
}
}
