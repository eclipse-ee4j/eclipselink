/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

/**
 * INTERNAL:
 * English ResourceBundle for ConcurrencyException messages.
 *
 * Creation date: (12/6/00 9:47:38 AM)
 * @author: TopLink maintenance team
 */
public class SynchronizationExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "15001", "Warning: Unable to send changes to distributed session: {0}" },
                                           { "15002", "Warning: Dropped inactive connection to remote server." },
                                           { "15003", "Warning: Dropped inactive connection to remote server {0}" },
                                           { "15004", "Could not do remote merge." },
                                           { "15005", "Could not do remote merge for session {0}" },
                                           { "15006", "Could not do remote merge for session {0} on server {1}" },
                                           { "15007", "Could not do remote merge on server {0}" },
                                           { "15008", "Could not do local merge." },
                                           { "15009", "Could not do local merge from server {0}" },
                                           { "15010", "Could not look up local host." },
                                           { "15011", "Could not bind controller under registry name: {0}" },
                                           { "15012", "Could not look up controller under registry name: {0}" },
                                           { "15013", "Could not find JMS Service Topic named: {0}" },
                                           { "15014", "Could not unmarshall received session announcement." },
                                           { "15015", "Could not unmarshall received session announcement for session {0}." },
                                           { "15016", "Could not gain access to SynchronizationService class" },
                                           { "15017", "Could not notify other sessions of this node's existence." },
                                           { "15018", "Could not join multicast group." },
                                           { "15019", "Failed doing register for synchronization." },
                                           { "15020", "Failed doing register for synchronization for session {0} on source server {1}." },
                                           { "15021", "Failed doing register for synchronization for session {0}." },
                                           { "15022", "Failed doing register for synchronization on source server {0}." },
                                           { "15023", "Failed trying to receive a service announcement from a remote service." },
                                           { "15024", "Failed trying to receive a service announcement from a remote service for session {0}." },
                                           { "15025", "Failed to Reset Cache Synchronization Service." }
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}