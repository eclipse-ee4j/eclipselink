/*******************************************************************************
 * Copyright (c) 1998, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial implementation
 ******************************************************************************/
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