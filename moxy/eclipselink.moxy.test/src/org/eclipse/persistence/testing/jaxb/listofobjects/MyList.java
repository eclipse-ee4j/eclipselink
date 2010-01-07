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
 *     Denise Smith  November 16, 2009 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

public class MyList<T> implements java.util.List<T>{
	private List internalList;
	
	public MyList(){
		internalList = new Stack();
	}
	
	public boolean add(Object e) {
		return internalList.add(e);
	}

	public void add(int index, Object element) {
		internalList.add(index, element);		
	}

	public boolean addAll(Collection c) {
		return internalList.addAll(c);
	}

	public boolean addAll(int index, Collection c) {
		return internalList.addAll(index, c); 		
	}

	public void clear() {
		internalList.clear();
	}

	public boolean contains(Object o) {
		return internalList.contains(o);
	}

	public boolean containsAll(Collection c) {
		return internalList.containsAll(c);
	}

	public T get(int index) {
		return (T) internalList.get(index);
	}

	public int indexOf(Object o) {
		return internalList.indexOf(o);

	}

	public boolean isEmpty() {
		return internalList.isEmpty();
	}

	public Iterator iterator() {
		return internalList.iterator();
	}

	public int lastIndexOf(Object o) {
		return internalList.lastIndexOf(o);
	}

	public ListIterator listIterator() {
		return internalList.listIterator();
	}

	public ListIterator listIterator(int index) {
		return internalList.listIterator(index);
	}

	public boolean remove(Object o) {
		return internalList.remove(o);
	}

	public T remove(int index) {
		return (T) internalList.remove(index);
	}

	public boolean removeAll(Collection c) {
		return internalList.removeAll(c);
	}

	public boolean retainAll(Collection c) {
		return internalList.retainAll(c);
	}

	public Object set(int index, Object element) {
		return internalList.set(index, element);
	}

	public int size() {
		return internalList.size();
	}

	public List subList(int fromIndex, int toIndex) {
		return internalList.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return internalList.toArray();
	}

	public Object[] toArray(Object[] a) {
		return internalList.toArray(a);
	}

}
