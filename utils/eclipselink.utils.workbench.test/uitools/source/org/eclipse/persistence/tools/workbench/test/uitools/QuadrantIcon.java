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
public class QuadrantIcon implements Icon {
	private final Color color;

	private final int width;
	private final int widthOffset;
	private final int widthMiddle;

	private final int height;
	private final int heightOffset;
	private final int heightMiddle;


	/**
	 * Construct an icon that will paint a Four Square with
	 * the specified settings.
	 */
	public QuadrantIcon(Color color, int width, int height) {
		super();
		this.color = color;

		this.width = width;
		this.widthOffset = width - 1;
		this.widthMiddle = width >> 1;

		this.height = height;
		this.heightOffset = height - 1;
		this.heightMiddle = height >> 1;
	}

	/**
	 * Construct an icon that will paint a Four Square of the specified size.
	 */
	public QuadrantIcon(int width, int height) {
		this(Color.BLACK, width, height);
	}

	/**
	 * Construct an icon that will paint a Four Square of the specified size.
	 */
	public QuadrantIcon(Color color, int size) {
		this(color, size, size);
	}

	/**
	 * Construct an icon that will paint a Four Square of the specified size.
	 */
	public QuadrantIcon(int size) {
		this(Color.BLACK, size);
	}


	/**
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		int x_off = x + this.widthOffset;
		int x_mid = x + this.widthMiddle;
		int y_off = y + this.heightOffset;
		int y_mid = y + this.heightMiddle;
		Color savedColor = g.getColor();

		g.setColor(this.color);

		// draw a square around the entire icon
		g.drawLine(x, y, x_off, y);		// top (left to right)
		g.drawLine(x_off, y + 1, x_off, y_off);		// right (top to bottom)
		g.drawLine(x_off - 1, y_off, x, y_off);		// bottom (right to left)
		g.drawLine(x, y_off - 1, x, y + 1);		// left (bottom to top)

		// draw crosshairs
		g.drawLine(x_mid, y + 1, x_mid, y_off - 1);		// vertical (top to bottom)
		g.drawLine(x + 1, y_mid, x_off - 1, y_mid);		// horizontal (left to right)
		if ((this.width & 1) == 0) {		// if width is even
			// draw a second line if there is not a true middle line
			g.drawLine(x_mid - 1, y + 1, x_mid - 1, y_off - 1);		// vertical (top to bottom)
		}
		if ((this.height & 1) == 0) {		// if height is even
			// draw a second line if there is not a true middle line
			g.drawLine(x + 1, y_mid - 1, x_off - 1, y_mid - 1);		// horizontal (left to right)
		}

		g.setColor(savedColor);
	}

	/**
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth() {
		return this.width;
	}

	/**
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight() {
		return this.height;
	}

}
