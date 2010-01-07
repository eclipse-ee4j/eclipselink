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
package org.eclipse.persistence.tools.workbench.uitools.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.persistence.tools.workbench.utility.Model;


/**
 * This AspectAdapter provides basic PropertyChange support.
 * This allows us to convert a set of one or more properties into
 * a single property, VALUE.
 * 
 * The typical subclass will override the following methods:
 * #getValueFromSubject()
 *     at the very minimum, override this method to return the value of the
 *     subject's property (or "virtual" property); it does not need to be
 *     overridden if #buildValue() is overridden and its behavior changed
 * #setValueOnSubject(Object)
 *     override this method if the client code needs to *set* the value of
 *     the subject's property; oftentimes, though, the client code (e.g. UI)
 *     will need only to *get* the value; it does not need to be
 *     overridden if #setValue(Object) is overridden and its behavior changed
 * #buildValue()
 *     override this method only if returning a null value when the subject is null
 *     is unacceptable
 * #setValue(Object)
 *     override this method only if something must be done when the subject
 *     is null (e.g. throw an exception)
 */
public abstract class PropertyAspectAdapter 
	extends AspectAdapter
	implements PropertyValueModel
{
	/**
	 * Cache the current value of the aspect so we
	 * can pass an "old value" when we fire a property change event.
	 * We need this because the value may be calculated and may
	 * not be in the property change event fired by the subject,
	 * especially when dealing with multiple aspects.
	 */
	protected Object value;

	/** The name of the subject's properties that we use for the value. */
	protected String[] propertyNames;

	/** A listener that listens to the appropriate properties of the subject. */
	protected PropertyChangeListener propertyChangeListener;


	// ********** constructors **********

	/**
	 * Construct a PropertyAspectAdapter for the specified subject
	 * and property.
	 */
	protected PropertyAspectAdapter(String propertyName, Model subject) {
		this(new String[] {propertyName}, subject);
	}

	/**
	 * Construct a PropertyAspectAdapter for the specified subject
	 * and properties.
	 */
	protected PropertyAspectAdapter(String[] propertyNames, Model subject) {
		super(subject);
		this.propertyNames = propertyNames;
	}

	/**
	 * Construct a PropertyAspectAdapter for the specified subject holder
	 * and property.
	 */
	protected PropertyAspectAdapter(ValueModel subjectHolder, String propertyName) {
		this(subjectHolder, new String[] {propertyName});
	}

	/**
	 * Construct a PropertyAspectAdapter for the specified subject holder
	 * and properties.
	 */
	protected PropertyAspectAdapter(ValueModel subjectHolder, String propertyName1, String propertyName2) {
		this(subjectHolder, new String[] {propertyName1, propertyName2});
	}

	/**
	 * Construct a PropertyAspectAdapter for the specified subject holder
	 * and properties.
	 */
	protected PropertyAspectAdapter(ValueModel subjectHolder, String propertyName1, String propertyName2, String propertyName3) {
		this(subjectHolder, new String[] {propertyName1, propertyName2, propertyName3});
	}

	/**
	 * Construct a PropertyAspectAdapter for the specified subject holder
	 * and properties.
	 */
	protected PropertyAspectAdapter(ValueModel subjectHolder, String[] propertyNames) {
		super(subjectHolder);
		this.propertyNames = propertyNames;
	}

	/**
	 * Construct a PropertyAspectAdapter for the specified subject holder
	 * and properties.
	 */
	protected PropertyAspectAdapter(ValueModel subjectHolder, Collection propertyNames) {
		this(subjectHolder, (String[]) propertyNames.toArray(new String[propertyNames.size()]));
	}

	/**
	 * Construct a PropertyAspectAdapter for an "unchanging" property in
	 * the specified subject. This is useful for a property aspect that does not
	 * change for a particular subject; but the subject will change, resulting in
	 * a new property.
	 */
	protected PropertyAspectAdapter(ValueModel subjectHolder) {
		this(subjectHolder, new String[0]);
	}


	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		// our value is null when we are not listening to the subject
		this.value = null;
		this.propertyChangeListener = this.buildPropertyChangeListener();
	}

	/**
	 * The subject's property has changed, notify the listeners.
	 */
	protected PropertyChangeListener buildPropertyChangeListener() {
		// transform the subject's property change events into VALUE property change events
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				PropertyAspectAdapter.this.propertyChanged();
			}
			public String toString() {
				return "property change listener: " + Arrays.asList(PropertyAspectAdapter.this.propertyNames);
			}
		};
	}


	// ********** ValueModel implementation **********

	/**
	 * Return the value of the subject's property.
	 * @see ValueModel#getValue()
	 */
	public final Object getValue() {
		return this.value;
	}


	// ********** PropertyValueModel implementation **********

	/**
	 * Set the value of the subject's property.
	 * @see PropertyValueModel#setValue(Object)
	 */
	public void setValue(Object value) {
		if (this.subject != null) {
			this.setValueOnSubject(value);
		}
	}

	/**
	 * Set the value of the subject's property.
	 * At this point we can be sure that the subject is not null.
	 * @see PropertyValueModel#setValue(Object)
	 */
	protected void setValueOnSubject(Object value) {
		throw new UnsupportedOperationException();
	}


	// ********** AspectAdapter implementation **********

	/**
	 * @see AspectAdapter#hasListeners()
	 */
	protected boolean hasListeners() {
		return this.hasAnyPropertyChangeListeners(VALUE);
	}

	/**
	 * @see AspectAdapter#fireAspectChange(Object, Object)
	 */
	protected void fireAspectChange(Object oldValue, Object newValue) {
		this.firePropertyChanged(VALUE, oldValue, newValue);
	}

	/**
	 * @see AspectAdapter#engageSubject()
	 */
	protected void engageSubject() {
		super.engageSubject();
		// synch our value *after* we start listening to the subject,
		// since its value might change when a listener is added
		this.value = this.buildValue();
	}

	/**
	 * @see AspectAdapter#engageNonNullSubject()
	 */
	protected void engageNonNullSubject() {
		for (int i = this.propertyNames.length; i-- > 0; ) {
			((Model) this.subject).addPropertyChangeListener(this.propertyNames[i], this.propertyChangeListener);
		}
	}

	/**
	 * @see AspectAdapter#disengageSubject()
	 */
	protected void disengageSubject() {
		super.disengageSubject();
		// clear out our value when we are not listening to the subject
		this.value = null;
	}

	/**
	 * @see AspectAdapter#disengageNonNullSubject()
	 */
	protected void disengageNonNullSubject() {
		for (int i = this.propertyNames.length; i-- > 0; ) {
			((Model) this.subject).removePropertyChangeListener(this.propertyNames[i], this.propertyChangeListener);
		}
	}


	// ********** AbstractModel implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#toString(StringBuffer)
	 */
	public void toString(StringBuffer sb) {
		for (int i = 0; i < this.propertyNames.length; i++) {
			if (i != 0) {
				sb.append(", ");
			}
			sb.append(this.propertyNames[i]);
		}
	}


	// ********** queries **********

	/**
	 * Return the aspect's value.
	 * At this point the subject may be null.
	 */
	protected Object buildValue() {
		if (this.subject == null) {
			return null;
		}
		return this.getValueFromSubject();
	}

	/**
	 * Return the value of the subject's property.
	 * At this point we can be sure that the subject is not null.
	 * @see ValueModel#getValue()
	 */
	protected Object getValueFromSubject() {
		throw new UnsupportedOperationException();
	}


	// ********** behavior **********

	protected void propertyChanged() {
		Object old = this.value;
		this.value = this.buildValue();
		this.fireAspectChange(old, this.value);
	}

}
