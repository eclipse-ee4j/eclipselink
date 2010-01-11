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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


public final class JaxbProjectCreationDialog 
	extends AbstractDialog 
{
	private PropertyValueModel inputSchemaFileValue;
	private PropertyValueModel inputCustomizationFileValue;
	
	private PropertyValueModel outputMasterDirectoryValue;
	private PropertyValueModel outputSourceDirectoryValue;
	private PropertyValueModel outputWorkbenchProjectDirectoryValue;
	private PropertyValueModel outputInterfacePackageNameValue;
	private PropertyValueModel outputImplClassPackageNameValue;
	
	
	JaxbProjectCreationDialog(WorkbenchContext context) {
		super(context);
	}
	
	protected void initialize() {
		super.initialize();
		
		this.inputSchemaFileValue = this.buildInputSchemaFileValue();
		this.inputCustomizationFileValue = this.buildInputCustomizationFileValue();
		
		this.outputMasterDirectoryValue = this.buildOutputMasterDirectoryValue();
		this.outputSourceDirectoryValue = this.buildOutputSourceDirectoryValue();
		this.outputWorkbenchProjectDirectoryValue = this.buildOutputWorkbenchProjectDirectoryValue();
		this.outputInterfacePackageNameValue = this.buildOutputInterfacePackageNameValue();
		this.outputImplClassPackageNameValue = this.buildOutputImplClassPackageNameValue();
	
		getOKAction().setEnabled(false);
	}
	
	private PropertyValueModel buildInputSchemaFileValue() {
		PropertyValueModel value = new SimplePropertyValueModel("");
		value.addPropertyChangeListener(ValueModel.VALUE, this.buildGeneralSettingChangeListener());
		return value;
	}
	
	private PropertyValueModel buildInputCustomizationFileValue() {
		// optional so no listener needed
		return new SimplePropertyValueModel("");
	}
	
	private PropertyValueModel buildOutputMasterDirectoryValue() {
		// only used as a helper for the three other output dirs so no general listener
		PropertyValueModel value = new SimplePropertyValueModel("");
		value.addPropertyChangeListener(ValueModel.VALUE, this.buildGeneralSettingChangeListener());
		return value;
	}
	
	private PropertyValueModel buildOutputSourceDirectoryValue() {
		PropertyValueModel value = new SimplePropertyValueModel("." + File.separator + "source");
		value.addPropertyChangeListener(ValueModel.VALUE, this.buildGeneralSettingChangeListener());
		return value;
	}
	
	private PropertyValueModel buildOutputWorkbenchProjectDirectoryValue() {
		PropertyValueModel value = new SimplePropertyValueModel("." + File.separator + "mw");
		value.addPropertyChangeListener(ValueModel.VALUE, this.buildGeneralSettingChangeListener());
		return value;
	}
	
	private PropertyValueModel buildOutputInterfacePackageNameValue() {
		PropertyValueModel value = new SimplePropertyValueModel("");
		return value;
	}
	
	private PropertyValueModel buildOutputImplClassPackageNameValue() {
		PropertyValueModel value = new SimplePropertyValueModel("");
		this.outputInterfacePackageNameValue.addPropertyChangeListener(ValueModel.VALUE, this.buildInterfacePackageNameListener());
		return value;
	}
	
	/** 
	 * Sets the impl class package name to the interface package name, unless
	 * the impl class package name is already different 
	 */
	private PropertyChangeListener buildInterfacePackageNameListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				String oldInterfacePackageName = (String) evt.getOldValue();
				String newInterfacePackageName = (String) evt.getNewValue();
				String implClassPackageName = JaxbProjectCreationDialog.this.getOutputImplClassPackageName();
				
				if (implClassPackageName.equals(oldInterfacePackageName)) {
					JaxbProjectCreationDialog.this.outputImplClassPackageNameValue.setValue(newInterfacePackageName);
				}
			}
		};
	}
	
	private PropertyChangeListener buildGeneralSettingChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				JaxbProjectCreationDialog.this.updateOKAction();
			}
		};
	}
	
	protected String helpTopicId() {
		return "dialog.newJaxbProjectDialog";
	}
	
	protected Component buildMainPanel() {	
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		this.setTitle(this.resourceRepository().getString("JAXB_PROJECT_DIALOG_TITLE"));
		
		JPanel fromPanel = this.buildFromPanel();
		constraints.gridx 		= 0;
		constraints.gridy 		= 0;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(0, 0, 0, 0);
		mainPanel.add(fromPanel, constraints);
		
		JPanel toPanel = this.buildToPanel();
		constraints.gridx 		= 0;
		constraints.gridy 		= 1;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 1;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.PAGE_START;
		constraints.insets 		= new Insets(5, 0, 0, 0);
		mainPanel.add(toPanel, constraints);
				
		return mainPanel;
	}
	
	private JPanel buildFromPanel() {
		JPanel fromPanel = new JPanel(new GridBagLayout());
		fromPanel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(resourceRepository().getString("FROM")),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)
			)
		);
		
		GridBagConstraints constraints = new GridBagConstraints();		
		
		
		// *** input schema file ***
		
		JLabel inputSchemaFileLabel = this.buildInputSchemaFileLabel();
		constraints.gridx 		= 0;
		constraints.gridy 		= 0;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.NONE;
		constraints.anchor 		= GridBagConstraints.LINE_START;
		constraints.insets 		= new Insets(0, 0, 0, 0);
		fromPanel.add(inputSchemaFileLabel, constraints);

		JTextField inputSchemaFileTextField = this.buildInputSchemaFileTextField();
		inputSchemaFileLabel.setLabelFor(inputSchemaFileTextField);
		constraints.gridx 		= 0;
		constraints.gridy 		= 1;
		constraints.gridwidth 	= 2;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(0, 0, 0, 0);
		fromPanel.add(inputSchemaFileTextField, constraints);
		
		JButton inputSchemaFileButton = this.buildInputSchemaFileButton();
		constraints.gridx 		= 2;
		constraints.gridy 		= 1;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.NONE;
		constraints.anchor 		= GridBagConstraints.LINE_END;
		constraints.insets 		= new Insets(0, 5, 0, 0);
		fromPanel.add(inputSchemaFileButton, constraints);
		
		// *** end input schema file ***
		
		
		// *** input customization file ***

		JLabel inputCustomizationFileLabel = this.buildInputCustomizationFileLabel();
		constraints.gridx 		= 0;
		constraints.gridy 		= 2;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.NONE;
		constraints.anchor 		= GridBagConstraints.LINE_START;
		constraints.insets 		= new Insets(5, 0, 0, 0);
		fromPanel.add(inputCustomizationFileLabel, constraints);

		JTextField inputCustomizationFileTextField = this.buildInputCustomizationFileTextField();
		inputCustomizationFileLabel.setLabelFor(inputCustomizationFileTextField);
		constraints.gridx 		= 0;
		constraints.gridy 		= 3;
		constraints.gridwidth 	= 2;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(1, 0, 0, 0);
		fromPanel.add(inputCustomizationFileTextField, constraints);
		
		JButton inputCustomizationFileButton = this.buildInputCustomizationFileButton();
		constraints.gridx 		= 2;
		constraints.gridy 		= 3;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.NONE;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(1, 5, 0, 0);
		fromPanel.add(inputCustomizationFileButton, constraints);
		
		// *** end input customization file ***
			
		this.helpManager().addTopicID(fromPanel, this.helpTopicId() + ".From");
						
		return fromPanel;
	}
	
	private JLabel buildInputSchemaFileLabel() {
		JLabel label = SwingComponentFactory.buildLabel("INPUT_SCHEMA_FILE_LABEL", this.resourceRepository());
		this.helpManager().addTopicID(label, this.helpTopicId() + ".inputSchema");
		return label;
	}
	
	private JTextField buildInputSchemaFileTextField() {
		JTextField textField = new JTextField(60);
		textField.setDocument(this.buildInputSchemaFileDocument());
		this.helpManager().addTopicID(textField, this.helpTopicId() + ".inputSchema");
		return textField;
	}
	
	private Document buildInputSchemaFileDocument() {
		return new DocumentAdapter(this.inputSchemaFileValue);
	}

	private JButton buildInputSchemaFileButton() {
		JButton button = SwingComponentFactory.buildButton("BROWSE_BUTTON_1", this.resourceRepository());
		button.addActionListener(this.buildFileBrowseAction(this.inputSchemaFileValue, this.buildXsdFileFilter()));
		this.helpManager().addTopicID(button, this.helpTopicId() + ".inputSchema");
		return button;
	}
	
	private FileFilter buildXsdFileFilter() {
		return new FileFilter() {		
			public boolean accept(File f) {
				return (f.isDirectory() || this.validSchemaExtension(f));
			}
			
			public String getDescription() {
				return resourceRepository().getString("XSD_FILE_FILTER");
			}
			
			private boolean validSchemaExtension(File f) {
				return FileTools.extension(f).equalsIgnoreCase(".xsd");
			}
		};
	}
	
	private JLabel buildInputCustomizationFileLabel() {
		JLabel label = SwingComponentFactory.buildLabel("INPUT_CUSTOMIZATION_FILE_LABEL", this.resourceRepository());
		this.helpManager().addTopicID(label, this.helpTopicId() + ".customizationFile");
		return label;
	}
	
	private JTextField buildInputCustomizationFileTextField() {
		JTextField textField = new JTextField(60);
		textField.setDocument(this.buildInputCustomizationFileDocument());
		this.helpManager().addTopicID(textField, this.helpTopicId() + ".customizationFile");
		return textField;
	}
	
	private Document buildInputCustomizationFileDocument() {
		return new DocumentAdapter(this.inputCustomizationFileValue);
	}
	
	private JButton buildInputCustomizationFileButton() {
		JButton button = SwingComponentFactory.buildButton("BROWSE_BUTTON_2", this.resourceRepository());
		button.addActionListener(this.buildFileBrowseAction(this.inputCustomizationFileValue, this.buildXmlFileFilter()));
		this.helpManager().addTopicID(button, this.helpTopicId() + ".customizationFile");
		return button;
	}
	
	private FileFilter buildXmlFileFilter() {
		return new FileFilter() {		
			public boolean accept(File f) {
				return (f.isDirectory() || this.validXmlExtension(f));
			}
			
			public String getDescription() {
				return resourceRepository().getString("XML_FILE_FILTER");
			}
			
			private boolean validXmlExtension(File f) {
				return FileTools.extension(f).equalsIgnoreCase(".xml");
			}
		};
	}
	
	protected ActionListener buildFileBrowseAction(final PropertyValueModel fileValue, final FileFilter fileFilter) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent  e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(fileFilter);
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setDialogTitle(resourceRepository().getString("JAXB_FILE_CHOOSER_TITLE"));
				
				int buttonChoice = fileChooser.showDialog(getWorkbenchContext().getCurrentWindow(), resourceRepository().getString("SELECT"));
				if (buttonChoice != JFileChooser.APPROVE_OPTION) {
					return;
				}
							
				fileValue.setValue(fileChooser.getSelectedFile().getAbsolutePath());
			}
		};
	}	
	
	private JPanel buildToPanel() {
		JPanel toPanel = new JPanel(new GridBagLayout());
		toPanel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(this.resourceRepository().getString("TO")),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)
			)
		);
		
		GridBagConstraints constraints = new GridBagConstraints();		
		
		
		// *** output master directory ***
		
		JLabel outputMasterDirectoryLabel = this.buildOutputMasterDirectoryLabel();
		constraints.gridx 		= 0;
		constraints.gridy 		= 0;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.NONE;
		constraints.anchor 		= GridBagConstraints.LINE_START;
		constraints.insets 		= new Insets(0, 0, 0, 0);
		toPanel.add(outputMasterDirectoryLabel, constraints);

		JTextField outputMasterDirectoryTextField = this.buildOutputMasterDirectoryTextField();
		outputMasterDirectoryLabel.setLabelFor(outputMasterDirectoryTextField);
		constraints.gridx 		= 0;
		constraints.gridy 		= 1;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(0, 0, 0, 0);
		toPanel.add(outputMasterDirectoryTextField, constraints);
		
		JButton outputMasterDirectoryButton = this.buildOutputMasterDirectoryButton();
		constraints.gridx 		= 1;
		constraints.gridy 		= 1;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.NONE;
		constraints.anchor 		= GridBagConstraints.LINE_END;
		constraints.insets 		= new Insets(0, 5, 0, 0);
		toPanel.add(outputMasterDirectoryButton, constraints);
		
		// *** end output master directory ***
		
		
		// *** output source directory ***
		
		JLabel outputSourceDirectoryLabel = this.buildOutputSourceDirectoryLabel();
		constraints.gridx 		= 0;
		constraints.gridy 		= 2;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.NONE;
		constraints.anchor 		= GridBagConstraints.LINE_START;
		constraints.insets 		= new Insets(5, 25, 0, 0);
		toPanel.add(outputSourceDirectoryLabel, constraints);
		
		JTextField outputSourceDirectoryTextField = this.buildOutputSourceDirectoryTextField();
		outputSourceDirectoryLabel.setLabelFor(outputSourceDirectoryTextField);
		constraints.gridx 		= 0;
		constraints.gridy 		= 3;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(0, 25, 0, 0);
		toPanel.add(outputSourceDirectoryTextField, constraints);
		
		JButton outputSourceDirectoryButton = this.buildOutputSourceDirectoryButton();
		constraints.gridx 		= 1;
		constraints.gridy 		= 3;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.NONE;
		constraints.anchor 		= GridBagConstraints.LINE_END;
		constraints.insets 		= new Insets(0, 5, 0, 0);
		toPanel.add(outputSourceDirectoryButton, constraints);
		
		// *** end output source directory ***
		
		
		// *** output workbench project directory ***
		
		JLabel outputWorkbenchProjectDirectoryLabel = this.buildOutputWorkbenchProjectDirectoryLabel();
		constraints.gridx 		= 0;
		constraints.gridy 		= 4;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.NONE;
		constraints.anchor 		= GridBagConstraints.LINE_START;
		constraints.insets 		= new Insets(5, 25, 0, 0);
		toPanel.add(outputWorkbenchProjectDirectoryLabel, constraints);
		
		JTextField outputWorkbenchProjectDirectoryTextField = this.buildOutputWorkbenchProjectDirectoryTextField();
		outputWorkbenchProjectDirectoryLabel.setLabelFor(outputWorkbenchProjectDirectoryTextField);
		constraints.gridx 		= 0;
		constraints.gridy 		= 5;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(0, 25, 0, 0);
		toPanel.add(outputWorkbenchProjectDirectoryTextField, constraints);
		
		JButton outputWorkbenchProjectDirectoryButton = this.buildOutputWorkbenchProjectDirectoryButton();
		constraints.gridx 		= 1;
		constraints.gridy 		= 5;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.NONE;
		constraints.anchor 		= GridBagConstraints.LINE_END;
		constraints.insets 		= new Insets(0, 5, 0, 0);
		toPanel.add(outputWorkbenchProjectDirectoryButton, constraints);
		
		// *** end output workbench project directory ***
		
		
		// *** output interface package name ***
		
		JLabel outputInterfacePackageNameLabel = this.buildOutputInterfacePackageNameLabel();
		constraints.gridx 		= 0;
		constraints.gridy 		= 8;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.NONE;
		constraints.anchor 		= GridBagConstraints.LINE_START;
		constraints.insets 		= new Insets(15, 0, 0, 0);
		toPanel.add(outputInterfacePackageNameLabel, constraints);
		
		JTextField outputInterfacePackageNameTextField = this.buildOutputInterfacePackageNameTextField();
		outputInterfacePackageNameLabel.setLabelFor(outputInterfacePackageNameTextField);
		constraints.gridx 		= 0;
		constraints.gridy 		= 9;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(1, 0, 0, 0);
		toPanel.add(outputInterfacePackageNameTextField, constraints);
		
		// *** end output interface package name ***
		
		
		// *** output impl class package name ***
		
		JLabel outputImplClassPackageNameLabel = this.buildOutputImplClassPackageNameLabel();
		constraints.gridx 		= 0;
		constraints.gridy 		= 10;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.NONE;
		constraints.anchor 		= GridBagConstraints.LINE_START;
		constraints.insets 		= new Insets(5, 0, 0, 0);
		toPanel.add(outputImplClassPackageNameLabel, constraints);
		
		JTextField outputImplClassPackageNameTextField = this.buildOutputImplClassPackageNameTextField();
		outputImplClassPackageNameLabel.setLabelFor(outputImplClassPackageNameTextField);
		constraints.gridx 		= 0;
		constraints.gridy 		= 11;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(1, 0, 0, 0);
		toPanel.add(outputImplClassPackageNameTextField, constraints);
		
		// *** end output interface package name ***
		
		
		this.helpManager().addTopicID(toPanel, this.helpTopicId() + ".To");
				
		return toPanel;		
	}
	
	private JLabel buildOutputMasterDirectoryLabel() {
		JLabel label = SwingComponentFactory.buildLabel("OUTPUT_MASTER_DIRECTORY_LABEL", this.resourceRepository());
		this.helpManager().addTopicID(label, this.helpTopicId() + ".outputDirectory");
		return label;
	}
	
	private JTextField buildOutputMasterDirectoryTextField() {
		JTextField textField = new JTextField(60);
		textField.setDocument(this.buildOutputMasterDirectoryDocument());
		this.helpManager().addTopicID(textField, this.helpTopicId() + ".outputDirectory");
		return textField;
	}
	
	private Document buildOutputMasterDirectoryDocument() {
		return new DocumentAdapter(this.outputMasterDirectoryValue);
	}
	
	private JButton buildOutputMasterDirectoryButton() {
		JButton button = SwingComponentFactory.buildButton("BROWSE_BUTTON_3", this.resourceRepository());
		button.addActionListener(this.buildDirectoryBrowseAction(this.outputMasterDirectoryValue));
		this.helpManager().addTopicID(button, this.helpTopicId() + ".outputDirectory");
		return button;
	}
	
	private JLabel buildOutputSourceDirectoryLabel() {
		JLabel label = SwingComponentFactory.buildLabel("OUTPUT_SOURCE_DIRECTORY_LABEL", this.resourceRepository());
		this.helpManager().addTopicID(label, this.helpTopicId() + ".outputSourceDirectory");
		return label;
	}
	
	private JTextField buildOutputSourceDirectoryTextField() {
		JTextField textField = new JTextField(60);
		textField.setDocument(this.buildOutputSourceDirectoryDocument());
		this.helpManager().addTopicID(textField, this.helpTopicId() + ".outputSourceDirectory");
		return textField;
	}
	
	private Document buildOutputSourceDirectoryDocument() {
		return new DocumentAdapter(this.outputSourceDirectoryValue);
	}
	
	private JButton buildOutputSourceDirectoryButton() {
		JButton button = SwingComponentFactory.buildButton("BROWSE_BUTTON_4", this.resourceRepository());
		button.addActionListener(this.buildDirectoryBrowseAction(this.outputSourceDirectoryValue));
		this.helpManager().addTopicID(button, this.helpTopicId() + ".outputSourceDirectory");
		return button;
	}
	
	private JLabel buildOutputWorkbenchProjectDirectoryLabel() {
		JLabel label = SwingComponentFactory.buildLabel("OUTPUT_WORKBENCH_PROJECT_DIRECTORY_LABEL", this.resourceRepository());
		this.helpManager().addTopicID(label, this.helpTopicId() + ".outputWorkbenchProjectDirectory");
		return label;
	}
	
	private JTextField buildOutputWorkbenchProjectDirectoryTextField() {
		JTextField textField = new JTextField(60);
		textField.setDocument(this.buildOutputWorkbenchProjectDirectoryDocument());
		this.helpManager().addTopicID(textField, this.helpTopicId() + ".outputWorkbenchProjectDirectory");
		return textField;
	}
	
	private Document buildOutputWorkbenchProjectDirectoryDocument() {
		return new DocumentAdapter(this.outputWorkbenchProjectDirectoryValue);
	}
	
	private JButton buildOutputWorkbenchProjectDirectoryButton() {
		JButton button = SwingComponentFactory.buildButton("BROWSE_BUTTON_6", this.resourceRepository());
		button.addActionListener(this.buildDirectoryBrowseAction(this.outputWorkbenchProjectDirectoryValue));
		this.helpManager().addTopicID(button, this.helpTopicId() + ".outputWorkbenchProjectDirectory");
		return button;
	}
	
	private JLabel buildOutputInterfacePackageNameLabel() {
		JLabel label = SwingComponentFactory.buildLabel("OUTPUT_INTERFACE_PACKAGE_NAME_LABEL", this.resourceRepository());
		this.helpManager().addTopicID(label, this.helpTopicId() + ".interfacePackageName");
		return label;
	}
	
	private JTextField buildOutputInterfacePackageNameTextField() {
		JTextField textField = new JTextField(60);
		textField.setDocument(this.buildOutputInterfacePackageNameDocument());
		this.helpManager().addTopicID(textField, helpTopicId() + ".interfacePackageName");
		return textField;
	}
	
	private Document buildOutputInterfacePackageNameDocument() {
		return new DocumentAdapter(
			this.outputInterfacePackageNameValue, 
			new RegexpDocument(RegexpDocument.RE_PACKAGE)
		);
	}
	
	private JLabel buildOutputImplClassPackageNameLabel() {
		JLabel label = SwingComponentFactory.buildLabel("OUTPUT_IMPL_CLASS_PACKAGE_NAME_LABEL", this.resourceRepository());
		this.helpManager().addTopicID(label, this.helpTopicId() + ".implClassPackageName");
		return label;
	}
	
	private JTextField buildOutputImplClassPackageNameTextField() {
		JTextField textField = new JTextField(60);
		textField.setDocument(this.buildOutputImplClassPackageNameDocument());
		this.helpManager().addTopicID(textField, helpTopicId() + ".implClassPackageName");
		return textField;
	}
	
	private Document buildOutputImplClassPackageNameDocument() {
		return new DocumentAdapter(
			this.outputImplClassPackageNameValue, 
			new RegexpDocument(RegexpDocument.RE_PACKAGE)
		);
	}
	
	private ActionListener  buildDirectoryBrowseAction(final PropertyValueModel fileValue) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent  e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setDialogTitle(resourceRepository().getString("JAXB_DIRECTORY_CHOOSER_TITLE"));

				int buttonChoice = fileChooser.showDialog(getWorkbenchContext().getCurrentWindow(), resourceRepository().getString("SELECT"));
				if (buttonChoice != JFileChooser.APPROVE_OPTION) {
					return;
				}
				
				fileValue.setValue(fileChooser.getSelectedFile().getAbsolutePath());
			}
		};
	}
	
	
	// **************** Behavior **********************************************
	
	protected void updateOKAction() {
		boolean enabled = 
			this.getInputSchemaFile().length() > 0
			&& this.getOutputSourceDirectory().length() > 0
			&& this.getOutputWorkbenchProjectDirectory().length() > 0
			&& this.getOutputMasterDirectory().length() > 0;
		
		this.getOKAction().setEnabled(enabled);	
	}
	
	protected boolean preConfirm() {
		// *** validate input schema file ***
		//
		//  We've already limited to .xsd extensions, so no reason 
		//  to double check that.  If a user wants to use a file
		//  without an .xsd extension, that's his/her business.
		//
		if (! new File(this.getInputSchemaFile()).exists()) {
			JOptionPane.showMessageDialog(
				this, 
				this.resourceRepository().getString("SCHEMA_FILE_DOES_NOT_EXIST"), 
				this.resourceRepository().getString("WARNING"), 
				JOptionPane.ERROR_MESSAGE
			);
			return false;
		} 
		
		// *** validate customization file ***
		//
		//  We've already limited to .xml extensions, so no reason 
		//  to double check that.  If a user wants to use a file
		//  without an .xml extension, that's his/her business.
		//
		String inputCustomizationFile = this.getInputCustomizationFile();
		
		if (inputCustomizationFile != "" && ! new File(inputCustomizationFile).exists()) {
			JOptionPane.showMessageDialog(
				this, 
				this.resourceRepository().getString("CUSTOMIZATION_FILE_DOES_NOT_EXIST"), 
				this.resourceRepository().getString("WARNING"), 
				JOptionPane.ERROR_MESSAGE
			);
			return false;
		}
		
		// *** validate output source directory ***
		//
		//  We attempt to create the directory.
		//
		File outputSourceDirectory = new File(this.getAbsoluteOutputSourceDirectory());
		String canonicalOutputSourceDirectoryPath = null;
		
		try {
			canonicalOutputSourceDirectoryPath = outputSourceDirectory.getCanonicalPath();
		}
		catch (IOException ioe) {
			JOptionPane.showMessageDialog(
				this, 
				this.resourceRepository().getString("OUTPUT_SOURCE_DIRECTORY_COULD_NOT_BE_RESOLVED"), 
				this.resourceRepository().getString("WARNING"), 
				JOptionPane.ERROR_MESSAGE
			);
			return false;
		}
		
		if (! outputSourceDirectory.isDirectory() && ! outputSourceDirectory.mkdirs()) {
			JOptionPane.showMessageDialog(
				this, 
				this.resourceRepository().getString(
					"OUTPUT_SOURCE_DIRECTORY_COULD_NOT_BE_CREATED",
					canonicalOutputSourceDirectoryPath
				), 
				this.resourceRepository().getString("WARNING"), 
				JOptionPane.ERROR_MESSAGE
			);	
			return false;
		}
		
		// *** validate workbench project resource directory ***
		//
		//  We attempt to create the directory, plus we make sure,
		//  if the directory exists, that there is no project already
		//  in the directory.
		//
		File outputWorkbenchProjectDirectory = new File(this.getAbsoluteOutputWorkbenchProjectDirectory());
		String canonicalOutputWorkbenchProjectDirectoryPath = null;
		
		try {
			canonicalOutputWorkbenchProjectDirectoryPath = outputWorkbenchProjectDirectory.getCanonicalPath();
		}
		catch (IOException ioe) {
			JOptionPane.showMessageDialog(
				this, 
				this.resourceRepository().getString("OUTPUT_WORKBENCH_PROJECT_DIRECTORY_COULD_NOT_BE_RESOLVED"), 
				this.resourceRepository().getString("WARNING"), 
				JOptionPane.ERROR_MESSAGE
			);
			return false;
		}
		
		if (outputWorkbenchProjectDirectory.isDirectory()) {
			if (this.directoryHasExistingWorkbenchProject(outputWorkbenchProjectDirectory)) {
				JOptionPane.showMessageDialog(
					this, 
					this.resourceRepository().getString(
						"OUTPUT_WORKBENCH_PROJECT_DIRECTORY_INVALID",
						canonicalOutputWorkbenchProjectDirectoryPath
					), 
					this.resourceRepository().getString("WARNING"), 
					JOptionPane.ERROR_MESSAGE
				);
				return false;
			}
		}
		else if (! outputWorkbenchProjectDirectory.mkdirs()) {
			JOptionPane.showMessageDialog(
				this, 
				this.resourceRepository().getString(
					"OUTPUT_WORKBENCH_PROJECT_DIRECTORY_COULD_NOT_BE_CREATED",
					canonicalOutputWorkbenchProjectDirectoryPath
				), 
				this.resourceRepository().getString("WARNING"), 
				JOptionPane.ERROR_MESSAGE
			);	
			return false;
		}
		
		return true;
	}

	private boolean directoryHasExistingWorkbenchProject(File directory) {
		File[] files = directory.listFiles();
		
		if (files == null || files.length == 0) {
			return false;	// an empty directory is OK
		}
		
		for (int i = files.length; i-- > 0; ) {
			File file = files[i];
			// if the file is not a directory and has the extension .mwp we disallow the save
			if (! file.isDirectory() 
				&& MWProject.FILE_NAME_EXTENSION.equals(FileTools.extension(file.getName()))
			) {
				return true;
			}
		}
		return false;
	}

	public String getInputSchemaFile() {
		return (String) this.inputSchemaFileValue.getValue();
	}
		
	public String getInputCustomizationFile() {
		return (String) this.inputCustomizationFileValue.getValue();
	}
	
	private String getOutputMasterDirectory() {
		return (String) this.outputMasterDirectoryValue.getValue();
	}
	
	private String getOutputSourceDirectory() {
		return (String) this.outputSourceDirectoryValue.getValue();
	}
	
	public String getAbsoluteOutputSourceDirectory() {
		return FileTools.convertToAbsoluteFile(new File(this.getOutputSourceDirectory()), new File(this.getOutputMasterDirectory())).getPath();
	}
	
	private String getOutputWorkbenchProjectDirectory() {
		return (String) this.outputWorkbenchProjectDirectoryValue.getValue();
	}
	
	public String getAbsoluteOutputWorkbenchProjectDirectory() {
		return FileTools.convertToAbsoluteFile(new File(this.getOutputWorkbenchProjectDirectory()), new File(this.getOutputMasterDirectory())).getPath();
	}
	
	public String getOutputInterfacePackageName() {
		return (String) this.outputInterfacePackageNameValue.getValue();
	}
	
	public String getOutputImplClassPackageName() {
		return (String) this.outputImplClassPackageNameValue.getValue();
	}
}
