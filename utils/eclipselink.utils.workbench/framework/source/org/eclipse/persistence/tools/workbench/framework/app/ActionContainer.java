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
package org.eclipse.persistence.tools.workbench.framework.app;

import java.util.Collection;
import java.util.Iterator;

/**
 * This interface describes an object that contains <code>FrameworkActions</code>
 * and knows how to update itself based upon a given Collection of those actions.
 * 
 * @see org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction
 * @version 10.1.3
 */
public interface ActionContainer
{
	/**
	 * Returns an <code>Iterator</code> over the collection of all <code>FrameworkAction</code> contained in the
	 * implementing class.
	 * 
	 * @see org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction
	 * @return - Iterator of <code>FrameworkActions</code>. 
	 */
	public Iterator actions();
	
	/**
	 * Updates the state of the given object based upon the provided <code>Collection</code>
	 * of <code>FrameworkAction</code>. 
	 * 
	 * @see org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction
	 * @param frameworkActions - a Collection of FrameworkActions.
	 */
	public void updateOn(Collection frameworkActions);
}
