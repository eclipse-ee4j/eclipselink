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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AccessibleTitledPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWReturningPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWReturningPolicyInsertFieldReturnOnlyFlag;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.CheckBoxTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.TableCellEditorAdapter;


public abstract class ReturningInsertFieldsPanel extends AbstractSubjectPanel {

	private TableModel tableModel;
	private ListValueModel sortedFieldsAdapter;
	private ObjectListSelectionModel rowSelectionModel;

	private Action removeAction;

	protected ReturningInsertFieldsPanel( PropertyValueModel subjectHolder, WorkbenchContextHolder contextHolder) {
		super( subjectHolder, contextHolder);
	}

	protected void initializeLayout()
	{
		this.sortedFieldsAdapter = this.buildSortedInsertFieldReturnOnlyFlagsAdapter();
		this.tableModel = this.buildTableModel();
		this.rowSelectionModel = this.buildRowSelectionModel();
		
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the button panel first
		JPanel buttonPanel = buildButtonPanel();
		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_END;
		constraints.insets		= new Insets(5, 0, 0, 0);
		
		add( buttonPanel, constraints);

		// Create the table after since some listeners need access to the buttons
		JComponent insertTablePane = buildInsertPanel();
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		add( insertTablePane, constraints);
	}

	private JComponent buildInsertPanel()
	{
		JTable table =  this.buildTable();
		JScrollPane insertTablePane = new JScrollPane( table);
		insertTablePane.getViewport().setBackground( table.getBackground());

		return insertTablePane;
	}

	private JTable buildTable() {		
		JTable table = SwingComponentFactory.buildTable(tableModel, rowSelectionModel);
		int rowHeight = 20;	// start with minimum of 20
		
		// Name column
		TableColumn column = table.getColumnModel().getColumn( InsertFieldColumnAdapter.INSERT_FIELD_COLUMN);
		TableCellRenderer fieldRenderer = this.buildFieldColumnCellRenderer();
		column.setCellRenderer(fieldRenderer);

		// Return-only column (check box)
		column = table.getColumnModel().getColumn( InsertFieldColumnAdapter.RETURN_ONLY_COLUMN);
		CheckBoxTableCellRenderer returnOnlyRenderer = new CheckBoxTableCellRenderer();
		column.setCellRenderer( returnOnlyRenderer);
		column.setCellEditor(new TableCellEditorAdapter(new CheckBoxTableCellRenderer()));
		rowHeight = Math.max( rowHeight, returnOnlyRenderer.getPreferredHeight());

		table.setRowHeight(rowHeight);
		
		return table;
	}

	protected TableCellRenderer buildFieldColumnCellRenderer() {
		return new SimpleTableCellRenderer() {
			protected String buildText(Object value) {
				return value == null ? null : ((MWDataField) value).fieldName();
			}
		};
	}
	
	private ObjectListSelectionModel buildRowSelectionModel() {
		ObjectListSelectionModel rowSelectionModel = new ObjectListSelectionModel( new ListModelAdapter( sortedFieldsAdapter));
		rowSelectionModel.addListSelectionListener( this.buildRowSelectionListener());
		rowSelectionModel.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		return rowSelectionModel;
	}

	private ListSelectionListener buildRowSelectionListener() {
		return new ListSelectionListener() {
			public void valueChanged( ListSelectionEvent e) {
				if ( ! e.getValueIsAdjusting()) {
					ReturningInsertFieldsPanel.this.rowSelectionChanged();
				}
			}
		};
	}

	private void rowSelectionChanged() {
		Object[] selection = rowSelectionModel.getSelectedValues();
		boolean fieldSelected = ( selection.length > 0);
		removeAction.setEnabled( fieldSelected);
	}

	private TableModel buildTableModel() {
		return new TableModelAdapter( sortedFieldsAdapter, this.buildInsertColumnAdapter());
	}
	
	private ColumnAdapter buildInsertColumnAdapter() {
		return new InsertFieldColumnAdapter( resourceRepository());
	}
	
	
	private ListValueModel buildSortedInsertFieldReturnOnlyFlagsAdapter() {
		return new SortedListValueModelAdapter(buildColumnNameAdapter());
	}
	
	private ListValueModel buildColumnNameAdapter() {
		return new ItemPropertyListValueModelAdapter(buildInsertFieldReturnOnlyFlagsAdapter(), MWColumn.NAME_PROPERTY);
	}

