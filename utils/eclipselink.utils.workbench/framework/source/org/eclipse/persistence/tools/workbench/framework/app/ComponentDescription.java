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

import java.awt.Component;

/**
 * An implementing class should describe how to build a <code>Component</code>
 * based upon the given actions described in the <code>ActionContainer</code> 
 * interface. 
 * 
 * @see org.eclipse.persistence.tools.workbench.framework.app.ActionContainer
 * @see java.awt.Component
 * @version 10.1.3
 */
public interface ComponentDescription extends ActionContainer
{
	/**
	 * Returns a <code>Component</code> built based upon the description
	 * of the implementing class.
	 */
	public Component component();
}
