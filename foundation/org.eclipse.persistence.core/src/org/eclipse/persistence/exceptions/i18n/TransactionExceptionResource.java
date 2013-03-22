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
package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

/**
 * INTERNAL:
 * English ResourceBundle for TransactionException messages.
 *
 */
public class TransactionExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "23001", "Error looking up external Transaction resource under JNDI name [{0}]" },
                                           { "23002", "Error obtaining status of current externally managed transaction" },
                                           { "23003", "Error obtaining current externally managed transaction" },
                                           { "23004", "Error obtaining the Transaction Manager" },
                                           { "23005", "Error binding to externally managed transaction" },
                                           { "23006", "Error beginning new externally managed transaction" },
                                           { "23007", "Error committing externally managed transaction" },
                                           { "23008", "Error rolling back externally managed transaction" },
                                           { "23009", "Error marking externally managed transaction for rollback" },
                                           { "23010", "No externally managed transaction is currently active for this thread" },
                                           { "23011", "UnitOfWork [{0}] was rendered inactive before associated externally managed transaction was complete." },
                                           { "23012", "No transaction is currently active" },
                                           { "23013", "Transaction is currently active" },
                                           { "23014", "Cannot use an EntityTransaction while using JTA." },
                                           { "23015", "Cannot enlist multiple datasources in the transaction." },
                                           { "23016", "Exception in Proxy execution." }
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
