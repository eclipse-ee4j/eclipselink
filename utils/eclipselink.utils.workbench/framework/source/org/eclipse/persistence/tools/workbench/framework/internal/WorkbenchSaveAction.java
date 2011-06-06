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
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;


/**
 * loop through the selected "project" nodes and tell them each to save;
 * enabled whenever ALL the selected nodes are descendants of
 * dirty "project" nodes;
 * this action is part of the FileMenu and MainToolBar, while SaveAction is
 * part of the SelectionMenu
 */
final class WorkbenchSaveAction
	extends AbstractFrameworkAction
{
	
	private TreeSelectionListener treeSelectionListener;

	/** we need access to the node manager's internal api */
	private FrameworkNodeManager nodeManager;

	/** listen for changes to the currently selected "project" nodes' dirty flags */
	private ApplicationNode[] selectedProjectNodes;
	private PropertyChangeListener dirtyListener;
	
	private WindowListener windowListener;


	WorkbenchSaveAction(WorkbenchContext context, FrameworkNodeManager nodeManager) {
		super(context);
		this.nodeManager = nodeManager;
	}

	protected void initialize() {
		super.initialize();
		this.treeSelectionListener = buildTreeSelectionListener();

		this.initializeTextAndMnemonic("file.save");
		this.initializeIcon("file.save");
		this.initializeToolTipText("file.save.toolTipText");
		this.initializeAccelerator("file.save.ACCELERATOR");

		this.selectedProjectNodes = new ApplicationNode[0];
		this.dirtyListener = this.buildDirtyListener();
		this.windowListener = this.buildWorkbenchWindowListener();
		this.currentWindow().addWindowListener(this.windowListener);
		this.navigatorSelectionModel().addTreeSelectionListener(this.treeSelectionListener);
		
		this.updateEnabledState();
	}

	private TreeSelectionListener buildTreeSelectionListener() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				synchronized (WorkbenchSaveAction.this) {
					WorkbenchSaveAction.this.update();
				}
			}
		};
	}

	private PropertyChangeListener buildDirtyListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				WorkbenchSaveAction.this.updateEnabledState();
			}
		};
	}

	private WindowListener buildWorkbenchWindowListener() {
		return new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				WorkbenchSaveAction.this.workbenchWindowClosed();
			}
		};
	}

	/**
	 * the navigator selection changed
	 */
	void update() {
		this.disengageSelectedProjectNodes();
		this.selectedProjectNodes = this.selectedProjectNodes();
		this.engageSelectedProjectNodes();
		this.updateEnabledState();
	}

	void updateEnabledState() {
		if (this.selectedNodes().length == 0) {
			this.setEnabled(false);
			return;
		}

		ApplicationNode[] projectNodes = this.selectedProjectNodes();
		for (int i = projectNodes.length; i-- > 0; ) {
			if ( ! projectNodes[i].isDirty()) {
				this.setEnabled(false);
				return;	// no need to check further
			}
		}
		this.setEnabled(true);
	}

	private void engageSelectedProjectNodes() {
		for (int i = this.selectedProjectNodes.length; i-- > 0; ) {
			this.selectedProjectNodes[i].addPropertyChangeListener(ApplicationNode.DIRTY_PROPERTY, this.dirtyListener);
		}
	}
	
	private void disengageSelectedProjectNodes() {
		for (int i = this.selectedProjectNodes.length; i-- > 0; ) {
			this.selectedProjectNodes[i].removePropertyChangeListener(ApplicationNode.DIRTY_PROPERTY, this.dirtyListener);
		}
		this.selectedProjectNodes = null;
	}

	protected void execute() {
		for (int i = this.selectedProjectNodes.length; i-- > 0; ) {
			this.nodeManager.save(this.selectedProjectNodes[i], getWorkbenchContext());
		}
	}

	void workbenchWindowClosed() {
		this.navigatorSelectionModel().removeTreeSelectionListener(this.treeSelectionListener);
		this.disengageSelectedProjectNodes();
		// stop listening to the window, or, for some odd reason,
		// we will receive the WINDOW_CLOSED event twice...
		this.currentWindow().removeWindowListener(this.windowListener);
	}

}
