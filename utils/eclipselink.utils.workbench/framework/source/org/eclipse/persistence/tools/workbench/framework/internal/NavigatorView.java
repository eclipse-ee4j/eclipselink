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
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.eclipse.persistence.tools.workbench.framework.app.AccessibleNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.help.HelpManager;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TreeModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.DisplayableTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.SimpleStack;
import org.eclipse.persistence.tools.workbench.utility.Stack;
import org.eclipse.persistence.tools.workbench.utility.iterators.EnumerationIterator;


/**
 * This view is the "navigator" tree that displays all the various nodes
 * in a JTree and manages user interactions with the tree.
 */
final class NavigatorView {

	/**
	 * The context provides us with the application-wide root node,
	 * help manager, and resource repository (for our label).
	 */
	private ApplicationContext context;

	/**
	 * The pop-up menu is rebuilt whenever the selection
	 * menu description changes.
	 */
	private ValueModel selectionMenuDescriptionHolder;
	private PropertyChangeListener selectionMenuDescriptionListener;
	private JPopupMenu selectionPopupMenu;

	/**
	 * We need to hold the navigator selection so the workspace view
	 * can listen to it and coordinate the editor and problem views.
	 */
	private SelectionModel selectionModel;

	/**
	 * We listen for the first node to be added so we can select it
	 * and have it show up in the navigator; otherwise the root node
	 * is hidden and un-expanded, making it impossible for the user
	 * to see or manipulate anything in the navigator.
	 * (Originally, we listened to the root node's children, but then
	 * we would get notified before the TreeModelAdapter and the
	 * selection would never occur.)
	 */
	private TreeModel treeModel;
	private TreeModelListener treeModelListener;
	private int rootNodeChildrenSize;

	/** This is the view's component, the tree. */
	private JPanel component;
	private JTree tree;


	// ********** constructor/initialization **********

	/**
	 * We need access to the selection menu so we can display it
	 * as a pop-up menu when the user either right-clicks with the
	 * mouse or presses Shift-F10.
	 */
	NavigatorView(ApplicationContext context, ValueModel selectionMenuDescriptionHolder) {
		super();
		this.context = context;
		this.selectionPopupMenu = new JPopupMenu();
		this.selectionMenuDescriptionHolder = selectionMenuDescriptionHolder;
		this.selectionMenuDescriptionListener = this.buildSelectionMenuDescriptionListener();
		this.selectionMenuDescriptionHolder.addPropertyChangeListener(ValueModel.VALUE, this.selectionMenuDescriptionListener);

		this.treeModel = this.buildTreeModel();
		this.treeModelListener = this.buildTreeModelListener();
		this.treeModel.addTreeModelListener(this.treeModelListener);
		this.rootNodeChildrenSize = this.rootNode().getChildrenModel().size();

		this.component = this.buildComponent();
	}

