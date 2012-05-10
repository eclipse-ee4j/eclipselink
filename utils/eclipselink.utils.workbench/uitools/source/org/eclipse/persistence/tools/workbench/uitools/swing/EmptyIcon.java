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

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * Implement the Icon interface with an icon that has a size but
 * does not paint anything on the graphics context.
 */
public class EmptyIcon
	implements Icon
{
	private final int width;
	private final int height;

	public static final EmptyIcon NULL_INSTANCE = new EmptyIcon(0);

	public static final EmptyIcon SMALL_ICON = new EmptyIcon(16);

	public EmptyIcon(int width, int height) {
		super();
		this.width = width;
		this.height = height;
	}

	public EmptyIcon(int size) {
		this(size, size);
	}


	// ********** Icon implementation **********

	public void paintIcon(Component c, Graphics g, int x, int y) {
		// don't paint anything for an empty icon
	}

	public int getIconWidth() { 
		return this.width;
	}

	public int getIconHeight() {
		return this.height;
	}

}
