/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2018 IBM Corporation and/or its affiliates. All rights reserved.
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
//     09/24/2014-2.6 Rick Curtis
//       - 443762 : Misc message cleanup.
//     12/18/2014-2.6 Rick Curtis
//       - 454189 : Misc message cleanup.#2
package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

/**
 * INTERNAL:
 * English ResourceBundle for JMSProcessingException messages.
 *
 * Creation date: (12/6/00 9:47:38 AM)
 * @author: TopLink maintenance team
 */
public class JMSProcessingExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "18001", "Error while processing incoming JMS message" },
                                           { "18002", "The Topic created in the JMS Service for the interconnection of Sessions must be set in the JMSClusteringService" },
                                           { "18003", "Failed to lookup the session''s name defined it the env-entry element of the Message Driven Bean" },
                                           { "18004", "The Message Driven Bean (MDB) cannot find the session.  The MDB getSession() method must return a non null session." }
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
