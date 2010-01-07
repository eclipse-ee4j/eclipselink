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
package org.eclipse.persistence.tools.workbench.mappingsplugin;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractValidatingDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.AccessibleTitledPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisLoginSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.DefaultSPIManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.SPIManager;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.PlatformComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


public final class ProjectCreationDialog extends AbstractValidatingDialog {
	public static final String DATABASE_PLATFORM_PREFERENCE = "database platform";
	public static final String DATABASE_PLATFORM_PREFERENCE_DEFAULT = "Oracle11";


	private JTextField nameTextField;
	
	private JRadioButton relationalRadioButton;
	private JRadioButton xmlRadioButton;
	private JRadioButton eisRadioButton;

	private SimplePropertyValueModel databasePlatformHolder;
	
	private ComboBoxModel j2cAdapterComboBoxModel;
				

	// ************ constructors / initialization ****************
	
	ProjectCreationDialog(WorkbenchContext context) {
		super(context);
	}
		
	protected void initialize() {
		super.initialize();
		getOKAction().setEnabled(false);
	}
		
	protected Component buildMainPanel() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		setTitle(resourceRepository().getString("PROJECT_CREATION_DIALOG_TITLE"));
		
		JPanel namePanel = buildProjectNamePanel();
		helpManager().addTopicID(namePanel, helpTopicId() + ".name");
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 0, 0, 0);
		mainPanel.add(namePanel, constraints);
	
		
		//Directions label
		JPanel dataSourcePanel = buildDataSourcePanel();
		helpManager().addTopicID(dataSourcePanel, helpTopicId() + ".datasource");
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(10, 0, 10, 0);
		mainPanel.add(dataSourcePanel, constraints);
		
		return mainPanel;
	}
	
	private JPanel buildProjectNamePanel() {
		JPanel namePanel = new JPanel(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
	
		JLabel projectNameLabel = SwingComponentFactory.buildLabel("PROJECT_NAME_LABEL", resourceRepository());
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		namePanel.add(projectNameLabel, constraints);
	
		nameTextField = new JTextField(35);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(1, 0, 0, 0);
		namePanel.add(nameTextField, constraints);
		projectNameLabel.setLabelFor(nameTextField);
	
		nameTextField.getDocument().addDocumentListener(
			new DocumentListener() {
				public void insertUpdate(DocumentEvent e) {
					updateDialog();
				}
				public void removeUpdate(DocumentEvent e) {
					updateDialog();
				}
				public void changedUpdate(DocumentEvent e) {
					updateDialog();
				}
			}
		);
		
		return namePanel;
	}

	private JPanel buildDataSourcePanel() {
		JPanel datasourcePanel = new JPanel(new GridBagLayout());
		datasourcePanel.setBorder(BorderFactory.createTitledBorder(resourceRepository().getString("DATASOURCE_LABEL")));
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		ButtonGroup buttonGroup = new ButtonGroup();
		relationalRadioButton = new JRadioButton();
		relationalRadioButton.setText(resourceRepository().getString("RELATIONAL_RADIO_BUTTON"));
		relationalRadioButton.setMnemonic(resourceRepository().getMnemonic("RELATIONAL_RADIO_BUTTON"));

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 5, 0, 0);
		datasourcePanel.add(relationalRadioButton, constraints);
		buttonGroup.add(relationalRadioButton);
		
		relationalRadioButton.setSelected(true);

		JPanel relationalPanel = buildRelationalPanel();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 5, 0, 0);		
		datasourcePanel.add(relationalPanel, constraints);

		eisRadioButton = new JRadioButton();
		eisRadioButton.setText(resourceRepository().getString("EIS_RADIO_BUTTON"));
		eisRadioButton.setMnemonic(resourceRepository().getMnemonic("EIS_RADIO_BUTTON"));
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(5, 5, 0, 0);
		datasourcePanel.add(eisRadioButton, constraints);
		buttonGroup.add(eisRadioButton);
		
		JPanel eisPanel = buildEisPanel();
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(0, 5, 0, 0);		
		datasourcePanel.add(eisPanel, constraints);

		xmlRadioButton = new JRadioButton();
		xmlRadioButton.setText(resourceRepository().getString("XML_RADIO_BUTTON"));
		xmlRadioButton.setMnemonic(resourceRepository().getMnemonic("XML_RADIO_BUTTON"));
		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(5, 5, 0, 0);
		datasourcePanel.add(xmlRadioButton, constraints);
		buttonGroup.add(xmlRadioButton);

		return datasourcePanel;
	}
	private JPanel buildRelationalPanel() {
		GridBagConstraints constraints = new GridBagConstraints();
		
		JPanel relationalPanel = new AccessibleTitledPanel(new GridBagLayout());
		int offset = SwingTools.checkBoxIconWidth();

		// Platform label
		final JLabel platformLabel = new JLabel(resourceRepository().getString("DATABASE_PLATFORM_LABEL"));
		platformLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("DATABASE_PLATFORM_LABEL"));
		platformLabel.setDisplayedMnemonicIndex(resourceRepository().getMnemonicIndex("DATABASE_PLATFORM_LABEL"));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, offset, 0, 0);
		relationalPanel.add(platformLabel, constraints);

		// Database platform drop-down
		String platformName = preferences().get(DATABASE_PLATFORM_PREFERENCE, DATABASE_PLATFORM_PREFERENCE_DEFAULT);
		databasePlatformHolder = new SimplePropertyValueModel();
		databasePlatformHolder.setValue(DatabasePlatformRepository.getDefault().platformNamed(platformName));

		final JComboBox databasePlatformChooser = PlatformComponentFactory.buildPlatformChooser(databasePlatformHolder);
		
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 5, 0, 5);
		relationalPanel.add(databasePlatformChooser, constraints);
		platformLabel.setLabelFor(databasePlatformChooser);

		relationalRadioButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				databasePlatformChooser.setEnabled(relationalRadioButton.isSelected());
				platformLabel.setEnabled(relationalRadioButton.isSelected());
			}
		});
		
		return relationalPanel;
	}

	private JPanel buildEisPanel() {
		GridBagConstraints constraints = new GridBagConstraints();
		
		JPanel eisPanel = new JPanel(new GridBagLayout());
		int offset = SwingTools.checkBoxIconWidth();
		
		// Platform label
		final JLabel adapterLabel = new JLabel(resourceRepository().getString("EIS_PLATFORM_LABEL"));
		adapterLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("DATABASE_PLATFORM_LABEL"));
		adapterLabel.setDisplayedMnemonicIndex(resourceRepository().getMnemonicIndex("DATABASE_PLATFORM_LABEL"));
		adapterLabel.setEnabled(false);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, offset, 0, 0);
		eisPanel.add(adapterLabel, constraints);

		// Database platform drop-down
		j2cAdapterComboBoxModel = buildJ2CAdaptersComboBoxModel();
		final JComboBox adapterComboBox = new JComboBox(j2cAdapterComboBoxModel);

		String platformName = preferences().get(MWEisLoginSpec.PLATFORM_PREFERENCE, MWEisLoginSpec.PLATFORM_PREFERENCE_DEFAULT);

		adapterComboBox.setSelectedItem(platformName);
		adapterComboBox.setEnabled(false);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 5, 0, 5);
		eisPanel.add(adapterComboBox, constraints);
		adapterLabel.setLabelFor(adapterComboBox);

	
		eisRadioButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				adapterComboBox.setEnabled(eisRadioButton.isSelected());
				adapterLabel.setEnabled(eisRadioButton.isSelected());
			}

		});
		
		return eisPanel;
	}

	private void updateDialog() {
		String name = nameTextField.getText().trim();

		if (name.length() == 0) {
			setErrorMessageKey("PROJECT_CREATION_DIALOG_NO_FILE_NAME");
			getOKAction().setEnabled(false);
		}
		else if (FileTools.fileNameIsInvalid(name)) {
			setErrorMessageKey("PROJECT_CREATION_DIALOG_INVALID_FILE_NAME");
			getOKAction().setEnabled(false);
		}
		else {
			clearErrorMessage();
			getOKAction().setEnabled(true);
		}
	}

	private ComboBoxModel buildJ2CAdaptersComboBoxModel() {
		return new DefaultComboBoxModel(CollectionTools.vector(supportedJ2CAdapters()));
	}
		
	MWProject getProject() {
		String projectName = nameTextField.getText().trim();
		SPIManager spiManager = new DefaultSPIManager(this.preferences(), projectName);
		if (relationalRadioButton.isSelected()) {
			DatabasePlatform platform = (DatabasePlatform) databasePlatformHolder.getValue();
			preferences().put(DATABASE_PLATFORM_PREFERENCE, platform.getName());
			return new MWRelationalProject(projectName, spiManager, platform);
		}
		else if (xmlRadioButton.isSelected()) {
			return new MWOXProject(projectName, spiManager);
		}
		else {
			String platformName = (String) j2cAdapterComboBoxModel.getSelectedItem();
			preferences().put(MWEisLoginSpec.PLATFORM_PREFERENCE, platformName);
			return new MWEisProject(projectName, platformName, spiManager);
		}		
	}
		
	private Iterator supportedJ2CAdapters() {
		return MWEisLoginSpec.j2CAdapterNames();
	}
		
	// ********** opening **********

	protected String helpTopicId() {
		return "dialog.projectCreation";
	}

	protected Component initialFocusComponent() {
		return nameTextField;
	}
}
