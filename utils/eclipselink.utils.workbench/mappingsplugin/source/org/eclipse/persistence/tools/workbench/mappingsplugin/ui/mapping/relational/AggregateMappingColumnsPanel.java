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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.AggregateFieldDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregatePathToColumn;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ColumnCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.ComboBoxTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.TableCellEditorAdapter;
import org.eclipse.persistence.tools.workbench.uitools.swing.CachingComboBoxModel;



final class AggregateMappingColumnsPanel extends ScrollablePropertiesPage {

	private ListValueModel sortedPathsToFieldsAdapter;
	private TableModel tableModel;
	private ObjectListSelectionModel rowSelectionModel;

	AggregateMappingColumnsPanel(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}


	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		this.sortedPathsToFieldsAdapter = buildSortedPathToFieldsAdapter();
		this.tableModel = buildTableModel();
		this.rowSelectionModel = buildRowSelectionModel();
	}

	private ListValueModel buildSortedPathToFieldsAdapter() {
		return new SortedListValueModelAdapter(this.buildPathsToFieldsAdapter());
	}


	private CollectionValueModel buildPathsToFieldsAdapter() {
		return new CollectionAspectAdapter(getSelectionHolder(), MWAggregateMapping.PATHS_TO_FIELDS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWAggregateMapping) this.subject).pathsToFields();
			}
			protected int sizeFromSubject() {
				return ((MWAggregateMapping) this.subject).pathsToFieldsSize();
			}
		};
	}


	private TableModel buildTableModel() {
		return new TableModelAdapter(this.sortedPathsToFieldsAdapter, this.buildColumnAdapter());
	}
	
	private ColumnAdapter buildColumnAdapter() {
		return new ColumnColumnAdapter(resourceRepository());
	}
	
	private ObjectListSelectionModel buildRowSelectionModel() {
		ObjectListSelectionModel result = new ObjectListSelectionModel(new ListModelAdapter(this.sortedPathsToFieldsAdapter));
		result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return result;
	}



	protected Component buildPage() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		JTable table = this.buildTable();
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.getViewport().setPreferredSize(new Dimension(50, 50));
		scrollPane.getViewport().setBackground(table.getBackground());
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 5, 5);
			
		mainPanel.add(scrollPane, constraints);

		addHelpTopicId(mainPanel, helpTopicId());
		
		return mainPanel;
	}


	private JTable buildTable() {
		JTable table = SwingComponentFactory.buildTable(this.tableModel, this.rowSelectionModel);

		updateTableColumns(table);
		SwingComponentFactory.attachTableEditorCanceler(table, getSelectionHolder());
		
		return table;
	}

	private void updateTableColumns(JTable table) {
		int rowHeight = 20;	// start with minimum of 20

		// name column
		TableColumn column = table.getColumnModel().getColumn(ColumnColumnAdapter.COLUMN_DESCRIPTION_COLUMN);
		column.setPreferredWidth(200);

		TableColumn fieldColumn = table.getColumnModel().getColumn(ColumnColumnAdapter.COLUMN_COLUMN);
		fieldColumn.setPreferredWidth(100);
		ComboBoxTableCellRenderer fieldRenderer = this.buildColumnsComboBoxRenderer();
		fieldColumn.setCellRenderer(fieldRenderer);
		fieldColumn.setCellEditor(new TableCellEditorAdapter(buildColumnsComboBoxRenderer()));
		rowHeight = Math.max(rowHeight, fieldRenderer.getPreferredHeight());

		table.setRowHeight(rowHeight);
	}


	// ********** database type **********

	private ComboBoxTableCellRenderer buildColumnsComboBoxRenderer() {
		return new ComboBoxTableCellRenderer(this.buildColumnsComboBoxModel(), this.buildColumnListCellRenderer());
	}

	private CachingComboBoxModel buildColumnsComboBoxModel() {
		return RelationalMappingComponentFactory.buildExtendedColumnComboBoxModel(new SimplePropertyValueModel(), buildParentDescriptorHolder());
	}
	
	private ListCellRenderer buildColumnListCellRenderer() {
		return new AdaptableListCellRenderer(new ColumnCellRendererAdapter(resourceRepository()));
	}
	

	private PropertyValueModel buildParentDescriptorHolder() {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWAggregateMapping) this.subject).getParentDescriptor();
			}
		};
	}
	
	protected String helpTopicId() {
		return "mapping.aggregate.fields";
	}
	
	// ********** classes **********

	public static class ColumnColumnAdapter implements ColumnAdapter {
		
		ResourceRepository resourceRepository;
		
		public static final int COLUMN_COUNT = 2;

		public static final int COLUMN_DESCRIPTION_COLUMN = 0;
		public static final int COLUMN_COLUMN = 1;

		private static final String[] COLUMN_NAME_KEYS = new String[] {
			"FIELD_DESCRIPTION_COLUMN_HEADER",
			"FIELDS_COLUMN_HEADER",
		};

		protected ColumnColumnAdapter(ResourceRepository repository) {
			super();
			this.resourceRepository = repository;
		}
		
		public int getColumnCount() {
			return COLUMN_COUNT;
		}

		public String getColumnName(int index) {
			return this.resourceRepository.getString(COLUMN_NAME_KEYS[index]);
		}

		public Class getColumnClass(int index) {
			switch (index) {
				case COLUMN_DESCRIPTION_COLUMN:		return Object.class;
				case COLUMN_COLUMN:				return Object.class;
				default: 							return Object.class;
			}
		}

		public boolean isColumnEditable(int index) {
			return index != COLUMN_DESCRIPTION_COLUMN;
		}

		public PropertyValueModel[] cellModels(Object subject) {
			MWAggregatePathToColumn pathToField = (MWAggregatePathToColumn) subject;
			PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];

			result[COLUMN_DESCRIPTION_COLUMN]	= this.buildColumnDescriptionAdapter(pathToField);
			result[COLUMN_COLUMN]				= this.buildColumnAdapter(pathToField);

			return result;
		}

		private PropertyValueModel buildColumnDescriptionAdapter(MWAggregatePathToColumn pathToField) {
			// TODO apparently this property never changes - it's never fired in an event...
			return new PropertyAspectAdapter(MWAggregatePathToColumn.AGGREGATE_RUNTIME_FIELD_NAME_GENERATOR_PROPERTY, pathToField) {
				protected Object getValueFromSubject() {
					MWAggregatePathToColumn ptf = (MWAggregatePathToColumn) this.subject;
					AggregateFieldDescription fieldDescription = ptf.getAggregateRuntimeFieldNameGenerator().fullFieldDescription();
					return ptf.getPathDescription() + ColumnColumnAdapter.this.resourceRepository.getString(fieldDescription.getMessageKey(), fieldDescription.getMessageArguments());
				}
			};
		}
		
		private PropertyValueModel buildColumnAdapter(MWAggregatePathToColumn pathToField) {
			PropertyValueModel adapter = new PropertyAspectAdapter(MWAggregatePathToColumn.COLUMN_PROPERTY, pathToField) {
				protected Object getValueFromSubject() {
					return ((MWAggregatePathToColumn) this.subject).getColumn();
				}
				protected void setValueOnSubject(Object value) {
					((MWAggregatePathToColumn) this.subject).setColumn((MWColumn) value);
				}
			};
			return new ValuePropertyPropertyValueModelAdapter(adapter, MWColumn.QUALIFIED_NAME_PROPERTY, MWColumn.DATABASE_TYPE_PROPERTY);
		}
	
	}
}
