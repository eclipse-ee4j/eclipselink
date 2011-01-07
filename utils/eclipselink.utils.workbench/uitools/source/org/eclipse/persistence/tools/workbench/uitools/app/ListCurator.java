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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.Model;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyListIterator;


/**
 * This extension of AspectAdapter provides ListChange support
 * by adapting a subject's state change events to a minimum set
 * of list change events.
 * 
 * The typical subclass will override the following methods:
 * #getValueForRecord()
 * 	at the very minimum, override this method to return an iterator
 * 	on the subject's collection state; it does not need to be overridden if
 * 	#getValue() is overridden and its behavior changed
 */
public abstract class ListCurator 
	extends AspectAdapter
	implements ListValueModel
{	
	/** How the list looked before the last state change */
	private List record;
	
	/** A listener that listens for the subject's state to change */
	private StateChangeListener stateChangeListener;
	
	
	// **************** Constructors ******************************************
	
	/**
	 * Construct a Curator for the specified subject.
	 */
	protected ListCurator(Model subject) {
		super(subject);
	}
	
	/**
	 * Construct a curator for the specified subject holder.
	 * The subject holder cannot be null.
	 */
	protected ListCurator(ValueModel subjectHolder) {
		super(subjectHolder);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize() {
		super.initialize();
		this.record = new ArrayList();
		this.stateChangeListener = this.buildStateChangeListener();
	}
	
	/**
	 * The subject's state has changed, do inventory and report to listeners.
	 */
	protected StateChangeListener buildStateChangeListener() {
		return new StateChangeListener() {
			public void stateChanged(StateChangeEvent e) {
				ListCurator.this.submitInventoryReport();
			}
			public String toString() {
				return "state change listener";
			}
		};
	}
	
	
	// **************** ValueModel contract ***********************************
	
	public Object getValue() {
		return new ReadOnlyListIterator(this.record);
	}
	
	
	// **************** ListValueModel contract *******************************
	
	/**
	 * Return the item at the specified index of the subject's list aspect.
	 * @see ListValueModel#getItem(int)
	 */
	public Object getItem(int index) {
		return this.record.get(index);
	}
	
	/**
	 * Return the size of the subject's list aspect.
	 * @see ListValueModel#size()
	 */
	public int size() {
		return this.record.size();
	}
	
	/**
	 * Unsupported in this implementation
	 * @see ListValueModel.addItem(int, Object)
	 */
	public void addItem(int index, Object item) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Unsupported in this implementation
	 * @see ListValueModel.addItems(int, List)
	 */
	public void addItems(int index, List items) {
		for (int i = 0; i < items.size(); i++) {
			this.addItem(index + i, items.get(i));
		}
	}
	
	/**
	 * Unsupported in this implementation
	 * @see ListValueModel.removeItem(int)
	 */
	public Object removeItem(int index) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Unsupported in this implementation
	 * @see ListValueModel.removeItems(int, int)
	 */
	public List removeItems(int index, int length) {
		List removedItems = new ArrayList(length);
		for (int i = 0; i < length; i++) {
			removedItems.add(this.removeItem(index));
		}
		return removedItems;
	}
	
	/**
	 * Unsupported in this implementation
	 * @see ListValueModel.replaceItem(int, Object)
	 */
	public Object replaceItem(int index, Object item) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Unsupported in this implementation
	 * @see ListValueModel.replaceItems(int, List)
	 */
	public List replaceItems(int index, List items) {
		List replacedItems = new ArrayList(items.size());
		for (int i = 0; i < items.size(); i++) {
			replacedItems.add(this.replaceItem(index + i, items.get(i)));
		}
		return replacedItems;
	}
	
	
	// ***************** AspectAdapter contract *******************************
	
	/**
	 * Return whether there are any listeners.
	 */
	protected boolean hasListeners() {
		return this.hasAnyListChangeListeners(VALUE);
	}
	
	/**
	 * The aspect has changed, notify listeners appropriately.
	 */
	protected void fireAspectChange(Object oldValue, Object newValue) {
		this.fireListChanged(VALUE);
	}
	
	/**
	 * The subject is not null - add our listener.
	 */
	protected void engageNonNullSubject() {
		((Model) this.subject).addStateChangeListener(this.stateChangeListener);
		// synch our list *after* we start listening to the subject,
		// since its value might change when a listener is added
		CollectionTools.addAll(this.record, this.getValueForRecord());
	}
	
	/**
	 * The subject is not null - remove our listener.
	 */
	protected void disengageNonNullSubject() {
		((Model) this.subject).removeStateChangeListener(this.stateChangeListener);
		// clear out the list when we are not listening to the subject
		this.record.clear();
	}
	

	// **************** ListCurator contract **********************************
	
	/**
	 * This is intended to be different from #ValueModel.getValue().
	 * It is intended to be used only when the subject changes or the subject's state changes.
	 */
	protected abstract Iterator getValueForRecord();
	
	
	// **************** Behavior **********************************************
	
	void submitInventoryReport() {
		List newRecord = CollectionTools.list(this.getValueForRecord());
		int recordIndex = 0;
		
		// add items from the new record
		for (Iterator newItems = newRecord.iterator(); newItems.hasNext(); ) {
			this.inventoryNewItem(recordIndex, newItems.next());
			recordIndex ++;
		}
		
		// clean out items that are no longer in the new record
		for (recordIndex = 0; recordIndex < this.record.size(); ) {
			Object item = this.record.get(recordIndex);
			
			if (! newRecord.contains(item)) {
				this.removeItemFromInventory(recordIndex, item);
			}
			else {
				recordIndex ++;
			}
		}
	}
	
	private void inventoryNewItem(int recordIndex, Object newItem) {
		List rec = new ArrayList(this.record);
		
		if (recordIndex < rec.size() && rec.get(recordIndex).equals(newItem)) {
			return;
		}
		else if (! rec.contains(newItem)) {
			this.addItemToInventory(recordIndex, newItem);
		}
		else {
			this.removeItemFromInventory(recordIndex, rec.get(recordIndex));
			this.inventoryNewItem(recordIndex, newItem);
		}
	}
	
	private void addItemToInventory(int index, Object item) {
		this.record.add(index, item);
		this.fireItemAdded(VALUE, index, item);
	}
	
	private void removeItemFromInventory(int index, Object item) {
		this.record.remove(item);
		this.fireItemRemoved(VALUE, index, item);
	}

}
