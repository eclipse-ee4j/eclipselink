/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
