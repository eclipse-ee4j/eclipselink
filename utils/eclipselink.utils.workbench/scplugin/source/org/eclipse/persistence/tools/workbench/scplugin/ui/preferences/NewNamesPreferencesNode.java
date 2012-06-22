/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.scplugin.ui.preferences;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode;
import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;


/**
 * Preferences node for general settings used by the sessions configuration
 * plug-in.
 */
final class NewNamesPreferencesNode extends AbstractPreferencesNode
{
	NewNamesPreferencesNode(PreferencesContext context)
	{
		super(context);
	}

	protected String buildDisplayString()
	{
		return resourceRepository().getString("PREFERENCES.SC.NEW_NAMES");
	}

	protected Component buildPropertiesPage()
	{
		return new NewNamesPreferencesPage(getPreferencesContext());
	}

	public String helpTopicId()
	{
		return "preferences.sessions.newNames";
	}
}
