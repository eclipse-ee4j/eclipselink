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
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;


/**
 * loop through ALL the "project" nodes and tell the dirty nodes each to save;
 * enabled whenever there is a dirty "project" node
 */
final class SaveAllAction
	extends AbstractFrameworkAction
{
	/** we need access to the node manager's internal api */
	private FrameworkNodeManager nodeManager;

	/** listen for changes to the project nodes or their dirty flags */
	private ListValueModel projectNodesHolder;
	private ListChangeListener projectNodesListener;

	/** hold the listener so we can remove it */
	private WindowListener windowListener;


	SaveAllAction(WorkbenchContext context, FrameworkNodeManager nodeManager) {
		super(context);
		this.initialize(nodeManager);
	}

	protected void initialize() {
		super.initialize();
		this.initializeTextAndMnemonic("file.saveAll");
		this.initializeIcon("file.saveAll");
		this.initializeToolTipText("file.saveAll.toolTipText");
		this.initializeAccelerator("file.saveAll.ACCELERATOR");
	}

	private void initialize(FrameworkNodeManager frameworkNodeManager) {
		this.nodeManager = frameworkNodeManager;
		this.projectNodesHolder = this.buildDirtyProjectNodesAdapter();
		this.projectNodesListener = this.buildProjectNodesListener();
		this.projectNodesHolder.addListChangeListener(ValueModel.VALUE, this.projectNodesListener);
		this.windowListener = this.buildWorkbenchWindowListener();
		this.currentWindow().addWindowListener(this.windowListener);
		this.updateEnabledState();
	}

	private ListValueModel buildDirtyProjectNodesAdapter() {
		return new ItemPropertyListValueModelAdapter(this.buildProjectNodesAdapter(), ApplicationNode.DIRTY_PROPERTY);
	}

	private CollectionValueModel buildProjectNodesAdapter() {
		return new CollectionAspectAdapter(FrameworkNodeManager.PROJECT_NODES_COLLECTION, this.nodeManager) {
			protected Iterator getValueFromSubject() {
				return ((FrameworkNodeManager) this.subject).projectNodes();
			}
			protected int sizeFromSubject() {
				return ((FrameworkNodeManager) this.subject).projectNodesSize();
			}
		};
	}

	private ListChangeListener buildProjectNodesListener() {
		return new ListChangeListener() {
			public void itemsAdded(ListChangeEvent e) {
				SaveAllAction.this.updateEnabledState();
			}
			public void itemsRemoved(ListChangeEvent e) {
				SaveAllAction.this.updateEnabledState();
			}
			public void itemsReplaced(ListChangeEvent e) {
				SaveAllAction.this.updateEnabledState();
			}
			public void listChanged(ListChangeEvent e) {
				SaveAllAction.this.updateEnabledState();
			}
		};
	}

	private WindowListener buildWorkbenchWindowListener() {
		return new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				SaveAllAction.this.workbenchWindowClosed();
			}
		};
	}

	void updateEnabledState() {
		if (this.nodeManager.projectNodesSize() == 0) {
			this.setEnabled(false);
			return;
		}

		for (Iterator stream = this.nodeManager.projectNodes(); stream.hasNext(); ) {
			if (((ApplicationNode) stream.next()).isDirty()) {
				this.setEnabled(true);
				return;	// no need to check further
			}
		}
		// there were no dirty project nodes
		this.setEnabled(false);
	}

	protected void execute() {
		this.nodeManager.saveAll(getWorkbenchContext());
	}

	void workbenchWindowClosed() {
		this.projectNodesHolder.removeListChangeListener(ValueModel.VALUE, this.projectNodesListener);
		// stop listening to the window, or, for some odd reason,
		// we will receive the WINDOW_CLOSED event twice...
		this.currentWindow().removeWindowListener(this.windowListener);
	}

}
