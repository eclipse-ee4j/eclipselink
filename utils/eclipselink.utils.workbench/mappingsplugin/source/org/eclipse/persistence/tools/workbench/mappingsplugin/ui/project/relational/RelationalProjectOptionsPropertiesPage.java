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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.FileChooserPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWTableGenerationPolicy;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectOptionsPropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;


final class RelationalProjectOptionsPropertiesPage extends ProjectOptionsPropertiesPage {

	public static final String PROJECT_SOURCE_DIRECTORY_PREFERENCE = "project options-project source directory";
	public static final String TABLE_CREATOR_SOURCE_DIRECTORY_PREFERENCE = "project options-table creator source directory";
	private static final long serialVersionUID = 1L;

	RelationalProjectOptionsPropertiesPage(PropertyValueModel relationalProjectNodeHolder, WorkbenchContextHolder contextHolder) {
		super(relationalProjectNodeHolder, contextHolder);
	}


	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		// create the deployment and source code generation panel
		JPanel deploymentAndCodeGenerationPanel = new JPanel(new GridBagLayout());
		deploymentAndCodeGenerationPanel.setBorder(buildTitledBorder("DEPLOYMENT_AND_CODE_GENERATION_PANEL_TITLE"));

		constraints.gridx 		= 0;
		constraints.gridy 		= 0;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(0, 0, 0, 0);

		panel.add(deploymentAndCodeGenerationPanel, constraints);
				
			// create the project java source panel
			JPanel projectSourcePanel = new JPanel(new GridBagLayout());
			projectSourcePanel.setBorder(buildTitledBorder("PROJECT_JAVA_SOURCE_PANEL_TITLE"));

			constraints.gridx 		= 0;
			constraints.gridy 		= 0;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 1;
			constraints.weighty 	= 0;
			constraints.fill 		= GridBagConstraints.HORIZONTAL;
			constraints.anchor 		= GridBagConstraints.NORTH;
			constraints.insets 		= new Insets(0, 5, 0, 5);

			deploymentAndCodeGenerationPanel.add(projectSourcePanel, constraints);

				// create the project class widgets
				JComponent projectClassWidgets = buildLabeledTextField
				(
					"PROJECT_CLASS_LABEL",
					buildProjectClassNameDocument(buildProjectClassNameHolder(getSelectionHolder()))
				);

				constraints.gridx      = 0;
				constraints.gridy      = 0;
				constraints.gridwidth  = 1;
				constraints.gridheight = 1;
				constraints.weightx    = 1;
				constraints.weighty    = 0;
				constraints.fill       = GridBagConstraints.HORIZONTAL;
				constraints.anchor     = GridBagConstraints.CENTER;
				constraints.insets     = new Insets(5, 5, 0, 5);

				projectSourcePanel.add(projectClassWidgets, constraints);
				
				// create the project source code root directory chooser
				FileChooserPanel rootDirectoryWidget = new FileChooserPanel(
					getApplicationContext(),
					buildProjectSourceRootDirectoryHolder(getSelectionHolder()),
					"PROJECT_SOURCE_ROOT_DIRECTORY_LABEL",
					"BROWSE_BUTTON_1",
					JFileChooser.DIRECTORIES_ONLY);

				rootDirectoryWidget.setFileChooserRootFileHolder(this.buildFileChooserRootFileHolder());
				rootDirectoryWidget.setFileChooserDefaultDirectoryHolder(this.buildFileChooserDefaultDirectoryHolder(PROJECT_SOURCE_DIRECTORY_PREFERENCE));

				constraints.gridx      = 0;
				constraints.gridy      = 1;
				constraints.gridwidth  = 1;
				constraints.gridheight = 1;
				constraints.weightx    = 1;
				constraints.weighty    = 0;
				constraints.fill       = GridBagConstraints.HORIZONTAL;
				constraints.anchor     = GridBagConstraints.CENTER;
				constraints.insets     = new Insets(5, 5, 5, 5);

				projectSourcePanel.add(rootDirectoryWidget, constraints);
				addPaneForAlignment(rootDirectoryWidget);

