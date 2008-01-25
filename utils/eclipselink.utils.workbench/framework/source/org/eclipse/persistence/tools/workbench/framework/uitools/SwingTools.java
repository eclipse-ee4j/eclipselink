/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.uitools;

import javax.swing.Icon;
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
	 * Retrieves the width taken by the check box icon including the gap between
	 * the icon and the text.
	 *
	 * @return The size of the icon and the gap
	 */
	public static int checkBoxIconWidth()
	{
		if (checkBoxIconWidth == -1)
		{
			Icon icon = (Icon) UIManager.get("CheckBox.icon");
			checkBoxIconWidth = (icon != null) ? icon.getIconWidth() : 0;
			Integer gap = (Integer) UIManager.get("CheckBox.textIconGap");
			checkBoxIconWidth += (gap != null) ? gap.intValue() + 4 : 4;
		}
	
		return checkBoxIconWidth;
	}
}
