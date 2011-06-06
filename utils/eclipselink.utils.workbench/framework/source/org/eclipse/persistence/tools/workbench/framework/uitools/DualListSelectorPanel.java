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
package org.eclipse.persistence.tools.workbench.framework.uitools;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicArrowButton;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.DisplayableListCellRenderer;


/**
 * This panel can be used to manage a pair of lists,
 * one which contains the "available" items and one which
 * contains the "selected" items. The panel displays the
 * lists and provides the actions to move the items back
 * and forth. Client must provide an adapter that performs
 * the actually moving of the items, typically between
 * models.
 */
public final class DualListSelectorPanel
	extends AbstractPanel
{
	ListValueModel availableLVM;
	private ListModel availableLM;
	private ObjectListSelectionModel availableLSM;

	ListValueModel selectedLVM;
	private ListModel selectedLM;
	private ObjectListSelectionModel selectedLSM;

	private Adapter adapter;
	private ListCellRenderer listCellRenderer;

	private String availableListBoxLabelKey;
	private String selectedListBoxLabelKey;

	private String selectButtonToolTipKey;
	private String deselectButtonToolTipKey;

	private Action selectAction;
	private Action deselectAction;


	// ********** constructor/initialization **********

	DualListSelectorPanel(Builder builder) {
		super(builder.getContext());

		this.availableLVM = builder.getAvailableLVM();
		if (this.availableLVM == null) {
			throw new NullPointerException();
		}
		this.availableLM = new ListModelAdapter(this.availableLVM);
		this.availableLSM = new ObjectListSelectionModel(this.availableLM);

		this.selectedLVM = builder.getSelectedLVM();
		if (this.selectedLVM == null) {
			throw new NullPointerException();
		}
		this.selectedLM = new ListModelAdapter(this.selectedLVM);
		this.selectedLSM = new ObjectListSelectionModel(this.selectedLM);

		this.adapter = builder.getAdapter();
		if (this.adapter == null) {
			this.adapter = new DefaultAdapter();
		}

		this.listCellRenderer = builder.getListCellRenderer();
		if (this.listCellRenderer == null) {
			this.listCellRenderer = new DisplayableListCellRenderer();
		}

		this.availableListBoxLabelKey = builder.getAvailableListBoxLabelKey();
		if (this.availableListBoxLabelKey == null) {
			this.availableListBoxLabelKey = "AVAILABLE_ITEMS_LIST_BOX_LABEL";
		}
		this.selectedListBoxLabelKey = builder.getSelectedListBoxLabelKey();
		if (this.selectedListBoxLabelKey == null) {
			this.selectedListBoxLabelKey = "SELECTED_ITEMS_LIST_LABEL";
		}

		this.selectButtonToolTipKey = builder.getSelectButtonToolTipKey();
		if (this.selectButtonToolTipKey == null) {
			this.selectButtonToolTipKey = "ADD_SELECTED_ITEMS_BUTTON.toolTipText";
		}
		this.deselectButtonToolTipKey = builder.getDeselectButtonToolTipKey();
		if (this.deselectButtonToolTipKey == null) {
			this.deselectButtonToolTipKey = "REMOVE_SELECTED_ITEMS_BUTTON.toolTipText";
		}

		this.addHelpTopicId(this, builder.getHelpTopicID());

		this.selectAction = this.buildSelectAction();
		this.deselectAction = this.buildDeselectAction();

		this.initializeLayout();
	}

	private Action buildSelectAction() {
		Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				DualListSelectorPanel.this.selectItems();
			}
		};
		action.setEnabled(false);
		return action;
	}

	private Action buildDeselectAction() {
		Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				DualListSelectorPanel.this.deselectItems();
			}
		};
		action.setEnabled(false);
		return action;
	}

	private void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();

		// available items
		JLabel availableLabel = new JLabel(this.resourceRepository().getString(this.availableListBoxLabelKey));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		this.add(availableLabel, constraints);

		Component availableListBox = this.buildListBox(this.availableLM, this.availableLSM, this.selectAction);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 2;
		constraints.weightx = 0.5;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(1, 0, 0, 0);
		this.add(availableListBox, constraints);
		
		availableLabel.setLabelFor(availableListBox);

		// selected items
		JLabel selectedLabel = new JLabel(this.resourceRepository().getString(this.selectedListBoxLabelKey));
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 5, 0, 0);
		this.add(selectedLabel, constraints);

		Component selectedListBox = this.buildListBox(this.selectedLM, this.selectedLSM, this.deselectAction);
		constraints.gridx = 2;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 2;
		constraints.weightx = 0.5;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(1, 5, 0, 0);
		this.add(selectedListBox, constraints);

		selectedLabel.setLabelFor(selectedListBox);

		// right arrow button (select)
		JButton rightArrowButton = new JButton();
		rightArrowButton.setAction(this.selectAction);
		rightArrowButton.setIcon(resourceRepository().getIcon("shuttle.right"));
		rightArrowButton.setToolTipText(this.resourceRepository().getString(this.selectButtonToolTipKey));
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(1, 5, 0, 0);
		this.add(rightArrowButton, constraints);

		// left arrow button (deselect)
		JButton leftArrowButton = new JButton();
		leftArrowButton.setAction(this.deselectAction);
		leftArrowButton.setIcon(resourceRepository().getIcon("shuttle.left"));
		leftArrowButton.setToolTipText(this.resourceRepository().getString(this.deselectButtonToolTipKey));
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 0);
		this.add(leftArrowButton, constraints);
	}

	/**
	 * build a list box that displays the specified list and performs
	 * the specified action whenever it is double-clicked
	 */
	private Component buildListBox(ListModel lm, ListSelectionModel lsm, Action action) {
		JList listBox = SwingComponentFactory.buildList(lm);
		listBox.setSelectionModel(lsm);
		listBox.setCellRenderer(this.listCellRenderer);
		listBox.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		listBox.addListSelectionListener(new LocalListSelectionListener(action));
		listBox.addMouseListener(new DoubleClickActionMouseAdapter(action));

		JScrollPane scrollPane = new JScrollPane(listBox);
		scrollPane.setPreferredSize(new Dimension(200, 100));
		scrollPane.setMinimumSize(new Dimension(200, 100));
		return scrollPane;
	}


	// ********** behavior **********

	/**
	 * delegate to the adapter to "select" the items,
	 * then select them once they are in the "selected" list
	 */
	void selectItems() {
		Object[] items = this.availableLSM.getSelectedValues();
		for (int i = 0; i < items.length; i++) {
			this.adapter.select(items[i]);
		}
		this.selectedLSM.setSelectedValues(items);
	}

	/**
	 * delegate to the adapter to "deselect" the items,
	 * then select them once they are in the "available" list
	 */
	void deselectItems() {
		Object[] items = this.selectedLSM.getSelectedValues();
		for (int i = 0; i < items.length; i++) {
			this.adapter.deselect(items[i]);
		}
		this.availableLSM.setSelectedValues(items);
	}


	// ********** nested classes **********

	/**
	 * an adapter is used by the dual list selector panel
	 * to move items back and forth between the "available"
	 * and "selected" lists
	 */
	public interface Adapter {

		/**
		 * the specified item is to be moved from the "available"
		 * list to the "selected" list
		 */
		void select(Object item);

		/**
		 * the specified item is to be moved from the "selected"
		 * list to the "available" list
		 */
		void deselect(Object item);

	}


	/**
	 * The default adapter assumes that the supplied list value
	 * models can be modified (which usually is NOT the case).
	 */
	private class DefaultAdapter implements Adapter {
		public void select(Object item) {
			this.remove(DualListSelectorPanel.this.availableLVM, item);
			this.add(DualListSelectorPanel.this.selectedLVM, item);
		}
		public void deselect(Object item) {
			this.remove(DualListSelectorPanel.this.selectedLVM, item);
			this.add(DualListSelectorPanel.this.availableLVM, item);
		}
		private void remove(ListValueModel lvm, Object item) {
			int size = lvm.size();
			for (int i = 0; i < size; i++) {
				if (lvm.getItem(i) == item) {
					lvm.removeItem(i);
					return;
				}
			}
		}
		private void add(ListValueModel lvm, Object item) {
			lvm.addItem(lvm.size(), item);
		}
	}


	/**
	 * Use this builder to configure and construct a dual
	 * list selector panel. See the comments on the setters.
	 */
	public static class Builder {
		private ListValueModel availableLVM;
		private ListValueModel selectedLVM;
		private Adapter adapter;
		private ListCellRenderer listCellRenderer;
		private ApplicationContext context;
		private String availableListBoxLabelKey;
		private String selectedListBoxLabelKey;
		private String selectButtonToolTipKey;
		private String deselectButtonToolTipKey;
		private String helpTopicID;

		/**
		 * build a dual list selector panel with the current
		 * builder configuration
		 */
		public DualListSelectorPanel buildPanel() {
			return new DualListSelectorPanel(this);
		}

		/**
		 * set the items that will be displayed in the
		 * "available" list (the left hand list); REQUIRED
		 */
		public void setAvailableLVM(ListValueModel availableLVM) {
			this.availableLVM = availableLVM;
		}
		public ListValueModel getAvailableLVM() {
			return this.availableLVM;
		}

		/**
		 * set the items that will be displayed in the
		 * "selected" list (the right hand list); REQUIRED
		 */
		public void setSelectedLVM(ListValueModel selectedLVM) {
			this.selectedLVM = selectedLVM;
		}
		public ListValueModel getSelectedLVM() {
			return this.selectedLVM;
		}

		/**
		 * set the adapter that will be invoked when items
		 * are "selected" or "deselected"; the default adapter
		 * assumes the list value models can be modified directly,
		 * which is usually NOT the case
		 */
		public void setAdapter(Adapter adapter) {
			this.adapter = adapter;
		}
		public Adapter getAdapter() {
			return this.adapter;
		}

		/**
		 * set the renderer used in both lists; the default renderer
		 * assumes the items in the list implement Displayable
		 */
		public void setListCellRenderer(ListCellRenderer listCellRenderer) {
			this.listCellRenderer = listCellRenderer;
		}
		public ListCellRenderer getListCellRenderer() {
			return this.listCellRenderer;
		}

		/**
		 * set the application context that will supply the
		 * various resources; REQUIRED
		 */
		public void setContext(ApplicationContext context) {
			this.context = context;
		}
		public ApplicationContext getContext() {
			return this.context;
		}

		/**
		 * set the key used to look up a label for the
		 * "available" list box; optional
		 */
		public void setAvailableListBoxLabelKey(String availableListBoxLabelKey) {
			this.availableListBoxLabelKey = availableListBoxLabelKey;
		}
		public String getAvailableListBoxLabelKey() {
			return this.availableListBoxLabelKey;
		}

		/**
		 * set the key used to look up a label for the
		 * "selected" list box; optional
		 */
		public void setSelectedListBoxLabelKey(String selectedListBoxLabelKey) {
			this.selectedListBoxLabelKey = selectedListBoxLabelKey;
		}
		public String getSelectedListBoxLabelKey() {
			return this.selectedListBoxLabelKey;
		}

		/**
		 * set the key used to look up a "tool tip" for the
		 * "select" button; optional
		 */
		public void setSelectButtonToolTipKey(String selectButtonToolTipKey) {
			this.selectButtonToolTipKey = selectButtonToolTipKey;
		}
		public String getSelectButtonToolTipKey() {
			return this.selectButtonToolTipKey;
		}

		/**
		 * set the key used to look up a "tool tip" for the
		 * "deselect" button; optional
		 */
		public void setDeselectButtonToolTipKey(String deselectButtonToolTipKey) {
			this.deselectButtonToolTipKey = deselectButtonToolTipKey;
		}
		public String getDeselectButtonToolTipKey() {
			return this.deselectButtonToolTipKey;
		}

		/**
		 * set the Help topic ID used for the entire panel; REQUIRED
		 */
		public void setHelpTopicID(String helpTopicID) {
			this.helpTopicID = helpTopicID;
		}
		public String getHelpTopicID() {
			return this.helpTopicID;
		}

	}


	/**
	 * A MouseListener that performs an action whenever a double-click
	 * occurs. The action must ignore the ActionEvent, because we are
	 * going to pass in a null.
	 */
	private class DoubleClickActionMouseAdapter extends MouseAdapter {
		private Action action;

		DoubleClickActionMouseAdapter(Action action) {
			super();
			this.action = action;
		}

		public void mouseClicked(MouseEvent event) {
			if (event.getClickCount() == 2) {
				this.action.actionPerformed(null);
			}
		}
	}


	/**
	 * A ListSelectionListener that enables an action only when
	 * items in the list box are selected (i.e. the action is disabled
	 * whenever none of the items in the list box are selected).
	 */
	private class LocalListSelectionListener implements ListSelectionListener {
		private Action action;

		LocalListSelectionListener(Action action) {
			super();
			this.action = action;
		}

		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			this.action.setEnabled(((JList) e.getSource()).getSelectedIndex() != -1);
		}
	}

}
