/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.uitools;

import javax.swing.Icon;

/**
 * Indirectly adapt an object to the Displayable interface.
 */
public interface DisplayableAdapter {

    /**
     * Return the specified object's display string.
     * @see Displayable#displayString()
     */
    String displayString(Object object);

    /**
     * Return the specified object's icon.
     * @see Displayable#icon()
     */
    Icon icon(Object object);


    /**
     * Simple implementation of the interface that uses the object's
     * #toString() for its display string and returns null for the icon.
     */
    DisplayableAdapter DEFAULT_INSTANCE =
        new DisplayableAdapter() {

            public String displayString(Object object) {
                return String.valueOf(object);
            }

            public Icon icon(Object object) {
                return null;
            }

        };

}
