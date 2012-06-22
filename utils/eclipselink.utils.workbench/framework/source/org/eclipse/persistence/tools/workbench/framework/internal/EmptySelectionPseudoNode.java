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
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.ShellWorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.EmptyPropertiesPage;


/**
 * This is a pseudo-node that is "selected" whenever no nodes
 * on the tree are selected. It supplies a properties page that
 * displays nothing.
 * 
 * There is one empty selection "pseudo-node" per workspace view.
 */
final class EmptySelectionPseudoNode
	extends AbstractApplicationNode
{
	/** cache the properties page so we don't have to rebuild it repeatedly */
	private Component emptyPropertiesPage;


	// ************ constructor ************

	/**
	 * use the super-secret, framework-only constructor...
	 */
	public EmptySelectionPseudoNode(ApplicationContext context) {
		super(context);
		this.emptyPropertiesPage = new EmptyPropertiesPage(new ShellWorkbenchContext(this.getApplicationContext()));
	}


	// ********** AbstractTreeNodeValueModel overrides **********

	public boolean equals(Object o) {
		return this == o;
	}

	public int hashCode() {
		return System.identityHashCode(this);
	}


	// ********** AbstractApplicationNode overrides **********

	/**
	 * this node does not have a value; do not call this method
	 * willy-nilly on a collection of heterogeneous nodes - narrow
	 * down the collection to the relevant nodes  ~bjv
	 */
	public Object getValue() {
		throw new UnsupportedOperationException();
	}

	/**
	 * the problems view will want to listen to our list of problems,
	 * but we don't have any
	 */
	protected void engageValueBranchProblems() {
		// do nothing since we don't have a value
	}
	protected void disengageValueBranchProblems() {
		// do nothing since we don't have a value
	}

	public void toString(StringBuffer sb) {
		sb.append("[empty]");
	}


	// ********** ApplicationNode implementation **********

	public GroupContainerDescription buildMenuDescription(WorkbenchContext workbenchContext) {
		throw new UnsupportedOperationException();
	}

	public GroupContainerDescription buildToolBarDescription(WorkbenchContext workbenchContext) {
		throw new UnsupportedOperationException();
	}


	// ********** EditorNode implementation **********

	public Component propertiesPage(WorkbenchContext workbenchContext) {
		return this.emptyPropertiesPage;
	}

	public void releasePropertiesPage(Component propertiesPage) {
		// do nothing
	}

}
