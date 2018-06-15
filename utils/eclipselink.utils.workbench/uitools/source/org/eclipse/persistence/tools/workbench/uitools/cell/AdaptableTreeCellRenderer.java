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
package org.eclipse.persistence.tools.workbench.uitools.cell;

import javax.swing.Icon;

/**
 * Delegate rendering to an adapter.
 */
public class AdaptableTreeCellRenderer
    extends SimpleTreeCellRenderer
{
    private CellRendererAdapter adapter;

    /**
     * Construct a renderer with the specified adapter.
     */
    public AdaptableTreeCellRenderer(CellRendererAdapter adapter) {
        super();
        if (adapter == null) {
            throw new NullPointerException();
        }
        this.adapter = adapter;
    }

    protected Icon buildIcon(Object value) {
        return this.adapter.buildIcon(value);
    }

    protected String buildText(Object value) {
        return this.adapter.buildText(value);
    }

    protected String buildToolTipText(Object value) {
        return this.adapter.buildToolTipText(value);
    }

    protected String buildAccessibleName(Object value) {
        return this.adapter.buildAccessibleName(value);
    }

}