			addHelpTopicId(projectSourcePanel, helpTopicId() + ".project");
				
			// create the project deployment xml panel
			JPanel projectXmlPanel = new JPanel(new GridBagLayout());
			projectXmlPanel.setBorder(buildTitledBorder("PROJECT_DEPLOYMENT_XML_PANEL_TITLE"));

			constraints.gridx      = 0;
			constraints.gridy      = 1;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 1;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.HORIZONTAL;
			constraints.anchor     = GridBagConstraints.CENTER;
			constraints.insets     = new Insets(5, 5, 0, 5);

			deploymentAndCodeGenerationPanel.add(projectXmlPanel, constraints);
				
				// project deployment xml
				FileChooserPanel projectXmlFileChooserPanel = new FileChooserPanel(
					getApplicationContext(),
					buildDeploymentXMLFileNameHolder(getSelectionHolder()),
					"PROJECT_DEPLOYMENT_XML_FILE_LABEL",
					"BROWSE_BUTTON_7",
					JFileChooser.FILES_ONLY
				);

				projectXmlFileChooserPanel.setFileChooserRootFileHolder(this.buildFileChooserRootFileHolder());
				projectXmlFileChooserPanel.setFileChooserDefaultDirectoryHolder(this.buildFileChooserDefaultDirectoryHolder(PROJECT_DEPLOYMENT_XML_DIRECTORY_PREFERENCE));

				constraints.gridx      = 0;
				constraints.gridy      = 0;
				constraints.gridwidth  = 1;
				constraints.gridheight = 1;
				constraints.weightx    = 1;
				constraints.weighty    = 0;
				constraints.fill       = GridBagConstraints.HORIZONTAL;
				constraints.anchor     = GridBagConstraints.CENTER;
				constraints.insets     = new Insets(5, 5, 5, 5);

				projectXmlPanel.add(projectXmlFileChooserPanel, constraints);
				addPaneForAlignment(projectXmlFileChooserPanel);

			addHelpTopicId(projectXmlPanel, helpTopicId() + ".xml");
				
			// create the table creator java source panel
			JPanel tableCreatorPanel = new JPanel(new GridBagLayout());
			tableCreatorPanel.setBorder(buildTitledBorder("TABLE_CREATOR_JAVA_SOURCE_PANEL_TITLE"));

			constraints.gridx      = 0;
			constraints.gridy      = 2;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 1;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.HORIZONTAL;
			constraints.anchor     = GridBagConstraints.CENTER;
			constraints.insets     = new Insets(5, 5, 0, 5);

			deploymentAndCodeGenerationPanel.add(tableCreatorPanel, constraints);
				
				// create the table creator class name label
				JComponent tableCreatorClassWidgets = buildLabeledTextField
				(
					"TABLE_CREATOR_CLASS_LABEL",
					buildTableCreatorClassNameDocument(buildTableCreatorClassNameHolder(getSelectionHolder()))
				);

				constraints.gridx      = 0;
				constraints.gridy      = 0;
				constraints.gridwidth  = 1;
				constraints.gridheight = 1;
				constraints.weightx    = 0;
				constraints.weighty    = 0;
				constraints.fill       = GridBagConstraints.HORIZONTAL;
				constraints.anchor     = GridBagConstraints.CENTER;
				constraints.insets     = new Insets(5, 5, 0, 5);

				tableCreatorPanel.add(tableCreatorClassWidgets, constraints);
				
				// create the table creator source root directory chooser
				FileChooserPanel tableCreatorSourceRootDirChooserPanel = new FileChooserPanel(
					getApplicationContext(),
					buildTableCreatorRootDirectoryHolder(getSelectionHolder()),
					"TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_LABEL",
					"BROWSE_BUTTON_3",
					JFileChooser.DIRECTORIES_ONLY);

				tableCreatorSourceRootDirChooserPanel.setFileChooserRootFileHolder(this.buildFileChooserRootFileHolder());
				tableCreatorSourceRootDirChooserPanel.setFileChooserDefaultDirectoryHolder(this.buildFileChooserDefaultDirectoryHolder(TABLE_CREATOR_SOURCE_DIRECTORY_PREFERENCE));

