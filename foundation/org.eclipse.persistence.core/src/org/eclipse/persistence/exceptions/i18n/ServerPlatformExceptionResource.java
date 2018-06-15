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

public class ServerPlatformExceptionResource extends ListResourceBundle {

    static final Object[][] contents = {
        { "63001", "Server platform class {0} not found." },
        { "63002", "Server platform class is not valid: {0}" }
    };

    /**
     * Return the lookup table.
     * @return lookup table
     */
    @Override
    protected Object[][] getContents() {
        return contents;
    }

}
