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
package org.eclipse.persistence.testing.tests.remote;

import java.io.*;

import org.eclipse.persistence.testing.tests.queries.inmemory.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.server.*;

//When query's shouldMaintainCache is true and descriptor's shouldDisableCacheHits and
//shouldDisableCacheHitsOnRemote are true, cache is checked and the same object is returned.
public class QueryCacheHitEnabledAndDescriptorDisabledOnRemoteTest extends QueryCacheHitEnabledAndDescriptorDisabledTest {
    protected boolean orgDisableCacheHitsOnRemote;
    protected ClassDescriptor serverDescriptor;
    protected boolean orgServerDisableCacheHits;
    protected ServerSession serverSession;

    public QueryCacheHitEnabledAndDescriptorDisabledOnRemoteTest() {
        setDescription("Test when cache hit is enabled in query and disabled descriptor, cache is checked");
    }

    protected void setup() {
        super.setup();
        orgDisableCacheHitsOnRemote = descriptor.shouldDisableCacheHitsOnRemote();
        descriptor.setShouldDisableCacheHitsOnRemote(true);

        serverSession = ((ClientSession)RemoteModel.getServerSession()).getParent();
        //Set shouldDisableCacheHits to true on the server session
        serverDescriptor = serverSession.getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        orgServerDisableCacheHits = serverDescriptor.shouldDisableCacheHits();
        serverDescriptor.setShouldDisableCacheHits(true);
        //Set shouldLogMessages to true on the server session
        oldLog = serverSession.getSessionLog();
        tempStream = new StringWriter();
        serverSession.setLog(tempStream);
        serverSession.setLogLevel(SessionLog.FINE);
    }

    public void reset() {
        descriptor.setShouldDisableCacheHitsOnRemote(orgDisableCacheHitsOnRemote);

        serverDescriptor.setShouldDisableCacheHits(orgServerDisableCacheHits);
        serverSession.setSessionLog(oldLog);

        super.reset();
    }
}
