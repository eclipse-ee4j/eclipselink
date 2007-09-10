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
 * English ResourceBundle for SDKQueryException messages.
 *
 * Creation date: (2/26/01 9:47:38 AM)
 * @author TopLink Maintenance Team
 */
public class SDKQueryExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "20001", "Invalid SDK call - the call must be an instance of SDKCall: [{0}]" },
                                           { "20002", "Invalid SDK mechanism state - only one call is allowed." },
                                           { "20003", "Invalid SDK accessor - the accessor must be an instance of SDKAccessor: [{0}]" },
                                           { "20004", "Invalid Accessor class - the accessor class must extend: [{0}] {2}actual class: [{1}]" }
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}