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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.util.Iterator;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel.UpDownOptionAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWGroupingItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportQuery;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;


final class GroupingAttributesPanel extends AbstractAttributeItemsPanel {

	GroupingAttributesPanel(PropertyValueModel queryHolder, WorkbenchContextHolder contextHolder) {
		super(queryHolder, contextHolder);
	}

	String listTitleKey() {
		return "REPORT_QUERY_GROUPING_ATTRIBUTES_LIST";
	}

	protected String helpTopicId() {
		return "query.report.groupings";	
	}

    UpDownOptionAdapter buildAttributesPanelAdapter() {
		return new UpDownOptionAdapter() {
			public String optionalButtonKey() {
				return "REPORT_QUERY_GROUPING_ATTRIBUTES_LIST_EDIT_BUTTON";
			}

			public void optionOnSelection(ObjectListSelectionModel listSelectionModel) {
			    editSelectedAttribute((MWGroupingItem) listSelectionModel.getSelectedValue());
			}
			
			public boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel) {
				return listSelectionModel.getSelectedValuesSize() == 1;
            }

			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
				addGroupingAttribute();
			}

			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
				Object[] selectedValues = listSelectionModel.getSelectedValues();
				for (int i = 0; i < selectedValues.length; i++) {
					((MWReportQuery) getQuery()).removeGroupingItem((MWGroupingItem) selectedValues[i]);
				}
			}
			
            public void moveItemsDown(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                   ((MWReportQuery) getQuery()).moveGroupingItemDown((MWGroupingItem) items[i]);
                }
            }
            
            public void moveItemsUp(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                    ((MWReportQuery) getQuery()).moveGroupingItemUp((MWGroupingItem) items[i]);
                 }           
            }   
		};
	}
	
	
	protected ListValueModel buildAttributesHolder() {
		return new ListAspectAdapter(getQueryHolder(), MWReportQuery.GROUPING_ITEMS_LIST) {
			protected ListIterator getValueFromSubject() {
				return ((MWReportQuery) this.subject).groupingItems();
			}
			protected int sizeFromSubject() {
				return ((MWReportQuery) this.subject).groupingItemsSize();
			}
		};
	}
    
    protected boolean panelEnabled(MWQueryFormat queryFormat) {
        return queryFormat.groupingAtributesAllowed();
    }
    
	private void addGroupingAttribute() {
	    editSelectedAttribute(null);
	}

    AttributeItemDialog buildAttributeItemDialog(MWAttributeItem item) {
		AttributeItemDialog dialog = new AttributeItemDialog(getQuery(), item, getWorkbenchContext()) {
			protected String titleKey() {
				return "GROUPING_ATTRIBUTES_DIALOG_TITLE";
			}
            
            protected String editTitleKey() {
                return "GROUPING_ATTRIBUTES_EDIT_DIALOG_TITLE";
            }
            
			protected String helpTopicId() {
				return "dialog.groupingAttribute";
			}
			
			protected int attributeItemsSize() {
				return ((MWReportQuery) getQuery()).groupingItemsSize();
			}
			
			protected int indexOfAttributeItem(MWAttributeItem attributeItem) {
				return ((MWReportQuery) getQuery()).indexOfGroupingItem((MWGroupingItem) attributeItem);
			}
			
			protected void removeAttributeItem(int index) {
				((MWReportQuery) getQuery()).removeGroupingItem(index);
			}
			
			protected void addAttributeItem(int index, Iterator queryables, Iterator allowsNulls) {
				((MWReportQuery) getQuery()).addGroupingItem(index, queryables, allowsNulls);
			}
		};       
		return dialog;
    }


}
