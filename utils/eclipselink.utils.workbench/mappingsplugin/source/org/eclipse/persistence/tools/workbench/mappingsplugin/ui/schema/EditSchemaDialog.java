/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.eclipse.persistence.tools.workbench.framework.context.DefaultWorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractValidatingDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.WaitDialog;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.ResourceException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractReadOnlyPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


final class EditSchemaDialog 
	extends AbstractValidatingDialog 
{
	private EditableSchemaPropertiesPanel schemaPanel;
	
	MWXmlSchema schema;
	
	private Action reimportAction;
	
	
	// **************** Constructors ******************************************
	
	EditSchemaDialog(WorkbenchContext context, MWXmlSchema schema) {
		super(context);
		this.schema = schema;
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize() {
		super.initialize();
		
		this.setTitle(this.resourceRepository().getString("EDIT_SCHEMA_DIALOG.TITLE"));
		this.setSize(550, 250);
		this.getOKAction().setEnabled(false);
	}
		
	protected Iterator buildCustomActions() {
		this.reimportAction = this.buildReimportAction();
		return new SingleElementIterator(this.reimportAction);
	}
	
	private Action buildReimportAction() {
		Action action = new AbstractAction(this.buildReimportText()) {
			public void actionPerformed(ActionEvent e) {
				EditSchemaDialog.this.reimportSchema();
			}
		};
		action.setEnabled(false);
		return action;
	}	

	protected String buildReimportText() {
		return this.resourceRepository().getString("EDIT_SCHEMA_DIALOG.REIMPORT_TEXT");
	}
	
	protected Component buildMainPanel() {
		this.schemaPanel = this.buildSchemaPanel();
		return this.schemaPanel;
	}
	
	private EditableSchemaPropertiesPanel buildSchemaPanel() {
		EditableSchemaPropertiesPanel panel = new EditableSchemaPropertiesPanel(this.buildSchemaRepositoryHolder(), new DefaultWorkbenchContextHolder(this.getWorkbenchContext()));
		panel.populate(this.schema);
		panel.addPropertyChangeListener(EditableSchemaPropertiesPanel.SCHEMA_PROPERTY, this.buildSchemaPropertyChangeListener());
		return panel;
	}
	
	private PropertyValueModel buildSchemaRepositoryHolder() {
		return new AbstractReadOnlyPropertyValueModel() {
			public Object getValue() {
				return EditSchemaDialog.this.schema.schemaRepository();
			}
		};
	}
	
	private PropertyChangeListener buildSchemaPropertyChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				EditSchemaDialog.this.updateForErrors();
			}
		};
	}
	
	
	// **************** AbstractDialog contract *******************************
	
	protected String helpTopicId() {
		return "dialog.schemaProperties";
	}
	
	
	// **************** Behavior **********************************************
	
	public void promptToEditSchema() {
		this.show();
	}
	
	protected void prepareToShow() {
		super.prepareToShow();
		updateForErrors();
	}
	
	void updateForErrors() {
		boolean noError = 
			this.checkSchemaNameOk()
			&& this.checkLoadMechanismOk();
		
		if (noError) {
			this.setErrorMessage(null);
		}
		
		this.reimportAction.setEnabled(noError);
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
		MWXmlSchema schemaWithNewName = this.schema.schemaRepository().getSchema(schemaName);
		if (schemaWithNewName != null && schemaWithNewName != this.schema) {
			this.setErrorMessage(this.resourceRepository().getString("SCHEMA_NAME_NOT_UNIQUE_ERROR_MESSAGE"));
			return false;
		}
		
		// schema name unique 2
		MWXmlSchema schemaWithNewNameIgnoreCase = this.schema.schemaRepository().getSchemaIgnoreCase(schemaName);
		if (schemaWithNewNameIgnoreCase != null && schemaWithNewNameIgnoreCase != this.schema) {
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
	
	protected void okConfirmed() {
		this.setSchemaValues();
		super.okConfirmed();
	}
	
	void reimportSchema() {
		this.setSchemaValues();
		this.startReloadSchemaThread();
	}
	
	private void setSchemaValues() {
		this.schema.setName(this.schemaPanel.getSchemaName());
		
		if (this.schemaPanel.isFileMechanism()) {
			this.schema.setFileSchemaLocation(this.schemaPanel.getFileName());
		}
		else if (this.schemaPanel.isUrlMechanism()) {
			this.schema.setUrlSchemaLocation(this.schemaPanel.getUrl());
		}
		else if (this.schemaPanel.isClasspathResourceMechanism()) {
			this.schema.setClasspathSchemaLocation(this.schemaPanel.getClasspathResourceName());
		}
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

	/**
	 * Starts a thread that will reload the XML schema.
	 */
	private void startReloadSchemaThread() {
		Thread thread = new Thread(new ReloadSchemaRunnable(), "XML Schema  Importation");
		thread.setPriority(Thread.NORM_PRIORITY - (Thread.NORM_PRIORITY - Thread.MIN_PRIORITY) /2);
		thread.start();
	}

	private class ReloadSchemaRunnable implements Runnable {
		private WaitDialog waitDialog;

		ReloadSchemaRunnable() {
			super();
			initialize();
		}

		private void initialize() {
			this.waitDialog = new WaitDialog((Dialog) getWorkbenchContext().getCurrentWindow(), resourceRepository().getIcon("file.xml.large"), resourceRepository().getString("IMPORT_SCHEMA_DIALOG.WAIT_DIALOG.TITLE"), resourceRepository().getString("IMPORT_SCHEMA_DIALOG.WAIT_DIALOG.DESCRIPTION", EditSchemaDialog.this.schema.getName()));
		}

		private void showWaitCursor() {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					EditSchemaDialog.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					getWorkbenchContext().getCurrentWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				}
			});
		}

		private void hideWaitCursor() {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					EditSchemaDialog.this.setCursor(Cursor.getDefaultCursor());
					getWorkbenchContext().getCurrentWindow().setCursor(Cursor.getDefaultCursor());
				}
			});
		}

		private void disposeImportSchemaDialog() {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					EditSchemaDialog.this.okConfirmed();
				}
			});
		}

		public void run() {
			try {
				reloadSchema();
				disposeImportSchemaDialog();
			}
			catch (final ResourceException re) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						EditSchemaDialog.this.showUrlLoadFailure(EditSchemaDialog.this.schema.getName(), re);
					}
				});
			}
			// In XDK version 9.0.4 only RuntimeExceptons are thrown
			catch (final RuntimeException re) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						EditSchemaDialog.this.showSchemaLoadFailure(EditSchemaDialog.this.schema.getName(), re);
					}
				});
			}
		}

		private void reloadSchema() throws RuntimeException,ResourceException {
			showWaitCursor();
			launchLater(this.waitDialog);
			try {
				EditSchemaDialog.this.schema.reload();
			}
			finally {
				hideWaitCursor();
				this.waitDialog.dispose();
			}
		}
	}
}
