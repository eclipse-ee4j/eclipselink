/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.isolatedsession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.config.CacheIsolationType;
import org.eclipse.persistence.config.ExclusiveConnectionMode;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ConnectionPolicy;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestProblemException;

public class ClientServerTest extends AutoVerifyTestCase {
    protected DatabaseLogin login;
    protected ArrayList clients;
    protected ServerSession server;
    protected String exclusiveConnectionMode;
    protected boolean isIsolated = true;
    protected Map isolatedDescriptors;

    public ClientServerTest(boolean isExclusive) {
        this((isExclusive ? ExclusiveConnectionMode.Isolated : ExclusiveConnectionMode.Transactional), true);
    }

    public ClientServerTest(String exclusiveConnectionMode, boolean isIsolated) {
        clients = new ArrayList();
        isolatedDescriptors = new HashMap();
        this.exclusiveConnectionMode = exclusiveConnectionMode;
        this.isIsolated = isIsolated;
        if(exclusiveConnectionMode.equals(ExclusiveConnectionMode.Isolated) && !isIsolated) {
            throw new TestProblemException("ExclusiveConnectionMode.Isolated requires isIsolated==true");
        }
        setDescription("This test acts as a template for tests using the client server framework");
    }

    public void copyDescriptors(Session session) {
        Vector descriptors = new Vector();

        for (Iterator iterator = session.getDescriptors().values().iterator(); iterator.hasNext(); ) {
            ClassDescriptor desc = (ClassDescriptor)iterator.next();
            descriptors.addElement(desc);
            // it's an isolated descriptor, but the test requires no isolation.
            // switch isolation off, cache the descriptor to restore isolation after the test is complete.
            if(!isIsolated && desc.isIsolated()) {
                // uowCacheIsolationLevel seems to be the only attribute affected by isolation,
                // cache the original one in the map.
                isolatedDescriptors.put(desc, desc.getUnitOfWorkCacheIsolationLevel());
                // un-isolate descriptor
                desc.setCacheIsolation(CacheIsolationType.SHARED);
                // the value assigned by default during initialization for non-isolated descriptor.
                desc.setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_NEW_DATA_AFTER_TRANSACTION);
            }
        }
        this.server.addDescriptors(descriptors);
        // Since the descriptors are already initialized, must also set the session to isolated.
        this.server.getProject().setHasIsolatedClasses(true);
    }

    @Override
    public void reset() {
        try {
            while (!this.clients.isEmpty()) {
                ((Session)this.clients.get(0)).release();
                this.clients.remove(0);
            }
            this.server.logout();

            // restore descriptors' isolation removed in setup
            if(!isIsolated) {
                Iterator it = isolatedDescriptors.entrySet().iterator();
                while(it.hasNext()) {
                    Map.Entry entry = (Map.Entry)it.next();
                    ClassDescriptor desc = (ClassDescriptor)entry.getKey();
                    desc.setCacheIsolation(CacheIsolationType.ISOLATED);
                    desc.setUnitOfWorkCacheIsolationLevel((Integer)entry.getValue());
                }
                isolatedDescriptors.clear();
            }

            getDatabaseSession().logout();
            getDatabaseSession().login();

        } catch (Exception ex) {
            if (ex instanceof ValidationException) {
                this.verify();
            }
        }
    }

    @Override
    public void setup() {
        try {
            this.login = (DatabaseLogin)getSession().getLogin().clone();
            this.server = new ServerSession(this.login, 2, 5);
            this.server.setSessionLog(getSession().getSessionLog());
            copyDescriptors(getSession());
            this.server.login();
            ConnectionPolicy connectionPolicy = this.server.getDefaultConnectionPolicy();
            if (this.exclusiveConnectionMode.equals(ExclusiveConnectionMode.Isolated)) {
                connectionPolicy.setExclusiveMode(ConnectionPolicy.ExclusiveMode.Isolated);
            } else if (this.exclusiveConnectionMode.equals(ExclusiveConnectionMode.Always)) {
                connectionPolicy.setExclusiveMode(ConnectionPolicy.ExclusiveMode.Always);
            }
            String propertyName = exclusiveConnectionMode;
            if(isIsolated) {
                propertyName += "_Isolated";
            }
            connectionPolicy.setProperty(propertyName, "true");
            this.clients.add(this.server.acquireClientSession(connectionPolicy));
            this.clients.add(this.server.acquireClientSession(connectionPolicy));
            this.clients.add(this.server.acquireClientSession(connectionPolicy));
        } catch (ValidationException ex) {
            this.verify();
        }
    }

    @Override
    public void test() {
    }

    @Override
    public void verify() {
    }
}
