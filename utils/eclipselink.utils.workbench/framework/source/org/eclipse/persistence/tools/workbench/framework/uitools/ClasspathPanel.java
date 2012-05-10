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
package org.eclipse.persistence.tools.workbench.framework.uitools;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.PrimitiveListTreeModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


public final class ClasspathPanel 
	extends AbstractPanel
{	
	private ListValueModel classpathEntriesHolder;
	private ValueModel rootFolderHolder;
	private DefaultClasspathDirectoryHolder defaultClasspathDirectoryHolder;

	/** for the actual list/tree */
	private JTree classpathTree;
	private TreeModel classpathModel;
	private TreeSelectionModel classpathSelectionModel;
	
	/** this indicates whether the classpath entries should be editable */
	private boolean editable;

	/** Determines if the path should be converted to a relative path or not. **/
	private boolean convertToRelativePath;

	/** this will add a new entry directly into the tree */
	private Action addEntryAction;

	/** this will open a class chooser dialog */
	private Action browseAction;

	/** this will remove the currently selected entries from the classpath */
	private Action removeAction;

	/** this will move the currently selected entry up one position in the classpath */
	private Action upAction;

	/** this will move the currently selected entry down one position in the classpath */
	private Action downAction;


	// *********** most recent directory preference
	public static final String MOST_RECENT_CLASSPATH_DIRECTORY_PREFERENCE = "recent classpath directory";


	// ********** constructors **********

	public ClasspathPanel(ApplicationContext context, ListValueModel classpathEntriesHolder, ValueModel rootFileHolder) {
		this(context, classpathEntriesHolder, rootFileHolder, true, "CLASSPATH_PANEL_TITLE");
	}

	public ClasspathPanel(ApplicationContext context, ListValueModel classpathEntriesHolder, boolean shouldBeEditable) {
		this(context, classpathEntriesHolder, shouldBeEditable, "CLASSPATH_PANEL_TITLE");
	}

	public ClasspathPanel(ApplicationContext context, ListValueModel classpathEntriesHolder, boolean shouldBeEditable, String title) {
		this(context, classpathEntriesHolder, new SimplePropertyValueModel(), shouldBeEditable, title);
	}

	public ClasspathPanel(ApplicationContext context, ListValueModel classpathEntriesHolder, ValueModel rootFileHolder, boolean shouldBeEditable) {
		this(context, classpathEntriesHolder, rootFileHolder, shouldBeEditable, "CLASSPATH_PANEL_TITLE");
	}

	public ClasspathPanel(ApplicationContext context, ListValueModel classpathEntriesHolder, ValueModel rootFileHolder, boolean shouldBeEditable, String title) {
		super(context);
		initialize(classpathEntriesHolder, rootFileHolder, shouldBeEditable);
		initializeLayout(title);
	}
	
	// ********** initialization **********
	
	private void initialize(ListValueModel classpathEntriesHolder, ValueModel rootFolderHolder, boolean shouldBeEditable) {
		this.classpathEntriesHolder = classpathEntriesHolder;
		this.rootFolderHolder = rootFolderHolder;
		this.defaultClasspathDirectoryHolder = DefaultClasspathDirectoryHolder.NULL_INSTANCE;
		classpathModel = this.buildClasspathModel(classpathEntriesHolder);
		classpathSelectionModel = this.buildClasspathSelectionModel();
		addEntryAction = this.buildAddEntryAction();
		browseAction = this.buildBrowseAction();
		removeAction = this.buildRemoveAction();
		upAction = this.buildUpAction();
		downAction = this.buildDownAction();
		this.editable = shouldBeEditable;

		// When all the entries are removed, root node is been used for rendering
		// so it needs a user object otherwise another check will have to be
		// performed
		((DefaultMutableTreeNode) classpathModel.getRoot()).setUserObject("");
	}
	
	protected ApplicationContext initializeContext(ApplicationContext parentContext) {
		return parentContext.buildExpandedResourceRepositoryContext(UIToolsResourceBundle.class);
	}

	private TreeModel buildClasspathModel(ListValueModel model) {
		return new PrimitiveListTreeModel(model) {
			protected void primitiveChanged(int index, Object newValue) {
				ClasspathPanel.this.replaceEntry(index, newValue);
			}
		};
	}

	private TreeModelListener buildTreeModelListener(final JTree classpathTree) {
		return new TreeModelListener() {
			public void treeNodesChanged(TreeModelEvent e) {
			}

			public void treeNodesInserted(TreeModelEvent e) {
				TreeModel model = (TreeModel) e.getSource();
				ExpandPathRunner runner = new ExpandPathRunner(classpathTree, model.getRoot());
				EventQueue.invokeLater(runner);
			}

			public void treeNodesRemoved(TreeModelEvent e) {
			}

			public void treeStructureChanged(TreeModelEvent e) {
			}
		};
	}

	private TreeSelectionModel buildClasspathSelectionModel() {
		TreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
		selectionModel.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		return selectionModel;
	}

	private Action buildAddEntryAction() {
		Action action = new AbstractFrameworkAction(getApplicationContext()) {
			protected void initialize() {
				setText(resourceRepository().getString("ADD_ENTRY_BUTTON_TEXT"));
				setMnemonic(resourceRepository().getMnemonic("ADD_ENTRY_BUTTON_TEXT"));
			}

			public void actionPerformed(ActionEvent event) {
				ClasspathPanel.this.addEntry();
			}
		};
		action.setEnabled(true);
		return action;
	}

	private Action buildBrowseAction() {
		Action action = new AbstractFrameworkAction(getApplicationContext()) {
			protected void initialize() {
				this.initializeTextAndMnemonic("BROWSE_BUTTON_1");
			}
			public void actionPerformed(ActionEvent event) {
				ClasspathPanel.this.promptToAddEntries();
			}
		};
		action.setEnabled(true);
		return action;
	}

	private Action buildRemoveAction() {
		Action action = new AbstractFrameworkAction(getApplicationContext()) {
			protected void initialize() {
				setText(resourceRepository().getString("REMOVE_BUTTON_TEXT"));
				setMnemonic(resourceRepository().getMnemonic("REMOVE_BUTTON_TEXT"));
			}
			public void actionPerformed(ActionEvent event) {
				ClasspathPanel.this.removeEntries();
			}
		};
		action.setEnabled(false);
		return action;
	}

	private Action buildUpAction() {
		Action action = new AbstractFrameworkAction(getApplicationContext()) {
			protected void initialize() {
				setText(resourceRepository().getString("UP_BUTTON_TEXT"));
				setMnemonic(resourceRepository().getMnemonic("UP_BUTTON_TEXT"));
			}
			public void actionPerformed(ActionEvent event) {
				ClasspathPanel.this.moveSelectedEntriesUp();
			}
		};
		action.setEnabled(false);
		return action;
	}

	private Action buildDownAction() {
		Action action = new AbstractFrameworkAction(getApplicationContext()) {
			protected void initialize() {
				setText(resourceRepository().getString("DOWN_BUTTON_TEXT"));
				setMnemonic(resourceRepository().getMnemonic("DOWN_BUTTON_TEXT"));
			}
			public void actionPerformed(ActionEvent event) {
				ClasspathPanel.this.moveSelectedEntriesDown();
			}
		};
		action.setEnabled(false);
		return action;
	}

	protected void initializeLayout(String title) {
		GridBagConstraints constraints = new GridBagConstraints();
		setBorder(BorderFactory.createCompoundBorder
		(
			buildTitledBorder(title),
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		));
		
		// Commented out until jdk gets off their butt and makes the JList cells editable.
		// Replaced temporarily with JTree 
		/*
		JList classpathListBox = new JList();
		classpathListBox.setModel(classpathListModel);
		classpathListBox.setSelectionModel(classpathListSelectionModel);
		classpathListBox.setDoubleBuffered(true);
		*/
		classpathTree = buildClasspathTree();

		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(1, 0, 0, 0);

		JScrollPane scrollPane = new JScrollPane(classpathTree);
		scrollPane.setMinimumSize(new Dimension(1, 1));
		scrollPane.setPreferredSize(new Dimension(1, 1));
		this.add(scrollPane, constraints);

		// button panel
		JPanel buttonPanel = new AccessibleTitledPanel(new GridLayout(5, 1, 0, 5));
		buttonPanel.add(new JButton(addEntryAction));
		buttonPanel.add(new JButton(browseAction));
		buttonPanel.add(new JButton(removeAction));
		buttonPanel.add(new JButton(upAction));
		buttonPanel.add(new JButton(downAction));

		constraints.gridx		= 1;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(1, 5, 0, 0);

		this.add(buttonPanel, constraints);
		addAlignRight(buttonPanel);
	}
	
	private JTree buildClasspathTree() {
		JTree classpathTree = new SwingComponentFactory.AccessibleTree(classpathModel) {
			public void cancelEditing() {
				if (isEditing()) {
					TreePath path = getEditingPath();
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
					String currentEntry = (String) node.getUserObject();

					super.cancelEditing();

					if (StringTools.stringIsEmpty(currentEntry)) {
						super.cancelEditing();
						int rowIndex = getRowForPath(path);
						classpathEntriesHolder().removeItems(rowIndex, 1);
					}
				}
			}
		};
		classpathTree.setSelectionModel(classpathSelectionModel);
		classpathTree.addTreeSelectionListener(this.buildClasspathSelectionListener());
		DefaultTreeCellRenderer renderer = this.buildTreeCellRenderer();
		classpathTree.setCellRenderer(renderer);
		classpathTree.setCellEditor(buildTreeCellEditor(classpathTree, renderer));
		classpathTree.setRootVisible(false);
		classpathTree.expandPath(new TreePath(classpathModel.getRoot()));
		classpathTree.setEditable(this.editable);		// key feature!
		classpathTree.setRowHeight(0);
		classpathTree.setExpandsSelectedPaths(true);
		classpathTree.setDoubleBuffered(true);
		classpathModel.addTreeModelListener(buildTreeModelListener(classpathTree));
		return classpathTree;
	}
	
	private TreeSelectionListener buildClasspathSelectionListener() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				ClasspathPanel.this.classpathSelectionChanged();
			}
		};
	}
	
	File getRootFolder() {
		return (File) this.rootFolderHolder.getValue();
	}
	
	private DefaultTreeCellRenderer buildTreeCellRenderer() {
		return new DefaultTreeCellRenderer() {
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean focus) {
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focus);
				if (leaf) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
					Icon icon = this.buildIconForFile(new File((String) node.getUserObject()));
					this.setLeafIcon(icon);
					this.setIcon(icon);
				}
				return this;
			}

			private Icon buildIconForFile(File file) {
				if ( ! file.isAbsolute()) {
					File rootFolder = ClasspathPanel.this.getRootFolder();
					if (rootFolder != null) {
						file = new File(rootFolder, file.getPath());
					}
				}
				return ClasspathPanel.this.resourceRepository().getIcon(this.buildIconKeyForFile(file));
			}

			private String buildIconKeyForFile(File file) {
				if ( ! file.isAbsolute()) {
					// the file can still be relative if the project has not been saved yet
					return "folder";
				}
				if (file.exists()) {
					return file.isDirectory() ? "folder" : "file";
				}
				return "warning";
			}
		};
	}
	
	private TreeCellEditor buildTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
		return new DefaultTreeCellEditor(tree, renderer);
	}

	// ********** HelpTopicID implementation **********
	
	public String getTopicID() {
		return "project.classpath";
	}

	// ********** queries **********

	private ListValueModel classpathEntriesHolder() {
		return this.classpathEntriesHolder;
	}
	
	private int lastEntryIndex() {
		return classpathEntriesHolder().size() - 1;
	}

	// ********** behavior **********

	private void classpathSelectionChanged() {
		this.enableActions();
	}

	//TODO problem here, the up and down buttons are enabled even when only one item selected
	private void enableActions() {
		boolean anyEntrySelected = ! this.classpathSelectionModel.isSelectionEmpty();
		boolean firstEntrySelected = this.classpathSelectionModel.getMinSelectionRow() == 0;
		boolean lastEntrySelected = this.classpathSelectionModel.getMaxSelectionRow() == this.lastEntryIndex();
		
		removeAction.setEnabled(anyEntrySelected);
		upAction.setEnabled(anyEntrySelected && ! firstEntrySelected);
		downAction.setEnabled(anyEntrySelected && ! lastEntrySelected);
	}

	private void removeEntries() {
		classpathEntriesHolder().removeItems(classpathSelectionModel.getMinSelectionRow(), classpathSelectionModel.getSelectionCount());
	}

	private void replaceEntry(int index, Object newEntry) {
		if ( ! newEntry.equals("")) {
			classpathEntriesHolder().replaceItem(index, newEntry);
		}
	}

	private void moveSelectedEntriesUp() {
		// move the entry just before the selection to just after
		Object entry = classpathEntriesHolder().removeItem(classpathSelectionModel.getMinSelectionRow() - 1);
		classpathEntriesHolder().addItem(classpathSelectionModel.getMaxSelectionRow() + 1, entry);
		// since the selection does not change, we need to recalculate the state of the actions
		this.enableActions();
	}

	private void moveSelectedEntriesDown() {
		// move the entry just after the selection to just before
		Object entry = classpathEntriesHolder().removeItem(classpathSelectionModel.getMaxSelectionRow() + 1);
		classpathEntriesHolder().addItem(classpathSelectionModel.getMinSelectionRow(), entry);
		// since the selection does not change, we need to recalculate the state of the actions
		this.enableActions();
	}

	private void addEntry() {
		if (classpathTree.isEditing()) {
			classpathTree.cancelEditing();
		}
		TreeNode rootNode = (TreeNode) classpathModel.getRoot();

		// Get the best row for insertion
		int index = rootNode.getChildCount();
		int[] selectedRows = classpathTree.getSelectionRows();

		if ((selectedRows != null) && (selectedRows.length > 0)) {
			index = selectedRows[selectedRows.length - 1];
		}

		// Add the item to the holder, which will then be added to the tree model
		classpathEntriesHolder().addItems(index, Collections.singletonList("")); // Empty entry

		// Get the node and start editing it
		TreeNode newEntryNode = rootNode.getChildAt(index);
		classpathTree.startEditingAtPath(new TreePath(new Object[] { rootNode, newEntryNode }));
	}

	void promptToAddEntries() {
		if (classpathTree.isEditing()) {
			classpathTree.cancelEditing();
		}
		File[] selectedFiles = this.promptToAddFiles();
		int len = selectedFiles.length;
		if (len == 0) {
			return;
		}
		List selectedFileNames = new ArrayList(len);
		for (int i = 0; i < len; i++) {
			selectedFileNames.add(selectedFiles[i].getPath());
		}

		// remove *exact* duplicates
		CollectionTools.removeAll(selectedFileNames, (Iterator) classpathEntriesHolder().getValue());
		if (selectedFileNames.isEmpty()) {
			return;
		}

		TreeNode rootNode = (TreeNode) this.classpathModel.getRoot();
		this.classpathSelectionModel.clearSelection();
		int newSelectionStartIndex = this.classpathEntriesHolder().size();

		this.classpathEntriesHolder().addItems(newSelectionStartIndex, selectedFileNames);

		for (Iterator stream = selectedFileNames.iterator(); stream.hasNext(); ) {
			this.classpathSelectionModel.addSelectionPath(this.pathFor(rootNode, stream.next()));
		}

		// The up/down buttons do not get disabled the first time an entry is
		// added to the list. Looks like a listener order problem so I am
		// calling enableActions after the selection is complete.
		// classpathSelectionChanged() gets called, but the TreeSelectionModel
		// and it's underlying ListSelectionModel appear out of synch
		this.enableActions();
	}

	private TreePath pathFor(TreeNode parentNode, Object entry) {
		for(Enumeration stream = parentNode.children(); stream.hasMoreElements(); ) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) stream.nextElement();
			if (node.getUserObject().equals(entry)) {
				return new TreePath(node.getPath());
			}
		}
		throw new IllegalArgumentException("missing entry");
	}

	private File[] promptToAddFiles() {
		JFileChooser fileChooser = this.buildFileChooser();
		int selection = fileChooser.showDialog(SwingUtilities.windowForComponent(this), this.resourceRepository().getString("DIALOG.OK_BUTTON_TEXT"));
		
		if (selection == JFileChooser.APPROVE_OPTION) {
			this.defaultClasspathDirectoryHolder.setDefaultClasspathDirectory(fileChooser.getCurrentDirectory());
			return fileChooser.getSelectedFiles();
		}
		return new File[0];
	}

	private JFileChooser buildFileChooser() {
		JFileChooser fc = new FileChooser(this.getDefaultClasspathDirectory(), this.getRootFolder());
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setDialogTitle(this.resourceRepository().getString("ADD_CLASSPATH_ENTRY_DIALOG.TITLE"));		
		fc.setMultiSelectionEnabled(true);
		fc.setFileFilter(this.buildFileFilter());
		return fc;
	}

	private FileFilter buildFileFilter() {
		return new FileFilter() {
			public boolean accept(File file) {
				String name = file.getName().toLowerCase();
				return name.endsWith(".jar")
						|| name.endsWith(".zip")
						|| file.isDirectory();
			}
			public String getDescription() {
				return resourceRepository().getString(".jar.zip");
			}
		};
	}

	private File getDefaultClasspathDirectory() {
		return this.defaultClasspathDirectoryHolder.getDefaultClasspathDirectory();
	}

	public void setDefaultClasspathDirectoryHolder(DefaultClasspathDirectoryHolder defaultClasspathDirectoryHolder) {
		this.defaultClasspathDirectoryHolder = defaultClasspathDirectoryHolder;
	}

	// ********** inner classes **********

	/**
	 * 
	 */
	private class ExpandPathRunner implements Runnable {
		private final JTree classpathTree;
		private final Object rootNode;

		ExpandPathRunner(JTree classpathTree, Object rootNode) {
			super();
			this.classpathTree = classpathTree;
			this.rootNode = rootNode;
		}

		public void run() {
			TreePath path = new TreePath(rootNode);

			if (!classpathTree.isExpanded(path)) {
				classpathTree.expandPath(path);
			}
		}

	}

	/**
	 * Used by the ClasspathPanel to indirectly reference the default
	 * classpath directory used to initialize the FileChooser.
	 */
	public interface DefaultClasspathDirectoryHolder {

		/**
		 * Return the directory to be used as the default in the file
		 * chooser used by the classpath panel.
		 */
		File getDefaultClasspathDirectory();

		/**
		 * Set the directory to be used as the default in the file
		 * chooser used by the classpath panel.
		 */
		void setDefaultClasspathDirectory(File defaultClasspathDirectory);

		DefaultClasspathDirectoryHolder NULL_INSTANCE =
			new DefaultClasspathDirectoryHolder() {
				public File getDefaultClasspathDirectory() {
					return null;
				}
				public void setDefaultClasspathDirectory(File defaultClasspathDirectory) {
					// do nothing
				}
				public String toString() {
					return "NullDefaultClasspathDirectoryHolder";
				}
			};
	}

}
