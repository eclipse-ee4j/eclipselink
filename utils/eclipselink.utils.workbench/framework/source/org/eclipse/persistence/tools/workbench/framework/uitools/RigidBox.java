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

// JDK
import java.awt.Dimension;
import javax.swing.Box;

/**
 * When using a rigid box with a GrigLayout, the box will not
 * be resized like the other component but will keep its size.
 *
 * @version 1.2
 * @author Pascal Filion
 */
public class RigidBox extends Box.Filler
{
	/**
	 * Creates a new rigid box with a size of (5, 5).
	 */
	public RigidBox()
	{
		this(5);
	}

	/**
	 * Creates a new rigid box with a size of
	 * (<code>size</code>, <code>size</code>).
	 *
	 * @param size The size of the rigid box
	 */
	public RigidBox(int size)
	{
		this(new Dimension(size, size));
	}

	/**
	 * Creates a new rigid box with the given size.
	 *
	 * @param size The size of the rigid box
	 */
	public RigidBox(Dimension size)
	{
		this(size, size, size);
	}

	/**
	 * Creates a new rigid box based on the given rigid box.
	 *
	 * @param rigidBox The template rigid box
	 */
	public RigidBox(RigidBox rigidBox)
	{
		this(rigidBox.getMinimumSize(), rigidBox.getPreferredSize(), rigidBox.getMaximumSize());
	}

	/**
	 * Creates a new rigid box with the given size.
	 *
	 * @param minimumSize The mimimum size of the rigid box
	 * @param preferredSize The preferred size of the rigid box
	 * @param maximumSize The maximum size of the rigid box
	 */
	public RigidBox(Dimension minimumSize, Dimension preferredSize, Dimension maximumSize)
	{
		super(minimumSize, preferredSize, maximumSize);
	}

	/**
	 * Returns a new rigid box with a size of (5, 5).
	 *
	 * @return A rigid box
	 */
	public static RigidBox box()
	{
		return new RigidBox();
	}

	/**
	 * Returns a new rigid box with a size of (<code>size</code>, <code>size</code>).
	 *
	 * @param size The size of the rigid box
	 * @return A rigid box
	 */
	public static RigidBox box(int size)
	{
		return new RigidBox(size);
	}

	/**
	 * Returns a new rigid box with the given size.
	 *
	 * @param size The size of the rigid box
	 * @return A rigid box
	 */
	public static RigidBox box(Dimension size)
	{
		return new RigidBox(size);
	}
}
