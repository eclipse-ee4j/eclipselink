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
 * Describes an implementing class that knows how to add and remove 
 * <code>ComponentDescription</code> objects with inherited container behavior
 * specified by the <code>ComponentContainerDescriptor</code>. 
 * 
 * @see org.eclipse.persistence.tools.workbench.framework.app.ComponentContainerDescription
 * @see org.eclipse.persistence.tools.workbench.framework.app.ComponentDescription
 * @version 10.1.3
 */
public interface ComponentGroupDescription extends ComponentContainerDescription
{
	/**
	 * Adds an instance of <code>ComponentDescription</code> which describes
	 * how a component should be built to this group.
	 * 
	 * @see org.eclipse.persistence.tools.workbench.framework.app.ComponentDescription
	 * @param button
	 */
	public void add(ComponentDescription description);
	
	/**
	 * Removes an instance of <code>ComponentDescription</code> which describes
	 * how a component should be built to this group.
	 * 
	 * @param button
	 */
	public void remove(ComponentDescription description);
}
