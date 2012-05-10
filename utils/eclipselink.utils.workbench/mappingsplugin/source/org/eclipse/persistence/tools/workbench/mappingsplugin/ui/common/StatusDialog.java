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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.accessibility.AccessibleContext;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWError;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.node.Node;


/**
 * This dialog shows a tree representation of status. A status is composed of
 * an item that is been described by a collection of status. The status can have
 * a collection of children.
 */
public class StatusDialog extends AbstractDialog
{
	/**
	 * The id used to retrieve the help content.
	 */
	private final String helpTopicId;

	/**
	 * The key used to retrieve the text to be shown above the tree.
	 */
	private final String messageKey;

	/**
	 * The collection of <code>ModelStatus</code>.
	 */
	private final Collection status;

	/**
	 * The tree used to display all the <code>ModelStatus</code>.
	 */
	JTree tree;

	/**
	 * Creates a new <code>StatusDialog</code>.
	 *
	 * @param context The context of this dialog
	 * @param status The collection of <code>ModelStatus</code> to be displayed
	 * @param titleKey The key used to retreive the title from the repository
	 */
	public StatusDialog(WorkbenchContext context,
							  Collection status,
							  String titleKey)
	{
		this(context, status, titleKey, "dialog.status");
	}

	/**
	 * Creates a new <code>StatusDialog</code>.
	 *
	 * @param context The context of this dialog
	 * @param owner  The dialog owner of this dialog
	 * @param status The collection of <code>ModelStatus</code> to be displayed
	 * @param titleKey The key used to retreive the title from the repository
	 */
	public StatusDialog(WorkbenchContext context,
						Dialog owner,
						Collection status,
						String titleKey)
	{
		this(context, owner, status, titleKey, "dialog.status");
	}

	/**
	 * Creates a new <code>StatusDialog</code>.
	 *
	 * @param context The context of this dialog
	 * @param status The collection of <code>ModelStatus</code> to be displayed
	 * @param titleKey The key used to retreive the title from the repository
	 * @param helpTopicId The key used to retrieve the help content
	 */
	public StatusDialog(WorkbenchContext context,
							  Collection status,
							  String titleKey,
							  String helpTopicId)
	{
		this(context, status, titleKey, "STATUS_DIALOG_MESSAGE", helpTopicId);
	}
	
	/**
	 * Creates a new <code>StatusDialog</code>.
	 *
	 * @param context The context of this dialog
	 * @param owner  The dialog owner of this dialog
	 * @param status The collection of <code>ModelStatus</code> to be displayed
	 * @param titleKey The key used to retreive the title from the repository
	 * @param helpTopicId The key used to retrieve the help content
	 */
	public StatusDialog(WorkbenchContext context,
						Dialog owner,
						Collection status,
						String titleKey,
						String helpTopicId)
	{
		this(context, owner, status, titleKey, "STATUS_DIALOG_MESSAGE", helpTopicId);
	}
	
	/**
	 * Creates a new <code>StatusDialog</code>.
	 *
	 * @param context The context of this dialog
	 * @param status The collection of <code>ModelStatus</code> to be displayed
	 * @param titleKey The key used to retreive the title from the repository
	 * @param messageKey The key used to retrieve the text to be shown above the
	 * tree
	 * @param helpTopicId The key used to retrieve the help content
	 */
	public StatusDialog(WorkbenchContext context,
							  Collection status,
							  String titleKey,
							  String messageKey,
							  String helpTopicId)
	{
		super(context);
		if (status == null) {
			throw new NullPointerException("The collection of status cannot be null");
		}
		this.status = status;
		this.messageKey = messageKey;
		this.helpTopicId = helpTopicId;
		setTitle(resourceRepository().getString(titleKey));
	}
	
