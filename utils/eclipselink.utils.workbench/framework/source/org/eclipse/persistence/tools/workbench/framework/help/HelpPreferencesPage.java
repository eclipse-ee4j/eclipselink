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
package org.eclipse.persistence.tools.workbench.framework.help;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.FileChooserPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.adapters.PreferencePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.string.BidiStringConverter;


/**
 * Preferences node for general help settings used by
 * the help manager and OHJ.
 */
final class HelpPreferencesPage extends AbstractPanel {

	private PropertyValueModel bufferedHostLocalAdapter;
	
	HelpPreferencesPage(PreferencesContext context) {
		super(new BorderLayout(), context);
		this.intializeLayout();
	}

	private void intializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();
		int offset = SwingTools.checkBoxIconWidth();

		JPanel scrollPaneView = new JPanel(new GridBagLayout());

		JScrollPane scrollPane = new JScrollPane(scrollPaneView);
		scrollPane.getVerticalScrollBar().setBlockIncrement(20);
		scrollPane.setBorder(null);
		scrollPane.setViewportBorder(null);
		add(scrollPane, BorderLayout.CENTER);

		JPanel container = new JPanel(new GridBagLayout());
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

		// display welcome screen check box
		JCheckBox welcomeScreenCheckBox = this.buildCheckBox("PREFERENCES.GENERAL.HELP.DISPLAY_WELCOME", this.buildDisplayWelcomeModel());
		addHelpTopicId(welcomeScreenCheckBox, this.helpTopicId() + ".welcome");
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
//		container.add(welcomeScreenCheckBox, constraints);

		// external browser label
		JLabel browserLabel = this.buildLabel("PREFERENCES.GENERAL.HELP.BROWSER_CHOOSER");
		addHelpTopicId(browserLabel, this.helpTopicId() + ".browser");
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 5, 0, 0);

		container.add(browserLabel, constraints);

		// external browser file chooser
		JPanel browserChooser = new FileChooserPanel(
			this.getPreferencesContext(),
			this.buildBrowserHolder(),
			"PREFERENCES.GENERAL.HELP.BROWSER_CHOOSER",
			"PREFERENCES.GENERAL.HELP.BROWSER_CHOOSER_BUTTON",
			JFileChooser.FILES_ONLY,
			true) {

			protected boolean labelVisible() {
				return false;
			}
		};
		addHelpTopicId(browserChooser, this.helpTopicId() + ".browser");
		constraints.gridx		= 0;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(1, offset, 0, 5);
		container.add(browserChooser, constraints);
		browserLabel.setLabelFor(browserChooser);
								
		// local help label
		JLabel localLabel = this.buildLabel("PREFERENCES.GENERAL.HELP.LOCAL");
		addHelpTopicId(localLabel, this.helpTopicId() + ".local");
		constraints.gridx		= 0;
		constraints.gridy		= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 5, 0, 0);

//		container.add(localLabel, constraints);

		// local content file chooser
		JPanel localFileChooser = new FileChooserPanel(
			this.getPreferencesContext(),
			this.buildLocalFileHolder(),
			"PREFERENCES.GENERAL.HELP.LOCAL",
			"PREFERENCES.GENERAL.HELP.LOCAL_BROWSER_CHOOSER_BUTTON",
			JFileChooser.FILES_ONLY)
		{
			protected boolean labelVisible() {
				return false;
			}
		};
        addHelpTopicId(localFileChooser, this.helpTopicId() + ".local");
		constraints.gridx		= 0;
		constraints.gridy		= 4;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(0, offset, 0, 5);
		
//		container.add(localFileChooser, constraints);
		
        addHelpTopicId(this, helpTopicId());
	}

	// ***** display welcome check box
	private ButtonModel buildDisplayWelcomeModel() {
		return new CheckBoxModelAdapter(this.buildBufferedDisplayWelcomeAdapter());
	}

	private PropertyValueModel buildBufferedDisplayWelcomeAdapter() {
		return new BufferedPropertyValueModel(this.buildDisplayWelcomeAdapter(), this.getPreferencesContext().getBufferTrigger());
	}

	private PropertyValueModel buildDisplayWelcomeAdapter() {
		PreferencePropertyValueModel adapter = new PreferencePropertyValueModel(this.preferences(), HelpFacade.DISPLAY_WELCOME_PREFERENCE, HelpFacade.DISPLAY_WELCOME_PREFERENCE_DEFAULT);
		adapter.setConverter(BidiStringConverter.BOOLEAN_CONVERTER);
		return adapter;
	}

	// ***** local file chooser
	private PropertyValueModel buildLocalFileHolder() {
		return new BufferedPropertyValueModel(this.buildLocalFileAdapter(), this.getPreferencesContext().getBufferTrigger());
	}
	
	private PropertyValueModel buildLocalFileAdapter() {
		return new PreferencePropertyValueModel(this.preferences(), HelpFacade.LOCAL_FILE_PREFERENCE, HelpFacade.LOCAL_FILE_PREFERENCE_DEFAULT);
	}
		
	// ***** browser file chooser
	private PropertyValueModel buildBrowserHolder() {
		return new BufferedPropertyValueModel(this.buildBrowserAdapter(), this.getPreferencesContext().getBufferTrigger());	
	}
	
	private PropertyValueModel buildBrowserAdapter() {
		PreferencePropertyValueModel adapter = new PreferencePropertyValueModel(this.preferences(), DefaultHelpManager.BROWSER_PREFERENCE);
		adapter.setConverter(BidiStringConverter.DEFAULT_INSTANCE);
		return adapter;
	}


	public String helpTopicId(){
		return "preferences.general.help";
	}

}
