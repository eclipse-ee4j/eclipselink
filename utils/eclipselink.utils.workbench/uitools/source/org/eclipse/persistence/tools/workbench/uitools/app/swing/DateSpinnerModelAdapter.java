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
package org.eclipse.persistence.tools.workbench.uitools.app.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeListener;

import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This javax.swing.SpinnerDateModel can be used to keep a ChangeListener
 * (e.g. a JSpinner) in synch with a PropertyValueModel that holds a date.
 * 
 * This class must be a sub-class of SpinnerDateModel because of some
 * crappy jdk code....  ~bjv
 * @see javax.swing.JSpinner#createEditor(javax.swing.SpinnerModel)
 * 
 * If this class needs to be modified, it would behoove us to review the
 * other, similar classes:
 * @see ListSpinnerModelAdapter
 * @see NumberSpinnerModelAdapter
 */
public class DateSpinnerModelAdapter extends SpinnerDateModel {

	/**
	 * The default spinner value; used when the underlying model date value is null.
	 * The default is the current date.
	 */
	private Date defaultValue;

	/** A value model on the underlying date. */
	private PropertyValueModel dateHolder;

	/** A listener that allows us to synchronize with changes made to the underlying date. */
	private PropertyChangeListener dateChangeListener;


	// ********** constructors **********

	/**
	 * Constructor - the date holder is required.
	 * The default spinner value is the current date.
	 */
	public DateSpinnerModelAdapter(PropertyValueModel dateHolder) {
		this(dateHolder, new Date());
	}

	/**
	 * Constructor - the date holder and default value are required.
	 */
	public DateSpinnerModelAdapter(PropertyValueModel dateHolder, Date defaultValue) {
		this(dateHolder, null, null, Calendar.DAY_OF_MONTH, defaultValue);
	}

	/**
	 * Constructor - the date holder is required.
	 * The default spinner value is the current date.
	 */
	public DateSpinnerModelAdapter(PropertyValueModel dateHolder, Comparable start, Comparable end, int calendarField) {
		this(dateHolder, start, end, calendarField, new Date());
	}

	/**
	 * Constructor - the date holder is required.
	 */
	public DateSpinnerModelAdapter(PropertyValueModel dateHolder, Comparable start, Comparable end, int calendarField, Date defaultValue) {
		super(dateHolder.getValue() == null ? defaultValue : (Date) dateHolder.getValue(), start, end, calendarField);
		this.dateHolder = dateHolder;
		this.dateChangeListener = this.buildDateChangeListener();
		// postpone listening to the underlying date
		// until we have listeners ourselves...
		this.defaultValue = defaultValue;
	}


	// ********** initialization **********

	private PropertyChangeListener buildDateChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				DateSpinnerModelAdapter.this.synchronize(e.getNewValue());
			}
			public String toString() {
				return "date listener";
			}
		};
	}


	// ********** SpinnerModel implementation **********

	/**
	 * Extend to check whether this method is being called before we 
	 * have any listeners.
	 * This is necessary because some crappy jdk code gets the value
	 * from the model *before* listening to the model.  ~bjv
	 * @see javax.swing.JSpinner.DefaultEditor(javax.swing.JSpinner)
	 * @see javax.swing.SpinnerModel#getValue()
	 */
	public Object getValue() {
		if (this.getChangeListeners().length == 0) {
			// sorry about this "lateral" call to super  ~bjv
			super.setValue(this.spinnerValueOf(this.dateHolder.getValue()));
		}
		return super.getValue();
	}

	/**
	 * Extend to update the underlying date directly.
	 * The resulting event will be ignored: @see synchronize(Object).
	 * @see javax.swing.SpinnerModel#setValue(java.lang.Object)
	 */
	public void setValue(Object value) {
		super.setValue(value);
		this.dateHolder.setValue(value);
	}

	/**
	 * Extend to start listening to the underlying date if necessary.
	 * @see javax.swing.SpinnerModel#addChangeListener(javax.swing.event.ChangeListener)
	 */
	public void addChangeListener(ChangeListener listener) {
		if (this.getChangeListeners().length == 0) {
			this.dateHolder.addPropertyChangeListener(ValueModel.VALUE, this.dateChangeListener);
			this.synchronize(this.dateHolder.getValue());
		}
		super.addChangeListener(listener);
	}

	/**
	 * Extend to stop listening to the underlying date if appropriate.
	 * @see javax.swing.SpinnerModel#removeChangeListener(javax.swing.event.ChangeListener)
	 */
	public void removeChangeListener(ChangeListener listener) {
		super.removeChangeListener(listener);
		if (this.getChangeListeners().length == 0) {
			this.dateHolder.removePropertyChangeListener(ValueModel.VALUE, this.dateChangeListener);
		}
	}


	// ********** queries **********

	protected Date getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * Convert to a non-null value.
	 */
	protected Object spinnerValueOf(Object value) {
		return (value == null) ? this.getDefaultValue() : value;
	}


	// ********** behavior **********

	/**
	 * Set the spinner value if it has changed.
	 */
	void synchronize(Object value) {
		Object newValue = this.spinnerValueOf(value);
		// check to see whether the spinner date has already been synchronized
		// (via #setValue())
		if ( ! this.getValue().equals(newValue)) {
			this.setValue(newValue);
		}
	}


	// ********** standard methods **********

	public String toString() {
		return StringTools.buildToStringFor(this, this.dateHolder);
	}

}
