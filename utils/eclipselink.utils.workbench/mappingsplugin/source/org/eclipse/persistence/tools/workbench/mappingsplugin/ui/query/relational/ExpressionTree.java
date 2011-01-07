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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;


/**
 * Used in the ExpressionTreeDialog and on the QueryFormatPanel
 * Not much behavior here, but I separated it out into it's own class
 * to set up the treeCellRenderer information only once
 */
final class ExpressionTree extends JTree
{

	ExpressionTree(TreeModel treeModel) {
		super(treeModel);
		initialize();
	}
	
	protected void initialize()
	{
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		setCellRenderer(renderer);
		setRootVisible(false);
		
		
		getModel().addTreeModelListener(new TreeModelListener() {
            public void treeNodesChanged(TreeModelEvent e) {
                updateRootVisibility();
            }
            public void treeNodesInserted(TreeModelEvent e) {
                updateRootVisibility();
            }
            public void treeNodesRemoved(TreeModelEvent e) {
                updateRootVisibility();
            }
            public void treeStructureChanged(TreeModelEvent e) {
                updateRootVisibility();
            }
            
            private void updateRootVisibility() {
	            if (getModel().getChildCount(getModel().getRoot()) == 0) {
	                setRootVisible(false);
	            }
	            else {
	                setRootVisible(true);
	            }
            }
       });
	}
}