	private CollectionValueModel buildInsertFieldReturnOnlyFlagsAdapter() {
		return new CollectionAspectAdapter( getSubjectHolder(), MWReturningPolicy.INSERT_FIELD_RETURN_ONLY_FLAGS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return (( MWReturningPolicy) this.subject).insertFieldReturnOnlyFlags();
			}
			protected int sizeFromSubject() {
				return (( MWReturningPolicy) this.subject).insertFieldReturnOnlyFlagsSize();
			}
		};
	}

	protected MWReturningPolicy returningPolicy() {
		return (MWReturningPolicy)this.getSubjectHolder().getValue();
	}
	
	//*********** button panel **********

	private JPanel buildButtonPanel() {
		JPanel buttonPanel = new AccessibleTitledPanel( new GridLayout( 1, 0, 5, 0));
		buttonPanel.add( this.buildAddButton());
		buttonPanel.add( this.buildRemoveButton());
		return buttonPanel;
	}

	private JButton buildAddButton() {
		return new JButton( this.buildAddAction());
	}
	
	private Action buildAddAction() {
		final Action action = new AbstractFrameworkAction( this.getApplicationContext()) {
			protected void initialize() {
				initializeText( "ADD_BUTTON");
				initializeMnemonic( "ADD_BUTTON");
			}
			
			public void actionPerformed( ActionEvent event) {
				ReturningInsertFieldsPanel.this.addField();
			}
		};
		action.setEnabled( true);
		
		this.getSubjectHolder().addPropertyChangeListener(PropertyValueModel.VALUE, new PropertyChangeListener() {
			public void propertyChange( PropertyChangeEvent evt) {
				action.setEnabled( returningPolicy() != null);
			}
		});
		return action;
	}
	
	protected abstract void addField();

	// ********** remove **********

	private JButton buildRemoveButton() {
		return new JButton( this.buildRemoveAction());
	}

	private Action buildRemoveAction() {
		removeAction = new AbstractFrameworkAction( this.getApplicationContext()) {
			protected void initialize() {
				initializeText( "REMOVE_BUTTON");
				initializeMnemonic( "REMOVE_BUTTON");
			}
			
			public void actionPerformed(ActionEvent event) {
				ReturningInsertFieldsPanel.this.removeField();
			}
		};
		removeAction.setEnabled( false);
		return removeAction;
	}

	private void removeField() {
		Object[] selectedFields = this.selectedFields();
		for (int index = selectedFields.length; --index >= 0;) {
			this.returningPolicy().removeInsertFieldReturnOnlyFlag( (MWReturningPolicyInsertFieldReturnOnlyFlag) selectedFields[index]);
		}
	}

	private Object[] selectedFields() {
		return rowSelectionModel.getSelectedValues();
	}

	// ********** classes **********

	private static class InsertFieldColumnAdapter implements ColumnAdapter {
		
		private ResourceRepository resourceRepository;
		
		public static final int COLUMN_COUNT = 2;

		public static final int INSERT_FIELD_COLUMN = 0;
		public static final int RETURN_ONLY_COLUMN = 1;

		private final String[] COLUMN_NAME_KEYS = new String[] {
			"RETURNING_POLICY_INSERT_COLUMN_HEADER",
			"RETURNING_POLICY_RETURN_ONLY_COLUMN_HEADER",
		};

		private static final String[] EMPTY_STRING_ARRAY = new String[0];

		protected InsertFieldColumnAdapter(ResourceRepository repository) {
			super();
			this.resourceRepository = repository;
		}
		
		public int getColumnCount() {
			return COLUMN_COUNT;
		}

		public String getColumnName(int index) {
			return this.resourceRepository.getString( COLUMN_NAME_KEYS[ index]);
		}

		public Class getColumnClass(int index) {
			switch (index) {
				case INSERT_FIELD_COLUMN:
					return Object.class;
				case RETURN_ONLY_COLUMN:
					return Object.class;
				default:
					return Object.class;
			}
		}

		public boolean isColumnEditable( int index) {
			switch (index) {
				case INSERT_FIELD_COLUMN:
					return false;
				case RETURN_ONLY_COLUMN:
					return true;
				default:
					return false;
			}
		}

		public PropertyValueModel[] cellModels( Object subject) {
			MWReturningPolicyInsertFieldReturnOnlyFlag insert = ( MWReturningPolicyInsertFieldReturnOnlyFlag)subject;
			PropertyValueModel[] result = new PropertyValueModel[ COLUMN_COUNT];

			result[ INSERT_FIELD_COLUMN] = this.buildInsertFieldAdapter( insert);
			result[ RETURN_ONLY_COLUMN] = this.buildReturnOnlyAdapter( insert);

			return result;
		}
		
		private PropertyValueModel buildInsertFieldAdapter( MWReturningPolicyInsertFieldReturnOnlyFlag field) {
			// the field does not change
			PropertyValueModel adapter = new PropertyAspectAdapter( EMPTY_STRING_ARRAY, field) {
				protected Object getValueFromSubject() {
					return (( MWReturningPolicyInsertFieldReturnOnlyFlag) this.subject).getField();
				}
			};
			return new ValuePropertyPropertyValueModelAdapter(adapter, MWDataField.FIELD_NAME_PROPERTY);
		}
		
		private PropertyValueModel buildReturnOnlyAdapter( MWReturningPolicyInsertFieldReturnOnlyFlag field) {
			return new PropertyAspectAdapter( MWReturningPolicyInsertFieldReturnOnlyFlag.RETURN_ONLY_PROPERTY, field) {
				protected Object getValueFromSubject() {
					return Boolean.valueOf((( MWReturningPolicyInsertFieldReturnOnlyFlag)subject).isReturnOnly());
				}
				protected void setValueOnSubject( Object value) {
					(( MWReturningPolicyInsertFieldReturnOnlyFlag)subject).setReturnOnly((( Boolean)value).booleanValue());
				}
			};
		}

	}
}

