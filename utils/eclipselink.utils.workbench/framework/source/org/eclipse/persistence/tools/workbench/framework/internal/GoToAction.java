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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.NodeChooserDialog;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;


/**
 * allow the user to quickly find a node in the navigator
 */
final class GoToAction
	extends AbstractFrameworkAction
{

	public GoToAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		super.initialize();
		this.setIcon(EMPTY_ICON);
		this.initializeTextAndMnemonic("GO_TO");
		this.initializeAccelerator("GO_TO.ACCELERATOR");
	}

	protected void execute() {
		NodeChooserDialog dialog = NodeChooserDialog.createDialog(this.xxx(), this.getWorkbenchContext());
		dialog.show();
		if (dialog.wasConfirmed()) {
			this.navigatorSelectionModel().setSelectedNode(dialog.selection());
		}
	}

	private ApplicationNode[] xxx() {
		TreeNodeValueModel root = this.nodeManager().getRootNode();
		Collection nodes = new ArrayList(1000);
		this.addChildrenTo(root, nodes);
		return (ApplicationNode[]) nodes.toArray(new ApplicationNode[nodes.size()]);
	}

	private void addChildrenTo(TreeNodeValueModel node, Collection nodes) {
		for (Iterator stream = (Iterator) node.getChildrenModel().getValue(); stream.hasNext(); ) {
			TreeNodeValueModel child = (TreeNodeValueModel) stream.next();
			nodes.add(child);
			this.addChildrenTo(child, nodes);		// recurse
		}
	}

}
