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
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.utility.NameTools;


/**
 * prompt the user for a new table name (catalog, schema, short name),
 * disallowing duplicates
 */
final class AddNewTableAction extends AbstractFrameworkAction {

	AddNewTableAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		super.initialize();
		this.initializeIcon("table.add");
		this.initializeTextAndMnemonic("ADD_NEW_TABLE_ACTION");
		this.initializeToolTipText("ADD_NEW_TABLE_ACTION.toolTipText");
	}

	protected void execute(ApplicationNode selectedNode) {
		DatabaseNode databaseNode = (DatabaseNode) selectedNode;
		MWTable newTable = this.promptToCreateNewTableFor(databaseNode.getDatabase());
		if (newTable != null) {
			databaseNode.selectDescendantNodeForValue(newTable, this.navigatorSelectionModel());
		}
	}

	private MWTable promptToCreateNewTableFor(MWDatabase database) {
		String catalog = null;
		String schema = null;
		String shortName = NameTools.uniqueNameForIgnoreCase(this.resourceRepository().getString("NEW_TABLE_DEFAULT_NAME"), database.tableNames());
		while (true) {
			TableNameDialog tableNameDialog =
				new TableNameDialog(
						this.getWorkbenchContext(),
						this.resourceRepository().getString("ADD_NEW_TABLE_DAILOG.title"),
						this.resourceRepository().getString("ADD_NEW_TABLE_DAILOG.message"),
						catalog,
						schema,
						shortName
				);
			tableNameDialog.show();
			if (tableNameDialog.wasCanceled()) {
				return null;		// cancelled from the table name dialog
			}

			catalog = tableNameDialog.catalog();
			schema = tableNameDialog.schema();
			shortName = tableNameDialog.shortName();
			try {
				return database.addTable(catalog, schema, shortName);
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
					return null;		// cancelled from the duplicate name error dialog
				}
			}
		}
	}

}
