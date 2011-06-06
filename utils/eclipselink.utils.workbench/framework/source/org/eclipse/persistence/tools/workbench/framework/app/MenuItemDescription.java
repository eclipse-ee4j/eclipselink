/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JMenuItem;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;


/**
 * Specialized instance of a <code>ButtonDescription</code> that possesses a 
 * default <code>ButtonCreator</code> that knows how to build an instance of 
 * <code>JMenuItem</code>. 
 * 
 * @see org.eclipse.persistence.tools.workbench.framework.app.ButtonDescription
 * @see org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction
 * @see javax.swing.JMenuItem
 * @version 10.1.3
 */
public class MenuItemDescription extends ButtonDescription
{
	public MenuItemDescription(FrameworkAction action, String text,
			String toolTip, int mnemonic, Icon icon)
	{
		super(action, new MenuItemCreator(), text, toolTip, mnemonic, icon);
	}

	public MenuItemDescription(FrameworkAction action)
	{
		super(action, new MenuItemCreator());
	}

	/**
	 * ButtonCreator implementation that creates a JMenuItem for the
	 * MenuItemDescription class.
	 */
	private static class MenuItemCreator implements ButtonCreator
	{
		public AbstractButton createButton(FrameworkAction action)
		{
			return new JMenuItem(action);
		}
	}
}
