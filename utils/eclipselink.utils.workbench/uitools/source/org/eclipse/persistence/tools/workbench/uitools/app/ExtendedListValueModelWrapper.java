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
package org.eclipse.persistence.tools.workbench.uitools.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeListIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyListIterator;


/**
 * This wrapper extends a ListValueModel (or CollectionValueModel)
 * with fixed collections of items on either end.
 * 
 * NB: Be careful using or wrapping this list value model, since the
 * "extended" items may be unexpected by the client code or wrapper.
 */
public class ExtendedListValueModelWrapper
	extends ListValueModelWrapper
{
	/** the items "prepended" to the wrapped list */
	protected final List prefix;

	/** the items "appended" to the wrapped list */
	protected final List suffix;


	// ********** lots o' constructors **********

	/**
	 * Extend the specified list with a prefix and suffix.
	 */
	public ExtendedListValueModelWrapper(List prefix, ListValueModel listHolder, List suffix) {
		super(listHolder);
		this.prefix = new ArrayList(prefix);
		this.suffix = new ArrayList(suffix);
	}

	/**
	 * Extend the specified list with a prefix and suffix.
	 */
	public ExtendedListValueModelWrapper(Object prefix, ListValueModel listHolder, Object suffix) {
		this(Collections.singletonList(prefix), listHolder, Collections.singletonList(suffix));
	}

	/**
	 * Extend the specified list with a prefix.
	 */
	public ExtendedListValueModelWrapper(List prefix, ListValueModel listHolder) {
		this(prefix, listHolder, Collections.EMPTY_LIST);
	}

	/**
	 * Extend the specified list with a prefix.
	 */
	public ExtendedListValueModelWrapper(Object prefix, ListValueModel listHolder) {
		this(Collections.singletonList(prefix), listHolder, Collections.EMPTY_LIST);
	}

	/**
	 * Extend the specified list with a suffix.
	 */
	public ExtendedListValueModelWrapper(ListValueModel listHolder, List suffix) {
		this(Collections.EMPTY_LIST, listHolder, suffix);
	}

	/**
	 * Extend the specified list with a suffix.
	 */
	public ExtendedListValueModelWrapper(ListValueModel listHolder, Object suffix) {
		this(Collections.EMPTY_LIST, listHolder, Collections.singletonList(suffix));
	}

	/**
	 * Extend the specified list with a prefix containing a single null item.
	 */
	public ExtendedListValueModelWrapper(ListValueModel listHolder) {
		this(Collections.singletonList(null), listHolder, Collections.EMPTY_LIST);
	}


	// ********** ValueModel implementation **********

	/**
	 * @see ValueModel#getValue()
	 */
	public Object getValue() {
		// try to prevent backdoor modification of the lists
		return new ReadOnlyListIterator(
			new CompositeListIterator(
				this.prefix.listIterator(),
				(ListIterator) this.listHolder.getValue(),
				this.suffix.listIterator()
			)
		);
	}


	// ********** ListValueModel implementation **********

	/**
	 * @see ListValueModel#addItem(int, Object)
	 */
	public void addItem(int index, Object item) {
		this.addItems(index, Collections.singletonList(item));
	}

	/**
	 * @see ListValueModel#addItems(int, java.util.List)
	 */
	public void addItems(int index, List items) {
		if (items.size() == 0) {
			return;
		}
		int prefixSize = this.prefix.size();
		if (index < prefixSize) {
			throw new IllegalArgumentException("the prefix cannot be modified");
		}
		if (index > prefixSize + this.listHolder.size()) {
			throw new IllegalArgumentException("the suffix cannot be modified");
		}
		this.listHolder.addItems(index - prefixSize, items);
	}

	/**
	 * @see ListValueModel#removeItem(int)
	 */
	public Object removeItem(int index) {
		int prefixSize = this.prefix.size();
		if (index < prefixSize) {
			throw new IllegalArgumentException("the prefix cannot be modified");
		}
		if (index >= prefixSize + this.listHolder.size()) {
			throw new IllegalArgumentException("the suffix cannot be modified");
		}
		return this.listHolder.removeItem(index - prefixSize);
	}

	/**
	 * @see ListValueModel#removeItems(int, int)
	 */
	public List removeItems(int index, int length) {
		if (length == 0) {
			return Collections.EMPTY_LIST;
		}
		int prefixSize = this.prefix.size();
		if (index < prefixSize) {
			throw new IllegalArgumentException("the prefix cannot be modified");
		}
		if (index + length > prefixSize + this.listHolder.size()) {
			throw new IllegalArgumentException("the suffix cannot be modified");
		}
		return this.listHolder.removeItems(index - prefixSize, length);
	}

	/**
	 * @see ListValueModel#replaceItem(int, Object)
	 */
	public Object replaceItem(int index, Object item) {
		int prefixSize = this.prefix.size();
		if (index < prefixSize) {
			throw new IllegalArgumentException("the prefix cannot be modified");
		}
		if (index >= prefixSize + this.listHolder.size()) {
			throw new IllegalArgumentException("the suffix cannot be modified");
		}
		return this.listHolder.replaceItem(index - prefixSize, item);
	}

	/**
	 * @see ListValueModel#replaceItems(int, java.util.List)
	 */
	public List replaceItems(int index, List items) {
		if (items.size() == 0) {
			return Collections.EMPTY_LIST;
		}
		int prefixSize = this.prefix.size();
		if (index < prefixSize) {
			throw new IllegalArgumentException("the prefix cannot be modified");
		}
		if (index + items.size() > prefixSize + this.listHolder.size()) {
			throw new IllegalArgumentException("the suffix cannot be modified");
		}
		return this.listHolder.replaceItems(index - prefixSize, items);
	}

	/**
	 * @see ListValueModel#getItem(int)
	 */
	public Object getItem(int index) {
		int prefixSize = this.prefix.size();
		if (index < prefixSize) {
			return this.prefix.get(index);
		} else if (index >= prefixSize + this.listHolder.size()) {
			return this.suffix.get(index - (prefixSize + this.listHolder.size()));
		} else {
			return this.listHolder.getItem(index - prefixSize);
		}
	}

	/**
	 * @see ListValueModel#size()
	 */
	public int size() {
		return this.prefix.size() + this.listHolder.size() + this.suffix.size();
	}


	// ********** ListValueModelWrapper implementation **********

	/**
	 * @see ListValueModelWrapper#itemsAdded(org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent)
	 */
	protected void itemsAdded(ListChangeEvent e) {
		this.fireItemsAdded(e.cloneWithSource(this, VALUE, this.prefix.size()));
	}

	/**
	 * @see ListValueModelWrapper#itemsRemoved(org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent)
	 */
	protected void itemsRemoved(ListChangeEvent e) {
		this.fireItemsRemoved(e.cloneWithSource(this, VALUE, this.prefix.size()));
	}

	/**
	 * @see ListValueModelWrapper#itemsReplaced(org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent)
	 */
	protected void itemsReplaced(ListChangeEvent e) {
		this.fireItemsReplaced(e.cloneWithSource(this, VALUE, this.prefix.size()));
	}

	/**
	 * @see ListValueModelWrapper#listChanged(org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent)
	 */
	protected void listChanged(ListChangeEvent e) {
		this.fireListChanged(VALUE);
	}


	// ********** AbstractModel implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.tools.AbstractModel#toString(StringBuffer)
	 */
	public void toString(StringBuffer sb) {
		sb.append(this.prefix);
		sb.append(" ");
		super.toString(sb);
		sb.append(" ");
		sb.append(this.suffix);
	}

}
