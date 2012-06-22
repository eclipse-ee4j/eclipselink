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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.preferences;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ZeroArgConstructorPreference;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.adapters.PreferencePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.string.BidiStringConverter;


/**
 * Preferences page for general (non-plug-in-specific) settings used by
 * the framework.
 */
final class ClassPreferencesPage extends AbstractPanel {

	private PropertyValueModel bufferedMaintainZeroArgumentConstructorAdapter;

	ClassPreferencesPage(PreferencesContext context) {
		super(new BorderLayout(), context);
		this.intializeLayout();
	}

	private void intializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel scrollPaneView = new JPanel(new GridBagLayout());

		JScrollPane scrollPane = new JScrollPane(scrollPaneView);
		scrollPane.getVerticalScrollBar().setBlockIncrement(20);
		scrollPane.setBorder(null);
		scrollPane.setViewportBorder(null);
		add(scrollPane, BorderLayout.CENTER);

		JPanel container = new JPanel(new GridBagLayout());
		container.setBorder(BorderFactory.createCompoundBorder
		(
			buildTitledBorder("PREFERENCES.MAPPINGS.CLASS.MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR"),
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		));

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(5, 5, 5, 5);
		scrollPaneView.add(container, constraints);

		// Message area
		LabelArea label = new LabelArea(resourceRepository().getString("PREFERENCES.MAPPINGS.CLASS.MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR_EXPLANATION"));
		label.setScrollable(true);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(0, 5, 0, 5);
		container.add(label, constraints);

		JRadioButton yesRadioButton = 
			buildRadioButton(
				"PREFERENCES.MAPPINGS.CLASS.MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR_YES", 
				buildRadioButtonModel(ZeroArgConstructorPreference.YES)
			);
		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 5, 0, 5);
		container.add(yesRadioButton, constraints);
		
		JRadioButton noRadioButton = 
			buildRadioButton(
				"PREFERENCES.MAPPINGS.CLASS.MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR_NO", 
				buildRadioButtonModel(ZeroArgConstructorPreference.NO)
			);
		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 5, 0, 5);
		container.add(noRadioButton, constraints);
		
		JRadioButton promptRadioButton = 
			buildRadioButton(
				"PREFERENCES.MAPPINGS.CLASS.MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR_PROMPT", 
				buildRadioButtonModel(ZeroArgConstructorPreference.PROMPT)
			);
		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 5, 5, 5);
		container.add(promptRadioButton, constraints);
		
		// last refresh
		JCheckBox lastRefreshCheckBox = this.buildCheckBox("PREFERENCES.MAPPINGS.CLASS.PERSIST_LAST_REFRESH_TIMESTAMP", this.buildPersistLastRefreshModel());
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		add(lastRefreshCheckBox, BorderLayout.NORTH);
		
		addHelpTopicId(this, "preferences.class");
	}

	private ButtonModel buildRadioButtonModel(Object buttonValue) {
		return new RadioButtonModelAdapter(buildBufferedMaintainZeroArgumentConstructorAdapter(), buttonValue);
	}
	
	private PropertyValueModel buildBufferedMaintainZeroArgumentConstructorAdapter() {
		if (bufferedMaintainZeroArgumentConstructorAdapter == null) {
			bufferedMaintainZeroArgumentConstructorAdapter = new BufferedPropertyValueModel(this.buildMaintainZeroArgumentConstructorAdapter(), this.getPreferencesContext().getBufferTrigger());
		}
		return bufferedMaintainZeroArgumentConstructorAdapter;
	}	
	
	private PropertyValueModel buildMaintainZeroArgumentConstructorAdapter() {
		PreferencePropertyValueModel adapter =  
			new PreferencePropertyValueModel(
				this.preferences(), 
				ZeroArgConstructorPreference.MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR_PREFERENCE, 
				ZeroArgConstructorPreference.DEFAULT
			);
		adapter.setConverter(this.buildConverter());
		return adapter;
	}
	
	private BidiStringConverter buildConverter() {
		return new BidiStringConverter() {
			/** Cast the object to a string. */
			public String convertToString(Object o) {
				return (String) o;
			}
			
			/** Return the correct option for the string, if it exists. */
			public Object convertToObject(String s) {
				return s.intern();
			}
			
			@Override
			public String toString() {
				return "MaintainZeroArgumentConstructor-BidiStringConverter";
			}
		};
	}
	
	// ***** persiste refresh check box
	private ButtonModel buildPersistLastRefreshModel() {
		return new CheckBoxModelAdapter(this.buildBufferedPersistLastRefreshAdapter());
	}

	private PropertyValueModel buildBufferedPersistLastRefreshAdapter() {
		return new BufferedPropertyValueModel(this.buildPersistLastRefreshAdapter(), this.getPreferencesContext().getBufferTrigger());
	}

	private PropertyValueModel buildPersistLastRefreshAdapter() {
		PreferencePropertyValueModel adapter = new PreferencePropertyValueModel(this.preferences(), MWClassRepository.PERSIST_LAST_REFRESH_PREFERENCE, MWClassRepository.PERSIST_LAST_REFRESH_PREFERENCE_DEFAULT);
		adapter.setConverter(BidiStringConverter.BOOLEAN_CONVERTER);
		return adapter;
	}


}
