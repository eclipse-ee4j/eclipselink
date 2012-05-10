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


import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;


/**
 * Specialized instance of a <code>ButtonDescription</code> that possesses a 
 * default <code>ButtonCreator</code> that knows how to build an instance of 
 * <code>SelectableToolBarButton</code>. Requires a <code>SelectableFrameworkAction</code>
 * that possess knowledge of the selection state of the action.
 * 
 * @see org.eclipse.persistence.tools.workbench.framework.app.ButtonDescription
 * @see org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction
 * @see org.eclipse.persistence.tools.workbench.framework.app.ToolBarButton
 * @version 10.1.3
 */
public class ToolBarButtonDescription extends ButtonDescription
{
	public ToolBarButtonDescription(FrameworkAction action, String text, 
								String toolTip, int mnemonic, Icon icon)
	{
		super(action, new ToolBarButtonCreator(), text, toolTip, mnemonic, icon);
	}
	
	public ToolBarButtonDescription(FrameworkAction action)
	{
		super(action, new ToolBarButtonCreator());
	}
	
	/**
	 * Implementor of <code>ButtonCreator/code> that knows how to build a
	 * <code>ToolBarButton</code>.
	 */
	private static class ToolBarButtonCreator implements ButtonCreator {

		public AbstractButton createButton(FrameworkAction action) {
			AbstractButton button = new JButton();
			// the client property hack must be set before the action is set
			button.putClientProperty("hideActionText", Boolean.TRUE);
			button.setAction(action);
			button.setMnemonic('\0'); // Make sure the mnemonic is never active
			return button;
		}
	}
}
