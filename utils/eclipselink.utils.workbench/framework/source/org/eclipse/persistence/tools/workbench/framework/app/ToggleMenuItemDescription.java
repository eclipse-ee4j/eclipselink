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

import javax.swing.AbstractButton;
import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.action.ToggleFrameworkAction;


/**
 * Specialized instance of a <code>ButtonDescription</code> that possesses a 
 * default <code>ButtonCreator</code> that knows how to build an instance of 
 * <code>ToggleMenuItem</code>. Requires a <code>ToggleFrameworkAction</code>
 * that possess knowledge of the selection state of the action.
 * 
 * @see org.eclipse.persistence.tools.workbench.framework.app.ButtonDescription
 * @see org.eclipse.persistence.tools.workbench.framework.action.ToggleFrameworkAction
 * @see org.eclipse.persistence.tools.workbench.framework.app.ToggleMenuItem
 * @version 10.1.3
 */
public class ToggleMenuItemDescription extends ButtonDescription
{
	public ToggleMenuItemDescription(ToggleFrameworkAction action, String text,
			String toolTip, int mnemonic, Icon icon)
	{
		super(action, new CheckBoxMenuItemCreator(), text, toolTip, mnemonic, icon);
	}

	
	public ToggleMenuItemDescription(ToggleFrameworkAction action)
	{
		super(action, new CheckBoxMenuItemCreator());
	}
	
	/**
	 * Implementor of <code>ButtonCreator/code> that knows how to build a
	 * <code>ToggleMenuItem</code>.
	 */
	private static class CheckBoxMenuItemCreator implements ButtonCreator
	{
		public AbstractButton createButton(FrameworkAction action)
		{
			return new ToggleMenuItem((ToggleFrameworkAction) action);
		}
	}
}
