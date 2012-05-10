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
package org.eclipse.persistence.tools.workbench.uitools.app.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ComboBoxModel;

import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This javax.swing.ComboBoxModel can be used to keep a ListDataListener
 * (e.g. a JComboBox) in synch with a ListValueModel (or a CollectionValueModel).
 * For combo boxes, the model object that holds the current selection is
 * typically a different model object than the one that holds the collection
 * of choices.
 * 
 * For example, a MWReference (the selectionOwner) has an attribute
 * "sourceTable" (the collectionOwner)
 * which holds on to a collection of MWDatabaseFields. When the selection
 * is changed this model will keep the listeners aware of the changes.
 * The inherited list model will keep its listeners aware of changes to the
 * collection model
 * 
 * In addition to the collection holder required by the superclass,
 * an instance of this ComboBoxModel must be supplied with a
 * selection holder, which is a PropertyValueModel that provides access
 * to the selection (typically a PropertyAspectAdapter).
 */
public class ComboBoxModelAdapter extends ListModelAdapter implements ComboBoxModel {

	protected PropertyValueModel selectionHolder;
	protected PropertyChangeListener selectionListener;


	// ********** constructors **********

	/**
	 * Constructor - the list holder and selection holder are required;
	 */
	public ComboBoxModelAdapter(ListValueModel listHolder, PropertyValueModel selectionHolder) {
		super(listHolder);
		if (selectionHolder == null) {
			throw new NullPointerException();
		}
		this.selectionHolder = selectionHolder;
	}

	/**
	 * Constructor - the collection holder and selection holder are required;
	 */
	public ComboBoxModelAdapter(CollectionValueModel collectionHolder, PropertyValueModel selectionHolder) {
		super(collectionHolder);
		if (selectionHolder == null) {
			throw new NullPointerException();
		}
		this.selectionHolder = selectionHolder;
	}


	// ********** initialization **********

	/**
	 * Extend to build the selection listener.
	 */
	protected void initialize() {
		super.initialize();
		this.selectionListener = this.buildSelectionListener();
	}

	protected PropertyChangeListener buildSelectionListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				// notify listeners that the selection has changed
				ComboBoxModelAdapter.this.fireSelectionChanged();
			}
			public String toString() {
				return "selection listener";
			}
		};
	}


	// ********** ComboBoxModel implementation **********

	/**
	 * @see javax.swing.ComboBoxModel#getSelectedItem()
	 */
	public Object getSelectedItem() {
		return this.selectionHolder.getValue();
	}

	/**
	 * @see javax.swing.ComboBoxModel#setSelectedItem(Object)
	 */
	public void setSelectedItem(Object selectedItem) {
		this.selectionHolder.setValue(selectedItem);
	}


	// ********** behavior **********

	/**
	 * Extend to engage the selection holder.
	 */
	protected void engageModel() {
		super.engageModel();
		this.selectionHolder.addPropertyChangeListener(ValueModel.VALUE, this.selectionListener);
	}

	/**
	 * Extend to disengage the selection holder.
	 */
	protected void disengageModel() {
		this.selectionHolder.removePropertyChangeListener(ValueModel.VALUE, this.selectionListener);
		super.disengageModel();
	}

	/**
	 * Notify the listeners that the selection has changed.
	 */
	protected void fireSelectionChanged() {
		// I guess this will work...
		this.fireContentsChanged(this, -1, -1);
	}

	public String toString() {
		return StringTools.buildToStringFor(this, this.selectionHolder + ":" + this.listHolder);
	}

}
