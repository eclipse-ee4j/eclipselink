/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;

/**
 * Helper class that keeps an internal list in synch with the
 * list held by a list value model.
 */
public class SynchronizedList implements List, ListChangeListener, ListDataListener {
	private List synchList = new ArrayList();

	public SynchronizedList(ListValueModel listValueModel) {
		listValueModel.addListChangeListener(ValueModel.VALUE, this);
		for (Iterator stream = (ListIterator) listValueModel.getValue(); stream.hasNext(); ) {
			this.add(stream.next());
		}
	}

	public SynchronizedList(ListModel listModel) {
		listModel.addListDataListener(this);
		for (int i = 0; i < listModel.getSize(); i++) {
			this.add(i, listModel.getElementAt(i));
		}
	}


	// ********** List implementation **********

	/**
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, Object element) {
		this.synchList.add(index, element);
	}

	/**
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(Object o) {
		return this.synchList.add(o);
	}

	/**
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection c) {
		return this.synchList.addAll(c);
	}

	/**
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection c) {
		return this.synchList.addAll(index, c);
	}

	/**
	 * @see java.util.Collection#clear()
	 */
	public void clear() {
		this.synchList.clear();
	}

	/**
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return this.synchList.contains(o);
	}

	/**
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection c) {
		return this.synchList.containsAll(c);
	}

	/**
	 * @see java.util.List#get(int)
	 */
	public Object get(int index) {
		return this.synchList.get(index);
	}

	/**
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) {
		return this.synchList.indexOf(o);
	}

	/**
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty() {
		return this.synchList.isEmpty();
	}

	/**
	 * @see java.util.Collection#iterator()
	 */
	public Iterator iterator() {
		return this.synchList.iterator();
	}

	/**
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o) {
		return this.synchList.lastIndexOf(o);
	}

	/**
	 * @see java.util.List#listIterator()
	 */
	public ListIterator listIterator() {
		return this.synchList.listIterator();
	}

	/**
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator listIterator(int index) {
		return this.synchList.listIterator(index);
	}

	/**
	 * @see java.util.List#remove(int)
	 */
	public Object remove(int index) {
		return this.synchList.remove(index);
	}

	/**
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return this.synchList.remove(o);
	}

	/**
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection c) {
		return this.synchList.removeAll(c);
	}

	/**
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection c) {
		return this.synchList.retainAll(c);
	}

	/**
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public Object set(int index, Object element) {
		return this.synchList.set(index, element);
	}

	/**
	 * @see java.util.Collection#size()
	 */
	public int size() {
		return this.synchList.size();
	}

	/**
	 * @see java.util.List#subList(int, int)
	 */
	public List subList(int fromIndex, int toIndex) {
		return this.synchList.subList(fromIndex, toIndex);
	}

	/**
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray() {
		return this.synchList.toArray();
	}

	/**
	 * @see java.util.Collection#toArray(java.lang.Object[])
	 */
	public Object[] toArray(Object[] a) {
		return this.synchList.toArray(a);
	}


	// ********** ListChangeListener implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener#itemsAdded(org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent)
	 */
	public void itemsAdded(ListChangeEvent e) {
		int i = e.getIndex();
		for (Iterator stream = e.items(); stream.hasNext(); ) {
			this.synchList.add(i++, stream.next());
		}
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener#itemsRemoved(org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent)
	 */
	public void itemsRemoved(ListChangeEvent e) {
		int i = e.getIndex();
		for (Iterator stream = e.items(); stream.hasNext(); ) {
			stream.next();
			this.synchList.remove(i);
		}
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener#itemsReplaced(org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent)
	 */
	public void itemsReplaced(ListChangeEvent e) {
		int i = e.getIndex();
		for (ListIterator stream = e.items(); stream.hasNext(); ) {
			this.synchList.set(i++, stream.next());
		}
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener#listChanged(org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent)
	 */
	public void listChanged(ListChangeEvent e) {
		this.synchList.clear();
		CollectionTools.addAll(this.synchList, (Iterator) ((ListValueModel) e.getSource()).getValue());
	}


	// ********** ListDataListener implementation **********

	/**
	 * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
	 */
	public void contentsChanged(ListDataEvent e) {
		this.synchList.clear();
		ListModel lm = (ListModel) e.getSource();
		int size = lm.getSize();
		for (int i = 0; i < size; i++) {
			this.synchList.add(i, lm.getElementAt(i));
		}
	}

	/**
	 * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
	 */
	public void intervalAdded(ListDataEvent e) {
		ListModel lm = (ListModel) e.getSource();
		int start = Math.min(e.getIndex0(), e.getIndex1());
		int end = Math.max(e.getIndex0(), e.getIndex1());
		for (int i = start; i <= end; i++) {
			this.synchList.add(i, lm.getElementAt(i));
		}
	}

	/**
	 * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
	 */
	public void intervalRemoved(ListDataEvent e) {
		int start = Math.min(e.getIndex0(), e.getIndex1());
		int end = Math.max(e.getIndex0(), e.getIndex1());
		int length = end - start + 1;
		for (int i = 1; i <= length; i++) {
			this.synchList.remove(start);
		}
	}


	// ********** standard methods **********

	/**
	 * @see java.util.List#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return this.synchList.equals(o);
	}

	/**
	 * @see java.util.List#hashCode()
	 */
	public int hashCode() {
		return this.synchList.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.synchList.toString();
	}

}
