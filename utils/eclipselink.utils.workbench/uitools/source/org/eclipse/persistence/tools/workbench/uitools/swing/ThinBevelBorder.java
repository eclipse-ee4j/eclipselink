/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.uitools.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.BevelBorder;

/**
 * The JDK bevel border is 2 pixels thick. Override its
 * behavior to only paint 1 pixel.
 */
public class ThinBevelBorder extends BevelBorder {

	public ThinBevelBorder(int bevelType) {
		super(bevelType);
	}

	public ThinBevelBorder(int bevelType, Color highlight, Color shadow) {
		super(bevelType, highlight, shadow.darker());
		// work around possible JDK bug: the code in BevelBorder(int bevelType, Color highlight, Color shadow)
		// should probably read:
		// 	this(bevelType, highlight.brighter(), highlight, shadow.darker(), shadow);
		// not:
		// 	this(bevelType, highlight.brighter(), highlight, shadow, shadow.brighter());
		// this would be more consistent with the default behavior, where the "inside"
		// colors are not as contrasted with the component's background as the
		// "outside" colors
	}

	/**
	 * @see javax.swing.border.BevelBorder#getBorderInsets(java.awt.Component)
	 */
	public Insets getBorderInsets(Component c) {
		return new Insets(1, 1, 1, 1);
	}

	/**
	 * @see javax.swing.border.BevelBorder#getBorderInsets(java.awt.Component, java.awt.Insets)
	 */
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.left = insets.top = insets.right = insets.bottom = 1;
		return insets;
	}

	/**
	 * @see javax.swing.border.BevelBorder#paintRaisedBevel(java.awt.Component, java.awt.Graphics, int, int, int, int)
	 */
	protected void paintRaisedBevel(Component c, Graphics g, int x, int y, int width, int height) {
		Color oldColor = g.getColor();
		int h = height;
		int w = width;

		g.translate(x, y);

		// use the inner colors - they have less contrast
		g.setColor(this.getHighlightInnerColor(c));
		g.drawLine(0, 0, 0, h-2);
		g.drawLine(1, 0, w-2, 0);

		g.setColor(this.getShadowInnerColor(c));
		g.drawLine(0, h-1, w-1, h-1);
		g.drawLine(w-1, 0, w-1, h-2);

		g.translate(-x, -y);
		g.setColor(oldColor);
	}

	/**
	 * @see javax.swing.border.BevelBorder#paintLoweredBevel(java.awt.Component, java.awt.Graphics, int, int, int, int)
	 */
	protected void paintLoweredBevel(Component c, Graphics g, int x, int y, int width, int height) {
		Color oldColor = g.getColor();
		int h = height;
		int w = width;

		g.translate(x, y);

		// use the inner colors - they have less contrast
		g.setColor(this.getShadowInnerColor(c));
		g.drawLine(0, 0, 0, h-1);
		g.drawLine(1, 0, w-1, 0);

		g.setColor(this.getHighlightInnerColor(c));
		g.drawLine(1, h-1, w-1, h-1);
		g.drawLine(w-1, 1, w-1, h-2);

		g.translate(-x, -y);
		g.setColor(oldColor);
	}

}
