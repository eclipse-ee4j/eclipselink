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
package org.eclipse.persistence.tools.workbench.uitools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;


/**
 * Adapt a property value model to be a displayable. Any time the value holder's
 * value changes, listeners are notified that the displayable's display string and
 * icon have changed. This class is not so useful if the display string or icon of
 * a particular value can change over time - in that case, you will need to listen
 * to the aspects of the value itself.
 * 
 * Either construct this adapter with a DisplayableAdapter
 * or override either or both of the following methods:
 *     #displayString(Object) - return the value's display string
 *     #icon(Object) - return the value's icon
 */
public class PropertyValueModelDisplayableAdapter
	extends AbstractModel
	implements Displayable 
{
	protected PropertyValueModel valueHolder;
	protected PropertyChangeListener valueChangeListener;

	private DisplayableAdapter adapter;


	// ********** constructors/initialization **********

	/**
	 * Construct an adapter for the specified property value model.
	 * This constructor is typically used when overriding the methods
	 * #displayString(Object) and #icon(Object).
	 */
	public PropertyValueModelDisplayableAdapter(PropertyValueModel valueHolder) {
		this(valueHolder, DisplayableAdapter.DEFAULT_INSTANCE);
	}
	
	/**
	 * Construct an adapter for the specified property value model.
	 * The specified displayable adapter will be used to derive the
	 * value's display string and icon.
	 */
	public PropertyValueModelDisplayableAdapter(PropertyValueModel valueHolder, DisplayableAdapter adapter) {
		super();
		this.valueHolder = valueHolder;
		this.adapter = adapter;
	}

	protected void initialize() {
		super.initialize();
		this.valueChangeListener = this.buildValueChangeListener();
	}

	protected PropertyChangeListener buildValueChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				PropertyValueModelDisplayableAdapter.this.valueChanged(e);
			}
			public String toString() {
				return "value change listener";
			}
		};
	}


	// ********** AbstractModel extensions **********

	/**
	 * Extend to start listening to the nested model if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		if (this.hasNoRelevantListeners()) {
			this.valueHolder.addPropertyChangeListener(ValueModel.VALUE, this.valueChangeListener);
		}
		super.addPropertyChangeListener(listener);
	}
	
	/**
	 * Extend to start listening to the nested model if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#addPropertyChangeListener(String, java.beans.PropertyChangeListener)
	 */
	public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (this.propertyIsRelevant(propertyName) && this.hasNoRelevantListeners()) {
			this.valueHolder.addPropertyChangeListener(ValueModel.VALUE, this.valueChangeListener);
		}
		super.addPropertyChangeListener(propertyName, listener);
	}
	
	/**
	 * Extend to stop listening to the nested model if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);
		if (this.hasNoRelevantListeners()) {
			this.valueHolder.removePropertyChangeListener(ValueModel.VALUE, this.valueChangeListener);
		}
	}
	
	/**
	 * Extend to stop listening to the nested model if necessary.
	 * @see org.eclipse.persistence.tools.workbench.utility.Model#removePropertyChangeListener(String, java.beans.PropertyChangeListener)
	 */
	public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		super.removePropertyChangeListener(propertyName, listener);
		if (this.propertyIsRelevant(propertyName) && this.hasNoRelevantListeners()) {
			this.valueHolder.removePropertyChangeListener(ValueModel.VALUE, this.valueChangeListener);
		}
	}

	/**
	 * Return whether the specified property is relevant.
	 */
	protected boolean propertyIsRelevant(String propertyName) {
		return (propertyName == DISPLAY_STRING_PROPERTY) ||
				(propertyName == ICON_PROPERTY);
	}

	/**
	 * Return whether we have no listeners for either
	 * DISPLAY_STRING_PROPERTY or ICON_PROPERTY.
	 */
	protected boolean hasNoRelevantListeners() {
		return this.hasNoPropertyChangeListeners(DISPLAY_STRING_PROPERTY)
				&& this.hasNoPropertyChangeListeners(ICON_PROPERTY);
	}


	// ********** Comparable implementation **********

	/**
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(Object o) {
		return DEFAULT_COMPARATOR.compare(this, o);
	}


	// ********** Displayable implementation **********

	/**
	 * @see Displayable#displayString()
	 */
	public String displayString() {
		return this.displayString(this.valueHolder.getValue());
	}

	/**
	 * @see Displayable#icon()
	 */
	public Icon icon() {
		return this.icon(this.valueHolder.getValue());
	}


	// ********** behavior **********

	/**
	 * The value changed, notifier listeners that the display string
	 * and icon have (probably) changed.
	 */
	protected void valueChanged(PropertyChangeEvent e) {
		this.firePropertyChanged(DISPLAY_STRING_PROPERTY, this.displayString(e.getOldValue()), this.displayString(e.getNewValue()));
		this.firePropertyChanged(ICON_PROPERTY, this.icon(e.getOldValue()), this.icon(e.getNewValue()));
	}

	/**
	 * Return the specified object's display string.
	 */
	protected String displayString(Object object) {
		return this.adapter.displayString(object);
	}

	/**
	 * Return the specified object's icon.
	 */
	public Icon icon(Object object) {
		return this.adapter.icon(object);
	}

}
