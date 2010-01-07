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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.utility.NameTools;



class RenameTableAction extends AbstractFrameworkAction {

	RenameTableAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		super.initialize();
		this.initializeIcon("rename");
		this.initializeTextAndMnemonic("RENAME");
		this.initializeToolTipText("RENAME");
	}

	protected void execute(ApplicationNode selectedNode) {
		this.promptToRenameTable((MWTable) selectedNode.getValue());
		// re-select because the node will removed, sorted, and re-added
		// if it ends up being renamed
		this.navigatorSelectionModel().setSelectedNode(selectedNode);
	}

	private void promptToRenameTable(MWTable table) {		
		String catalog = table.getCatalog();
		String schema = table.getSchema();
		String shortName = table.getShortName();
		while (true) {
			TableNameDialog tableNameDialog =
				new TableNameDialog(
						this.getWorkbenchContext(),
						this.resourceRepository().getString("RENAME_TABLE_DIALOG.title", table.getName()),
						this.resourceRepository().getString("RENAME_TABLE_DIALOG.message"),
						catalog,
						schema,
						shortName
				);
			tableNameDialog.show();
			if (tableNameDialog.wasCanceled()) {
				return;		// cancelled from the table name dialog
			}

			catalog = tableNameDialog.catalog();
			schema = tableNameDialog.schema();
			shortName = tableNameDialog.shortName();
			try {
				table.rename(catalog, schema, shortName);
				return;		// table renamed successfully
			} catch (IllegalArgumentException ex) {
				String duplicateName = NameTools.buildQualifiedDatabaseObjectName(catalog, schema, shortName);
				int input =
					JOptionPane.showConfirmDialog(
							this.getWorkbenchContext().getCurrentWindow(),
							this.resourceRepository().getString("ADD_NEW_TABLE_DUPLICATE_TABLE_NAME_MESSAGE", duplicateName),
							this.resourceRepository().getString("ADD_NEW_TABLE_DUPLICATE_TABLE_NAME_TITLE"),
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE
					);
				if (input == JOptionPane.CANCEL_OPTION) {
					return;		// cancelled from the duplicate name error dialog
				}
			}
		}
	}
	
}
