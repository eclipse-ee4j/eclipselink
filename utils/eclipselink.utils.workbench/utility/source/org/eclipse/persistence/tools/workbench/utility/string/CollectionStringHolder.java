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
package org.eclipse.persistence.tools.workbench.utility.string;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This implementation of StringHolder associates a collection of objects
 * with the string.
 */
public class CollectionStringHolder extends AbstractStringHolder {
	/** The objects associated with the string holder's string. */
	protected final Collection collection;

	/** The string associated with the string holder's objects. */
	protected final String string;


	public CollectionStringHolder(String string) {
		super();
		this.collection = new ArrayList();
		this.string = string;
	}

	public final String getString() {
		return this.string;
	}

	public boolean add(Object o) {
		return this.collection.add(o);
	}

	public boolean addAll(Collection c) {
		return this.collection.addAll(c);
	}

	public void clear() {
		this.collection.clear();
	}

	public boolean contains(Object o) {
		return this.collection.contains(o);
	}

	public boolean containsAll(Collection c) {
		return this.collection.containsAll(c);
	}

	public boolean isEmpty() {
		return this.collection.isEmpty();
	}

	public Iterator iterator() {
		return this.collection.iterator();
	}

	public boolean remove(Object o) {
		return this.collection.remove(o);
	}

	public boolean removeAll(Collection c) {
		return this.collection.removeAll(c);
	}

	public boolean retainAll(Collection c) {
		return this.collection.retainAll(c);
	}

	public int size() {
		return this.collection.size();
	}

	public Object[] toArray() {
		return this.collection.toArray();
	}

	public Object[] toArray(Object[] a) {
		return this.collection.toArray(a);
	}

}
