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
package org.eclipse.persistence.tools.workbench.framework.app;

/**
 * Describes a Component container which contains groups of other Components.
 * The end result of this group should be a single <code>Component</code> 
 * describing the aggregated structure of all groups. 
 * 
 * @see org.eclipse.persistence.tools.workbench.framework.app.ComponentGroupDescription
 * @see org.eclipse.persistence.tools.workbench.framework.app.ComponentDescription
 * @see org.eclipse.persistence.tools.workbench.framework.app.MergeableContainer
 * @version 10.1.3
 */
public interface GroupContainerDescription extends ComponentContainerDescription, 
																ComponentDescription,
																MergeableContainer
{
	/**
	 * Adds a <code>ComponentGroupDescription</code> which describes a group
	 * of <code>Component</code> objects to this container.
	 */
	public void add(ComponentGroupDescription group);
	
	
	/**
	 * Removes a <code>ComponentGroupDescription</code> which describes a group
	 * of <code>Component</code> objects from this container.
	 */
	public void remove(ComponentGroupDescription group);
	
}
