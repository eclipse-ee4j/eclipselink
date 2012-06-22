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
package org.eclipse.persistence.tools.workbench.framework.uitools;

import java.awt.Cursor;

/**
 * Defines constants for predefined cursors.
 *
 * @version 1.3
 * @author Pascal Filion
 */
public interface CursorConstants
{
	/** The default cursor. */
	public static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

	/** The hand cursor. */
	public static final Cursor HAND_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

	/** The text cursor. */
	public static final Cursor TEXT_CURSOR = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);

	/** The move cursor, it's usually a + sign. */
	public static final Cursor MOVE_CURSOR = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);

	/** The cross hair cursor */
	public static final Cursor CROSSHAIR_CURSOR = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);

	/** The horizontal-east resize cursor. */
	public static final Cursor E_RESIZE_CURSOR = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);

//	/** The horizontal resize cursor. */
//	public static final Cursor HORIZONTAL_RESIZE_CURSOR =
//		Toolkit.getDefaultToolkit().createCustomCursor(((ImageIcon) UIDefaults.instance().getIcon("resize.horizontally")).getImage(),
//																	  new Point(6, 6),
//																	  "resize.horizontally");
//
//	/** The large version of the horizontal resize cursor. */
//	public static final Cursor HORIZONTAL_RESIZE_CURSOR_LARGE =
//		Toolkit.getDefaultToolkit().createCustomCursor(((ImageIcon) UIDefaults.instance().getIcon("resize.horizontally.large")).getImage(),
//																	  new Point(6, 6),
//																	  "resize.horizontally");

	/** The vertical-north resize cursor. */
	public static final Cursor N_RESIZE_CURSOR = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);

	/** The vertical-north-east resize cursor. */
	public static final Cursor NE_RESIZE_CURSOR = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);

	/** The vertical-north-west resize cursor. */
	public static final Cursor NW_RESIZE_CURSOR = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);

	/** The vertical-south resize cursor. */
	public static final Cursor S_RESIZE_CURSOR = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);

	/** The vertical-south-east resize cursor. */
	public static final Cursor SE_RESIZE_CURSOR = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);

	/** The vertical-south-west resize cursor. */
	public static final Cursor SW_RESIZE_CURSOR = Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);

//	/** The horizontal resize cursor. */
//	public static final Cursor VERTICAL_RESIZE_CURSOR =
//		Toolkit.getDefaultToolkit().createCustomCursor(((ImageIcon) UIDefaults.instance().getIcon("resize.vertically")).getImage(),
//																	  new Point(6, 6),
//																	  "resize.vertically");
//
//	/** The large version of the horizontal resize cursor. */
//	public static final Cursor VERTICAL_RESIZE_CURSOR_LARGE =
//		Toolkit.getDefaultToolkit().createCustomCursor(((ImageIcon) UIDefaults.instance().getIcon("resize.vertically.large")).getImage(),
//																	  new Point(6, 6),
//																	  "resize.vertically");

	/** The horizontal-west resize cursor. */
	public static final Cursor W_RESIZE_CURSOR = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);

	/** The wait cursor, it's usually an hour glass. */
	public static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
}
