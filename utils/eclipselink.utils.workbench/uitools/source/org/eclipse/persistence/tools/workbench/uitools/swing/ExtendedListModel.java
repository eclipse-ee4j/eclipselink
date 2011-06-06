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
package org.eclipse.persistence.tools.workbench.uitools.swing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * This wrapper extends a ListModel with fixed lists of items on either end.
 * 
 * NB: Be careful using or wrapping this list model, since the "extended" 
 * items may be unexpected by the client code or wrapper.
 */
public class ExtendedListModel
	extends AbstractListModel
	implements ListModel
{
	// **************** Variables *********************************************
	
	/** The wrapped list model */
	protected ListModel listModel;
	
	/** The items "prepended" to the wrapped model */
	protected List prefix;
	
	/** The items "appended" to the wrapped model */
	protected List suffix;
	
	
	// **************** Constructors ******************************************
	
	/**
	 * Extend the specified list model with a prefix and suffix.
	 */
	public ExtendedListModel(List prefix, ListModel listModel, List suffix) {
		super();
		this.initialize(listModel);
		this.prefix = new ArrayList(prefix);
		this.suffix = new ArrayList(suffix);
	}
	
	/**
	 * Extend the specified list model with a singleton prefix and suffix.
	 */
	public ExtendedListModel(Object prefix, ListModel listModel, Object suffix) {
		this(Collections.singletonList(prefix), listModel, Collections.singletonList(suffix));
	}
	
	/**
	 * Extend the specified list model with a prefix.
	 */
	public ExtendedListModel(List prefix, ListModel listModel) {
		this(prefix, listModel, Collections.EMPTY_LIST);
	}

	/**
	 * Extend the specified list model with a singleton prefix.
	 */
	public ExtendedListModel(Object prefix, ListModel listModel) {
		this(Collections.singletonList(prefix), listModel, Collections.EMPTY_LIST);
	}
	
	/**
	 * Extend the specified list model with a suffix.
	 */
	public ExtendedListModel(ListModel listModel, List suffix) {
		this(Collections.EMPTY_LIST, listModel, suffix);
	}
	
	/**
	 * Extend the specified list with a singleton suffix.
	 */
	public ExtendedListModel(ListModel listModel, Object suffix) {
		this(Collections.EMPTY_LIST, listModel, Collections.singletonList(suffix));
	}
	
	/**
	 * Extend the specified list model with a prefix containing a single null item.
	 */
	public ExtendedListModel(ListModel listModel) {
		this(Collections.singletonList(null), listModel, Collections.EMPTY_LIST);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize(ListModel model) {
		this.listModel = model;
		this.listModel.addListDataListener(this.buildListDataListener());
	}
	
	protected ListDataListener buildListDataListener() {
		return new ListDataListener() {
			public void intervalAdded(ListDataEvent e) {
				ExtendedListModel.this.intervalAdded(e);
			}
			public void intervalRemoved(ListDataEvent e) {
				ExtendedListModel.this.intervalRemoved(e);
			}
			public void contentsChanged(ListDataEvent e) {
				ExtendedListModel.this.contentsChanged(e);
			}
			public String toString() {
				return "list data listener";
			}
		};
	}
	
	void intervalAdded(ListDataEvent e) {
		// Add prefix size to indices
		int prefixSize = this.prefix.size();
		this.fireIntervalAdded(this, e.getIndex0() + prefixSize, e.getIndex1() + prefixSize);
	}
	
	void intervalRemoved(ListDataEvent e) {
		// Add prefix size to indices
		int prefixSize = this.prefix.size();
		this.fireIntervalRemoved(this, e.getIndex0() + prefixSize, e.getIndex1() + prefixSize);
	}
	
	void contentsChanged(ListDataEvent e) {
		// Add prefix size to indices, unless the index is -1,
		// because for some reason, -1 is special
		int prefixSize = this.prefix.size();
		int newIndex0 = (e.getIndex0() == -1) ? -1 : e.getIndex0() + prefixSize;
		int newIndex1 = (e.getIndex1() == -1) ? -1 : e.getIndex1() + prefixSize;
		this.fireContentsChanged(this, newIndex0, newIndex1);
	}
	
	
	// **************** ListModel implementation ******************************
	
	public int getSize() {
		return this.prefix.size() + this.listModel.getSize() + this.suffix.size();
	}

	public Object getElementAt(int index) {
		int prefixSize = this.prefix.size();
		
		if (index < prefixSize) {
			return this.prefix.get(index);
		}
		else if (index >= prefixSize + this.listModel.getSize()) {
			return this.suffix.get(index - (prefixSize + this.listModel.getSize()));
		}
		else {
			return this.listModel.getElementAt(index - prefixSize);
		}
	}
}
