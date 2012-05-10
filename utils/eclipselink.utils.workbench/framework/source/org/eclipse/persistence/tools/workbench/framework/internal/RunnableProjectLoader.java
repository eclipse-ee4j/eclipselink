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
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.OpenException;
import org.eclipse.persistence.tools.workbench.framework.UnsupportedFileException;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.WaitDialog;
import org.eclipse.persistence.tools.workbench.uitools.CancelException;


/**
 * allow a project to be read in a separate thread
 */
final class RunnableProjectLoader implements Runnable {
	private volatile FrameworkNodeManager nodeManager;
	private File projectFile;
	private WorkbenchContext context;

	Dialog waitDialog;


	// ********** constructor/initialization **********

	RunnableProjectLoader(FrameworkNodeManager nodeManager, File projectFile, WorkbenchContext context) {
		super();
		this.nodeManager = nodeManager;
		this.projectFile = projectFile;
		this.context = context;

		this.waitDialog = this.buildWaitDialog();
	}

	private Dialog buildWaitDialog() {
		Window owner = this.context.getCurrentWindow();
		ResourceRepository rr = this.context.getApplicationContext().getResourceRepository();
		Icon icon = rr.getIcon("file.mwp.large");
		String title = rr.getString("PROJECT_LOADER_DIALOG.TITLE");
		String message = rr.getString("OPENING_PROJECT_MESSAGE", this.projectFile.getName());
		return (owner instanceof Frame) ?
			new WaitDialog((Frame) owner, icon, title, message)
		:
			new WaitDialog((Dialog) owner, icon, title, message);
	}


	// ********** Runnable implementation **********

	/**
	 * launch the dialog, open the project file, add the node to the node
	 * manager, and shut down the dialog
	 */
	public void run() {
		try {
			this.run2();
		} catch (Throwable t) {
			// we're probably in trouble if we get here...
			this.waitDialog.dispose();
			EventQueue.invokeLater(this.buildDialogLauncher(this.buildExceptionDialog("OPEN_EXCEPTION", new Object[] {this.projectFile.getName()}, t)));
		}
	}

	private void run2() throws InterruptedException, InvocationTargetException {
		// put the dialog launcher on the AWT event queue
		EventQueue.invokeLater(this.buildDialogLauncher(this.waitDialog));
		ApplicationNode node = null;
		try {
			node = this.nodeManager.openCallback(this.projectFile, this.context);
		} catch (OpenException ex) {
			// shut down the dialog and display an error dialog
			this.waitDialog.dispose();
			EventQueue.invokeLater(this.buildDialogLauncher(this.buildExceptionDialog("OPEN_EXCEPTION", new Object[] {this.projectFile.getName()}, ex)));
			return;
		} catch (UnsupportedFileException ex) {
			// shut down the dialog and display an error dialog
			this.waitDialog.dispose();
			EventQueue.invokeLater(this.buildDialogLauncher(this.buildExceptionDialog("UNSUPPORTED_FILE_TYPE", new Object[] {this.projectFile.getName()}, ex)));
			return;
		} catch (CancelException exception) {
			// just shutdown the dialog and return, no error has occurred
			this.waitDialog.dispose();
			return;
		}
		// the project read in OK - add it to the node manager and shut down the dialog
		EventQueue.invokeAndWait(new NodeInstaller(this.nodeManager, node, this.projectFile, this.context));
		this.waitDialog.dispose();
	}

	private Runnable buildDialogLauncher(final Dialog d) {
		return new Runnable() {
			public void run() {
				d.show();
			}
		};
	}

	Dialog buildExceptionDialog(String messageKey, Object[] messageArguments, Throwable exception) {
		Window owner = this.context.getCurrentWindow();
		String message = this.context.getApplicationContext().getResourceRepository().getString(messageKey, messageArguments);
		return (owner instanceof Frame) ?
			new FrameworkExceptionDialog(message, exception, this.context) :
			new FrameworkExceptionDialog(message, exception, this.context, (Dialog) owner);
	}


	// ********** member classes **********
	/**
	 * a Runnable that can be placed on the AWT Event Queue
	 * and will add a node to the node manager when it gets "dispatched"
	 */
	private static class NodeInstaller implements Runnable {
		private FrameworkNodeManager nodeManager;
		private ApplicationNode projectNode;
		private File projectFile;
		private WorkbenchContext context;

		NodeInstaller(FrameworkNodeManager nodeManager, ApplicationNode projectNode, File projectFile, WorkbenchContext context) {
			super();
			this.nodeManager = nodeManager;
			this.projectNode = projectNode;
			if (projectNode.saveFile() != null) {
				this.projectFile = projectNode.saveFile();
			} else {
				this.projectFile = projectFile;				
			}
			this.context = context;
		}

		public void run() {
			if ( ! EventQueue.isDispatchThread()) {
				throw new IllegalStateException("this method must be executed in the AWT event dispatcher thread");
			}
			this.nodeManager.addProjectNodeCallback(this.projectNode, this.projectFile, this.context);
		}

	}

}
