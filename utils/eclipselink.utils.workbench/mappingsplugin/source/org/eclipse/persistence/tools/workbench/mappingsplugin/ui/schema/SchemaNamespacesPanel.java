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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;
import java.util.ListIterator;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamespace;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.CheckBoxTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.TableCellEditorAdapter;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementListIterator;


final class SchemaNamespacesPanel
	extends AbstractPropertiesPage
{
	SchemaNamespacesPanel(PropertyValueModel schemaNodeHolder, WorkbenchContextHolder contextHolder) {
		super(schemaNodeHolder, contextHolder);
	}
	
	protected void initializeLayout() {
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 0, 5);
		this.add(this.buildBuiltInNamespacesPanel(), constraints);
		
		constraints.gridx		= 1;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(10, 5, 0, 5);
		this.add(this.buildTargetNamespacePanel(), constraints);
		
		constraints.gridx		= 1;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(10, 5, 5, 5);
		this.add(this.buildImportedNamespacesPanel(), constraints);
	}
	
	private JPanel buildBuiltInNamespacesPanel() {
		return this.buildNamespacePanel(this.buildBuiltInNamespacesTableLabel(), this.buildBuiltInNamespacesTable());
	}
	
	private JLabel buildBuiltInNamespacesTableLabel() {
		return buildLabel("BUILT_IN_NAMESPACES_TABLE_LABEL");
	}
	
	private JTable buildBuiltInNamespacesTable() {
		JTable table = SwingComponentFactory.buildTable(this.buildBuiltInNamespacesTableModel());
		table.getTableHeader().setReorderingAllowed(false);
		return table;
	}
	
	private TableModelAdapter buildBuiltInNamespacesTableModel() {
		return new TableModelAdapter(this.buildBuiltInNamespacesAdapter(), this.buildColumnAdapter());
	}
	
	private ListValueModel buildBuiltInNamespacesAdapter() {
		return new ListAspectAdapter(this.getSelectionHolder()) {
			protected ListIterator getValueFromSubject() {
				return ((MWXmlSchema) this.subject).builtInNamespaces();
			}
		};
	}
	
	private JPanel buildTargetNamespacePanel() {
		return this.buildNamespacePanel(this.buildTargetNamespaceTableLabel(), this.buildTargetNamespaceTable());
	}
	
	private JLabel buildTargetNamespaceTableLabel() {
		return buildLabel("TARGET_NAMESPACE_TABLE_LABEL");
	}
	
	private JTable buildTargetNamespaceTable() {
		JTable table = SwingComponentFactory.buildTable(this.buildTargetNamespaceTableModel());
		table.getTableHeader().setReorderingAllowed(false);
		return table;
	}
	
	private TableModelAdapter buildTargetNamespaceTableModel() {
		return new TableModelAdapter(this.buildTargetNamespaceAdapter(), this.buildColumnAdapter()) {
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				// prefix column should not be editable if the namespace is absent ("")
				if (columnIndex == NamespaceColumnAdapter.PREFIX_COLUMN
					&& "".equals(SchemaNamespacesPanel.this.schema().targetNamespace().getNamespaceUrl())
				) {
					return false;
				}
				else {
					return super.isCellEditable(rowIndex, columnIndex);
				}
			}
		};
	}
	
	private ListValueModel buildTargetNamespaceAdapter() {
		return new ListAspectAdapter(this.getSelectionHolder()) {
			protected ListIterator getValueFromSubject() {
				return new SingleElementListIterator(((MWXmlSchema) this.subject).targetNamespace());
			}
			protected int sizeFromSubject() {
				return 1;
			}
		};
	}
	
	private JPanel buildImportedNamespacesPanel() {
		return this.buildNamespacePanel(this.buildImportedNamespacesTableLabel(), this.buildImportedNamespacesTable());
	}
	
	private JLabel buildImportedNamespacesTableLabel() {
		return buildLabel("IMPORTED_NAMESPACES_TABLE_LABEL");
	}
	
	private JTable buildImportedNamespacesTable() {
		JTable table = SwingComponentFactory.buildTable(this.buildImportedNamespacesTableModel());
		table.getTableHeader().setReorderingAllowed(false);
		return table;
	}
	
	private TableModelAdapter buildImportedNamespacesTableModel() {
		return new TableModelAdapter(this.buildSortedImportedNamespacesAdapter(), this.buildColumnAdapter());
	}
	
	private ListValueModel buildSortedImportedNamespacesAdapter() {
		return new SortedListValueModelAdapter(this.buildUpdatingImportedNamespacesAdapter(), MWNamespace.COMPARATOR);
	}
	
	private ListValueModel buildUpdatingImportedNamespacesAdapter() {
		return new ItemPropertyListValueModelAdapter(this.buildImportedNamespacesAdapter(), MWNamespace.NAMESPACE_URL_PROPERTY);
	}
	
	private CollectionValueModel buildImportedNamespacesAdapter() {
		return new CollectionAspectAdapter(this.getSelectionHolder(), MWXmlSchema.NAMESPACES_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWXmlSchema) this.subject).importedNamespaces();
			}
		};
	}
	
	private JPanel buildNamespacePanel(JLabel label, JTable table) {
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		label.setLabelFor(table);
		JComponent tablePanel = this.buildTablePanel(table);
		
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		panel.add(label, constraints);
		
		constraints.gridx		= 1;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(1, 0, 0, 0);
		panel.add(tablePanel, constraints);
		
		return panel;
	}
	
	private ColumnAdapter buildColumnAdapter() {
		return new NamespaceColumnAdapter(this.resourceRepository());
	}
	
	private JComponent buildTablePanel(JTable table) {
		TableColumn urlColumn = table.getColumnModel().getColumn(NamespaceColumnAdapter.URL_COLUMN);
		urlColumn.setPreferredWidth(200);
		
		TableColumn prefixColumn = table.getColumnModel().getColumn(NamespaceColumnAdapter.PREFIX_COLUMN);
		prefixColumn.setPreferredWidth(50);
		
		TableColumn declaredColumn = table.getColumnModel().getColumn(NamespaceColumnAdapter.DECLARED_COLUMN);
		declaredColumn.setPreferredWidth(50);
		CheckBoxTableCellRenderer declaredRenderer = this.buildDeclaredRenderer();
		declaredColumn.setCellRenderer(declaredRenderer);
		declaredColumn.setCellEditor(this.buildDeclaredEditor(declaredRenderer));
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		panel.add(table, BorderLayout.CENTER);

		return new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
	
	private CheckBoxTableCellRenderer buildDeclaredRenderer() {
		return new CheckBoxTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
				MWNamespace namespace = (MWNamespace) ((TableModelAdapter) table.getModel()).getModel().getItem(row);
				
				if (! namespace.getNamespaceUrl().equals("")) {
					return super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);
				}
				else {
					return null;
				}
			}
		};
	}
	
	private TableCellEditor buildDeclaredEditor(CheckBoxTableCellRenderer declaredRenderer) {
		return new TableCellEditorAdapter(declaredRenderer) {
			public Component getTableCellEditorComponent(JTable table, Object value, boolean selected, int row, int column) {
				MWNamespace namespace = (MWNamespace) ((TableModelAdapter) table.getModel()).getModel().getItem(row);
				
				if (! namespace.getNamespaceUrl().equals("")) {
					return super.getTableCellEditorComponent(table, value, selected, row, column);
				}
				else {
					return null;
				}
			}
		};
	}
	
	
	// **************** Convenience *******************************************
	
	private MWXmlSchema schema() {
		return (MWXmlSchema) this.selection();
	}
	
	
	// **************** Member classes ****************************************
	
	private static class NamespaceColumnAdapter
		implements ColumnAdapter
	{
		private ResourceRepository resourceRepository;
		private static final int COLUMN_COUNT 		= 3;
		
		private static final int URL_COLUMN 		= 0;
		private static final int PREFIX_COLUMN 		= 1;
		private static final int DECLARED_COLUMN	= 2;
		
		NamespaceColumnAdapter(ResourceRepository resourceRepository) {
			super();
			this.resourceRepository = resourceRepository;
		}
		
		public int getColumnCount() {
			return COLUMN_COUNT;
		}
		
		public String getColumnName(int index) {
			switch (index) {
				case URL_COLUMN: 
					return this.resourceRepository.getString("URL_COLUMN_LABEL");
					
				case PREFIX_COLUMN: 
					return this.resourceRepository.getString("PREFIX_COLUMN_LABEL");
				
				case DECLARED_COLUMN:
					return this.resourceRepository.getString("DECLARED_COLUMN_LABEL");
			}
			
			return "";
		}
		
		public Class getColumnClass(int index) {
			switch (index) {
				case URL_COLUMN:
					return String.class;
				
				case PREFIX_COLUMN:
					return String.class;
				
				case DECLARED_COLUMN:
					return Boolean.class;
			}
			
			return null;
		}
		
		public boolean isColumnEditable(int index) {
			switch (index) {
				case URL_COLUMN:
					return false;
				
				case PREFIX_COLUMN:
					return true;
				
				case DECLARED_COLUMN:
					return true;
			}
			
			return false;
		}
		
		public PropertyValueModel[] cellModels(Object subject) {
			MWNamespace namespace = (MWNamespace) subject;
			PropertyValueModel[] cellModels = new PropertyValueModel[COLUMN_COUNT];
			
			cellModels[URL_COLUMN] = this.buildUrlAdapter(namespace);
			cellModels[PREFIX_COLUMN] = this.buildPrefixAdapter(namespace);
			cellModels[DECLARED_COLUMN] = this.buildDeclaredAdapter(namespace);
			
			return cellModels;
		}
		
		private PropertyValueModel buildUrlAdapter(MWNamespace namespace) {
			return new PropertyAspectAdapter(MWNamespace.NAMESPACE_URL_PROPERTY, namespace) {
				protected Object getValueFromSubject() {
					return ((MWNamespace) this.subject).getNamespaceUrlForDisplay();
				}
			};
		}
		
		private PropertyValueModel buildPrefixAdapter(MWNamespace namespace) {
			return new PropertyAspectAdapter(MWNamespace.NAMESPACE_PREFIX_PROPERTY, namespace) {
				protected Object getValueFromSubject() {
					return ((MWNamespace) this.subject).getNamespacePrefix();
				}
				
				protected void setValueOnSubject(Object value) {
					((MWNamespace) this.subject).setNamespacePrefixFromUser((String) value);
				}
			};
		}
		
		private PropertyValueModel buildDeclaredAdapter(MWNamespace namespace) {
			return new PropertyAspectAdapter(MWNamespace.DECLARED_PROPERTY, namespace) {
				protected Object getValueFromSubject() {
					return new Boolean(((MWNamespace) this.subject).isDeclared());
				}
				
				protected void setValueOnSubject(Object value) {
					((MWNamespace) this.subject).setDeclared(((Boolean) value).booleanValue());
				}
			};
		}
	}	
}
