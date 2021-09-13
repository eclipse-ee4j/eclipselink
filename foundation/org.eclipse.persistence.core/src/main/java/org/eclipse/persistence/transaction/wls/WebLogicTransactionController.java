/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.transaction.wls;

import jakarta.transaction.TransactionManager;
import org.eclipse.persistence.transaction.JTATransactionController;

/**
 * <p>
 * <b>Purpose</b>: TransactionController implementation for WLS JTA
 * <p>
 * <b>Description</b>: Implements the required behavior for controlling JTA 1.0
 * transactions in WLS. The JTA TransactionManager must be set on the instance.
 *
 * @see org.eclipse.persistence.transaction.JTATransactionController
 */
public class WebLogicTransactionController extends JTATransactionController {

    static final String JNDI_TRANSACTION_MANAGER_NAME = "weblogic.transaction.TransactionManager";

    /**
     * Default constructor
     */
    public WebLogicTransactionController() {
        super();
    }

    /**
     * INTERNAL:
     * Obtain and return the JTA TransactionManager on this platform
     */
    @Override
    protected TransactionManager acquireTransactionManager() throws Exception {
        return (TransactionManager)jndiLookup(JNDI_TRANSACTION_MANAGER_NAME);
    }

}
