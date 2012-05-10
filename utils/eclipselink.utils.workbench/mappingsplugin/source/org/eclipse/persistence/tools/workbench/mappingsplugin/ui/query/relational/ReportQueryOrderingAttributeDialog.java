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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportOrderingItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportQuery;
import org.eclipse.persistence.tools.workbench.uitools.SwitcherPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.Transformer;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;


final class ReportQueryOrderingAttributeDialog extends OrderingAttributeDialog {
	
    private ObjectListSelectionModel attributesListSelectionModel;
    private Component existingAttributePanel;
    private Component newAttributePanel;
    private PropertyValueModel attributeTypeHolder;
    
    ReportQueryOrderingAttributeDialog(MWReportQuery query, MWReportOrderingItem item, Filter traversableFilter, Filter chooseableFilter, WorkbenchContext context) {
		super(query, item, traversableFilter, chooseableFilter, context);
	}
    
	protected String helpTopicId() {
		return "descriptor.queryManager.reportQuery.orderingAttributes";
	}

	
	protected Component buildMainPanel() {
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		this.attributeTypeHolder = new SimplePropertyValueModel();

		ButtonModel selectedButtonModel = new RadioButtonModelAdapter(this.attributeTypeHolder, "Selected Attribute");
		JRadioButton selectedRadioButton = 
		    SwingComponentFactory.buildRadioButton(
		            "ORDERING_ATTRIBUTES_DIALOG_SELECTED_ATTRIBUTE_BUTTON", 
		            selectedButtonModel, 
		            resourceRepository());
		selectedRadioButton.setSelected(true);	
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		panel.add(selectedRadioButton, constraints);	
	
		ButtonModel newButtonModel = new RadioButtonModelAdapter(this.attributeTypeHolder, "New Attribute");
		JRadioButton newRadioButton = 
		    SwingComponentFactory.buildRadioButton(
		            "ORDERING_ATTRIBUTES_DIALOG_NEW_ATTRIBUTE_BUTTON", 
		            newButtonModel, 
		            resourceRepository());
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		panel.add(newRadioButton, constraints);	
	
		this.existingAttributePanel = buildExistingAttributePanel();
		this.newAttributePanel = buildNewAttributePanel();
		
		SwitcherPanel switcherPanel = new SwitcherPanel(this.attributeTypeHolder, buildSwitchPanelTransformer());
		constraints.gridx		= 0;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		panel.add(switcherPanel, constraints);
		
		
		JPanel orderPanel = new JPanel(new GridBagLayout());
		
			JLabel orderLabel = new JLabel(resourceRepository().getString("ORDERING_ATTRIBUTES_DIALOG_ORDER_LABEL"));
			constraints.gridx		= 0;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.LINE_START;
			constraints.insets		= new Insets(10, 0, 0, 0);
			orderPanel.add(orderLabel, constraints);	
	
			JComboBox orderComboBox = buildOrderComboBox();
			constraints.gridx		= 1;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.LINE_START;
			constraints.insets		= new Insets(10, 5, 0, 0);
			orderPanel.add(orderComboBox, constraints);	

		constraints.gridx		= 0;
		constraints.gridy		= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		panel.add(orderPanel, constraints);	
	
		return panel;
	}

	private ListModel buildReportAttributesListModel() {
	    DefaultListModel listModel = new DefaultListModel();
	    for (Iterator i = ((MWReportQuery) getQuery()).attributeItems(); i.hasNext(); ) {
		    listModel.addElement(i.next());	        
	    }
	    
	    return listModel;
	}
	
	private Transformer buildSwitchPanelTransformer() {
	    return new Transformer() {
            public Object transform(Object o) {
                if (o == "Selected Attribute") {
                    return getExistingAttributePanel();
                }
                attributesListSelectionModel.setSelectedValue(null);
                return getNewAttributePanel();
            }
        };
	}
	
	private Component getExistingAttributePanel() {
	    return this.existingAttributePanel;
	}
	
	private Component buildExistingAttributePanel() {
	    JPanel panel = new JPanel(new GridBagLayout());
	    GridBagConstraints constraints = new GridBagConstraints();
		ListModel listModel = buildReportAttributesListModel();
		JList reportAttributesList = new JList(listModel);
		this.attributesListSelectionModel = new ObjectListSelectionModel(listModel);
		reportAttributesList.setSelectionModel(this.attributesListSelectionModel);
		reportAttributesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		reportAttributesList.setCellRenderer(new SimpleListCellRenderer() {
		    protected String buildText(Object value) {
                return ((MWReportAttributeItem) value).getName();
            }
		});
		reportAttributesList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
               updateOKButton();
            }
        });
		final JScrollPane listScrollPane = new JScrollPane(reportAttributesList);
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		panel.add(listScrollPane, constraints);	
		
		return panel;	
	}
	
	private Component getNewAttributePanel() {
	    return this.newAttributePanel;    
	}
	
	private Component buildNewAttributePanel() {
	    JPanel panel = new JPanel(new GridBagLayout());
	    GridBagConstraints constraints = new GridBagConstraints();

		JScrollPane scrollPane = new JScrollPane(buildQueryableTree());
		constraints.gridx		= 0;
		constraints.gridy		= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		panel.add(scrollPane, constraints);	
		
		return panel;	
	}
	
	protected void initializeEditMode(MWAttributeItem attributeItem) {
	    if (((MWReportOrderingItem) attributeItem).getItemName() == null) {
	        super.initializeEditMode(attributeItem);
	        this.attributeTypeHolder.setValue("New Attribute");
	    }
		else {
		    for (int i = 0 ; i < this.attributesListSelectionModel.getListModel().getSize(); i++) {
		        MWReportAttributeItem item = (MWReportAttributeItem)this.attributesListSelectionModel.getListModel().getElementAt(i);
		        if (item.getQueryableArgument() == attributeItem.getQueryableArgument()) {
		            this.attributesListSelectionModel.setSelectedValue(item);
		            break;
		        }
		    }
		    initializeOrderComboBox(attributeItem);
		}
	}
	
	protected void updateOKButton() {
	    if (this.attributesListSelectionModel.getSelectedValue() != null) {
	        getOKAction().setEnabled(true);
	    }
	    else {
	        super.updateOKButton();
	    }
	}
	

    protected boolean preConfirm() {
        MWReportAttributeItem attributeItem = (MWReportAttributeItem) this.attributesListSelectionModel.getSelectedValue();
        if (attributeItem == null) {
            return super.preConfirm();
        }
        
		int index = attributeItemsSize();
		
		if (getAttributeItem() != null) {
			index = indexOfAttributeItem(getAttributeItem());
			removeAttributeItem(index);
		}		
      
        MWReportOrderingItem item =  ((MWReportQuery) getQuery()).addOrderingItem(attributeItem);
        item.setAscending(isAscending());
        return true;
    }
}
