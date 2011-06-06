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
package org.eclipse.persistence.tools.workbench.uitools;

// JDK
import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


/**
 * This <code>ComponentVisibilityEnabler</code> keeps the "visible" state of a
 * collection of components in synch with the provided boolean holder.
 * 
 * @version 10.1.3
 * @author Pascal Filion
 */
public class ComponentVisibilityEnabler extends ComponentEnabler
{
	/**
	 * Creates a new <code>ComponentEnabler</code> with a default value of
	 * <code>true</code> (i.e. visible).
	 * 
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param component A component whose "visible" state is
	 * kept in sync with the boolean holder's value
	 */
	public ComponentVisibilityEnabler(ValueModel booleanHolder, 
									  Component component) 
	{
		super(booleanHolder, component, true);
	}

	/**
	 * Creates a new <code>ComponentVisibilityEnabler</code>.
	 * 
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param component The component whose "visible" state is
	 * kept in sync with the boolean holder's value
	 * @param defaultValue The value to use when the underlying model is
	 * <code>null</code>
	 */
	public ComponentVisibilityEnabler(ValueModel booleanHolder,
									  Component component,
									  boolean defaultValue)
	{
		super(booleanHolder, component, defaultValue);
	}

	/**
	 * Creates a new <code>ComponentEnabler</code> with a default value of
	 * <code>true</code> (i.e. visible).
	 * 
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param components The collection of components whose "visible" state is
	 * kept in sync with the boolean holder's value
	 */
	public ComponentVisibilityEnabler(ValueModel booleanHolder,
												 Collection components)
	{
		super(booleanHolder, components, true);
	}

	/**
	 * Creates a new <code>ComponentVisibilityEnabler</code>.
	 * 
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param components The collection of components whose "visible" state is
	 * kept in sync with the boolean holder's value
	 * @param defaultValue The value to use when the underlying model is
	 * <code>null</code>
	 */
	public ComponentVisibilityEnabler(ValueModel booleanHolder,
												 Collection components,
												 boolean defaultValue)
	{
		super(booleanHolder, components, defaultValue);
	}

	/**
	 * Creates a new <code>ComponentVisibilityEnabler</code> with a default
	 * value of <code>false</code> (i.e. visible).
	 * 
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param components The collection of components whose "visible" state is
	 * kept in sync with the boolean holder's value
	 */
	public ComponentVisibilityEnabler(ValueModel booleanHolder,
												 Component[] components)
	{
		super(booleanHolder, components, true);
	}

	/**
	 * Creates a new <code>ComponentVisibilityEnabler</code>.
	 * 
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param components The collection of components whose "visible" state is
	 * kept in sync with the boolean holder's value
	 * @param defaultValue The value to use when the underlying model is
	 * <code>null</code>
	 */
	public ComponentVisibilityEnabler(ValueModel booleanHolder,
												 Component[] components,
												 boolean defaultValue)
	{
		super(booleanHolder, components, defaultValue);
	}

	/**
	 * Creates a new <code>ComponentVisibilityEnabler</code> with a default
	 * value of <code>true</code> (i.e. visible).
	 * 
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param components An iterator on the collection of components whose
	 * "visible" state is kept in sync with the boolean holder's value
	 */
	public ComponentVisibilityEnabler(ValueModel booleanHolder,
												 Iterator components)
	{
		super(booleanHolder, components, true);
	}

	/**
	 * Creates a new <code>ComponentVisibilityEnabler</code>.
	 * 
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param components An iterator on the collection of components whose
	 * "visible" state is kept in sync with the boolean holder's value
	 * @param defaultValue The value to use when the underlying model is
	 * <code>null</code>
	 */
	public ComponentVisibilityEnabler(ValueModel booleanHolder,
												 Iterator components,
												 boolean defaultValue)
	{
		super(booleanHolder, components, defaultValue);
	}

	/**
	 * Updates the visible state of the <code>Component</code> s that are given
	 * by the <code>CollectionValueModel</code>.
	 * 
	 * @param visible The new visible state the components need to have
	 */
	protected void updateEnableState(boolean visible)
	{
		for (Iterator iter = components(); iter.hasNext();)
		{
			((Component) iter.next()).setVisible(visible);
		}
	}
}
