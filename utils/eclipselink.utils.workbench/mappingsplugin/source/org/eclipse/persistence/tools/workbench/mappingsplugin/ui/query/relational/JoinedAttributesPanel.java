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

import java.util.Iterator;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAbstractRelationalReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWJoinedItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryFormat;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;


final class JoinedAttributesPanel extends AbstractAttributeItemsPanel {

	public JoinedAttributesPanel(PropertyValueModel queryHolder, WorkbenchContextHolder contextHolder) {
		super(queryHolder, contextHolder);
	}
	
	protected PropertyValueModel buildQueryHolder(PropertyValueModel queryHolder) {
		return new FilteringPropertyValueModel(queryHolder) {
			protected boolean accept(Object value) {
				return value instanceof MWAbstractRelationalReadQuery;
			}
		};
	}
	
	protected String helpTopicId() {
		return "query.joinedAtributes";	
	}

	String listTitleKey() {
		return "JOINED_ATTRIBUTES_LIST";
	}
	
	AddRemovePanel.UpDownOptionAdapter buildAttributesPanelAdapter() {
		return new AddRemovePanel.UpDownOptionAdapter() {
			public String optionalButtonKey() {
				return "JOINED_ATTRIBUTES_LIST_EDIT_BUTTON";
			}

			public void optionOnSelection(ObjectListSelectionModel listSelectionModel) {
				editSelectedAttribute((MWJoinedItem) listSelectionModel.getSelectedValue());
			}
			
			public boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel) {
				return listSelectionModel.getSelectedValuesSize() == 1;
            }

			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
				addJoinedAttribute();
			}

			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
				Object[] selectedValues = listSelectionModel.getSelectedValues();
				for (int i = 0; i < selectedValues.length; i++) {
					((MWAbstractRelationalReadQuery) getQuery()).removeJoinedItem((MWJoinedItem) selectedValues[i]);
				}
			}
			
            public void moveItemsDown(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                   ((MWAbstractRelationalReadQuery) getQuery()).moveJoinedItemDown((MWJoinedItem) items[i]);
                }
            }
            
            public void moveItemsUp(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                    ((MWAbstractRelationalReadQuery) getQuery()).moveJoinedItemUp((MWJoinedItem) items[i]);
                 }           
            }	
        };
	}
	
	
	
	protected ListValueModel buildAttributesHolder() {
		return new ListAspectAdapter(getQueryHolder(), MWAbstractRelationalReadQuery.JOINED_ITEMS_LIST) {
			protected ListIterator getValueFromSubject() {
				return ((MWAbstractRelationalReadQuery) this.subject).joinedItems();
			}
			protected int sizeFromSubject() {
				return ((MWAbstractRelationalReadQuery) this.subject).joinedItemsSize();
			}
		};
	}
	
    protected boolean panelEnabled(MWQueryFormat queryFormat) {
        return true;
    }
    
	private void addJoinedAttribute() {
	    editSelectedAttribute(null);
	}
	
    AttributeItemDialog buildAttributeItemDialog(MWAttributeItem item) {  
		return
			new AttributeItemDialog(getQuery(), item, getWorkbenchContext()) {
				protected String titleKey() {
					return "JOINED_ATTRIBUTES_DIALOG_TITLE";
				}
                
                protected String editTitleKey() {
                    return "JOINED_ATTRIBUTES_EDIT_DIALOG_TITLE";
                }
    	
				protected String helpTopicId() {
					return "dialog.joinedAttribute";
				}
				
				protected Filter buildTraversableFilter() {
					return new Filter() {
						public boolean accept(Object o) {
							return ((MWQueryable) o).isTraversableForJoinedAttribute();
						}
					};
				}
			
				protected Filter buildChooseableFilter() {
					return new Filter() {
						public boolean accept(Object o) {
							return ((MWQueryable) o).isValidForJoinedAttribute();
						}
					};
				}
				
				protected int attributeItemsSize() {
					return ((MWAbstractRelationalReadQuery) getQuery()).joinedItemsSize();
				}
				
				protected int indexOfAttributeItem(MWAttributeItem attributeItem) {
					return ((MWAbstractRelationalReadQuery) getQuery()).indexOfJoinedItem((MWJoinedItem) attributeItem);
				}
				
				protected void removeAttributeItem(int index) {
					((MWAbstractRelationalReadQuery) getQuery()).removeJoinedItem(index);
				}
				
				protected void addAttributeItem(int index, Iterator queryables, Iterator allowsNulls) {
					((MWAbstractRelationalReadQuery) getQuery()).addJoinedItem(index, queryables, allowsNulls);
				}
			};			
	}

}
