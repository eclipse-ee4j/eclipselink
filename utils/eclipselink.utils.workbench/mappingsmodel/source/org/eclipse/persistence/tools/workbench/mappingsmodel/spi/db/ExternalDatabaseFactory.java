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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db;

/**
 * Simple factory interface for building external databases.
 */
public interface ExternalDatabaseFactory {

	/**
	 * Build and return an ExternalDatabase for the specified
	 * connection. The connection will be null if a connection to
	 * a database has not been established or is inappropriate
	 * (e.g. when executing inside jdev).
	 * The connection can be ignored, as appropriate.
	 */
	ExternalDatabase buildDatabase(java.sql.Connection connection);

}
