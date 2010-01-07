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
package org.eclipse.persistence.tools.workbench.utility.diff;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Given a pair of ordered containers to compare, use the element differentiator
 * to compare the elements' values. The comparisons are based on the indices
 * of the elements; i.e. an element in the first container is only compared to 
 * the element in the second container at the same index - there is no searching
 * of the second container for a match. If the containers are different sizes,
 * the "remainder" elements at the end of the longer container are recorded as
 * "missing".
 */
public class OrderedContainerDifferentiator
	implements Differentiator
{
	/** the adapter used to adapter the containers */
	private Adapter adapter;

	/** the differentiator used to compare the containers' elements */
	private Differentiator elementDifferentiator;

	/** used as a placeholder when the containers are different sizes */
	static final Object UNDEFINED_ELEMENT =
		new Object() {
			public String toString() {
				return "<undefined>";
			}
		};


	// ********** convenience static methods **********

	public static OrderedContainerDifferentiator forLists() {
		return new OrderedContainerDifferentiator(ListAdapter.instance());
	}

	public static OrderedContainerDifferentiator forLists(Differentiator elementDifferentiator) {
		return new OrderedContainerDifferentiator(ListAdapter.instance(), elementDifferentiator);
	}

	public static OrderedContainerDifferentiator forArrays() {
		return new OrderedContainerDifferentiator(ArrayAdapter.instance());
	}

	public static OrderedContainerDifferentiator forArrays(Differentiator elementDifferentiator) {
		return new OrderedContainerDifferentiator(ArrayAdapter.instance(), elementDifferentiator);
	}


	// ********** constructors **********

	/**
	 * by default, use an "equality" element differentiator
	 * use this constructor if you want to override the various
	 * "adapter" methods instead of building an Adapter
	 */
	public OrderedContainerDifferentiator() {
		this(Adapter.INVALID_INSTANCE);
	}

	/**
	 * use this constructor if you want to override the various
	 * "adapter" methods instead of building an Adapter
	 */
	public OrderedContainerDifferentiator(Differentiator elementDifferentiator) {
		this(Adapter.INVALID_INSTANCE, elementDifferentiator);
	}

	/**
	 * by default, use an "equality" element differentiator
	 */
	public OrderedContainerDifferentiator(Adapter adapter) {
		this(adapter, EqualityDifferentiator.instance());
	}

	public OrderedContainerDifferentiator(Adapter adapter, Differentiator elementDifferentiator) {
		super();
		this.adapter = adapter;
		this.elementDifferentiator = elementDifferentiator;
	}


	// ********** Differentiator implementation **********

	/**
	 * @see Differentiator#diff(Object, Object)
	 */
	public Diff diff(Object object1, Object object2) {
		return this.diff(object1, object2, DifferentiatorAdapter.NORMAL);
	}

	/**
	 * @see Differentiator#keyDiff(Object, Object)
	 */
	public Diff keyDiff(Object object1, Object object2) {
		return this.diff(object1, object2, DifferentiatorAdapter.KEY);
	}

	/**
	 * compare the containers, element by element at the same index
	 */
	private Diff diff(Object object1, Object object2, DifferentiatorAdapter drAdapter) {
		if (object1 == object2) {
			return new NullDiff(object1, object2, this);
		}
		if (this.diffIsFatal(object1, object2)) {
			return new SimpleDiff(object1, object2, this.fatalDescriptionTitle(), this);
		}

		int size1 = this.size(object1);
		int size2 = this.size(object2);
		int min = Math.min(size1, size2);
		int max = Math.max(size1, size2);
		Diff[] diffs = new Diff[max];
		for (int i = 0; i < min; i++) {
			Diff elementDiff = drAdapter.diff(this.elementDifferentiator, this.get(object1, i), this.get(object2, i));
			diffs[i] = new OrderedContainerElementDiff(i, elementDiff, this);
		}
		if (min != max) {
			// if the lists are different sizes, add some more element diffs
			if (size1 < size2) {
				for (int i = min; i < max; i++) {
					Diff elementDiff = new SimpleDiff(UNDEFINED_ELEMENT, this.get(object2, i), this.missingElementDescriptionTitle(i), this);
					diffs[i] = new OrderedContainerElementDiff(i, elementDiff, this);
				}
			} else {
				for (int i = min; i < max; i++) {
					Diff elementDiff = new SimpleDiff(this.get(object1, i), UNDEFINED_ELEMENT, this.missingElementDescriptionTitle(i), this);
					diffs[i] = new OrderedContainerElementDiff(i, elementDiff, this);
				}
			}
		}
		return new OrderedContainerDiff(this.containerClass(), object1, object2, diffs, this);
	}

	private String missingElementDescriptionTitle(int index) {
		return "Only one of the " + ClassTools.shortNameFor(this.containerClass()) + "s has a value at index " + index;
	}

	private String fatalDescriptionTitle() {
		return "The two " + ClassTools.shortNameFor(this.containerClass()) + "s cannot be compared";
	}

	/**
	 * this will probably never be called, but we'll try 'false' for now
	 * @see Differentiator#comparesValueObjects()
	 */
	public boolean comparesValueObjects() {
		return false;
	}


	// ********** accessors **********

	public Differentiator getElementDifferentiator() {
		return this.elementDifferentiator;
	}

	public void setElementDifferentiator(Differentiator elementDifferentiator) {
		this.elementDifferentiator = elementDifferentiator;
	}


	// ********** "adapter" methods **********

	/**
	 * return whether the the compared objects can even be
	 * compared (e.g. they both must be non-null and instances
	 * of java.util.List)
	 */
	protected boolean diffIsFatal(Object object1, Object object2) {
		return this.adapter.diffIsFatal(object1, object2);
	}

	/**
	 * return the class to be passed to any diffs created
	 * during the comparison
	 */
	protected Class containerClass() {
		return this.adapter.containerClass();
	}

	/**
	 * return the size of the specified container
	 */
	protected int size(Object container) {
		return this.adapter.size(container);
	}

	/**
	 * return the element at the specified index
	 */
	protected Object get(Object container, int index) {
		return this.adapter.get(container, index);
	}


	// ********** standard override **********

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, this.elementDifferentiator);
	}


	// ********** member interface **********

	/**
	 * This adapter is used by the differentiator to adapt
	 * access to the various containers to be compared
	 * (e.g. List, Array).
	 */
	public interface Adapter {

		/**
		 * Return whether the the specified objects can even be
		 * compared (e.g. they both must be non-null and instances
		 * of java.util.List).
		 */
		boolean diffIsFatal(Object object1, Object object2);

		/**
		 * Return the class to be passed to any diffs created
		 * during the comparison. Most of the time this is used
		 * only in user messages.
		 */
		Class containerClass();

		/**
		 * Return the size of the specified container.
		 */
		int size(Object container);

		/**
		 * Return the element at the specified index.
		 */
		Object get(Object container, int index);

		Adapter INVALID_INSTANCE =
			new Adapter() {
				public boolean diffIsFatal(Object object1, Object object2) {
					throw new UnsupportedOperationException();
				}
				public Class containerClass() {
					throw new UnsupportedOperationException();
				}
				public int size(Object container) {
					throw new UnsupportedOperationException();
				}
				public Object get(Object container, int index) {
					throw new UnsupportedOperationException();
				}
				public String toString() {
					return "InvalidAdapter";
				}
			};

	}

}
