/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.ui.tools;

// Mapping Workbench
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;

/**
 * This <code>ProjectDisplayableTranslatorAdapter</code> is responsible to add
 * more information into the string used in the title bar that represents the
 * edited project.
 * 
 * @version 10.1.3
 * @author Pascal Filion
 */
public final class ProjectDisplayableTranslatorAdapter extends AbstractDisplayableTranslatorAdapter
{
	/**
	 * Creates a new <code>ProjectDisplayableTranslatorAdapter</code>.
	 *
	 * @param repository The repository used to retrieve localized string and the
	 * icon that decorates objects of type {@link ProjectNode}
	 */
	public ProjectDisplayableTranslatorAdapter(ResourceRepository repository)
	{
		super(new ProjectCellRendererAdapter(repository));
	}
}
