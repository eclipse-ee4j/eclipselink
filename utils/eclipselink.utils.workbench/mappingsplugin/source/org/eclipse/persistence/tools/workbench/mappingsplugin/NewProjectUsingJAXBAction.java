/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Frame;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.eclipse.persistence.oxm.jaxb.compiler.TopLinkJAXBGenerationException;
import org.eclipse.persistence.oxm.jaxb.compiler.tljaxb;
import org.eclipse.persistence.tools.workbench.framework.OpenException;
import org.eclipse.persistence.tools.workbench.framework.UnsupportedFileException;
import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.WaitDialog;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.uitools.CancelException;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.uitools.RunnableExceptionDialogLauncher;


final class NewProjectUsingJAXBAction extends AbstractFrameworkAction {
	private MappingsPlugin plugin;

	NewProjectUsingJAXBAction(MappingsPlugin plugin, WorkbenchContext context) {
		super(context);
		this.plugin = plugin;
	}

	protected void initialize() {
		initializeTextAndMnemonic("USING_JAXB_ACTION");
		initializeToolTipText("USING_JAXB_ACTION.toolTipText");
		initializeAccelerator("USING_JAXB_ACTION.accelerator");
		initializeIcon("PROJECT.NEW");
	}

	protected void openProjectCallback(File projectFile) throws UnsupportedFileException, OpenException,
																InterruptedException, InvocationTargetException {
		final ApplicationNode node = plugin.open(projectFile, getWorkbenchContext());
		MWProject project = (MWProject)node.getValue();

		//opening of project in main UI needs to be executed in the main UI thread to avoid problems
		EventQueue.invokeAndWait( new Runnable() {
			public void run() {			
				nodeManager().addProjectNode(node);
				nodeManager().save(node, getWorkbenchContext());
				navigatorSelectionModel().setSelectedNode(node);
			}
		});		
	}
	
	protected void execute() {
		this.navigatorSelectionModel().pushExpansionState();
		this.startMigrationThread(this.getWorkbenchContext());
		this.navigatorSelectionModel().popAndRestoreExpansionState();
	}
	
	/**
	 * Start a thread that will migrate a MW project from CMP
	 */
	private void startMigrationThread(WorkbenchContext context) {
		Thread thread = new Thread(new GenerationRunnable(context, new JaxbProjectCreationDialog(getWorkbenchContext()), this), "JAXB Project Generation");
		thread.setPriority(Thread.NORM_PRIORITY);
		thread.start();
	}

	/**
	 * Build a Runnable that will be the master thread for all generation actions
	 */
	private class GenerationRunnable implements Runnable {
		
		private JDialog waitDialog;
		
		private boolean generationSuccess;

		private JaxbProjectCreationDialog dialog;
	
		private WorkbenchContext context;

		private tljaxb jaxbCompiler;
		
		private NewProjectUsingJAXBAction generationAction;
		
		GenerationRunnable(WorkbenchContext context, JaxbProjectCreationDialog projectGenerationDialog, NewProjectUsingJAXBAction genAction) {
			super();
			this.context = context;
			this.generationAction = genAction;
			this.dialog = projectGenerationDialog;
			this.waitDialog = new WaitDialog(
					(Frame)context.getCurrentWindow(),
					resourceRepository().getIcon("file.mwp.large"),
					resourceRepository().getString("PROJECT_GENERATING_DIALOG.TITLE"),
					resourceRepository().getString("PROJECT_GENERATING_MESSAGE")
			);
		}

		public void run() {
			try {
				run2();	
			} catch (InvocationTargetException exception) {
				// we're probably in trouble if we get here...
				this.waitDialog.dispose();
				throw new RuntimeException(exception);
			} catch (InterruptedException exception) {
				// we're probably in trouble if we get here...
				this.waitDialog.dispose();
				throw new RuntimeException(exception);					
			}
		}

		private void run2() throws InvocationTargetException, InterruptedException {
			generationSuccess = false;
			while (!generationSuccess) {
				dialog.show();
				if (dialog.wasConfirmed()) {
					this.context.getCurrentWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
					//init migration manager with info from dialog
					initializeTlJaxbCompilerFromDialog();
											
					//do generation
					generate();
			
					this.context.getCurrentWindow().setCursor(Cursor.getDefaultCursor());
				} else if (dialog.wasCanceled()) {
					return;
				}
			}
		}

		protected void showJaxbGenerationException(String message) {
            LabelArea label = new LabelArea(message); 
			JOptionPane.showMessageDialog(this.dialog, label, resourceRepository().getString("GENERATION_EXCEPTION"), JOptionPane.ERROR_MESSAGE);
		}

		private void initializeTlJaxbCompilerFromDialog() {
			this.jaxbCompiler = new tljaxb();
			this.jaxbCompiler.setSchema(this.dialog.getInputSchemaFile());
			
			if (! "".equals(this.dialog.getInputCustomizationFile())) {
				this.jaxbCompiler.setCustomizationFile(this.dialog.getInputCustomizationFile());
			}
			
			this.jaxbCompiler.setSourceDir(this.dialog.getAbsoluteOutputSourceDirectory());
			this.jaxbCompiler.setWorkbenchDir(this.dialog.getAbsoluteOutputWorkbenchProjectDirectory());
			if (!this.dialog.getOutputInterfacePackageName().equals("")) {
				this.jaxbCompiler.setTargetPkg(this.dialog.getOutputInterfacePackageName());
			}
			if (!this.dialog.getOutputImplClassPackageName().equals("")) {
				this.jaxbCompiler.setImplClassPkg(this.dialog.getOutputImplClassPackageName());
			}
	
		}

		private synchronized void generate() throws InvocationTargetException, InterruptedException {
			launchLater(this.waitDialog);
			try {
				this.jaxbCompiler.generate();
				//open new project
				String schemaFile = this.dialog.getInputSchemaFile();	
				File projectFile = new File(this.dialog.getAbsoluteOutputWorkbenchProjectDirectory() + File.separator + schemaFile.substring(schemaFile.lastIndexOf(File.separator) + 1, schemaFile.lastIndexOf(".")) + MWProject.FILE_NAME_EXTENSION);
				try {
					this.generationAction.openProjectCallback(projectFile);
					generationSuccess = true;
				} catch (OpenException e) {
					terminateMigration(resourceRepository().getString("OPEN_EXCEPTION"));
				} catch (UnsupportedFileException e) {
					terminateMigration(resourceRepository().getString("UNSUPPORTED_FILE_EXCEPTION"));
				} catch (CancelException exception) {
					//shouldn't be happening in this case, but handle it gracefully anyway.
					terminateMigration(resourceRepository().getString("CANCEL_EXCEPTION"));
				}

			} catch (TopLinkJAXBGenerationException exception) {
				terminateMigration(resourceRepository().getString("JAXB_GENERATION_EXCEPTION.MESSAGE") + exception.getMessage());
				return;
			} catch (RuntimeException rtException) {
				terminateMigration(resourceRepository().getString("GENERATION_FAILURE"));
				throw rtException;
			}
		
			this.waitDialog.dispose();
		}

		private void terminateMigration(String reason) throws InterruptedException, InvocationTargetException{
			this.waitDialog.dispose();
			EventQueue.invokeAndWait(buildMigrationExceptionDialogLauncher(reason));
		}

		private Runnable buildMigrationExceptionDialogLauncher(String message) {
			generationSuccess = false;
			return new RunnableExceptionDialogLauncher(currentWindow(), message, resourceRepository().getString("GENERATION_EXCEPTION"));
		}
		
	}

}
