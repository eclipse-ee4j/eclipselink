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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
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
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryableArgument;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;



/**
 *	Used for choosing an MWQueryable when creating an MWExpression
 */
final class QueryableEditDialog 
	extends AbstractDialog
{
	private QueryableTree queryKeyTree;	
	private MWQueryableArgument argument;
		
	QueryableEditDialog(MWQueryableArgument argument, WorkbenchContext context) {
		super(context, (Dialog) context.getCurrentWindow());
		this.argument = argument;
	}
	
	protected String helpTopicId() {
		return "dialogEditQueryable";
	}

	/**
	* Invoked each time a node is selected or unselected.
	*/
	private TreeSelectionListener buildTreeSelectionHandler() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				updateOKButton();
			}
		};
	}
	
	private MWMappingDescriptor getDescriptor() {
		return argument.getParentQuery().getOwningDescriptor();
	}
		
	protected Component buildMainPanel() {
		getOKAction().setEnabled(false);

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		this.setTitle(resourceRepository().getString("CHOOSE_QUERY_KEY_DIALOG_TITLE.title"));
		

		queryKeyTree = new QueryableTree(new QueryableTreeModel(new DefaultMutableTreeNode(getDescriptor()), buildQueryableFilter()), getWorkbenchContext());
		JScrollPane scrollPane = new JScrollPane(queryKeyTree);
		scrollPane.setPreferredSize(new Dimension(250,200));

		queryKeyTree.addTreeSelectionListener(buildTreeSelectionHandler());
		queryKeyTree.setSelectedQueryableArgumentElement(argument.getQueryableArgumentElement());

		SwingComponentFactory.addDoubleClickMouseListener(queryKeyTree, new DoubleClickMouseListener() {
			public void mouseDoubleClicked(MouseEvent e) {
				TreePath path = queryKeyTree.getPathForLocation(e.getX(), e.getY());

				if (path != null) {
					clickOK();
				}
			}
		});
		
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 5, 5);
		panel.add(scrollPane, constraints);	
		
		return panel;	
	}

    private Filter buildQueryableFilter() {
        return new Filter() {
            public boolean accept(Object o) {
                return ((MWQueryable) o).isTraversableForQueryExpression();
            }
        };
    }
    
	protected boolean preConfirm() {
		TreePath selectionPath = this.queryKeyTree.getSelectionPath();

		List queryablePath = new ArrayList();
		List allowsNull = new ArrayList();
		
		QueryableTreeNode selectedNode = (QueryableTreeNode) selectionPath.getLastPathComponent();
		allowsNull.add(Boolean.valueOf(selectedNode.isAllowsNull()));
		MWQueryable queryableObject = selectedNode.getQueryable();
		queryablePath.add(queryableObject);
		selectionPath = selectionPath.getParentPath();
		
		while (selectionPath.getPathCount() > 1) //first path component is always a descriptor, we want to quit before reaching it
		{
			selectedNode = (QueryableTreeNode) selectionPath.getLastPathComponent();
			allowsNull.add(new Boolean(selectedNode.isAllowsNull()));
			MWQueryable joinedQueryable = selectedNode.getQueryable();
			queryablePath.add(joinedQueryable);
			selectionPath = selectionPath.getParentPath();
		}
		this.argument.setQueryableArgument(queryablePath.iterator(),  allowsNull.iterator());


		((DefaultMutableTreeNode) queryKeyTree.getModel().getRoot()).removeAllChildren();
		return super.preConfirm();
	}
	
	protected void cancelPressed() {
		((DefaultMutableTreeNode) queryKeyTree.getModel().getRoot()).removeAllChildren();
		super.cancelPressed();
	}
	
	private void updateOKButton() {
		getOKAction().setEnabled(queryKeyTree.getSelectionCount() > 0);
	}	
}
