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

import java.awt.event.MouseEvent;
import java.util.ListIterator;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveTablePanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.DoubleClickMouseListener;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWOrderableQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryableArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.Ordering;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.ComboBoxTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.TableCellEditorAdapter;
import org.eclipse.persistence.tools.workbench.utility.BidiTransformer;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;


class OrderingAttributesPanel extends AbstractAttributeItemsPanel {

	private Filter traversableFilter;
	private Filter chooseableFilter;
	
	public OrderingAttributesPanel(PropertyValueModel queryHolder, Filter traversableFilter, Filter chooseableFilter, WorkbenchContextHolder contextHolder) {
		super(queryHolder, contextHolder);
		this.traversableFilter = traversableFilter;
		this.chooseableFilter = chooseableFilter;
	}
	
	protected PropertyValueModel buildQueryHolder(PropertyValueModel queryHolder) {
		return new FilteringPropertyValueModel(queryHolder) {
			protected boolean accept(Object value) {
				return value instanceof MWOrderableQuery;
			}
		};
	}
	
	protected String helpTopicId() {
		return "query.orderingAttributes";	
	}

	String listTitleKey() {
		return "ORDERING_ATTRIBUTES_LIST";
	}
	
	protected AddRemovePanel buildAddRemovePanel() {
        final AddRemoveTablePanel tablePanel =  new AddRemoveTablePanel(
				getApplicationContext(), 
				buildAttributesPanelAdapter(), 
				buildAttributesHolder(),
				new OrderingAttributesColumnAdapter(resourceRepository()),
				AddRemovePanel.RIGHT);
		
        tablePanel.setBorder(buildTitledBorder(listTitleKey()));
		SwingComponentFactory.addDoubleClickMouseListener(tablePanel.getComponent(),
		        new DoubleClickMouseListener() {
                    public void mouseDoubleClicked(MouseEvent e) {
                        editSelectedAttribute((MWAttributeItem) tablePanel.getSelectionModel().getSelectedValue());
                    }
                });
		addHelpTopicId(tablePanel, helpTopicId());
		
        updateTableColumns((JTable) tablePanel.getComponent());
        return tablePanel;
    }
    
    protected boolean panelEnabled(MWQueryFormat queryFormat) {
        return queryFormat.orderingAttributesAllowed();
    }
    
	private void updateTableColumns(JTable table) {
		int rowHeight = 0;

		// function column (combo-box)
		TableColumn column = table.getColumnModel().getColumn(OrderingAttributesColumnAdapter.ORDER_COLUMN);
		ComboBoxTableCellRenderer functionRenderer = this.buildOrderingComboBoxRenderer();
		column.setCellRenderer(functionRenderer);
		column.setCellEditor(new TableCellEditorAdapter(this.buildOrderingComboBoxRenderer()));
		rowHeight = Math.max(rowHeight, functionRenderer.getPreferredHeight());
		
		column = table.getColumnModel().getColumn(OrderingAttributesColumnAdapter.ATTRIBUTE_COLUMN);
		column.setCellRenderer(new SimpleTableCellRenderer() {
			protected String buildText(Object value) {
				if (value != null) {
					return ((MWQueryableArgument) value).displayString();
				}
				return "";
			}
		});

		table.setRowHeight(rowHeight);
	}
	
	private ComboBoxModel buildOrderingComboBoxModel() {	
	    return new DefaultComboBoxModel(new Object[] {
            		resourceRepository().getString("ASCENDING_CHOICE"),
                    resourceRepository().getString("DESCENDING_CHOICE")});	
	}
	
	private ComboBoxTableCellRenderer buildOrderingComboBoxRenderer() {
		return new ComboBoxTableCellRenderer(this.buildOrderingComboBoxModel());
	}
	
	   
	AddRemovePanel.UpDownOptionAdapter buildAttributesPanelAdapter() {
		return new AddRemoveListPanel.UpDownOptionAdapter() {
			public String optionalButtonKey() {
				return "ORDERING_ATTRIBUTES_LIST_EDIT_BUTTON";
			}

			public void optionOnSelection(ObjectListSelectionModel listSelectionModel) {
			    editSelectedAttribute((MWAttributeItem) listSelectionModel.getSelectedValue());
			}
			
			public boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel) {
				return listSelectionModel.getSelectedValuesSize() == 1;
            }

			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
			    addOrderingItem();
			}

			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {

				Object[] selectedValues = listSelectionModel.getSelectedValues();
				for (int i = 0; i < selectedValues.length; i++) {
					((MWOrderableQuery) getQuery()).removeOrderingItem((Ordering) selectedValues[i]);
				}
			}
			
