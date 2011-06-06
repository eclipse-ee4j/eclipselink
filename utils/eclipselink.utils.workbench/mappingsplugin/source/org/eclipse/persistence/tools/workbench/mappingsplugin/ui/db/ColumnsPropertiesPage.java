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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabaseType;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.CheckBoxTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.ComboBoxTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.SpinnerTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.TableCellEditorAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.NameTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;



public class ColumnsPropertiesPage extends ScrollablePropertiesPage {

	private ListValueModel databaseTypesHolder;
	private ListValueModel sortedColumnsAdapter;
	private TableModel tableModel;
	private ObjectListSelectionModel rowSelectionModel;
	private PropertyValueModel selectedColumnHolder;
	private Action removeAction;
	private Action renameAction;
	private JTable table;
	
	public ColumnsPropertiesPage(PropertyValueModel tableNodeHolder, WorkbenchContextHolder contextHolder) {
		super(tableNodeHolder, contextHolder);
	}

	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		this.databaseTypesHolder = buildDatabaseTypesListHolder();
		this.sortedColumnsAdapter = buildSortedColumnsAdapter();
		this.tableModel = buildTableModel();
		this.selectedColumnHolder = buildSelectedColumnHolder();
		this.rowSelectionModel = buildRowSelectionModel();
	}

	private ListValueModel buildDatabaseTypesListHolder() {	
		return new CollectionListValueModelAdapter(buildDatabaseTypesHolder());
	}
	
	private CollectionValueModel buildDatabaseTypesHolder() {
		return new CollectionAspectAdapter(this.buildDatabasePlatformAdapter(), DatabasePlatform.DATABASE_TYPES_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((DatabasePlatform) this.subject).databaseTypes();
			}
			protected int sizeFromSubject() {
				return ((DatabasePlatform) this.subject).databaseTypesSize();
			}
		};
	} 
	
	private PropertyValueModel buildDatabasePlatformAdapter() {
		return new PropertyAspectAdapter(this.buildDatabaseAdapter(), MWDatabase.DATABASE_PLATFORM_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDatabase) this.subject).getDatabasePlatform();
			}
		};
	}

	private ValueModel buildDatabaseAdapter() {
		return new TransformationPropertyValueModel(this.getSelectionHolder()) {
			protected Object transform(Object value) {
				return (value == null) ? null : ((MWTable) value).getDatabase();
			}
			protected Object reverseTransform(Object value) {
				throw new UnsupportedOperationException();
			}
		};
	}

	private ListValueModel buildSortedColumnsAdapter() {
		return new SortedListValueModelAdapter(this.buildColumnNameAdapter());
	}

	// the list will need to be re-sorted if a name changes
	private ListValueModel buildColumnNameAdapter() {
		return new ItemPropertyListValueModelAdapter(this.buildColumnsAdapter(), MWColumn.NAME_PROPERTY);
	}

	private CollectionValueModel buildColumnsAdapter() {
		return new CollectionAspectAdapter(getSelectionHolder(), MWTable.COLUMNS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWTable) this.subject).columns();
			}
			protected int sizeFromSubject() {
				return ((MWTable) this.subject).columnsSize();
			}
		};
	}

	private TableModel buildTableModel() {
		return new TableModelAdapter(this.sortedColumnsAdapter, this.buildColumnAdapter());
	}
	
	private ColumnAdapter buildColumnAdapter() {
		return new ColumnsColumnAdapter(resourceRepository());
	}
	
	private PropertyValueModel buildSelectedColumnHolder() {
		return new SimplePropertyValueModel(null);
	}

	private ObjectListSelectionModel buildRowSelectionModel() {
		ObjectListSelectionModel model = new ObjectListSelectionModel(new ListModelAdapter(this.sortedColumnsAdapter));
		model.addListSelectionListener(this.buildRowSelectionListener());
		return model;
	}

	private ListSelectionListener buildRowSelectionListener() {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					ColumnsPropertiesPage.this.rowSelectionChanged(e);
				}
			}
		};
	}

	void rowSelectionChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting())
			return;

		Object selection = this.rowSelectionModel.getSelectedValue();
		this.selectedColumnHolder.setValue(selection);
		boolean fieldSelected = (selection != null);
		this.removeAction.setEnabled(fieldSelected);
		this.renameAction.setEnabled(fieldSelected);
	}

	protected Component buildPage() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the button panel first
		JPanel buttonPanel = buildButtonPanel();
		constraints.gridx			= 1;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(5, 0, 5, 5);
		mainPanel.add(buttonPanel, constraints);
			
		this.table = this.buildTable();
		JScrollPane scrollPane = new JScrollPane(this.table);
		scrollPane.getViewport().setPreferredSize(new Dimension(50, 50));
		scrollPane.getViewport().setBackground(this.table.getBackground());
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(5, 5, 5, 5);
			
		mainPanel.add(scrollPane, constraints);

		addHelpTopicId(mainPanel, helpTopicId());
		return mainPanel;
	}
	
	private JTable buildTable() {
		JTable t = SwingComponentFactory.buildTable(this.tableModel, this.rowSelectionModel);
		t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		t.getTableHeader().setReorderingAllowed(false);

		SwingComponentFactory.attachTableEditorCanceler(t, getSelectionHolder());
		updateTableColumns(t);
		
		return t;
	}

	private void updateTableColumns(JTable t) {
		int rowHeight = 20;	// start with minimum of 20

		// name column
		TableColumn column = t.getColumnModel().getColumn(ColumnsColumnAdapter.NAME_COLUMN);
		column.setPreferredWidth(100);

		// database type column (combo-box)
		column = t.getColumnModel().getColumn(ColumnsColumnAdapter.TYPE_COLUMN);
		column.setPreferredWidth(100);
		ComboBoxTableCellRenderer typeRenderer = this.buildDatabaseTypeComboBoxRenderer();
		column.setCellRenderer(typeRenderer);
		column.setCellEditor(new TableCellEditorAdapter(this.buildDatabaseTypeComboBoxRenderer()));
		rowHeight = Math.max(rowHeight, typeRenderer.getPreferredHeight());

		// size column (spinner)
		column = t.getColumnModel().getColumn(ColumnsColumnAdapter.SIZE_COLUMN);
		SpinnerTableCellRenderer sizeRenderer = this.buildSizeRenderer();
		column.setCellRenderer(sizeRenderer);
		column.setCellEditor(this.buildSizeEditor());
		rowHeight = Math.max(rowHeight, sizeRenderer.getPreferredHeight());
		
		// sub-size column (spinner)
		column = t.getColumnModel().getColumn(ColumnsColumnAdapter.SUB_SIZE_COLUMN);
		SpinnerTableCellRenderer subSizeRenderer = this.buildSubSizeRenderer();
		column.setCellRenderer(subSizeRenderer);
		column.setCellEditor(this.buildSubSizeEditor());
		rowHeight = Math.max(rowHeight, subSizeRenderer.getPreferredHeight());
		
		// allows null column (check box)
		column = t.getColumnModel().getColumn(ColumnsColumnAdapter.ALLOWS_NULL_COLUMN);
		CheckBoxTableCellRenderer allowsNullRenderer = this.buildAllowsNullRenderer();
		column.setCellRenderer(allowsNullRenderer);
		column.setCellEditor(this.buildAllowsNullEditor());
		rowHeight = Math.max(rowHeight, allowsNullRenderer.getPreferredHeight());
		
		// unique column (check box)
		column = t.getColumnModel().getColumn(ColumnsColumnAdapter.UNIQUE_COLUMN);
		CheckBoxTableCellRenderer uniqueRenderer = new CheckBoxTableCellRenderer();
		column.setCellRenderer(uniqueRenderer);
		column.setCellEditor(new TableCellEditorAdapter(new CheckBoxTableCellRenderer()));
		rowHeight = Math.max(rowHeight, uniqueRenderer.getPreferredHeight());

		// primary key column (check box)
		column = t.getColumnModel().getColumn(ColumnsColumnAdapter.PRIMARY_KEY_COLUMN);
		CheckBoxTableCellRenderer primaryKeyRenderer = new CheckBoxTableCellRenderer();
		column.setCellRenderer(primaryKeyRenderer);
		column.setCellEditor(new TableCellEditorAdapter(new CheckBoxTableCellRenderer()));
		rowHeight = Math.max(rowHeight, primaryKeyRenderer.getPreferredHeight());

		// identity column (check box)
		column = t.getColumnModel().getColumn(ColumnsColumnAdapter.IDENTITY_COLUMN);
		CheckBoxTableCellRenderer identityRenderer = this.buildIdentityRenderer();
		column.setCellRenderer(identityRenderer);
		column.setCellEditor(this.buildIdentityEditor());
		rowHeight = Math.max(rowHeight, identityRenderer.getPreferredHeight());

		t.setRowHeight(rowHeight);
	}
	
	private SpinnerTableCellRenderer buildNumberSpinnerRenderer() {
		return new SpinnerTableCellRenderer(new NumberSpinnerModelAdapter(new SimplePropertyValueModel(), new Integer(0), null, new Integer(1), new Integer(0)));
	}
	
	private SpinnerTableCellRenderer buildSizeRenderer() {
		return new SpinnerTableCellRenderer(new NumberSpinnerModelAdapter(new SimplePropertyValueModel(), new Integer(0), null, new Integer(1), new Integer(0))) {
			public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean hasFocus, int row, int column) {
				if (ColumnsPropertiesPage.this.column(row).getDatabaseType().allowsSize()) {
					return super.getTableCellRendererComponent(t, value, selected, hasFocus, row, column);
				}
				return null;
			}
		};
	}
	
	private TableCellEditor buildSizeEditor() {
		return new TableCellEditorAdapter(this.buildNumberSpinnerRenderer()) {
			public Component getTableCellEditorComponent(JTable t, Object value, boolean selected, int row, int column) {
				if (ColumnsPropertiesPage.this.column(row).getDatabaseType().allowsSize()) {
					return super.getTableCellEditorComponent(t, value, selected, row, column);
				}
				return null;
			}
		};
	}
	
	private SpinnerTableCellRenderer buildSubSizeRenderer() {
		return new SpinnerTableCellRenderer(new NumberSpinnerModelAdapter(new SimplePropertyValueModel(), new Integer(0), null, new Integer(1), new Integer(0))) {
			public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean hasFocus, int row, int column) {
				if (ColumnsPropertiesPage.this.column(row).getDatabaseType().allowsSubSize()) {
					return super.getTableCellRendererComponent(t, value, selected, hasFocus, row, column);
				}
				return null;
			}
		};
	}
	
	private TableCellEditor buildSubSizeEditor() {
		return new TableCellEditorAdapter(this.buildNumberSpinnerRenderer()) {
			public Component getTableCellEditorComponent(JTable t, Object value, boolean selected, int row, int column) {
				if (ColumnsPropertiesPage.this.column(row).getDatabaseType().allowsSubSize()) {
					return super.getTableCellEditorComponent(t, value, selected, row, column);
				}
				return null;
			}
		};
	}
	
	private CheckBoxTableCellRenderer buildAllowsNullRenderer() {
		return new CheckBoxTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean hasFocus, int row, int column) {
				if (ColumnsPropertiesPage.this.column(row).getDatabaseType().allowsNull()) {
					return super.getTableCellRendererComponent(t, value, selected, hasFocus, row, column);
				}
				return null;
			}
		};
	}
	
	private TableCellEditor buildAllowsNullEditor() {
		return new TableCellEditorAdapter(new CheckBoxTableCellRenderer()) {
			public Component getTableCellEditorComponent(JTable t, Object value, boolean selected, int row, int column) {
				if (ColumnsPropertiesPage.this.column(row).getDatabaseType().allowsNull()) {
					return super.getTableCellEditorComponent(t, value, selected, row, column);
				}
				return null;
			}
		};
	}
	
	private CheckBoxTableCellRenderer buildIdentityRenderer() {
		return new CheckBoxTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean hasFocus, int row, int column) {
				if (ColumnsPropertiesPage.this.column(row).supportsIdentityClause()) {
					return super.getTableCellRendererComponent(t, value, selected, hasFocus, row, column);
				}
				return null;
			}
		};
	}
	
	private TableCellEditor buildIdentityEditor() {
		return new TableCellEditorAdapter(new CheckBoxTableCellRenderer()) {
			public Component getTableCellEditorComponent(JTable t, Object value, boolean selected, int row, int column) {
				if (ColumnsPropertiesPage.this.column(row).supportsIdentityClause()) {
					return super.getTableCellEditorComponent(t, value, selected, row, column);
				}
				return null;
			}
		};
	}
	
	private JPanel buildButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 5, 5));
		buttonPanel.add(this.buildAddButton());
		buttonPanel.add(this.buildRemoveButton());
		buttonPanel.add(this.buildRenameButton());
		return buttonPanel;
	}

	// ********** add **********

	private JButton buildAddButton() {
		return new JButton(this.buildAddAction());
	}

	private Action buildAddAction() {
		FrameworkAction action = new AbstractFrameworkAction(getApplicationContext()) {
			protected void initialize() {
				initializeText("ADD_DATABASE_FIELD_BUTTON_TEXT");
				initializeMnemonic("ADD_DATABASE_FIELD_BUTTON_TEXT");
			}
			
			public void actionPerformed(ActionEvent event) {
				ColumnsPropertiesPage.this.addColumn();
			}
		};
		action.setEnabled(true);
		return action;
	}

	void addColumn() {
		if (this.table.isEditing()) {
			this.table.getCellEditor().stopCellEditing();
		}

		String name = this.promptUserForColumnName();
		if (name != null) {
			this.setSelectedColumn(this.table().addColumn(name));
		}
	}


	// ********** remove **********

	private JButton buildRemoveButton() {
		return new JButton(this.buildRemoveAction());
	}

	private Action buildRemoveAction() {
		this.removeAction = new AbstractFrameworkAction(getApplicationContext()) {
			protected void initialize() {
				initializeText("REMOVE_DATABASE_FIELD_BUTTON_TEXT");
				initializeMnemonic("REMOVE_DATABASE_FIELD_BUTTON_TEXT");
			}
			
			public void actionPerformed(ActionEvent event) {
				ColumnsPropertiesPage.this.removeColumn();
			}
		};
		this.removeAction.setEnabled(false);
		return this.removeAction;
	}

	void removeColumn() {
		if (this.table.isEditing()) {
			this.table.getCellEditor().stopCellEditing();
		}

		int option = JOptionPane.showConfirmDialog(currentWindow(),
										resourceRepository().getString("removeField.message"),
										resourceRepository().getString("removeField.title"),
										JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE);
										
		if (option == JOptionPane.YES_OPTION) {
			this.table().removeColumns(selectedColumns());
		}
	}


	// ********** rename **********

	private JButton buildRenameButton() {
		return new JButton(this.buildRenameAction());
	}

	private Action buildRenameAction() {
		this.renameAction = new AbstractFrameworkAction(getApplicationContext()) {
			protected void initialize() {
				initializeText("RENAME_DATABASE_FIELD_BUTTON_TEXT");
				initializeMnemonic("RENAME_DATABASE_FIELD_BUTTON_TEXT");
			}
			
			public void actionPerformed(ActionEvent event) {
				ColumnsPropertiesPage.this.renameColumn();
			}
		};
		
		this.renameAction.setEnabled(false);
		return this.renameAction;
	}

	void renameColumn() {
        for (Iterator i = selectedColumns(); i.hasNext(); ) {
            MWColumn column = (MWColumn) i.next();
			String name = this.promptUserForColumnName(column.getName(), "RENAME_FIELD_DIALOG", "dialog.dbFieldRename");
			if (name != null) {
                column.setName(name);
				this.setSelectedColumn(column);
			}
		}
	}

	// ********** new name dialog **********

	private String promptUserForColumnName() {
		String originalName = NameTools.uniqueNameFor(resourceRepository().getString("NEW_FIELD_DEFAULT_NAME"), columnNames());
		return this.promptUserForColumnName(originalName, "ADD_NEW_FIELD_DIALOG", "dialog.dbFieldAdd");
	}

	private String promptUserForColumnName(String originalName, String messageKey, String helpTopicId) {
		NewNameDialog dialog = this.buildNewNameDialog(originalName, messageKey, helpTopicId);
		dialog.show();
		return (dialog.wasConfirmed()) ? dialog.getNewName() : null;
	}

	protected String helpTopicId() {
		return "table.columns";	
	}
	
	private NewNameDialog buildNewNameDialog(String originalName, String messageKey, String helpTopicId) {
		NewNameDialog.Builder builder = new NewNameDialog.Builder();
		builder.setExistingNames(this.columnNames());
		builder.setOriginalName(originalName);
		builder.setTextFieldDescription(resourceRepository().getString(messageKey + ".message"));
		builder.setTitle(resourceRepository().getString(messageKey + ".title"));
		builder.setHelpTopicId(helpTopicId);
		return builder.buildDialog(getWorkbenchContext());
	}

	private Iterator columnNames() {
		return new TransformationIterator(table().columns()) {
			protected Object transform(Object next) {
				return ((MWColumn) next).getName();
			}
		};
	}

	// ********** database type **********

	private ComboBoxTableCellRenderer buildDatabaseTypeComboBoxRenderer() {
		return new ComboBoxTableCellRenderer(this.buildDatabaseTypeComboBoxModel(), this.buildDatabaseTypeListCellRenderer());
	}

	private ComboBoxModel buildDatabaseTypeComboBoxModel() {
		return new ComboBoxModelAdapter(this.databaseTypesHolder, new SimplePropertyValueModel());
	}

	private ListCellRenderer buildDatabaseTypeListCellRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				return ((DatabaseType) value).getName();
			}
		};
	}
	

	// ********** queries **********

	private Iterator selectedColumns() {
		return CollectionTools.iterator(this.rowSelectionModel.getSelectedValues());
	}

	void setSelectedColumn(MWColumn field) {
		this.rowSelectionModel.setSelectedValue(field);
	}
	
	MWColumn column(int rowIndex) {
		return (MWColumn) this.sortedColumnsAdapter.getItem(rowIndex);
	}

	private MWTable table() {
		return (MWTable) getSelectionHolder().getValue();
	}
	
	
	// ********** classes **********

	public static class ColumnsColumnAdapter implements ColumnAdapter {
		
		private ResourceRepository resourceRepository;
		
		public static final int COLUMN_COUNT = 8;

		public static final int NAME_COLUMN = 0;
		public static final int TYPE_COLUMN = 1;
		public static final int SIZE_COLUMN = 2;
		public static final int SUB_SIZE_COLUMN = 3;
		public static final int ALLOWS_NULL_COLUMN = 4;
		public static final int UNIQUE_COLUMN = 5;
		public static final int PRIMARY_KEY_COLUMN = 6;
		public static final int IDENTITY_COLUMN = 7;

		private static final String[] COLUMN_NAME_KEYS = new String[] {
			"NAME_COLUMN_HEADER",
			"TYPE_COLUMN_HEADER",
			"SIZE_COLUMN_HEADER",
			"SUB_SIZE_COLUMN_HEADER",
			"ALLOWS_NULL_COLUMN_HEADER",
			"UNIQUE_COLUMN_HEADER",
			"PRIMARY_KEY_COLUMN_HEADER",
			"IDENTITY_COLUMN_HEADER"
		};

		protected ColumnsColumnAdapter(ResourceRepository repository) {
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
				case NAME_COLUMN:			return Object.class;
				case TYPE_COLUMN:			return Object.class;
				case SIZE_COLUMN:			return Integer.class;
				case SUB_SIZE_COLUMN:		return Integer.class;
				case ALLOWS_NULL_COLUMN:	return Boolean.class;
				case PRIMARY_KEY_COLUMN:	return Boolean.class;
				case UNIQUE_COLUMN:		return Boolean.class;
				default: 					return Object.class;
			}
		}

		public boolean isColumnEditable(int index) {
			return index != NAME_COLUMN;
		}

		public PropertyValueModel[] cellModels(Object subject) {
			MWColumn field = (MWColumn) subject;
			PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];

			result[NAME_COLUMN]			= this.buildNameAdapter(field);
			result[TYPE_COLUMN]			= this.buildTypeAdapter(field);
			result[SIZE_COLUMN]			= this.buildSizeAdapter(field);
			result[SUB_SIZE_COLUMN]		= this.buildSubSizeAdapter(field);
			result[ALLOWS_NULL_COLUMN]	= this.buildAllowsNullAdapter(field);
			result[PRIMARY_KEY_COLUMN]	= this.buildPrimaryKeyAdapter(field);
			result[IDENTITY_COLUMN]	= this.buildIdentityAdapter(field);
			result[UNIQUE_COLUMN]		= this.buildUniqueAdapter(field);

			return result;
		}

		private PropertyValueModel buildNameAdapter(MWColumn field) {
			return new PropertyAspectAdapter(MWColumn.NAME_PROPERTY, field) {
				protected Object getValueFromSubject() {
					return ((MWColumn) this.subject).getName();
				}
				protected void setValueOnSubject(Object value) {
					((MWColumn) this.subject).setName((String) value);
				}
			};
		}
		
		private PropertyValueModel buildTypeAdapter(MWColumn table) {
			return new PropertyAspectAdapter(MWColumn.DATABASE_TYPE_PROPERTY, table) {
				protected Object getValueFromSubject() {
					return ((MWColumn) this.subject).getDatabaseType();
				}
				protected void setValueOnSubject(Object value) {
					((MWColumn) this.subject).setDatabaseType((DatabaseType) value);
				}
			};
		}
		
		private PropertyValueModel buildSizeAdapter(MWColumn field) {
			return new PropertyAspectAdapter(MWColumn.SIZE_PROPERTY, field) {
				protected Object getValueFromSubject() {
					return new Integer(((MWColumn) this.subject).getSize());
				}
				protected void setValueOnSubject(Object value) {
					((MWColumn) this.subject).setSize(((Integer) value).intValue());
				}
			};
		}
		
		private PropertyValueModel buildSubSizeAdapter(MWColumn field) {
			return new PropertyAspectAdapter(MWColumn.SUB_SIZE_PROPERTY, field) {
				protected Object getValueFromSubject() {
					return new Integer(((MWColumn) this.subject).getSubSize());
				}
				protected void setValueOnSubject(Object value) {
					((MWColumn) this.subject).setSubSize(((Integer) value).intValue());
				}
			};
		}

		private PropertyValueModel buildAllowsNullAdapter(MWColumn field) {
			return new PropertyAspectAdapter(MWColumn.ALLOWS_NULL_PROPERTY, field) {
				protected Object getValueFromSubject() {
					return Boolean.valueOf(((MWColumn) this.subject).allowsNull());
				}
				protected void setValueOnSubject(Object value) {
					((MWColumn) this.subject).setAllowsNull(((Boolean) value).booleanValue());
				}
			};
		}
		
		private PropertyValueModel buildPrimaryKeyAdapter(MWColumn field) {
			return new PropertyAspectAdapter(MWColumn.PRIMARY_KEY_PROPERTY, field) {
				protected Object getValueFromSubject() {
					return Boolean.valueOf(((MWColumn) this.subject).isPrimaryKey());
				}
				protected void setValueOnSubject(Object value) {
					((MWColumn) this.subject).setPrimaryKey(((Boolean) value).booleanValue());
				}
			};
		}
		
		private PropertyValueModel buildIdentityAdapter(MWColumn field) {
			return new PropertyAspectAdapter(MWColumn.IDENTITY_PROPERTY, field) {
				protected Object getValueFromSubject() {
					return Boolean.valueOf(((MWColumn) this.subject).isIdentity());
				}
				protected void setValueOnSubject(Object value) {
					((MWColumn) this.subject).setIdentity(((Boolean) value).booleanValue());
				}
			};
		}
		
		private PropertyValueModel buildUniqueAdapter(MWColumn field) {
			return new PropertyAspectAdapter(MWColumn.UNIQUE_PROPERTY, field) {
				protected Object getValueFromSubject() {
					return Boolean.valueOf(((MWColumn) this.subject).isUnique());
				}
				protected void setValueOnSubject(Object value) {
					((MWColumn) this.subject).setUnique(((Boolean) value).booleanValue());
				}
			};
		}
	}
}
