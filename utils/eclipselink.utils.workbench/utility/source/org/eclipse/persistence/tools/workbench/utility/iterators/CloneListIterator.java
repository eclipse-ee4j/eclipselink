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
package org.eclipse.persistence.tools.workbench.utility.iterators;

import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * A <code>CloneListIterator</code> iterates over a copy of a list,
 * allowing for concurrent access to the original list.
 * <p>
 * The original list passed to the <code>CloneListIterator</code>'s
 * constructor should be synchronized; otherwise you run the risk of
 * a corrupted list.
 * <p>
 * By default, a <code>CloneListIterator</code> does not support the
 * modification operations; this is because it does not have
 * access to the original list. But if the <code>CloneListIterator</code>
 * is supplied with a <code>Mutator</code> it will delegate the
 * modification operations to the <code>Mutator</code>.
 * Alternatively, a subclass can override the modification methods.
 */
public class CloneListIterator implements ListIterator {
	private ListIterator nestedListIterator;
	private int cursor;
	private String state;
	private Mutator mutator;

	private static final String UNKNOWN = "unknown";
	private static final String PREVIOUS = "previous";
	private static final String NEXT = "next";


	// ********** constructors **********

	/**
	 * Construct a list iterator on a copy of the specified list.
	 * The modification methods will not be supported,
	 * unless a subclass overrides them.
	 */
	public CloneListIterator(List list) {
		this(list, Mutator.READ_ONLY_INSTANCE);
	}

	/**
	 * Construct a list iterator on a copy of the specified list.
	 * Use the specified list mutator to modify the original list.
	 */
	public CloneListIterator(List list, Mutator mutator) {
		super();
		this.nestedListIterator = CollectionTools.list(list.toArray()).listIterator();
		this.mutator = mutator;
		this.cursor = 0;
		this.state = UNKNOWN;
	}


	// ********** ListIterator implementation **********

	/**
	 * @see java.util.ListIterator#hasNext()
	 */
	public boolean hasNext() {
		return this.nestedListIterator.hasNext();
	}

	/**
	 * @see java.util.ListIterator#next()
	 */
	public Object next() {
		// allow the nested iterator to throw an exception before we modify the index
		Object next = this.nestedListIterator.next();
		this.cursor++;
		this.state = NEXT;
		return next;
	}

	/**
	 * @see java.util.ListIterator#remove()
	 */
	public void remove() {
		// allow the nested iterator to throw an exception before we modify the original list
		this.nestedListIterator.remove();
		if (this.state == PREVIOUS) {
			this.remove(this.cursor);
		} else {
			this.cursor--;
			this.remove(this.cursor);
		}
	}

	/**
	 * @see java.util.ListIterator#nextIndex()
	 */
	public int nextIndex() {
		return this.nestedListIterator.nextIndex();
	}

	/**
	 * @see java.util.ListIterator#previousIndex()
	 */
	public int previousIndex() {
		return this.nestedListIterator.previousIndex();
	}

	/**
	 * @see java.util.ListIterator#hasPrevious()
	 */
	public boolean hasPrevious() {
		return this.nestedListIterator.hasPrevious();
	}

	/**
	 * @see java.util.ListIterator#previous()
	 */
	public Object previous() {
		// allow the nested iterator to throw an exception before we modify the index
		Object previous = this.nestedListIterator.previous();
		this.cursor--;
		this.state = PREVIOUS;
		return previous;
	}

	/**
	 * @see java.util.ListIterator#add(java.lang.Object)
	 */
	public void add(Object o) {
		// allow the nested iterator to throw an exception before we modify the original list
		this.nestedListIterator.add(o);
		this.add(this.cursor, o);
		this.cursor++;
	}

	/**
	 * @see java.util.ListIterator#set(java.lang.Object)
	 */
	public void set(Object o) {
		// allow the nested iterator to throw an exception before we modify the original list
		this.nestedListIterator.set(o);
		if (this.state == PREVIOUS) {
			this.set(this.cursor, o);
		} else {
			this.set(this.cursor - 1, o);
		}
	}


	// ********** internal methods **********

	/**
	 * Add the specified element to the original list.
	 * <p>
	 * This method can be overridden by a subclass as an
	 * alternative to building a <code>Mutator</code>.
	 */
	protected void add(int index, Object o) {
		this.mutator.add(index, o);
	}

	/**
	 * Remove the specified element from the original list.
	 * <p>
	 * This method can be overridden by a subclass as an
	 * alternative to building a <code>Mutator</code>.
	 */
	protected void remove(int index) {
		this.mutator.remove(index);
	}

	/**
	 * Set the specified element in the original list.
	 * <p>
	 * This method can be overridden by a subclass as an
	 * alternative to building a <code>Mutator</code>.
	 */
	protected void set(int index, Object o) {
		this.mutator.set(index, o);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this);
	}


	//********** member interface **********

	/**
	 * Used by <code>CloneListIterator</code> to remove
	 * elements from the original list; since the list iterator
	 * does not have direct access to the original list.
	 */
	public interface Mutator {

		/**
		 * Add the specified object to the original list.
		 */
		void add(int index, Object o);

		/**
		 * Remove the specified object from the original list.
		 */
		void remove(int index);

		/**
		 * Set the specified object in the original list.
		 */
		void set(int index, Object o);


		Mutator READ_ONLY_INSTANCE =
			new Mutator() {
				public void add(int index, Object o) {
					throw new UnsupportedOperationException();
				}
				public void remove(int index) {
					throw new UnsupportedOperationException();
				}
				public void set(int index, Object o) {
					throw new UnsupportedOperationException();
				}
				public String toString() {
					return "ReadOnlyListMutator";
				}
			};

	}

}
