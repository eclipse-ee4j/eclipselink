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
package org.eclipse.persistence.tools.workbench.mappingsplugin;

import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.FileChooser;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.uitools.CancelException;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This class is responsible for 
 * 	- gathering user information
 *	- displaying information back to the user
 * during the process of generating deployment XML for a project.
 */
public final class ProjectDeploymentXmlGenerationCoordinator {	
	
	private WorkbenchContext context;
	
	public ProjectDeploymentXmlGenerationCoordinator(WorkbenchContext context) {
		super();
		this.context = context;
	}
	
	public boolean exportProjectDeploymentXml(MWProject project) {
		try {
			configureProjectDeploymentXMLFile(project);
			checkProjectProblems(project);
		} catch (CancelException ex) {
			return false;
		}
		if (project.deploymentXMLFile().isDirectory()) {
			this.showInvalidFileDialog(project);
			return false;
		}
		try { 
			project.exportDeploymentXML();
		} catch (NoClassDefFoundError error) {
			if (error.getMessage().startsWith("oracle.xdb")) {
				this.showMissingXdbJarDialog();
				return false;
			} else {
				throw error;
			}
		}
		showSuccessDialog(project);
		return true;
	}

	private void configureProjectDeploymentXMLFile(MWProject project) {
		// if the project already has a file specified, just use that...
		if ( ! StringTools.stringIsEmpty(project.getDeploymentXMLFileName())) {
			return;
		}

		// ...otherwise prompt the user for a file
		File directory = MappingsPlugin.buildExportDirectory(project, "", this.preferences());
		File initialFile = new File(directory, project.getName() + ".xml");

		// prompt the user
		LocalFileChooser chooser = new LocalFileChooser(this.context, directory, project.getSaveDirectory());
		chooser.setDialogTitle(this.resourceRepository().getString("PROJECT_XML_SAVE_AS_DIALOG_TITLE"));
		chooser.setAcceptAllFileFilterUsed(true);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		chooser.setSelectedFile(initialFile);
		chooser.setFileFilter(this.buildXMLFileFilter());

		int result = chooser.showSaveDialog(this.context.getCurrentWindow());

		if (result == JFileChooser.CANCEL_OPTION) {
			throw new CancelException();
		}

		// update the project with the new file
		File selectedFile = chooser.getSelectedFile();
		project.setDeploymentXMLFileName(selectedFile.getPath());

		// update the preferences if appropriate
		File selectedDir = selectedFile.getParentFile();
		if ((selectedDir != null) && selectedDir.isAbsolute() && ! selectedDir.equals(project.getSaveDirectory())) {
			this.preferences().put(MappingsPlugin.EXPORT_LOCATION_PREFERENCE, selectedDir.getPath());
		}
	}

	private Preferences preferences() {
		return this.context.getApplicationContext().getPreferences();
	}

