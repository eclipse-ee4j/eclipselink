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
package org.eclipse.persistence.tools.workbench.uitools.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * Icon that simply draws a solid block.
 */
public class BlockIcon
	implements Icon
{
	private final int width;
	private final int height;
	private final Color color;


	public BlockIcon(int width, int height, Color color) {
		super();
		this.width = width;
		this.height = height;
		this.color = color;
	}

	public BlockIcon(int width, int height) {
		this(width, height, Color.BLACK);
	}

	public BlockIcon(int size) {
		this(size, size);
	}

	public BlockIcon(int size, Color color) {
		this(size, size, color);
	}


	// ********** Icon implementation **********

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Color oldColor = g.getColor();
		g.setColor(this.color);
		g.fillRect(x, y, this.width, this.height);
		g.setColor(oldColor);
	}

	public int getIconWidth() { 
		return this.width;
	}

	public int getIconHeight() {
		return this.height;
	}


	// ********** additional stuff **********

	public Color getColor() {
		return this.color;
	}

}
