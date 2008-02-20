/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.scplugin.ui.pool.basic;

// JDK
import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.scplugin.ui.login.EisLoginPane;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;

// Mapping Workbench

/**
 * This page shows the information regarding the Database login which is
 * specific for {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.EISLoginAdapter DatabaseLoginAdapter}.
 * <p>
 * Here the layout:
 * <pre>
 * _____________________________________________
 * |                                           |
 * | x Use Non-Transactional Read Login        |<- Enable/Disable the EisLoginPane
 * |                                           |
 * |   --------------------------------------- |
 * |   |                                     | |
 * |   | {@link EisLoginPane}                             | |
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
public class EisReadPoolLoginPropertiesPage extends AbstractReadPoolLoginPropertiesPage
{
	/**
	 * Creates a new <code>RdbmsReadPoolLoginPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of {@link PoolNode}
	 */
	public EisReadPoolLoginPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
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
		return new EisLoginPane(buildLoginHolder(), getWorkbenchContextHolder());
	}
}