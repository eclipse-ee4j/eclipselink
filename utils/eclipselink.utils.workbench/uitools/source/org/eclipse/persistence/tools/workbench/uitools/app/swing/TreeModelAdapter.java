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
package org.eclipse.persistence.tools.workbench.uitools.app.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeListener;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This javax.swing.tree.TreeModel can be used to keep a TreeModelListener
 * (e.g. a JTree) in synch with a tree of TreeNodeValueModel objects. Unlike
 * javax.swing.tree.DefaultTreeModel, you do not add and remove nodes with
 * methods implemented here. You can add and remove nodes by adding and
 * removing them directly to/from the nodes (or, more typically, the domain
 * objects the nodes are wrapping and listening to).
 * 
 * Due to limitations in JTree, the root of the tree can never be null,
 * which, typically, should not be a problem. (If you want to display an empty
 * tree you can set the JTree's treeModel to null.)
 */
public class TreeModelAdapter extends AbstractTreeModel {

	/**
	 * A value model on the underlying tree's root node and its
	 * corresponding listener. This allows clients to swap out
	 * the entire tree. Due to limitations in JTree, the root should
	 * never be set to null while we have listeners.
	 */
	private PropertyValueModel rootHolder;
	private PropertyChangeListener rootListener;

	/**
	 * A listener that notifies us when a node's internal
	 * "state" changes (as opposed to the node's value or list of
	 * children), allowing us to forward notification to our listeners.
	 */
	private StateChangeListener nodeStateListener;

	/**
	 * A listener that notifies us when a node's "value"
	 * changes (as opposed to the node's state or list of
	 * children), allowing us to forward notification to our listeners.
	 * Typically, this will only happen with nodes that hold
	 * primitive data.
	 */
	private PropertyChangeListener nodeValueListener;

	/**
	 * A listener that notifies us when an underlying node's
	 * "list" of children changes, allowing us to keep our
	 * internal tree in synch with the underlying tree model.
	 */
	private ListChangeListener childrenListener;

	/* these attributes make up our internal tree */
	/**
	 * The root cannot be null while we have listeners, which is
	 * most of the time. The root is cached so we can disengage
	 * from it when it has been swapped out.
	 */
	private TreeNodeValueModel root;

	/**
	 * Map the nodes to their lists of children.
	 * We cache these so we can swap out the entire list of children
	 * when we receive a #listChanged() event (which does not include
	 * the items that were affected).
	 * @see EventHelper#rebuildChildren()
	 */
	IdentityHashMap childrenLists;

	/**
	 * Map the children models to their parents.
	 * We cache these so we can figure out the "real" source of the
	 * list change events (the parent).
	 * @see EventHelper#parent()
	 */
	IdentityHashMap parents;


	// ********** constructors **********

	/**
	 * Construct a tree model for the specified root.
	 */
	public TreeModelAdapter(PropertyValueModel rootHolder) {
		super();
		if (rootHolder == null) {
			throw new NullPointerException();
		}
		this.rootHolder = rootHolder;
	}

	/**
	 * Construct a tree model for the specified root.
	 */
	public TreeModelAdapter(TreeNodeValueModel root) {
		this(new ReadOnlyPropertyValueModel(root));
	}


	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		this.rootListener = this.buildRootListener();
		this.nodeStateListener = this.buildNodeStateListener();
		this.nodeValueListener = this.buildNodeValueListener();
		this.childrenListener = this.buildChildrenListener();
		this.childrenLists = new IdentityHashMap();
		this.parents = new IdentityHashMap();
	}

	private PropertyChangeListener buildRootListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				TreeModelAdapter.this.rootChanged();
			}
			public String toString() {
				return "root listener";
			}
		};
	}

	private PropertyChangeListener buildNodeValueListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				TreeModelAdapter.this.nodeChanged((TreeNodeValueModel) e.getSource());
			}
			public String toString() {
				return "node value listener";
			}
		};
	}

	private StateChangeListener buildNodeStateListener() {
		return new StateChangeListener() {
			public void stateChanged(StateChangeEvent e) {
				TreeModelAdapter.this.nodeChanged((TreeNodeValueModel) e.getSource());
			}
			public String toString() {
				return "node state listener";
			}
		};
	}

	private ListChangeListener buildChildrenListener() {
		return new ListChangeListener() {
			public void itemsAdded(ListChangeEvent e) {
				new EventChangePolicy(e).addChildren();
			}
			public void itemsRemoved(ListChangeEvent e) {
				new EventChangePolicy(e).removeChildren();
			}
			public void itemsReplaced(ListChangeEvent e) {
				new EventChangePolicy(e).replaceChildren();
			}
			public void listChanged(ListChangeEvent e) {
				new EventChangePolicy(e).rebuildChildren();
			}
			public String toString() {
				return "children listener";
			}
		};
	}


	// ********** TreeModel implementation **********

	/**
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	public Object getRoot() {
		return this.root;
	}

	/**
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parent, int index) {
		return ((TreeNodeValueModel) parent).getChild(index);
	}

	/**
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	public int getChildCount(Object parent) {
		return ((TreeNodeValueModel) parent).childrenSize();
	}

	/**
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object node) {
		return ((TreeNodeValueModel) node).isLeaf();
	}

	/**
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
		((TreeNodeValueModel) path.getLastPathComponent()).setValue(newValue);
	}

	/**
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
	 */
	public int getIndexOfChild(Object parent, Object child) {
		return ((TreeNodeValueModel) parent).indexOfChild((TreeNodeValueModel) child);
	}

	/**
	 * Extend to start listening to the underlying model if necessary.
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void addTreeModelListener(TreeModelListener l) {
		if (this.hasNoTreeModelListeners()) {
			this.engageModel();
		}
		super.addTreeModelListener(l);
	}

	/**
	 * Extend to stop listening to the underlying model if appropriate.
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void removeTreeModelListener(TreeModelListener l) {
		super.removeTreeModelListener(l);
		if (this.hasNoTreeModelListeners()) {
			this.disengageModel();
		}
	}


	// ********** behavior **********

	/**
	 * Listen to the root and all the other nodes
	 * in the underlying tree model.
	 */
	private void engageModel() {
		this.rootHolder.addPropertyChangeListener(ValueModel.VALUE, this.rootListener);
		this.root = (TreeNodeValueModel) this.rootHolder.getValue();
		if (this.root == null) {
			throw new NullPointerException();	// the root cannot be null while we have listeners
		}
		this.engageNode(this.root);
		this.addRoot();
	}

	/**
	 * Add the root and all of the nodes to the underlying tree.
	 */
	private void addRoot() {
		this.addNode(0, this.root);
	}

	/**
	 * Stop listening to the root and all the other
	 * nodes in the underlying tree model.
	 */
	private void disengageModel() {
		this.removeRoot();
		this.disengageNode(this.root);
		this.root = null;
		this.rootHolder.removePropertyChangeListener(ValueModel.VALUE, this.rootListener);
	}

	/**
	 * Remove the root and all of the nodes from the underlying tree.
	 */
	private void removeRoot() {
		this.removeNode(0, this.root);
	}

	/**
	 * The root has been swapped.
	 * This method is a bit gnarly because the API for notifying listeners
	 * that the root has changed is a bit inconsistent with that used for
	 * non-root nodes.
	 */
	void rootChanged() {
		TreeNodeValueModel newRoot = (TreeNodeValueModel) this.rootHolder.getValue();
		if (newRoot == null) {
			throw new NullPointerException();	// the root cannot be null while we have listeners
		}
		// remove all the current root's children from the tree
		// and remove the it from the internal tree
		this.removeRoot(); 

		// save the old root and swap in the new root
		TreeNodeValueModel oldRoot = this.root;
		this.root = newRoot;

		// we must be listening to both the old and new roots when we fire the event
		// because their values can be affected by whether they have listeners
		this.engageNode(this.root);
		this.fireTreeRootReplaced(this.root);
		// now we can stop listening to the old root
		this.disengageNode(oldRoot);

		// add the new root to the internal tree and
		// add all its children to the tree also
		this.addRoot();
	}

	/**
	 * Either the "value" or the "state" of the specified node has changed,
	 * forward notification to our listeners.
	 */
	void nodeChanged(TreeNodeValueModel node) {
		TreeNodeValueModel parent = node.getParent();
		if (parent == null) {
			this.fireTreeRootChanged(node);
		} else {
			this.fireTreeNodeChanged(parent.path(), parent.indexOfChild(node), node);
		}
	}

	/**
	 * Listen to the nodes, notify our listeners that the nodes were added,
	 * and then add the nodes to our internal tree.
	 * We must listen to the nodes before notifying anybody, because
	 * adding a listener can change the value of a node.
	 */
	void addChildren(Object[] path, int[] childIndices, Object[] children) {
		int len = childIndices.length;
		for (int i = 0; i < len; i++) {
			this.engageNode((TreeNodeValueModel) children[i]);
		}
		this.fireTreeNodesInserted(path, childIndices, children);
		for (int i = 0; i < len; i++) {
			this.addNode(childIndices[i], (TreeNodeValueModel) children[i]);
		}
	}

	/**
	 * Listen to the node and its children model.
	 */
	private void engageNode(TreeNodeValueModel node) {
		node.addStateChangeListener(this.nodeStateListener);
		node.addPropertyChangeListener(ValueModel.VALUE, this.nodeValueListener);
		node.getChildrenModel().addListChangeListener(ValueModel.VALUE, this.childrenListener);
	}

	/**
	 * Add the node to our internal tree;
	 * then recurse down through the node's children,
	 * adding them to the internal tree also.
	 */
	private void addNode(int index, TreeNodeValueModel node) {
		this.addNodeToInternalTree(node.getParent(), index, node, node.getChildrenModel());
		new NodeChangePolicy(node).addChildren();
	}

	/**
	 * Add the specified node to our internal tree.
	 */
	private void addNodeToInternalTree(TreeNodeValueModel parent, int index, TreeNodeValueModel node, ListValueModel childrenModel) {
		List siblings = (List) this.childrenLists.get(parent);
		if (siblings == null) {
			siblings = new ArrayList();
			this.childrenLists.put(parent, siblings);
		}
		siblings.add(index, node);

		this.parents.put(childrenModel, node);
	}

	/**
	 * Remove nodes from our internal tree, notify our listeners that the
	 * nodes were removed, then stop listening to the nodes.
	 * We must listen to the nodes until after notifying anybody, because
	 * removing a listener can change the value of a node.
	 */
	void removeChildren(Object[] path, int[] childIndices, Object[] children) {
		int len = childIndices.length;
		for (int i = 0; i < len; i++) {
			// the indices slide down a notch each time we remove a child
			this.removeNode(childIndices[i] - i, (TreeNodeValueModel) children[i]);
		}
		this.fireTreeNodesRemoved(path, childIndices, children);
		for (int i = 0; i < len; i++) {
			this.disengageNode((TreeNodeValueModel) children[i]);
		}
	}

	/**
	 * First, recurse down through the node's children,
	 * removing them from our internal tree;
	 * then remove the node itself from our internal tree.
	 */
	private void removeNode(int index, TreeNodeValueModel node) {
		new NodeChangePolicy(node).removeChildren();
		this.removeNodeFromInternalTree(node.getParent(), index, node, node.getChildrenModel());
	}

	/**
	 * Remove the specified node from our internal tree.
	 */
	private void removeNodeFromInternalTree(TreeNodeValueModel parent, int index, TreeNodeValueModel node, ListValueModel childrenModel) {
		this.parents.remove(childrenModel);

		List siblings = (List) this.childrenLists.get(parent);
		siblings.remove(index);
		if (siblings.isEmpty()) {
			this.childrenLists.remove(parent);
		}
	}

	/**
	 * Stop listening to the node and its children model.
	 */
	private void disengageNode(TreeNodeValueModel node) {
		node.getChildrenModel().removeListChangeListener(ValueModel.VALUE, this.childrenListener);
		node.removePropertyChangeListener(ValueModel.VALUE, this.nodeValueListener);
		node.removeStateChangeListener(this.nodeStateListener);
	}


	// ********** standard methods **********

	public String toString() {
		return StringTools.buildToStringFor(this, this.root);
	}


