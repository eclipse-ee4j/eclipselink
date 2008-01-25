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

import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;


/**
 * Object to be removed must implement Removable
 * 
 */
public final class RemoveAction extends AbstractFrameworkAction {

	public RemoveAction(WorkbenchContext context) {
		this(context, "remove");
	}
	
	public RemoveAction(WorkbenchContext context, String iconKey) {
		super(context);
		initializeIcon(iconKey);
	}
	
	protected void initialize() {
		super.initialize();
		
		initializeTextAndMnemonic("REMOVE_ACTION");
		initializeToolTipText("REMOVE_ACTION.toolTipText");
	}
	
	protected void execute() {
		ApplicationNode[] selectedNodes = selectedNodes();
		if (selectedNodes.length == 1) {
			if (!confirmSingleItemRemoval((RemovableNode) selectedNodes[0])) {
				return;
			}
		}
		else {
			if (!confirmMultipleItemRemoval()) {
				return;
			}
		}
		
		for (int i = 0; i < selectedNodes.length; i ++) {
			((RemovableNode) selectedNodes[i]).remove();
		}
		
	}
	
	protected boolean confirmSingleItemRemoval(RemovableNode removable) {
		int option = JOptionPane.showConfirmDialog(getWorkbenchContext().getCurrentWindow(),
									  resourceRepository().getString("CONFIRM_REMOVE.message", removable.getName()),
									  resourceRepository().getString("CONFIRM_REMOVE.title"),
									  JOptionPane.YES_NO_OPTION);
		return option == JOptionPane.YES_OPTION;
	}

	protected boolean confirmMultipleItemRemoval() {
		int option = JOptionPane.showConfirmDialog(getWorkbenchContext().getCurrentWindow(),
									  resourceRepository().getString("CONFIRM_MULTIPLE_REMOVE.message"),
									  resourceRepository().getString("CONFIRM_REMOVE.title"),
									  JOptionPane.YES_NO_OPTION);
		return option == JOptionPane.YES_OPTION;
	}

}
