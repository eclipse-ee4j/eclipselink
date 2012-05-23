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
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.persistence.tools.workbench.framework.context.DefaultWorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractValidatingDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.WaitDialog;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.ResourceException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchemaRepository;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractReadOnlyPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


class ImportSchemaDialog 
	extends AbstractValidatingDialog
{
	private EditableSchemaPropertiesPanel schemaPanel;
	
	MWXmlSchemaRepository schemaRepository;
	
	MWXmlSchema importedSchema;
	
	
	// **************** Constructors ******************************************
	
	ImportSchemaDialog(WorkbenchContext context, MWXmlSchemaRepository schemaRepository) {
		super(context);
		this.schemaRepository = schemaRepository;
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize() {
		super.initialize();
		
		this.setTitle(this.resourceRepository().getString("IMPORT_SCHEMA_DIALOG.TITLE"));
		this.setSize(550, 325);
		this.getOKAction().setEnabled(false);
	}
		
	protected Component buildMainPanel() {
		this.schemaPanel = this.buildSchemaPanel();
		return this.schemaPanel;
	}
	
	private EditableSchemaPropertiesPanel buildSchemaPanel() {
		EditableSchemaPropertiesPanel panel = new EditableSchemaPropertiesPanel(this.buildSchemaRepositoryHolder(), new DefaultWorkbenchContextHolder(this.getWorkbenchContext()));
		panel.addPropertyChangeListener(EditableSchemaPropertiesPanel.SCHEMA_PROPERTY, this.buildSchemaPropertyChangeListener());
		return panel;
	}
	
	private PropertyValueModel buildSchemaRepositoryHolder() {
		return new AbstractReadOnlyPropertyValueModel() {
			public Object getValue() {
				return ImportSchemaDialog.this.schemaRepository;
			}
		};
	}
	
	private PropertyChangeListener buildSchemaPropertyChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				ImportSchemaDialog.this.updateForErrors();
			}
		};
	}
	
	
	// **************** AbstractDialog contract *******************************
	
	protected String helpTopicId() {
		return "dialog.addSchema";
	}
	
	
	// **************** Behavior **********************************************
	
	public MWXmlSchema promptToImportSchema() {
		this.show();
		return this.importedSchema;
	}
	//overriding so that the dialog is not packed, this allows 
	//the setSize in initialize to take effect
	protected void prepareToShow() {
		this.setLocationRelativeTo(this.getParent());
	}

	void updateForErrors() {
		boolean noError = 
			this.checkSchemaNameOk()
			&& this.checkLoadMechanismOk();
		
		if (noError) {
			this.setErrorMessage(null);
		}
		
		this.getOKAction().setEnabled(noError);
	}
	
	private boolean checkSchemaNameOk() {
		String schemaName = this.schemaPanel.getSchemaName();
		
		// schema name specified
		if (StringTools.stringIsEmpty(schemaName)) {
			this.setErrorMessage(this.resourceRepository().getString("SCHEMA_NAME_NOT_SPECIFIED_ERROR_MESSAGE"));
			return false;
		}
		
		// schema name unique
		if ((this.schemaRepository.getSchema(schemaName) != null)
					|| this.schemaRepository.containsSchemaIgnoreCase(schemaName)) {
			this.setErrorMessage(this.resourceRepository().getString("SCHEMA_NAME_NOT_UNIQUE_ERROR_MESSAGE"));
			return false;
		}
		
		return true;
	}
	
	private boolean checkLoadMechanismOk() {
		return this.checkFileMechanismOk()
				&& this.checkUrlMechanismOk()
				&& this.checkClasspathResourceMechanismOk();
	}
	
	private boolean checkFileMechanismOk() {
		if (this.schemaPanel.isFileMechanism()) {
			String schemaFileName = this.schemaPanel.getFileName();
			
			if (schemaFileName == null || schemaFileName.equals("")) {
				this.setErrorMessage(this.resourceRepository().getString("FILE_NOT_SPECIFIED_ERROR_MESSAGE"));
				return false;
			}
		}
		
		return true;
	}
	
	private boolean checkUrlMechanismOk() {
		if (this.schemaPanel.isUrlMechanism()) {
			String schemaUrl = this.schemaPanel.getUrl();
			
			if (schemaUrl == null || schemaUrl.equals("")) {
				this.setErrorMessage(this.resourceRepository().getString("URL_NOT_SPECIFIED_ERROR_MESSAGE"));
				return false;
			}
		}
		
		return true;
	}
	
	private boolean checkClasspathResourceMechanismOk() {
		if (this.schemaPanel.isClasspathResourceMechanism()) {
			String schemaResourceName = this.schemaPanel.getClasspathResourceName();
			
			if (schemaResourceName == null || schemaResourceName.equals("")) {
				this.setErrorMessage(this.resourceRepository().getString("CLASSPATH_RESOURCE_NOT_SPECIFIED_ERROR_MESSAGE"));
				return false;
			}
		}
		
		return true;
	}
	
	protected boolean preConfirm() {
		startImportationThread();
		//	 Returns false, we'll close the dialog once the validation is complete
		return false;
	}

	/**
	 * Start a thread that will import tables from the database
	 */
	private void startImportationThread() {
		Thread thread = new Thread(new ImportationRunnable(buildSchemaCreator()), "XML Schema  Importation");
		thread.setPriority(Thread.NORM_PRIORITY - (Thread.NORM_PRIORITY - Thread.MIN_PRIORITY) /2);
		thread.start();
	}

	private SchemaCreator buildSchemaCreator() {
		if (this.schemaPanel.isFileMechanism()) {
			return new FileSchemaCreator(this.schemaPanel.getSchemaName(), this.schemaPanel.getFileName());
		}
		else if (this.schemaPanel.isUrlMechanism()) {
			return new URLSchemaCreator(this.schemaPanel.getSchemaName(), this.schemaPanel.getUrl());
		}
		else if (this.schemaPanel.isClasspathResourceMechanism()) {
			return new ClasspathSchemaCreator(this.schemaPanel.getSchemaName(), this.schemaPanel.getClasspathResourceName());
		}

		throw new IllegalArgumentException();
	}
	
	void showUrlLoadFailure(String schemaName, ResourceException re) {
		if (this.schemaPanel.isFileMechanism()) {
			SchemaDialogUtilities.showUrlLoadFailure(this, schemaName, re, "FILE_RESOURCE", this.schemaPanel.getFileName());
		}
		else if (this.schemaPanel.isUrlMechanism()) {
			SchemaDialogUtilities.showUrlLoadFailure(this, schemaName, re, "URL_RESOURCE", this.schemaPanel.getUrl());
		}
		else if (this.schemaPanel.isClasspathResourceMechanism()) {
			SchemaDialogUtilities.showUrlLoadFailure(this, schemaName, re, "CLASSPATH_RESOURCE", this.schemaPanel.getClasspathResourceName());
		}
		
		this.getOKAction().setEnabled(false);
	}
	
	void showSchemaLoadFailure(String schemaName) {
		SchemaDialogUtilities.showSchemaLoadFailure(this, schemaName, null);
		this.getOKAction().setEnabled(false);
	}
	
	void showSchemaLoadFailure(String schemaName, RuntimeException re) {
		SchemaDialogUtilities.showSchemaLoadFailure(this, schemaName, re);
		this.getOKAction().setEnabled(false);
	}

	/*
	private void initializeFields() {
		schemaNameTextField.setText(schema.getName());
		schemaNameTextField.setEnabled(true);
		if(schema.getSource() == MWXmlSchema.URL) {
			urlTextField.setText(schema.getPath());
			urlButton.setSelected(true);
			urlTextField.setEnabled(true);
			fileChooser.setEnabled(false);
			cpTextField.setEnabled(false);
		}
		else if(schema.getSource() == MWXmlSchema.FILE) {
			fileChooser.setSelectedFile(new File(schema.getPath()));
			fileButton.setSelected(true);
			fileChooser.setEnabled(true);
			urlTextField.setEnabled(false);
			cpTextField.setEnabled(false);
		}
		else if(schema.getSource() == MWXmlSchema.CLASSPATH) {
			cpTextField.setText(schema.getPath());
			cpTextField.setEnabled(true);
			cpButton.setSelected(true);
			urlTextField.setEnabled(false);
			fileChooser.setEnabled(false);
		}
	}
	*/

	/**
	 * loosen the access level a bit
	 */
	protected void okConfirmed() {
		super.okConfirmed();
	}

	private class ImportationRunnable implements Runnable {
		WaitDialog waitDialog;
		SchemaCreator schemaCreator;

		ImportationRunnable(SchemaCreator schemaCreator) {
			super();
			initialize(schemaCreator);
		}

		private void initialize(SchemaCreator sc) {
			this.schemaCreator = sc;
			this.waitDialog = new WaitDialog(
					(Dialog) getWorkbenchContext().getCurrentWindow(),
					resourceRepository().getIcon("file.xml.large"),
					resourceRepository().getString("IMPORT_SCHEMA_DIALOG.WAIT_DIALOG.TITLE"),
					resourceRepository().getString("IMPORT_SCHEMA_DIALOG.WAIT_DIALOG.DESCRIPTION", sc.getSchemaName())
			);
		}

		private void showWaitCursor() {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					ImportSchemaDialog.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					getWorkbenchContext().getCurrentWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				}
			});
		}

		private void hideWaitCursor() {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					ImportSchemaDialog.this.setCursor(Cursor.getDefaultCursor());
					getWorkbenchContext().getCurrentWindow().setCursor(Cursor.getDefaultCursor());
				}
			});
		}

		private void completeTermination(MWXmlSchema schema) {
			ImportSchemaDialog.this.importedSchema = schema;
			disposeImportSchemaDialog();
		}

		private void disposeImportSchemaDialog() {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					ImportSchemaDialog.this.okConfirmed();
				}
			});
		}

		public void run() {
			try {
				MWXmlSchema schema = importSchema();
				completeTermination(schema);
			}
			catch (final ResourceException re) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						ImportSchemaDialog.this.showUrlLoadFailure(ImportationRunnable.this.schemaCreator.getSchemaName(), re);
					}
				});
			}
			// In XDK version 9.0.4 only RuntimeExceptons are thrown
			catch (final RuntimeException re) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						ImportSchemaDialog.this.showSchemaLoadFailure(ImportationRunnable.this.schemaCreator.getSchemaName(), re);
					}
				});
			}
		}

		private MWXmlSchema importSchema() throws RuntimeException, ResourceException {
			showWaitCursor();
			launchLater(this.waitDialog);
			try {
				return this.schemaCreator.createSchema();
			}
			finally {
				hideWaitCursor();
				this.waitDialog.dispose();
			}
		}

	}

	private interface SchemaCreator {
		MWXmlSchema createSchema() throws RuntimeException, ResourceException;
		public String getSchemaName();
		public String getFileName();
	}

	private abstract class AbstractSchemaCreator implements SchemaCreator {
		protected String fileName;
		protected String schemaName;
		
		AbstractSchemaCreator(String schemaName, String fileName) {
			super();
			this.fileName = fileName;
			this.schemaName = schemaName;
		}

		public String getSchemaName() {
			return this.schemaName;
		}

		public String getFileName() {
			return this.fileName;
		}
	}

	private class ClasspathSchemaCreator extends AbstractSchemaCreator {
		ClasspathSchemaCreator(String schemaName, String fileName) {
			super(schemaName, fileName);
		}

		public MWXmlSchema createSchema() throws RuntimeException, ResourceException {
			return ImportSchemaDialog.this.schemaRepository.createSchemaFromClasspath(this.schemaName, this.fileName);
		}
	}

	private class FileSchemaCreator extends AbstractSchemaCreator {
		FileSchemaCreator(String schemaName, String fileName) {
			super(schemaName, fileName);
		}

		public MWXmlSchema createSchema() throws RuntimeException, ResourceException {
			return ImportSchemaDialog.this.schemaRepository.createSchemaFromFile(this.schemaName, this.fileName);
		}
	}

	private class URLSchemaCreator extends AbstractSchemaCreator {
		URLSchemaCreator(String schemaName, String fileName) {
			super(schemaName, fileName);
		}

		public MWXmlSchema createSchema() throws RuntimeException, ResourceException {
			return ImportSchemaDialog.this.schemaRepository.createSchemaFromUrl(this.schemaName, this.fileName);
		}
	}
}