				constraints.gridx      = 0;
				constraints.gridy      = 1;
				constraints.gridwidth  = 1;
				constraints.gridheight = 1;
				constraints.weightx    = 1;
				constraints.weighty    = 0;
				constraints.fill       = GridBagConstraints.HORIZONTAL;
				constraints.anchor     = GridBagConstraints.CENTER;
				constraints.insets     = new Insets(5, 5, 5, 5);

				tableCreatorPanel.add(tableCreatorSourceRootDirChooserPanel, constraints);
				addPaneForAlignment(tableCreatorSourceRootDirChooserPanel);

			addHelpTopicId(tableCreatorPanel, helpTopicId() + ".creator");
			
			// create the model java source panel
			JPanel modelSourcePanel = new JPanel(new GridBagLayout());
			modelSourcePanel.setBorder(BorderFactory.createTitledBorder(this.resourceRepository().getString("MODEL_JAVA_SOURCE_PANEL_TITLE")));

			constraints.gridx 		= 0;
			constraints.gridy 		= 3;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 1;
			constraints.weighty 	= 0;
			constraints.fill 		= GridBagConstraints.HORIZONTAL;
			constraints.anchor 		= GridBagConstraints.CENTER;
			constraints.insets 		= new Insets(5, 5, 5, 5);

			deploymentAndCodeGenerationPanel.add(modelSourcePanel, constraints);
			
				// create the source code root directory chooser
				FileChooserPanel modelSourceRootDirChooserPanel = new FileChooserPanel(
					getApplicationContext(),
					buildModelSourceDirectoryNameHolder(getSelectionHolder()),
					"MODEL_SOURCE_ROOT_DIRECTORY_LABEL",
					"BROWSE_BUTTON_4",
					JFileChooser.DIRECTORIES_ONLY);

				modelSourceRootDirChooserPanel.setFileChooserRootFileHolder(this.buildFileChooserRootFileHolder());
				modelSourceRootDirChooserPanel.setFileChooserDefaultDirectoryHolder(this.buildFileChooserDefaultDirectoryHolder(MODEL_SOURCE_DIRECTORY_PREFERENCE));

				constraints.gridx      = 0;
				constraints.gridy      = 1;
				constraints.gridwidth  = 1;
				constraints.gridheight = 1;
				constraints.weightx    = 1;
				constraints.weighty    = 0;
				constraints.fill       = GridBagConstraints.HORIZONTAL;
				constraints.anchor     = GridBagConstraints.CENTER;
				constraints.insets     = new Insets(5, 5, 5, 5);

				modelSourcePanel.add(modelSourceRootDirChooserPanel, constraints);
				addPaneForAlignment(modelSourceRootDirChooserPanel);

			addHelpTopicId(modelSourcePanel, helpTopicId() + ".model");

		addHelpTopicId(deploymentAndCodeGenerationPanel, helpTopicId() + ".deployment");
			
		// create the table generation panel
		JPanel tableGenerationPanel = new JPanel();
		tableGenerationPanel.setLayout(new GridBagLayout());
		tableGenerationPanel.setBorder(BorderFactory.createTitledBorder(this.resourceRepository().getString("TABLE_GENERATION_PANEL_TITLE")));
		constraints.gridx 		= 0;
		constraints.gridy 		= 1;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.NORTH;
		constraints.insets 		= new Insets(5, 0, 0, 0);
		panel.add(tableGenerationPanel, constraints);
			
