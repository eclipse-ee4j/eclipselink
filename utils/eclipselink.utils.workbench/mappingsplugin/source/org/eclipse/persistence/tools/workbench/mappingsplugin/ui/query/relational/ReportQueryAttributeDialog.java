/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportQuery;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;



final class ReportQueryAttributeDialog extends AttributeItemDialog {

	private JComboBox functionComboBox;
	private JTextField itemNameTextField;
	
	ReportQueryAttributeDialog(MWReportQuery query, MWReportAttributeItem attributeItem, WorkbenchContext context) {
		super(query, attributeItem, context);
		this.functionComboBox = new JComboBox(buildFunctionComboBoxModel());
		this.itemNameTextField = new JTextField();
	}

	protected String helpTopicId() {
		return "descriptor.queryManager.report.attributes";
	}

    protected String titleKey() {
        return "REPORT_QUERY_ATTRIBUTES_DIALOG_TITLE";
    }    
    
    protected String editTitleKey() {
        return "REPORT_QUERY_ATTRIBUTES_EDIT_DIALOG_TITLE";
    }
    
    protected Filter buildChooseableFilter() {
        return new Filter() {
            public boolean accept(Object o) {
                return ((MWQueryable) o).isValidForReportQueryAttribute();
            }
        };
    }
    
    protected Filter buildTraversableFilter() {
        return new Filter() {
            public boolean accept(Object o) {
                return ((MWQueryable) o).isTraversableForReportQueryAttribute();
            }
        };
    }
    
	protected Component buildMainPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraints constraints = new GridBagConstraints();
		
		QueryableTree queryableTree = buildQueryableTree();
		
		JScrollPane scrollPane = new JScrollPane(queryableTree);

		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);
		panel.add(scrollPane, constraints);	
		
		JLabel functionLabel = SwingComponentFactory.buildLabel("FUNCTION_LABEL", resourceRepository());
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 0, 0, 0);
		panel.add(functionLabel, constraints);	
		
        this.functionComboBox.setEditable(true);
		this.functionComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                updateOKButton();
                calculateItemName();
            }
        });
		constraints.gridx		= 1;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 0, 0);
		panel.add(this.functionComboBox, constraints);	
		functionLabel.setLabelFor(this.functionComboBox);
		
		
		JLabel itemNameLabel = SwingComponentFactory.buildLabel("ATTRIBUTE_ITEM_NAME_LABEL", resourceRepository());
		constraints.gridx		= 0;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 0, 0, 0);
		panel.add(itemNameLabel, constraints);	
		
		constraints.gridx		= 1;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 0, 0);
		panel.add(this.itemNameTextField, constraints);
		itemNameLabel.setLabelFor(this.itemNameTextField);

		return panel;
	}

	private ComboBoxModel buildFunctionComboBoxModel() {
		return new DefaultComboBoxModel(MWReportAttributeItem.FUNCTIONS);	
	}
	
	String getFunction() {
		return (String) this.functionComboBox.getSelectedItem();
	}
	
	String getItemName() {
		return this.itemNameTextField.getText();
	}
	
    protected void initializeEditMode(MWAttributeItem attributeItem) {
        super.initializeEditMode(attributeItem);
        this.functionComboBox.setSelectedItem(((MWReportAttributeItem) attributeItem).getFunction());
        this.itemNameTextField.setText(((MWReportAttributeItem) attributeItem).getName());
    }  
    
    protected void treeSelectionChanged(TreeSelectionEvent e) {
        super.treeSelectionChanged(e);
        calculateItemName();
    }
    

    protected void updateOKButton() {       
        if (getFunction() == MWReportAttributeItem.COUNT_FUNCTION
                && getQueryableTree().getSelectionCount() == 0) {
            getOKAction().setEnabled(true);
            return;
        }
        super.updateOKButton();
    }
    
    private void calculateItemName() {
		TreePath selectionPath = getQueryableTree().getSelectionPath();
		if (selectionPath == null) {
		    return;
		}
		QueryableTreeNode selectedNode = (QueryableTreeNode) selectionPath.getLastPathComponent();
		MWQueryable queryableObject = selectedNode.getQueryable();
		String suggestedItemName = queryableObject.getName();
		
		selectionPath = selectionPath.getParentPath();
		
		while (selectionPath.getPathCount() > 1) {//first path component is always a descriptor, we want to quit before reaching it
			selectedNode = (QueryableTreeNode) selectionPath.getLastPathComponent();
			MWQueryable joinedQueryable = selectedNode.getQueryable();
			suggestedItemName = joinedQueryable.getName() + suggestedItemName;
			selectionPath = selectionPath.getParentPath();
		}

		if (getFunction() != MWReportAttributeItem.NO_FUNCTION) {
		    suggestedItemName += getFunction();
		}
		this.itemNameTextField.setText(suggestedItemName);
    }
	

    protected int attributeItemsSize() {
        return ((MWReportQuery) getQuery()).attributeItemsSize();
    }

    protected int indexOfAttributeItem(MWAttributeItem attributeItem) {
        return ((MWReportQuery) getQuery()).indexOfAttributeItem((MWReportAttributeItem) attributeItem);
    }
    
    protected void addAttributeItem(int index, Iterator queryables,
            Iterator allowsNulls) {
        MWReportAttributeItem attributeItem = ((MWReportQuery) getQuery()).addAttributeItem(index, getItemName(), queryables, allowsNulls);
        attributeItem.setFunction(getFunction());
    }
    
    protected void removeAttributeItem(int index) {
        ((MWReportQuery) getQuery()).removeAttributeItem(index);
    }
    
    protected boolean preConfirm() {   
        TreePath selectionPath = getQueryableTree().getSelectionPath();
        if (selectionPath == null) {
            if (getFunction() != MWReportAttributeItem.COUNT_FUNCTION ) {
                throw new IllegalStateException("Must select an item from the tree unless the chosen funciton is Count");
            }
            MWReportAttributeItem item = ((MWReportQuery) getQuery()).addAttributeItem(null, (MWQueryable) null);
            item.setFunction(getFunction());
            return true;           
        }
        return super.preConfirm();
	}
}