	/**
	 * Creates a new <code>StatusDialog</code>.
	 *
	 * @param context The context of this dialog
	 * @param owner  The dialog owner of this dialog
	 * @param status The collection of <code>ModelStatus</code> to be displayed
	 * @param titleKey The key used to retreive the title from the repository
	 * @param messageKey The key used to retrieve the text to be shown above the
	 * tree
	 * @param helpTopicId The key used to retrieve the help content
	 */
	public StatusDialog(WorkbenchContext context,
						Dialog owner,
						Collection status,
						String titleKey,
						String messageKey,
						String helpTopicId)
	{
		super(context, owner);
		if (status == null) {
			throw new NullPointerException("The collection of status cannot be null");
		}
		this.status = status;
		this.messageKey = messageKey;
		this.helpTopicId = helpTopicId;
		setTitle(resourceRepository().getString(titleKey));
	}
	
	/**
	 * Creates a new <code>IStatus</code> for the given item.
	 *
	 * @param item The item that has a status to be displayed
	 * @param status The list of status
	 * @return A new <code>IStatus</code>
	 */
	public static Status createStatus(Object item, List status)
	{
		return createStatus(item, status, Collections.EMPTY_LIST);
	}

	/**
	 * Creates a new <code>IStatus</code> for the given item.
	 *
	 * @param item The item that has a status to be displayed
	 * @param status The list of status
	 * @param children The children <code>IStatus</code>
	 * @return A new <code>IStatus</code>
	 */
	public static Status createStatus(Object item, List status, List children)
	{
		return new LocalStatus(item, status, children);
	}

	/**
	 * Creates a new <code>IStatus</code> for the given item.
	 *
	 * @param item The item that has a status to be displayed
	 * @param status The list of status where the status are the keys of this map
	 * @return A new <code>IStatus</code>
	 */
	public static Status createStatus(Object item, Map status)
	{
		return createStatus(item, new Vector(status.keySet()));
	}

	/**
	 * Creates a new <code>IStatus</code> for the given item.
	 *
	 * @param item The item that has a status to be displayed
	 * @param status The list of status where the status are the keys of this map
	 * @param children The children <code>IStatus</code>
	 * @return A new <code>IStatus</code>
	 */
	public static Status createStatus(Object item, Map status, List children)
	{
		return createStatus(item, new Vector(status.keySet()), children);
	}

