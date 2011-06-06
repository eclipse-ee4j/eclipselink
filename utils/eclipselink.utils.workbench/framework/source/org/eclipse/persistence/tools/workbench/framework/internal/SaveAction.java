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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;


/**
 * loop through the selected "project" nodes and tell them each to save;
 * enabled whenever ALL the selected nodes are descendants of
 * dirty "project" nodes;
 * this action is part of the SelectionMenu, while WorkbenchSaveAction is
 * part of the FileMenu and MainToolBar
 */
final class SaveAction
	extends AbstractFrameworkAction
{
	/** we need access to the node manager's internal api */
	private FrameworkNodeManager nodeManager;

	/** listen for changes to the currently selected "project" nodes' dirty flags */
	private ApplicationNode[] selectedProjectNodes;
	private PropertyChangeListener dirtyListener;
	

	SaveAction(WorkbenchContext context, FrameworkNodeManager nodeManager) {
		super(context);
		this.nodeManager = nodeManager;
	}

	protected void initialize() {
		super.initialize();

		this.initializeTextAndMnemonic("file.save");
		this.initializeIcon("file.save");
		this.initializeToolTipText("file.save.toolTipText");
		this.initializeAccelerator("file.save.ACCELERATOR");

		this.dirtyListener = this.buildDirtyListener();
	}

	private PropertyChangeListener buildDirtyListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				SaveAction.this.updateEnabledState();
			}
		};
	}

	public void setUp() {
		super.setUp();
		this.selectedProjectNodes = selectedProjectNodes();
		this.engageSelectedProjectNodes();
		this.updateEnabledState();
	}
	
	public void tearDown() {
		this.disengageSelectedProjectNodes();
		super.tearDown();
	}

	void updateEnabledState() {
		if (this.selectedNodes().length == 0) {
			this.setEnabled(false);
			return;
		}

		for (int i = this.selectedProjectNodes.length; i-- > 0; ) {
			if ( ! this.selectedProjectNodes[i].isDirty()) {
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

}
