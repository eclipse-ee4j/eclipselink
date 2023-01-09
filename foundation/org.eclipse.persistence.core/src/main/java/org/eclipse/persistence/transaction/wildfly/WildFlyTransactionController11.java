/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.transaction.wildfly;

import jakarta.transaction.TransactionManager;
import jakarta.transaction.TransactionSynchronizationRegistry;
import org.eclipse.persistence.exceptions.TransactionException;
import org.eclipse.persistence.transaction.JTA11TransactionController;

/**
 * <p>
 * <b>Purpose</b>: TransactionController implementation for WildFly JTA
 * <p>
 * <b>Description</b>: Implements the required behavior for controlling JTA 1.0
 * transactions in WildFly. The JTA TransactionManager must be set on the instance.
 *
 * @see org.eclipse.persistence.transaction.JTATransactionController
 */
public class WildFlyTransactionController11 extends JTA11TransactionController {

    /** WildFly specific JNDI name of {@code TransactionSynchronizationRegistry} instance. */
    public static final String JNDI_TRANSACTION_SYNCHRONIZATION_REGISTRY = "java:jboss/TransactionSynchronizationRegistry";

    /**
     * Default constructor
     */
    public WildFlyTransactionController11() {
        super();
    }

    /**
     * INTERNAL:
     * Obtain and return the JTA TransactionManager on this platform
     */
    @Override
    protected TransactionManager acquireTransactionManager() throws Exception {
        try {
            return (TransactionManager)jndiLookup(WildFlyTransactionController.JNDI_TRANSACTION_MANAGER_NAME_AS7);
        } catch(TransactionException transactionException) {
            if (transactionException.getErrorCode() == TransactionException.ERROR_DOING_JNDI_LOOKUP) {
                return (TransactionManager)jndiLookup(WildFlyTransactionController.JNDI_TRANSACTION_MANAGER_NAME_AS4);
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
