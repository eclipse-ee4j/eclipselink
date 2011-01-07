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
package org.eclipse.persistence.tools.workbench.test.uitools;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.Collator;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeEvent;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.adapters.PreferencePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.adapters.PreferencesCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

/**
 * A panel for displaying a tree of preferences.
 * On the left hand side is a tree of preferences nodes.
 * On the right hand side is a table of the preferences for
 * the currently selected node. The preferences values are
 * displayed and edited as simple strings (as opposed to
 * using the Preferences API that allows you to get and
 * set various other primitive types - e.g. int).
 */
// TODO add popup menu to tree
public class PreferencesPanel extends JPanel {
	/** This is used to sort the nodes and preferences. */
	Collator collator;

	/** This holds the preferences node currently selected in the tree. */
	private PropertyValueModel selectedPreferencesHolder;
	private TreeSelectionModel treeSelectionModel;

	/** This holds the single preference currently selected in the table. */
	private ListValueModel preferencesAdapter;
	private PropertyValueModel selectedPreferenceHolder;
	private TableModel tableModel;
	private ObjectListSelectionModel tableSelectionModel;

	private JPanel controlPanel;

	// ********** constructor/initialization **********

	/**
	 * Construct a panel to display the "system"
	 * and "current user" preferences.
	 */
	public PreferencesPanel() {
		this(null);
	}

	/**
	 * Construct a panel to display the specified preferences.
	 * If the preferences is null, the panel will display the "system"
	 * and "current user" preferences.
	 */
	public PreferencesPanel(Preferences preferences) {
		super(new BorderLayout());
		this.initialize(preferences);
	}

	private void initialize(Preferences preferences) {
		// cache a collator for sorting nodes
		this.collator = Collator.getInstance();
		this.selectedPreferencesHolder = new SimplePropertyValueModel();
		this.selectedPreferenceHolder = new SimplePropertyValueModel();
		this.add(this.buildSplitPane(preferences), BorderLayout.CENTER);
		this.add(this.buildControlPanel(), BorderLayout.SOUTH);
	}

	private JSplitPane buildSplitPane(Preferences preferences) {
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDoubleBuffered(true);
		splitPane.setLeftComponent(new JScrollPane(this.buildTree(preferences)));
		splitPane.setRightComponent(new JScrollPane(this.buildTable()));
		splitPane.setDividerLocation(150);
		return splitPane;
	}

	private JTree buildTree(Preferences preferences) {
		JTree tree = new JTree(this.buildTreeModel(preferences));
		this.treeSelectionModel = tree.getSelectionModel();
		this.treeSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.treeSelectionModel.addTreeSelectionListener(this.buildTreeSelectionListener());
		tree.setCellRenderer(this.buildTreeCellRenderer());
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setDoubleBuffered(true);
		return tree;
	}

	private TreeModel buildTreeModel(Preferences preferences) {
		DefaultTreeModel treeModel = new DefaultTreeModel(null, true);	// true = asks allows children
		treeModel.setRoot(this.buildRootNode(preferences, treeModel));
		return treeModel;
	}

	private TreeNode buildRootNode(Preferences preferences, DefaultTreeModel treeModel) {
		try {
			return this.buildRootNode2(preferences, treeModel);
		} catch (BackingStoreException ex) {
			this.handleException(ex);
			return null;
		}
	}

