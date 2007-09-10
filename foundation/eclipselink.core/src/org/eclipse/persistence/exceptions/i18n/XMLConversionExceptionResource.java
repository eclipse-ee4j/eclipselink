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
 */
public class XMLConversionExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "25501", "Cannot create URL for file  [{0}] ." },
                                           { "25502", "Incorrect gDay format: [{0}] (expected [----DD])" },
                                           { "25503", "Incorrect gMonth format: [{0}] (expected [--MM--])" },
                                           { "25504", "Incorrect gMonthDay format: [{0}] (expected [--MM-DD])" },
                                           { "25505", "Incorrect gYear format: [{0}] (expected [YYYY])" },
                                           { "25506", "Incorrect gYearMonth format: [{0}] (expected [YYYY-MM])" },
                                           { "25507", "Incorrect Timestamp dateTime format: [{0}] (expected [YYYY-MM-DD'T'HH:MM:SS.NNNNNNNNN])" },
                                           { "25508", "Incorrect Timestamp time format: [{0}] (expected [HH:MM:SS.NNNNNNNNN])" },
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
