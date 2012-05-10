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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.DoubleClickMouseListener;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryableArgumentElement;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;



abstract class AttributeItemDialog extends AbstractDialog {

	private MWQuery query;
	private MWAttributeItem attributeItem;
	
	private QueryableTree queryableTree;
	private Filter traversableFilter;
	private Filter chooseableFilter;
	
	AttributeItemDialog(MWQuery query, MWAttributeItem attributeItem,  Filter traversableFilter, Filter chooseableFilter, WorkbenchContext context) {
		super(context);
		this.query = query;
		this.attributeItem = attributeItem;
		this.traversableFilter = traversableFilter;
		this.chooseableFilter = chooseableFilter;
		getOKAction().setEnabled(false);
	}
	
	AttributeItemDialog(MWQuery query, MWAttributeItem attributeItem, WorkbenchContext context) {
		this(query, attributeItem, null, null, context);
		this.traversableFilter = buildTraversableFilter();
		this.chooseableFilter = buildChooseableFilter();
	}
	
	protected Filter buildTraversableFilter() {
		return Filter.NULL_INSTANCE;
	}
	
	protected Filter buildChooseableFilter() {
		return Filter.NULL_INSTANCE;
	}
	
	protected abstract String titleKey();
    
    protected abstract String editTitleKey();

	private MWMappingDescriptor getDescriptor() {
		return this.query.getOwningDescriptor();
	}	
	
	protected MWQuery getQuery() {
		return this.query;
	}	
	
	protected MWAttributeItem getAttributeItem() {
		return this.attributeItem;
	}
	
	protected QueryableTree getQueryableTree() {
	    return this.queryableTree;
	}

    protected void initializeContentPane() {
        super.initializeContentPane();
		if (this.attributeItem != null) {
		    initializeEditMode(this.attributeItem );
            setTitle(resourceRepository().getString(editTitleKey()));
		}
        else {
            setTitle(resourceRepository().getString(titleKey()));            
        }
    }
    
	protected Component buildMainPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
			
		JScrollPane scrollPane = new JScrollPane(this.buildQueryableTree());		
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 5, 5);
		panel.add(scrollPane, constraints);	
		
		return panel;	
	}

	protected QueryableTree buildQueryableTree() {
		this.queryableTree = new QueryableTree(buildQueryableTreeModel(), getWorkbenchContext());
		this.queryableTree.addTreeSelectionListener(buildTreeSelectionHandler());


		SwingComponentFactory.addDoubleClickMouseListener(queryableTree, new DoubleClickMouseListener() {
			public void mouseDoubleClicked(MouseEvent e) {
				TreePath path = queryableTree.getPathForLocation(e.getX(), e.getY());

				if (path != null) {
					clickOK();
				}
			}
		});
		return this.queryableTree;
	}
	
	protected QueryableTreeModel buildQueryableTreeModel() {
		return new QueryableTreeModel(new DefaultMutableTreeNode(getDescriptor()), this.traversableFilter);
	}
	
	protected void initializeEditMode(MWAttributeItem attributeItem) {
        MWQueryableArgumentElement element = attributeItem.getQueryableArgument().getQueryableArgumentElement();
        if ( element.getQueryable() != null && this.chooseableFilter.accept(element.getQueryable())) {
            this.queryableTree.setSelectedQueryableArgumentElement(attributeItem.getQueryableArgument().getQueryableArgumentElement());
        }
        setTitle(resourceRepository().getString(titleKey()));
	}

	/**
	* Invoked each time a node is selected or unselected.
	*/
	private TreeSelectionListener buildTreeSelectionHandler() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
			    treeSelectionChanged(e);
			}
		};
	}
	
	protected void treeSelectionChanged(TreeSelectionEvent e) {
	    updateOKButton();
	}
	
	protected void updateOKButton() {
		boolean selected = this.queryableTree.getSelectionCount() > 0;
		if (selected) {
			QueryableTreeNode selectedNode = (QueryableTreeNode) this.queryableTree.getSelectionPath().getLastPathComponent();
			getOKAction().setEnabled(this.chooseableFilter.accept(selectedNode.getQueryable()));			
		}
		else {
			getOKAction().setEnabled(false);
		}	
	}	

	protected void cancelPressed() {
		((DefaultMutableTreeNode) this.queryableTree.getModel().getRoot()).removeAllChildren();
		super.cancelPressed();
	}	

	protected boolean preConfirm() {
		TreePath selectionPath = this.queryableTree.getSelectionPath();

		List queryablePath = new ArrayList();
		List allowsNull = new ArrayList();
		
		QueryableTreeNode selectedNode = (QueryableTreeNode) selectionPath.getLastPathComponent();
		allowsNull.add(Boolean.valueOf(selectedNode.isAllowsNull()));
		MWQueryable queryableObject = selectedNode.getQueryable();
		queryablePath.add(queryableObject);
		selectionPath = selectionPath.getParentPath();
		
		while (selectionPath.getPathCount() > 1) {//first path component is always a descriptor, we want to quit before reaching it
			selectedNode = (QueryableTreeNode) selectionPath.getLastPathComponent();
			allowsNull.add(new Boolean(selectedNode.isAllowsNull()));
			MWQueryable joinedQueryable = selectedNode.getQueryable();
			queryablePath.add(joinedQueryable);
			selectionPath = selectionPath.getParentPath();
		}
		
				
		int index = attributeItemsSize();
		
		if (this.attributeItem != null) {
			index = indexOfAttributeItem(this.attributeItem);
			removeAttributeItem(index);
		}		
		
		addAttributeItem(index, queryablePath.iterator(),  allowsNull.iterator());

		((DefaultMutableTreeNode) this.queryableTree.getModel().getRoot()).removeAllChildren();
		return super.preConfirm();
	}
	
	protected abstract int attributeItemsSize();
	
	protected abstract int indexOfAttributeItem(MWAttributeItem attributeItem);
	
	protected abstract void removeAttributeItem(int index);
	
	protected abstract void addAttributeItem(int index, Iterator queryables, Iterator allowsNulls);
}
