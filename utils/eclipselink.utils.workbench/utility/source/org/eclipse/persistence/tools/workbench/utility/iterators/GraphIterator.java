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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * A <code>GraphIterator</code> is similar to a <code>TreeIterator</code>
 * except that it cannot be assumed that all nodes assume a strict tree
 * structure.  For instance, in a tree, a node cannot be a descendent of
 * itself, but a graph may have a cyclical structure.
 * 
 * A <code>GraphIterator</code> simplifies the traversal of a
 * graph of objects, where the objects' protocol(s) provides
 * a method for getting the next collection of nodes in the graph,
 * (or *neighbors*), but does not provide a method for getting *all* 
 * of the nodes in the graph.
 * (e.g. a neighbor can return his neighbors, and those neighbors
 * can return their neighbors, which might also include the original
 * neighbor, but you only want to visit the original neighbor once.)
 * <p>
 * If a neighbor has already been visited (determined by using 
 * <code>equals(Object)</code>), that neighbor is not visited again,
 * nor are the neighbors of that object.
 * <p>
 * It is up to the user of this class to ensure a *complete* graph.
 * <p>
 * To use, supply:<ul>
 * <li> either the initial node of the graph or an Iterator over an
 * initial collection of graph nodes
 * <li> a <code>MisterRogers</code> that tells who the neighbors are
 * of each node
 * (alternatively, subclass <code>GraphIterator</code>
 * and override the <code>neighbors(Object)</code> method)
 * </ul>
 * <p>
 * <code>remove()</code> is not supported.  This method, if 
 * desired, must be implemented by the user of this class.
 */
public class GraphIterator 
	implements Iterator
{
	private Collection iterators;
	private Set visitedNeighbors;
	private MisterRogers misterRogers;
	
	private Iterator currentIterator;
	private static final Iterator END_ITERATOR = NullIterator.instance();
	
	private Object nextNeighbor;
	private static final Object END_NEIGHBOR = new Object();

	/**
	 * Construct an iterator with the specified collection of roots
	 * and a misterRogers that simply returns an empty iterator
	 * for each of the roots.
	 * Use this constructor if you want to override the
	 * <code>children(Object)</code> method instead of building
	 * a <code>MisterRogers</code>.
	 */
	public GraphIterator(Iterator roots) {
		this(roots, MisterRogers.NULL_INSTANCE);
	}

	/**
	 * Construct an iterator with the specified root
	 * and a misterRogers that simply returns an empty iterator
	 * for the root.
	 * Use this constructor if you want to override the
	 * <code>children(Object)</code> method instead of building
	 * a <code>MisterRogers</code>.
	 */
	public GraphIterator(Object root) {
		this(root, MisterRogers.NULL_INSTANCE);
	}

	/**
	 * Construct an iterator with the specified root
	 * and misterRogers.
	 */
	public GraphIterator(Object root, MisterRogers misterRogers) {
		this(new SingleElementIterator(root), misterRogers);
	}

	/**
	 * Construct an iterator with the specified roots
	 * and misterRogers.
	 */
	public GraphIterator(Iterator roots, MisterRogers misterRogers) {
		super();
		this.currentIterator = roots;
		// use a LinkedList since we will be pulling off the front and adding to the end
		this.iterators = new LinkedList();
		this.misterRogers = misterRogers;
		this.visitedNeighbors = new HashSet();
		this.loadNextNeighbor();
	}

	/**
	 * Load nextNeighbor with the next entry from the current iterator.
	 * If the current iterator has none, load the next iterator.
	 * If there are no more, nextNeighbor is set to <code>END_NEIGHBOR</code>.
	 */
	private void loadNextNeighbor() {
		if (this.currentIterator == END_ITERATOR) {
			this.nextNeighbor = END_NEIGHBOR;
		}
		else if (this.currentIterator.hasNext()) {
			Object nextPossibleNeighbor = this.currentIterator.next();
			
			if (this.visitedNeighbors.contains(nextPossibleNeighbor)) {
				this.loadNextNeighbor();
			}
			else {
				this.nextNeighbor = nextPossibleNeighbor;
				this.visitedNeighbors.add(nextPossibleNeighbor);
				this.iterators.add(this.neighbors(nextPossibleNeighbor));
			}
		} 
		else {
			for (Iterator stream = this.iterators.iterator(); 
				! this.currentIterator.hasNext() && stream.hasNext(); 
			) {
				this.currentIterator = (Iterator) stream.next();
				stream.remove();
			}
			
			if (! this.currentIterator.hasNext()) {
				this.currentIterator = END_ITERATOR;
			}
			
			this.loadNextNeighbor();
		}
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return this.nextNeighbor != END_NEIGHBOR;
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		if (this.nextNeighbor == END_NEIGHBOR) {
			throw new NoSuchElementException();
		}
		
		Object next = this.nextNeighbor;
		this.loadNextNeighbor();
		return next;
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException("remove()");
	}

	/**
	 * Return the immediate children of the specified object.
	 */
	protected Iterator neighbors(Object next) {
		return this.misterRogers.neighbors(next);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.currentIterator + ')';
	}
	
	
	//********** inner classes **********
	
	/**
	 * Used by <code>GraphIterator</code> to retrieve
	 * the immediate neighbors of a node in the graph.
	 * "These are the people in your neighborhood..."
	 */
	public interface MisterRogers 
	{
		/**
		 * Return the immediate neighbors of the specified object.
		 */
		Iterator neighbors(Object next);
		
		
		MisterRogers NULL_INSTANCE =
			new MisterRogers() {
				// return no neighbors
				public Iterator neighbors(Object next) {
					return NullIterator.instance();
				}
				
				public String toString() {
					return super.toString() + "(Hello, neighbor.)";
				}
			};
	}
}
