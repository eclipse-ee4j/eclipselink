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
