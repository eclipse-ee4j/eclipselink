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
package org.eclipse.persistence.tools.workbench.scplugin;

// JDK
import java.util.ListResourceBundle;

/**
 * The resource bundle containing all the problem messages.
 *
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public final class SCProblemsResourceBundle extends ListResourceBundle
{
	/**
	 * The association of a key and the value that will be loaded into the table
	 * when the resource bundle is loaded
	 */
	private static final Object[][] contents =
	{
		// Login
		{ "0801", "''{0}'' - Login - The Connection URL has to be specified." },
		{ "0802", "''{0}'' - Login - The driver class has to be specified." },
		{ "0803", "''{0}'' - Login - The data source name has to be specified." },
		{ "0810", "''{0}'' - Login - The platform class is unknown: {1}."},

		// Broker
		{ "0804", "''{0}'' - Session Broker - It has to have at least one session, either a server or a database session." },

		// DatabaseSession
		{ "0805", "''{0}'' - Database Session - It has to have at least one XML file or a class specified." },

		// UserDefinedTransportManager
		{ "0806", "''{0}'' - The transport class has to be specified." },

		// DatabaseSessionAdapter
		{ "0807", "''{0}'' - The location of the log file has to be specified." },

		// ReadConnectionPool
		{ "0808", "''{0}'' - Only Non-JTS Connection URL or Non-JTS Data Source can be set, not both." },

		// CustomServerPlatform
		{ "0811", "''{0}'' - An external transaction controller class (JTA) has to be specified." },
		{ "0812", "''{0}'' - A server class has to be specified" },
		{ "0813", "''{0}'' - Note: 9.0.4 JTA have been deprecated, see Server Platform tab and release notes for more details."},
//		    	// 0813 - Complete message
//				"Note: 9.0.4 external transaction controllers have been deprecated, \n" +
//				"a server platform should now be used to integrate with a J2EE server, \n" +
//				"see tab Server Platform to configure a server platform, if you used a \n" +
//				"custom external transaction controller you may need to implement \n" +
//				"a custom server platform." },
		{ "0814", "''{0}'' - Note: Cache Synchronization have been deprecated, see Cache Coordination tab and release notes for more details."},
		{ "0815", "{0} - is no longer a supported platform, select another."},

	};

	/**
	 * Returns the table of bundles.
	 *
	 * @return The association of a key and a value that will be loaded into the
	 * table when the resource bundle is loaded
	 */
	protected Object[][] getContents()
	{
		return contents;
	}
}
