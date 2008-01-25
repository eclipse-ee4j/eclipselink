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

/**
 * Describes an UI component class that can be merged with another
 * based upon the <code>FrameworkAction</code> objects contained therein. 
 * 
 * @see org.eclipse.persistence.tools.workbench.framework.app.ActionContainer
 * @version 10.1.3
 */
public interface MergeableContainer {
	
	/**
	 * Implementing class should represent merge behavior in this
	 * method.
	 */
	public void mergeWith(ActionContainer actionContainer);
}
