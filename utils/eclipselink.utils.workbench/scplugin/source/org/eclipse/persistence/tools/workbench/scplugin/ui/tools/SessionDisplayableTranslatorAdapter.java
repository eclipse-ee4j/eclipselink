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
package org.eclipse.persistence.tools.workbench.scplugin.ui.tools;

// Mapping Workbench
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;

/**
 * This <code>SessionDisplayableTranslatorAdapter</code> is responsible to add
 * more information into the string used in the title bar that represents the
 * edited session.
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public final class SessionDisplayableTranslatorAdapter extends AbstractDisplayableTranslatorAdapter
{
    /**
     * Creates a new <code>SessionDisplayableTranslatorAdapter</code>.
     *
     * @param repository The repository used to retrieve localized string and the
     * icon that decorates objects of type {@link SessionNode}
     */
    public SessionDisplayableTranslatorAdapter(ResourceRepository repository)
    {
        super(new SessionCellRendererAdapter(repository, "SESSION_DISPLAY_STRING_TITLE_BAR"));
    }
}
