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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Stack;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryableArgumentElement;


/**
 * QueryableTree is used on the QueryableEditDialog.  The renderer and editor for each node
 * contains a panel with a label(name and icon of the queryable) and check box for allowsNull.
 * The tree maintains selection.  When a node is selected, all of it's parent nodes are selected as well
 */
final class QueryableTree extends SwingComponentFactory.AccessibleTree
{
	private WorkbenchContext context;

	QueryableTree(QueryableTreeModel newModel, WorkbenchContext context)
	{
		super(newModel);
		initialize(context);
	}

	private FocusListener buildFocusListener()
	{
		return new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
				TreePath[] paths = getSelectionPaths();

				if (paths == null)
					return;

				for (int index = 0; index < paths.length; index++)
				{
					Rectangle pathBounds = getUI().getPathBounds(QueryableTree.this, paths[index]);
					repaint(pathBounds.x, pathBounds.y, pathBounds.width, pathBounds.height);
				}
			}

			public void focusLost(FocusEvent e)
			{
				focusGained(e);
			}
		};
	}

	private TreeSelectionListener buildTreeSelectionListener()
	{
		return new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				TreePath treePath = e.getNewLeadSelectionPath();

				if (treePath != null)
				{
	            TreePath[] paths = new TreePath[treePath.getPathCount() - 1];
	            int count = paths.length - 1;
	           	paths[count--] = treePath;
	            treePath = treePath.getParentPath();
	            Object rootNode = getModel().getRoot();
	            
	            while (rootNode != treePath.getLastPathComponent())
	            {
	            	paths[count--] = treePath;
	            	treePath = treePath.getParentPath();
	            }

	            setSelectionPaths(paths);
				}
			}
		};
	}

	/**
	 * To fix the editing when there is more than one node selected, the order
	 * of the nodes had to be reversed, see buildTreeSelectionListener(). But
	 * that caused this method to return the wrong node, so here we simply return
	 * the good one.
	 */
	public TreePath getSelectionPath()
	{
		TreePath[] paths = getSelectionPaths();

		if ((paths == null) || (paths.length == 0))
			return null;

		return paths[paths.length - 1];
	}
	
	private void initialize(WorkbenchContext context)
	{
		this.context = context;

		setRowHeight(0);
		setEditable(true);
		setAutoscrolls(true);
		setRootVisible(false);
		setShowsRootHandles(true);
		addFocusListener(buildFocusListener());

		QueryableTreeCell treeCell = new QueryableTreeCell();
		setCellEditor(treeCell);
		setCellRenderer(treeCell);

		addTreeSelectionListener(buildTreeSelectionListener());
	}
	
	/**
	 * Processes <code>MouseEvent</code>s occurring on this component by
	 * dispatching them to any registered <code>MouseListener</code> objects.
	 *
	 * @param e The <code>MouseEvent</code>
	 */
	protected void processMouseEvent(MouseEvent e)
	{
		// Make sure the editing is stopped before the new MousePressed is
		// dispatched to a new editor
		if (isEditing() && (e.getButton() == MouseEvent.BUTTON1))
		{
			TreePath path = getEditingPath();
			Rectangle pathBounds = getPathBounds(path);

			if (!pathBounds.contains(e.getX(), e.getY()))
			{
				stopEditing();
			}
		}

		super.processMouseEvent(e);
	}

	private ResourceRepository resourceRepository() {
		return this.context.getApplicationContext().getResourceRepository();
	}
    
    //this is used to populate the queryKey tree
    //it sets up which nodes are selected and whether allowsNull is true/false on the nodes
	void setSelectedQueryableArgumentElement(MWQueryableArgumentElement element)
	{	
		if (element == null)
			return;
		
		Stack joinedElementsStack = new Stack();
		while (element != null)
		{
			joinedElementsStack.add(element);
			element = element.getJoinedQueryableElement();
		}
				
		QueryableTreeModel treeModel = (QueryableTreeModel) getModel();
		
		MWQueryableArgumentElement nextElement = (MWQueryableArgumentElement) joinedElementsStack.pop();

		if (nextElement.getQueryable() == null)
			return;
		
		int index = treeModel.buildQueryableObjects().indexOf(nextElement.getQueryable());
		
		//bug #5231484 -invalid queryable object in the model, don't select anything in the tree
		if (index == -1) {
			return;
		}
		int count = 0;
		int selectionIndex = index;
		QueryableTreeNode node = (QueryableTreeNode) treeModel.getChild(treeModel.getRoot(), index);
		node.setAllowsNull(nextElement.isAllowsNull());
		expandRow(index);
		MWQueryable queryable = nextElement.getQueryable();
			
		while (!joinedElementsStack.empty())
		{		
			count++;	
			nextElement = (MWQueryableArgumentElement) joinedElementsStack.pop();

			if (nextElement.getQueryable() == null || !treeModel.getQueryableFilter().accept(nextElement.getQueryable()))
				return;

			index = queryable.subQueryableElements(treeModel.getQueryableFilter()).indexOf(nextElement.getQueryable());
					
			node = (QueryableTreeNode) treeModel.getChild(node , index);
			node.setAllowsNull(nextElement.isAllowsNull());
			selectionIndex = selectionIndex + index + 1;
			expandRow(selectionIndex);
			queryable = nextElement.getQueryable();
		}

		setSelectionRow(selectionIndex);
	}
 	
 	/**
 	 * This panel is used as the renderer and editor for the QueryableTree.
 	 */
	private final class QueryableNodeCheckBoxPanel extends JPanel
	{
		private JCheckBox allowsNullCheckBox;
		private DefaultTreeCellRenderer queryableLabel;
		private QueryableTreeNode queryableNode;

		protected QueryableNodeCheckBoxPanel()
		{
			super(new GridBagLayout());
			initialize();
		}

		protected ActionListener buildAllowsNullAction()
		{
			return new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					queryableNode.setAllowsNull(allowsNullCheckBox.isSelected());
					stopEditing();
				}
			};
		}

		private DefaultTreeCellRenderer buildQueryableLabel()
		{
			return new DefaultTreeCellRenderer()
			{
				public Color getBackgroundSelectionColor()
				{
					if (!QueryableTree.this.hasFocus() && !isEditing())
						return UIManager.getColor("Panel.background");

					return super.getBackgroundSelectionColor();
				}

				public Color getBorderSelectionColor()
				{
					if (!QueryableTree.this.hasFocus() && !isEditing())
						return UIManager.getColor("Panel.background");

					return super.getBorderSelectionColor();
				}

				public Dimension getPreferredSize()
				{
					Dimension size = super.getPreferredSize();
					size.height += 2;
					return size;
				}
			};
		}

		private void initialize()
		{
			setOpaque(false);
			GridBagConstraints constraints = new GridBagConstraints();

			this.queryableLabel = buildQueryableLabel();

			constraints.gridx			= 0;
			constraints.gridy			= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill			= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.LINE_START;
			constraints.insets		= new Insets(0, 0, 0, 0);

			add(this.queryableLabel, constraints);

			this.allowsNullCheckBox = new JCheckBox();
			this.allowsNullCheckBox.setMargin(new Insets(0,0,0,0));
			this.allowsNullCheckBox.setOpaque(false);
			this.allowsNullCheckBox.addActionListener(buildAllowsNullAction());

			constraints.gridx			= 1;
			constraints.gridy			= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill			= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(0, 8, 0, 0);

			add(this.allowsNullCheckBox, constraints);
		}

		protected void populate(QueryableTreeNode queryableNode, boolean selected)
		{
			this.queryableNode = queryableNode;
			MWQueryable queryable = (MWQueryable) queryableNode.getUserObject();

			this.allowsNullCheckBox.setVisible(queryable.allowsOuterJoin());
			this.allowsNullCheckBox.setEnabled(selected);
			this.allowsNullCheckBox.setSelected(queryableNode.isAllowsNull());

			this.queryableLabel.setText(queryable.getName());
			this.queryableLabel.setIcon(resourceRepository().getIcon(queryable.iconKey()));

			if (queryable.usesAnyOf())
			{
				this.allowsNullCheckBox.setText(resourceRepository().getString("ALLOWS_NONE_CHECK_BOX_LABEL_IN_QUERYABLE_TREE"));
				this.allowsNullCheckBox.setMnemonic(resourceRepository().getMnemonic("ALLOWS_NONE_CHECK_BOX_LABEL_IN_QUERYABLE_TREE"));
				this.allowsNullCheckBox.setDisplayedMnemonicIndex(resourceRepository().getMnemonicIndex("ALLOWS_NONE_CHECK_BOX_LABEL_IN_QUERYABLE_TREE"));
			}
			else
			{
				this.allowsNullCheckBox.setText(resourceRepository().getString("ALLOWS_NULL_CHECK_BOX_LABEL_IN_QUERYABLE_TREE"));
				this.allowsNullCheckBox.setMnemonic(resourceRepository().getMnemonic("ALLOWS_NULL_CHECK_BOX_LABEL_IN_QUERYABLE_TREE"));
				this.allowsNullCheckBox.setDisplayedMnemonicIndex(resourceRepository().getMnemonicIndex("ALLOWS_NULL_CHECK_BOX_LABEL_IN_QUERYABLE_TREE"));
			}
		}

		protected void update(JTree tree, boolean selected, boolean expanded, boolean leaf, int rowIndex, boolean hasFocus)
		{
			queryableLabel.getTreeCellRendererComponent(tree, null, selected, expanded, leaf, rowIndex, hasFocus);
		}
	}

	private final class QueryableTreeCell extends AbstractCellEditor
	                                      implements TreeCellEditor,
	                                                 TreeCellRenderer
	{ 
		private QueryableNodeCheckBoxPanel editor;
		private Object value;

		QueryableTreeCell()
		{
			super();
			initialize();
		}

		private QueryableNodeCheckBoxPanel createQueryKeyPanel()
		{
			return new QueryableNodeCheckBoxPanel();
		}

		public Object getCellEditorValue()
		{
			return value;
		}

		public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row)
		{
			// If I am asking for the editor, then a selection is about to take
			// place so 'selected' might not be true right now, but it will be soon
			editor.update(tree, true, expanded, leaf, row, true);
			editor.populate((QueryableTreeNode) value, true);
			this.value = value;

			return editor;
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int rowIndex, boolean hasFocus)
		{
			if (tree.getModel().getRoot() == value)
				return new JLabel();

			QueryableTreeNode node = (QueryableTreeNode) value;
			hasFocus = isPathSelected(new TreePath(node.getPath()));

			QueryableNodeCheckBoxPanel checkBoxPanel = createQueryKeyPanel();
			checkBoxPanel.update(tree, selected, expanded, leaf, rowIndex, hasFocus);
			checkBoxPanel.populate((QueryableTreeNode) value, selected);	
			checkBoxPanel.invalidate();

			return checkBoxPanel;
		}

		private void initialize()
		{
			editor = createQueryKeyPanel();
		}

		public boolean isCellEditable(EventObject e)
		{
			// Retrieve the node where the mouse event location occurred
			TreePath path = null;
			MouseEvent mouseEvent = null;

			if (e instanceof MouseEvent)
			{
				mouseEvent = (MouseEvent) e;
				path = getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
			}
			else
			{
				path = getSelectionPath();
			}

			if (path == null)
				return false;

			QueryableTreeNode node = (QueryableTreeNode) path.getLastPathComponent();
			MWQueryable queryable = (MWQueryable) node.getUserObject();

			if (!queryable.allowsOuterJoin())
				return false;

			if (mouseEvent == null)
				return true;

			// Layout panel so we know if the location of the mouse is
			// inside of the check box
			JCheckBox checkBox = editor.allowsNullCheckBox;
			editor.populate(node, true);

			int x_checkBox = editor.queryableLabel.getPreferredSize().width + 8;
			int x_allowsNullCheckBox = getPathBounds(path).x + x_checkBox + checkBox.getBorder().getBorderInsets(checkBox).left;

			return mouseEvent.getX() >= x_allowsNullCheckBox;
		}
	} 
}
