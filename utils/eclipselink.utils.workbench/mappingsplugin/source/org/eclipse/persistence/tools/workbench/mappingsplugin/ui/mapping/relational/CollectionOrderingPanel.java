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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Collection;
import java.util.ListIterator;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableColumn;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveTablePanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel.UpDownAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWCollectionOrdering;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.QueryKeyCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational.RelationalProjectNode;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.ComboBoxTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.TableCellEditorAdapter;
import org.eclipse.persistence.tools.workbench.uitools.chooser.NodeSelector;
import org.eclipse.persistence.tools.workbench.uitools.swing.CachingComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.IndirectComboBoxModel;
import org.eclipse.persistence.tools.workbench.utility.BidiTransformer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullListIterator;


final class CollectionOrderingPanel extends AbstractSubjectPanel
{
    
	public CollectionOrderingPanel(ValueModel subjectHolder, WorkbenchContextHolder contextHolder) {
		super(subjectHolder, contextHolder);
	}
    
    MWCollectionMapping getCollectionMapping() {
        return (MWCollectionMapping) getSubjectHolder().getValue();
    }
    
    protected void initializeLayout() {
        GridBagConstraints constraints = new GridBagConstraints();
        
        AddRemoveTablePanel orderingsPanel = buildOrderingsTablePanel();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(5, 5, 5, 5);
        add(orderingsPanel, constraints);
    }
    
    private AddRemoveTablePanel buildOrderingsTablePanel() {
        AddRemoveTablePanel tablePanel = new AddRemoveTablePanel(
                getApplicationContext(),
                buildTablePanelAdapter(),
                buildOrderingsListModel(),
                buildOrderingColumnAdapter(),
                AddRemovePanel.RIGHT,
                buildOrderingSelector());

        updateTableColumns((JTable) tablePanel.getComponent());
       
        return tablePanel;
    }
    
    private void updateTableColumns(JTable table) {
        int rowHeight = 0;

        TableColumn column = table.getColumnModel().getColumn(OrderingsColumnAdapter.ORDER_COLUMN);
        ComboBoxTableCellRenderer orderingRenderer = this.buildOrderingComboBoxRenderer();
        column.setCellRenderer(orderingRenderer);
        column.setCellEditor(new TableCellEditorAdapter(this.buildOrderingComboBoxRenderer()));
        rowHeight = Math.max(rowHeight, orderingRenderer.getPreferredHeight());
        
        column = table.getColumnModel().getColumn(OrderingsColumnAdapter.QUERY_KEY_COLUMN);
        ComboBoxTableCellRenderer queryKeyRenderer = this.buildQueryKeyComboBoxRenderer();
        column.setCellRenderer(queryKeyRenderer);
        column.setCellEditor(new TableCellEditorAdapter(this.buildQueryKeyComboBoxRenderer()));

        table.setRowHeight(rowHeight);
    }
    
    private UpDownAdapter buildTablePanelAdapter() {
        return new UpDownAdapter() {   
            public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
                Object[] selectedValues = listSelectionModel.getSelectedValues();
                for (int i = 0; i < selectedValues.length; i++) {
                   getCollectionMapping().removeOrdering((MWCollectionOrdering) selectedValues[i]);
                }        
            }
        
            public void addNewItem(ObjectListSelectionModel listSelectionModel) {
                if (getCollectionMapping().getReferenceDescriptor() != null) {
                    Collection queryKeys = getCollectionMapping().getReferenceDescriptor().getAllQueryKeysIncludingInherited();
                    
                    if (queryKeys.size() == 0) {
                        getCollectionMapping().addOrdering(null);
                    }
                    else {
                        getCollectionMapping().addOrdering((MWQueryKey) queryKeys.iterator().next());
                    }
                } else {
                    getCollectionMapping().addOrdering(null);
                }
        
            }
        
            public void moveItemsDown(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                    getCollectionMapping().moveOrderingDown((MWCollectionOrdering) items[i]);
                }        
            }
        
