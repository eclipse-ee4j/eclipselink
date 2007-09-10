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
 * English ResourceBundle for SDKDescriptException messages.
 *
 * Creation date: (2/27/01 9:47:38 AM)
 * @author TopLink Maintenance Team
 */
public class SDKDescriptorExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "19001", "The TopLink SDK does not currently support [{0}]." },
                                           { "19002", "A custom selection query is required for this mapping." },
                                           { "19003", "The sizes of the field translation arrays must be equal." },
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}