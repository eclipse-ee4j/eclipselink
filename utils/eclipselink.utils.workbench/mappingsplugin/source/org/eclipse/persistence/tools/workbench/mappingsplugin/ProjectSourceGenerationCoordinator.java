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
package org.eclipse.persistence.tools.workbench.mappingsplugin;

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
import org.eclipse.persistence.tools.workbench.uitools.CancelException;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This class is responsible for 
 * 	- gathering user information
 *	- displaying information back to the user
 * during the process of exporting a project to java source.
 * 
 * author - pwf
 */
public final class ProjectSourceGenerationCoordinator {
	
	private WorkbenchContext context;
	
	ProjectSourceGenerationCoordinator(WorkbenchContext context) {
		super();
		this.context = context;
	}
	
	private ResourceRepository resourceRepository() {
		return this.context.getApplicationContext().getResourceRepository();
	}
	
	void exportProjectSource(MWProject project)
	{
		try
		{
			validateProjectClassName(project);
			validateProjectSourceRootDirectory(project);
			validateProjectProblems(project);
			validateOverwriteProject(project);
		}
		catch (CancelException ce)
		{
			return;
		}
		
		project.exportProjectSource();
		showSuccessDialog(project);
	}
	
	private void validateProjectClassName(MWProject project)
	{	
		String projectClassName = project.getProjectSourceClassName();
		
		if (! projectClassName.equals("") && ! projectClassName.endsWith("."))
			return;
		
		ProjectClassNameDialog dlg = new ProjectClassNameDialog(this.context, buildProjectClassNameHolder(projectClassName), project.getName());
		dlg.show();
		
		if (dlg.wasCanceled()) {
			throw new CancelException();
		}
		project.setProjectSourceClassName(dlg.getProjectClassName());
	}
	
	private PropertyValueModel buildProjectClassNameHolder(String name) {
		return new SimplePropertyValueModel(name);
	}
	
	private void validateProjectSourceRootDirectory(MWProject project)
	{
		if (project.getProjectSourceDirectoryName().equals(""))
			promptForProjectSourceRootDirectory(project);
		
		if (project.absoluteProjectSourceDirectory().isFile()) // i.e. not a directory
			promptForNonFileProjectSourceRootDirectory(project);
					
		if (! project.absoluteProjectSourceDirectory().exists())
			promptToCreateProjectSourceRootDirectory(project);
		
		return;
	}
	
	/**
	 * used when there is no directory already chosen
	 */
	private void promptForProjectSourceRootDirectory(MWProject project)
	{
		String description = resourceRepository().getString("PROJECT_SOURCE_ROOT_DIRECTORY_DIALOG_NO_DIRECTORY_CHOSEN.message");
		promptForDirectory(project, description);
	}	
	
	/**
	 * used when the chosen directory is not actually a directory
	 */
	private void promptForNonFileProjectSourceRootDirectory(MWProject project)
	{
		String description = resourceRepository().getString("PROJECT_SOURCE_ROOT_DIRECTORY_DIALOG_CHOSEN_DIRECTORY_IS_A_FILE.message",
															new Object[] {project.absoluteProjectSourceDirectory()});
		promptForDirectory(project, description);
	}
	
	/**
	 * used when the chosen directory is invalid (e.g. "Z:\foo" if there is no Z drive)
	 */
	private void promptForValidProjectSourceRootDirectory(MWProject project)
	{
		String description = resourceRepository().getString("PROJECT_SOURCE_ROOT_DIRECTORY_DIALOG_CHOSEN_DIRECTORY_IS_INVALID.message",
															new Object[] {project.absoluteProjectSourceDirectory()});
		promptForDirectory(project, description);
	}
	
	/**
	 * used when the chosen directory could not be created (various reasons)
	 */
	private void promptForCreatableProjectSourceRootDirectory(MWProject project)
	{
		String description = resourceRepository().getString("PROJECT_SOURCE_ROOT_DIRECTORY_DIALOG_CHOSEN_DIRECTORY_COULD_NOT_BE_CREATED.message",
															new Object[] {project.absoluteProjectSourceDirectory()});
		promptForDirectory(project, description);
	}
	