	private PropertyChangeListener buildSelectionMenuDescriptionListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				NavigatorView.this.selectionMenuDescriptionChanged();
			}
		};
	}

	private JPanel buildComponent() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.setMinimumSize(new Dimension(0, 0));

		JLabel label = new JLabel(this.resourceRepository().getString("NAVIGATOR_LABEL"));
		label.setDisplayedMnemonic(this.resourceRepository().getMnemonic("NAVIGATOR_LABEL"));
		label.setIcon(this.resourceRepository().getIcon("navigator"));
		label.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, panel.getBackground().darker()), 
				BorderFactory.createEmptyBorder(2, 2, 2, 2)
			)
		);
		panel.add(label, BorderLayout.PAGE_START);

		// we need a circular reference between the tree and the tree selection model
		this.tree = this.buildTree();
		this.selectionModel = new SelectionModel(this.tree);
		this.tree.setSelectionModel(this.selectionModel);
		JScrollPane scrollPane = new JScrollPane(this.tree);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
		scrollPane.setBorder(null);
		panel.add(scrollPane, BorderLayout.CENTER);

		label.setLabelFor(this.tree);

		return panel;
	}

	/**
	 * Build a tree using the root node from the workbench context.
	 * A custom selection model will be set later.
	 */
	private JTree buildTree() {
		JTree result = new NavigatorTree(this.treeModel);
		result.setShowsRootHandles(true);
		result.setRootVisible(false);

		result.setCellRenderer(this.buildTreeCellRenderer());
		result.setRowHeight(0);	// row height will be determined by the renderer

		result.addMouseListener(this.buildMouseListener());
		result.addKeyListener(this.buildKeyListener());

		return result;
	}

	/**
	 * HACK: Build a new renderer every time or things get screwed
	 * up when JAWS is running.
	 */
	private TreeCellRenderer buildTreeCellRenderer() {
		return new TreeCellRenderer() {
			public Component getTreeCellRendererComponent(JTree t, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) 	{
				return new ApplicationNodeTreeCellRenderer().getTreeCellRendererComponent(t, value, selected, expanded, leaf, row, hasFocus);
			}
		};
	}

	private MouseListener buildMouseListener() {
		return new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				this.handleMouseEvent(e);
			}
			public void mouseReleased(MouseEvent e) {
				this.handleMouseEvent(e);
			}
			private void handleMouseEvent(MouseEvent e) {
				if (e.isPopupTrigger()) {
					NavigatorView.this.displayPopupMenu((JTree) e.getSource(), e.getX(), e.getY());
				}
			}
		};
	}

	private KeyListener buildKeyListener() {
		return new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				NavigatorView.this.keyPressed(e);
			}
		};
	}

	private TreeModel buildTreeModel() {
		return new TreeModelAdapter(this.rootNode());
	}

	private TreeModelListener buildTreeModelListener() {
		return new TreeModelListener() {
			public void treeNodesChanged(TreeModelEvent e) {
				NavigatorView.this.treeChanged();
			}
			public void treeNodesInserted(TreeModelEvent e) {
				NavigatorView.this.treeChanged();
			}
			public void treeNodesRemoved(TreeModelEvent e) {
				NavigatorView.this.treeChanged();
			}
			public void treeStructureChanged(TreeModelEvent e) {
				NavigatorView.this.treeChanged();
			}
		};
	}


	// ********** behavior **********

	/**
	 * Add a listener that will be notified of tree selection events.
	 */
	void addTreeSelectionListener(TreeSelectionListener listener) {
		this.selectionModel.addTreeSelectionListener(listener);
	}

	/**
	 * Remove the specified listener.
	 */
	void removeTreeSelectionListener(TreeSelectionListener listener) {
		this.selectionModel.removeTreeSelectionListener(listener);
	}

	void selectionMenuDescriptionChanged() {
		this.selectionPopupMenu.removeAll();
		GroupContainerDescription selectionMenuDescription = (GroupContainerDescription) this.selectionMenuDescriptionHolder.getValue();
		for (Iterator stream = selectionMenuDescription.components(); stream.hasNext(); ) {
			this.selectionPopupMenu.add((Component) stream.next());
		}
	}

	/**
	 * mouse "right-click"
	 */
	void displayPopupMenu(JTree t, int x, int y) {
		TreePath path = t.getPathForLocation(x, y);
		if (path == null)  {
			return;
		}

		TreePath[] selectedPaths = t.getSelectionPaths();
		
		if ((selectedPaths == null) || ! CollectionTools.contains(selectedPaths, path)) {
			t.setSelectionPath(path);
		}

		if (this.selectionPopupMenu.getComponentCount() > 0) {
			this.selectionPopupMenu.show(t, x, y);
		}
	}

	void keyPressed(KeyEvent e) {
		if (e.isConsumed()) {
			return;
		}
		switch (e.getKeyCode()) {
			case KeyEvent.VK_F1:
				NavigatorView.this.displayHelp();
				e.consume();
				break;
			case KeyEvent.VK_F10:
				if (e.isShiftDown()) {
					NavigatorView.this.displayPopupMenu((JTree) e.getSource());
					e.consume();
				}
				break;
			default:
				break;
		}
	}

	/**
	 * F1 pressed
	 */
	private void displayHelp() {
		ApplicationNode[] nodes = this.selectionModel.getSelectedNodes();
		if (nodes.length != 1) {
			return;
		}
		ApplicationNode node = nodes[0];
		this.helpManager().showTopic(node.helpTopicID());
	}

	/**
	 * Shift-F10 pressed
	 */
	private void displayPopupMenu(JTree t) {
		ApplicationNode[] nodes = this.selectionModel.getSelectedNodes();
		if (nodes.length != 1) {
			return;
		}
		ApplicationNode node = nodes[0];

		Rectangle rec = t.getPathBounds(new TreePath(node.path()));
		if (this.selectionPopupMenu.getComponentCount() > 0) {
			this.selectionPopupMenu.show(t, (int) rec.getCenterX(), (int) rec.getCenterY());
		}
	}

	/**
	 * Select the first node added to the root so that it is visible to the user.
	 */
	void treeChanged() {
		int oldSize = this.rootNodeChildrenSize;
		int newSize = this.rootNode().getChildrenModel().size();
		if ((oldSize == 0) && (newSize != 0)) {
			this.selectionModel.setSelectedNode((ApplicationNode) this.rootNode().getChildrenModel().getItem(0));
		}
		this.rootNodeChildrenSize = newSize;
	}

	void saveTreeExpansionState(Preferences windowsPreferences) {
		this.selectionModel.saveTreeExpansionState(windowsPreferences);
	}

	void restoreTreeExpansionState(Preferences windowsPreferences) {
		this.selectionModel.restoreTreeExpansionState(windowsPreferences);
	}

	/**
	 * This is called when the window containing the navigator is closed.
	 */
	void close() {
		this.treeModel.removeTreeModelListener(this.treeModelListener);
		this.selectionMenuDescriptionHolder.removePropertyChangeListener(ValueModel.VALUE, this.selectionMenuDescriptionListener);
		this.tree.setModel(null);
	}


	// ********** queries **********

	Component getComponent() {
		return this.component;
	}

	NavigatorSelectionModel getSelectionModel() {
		return this.selectionModel;
	}

	private ResourceRepository resourceRepository() {
		return this.context.getResourceRepository();
	}

	private TreeNodeValueModel rootNode() {
		return this.context.getNodeManager().getRootNode();
	}

	private HelpManager helpManager() {
		return this.context.getHelpManager();
	}


	// ******************** member classes ********************

	/**
	 * Override JTree#convertValueToText() so JTree#getNextMatch()
	 * works correctly, which is used by the TreeUIs to jump to a node
	 * with a text value that begins with the letter typed by the user.
	 */
	private class NavigatorTree extends JTree {
		public NavigatorTree(TreeModel model) {
			super(model);
		}
		public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			return (value == null) ? "" : ((Displayable) value).displayString();
		}
	}


	/**
	 * This class implements both the Swing TreeSelectionModel interface
	 * (via inheritance) and the Framework NavigatorSelectionModel interface.
	 * 
	 * This model needs a backpointer to the tree because there is no "model"
	 * for a JTree's "expansion" state - we have to manipulate the tree directly
	 * if we want to save and restore the tree's "expansion" state.
	 */
	private static class SelectionModel
		extends DefaultTreeSelectionModel
		implements NavigatorSelectionModel
	{
		/** backpointer to the tree; so we can push and pop its expansion state */
		private JTree tree;

		/** stack of tree expansion states */
		private Stack expansionStates;

		/** performance tweak */
		private static final ApplicationNode[] EMPTY_SELECTED_NODES = new ApplicationNode[0];

		// ********** constructor **********

		public SelectionModel(JTree tree) {
			super();
			this.tree = tree;
			this.expansionStates = new SimpleStack();
		}

		// ********** NavigatorSelectionModel implementation **********

		/**
		 * Pull the last node off of each selection path.
		 * @see org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel#getSelectedNodes()
		 */
		public ApplicationNode[] getSelectedNodes() {
			TreePath[] paths = this.getSelectionPaths();
			int len = (paths == null) ? 0 : paths.length;
			if (len == 0) {
				return EMPTY_SELECTED_NODES;
			}
			ApplicationNode[] nodes = new ApplicationNode[len];
			for (int i = len; i-- > 0; ) {
				nodes[i] = (ApplicationNode) paths[i].getLastPathComponent();
			}
			return nodes;
		}

		/**
		 * Gather up the "project" nodes for all of the currently selected "leaf" nodes.
		 * @see org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel#getSelectedProjectNodes()
		 */
		public ApplicationNode[] getSelectedProjectNodes() {
			ApplicationNode[] selectedNodes = this.getSelectedNodes();
			Set selectedRootNodes = new HashSet(selectedNodes.length);
			for (int i = selectedNodes.length; i-- > 0; ) {
				selectedRootNodes.add(selectedNodes[i].getProjectRoot());
			}
			return (ApplicationNode[]) selectedRootNodes.toArray(new ApplicationNode[selectedRootNodes.size()]);
		}

		/**
		 * @see org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel#pushExpansionState()
		 */
		public void pushExpansionState() {
			this.expansionStates.push(this.currentExpansionState());
		}

		/**
		 * @see org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel#popAndRestoreExpansionState()
		 */
		public void popAndRestoreExpansionState() {
			Collection expandedPaths = (Collection) this.expansionStates.pop(); 
			for (Iterator stream = expandedPaths.iterator(); stream.hasNext(); ) {
				this.tree.expandPath((TreePath) stream.next());
			}
		}

		public void popAndRestoreExpansionState(ApplicationNode oldNode, ApplicationNode morphedNode) {
			Collection expandedPaths = (Collection) this.expansionStates.pop(); 
			for (Iterator stream = expandedPaths.iterator(); stream.hasNext(); ) {
				TreePath path = (TreePath) stream.next();
				if (path.getLastPathComponent() == oldNode) {
					path = new TreePath(morphedNode.path());
				}
				this.tree.expandPath(path);
			}
		}		

		/**
		 * Scroll so the node is visible once it is selected.
		 * @see org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel#setSelectedNode(org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode)
		 */
		public void setSelectedNode(ApplicationNode node) {
			TreePath path = new TreePath(node.path());
			this.setSelectionPath(path);
			this.tree.scrollPathToVisible(path);
		}

		/**
		 * Scroll so the paths are visible once they are selected
		 */
		public void setSelectionPaths(TreePath[] paths) {
			super.setSelectionPaths(paths);
			for (int i = 0; i < paths.length; i++) {
				this.tree.scrollPathToVisible(paths[i]);
			}
		}

		/**
		 * Scroll so the node is visible once it is selected.
		 * @see org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel#expandNode(org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode)
		 */
		public void expandNode(ApplicationNode node) {
			this.tree.expandPath(new TreePath(node.path()));
		}

		// ********** behavior **********

		void saveTreeExpansionState(Preferences windowsPreferences) {
			// TODO save expansion state
		}
	
		void restoreTreeExpansionState(Preferences windowsPreferences) {
			// TODO restore expansion state		
		}

		// ********** queries **********

		/**
		 * Return a collection of the tree's currently expanded paths.
		 */
		private Collection currentExpansionState() {
			Enumeration stream = this.tree.getExpandedDescendants(new TreePath(this.tree.getModel().getRoot()));
			if (stream == null) {
				return Collections.EMPTY_LIST;
			}
			return CollectionTools.list(new EnumerationIterator(stream));
		}

	}


	/**
	 * Tweak the displayable tree cell renderer
	 * to indicate a dirty node when appropriate.
	 */
	private static class ApplicationNodeTreeCellRenderer
		extends DisplayableTreeCellRenderer
	{
		/**
		 * Ask the AccessibleNode for a description that can be different than
		 * the regular text shown on the node.
		 */
		protected String buildAccessibleName(Object value) {
			return ((AccessibleNode) value).accessibleName();
		}

		/**
		 * Prepend the node's text with an asterisk if the node is dirty.
		 */
		protected String buildText(Object value) {
			String text = super.buildText(value);
			if (text == null) {
				return text;	// the root node is NOT an app node...
			}
			return (((ApplicationNode) value).isDirty()) ? '*' + text : text;
		}

		/**
		 * ask Paul what this is for...
		 */
		public Dimension getPreferredSize() {
			Dimension d = super.getPreferredSize();
			return new Dimension(d.width, d.height + 1);
		}

	}

}
