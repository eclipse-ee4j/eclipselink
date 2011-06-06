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
package org.eclipse.persistence.tools.workbench.uitools.app;

import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.NullModel;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Implementation of ListValueModel that can be subclassed and used for
 * returning a list iterator on a static list, but still allows listeners to be added.
 * Listeners will NEVER be notified of any changes, because there should be none.
 * Subclasses need only implement the #getValue() method to
 * return a list iterator on the static values required by the client code. This class is
 * really only useful for simplifying the building of anonymous inner
 * classes that implement the ListValueModel interface:
 * 	private ListValueModel buildCacheUsageOptionsHolder() {
 * 		return new AbstractReadOnlyListValueModel() {
 * 			public Object getValue() {
 * 				return MWQuery.cacheUsageOptions();
 * 			}
 * 			public int size() {
 * 				return MWQuery.cacheUsageOptionsSize();
 * 			}
 * 		};
 * 	}
 */
public abstract class AbstractReadOnlyListValueModel
	extends NullModel
	implements ListValueModel
{
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	protected AbstractReadOnlyListValueModel() {
		super();
	}


	// ********** ListValueModel implementation **********

	/**
	 * @see ListValueModel#addItem(int, Object)
	 */
	public void addItem(int index, Object item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ListValueModel#addItems(int, java.util.List)
	 */
	public void addItems(int index, List items) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ListValueModel#removeItem(int)
	 */
	public Object removeItem(int index) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ListValueModel#removeItems(int, int)
	 */
	public List removeItems(int index, int length) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ListValueModel#replaceItem(int, Object)
	 */
	public Object replaceItem(int index, Object item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ListValueModel#replaceItems(int, java.util.List)
	 */
	public List replaceItems(int index, List items) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ListValueModel#getItem(int)
	 */
	public Object getItem(int index) {
		return CollectionTools.get((ListIterator) this.getValue(), index);
	}

	/**
	 * @see ListValueModel#size()
	 */
	public int size() {
		return CollectionTools.size((ListIterator) this.getValue());
	}


	// ********** Object overrides **********

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, CollectionTools.collection((ListIterator) this.getValue()));
	}

}
