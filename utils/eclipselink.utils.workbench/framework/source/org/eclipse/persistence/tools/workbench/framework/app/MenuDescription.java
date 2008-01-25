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

import javax.swing.Icon;
import javax.swing.JMenu;

/**
 * Describes a non-root menu or what is known as a sub-menu.
 */
public class MenuDescription extends RootMenuDescription
{
	private String text;
	private String toolTip;
	private Icon icon;
	private int mnemonic;
	
	
	public MenuDescription(String text, String toolTip, int mnemonic,  Icon icon)
	{
		super();
		this.text = text;
		this.toolTip = toolTip;
		this.icon = icon;
		this.mnemonic = mnemonic;
	}

	/**
	 * Returns the <code>JMenu</code> represented by this description.
	 */
	public Component component()
	{
		JMenu menu = (JMenu)super.component();
		
		menu.setText(text);
		menu.setIcon(icon);
		menu.setToolTipText(toolTip);
		menu.setMnemonic(mnemonic);
		
		return menu;
	}
}
