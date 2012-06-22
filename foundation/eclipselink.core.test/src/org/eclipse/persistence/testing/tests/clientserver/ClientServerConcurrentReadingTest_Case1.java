/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
