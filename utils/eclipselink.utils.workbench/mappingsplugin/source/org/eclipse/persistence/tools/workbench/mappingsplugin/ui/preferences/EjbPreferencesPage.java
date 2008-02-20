/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.ComponentAligner;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.adapters.PreferencePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;
import org.eclipse.persistence.tools.workbench.utility.string.BidiStringConverter;


/**
 * Preferences page for general (non-plug-in-specific) settings used by
 * the framework.
 */
final class EjbPreferencesPage extends AbstractPanel {

	private final String NO_OPTION = TriStateBoolean.FALSE.toString();
	private final String YES_OPTION = TriStateBoolean.TRUE.toString();
	private final String PROMPT_OPTION = TriStateBoolean.UNDEFINED.toString();
	private ComponentAligner aligner;
	
	EjbPreferencesPage(PreferencesContext context) {
		super(context);
		this.intializeLayout();
	}

	private JPanel buildWriteEjbJarXmlOnProjectSaveGroupBox() {
		GridBagConstraints constraints = new GridBagConstraints();
		PropertyValueModel writeEjbJarXmlOnSaveAdapter = buildWriteEjbJarXmlAdapter();

		JPanel container = buildGroupBox("PREFERENCES.MAPPINGS.EJB.WRITE_EJB_JAR_XML");

		// Always
		JRadioButton alwaysButton = buildRadioButton(
			"PREFERENCES.MAPPINGS.EJB.ALWAYS_WRITE_EJB_JAR_XML", 
			buildButtonAdapter(writeEjbJarXmlOnSaveAdapter, ProjectNode.WRITE_EJB_JAR_XML_ON_SAVE_PREFERENCE_ALWAYS)
		);

		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		container.add(alwaysButton, constraints);
		aligner.add(alwaysButton);

		// Never
		JRadioButton neverButton = buildRadioButton(
			"PREFERENCES.MAPPINGS.EJB.NEVER_WRITE_EJB_JAR_XML", 
			buildButtonAdapter(writeEjbJarXmlOnSaveAdapter, ProjectNode.WRITE_EJB_JAR_XML_ON_SAVE_PREFERENCE_NEVER)
		);

		constraints.gridx			= 1;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 20, 0, 0);

		container.add(neverButton, constraints);
		aligner.add(neverButton);

		// Prompt
		JRadioButton promptButton = buildRadioButton(
			"PREFERENCES.MAPPINGS.EJB.ALWAYS_PROMPT_WRITE_EJB_JAR_XML", 
			buildButtonAdapter(writeEjbJarXmlOnSaveAdapter, ProjectNode.WRITE_EJB_JAR_XML_ON_SAVE_PREFERENCE_PROMPT)
		);

