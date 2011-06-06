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
package org.eclipse.persistence.tools.workbench.mappingsplugin.sourcegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.ExceptionDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.FileChooser;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;
import org.eclipse.persistence.tools.workbench.uitools.CancelException;


public final class ModelSourceGenerationCoordinator
	implements SourceCodeGenerator.FileAlreadyExistsListener, 
			   SourceCodeGenerator.ContinuableExceptionListener
{

	private WorkbenchContext context;
	private SourceCodeGenerator generator;
	
	private boolean processCanceled = false;
	private boolean processErrorless = true;
	
	public ModelSourceGenerationCoordinator(WorkbenchContext context) 
	{
		super();
		initialize(context);
	}
	
	private void initialize(WorkbenchContext ctx)
	{
		this.context = ctx;
		setGenerator(new SourceCodeGenerator(ctx.getApplicationContext()));
	}
	
	private void setGenerator(SourceCodeGenerator generator)
	{
		this.generator = generator;
		this.generator.addContinuableExceptionListener(this);
		this.generator.addFileAlreadyExistsListener(this);
	}
	
	public void exportModelJavaSource(MWProject project, Collection descriptors) 
	{
		try
		{
			validateModelSourceRootDirectory(project);
			this.generator.generateSourceCode(project, descriptors);
		}
		catch (CancelException ce)
		{
			return;
		}
		
		showUserFeedback(project);
	}
	
	private void validateModelSourceRootDirectory(MWProject project)
	{
		if (project.getModelSourceDirectoryName().equals(""))
			promptForModelSourceRootDirectory(project);
		
		if (project.absoluteModelSourceDirectory().isFile()) // i.e. not a directory
			promptForNonFileModelSourceRootDirectory(project);
					
		if (! project.absoluteModelSourceDirectory().exists())
			promptToCreateModelSourceRootDirectory(project);
		
		return;
	}
	

	/**
	 * used when there is no directory already chosen
	 */
	private void promptForModelSourceRootDirectory(MWProject project)
	{
		String description = resourceRepository().getString("MODEL_SOURCE_ROOT_DIRECTORY_DIALOG_NO_DIRECTORY_CHOSEN.message");
		promptForDirectory(project, description);
	}	
	
	/**
	 * used when the chosen directory is not actually a directory
	 */
	private void promptForNonFileModelSourceRootDirectory(MWProject project)
	{
		String description = resourceRepository().getString("MODEL_SOURCE_ROOT_DIRECTORY_DIALOG_CHOSEN_DIRECTORY_IS_A_FILE.message",
															new Object[] {project.absoluteModelSourceDirectory()});
		promptForDirectory(project, description);
	}
	
	/**
	 * used when the chosen directory is invalid (e.g. "Z:\foo" if there is no Z drive)
	 */
	private void promptForValidModelSourceRootDirectory(MWProject project)
	{
		String description = resourceRepository().getString("MODEL_SOURCE_ROOT_DIRECTORY_DIALOG_CHOSEN_DIRECTORY_IS_INVALID.message",
															new Object[] {project.absoluteModelSourceDirectory()});
		promptForDirectory(project, description);
	}
	
	/**
	 * used when the chosen directory could not be created (various reasons)
	 */
	private void promptForCreatableModelSourceRootDirectory(MWProject project)
	{
		String description = resourceRepository().getString("MODEL_SOURCE_ROOT_DIRECTORY_DIALOG_CHOSEN_DIRECTORY_COULD_NOT_BE_CREATED.message",
															new Object[] {project.absoluteModelSourceDirectory()});
		promptForDirectory(project, description);
	}
	
	private Preferences preferences() {
		return this.context.getApplicationContext().getPreferences();
	}

	//TODO description is not being used, look for the use in 9.0.4
	private void promptForDirectory(MWProject project, String description)
	{
		File startDir = MappingsPlugin.buildExportDirectory(project, project.getModelSourceDirectoryName(), this.preferences());
		JFileChooser fileChooser = new FileChooser(startDir, project.getSaveDirectory());
		fileChooser.setDialogTitle(resourceRepository().getString("MODEL_SOURCE_ROOT_DIRECTORY_DIALOG.title", project.getName()));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setApproveButtonText(resourceRepository().getString("MODEL_SOURCE_ROOT_DIRECTORY_DIALOG_SELECT_BUTTON"));
		fileChooser.setApproveButtonMnemonic(resourceRepository().getMnemonic("MODEL_SOURCE_ROOT_DIRECTORY_DIALOG_SELECT_BUTTON"));

		int option = fileChooser.showSaveDialog(this.context.getCurrentWindow());
		
		if (option == JFileChooser.CANCEL_OPTION) {
			throw new CancelException();
		}
		File directory = fileChooser.getSelectedFile();
		project.setModelSourceDirectoryName(directory.getPath());
		if ((directory != null) && directory.isAbsolute() && ! directory.equals(project.getSaveDirectory())) {
			this.preferences().put(MappingsPlugin.EXPORT_LOCATION_PREFERENCE, directory.getPath());
		}
	}

	
	private void promptToCreateModelSourceRootDirectory(MWProject project)
	{
		File canonicalDirectory;
		
		try
		{
			canonicalDirectory = project.absoluteModelSourceDirectory().getCanonicalFile();
		}
		catch (IOException ioe)
		{
			promptForValidModelSourceRootDirectory(project);
			return;
		}
		
		int input = JOptionPane.showConfirmDialog(this.context.getCurrentWindow(),
										resourceRepository().getString("CREATE_MODEL_SOURCE_ROOT_DIRECTORY_DIALOG.message", 
																							new Object[] {canonicalDirectory}),
										resourceRepository().getString("CREATE_MODEL_SOURCE_ROOT_DIRECTORY_DIALOG.title"),
										JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE);
		
		if (input != JOptionPane.YES_OPTION) {
			throw new CancelException();
		}
		if (! project.absoluteModelSourceDirectory().mkdirs()) {
			promptForCreatableModelSourceRootDirectory(project);
		}			
	}
	
	private boolean promptToConformEntityBeans() 
	{
		int input = JOptionPane.showConfirmDialog(this.context.getCurrentWindow(),
										resourceRepository().getString("sourceCodeGeneration_specCompliance.message"),
										resourceRepository().getString("sourceCodeGeneration_specCompliance.title"),
										JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE);

		return input == JOptionPane.YES_OPTION;
	}
	
	private boolean promptToGenerateAssociatedClasses() 
	{
		int input = JOptionPane.showConfirmDialog(this.context.getCurrentWindow(),
										resourceRepository().getString("sourceCodeGeneration_generateAssociatedClasses.message"),
										resourceRepository().getString("sourceCodeGeneration_generateAssociatedClasses.title"),
										JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE);

		return input == JOptionPane.YES_OPTION;
	}


	public boolean continueOnException(SourceCodeGenerator.ContinuableExceptionEvent cee) 
	{
		this.processErrorless = false;
		ExceptionDialog dialog = 
			new ExceptionDialog(
					resourceRepository().getString("EXPORT_MODEL_SOURCE_CONTINUABLE_ERROR_DIALOG.message"),
					cee.getException(),
					this.context,
					resourceRepository().getString("EXPORT_MODEL_SOURCE_CONTINUABLE_ERROR_DIALOG.title"))
			 {
			
			protected String buildOKText() {
				return resourceRepository().getString("EXPORT_MODEL_SOURCE_YES_BUTTON");
			}
			
			protected String buildCancelText() {
				return resourceRepository().getString("EXPORT_MODEL_SOURCE_NO_BUTTON");
			}
			
		};

		dialog.show();
		return dialog.wasConfirmed();
	}
	
	public boolean fileAlreadyExists(MWProject project,
	                                 SourceCodeGenerator.FileAlreadyExistsEvent faee) {

		int input = JOptionPane.showConfirmDialog(
			this.context.getCurrentWindow(),
			resourceRepository().getString("sourceCodeGeneration_fileExists.message"),
			resourceRepository().getString("sourceCodeGeneration_fileExists.title"),
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE);

		this.generator.setOverwriteFiles(input == JOptionPane.YES_OPTION);

		// Request the user to select another location
		if (input == JOptionPane.NO_OPTION) {
			promptForModelSourceRootDirectory(project);
			validateModelSourceRootDirectory(project);
			this.generator.checkForExistingFiles();
		}

		return (input != JOptionPane.CANCEL_OPTION);
	}
	
	private void showUserFeedback(MWProject project)
	{
		if (this.processCanceled)
			return; // user knows (s)he cancelled
		
		if (this.generator.isAnyFileWritten()) {
			if (this.processErrorless)
				showSuccessDialog(project);
			else
				showCompletionDialog(project);
		}
	}
	
	private void showSuccessDialog(MWProject project) 
	{
		JOptionPane.showMessageDialog(this.context.getCurrentWindow(),
						resourceRepository().getString("EXPORT_MODEL_SOURCE_SUCCESS_DIALOG.message"),
						resourceRepository().getString("EXPORT_MODEL_SOURCE_SUCCESS_DIALOG.title", project.getName()),
						JOptionPane.INFORMATION_MESSAGE );
	}
	
	private void showCompletionDialog(MWProject project) 
	{
		JOptionPane.showMessageDialog(this.context.getCurrentWindow(),
						resourceRepository().getString("EXPORT_MODEL_SOURCE_COMPLETION_DIALOG.message"),
						resourceRepository().getString("EXPORT_MODEL_SOURCE_COMPLETION_DIALOG.title", project.getName()),
						JOptionPane.INFORMATION_MESSAGE );
	}

	private ResourceRepository resourceRepository() {
		return this.context.getApplicationContext().getResourceRepository();
	}
}