	private Preferences preferences() {
		return this.context.getApplicationContext().getPreferences();
	}

	private void promptForDirectory(MWProject project, String description) {	
		File startDir = MappingsPlugin.buildExportDirectory(project, project.getProjectSourceDirectoryName(), this.preferences());
		JFileChooser fileChooser = new FileChooser(startDir, project.getSaveDirectory());
		fileChooser.setDialogTitle(resourceRepository().getString("PROJECT_SOURCE_ROOT_DIRECTORY_DIALOG.title", project.getName()));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setApproveButtonText(resourceRepository().getString("PROJECT_SOURCE_ROOT_DIRECTORY_DIALOG_SELECT_BUTTON"));
		fileChooser.setApproveButtonMnemonic(resourceRepository().getMnemonic("PROJECT_SOURCE_ROOT_DIRECTORY_DIALOG_SELECT_BUTTON"));
		
		int option = fileChooser.showOpenDialog(this.context.getCurrentWindow());
		
		if (option == JFileChooser.CANCEL_OPTION) {
			throw new CancelException();
		}
		File directory = fileChooser.getSelectedFile();
		project.setProjectSourceDirectoryName(directory.getPath());
		if ((directory != null) && directory.isAbsolute() && ! directory.equals(project.getSaveDirectory())) {
			this.preferences().put(MappingsPlugin.EXPORT_LOCATION_PREFERENCE, directory.getPath());
		}
	}
	
	private void promptToCreateProjectSourceRootDirectory(MWProject project)
	{
		File canonicalProjectSourceRootDirectory;
		
		try
		{
			canonicalProjectSourceRootDirectory = project.absoluteProjectSourceDirectory().getCanonicalFile();
		}
		catch (IOException ioe)
		{
			promptForValidProjectSourceRootDirectory(project);
			return;
		}
		
		int input = JOptionPane.showConfirmDialog(this.context.getCurrentWindow(),
										resourceRepository().getString("CREATE_PROJECT_SOURCE_ROOT_DIRECTORY_DIALOG.message", 
																							new Object[] {canonicalProjectSourceRootDirectory}),
										resourceRepository().getString("CREATE_PROJECT_SOURCE_ROOT_DIRECTORY_DIALOG.title"),
										JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE);
		
		if (input != JOptionPane.YES_OPTION) {
			throw new CancelException();
		}
		if (! project.absoluteProjectSourceDirectory().mkdirs())
			promptForCreatableProjectSourceRootDirectory(project);
	}
	
	private void validateProjectProblems(MWProject project)
	{
		if (!project.hasBranchProblems()) {
			return;
		}
		
		int input = JOptionPane.showConfirmDialog(this.context.getCurrentWindow(),
										new LabelArea(resourceRepository().getString("exportingProjectJavaSource", StringTools.CR)),
										resourceRepository().getString("exportProjectJavaSource", project.getName()),
										JOptionPane.YES_NO_OPTION,
										JOptionPane.WARNING_MESSAGE );
		
		if(input != JOptionPane.YES_OPTION) {
			throw new CancelException();
		}
	}
		
	private void validateOverwriteProject(MWProject project)
	{
		File projectFile = project.projectSourceFile();
		String projectFilePath = projectFile.getAbsolutePath();
		
		try
		{
			projectFilePath = projectFile.getCanonicalPath();
		}
		catch (IOException ioe) 
		{
			/* If we haven't determined that the path is legal by now,
				we're already in trouble. */
		}
		
		if (! projectFile.exists())
			return;
			
		int input = JOptionPane.showConfirmDialog(this.context.getCurrentWindow(),
										resourceRepository().getString("PROJECT_FILE_EXISTS_DIALOG.message", projectFilePath),
										resourceRepository().getString("PROJECT_FILE_EXISTS_DIALOG.title"), 
										JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE );
		
		if(input != JOptionPane.YES_OPTION) {
			throw new CancelException();
		}
	}
	
