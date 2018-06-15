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
//     10/24/2017-3.0 Tomas Kraus
//       - 526419: Modify EclipseLink to reflect changes in JTA 1.1.
package org.eclipse.persistence.transaction.glassfish;

import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;

import org.eclipse.persistence.exceptions.TransactionException;
import org.eclipse.persistence.transaction.JTA11TransactionController;

/**
 * <p>
 * <b>Purpose</b>: TransactionController implementation for Glassfish JTA
 * <p>
 * <b>Description</b>: Implements the required behavior for controlling JTA
 * transactions in Glassfish. The JTA TransactionManager must be set on the instance.
 *
 * @see org.eclipse.persistence.transaction.JTATransactionController
 */
public class GlassfishTransactionController11 extends JTA11TransactionController {

    public GlassfishTransactionController11() {
        super();
    }

    /**
     * INTERNAL:
     * Obtain and return the JTA TransactionManager on this platform
     */
    @Override
    protected TransactionManager acquireTransactionManager() throws Exception {
        return (TransactionManager)jndiLookup(GlassfishTransactionController.JNDI_TRANSACTION_MANAGER_NAME);
    }

    /**
     * INTERNAL:
     * Obtain and return the JTA 1.1 {@link TransactionSynchronizationRegistry} on this platform.
     *
     * @return the {@code TransactionSynchronizationRegistry} for the transaction system
     * @since 2.7.1
     */
    @Override
    protected TransactionSynchronizationRegistry acquireTransactionSynchronizationRegistry() throws TransactionException {
        return (TransactionSynchronizationRegistry)jndiLookup(JNDI_TRANSACTION_SYNCHRONIZATION_REGISTRY);
    }

}
