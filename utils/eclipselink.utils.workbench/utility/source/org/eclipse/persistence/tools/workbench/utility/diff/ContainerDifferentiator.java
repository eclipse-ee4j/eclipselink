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
package org.eclipse.persistence.tools.workbench.utility.diff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Counter;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Given a pair of containers to compare, use the element differentiator
 * to compare the elements' values. The order of the elements is insignificant.
 * Elements are matched up by using "key diffs": if the "key diff" indicates
 * that two elements have the same "key", they are then compared with
 * a "normal diff".
 */
public class ContainerDifferentiator
	implements Differentiator
{
	/** the adapter used to adapter the containers */
	private Adapter adapter;

	/** the differentiator used to compare the containers' elements */
	private Differentiator elementDifferentiator;


	// ********** convenience static methods **********

	public static ContainerDifferentiator forCollections() {
		return new ContainerDifferentiator(CollectionAdapter.instance());
	}

	public static ContainerDifferentiator forCollections(Differentiator elementDifferentiator) {
		return new ContainerDifferentiator(CollectionAdapter.instance(), elementDifferentiator);
	}

	public static ContainerDifferentiator forArrays() {
		return new ContainerDifferentiator(UnorderedArrayAdapter.instance());
	}

	public static ContainerDifferentiator forArrays(Differentiator elementDifferentiator) {
		return new ContainerDifferentiator(UnorderedArrayAdapter.instance(), elementDifferentiator);
	}

	public static ContainerDifferentiator forMaps() {
		return new ContainerDifferentiator(MapAdapter.instance(), new MapEntryDifferentiator());
	}

	public static ContainerDifferentiator forMaps(Differentiator keyDifferentiator, Differentiator valueDifferentiator) {
		return new ContainerDifferentiator(MapAdapter.instance(), new MapEntryDifferentiator(keyDifferentiator, valueDifferentiator));
	}

	public static ContainerDifferentiator forMaps(Differentiator entryDifferentiator) {
		return new ContainerDifferentiator(MapAdapter.instance(), entryDifferentiator);
	}


	// ********** constructors **********

	/**
	 * by default, use an "equality" element differentiator;
	 * use this constructor if you want to override the various
	 * "adapter" methods instead of building an Adapter
	 */
	public ContainerDifferentiator() {
		this(Adapter.INVALID_INSTANCE);
	}

	/**
	 * use this constructor if you want to override the various
	 * "adapter" methods instead of building an Adapter
	 */
	public ContainerDifferentiator(Differentiator elementDifferentiator) {
		this(Adapter.INVALID_INSTANCE, elementDifferentiator);
	}

	/**
	 * by default, use an "equality" element differentiator
	 */
	public ContainerDifferentiator(Adapter adapter) {
		this(adapter, EqualityDifferentiator.instance());
	}

	public ContainerDifferentiator(Adapter adapter, Differentiator elementDifferentiator) {
		super();
		this.adapter = adapter;
		this.elementDifferentiator = elementDifferentiator;
	}


	// ********** Differentiator implementation **********

	/**
	 * @see Differentiator#diff(Object, Object)
	 */
	public Diff diff(Object object1, Object object2) {
		return this.diff(object1, object2, true);
	}

	/**
	 * @see Differentiator#keyDiff(Object, Object)
	 */
	public Diff keyDiff(Object object1, Object object2) {
		return this.diff(object1, object2, false);
	}

	/**
	 * oh yeah - this is a pretty spot of code...
	 */
	private Diff diff(Object object1, Object object2, boolean fullDiff) {
		if (object1 == object2) {
			return new NullDiff(object1, object2, this);
		}
		if (this.diffIsFatal(object1, object2)) {
			return new SimpleDiff(object1, object2, this.fatalDescriptionTitle(), this);
		}

		Map counters1 = this.buildCounters(object1);
		Map counters2 = this.buildCounters(object2);

		Collection removedElements = new ArrayList();
		Collection keyMatchedDiffs = new ArrayList();
		for (Iterator stream1 = counters1.entrySet().iterator(); stream1.hasNext(); ) {
			Map.Entry entry1 = (Map.Entry) stream1.next();
			Object element1 = entry1.getKey();
			Counter counter1 = (Counter) entry1.getValue();
			for (int i = counter1.count(); i-- > 0; ) {
				boolean keyMatchFound = false;
				for (Iterator stream2 = counters2.entrySet().iterator(); stream2.hasNext(); ) {
					Map.Entry entry2 = (Map.Entry) stream2.next();
					Object element2 = entry2.getKey();
					Diff keyDiff = this.elementDifferentiator.keyDiff(element1, element2);
					if (keyDiff.identical()) {
						keyMatchFound = true;
						Counter counter2 = (Counter) entry2.getValue();
						counter2.decrement();
						if (counter2.count() == 0) {
							stream2.remove();
						}
						if (fullDiff) {
							keyMatchedDiffs.add(this.elementDifferentiator.diff(element1, element2));
						} else {
							keyMatchedDiffs.add(keyDiff);
						}
						break;	// skip remainder of elements in container 2 and go to the next element in container 1
					}
				}
				// if a "key" match was not found, the element must have been removed
				if ( ! keyMatchFound) {
					removedElements.add(element1);
				}
			}
		}

		// whatever elements remain in container 2 must have been added
		Collection addedElements = new ArrayList();
		for (Iterator stream2 = counters2.entrySet().iterator(); stream2.hasNext(); ) {
			Map.Entry entry2 = (Map.Entry) stream2.next();
			Object element2 = entry2.getKey();
			Counter counter2 = (Counter) entry2.getValue();
			for (int i = counter2.count(); i-- > 0; ) {
				addedElements.add(element2);
			}
		}

		return new ContainerDiff(
				this.containerClass(),
				object1,
				object2,
				(Diff[]) keyMatchedDiffs.toArray(new Diff[keyMatchedDiffs.size()]),
				removedElements.toArray(),
				addedElements.toArray(),
				this
		);
	}

	private Map buildCounters(Object container) {
		Map counters = new IdentityHashMap(this.size(container));
		for (Iterator stream = this.iterator(container); stream.hasNext(); ) {
			Object element = stream.next();
			Counter counter = (Counter) counters.get(element);
			if (counter == null) {
				counter = new Counter();
				counters.put(element, counter);
			}
			counter.increment();
		}
		return counters;
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
	 * of java.util.Collection)
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
	 * return an iterator on the elements of the specified container
	 */
	protected Iterator iterator(Object container) {
		return this.adapter.iterator(container);
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
	 * (e.g. Collection, Array, Map).
	 */
	public interface Adapter {

		/**
		 * Return whether the the specified objects can even be
		 * compared (e.g. they both must be non-null and instances
		 * of java.util.Collection).
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
		 * Return an iterator on the elements of the specified container.
		 */
		Iterator iterator(Object container);

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
				public Iterator iterator(Object container) {
					throw new UnsupportedOperationException();
				}
				public String toString() {
					return "InvalidAdapter";
				}
			};

	}

}