// ********** inner classes **********

/**
 * Coalesce some of the common change policy behavior.
 */
private abstract class ChangePolicy {

	ChangePolicy() {
		super();
	}

	/**
	 * Add the current set of children.
	 */
	void addChildren() {
		TreeModelAdapter.this.addChildren(this.parent().path(), this.childIndices(), this.childArray());
	}

	/**
	 * Remove the current set of children.
	 */
	void removeChildren() {
		TreeModelAdapter.this.removeChildren(this.parent().path(), this.childIndices(), this.childArray());
	}

	/**
	 * Return an array of the indices of the current set of children,
	 * which should be contiguous.
	 */
	int[] childIndices() {
		return this.buildIndices(this.childrenStartIndex(), this.childrenSize());
	}

	/**
	 * Return an array of the current set of children.
	 */
	Object[] childArray() {
		return this.buildArray(this.children(), this.childrenSize());
	}

	/**
	 * Build an array to hold the elements in the specified iterator.
	 * If they are different sizes, something is wrong...
	 */
	Object[] buildArray(Iterator stream, int size) {
		Object[] array = new Object[size];
		for (int i = 0; stream.hasNext(); i++) {
			array[i] = stream.next();
		}
		return array;
	}

	/**
	 * Return a set of indices, starting at zero and
	 * continuing for the specified size.
	 */
	int[] buildIndices(int size) {
		return buildIndices(0, size);
	}

