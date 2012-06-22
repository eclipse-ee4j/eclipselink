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

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * A <code>CloneIterator</code> iterates over a copy of a collection,
 * allowing for concurrent access to the original collection.
 * <p>
 * The original collection passed to the <code>CloneIterator</code>'s
 * constructor should be synchronized; otherwise you run the risk of
 * a corrupted collection.
 * <p>
 * By default, a <code>CloneIterator</code> does not support the
 * <code>#remove()</code> operation; this is because it does not have
 * access to the original collection. But if the <code>CloneIterator</code>
 * is supplied with an <code>Mutator</code> it will delegate the
 * <code>#remove()</code> operation to the <code>Mutator</code>.
 * Alternatively, a subclass can override the <code>#remove(Object)</code>
 * method.
 */
public class CloneIterator
	implements Iterator
{
	private Iterator nestedIterator;
	private Object current;
	private Mutator mutator;

	private static final Object UNKNOWN = new Object();


	// ********** constructors **********

	/**
	 * Construct an iterator on a copy of the specified collection.
	 * The <code>#remove()</code> method will not be supported,
	 * unless a subclass overrides the <code>#remove(Object)</code>.
	 */
	public CloneIterator(Collection c) {
		this(c, Mutator.READ_ONLY_INSTANCE);
	}

	/**
	 * Construct an iterator on a copy of the specified collection.
	 * Use the specified mutator to remove objects from the
	 * original collection.
	 */
	public CloneIterator(Collection c, Mutator mutator) {
		super();
		this.nestedIterator = new ArrayIterator(c.toArray());
		this.mutator = mutator;
		this.current = UNKNOWN;
	}


	// ********** Iterator implementation **********

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return this.nestedIterator.hasNext();
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		this.current = this.nestedIterator.next();
		return this.current;
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		if (this.current == UNKNOWN) {
			throw new IllegalStateException();
		}
		this.remove(this.current);
		this.current = UNKNOWN;
	}


	// ********** internal methods **********

	/**
	 * Remove the specified element from the original collection.
	 * <p>
	 * This method can be overridden by a subclass as an
	 * alternative to building an <code>Mutator</code>.
	 */
	protected void remove(Object o) {
		this.mutator.remove(o);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this);
	}


	//********** member interface **********

	/**
	 * Used by <code>CloneIterator</code> to remove
	 * elements from the original collection; since the iterator
	 * does not have direct access to the original collection.
	 */
	public interface Mutator {

		/**
		 * Remove the specified object from the original collection.
		 */
		void remove(Object current);


		Mutator READ_ONLY_INSTANCE =
			new Mutator() {
				public void remove(Object current) {
					throw new UnsupportedOperationException();
				}
				public String toString() {
					return "ReadOnlyMutator";
				}
			};

	}

}