	private TreeNode buildRootNode2(Preferences preferences, DefaultTreeModel treeModel) throws BackingStoreException {
		if (preferences == null) {
			return new DefaultRootNode(treeModel);
		}
		return new PreferencesNode(treeModel, preferences);
	}
	private TreeSelectionListener buildTreeSelectionListener() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				PreferencesPanel.this.treeSelectionChanged();
			}
		};
	}

	/**
	 * All the nodes in the tree implement LocalNode.
	 */
	private TreeCellRenderer buildTreeCellRenderer() {
		return new SimpleTreeCellRenderer() {
			protected String buildText(Object value) {
				return ((LocalNode) value).displayString();
			}
		};
	}

	private JTable buildTable() {
		this.preferencesAdapter = this.buildSortedPreferencesAdapter();
		this.tableModel = new TableModelAdapter(this.preferencesAdapter, this.buildColumnAdapter());
		this.tableSelectionModel = this.buildTableSelectionModel(this.preferencesAdapter);

		JTable table = new JTable(this.tableModel);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);	// see Java bug 5007652
		table.setSelectionModel(this.tableSelectionModel);
		table.setDoubleBuffered(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		return table;
	}

	private ListValueModel buildSortedPreferencesAdapter() {
		return new SortedListValueModelAdapter(this.buildPreferencesAdapter(), this.buildPreferenceComparator());
	}

	/**
	 * Override setting the preference value to truncate the string to the
	 * maximum allowed length.
	 */
	private CollectionValueModel buildPreferencesAdapter() {
		return new PreferencesCollectionValueModel(this.selectedPreferencesHolder) {
			protected PreferencePropertyValueModel buildModel(String key) {
				return new PreferencePropertyValueModel(this.subjectHolder, key) {
					protected String convertToString(Object o) {
						String string = super.convertToString(o);
						if (string == null) {
							return string;
						}
						if (string.length() > Preferences.MAX_VALUE_LENGTH) {
							string = string.substring(0, Preferences.MAX_VALUE_LENGTH);
						}
						return string;
					}

				};
			}

		};
	}

	private Comparator buildPreferenceComparator() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				return PreferencesPanel.this.collator.compare(((PreferencePropertyValueModel) o1).getKey(), ((PreferencePropertyValueModel) o2).getKey());
			}
		};
	}

	private ColumnAdapter buildColumnAdapter() {
		return new PreferenceColumnAdapter();
	}

	private ObjectListSelectionModel buildTableSelectionModel(ListValueModel preferencesHolder) {
		ObjectListSelectionModel tsm = new ObjectListSelectionModel(new ListModelAdapter(preferencesHolder));
		tsm.addListSelectionListener(this.buildTableSelectionListener());
		tsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return tsm;
	}

	private ListSelectionListener buildTableSelectionListener() {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				PreferencesPanel.this.tableSelectionChanged(e);
			}
		};
	}

	void tableSelectionChanged(ListSelectionEvent e) {
		this.selectedPreferenceHolder.setValue(this.tableSelectionModel.getSelectedValue());
	}

	private JPanel buildControlPanel() {
		this.controlPanel = new JPanel(new GridLayout(2, 4));
		this.controlPanel.add(this.buildAddNodeButton());
		this.controlPanel.add(this.buildRenameNodeButton());
		this.controlPanel.add(this.buildRemoveNodeButton());
		this.controlPanel.add(this.buildExportButton());
		this.controlPanel.add(this.buildAddPreferenceButton());
		this.controlPanel.add(this.buildRenamePreferenceButton());
		this.controlPanel.add(this.buildRemovePreferenceButton());
		return this.controlPanel;
	}
	private JButton buildAddNodeButton() {
		return new JButton(this.buildAddNodeAction());
	}
	private JButton buildRenameNodeButton() {
		return new JButton(this.buildRenameNodeAction());
	}
	private JButton buildRemoveNodeButton() {
		return new JButton(this.buildRemoveNodeAction());
	}
	private JButton buildExportButton() {
		return new JButton(this.buildExportNodeAction());
	}
	private JButton buildAddPreferenceButton() {
		return new JButton(this.buildAddPreferenceAction());
	}
	private JButton buildRenamePreferenceButton() {
		return new JButton(this.buildRenamePreferenceAction());
	}
	private JButton buildRemovePreferenceButton() {
		return new JButton(this.buildRemovePreferenceAction());
	}


	// ********** public API **********

	public void addAction(Action action) {
		this.controlPanel.add(new JButton(action));
	}


	// ********** accessors **********

	Collator getCollator() {
		return this.collator;
	}


	// ********** behavior **********

	void treeSelectionChanged() {
		this.selectedPreferencesHolder.setValue(this.selectedPreferences());
	}

	private void handleException(Exception ex) {
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, ex.getMessage(), ex.getClass().getName(), JOptionPane.ERROR_MESSAGE);
	}

	private String promptForNodeName() {
		String name = JOptionPane.showInputDialog("Name");
		if (name == null) {
			return null;
		}
		if (name.length() > Preferences.MAX_NAME_LENGTH) {
			name = name.substring(0, Preferences.MAX_NAME_LENGTH);
		}
		if (name.indexOf('/') != -1) {
			return null;
		}
		return name;
	}

	private String promptForPreferenceName() {
		String name = JOptionPane.showInputDialog("Name");
		if (name == null) {
			return null;
		}
		if (name.length() > Preferences.MAX_KEY_LENGTH) {
			name = name.substring(0, Preferences.MAX_KEY_LENGTH);
		}
		return name;
	}


	// ********** convenience methods **********

	private PreferencesNode selectedPreferencesNode() {
		if (this.treeSelectionModel.getSelectionCount() != 1) {
			return null;
		}
		// the DefaultRootNode should never be visible and selected
		// so we can safely cast to PreferencesNode
		return (PreferencesNode) this.treeSelectionModel.getSelectionPath().getLastPathComponent();
	}

	private Preferences selectedPreferences() {
		PreferencesNode node = this.selectedPreferencesNode();
		return (node == null) ? null : node.preferences();
	}

	private PreferencePropertyValueModel selectedPreference() {
		return (PreferencePropertyValueModel) this.selectedPreferenceHolder.getValue();
	}

	private PreferencePropertyValueModel preference(String key) {
		for (Iterator stream = (Iterator) this.preferencesAdapter.getValue(); stream.hasNext(); ) {
			PreferencePropertyValueModel next = (PreferencePropertyValueModel) stream.next();
			if (next.getKey().equals(key)) {
				return next;
			}
		}
		throw new IllegalArgumentException(key);
	}


	// ********** add child node **********

	private Action buildAddNodeAction() {
		return new AbstractAction("Add Child Node") {
			public void actionPerformed(ActionEvent event) {
				PreferencesPanel.this.addChildToSelectedNode();
			}
		};
	}

	void addChildToSelectedNode() {
		PreferencesNode selectedNode = this.selectedPreferencesNode();
		if (selectedNode == null) {
			return;
		}
		String name = this.promptForNodeName();
		if (name == null) {
			return;
		}

		Preferences preferences = selectedNode.preferences();
		// this will cause the preferences to be created if it does not already exist
		Preferences newPreferences = preferences.node(name);
		try {
			// flush the change, so an event is fired and the node is
			// created and added to the tree before we try to select it
			preferences.flush();
		} catch (BackingStoreException ex) {
			this.handleException(ex);
			return;	// skip the selection of a node that might not be there...
		}
		PreferencesNode newNode = selectedNode.childNodeFor(newPreferences);
		this.treeSelectionModel.setSelectionPath(new TreePath(newNode.getPath()));
	}


	// ********** rename node **********

	private Action buildRenameNodeAction() {
		return new AbstractAction("Rename Node") {
			public void actionPerformed(ActionEvent event) {
				PreferencesPanel.this.renameSelectedNode();
			}
		};
	}

	/**
	 * we can't simply rename the node - we have to clone
	 * it, then remove it...
	 */
	void renameSelectedNode() {
		PreferencesNode selectedNode = this.selectedPreferencesNode();
		if (selectedNode == null || selectedNode.cannotBeRemoved()) {
			return;
		}

		String name = this.promptForNodeName();
		if (name == null) {
			return;
		}

		Preferences oldPreferences = selectedNode.preferences();
		PreferencesNode parentNode = (PreferencesNode) selectedNode.getParent();
		Preferences parentPreferences = parentNode.preferences();
		try {
			if (parentPreferences.nodeExists(name)) {
				// cannot rename to same name as existing node
				return;
			}
		} catch (BackingStoreException ex) {
			this.handleException(ex);
			return;
		}

		// clear the selection *before* removing the node, so we are no longer
		// listening to the node when it gets deleted - it will be in an invalid state...
		this.treeSelectionModel.clearSelection();
		Preferences newPreferences = parentPreferences.node(name);
		try {
			this.clone(oldPreferences, newPreferences);
			oldPreferences.removeNode();
			// flush the changes, so events are fired and the tree is updated
			parentPreferences.flush();
		} catch (BackingStoreException ex) {
			this.handleException(ex);
			return;	// skip the selection of a node that might not be there...
		}
		PreferencesNode newNode = parentNode.childNodeFor(newPreferences);
		this.treeSelectionModel.setSelectionPath(new TreePath(newNode.getPath()));
	}

	private void clone(Preferences source, Preferences target) throws BackingStoreException {
		String[] names = source.childrenNames();
		int len = names.length;
		for (int i = 0; i < len; i++) {
			String name = names[i];
			this.clone(source.node(name), target.node(name));
		}

		names = source.keys();
		len = names.length;
		for (int i = 0; i < len; i++) {
			String name = names[i];
			target.put(name, source.get(name, ""));
		}
	}


	// ********** remove node **********

	private Action buildRemoveNodeAction() {
		return new AbstractAction("Remove Node") {
			public void actionPerformed(ActionEvent event) {
				PreferencesPanel.this.removeSelectedNode();
			}
		};
	}

	void removeSelectedNode() {
		PreferencesNode selectedNode = this.selectedPreferencesNode();
		if (selectedNode == null || selectedNode.cannotBeRemoved()) {
			return;
		}

		TreePath postRemoveSelectionPath = this.calculatePostRemoveSelectionPath(selectedNode);
		// clear the selection *before* removing the node, so we are no longer
		// listening to the node when it gets deleted - it will be in an invalid state...
		this.treeSelectionModel.clearSelection();
		Preferences preferences = selectedNode.preferences();
		try {
			preferences.removeNode();
			// flush the change, so an event is fired and the parent node is updated
			preferences.flush();
		} catch (BackingStoreException ex) {
			this.handleException(ex);
			return;	// skip the selection of the parent node...
		}
		this.treeSelectionModel.setSelectionPath(postRemoveSelectionPath);
	}

	/**
	 * Calculate what the selection path should be once the
	 * specified node has been removed from the tree.
	 */
	private TreePath calculatePostRemoveSelectionPath(PreferencesNode node) {
		PreferencesNode parentNode = (PreferencesNode) node.getParent();
		int childCount = parentNode.getChildCount();
		if (childCount == 1) {
			// the only child is to be removed - select the parent
			return new TreePath(parentNode.getPath());
		}
		int nodeIndex = parentNode.getIndex(node);
		if (nodeIndex == childCount - 1) {
			// the last child in the list is to be removed - select the previous child
			return new TreePath(((PreferencesNode) parentNode.getChildAt(nodeIndex - 1)).getPath());
		}
		// by default, select the next child
		return new TreePath(((PreferencesNode) parentNode.getChildAt(nodeIndex + 1)).getPath());
	}


	// ********** export **********

	private Action buildExportNodeAction() {
		return new AbstractAction("Export Node") {
			public void actionPerformed(ActionEvent event) {
				PreferencesPanel.this.exportSelectedNode();
			}
		};
	}

	void exportSelectedNode() {
		PreferencesNode selectedNode = this.selectedPreferencesNode();
		if (selectedNode == null) {
			return;
		}
		JFileChooser fc = new JFileChooser();
		int rc = fc.showSaveDialog(this);
		if (rc != JFileChooser.APPROVE_OPTION) {
			return;
		}
		try {
			OutputStream stream = new BufferedOutputStream(new FileOutputStream(fc.getSelectedFile()), 2048);
			selectedNode.preferences().exportSubtree(stream);
			stream.close();
		} catch (IOException ex) {
			this.handleException(ex);
		} catch (BackingStoreException ex) {
			this.handleException(ex);
		}
	}


	// ********** add preference **********

	private Action buildAddPreferenceAction() {
		return new AbstractAction("Add Preference") {
			public void actionPerformed(ActionEvent event) {
				PreferencesPanel.this.addPreferenceToSelectedNode();
			}
		};
	}

	void addPreferenceToSelectedNode() {
		Preferences preferences = this.selectedPreferences();
		if (preferences == null) {
			return;
		}
		String name = this.promptForPreferenceName();
		if (name == null) {
			return;
		}

		try {
			if (CollectionTools.contains(preferences.keys(), name)) {
				return;
			}
		} catch (BackingStoreException ex) {
			this.handleException(ex);
			return;	// skip the selection of a row that might not be there...
		}
		preferences.put(name, "");
		try {
			// flush the change, so an event is fired and the preference is
			// created and added to the table before we try to select it
			preferences.flush();
		} catch (BackingStoreException ex) {
			this.handleException(ex);
			return;	// skip the selection of a row that might not be there...
		}
		this.tableSelectionModel.setSelectedValue(this.preference(name));
	}


	// ********** rename preference **********

	private Action buildRenamePreferenceAction() {
		return new AbstractAction("Rename Preference") {
			public void actionPerformed(ActionEvent event) {
				PreferencesPanel.this.renameSelectedPreference();
			}
		};
	}

	void renameSelectedPreference() {
		PreferencePropertyValueModel preference = this.selectedPreference();
		if (preference == null) {
			return;
		}

		String name = this.promptForPreferenceName();
		if (name == null) {
			return;
		}

		Preferences preferences = this.selectedPreferences();
		try {
			if (CollectionTools.contains(preferences.keys(), name)) {
				return;
			}
		} catch (BackingStoreException ex) {
			this.handleException(ex);
			return;
		}

		String value = (String) preference.getValue();
		preferences.put(name, value);
		preferences.remove(preference.getKey());
		try {
			// flush the changes, so events are fired and the table is updated
			preferences.flush();
		} catch (BackingStoreException ex) {
			this.handleException(ex);
			return;	// skip the selection of a node that might not be there...
		}
		this.tableSelectionModel.setSelectedValue(this.preference(name));
	}


	// ********** remove preference **********

	private Action buildRemovePreferenceAction() {
		return new AbstractAction("Remove Preference") {
			public void actionPerformed(ActionEvent event) {
				PreferencesPanel.this.removeSelectedPreference();
			}
		};
	}

	void removeSelectedPreference() {
		PreferencePropertyValueModel preference = this.selectedPreference();
		if (preference == null) {
			return;
		}

		Preferences preferences = this.selectedPreferences();
		preferences.remove(preference.getKey());
		try {
			// flush the change, so an event is fired and the table is updated
			preferences.flush();
		} catch (BackingStoreException ex) {
			this.handleException(ex);
			return;	// skip the selection of the parent node...
		}
	}


