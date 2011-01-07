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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.TableCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational.RelationalProjectComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.CheckBoxTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.ComboBoxTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.TableCellEditorAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;



public class ReferencesPropertiesPage extends ScrollablePropertiesPage {

	private ListValueModel targetTablesHolder;
	ListValueModel sortedReferencesAdapter;
	private TableModel tableModel;
	private ObjectListSelectionModel rowSelectionModel;
	private PropertyValueModel selectedReferenceHolder;
	private Action removeAction;
	private Action renameAction;
	JTable table;
	
	public ReferencesPropertiesPage(PropertyValueModel tableNodeHolder, WorkbenchContextHolder contextHolder) {
		super(tableNodeHolder, contextHolder);
	}

	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		this.targetTablesHolder = RelationalProjectComponentFactory.buildExtendedTablesHolder(getSelectionHolder());
		this.sortedReferencesAdapter = buildSortedReferencesAdapter();
		this.tableModel = buildTableModel();
		this.selectedReferenceHolder = buildSelectedReferenceHolder();
		this.rowSelectionModel = buildRowSelectionModel();
		
		getSelectionHolder().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (ReferencesPropertiesPage.this.table.isEditing()) {
					ReferencesPropertiesPage.this.table.getCellEditor().cancelCellEditing();
				}
				if (ReferencesPropertiesPage.this.sortedReferencesAdapter.size() > 0) {
					setSelectedReference((MWReference) ReferencesPropertiesPage.this.sortedReferencesAdapter.getItem(0));
				}
			}
		});
	}
	
	protected String helpTopicId() {
		return "table.references";	
	}
	
	private ListValueModel buildSortedReferencesAdapter() {
		return new SortedListValueModelAdapter(this.buildReferenceNameAdapter());
	}

	// the list will need to be re-sorted if a name changes
	private ListValueModel buildReferenceNameAdapter() {
		return new ItemPropertyListValueModelAdapter(this.buildReferencesAdapter(), MWColumn.NAME_PROPERTY);
	}

	private CollectionValueModel buildReferencesAdapter() {
		return new CollectionAspectAdapter(getSelectionHolder(), MWTable.REFERENCES_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWTable) this.subject).references();
			}
			protected int sizeFromSubject() {
				return ((MWTable) this.subject).referencesSize();
			}
		};
	}

	private TableModel buildTableModel() {
		return new TableModelAdapter(this.sortedReferencesAdapter, this.buildColumnAdapter());
	}
	
	private ColumnAdapter buildColumnAdapter() {
		return new ReferencesColumnAdapter(resourceRepository());
	}
	
	private PropertyValueModel buildSelectedReferenceHolder() {
		return new SimplePropertyValueModel(null);
	}

	private ObjectListSelectionModel buildRowSelectionModel() {
		ObjectListSelectionModel model = new ObjectListSelectionModel(new ListModelAdapter(this.sortedReferencesAdapter));
		model.addListSelectionListener(this.buildRowSelectionListener());
		model.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return model;
	}

	private ListSelectionListener buildRowSelectionListener() {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if ( ! e.getValueIsAdjusting()) {
					ReferencesPropertiesPage.this.rowSelectionChanged();
				}
			}
		};
	}

	void rowSelectionChanged() {
		Object selection = this.rowSelectionModel.getSelectedValue();
		this.selectedReferenceHolder.setValue(selection);
		boolean referenceSelected = (selection != null);
		this.removeAction.setEnabled(referenceSelected);
		this.renameAction.setEnabled(referenceSelected);
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
		constraints.insets		= new Insets(5, 5, 5, 5);
		mainPanel.add(buttonPanel, constraints);
			
		this.table = this.buildTable();
		JScrollPane scrollPane = new JScrollPane(this.table);
		scrollPane.getViewport().setPreferredSize(new Dimension(50,50));
		scrollPane.getViewport().setBackground(this.table.getBackground());
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(5, 5, 5, 0);
			
		mainPanel.add(scrollPane, constraints);

		ColumnPairsPanel fieldAssociationPage = buildFieldAssociationsPanel();
		addHelpTopicId(fieldAssociationPage, helpTopicId() + ".associations");
		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(5,5,5,5);
		mainPanel.add(fieldAssociationPage, constraints);
		addPaneForAlignment(fieldAssociationPage);

		addHelpTopicId(mainPanel, helpTopicId());
		return mainPanel;
	}
	
	private JTable buildTable() {
		JTable t = SwingComponentFactory.buildTable(this.tableModel, this.rowSelectionModel);
		t.getTableHeader().setReorderingAllowed(false);
		int rowHeight = 20;	// start with minimum of 20

		// target table column (combo-box)
		TableColumn column = t.getColumnModel().getColumn(ReferencesColumnAdapter.TARGET_TABLE_COLUMN);
		ComboBoxTableCellRenderer targetTableRenderer = this.buildTargetTableComboBoxRenderer();
		column.setCellRenderer(targetTableRenderer);
		column.setCellEditor(new TableCellEditorAdapter(this.buildTargetTableComboBoxRenderer()));
		rowHeight = Math.max(rowHeight, targetTableRenderer.getPreferredHeight());
		
		// on database column (check box)
		column = t.getColumnModel().getColumn(ReferencesColumnAdapter.ON_DATABASE_COLUMN);
		CheckBoxTableCellRenderer onDatabaseRenderer = new CheckBoxTableCellRenderer();
		column.setCellRenderer(onDatabaseRenderer);
		column.setCellEditor(new TableCellEditorAdapter(new CheckBoxTableCellRenderer()));
		rowHeight = Math.max(rowHeight, onDatabaseRenderer.getPreferredHeight());

		t.setRowHeight(rowHeight);
		return t;
	}

	private JPanel buildButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 0, 5));

		JButton button = this.buildAddButton();
		buttonPanel.add(button);
		addAlignRight(button);

		button = this.buildRemoveButton();
		buttonPanel.add(button);
		addAlignRight(button);

		button = this.buildRenameButton();
		buttonPanel.add(button);
		addAlignRight(button);

		return buttonPanel;
	}

	private ColumnPairsPanel buildFieldAssociationsPanel() {
		return new ColumnPairsPanel(getWorkbenchContextHolder(), this.selectedReferenceHolder);
	} 
	// ********** add **********

	private JButton buildAddButton() {
		return new JButton(this.buildAddAction());
	}

	private Action buildAddAction() {
		FrameworkAction action = new AbstractFrameworkAction(getApplicationContext()) {
			protected void initialize() {
				initializeText("ADD_REFERENCE_BUTTON_TEXT");
				initializeMnemonic("ADD_REFERENCE_BUTTON_TEXT");
			}
			
			public void actionPerformed(ActionEvent event) {
				ReferencesPropertiesPage.this.addReference();
			}
		};
		action.setEnabled(true);
		return action;
	}

	void addReference() {
		if (this.table.isEditing()) {
			this.table.getCellEditor().stopCellEditing();
		}

		List targetTables = new ArrayList(this.table().getDatabase().tablesSize());
		CollectionTools.addAll(targetTables, this.table().getDatabase().tables());
		CollectionTools.sort(targetTables);
		NewTableReferenceDialog dialog = 
			NewTableReferenceDialog.buildReferenceDialogDisallowSourceTableSelection(
					this.getWorkbenchContext(),
					Collections.singletonList(this.selection()),
					targetTables
			);

		dialog.setSourceTable(this.table());
		dialog.setTargetTable((MWTable) targetTables.get(0));		// there will always be at least one table in the list
		dialog.show();
		if (dialog.wasCanceled()) {
			return;
		}			
		MWReference reference = table().addReference(dialog.getReferenceName(), dialog.getTargetTable());
		reference.setOnDatabase(dialog.isOnDatabase());
		setSelectedReference(reference);
	}
	
	
	// ********** remove **********

	private JButton buildRemoveButton() {
		return new JButton(this.buildRemoveAction());
	}

	private Action buildRemoveAction() {
		this.removeAction = new AbstractFrameworkAction(getApplicationContext()) {
			protected void initialize() {
				initializeText("REMOVE_REFERENCE_BUTTON_TEXT");
				initializeMnemonic("REMOVE_REFERENCE_BUTTON_TEXT");
			}
			
			public void actionPerformed(ActionEvent event) {
				ReferencesPropertiesPage.this.removeReference();
			}
		};
		this.removeAction.setEnabled(false);
		return this.removeAction;
	}

	void removeReference() {
		if (this.table.isEditing()) {
			this.table.getCellEditor().stopCellEditing();
		}

		int option = JOptionPane.showConfirmDialog(getWorkbenchContext().getCurrentWindow(),
										resourceRepository().getString("REMOVE_REFERENCES_WARNING_DIALOG.message"),
										resourceRepository().getString("REMOVE_REFERENCES_WARNING_DIALOG.title"),
										JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE);
										
		if (option == JOptionPane.YES_OPTION) {
			MWReference reference = this.selectedReference();
			if (reference != null) {
				this.table().removeReference(reference);
			}
		}
	}


	// ********** rename **********

	private JButton buildRenameButton() {
		return new JButton(this.buildRenameAction());
	}

	private Action buildRenameAction() {
		this.renameAction = new AbstractFrameworkAction(getApplicationContext()) {
			protected void initialize() {
				initializeText("RENAME_REFERENCE_BUTTON_TEXT");
				initializeMnemonic("RENAME_REFERENCE_BUTTON_TEXT");
			}
			
			public void actionPerformed(ActionEvent event) {
				ReferencesPropertiesPage.this.renameReference();
			}
		};
		
		this.renameAction.setEnabled(false);
		return this.renameAction;
	}

	void renameReference() {
		MWReference reference = this.selectedReference();
		if (reference != null) {
			String name = this.getReferenceNameFromUser(reference.getName(), "RENAME_REFERENCE_DIALOG");
			if (name != null) {
				reference.setName(name);
				this.setSelectedReference(reference);
			}
		}
	}

	// ********** new name dialog **********


	private String getReferenceNameFromUser(String originalName, String messageKey) {
		NewNameDialog dialog = this.buildNewNameDialog(originalName, messageKey);
		dialog.show();
		return (dialog.wasConfirmed()) ? dialog.getNewName() : null;
	}

	private NewNameDialog buildNewNameDialog(String originalName, String messageKey) {
		NewNameDialog.Builder builder = new NewNameDialog.Builder();
		builder.setExistingNames(this.table().referenceNames());
		builder.setOriginalName(originalName);
		builder.setTextFieldDescription(resourceRepository().getString(messageKey + ".message"));
		builder.setTitle(resourceRepository().getString(messageKey + ".title"));
		builder.setHelpTopicId("dialog.referenceRename");
		return builder.buildDialog(getWorkbenchContext());
	}


	// **********target table (for cell editor) **********

	private ComboBoxTableCellRenderer buildTargetTableComboBoxRenderer() {
		return new ComboBoxTableCellRenderer(this.buildTargetTableComboBoxModel(), this.buildTargetTableListCellRenderer());
	}

	private ComboBoxModel buildTargetTableComboBoxModel() {
		return new ComboBoxModelAdapter(this.targetTablesHolder, new SimplePropertyValueModel());
	}

	private ListCellRenderer buildTargetTableListCellRenderer() {
		return new AdaptableListCellRenderer(new TableCellRendererAdapter(this.resourceRepository()));
	}
	

	// ********** queries **********

	private MWReference selectedReference() {
		if (this.rowSelectionModel.isSelectionEmpty()) {
			return null;
		}
		return (MWReference) this.rowSelectionModel.getSelectedValue();
	}

	void setSelectedReference(MWReference reference) {
		this.rowSelectionModel.setSelectedValue(reference);
	}

	private MWTable table() {
		return (MWTable) getSelectionHolder().getValue();
	}
	
	
	// ********** classes **********

	public static class ReferencesColumnAdapter implements ColumnAdapter {
		
		private ResourceRepository resourceRepository;
		
		public static final int COLUMN_COUNT = 3;

		public static final int REFERENCE_NAME_COLUMN = 0;
		public static final int TARGET_TABLE_COLUMN = 1;
		public static final int ON_DATABASE_COLUMN = 2;

		private static final String[] COLUMN_NAME_KEYS = new String[] {
			"REFERENCE_NAME_COLUMN_HEADER",
			"TARGET_TABLE_COLUMN_HEADER",
			"ON_DATABASE_COLUMN_HEADER",
		};

		protected ReferencesColumnAdapter(ResourceRepository repository) {
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
				case REFERENCE_NAME_COLUMN:	return Object.class;
				case TARGET_TABLE_COLUMN:		return Object.class;
				case ON_DATABASE_COLUMN:		return Boolean.class;
				default: 					return Object.class;
			}
		}

		public boolean isColumnEditable(int index) {
			return index != REFERENCE_NAME_COLUMN;
		}

		public PropertyValueModel[] cellModels(Object subject) {
			MWReference reference = (MWReference) subject;
			PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];

			result[REFERENCE_NAME_COLUMN]			= this.buildReferenceNameAdapter(reference);
			result[TARGET_TABLE_COLUMN]			= this.buildTargetTableAdapter(reference);
			result[ON_DATABASE_COLUMN]	= this.buildOnDatabaseAdapter(reference);

			return result;
		}

		private PropertyValueModel buildReferenceNameAdapter(MWReference reference) {
			return new PropertyAspectAdapter(MWReference.NAME_PROPERTY, reference) {
				protected Object getValueFromSubject() {
					return ((MWReference) this.subject).getName();
				}
				protected void setValueOnSubject(Object value) {
					((MWReference) this.subject).setName((String) value);
				}
			};
		}
		
		private PropertyValueModel buildTargetTableAdapter(MWReference reference) {
			PropertyValueModel propertyValueModel =  new PropertyAspectAdapter(MWReference.TARGET_TABLE_PROPERTY, reference) {
				protected Object getValueFromSubject() {
					return ((MWReference) this.subject).getTargetTable();
				}
				protected void setValueOnSubject(Object value) {
					((MWReference) this.subject).setTargetTable((MWTable) value);
				}
			};
			return new ValuePropertyPropertyValueModelAdapter(propertyValueModel, MWTable.QUALIFIED_NAME_PROPERTY);
		}
	
		private PropertyValueModel buildOnDatabaseAdapter(MWReference reference) {
			return new PropertyAspectAdapter(MWReference.ON_DATABASE_PROPERTY, reference) {
				protected Object getValueFromSubject() {
					return Boolean.valueOf(((MWReference) this.subject).isOnDatabase());
				}
				protected void setValueOnSubject(Object value) {
					((MWReference) this.subject).setOnDatabase(((Boolean) value).booleanValue());
				}
			};
		}
	}
}
