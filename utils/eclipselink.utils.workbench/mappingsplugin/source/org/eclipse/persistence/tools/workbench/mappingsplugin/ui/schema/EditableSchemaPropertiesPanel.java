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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.FileChooserPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.framework.uitools.FileChooserPanel.FileHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.ClasspathResourceSpecification;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.FileResourceSpecification;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.ResourceSpecification;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.UrlResourceSpecification;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchemaRepository;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


final class EditableSchemaPropertiesPanel 
	extends AbstractPanel 
{
	private PropertyValueModel schemaRepositoryHolder;
	
	private PropertyValueModel schemaNameHolder;
	
	private PropertyValueModel schemaLoadMechanismHolder;
		private final static String FILE_MECHANISM = "file";
		private final static String CLASSPATH_MECHANISM = "classpath";
		private final static String URL_MECHANISM = "url";
	
	private PropertyValueModel fileHolder;
	
	private PropertyValueModel classpathResourceNameHolder;
	
	private PropertyValueModel urlHolder;
	
	public final static String SCHEMA_PROPERTY = "SCHEMA_PROPERTY";
	
	private static final long serialVersionUID = 1L;

	// *********** schema directory preference
	private final static String MOST_RECENT_SCHEMA_DIRECTORY_PREFERENCE = "recent schema directory";


	EditableSchemaPropertiesPanel(PropertyValueModel schemaRepositoryHolder, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		this.schemaRepositoryHolder = schemaRepositoryHolder;
		this.initialize();
	}
	
	private void initialize() {
		this.schemaNameHolder = this.buildSchemaNameHolder();
		this.schemaLoadMechanismHolder = this.buildSchemaLoadMechanismHolder();
		this.fileHolder = this.buildFileHolder();
		this.classpathResourceNameHolder = this.buildClasspathResourceNameHolder();
		this.urlHolder = this.buildUrlHolder();
		this.initializeLayout();
	}
		
	private PropertyValueModel buildSchemaNameHolder() {
		PropertyValueModel result = new SimplePropertyValueModel("");
		result.addPropertyChangeListener(ValueModel.VALUE, this.buildErrorListener());
		return result;
	}
	
	private PropertyValueModel buildSchemaLoadMechanismHolder() {
		PropertyValueModel result = new SimplePropertyValueModel(FILE_MECHANISM);
		result.addPropertyChangeListener(ValueModel.VALUE, this.buildErrorListener());
		return result;
	}
	
	private PropertyValueModel buildFileHolder() {
		PropertyValueModel result = new SimplePropertyValueModel(null);
		result.addPropertyChangeListener(ValueModel.VALUE, this.buildErrorListener());
		result.addPropertyChangeListener(ValueModel.VALUE, this.buildFileListener());
		return result;
	}
	
	private PropertyChangeListener buildFileListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				File newFile = new File((String) evt.getNewValue());
				
				if (
					"".equals(EditableSchemaPropertiesPanel.this.getSchemaName()) 
					// this part is to distinguish between typing and browsing
					// so that only "complete" file names are used for the schema name
					&& newFile.exists()
					// this part is to eliminate folder files
					&& newFile.isFile()
				) {
					EditableSchemaPropertiesPanel.this.setSchemaNameFromFile(newFile);
			}
			}
		};
	}
	
	private PropertyValueModel buildClasspathResourceNameHolder() {
		PropertyValueModel result = new SimplePropertyValueModel("");
		result.addPropertyChangeListener(ValueModel.VALUE, this.buildErrorListener());
		return result;
	}
	
	private PropertyValueModel buildUrlHolder() {
		PropertyValueModel result = new SimplePropertyValueModel("");
		result.addPropertyChangeListener(ValueModel.VALUE, this.buildErrorListener());
		return result;
	}
	
	private PropertyChangeListener buildErrorListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				EditableSchemaPropertiesPanel.this.notifyErrorCheckers(evt);
			}
		};
	}

	void notifyErrorCheckers(PropertyChangeEvent evt) {
		this.firePropertyChange(EditableSchemaPropertiesPanel.SCHEMA_PROPERTY, evt.getOldValue(), evt.getNewValue());
	}
	
	private void initializeLayout() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		//	Create the schema name label
		JLabel schemaNameLabel = new JLabel(resourceRepository().getString("SCHEMA_NAME_LABEL"));
		schemaNameLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("SCHEMA_NAME_LABEL"));
		constraints.gridx 		= 0;
		constraints.gridy 		= 0;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(5, 5, 0, 5);
		this.add(schemaNameLabel, constraints);
		
		// Create the schema name text field
		JTextField schemaNameTextField = this.buildSchemaNameTextField();
		addHelpTopicId(schemaNameTextField, "dialog.schemaProperties.name");
		schemaNameLabel.setLabelFor(schemaNameTextField);
		constraints.gridx 		= 1;
		constraints.gridy 		= 0;
		constraints.gridwidth 	= 2;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(5, 0, 0, 5);
		this.add(schemaNameTextField, constraints);
		
		// Source Radio button panel
		JPanel sourcePanel = new JPanel(new GridBagLayout());
		addHelpTopicId(schemaNameTextField, "dialog.schemaProperties.source");
		sourcePanel.setBorder(BorderFactory.createTitledBorder(resourceRepository().getString("SOURCE_PANEL_LABEL")));
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 3;
		constraints.gridheight	= 3;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 0, 0, 0);
		this.add(sourcePanel, constraints);
		
		// File source button
		JRadioButton fileButton = this.buildFileRadioButton();
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		sourcePanel.add(fileButton, constraints);
		
		// File chooser panel
		FileChooserPanel fileChooserPanel = this.buildFileChooserPanel();
		constraints.gridx 		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth 	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;		
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets = 	new Insets(0, SwingTools.checkBoxIconWidth(), 10, 5);
		sourcePanel.add(fileChooserPanel, constraints);
		
		// URL source button
		JRadioButton urlButton = this.buildUrlRadioButton();
		constraints.gridx 		= 0;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx 	= 0;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		sourcePanel.add(urlButton, constraints);
		
		// URL text field
	 	JTextField urlTextField = this.buildUrlTextField();
		constraints.gridx 		= 0;
		constraints.gridy		= 3;
		constraints.gridwidth 	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;	
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(0, SwingTools.checkBoxIconWidth(), 10, 5);
		sourcePanel.add(urlTextField, constraints);
		
		// Classpath button
		JRadioButton classpathButton = this.buildClasspathRadioButton();
		constraints.gridx 		= 0;
		constraints.gridy		= 4;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx 	= 0;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		sourcePanel.add(classpathButton, constraints);
		
		// Classpath resource panel
		ClasspathResourcePanel classpathResourcePanel = this.buildClasspathResourcePanel();
		constraints.gridx 		= 0;
		constraints.gridy		= 5;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx 	= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, SwingTools.checkBoxIconWidth(), 5, 5);
		sourcePanel.add(classpathResourcePanel, constraints);
		
		this.setPreferredSize(new Dimension(600, this.getPreferredSize().height));
	}
	
	private JTextField buildSchemaNameTextField() {
		JTextField schemaNameTextField = new JTextField(new DocumentAdapter(this.schemaNameHolder), null, 1);
		/*
		this.setFocusComponent(schemaNameTextField);
		TODO
		*/
		return schemaNameTextField;
	}
	
	private JRadioButton buildFileRadioButton() {
		return SwingComponentFactory.buildRadioButton("FILE_RADIO_BUTTON_LABEL", this.buildFileRadioButtonModel(), this.resourceRepository());
	}
	
	private RadioButtonModelAdapter buildFileRadioButtonModel() {
		return new RadioButtonModelAdapter(this.schemaLoadMechanismHolder, FILE_MECHANISM);
	}
	
	private FileChooserPanel buildFileChooserPanel() {
		FileChooserPanel fileChooserPanel = new FileChooserPanel(getApplicationContext(), this.fileHolder, "FILE_RADIO_BUTTON_LABEL") {
			protected boolean labelVisible() {
				return false;
			}
		};
		fileChooserPanel.setFileChooserFileFilter(this.buildFileFilter());
		fileChooserPanel.setFileChooserRootFileHolder(this.buildFileChooserRootFileHolder());
		fileChooserPanel.setEnabled(true);
		fileChooserPanel.setFileChooserDefaultDirectoryHolder(this.buildFileChooserDefaultDirectoryHolder(MOST_RECENT_SCHEMA_DIRECTORY_PREFERENCE));

		this.schemaLoadMechanismHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildLoadMechanismListener(fileChooserPanel, FILE_MECHANISM));
		return fileChooserPanel;
	}
	
	private FileFilter buildFileFilter() {
		return new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory() || pathname.getAbsolutePath().endsWith(".xsd");
			}
			
			public String getDescription() {
				return EditableSchemaPropertiesPanel.this.resourceRepository().getString("XML_SCHEMA_DEFINITION_DESCRIPTION");
			}
		};
	}
	
	private MWXmlSchemaRepository schemaRepository() {
		return (MWXmlSchemaRepository) this.schemaRepositoryHolder.getValue();
	}

	MWProject project() {
		return this.schemaRepository().getProject();
	}
	
	private FileHolder buildFileChooserRootFileHolder() {
		return new FileHolder() {
			public File getFile() {
				return project().getSaveDirectory();
			}
			public void setFile(File file) {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	/**
	 * the default directory for the file chooser is one of the following:
	 * - project save directory
	 * - preference setting
	 * - user home directory
	 */
	private FileHolder buildFileChooserDefaultDirectoryHolder(final String prefKey) {
		return new FileHolder() {
			public File getFile() {
				File projectSaveDir = EditableSchemaPropertiesPanel.this.project().getSaveDirectory();
				if (projectSaveDir != null) {
					return projectSaveDir;
				}

				String prefDirName = EditableSchemaPropertiesPanel.this.preferences().get(prefKey, null);
				if (prefDirName != null) {
					return new File(prefDirName);
				}

				return FileTools.userHomeDirectory();
			}

			public void setFile(File file) {
				if (file.equals(FileTools.userHomeDirectory())) {
					// don't save the user home
					file = null;
				} else if (file.equals(EditableSchemaPropertiesPanel.this.project().getSaveDirectory())) {
					// don't save the project save directory
					file = null;
				}

				// if the directory is special, save it as a preference, otherwise clear the preference
				if (file == null) {
					EditableSchemaPropertiesPanel.this.preferences().remove(prefKey);
				} else {
					EditableSchemaPropertiesPanel.this.preferences().put(prefKey, file.getPath());
				}
			}
		};
	}

	private JRadioButton buildUrlRadioButton() {
		JRadioButton urlButton = new JRadioButton();
		urlButton.setModel(this.buildUrlRadioButtonModel());
		urlButton.setText(resourceRepository().getString("URL_RADIO_BUTTON_LABEL"));
		urlButton.setMnemonic(resourceRepository().getMnemonic("URL_RADIO_BUTTON_LABEL"));
		return urlButton;
	}
	
	private RadioButtonModelAdapter buildUrlRadioButtonModel() {
		return new RadioButtonModelAdapter(this.schemaLoadMechanismHolder, URL_MECHANISM);
	}
	
	private JTextField buildUrlTextField() {
		JTextField urlTextField = new JTextField();
		urlTextField.setEnabled(false);
		urlTextField.setDocument(new DocumentAdapter(this.urlHolder));
		this.schemaLoadMechanismHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildLoadMechanismListener(urlTextField, URL_MECHANISM));
		return urlTextField;
	}
	
	private JRadioButton buildClasspathRadioButton() {
		JRadioButton classpathButton = new JRadioButton();
		classpathButton.setModel(this.buildClasspathRadioButtonModel());
		classpathButton.setText(resourceRepository().getString("CLASSPATH_RADIO_BUTTON_LABEL"));
		classpathButton.setMnemonic(resourceRepository().getMnemonic("CLASSPATH_RADIO_BUTTON_LABEL"));
		return classpathButton;
	}
	
	private RadioButtonModelAdapter buildClasspathRadioButtonModel() {
		return new RadioButtonModelAdapter(this.schemaLoadMechanismHolder, CLASSPATH_MECHANISM);
	}
	
	private ClasspathResourcePanel buildClasspathResourcePanel() {
		ClasspathResourcePanel classpathResourcePanel = new ClasspathResourcePanel(this.classpathResourceNameHolder);
		classpathResourcePanel.setEnabled(false);
		this.schemaLoadMechanismHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildLoadMechanismListener(classpathResourcePanel, CLASSPATH_MECHANISM));
		return classpathResourcePanel;
	}		
	
	private PropertyChangeListener buildLoadMechanismListener(final Component component, final String loadMechanism) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				component.setEnabled(evt.getNewValue() == loadMechanism);
			}
		};
	}
	
	
	// **************** API ***************************************************
	
	String getSchemaName() {
		return (String) this.schemaNameHolder.getValue();
	}
	
	void setSchemaNameFromFile(File schemaFile) {
		String schemaName = schemaFile.getName();
		
		if (schemaName.lastIndexOf(".") != -1) {
			schemaName = schemaName.substring(0, schemaName.lastIndexOf("."));
		}
		
		this.schemaNameHolder.setValue(schemaName);
	}
	
	boolean isFileMechanism() {
		return this.schemaLoadMechanismHolder.getValue() == FILE_MECHANISM;
	}
	
	String getFileName() {
		return (String) this.fileHolder.getValue();
	}
	
	boolean isUrlMechanism() {
		return this.schemaLoadMechanismHolder.getValue() == URL_MECHANISM;
	}
	
	String getUrl() {
		return (String) this.urlHolder.getValue();
	}
	
	boolean isClasspathResourceMechanism() {
		return this.schemaLoadMechanismHolder.getValue() == CLASSPATH_MECHANISM;
	}
	
	String getClasspathResourceName() {
		return (String) this.classpathResourceNameHolder.getValue();
	}
	
	void populate(MWXmlSchema schema) {
		this.schemaNameHolder.setValue(schema.getName());
		
		ResourceSpecification schemaSource = schema.getSchemaSource();
		if (schemaSource instanceof FileResourceSpecification) {
			this.schemaLoadMechanismHolder.setValue(FILE_MECHANISM);
			this.fileHolder.setValue(schemaSource.getLocation());
		}
		else if (schemaSource instanceof UrlResourceSpecification) {
			this.schemaLoadMechanismHolder.setValue(URL_MECHANISM);
			this.urlHolder.setValue(schemaSource.getLocation());
		}
		if (schemaSource instanceof ClasspathResourceSpecification) {
			this.schemaLoadMechanismHolder.setValue(CLASSPATH_MECHANISM);
			this.classpathResourceNameHolder.setValue(schemaSource.getLocation());
		}
	}
	
	
	// **************** Member classes ****************************************
	
	private class ClasspathResourcePanel
		extends JPanel
	{
		private static final long serialVersionUID = 1L;

		private ClasspathResourcePanel(PropertyValueModel resourceNameHolder) {
			super();
			this.initialize(resourceNameHolder);
		}
		
		private void initialize(PropertyValueModel resourceNameHolder) {
			this.setLayout(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			
			//Resource name label
			JLabel resourceNameLabel = new JLabel(EditableSchemaPropertiesPanel.this.resourceRepository().getString("RESOURCE_NAME_LABEL"));
			constraints.gridx 		= 0;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx 	= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.LINE_START;
			constraints.insets		= new Insets(0, 0, 0, 0);
			this.add(resourceNameLabel, constraints);
			
			// Resource name text field
		 	JTextField resourceNameTextField = this.buildResourceNameTextField(resourceNameLabel, resourceNameHolder);
		 	constraints.gridx 		= 1;
			constraints.gridy		= 0;
			constraints.gridwidth 	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;	
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(0, 5, 0, 0);
			this.add(resourceNameTextField, constraints);
			resourceNameLabel.setLabelFor(resourceNameTextField);
		}
		
		private JTextField buildResourceNameTextField(JLabel resourceNameLabel, PropertyValueModel resourceNameHolder) {
			JTextField resourceNameTextField = new JTextField();
			resourceNameTextField.setDocument(new DocumentAdapter(resourceNameHolder));
			resourceNameTextField.setPreferredSize(new Dimension(200, (int) resourceNameTextField.getPreferredSize().getHeight()));
			resourceNameLabel.setLabelFor(resourceNameTextField);
			return resourceNameTextField;
		}
		
		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			
			for (Iterator components = CollectionTools.iterator(this.getComponents()); components.hasNext(); ) {
				((Component) components.next()).setEnabled(enabled);
			}
		}
	}
}