// ********** inner classes **********

/**
 * Define interface used by the tree cell renderer.
 */
private interface LocalNode {
	String displayString();
}


/**
 * The implied root node that holds both the system
 * and current user root nodes.
 */
private class DefaultRootNode
	extends DefaultMutableTreeNode
	implements LocalNode
{
	private static final String NAME = "Root";

	DefaultRootNode(DefaultTreeModel treeModel) throws BackingStoreException {
		super(NAME, true);	// true = allows children
		this.initialize(treeModel);
	}

	private void initialize(DefaultTreeModel treeModel) throws BackingStoreException {
		// the "default root" node has two, hard-coded children
		this.add(this.buildNode(treeModel, Preferences.systemRoot(), "System"));
		this.add(this.buildNode(treeModel, Preferences.userRoot(), "User"));
	}

	private MutableTreeNode buildNode(DefaultTreeModel treeModel, Preferences preferences, String rootName) throws BackingStoreException {
		return new RootPreferencesNode(treeModel, preferences, rootName);
	}

	/**
	 * Simply display the string "root".
	 * @see org.eclipse.persistence.tools.workbench.test.uitools.PreferencesPanel.LocalNode#displayString()
	 */
	public String displayString() {
		return NAME;
	}

}


/**
 * A node for a Preferences. It will automatically populate itself
 * with the appropriate child nodes upon construction. Then it
 * will listen for any child nodes being added or removed and update
 * itself accordingly.
 */
