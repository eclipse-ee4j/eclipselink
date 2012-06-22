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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.FileChooser;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;
import org.eclipse.persistence.tools.workbench.uitools.CancelException;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;



/**
 * This class is responsible for 
 * 	- gathering user information
 *	- displaying information back to the user
 * during the process of generating the table creator source code for a project.
 * 
 * author - pwf
 */
final class TableCreatorSourceGenerationCoordinator
{
		
	private class TableCreatorClassNameDialog
		extends AbstractDialog
	{
		private JTextField tableCreatorClassNameTextField;
			private PropertyValueModel tableCreatorClassNameHolder;
	
		private static final long serialVersionUID = 1L;

		private TableCreatorClassNameDialog(WorkbenchContext context, PropertyValueModel tableCreatorClassNameHolder, MWProject project) {
			super(context);
			this.tableCreatorClassNameHolder = tableCreatorClassNameHolder;	
			initialize(project);
		}
		
		protected void initialize(MWProject project) {
			super.initialize();
			setTitle(resourceRepository().getString("TABLE_CREATOR_CLASS_NAME_DIALOG.title", project.getName()));
			getOKAction().setEnabled(false);
			//setMinimumWidth(250);
		}
		
		protected String helpTopicId() {
			return "dialog.tableCreatorClassName";
		}
				
		protected Component buildMainPanel() {
			JPanel panel = new JPanel(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			
			JLabel messageLabel = new JLabel();
			messageLabel.setText(resourceRepository().getString("TABLE_CREATOR_CLASS_NAME_DIALOG.message"));
			messageLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("TABLE_CREATOR_CLASS_NAME_DIALOG.message"));
			constraints.gridx 		= 0;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.LINE_START;
			constraints.insets 		= new Insets(5, 5, 0, 5);
			panel.add(messageLabel, constraints);
			
			this.tableCreatorClassNameTextField = buildTableCreatorClassNameTextField();
			messageLabel.setLabelFor(this.tableCreatorClassNameTextField);
			constraints.gridx 		= 0;
			constraints.gridy		= 1;
			constraints.gridwidth	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets 		= new Insets(5, 5, 5, 5);
			panel.add(this.tableCreatorClassNameTextField, constraints);
			
			return panel;
		}
		
		private JTextField buildTableCreatorClassNameTextField() {
			JTextField textField = new JTextField();
			textField.setDocument(buildTableCreatorClassNameDocumentAdapter());
			return textField;
		}
		
		private Document buildTableCreatorClassNameDocumentAdapter() {
			DocumentAdapter adapter =  new DocumentAdapter(this.tableCreatorClassNameHolder, new RegexpDocument(RegexpDocument.RE_FULLY_QUALIFIED_CLASS_NAME));
			adapter.addDocumentListener(buildTableCreatorClassNameDocumentListener());
			return adapter;
		}
		
		protected Component initialFocusComponent() {
			return this.tableCreatorClassNameTextField;
		}
		
		String getTableCreatorClassName() {
			return (String) this.tableCreatorClassNameHolder.getValue();
		}
		
		private DocumentListener buildTableCreatorClassNameDocumentListener() {
			return new DocumentListener() {
				public void insertUpdate(DocumentEvent de) {
					tableCreatorClassNameChanged();
				}
				public void removeUpdate(DocumentEvent de) {
					tableCreatorClassNameChanged();
				}
				public void changedUpdate(DocumentEvent de) {
					tableCreatorClassNameChanged();
				}
			};
		}
		void tableCreatorClassNameChanged() {
			String tableCreatorClassName = getTableCreatorClassName();
			getOKAction().setEnabled(! tableCreatorClassName.equals("") && ! tableCreatorClassName.endsWith("."));
		}
	
	}
	
	private WorkbenchContext context;
	
	TableCreatorSourceGenerationCoordinator(WorkbenchContext context) {
		super();
		initialize(context);
	}
	
	private void initialize(WorkbenchContext ctx) {
		this.context = ctx;
	}
	
