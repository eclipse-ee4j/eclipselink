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
package org.eclipse.persistence.tools.workbench.uitools;

import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;

/**
 * Straightforward adaptation of Display to StringConverter.
 */
public class DisplayableStringConverter implements StringConverter {

    // singleton
    private static DisplayableStringConverter INSTANCE;

    /**
     * Return the singleton.
     */
    public static synchronized StringConverter instance() {
        if (INSTANCE == null) {
            INSTANCE = new DisplayableStringConverter();
        }
        return INSTANCE;
    }

    /**
     * Ensure non-instantiability.
     */
    private DisplayableStringConverter() {
        super();
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.utility.StringConverter#convert(java.lang.Object)
     */
    public String convertToString(Object o) {
        return (o == null) ? null : ((Displayable) o).displayString();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "DisplayableStringConverter";
    }

}
