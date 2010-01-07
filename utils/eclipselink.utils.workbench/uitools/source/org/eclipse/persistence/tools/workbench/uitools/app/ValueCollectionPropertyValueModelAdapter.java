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
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;


/**
 * Extend ValueAspectPropertyValueModelAdapter to listen to one or more collection
 * aspects of the value in the wrapped value model.
 */
public class ValueCollectionPropertyValueModelAdapter
	extends ValueAspectPropertyValueModelAdapter
{

	/** The names of the value's collections that we listen to. */
	protected final String[] collectionNames;

	/** Listener that listens to the value. */
	protected CollectionChangeListener valueCollectionListener;


	// ********** constructors **********

	/**
	 * Construct an adapter for the specified value collection.
	 */
	public ValueCollectionPropertyValueModelAdapter(PropertyValueModel valueHolder, String collectionName) {
		this(valueHolder, new String[] {collectionName});
	}

	/**
	 * Construct an adapter for the specified value collections.
	 */
	public ValueCollectionPropertyValueModelAdapter(PropertyValueModel valueHolder, String collectionName1, String collectionName2) {
		this(valueHolder, new String[] {collectionName1, collectionName2});
	}

	/**
	 * Construct an adapter for the specified value collections.
	 */
	public ValueCollectionPropertyValueModelAdapter(PropertyValueModel valueHolder, String collectionName1, String collectionName2, String collectionName3) {
		this(valueHolder, new String[] {collectionName1, collectionName2, collectionName3});
	}

	/**
	 * Construct an adapter for the specified value collections.
	 */
	public ValueCollectionPropertyValueModelAdapter(PropertyValueModel valueHolder, String[] collectionNames) {
		super(valueHolder);
		this.collectionNames = collectionNames;
	}


	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		this.valueCollectionListener = this.buildValueCollectionListener();
	}

	/**
	 * All we really care about is the fact that a Collection aspect has 
	 * changed.  Do the same thing no matter which event occurs.
	 */
	protected CollectionChangeListener buildValueCollectionListener() {
		return new CollectionChangeListener() {
			public void itemsAdded(CollectionChangeEvent e) {
				ValueCollectionPropertyValueModelAdapter.this.valueAspectChanged();
			}
			public void itemsRemoved(CollectionChangeEvent e) {
				ValueCollectionPropertyValueModelAdapter.this.valueAspectChanged();
			}
			public void collectionChanged(CollectionChangeEvent e) {
				ValueCollectionPropertyValueModelAdapter.this.valueAspectChanged();
			}
			public String toString() {
				return "value collection listener: " + Arrays.asList(ValueCollectionPropertyValueModelAdapter.this.collectionNames);
			}
		};
	}

	protected void startListeningToValue() {
		Model v = (Model) this.value;
		for (int i = this.collectionNames.length; i-- > 0; ) {
			v.addCollectionChangeListener(this.collectionNames[i], this.valueCollectionListener);
		}
	}

	protected void stopListeningToValue() {
		Model v = (Model) this.value;
		for (int i = this.collectionNames.length; i-- > 0; ) {
			v.removeCollectionChangeListener(this.collectionNames[i], this.valueCollectionListener);
		}
	}

}
