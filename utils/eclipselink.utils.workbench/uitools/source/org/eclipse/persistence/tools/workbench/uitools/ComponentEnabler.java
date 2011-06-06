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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * This <code>ComponentEnabler</code> keeps the "enabled" state of
 * a collection of components in synch with the provided boolean holder.
 * 
 * @version 10.1.3
 * @author Pascal Filion
 */
public class ComponentEnabler
{
	/**
	 * A listener that allows us to synchronize the components with
	 * changes made to the underlying boolean model.
	 */
	private PropertyChangeListener booleanChangeListener;
	
	/**
	 * A value model on the underlying boolean model
	 */
	ValueModel booleanHolder;

	/**
	 * The collection of components whose "enabled" state is kept
	 * in sync with the boolean holder's value.
	 */
	private Collection components;

	/**
	 * The default setting for the "enabled" state; for when the underlying model is
	 * <code>null</code>. The default [default value] is <code>false<code> (i.e.
	 * the components are disabled).
	 */
	private boolean defaultValue;
	
	/**
	 * Creates a new <code>ComponentEnabler</code>.
	 */
	private ComponentEnabler()
	{
		super();
		initialize();
	}

	/**
	 * Creates a new <code>ComponentEnabler</code> with a default value of
	 * <code>false</code> (i.e. disabled).
	 * 
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param component The component whose "enabled" state is
	 * kept in sync with the boolean holder's value
	 */
	public ComponentEnabler(ValueModel booleanHolder, Component component)
	{
		this(booleanHolder, component, false);
	}

	/**
	 * Creates a new <code>ComponentEnabler</code>.
	 * 
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param component The component whose "enabled" state is
	 * kept in sync with the boolean holder's value
	 * @param defaultValue The value to use when the underlying model is
	 * <code>null</code>
	 */
	public ComponentEnabler(ValueModel booleanHolder, 
							Component component, 
							boolean defaultValue)
	{
		this(booleanHolder, new Component[] {component}, false);
	}

	/**
	 * Creates a new <code>ComponentEnabler</code> with a default value of
	 * <code>false</code> (i.e. disabled).
	 * 
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param components The collection of components whose "enabled" state is
	 * kept in sync with the boolean holder's value
	 */
	public ComponentEnabler(ValueModel booleanHolder,
									Collection components)
	{
		this(booleanHolder, components, false);
	}

	/**
	 * Creates a new <code>ComponentEnabler</code>.
	 * 
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param components The collection of components whose "enabled" state is
	 * kept in sync with the boolean holder's value
	 * @param defaultValue The value to use when the underlying model is
	 * <code>null</code>
	 */
	public ComponentEnabler(ValueModel booleanHolder,
									Collection components,
									boolean defaultValue)
	{
		this();
		initialize(booleanHolder, components, defaultValue);
	}

	/**
	 * Creates a new <code>ComponentEnabler</code> with a default value of
	 * <code>false</code> (i.e. disabled).
	 * 
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param components The collection of components whose "enabled" state is
	 * kept in sync with the boolean holder's value
	 */
	public ComponentEnabler(ValueModel booleanHolder,
									Component[] components)
	{
		this(booleanHolder, CollectionTools.collection(components), false);
	}

	/**
	 * Creates a new <code>ComponentEnabler</code>.
	 * 
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param components The collection of components whose "enabled" state is
	 * kept in sync with the boolean holder's value
	 * @param defaultValue The value to use when the underlying model is
	 * <code>null</code>
	 */
	public ComponentEnabler(ValueModel booleanHolder,
									Component[] components,
									boolean defaultValue)
	{
		this();
		initialize(booleanHolder, CollectionTools.collection(components), defaultValue);
	}

	/**
	 * Creates a new <code>ComponentEnabler</code> with a default value of
	 * <code>false</code> (i.e. disabled).
	 * 
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param components An iterator on the collection of components whose
	 * "enabled" state is kept in sync with the boolean holder's value
	 */
	public ComponentEnabler(ValueModel booleanHolder,
									Iterator components)
	{
		this(booleanHolder, CollectionTools.collection(components), false);
	}

	/**
	 * Creates a new <code>ComponentEnabler</code>.
	 * 
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param components An iterator on the collection of components whose
	 * "enabled" state is kept in sync with the boolean holder's value
	 * @param defaultValue The value to use when the underlying model is
	 * <code>null</code>
	 */
	public ComponentEnabler(ValueModel booleanHolder,
									Iterator components,
									boolean defaultValue)
	{
		this();
		initialize(booleanHolder, CollectionTools.collection(components), defaultValue);
	}

	/**
	 * Returns the boolean primitive of the given <code>Boolean</code> value but
	 * also checks for <code>null</code>, if that is the case, then
	 * {@link #defaultValue} is returned.
	 * 
	 * @param value The <code>Boolean</code> value to be returned as a primitive
	 * @return The primitive of the given value or {@link #defaultValue}when the
	 * value is <code>null</code>
	 */
	protected boolean booleanValue(Boolean value)
	{
		return (value == null) ? this.defaultValue : value.booleanValue();
	}

	/**
	 * Creates a listener for the boolean holder.
	 *
	 * @return A new <code>PropertyChangeListener</code>
	 */
	protected PropertyChangeListener buildBooleanChangeListener()
	{
		return new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				updateEnableState(booleanValue((Boolean) ComponentEnabler.this.booleanHolder.getValue()));
			}
			public String toString() {
				return "boolean change listener";
			}
		};
	}

	/**
	 * Returns an <code>Iterator</code> over the collection of <code>Component</code>s.
	 *
	 * @return The iteration of components
	 */
	protected Iterator components()
	{
		return this.components.iterator();
	}

	/**
	 * Initializes this <code>ComponentEnabler</code> by building the
	 * appropriate listeners.
	 */
	protected void initialize()
	{
		this.booleanChangeListener = buildBooleanChangeListener();
	}

	/**
	 * Initializes this <code>ComponentEnabler</code> with the given state.
	 * 
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param components An iterator on the collection of components whose
	 * "enabled" state is kept in sync with the boolean holder's value
	 * @param defaultValue The value to use when the underlying model is
	 * <code>null</code>
	 */
	protected void initialize(ValueModel booleanVM,
									  Collection comps,
									  boolean defaultVal)
	{
		if ((booleanVM == null) || (comps == null))
			throw new NullPointerException();

		this.components = comps;
		this.defaultValue = defaultVal;
		this.booleanHolder = booleanVM;

		booleanVM.addPropertyChangeListener(ValueModel.VALUE, this.booleanChangeListener);
		updateEnableState(booleanValue((Boolean) booleanVM.getValue()));
	}

	/**
	 * Updates the enable state of the <code>Component</code>s that are given
	 * by the <code>CollectionValueModel</code>.
	 * 
	 * @param enabledState The new enable state the components need to have
	 */
	protected void updateEnableState(boolean enabled)
	{
		for (Iterator iter = components(); iter.hasNext();)
		{
			((Component) iter.next()).setEnabled(enabled);
		}
	}
}
