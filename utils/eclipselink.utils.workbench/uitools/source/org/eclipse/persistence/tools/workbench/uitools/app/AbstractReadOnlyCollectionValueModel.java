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

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.NullModel;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Implementation of CollectionValueModel that can be subclassed and used for
 * returning an iterator on a static collection, but still allows listeners to be added.
 * Listeners will NEVER be notified of any changes, because there should be none.
 * Subclasses need only implement the #getValue() method to
 * return an iterator on the static values required by the client code. This class is
 * really only useful for simplifying the building of anonymous inner
 * classes that implement the CollectionValueModel interface:
 * 	private CollectionValueModel buildCacheUsageOptionsHolder() {
 * 		return new AbstractReadOnlyCollectionValueModel() {
 * 			public Object getValue() {
 * 				return MWQuery.cacheUsageOptions();
 * 			}
 * 			public int size() {
 * 				return MWQuery.cacheUsageOptionsSize();
 * 			}
 * 		};
 * 	}
 */
public abstract class AbstractReadOnlyCollectionValueModel
	extends NullModel
	implements CollectionValueModel
{
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	protected AbstractReadOnlyCollectionValueModel() {
		super();
	}


	// ********** CollectionValueModel implementation **********

	/**
	 * @see CollectionValueModel#addItem(Object)
	 */
	public void addItem(Object item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see CollectionValueModel#addItems(java.util.Collection)
	 */
	public void addItems(Collection items) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see CollectionValueModel#removeItem(Object)
	 */
	public void removeItem(Object item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see CollectionValueModel#removeItems(java.util.Collection)
	 */
	public void removeItems(Collection items) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see CollectionValueModel#size()
	 */
	public int size() {
		return CollectionTools.size((Iterator) this.getValue());
	}


	// ********** Object overrides **********

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, CollectionTools.collection((Iterator) this.getValue()));
	}

}
