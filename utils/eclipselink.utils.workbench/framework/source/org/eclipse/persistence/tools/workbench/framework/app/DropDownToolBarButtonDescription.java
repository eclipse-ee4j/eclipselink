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

import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JMenu;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.action.ToggleFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.uitools.DropDownButton;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;


/**
 * Specialized instance of a <code>ButtonDescription</code> that possesses a 
 * default <code>ButtonCreator</code> that knows how to build an instance of 
 * <code>DropDownToolBarButton</code>. Requires a <code>FrameworkAction</code>
 * that possess knowledge of the selection state of the action as well as a 
 * <code>MenuDescription</code> describing the sub-menu of the button.
 * 
 * @see org.eclipse.persistence.tools.workbench.framework.app.ButtonDescription
 * @see org.eclipse.persistence.tools.workbench.framework.action.ToggleFrameworkAction
 * @see org.eclipse.persistence.tools.workbench.framework.app.ToggleMenuItem
 * @version 10.1.3
 */
public class DropDownToolBarButtonDescription extends ButtonDescription
{
	private MenuDescription menuDescription;
	
	
	/**
	 * Use this constructor to override the resources specified in the action.
	 */
	public DropDownToolBarButtonDescription(ToggleFrameworkAction action, MenuDescription menuDescription, String text,
			String toolTip, int mnemonic, Icon icon)
	{
		super(action, new ToolBarButtonCreator(menuDescription), text, toolTip, mnemonic, icon);
		this.menuDescription = menuDescription;
	}

	/**
	 * Use this constructor to accept the default action resources.
	 */
	public DropDownToolBarButtonDescription(ToggleFrameworkAction action, MenuDescription menuDescription)
	{
		super(action, new ToolBarButtonCreator(menuDescription));
		this.menuDescription = menuDescription;
	}
	
	public Iterator actions()
	{
		return new CompositeIterator(super.actions(),
								this.menuDescription.actions());
	}

	public void updateOn(Collection frameworkActions)
	{
		super.updateOn(frameworkActions);
		this.menuDescription.updateOn(frameworkActions);
	}
	
	private static class ToolBarButtonCreator implements ButtonCreator
	{
		private MenuDescription menuDescription;
		
		private ToolBarButtonCreator(MenuDescription menuDescription)
		{
			this.menuDescription = menuDescription; 
		}
		
		public AbstractButton createButton(FrameworkAction action)
		{
			return new DropDownButton(action, ((JMenu) this.menuDescription.component()).getPopupMenu());
		}
	}
}