	private void showSuccessDialog(MWProject project) {
		JOptionPane.showMessageDialog(this.context.getCurrentWindow(),
						resourceRepository().getString("EXPORT_PROJECT_SOURCE_SUCCESS_DIALOG.message"),
						resourceRepository().getString("EXPORT_PROJECT_SOURCE_SUCCESS_DIALOG.title", project.getName()),
						JOptionPane.INFORMATION_MESSAGE );
	}
	
	
	
	
	private class ProjectClassNameDialog extends AbstractDialog {
		
		private JTextField projectClassNameTextField;
		private PropertyValueModel projectClassNameHolder;
		private static final long serialVersionUID = 1L;
	
		private ProjectClassNameDialog(WorkbenchContext context, PropertyValueModel projectClassNameHolder, String projectName) {
			super(context);
			this.projectClassNameHolder = projectClassNameHolder;
			initialize(projectName);
		}
		
		protected void initialize(String projectName) {
			super.initialize();
			setTitle(resourceRepository().getString("PROJECT_CLASS_NAME_DIALOG.title", projectName));
			getOKAction().setEnabled(false);
			//setMinimumWidth(250);
		}
		
		protected String helpTopicId() {
			return "dialog.projectSourceClassName";
		}
		
		protected Component buildMainPanel() {
			JPanel mainPanel = new JPanel(new GridBagLayout());

			GridBagConstraints constraints = new GridBagConstraints();
			
			JLabel messageLabel = new JLabel();
			messageLabel.setText(resourceRepository().getString("PROJECT_CLASS_NAME_DIALOG.message"));
			messageLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("PROJECT_CLASS_NAME_DIALOG.message"));
			constraints.gridx 		= 0;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.LINE_START;
			constraints.insets 		= new Insets(5, 5, 0, 5);
			mainPanel.add(messageLabel, constraints);
			
			this.projectClassNameTextField = buildProjectClassNameTextField();
			helpManager().addTopicID(this.projectClassNameTextField, helpTopicId() + ".name");
			messageLabel.setLabelFor(this.projectClassNameTextField);
			constraints.gridx 		= 0;
			constraints.gridy		= 1;
			constraints.gridwidth	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets 		= new Insets(5, 5, 5, 5);
			mainPanel.add(this.projectClassNameTextField, constraints);
			
			return mainPanel;
		}
	
		protected Component initialFocusComponent() {
			return this.projectClassNameTextField;
		}

		private JTextField buildProjectClassNameTextField() {
			JTextField textField = new JTextField();
			textField.setDocument(buildProjectClassNameDocumentAdapter());
			return textField;
		}
		
		private Document buildProjectClassNameDocumentAdapter() {
			DocumentAdapter adapter =  new DocumentAdapter(this.projectClassNameHolder, new RegexpDocument(RegexpDocument.RE_FULLY_QUALIFIED_CLASS_NAME));
			adapter.addDocumentListener(buildProjectClassNameDocumentListener());
			return adapter;
		}
		
		private DocumentListener buildProjectClassNameDocumentListener() {
			return new DocumentListener() {
				public void insertUpdate(DocumentEvent de) {
					projectClassNameChange();
				}
		
				public void removeUpdate(DocumentEvent de) {
					projectClassNameChange();
				}
		
				public void changedUpdate(DocumentEvent de) {
					projectClassNameChange();
				}
			};
		}
		
		void projectClassNameChange() {
			String projectClassName = getProjectClassName();
			getOKAction().setEnabled(! projectClassName.equals("") && ! projectClassName.endsWith("."));
		}
		
		String getProjectClassName() {
			return (String) this.projectClassNameHolder.getValue();
		}

	}
}
