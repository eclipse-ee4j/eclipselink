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

// JDK
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.util.Locale;
import javax.swing.JSplitPane;

/**
 * This extension over <code>OSplitPane</code> simply check for the component
 * orientation and update the left and right components accordingly.
 *
 * @version 1.3
 * @author Pascal Filion
 */
public class OSplitPane extends JSplitPane
{
	/**
	 * Creates a new <code>OSplitPane</code> configured to arrange the child
	 * components side-by-side horizontally with no continuous layout, using two
	 * buttons for the components.
	 */
	public OSplitPane()
	{
		super();
	}

	/**
	 * Creates a new <code>OSplitPane</code> configured with the specified
	 * orientation and no continuous layout.
	 *
	 * @param orientation <code>OSplitPane.HORIZONTAL_SPLIT</code> or
	 * <code>OSplitPane.VERTICAL_SPLIT</code>
	 * @exception IllegalArgumentException if <code>orientation</code>
	 *	is not one of <code>HORIZONTAL_SPLIT</code> or <code>VERTICAL_SPLIT</code>
	 */
	public OSplitPane(int orientation)
	{
		super(orientation);
	}

	/**
	 * Creates a new <code>OSplitPane</code> with the specified
	 * orientation and redrawing style.
	 *
	 * @param orientation <code>OSplitPane.HORIZONTAL_SPLIT</code> or
	 * <code>OSplitPane.VERTICAL_SPLIT</code>
	 * @param newContinuousLayout  a boolean, true for the components to redraw
	 * continuously as the divider changes position, <code>false</code> to wait
	 * until the divider position stops changing to redraw
	 * @exception IllegalArgumentException if <code>orientation</code> is not one
	 * of <code>HORIZONTAL_SPLIT</code> or <code>VERTICAL_SPLIT</code>
	 */
	public OSplitPane(int orientation, boolean newContinuousLayout)
	{
		this(orientation, newContinuousLayout, null, null);
	}

	/**
	 * Creates a new <code>OSplitPane</code> with the specified orientation and
	 * with the specified components that do not do continuous redrawing.
	 *
	 * @param orientation <code>OSplitPane.HORIZONTAL_SPLIT</code> or
	 * <code>OSplitPane.VERTICAL_SPLIT</code>
	 * @param leftComponent the <code>Component</code> that will appear on the
	 * left of a horizontally-split pane, or at the top of a vertically-split pane
	 * @param newRightComponent the <code>Component</code> that will appear on
	 * the right of a horizontally-split pane, or at the bottom of a
	 * vertically-split pane
	 * @exception IllegalArgumentException if <code>orientation</code> is not one
	 * of: <code>HORIZONTAL_SPLIT</code> or <code>VERTICAL_SPLIT</code>
	 */
	public OSplitPane(int orientation, Component leftComponent, Component rightComponent)
	{
		super(orientation, leftComponent, rightComponent);
	}

	/**
	 * Creates a new <code>OSplitPane</code> with the specified orientation and
	 * redrawing style, and with the specified components.
	 *
	 * @param orientation <code>OSplitPane.HORIZONTAL_SPLIT</code> or
	 * <code>OSplitPane.VERTICAL_SPLIT</code>
	 * @param continuousLayout <code>true</code> for the components to redraw
	 * continuously as the divider changes position, <code>false</code> to wait
	 * until the divider position stops changing to redraw
	 * @param leftComponent the <code>Component</code> that will appear on the
	 * left of a horizontally-split pane, or at the top of a vertically-split pane
	 * @param rightComponent the <code>Component</code> that will appear on the
	 * right of a horizontally-split pane, or at the bottom of a vertically-split
	 * pane
	 * @exception IllegalArgumentException if <code>orientation</code> is not one
	 * of: <code>HORIZONTAL_SPLIT</code> or <code>VERTICAL_SPLIT</code>
	 */
	public OSplitPane(int orientation,
							boolean continuousLayout,
							Component leftComponent,
							Component rightComponent)
	{
		super(orientation, continuousLayout, leftComponent, rightComponent);
	}

	/**
	 * Sets the component to the left (or above) the divider. If the component
	 * orientation is Right To Left, then the component will be added to the
	 * right side instead of the left side. <code>getLeftComponent()</code> will
	 * however returns the given component.
	 *
	 * @param component The <code>Component</code> to display in that position
	 */
	public void setLeftComponent(Component component)
	{
		ComponentOrientation orientation = ComponentOrientation.getOrientation(Locale.getDefault());

		if (orientation.isLeftToRight())
			super.setLeftComponent(component);
		else
			super.setRightComponent(component);
	}

	/**
	 * Sets the component to the right (or below) the divider. If the component
	 * orientation is Right To Left, then the component will be added to the
	 * left side instead of the right side. <code>getRightComponent()</code> will
	 * however returns the given component.
	 *
	 * @param component The <code>Component</code> to display in that position
	 * @beaninfo
	 *    preferred: true
	 *  description: The component to the right (or below) the divider.
	 */
	public void setRightComponent(Component component)
	{
		ComponentOrientation orientation = ComponentOrientation.getOrientation(Locale.getDefault());

		if (orientation.isLeftToRight())
			super.setRightComponent(component);
		else
			super.setLeftComponent(component);
	}
}
