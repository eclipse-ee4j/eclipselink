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
package org.eclipse.persistence.tools.workbench.test.uitools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * An icon that is useful for testing.
 */
public class BorderIcon
	implements Icon
{
	private final Icon icon;
	private final Color color;


	/**
	 * Construct an icon that will paint a border around another icon.
	 */
	public BorderIcon(Icon icon, Color color) {
		super();
		this.icon = icon;
		this.color = color;
	}

	/**
	 * Construct an icon that will paint a border around another icon.
	 */
	public BorderIcon(Icon icon) {
		this(icon, Color.BLACK);
	}

	/**
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		int x_off = x + this.icon.getIconWidth() + 1;
		int y_off = y + this.icon.getIconHeight() + 1;
		Color savedColor = g.getColor();

		g.setColor(this.color);

		// draw a square around the entire icon
		g.drawLine(x, y, x_off, y);		// top (left to right)
		g.drawLine(x_off, y + 1, x_off, y_off);		// right (top to bottom)
		g.drawLine(x_off - 1, y_off, x, y_off);		// bottom (right to left)
		g.drawLine(x, y_off - 1, x, y + 1);		// left (bottom to top)

		this.icon.paintIcon(c, g, x + 1, y + 1);

		g.setColor(savedColor);
	}

	/**
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth() {
		return this.icon.getIconWidth() + 2;
	}

	/**
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight() {
		return this.icon.getIconHeight() + 2;
	}

}
