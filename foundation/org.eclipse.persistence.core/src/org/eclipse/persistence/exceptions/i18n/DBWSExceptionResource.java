/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022 IBM Corporation and/or its affiliates. All rights reserved.
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
 * <b>Purpose:</b><p>English ResourceBundle for DBWSException.</p>
 */
public class DBWSExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
            {"47000", "Cannot locate \"[{0}]\" file"},
            {"47001", "Cannot locate [{0}] descriptor for [{1}] operation in the O-R project"},
            {"47002", "Cannot locate \"[{0}]\" query for [{1}] descriptor"},
            {"47003", "Cannot locate \"[{0}]\" query for [{1}] session"},
            {"47004", "[{0}] parameter type, for [{1}] operation, does not exist in the schema"},
            {"47005", "[{0}] parameter type, for [{1}] operation, has no O-X mapping"},
            {"47006", "[{0}] result type, for [{1}] operation, does not exist in the schema"},
            {"47007", "[{0}] result type, for [{1}] operation, has no O-X mapping"},
            {"47008", "Only Simple XML Format queries support multiple output arguments"},
            {"47009", "INOUT cursor parameters are not supported"},
            {"47010", "Cannot locate O-R session for [{0}] service"},
            {"47011", "Cannot locate O-X session for [{0}] service"},
            {"47012", "Cannot parse DBWS file"},
    };

    /**
     * Return the lookup table.
     */
    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
