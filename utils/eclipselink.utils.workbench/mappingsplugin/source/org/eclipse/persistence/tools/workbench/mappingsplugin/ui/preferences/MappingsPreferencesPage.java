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
import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.adapters.PreferencePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;
import org.eclipse.persistence.tools.workbench.utility.string.BidiStringConverter;


/**
 * Preferences page for mappings settings.
 */
final class MappingsPreferencesPage extends AbstractPanel
{
	private ComponentAligner aligner;
	private final String NO_OPTION = TriStateBoolean.FALSE.toString();
	private final String PROMPT_OPTION = TriStateBoolean.UNDEFINED.toString();
	private final String YES_OPTION = TriStateBoolean.TRUE.toString();

	MappingsPreferencesPage(PreferencesContext context) {
		super(context);
		this.intializeLayout();
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

	private PropertyValueModel buildBufferedAdapter(PropertyValueModel valueHolder) {
		return new BufferedPropertyValueModel(valueHolder, getPreferencesContext().getBufferTrigger());
	}

	private ButtonModel buildButtonAdapter(PropertyValueModel valueHolder, String buttonValue) {
		return new RadioButtonModelAdapter(valueHolder, buttonValue);
	}

	private PropertyValueModel buildChangeQueryFormatAdapter() {
		PreferencePropertyValueModel adapter = new PreferencePropertyValueModel(
			this.preferences(),
			MappingsPlugin.CHANGE_QUERY_FORMAT_DO_NOT_SHOW_THIS_AGAIN_PREFERENCE);

		applyConverter(adapter);
		return buildBufferedAdapter(adapter);
	}

	private JComponent buildChangeQueryFormatPromptGroupBox() {
		GridBagConstraints constraints = new GridBagConstraints();
		PropertyValueModel changeQueryFormatHolder = buildChangeQueryFormatAdapter();

		JPanel container = buildGroupBox("PREFERENCES.MAPPINGS.QUERY.CHANGE_QUERY_FORMAT");

		// Yes 
		JRadioButton yesButton = buildRadioButton(
			"PREFERENCES.MAPPINGS.QUERY.CHANGE_QUERY_FORMAT.YES",
			buildButtonAdapter(changeQueryFormatHolder, YES_OPTION));

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
			"PREFERENCES.MAPPINGS.QUERY.CHANGE_QUERY_FORMAT.NO",
			buildButtonAdapter(changeQueryFormatHolder, NO_OPTION));

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
			"PREFERENCES.MAPPINGS.QUERY.CHANGE_QUERY_FORMAT.PROMPT",
			buildButtonAdapter(changeQueryFormatHolder, PROMPT_OPTION));

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

		addHelpTopicId(container, "preferences.mappings.query.changeQueryFormat");
		return container;
	}

	private PropertyValueModel buildChangeQueryTypeAdapter() {
		PreferencePropertyValueModel adapter = new PreferencePropertyValueModel(
			this.preferences(),
			MappingsPlugin.CHANGE_QUERY_TYPE_DO_NOT_THIS_SHOW_AGAIN_PREFERENCE);

		applyConverter(adapter);
		return buildBufferedAdapter(adapter);
	}

	private JComponent buildChangeQueryTypePromptGroupBox() {
		GridBagConstraints constraints = new GridBagConstraints();
		PropertyValueModel changeQueryTypeHolder = buildChangeQueryTypeAdapter();

		JPanel container = buildGroupBox("PREFERENCES.MAPPINGS.QUERY.CHANGE_QUERY_TYPE");

		// Yes 
		JRadioButton yesButton = buildRadioButton(
			"PREFERENCES.MAPPINGS.QUERY.CHANGE_QUERY_TYPE.YES",
			buildButtonAdapter(changeQueryTypeHolder, YES_OPTION));

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
			"PREFERENCES.MAPPINGS.QUERY.CHANGE_QUERY_TYPE.NO",
			buildButtonAdapter(changeQueryTypeHolder, NO_OPTION));

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
			"PREFERENCES.MAPPINGS.QUERY.CHANGE_QUERY_TYPE.PROMPT",
			buildButtonAdapter(changeQueryTypeHolder, PROMPT_OPTION));

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

		addHelpTopicId(container, "preferences.mappings.query.changeQueryType");
		return container;
	}

	private JPanel buildGroupBox(String titleKey) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(buildTitledBorder(titleKey));
		return panel;
	}

	private void intializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		aligner = new ComponentAligner();

		// Change Query Type
		JComponent changeQueryTypeGroupBox = buildChangeQueryTypePromptGroupBox();

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);

		this.add(changeQueryTypeGroupBox, constraints);

		// Change Query Format
		JComponent changeQueryFormatGroupBox = buildChangeQueryFormatPromptGroupBox();

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);

		this.add(changeQueryFormatGroupBox, constraints);

		// Filler until there are more options
		constraints.gridy      = 2;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		this.add(new Spacer(), constraints);

		addHelpTopicId(this, "preferences.mappings");
	}
}
