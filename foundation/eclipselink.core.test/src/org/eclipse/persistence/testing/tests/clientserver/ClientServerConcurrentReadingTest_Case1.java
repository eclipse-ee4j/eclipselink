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
package org.eclipse.persistence.testing.tests.clientserver;

import org.eclipse.persistence.sessions.DatabaseLogin;

public class ClientServerConcurrentReadingTest_Case1 extends ClientServerReadingTest {
    protected DatabaseLogin login;
    protected Reader[] reader;
    protected Server1 server;
    private static final int NUM_THREADS = 50;
    int type;

    public ClientServerConcurrentReadingTest_Case1() {
        this(false);

    }

    public ClientServerConcurrentReadingTest_Case1(boolean useStreamReader) {
        super(useStreamReader, 2);

    }
}
