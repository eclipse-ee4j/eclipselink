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
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWOrderableQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.Ordering;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;


class OrderingAttributeDialog extends AttributeItemDialog {

	private JComboBox orderComboBox;
	
	OrderingAttributeDialog(MWOrderableQuery query, Ordering item, Filter traversableFilter, Filter chooseableFilter, WorkbenchContext context) {
		super(query, (MWAttributeItem) item, traversableFilter, chooseableFilter, context);
	}

    protected String titleKey() {
        return "ORDERING_ATTRIBUTES_DIALOG_TITLE";
    }   
    
    protected String editTitleKey() {
        return "ORDERING_ATTRIBUTES_EDIT_DIALOG_TITLE";
    }
	
	protected String helpTopicId() {
		return "descriptor.queryManager.query.orderingAttributes";
	}
	
	protected Component buildMainPanel() {
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JScrollPane scrollPane = new JScrollPane(buildQueryableTree());	
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);
		panel.add(scrollPane, constraints);	
		
		JComboBox orderComboBox = buildOrderComboBox();
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 0, 0, 0);
		panel.add(orderComboBox, constraints);	

		return panel;
	}

	protected void initializeEditMode(MWAttributeItem attributeItem) {
        super.initializeEditMode(attributeItem);
        initializeOrderComboBox(attributeItem);
    }
	
	protected void initializeOrderComboBox(MWAttributeItem attributeItem) {
        if (((Ordering) attributeItem).isAscending()) {
            this.orderComboBox.setSelectedItem(resourceRepository().getString("ASCENDING_CHOICE"));
        }
        else {
            this.orderComboBox.setSelectedItem(resourceRepository().getString("DESCENDING_CHOICE"));          
        }    
	}
	
	protected JComboBox buildOrderComboBox() {
	    this.orderComboBox = new JComboBox();
	    this.orderComboBox.addItem(resourceRepository().getString("ASCENDING_CHOICE"));
	    this.orderComboBox.addItem(resourceRepository().getString("DESCENDING_CHOICE"));
		return this.orderComboBox;
	}

	boolean isAscending() {
		return this.orderComboBox.getSelectedItem().equals(resourceRepository().getString("ASCENDING_CHOICE")); 
	}


	protected int attributeItemsSize() {
		return ((MWOrderableQuery) getQuery()).orderingItemsSize();
	}
	
	
	protected int indexOfAttributeItem(MWAttributeItem attributeItem) {
		return ((MWOrderableQuery) getQuery()).indexOfOrderingItem((Ordering) attributeItem);
	}
	
	protected void removeAttributeItem(int index) {
		((MWOrderableQuery) getQuery()).removeOrderingItem(index);
	}
	
	protected void addAttributeItem(int index, Iterator queryables, Iterator allowsNulls) {
	    Ordering item = ((MWOrderableQuery) getQuery()).addOrderingItem(index, queryables, allowsNulls);
		item.setAscending(isAscending());
	}

}
