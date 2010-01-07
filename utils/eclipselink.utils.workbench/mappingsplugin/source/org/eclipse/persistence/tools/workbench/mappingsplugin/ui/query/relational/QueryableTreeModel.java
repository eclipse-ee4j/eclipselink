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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.util.Collections;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.node.Node;


/**
 *	The tree model for the QueryableTree.  
 * Tree nodes are created when the method getChild() is called
 *  
 */
final class QueryableTreeModel implements TreeModel {

	private DefaultMutableTreeNode rootDescriptor;
	
	private Filter queryableFilter;
	
	//a list of queryables for the root node which not a QueryableTreeNode
	private List queryableObjects;
	
	QueryableTreeModel(DefaultMutableTreeNode root) {
		this(root, Filter.NULL_INSTANCE);
	}
	
	QueryableTreeModel(DefaultMutableTreeNode root, Filter queryableFilter) {
		super();
		this.rootDescriptor = root;
		this.queryableFilter = queryableFilter;
		this.queryableObjects = buildQueryableObjects();
	}
	
	protected Filter getQueryableFilter() {
		return this.queryableFilter;
	}
	
	public Object getRoot() {
		return this.rootDescriptor;
	}
	
	MWTableDescriptor getDescriptor() {
		return (MWTableDescriptor)((DefaultMutableTreeNode)getRoot()).getUserObject();
	}
	
	List buildQueryableObjects() {
        List list = getDescriptor().getQueryables(this.queryableFilter);
        Collections.sort(list, Node.DEFAULT_COMPARATOR);
		return list;
	}

	public Object getChild(Object parent, int index) {
		MWQueryable queryableObject;
		
		if (QueryableTreeNode.class.isAssignableFrom(parent.getClass())) {
			queryableObject = ((MWQueryable) ((QueryableTreeNode) parent).getUserObject()).subQueryableElementAt(index, this.queryableFilter);
		}
		
		else {//the root node(the descriptor) is the parent
			queryableObject = (MWQueryable) this.queryableObjects.get(index);
		}
			
		if (((DefaultMutableTreeNode) parent).getChildCount() > index) {
			return ((DefaultMutableTreeNode) parent).getChildAt(index);
		}
			
		QueryableTreeNode childNode = new QueryableTreeNode(queryableObject, !queryableObject.isLeaf(this.queryableFilter));
		((DefaultMutableTreeNode) parent).add(childNode);
		return childNode;
	}
	
	public int getChildCount(Object parent) {
		if (parent == getRoot())
			return this.queryableObjects.size();
		else
		{		
			return ((QueryableTreeNode)parent).getQueryable().subQueryableElements(this.queryableFilter).size();
		}
	}

	public boolean isLeaf(Object node) 
	{
		if (node == getRoot())
			return false;
		return ((QueryableTreeNode)node).isLeaf(this.queryableFilter);
	}

	public void valueForPathChanged(TreePath path, Object newValue) 
	{
	//The tree will not change, so i do not need to implement this method
	}

	public int getIndexOfChild(Object parent, Object child) 
	{
		if (parent == getRoot())
			return this.queryableObjects.indexOf(((QueryableTreeNode)child).getQueryable());
		else
		{		 
			List children =((QueryableTreeNode)child).getQueryable().subQueryableElements(this.queryableFilter);
			if (children != null && children.size() > 0)	
				return children.indexOf(((QueryableTreeNode)child).getQueryable());
		}
		return 0;

	}

	public void addTreeModelListener(TreeModelListener l) 
	{
		//The tree will not change, so i don't need any listeners
	}

	public void removeTreeModelListener(TreeModelListener l) 
	{
		//The tree will not change, so i don't need any listeners
	}

}
