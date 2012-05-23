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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.chooser.NodeSelector;



public class AddRemoveTablePanel extends AddRemovePanel {

	private JTable table;
	
    
    public AddRemoveTablePanel(ApplicationContext context, Adapter adapter, ListValueModel listModel, ColumnAdapter columnModel) {
        this(context, adapter, listModel, columnModel, BOTTOM); 
    }
    
    public AddRemoveTablePanel(ApplicationContext context, Adapter adapter, ListValueModel listModel, ColumnAdapter columnModel, int buttonOrientation) {
        this(context, adapter,listModel, columnModel, buttonOrientation, null, new NodeSelector.DefaultNodeSelector()); 
    }
    
    public AddRemoveTablePanel(ApplicationContext context, Adapter adapter, ListValueModel listModel, ColumnAdapter columnModel, int buttonOrientation, NodeSelector nodeSelector) {
        this(context, adapter,listModel, columnModel, buttonOrientation, null, nodeSelector); 
    }
    
    public AddRemoveTablePanel(ApplicationContext context, Adapter adapter, ListValueModel listModel, ColumnAdapter columnModel, int buttonOrientation, String initialAccessName, NodeSelector nodeSelector) {
        super(context, adapter, listModel, buttonOrientation, nodeSelector);
        initialize(listModel, columnModel); 
        initializeLayout(initialAccessName);
    }
    
    public AddRemoveTablePanel(ApplicationContext context, Adapter adapter, ListValueModel listModel, ColumnAdapter columnModel, String initialAccessName) {
        this(context, adapter, listModel, columnModel, BOTTOM, initialAccessName, new NodeSelector.DefaultNodeSelector());  
    }
    
    
	protected void initialize(ListValueModel listModel, ColumnAdapter columnModel) {	
		this.table = SwingComponentFactory.buildTable(new TableModelAdapter(listModel, columnModel), getSelectionModel());
		this.table.getTableHeader().setReorderingAllowed(false);
        this.table.addKeyListener(buildF3KeyListener());
	}
	
	
	/**
	 * Initializes the layout.
	 */
	protected void initializeLayout(String accessibleName) {
		initializeButtonPanel();
		initializeTablePanel(accessibleName);
	}

	protected void initializeTablePanel(String accessibleName) {	
		
		GridBagConstraints constraints = new GridBagConstraints();

		if (getButtonOrientation() == TOP) {
			constraints.gridx = 0;
			constraints.gridy = 1;
		}
		else if (getButtonOrientation() == LEFT) {
			constraints.gridx = 1;
			constraints.gridy = 0;
		} 
		else {
			constraints.gridx = 0;
			constraints.gridy = 0;
		}

		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);

		this.table.getAccessibleContext().setAccessibleName(accessibleName);

		JScrollPane scrollPane = new JScrollPane(this.table);
		scrollPane.setMinimumSize(new Dimension(1, 1));
		scrollPane.setPreferredSize(new Dimension(1, 1));
		scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.getViewport().setBackground(this.table.getBackground());
		add(scrollPane, constraints);

	}

	public JComponent getComponent() {
		return this.table;
	}

	protected void moveItemsDown() {
		if (this.table.isEditing()) {
			this.table.getCellEditor().stopCellEditing();
		}
		super.moveItemsDown();
	}

	protected void moveItemsUp() {
		if (this.table.isEditing()) {
			this.table.getCellEditor().stopCellEditing();
		}
		super.moveItemsUp();
	}

	protected void addNewItem() {
		if (this.table.isEditing()) {
			this.table.getCellEditor().stopCellEditing();
		}
		super.addNewItem();
	}

	protected void removeSelectedItems() {
		if (this.table.isEditing()) {
			this.table.getCellEditor().stopCellEditing();
		}
		super.removeSelectedItems();
	}
}
