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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
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
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
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
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.ComboBoxTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.TableCellEditorAdapter;



public class ColumnPairsPanel extends AbstractPanel {

	private PropertyValueModel referenceHolder;

	private ListValueModel sourceColumnsHolder;
	private ListValueModel targetColumnsHolder;
	private TableModel tableModel;
	private ObjectListSelectionModel rowSelectionModel;
	private PropertyValueModel selectedColumnPairHolder;
	private CollectionValueModel sortedColumnPairsAdapter;
	private Collection componentsForEnablement;
	
	private Action removeAction;
	private JTable table;

	public ColumnPairsPanel(WorkbenchContextHolder contextHolder, PropertyValueModel referenceHolder) {
		super(contextHolder);
		initialize(referenceHolder);
		initializeLayout();
	}
	
	private void initialize(PropertyValueModel referenceHolder) {
		this.referenceHolder = referenceHolder;
		this.componentsForEnablement = new Vector();
		this.sourceColumnsHolder = buildSortedColumnsHolder(buildSourceTableHolder());
		this.targetColumnsHolder = buildSortedColumnsHolder(buildTargetTableHolder());
		
		this.sortedColumnPairsAdapter = buildColumnPairsAdapter();
		this.tableModel = buildTableModel();
		this.selectedColumnPairHolder = buildSelectedColumnPairHolder();
		this.rowSelectionModel = buildRowSelectionModel();
	}
		
	private ListValueModel buildSortedColumnsHolder(PropertyValueModel tableHolder) {
		return new SortedListValueModelAdapter(buildColumnsAdapter(tableHolder));
	}
	