            public void moveItemsUp(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                    getCollectionMapping().moveOrderingUp((MWCollectionOrdering) items[i]);
                } 
            }
        
        };
    }
    
    private ListValueModel buildOrderingsListModel() {
        return new ListAspectAdapter(getSubjectHolder(), MWCollectionMapping.ORDERINGS_LIST) {
            protected ListIterator getValueFromSubject() {
                return ((MWCollectionMapping) this.subject).orderings();
            }
            
            protected int sizeFromSubject() {
                return ((MWCollectionMapping) this.subject).orderingsSize();
            }
        };
    }
    
    private ColumnAdapter buildOrderingColumnAdapter() {
        return new OrderingsColumnAdapter(resourceRepository());
    }
    
    private ComboBoxModel buildOrderingComboBoxModel() {    
        return new DefaultComboBoxModel(new Object[] {
                    resourceRepository().getString("ASCENDING_OPTION"),
                    resourceRepository().getString("DESCENDING_OPTION")});  
    }
    
    private ComboBoxTableCellRenderer buildOrderingComboBoxRenderer() {
        return new ComboBoxTableCellRenderer(this.buildOrderingComboBoxModel());
    }
    
    private ComboBoxTableCellRenderer buildQueryKeyComboBoxRenderer() {
        return new ComboBoxTableCellRenderer(this.buildQueryKeyComboBoxModel(), buildQueryListCellRenderer());
    }
    
    private ListCellRenderer buildQueryListCellRenderer(){
        return new AdaptableListCellRenderer(new QueryKeyCellRendererAdapter(resourceRepository()));
    }
    
    private NodeSelector buildOrderingSelector() {
        return new NodeSelector() {
            public void selectNodeFor(Object item) {
                RelationalProjectNode projectNode = (RelationalProjectNode) navigatorSelectionModel().getSelectedProjectNodes()[0];
                projectNode.selectQueryKey(((MWCollectionOrdering) item).getQueryKey(), getWorkbenchContext());       
             }       
        };
    }
    
    
    // ********** classes **********

    private static class OrderingsColumnAdapter implements ColumnAdapter {
        
        ResourceRepository resourceRepository;
        public static final int QUERY_KEY_COLUMN = 0;
        public static final int ORDER_COLUMN = 1;
        public static final int COLUMN_COUNT = 2;

        private static final String[] COLUMN_NAME_KEYS = new String[] {
            "QUERY_KEY_COLUMN_HEADER",
            "ORDER_COLUMN_HEADER",
        };

        private OrderingsColumnAdapter(ResourceRepository repository) {
            super();
            this.resourceRepository = repository;
        }
        
        private PropertyValueModel buildQueryKeyAdapter(MWCollectionOrdering ordering) {
            PropertyValueModel adapter = new PropertyAspectAdapter(MWCollectionOrdering.QUERY_KEY_PROPERTY, ordering) {
                protected Object getValueFromSubject() {
                    return ((MWCollectionOrdering) this.subject).getQueryKey();
                }
                protected void setValueOnSubject(Object value) {
                    ((MWCollectionOrdering) this.subject).setQueryKey((MWQueryKey) value);
                }
            };
			return new ValuePropertyPropertyValueModelAdapter(adapter, MWQueryKey.NAME_PROPERTY);
        }
        
        private PropertyValueModel buildOrderAdapter(MWCollectionOrdering ordering) {
            return new TransformationPropertyValueModel(
                    new PropertyAspectAdapter(MWCollectionOrdering.ASCENDING_PROPERTY, ordering) {
                        protected Object getValueFromSubject() {  
                            return Boolean.valueOf(((MWCollectionOrdering) this.subject).isAscending());
                        }
                        protected void setValueOnSubject(Object value) {
                            ((MWCollectionOrdering) this.subject).setAscending(((Boolean) value).booleanValue());
                        }
                    },
                    new BidiTransformer() {
                        public Object reverseTransform(Object o) {

                            if (o == null) {
                                return Boolean.FALSE;
                            }
                            if (((String) o).equals(OrderingsColumnAdapter.this.resourceRepository.getString("ASCENDING_OPTION"))) {
                                return Boolean.TRUE;
                            }
                            return Boolean.FALSE;
                        }

                        public Object transform(Object o) {
                            if (((Boolean) o).equals(Boolean.TRUE)) {
                                return OrderingsColumnAdapter.this.resourceRepository.getString("ASCENDING_OPTION");
                            }
                            return OrderingsColumnAdapter.this.resourceRepository.getString("DESCENDING_OPTION");
                        }
                    });
        }

        public PropertyValueModel[] cellModels(Object subject) {
            MWCollectionOrdering ordering = (MWCollectionOrdering) subject;
            PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];

            result[QUERY_KEY_COLUMN]            = this.buildQueryKeyAdapter(ordering);
            result[ORDER_COLUMN]            = this.buildOrderAdapter(ordering);

            return result;
        }

        public Class getColumnClass(int index) {
            switch (index) {
                case QUERY_KEY_COLUMN:      return Object.class;
                case ORDER_COLUMN:          return Object.class;
                default:                    return Object.class;
            }
        }
        
        public int getColumnCount() {
            return COLUMN_COUNT;
        }

        public String getColumnName(int index) {
            return this.resourceRepository.getString(COLUMN_NAME_KEYS[index]);
        }

        public boolean isColumnEditable(int index) {
            return true;
        }
    }

	private CachingComboBoxModel buildQueryKeyComboBoxModel() {
		return new IndirectComboBoxModel(new SimplePropertyValueModel(), this.getSubjectHolder()) {
			protected ListIterator listValueFromSubject(Object subject) {
				return CollectionOrderingPanel.this.orderedQueryKeyChoices((MWCollectionMapping) subject);
			}
		};
	}
	
	ListIterator orderedQueryKeyChoices(MWCollectionMapping collectionMapping) {
		MWRelationalDescriptor referenceDescriptor = (MWRelationalDescriptor) collectionMapping.getReferenceDescriptor();
		if (referenceDescriptor != null) {
			return CollectionTools.sort(referenceDescriptor.allQueryKeysIncludingInherited()).listIterator();
		}
		return NullListIterator.instance();
	}

}