	/**
	 * Return a set of indices, starting at the specified index and
	 * continuing for the specified size.
	 */
	private int[] buildIndices(int start, int size) {
		int[] indices = new int[size];
		int index = start;
		for (int i = 0; i < size; i++) {
			indices[i] = index++;
		}
		return indices;
	}

	/**
	 * Return the parent of the current set of children.
	 */
	abstract TreeNodeValueModel parent();

	/**
	 * Return the starting index for the current set of children.
	 */
	abstract int childrenStartIndex();

	/**
	 * Return the size of the current set of children.
	 */
	abstract int childrenSize();

	/**
	 * Return an interator on the current set of children.
	 */
	abstract Iterator children();

}


/**
 * Wraps a ListChangeEvent for adding, removing, replacing,
 * and changing children.
 */
private class EventChangePolicy extends ChangePolicy {
	private ListChangeEvent event;

	EventChangePolicy(ListChangeEvent event) {
		this.event = event;
	}

	/**
	 * Map the ListChangeEvent's source to the corresponding parent.
	 */
	TreeNodeValueModel parent() {
		return (TreeNodeValueModel) TreeModelAdapter.this.parents.get(this.event.getSource());
	}

	/**
	 * The ListChangeEvent's item index is the children start index.
	 */
	int childrenStartIndex() {
		return this.event.getIndex();
	}

