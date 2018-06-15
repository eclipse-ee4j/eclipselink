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
 * <b>Purpose:</b><p>English ResourceBundle for DBWSException.</p>
 */
public class DBWSExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
        {"47000", "Could not locate file [{0}]"},
        {"47001", "Could not locate descriptor [{0}] for operation [{1}] in the O-R project"},
        {"47002", "Could not locate query [{0}] for descriptor [{1}]"},
        {"47003", "Could not locate query [{0}] for session [{1}]"},
        {"47004", "Parameter type [{0}] for operation [{1}] does not exist in the schema"},
        {"47005", "Parameter type [{0}] for operation [{1}] has no O-X mapping"},
        {"47006", "Result type [{0}] for operation [{1}] does not exist in the schema"},
        {"47007", "Result type [{0}] for operation [{1}] has no O-X mapping"},
        {"47008", "Only Simple XML Format queries support multiple output arguments"},
        {"47009", "INOUT cursor parameters are not supported"},
        {"47010", "Could not locate O-R session for service [{0}]"},
        {"47011", "Could not locate O-X session for service [{0}]"},
        {"47012", "Could not parse DBWS file"},
    };

    /**
     * Return the lookup table.
     */
    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
