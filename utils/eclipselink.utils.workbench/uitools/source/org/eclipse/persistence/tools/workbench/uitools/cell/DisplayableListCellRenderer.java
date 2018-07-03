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
package org.eclipse.persistence.tools.workbench.uitools.cell;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.uitools.Displayable;


/**
 * This renderer assumes that the list cell values
 * implement the Displayable interface.
 */
public class DisplayableListCellRenderer extends SimpleListCellRenderer {

    /**
     * Construct a renderer that handles Displayables.
     */
    public DisplayableListCellRenderer() {
        super();
    }

    /**
     * Cast the value to Displayable and return its icon.
     */
    protected Icon buildIcon(Object value) {
        return ((Displayable) value).icon();
    }

    /**
     * Cast the value to Displayable and return its display string.
     */
    protected String buildText(Object value) {
        return ((Displayable) value).displayString();
    }

}
