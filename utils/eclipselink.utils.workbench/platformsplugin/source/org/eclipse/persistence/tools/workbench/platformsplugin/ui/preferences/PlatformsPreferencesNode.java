/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.preferences;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode;
import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;


/**
 * Preferences node for general settings used by
 * the platforms plug-in.
 */
public final class PlatformsPreferencesNode extends AbstractPreferencesNode {

	/**
	 * constructor
	 */
	public PlatformsPreferencesNode(PreferencesContext context) {
		super(context);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode#initialize()
	 */
	protected void initialize() {
		super.initialize();
//		this.insert(new HelpPreferencesNode(this.getPreferencesContext()), 0);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode#buildPropertiesPage()
	 */
	protected Component buildPropertiesPage() {
		return new PlatformsPreferencesPage(this.getPreferencesContext());
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode#buildDisplayString()
	 */
	protected String buildDisplayString() {
		return this.resourceRepository().getString("PREFERENCES.PLATFORMS");
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.app.PreferencesNode#helpTopicId()
	 */
	public String helpTopicId() {
		return "preferences.platforms";
	}

}
