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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.platform;

import java.awt.Component;
import java.util.Iterator;

import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabaseType;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCType;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCTypeToDatabaseTypeMapping;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.ComboBoxTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.TableCellEditorAdapter;
import org.eclipse.persistence.tools.workbench.uitools.swing.EmptyIcon;


/**
 * this is the JDBC Mappings tab on the
 * database platform tabbed properties page;
 * nothing special...
 */
final class DatabasePlatformJDBCPropertiesPage extends ScrollablePropertiesPage {
	private ListValueModel databaseTypesHolder;
	private TableModel tableModel;

	static final Icon EMPTY_ICON = new EmptyIcon(5);


	public DatabasePlatformJDBCPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}

	protected void initialize(PropertyValueModel selectionNodeHolder) {
		super.initialize(selectionNodeHolder);
		this.databaseTypesHolder = this.buildSortedDatabaseTypesAdapter();
		this.tableModel = this.buildTableModel();
	}

	private ListValueModel buildSortedDatabaseTypesAdapter() {
		return new SortedListValueModelAdapter(this.buildDatabaseTypesAdapter());
	}

	private CollectionValueModel buildDatabaseTypesAdapter() {
		return new CollectionAspectAdapter(this.getSelectionHolder(), DatabasePlatform.DATABASE_TYPES_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((DatabasePlatform) this.subject).databaseTypes();
			}
			protected int sizeFromSubject() {
				return ((DatabasePlatform) this.subject).databaseTypesSize();
			}
		};
	}

	private TableModel buildTableModel() {
		return new TableModelAdapter(this.buildSortedMappingsAdapter(), this.buildColumnAdapter());
	}

	private ListValueModel buildSortedMappingsAdapter() {
		return new SortedListValueModelAdapter(this.buildMappingsAdapter());
	}

	private CollectionValueModel buildMappingsAdapter() {
		return new CollectionAspectAdapter(this.getSelectionHolder(), DatabasePlatform.JDBC_TYPE_TO_DATABASE_TYPE_MAPPINGS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((DatabasePlatform) this.subject).jdbcTypeToDatabaseTypeMappings();
			}
			protected int sizeFromSubject() {
				return ((DatabasePlatform) this.subject).jdbcTypeToDatabaseTypeMappingsSize();
			}
		};
	}

	private ColumnAdapter buildColumnAdapter() {
		return new MappingColumnAdapter(this.getApplicationContext());
	}

	protected Component buildPage() {
		return new JScrollPane(this.buildTable());
	}

	private JTable buildTable() {
		JTable table = SwingComponentFactory.buildTable(this.tableModel);
		int rowHeight = 20;	// start with minimum of 20

		TableColumn jdbcTypeColumn = table.getColumnModel().getColumn(MappingColumnAdapter.JDBC_TYPE_COLUMN);
		jdbcTypeColumn.setCellRenderer(this.buildJDBCTypeRenderer());

		// database type column (combo-box)
		// the jdk combo-box renderer looks like a text field
		// until the user starts an edit - use a custom one
		TableColumn dbTypeColumn = table.getColumnModel().getColumn(MappingColumnAdapter.DATABASE_TYPE_COLUMN);
		ComboBoxTableCellRenderer dbTypeRenderer = this.buildDatabaseTypeComboBoxRenderer();
		dbTypeColumn.setCellRenderer(dbTypeRenderer);
		dbTypeColumn.setCellEditor(new TableCellEditorAdapter(this.buildDatabaseTypeComboBoxRenderer()));
		rowHeight = Math.max(rowHeight, dbTypeRenderer.getPreferredHeight());

		table.setRowHeight(rowHeight);
		return table;
	}

	private TableCellRenderer buildJDBCTypeRenderer() {
		return new AdaptableTableCellRenderer(new AbstractCellRendererAdapter() {
			public String buildText(Object value) {
				return ((JDBCType) value).displayString();
			}
			public Icon buildIcon(Object value) {
				return EMPTY_ICON;
			}
		});
	}

	private ComboBoxTableCellRenderer buildDatabaseTypeComboBoxRenderer() {
		return new ComboBoxTableCellRenderer(this.buildReadOnlyDatabaseTypeComboBoxModel(), this.buildDatabaseTypeRenderer());
	}

	private ComboBoxModel buildReadOnlyDatabaseTypeComboBoxModel() {
		return new ComboBoxModelAdapter(this.databaseTypesHolder, new SimplePropertyValueModel());
	}

	private ListCellRenderer buildDatabaseTypeRenderer() {
		return new AdaptableListCellRenderer(new AbstractCellRendererAdapter() {
			public String buildText(Object value) {
				return (value == null) ? null : ((DatabaseType) value).displayString();
			}
			public Icon buildIcon(Object value) {
				return EMPTY_ICON;
			}
		});
	}


	// ********** member classes **********

	private static class MappingColumnAdapter implements ColumnAdapter {
		private ApplicationContext context;

		public static final int COLUMN_COUNT = 2;
	
		public static final int JDBC_TYPE_COLUMN = 0;
		public static final int DATABASE_TYPE_COLUMN = 1;
	
		private static final String[] COLUMN_NAMES = new String[] {
			"DATABASE_PLATFORM_JDBC_TYPE_COLUMN",
			"DATABASE_PLATFORM_DATABASE_TYPE_COLUMN"
		};
	
		private static final String[] EMPTY_STRING_ARRAY = new String[0];

		MappingColumnAdapter(ApplicationContext context) {
			super();
			this.context = context;
		}

		public int getColumnCount() {
			return COLUMN_COUNT;
		}
	
		public String getColumnName(int index) {
			return this.context.getResourceRepository().getString(COLUMN_NAMES[index]);
		}
	
		public Class getColumnClass(int index) {
			return Object.class;
		}
	
		public boolean isColumnEditable(int index) {
			return index != JDBC_TYPE_COLUMN;
		}
	
		public PropertyValueModel[] cellModels(Object subject) {
			JDBCTypeToDatabaseTypeMapping mapping = (JDBCTypeToDatabaseTypeMapping) subject;
			PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];
	
			result[JDBC_TYPE_COLUMN]			= this.buildJDBCTypeAdapter(mapping);
			result[DATABASE_TYPE_COLUMN]	= this.buildDatabaseTypeAdapter(mapping);
	
			return result;
		}
	
		private PropertyValueModel buildJDBCTypeAdapter(JDBCTypeToDatabaseTypeMapping mapping) {
			PropertyValueModel adapter = new PropertyAspectAdapter(EMPTY_STRING_ARRAY, mapping) {		// the jdbc type cannot change
				protected Object getValueFromSubject() {
					return ((JDBCTypeToDatabaseTypeMapping) this.subject).getJDBCType();
				}
				protected void setValueOnSubject(Object value) {
					throw new UnsupportedOperationException();
				}
			};
			return new ValuePropertyPropertyValueModelAdapter(adapter, JDBCType.NAME_PROPERTY);
		}
	
		private PropertyValueModel buildDatabaseTypeAdapter(JDBCTypeToDatabaseTypeMapping mapping) {
			PropertyValueModel adapter = new PropertyAspectAdapter(JDBCTypeToDatabaseTypeMapping.DATABASE_TYPE_PROPERTY, mapping) {
				protected Object getValueFromSubject() {
					return ((JDBCTypeToDatabaseTypeMapping) this.subject).getDatabaseType();
				}
				protected void setValueOnSubject(Object value) {
					((JDBCTypeToDatabaseTypeMapping) this.subject).setDatabaseType((DatabaseType) value);
				}
			};
			return new ValuePropertyPropertyValueModelAdapter(adapter, DatabaseType.NAME_PROPERTY);
		}
	
	}

}
