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
import java.util.LinkedList;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * A <code>TreeIterator</code> simplifies the traversal of a
 * tree of objects, where the objects' protocol(s) provides
 * a method for getting the immediate children of the given
 * node but does not provide a method for getting all the
 * descendants (children, grandchildren, etc.) of the given node.
 * <p>
 * To use, supply:<ul>
 * <li> either the root element of the tree or, if the tree has
 * multiple roots, an <code>Iterator</code> over the set of roots
 * <li> a <code>Midwife</code> that delivers the children
 * of each child
 * (alternatively, subclass <code>TreeIterator</code>
 * and override the <code>children(Object)</code> method)
 * </ul>
 * <p>
 */
public class TreeIterator implements Iterator {
	private Iterator currentIterator;
	private Collection iterators;
	private Midwife midwife;


	/**
	 * Construct an iterator with the specified collection of roots
	 * and a midwife that simply returns an empty iterator
	 * for each of the roots.
	 * Use this constructor if you want to override the
	 * <code>children(Object)</code> method instead of building
	 * a <code>Midwife</code>.
	 */
	public TreeIterator(Iterator roots) {
		this(roots, Midwife.NULL_INSTANCE);
	}

	/**
	 * Construct an iterator with the specified root
	 * and a midwife that simply returns an empty iterator
	 * for the root.
	 * Use this constructor if you want to override the
	 * <code>children(Object)</code> method instead of building
	 * a <code>Midwife</code>.
	 */
	public TreeIterator(Object root) {
		this(root, Midwife.NULL_INSTANCE);
	}

	/**
	 * Construct an iterator with the specified root
	 * and midwife.
	 */
	public TreeIterator(Object root, Midwife midwife) {
		this(new SingleElementIterator(root), midwife);
	}

	/**
	 * Construct an iterator with the specified roots
	 * and midwife.
	 */
	public TreeIterator(Iterator roots, Midwife midwife) {
		super();
		this.currentIterator = roots;
		// use a LinkedList since we will be pulling off the front and adding to the end
		this.iterators = new LinkedList();
		this.midwife = midwife;
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if (this.currentIterator.hasNext()) {
			return true;
		}
		for (Iterator stream = this.iterators.iterator(); stream.hasNext(); ) {
			Iterator iterator = (Iterator) stream.next();
			if (iterator.hasNext()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		if (this.currentIterator.hasNext()) {
			return this.nextInternal();
		}
		for (Iterator stream = this.iterators.iterator(); stream.hasNext(); ) {
			this.currentIterator = (Iterator) stream.next();
			if (this.currentIterator.hasNext()) {
				break;
			}
			stream.remove();
		}
		return this.nextInternal();
	}

	/**
	 * Fetch the children of the next node before returning it.
	 */
	private Object nextInternal() {
		Object next = this.currentIterator.next();
		this.iterators.add(this.children(next));
		return next;
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		this.currentIterator.remove();
	}

	/**
	 * Return the immediate children of the specified object.
	 */
	protected Iterator children(Object next) {
		return this.midwife.children(next);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.currentIterator + ')';
	}


//********** inner classes **********

/**
 * Used by <code>TreeIterator</code> to retrieve
 * the immediate children of a node in the tree.
 */
public interface Midwife {

	/**
	 * Return the immediate children of the specified object.
	 */
	Iterator children(Object next);


	Midwife NULL_INSTANCE =
		new Midwife() {
			// return no children
			public Iterator children(Object next) {
				return NullIterator.instance();
			}
		};

}

}
