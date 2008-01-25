/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.ui.pool.basic;

// JDK
import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.scplugin.ui.login.RdbmsPoolLoginPane;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;

// Mapping Workbench

/**
 * This page shows the information regarding the Database login which is
 * specific for {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter DatabaseLoginAdapter}.
 * <p>
 * Here the layout:
 * <pre>
 * _____________________________________________
 * |                                           |
 * | x Use Non-Transactional Read Login        |<- Enable/Disable the RdbmsPoolLoginPane
 * |                                           |
 * |   --------------------------------------- |
 * |   |                                     | |
 * |   | {@link RdbmsPoolLoginPane}                  | |
 * |   |                                     | |
 * |   --------------------------------------- |
 * |                                           |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 * @see ConnectionPoolAdapter
 * @see ReadConnectionPoolAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class RdbmsReadPoolLoginPropertiesPage extends AbstractReadPoolLoginPropertiesPage
{
	/**
	 * Creates a new <code>RdbmsReadPoolLoginPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of {@link PoolNode}
	 */
	public RdbmsReadPoolLoginPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
	}

	/**
	 * Creates the pane that will show the login information.
	 *
	 * @return {@link EisLoginPane}
	 */
	protected JComponent buildLoginPane()
	{
		return new RdbmsPoolLoginPane(buildLoginHolder(), getWorkbenchContextHolder());
	}
}