/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.preferences;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode;
import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;


/**
 * Preferences node for EJB settings used by
 * the mappings plug-in.
 */
final class EjbPreferencesNode extends AbstractPreferencesNode {

	EjbPreferencesNode(PreferencesContext context) {
		super(context);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode#initialize()
	 */
	protected void initialize() {
		super.initialize();
//		this.insert(new XXXXPreferencesNode(this.getPreferencesContext()), 0);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode#buildPropertiesPage()
	 */
	protected Component buildPropertiesPage() {
		return new EjbPreferencesPage(this.getPreferencesContext());
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode#buildDisplayString()
	 */
	protected String buildDisplayString() {
		return this.resourceRepository().getString("PREFERENCES.MAPPINGS.EJB");
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.app.PreferencesNode#helpTopicId()
	 */
	public String helpTopicId() {
		return "preferences.ejb";
	}
}