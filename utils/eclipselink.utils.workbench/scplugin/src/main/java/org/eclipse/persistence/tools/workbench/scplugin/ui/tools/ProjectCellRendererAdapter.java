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
import java.io.File;
import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;

// Mapping Workbench

/**
 * This <code>ProjectCellRendererAdapter</code> formats
 * {@link TopLinkSessionsAdapter} objects by using its display string.
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public final class ProjectCellRendererAdapter extends AbstractCellRendererAdapter
{
    /**
     * The repository used to retrieve the localized string and icon.
     */
    private final ResourceRepository repository;

    /**
     * Creates a new <code>ProjectCellRendererAdapter</code>.
     *
     * @param repository The repository used to retrieve localized string and the
     * icon that decorates objects of type {@link TopLinkSessionsAdapter}
     */
    public ProjectCellRendererAdapter(ResourceRepository repository)
    {
        super();
        this.repository = repository;
    }

    /**
     * Returns the accessible name to be given to the component used to rendered
     * the given value.
     *
     * @param value The object to be decorated
     * @return A string that can add more description to the rendered object when
     * the text is not sufficient, if <code>null</code> is returned, then the
     * text is used as the accessible text
     */
    public String buildAccessibleName(Object value)
    {
        TopLinkSessionsAdapter topLinkSessions = (TopLinkSessionsAdapter) value;
        File location = topLinkSessions.getSaveDirectory();

        if (location == null)
            return topLinkSessions.getName();

        return repository.getString("SESSIONS_CONFIGURATION", topLinkSessions.getName(), location);
    }

    /**
     * Returns an icon that can be used to identify the object in a UI component
     * that supports icons.
     *
     * @param value The object to be represented by an icon, if one is required
     * @return An icon representing the given object or <code>null</code>
     */
    public Icon buildIcon(Object value)
    {
        return repository.getIcon("SESSIONS_CONFIGURATION");
    }

    /**
     * Returns a string that can be used to identify the object in a textual UI
     * setting (typically the object's name).
     *
     * @param value The object to be represented by a string
     * @return A string representation of the given object
     */
    public String buildText(Object value)
    {
        TopLinkSessionsAdapter topLinkSessions = (TopLinkSessionsAdapter) value;
        return topLinkSessions.getPath().getName();
    }
}
