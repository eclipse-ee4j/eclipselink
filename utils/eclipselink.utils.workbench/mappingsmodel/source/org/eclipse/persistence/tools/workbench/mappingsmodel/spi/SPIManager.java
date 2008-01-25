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
 * Central manager for access to implementations of the various SPIs
 * (Service Provider Interfaces).
 */
public interface SPIManager {

	ExternalClassRepositoryFactory getExternalClassRepositoryFactory();

	ExternalDatabaseFactory getExternalDatabaseFactory();

}
