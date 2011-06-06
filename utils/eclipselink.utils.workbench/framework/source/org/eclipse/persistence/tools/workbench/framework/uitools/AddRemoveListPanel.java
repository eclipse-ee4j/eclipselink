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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.DisplayableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.NodeSelector;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public class AddRemoveListPanel extends AddRemovePanel {
	
	private JList list;
	
		
	// *********************** Constructors ****************************
	
	public AddRemoveListPanel(ApplicationContext context, Adapter adapter, ListValueModel listModel) {
		this(context, adapter, listModel, BOTTOM);	
	}
	
	public AddRemoveListPanel(ApplicationContext context, Adapter adapter, ListValueModel listModel, int buttonOrientation) {
		this(context, adapter, listModel, buttonOrientation, null);	
	}
	
	public AddRemoveListPanel(ApplicationContext context, Adapter adapter, ListValueModel listModel, int buttonOrientation, String initialAccessName) {
        this(context, adapter, listModel, buttonOrientation, initialAccessName, new NodeSelector.DefaultNodeSelector()); 
	}
    
    public AddRemoveListPanel(ApplicationContext context, Adapter adapter, ListValueModel listModel, int buttonOrientation, String initialAccessName, NodeSelector nodeSelector) {
        super(context, adapter, listModel, buttonOrientation, nodeSelector);
        initializeLayout(initialAccessName);
    }
    
	public AddRemoveListPanel(ApplicationContext context, Adapter adapter, ListValueModel listModel, String initialAccessName) {
		this(context, adapter,listModel, BOTTOM, initialAccessName);	
	}

	protected void initialize(ListValueModel listModel) {
		super.initialize(listModel);
		this.list = SwingComponentFactory.buildList(new ListModelAdapter(listModel));
		this.list.setSelectionModel(getSelectionModel());
		this.list.setCellRenderer(new DisplayableListCellRenderer());
        this.list.addKeyListener(buildF3KeyListener());
        helpManager().addItemsToPopupMenuForComponent(new JMenuItem[] {buildGoToMenuItem()}, this.list);
	}

	/**
	 * Initializes the layout.
	 */
	protected void initializeLayout(String accessibleName) {
		initializeListPanel(accessibleName);
		initializeButtonPanel();
	}

	protected void initializeListPanel(String accessibleName) {	
		
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

		this.list.getAccessibleContext().setAccessibleName(accessibleName);

		JScrollPane scrollPane = new JScrollPane(this.list);
		scrollPane.setMinimumSize(new Dimension(1, 1));
		scrollPane.setPreferredSize(new Dimension(1, 1));
		scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		add(scrollPane, constraints);

	}

	public JComponent getComponent() {
		return this.list;
	}

	public JList getList() {
		return this.list;
	}

	public Object[] getSelectedValues() {
		return this.list.getSelectedValues();
	}
		
	public Iterator selectedValues() {
		return CollectionTools.iterator(this.list.getSelectedValues());
	}
	
	public void setCellRenderer(ListCellRenderer cellRenderer) {
		this.list.setCellRenderer(cellRenderer);
	}

	public void setSelectedIndex(int index) {
		this.list.setSelectedIndex(index);
	}

	public void setSelectedIndices(int[] indices) {
		this.list.setSelectedIndices(indices);
	}

	public void setSelectedValue(Object anObject, boolean shouldScroll) {
		this.list.setSelectedValue(anObject, shouldScroll);
	}

	public void setSelectionInterval(int anchor, int lead) {
		this.list.setSelectionInterval(anchor, lead);
	}

}