	private CollectionValueModel buildColumnsAdapter(PropertyValueModel tableHolder)  {
		return new CollectionAspectAdapter(tableHolder, MWTable.COLUMNS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWTable) this.subject).columns();
			}
			protected int sizeFromSubject() {
				return ((MWTable) this.subject).columnsSize();
			}
		};
	} 
		
	private PropertyValueModel buildSourceTableHolder() {
		return new PropertyAspectAdapter(this.referenceHolder) {
			protected Object getValueFromSubject() {
				return ((MWReference) this.subject).getSourceTable();
			}
		};
	}
	
	private PropertyValueModel buildTargetTableHolder() {
		return new PropertyAspectAdapter(this.referenceHolder, MWReference.TARGET_TABLE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWReference) this.subject).getTargetTable();
			}
		};
	}

	private CollectionValueModel buildColumnPairsAdapter() {
		return new CollectionAspectAdapter(referenceHolder, MWReference.COLUMN_PAIRS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWReference) this.subject).columnPairs();
			}
			protected int sizeFromSubject() {
				return ((MWReference) this.subject).columnPairsSize();
			}
		};
	}


	private TableModel buildTableModel() {
		return new TableModelAdapter(sortedColumnPairsAdapter, this.buildColumnAdapter());
	}
	
	protected ColumnAdapter buildColumnAdapter() {
		return new ColumnPairsColumnAdapter(resourceRepository());
	}

	private PropertyValueModel buildSelectedColumnPairHolder() {
		return new SimplePropertyValueModel(null);
	}

	private ObjectListSelectionModel buildRowSelectionModel() {
		ObjectListSelectionModel rowSelectionModel = new ObjectListSelectionModel(new ListModelAdapter(sortedColumnPairsAdapter));
		rowSelectionModel.addListSelectionListener(this.buildRowSelectionListener());
		rowSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return rowSelectionModel;
	}


	private ListSelectionListener buildRowSelectionListener() {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if ( ! e.getValueIsAdjusting()) {
					ColumnPairsPanel.this.rowSelectionChanged();
				}
			}
		};
	}

	private void rowSelectionChanged() {
		Object selection = rowSelectionModel.getSelectedValue();
		selectedColumnPairHolder.setValue(selection);
		boolean associationSelected = (selection != null);
		removeAction.setEnabled(associationSelected);
	}

	private void initializeLayout() {
		setLayout(new GridBagLayout());
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
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(0, 5, 0, 0);
		add(buttonPanel, constraints);	

		table = this.buildTable();
		componentsForEnablement.add(table);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.getViewport().setPreferredSize(new Dimension(50,50));
		scrollPane.getViewport().setBackground(table.getBackground());

		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);
			
		add(scrollPane, constraints);
	}

	private JTable buildTable() {
		JTable table = SwingComponentFactory.buildTable(tableModel, rowSelectionModel);
		SwingComponentFactory.attachTableEditorCanceler(table, referenceHolder);
		table.getTableHeader().setReorderingAllowed(false);
		int rowHeight = 20;	// start with minimum of 20

		// source field column (combo-box)
		TableColumn sourceFieldColumn = table.getColumnModel().getColumn(ColumnPairsColumnAdapter.SOURCE_FIELD_COLUMN);
		ComboBoxTableCellRenderer sourceFieldRenderer = this.buildSourceColumnComboBoxRenderer();
		sourceFieldColumn.setCellRenderer(sourceFieldRenderer);
		sourceFieldColumn.setCellEditor(new TableCellEditorAdapter(this.buildSourceColumnComboBoxRenderer()));
		rowHeight = Math.max(rowHeight, sourceFieldRenderer.getPreferredHeight());

		// target field column (combo-box)
		TableColumn targetFieldColumn = table.getColumnModel().getColumn(ColumnPairsColumnAdapter.TARGET_FIELD_COLUMN);
		ComboBoxTableCellRenderer targetFieldRenderer = this.buildTargetColumnComboBoxRenderer();
		targetFieldColumn.setCellRenderer(targetFieldRenderer);
		targetFieldColumn.setCellEditor(new TableCellEditorAdapter(this.buildTargetColumnComboBoxRenderer()));
		rowHeight = Math.max(rowHeight, targetFieldRenderer.getPreferredHeight());
		
		table.setRowHeight(rowHeight);
		
		return table;
	}

	private ListCellRenderer buildColumnListCellRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				// need null check for combo-box
				return (value == null) ? "" : ((MWColumn) value).getName();
			}
		};
	}
	
	
	// ********** source field (for cell editor) **********


	private ComboBoxTableCellRenderer buildSourceColumnComboBoxRenderer() {
		return new ComboBoxTableCellRenderer(this.buildSourceColumnComboBoxModel(), this.buildColumnListCellRenderer());
	}

	private ComboBoxModel buildSourceColumnComboBoxModel() {
		return new ComboBoxModelAdapter(sourceColumnsHolder, new SimplePropertyValueModel());
	}


	// ********** target field (for cell editor) **********

	private ComboBoxTableCellRenderer buildTargetColumnComboBoxRenderer() {
		return new ComboBoxTableCellRenderer(this.buildTargetColumnComboBoxModel(), this.buildColumnListCellRenderer());
	}

	private ComboBoxModel buildTargetColumnComboBoxModel() {
		return new ComboBoxModelAdapter(targetColumnsHolder, new SimplePropertyValueModel());
	}

	
	
	//*********** button panel **********

	private JPanel buildButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 5, 5));

		JButton addButton = buildAddButton();
		componentsForEnablement.add(addButton);
		buttonPanel.add(addButton);
		addAlignRight(addButton);

		JButton removeButton = buildRemoveButton();
		componentsForEnablement.add(removeButton);
		buttonPanel.add(removeButton);
		addAlignRight(removeButton);

		return buttonPanel;
	}
	
	// ********** add **********

	private JButton buildAddButton() {
		return new JButton(this.buildAddAction());
	}

	private Action buildAddAction() {
		final Action action = new AbstractFrameworkAction(getApplicationContext()) {
			protected void initialize() {
				initializeText("ADD_ASSOCIATION_BUTTON_TEXT");
				initializeMnemonic("ADD_ASSOCIATION_BUTTON_TEXT");
			}
			
			public void actionPerformed(ActionEvent event) {
				ColumnPairsPanel.this.addColumnPair();
			}
		};
		action.setEnabled(false);
		
		referenceHolder.addPropertyChangeListener(PropertyValueModel.VALUE, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				action.setEnabled(selectedReference() != null);

			}
		});
		
		return action;
	}
	
	private void addColumnPair() {
		if (this.table.isEditing()) {
			this.table.getCellEditor().stopCellEditing();
		}

		if (selectedReference().getSourceTable().columnsSize() == 0 
			|| selectedReference().getTargetTable().columnsSize() == 0) {
				JOptionPane.showMessageDialog(
					currentWindow(), 
					resourceRepository().getString("TABLE_HAS_NO_FIELDS.message"), 
					resourceRepository().getString("TABLE_HAS_NO_FIELDS.title"), 
					JOptionPane.WARNING_MESSAGE);
				return;
			}
		selectedReference().addColumnPair((MWColumn) selectedReference().getSourceTable().columns().next(), 
										   (MWColumn) selectedReference().getTargetTable().columns().next());
	}


	// ********** remove **********

	private JButton buildRemoveButton() {
		return new JButton(this.buildRemoveAction());
	}

	private Action buildRemoveAction() {
		removeAction = new AbstractFrameworkAction(getApplicationContext()) {
			protected void initialize() {
				initializeText("REMOVE_ASSOCIATION_BUTTON_TEXT");
				initializeMnemonic("REMOVE_ASSOCIATION_BUTTON_TEXT");
			}
			
			public void actionPerformed(ActionEvent event) {
				ColumnPairsPanel.this.removeColumnPair();
			}
		};
		removeAction.setEnabled(false);
		return removeAction;
	}

	private void removeColumnPair() {
		if (this.table.isEditing()) {
			this.table.getCellEditor().stopCellEditing();
		}

		int option = JOptionPane.showConfirmDialog(getWorkbenchContext().getCurrentWindow(),
										resourceRepository().getString("REMOVE_FIELD_ASSOCIATIONS_WARNING_DIALOG.message"),
										resourceRepository().getString("REMOVE_FIELD_ASSOCIATIONS_WARNING_DIALOG.title"),
										JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE);
										
		if (option == JOptionPane.YES_OPTION) {
			MWColumnPair association = this.selectedColumnPair();
			if (association != null) {
				this.selectedReference().removeColumnPair(association);
			}
		}
	}


	// ********** queries **********

	private MWColumnPair selectedColumnPair() {
		if (rowSelectionModel.isSelectionEmpty()) {
			return null;
		}
		return (MWColumnPair) rowSelectionModel.getSelectedValue();
	}
	
	private MWReference selectedReference() {
		return (MWReference) this.referenceHolder.getValue();
	}


	// ********** classes **********

	public static class ColumnPairsColumnAdapter implements ColumnAdapter {
		
		private ResourceRepository resourceRepository;
		
		public static final int COLUMN_COUNT = 2;

		public static final int SOURCE_FIELD_COLUMN = 0;
		public static final int TARGET_FIELD_COLUMN = 1;

		private static final String[] COLUMN_NAME_KEYS = new String[] {
			"SOURCE_COLUMN_COLUMN_HEADER",
			"TARGET_COLUMN_COLUMN_HEADER",
		};

		protected ColumnPairsColumnAdapter(ResourceRepository repository) {
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
				case SOURCE_FIELD_COLUMN:	return Object.class;
				case TARGET_FIELD_COLUMN:	return Object.class;
				default: 					return Object.class;
			}
		}

		public boolean isColumnEditable(int index) {
			return true;
		}

		public PropertyValueModel[] cellModels(Object subject) {
			MWColumnPair association = (MWColumnPair) subject;
			PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];

			result[SOURCE_FIELD_COLUMN]			= this.buildSourceColumnAdapter(association);
			result[TARGET_FIELD_COLUMN]			= this.buildTargetColumnAdapter(association);

			return result;
		}

		private PropertyValueModel buildSourceColumnAdapter(MWColumnPair association) {
			PropertyValueModel adapter = new PropertyAspectAdapter(MWColumnPair.SOURCE_COLUMN_PROPERTY, association) {
				protected Object getValueFromSubject() {
					return ((MWColumnPair) this.subject).getSourceColumn();
				}
				protected void setValueOnSubject(Object value) {
					((MWColumnPair) this.subject).setSourceColumn((MWColumn) value);
				}
			};
			return new ValuePropertyPropertyValueModelAdapter(adapter, MWColumn.NAME_PROPERTY);
		}
		
		private PropertyValueModel buildTargetColumnAdapter(MWColumnPair association) {
			PropertyValueModel adapter = new PropertyAspectAdapter(MWColumnPair.TARGET_COLUMN_PROPERTY, association) {
				protected Object getValueFromSubject() {
					return ((MWColumnPair) this.subject).getTargetColumn();
				}
				protected void setValueOnSubject(Object value) {
					((MWColumnPair) this.subject).setTargetColumn((MWColumn) value);
				}
			};
			return new ValuePropertyPropertyValueModelAdapter(adapter, MWColumn.NAME_PROPERTY);
		}
	}
	public Collection getComponentsForEnablement() {
		return componentsForEnablement;
	}

}
