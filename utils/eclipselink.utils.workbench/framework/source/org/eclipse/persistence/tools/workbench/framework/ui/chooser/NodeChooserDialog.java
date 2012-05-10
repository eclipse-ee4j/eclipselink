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
package org.eclipse.persistence.tools.workbench.framework.ui.chooser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.DisplayableStringConverter;
import org.eclipse.persistence.tools.workbench.uitools.FilteringListPanel;
import org.eclipse.persistence.tools.workbench.uitools.cell.DisplayableListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * This dialog presents a list of nodes to the user that can be filtered
 * down by typing in the text field. When the user selects one of the nodes,
 * we display the node's path in the lower list box.
 * 
 * Once the user presses the OK button, clients of this dialog 
 * can retrieve the selected node by calling #selection().
 */
public class NodeChooserDialog
	extends AbstractDialog
{
	/** The complete list of nodes. */
	private ApplicationNode[] nodes;

	/** Hold this so we can give it the initial focus. */
	private FilteringListPanel filteringPanel;

	/** Hold this so we can manipulate it some. */
	private JList pathListBox;


	private static final Object[] EMPTY_LIST = new Object[0];


	// ********** static methods **********

	/** Factory method. */
	public static NodeChooserDialog createDialog(ApplicationNode[] nodes, WorkbenchContext context) {
		Window window = context.getCurrentWindow();
		if (window instanceof Dialog) {
			return new NodeChooserDialog(nodes, context, (Dialog) window);
		}
		return new NodeChooserDialog(nodes, context);
	}

	private static String title(ApplicationContext context) {
		return context.getResourceRepository().getString("NODE_CHOOSER_DIALOG.TITLE");
	}


	// ********** constructors **********

	private NodeChooserDialog(ApplicationNode[] nodes, WorkbenchContext context) {
		super(context, title(context.getApplicationContext()));
		this.initialize(nodes);
	}

	private NodeChooserDialog(ApplicationNode[] nodes, WorkbenchContext context, Dialog owner) {
		super(context, title(context.getApplicationContext()), owner);
		this.initialize(nodes);
	}


	// ********** initialization **********

	private void initialize(ApplicationNode[] allNodes) {
		this.nodes = allNodes;
		Arrays.sort(this.nodes);
	}


	// ********** main panel **********

	protected Component buildMainPanel() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		GridBagConstraints constraints = new GridBagConstraints();

		// filtering panel
		this.filteringPanel = new FilteringListPanel(this.nodes, null, DisplayableStringConverter.instance());
		this.configureLabel(this.filteringPanel.getTextFieldLabel(), "NODE_CHOOSER_DIALOG.TEXT_FIELD_LABEL");
		this.configureLabel(this.filteringPanel.getListBoxLabel(), "NODE_CHOOSER_DIALOG.NODE_LIST_BOX_LABEL");
		this.filteringPanel.setListBoxCellRenderer(new DisplayableListCellRenderer());
		this.filteringPanel.getListBox().getSelectionModel().addListSelectionListener(this.buildNodeListSelectionListener());
		this.filteringPanel.getListBox().addMouseListener(this.buildDoubleClickMouseListener());

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 3;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(0, 0, 0, 0);
	
		mainPanel.add(this.filteringPanel, constraints);

		// path list box
		JPanel pathListPanel = new JPanel(new BorderLayout());
		JLabel pathListLabel = new JLabel();
		this.configureLabel(pathListLabel, "NODE_CHOOSER_DIALOG.PATH_LIST_BOX_LABEL");
		pathListLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		pathListPanel.add(pathListLabel, BorderLayout.PAGE_START);

		this.pathListBox = SwingComponentFactory.buildList();
		this.pathListBox.setDoubleBuffered(true);
		this.pathListBox.setCellRenderer(new DisplayableListCellRenderer());
		this.pathListBox.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pathListLabel.setLabelFor(this.pathListBox);
		pathListPanel.add(new JScrollPane(this.pathListBox), BorderLayout.CENTER);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.anchor = GridBagConstraints.PAGE_END;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(0, 0, 0, 0);
	
		mainPanel.add(pathListPanel, constraints);

		return mainPanel;
	}

	/**
	 * Build a listener that makes a double-click equivalent to
	 * clicking the OK button.
	 */
	private MouseListener buildDoubleClickMouseListener() {
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					NodeChooserDialog.this.clickOK();
				}
			}
		};
	}

	/**
	 * Configure the specified label's text and mnemonic.
	 */
	private void configureLabel(JLabel label, String key) {
		label.setText(this.resourceRepository().getString(key));
		label.setDisplayedMnemonic(this.resourceRepository().getMnemonic(key));
	}

	private ListSelectionListener buildNodeListSelectionListener() {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if ( ! e.getValueIsAdjusting()) {
					NodeChooserDialog.this.nodeSelectionChanged(e);
				}
			}
		};
	}


	// ********** AbstractDialog implementation/overrides **********

	/**
	 * nothing is selected initially - so disable the OK button
	 */
	protected Action buildOKAction() {
		Action action = super.buildOKAction();
		action.setEnabled(false);
		return action;
	}

	protected String helpTopicId() {
		return "dialog.nodeChooser";
	}

	protected Component initialFocusComponent() {
		return this.filteringPanel.getTextField();
	}

	protected void prepareToShow() {
		this.setSize(350, 566);	// use the golden ratio
		this.setLocationRelativeTo(this.getParent());
	}

	/**
	 * increase visibility slightly for inner class
	 */
	protected void clickOK() {
		super.clickOK();
	}


	// ********** behavior **********

	void nodeSelectionChanged(ListSelectionEvent e) {
		ApplicationNode selectedNode = (ApplicationNode) this.filteringPanel.getSelection();
		if (selectedNode == null) {
			this.pathListBox.setListData(EMPTY_LIST);
		} else {
			this.pathListBox.setListData(this.reversePathFor(selectedNode));
		}

		// enable the OK button only when the user has selected a node
		this.getOKAction().setEnabled(selectedNode != null);
	}

	private Object[] reversePathFor(ApplicationNode node) {
		Object[] path = node.path();
		Object[] shortPath = new Object[path.length - 1];
		System.arraycopy(path, 0, shortPath, 0, shortPath.length);
		return CollectionTools.reverse(shortPath);
	}


	// ********** public API **********

	/**
	 * Return the type selected by the user.
	 */
	public ApplicationNode selection() {
		if ( ! this.wasConfirmed()) {
			throw new IllegalStateException();
		}
		return (ApplicationNode) this.filteringPanel.getSelection();
	}

}
