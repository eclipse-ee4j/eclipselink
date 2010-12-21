/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.scplugin;

import org.eclipse.persistence.tools.workbench.framework.resources.AbstractIconResourceFileNameMap;

public final class SCPluginIconResourceFileNameMap extends AbstractIconResourceFileNameMap {

	private static final String[][] entries = {

		// sessions.xml
		{ "SESSIONS_CONFIGURATION",     "sc/TopLinkSessions.gif" },
		{ "NEW_SESSIONS_CONFIGURATION", "sc/TopLinkSessions.gif" },

		// Miscelleneous
		{ "DELETE",                     "basic/edit/Delete.gif" },
		{ "FILE",                       "basic/file/File.gif" },
		{ "RENAME",                     "basic/misc/Rename.gif" },
		{ "REMOVE",                     "basic/misc/Remove.gif" },

		// Session
		{ "ADD_SESSION",                "sc/Session.add.gif" },
		{ "ADD_BROKER",                 "sc/Session.broker.add.gif" },

		{ "SESSION_BROKER",             "sc/Session.broker.gif" },

		{ "SESSION_SERVER_RDBMS",       "sc/Session.server.database.gif" },
		{ "SESSION_SERVER_EIS",         "sc/Session.server.eis.gif" },	
		{ "SESSION_SERVER_XML",         "sc/Session.server.xml.gif" },

		{ "SESSION_DATABASE_RDBMS",     "sc/Session.database.database.gif" },
		{ "SESSION_DATABASE_EIS",       "sc/Session.database.eis.gif" },	
		{ "SESSION_DATABASE_XML",       "sc/Session.database.xml.gif" },

		// Connection Pool
		{ "ADD_NAMED_CONNECTION_POOL",  "sc/ConnectionPool.add.gif"},
		{ "CONNECTION_POOL",            "sc/ConnectionPool.gif"},
		{ "CONNECTION_POOL_READ",       "sc/ConnectionPool.read.gif"},
		{ "CONNECTION_POOL_SEQUENCE",   "sc/ConnectionPool.sequence.gif"},
		{ "CONNECTION_POOL_WRITE",      "sc/ConnectionPool.write.gif"},
	};

	protected String[][] getEntries() {
		return entries;
	}
}
