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

import java.sql.SQLException;

import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractEnablableFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWLoginSpec;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;



final class LogInAction 
	extends AbstractEnablableFrameworkAction 
{

	LogInAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		super.initialize();
		initializeText("DATABASE_LOG_IN");
		initializeMnemonic("DATABASE_LOG_IN");
		initializeToolTipText("DATABASE_LOG_IN.toolTipText");
		initializeIcon("login");
	}

	
	protected String[] enabledPropertyNames() {
		return new String[] {MWDatabase.CONNECTED_PROPERTY};
	}
	
	protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
		return !((MWDatabase) ((DatabaseNode) selectedNode).getValue()).isConnected();
	}
	
	protected void execute(ApplicationNode selectedNode) {
		MWDatabase database = (MWDatabase) selectedNode.getValue();
		if (!ableToLogin(database)) {
			return;		
		}	

		try {
			database.login();
		} catch (SQLException sqle) {
			
			String exceptionMessage = sqle.getMessage();
			StringBuffer dialogMessage = new StringBuffer(50);
			dialogMessage.append(exceptionMessage.substring(exceptionMessage.lastIndexOf(']') + 1));
			dialogMessage.append(StringTools.CR);
			dialogMessage.append(resourceRepository().getString("USER_NAME_OR_PASSWORD_COULD_BE_INVALID_MESSAGE"));
			 
			JOptionPane.showMessageDialog(getWorkbenchContext().getCurrentWindow(),
										  dialogMessage.toString(), 
										  resourceRepository().getString("ERROR_LOGGING_IN_TO_DATABASE.title"),
										  JOptionPane.ERROR_MESSAGE);
											
		} catch (ClassNotFoundException cnfe) {
			
			String suggestion = resourceRepository().getString("JDBC_DRIVER_ON_CLASSPATH.message");

			JOptionPane.showMessageDialog(getWorkbenchContext().getCurrentWindow(),
										  resourceRepository().getString("DATABASE_DRIVER_NOT_FOUND.message", new Object[] { cnfe.getMessage(), suggestion, StringTools.CR }),
										  resourceRepository().getString("ERROR_LOGGING_IN_TO_DATABASE.title"),
										  JOptionPane.ERROR_MESSAGE);

		} 
	}
	
	private boolean ableToLogin(MWDatabase database) {
		if(database.isConnected()) {
			showErrorDialog(resourceRepository().getString("ALREADY_CONNECTED.message"));
		}
		MWLoginSpec login = database.getDevelopmentLoginSpec();
		if (login == null) {
			showErrorDialog(resourceRepository().getString("YOU_MUST_DEFINE_A_DEVELOPMENT_LOGIN.message"));
			return false;
		}
		String driverName = login.getDriverClassName();
		if (driverName == null || driverName.equals("")) {
			showErrorDialog(resourceRepository().getString("NO_DATABASE_DRIVER_SPECIFIED.message"));
			return false;
		}	
		String url = login.getURL();
		if (url == null || url.equals("")) {
			showErrorDialog(resourceRepository().getString("NO_URL_SPECIFIED.message"));
			return false;
		}
		
		return true;
	}
	
	private void showErrorDialog(String message) {
		JOptionPane.showMessageDialog(getWorkbenchContext().getCurrentWindow(),
								message,											 
								resourceRepository().getString("ERROR_LOGGING_IN_TO_DATABASE.title"),
								JOptionPane.ERROR_MESSAGE);
	}

}