private class PreferencesNode
	extends DefaultMutableTreeNode
	implements LocalNode, NodeChangeListener, Comparable
{
	private DefaultTreeModel treeModel;


	// ********** constructor/initialization **********

	PreferencesNode(DefaultTreeModel treeModel, Preferences preferences) throws BackingStoreException {
		super(preferences, true);	// true = allows children
		this.treeModel = treeModel;
		this.preferences().addNodeChangeListener(this);
		this.initializeChildNodes();
	}

	/**
	 * Add nodes for the preferences's "children" (which are other preferenceses).
	 */
	private void initializeChildNodes() throws BackingStoreException {
		Preferences preferences = this.preferences();
		String[] childrenNames = (String[]) CollectionTools.sort(preferences.childrenNames(), this.collator());
		int len = childrenNames.length;
		for (int i = 0; i < len; i++) {
			this.add(this.buildNode(preferences.node(childrenNames[i])));
		}
	}

	private PreferencesNode buildNode(Preferences preferences) throws BackingStoreException {
		return new PreferencesNode(this.treeModel, preferences);
	}


	// ********** LocalNode implementation **********

	/**
	 * Display the preferences's name.
	 * @see org.eclipse.persistence.tools.workbench.test.uitools.PreferencesPanel.LocalNode#displayString()
	 */
	public String displayString() {
		return this.name();
	}


	// ********** NodeChangeListener implementation **********

	/**
	 * @see java.util.prefs.NodeChangeListener#childAdded(java.util.prefs.NodeChangeEvent)
	 */
	public void childAdded(NodeChangeEvent evt) {
		try {
			this.addNodeFor(evt.getChild());
		} catch (BackingStoreException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void addNodeFor(Preferences preferences) throws BackingStoreException {
		PreferencesNode childNode = this.buildNode(preferences);
		this.treeModel.insertNodeInto(childNode, this, this.insertionIndexOf(childNode));
	}

	private int insertionIndexOf(PreferencesNode childNode) {
		return (this.children == null) ? 0 : CollectionTools.insertionIndexOf(this.children, childNode);
	}

	/**
	 * @see java.util.prefs.NodeChangeListener#childRemoved(java.util.prefs.NodeChangeEvent)
	 */
	public void childRemoved(NodeChangeEvent evt) {
		this.removeChildNodeFor(evt.getChild());
	}

	private void removeChildNodeFor(Preferences preferences) {
		this.treeModel.removeNodeFromParent(this.childNodeFor(preferences));
	}

	PreferencesNode childNodeFor(Preferences child) {
		for (Enumeration stream = this.children(); stream.hasMoreElements(); ) {
			PreferencesNode childNode = (PreferencesNode) stream.nextElement();
			if (childNode.preferences() == child) {
				return childNode;
			}
		}
		throw new IllegalArgumentException(child.toString());
	}

											
	// ********** Comparable implementation **********

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		return this.collator().compare(this.name(), ((PreferencesNode) o).name());
	}


	// ********** other methods **********

	Preferences preferences() {
		return (Preferences) this.getUserObject();
	}

	private Collator collator() {
		return PreferencesPanel.this.getCollator();
	}

	/**
	 * Return whether the node can be removed.
	 * Most nodes can be removed.
	 */
	boolean canBeRemoved() {
		return true;
	}

	/**
	 * Return whether the node cannot be removed.
	 */
	boolean cannotBeRemoved() {
		return ! this.canBeRemoved();
	}

	String name() {
		return this.preferences().name();
	}

}


/**
 * A node for a "root" Preferences (system or user). The "root" Preferences
 * do not have names, so require one to be supplied upon construction.
 */
private class RootPreferencesNode extends PreferencesNode {
	private String rootName;

	RootPreferencesNode(DefaultTreeModel treeModel, Preferences rootPreferences, String rootName) throws BackingStoreException {
		super(treeModel, rootPreferences);
		this.rootName = rootName;
	}

	public String displayString() {
		return this.rootName;
	}

	/**
	 * Return whether the node can be removed.
	 * Root nodes canNOT be removed.
	 */
	boolean canBeRemoved() {
		return false;
	}

}


/**
 * There are 2 columns: one for the preference key and one for the preference
 * value. Everything is displayed and edited as strings.
 */
private static class PreferenceColumnAdapter implements ColumnAdapter {
	public static final int COLUMN_COUNT = 2;

	public static final int KEY_COLUMN = 0;
	public static final int VALUE_COLUMN = 1;

	private static final String[] COLUMN_NAMES = new String[] {"Key", "Value"};


	public int getColumnCount() {
		return COLUMN_COUNT;
	}

	public String getColumnName(int index) {
		return COLUMN_NAMES[index];
	}

	public Class getColumnClass(int index) {
		switch (index) {
			case KEY_COLUMN:			return Object.class;
			case VALUE_COLUMN:		return Object.class;
			default: 						return Object.class;
		}
	}

	public boolean isColumnEditable(int index) {
		return index != KEY_COLUMN;
	}

	public PropertyValueModel[] cellModels(Object subject) {
		PreferencePropertyValueModel preference = (PreferencePropertyValueModel) subject;
		PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];

		result[KEY_COLUMN]		= new SimplePropertyValueModel(preference.getKey());
		result[VALUE_COLUMN]		= preference;

		return result;
	}

}


}
