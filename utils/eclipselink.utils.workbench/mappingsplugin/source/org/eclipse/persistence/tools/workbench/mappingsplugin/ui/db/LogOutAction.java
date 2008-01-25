/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import java.sql.SQLException;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractEnablableFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;



final class LogOutAction 
	extends AbstractEnablableFrameworkAction 
{
	
	LogOutAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		super.initialize();
		initializeText("DATABASE_LOG_OUT");
		initializeMnemonic("DATABASE_LOG_OUT");
		initializeToolTipText("DATABASE_LOG_OUT.toolTipText");
		initializeIcon("logout");
	}

	protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
		return ((MWDatabase) ((DatabaseNode) selectedNode).getValue()).isConnected();
	}
	
	protected String[] enabledPropertyNames() {
		return new String[] {MWDatabase.CONNECTED_PROPERTY};
	}
	
	protected void execute(ApplicationNode selectedNode) {
		MWDatabase database = (MWDatabase) selectedNode.getValue();
		try {
			database.logout();
		} catch (SQLException sqle) {
			//TODO pretty this up, once we have exception handling
			throw new RuntimeException(sqle);
			//ThrowableDialog.show(this, sqle);
		}
	}
	
}