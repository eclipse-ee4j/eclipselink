/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.transaction.jboss;

import javax.transaction.TransactionManager;

import org.eclipse.persistence.exceptions.TransactionException;
import org.eclipse.persistence.transaction.JTATransactionController;

/**
 * <p>
 * <b>Purpose</b>: TransactionController implementation for JBoss JTA
 * <p>
 * <b>Description</b>: Implements the required behavior for controlling JTA 1.0
 * transactions in JBoss. The JTA TransactionManager must be set on the instance.
 * <p>
 * @see org.eclipse.persistence.transaction.JTATransactionController
 */
public class JBossTransactionController extends JTATransactionController {
    public static final String JNDI_TRANSACTION_MANAGER_NAME_AS4 = "java:/TransactionManager";
    public static final String JNDI_TRANSACTION_MANAGER_NAME_AS7 = "java:jboss/TransactionManager";

    /**
     * INTERNAL:
     * Obtain and return the JTA TransactionManager on this platform
     */
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
