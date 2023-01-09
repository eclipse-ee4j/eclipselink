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
import org.eclipse.persistence.exceptions.TransactionException;
import org.eclipse.persistence.transaction.JTATransactionController;

/**
 * <p>
 * <b>Purpose</b>: TransactionController implementation for WildFly JTA
 * <p>
 * <b>Description</b>: Implements the required behavior for controlling JTA 1.0
 * transactions in WildFly. The JTA TransactionManager must be set on the instance.
 *
 * @see JTATransactionController
 */
public class WildFlyTransactionController extends JTATransactionController {

    public static final String JNDI_TRANSACTION_MANAGER_NAME_AS4 = "java:/TransactionManager";
    public static final String JNDI_TRANSACTION_MANAGER_NAME_AS7 = "java:wildfly/TransactionManager";

    /**
     * Default constructor
     */
    public WildFlyTransactionController() {
        super();
    }

    /**
     * INTERNAL:
     * Obtain and return the JTA TransactionManager on this platform
     */
    @Override
    protected TransactionManager acquireTransactionManager() throws Exception {
        try {
            return (TransactionManager)jndiLookup(JNDI_TRANSACTION_MANAGER_NAME_AS7);
        } catch(TransactionException transactionException) {
            if (transactionException.getErrorCode() == TransactionException.ERROR_DOING_JNDI_LOOKUP) {
                return (TransactionManager)jndiLookup(JNDI_TRANSACTION_MANAGER_NAME_AS4);
            } else {
                throw transactionException;
            }
        }
    }
}
