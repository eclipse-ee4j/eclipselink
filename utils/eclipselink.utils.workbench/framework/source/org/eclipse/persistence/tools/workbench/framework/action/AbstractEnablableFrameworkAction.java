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
package org.eclipse.persistence.tools.workbench.framework.action;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;



/**
 * Subclass this action if you would like to update the enabled state of
 * your action based on specified model properties.
 */
public abstract class AbstractEnablableFrameworkAction extends AbstractFrameworkAction {
													
	protected ApplicationNode[] selectedNodes;
	
	private PropertyChangeListener enabledStateListener;
	
	/** the model properties that drive the action's enabled property */
	protected static final String[] DEFAULT_ENABLED_PROPERTY_NAMES = {};

	protected AbstractEnablableFrameworkAction(WorkbenchContext context) {
		super(context);
	}

	/** 
	 * Short-circuit out if the action does not apply to even one of the selected Nodes.
	 * Subclasses may override this method if they would like to do something different.
	 * In the typical situation a subclass should only need to define
	 * shouldBeEnabled(ApplicationNode)
	 */
	protected void updateEnabledState()
	{
		for (int index = 0; index < this.selectedNodes.length; index++)
		{
			if (!shouldBeEnabled(this.selectedNodes[index])) {
				setEnabled(false);
				return;
			}
		}
		
		setEnabled(true);
	}	
	
	/**
	 * return whether the given node is in a state where this action could be
	 * performed
	 */
	protected abstract boolean shouldBeEnabled(ApplicationNode selectedNode);
	
	public void setUp() {
		super.setUp();
		this.selectedNodes = selectedNodes();
		engageListeners();
		updateEnabledState();
	}
	
	public void tearDown() {
		disengageListeners();
		this.selectedNodes = null;
		super.tearDown();
	}

	protected void engageListeners() {
		for (int i = 0; i < this.selectedNodes.length; i++) {
			this.engageListeners((AbstractApplicationNode) this.selectedNodes[i]);
		}	
	}
	
	protected void engageListeners(AbstractApplicationNode node) {
		this.engageValueEnabled(node);		
	}
	
	protected void disengageListeners() {
		for (int i = 0; i < this.selectedNodes.length; i++) {
			this.disengageListeners((AbstractApplicationNode) this.selectedNodes[i]);
		}		
	}
	
	protected void disengageListeners(AbstractApplicationNode node) {
		this.disengageValueEnabled(node);		
	}
	
	protected void engageValueEnabled(AbstractApplicationNode node) {
		this.engageValue(node, this.enabledPropertyNames(), this.getEnabledStateListener());
	}
	
	protected void engageValue(AbstractApplicationNode node, String[] propertyNames, PropertyChangeListener listener) {
		this.engage(node, propertyNames, listener);
	}

	protected void engage(AbstractApplicationNode node, String[] propertyNames, PropertyChangeListener listener) {
		for (int i = propertyNames.length; i-- > 0; ) {
			this.engage(node, propertyNames[i], listener);
		}
	}
	
	protected void engage(AbstractApplicationNode node, String propertyName, PropertyChangeListener listener) {
		node.addValuePropertyChangeListener(propertyName, listener);
	}

	protected void disengageValueEnabled(AbstractApplicationNode node) {
		this.disengageValue(node, this.enabledPropertyNames(), this.getEnabledStateListener());
	}
	
	protected void disengageValue(AbstractApplicationNode node, String[] propertyNames, PropertyChangeListener listener) {
		this.disengage(node, propertyNames, listener);
	}

	protected void disengage(AbstractApplicationNode node, String[] propertyNames, PropertyChangeListener listener) {
		for (int i = propertyNames.length; i-- > 0; ) {
			this.disengage(node, propertyNames[i], listener);
		}
	}
	
	protected void disengage(AbstractApplicationNode node, String propertyName, PropertyChangeListener listener) {
		node.removeValuePropertyChangeListener(propertyName, listener);
	}

	protected PropertyChangeListener getEnabledStateListener() {
		if (this.enabledStateListener == null) {
			this.enabledStateListener = this.buildEnabledStateListener();
		}
		return this.enabledStateListener;
	}
	
	protected PropertyChangeListener buildEnabledStateListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				AbstractEnablableFrameworkAction.this.updateEnabledState();
			}
		};
	}
	
	/**
	 * Return the names of the value's properties that affect
	 * the action's enabled state.
	 */
	protected String[] enabledPropertyNames() {
		return DEFAULT_ENABLED_PROPERTY_NAMES;
	}

}
