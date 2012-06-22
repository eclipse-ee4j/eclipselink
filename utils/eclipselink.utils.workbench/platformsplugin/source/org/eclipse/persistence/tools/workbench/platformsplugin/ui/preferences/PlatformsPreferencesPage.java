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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.preferences;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ButtonModel;
import javax.swing.JCheckBox;

import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.PlatformsPlugin;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.adapters.PreferencePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.string.BidiStringConverter;


/**
 * Preferences page for general (non-plug-in-specific) settings used by
 * the framework.
 */
final class PlatformsPreferencesPage extends AbstractPanel {

	PlatformsPreferencesPage(PreferencesContext context) {
		super(context);
		this.intializeLayout();
	}

	private void intializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();

		JCheckBox splashScreenCheckBox = this.buildCheckBox("PREFERENCES.PLATFORMS.VISIBLE_IN_PRODUCTION", this.buildVisibleInProductionModel());
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		this.add(splashScreenCheckBox, constraints);
		
		addHelpTopicId(this, "preferences.platforms");
	}

	// ***** "visible in production" check box
	private ButtonModel buildVisibleInProductionModel() {
		return new CheckBoxModelAdapter(this.buildBufferedVisibleInProductionAdapter());
	}

	private PropertyValueModel buildBufferedVisibleInProductionAdapter() {
		return new BufferedPropertyValueModel(this.buildVisibleInProductionAdapter(), this.getPreferencesContext().getBufferTrigger());
	}

	private PropertyValueModel buildVisibleInProductionAdapter() {
		PreferencePropertyValueModel adapter = new PreferencePropertyValueModel(this.preferences(), PlatformsPlugin.VISIBLE_IN_PRODUCTION_PREFERENCE, PlatformsPlugin.VISIBLE_IN_PRODUCTION_PREFERENCE_DEFAULT);
		adapter.setConverter(BidiStringConverter.BOOLEAN_CONVERTER);
		return adapter;
	}

}
