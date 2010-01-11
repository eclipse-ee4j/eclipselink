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
package org.eclipse.persistence.tools.workbench.uitools.app;

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.ChainIterator;


/**
 * Subclasses need only implement the following methods:
 * 
 * #getValue()
 *	    return the user-determined "value" of the node,
 *     i.e. the object "wrapped" by the node
 * 
 * #setValue(Object)
 *     set the user-determined "value" of the node,
 *     i.e. the object "wrapped" by the node;
 *     typically only overridden for nodes with "primitive" values
 * 
 * #getParent()
 *     return the parent of the node, which should be another
 *     TreeNodeValueModel
 * 
 * #getChildrenModel()
 *     return a ListValueModel for the node's children
 * 
 * #engageValue() and #disengageValue()
 *     override these methods to listen to the node's value if
 *     it can change in a way that should be reflected in the tree
 */
public abstract class AbstractTreeNodeValueModel
	extends AbstractModel
	implements TreeNodeValueModel
{


	// ********** constructors **********
	
	/**
	 * Default constructor.
	 */
	protected AbstractTreeNodeValueModel() {
		super();
	}
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#buildDefaultChangeSupport()
	 */
	protected ChangeSupport buildDefaultChangeSupport() {
		// this value model is allowed to fire state change events...
//		return new ValueModelChangeSupport(this);
		return super.buildDefaultChangeSupport();
	}


	// ********** extend AbstractModel implementation **********

	public void addStateChangeListener(StateChangeListener listener) {
		if (this.hasNoStateChangeListeners()) {
			this.engageValue();
		}
		super.addStateChangeListener(listener);
	}

	/**
	 * Begin listening to the node's value. If the state of the node changes
	 * in a way that should be reflected in the tree, fire a "state change" event.
	 * If the entire value of the node changes, fire a "value property change"
	 * event.
	 */
	protected abstract void engageValue();

	public void removeStateChangeListener(StateChangeListener listener) {
		super.removeStateChangeListener(listener);
		if (this.hasNoStateChangeListeners()) {
			this.disengageValue();
		}
	}

	/**
	 * Stop listening to the node's value.
	 * @see #engageValue()
	 */
	protected abstract void disengageValue();


	// ********** PropertyValueModel implementation **********
	
	/**
	 * @see PropertyValueModel#setValue(Object)
	 */
	public void setValue(Object value) {
		throw new UnsupportedOperationException();
	}


	// ********** TreeNodeValueModel implementation **********
	
	/**
	 * @see TreeNodeValueModel#path()
	 */
	public TreeNodeValueModel[] path() {
		List path = CollectionTools.reverseList(this.backPath());
		return (TreeNodeValueModel[]) path.toArray(new TreeNodeValueModel[path.size()]);
	}

	/**
	 * Return an iterator that climbs up the node's path,
	 * starting with, and including, the node
	 * and up to, and including, the root node.
	 */
	protected Iterator backPath() {
		return new ChainIterator(this) {
			protected Object nextLink(Object currentLink) {
				return ((TreeNodeValueModel) currentLink).getParent();
			}
		};
	}

	/**
	 * @see TreeNodeValueModel#getChild(int)
	 */
	public TreeNodeValueModel getChild(int index) {
		return (TreeNodeValueModel) this.getChildrenModel().getItem(index);
	}

	/**
	 * @see TreeNodeValueModel#childrenSize()
	 */
	public int childrenSize() {
		return this.getChildrenModel().size();
	}

	/**
	 * @see TreeNodeValueModel#indexOfChild(Object)
	 */
	public int indexOfChild(TreeNodeValueModel child) {
		ListValueModel children = this.getChildrenModel();
		int size = children.size();
		for (int i = 0; i < size; i++) {
			if (children.getItem(i) == child) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @see TreeNodeValueModel#isLeaf()
	 */
	public boolean isLeaf() {
		return this.getChildrenModel().size() == 0;
	}


	// ********** standard methods **********

	/**
	 * We implement #equals(Object) so that TreePaths containing these nodes
	 * will resolve properly when the nodes contain the same values. This is
	 * necessary because nodes are dropped and rebuilt willy-nilly when dealing
	 * with a sorted list of children; and this allows us to save and restore
	 * a tree's expanded paths. The nodes in the expanded paths that are
	 * saved before any modification (e.g. renaming a node) will be different
	 * from the nodes in the tree's paths after the modification, if the modification
	 * results in a possible change in the node sort order.  -bjv
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o.getClass() != this.getClass()) {
			return false;
		}
		AbstractTreeNodeValueModel other = (AbstractTreeNodeValueModel) o;
		return this.getValue().equals(other.getValue());
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return this.getValue().hashCode();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#toString(StringBuffer)
	 */
	public void toString(StringBuffer sb) {
		sb.append(this.getValue());
	}

}
