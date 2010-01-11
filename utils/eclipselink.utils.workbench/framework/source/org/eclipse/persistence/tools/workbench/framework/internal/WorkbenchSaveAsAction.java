/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;


/**
 * loop through the selected "project" nodes and tell them each to save-as;
 * enabled whenever at least a single node is selected;
 * this action is part of the FileMenu and MainToolBar, while SaveAction is
 * part of the SelectionMenu
 */
final class WorkbenchSaveAsAction extends AbstractFrameworkAction
{
	/** we need access to the node manager's internal api */
	private FrameworkNodeManager nodeManager;
	private TreeSelectionListener treeSelectionListener;
	private WindowListener windowListener;


	WorkbenchSaveAsAction(WorkbenchContext context, FrameworkNodeManager nodeManager) {
		super(context);
		this.nodeManager = nodeManager;
	}

	protected void initialize() {
		super.initialize();
		this.initializeTextAndMnemonic("file.saveAs");
		this.initializeIcon("file.saveAs");
		this.initializeToolTipText("file.saveAs.toolTipText");
		this.initializeAccelerator("file.saveAs.ACCELERATOR");
		
		this.treeSelectionListener = this.buildTreeSelectionListener();
		this.windowListener = this.buildWorkbenchWindowListener();
		
		this.currentWindow().addWindowListener(this.windowListener);
		this.navigatorSelectionModel().addTreeSelectionListener(this.treeSelectionListener);
		
		this.updateEnabledState();
	}
	

	private WindowListener buildWorkbenchWindowListener() {
		return new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				WorkbenchSaveAsAction.this.workbenchWindowClosed();
			}
		};
	}

	private TreeSelectionListener buildTreeSelectionListener() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				synchronized (WorkbenchSaveAsAction.this) {
					WorkbenchSaveAsAction.this.update();
				}
			}
		};
	}

	
	protected void execute() {
		ApplicationNode[] projectNodes = this.selectedProjectNodes();
		for (int i = projectNodes.length; i-- > 0; ) {
			this.nodeManager.saveAs(projectNodes[i], getWorkbenchContext());
		}
	}

	void update() {
		this.updateEnabledState();
	}
	
	private void updateEnabledState() {
		this.setEnabled(selectedProjectNodes().length > 0);
	}
	
	void workbenchWindowClosed() {
		this.navigatorSelectionModel().removeTreeSelectionListener(this.treeSelectionListener);
		// stop listening to the window, or, for some odd reason,
		// we will receive the WINDOW_CLOSED event twice...
		this.currentWindow().removeWindowListener(this.windowListener);
	}

}