	private FileFilter buildXMLFileFilter() {
		return new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory() || ".xml".equals(FileTools.extension(file).toLowerCase());
			}
			public String getDescription() {
				return resourceRepository().getString("PROJECT_XML_SAVE_AS_DIALOG_XML_DESCRIPTION");
			}
		};
	}

	private void checkProjectProblems(MWProject project) {		
		if ( ! project.hasBranchProblems()) {
			return;
		}
		
		int response = JOptionPane.showConfirmDialog(this.context.getCurrentWindow(),
										new LabelArea(resourceRepository().getString("PROJECT_XML_PROJECT_PROBLEMS_DIALOG.message", StringTools.CR)),
										resourceRepository().getString("PROJECT_XML_PROJECT_PROBLEMS_DIALOG.title", project.getName()),
										JOptionPane.YES_NO_OPTION,
										JOptionPane.WARNING_MESSAGE );
		
		if (response != JOptionPane.YES_OPTION) {
			throw new CancelException();
		}
	}
	
	private void showInvalidFileDialog(MWProject project) {
		JOptionPane.showMessageDialog(
			this.context.getCurrentWindow(),
			resourceRepository().getString("GENERATE_PROJECT_XML_INVALID_FILE_DIALOG.message", project.deploymentXMLFile()),
			resourceRepository().getString("GENERATE_PROJECT_XML_INVALID_FILE_DIALOG.title", project.getName()),
			JOptionPane.ERROR_MESSAGE
		);
	}

	private void showMissingXdbJarDialog() {
		JOptionPane.showMessageDialog(
			this.context.getCurrentWindow(),
			resourceRepository().getString("GENERATE_PROJECT_XML_MISSING_XDB_JAR.message", StringTools.CR),
			resourceRepository().getString("GENERATE_PROJECT_XML_MISSING_XDB_JAR.title"),
			JOptionPane.ERROR_MESSAGE
		);
	}

	private void showSuccessDialog(MWProject project) {
		JOptionPane.showMessageDialog(
			this.context.getCurrentWindow(),
			resourceRepository().getString("GENERATE_PROJECT_XML_SUCCESS_DIALOG.message"),
			resourceRepository().getString("GENERATE_PROJECT_XML_SUCCESS_DIALOG.title", project.getName()),
			JOptionPane.INFORMATION_MESSAGE
		);
	}

	ResourceRepository resourceRepository() {
		return this.context.getApplicationContext().getResourceRepository();
	}

	/**
	 * This extension over the <code>JFileChooser</code> makes sure the selected
	 * file is valid, ie is not Read-Only. If it's a different file, then prompt
	 * the user to make sure it is ok to overwrite it.
	 */
	private class LocalFileChooser extends FileChooser
	{
		private final WorkbenchContext workbenchContext;
		private static final long serialVersionUID = 1L;

		private LocalFileChooser(WorkbenchContext workbenchContext, File currentDirectory, File rootFile)
		{
			super(currentDirectory, rootFile);
			this.workbenchContext = workbenchContext;
		}

		/**
		 * Determines whether the selected file can be used as the new location to
		 * persist the document.
		 */
		public void approveSelection()
		{
			int result = canReplaceExistingFile();

			if (result == JOptionPane.YES_OPTION)
				super.approveSelection();
			else if (result == JOptionPane.CANCEL_OPTION)
				cancelSelection();
		}

		/**
		 * Verifies if the file (which is currently selected) can be replaced with
		 * the document to be saved.
		 *
		 * @return <code>JOptionPane.YES_OPTION</code> if the file is not
		 * Read-Only or can be replaced, <code>JOptionPane.NO_OPTION</code> if the
		 * document can't be saved because the selected file is Read-Only or the
		 * user said no to replace it, or <code>JOptionPane.NO_OPTION</code> if
		 * the user does not want to replace the file and canceled the confirm
		 * dialog
		 */
		private int canReplaceExistingFile()
		{
			File file = getSelectedFile();

			// The file exist but is marked as Read-Only, show we can't save it
			if (file.exists() && !file.canWrite())
			{
				String message = resourceRepository().getString("PROJECT_XML_SAVE_AS_DIALOG_CANT_SAVE", file);
                LabelArea label = new LabelArea(message);

				JOptionPane.showMessageDialog
				(
					this.workbenchContext.getCurrentWindow(),
					label,
					resourceRepository().getString("PROJECT_XML_SAVE_AS_DIALOG_CANT_SAVE_TITLE"),
					JOptionPane.WARNING_MESSAGE
				);

				return JOptionPane.NO_OPTION;
			}

			// The file exist
			if (file.exists())
			{
				String message = resourceRepository().getString("PROJECT_XML_SAVE_AS_DIALOG_REPLACE", getSelectedFile().getPath());
				LabelArea label = new LabelArea(message);

				return JOptionPane.showConfirmDialog
				(
					this.workbenchContext.getCurrentWindow(),
					label,
					resourceRepository().getString("PROJECT_XML_SAVE_AS_DIALOG_REPLACE_TITLE"),
					JOptionPane.YES_NO_CANCEL_OPTION
				);
			}

			return JOptionPane.YES_OPTION;
		}
	}

}
