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
package org.eclipse.persistence.tools.workbench.framework.uitools;

// JDK
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import javax.swing.JComponent;

/**
 * A <code>Spacer</code> is a light component used to add space between
 * components in order to align them.
 *
 * @version 10.0.3
 * @author Pascal Filion
 */
public final class Spacer extends JComponent
{
	/**
	 * The minimum size this spacer should have as its minimum and preferred
	 * sizes.
	 */
	private final Dimension minimumSize;

	/**
	 * Creates a new <code>Spacer</code>.
	 */
	public Spacer()
	{
		this(0);
	}

	/**
	 * @param preferredSize
	 */
	public void setPreferredSize(Dimension preferredSize)
	{
		super.setPreferredSize(preferredSize);

		if (preferredSize != null)
			setVisible(preferredSize.width > 0);
	}

	/**
	 * Creates a new <code>Spacer</code>.
	 *
	 * @param minimumWidth The minimum width this spacer should have as its
	 * preferred size
	 */
	public Spacer(int minimumWidth)
	{
		super();

		this.minimumSize = new Dimension(minimumWidth, 0);
		setLayout(new SpacerLayout());
	}

	/**
	 * This <code>LayoutManager</code> returns the {@link Spacer#minimumWidth}
	 * for the preferred and minimum width.
	 */
	private class SpacerLayout implements LayoutManager
	{
		public void addLayoutComponent(String name, Component component) {}
		public void layoutContainer(Container parent) {}
		public void removeLayoutComponent(Component component) {}

		public Dimension minimumLayoutSize(Container parent)
		{
			return (Dimension) Spacer.this.minimumSize.clone();
		}

		public Dimension preferredLayoutSize(Container parent)
		{
			return (Dimension) Spacer.this.minimumSize.clone();
		}
	}
}
