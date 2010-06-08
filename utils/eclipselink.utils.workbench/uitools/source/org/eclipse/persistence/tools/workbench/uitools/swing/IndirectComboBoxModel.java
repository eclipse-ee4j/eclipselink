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
package org.eclipse.persistence.tools.workbench.uitools.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractListModel;
import javax.swing.event.ListDataListener;

import org.eclipse.persistence.tools.workbench.uitools.app.NullPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullListIterator;


/**
 * A basic implementation of ComboBoxModel that does not maintain
 * its list of selectable elements.  Instead it retrieves them 
 * each time they are asked for.
 * 
 * To use
 *  - override both #getSize() and #getElementAt(int)
 * or
 *  - override #listValue()  
 * 		(#getSize() or #getElementAt(int) may then also be overridden)
 * or
 * 	- override #listValueFromSubject(Object)
 * 		If this is used, a listSubjectHolder must also be used
 * 		(#getSize() or #getElementAt(int) may then also be overridden)
 */
public abstract class IndirectComboBoxModel
	extends AbstractListModel
	implements CachingComboBoxModel
{
	
	/**
	 * This holds the selected item.
	 */
	private PropertyValueModel selectedItemHolder;
	
	/** 
	 * This listens for when the selected item changes and 
	 * notifies all list data listeners, such as JComboBoxes.
	 */
	private PropertyChangeListener selectedItemListener;
	
	/** 
	 * This holds the list "subject". The "subject" supplies
	 * us with the list of items for the combo box.
	 */
	private ValueModel listSubjectHolder;
	
	/**
	 * This listens for when the list "subject" changes and
	 * updates all list data listeners, such as JComboBoxes.
	 */
	private PropertyChangeListener listSubjectListener;
	
	/**
	 * Cache the list size so that we may determine if
	 * the list has changed.
	 */
	private int cachedSize;
	
    /**
     * Cache the list so it is not built unecessarily.  Users
     * of IndirectComboBoxModel determine when to cache and 
     * uncache using the methods cacheList() and uncacheList()
     */
	private List cachedList;
    
	// ********** constructors/initialization **********
	
	/**
	 * Construct an indirect combo box model for the specified
	 * selected item. The list will be empty.
	 */
	public IndirectComboBoxModel(PropertyValueModel selectedItemHolder) {
		this(selectedItemHolder, NullPropertyValueModel.instance());
	}
	
	/**
	 * Construct an indirect combo box model for the specified
	 * selected item and list.
	 */
	public IndirectComboBoxModel(PropertyValueModel selectedItemHolder, ValueModel listSubjectHolder) {
		super();
		if (selectedItemHolder == null || listSubjectHolder == null) {
			throw new NullPointerException();
		}
		
		this.selectedItemHolder = selectedItemHolder;
		this.selectedItemListener = this.buildSelectedItemListener();
		
		this.listSubjectHolder = listSubjectHolder;
		this.listSubjectListener = this.buildListSubjectListener();
	}
	
	protected PropertyChangeListener buildSelectedItemListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				IndirectComboBoxModel.this.fireSelectedItemChanged();
			}
			public String toString() {
				return "selected item listener";
			}
		};
	}
	
	protected PropertyChangeListener buildListSubjectListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				IndirectComboBoxModel.this.fireContentsChanged();
			}
			public String toString() {
				return "list subject listener";
			}
		};
	}
	
	
	// ********** ComboBoxModel implementation **********
	
	/**
	 * @see javax.swing.ComboBoxModel#setSelectedItem(Object)
	 */
	public void setSelectedItem(Object newSelectedItem) {
		this.selectedItemHolder.setValue(newSelectedItem);
	}
	
	/**
	 * @see javax.swing.ComboBoxModel#getSelectedItem()
	 */
	public Object getSelectedItem() {
		return this.selectedItemHolder.getValue();
	}
	
	
	// ********** ListModel implementation **********
	
	/**
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
		int size = this.listSubjectHolder.getValue() == null ? 0 : listSizeFromSubject(this.listSubjectHolder.getValue());
		// if the size has changed, notify listeners
		if (this.cachedSize != size) {
			this.cachedSize = size;
			this.fireContentsChanged();
		}
		
		return size;
	}

	/**
	 * Return the size of the subject's collection aspect.
	 * At this point we can be sure that the subject is not null.
	 * @see #size()
	 */
	protected int listSizeFromSubject(Object listSubject) {
		return CollectionTools.size(this.listValue());
	}

	
	/**
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) {
		return CollectionTools.get(this.listValue(), index);
	}
	
	/**
	 * Extend to engage the underlying models.
	 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
	 */
	public void addListDataListener(ListDataListener l) {
		if (this.hasNoListDataListeners()) {
			this.engageModel();
		}
		super.addListDataListener(l);
	}
	
	/**
	 * Extend to dis-engage the underlying models.
	 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
	 */
	public void removeListDataListener(ListDataListener l) {
		super.removeListDataListener(l);
		if (this.hasNoListDataListeners()) {
			this.disengageModel();
		}
	}
	
	
	// ********** internal behavior **********
	
	/**
	 * Notify listeners that the selection has changed.
	 */
	protected void fireSelectedItemChanged() {
		// this is what DefaultComboBoxModel does, so we'll do it, too
	    this.fireContentsChanged(this, -1, -1);
	}
	
	/**
	 * Notify listeners that the entire list contents have changed.
	 */
	protected void fireContentsChanged() {
		// Using the cached size so that we don't necessarily calculate the new size just yet
		this.fireContentsChanged(this, 0, this.cachedSize - 1);
	}
	
	/**
	 * Return the items in the list. The "subject" may be null.
	 */
	protected ListIterator listValue() {
        if (this.cachedList != null) {
            return this.cachedList.listIterator();
        }
		Object listSubject = this.listSubjectHolder.getValue();
		if (listSubject == null) {
			return NullListIterator.instance();
		}
		return this.listValueFromSubject(listSubject);
	}
	
	/**
	 * Return the items in the list. The specified "subject" will not be null.
	 * Typically this method will be overridden by subclasses to
	 * return a list derived from the specified "subject".
	 */
	protected ListIterator listValueFromSubject(Object subject) {
		return NullListIterator.instance();
	}
	
	/**
	 * Return whether the model has no listeners.
	 */
	protected boolean hasNoListDataListeners() {
		return this.getListDataListeners().length == 0;
	}
	
	/**
	 * Return whether the model has any listeners.
	 */
	protected boolean hasListDataListeners() {
		return ! this.hasNoListDataListeners();
	}
	
	/**
	 * Start listening to the selected item and "subject" holders.
	 */
	protected void engageModel() {
		this.selectedItemHolder.addPropertyChangeListener(ValueModel.VALUE, this.selectedItemListener);
		this.listSubjectHolder.addPropertyChangeListener(ValueModel.VALUE, this.listSubjectListener);
	}
	
	/**
	 * Stop listening to the selected item and "subject" holders.
	 */
	protected void disengageModel() {
		this.listSubjectHolder.removePropertyChangeListener(ValueModel.VALUE, this.listSubjectListener);
		this.selectedItemHolder.removePropertyChangeListener(ValueModel.VALUE, this.selectedItemListener);
	}
        
    /**
     * @see CachingComboBoxModel#cacheList()
     */
    public void cacheList() {
        if (isCached()) {
            throw new IllegalArgumentException("List is already Cached, see isCached()");
        }
        this.cachedList = CollectionTools.list(listValue());
    }
    
    /**
     * @see CachingComboBoxModel#uncacheList()
     */
    public void uncacheList() {
        if (isCached()) {
            this.cachedList = null;
        }
        else {
            throw new IllegalArgumentException("List is not cached");
        }
        
    }
    
    /**
     * @see CachingComboBoxModel#isCached()
     */
    public boolean isCached() {
        return this.cachedList != null;
    }
    
}
