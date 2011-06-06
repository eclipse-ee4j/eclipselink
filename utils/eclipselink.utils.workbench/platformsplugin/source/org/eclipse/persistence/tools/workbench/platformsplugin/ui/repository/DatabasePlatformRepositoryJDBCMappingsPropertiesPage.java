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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.repository;

import java.awt.Component;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCType;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCTypeRepository;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCTypeToJavaTypeDeclarationMapping;
import org.eclipse.persistence.tools.workbench.platformsmodel.JavaTypeDeclaration;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.swing.EmptyIcon;


/**
 * display the JDBC mappings: JDBC type --> Java type;
 * for now, these are display-only
 */
final class DatabasePlatformRepositoryJDBCMappingsPropertiesPage extends ScrollablePropertiesPage {
	private PropertyValueModel repositoryHolder;
	private TableModel tableModel;

	static final Icon EMPTY_ICON = new EmptyIcon(5);


	public DatabasePlatformRepositoryJDBCMappingsPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}

	protected void initialize(PropertyValueModel selectionNodeHolder) {
		super.initialize(selectionNodeHolder);
		this.repositoryHolder = this.buildRepositoryHolder();
		this.tableModel = this.buildTableModel();
	}

	protected PropertyValueModel buildRepositoryHolder() {
		return new TransformationPropertyValueModel(this.getSelectionHolder()) {
			protected Object transform(Object value) {
				return (value == null) ? null : ((DatabasePlatformRepository) value).getJDBCTypeRepository();
			}
			protected Object reverseTransform(Object value) {
				throw new UnsupportedOperationException();
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
		return new CollectionAspectAdapter(this.repositoryHolder, JDBCTypeRepository.JDBC_TYPE_TO_JAVA_TYPE_DECLARATION_MAPPINGS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((JDBCTypeRepository) this.subject).jdbcTypeToJavaTypeDeclarationMappings();
			}
			protected int sizeFromSubject() {
				return ((JDBCTypeRepository) this.subject).jdbcTypeToJavaTypeDeclarationMappingsSize();
			}
		};
	}

	private ColumnAdapter buildColumnAdapter() {
		return new MappingColumnAdapter(this.getApplicationContext());
	}

	protected Component buildPage() {
		JTable table = SwingComponentFactory.buildTable(this.tableModel);

		TableColumn jdbcTypeColumn = table.getColumnModel().getColumn(MappingColumnAdapter.JDBC_TYPE_COLUMN);
		jdbcTypeColumn.setCellRenderer(this.buildJDBCTypeRenderer());

		TableColumn javaTypeColumn = table.getColumnModel().getColumn(MappingColumnAdapter.JAVA_TYPE_COLUMN);
		javaTypeColumn.setCellRenderer(this.buildJavaTypeRenderer());

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

	private TableCellRenderer buildJavaTypeRenderer() {
		return new AdaptableTableCellRenderer(new AbstractCellRendererAdapter() {
			public String buildText(Object value) {
				return ((JavaTypeDeclaration) value).displayString();
			}
			public Icon buildIcon(Object value) {
				return EMPTY_ICON;
			}
		});
	}


	// ********** member class **********

	private static class MappingColumnAdapter implements ColumnAdapter {
		private ApplicationContext context;

		public static final int COLUMN_COUNT = 2;
	
		public static final int JDBC_TYPE_COLUMN = 0;
		public static final int JAVA_TYPE_COLUMN = 1;
	
		private static final String[] COLUMN_NAMES = new String[] {
			"DATABASE_PLATFORM_REPOSITORY_JDBC_TYPE_COLUMN",
			"DATABASE_PLATFORM_REPOSITORY_JAVA_TYPE_COLUMN"
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
			return false;
		}
	
		public PropertyValueModel[] cellModels(Object subject) {
			JDBCTypeToJavaTypeDeclarationMapping mapping = (JDBCTypeToJavaTypeDeclarationMapping) subject;
			PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];
	
			result[JDBC_TYPE_COLUMN] = this.buildJDBCTypeAdapter(mapping);
			result[JAVA_TYPE_COLUMN] = this.buildJavaTypeAdapter(mapping);
	
			return result;
		}
	
		private PropertyValueModel buildJDBCTypeAdapter(JDBCTypeToJavaTypeDeclarationMapping mapping) {
			PropertyValueModel adapter = new PropertyAspectAdapter(EMPTY_STRING_ARRAY, mapping) {		// the jdbc type cannot change
				protected Object getValueFromSubject() {
					return ((JDBCTypeToJavaTypeDeclarationMapping) this.subject).getJDBCType();
				}
				protected void setValueOnSubject(Object value) {
					throw new UnsupportedOperationException();
				}
			};
			return new ValuePropertyPropertyValueModelAdapter(adapter, JDBCType.NAME_PROPERTY);
		}
	
		private PropertyValueModel buildJavaTypeAdapter(JDBCTypeToJavaTypeDeclarationMapping mapping) {
			// JavaTypeDeclarations are immutable
			return new PropertyAspectAdapter(JDBCTypeToJavaTypeDeclarationMapping.JAVA_TYPE_DECLARATION_PROPERTY, mapping) {
				protected Object getValueFromSubject() {
					return ((JDBCTypeToJavaTypeDeclarationMapping) this.subject).getJavaTypeDeclaration();
				}
				protected void setValueOnSubject(Object value) {
					throw new UnsupportedOperationException();
				}
			};
		}
	
	}

}
