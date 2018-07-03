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

/**
 * Simple template that allows selective overriding of the methods
 * required of a <code>CellRendererAdapter</code>.
 */
public abstract class AbstractCellRendererAdapter
    implements CellRendererAdapter
{

    /**
     * @see CellRendererAdapter#buildIcon(Object)
     */
    public Icon buildIcon(Object value) {
        return null;
    }

    /**
     * @see CellRendererAdapter#buildText(Object)
     */
    public String buildText(Object value) {
        return null;
    }

    /**
     * @see CellRendererAdapter#buildToolTipText(Object)
     */
    public String buildToolTipText(Object value) {
        return null;
    }

    /**
     * @see CellRendererAdapter#buildAccessibleName(Object)
     */
    public String buildAccessibleName(Object value) {
        return null;
    }

}