		constraints.gridx			= 2;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 20, 0, 0);

		container.add(promptButton, constraints);
		aligner.add(promptButton);

		addHelpTopicId(container, "preferences.ejb");
		return container;
	}


	private PropertyValueModel buildWriteEjbJarXmlAdapter() {
		PreferencePropertyValueModel adapter =  
			new PreferencePropertyValueModel(
				this.preferences(), 
				ProjectNode.WRITE_EJB_JAR_XML_ON_SAVE_PREFERENCE, 
				ProjectNode.WRITE_EJB_JAR_XML_ON_SAVE_PREFERENCE_DEFAULT
			);
		adapter.setConverter(this.buildConverter());
		
		return buildBufferedAdapter(adapter);
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
			
			public String toString() {
				return "AlwaysWriteEjbJarXmlOnSave-BidiStringConverter";
			}
		};
	}

	private JPanel buildGroupBox(String titleKey) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(buildTitledBorder(titleKey));
		return panel;
	}

	private JComponent buildRemoveEjbInfoPromptGroupBox() {
		GridBagConstraints constraints = new GridBagConstraints();
		PropertyValueModel removeEjbInfoHolder = buildRemoveEjbInfoAdapter();

		JPanel container = buildGroupBox("PREFERENCES.MAPPINGS.EJB.REMOVE_EJB_INFO");

		// Yes 
		JRadioButton yesButton = buildRadioButton(
			"PREFERENCES.MAPPINGS.EJB.REMOVE_EJB_INFO.YES",
			buildButtonAdapter(removeEjbInfoHolder, YES_OPTION));

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		container.add(yesButton, constraints);
		aligner.add(yesButton);

		// No 
		JRadioButton noButton = buildRadioButton(
			"PREFERENCES.MAPPINGS.EJB.REMOVE_EJB_INFO.NO",
			buildButtonAdapter(removeEjbInfoHolder, NO_OPTION));

		constraints.gridx      = 1;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 20, 0, 0);

		container.add(noButton, constraints);
		aligner.add(noButton);

		// Prompt 
		JRadioButton promptButton = buildRadioButton(
			"PREFERENCES.MAPPINGS.EJB.REMOVE_EJB_INFO.PROMPT",
			buildButtonAdapter(removeEjbInfoHolder, PROMPT_OPTION));

		constraints.gridx      = 2;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 20, 0, 0);

		container.add(promptButton, constraints);
		aligner.add(promptButton);

		addHelpTopicId(this, "preferences.ejb.removeEjbInfo");
		return container;
	}

	private JComponent buildRemoveEjb2xInfoPromptGroupBox() {
		GridBagConstraints constraints = new GridBagConstraints();
		PropertyValueModel removeEjb2xInfoHolder = buildRemoveEjb2xInfoAdapter();

		JPanel container = buildGroupBox("PREFERENCES.MAPPINGS.EJB.REMOVE_EJB_2X_INFO");

		// Yes 
		JRadioButton yesButton = buildRadioButton(
			"PREFERENCES.MAPPINGS.EJB.REMOVE_EJB_2X_INFO.YES",
			buildButtonAdapter(removeEjb2xInfoHolder, YES_OPTION));

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		container.add(yesButton, constraints);
		aligner.add(yesButton);

		// No 
		JRadioButton noButton = buildRadioButton(
			"PREFERENCES.MAPPINGS.EJB.REMOVE_EJB_2X_INFO.NO",
			buildButtonAdapter(removeEjb2xInfoHolder, NO_OPTION));

		constraints.gridx      = 1;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 20, 0, 0);

		container.add(noButton, constraints);
		aligner.add(noButton);

		// Prompt 
		JRadioButton promptButton = buildRadioButton(
			"PREFERENCES.MAPPINGS.EJB.REMOVE_EJB_2X_INFO.PROMPT",
			buildButtonAdapter(removeEjb2xInfoHolder, PROMPT_OPTION));

		constraints.gridx      = 2;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 20, 0, 0);

		container.add(promptButton, constraints);
		aligner.add(promptButton);

		addHelpTopicId(this, "preferences.ejb.removeEjb2xInfo");
		return container;
	}

	private void intializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		aligner = new ComponentAligner();

		// Write ejb-jar.xml
		JComponent writeEjbJarXmlGroupBox = buildWriteEjbJarXmlOnProjectSaveGroupBox();

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);

		this.add(writeEjbJarXmlGroupBox, constraints);

		// Remove Ejb Info
		JComponent removeEjbInfoPromptGroupBox = buildRemoveEjbInfoPromptGroupBox();

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);

		this.add(removeEjbInfoPromptGroupBox, constraints);

		// Remove Ejb 2.x Info
		JComponent removeEjb2xInfoPromptGroupBox = buildRemoveEjb2xInfoPromptGroupBox();

		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);

		this.add(removeEjb2xInfoPromptGroupBox, constraints);

		addHelpTopicId(this, "preferences.mappings");
	}

	private PropertyValueModel buildRemoveEjbInfoAdapter() {
		PreferencePropertyValueModel adapter = new PreferencePropertyValueModel(
			this.preferences(),
			MappingsPlugin.REMOVE_EJB_INFO_DO_NOT_THIS_SHOW_AGAIN_PREFERENCE);

		applyConverter(adapter);
		return buildBufferedAdapter(adapter);
	}

	private PropertyValueModel buildRemoveEjb2xInfoAdapter() {
		PreferencePropertyValueModel adapter = new PreferencePropertyValueModel(
			this.preferences(),
			MappingsPlugin.REMOVE_EJB_2X_INFO_DO_NOT_THIS_SHOW_AGAIN_PREFERENCE);

		applyConverter(adapter);
		return buildBufferedAdapter(adapter);
	}

	private void applyConverter(PreferencePropertyValueModel adapter) {
		adapter.setConverter(new BidiStringConverter() {
			public Object convertToObject(String value) {
				return convertToString(value);
			}

			public String convertToString(Object value) {
				if (YES_OPTION.equals(value)) {
					return YES_OPTION;
				}
				if (NO_OPTION.equals(value)) {
					return NO_OPTION;
				}
				else {
					return PROMPT_OPTION;
				}
			}
		});
	}

	private ButtonModel buildButtonAdapter(PropertyValueModel valueHolder, String buttonValue) {
		return new RadioButtonModelAdapter(valueHolder, buttonValue);
	}

	private PropertyValueModel buildBufferedAdapter(PropertyValueModel valueHolder) {
		return new BufferedPropertyValueModel(valueHolder, getPreferencesContext().getBufferTrigger());
	}
}
