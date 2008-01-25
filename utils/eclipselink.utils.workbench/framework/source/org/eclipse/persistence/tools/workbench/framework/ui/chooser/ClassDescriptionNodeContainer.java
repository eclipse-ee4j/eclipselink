/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.ui.chooser;

import java.util.Collection;

/**
 * Interface used by clients to gather up the selected
 * class descriptions from both package and class nodes.
 */
public interface ClassDescriptionNodeContainer {

	/**
	 * Add the container's class description nodes to the specified collection.
	 */
	void addClassDescriptionNodesTo(Collection classDescriptionNodes);

}
