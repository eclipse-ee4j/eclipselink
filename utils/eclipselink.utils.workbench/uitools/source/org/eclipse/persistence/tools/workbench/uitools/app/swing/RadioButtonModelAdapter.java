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
package org.eclipse.persistence.tools.workbench.uitools.app.swing;

import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.BidiTransformer;
import org.eclipse.persistence.tools.workbench.utility.filters.BidiFilter;


/**
 * This javax.swing.ButtonModel can be used to keep a listener
 * (e.g. a JRadioButton) in synch with a (typically shared)
 * PropertyValueModel that holds one value out of a set of values.
 * 
 * NOTE: Do *not* use this model with a ButtonGroup, since the
 * shared value holder and the wrappers built by this adapter will
 * keep the appropriate radio button checked. Also, this allows
 * us to uncheck all the radio buttons in a group when the shared
 * value is null.
 */
public class RadioButtonModelAdapter extends ToggleButtonModelAdapter {


	// ********** constructors **********

	/**
	 * Constructor - the value holder is required.
	 */
	public RadioButtonModelAdapter(PropertyValueModel valueHolder, Object buttonValue, boolean defaultValue) {
		super(buildBooleanHolder(valueHolder, buttonValue), defaultValue);
	}

	/**
	 * Constructor - the value holder is required.
	 * The default value will be false.
	 */
	public RadioButtonModelAdapter(PropertyValueModel valueHolder, Object buttonValue) {
		super(buildBooleanHolder(valueHolder, buttonValue));
	}


	// ********** static methods **********

	/**
	 * Build up a set of wrappers that will convert the
	 * specified value holder and button value to/from a boolean.
	 * 
	 * If the value holder's value matches the button value,
	 * the wrapper will return true. Likewise, if the value holder's
	 * value is set to true, the wrapper will set the value holder's
	 * value to the button value.
	 */
	public static PropertyValueModel buildBooleanHolder(PropertyValueModel valueHolder, Object buttonValue) {
		PropertyValueModel filteringPVM = new FilteringPropertyValueModel(valueHolder, new RadioButtonFilter(buttonValue));
		return new TransformationPropertyValueModel(filteringPVM, new RadioButtonTransformer(buttonValue));
	}


	// ********** overrides **********

	/**
	 * The user cannot de-select a radio button - the user
	 * can only *select* a radio button. Only the model can
	 * cause a radio button to be de-selected. We use the
	 * ARMED flag to indicate whether we are being de-selected
	 * by the user.
	 * @see ToggleButtonModelAdapter#setSelected(boolean)
	 */
	public void setSelected(boolean b) {
		// do not allow the user to de-select a radio button
		// radio buttons can
		if ((b == false) && this.isArmed()) {
			return;
		}
		super.setSelected(b);
	}


	// ********** inner classes **********

	/**
	 * This filter will only pass through a new value to the wrapped
	 * value holder when it matches the configured button value.
	 */
	public static class RadioButtonFilter implements BidiFilter {
		private Object buttonValue;

		public RadioButtonFilter(Object buttonValue) {
			super();
			this.buttonValue = buttonValue;
		}

		/**
		 * always return the wrapped value
		 */
		public boolean accept(Object value) {
			return true;
		}

		/**
		 * pass through the value to the wrapped property value model
		 * *only* when it matches our button value
		 */
		public boolean reverseAccept(Object value) {
			return value == this.buttonValue;
		}

	}

	/**
	 * This transformer will convert the wrapped value to Boolean.TRUE
	 * when it matches the configured button value.
	 */
	public static class RadioButtonTransformer implements BidiTransformer {
		private Object buttonValue;

		public RadioButtonTransformer(Object buttonValue) {
			super();
			this.buttonValue = buttonValue;
		}

		/**
		 * if the wrapped value matches our button value return true,
		 * if it is some other value return false;
		 * but if it is null simply pass it through because it will cause the
		 * button model's default value to be used
		 */
		public Object transform(Object value) {
			return (value == null) ? null : Boolean.valueOf(value == this.buttonValue);
		}

		/**
		 * if the new value is true, pass through the our button value;
		 * otherwise pass through null
		 */
		public Object reverseTransform(Object value) {
			return (((Boolean) value).booleanValue()) ? this.buttonValue : null;
		}

	}

}
