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
package org.eclipse.persistence.transaction.jboss;

import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;

import org.eclipse.persistence.exceptions.TransactionException;
import org.eclipse.persistence.transaction.JTA11TransactionController;

/**
 * <p>
 * <b>Purpose</b>: TransactionController implementation for JBoss JTA
 * <p>
 * <b>Description</b>: Implements the required behavior for controlling JTA 1.0
 * transactions in JBoss. The JTA TransactionManager must be set on the instance.
 *
 * @see org.eclipse.persistence.transaction.JTATransactionController
 */
public class JBossTransactionController11 extends JTA11TransactionController {

    /** JBoss specific JNDI name of {@code TransactionSynchronizationRegistry} instance. */
    public static final String JNDI_TRANSACTION_SYNCHRONIZATION_REGISTRY = "java:jboss/TransactionSynchronizationRegistry";

    /**
     * INTERNAL:
     * Obtain and return the JTA TransactionManager on this platform
     */
    @Override
    protected TransactionManager acquireTransactionManager() throws Exception {
        try {
            return (TransactionManager)jndiLookup(JBossTransactionController.JNDI_TRANSACTION_MANAGER_NAME_AS7);
        } catch(TransactionException transactionException) {
            if (transactionException.getErrorCode() == TransactionException.ERROR_DOING_JNDI_LOOKUP) {
                return (TransactionManager)jndiLookup(JBossTransactionController.JNDI_TRANSACTION_MANAGER_NAME_AS4);
            } else {
                throw transactionException;
            }
        }
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
