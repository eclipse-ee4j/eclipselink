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
package org.eclipse.persistence.tools.workbench.scplugin.ui.tools;

// JDK
import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.uitools.DisplayableAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;

// Mapping Workbench

/**
 * This <code>DisplayableTranslator</code> is responsible to add more
 * information into the string used in the title bar that represents the edited
 * <code>ApplicationNode</code>.
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public abstract class AbstractDisplayableTranslatorAdapter implements DisplayableAdapter
{
    /**
     * Reuse the <code>CellRendererAdapter</code> since the two interface are not
     * compatible (for now!).
     */
    private final CellRendererAdapter adapter;

    /**
     * Creates a new <code>SessionDisplayableAdater</code>.
     *
     * @param repository The repository used to retrieve localized string and the
     * icon that decorates <code>ApplicationNode</code>
     */
    public AbstractDisplayableTranslatorAdapter(CellRendererAdapter adapter)
    {
        super();
        this.adapter = adapter;
    }

    /**
     * Returns a string that can be used to identify the object in a textual UI
     * setting (typically the object's name).
     *
     * @param value The object to be represented by a string
     * @return A string representation of the given object
     */
    public String displayString(Object value)
    {
        if (value == null)
            return null;

        ApplicationNode node = (ApplicationNode) value;
        return this.adapter.buildText(node.getValue());
    }

    /**
     * Returns an icon that can be used to identify the object in a UI component
     * that supports icons.
     *
     * @param value The object to be represented by an icon, if one is required
     * @return An icon representing the given object or <code>null</code>
     */
    public Icon icon(Object value)
    {
        if (value == null)
            return null;

        ApplicationNode node = (ApplicationNode) value;
        return this.adapter.buildIcon(node.getValue());
    }
}
