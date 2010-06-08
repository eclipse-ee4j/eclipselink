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
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;



public abstract class ReturningUpdateFieldsPanel extends AbstractSubjectPanel {
	private TableModel tableModel;
	private ListValueModel sortedFieldsAdapter;
	private ObjectListSelectionModel rowSelectionModel;
	private AbstractFrameworkAction removeAction;
	static final String[] EMPTY_STRING_ARRAY = new String[0];

	/**
	 * Creates a new <code>ReturningUpdateFieldsPanel</code>.
	 */
	protected ReturningUpdateFieldsPanel( PropertyValueModel subjectHolder, WorkbenchContextHolder contextHolder)
	{
		super( subjectHolder, contextHolder);
	}	
	/**
	 * Initializes the layout of this pane.
	 *
	 * @return The container with all its widgets
	 */
	protected void initializeLayout()
	{
		this.sortedFieldsAdapter = this.buildSortedUpdateFieldsHolder();
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
		JComponent updateTablePane = buildUpdatePanel();
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		add( updateTablePane, constraints);
	}

	private JComponent buildUpdatePanel()
	{
		JTable table =  this.buildTable();
		JScrollPane updateTablePane = new JScrollPane( table);
		updateTablePane.getViewport().setBackground( table.getBackground());
		return updateTablePane;
	}

	private JTable buildTable() {
		JTable table = SwingComponentFactory.buildTable(tableModel, rowSelectionModel);
		int rowHeight = 20;	// start with minimum of 20

		table.setRowHeight(rowHeight);
		
		return table;
	}

	private ObjectListSelectionModel buildRowSelectionModel() {
		ObjectListSelectionModel rowSelectionModel = new ObjectListSelectionModel(new ListModelAdapter( sortedFieldsAdapter));
		rowSelectionModel.addListSelectionListener( this.buildRowSelectionListener());
		rowSelectionModel.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		return rowSelectionModel;
	}

	private ListSelectionListener buildRowSelectionListener() {
		return new ListSelectionListener() {
			public void valueChanged( ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					ReturningUpdateFieldsPanel.this.rowSelectionChanged();
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
		return new TableModelAdapter( sortedFieldsAdapter, this.buildUpdateColumnAdapter());
	}
	
	private ColumnAdapter buildUpdateColumnAdapter() {
		return new UpdateFieldColumnAdapter( resourceRepository());
	}
	
	
	private ListValueModel buildSortedUpdateFieldsHolder() {
		return new SortedListValueModelAdapter(buildDatabaseFieldNameAdapter());
	}
	
	private ListValueModel buildDatabaseFieldNameAdapter() {
		return new ItemPropertyListValueModelAdapter(buildUpdateFieldsAdapter(), MWColumn.QUALIFIED_NAME_PROPERTY);
	}

	private CollectionValueModel buildUpdateFieldsAdapter() {
		return new CollectionAspectAdapter( getSubjectHolder(), MWReturningPolicy.UPDATE_FIELDS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return (( MWReturningPolicy)subject).updateFields();
			}
			protected int sizeFromSubject() {
				return (( MWReturningPolicy)subject).updateFieldsSize();
			}
		};
	}

	protected MWReturningPolicy returningPolicy() {
		return (MWReturningPolicy) this.getSubjectHolder().getValue();
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

	protected String addButtonKey() {
		return "ADD_BUTTON";
	}

	protected String removeButtonKey() {
		return "REMOVE_BUTTON";
	}

	private Action buildAddAction() {
		final AbstractFrameworkAction action = new AbstractFrameworkAction( this.getApplicationContext()) {
			public void actionPerformed( ActionEvent event) {
				ReturningUpdateFieldsPanel.this.addField();
			}
		};
		action.setText(resourceRepository().getString(addButtonKey()));
		action.setMnemonic(resourceRepository().getMnemonic(addButtonKey()));
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
			public void actionPerformed(ActionEvent event) {
				ReturningUpdateFieldsPanel.this.removeField();
			}
		};
		removeAction.setText(resourceRepository().getString(removeButtonKey()));
		removeAction.setMnemonic(resourceRepository().getMnemonic(removeButtonKey()));
		removeAction.setEnabled( false);
		return removeAction;
	}

	private void removeField() {
		Object[] selectedFields = this.selectedFields();
		for (int index = selectedFields.length; --index >= 0;) {
			this.returningPolicy().removeUpdateField( (MWDataField) selectedFields[index]);
		}
	}

	private Object[] selectedFields() {
		return rowSelectionModel.getSelectedValues();
	}

	// ********** classes **********

	private static class UpdateFieldColumnAdapter implements ColumnAdapter {
		
		private ResourceRepository resourceRepository;
		
		public static final int COLUMN_COUNT = 1;

		public static final int UPDATE_FIELD_COLUMN = 0;

		private final String[] COLUMN_NAME_KEYS = new String[] {
			"RETURNING_POLICY_UPDATE_COLUMN_HEADER",
		};

		protected UpdateFieldColumnAdapter(ResourceRepository repository) {
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
				case UPDATE_FIELD_COLUMN:
					return Object.class;
				default:
					return Object.class;
			}
		}

		public boolean isColumnEditable( int index) {
			return false;
		}

		public PropertyValueModel[] cellModels( Object subject) {
			PropertyValueModel[] result = new PropertyValueModel[ COLUMN_COUNT];

			result[ UPDATE_FIELD_COLUMN] = this.buildUpdateFieldAdapter((MWDataField) subject);

			return result;
		}

		private PropertyValueModel buildUpdateFieldAdapter( MWDataField field) {
			return new PropertyAspectAdapter( MWDataField.FIELD_NAME_PROPERTY, field) {
				protected Object getValueFromSubject() {
					return ((MWDataField) this.subject).fieldName();
				}
			};
		}
	}
	
}
