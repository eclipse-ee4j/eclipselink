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

import java.util.Arrays;

import org.eclipse.persistence.tools.workbench.utility.Model;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;


/**
 * Extend ValueAspectPropertyValueModelAdapter to listen to one or more list
 * aspects of the value in the wrapped value model.
 */
public class ValueListPropertyValueModelAdapter
	extends ValueAspectPropertyValueModelAdapter
{

	/** The names of the value's lists that we listen to. */
	protected final String[] listNames;

	/** Listener that listens to the value. */
	protected ListChangeListener valueListListener;


	// ********** constructors **********

	/**
	 * Construct an adapter for the specified value list.
	 */
	public ValueListPropertyValueModelAdapter(PropertyValueModel valueHolder, String listName) {
		this(valueHolder, new String[] {listName});
	}

	/**
	 * Construct an adapter for the specified value lists.
	 */
	public ValueListPropertyValueModelAdapter(PropertyValueModel valueHolder, String listName1, String listName2) {
		this(valueHolder, new String[] {listName1, listName2});
	}

	/**
	 * Construct an adapter for the specified value lists.
	 */
	public ValueListPropertyValueModelAdapter(PropertyValueModel valueHolder, String listName1, String listName2, String listName3) {
		this(valueHolder, new String[] {listName1, listName2, listName3});
	}

	/**
	 * Construct an adapter for the specified value lists.
	 */
	public ValueListPropertyValueModelAdapter(PropertyValueModel valueHolder, String[] listNames) {
		super(valueHolder);
		this.listNames = listNames;
	}


	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		this.valueListListener = this.buildValueListListener();
	}

	/**
	 * All we really care about is the fact that a List aspect has 
	 * changed.  Do the same thing no matter which event occurs.
	 */
	protected ListChangeListener buildValueListListener() {
		return new ListChangeListener() {
			public void itemsAdded(ListChangeEvent e) {
				ValueListPropertyValueModelAdapter.this.valueAspectChanged();
			}
			public void itemsRemoved(ListChangeEvent e) {
				ValueListPropertyValueModelAdapter.this.valueAspectChanged();
			}
			public void itemsReplaced(ListChangeEvent e) {
				ValueListPropertyValueModelAdapter.this.valueAspectChanged();
			}
			public void listChanged(ListChangeEvent e) {
				ValueListPropertyValueModelAdapter.this.valueAspectChanged();
			}
			public String toString() {
				return "value list listener: " + Arrays.asList(ValueListPropertyValueModelAdapter.this.listNames);
			}
		};
	}

	protected void startListeningToValue() {
		Model v = (Model) this.value;
		for (int i = this.listNames.length; i-- > 0; ) {
			v.addListChangeListener(this.listNames[i], this.valueListListener);
		}
	}

	protected void stopListeningToValue() {
		Model v = (Model) this.value;
		for (int i = this.listNames.length; i-- > 0; ) {
			v.removeListChangeListener(this.listNames[i], this.valueListListener);
		}
	}

}
