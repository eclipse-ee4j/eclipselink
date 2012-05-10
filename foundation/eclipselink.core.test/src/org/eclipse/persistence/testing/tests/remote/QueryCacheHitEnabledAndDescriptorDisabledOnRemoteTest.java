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
