/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.ui;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;

/**
 * Describes a common help action that can be invoked across all MW
 * node types.
 * 
 * @version 10.1.3
 */
public final class OracleHelpAction extends AbstractFrameworkAction
{
	public OracleHelpAction(WorkbenchContext context)
	{
		super(context);
	}
	
	protected void initialize()
	{
		super.initialize();
		this.setIcon(EMPTY_ICON);
		initializeTextAndMnemonic("HELP");
		initializeToolTipText("HELP.tooltip");
	}
	
	protected void execute(ApplicationNode selectedNode)
	{
		helpManager().showTopic(selectedNode.helpTopicID());		
	}
}
