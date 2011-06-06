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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

/**
 * This <code>TitledBorder</code> makes JAWS read the title has the pane's title
 * but on screen is shown as an empty border.
 */
public class AccessibleTitledBorder extends TitledBorder
{
	/**
	 * Creates a new <code>AccessibleTitledBorder</code>.
	 */
	public AccessibleTitledBorder(String title)
	{
		super(BorderFactory.createEmptyBorder(), title);
	}

	public Insets getBorderInsets(Component component, Insets insets)
	{
		return insets;
	}

	public Dimension getMinimumSize(Component component)
	{
		return new Dimension(0, 0);
	}

	public void paintBorder(Component component,
									Graphics g,
									int x,
									int y,
									int width,
									int height)
	{
		// Nothing to paint, this is actually an empty border but that requires
		// to be an instanceof TitledBorder due to JAWS
	}
}
