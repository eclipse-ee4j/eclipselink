/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.framework;

/**
 * This superclass can be used to inherit transaction setup and reset to ensure the database is not modified.
 */
public abstract class TransactionalTestCase extends AutoVerifyTestCase {

    protected void setup() {
        getAbstractSession().beginTransaction();
    }

    public void reset() {
        if (getAbstractSession().isInTransaction() || getSession().isRemoteSession()) {
            getAbstractSession().rollbackTransaction();
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}