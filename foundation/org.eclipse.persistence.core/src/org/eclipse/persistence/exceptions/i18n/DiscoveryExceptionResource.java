/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

/**
 * INTERNAL:
 * English ResourceBundle for ConcurrencyException messages.
 *
 * @author Steven Vo
 */
public class DiscoveryExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "22001", "Could not join multicast group" },
                                           { "22002", "Could not send service announcment" },
                                           { "22003", "Failed doing lookup of local host" },
                                           { "22004", "Failed trying to receive a service announcement from a remote service" }
    };

    /**
     * Return the lookup table.
     */
    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
