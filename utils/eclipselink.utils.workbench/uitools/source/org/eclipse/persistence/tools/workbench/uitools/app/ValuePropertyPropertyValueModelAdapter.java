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
package org.eclipse.persistence.tools.workbench.uitools.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import org.eclipse.persistence.tools.workbench.utility.Model;


/**
 * Extend ValueAspectPropertyValueModelAdapter to listen to one or more
 * properties of the value in the wrapped value model.
 */
public class ValuePropertyPropertyValueModelAdapter
	extends ValueAspectPropertyValueModelAdapter
{
	/** The names of the value's properties that we listen to. */
	protected String[] propertyNames;

	/** Listener that listens to the value. */
	protected PropertyChangeListener valuePropertyListener;


	// ********** constructors **********

	/**
	 * Construct an adapter for the specified value property.
	 */
	public ValuePropertyPropertyValueModelAdapter(PropertyValueModel valueHolder, String propertyName) {
		this(valueHolder, new String[] {propertyName});
	}

	/**
	 * Construct an adapter for the specified value properties.
	 */
	public ValuePropertyPropertyValueModelAdapter(PropertyValueModel valueHolder, String propertyName1, String propertyName2) {
		this(valueHolder, new String[] {propertyName1, propertyName2});
	}

	/**
	 * Construct an adapter for the specified value properties.
	 */
	public ValuePropertyPropertyValueModelAdapter(PropertyValueModel valueHolder, String propertyName1, String propertyName2, String propertyName3) {
		this(valueHolder, new String[] {propertyName1, propertyName2, propertyName3});
	}

	/**
	 * Construct an adapter for the specified value properties.
	 */
	public ValuePropertyPropertyValueModelAdapter(PropertyValueModel valueHolder, String[] propertyNames) {
		super(valueHolder);
		this.propertyNames = propertyNames;
	}


	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		this.valuePropertyListener = this.buildValuePropertyListener();
	}

	protected PropertyChangeListener buildValuePropertyListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				ValuePropertyPropertyValueModelAdapter.this.valueAspectChanged();
			}
			public String toString() {
				return "value property listener: " + Arrays.asList(ValuePropertyPropertyValueModelAdapter.this.propertyNames);
			}
		};
	}
	

	// ********** behavior **********

	protected void startListeningToValue() {
		Model v = (Model) this.value;
		for (int i = this.propertyNames.length; i-- > 0; ) {
			v.addPropertyChangeListener(this.propertyNames[i], this.valuePropertyListener);
		}
	}

	protected void stopListeningToValue() {
		Model v = (Model) this.value;
		for (int i = this.propertyNames.length; i-- > 0; ) {
			v.removePropertyChangeListener(this.propertyNames[i], this.valuePropertyListener);
		}
	}


	// ********** item change support **********

}
