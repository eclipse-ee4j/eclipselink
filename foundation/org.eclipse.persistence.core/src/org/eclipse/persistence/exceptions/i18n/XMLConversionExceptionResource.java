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
                                           { "25507", "Incorrect Timestamp dateTime format: [{0}] (expected [YYYY-MM-DD''T''HH:MM:SS.NNNNNNNNN])" },
                                           { "25508", "Incorrect Timestamp time format: [{0}] (expected [HH:MM:SS.NNNNNNNNN])" },
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
