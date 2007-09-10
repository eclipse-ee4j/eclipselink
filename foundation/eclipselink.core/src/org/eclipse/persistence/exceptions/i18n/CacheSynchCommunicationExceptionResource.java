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
public class CacheSynchCommunicationExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "12001", "Unable to Connect to {0}." },
                                           { "15001", "Warning: Unable to send changes to distributed session: {0}" }
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}