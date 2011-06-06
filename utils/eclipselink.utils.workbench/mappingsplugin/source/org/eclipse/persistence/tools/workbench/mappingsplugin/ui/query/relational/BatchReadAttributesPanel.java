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

import java.awt.Color;
import java.awt.Dimension;
import java.util.Iterator;
import java.util.ListIterator;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel.UpDownOptionAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWBatchReadItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadAllQuery;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;


final class BatchReadAttributesPanel extends AbstractAttributeItemsPanel {

	public BatchReadAttributesPanel(PropertyValueModel queryHolder, WorkbenchContextHolder contextHolder) {
		super(queryHolder, contextHolder);
	}
	
	protected PropertyValueModel buildQueryHolder(PropertyValueModel queryHolder) {
		return new FilteringPropertyValueModel(queryHolder) {
			protected boolean accept(Object value) {
				return value instanceof MWRelationalReadAllQuery;
			}
		};
	}
	
	protected String helpTopicId() {
		return "query.report.batchReadAttributes";	
	}
	
	String listTitleKey() {
		return "BATCH_READ_ATTRIBUTES_LIST";
	}
		
	UpDownOptionAdapter buildAttributesPanelAdapter() {
		return new AddRemoveListPanel.UpDownOptionAdapter() {
			public String optionalButtonKey() {
				return "BATCH_READ_ATTRIBUTES_LIST_EDIT_BUTTON";
			}

			public void optionOnSelection(ObjectListSelectionModel listSelectionModel) {
			    editSelectedAttribute((MWAttributeItem) listSelectionModel.getSelectedValue());
			}
			
			public boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel) {
				return listSelectionModel.getSelectedValuesSize() == 1;
            }

			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
				addBatchReadAttribute();
			}

			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
				Object[] selectedValues = listSelectionModel.getSelectedValues();
				for (int i = 0; i < selectedValues.length; i++) {
					((MWRelationalReadAllQuery) getQuery()).removeBatchReadItem((MWBatchReadItem) selectedValues[i]);
				}
			}
			
            public void moveItemsDown(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                   ((MWRelationalReadAllQuery) getQuery()).moveBatchReadItemDown((MWBatchReadItem) items[i]);
                }
            }
            
            public void moveItemsUp(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                    ((MWRelationalReadAllQuery) getQuery()).moveBatchReadItemUp((MWBatchReadItem) items[i]);
                 }           
            }
		};
	}
	
	protected ListValueModel buildAttributesHolder() {
		return new ListAspectAdapter(getQueryHolder(), MWRelationalReadAllQuery.BATCH_READ_ITEMS_LIST) {
			protected ListIterator getValueFromSubject() {
				return ((MWRelationalReadAllQuery) this.subject).batchReadItems();
			}
			protected int sizeFromSubject() {
				return ((MWRelationalReadAllQuery) this.subject).batchReadItemsSize();
			}
		};
	}

    protected boolean panelEnabled(MWQueryFormat queryFormat) {
        return queryFormat.batchReadAttributesAllowed();
    }
    
	private void addBatchReadAttribute() {
	    editSelectedAttribute(null);
	}

	AttributeItemDialog buildAttributeItemDialog(MWAttributeItem item) {
		return
			new AttributeItemDialog(getQuery(), item, getWorkbenchContext()) {

            protected String titleKey() {
                return "BATCH_READ_ATTRIBUTE_DIALOG_TITLE";
            }           
            
            protected String editTitleKey() {
                return "BATCH_READ_ATTRIBUTE_EDIT_DIALOG_TITLE";
            }
            
			protected String helpTopicId() {
				return "dialog.batchReadAttribute";
			}

			protected QueryableTree buildQueryableTree() {
				QueryableTree tree = super.buildQueryableTree();
				tree.setCellRenderer(buildQueryableTreeRenderer(tree));
				tree.setCellEditor(null);
				return tree;
			}
			
			private TreeCellRenderer buildQueryableTreeRenderer(final QueryableTree tree) {
				return new SimpleTreeCellRenderer() {
					public Color getBackgroundSelectionColor() {
						if (!tree.hasFocus() && !tree.isEditing()) {
							return UIManager.getColor("Panel.background");
						}
						return super.getBackgroundSelectionColor();
					}
					public Color getBorderSelectionColor() {
						if (!tree.hasFocus() && !tree.isEditing()) {
							return UIManager.getColor("Panel.background");
						}
						return super.getBorderSelectionColor();
					}
					public Dimension getPreferredSize() {
						Dimension size = super.getPreferredSize();
						size.height += 2;
						return size;
					}
					protected String buildText(Object value) {
						if (MWTableDescriptor.class.isAssignableFrom(((DefaultMutableTreeNode)value).getUserObject().getClass())) {
							return "";
						}						
						return ((QueryableTreeNode) value).getQueryable().getName();
					}
					protected Icon buildIcon(Object value) {
						if (MWTableDescriptor.class.isAssignableFrom(((DefaultMutableTreeNode)value).getUserObject().getClass())) {
							return null;
						}	
						return resourceRepository().getIcon(((QueryableTreeNode) value).getQueryable().iconKey());
					}
				};
			}
			
			protected int attributeItemsSize() {
				return ((MWRelationalReadAllQuery) getQuery()).batchReadItemsSize();
			}
			
			protected int indexOfAttributeItem(MWAttributeItem attributeItem) {
				return ((MWRelationalReadAllQuery) getQuery()).indexOfBatchReadItem((MWBatchReadItem) attributeItem);
			}
			
			protected void removeAttributeItem(int index) {
				((MWRelationalReadAllQuery) getQuery()).removeBatchReadItem(index);
			}
			
			protected void addAttributeItem(int index, Iterator queryables, Iterator allowsNulls) {
				((MWRelationalReadAllQuery) getQuery()).addBatchReadItem(index, queryables, allowsNulls);
			}
			

			protected Filter buildTraversableFilter() {
				return new Filter() {
					public boolean accept(Object o) {
						return ((MWQueryable) o).isTraversableForBatchReadAttribute();
					}
				};
			}
			
			protected Filter buildChooseableFilter() {
				return new Filter() {
					public boolean accept(Object o) {
						return ((MWQueryable) o).isValidForBatchReadAttribute();
					}
				};
			}
		};	
	}
}
