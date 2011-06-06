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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.platform;

import java.awt.Cursor;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabaseType;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;


/**
 * add a type to the selected platforms
 */
final class AddDatabaseTypeAction extends AbstractFrameworkAction {

	public AddDatabaseTypeAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		this.initializeTextAndMnemonic("ADD_DATABASE_TYPE");
		// no accelerator
		this.initializeIcon("ADD_DATABASE_TYPE");
		this.initializeToolTipText("ADD_DATABASE_TYPE.TOOL_TIP");
	}

	protected void execute() {
		// turn off the "timer" because we are going to display a dialog
		this.currentWindow().setCursor(Cursor.getDefaultCursor());
		NewNameDialog.Builder builder = this.buildNewNameDialogBuilder();
		ApplicationNode[] nodes = this.selectedNodes();
		DatabasePlatformNode lastNode = null;
		DatabaseType lastType = null;
		for (int i = nodes.length; i-- > 0; ) {
			DatabasePlatformNode node = (DatabasePlatformNode) nodes[i];
			DatabaseType type = this.execute(node.getDatabasePlatform(), builder);
			if (type != null) {
				lastNode = node;
				lastType = type;
			}
		}
		// select the last node added
		if (lastNode != null) {
			lastNode.selectDescendantNodeForValue(lastType, this.navigatorSelectionModel());
		}
	}

	private NewNameDialog.Builder buildNewNameDialogBuilder() {
		// set the properties that stay the same for every node
		NewNameDialog.Builder builder = new NewNameDialog.Builder();
		builder.setTitle(this.resourceRepository().getString("ADD_DATABASE_TYPE_DIALOG_TITLE"));
		builder.setTextFieldDescription(this.resourceRepository().getString("ADD_DATABASE_TYPE_DIALOG_DESCRIPTION"));
		builder.setHelpTopicId("dialog.dbTypeAdd");
		builder.setComparisonIsCaseSensitive(true);
		return builder;
	}

	protected DatabaseType execute(DatabasePlatform dbPlatform, NewNameDialog.Builder builder) {
		builder.setExistingNames(this.existingTypeNames(dbPlatform));
		NewNameDialog dialog = builder.buildDialog(this.getWorkbenchContext());
		dialog.show();
		if (dialog.wasConfirmed()) {
			return dbPlatform.addDatabaseType(dialog.getNewName());
		}
		return null;
	}

	private Iterator existingTypeNames(DatabasePlatform platform) {
		return new TransformationIterator(platform.databaseTypes()) {
			protected Object transform(Object next) {
				return ((DatabaseType) next).getName();
			}
		};
	}

}
