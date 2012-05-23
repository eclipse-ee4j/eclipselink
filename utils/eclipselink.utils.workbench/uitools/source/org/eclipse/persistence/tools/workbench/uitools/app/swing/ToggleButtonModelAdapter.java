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

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.event.ChangeListener;

import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This javax.swing.ButtonModel can be used to keep a listener
 * (e.g. a JCheckBox or a JRadioButton) in synch with a PropertyValueModel
 * on a boolean.
 */
public class ToggleButtonModelAdapter extends ToggleButtonModel {

	/**
	 * The default setting for the toggle button; for when the underlying model is null.
	 * The default [default value] is false (i.e. the toggle button is unchecked/empty).
	 */
	protected boolean defaultValue;

	/** A value model on the underlying model boolean. */
	protected PropertyValueModel booleanHolder;

	/**
	 * A listener that allows us to synchronize with
	 * changes made to the underlying model boolean.
	 */
	protected PropertyChangeListener booleanChangeListener;


	// ********** constructors **********

	/**
	 * Default constructor - initialize stuff.
	 */
	private ToggleButtonModelAdapter() {
		super();
		this.initialize();
	}

	/**
	 * Constructor - the boolean holder is required.
	 */
	public ToggleButtonModelAdapter(PropertyValueModel booleanHolder, boolean defaultValue) {
		this();
		if (booleanHolder == null) {
			throw new NullPointerException();
		}
		this.booleanHolder = booleanHolder;
		// postpone listening to the underlying model
		// until we have listeners ourselves...
		this.defaultValue = defaultValue;
	}

	/**
	 * Constructor - the boolean holder is required.
	 * The default value will be false.
	 */
	public ToggleButtonModelAdapter(PropertyValueModel booleanHolder) {
		this(booleanHolder, false);
	}


	// ********** initialization **********

	protected void initialize() {
		this.booleanChangeListener = this.buildBooleanChangeListener();
	}

	protected PropertyChangeListener buildBooleanChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				ToggleButtonModelAdapter.this.booleanChanged(e);
			}
			public String toString() {
				return "boolean listener";
			}
		};
	}


	// ********** ButtonModel implementation **********

	/**
	 * Extend to update the underlying model if necessary.
	 * @see javax.swing.ButtonModel#setSelected(boolean)
	 */
	public void setSelected(boolean b) {
		if (this.isSelected() != b) {	// stop the recursion!
			super.setSelected(b);//put the super call first, otherwise the following gets called twice
			this.booleanHolder.setValue(Boolean.valueOf(b));
		}
	}

	/**
	 * Extend to start listening to the underlying model if necessary.
	 * @see javax.swing.ButtonModel#addActionListener(java.awt.event.ActionListener)
	 */
	public void addActionListener(ActionListener l) {
		if (this.hasNoListeners()) {
			this.engageModel();
		}
		super.addActionListener(l);
	}

	/**
	 * Extend to stop listening to the underlying model if appropriate.
	 * @see javax.swing.ButtonModel#removeActionListener(java.awt.event.ActionListener)
	 */
	public void removeActionListener(ActionListener l) {
		super.removeActionListener(l);
		if (this.hasNoListeners()) {
			this.disengageModel();
		}
	}

	/**
	 * Extend to start listening to the underlying model if necessary.
	 * @see java.awt.ItemSelectable#addItemListener(java.awt.event.ItemListener)
	 */
	public void addItemListener(ItemListener l) {
		if (this.hasNoListeners()) {
			this.engageModel();
		}
		super.addItemListener(l);
	}

	/**
	 * Extend to stop listening to the underlying model if appropriate.
	 * @see java.awt.ItemSelectable#removeItemListener(java.awt.event.ItemListener)
	 */
	public void removeItemListener(ItemListener l) {
		super.removeItemListener(l);
		if (this.hasNoListeners()) {
			this.disengageModel();
		}
	}

	/**
	 * Extend to start listening to the underlying model if necessary.
	 * @see javax.swing.ButtonModel#addChangeListener(javax.swing.event.ChangeListener)
	 */
	public void addChangeListener(ChangeListener l) {
		if (this.hasNoListeners()) {
			this.engageModel();
		}
		super.addChangeListener(l);
	}

	/**
	 * Extend to stop listening to the underlying model if appropriate.
	 * @see javax.swing.ButtonModel#removeChangeListener(javax.swing.event.ChangeListener)
	 */
	public void removeChangeListener(ChangeListener l) {
		super.removeChangeListener(l);
		if (this.hasNoListeners()) {
			this.disengageModel();
		}
	}


	// ********** queries **********

	/**
	 * Return whether we have no listeners at all.
	 */
	protected boolean hasNoListeners() {
		return this.listenerList.getListenerCount() == 0;
	}

	protected boolean getDefaultValue() {
		return this.defaultValue;
	}


	// ********** behavior **********

	/**
	 * Synchronize with the specified value.
	 * If it is null, use the default value (which is typically false).
	 */
	protected void setSelected(Boolean value) {
		if (value == null) {
			this.setSelected(this.getDefaultValue());
		} else {
			this.setSelected(value.booleanValue());
		}
	}

	/**
	 * The underlying model has changed - synchronize accordingly.
	 */
	protected void booleanChanged(PropertyChangeEvent e) {
		this.setSelected((Boolean) e.getNewValue());
	}

	protected void engageModel() {
		this.booleanHolder.addPropertyChangeListener(ValueModel.VALUE, this.booleanChangeListener);
		this.setSelected((Boolean) this.booleanHolder.getValue());
	}

	protected void disengageModel() {
		this.booleanHolder.removePropertyChangeListener(ValueModel.VALUE, this.booleanChangeListener);
	}


	// ********** standard methods **********

	public String toString() {
		return StringTools.buildToStringFor(this, this.booleanHolder);
	}

}
