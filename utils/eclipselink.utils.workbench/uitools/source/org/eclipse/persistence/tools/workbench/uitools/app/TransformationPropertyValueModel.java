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

import org.eclipse.persistence.tools.workbench.utility.BidiTransformer;


/**
 * A <code>TransformationPropertyValueModel</code> wraps another
 * <code>PropertyValueModel</code> and uses a <code>BidiTransformer</code>
 * to:<ul>
 * <li>transform the wrapped value before it is returned by <code>getValue()</code>
 * <li>"reverse-transform" the new value that comes in via
 * <code>setValue(Object)</code>
 * </ul>
 * As an alternative to building a <code>BidiTransformer</code>,
 * a subclass of <code>TransformationPropertyValueModel</code> can
 * override the <code>transform(Object)</code> and 
 * <code>reverseTransform(Object)</code> methods.
 */
public class TransformationPropertyValueModel extends PropertyValueModelWrapper {
	private BidiTransformer transformer;


	// ********** constructors **********

	/**
	 * Construct a property value model with the specified nested
	 * property value model and a transformer that performs no
	 * transformations at all.
	 * Use this constructor if you want to override the
	 * <code>transform(Object)</code> and <code>reverseTransform(Object)</code>
	 * methods instead of building a <code>BidiTransformer</code>.
	 */
	public TransformationPropertyValueModel(PropertyValueModel valueHolder) {
		this(valueHolder, BidiTransformer.NULL_INSTANCE);
	}

	/**
	 * Construct an property value model with the specified nested
	 * property value model and transformer.
	 */
	public TransformationPropertyValueModel(PropertyValueModel valueHolder, BidiTransformer transformer) {
		super(valueHolder);
		this.transformer = transformer;
	}


	// ********** ValueModel implementation **********

	/**
	 * @see ValueModel#getValue()
	 */
	public Object getValue() {
		// transform the object returned by the nested value model before returning it
		return this.transform(this.valueHolder.getValue());
	}


	// ********** PropertyValueModel implementation **********

	/**
	 * @see PropertyValueModel#setValue(java.lang.Object)
	 */
	public void setValue(Object value) {
		// "reverse-transform" the object before passing it to the the nested value model
		this.valueHolder.setValue(this.reverseTransform(value));
	}


	// ********** PropertyValueModelWrapper implementation **********

	/**
	 * @see PropertyValueModelWrapper#valueChanged(java.beans.PropertyChangeEvent)
	 */
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
		return this.transformer.transform(value);
	}

	/**
	 * "Reverse-transform" the specified object and return the result.
	 * This is called by #setValue(Object).
	 */
	protected Object reverseTransform(Object value) {
		return this.transformer.reverseTransform(value);
	}

}
