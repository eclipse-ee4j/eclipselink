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
 * English ResourceBundle for SDKDataStoreException messages.
 *
 * Creation date: (2/28/01 9:47:38 AM)
 * @author TopLink Maintenance Team
 */
public class SDKDataStoreExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "17001", "The TopLink SDK does not currently support [{0}]." },
                                           { "17002", "Incorrect login instance provided - an instance of [{0}] must be provided." },
                                           { "17003", "A call has not been defined for the query: [{0}]" },
                                           { "17004", "An InstantiationException occurred when instantiating the Accessor: [{0}]" },
                                           { "17005", "An IllegalAccessException occurred when instantiating the Accessor: [{0}]" },
                                           { "17006", "The SDKPlatform does not support sequences. Support for sequences must be provided by a subclass." }
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}