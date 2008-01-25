/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
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