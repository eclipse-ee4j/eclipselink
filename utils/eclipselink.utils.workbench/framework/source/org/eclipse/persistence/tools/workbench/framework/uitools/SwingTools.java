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
package org.eclipse.persistence.tools.workbench.framework.uitools;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public final class SwingTools 
{
	/** 
	 * A tricky way to set the border of a JSplitPane's divider. 
	 * It assumes that UI used to paint this splitpane extends BasicSplitPaneUI.
	 */
	public static void setSplitPaneDividerBorder(JSplitPane splitPane, Border border) {
		BasicSplitPaneUI splitPaneUi;
		
		try {
			splitPaneUi = (BasicSplitPaneUI) splitPane.getUI();
		}
		catch (ClassCastException cce) {
			return;
		}
		
		splitPaneUi.getDivider().setBorder(border);
	}

	/**
	 * The width taken by the check box icon and the gap between the icon and the
	 * text.
	 */
	private static int checkBoxIconWidth = -1;
	
	/**
	 * The width taken by the radio button icon and the gap between the icon and the
	 * text.
	 */
	private static int radioButtonIconWidth = -1;
	
	/**
	 * Retrieves the width taken by the check box icon including the gap between
	 * the icon and the text.
	 *
	 * @return The size of the icon and the gap
	 */
	public static int checkBoxIconWidth()
	{
		if (checkBoxIconWidth == -1)
		{
			Icon icon = checkBoxIcon();
			checkBoxIconWidth = (icon != null) ? icon.getIconWidth() : 0;
			checkBoxIconWidth += checkBoxTextIconGap();
			checkBoxIconWidth += new JCheckBox().getInsets().left;
		}
	
		return checkBoxIconWidth;
	}
	
	/**
	 * Returns the default icon showing the selection state of a check box.
	 * 
	 * @return The icon specified by the look and feel of a check box
	 */
	public static Icon checkBoxIcon() {
		return UIManager.getIcon("CheckBox.icon");
	}
	
	/**
	 * Returns the default gap (in pixels) between the icon and the text that is usually used on a check box.
	 * 
	 * @return The default space between the icon and the text on a check box
	 */
	public static int checkBoxTextIconGap() {
		return UIManager.getInt("CheckBox.textIconGap");
	}
	
	/**
	 * Returns the default gap (in pixels) between the icon and the text that is usually used on a radio button.
	 * 
	 * @return The default space between the icon and the text on a radio button
	 */
	public static int radioButtonTextIconGap() {
		return UIManager.getInt("RadioButton.textIconGap");
	}
	
	/**
	 * Returns the default icon showing the selection state of a radio button.
	 * 
	 * @return The icon specified by the look and feel of a radio button
	 */
	public static Icon radioButtonIcon() {
		return UIManager.getIcon("RadioButton.icon");
	}
	
	/**
	 * Retrieves the width taken by the radio button icon including the gap between
	 * the icon and the text.
	 *
	 * @return The size of the icon and the gap
	 */
	public static int radioButtonIconWidth() {
		{
			if (radioButtonIconWidth == -1)
			{
				Icon icon = radioButtonIcon();
				radioButtonIconWidth = (icon != null) ? icon.getIconWidth() : 0;
				radioButtonIconWidth += radioButtonTextIconGap();
				radioButtonIconWidth += new JRadioButton().getInsets().left;
			}
		
			return radioButtonIconWidth;
		}
	}
}
