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
import java.awt.LayoutManager;

/**
 * This <code>Pane</code> makes sure its children are also enable or disabled
 * when this pane is. It should be used for sub-pane where enabling and
 * disabling components can be done through
 * {@link javax.swing.JComponent#setEnabled(boolean)}; otherwise a regular
 * <code>JPanel</code> along with {@link ComponentEnabler} should be used.
 * 
 * @version 10.1.3
 * @author Pascal Filion
 */
public class Pane extends AccessibleTitledPanel
{
	/**
	 * Creates a new <code>Pane</code>.
	 */
	public Pane()
	{
		super();
	}

	/**
	 * Creates a new <code>Pane</code>.
	 *
	 * @param manager The <code>LayoutManager</code> responsible to properly
	 * align the children of this <code>Pane</code>
	 */
	public Pane(LayoutManager manager)
	{
		super(manager);
	}

	/**
    * Sets whether or not this component is enabled. Disabling this pane will
    * also disable its children through {@link #updateEnableStateOfChildren(boolean)}.
    *
    * @param enabled <code>true<code> if this component and its children should
    * be enabled, <code>false<code> otherwise
    */
	public final void setEnabled(boolean enabled)
	{
		if (isEnabled() == enabled)
			return;

		super.setEnabled(enabled);
		updateEnableStateOfChildren(enabled);
	}

	/**
	 * Updates the enable state of the children of this pane.
	 *
    * @param enabled <code>true<code> if this pane's children should be enabled,
    * <code>false<code> otherwise
	 */
	protected void updateEnableStateOfChildren(boolean enabled)
	{
		for (int index = getComponentCount(); --index >= 0;)
			getComponent(index).setEnabled(enabled);
	}
}
