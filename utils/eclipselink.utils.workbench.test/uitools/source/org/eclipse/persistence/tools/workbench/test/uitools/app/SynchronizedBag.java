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
package org.eclipse.persistence.tools.workbench.test.uitools.app;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.Bag;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;

/**
 * Helper class that keeps an internal collection in synch with the
 * collection held by a collection value model.
 */
class SynchronizedBag implements Bag, CollectionChangeListener {

	private Bag synchBag = new HashBag();

	SynchronizedBag(CollectionValueModel cvm) {
		cvm.addCollectionChangeListener(ValueModel.VALUE, this);
	}


	// ********** Collection implementation **********

	/**
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(Object o) {
		return this.synchBag.add(o);
	}

	/**
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection c) {
		return this.synchBag.addAll(c);
	}

	/**
	 * @see java.util.Collection#clear()
	 */
	public void clear() {
		this.synchBag.clear();
	}

	/**
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return this.synchBag.contains(o);
	}

	/**
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection c) {
		return this.synchBag.containsAll(c);
	}

	/**
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty() {
		return this.synchBag.isEmpty();
	}

	/**
	 * @see java.util.Collection#iterator()
	 */
	public Iterator iterator() {
		return this.synchBag.iterator();
	}

	/**
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return this.synchBag.remove(o);
	}

	/**
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection c) {
		return this.synchBag.removeAll(c);
	}

	/**
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection c) {
		return this.synchBag.retainAll(c);
	}

	/**
	 * @see java.util.Collection#size()
	 */
	public int size() {
		return this.synchBag.size();
	}

	/**
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray() {
		return this.synchBag.toArray();
	}

	/**
	 * @see java.util.Collection#toArray(java.lang.Object[])
	 */
	public Object[] toArray(Object[] a) {
		return this.synchBag.toArray(a);
	}


	// ********** CollectionChangeListener implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener#itemsAdded(org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent)
	 */
	public void itemsAdded(CollectionChangeEvent e) {
		for (Iterator stream = e.items(); stream.hasNext(); ) {
			this.synchBag.add(stream.next());
		}
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener#itemsRemoved(org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent)
	 */
	public void itemsRemoved(CollectionChangeEvent e) {
		for (Iterator stream = e.items(); stream.hasNext(); ) {
			this.synchBag.remove(stream.next());
		}
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener#collectionChanged(org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent)
	 */
	public void collectionChanged(CollectionChangeEvent e) {
		this.synchBag.clear();
		CollectionTools.addAll(this.synchBag, (Iterator) ((CollectionValueModel) e.getSource()).getValue());
	}


	// ********** standard methods **********

	/**
	 * @see java.util.List#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return this.synchBag.equals(o);
	}

	/**
	 * @see java.util.List#hashCode()
	 */
	public int hashCode() {
		return this.synchBag.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.synchBag.toString();
	}

}
