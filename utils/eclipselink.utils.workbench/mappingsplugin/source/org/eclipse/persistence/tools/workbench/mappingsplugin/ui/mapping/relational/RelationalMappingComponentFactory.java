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

import java.util.Iterator;
import java.util.ListIterator;

import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWJoinFetchableMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWJoinFetchableMapping.JoinFetchOption;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectMapMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWTableReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ColumnCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational.RelationalProjectComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational.RelationalProjectNode;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractReadOnlyListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ExtendedListValueModelWrapper;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.chooser.NodeSelector;
import org.eclipse.persistence.tools.workbench.uitools.swing.CachingComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.ExtendedComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.IndirectComboBoxModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


public final class RelationalMappingComponentFactory extends MappingComponentFactory {

	// ************* database field ************

	//relationalDirectMappingHolder expects an MWRelationalDirectMapping
	static ListChooser buildColumnChooser(ValueModel relationalDirectMappingHolder, ValueModel parentDescriptorHolder, WorkbenchContextHolder contextHolder) {
		ResourceRepository resourceRepository = contextHolder.getWorkbenchContext().getApplicationContext().getResourceRepository();
		ListChooser listChooser = 
			new DefaultListChooser(
					buildExtendedColumnComboBoxModel(buildColumnHolder(relationalDirectMappingHolder), parentDescriptorHolder),
					contextHolder,
                    buildColumnNodeSelector(contextHolder),
					buildColumnChooserDialogBuilder()
			);
		listChooser.setRenderer(buildColumnListRenderer(parentDescriptorHolder, resourceRepository));
		return listChooser;
	}

