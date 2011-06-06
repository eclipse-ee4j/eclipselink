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
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.util.Iterator;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.app.AccessibleNode;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractTreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;


/**
 * This is the "real root node" for the JTree displayed by the framework's
 * navigator view. It is never displayed and should never be selected.
 */
final class FrameworkRootNode
	extends AbstractTreeNodeValueModel
	implements Displayable, AccessibleNode
{

	private FrameworkNodeManager frameworkNodeManager;

	/** these will be the application nodes for each plug-in "project" */
	private ListValueModel childrenModel;


	// ********** constructors/initialization **********

	FrameworkRootNode(FrameworkNodeManager frameworkNodeManager) {
		super();
		this.frameworkNodeManager = frameworkNodeManager;
		this.childrenModel = this.buildSortedModel();
	}

	// the list should be sorted
	private ListValueModel buildSortedModel() {
		return new SortedListValueModelAdapter(this.buildDisplayStringAdapter());
	}

	// the display string of each project node can change, affecting the sort order
	private ListValueModel buildDisplayStringAdapter() {
		return new ItemPropertyListValueModelAdapter(this.buildProjectNodesAdapter(), Displayable.DISPLAY_STRING_PROPERTY);
	}

	// the list of project nodes is held by the framework node manager and can change
	private CollectionValueModel buildProjectNodesAdapter() {
		return new CollectionAspectAdapter(FrameworkNodeManager.PROJECT_NODES_COLLECTION, (FrameworkNodeManager) this.getValue()) {
			protected Iterator getValueFromSubject() {
				return ((FrameworkNodeManager) this.subject).projectNodes();
			}
			public int size() {
				return ((FrameworkNodeManager) this.subject).projectNodesSize();
			}
		};
	}


	// ********** TreeNodeValueModel implementation **********

	public Object getValue() {
		return this.frameworkNodeManager;
	}

	public TreeNodeValueModel getParent() {
		return null;
	}

	public ListValueModel getChildrenModel() {
		return this.childrenModel;
	}



	// ********** AbstractTreeNodeValueModel implementation **********

	protected void engageValue() {
		// do nothing - the framework node manager never changes
	}

	protected void disengageValue() {
		// do nothing - the framework node manager never changes
	}


	// ********** Displayable implementation **********

	public String displayString() {
		return null;
	}

	public Icon icon() {
		return null;
	}


	// ********** AccessibleNode implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.app.AccessibleNode#accessibleName()
	 */
	public String accessibleName() {
		return "RootNode";
	}


	// ********** Comparable implementation **********

	public int compareTo(Object o) {
		return 0;	// should never happen...
	}

}
