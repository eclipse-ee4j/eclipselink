/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial implementation
package org.eclipse.persistence.transaction.sap;

import javax.transaction.TransactionManager;

import org.eclipse.persistence.transaction.JTATransactionController;

/**
 * TransactionController implementation for SAP NetWeaver AS Java 7.1 (including
 * EhP 1), 7.2 and follow-up releases.
 */
public class SAPNetWeaverTransactionController extends JTATransactionController {
    public static final String JNDI_TRANSACTION_MANAGER_NAME = "TransactionManager";

    @Override
    protected TransactionManager acquireTransactionManager() throws Exception {
        return (TransactionManager)jndiLookup(JNDI_TRANSACTION_MANAGER_NAME);
    }
}