	/**
	 * Creates the listener responsible to revalidate the tree when the component
	 * listened is resided.
	 *
	 * @return A new <code>ComponentListener</code>
	 */
	private ComponentListener buildComponentListener() {
		return new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				StatusDialog.this.updateTree();
			}
		};
	}

	/**
	 * Creates the widgets of this dialog.
	 *
	 * @return The container with its widgets
	 */
	@Override
	protected Component buildMainPanel()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel container = new JPanel(new GridBagLayout());

		// Message label
		LabelArea messageLabel = new LabelArea(resourceRepository().getString(this.messageKey));
		messageLabel.getAccessibleContext().setAccessibleName(messageLabel.getText());
		messageLabel.setScrollable(true);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);

		container.add(messageLabel, constraints);

		// Tree
		this.tree = new StatusTree();
		this.tree.setRowHeight(0);
		this.tree.setShowsRootHandles(false);
		this.tree.setRootVisible(false);
		this.tree.setDoubleBuffered(true);
		this.tree.setCellRenderer(new StatusTreeNodeRenderer());

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(1, 0, 0, 0);

		JScrollPane pane = new JScrollPane(this.tree);
		pane.addComponentListener(buildComponentListener());

		container.add(pane, constraints);
		messageLabel.setLabelFor(this.tree);

		return container;
	}

	/**
	 * Creates a new <code>CellRendererAdapter</code> to render the given value.
	 *
	 * @param item The item to be formated on screen
	 * @return A new <code>CellRendererAdapter</code>
	 */
	protected CellRendererAdapter buildNodeRenderer(Object value)
	{
		if (value instanceof Node)
			return CellRendererAdapter.NODE_CELL_RENDERER_ADAPTER;

		if (value instanceof Displayable)
			return CellRendererAdapter.DISPLAYABLE_CELL_RENDERER_ADAPTER;

		if (value instanceof MWError)
			return new MWErrorCellRendererAdapter();

		return CellRendererAdapter.DEFAULT_CELL_RENDERER_ADAPTER;
	}

	/**
	 * Populates the tree by adding all the <code>ModelStatus</code> to the root
	 * node.
	 */
	private void buildTreeContent()
	{
		MutableTreeNode rootNode = rootNode();

		for (Iterator iter = this.status.iterator(); iter.hasNext(); )
		{
			buildTreeNode(rootNode, (Status) iter.next());
		}
	}

	/**
	 * Create the nodes for the given <code>ModelStatus</code> and add it to
	 * the given parent node.
	 *
	 * @param parentNode The node where the newly created node wrapping the given
	 * status
	 * @param status The status to be added to the tree
	 */
	private void buildTreeNode(MutableTreeNode parentNode, Status nodeStatus)
	{
		// Add the model node
		StatusTreeNode statusNode = buildTreeNode(nodeStatus.getItem(), false);
		parentNode.insert(statusNode, parentNode.getChildCount());

		// Add the status first
		for (Iterator iter = nodeStatus.status(); iter.hasNext();)
		{
			StatusTreeNode node = buildTreeNode(iter.next(), true);
			statusNode.insert(node, statusNode.getChildCount());
		}

		// Add the children after the status
		for (Iterator iter = nodeStatus.children(); iter.hasNext();)
		{
			buildTreeNode(statusNode, (Status) iter.next());
		}
	}

	/**
	 * Creates a new <code>IStatusTreeNode</code> where its object will be the
	 * given value.
	 *
	 * @param item The node's user object
	 * @param requireWrapping Flag used by the tree renderer to see if the text
	 * requires to be wrapped onto multiple lines
	 * @return A new <code>IStatusTreeNode</code>
	 */
	private StatusTreeNode buildTreeNode(Object value, boolean requireWrapping)
	{
		return new StatusTreeNode(value, buildNodeRenderer(value));
	}

	@Override
	protected boolean cancelButtonIsVisible() {
		return false;
	}

	private void expandAll(MutableTreeNode node)
	{
		if (node.isLeaf())
			return;

		expandPath(node);

		for (Enumeration stream = node.children(); stream.hasMoreElements();)
		{
			MutableTreeNode childNode = (MutableTreeNode) stream.nextElement();
			expandAll(childNode);
		}
	}

	/**
	 * Expands
	 *
	 * @param node
	 */
	private void expandPath(MutableTreeNode node)
	{
		DefaultTreeModel model = (DefaultTreeModel) this.tree.getModel();
		TreeNode[] paths = model.getPathToRoot(node);
		this.tree.expandPath(new TreePath(paths));
	}

	/**
	 * Returns the topic id used to retrieve the help topic id in the help.
	 *
	 * @return The id used to retrieve the help content
	 */
	@Override
	protected String helpTopicId()
	{
		return this.helpTopicId;
	}

	@Override
	protected void initialize(WorkbenchContext context) {
		super.initialize(context.buildExpandedResourceRepositoryContext(UiCommonBundle.class));
	}

	/**
	 * The dialog is about to become visible, build the content of tree.
	 */
	@Override
	protected void prepareToShow()
	{
		setSize(455, 230);
		setLocationRelativeTo(getParent());
		buildTreeContent();
		updateTree();
	}

	/**
	 * Reloads the tree model and revalidate the nodes in order to resize the
	 * status nodes.
	 */
	void updateTree()
	{
		((DefaultTreeModel) this.tree.getModel()).reload();
		expandAll(rootNode());

		this.tree.revalidate();
		this.tree.repaint();
	}

	/**
	 * Returns the root node of the tree.
	 *
	 * @return The root node
	 */
	protected final MutableTreeNode rootNode()
	{
		return (MutableTreeNode) ((DefaultTreeModel) this.tree.getModel()).getRoot();
	}

	/**
	 * The status of an item to be displayed. The item can have one or more
	 * status and zero or more children.
	 */
	public interface Status
	{
		/**
		 * This <code>Iterator</code> returns the list of <code>IStatus</code>
		 * contained in this <code>IStatus</code>.
		 *
		 * @return The iterator over the children if any exist
		 */
		public Iterator children();

		/**
		 * The item that has at least a status attached to it.
		 *
		 * @return The item that is shown as a root node
		 */
		public Object getItem();

		/**
		 * This <code>Iterator</code> returns the list of status (description of
		 * errors, warnings or a simple success status.
		 *
		 * @return The iterator over the status
		 */
		public Iterator status();
	}

	/**
	 * The renderer of <code>MWError</code> object.
	 */
	protected class MWErrorCellRendererAdapter extends AbstractCellRendererAdapter
	{
		@Override
		public Icon buildIcon(Object value)
		{
			MWError error = (MWError) value;
			String errorId = error.getErrorId();

			if (errorId.endsWith("SUCCESSFUL"))
				return resourceRepository().getIcon("approve");

			if (errorId.endsWith("ERROR"))
				return resourceRepository().getIcon("error");

			return resourceRepository().getIcon("warning");
		}

		@Override
		public String buildText(Object value)
		{
			MWError error = (MWError) value;
			return resourceRepository().getString(error.getErrorId(), error.getArguments());
		}
	}

	/**
	 * This <code>Status</code> reflects the information to be displayed in this
	 * dialog. Basically, the item is the object that has status attached to it.
	 * It is possible to have children status for this status.
	 */
	private static class LocalStatus implements Status {
		private final List children;
		private final Object item;
		private final List status;

		public LocalStatus(Object item, List status, List children) {
			super();
			this.item = item;
			this.status = status;
			this.children = children;
		}

		public Iterator children() {
			return this.children.iterator();
		}

		public Object getItem() {
			return this.item;
		}

		public Iterator status() {
			return this.status.iterator();
		}

	}

	/**
	 * This subclass only prevents the user from collapsing a node.
	 */
	private class StatusTree extends SwingComponentFactory.AccessibleTree {
		private StatusTree() {
			super(new DefaultTreeModel(new DefaultMutableTreeNode("")));
		}

		@Override
		public void collapsePath(TreePath path) {
			// Prevent the tree to collapse
		}

		@Override
		public void collapseRow(int row) {
			// Prevent the tree to collapse
		}

		@Override
		protected void setExpandedState(TreePath path, boolean state) {
			// Prevent the tree to collapse
			if (state) {
				super.setExpandedState(path, state);
			}
		}
	}

	/**
	 * Default tree node. The rendering of this node is performed by a
	 * <code>CellRendererAdapter</code> that is attached to it.
	 */
	private class StatusTreeNode extends DefaultMutableTreeNode {
		private final CellRendererAdapter renderer;

		private StatusTreeNode(Object value, CellRendererAdapter renderer) {
			super(value);
			this.renderer = renderer;
		}

		public String buildAccessibleName() {
			return this.renderer.buildAccessibleName(getUserObject());
		}

		public Icon buildIcon() {
			return this.renderer.buildIcon(getUserObject());
		}

		public String buildText() {
			return this.renderer.buildText(getUserObject());
		}

	}

	/**
	 * The renderer of tree where it request the information from the
	 * <code>IStatusNode</code>.
	 */
	// TODO convert this to use a LabelArea once LabelArea supports an icon
	private class StatusTreeNodeRenderer extends SimpleTreeCellRenderer {

		@Override
		public Component getTreeCellRendererComponent(JTree t, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean cellHasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			return node.isRoot() ? this : super.getTreeCellRendererComponent(t, value, sel, expanded, leaf, row, cellHasFocus);
		}

		@Override
		protected String buildAccessibleName(Object value) {
			return ((StatusTreeNode) value).buildAccessibleName();
		}

		@Override
		protected Icon buildIcon(Object value) {
			return ((StatusTreeNode) value).buildIcon();
		}

		@Override
		protected String buildText(Object value) {
			return ((StatusTreeNode) value).buildText();
		}

		@Override
		public AccessibleContext getAccessibleContext() {
			if (this.accessibleContext == null) {
				this.accessibleContext = new AccessibleStatusNode();
			}
			return this.accessibleContext;
		}

		protected class AccessibleStatusNode extends AccessibleJLabel {
			@Override
			public String getAccessibleName() {
				return super.getAccessibleName();
			}
		}
	}
}
