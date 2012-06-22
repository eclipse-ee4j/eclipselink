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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicArrowButton;
import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.DoubleClickMouseListener;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.generation.MWRelationshipHolder;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;


/**
 **	If you want the lists to be sorted, then pass in sorted collections.
 */
final class RelationshipGenerationDialog extends AbstractDialog 
	implements ActionListener, ListSelectionListener 
{

	private TreeSet availableListData;
	private TreeSet selectedListData;
	private JList availableList;
	private JList selectedList;
	private JButton oneToManyButton;
	private JButton oneToOneButton;
	
	private boolean generateBidirectionalRelationships;
	private JCheckBox generateBidirectionalRelationshipsCheckBox;
	
	protected final static String ID_ONE_TO_ONE = "One-To-One";
	protected final static String ID_CREATE = "Create";
	protected final static String ID_SKIP = "Skip";
	protected final static String ID_ONE_TO_MANY = "One-To-Many";
	protected final static String ID_DESELECT_ITEMS = "Deselect Items";
	

	public RelationshipGenerationDialog(Vector relationships, WorkbenchContext workbenchContext) {
		super(workbenchContext);
		setAvailableListData(relationships);
		setSelectedListData(new Vector());
	}
	public boolean getGenerateBidirectionalRelationships() {
		return this.generateBidirectionalRelationshipsCheckBox.isSelected();
	}
	
	private ListCellRenderer getListCellRenderer() {
		return new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				MWRelationshipHolder holder = (MWRelationshipHolder) value;
				label.setText(holder.displayString());
				if (holder.isOneToOne())
					label.setIcon(resourceRepository().getIcon("mapping.oneToOne"));
				else if (holder.isOneToMany())
					label.setIcon(resourceRepository().getIcon("mapping.oneToMany"));
				return label;
			}
		};
	}
	public Collection getRelationshipsToCreate() {
		return new Vector(selectedListData);
	}
	
	protected String helpTopicId() {
		return "dialog.relationshipGenerator";
	}

	protected Component initialFocusComponent() {
		return availableList;
	}

	protected void initialize(WorkbenchContext context) {
		super.initialize(context);

		setTitle(resourceRepository().getString("chooseRelationshipsToGenerate.title"));
	}

	protected void prepareToShow() {
		this.setSize(800, 400);
		this.setLocationRelativeTo(this.getParent());
	}

	protected Component buildMainPanel() {
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel mainPanel = new JPanel(new GridBagLayout());
		
		// Create text message
		LabelArea messageLabel = new LabelArea(resourceRepository().getString("basedOnTheForeignKeysOfThe"));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 5, 0, 5);
		mainPanel.add(messageLabel, constraints);
		
		// Create separator
		JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(10, 5, 5, 5);
		mainPanel.add(separator, constraints);
		
		// Create available items label, list, and scrollpane
		JLabel availableLabel = SwingComponentFactory.buildLabel("potentialRelationships", resourceRepository());
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(5, 5, 0, 0);
		mainPanel.add(availableLabel, constraints);
		
		availableList = new JList();
		availableList.setCellRenderer(getListCellRenderer());
		availableList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		availableList.addListSelectionListener(this);
		availableList.setListData(availableListData.toArray());

		availableLabel.setLabelFor(availableList);
		JScrollPane availableScrollpane = new JScrollPane(availableList);
		availableScrollpane.getViewport().setPreferredSize(new Dimension(0, 0));
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.gridheight = 3;
		constraints.weightx = 0.5;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(1, 5, 0, 0);
		mainPanel.add(availableScrollpane, constraints);
		
		// Create selected items label, list, and scrollpane
		JLabel selectedLabel = SwingComponentFactory.buildLabel("selectedRelationships", resourceRepository());
		constraints.gridx = 2;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(5, 5, 0, 5);
		mainPanel.add(selectedLabel, constraints);
		
		selectedList = SwingComponentFactory.buildList();
		selectedList.setCellRenderer(getListCellRenderer());
		selectedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		selectedList.setListData(selectedListData.toArray());

		// setup double clicking in the selected list to remove items
		SwingComponentFactory.addDoubleClickMouseListener(selectedList, new DoubleClickMouseListener() {
			public void mouseDoubleClicked(MouseEvent e) {
				deselectItems();
			}
		});

		selectedLabel.setLabelFor(selectedList);
		JScrollPane selectedScrollpane = new JScrollPane(selectedList);
		selectedScrollpane.getViewport().setPreferredSize(new Dimension(0, 0));
		constraints.gridx = 2;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.gridheight = 3;
		constraints.weightx = 0.5;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(1, 5, 0, 5);
		mainPanel.add(selectedScrollpane, constraints);
		// Create One to One button
		oneToOneButton = new JButton();
		oneToOneButton.setIcon(resourceRepository().getIcon("mapping.oneToOne"));
		oneToOneButton.setToolTipText(resourceRepository().getString("mapAsOneToOne"));
		oneToOneButton.setActionCommand(ID_ONE_TO_ONE);
		oneToOneButton.addActionListener(this);
		constraints.gridx = 1;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(1, 5, 0, 0);
		mainPanel.add(oneToOneButton, constraints);
		
		// Create One to Many button
		oneToManyButton = new JButton();
		oneToManyButton.setIcon(resourceRepository().getIcon("mapping.oneToMany"));
		oneToManyButton.setToolTipText(resourceRepository().getString("mapAsOneToMany"));
		oneToManyButton.setActionCommand(ID_ONE_TO_MANY);
		oneToManyButton.addActionListener(this);
		constraints.gridx = 1;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 5, 0, 0);
		mainPanel.add(oneToManyButton, constraints);
		
		// Left arrow button
		JButton leftArrowButton = new JButton();
		leftArrowButton.addActionListener(buildAction());
		leftArrowButton.setIcon(resourceRepository().getIcon("shuttle.left"));
		leftArrowButton.setEnabled(false);
		leftArrowButton.setToolTipText(resourceRepository().getString("removeSelectedMappings"));
		selectedList.addListSelectionListener(buildSelectionListListener(leftArrowButton));
		leftArrowButton.setActionCommand(ID_DESELECT_ITEMS);
		
		constraints.gridx = 1;
		constraints.gridy = 6;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.insets = new Insets(5, 5, 0, 0);
		mainPanel.add(leftArrowButton, constraints);
		
		// Generate Bidirectional Relationships Checkbox
		generateBidirectionalRelationshipsCheckBox = new JCheckBox(resourceRepository().getString("generateBidirectionalRelationships"));
		generateBidirectionalRelationshipsCheckBox.setMnemonic(resourceRepository().getMnemonic("generateBidirectionalRelationships"));
		generateBidirectionalRelationshipsCheckBox.setSelected(generateBidirectionalRelationships);
		constraints.gridx = 0;
		constraints.gridy = 7;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.insets = new Insets(5, 5, 0, 0);
		mainPanel.add(generateBidirectionalRelationshipsCheckBox, constraints);
		
		return mainPanel;
	}

	private ListSelectionListener buildSelectionListListener(final JButton leftArrowButton) {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				if (selectedList.getSelectedValues().length > 0) {
					leftArrowButton.setEnabled(true);
				} else {
					leftArrowButton.setEnabled(false);
				}
			}
		};
	}

	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand == ID_ONE_TO_ONE)
			selectItems(ID_ONE_TO_ONE);
		else if (actionCommand == ID_ONE_TO_MANY)
			selectItems(ID_ONE_TO_MANY);
		else if (actionCommand == ID_DESELECT_ITEMS)
			deselectItems();
	}

	private AbstractFrameworkAction buildAction() {
		return new AbstractFrameworkAction(getWorkbenchContext()) {
			public void actionPerformed(ActionEvent e) {
				RelationshipGenerationDialog.this.actionPerformed(e);
			}

		};
	}
	
	private void deselectItems() {
		Collection selectedItems = selectedListData;
		Collection availableItems = availableListData;
		Object[] selectedSelectedItems = selectedList.getSelectedValues();
		for (int i = 0; i < selectedSelectedItems.length; i++) {
			((MWRelationshipHolder) selectedSelectedItems[i]).setNoRelationship();
			selectedItems.remove(selectedSelectedItems[i]);
			availableItems.add(selectedSelectedItems[i]);
		}
		availableList.setListData(availableItems.toArray());
		selectedList.setListData(selectedItems.toArray());
		setAvailableListData(availableItems);
		setSelectedListData(selectedItems);
	}
	
	public void selectItems(String typeOfMapping) {
		Collection selectedItems = selectedListData;
		Collection availableItems = availableListData;
		Object[] selectedAvailableItems = availableList.getSelectedValues();
		for (int i = 0; i < selectedAvailableItems.length; i++) {
			if (!selectedItems.contains(selectedAvailableItems[i])) {
				MWRelationshipHolder mappingHolder = (MWRelationshipHolder) selectedAvailableItems[i];
				if (typeOfMapping.equals(ID_ONE_TO_ONE))
					mappingHolder.setOneToOne();
				else
					mappingHolder.setOneToMany();
				selectedItems.add(selectedAvailableItems[i]);
				availableItems.remove(selectedAvailableItems[i]);
			}
		}
		availableList.setListData(availableItems.toArray());
		selectedList.setListData(selectedItems.toArray());
		setAvailableListData(availableItems);
		setSelectedListData(selectedItems);
	}
	
	private void setAvailableListData(Collection data) {
		availableListData = new TreeSet(data);
	}
	
	public void setGenerateBidirectionalRelationships(boolean newValue) {
		this.generateBidirectionalRelationships = newValue;
	}
	
	private void setSelectedListData(Collection data) {
		selectedListData = new TreeSet(data);
	}
	
	public void valueChanged(ListSelectionEvent lse) {
		if (lse.getValueIsAdjusting())
			return;

		Object[] selectedAvailableItems = availableList.getSelectedValues();
		int oneToOneCount = 0;
		int oneToManyCount = 0;
		for (int i = 0; i < selectedAvailableItems.length; i++) {
			MWRelationshipHolder holder = (MWRelationshipHolder) selectedAvailableItems[i];
			if (holder.canMapOneToOne())
				oneToOneCount++;
			if (holder.canMapOneToMany())
				oneToManyCount++;
		}
		oneToOneButton.setEnabled(oneToOneCount > 0);
		oneToManyButton.setEnabled(oneToManyCount > 0 && oneToManyCount == selectedAvailableItems.length);
	}
}
