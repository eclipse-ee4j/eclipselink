/*
 * Copyright (c) 2007, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.uitools.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.Transformer;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport;

/**
 * A <code>TransformationValueModel</code> wraps another
 * <code>ValueModel</code> and uses a <code>Transformer</code> to transform
 * the wrapped value before it is returned by <code>getValue()</code>. As an
 * alternative to building a <code>Transformer</code>, a subclass of
 * <code>TransformationValueModel</code> can override the
 * <code>transform(Object)</code> method.
 */
@SuppressWarnings("nls")
public class TransformationValueModel
	extends AbstractModel
	implements ValueModel
{
	private Transformer transformer;

	/** The wrapped  value model. */
	protected ValueModel valueHolder;

	/** A listener that allows us to synch with changes to the wrapped value holder. */
	protected PropertyChangeListener valueChangeListener;

	// ********** constructors **********

	/**
	 * Construct a value model with the specified nested value model and a
	 * transformer that performs no transformations at all. Use this constructor
	 * if you want to override the <code>transform(Object)</code> and method
	 * instead of building a <code>Transformer</code>.
	 */
	public TransformationValueModel(ValueModel valueHolder) {
		this(valueHolder, NullTransformer.instance());
	}

	/**
	 * Construct an value model with the specified nested value model and
	 * transformer.
	 */
	@SuppressWarnings("unchecked")
	public TransformationValueModel(ValueModel valueHolder,
	                                Transformer transformer) {
		super();
		this.valueHolder = (ValueModel) valueHolder;
		this.transformer = (Transformer) transformer;
	}


	// ********** initialization **********

	@Override
	protected void initialize() {
		super.initialize();
		this.valueChangeListener = this.buildValueChangeListener();
	}

	/**
	 * @see oracle.toplink.workbench.utility.AbstractModel#buildDefaultChangeSupport()
	 */
	@Override
	protected ChangeSupport buildDefaultChangeSupport() {
		return new ValueModelChangeSupport(this);
	}

	protected PropertyChangeListener buildValueChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				TransformationValueModel.this.valueChanged(e);
			}
			@Override
			public String toString() {
				return "value change listener";
			}
		};
	}

	// ********** extend change support **********

	/**
	 * Extend to start listening to the nested model if necessary.
	 * @see Model#addPropertyChangeListener(PropertyChangeListener)
	 */
	@Override
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		if (this.hasNoPropertyChangeListeners(VALUE)) {
			this.engageValueHolder();
		}
		super.addPropertyChangeListener(listener);
	}

	/**
	 * Extend to start listening to the nested model if necessary.
	 * @see Model#addPropertyChangeListener(String, PropertyChangeListener)
	 */
	@Override
	public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (propertyName == VALUE && this.hasNoPropertyChangeListeners(VALUE)) {
			this.engageValueHolder();
		}
		super.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Extend to stop listening to the nested model if necessary.
	 * @see Model#removePropertyChangeListener(PropertyChangeListener)
	 */
	@Override
	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);
		if (this.hasNoPropertyChangeListeners(VALUE)) {
			this.disengageValueHolder();
		}
	}

	/**
	 * Extend to stop listening to the nested model if necessary.
	 * @see Model#removePropertyChangeListener(String, PropertyChangeListener)
	 */
	@Override
	public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		super.removePropertyChangeListener(propertyName, listener);
		if (propertyName == VALUE && this.hasNoPropertyChangeListeners(VALUE)) {
			this.disengageValueHolder();
		}
	}


	// ********** behavior **********

	/**
	 * Begin listening to the value holder.
	 */
	protected void engageValueHolder() {
		this.valueHolder.addPropertyChangeListener(VALUE, this.valueChangeListener);
	}

	/**
	 * Stop listening to the value holder.
	 */
	protected void disengageValueHolder() {
		this.valueHolder.removePropertyChangeListener(VALUE, this.valueChangeListener);
	}

	/**
	 * @see oracle.toplink.workbench.utility.AbstractModel#toString(StringBuffer)
	 */
	@Override
	public void toString(StringBuffer writer) {
		writer.append(this.valueHolder);
	}


	// ********** ValueModel implementation **********

	/**
	 * @see ValueModel#getValue()
	 */
	public Object getValue() {
		// transform the object returned by the nested value model before returning it
		return this.transform(this.valueHolder.getValue());
	}


	// ********** property change support **********

	@SuppressWarnings("unchecked")
	protected void valueChanged(PropertyChangeEvent e) {
		// transform the values before propagating the change event
		Object oldValue = this.transform(e.getOldValue());
		Object newValue = this.transform(e.getNewValue());
		this.firePropertyChanged(VALUE, oldValue, newValue);
	}


	// ********** behavior **********

	/**
	 * Transform the specified object and return the result.
	 * This is called by #getValue().
	 */
	protected Object transform(Object value) {
		return (value == null) ? null : this.transformNonNull(value);
	}

	/**
	 * Transform the specified object and return the result.
	 * This is called by #getValue().
	 */
	protected Object transformNonNull(Object value) {
		return this.transformer.transform(value);
	}
}