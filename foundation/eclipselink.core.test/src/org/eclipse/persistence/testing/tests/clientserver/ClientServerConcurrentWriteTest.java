/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.clientserver;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.DatabaseLogin;

public class ClientServerConcurrentWriteTest extends AutoVerifyTestCase {
    protected DatabaseLogin login;
    protected Vector clients = new Vector();
    protected Server server;
    public static int NUM_CLIENTS = 2;
    public Vector allSequenceNumbers = new Vector();

    public ClientServerConcurrentWriteTest() {
        setDescription("The test tests writing mutliple concurrent threads with pre-allocation");
    }

    public Vector getAllSequenceNumbers() {
        return allSequenceNumbers;
    }

    public Vector getClients() {
        if (clients == null) {
            setClients(new Vector());
        }
        return clients;
    }

    public void reset() {
        server.serverSession.rollbackTransaction();
        this.server.logout();
        stopThreads(getClients());
        setClients(new Vector());
    }

    public void setClients(Vector newClients) {
        this.clients = newClients;
    }

    public void setup() {
        this.login = (DatabaseLogin)getSession().getLogin().clone();
        this.server = new Server(this.login);
        this.server.serverSession.getLogin().getDefaultSequence().setPreallocationSize(1);
        this.server.serverSession.setSessionLog(getSession().getSessionLog());
        this.server.login();
        this.server.copyDescriptors(getSession());
        for (int i = 0; i < NUM_CLIENTS; i++) {
            getClients().addElement(new EmployeeClient(this.server, getSession(), "EmployeeClient" + i, 25));
        }
        server.serverSession.beginTransaction();
    }

    public void startThreads(Vector threads) {
        for (Enumeration enumtr = threads.elements(); enumtr.hasMoreElements();) {
            EmployeeClient thread = (EmployeeClient)enumtr.nextElement();
            thread.start();
        }
    }

    public void stopThreads(Vector threads) {
        for (Enumeration enumtr = threads.elements(); enumtr.hasMoreElements();) {
            EmployeeClient thread = (EmployeeClient)enumtr.nextElement();
            thread.pleaseStop();
        }
    }

    public void test() {
        startThreads(getClients());

        waitForThreads(getClients());

    }

    public void verify() {
        for (Enumeration enumtr = getClients().elements(); enumtr.hasMoreElements();) {
            EmployeeClient thread = (EmployeeClient)enumtr.nextElement();
            if (thread.anErrorOccurred()) {
                throw new TestErrorException("An exception " + thread.getTestException() + " occurred in client " + thread.toString());
            } else {
                for (Enumeration sequences = thread.getSequenceNumbers().elements();
                         sequences.hasMoreElements();) {
                    Object sequence = sequences.nextElement();
                    if (getAllSequenceNumbers().contains(sequence)) {
                        setClients(new Vector());
                        throw new TestErrorException("A duplicate sequence number has been detected -> " + sequence);
                    } else {
                        getAllSequenceNumbers().addElement(sequence);
                    }
                }
            }
        }
    }

    public void waitForThreads(Vector threads) {
        for (Enumeration enumtr = threads.elements(); enumtr.hasMoreElements();) {
            try {
                ((Thread)enumtr.nextElement()).join();
            } catch (Exception ex) {
                ex.printStackTrace();
                stopThreads(getClients());
                throw new TestErrorException("Client:" + toString() + "caught exception -> " + ex);
            }
        }
    }
}
