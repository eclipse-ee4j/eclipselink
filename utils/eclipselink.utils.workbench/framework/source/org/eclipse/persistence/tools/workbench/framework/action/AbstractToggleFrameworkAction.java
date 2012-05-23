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
package org.eclipse.persistence.tools.workbench.framework.action;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;


/**
 * Updating framework action that implements the <code>ToggleFrameworkAction</code>
 * interface.  Provides a notion of toggle state in addition to the inherited enabled
 * state identity.   
 * 
 * @see org.eclipse.persistence.tools.workbench.framework.app.ToggleToolBarButton
 * @version 10.1.3
 */
public abstract class AbstractToggleFrameworkAction 
	extends AbstractEnablableFrameworkAction
	implements	ToggleFrameworkAction
{
	private boolean selectionState;
	private PropertyChangeListener selectionStateListener;
	
	/** the model properties that drive the action's toggle property */
	protected static final String[] DEFAULT_SELECTED_PROPERTY_NAMES = {};
	

	protected AbstractToggleFrameworkAction(WorkbenchContext context) {
		super(context);
	}

	public void setUp() {
		super.setUp();
		updateSelectionState();
	}
	
	
	/**
	 * The tree selection changed, update the action's selection state as necessary.
	 * The selected nodes are available via #selectedNodes().
	 */
	protected void updateSelectionState() {
		ApplicationNode[] selectedNodes = selectedNodes();
		for (int i = 0; i < selectedNodes.length; i++) {
			ApplicationNode selectedNode = selectedNodes[i];
			if (!this.shouldBeSelected(selectedNode)) {
				setSelected(false);
				return;
			}
		}

		setSelected(true);
	}
	
	/**
	 * Subclasses implement this and return whether this action should be selected given
	 * the current selected node's state.
	 * 
	 * This does not refer to the node selection, it refers to the action's selection state.
	 * If the action is used for a toolBar button it can appear selected.  
	 * If the action is used as a checkBox menu item the check will appear/disappear based on
	 * selection state. 
	 */
	protected abstract boolean shouldBeSelected(ApplicationNode selectedNode);
	
	
	public void setSelected(boolean selectionState) {
		boolean oldValue = this.selectionState;
		this.selectionState = selectionState;
		firePropertyChange(TOGGLE_STATE_PROPERTY, Boolean.valueOf(oldValue), Boolean.valueOf(this.selectionState));
	}

	public boolean isSelected() {
		return this.selectionState;
	}
	
	protected void engageListeners(AbstractApplicationNode node) {
		super.engageListeners(node);
		engageValueSelected(node);
	}
	
	protected void engageValueSelected(AbstractApplicationNode node) {
		this.engageValue(node, this.selectedPropertyNames(), getSelectionStateListener());
	}
	
	protected void disengageListeners(AbstractApplicationNode node) {
		super.disengageListeners(node);
		disengageValueSelected(node);		
	}

	protected void disengageValueSelected(AbstractApplicationNode node) {
		this.disengageValue(node, this.selectedPropertyNames(), getSelectionStateListener());
	}

	protected PropertyChangeListener getSelectionStateListener() {
		if (this.selectionStateListener == null) {
			this.selectionStateListener = buildSelectionStateListener();
		}
		return this.selectionStateListener;
	}
	
	protected PropertyChangeListener buildSelectionStateListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updateSelectionState();
			}
		};
	}
	
	/**
	 * Return the names of the value's properties that affect
	 * the action's selection state.
	 */
	protected String[] selectedPropertyNames() {
		return DEFAULT_SELECTED_PROPERTY_NAMES;
	}
}
