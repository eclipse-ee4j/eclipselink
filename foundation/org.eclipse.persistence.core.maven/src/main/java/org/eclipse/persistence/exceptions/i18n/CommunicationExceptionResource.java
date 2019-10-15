/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
 * English ResourceBundle for CommunicationException messages.
 *
 * @author Shannon Chen
 * @since TOPLink/Java 5.0
 */
public class CommunicationExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "12000", "Error Sending connection service to {0}." },
                                           { "12001", "Unable to Connect to {0}." },
                                           { "12002", "Unable to propagate changes to {0}." },
                                           { "12003", "Error in invocation: {0}." },
                                           { "12004", "Error sending message from service {0}." }
    };

    /**
     * Return the lookup table.
     */
    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
