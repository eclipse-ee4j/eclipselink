/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.app;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;

/**
 * Use this interface to abstract out the building of the ToolBar and
 * the SelectionMenu of an ApplicationNode.  Often the only thing
 * differing from a group of nodes are the items in the selected menu
 */
public interface SelectionActionsPolicy {

	public GroupContainerDescription buildMenuDescription(WorkbenchContext context);
	
	public GroupContainerDescription buildToolBarDescription(WorkbenchContext context);

}
