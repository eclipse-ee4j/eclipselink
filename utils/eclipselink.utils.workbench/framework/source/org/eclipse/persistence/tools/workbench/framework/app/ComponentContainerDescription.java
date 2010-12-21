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

import java.util.ListIterator;

/**
 * This interface describes a class that knows how to return an ordered List of
 * <code>Component</code> objects contained within it as well as whether any 
 * <code>Component</code> are contained within the implementing class.  
 * Implementors of this interface can be used to build UI component structures
 * where aggregation of multiple <code>Component</code> type objects is useful
 * in operations such as merging. 
 * 
 * @version 10.1.3
 */
public interface ComponentContainerDescription extends ActionContainer
{
	/**
	 * Returns an ordered <code>ListIterator</code> of <code>Component</code> type
	 * objects.  The order of these <code>Component</code> objects should express
	 * the oreder in which they should be added to the UI.
	 * 
	 * @see org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction
	 * @see java.awt.Component
	 * @return - ListIterator of Component type objects.
	 */
	public ListIterator components();
	
	
	/**
	 * Returns whether this Container has any Components. 
	 */
	public boolean hasComponents();
}