	private ResourceRepository resourceRepository() {
		return this.context.getApplicationContext().getResourceRepository();
	}
	
	void exportTableCreatorSource(MWRelationalProject project) {
		try {
			validateTableCreatorClassName(project);
			validateTableCreatorSourceRootDirectory(project);
			validateProjectProblems(project);
			validateOverwriteFile(project);
		}
		catch (CancelException ce) {
			return;
		}
		
		try {
			project.exportTableCreatorSource();
		}
		catch(RuntimeException e) {
//			ExceptionHandler.getHandler().handleException(e);
//			showErrorDialog(e, project);
			return;
		}
		
		showSuccessDialog(project);
	}
	
	private void validateTableCreatorClassName(MWRelationalProject project)
	{	
		String tableCreatorClassName = project.getTableCreatorSourceClassName();
		
		if (! tableCreatorClassName.equals("") && ! tableCreatorClassName.endsWith("."))
			return;
		
		TableCreatorClassNameDialog dlg = new TableCreatorClassNameDialog(this.context, buildTableCreatorClassNameHolder(tableCreatorClassName), project);
		dlg.show();
		
		if (dlg.wasCanceled()) {
			throw new CancelException();
		}
		project.setTableCreatorSourceClassName(dlg.getTableCreatorClassName());
	}
	
	private PropertyValueModel buildTableCreatorClassNameHolder(String name) {
		return new SimplePropertyValueModel(name);
	}
	
	private void validateTableCreatorSourceRootDirectory(MWRelationalProject project)
	{
		if (project.getTableCreatorSourceDirectoryName().equals(""))
			promptForTableCreatorSourceRootDirectory(project);
		
		if (project.absoluteTableCreatorSourceDirectory().isFile()) // i.e. not a directory
			promptForNonFileTableCreatorSourceRootDirectory(project);
					
		if (! project.absoluteTableCreatorSourceDirectory().exists())
			promptToCreateTableCreatorSourceRootDirectory(project);
		
		return;
	}
	
	private void validateProjectProblems(MWRelationalProject project)
	{
		// at this point, we assume that the project can have problems and the table creator can still be generated
	}
		
	private void validateOverwriteFile(MWRelationalProject project)
	{
		File tableCreatorFile = project.tableCreatorSourceFile();
		String tableCreatorFilePath = tableCreatorFile.getAbsolutePath();
		
		try
		{
			tableCreatorFilePath = tableCreatorFile.getCanonicalPath();
		}
		catch (IOException ioe) 
		{
			/* If we haven't determined that the path is legal by now,
				we're already in trouble. */
		}
		
		if (! tableCreatorFile.exists())
			return;
			
		int input = JOptionPane.showConfirmDialog(this.context.getCurrentWindow(),
									resourceRepository().getString("TABLE_CREATOR_FILE_EXISTS_DIALOG.message", 
																	new Object[] {tableCreatorFilePath}),
									resourceRepository().getString("TABLE_CREATOR_FILE_EXISTS_DIALOG.title"),
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE);
																	
		
		if (input != JOptionPane.YES_OPTION)
			throw new CancelException();
	}
	
	/**
	 * used when there is no directory already chosen
	 */
	private void promptForTableCreatorSourceRootDirectory(MWRelationalProject project)
	{
		String description = resourceRepository().getString("TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_DIALOG_NO_DIRECTORY_CHOSEN.message");
		promptForDirectory(project, description);
	}
	
	/**
	 * used when the chosen directory is not actually a directory
	 */
	private void promptForNonFileTableCreatorSourceRootDirectory(MWRelationalProject project)
	{
		String description = resourceRepository().getString("TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_DIALOG_CHOSEN_DIRECTORY_IS_A_FILE.message",
															new Object[] {project.absoluteTableCreatorSourceDirectory()});
		promptForDirectory(project, description);
	}
	