			// Create the Default Primary Key Name
			JLabel defaultPrimaryKeyLabel = new JLabel();
			defaultPrimaryKeyLabel.setText(resourceRepository().getString("DEFAULT_PRIMARY_KEY"));
			defaultPrimaryKeyLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("DEFAULT_PRIMARY_KEY"));
			constraints.gridx		= 0;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.LINE_START;
			constraints.insets		= new Insets(0, 5, 0, 0);
			tableGenerationPanel.add(defaultPrimaryKeyLabel, constraints);
			
			PropertyValueModel tableGenerationPolicyHolder = buildTableGenerationHolder(getSelectionHolder());
			
			// Creating the text field
			JTextField defaultPkNameTextField = new JTextField();
			defaultPkNameTextField.setDocument(buildDefaultPkNameTextFieldDocument(buildDefaultPkNameHolder(tableGenerationPolicyHolder)));
			constraints.gridx		= 1;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(0, 5, 0, 5);
			tableGenerationPanel.add(defaultPkNameTextField, constraints);
			defaultPrimaryKeyLabel.setLabelFor(defaultPkNameTextField);
	
			// Create the Primary Key Seach Pattern
			JLabel primaryKeySearchPatternLabel = new JLabel();
			primaryKeySearchPatternLabel.setText(resourceRepository().getString("PRIMARY_KEY_SEARCH_PATTERN"));
			primaryKeySearchPatternLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("PRIMARY_KEY_SEARCH_PATTERN"));
			constraints.gridx		= 0;
			constraints.gridy		= 1;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.LINE_START;
			constraints.insets		= new Insets(5, 5, 5, 0);
			tableGenerationPanel.add(primaryKeySearchPatternLabel, constraints);
			
			// Creating the text field
			JTextField pkSearchPatternTextField = new JTextField();
			pkSearchPatternTextField.setDocument(buildPrimaryKeySearchPatternTextFieldDocument(buildPrimaryKeySearchPatternHolder(tableGenerationPolicyHolder)));
			constraints.gridx		= 1;
			constraints.gridy		= 1;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(5, 5, 5, 5);
			tableGenerationPanel.add(pkSearchPatternTextField, constraints);
			primaryKeySearchPatternLabel.setLabelFor(pkSearchPatternTextField);
			
		addHelpTopicId(tableGenerationPanel, helpTopicId() + ".generation");
			
		JCheckBox generateDeprecatedDirectMappingsChecksBox = buildCheckBox("GENERATE_DEPRECATED_DIRECT_MAPPINGS_CHECK_BOX", buildGenerateDeprecatedDirectMappingsButtonModel());	
		addHelpTopicId(generateDeprecatedDirectMappingsChecksBox, helpTopicId() + ".generateDeprecatedDirectMappings");
		constraints.gridx 		= 0;
		constraints.gridy 		= 2;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 1;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.NORTH;
		constraints.insets 		= new Insets(5, 0, 0, 0);
		panel.add(generateDeprecatedDirectMappingsChecksBox, constraints);
		
		JCheckBox usesWeavingChecksBox = buildCheckBox("USES_WEAVING_CHECK_BOX", buildWeavingButtonModel());	
		addHelpTopicId(usesWeavingChecksBox, helpTopicId() + ".useWeaving");
		constraints.gridx 		= 0;
		constraints.gridy 		= 3;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 1;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.NORTH;
		constraints.insets 		= new Insets(5, 0, 0, 0);
		panel.add(usesWeavingChecksBox, constraints);

		addHelpTopicId(panel, helpTopicId());
		return panel;
	}
	
	//********* Project Java Source ***********
	
	private Document buildProjectClassNameDocument(PropertyValueModel projectClassNameHolder) {
		return new DocumentAdapter(projectClassNameHolder, new RegexpDocument(RegexpDocument.RE_FULLY_QUALIFIED_CLASS_NAME));
	}
	
	private PropertyValueModel buildProjectClassNameHolder(ValueModel projectHolder) {
		return new PropertyAspectAdapter(projectHolder, MWProject.PROJECT_SOURCE_CLASS_NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalProject) this.subject).getProjectSourceClassName();
			}
			protected void setValueOnSubject(Object value) {
				((MWRelationalProject) this.subject).setProjectSourceClassName((String) value);
			}
		};
	}
	
	private PropertyValueModel buildProjectSourceRootDirectoryHolder(PropertyValueModel projectHolder) {
		return new PropertyAspectAdapter(projectHolder, MWProject.PROJECT_SOURCE_DIRECTORY_NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalProject) this.subject).getProjectSourceDirectoryName();
			}
			protected void setValueOnSubject(Object value) {
				((MWRelationalProject) this.subject).setProjectSourceDirectoryName((String) value);
			}
		};
	}
	
	
	
		
			
	//********* Table Creator ***********
	
	private Document buildTableCreatorClassNameDocument(PropertyValueModel tableCreatorClassNameHolder) {
		return new DocumentAdapter(tableCreatorClassNameHolder);
	}

	private PropertyValueModel buildTableCreatorClassNameHolder(ValueModel projectHolder) {
		return new PropertyAspectAdapter(projectHolder, MWRelationalProject.TABLE_CREATOR_SOURCE_CLASS_NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalProject) this.subject).getTableCreatorSourceClassName();
			}
			protected void setValueOnSubject(Object value) {
				((MWRelationalProject) this.subject).setTableCreatorSourceClassName((String) value);
			}
		};
	}
	
	private PropertyValueModel buildTableCreatorRootDirectoryHolder(PropertyValueModel projectHolder) {
		return new PropertyAspectAdapter(projectHolder, MWRelationalProject.TABLE_CREATOR_SOURCE_DIRECTORY_NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalProject) this.subject).getTableCreatorSourceDirectoryName();
			}
			protected void setValueOnSubject(Object value) {
				((MWRelationalProject) this.subject).setTableCreatorSourceDirectoryName((String) value);
			}
		};
	}

	
	//********* Default Primary Key  ***********
	
	private Document buildDefaultPkNameTextFieldDocument(PropertyValueModel defaultPkNameHolder) {
		return new DocumentAdapter(defaultPkNameHolder);
	}
		
	private PropertyValueModel buildDefaultPkNameHolder(PropertyValueModel tableGenerationPolicyHolder) {
		return new PropertyAspectAdapter(tableGenerationPolicyHolder, MWTableGenerationPolicy.DEFAULT_PRIMARY_KEY_NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWTableGenerationPolicy) this.subject).getDefaultPrimaryKeyName();
			}
			protected void setValueOnSubject(Object value) {
				((MWTableGenerationPolicy) this.subject).setDefaultPrimaryKeyName((String) value);
			}
		};
	}
	
	//********* Primary Key Search Pattern  ***********

	private Document buildPrimaryKeySearchPatternTextFieldDocument(PropertyValueModel primaryKeySearchPatternHolder) {
		return new DocumentAdapter(primaryKeySearchPatternHolder);
	}
	
	private PropertyValueModel buildPrimaryKeySearchPatternHolder(PropertyValueModel tableGenerationPolicyHolder) {
		return new PropertyAspectAdapter(tableGenerationPolicyHolder, MWTableGenerationPolicy.PRIMARY_KEY_SEARCH_PATTERN_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWTableGenerationPolicy) this.subject).getPrimaryKeySearchPattern();
			}
			protected void setValueOnSubject(Object value) {
				((MWTableGenerationPolicy) this.subject).setPrimaryKeySearchPattern((String) value);
			}
		};
	}
	
	private PropertyValueModel buildTableGenerationHolder(final PropertyValueModel projectHolder) {
		return new PropertyAspectAdapter(projectHolder) {
			protected Object getValueFromSubject() {
				return ((MWRelationalProject) this.subject).getTableGenerationPolicy();
			}
		};
	}
	
	
	//********* Generate Deprecated Direct Mappings  ***********
	
	private ButtonModel buildGenerateDeprecatedDirectMappingsButtonModel() {
		return new CheckBoxModelAdapter(buildGenerateDeprecatedDirectMappingsAdapter(), false);
	}
	
	private PropertyValueModel buildGenerateDeprecatedDirectMappingsAdapter() {
		return new PropertyAspectAdapter(getSelectionHolder(), MWRelationalProject.GENERATE_DEPRECATED_DIRECT_MAPPINGS_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWRelationalProject) this.subject).isGenerateDeprecatedDirectMappings());
			}
			protected void setValueOnSubject(Object value) {
				((MWRelationalProject) this.subject).setGenerateDeprecatedDirectMappings(((Boolean) value).booleanValue());
			}
		};
	}
	
	protected String helpTopicId() {
		return "project.options";
	}
}
