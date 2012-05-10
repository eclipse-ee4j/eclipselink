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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;


/**
 * close all the nodes;
 * enabled whenever there are ANY nodes open
 */
final class CloseAllAction
	extends AbstractFrameworkAction
{
	/** we need access to the node manager's internal api */
	private FrameworkNodeManager nodeManager;

	/** listen to the collection of all the open project nodes */
	private CollectionChangeListener projectNodesListener;

	/** hold the listener so we can remove it */
	private WindowListener windowListener;

	CloseAllAction(WorkbenchContext context, FrameworkNodeManager nodeManager) {
		super(context);
		this.initialize(nodeManager);
	}

	protected void initialize() {
		super.initialize();
		this.initializeTextAndMnemonic("file.closeAll");
		this.initializeIcon("file.closeAll");
		this.initializeAccelerator("file.closeAll.ACCELERATOR");
		this.initializeToolTipText("file.closeAll.toolTipText");
	}

	private void initialize(FrameworkNodeManager frameworkNodeManager) {
		this.nodeManager = frameworkNodeManager;
		this.projectNodesListener = this.buildProjectNodesListener();
		frameworkNodeManager.addCollectionChangeListener(FrameworkNodeManager.PROJECT_NODES_COLLECTION, this.projectNodesListener);
		this.windowListener = this.buildWorkbenchWindowListener();
		this.currentWindow().addWindowListener(this.windowListener);
		this.updateEnabledState();
	}

	private CollectionChangeListener buildProjectNodesListener() {
		return new CollectionChangeListener() {
			public void itemsAdded(CollectionChangeEvent e) {
				CloseAllAction.this.projectNodesChanged();
			}
			public void itemsRemoved(CollectionChangeEvent e) {
				CloseAllAction.this.projectNodesChanged();
			}
			public void collectionChanged(CollectionChangeEvent e) {
				CloseAllAction.this.projectNodesChanged();
			}
		};
	}

	private WindowListener buildWorkbenchWindowListener() {
		return new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				CloseAllAction.this.workbenchWindowClosed();
			}
		};
	}

	void projectNodesChanged() {
		this.updateEnabledState();
	}

	private void updateEnabledState() {
		this.setEnabled(this.nodeManager.projectNodesSize() > 0);
	}

	protected void execute() {
		this.nodeManager.closeAll(this.getWorkbenchContext());
	}

	void workbenchWindowClosed() {
		this.nodeManager.removeCollectionChangeListener(FrameworkNodeManager.PROJECT_NODES_COLLECTION, this.projectNodesListener);
		// stop listening to the window, or, for some odd reason,
		// we will receive the WINDOW_CLOSED event twice...
		this.currentWindow().removeWindowListener(this.windowListener);
	}

}
