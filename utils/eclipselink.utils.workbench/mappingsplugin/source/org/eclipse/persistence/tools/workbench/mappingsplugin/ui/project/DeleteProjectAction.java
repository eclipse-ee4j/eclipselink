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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


final class DeleteProjectAction extends AbstractFrameworkAction {
	
	DeleteProjectAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		initializeText("DELETE_PROJECT_ACTION");
		initializeMnemonic("DELETE_PROJECT_ACTION");
		initializeToolTipText("DELETE_PROJECT_ACTION.toolTipText");
		initializeIcon("project.remove");
	}
	
	protected void execute(ApplicationNode selectedNode) {
		MWProject selectedProject = (MWProject) selectedNode.getValue();
		File saveDirectory = selectedProject.getSaveDirectory();
		
		File projectFile = new File(saveDirectory, selectedProject.getName() + MWProject.FILE_NAME_EXTENSION);
		if (projectFile.exists()) {
			if (confirmDeletion()) {			
				deleteProject(selectedNode, projectFile);
			}
		}
		else {
			JOptionPane.showMessageDialog(getWorkbenchContext().getCurrentWindow(),
											resourceRepository().getString("PROJECT_FILE_DOES_NOT_EXIST.message", StringTools.CR));
			closeProject(selectedNode);
		}
				
	}
	
	private boolean confirmDeletion() {
		int option = JOptionPane.showConfirmDialog(getWorkbenchContext().getCurrentWindow(),
										resourceRepository().getString("DELETE_PROJECT_WARNING.message", StringTools.CR),
										resourceRepository().getString("DELETE_PROJECT_WARNING.title"),
										JOptionPane.YES_NO_OPTION,
										JOptionPane.WARNING_MESSAGE);
										
		return (option == JOptionPane.YES_OPTION);
	}

	/**
	* Deletes the project denoted by projectFileToDelete
	* 
	* @return boolean true if the project was deleted
	*/
	public void deleteProject(ApplicationNode projectNode, File projectFileToDelete) {
		boolean success = false;
		if (projectFileToDelete != null && projectFileToDelete.exists()) {
			String noExtensionProjectName;
			int extensionIndex = projectFileToDelete.getName().lastIndexOf('.');
			if (extensionIndex == -1)
				noExtensionProjectName = projectFileToDelete.getName();
			else 
				noExtensionProjectName = projectFileToDelete.getName().substring(0, extensionIndex);
			// Check and see if projectFileToDelete is the only project in the directory
			File directory = projectFileToDelete.getParentFile();
			File[] mwpFiles = directory.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return (name.toLowerCase().endsWith(MWProject.FILE_NAME_EXTENSION));
				}
			});
			// if it is, just delete it and all the subdirs associated with the project
			if (mwpFiles.length == 1) {
				success = deleteProjectByFile(projectFileToDelete);
			}
			// else nothing should be deleted.....
			else {
				JOptionPane.showMessageDialog(getWorkbenchContext().getCurrentWindow(), 
												resourceRepository().getString("UNABLE_TO_DELETE_PROJECT_DIALOG.message", new Object[] { noExtensionProjectName }),
												resourceRepository().getString("UNABLE_TO_DELETE_PROJECT_DIALOG.title"),
												JOptionPane.OK_OPTION);
				return;
			}
		}
		if (success) {
			closeProject(projectNode);
		}
	}
	
	private void closeProject(ApplicationNode projectNode) {
		nodeManager().removeProjectNode(projectNode);
	}
	
	/**
	* Deletes the project file,and the class, descriptor, scehma, and table directories
	* Should only be called if the project is the only one stored in the directory
	*/
	private boolean deleteProjectByFile(File projectFileToDelete) {
		//setCursor(CursorConstants.WAIT_CURSOR);
		boolean success = true;
		File directory = projectFileToDelete.getParentFile();
		projectFileToDelete.delete();
		File[] bldrDirectories = directory.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.equals("classes") 
					|| name.equals("descriptors") 
					|| name.equals("tables") 
					|| name.equals("schemas")) 
					&& dir.isDirectory();
			}
		});
		int length = bldrDirectories.length;
		for (int i = 0; i < length && success; i++) {
			success = deleteDirectory(bldrDirectories[i]);
		}
		//setCursor(CursorConstants.DEFAULT_CURSOR);
		return success;
	}
	
	/**
	* Deletes the directory and all its subdirs and files
	*/
	private boolean deleteDirectory(File directory) {
		boolean successful = true;
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();
			int length = files.length;
			for (int i = 0; i < length && successful; i++) {
				if (!files[i].isDirectory())
					successful = files[i].delete();
				else
					successful = deleteDirectory(files[i]);
			}
			if (successful)
				directory.delete();
		}
		return successful;
	}

}

