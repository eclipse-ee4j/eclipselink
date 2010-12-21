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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

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
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWColumnQueryKeyPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWVariableOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ColumnCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.ComboBoxTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.TableCellEditorAdapter;



final class VariableOneToOneQueryKeyAssociationsPanel extends ScrollablePropertiesPage {

	private PropertyValueModel selectedFieldAssociationHolder;
	private ValueModel parentDescriptorHolder;
	
	private CollectionValueModel fieldQueryKeyAssociationsAdapter;
	private ObjectListSelectionModel rowSelectionModel;

	private ListValueModel queryKeyNamesHolder;

	private Action removeAction;

	VariableOneToOneQueryKeyAssociationsPanel(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}
	
	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		this.parentDescriptorHolder = buildParentDescriptorHolder();
		this.queryKeyNamesHolder = buildSortedQueryKeyNamesHolder();

		this.fieldQueryKeyAssociationsAdapter = buildFieldQueryKeyAssociationsAdapter();
		this.selectedFieldAssociationHolder = new SimplePropertyValueModel(null);
		this.rowSelectionModel = buildRowSelectionModel();
	}

	private PropertyValueModel buildParentDescriptorHolder() {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWVariableOneToOneMapping) subject).getParentDescriptor();
			}
		};
	}

	private ListValueModel buildSortedQueryKeyNamesHolder() {
		return new SortedListValueModelAdapter(buildQueryKeyNamesHolder());
	}

	private CollectionValueModel buildQueryKeyNamesHolder()  {
		return new CollectionAspectAdapter(buildReferenceDescriptorHolder()) {
			protected Iterator getValueFromSubject() {
				return ((MWRelationalDescriptor) subject).allQueryKeyNames();
			}
		};
	} 
	
	private PropertyValueModel buildReferenceDescriptorHolder() {
		return new PropertyAspectAdapter(getSelectionHolder(), MWVariableOneToOneMapping.REFERENCE_DESCRIPTOR_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWVariableOneToOneMapping) subject).getReferenceDescriptor();
			}
		};
	}

	
	private CollectionValueModel buildFieldQueryKeyAssociationsAdapter() {
		return new CollectionAspectAdapter(getSelectionHolder(), MWVariableOneToOneMapping.COLUMN_QUERY_KEY_PAIRS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWVariableOneToOneMapping) subject).columnQueryKeyPairs();
			}
			protected int sizeFromSubject() {
				return ((MWVariableOneToOneMapping) subject).columnQueryKeyPairsSize();
			}
		};
	}

	private ObjectListSelectionModel buildRowSelectionModel() {
		ObjectListSelectionModel rowSelectionModel = new ObjectListSelectionModel(new ListModelAdapter(this.fieldQueryKeyAssociationsAdapter));
		rowSelectionModel.addListSelectionListener(this.buildRowSelectionListener());
		rowSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return rowSelectionModel;
	}


	private ListSelectionListener buildRowSelectionListener() {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if ( ! e.getValueIsAdjusting()) {
					VariableOneToOneQueryKeyAssociationsPanel.this.rowSelectionChanged();
				}
			}
		};
	}

	private void rowSelectionChanged() {
		Object selection = this.rowSelectionModel.getSelectedValue();
		this.selectedFieldAssociationHolder.setValue(selection);
		boolean associationSelected = (selection != null);
		this.removeAction.setEnabled(associationSelected);
	}

	protected Component buildPage() {
		JPanel panel =  new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the buttons first
		JPanel buttonPanel = buildButtonPanel();
		constraints.gridx			= 1;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(5, 5, 5, 5);
		panel.add(buttonPanel, constraints);	

		// Create the table, listeners needs to have access to the buttons
		JTable table = this.buildTable();
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
		constraints.insets		= new Insets(5, 5, 5, 0);
			
		panel.add(scrollPane, constraints);

		addHelpTopicId(panel, helpTopicId());
		
		return panel;
	}

	private JTable buildTable() {
		JTable table = SwingComponentFactory.buildTable(buildTableModel(), this.rowSelectionModel);
		int rowHeight = 20;	// start with minimum of 20

		// field column (combo-box)
		TableColumn fieldColumn = table.getColumnModel().getColumn(FieldAssociationsColumnAdapter.FIELD_COLUMN);
		ComboBoxTableCellRenderer sourceFieldRenderer = this.buildFieldComboBoxRenderer();
		fieldColumn.setCellRenderer(sourceFieldRenderer);
		fieldColumn.setCellEditor(new TableCellEditorAdapter(this.buildFieldComboBoxRenderer()));
		rowHeight = Math.max(rowHeight, sourceFieldRenderer.getPreferredHeight());

		// query key name column (combo-box)
		TableColumn queryKeyNameColumn = table.getColumnModel().getColumn(FieldAssociationsColumnAdapter.QUERY_KEY_NAME_COLUMN);
		ComboBoxTableCellRenderer queryKeyNameRenderer = this.buildQueryKeyNameComboBoxRenderer();
		queryKeyNameColumn.setCellRenderer(queryKeyNameRenderer);
		queryKeyNameColumn.setCellEditor(new TableCellEditorAdapter(this.buildQueryKeyNameComboBoxRenderer()));
		rowHeight = Math.max(rowHeight, queryKeyNameRenderer.getPreferredHeight());
		
		table.setRowHeight(rowHeight);
		
		return table;
	}
	

	private TableModel buildTableModel() {
		return new TableModelAdapter(buildFieldQueryKeyAssociationsAdapter(), this.buildColumnAdapter());
	}

	private ColumnAdapter buildColumnAdapter() {
		return new FieldAssociationsColumnAdapter(getSelectionHolder(), resourceRepository());
	}

	private ListCellRenderer buildDatabaseFieldListCellRenderer() {
		return new AdaptableListCellRenderer(new ColumnCellRendererAdapter(resourceRepository()));
	}
	
		
	// ********** field (for cell editor) **********

	private ComboBoxTableCellRenderer buildFieldComboBoxRenderer() {
		return new ComboBoxTableCellRenderer(this.buildFieldComboBoxModel(), this.buildDatabaseFieldListCellRenderer());
	}

	private ComboBoxModel buildFieldComboBoxModel() {
		return RelationalMappingComponentFactory.buildExtendedColumnComboBoxModel(new SimplePropertyValueModel(), this.parentDescriptorHolder);
	}


	// ********** query key name (for cell editor) **********

	private ComboBoxTableCellRenderer buildQueryKeyNameComboBoxRenderer() {
		return new ComboBoxTableCellRenderer(this.buildQueryKeyNameComboBoxModel());
	}

	private ComboBoxModel buildQueryKeyNameComboBoxModel() {
		return new ComboBoxModelAdapter(this.queryKeyNamesHolder, new SimplePropertyValueModel());
	}



	
	
	//*********** button panel **********

	private JPanel buildButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 5, 5));
		buttonPanel.add(this.buildAddButton());
		buttonPanel.add(this.buildRemoveButton());
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
				VariableOneToOneQueryKeyAssociationsPanel.this.addFieldQueryKeyNameAssociation();
			}
		};
		
		return action;
	}
	
	private void addFieldQueryKeyNameAssociation() {
		
		MWColumn field = null;
		MWRelationalClassDescriptor descriptor = (MWRelationalClassDescriptor) getParentDescriptor();
		if (!descriptor.isAggregateDescriptor()) {
			if (descriptor.allAssociatedColumns().hasNext()) {
				field = (MWColumn) descriptor.allAssociatedColumns().next();
			}
		}
		String queryKeyName = "";
		if (this.queryKeyNamesHolder.size() > 0) {
			queryKeyName = (String) this.queryKeyNamesHolder.getItem(0);
		}
		
		getVariableOneToOneMapping().addColumnQueryKeyPair(field, queryKeyName);
	}

	private MWVariableOneToOneMapping getVariableOneToOneMapping() {
		return (MWVariableOneToOneMapping) selection();
	}

	// ********** remove **********

	private JButton buildRemoveButton() {
		return new JButton(this.buildRemoveAction());
	}

	private Action buildRemoveAction() {
		this.removeAction = new AbstractFrameworkAction(getApplicationContext()) {
			protected void initialize() {
				initializeTextAndMnemonic("REMOVE_ASSOCIATION_BUTTON_TEXT");
			}
			
			public void actionPerformed(ActionEvent event) {
				VariableOneToOneQueryKeyAssociationsPanel.this.removeFieldQueryKeyNameAssociation();
			}
		};
		this.removeAction.setEnabled(false);
		return this.removeAction;
	}

	private void removeFieldQueryKeyNameAssociation() {
		int option = JOptionPane.showConfirmDialog(getWorkbenchContext().getCurrentWindow(),
										resourceRepository().getString("REMOVE_FIELD_ASSOCIATIONS_WARNING_DIALOG.message"),
										resourceRepository().getString("REMOVE_FIELD_ASSOCIATIONS_WARNING_DIALOG.title"),
										JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE);
										
		if (option == JOptionPane.YES_OPTION) {
			MWColumnQueryKeyPair association = this.selectedAssociation();
			if (association != null) {
				this.getVariableOneToOneMapping().removeColumnQueryKeyPair(association);
			}
		}
	}

	
	// ********** queries **********

	private MWColumnQueryKeyPair selectedAssociation() {
		if (this.rowSelectionModel.isSelectionEmpty()) {
			return null;
		}
		return (MWColumnQueryKeyPair) this.rowSelectionModel.getSelectedValue();
	}

	protected String helpTopicId() {
		return "mapping.variableOneToOne.queryKeyAssociations";
	}
	
	private MWRelationalDescriptor getParentDescriptor() {
		return (MWRelationalDescriptor) ((MWVariableOneToOneMapping) selection()).getParentDescriptor();

	}
	
	// ********** classes **********

	public static class FieldAssociationsColumnAdapter implements ColumnAdapter {
			
		private PropertyValueModel selectionHolder;
		private ResourceRepository resourceRepository;
		public static final int COLUMN_COUNT = 2;

		public static final int FIELD_COLUMN = 0;
		public static final int QUERY_KEY_NAME_COLUMN = 1;

		private final String[] COLUMN_NAME_KEYS = new String[] {
			"FIELD_COLUMN_HEADER",
			"QUERY_KEY_NAME_COLUMN_HEADER",
		};

		protected FieldAssociationsColumnAdapter(PropertyValueModel selectionHolder, ResourceRepository resourceRepository) {
			super();
			this.selectionHolder = selectionHolder;
			this.resourceRepository = resourceRepository;
		}
		
		private MWVariableOneToOneMapping mapping() {
			return (MWVariableOneToOneMapping) this.selectionHolder.getValue();
		}
		
		public int getColumnCount() {
			return COLUMN_COUNT;
		}

		public String getColumnName(int index) {
			return this.resourceRepository.getString(this.COLUMN_NAME_KEYS[index]);
		}

		public Class getColumnClass(int index) {
			switch (index) {
				case FIELD_COLUMN:	return Object.class;
				case QUERY_KEY_NAME_COLUMN:	return Object.class;
				default: 					return Object.class;
			}
		}

		public boolean isColumnEditable(int index) {
			//TODO the column should actually be disabled, not just uneditable
			
			if (index == FIELD_COLUMN) {
				if (this.mapping().parentDescriptorIsAggregate()) {
					return false;
				}
			}
			return true;
		}

		public PropertyValueModel[] cellModels(Object subject) {
			MWColumnQueryKeyPair association = (MWColumnQueryKeyPair) subject;
			PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];

			result[FIELD_COLUMN]			= this.buildFieldAdapter(association);
			result[QUERY_KEY_NAME_COLUMN]			= this.buildTargetFieldAdapter(association);

			return result;
		}

		private PropertyValueModel buildFieldAdapter(MWColumnQueryKeyPair association) {
			PropertyValueModel adapter = new PropertyAspectAdapter(MWColumnQueryKeyPair.COLUMN_PROPERTY, association) {
				protected Object getValueFromSubject() {
					return ((MWColumnQueryKeyPair) subject).getColumn();
				}
				protected void setValueOnSubject(Object value) {
					((MWColumnQueryKeyPair) subject).setColumn((MWColumn) value);
				}
			};
			return new ValuePropertyPropertyValueModelAdapter(adapter, MWColumn.NAME_PROPERTY);
		}
		
		private PropertyValueModel buildTargetFieldAdapter(MWColumnQueryKeyPair association) {
			return new PropertyAspectAdapter(MWColumnQueryKeyPair.QUERY_KEY_NAME_PROPERTY, association) {
				protected Object getValueFromSubject() {
					return ((MWColumnQueryKeyPair) subject).getQueryKeyName();
				}
				protected void setValueOnSubject(Object value) {
					((MWColumnQueryKeyPair) subject).setQueryKeyName((String) value);
				}
			};
		}
	}
}
