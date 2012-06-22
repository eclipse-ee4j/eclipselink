/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