            public void moveItemsDown(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                   ((MWOrderableQuery) getQuery()).moveOrderingItemDown((Ordering) items[i]);
                }
            }
            
            public void moveItemsUp(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                    ((MWOrderableQuery) getQuery()).moveOrderingItemUp((Ordering) items[i]);
                 }           
            }   
            
		};
	}
	
	
	protected ListValueModel buildAttributesHolder() {
		return new ListAspectAdapter(getQueryHolder(), MWOrderableQuery.ORDERING_ITEMS_LIST) {
			protected ListIterator getValueFromSubject() {
				return ((MWOrderableQuery) this.subject).orderingItems();
			}
			protected int sizeFromSubject() {
				return ((MWOrderableQuery) this.subject).orderingItemsSize();
			}
		};
	}
	
	private void addOrderingItem() {
	    editSelectedAttribute(null);
	}

    AttributeItemDialog buildAttributeItemDialog(MWAttributeItem item) {
		return new OrderingAttributeDialog(
					(MWOrderableQuery) getQuery(),
					(Ordering) item,
					OrderingAttributesPanel.this.traversableFilter, 
					OrderingAttributesPanel.this.chooseableFilter, 
					getWorkbenchContext());
    }
    
    protected Filter getTraversableFilter() {
        return this.traversableFilter;
    }
    
    protected Filter getChooseableFilter() {
        return this.chooseableFilter;
    }
    
	// ********** classes **********

	private static class OrderingAttributesColumnAdapter implements ColumnAdapter {
		
		ResourceRepository resourceRepository;
		public static final int ATTRIBUTE_COLUMN = 0;
		public static final int ORDER_COLUMN = 1;
		public static final int COLUMN_COUNT = 2;

		private final String[] COLUMN_NAME_KEYS = new String[] {
			"ATTRIBUTE_COLUMN_HEADER",
			"ORDER_COLUMN_HEADER",
		};

		private static final String[] EMPTY_STRING_ARRAY = new String[0];

		private OrderingAttributesColumnAdapter(ResourceRepository repository) {
			super();
			this.resourceRepository = repository;
		}
		
		private PropertyValueModel buildAttributeAdapter(MWAttributeItem item) {
			// TODO we need some change notifications from MWQueryableArgument and MWQueryableArgumentElement
			return new PropertyAspectAdapter(EMPTY_STRING_ARRAY, item) {	// the queryableArgument never changes
				protected Object getValueFromSubject() {
					return ((MWAttributeItem) this.subject).getQueryableArgument();
				}
			};
		}
		
		private PropertyValueModel buildOrderAdapter(Ordering item) {
			return new TransformationPropertyValueModel(
			        new PropertyAspectAdapter(Ordering.ASCENDING_PROPERTY, item) {
						protected Object getValueFromSubject() {  
							return Boolean.valueOf(((Ordering) this.subject).isAscending());
						}
						protected void setValueOnSubject(Object value) {
							((Ordering) this.subject).setAscending(((Boolean) value).booleanValue());
						}
					},
					new BidiTransformer() {
                        public Object reverseTransform(Object o) {
                            if (o != null && ((String) o).equals(OrderingAttributesColumnAdapter.this.resourceRepository.getString("ASCENDING_BUTTON"))) {
                                return Boolean.TRUE;
                            }
                            return Boolean.FALSE;
                        }

                        public Object transform(Object o) {
                            if (((Boolean) o).equals(Boolean.TRUE)) {
                                return OrderingAttributesColumnAdapter.this.resourceRepository.getString("ASCENDING_BUTTON");
                            }
                            return OrderingAttributesColumnAdapter.this.resourceRepository.getString("DESCENDING_BUTTON");
                        }
                    });
		}

		public PropertyValueModel[] cellModels(Object subject) {
		    Ordering attributeItem = (Ordering) subject;
			PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];

			result[ATTRIBUTE_COLUMN]			= this.buildAttributeAdapter((MWAttributeItem) attributeItem);
			result[ORDER_COLUMN]			= this.buildOrderAdapter(attributeItem);

			return result;
		}

		public Class getColumnClass(int index) {
			switch (index) {
				case ATTRIBUTE_COLUMN:		return Object.class;
				case ORDER_COLUMN:			return Object.class;
				default: 					return Object.class;
			}
		}
		
		public int getColumnCount() {
			return COLUMN_COUNT;
		}

		public String getColumnName(int index) {
			return this.resourceRepository.getString(COLUMN_NAME_KEYS[index]);
		}

		public boolean isColumnEditable(int index) {
			return index == ORDER_COLUMN;
		}
	}
}
