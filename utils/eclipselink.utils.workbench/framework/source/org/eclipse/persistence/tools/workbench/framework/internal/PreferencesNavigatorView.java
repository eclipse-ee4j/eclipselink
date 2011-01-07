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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode;
import org.eclipse.persistence.tools.workbench.framework.app.PreferencesNode;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * 
 */
final class PreferencesNavigatorView {

	/** tree of preferences nodes */
	private JTree tree;	
	
	/** root node of preferences nodes */
	private AbstractPreferencesNode rootNode; 

	/** simple panel holding the tree */
	private JPanel panel;


	// ********** constructors/initialization **********

	/**
	 * Construct a navigator for the specified root preferences node.
	 */
	public PreferencesNavigatorView(AbstractPreferencesNode root) {
		super();
		this.initialize(root);
	}


	private void initialize(AbstractPreferencesNode root) {
		this.rootNode = root;
		this.tree = this.buildTree(root);
		this.panel = this.buildPanel();
		expandAll(this.rootNode);
	}

	/**
	 * Expands the given node. The children will be recursively expanded.
	 */
	private void expandAll(TreeNode node) {
		this.tree.expandPath(buildPath(node));

		for (Enumeration enumeration = node.children(); enumeration.hasMoreElements(); ) {
			TreeNode child = (TreeNode) enumeration.nextElement();
			if (!child.isLeaf()) {
				expandAll(child);
			}
		}
	}

	private TreePath buildPath(TreeNode node) {
		Vector paths = new Vector();

		do {
			paths.add(0, node);
			node = node.getParent();
		} while (node != null);

		return new TreePath(paths.toArray());
	}

	/**
	 * build a tree using the specified root node
	 */
	private JTree buildTree(PreferencesNode root) {
		JTree tree = new JTree(root);
		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		tree.setCellRenderer(new PreferencesTreeCellRenderer());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addKeyListener(buildF1KeyListener());
		tree.addMouseListener(buildMouseListener());
		return tree;
	}
	
	/**
	 * The following Mouse and Key Listeners are customized so that "F1" and right-click context sensitive
	 * help will work with the preferences node tree on the preferences dialog.
	 */
	private MouseListener buildMouseListener() {
		return new LocalMouseListener(getRootNode().resourceRepository().getString("HELP_POPUP"));
	}
	
	private KeyListener buildF1KeyListener() {
		return new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				// show help for the first selected node
				if (e.getKeyCode() == KeyEvent.VK_F1) {
					AbstractPreferencesNode node = (AbstractPreferencesNode)getTree().getLastSelectedPathComponent();
					if (node != null) {
						node.showHelp();
					}
				}
			}
		};
	}
	
	private JPanel buildPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.setMinimumSize(new Dimension(0, 0));

		JScrollPane scrollPane = new JScrollPane(this.tree);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
		scrollPane.setBorder(null);
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}


	// ********** listeners **********

	/**
	 * Add a listener that will be notified of tree selection events.
	 */
	synchronized void addTreeSelectionListener(TreeSelectionListener listener) {
		this.tree.addTreeSelectionListener(listener);
	}

	/**
	 * Remove the specified listener.
	 */
	synchronized void removeTreeSelectionListener(TreeSelectionListener listener) {
		this.tree.removeTreeSelectionListener(listener);
	}


	// ********** queries **********

	Component getComponent() {
		return this.panel;
	}

	Component initialFocusComponent() {
		return this.tree;
	}
	
	JTree getTree() {
		return this.tree;
	}
	
	AbstractPreferencesNode getRootNode() {
		return this.rootNode;
	}


	// ********** behavior **********

	void selectFirstChild() {
		this.tree.setSelectionPath(buildPath(this.rootNode.getChildAt(0)));
	}

	// ********** inner classes **********

	/**
	 * This renderer creates a new PreferencesTreeCellRenderer each time the
	 * renderer is requested. It is required when JAWS is running.
	 */
	private static class PreferencesTreeCellRenderer implements TreeCellRenderer {
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			NodeRenderer renderer = new NodeRenderer();
			return renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		}
	}

	/**
	 * This renderer paints a label with no icon and text derived
	 * from the the preferences node.
	 */
	private static class NodeRenderer extends SimpleTreeCellRenderer {
		// this border improves the readability a bit
		private static final Border BORDER = BorderFactory.createEmptyBorder(0, 2, 0, 0);

		public NodeRenderer() {
			super();
		}

		/**
		 * @see org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
		 */
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			this.setBorder(BORDER);
			return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		}

		/**
		 * @see org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTreeCellRenderer#buildIcon(Object)
		 */
		protected Icon buildIcon(Object value) {
			return null;
		}

		/**
		 * @see org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTreeCellRenderer#buildText(Object)
		 */
		protected String buildText(Object value) {
			return ((PreferencesNode) value).displayString();
		}

	}
	/**
	 * This listener will respond to a mouse pop-up trigger and
	 * add the Help menu to the tree. Selecting the Help menu
	 * item will trigger the selected tree items help
	 * topic to be displayed.
	 */
	private class LocalMouseListener extends MouseAdapter {
		/** the Help pop-up menu */
		PopupMenu popupMenu;
		
		/** 
		 * coordinates where mouse event happened help onto so that we will
		 * know which item in the tree the mouse was clicked on.
		 */
		int x;
		int y;	
	
		// ********** constructor/initialization **********
	
		LocalMouseListener(String menuItemLabel) {
			super();
			this.initialize(menuItemLabel);
		}
	
		/**
		 * build the pop-up menu that will be added to the tree selection
		 */
		private void initialize(String menuItemLabel) {
			MenuItem item = new MenuItem(menuItemLabel);
			item.addActionListener(this.buildMenuItemListener());
	
			this.popupMenu = new PopupMenu();
			this.popupMenu.add(item);
		}
	
		/**
		 * this is the action that is executed if the user selects
		 * the Help menu item from the pop-up menu
		 */
		private ActionListener buildMenuItemListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// display help for the component stashed away when the pop-up menu was first displayed
					LocalMouseListener.this.showHelp();
					// not sure if this is needed...
					LocalMouseListener.this.popupMenu.getParent().remove(LocalMouseListener.this.popupMenu);
				}
			};
		}
	
	
		// ********** MouseListener implementation **********
	
		public void mousePressed(MouseEvent e) {
			handleMouseEvent(e);
		}
		public void mouseReleased(MouseEvent e) {
			handleMouseEvent(e);
		}
		private void handleMouseEvent(MouseEvent e) {
			if ( ! e.isPopupTrigger()) {
				return;
			}
	
			getTree().add(this.popupMenu);
			this.x = e.getX();
			this.y = e.getY();
			this.popupMenu.show(getTree(), e.getX(), e.getY());
		}	
	
		// ********** behavior **********
	
		/**
		 * show help for the pop-up menu's selected component
		 */
		void showHelp() {
			int row = getTree().getRowForLocation(this.x, this.y);
		
			if (row != -1)  {
				TreePath path = PreferencesNavigatorView.this.tree.getPathForRow(row);
				TreePath[] selectedPaths = PreferencesNavigatorView.this.tree.getSelectionPaths();
			
				if (selectedPaths == null || ! CollectionTools.contains(selectedPaths, path)) {
					PreferencesNavigatorView.this.tree.setSelectionPath(path);
				}
				AbstractPreferencesNode node = (AbstractPreferencesNode)getTree().getLastSelectedPathComponent();
				if (node != null) {
					node.showHelp();
				}
			}
		}
	}

}