	public static DefaultListChooserDialog.Builder buildColumnChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("DATABASE_FIELD_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("DATABASE_FIELD_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildColumnStringConverter());
		return builder;
	}	


	private static ListIterator orderedColumns(MWRelationalClassDescriptor descriptor) {
		return CollectionTools.sort(descriptor.allAssociatedColumns()).listIterator();
	}
		
	public static CachingComboBoxModel buildExtendedColumnComboBoxModel(PropertyValueModel databaseFieldHolder, ValueModel parentDescriptorHolder) {
		return new ExtendedComboBoxModel(buildColumnComboBoxModel(databaseFieldHolder, parentDescriptorHolder));
	}
	
	public static CachingComboBoxModel buildColumnComboBoxModel(PropertyValueModel databaseFieldHolder, ValueModel parentDescriptorHolder) {
		return new IndirectComboBoxModel(databaseFieldHolder, parentDescriptorHolder) {
			protected ListIterator listValueFromSubject(Object subject) {
				return orderedColumns((MWRelationalClassDescriptor) subject);
			}
			
			protected int listSizeFromSubject(Object subject) {
				return ((MWRelationalClassDescriptor) subject).allAssociatedColumnsSize();
			}
		};		
	}
	
	//Do not want to show <none selected> if the parent is an aggregate descriptor
	public static ListCellRenderer buildColumnListRenderer(final ValueModel parentDescriptorHolder, ResourceRepository resourceRepository) {
		return new AdaptableListCellRenderer(
			new ColumnCellRendererAdapter(resourceRepository) {
				protected String buildNullValueText() {
					if (parentDescriptorHolder.getValue() == null) {
						return super.buildNullValueText();
					}
					if (((MWRelationalDescriptor) parentDescriptorHolder.getValue()).isAggregateDescriptor()) {
						//use a space because otherwise the combo box will appear 'squished'
						return " ";
					}
					return super.buildNullValueText();
				}
			}
		);
	}
	
	public static StringConverter buildColumnStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return o == null ? "" : ((MWColumn) o).qualifiedName();
			}
		};
	}
	
	private static PropertyValueModel buildColumnHolder(ValueModel mappingHolder) {
		PropertyValueModel propertyValueModel = new PropertyAspectAdapter(mappingHolder, MWRelationalDirectMapping.COLUMN_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalDirectMapping) subject).getColumn();
			}
		
			protected void setValueOnSubject(Object value) {
				((MWRelationalDirectMapping) subject).setColumn((MWColumn) value);
			}
		};
		return new ValuePropertyPropertyValueModelAdapter(propertyValueModel, MWColumn.QUALIFIED_NAME_PROPERTY, MWColumn.DATABASE_TYPE_PROPERTY);
	}	
	
    public static NodeSelector buildColumnNodeSelector(final WorkbenchContextHolder contextHolder) {
        return new NodeSelector() {       
            public void selectNodeFor(Object item) {
                RelationalProjectNode projectNode = (RelationalProjectNode) contextHolder.getWorkbenchContext().getNavigatorSelectionModel().getSelectedProjectNodes()[0];
                projectNode.selectColumn((MWColumn) item, contextHolder.getWorkbenchContext());     
            }
        };
    }
    
	// ********** Batch Reading ***********

	//tableReferenceMappingHolder expects an MWTableReferenceMapping
	static JCheckBox buildBatchReadingCheckBox(ValueModel tableReferenceMappingHolder, ResourceRepository resourceRepository) {
		return buildCheckBox("MAPPING_BATCH_READING_CHECK_BOX", buildUseBatchReadingCheckBoxAdapter(tableReferenceMappingHolder), resourceRepository);
	}

	private static ButtonModel buildUseBatchReadingCheckBoxAdapter(ValueModel tableReferenceMappingHolder) {
		return new CheckBoxModelAdapter(buildUseBatchReadingHolder(tableReferenceMappingHolder));
	}

	private static PropertyValueModel buildUseBatchReadingHolder(ValueModel tableReferenceMappingHolder) {
		return new PropertyAspectAdapter(tableReferenceMappingHolder, MWTableReferenceMapping.BATCH_READING_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWTableReferenceMapping) subject).usesBatchReading());
			}

			protected void setValueOnSubject(Object value) {
				((MWTableReferenceMapping) subject).setUsesBatchReading(((Boolean) value).booleanValue());
			}
		};
	}
	
	//*************** join fetch *******************
	
	private static PropertyValueModel buildJoinFetchingHolder(ValueModel joinFetchableMappingHolder) {
		return new PropertyAspectAdapter(joinFetchableMappingHolder, MWJoinFetchableMapping.JOIN_FETCH_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWJoinFetchableMapping) this.subject).getJoinFetchOption();
	        }
	            
	        protected void setValueOnSubject(Object value) {
	            ((MWJoinFetchableMapping) this.subject).setJoinFetchOption((JoinFetchOption) value);
	        }
	    };		
	}
	
	public static JComboBox buildJoinFetchingCombobox(ValueModel joinFetchableMappingHolder, WorkbenchContextHolder context) {
		PropertyValueModel joinFetchingHolder = buildJoinFetchingHolder(joinFetchableMappingHolder);
		DefaultListChooser fetchChooser = new DefaultListChooser(new ComboBoxModelAdapter(buildJoinFetchCollectionHolder(joinFetchableMappingHolder.getValue()), joinFetchingHolder), context);
		fetchChooser.setRenderer(buildJoinFetchOptionRenderer(context.getWorkbenchContext().getApplicationContext().getResourceRepository()));

		return fetchChooser;		
	}
	
    private static ListValueModel buildJoinFetchCollectionHolder(final Object joinFetchableMapping) {
        return new AbstractReadOnlyListValueModel() {
            public Object getValue() {
            	return MWJoinFetchableMapping.JoinFetchOptionSet.joinFetchOptions().toplinkOptions();
            }
        };  
    }
    
    private static ListCellRenderer buildJoinFetchOptionRenderer(final ResourceRepository resourceRepository) {
        return new SimpleListCellRenderer() {
            protected String buildText(Object value) {
                return resourceRepository.getString(((JoinFetchOption) value).resourceKey());
            }
        };
    }

	
	// ************** target table ******************
	
	//directContainerMappingHolder expects an MWRelationalDirectContainerMapping
	
	static ListChooser buildTargetTableChooser(PropertyValueModel directContainerMappingHolder, WorkbenchContextHolder contextHolder) {
		return RelationalProjectComponentFactory.
					buildTableChooser(
						directContainerMappingHolder,
						buildTargetTableHolder(directContainerMappingHolder),
						buildTargetTableChooserDialogBuilder(),
						contextHolder
					);
		
	}
	

	private static DefaultListChooserDialog.Builder buildTargetTableChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("DIRECT_CONTAINER_MAPPING_TARGET_TABLE_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("DIRECT_CONTAINER_MAPPING_TARGET_TABLE_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildTableStringConverter());
		return builder;
	}
	
	private static StringConverter buildTableStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return o == null ? "" : ((MWTable) o).getName();
			}
		};
	}

	private static ValueModel buildParentDescriptorHolder(ValueModel mappingHolder) {
		return new PropertyAspectAdapter(mappingHolder) {
			protected Object getValueFromSubject() {
				return ((MWMapping) subject).getParentDescriptor();
			}
		};
	}

	private static PropertyValueModel buildTargetTableHolder(ValueModel directContainerMappingHolder) {
		return new PropertyAspectAdapter(directContainerMappingHolder, MWRelationalDirectContainerMapping.TARGET_TABLE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalDirectContainerMapping) subject).getTargetTable();
			}
			protected void setValueOnSubject(Object value) {
				((MWRelationalDirectContainerMapping) subject).setTargetTable((MWTable) value);
			}
		};
	}


	// ************** direct value field ******************

	//directContainerMappingHolder expects an MWRelationalDirectContainerMapping
	static ListChooser buildDirectValueColumnChooser(ValueModel directContainerMappingHolder, WorkbenchContextHolder contextHolder) {
		ListChooser listChooser = 
			new DefaultListChooser(
				buildDirectValueColumnComboModel(directContainerMappingHolder), 
				contextHolder,
                buildColumnNodeSelector(contextHolder),
				buildDirectValueColumnChooserDialogBuilder()
			);
		listChooser.setRenderer(buildColumnListRenderer(buildParentDescriptorHolder(directContainerMappingHolder), contextHolder.getWorkbenchContext().getApplicationContext().getResourceRepository()));
		return listChooser;
		
	}

	private static DefaultListChooserDialog.Builder buildDirectValueColumnChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("DIRECT_VALUE_FIELD_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("DIRECT_VALUE_FIELD_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildColumnStringConverter());
		return builder;
	}

	private static ComboBoxModel buildDirectValueColumnComboModel(ValueModel directContainerMappingHolder) {
		return new ComboBoxModelAdapter(
						buildDirectValueColumnListHolder(directContainerMappingHolder),
						buildDirectValueColumnAdapter(directContainerMappingHolder));
	}

	private static PropertyValueModel buildDirectValueColumnAdapter(ValueModel directContainerMappingHolder) {
		return new PropertyAspectAdapter(directContainerMappingHolder, MWRelationalDirectContainerMapping.DIRECT_VALUE_COLUMN_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalDirectContainerMapping) subject).getDirectValueColumn();
			}

			protected void setValueOnSubject(Object value) {
				((MWRelationalDirectContainerMapping) subject).setDirectValueColumn((MWColumn) value);
			}
		};
	}

	private static ListValueModel buildDirectValueColumnListHolder(ValueModel directContainerMappingHolder) {
		return new ExtendedListValueModelWrapper(
					new SortedListValueModelAdapter(
						buildDirectValueColumnCollectionHolder(directContainerMappingHolder)
					)
				);
	}
	
	private static CollectionValueModel buildDirectValueColumnCollectionHolder(ValueModel directContainerMappingHolder) {
		return new CollectionAspectAdapter(buildTargetTableHolder(directContainerMappingHolder), MWTable.COLUMNS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWTable) subject).columns();
			}
			public int size() {
				return ((MWTable) subject).columnsSize();
			}
		};
	}



	// ************** direct key field ******************

	//directContainerMappingHolder expects an MWRelationalDirectMapMapping
	static ListChooser buildDirectKeyColumnChooser(ValueModel directContainerMappingHolder, WorkbenchContextHolder contextHolder) {
		ListChooser listChooser = 
			new DefaultListChooser(
				buildDirectKeyColumnComboModel(directContainerMappingHolder), 
				contextHolder,
                buildColumnNodeSelector(contextHolder),
				buildDirectKeyColumnChooserDialogBuilder()
			);
		listChooser.setRenderer(buildColumnListRenderer(buildParentDescriptorHolder(directContainerMappingHolder), contextHolder.getWorkbenchContext().getApplicationContext().getResourceRepository()));
		return listChooser;
		
	}
	
	private static DefaultListChooserDialog.Builder buildDirectKeyColumnChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("DIRECT_KEY_FIELD_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("DIRECT_KEY_FIELD_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildColumnStringConverter());
		return builder;
	}

	private static ComboBoxModel buildDirectKeyColumnComboModel(ValueModel directContainerMappingHolder) {
		return new ComboBoxModelAdapter(
						buildDirectValueColumnListHolder(directContainerMappingHolder),
						buildDirectKeyColumnAdapter(directContainerMappingHolder));
	}

	private static PropertyValueModel buildDirectKeyColumnAdapter(ValueModel directContainerMappingHolder) {
		return new PropertyAspectAdapter(directContainerMappingHolder, MWRelationalDirectMapMapping.DIRECT_KEY_COLUMN_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalDirectMapMapping) subject).getDirectKeyColumn();
			}

			protected void setValueOnSubject(Object value) {
				((MWRelationalDirectMapMapping) subject).setDirectKeyColumn((MWColumn) value);
			}
		};
	}
}
