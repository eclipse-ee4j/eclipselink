/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

/**
 * INTERNAL:
 * English ResourceBundle for EISException messages.
 * <p>
 * Creation date: (2/28/01 9:47:38 AM)
 * @author TopLink Maintenance Team
 */
public final class EISExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "17007", "{0} property must be set." },
                                           { "17008", "Invalid {0} property encountered." },
                                           { "17009", "{0} or {1} property must be set." },
                                           { "17010", "Output record contains an unsupported message type" },
                                           { "17011", "No connection factory has been specified." },
                                           { "17012", "InteractionSspec must be a CciJMSInteractionSpec." },
                                           { "17013", "Record must be a CciJMSRecord." },
                                           { "17014", "Unknown interaction specification type" },
                                           { "17015", "Input must contain a single text element." },
                                           { "17016", "A timeout occurred - no message was received." },
                                           { "17017", "Input record contains an unsupported message type." },
                                           { "17018", "Cannot invoke \"begin()\" on a non-transacted session." },
                                           { "17019", "Problem testing for transacted session: " },
                                           { "17020", "InteractionSspec must be an AQInteractionSpec." },
                                           { "17021", "Record must be an AQRecord." },
                                           { "17022", "Input must contain a single raw element." },
                                           { "17023", "An exception occurred setting MQQueueConnectionFactory attributes." },
                                           { "17024", "Could not delete file: {0}" },
                                           { "17025", "This mapping requires a foreign key grouping element, as multiple foreign keys exist." },
                                           { "17026", "Operation property must be set on the query's interaction." },
                                           { "17027", "Target table name is not set. It must be set on the query's interaction." },
                                           { "17028", "Invalid consistency property value: {0}" },
                                           { "17029", "Invalid durability property value: {0}" },
                                           { "17030", "XMLInteraction is only valid for object queries, use MappedInteraction for native queries: {0}" },
                                           { "17031", "Query too complex for Oracle NoSQL translation, only select queries are supported in query: {0}" }
    };

    /**
     * Default constructor.
     */
    public EISExceptionResource() {
        // for reflection
    }

    /**
     * Return the lookup table.
     */
    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
