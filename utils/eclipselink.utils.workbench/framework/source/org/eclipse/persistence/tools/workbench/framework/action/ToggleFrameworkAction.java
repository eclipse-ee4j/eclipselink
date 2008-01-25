/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.action;

/**
 * Represents a <code>FrameworkAction</code> that contains the notion of 
 * a selection state.  By virtue of being an <code>Action</code> a <code>FrameworkAction</code>
 * has an enabled state.  When an action is in buttons or other UI widgets that must
 * maintain the notion of a selection state, an implementor of this action type can
 * be used to reduce coupling with the UI widget by maintaining the selection state
 * in the action.
 *  
 * @see org.eclipse.persistence.tools.workbench.framework.action.AbstractToggleFrameworkAction
 * @see org.eclipse.persistence.tools.workbench.framework.app.ToggleToolBarButtonDescription
 * @see org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction
 * @version 10.1.3
 */
public interface ToggleFrameworkAction extends FrameworkAction
{
	/** unique property representing the toggle state */
	public static final String TOGGLE_STATE_PROPERTY = "toggleStateProperty";
	
	/**
	 * Sets the selection state of the action.
	 */
	public void setSelected(boolean selectionState);
	
	/**
	 * Returns the selection state of the action.
	 */
	public boolean isSelected();
}
