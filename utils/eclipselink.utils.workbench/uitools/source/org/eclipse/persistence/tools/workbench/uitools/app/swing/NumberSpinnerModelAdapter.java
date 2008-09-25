package org.eclipse.persistence.tools.workbench.uitools.app.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * This javax.swing.SpinnerNumberModel can be used to keep a ChangeListener
 * (e.g. a JSpinner) in synch with a PropertyValueModel that holds a number.
 * 
 * This class must be a sub-class of SpinnerNumberModel because of some
 * crappy jdk code....  ~bjv
 * @see javax.swing.JSpinner#createEditor(javax.swing.SpinnerModel)
 * 
 * If this class needs to be modified, it would behoove us to review the
 * other, similar classes:
 * @see DateSpinnerModelAdapter
 * @see ListSpinnerModelAdapter
 */
public class NumberSpinnerModelAdapter extends SpinnerNumberModel {

	/**
	 * The default spinner value; used when the
	 * underlying model number value is null.
	 */
	private Number defaultValue;

	/** A value model on the underlying number. */
	protected PropertyValueModel numberHolder;

	/**
	 * A listener that allows us to synchronize with
	 * changes made to the underlying number.
	 */
	private PropertyChangeListener numberChangeListener;


	// ********** constructors **********

	/**
	 * Constructor - the number holder is required.
	 * The default spinner value is zero.
	 * The step size is one.
	 */
	public NumberSpinnerModelAdapter(PropertyValueModel numberHolder) {
		this(numberHolder, 0);
	}

	/**
	 * Constructor - the number holder is required.
	 * The step size is one.
	 */
	public NumberSpinnerModelAdapter(PropertyValueModel numberHolder, int defaultValue) {
		this(numberHolder, null, null, new Integer(1), new Integer(defaultValue));
	}

	/**
	 * Constructor - the number holder is required.
	 * Use the minimum value as the default spinner value.
	 */
	public NumberSpinnerModelAdapter(PropertyValueModel numberHolder, int minimum, int maximum, int stepSize) {
		this(numberHolder, minimum, maximum, stepSize, minimum);
	}

	/**
	 * Constructor - the number holder is required.
	 */
	public NumberSpinnerModelAdapter(PropertyValueModel numberHolder, int minimum, int maximum, int stepSize, int defaultValue) {
		this(numberHolder, new Integer(minimum), new Integer(maximum), new Integer(stepSize), new Integer(defaultValue));
	}

	/**
	 * Constructor - the number holder is required.
	 * Use the minimum value as the default spinner value.
	 */
	public NumberSpinnerModelAdapter(PropertyValueModel numberHolder, double value, double minimum, double maximum, double stepSize) {
		this(numberHolder, value, minimum, maximum, stepSize, minimum);
	}

	/**
	 * Constructor - the number holder is required.
	 */
	public NumberSpinnerModelAdapter(PropertyValueModel numberHolder, double value, double minimum, double maximum, double stepSize, double defaultValue) {
		this(numberHolder, new Double(minimum), new Double(maximum), new Double(stepSize), new Double(defaultValue));
	}

	/**
	 * Constructor - the number holder is required.
	 */
	public NumberSpinnerModelAdapter(PropertyValueModel numberHolder, Comparable minimum, Comparable maximum, Number stepSize, Number defaultValue) {
		super(numberHolder.getValue() == null ? defaultValue : (Number) numberHolder.getValue(), minimum, maximum, stepSize);
		this.numberHolder = numberHolder;
		this.numberChangeListener = this.buildNumberChangeListener();
		// postpone listening to the underlying number
		// until we have listeners ourselves...
		this.defaultValue = defaultValue;
	}


	// ********** initialization **********

	private PropertyChangeListener buildNumberChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				NumberSpinnerModelAdapter.this.synchronize(e.getNewValue());
			}
			public String toString() {
				return "number listener";
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
			super.setValue(this.spinnerValueOf(this.numberHolder.getValue()));
		}
		return super.getValue();
	}

	protected final Number getSuperValue() {
		return super.getNumber();
	}

	/**
	 * Extend to update the underlying number directly.
	 * The resulting event will be ignored: @see synchronizeDelegate(Object).
	 * @see javax.swing.SpinnerModel#setValue(java.lang.Object)
	 */
	public void setValue(Object value) {
		super.setValue(value);
		this.numberHolder.setValue(value);
	}

	/**
	 * Extend to start listening to the underlying number if necessary.
	 * @see javax.swing.SpinnerModel#addChangeListener(javax.swing.event.ChangeListener)
	 */
	public void addChangeListener(ChangeListener listener) {
		if (this.getChangeListeners().length == 0) {
			this.numberHolder.addPropertyChangeListener(ValueModel.VALUE, this.numberChangeListener);
			this.synchronize(this.numberHolder.getValue());
		}
		super.addChangeListener(listener);
	}

	/**
	 * Extend to stop listening to the underlying number if appropriate.
	 * @see javax.swing.SpinnerModel#removeChangeListener(javax.swing.event.ChangeListener)
	 */
	public void removeChangeListener(ChangeListener listener) {
		super.removeChangeListener(listener);
		if (this.getChangeListeners().length == 0) {
			this.numberHolder.removePropertyChangeListener(ValueModel.VALUE, this.numberChangeListener);
		}
	}


	// ********** queries **********

	protected Number getDefaultValue() {
		return this.defaultValue;
	}

	protected void setDefaultValue(Number defaultValue) {
		this.defaultValue = defaultValue;
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
	protected void synchronize(Object value) {
		Object newValue = this.spinnerValueOf(value);
		// check to see whether the date has already been synchronized
		// (via #setValue())
		if ( ! this.getValue().equals(newValue)) {
			this.setValue(newValue);
		}
	}


	// ********** standard methods **********

	public String toString() {
		return StringTools.buildToStringFor(this, this.numberHolder);
	}

}