	/**
	 * The ListChangeEvent's size is the children size.
	 */
	int childrenSize() {
		return this.event.size();
	}

	/**
	 * The ListChangeEvent's items are the children.
	 */
	Iterator children() {
		return this.event.items();
	}

	/**
	 * Remove the old nodes and add the new ones.
	 */
	void replaceChildren() {
		Object[] path = this.parent().path();
		int[] childIndices = this.childIndices();
		TreeModelAdapter.this.removeChildren(path, childIndices, this.replacedChildren());
		TreeModelAdapter.this.addChildren(path, childIndices, this.childArray());
	}

	/**
	 * Remove all the old nodes and add all the new nodes.
	 */
	void rebuildChildren() {
		TreeNodeValueModel parent = this.parent();
		Object[] path = parent.path();
		List childrenList = (List) TreeModelAdapter.this.childrenLists.get(parent);
		int[] childIndices = this.buildIndices(childrenList.size());
		Object[] childArray = this.buildArray(childrenList.iterator(), childrenList.size());
		TreeModelAdapter.this.removeChildren(path, childIndices, childArray);

		childIndices = this.buildIndices(parent.getChildrenModel().size());
		childArray = this.buildArray((Iterator) parent.getChildrenModel().getValue(), parent.childrenSize());
		TreeModelAdapter.this.addChildren(path, childIndices, childArray);
	}

	/**
	 * The ListChangeEvent's replaced items are the replaced children.
	 */
	Object[] replacedChildren() {
		return this.buildArray(this.event.replacedItems(), this.event.size());
	}

}


/**
 * Wraps a TreeNodeValueModel for adding and removing its children.
 */
private class NodeChangePolicy extends ChangePolicy {
	private TreeNodeValueModel node;

	NodeChangePolicy(TreeNodeValueModel node) {
		this.node = node;
	}

	/**
	 * The node itself is the parent.
	 */
	TreeNodeValueModel parent() {
		return this.node;
	}

	/**
	 * Since we will always be dealing with all of the node's
	 * children, the children start index is always zero.
	 */
	int childrenStartIndex() {
		return 0;
	}

	/**
	 * Since we will always be dealing with all of the node's
	 * children, the children size is always equal to the size
	 * of the children model.
	 */
	int childrenSize() {
		return this.node.getChildrenModel().size();
	}

	/**
	 * Since we will always be dealing with all of the node's
	 * children, the children are all the objects held by
	 * the children model.
	 */
	Iterator children() {
		return (Iterator) this.node.getChildrenModel().getValue();
	}

}

}