	/**
	 * used when the chosen directory is invalid (e.g. "Z:\foo" if there is no Z drive)
	 */
	private void promptForValidTableCreatorSourceRootDirectory(MWRelationalProject project)
	{
		String description = resourceRepository().getString("TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_DIALOG_CHOSEN_DIRECTORY_IS_INVALID.message",
															new Object[] {project.absoluteTableCreatorSourceDirectory()});
		promptForDirectory(project, description);
	}
	
	/**
	 * used when the chosen directory could not be created (various reasons)
	 */
	private void promptForCreatableTableCreatorSourceRootDirectory(MWRelationalProject project)
	{
		String description = resourceRepository().getString("TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_DIALOG_CHOSEN_DIRECTORY_COULD_NOT_BE_CREATED.message",
															new Object[] {project.absoluteTableCreatorSourceDirectory()});
		promptForDirectory(project, description);
	}
	
	private Preferences preferences() {
		return this.context.getApplicationContext().getPreferences();
	}

	private void promptForDirectory(MWRelationalProject project, String description)
	{
		File startDir = MappingsPlugin.buildExportDirectory(project, project.getTableCreatorSourceDirectoryName(), this.preferences());
		JFileChooser fileChooser = new FileChooser(startDir, project.getSaveDirectory());
		fileChooser.setDialogTitle(resourceRepository().getString("TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_DIALOG.title", project.getName()));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setApproveButtonText(resourceRepository().getString("TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_DIALOG_SELECT_BUTTON"));
		fileChooser.setApproveButtonMnemonic(resourceRepository().getMnemonic("TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_DIALOG_SELECT_BUTTON"));
	
		int option = fileChooser.showOpenDialog(this.context.getCurrentWindow());
		
		if (option == JFileChooser.CANCEL_OPTION) {
			throw new CancelException();
		}
		File directory = fileChooser.getSelectedFile();
		project.setTableCreatorSourceDirectoryName(directory.getPath());
		if ((directory != null) && directory.isAbsolute() && ! directory.equals(project.getSaveDirectory())) {
			this.preferences().put(MappingsPlugin.EXPORT_LOCATION_PREFERENCE, directory.getPath());
		}
	}
	
	private void promptToCreateTableCreatorSourceRootDirectory(MWRelationalProject project)
	{
		File canonicalDirectory;
		
		try
		{
			canonicalDirectory = project.absoluteTableCreatorSourceDirectory().getCanonicalFile();
		}
		catch (IOException ioe)
		{
			promptForValidTableCreatorSourceRootDirectory(project);
			return;
		}
		
		int input = JOptionPane.showConfirmDialog(this.context.getCurrentWindow(),
								resourceRepository().getString("CREATE_TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_DIALOG.message", 
																new Object[] {canonicalDirectory}),
								resourceRepository().getString("CREATE_TABLE_CREATOR_SOURCE_ROOT_DIRECTORY_DIALOG.title"),
								JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE);
																	
		
		if (input == JOptionPane.CANCEL_OPTION) {
			throw new CancelException();
		}
		if (! project.absoluteTableCreatorSourceDirectory().mkdirs()) {
			promptForCreatableTableCreatorSourceRootDirectory(project);
		}
	}
	
	private void showSuccessDialog(MWProject project) {
		JOptionPane.showMessageDialog(this.context.getCurrentWindow(),
							resourceRepository().getString("GENERATE_TABLE_CREATOR_SOURCE_SUCCESS_DIALOG.message"),
							resourceRepository().getString("GENERATE_TABLE_CREATOR_SOURCE_SUCCESS_DIALOG.title", project.getName()),
							JOptionPane.INFORMATION_MESSAGE);
	}
	
	//TODO show the error dialog~kfm
//	private void showErrorDialog(Throwable t, MWRelationalProject project)
//	{
//		ErrorDialogPane dialogPane = new ErrorDialogPane();
//		dialogPane.setMessage(this.resourceBundle.getMessage("GENERATE_TABLE_CREATOR_SOURCE_ERROR_DIALOG", 
//															 new Object[] {project.tableCreatorSourceFile().getAbsolutePath()}));
//		dialogPane.setThrowable(t);
//		dialogPane.showDialog();
//	}
}
