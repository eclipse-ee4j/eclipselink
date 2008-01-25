/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalDatabaseFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepositoryFactory;

/**
 * Straightforward implementation of the SPIManager interface.
 */
public class SimpleSPIManager implements SPIManager {
	private ExternalClassRepositoryFactory externalClassRepositoryFactory;
	private ExternalDatabaseFactory externalDatabaseFactory;

	/**
	 * Default constructor.
	 */
	public SimpleSPIManager() {
		super();
	}

	/**
	 * @see SPIManager#getExternalClassRepositoryFactory()
	 */
	public ExternalClassRepositoryFactory getExternalClassRepositoryFactory() {
		return this.externalClassRepositoryFactory;
	}

	public void setExternalClassRepositoryFactory(ExternalClassRepositoryFactory externalClassRepositoryFactory) {
		this.externalClassRepositoryFactory = externalClassRepositoryFactory;
	}

	/**
	 * @see SPIManager#getExternalDatabaseFactory()
	 */
	public ExternalDatabaseFactory getExternalDatabaseFactory() {
		return this.externalDatabaseFactory;
	}

	public void setExternalDatabaseFactory(ExternalDatabaseFactory externalDatabaseFactory) {
		this.externalDatabaseFactory = externalDatabaseFactory;
	}

}
