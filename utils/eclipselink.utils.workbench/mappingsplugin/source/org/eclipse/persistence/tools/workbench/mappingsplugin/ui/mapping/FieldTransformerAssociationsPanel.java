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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.ListIterator;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformer;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTableCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public abstract class FieldTransformerAssociationsPanel
	extends AbstractSubjectPanel
{
	// **************** Variables *********************************************
	
	/** The field transformer associations selection model */
	private ObjectListSelectionModel fieldTransformerAssociationsSelectionModel;
	
		
	// **************** Constructors ******************************************
	
	/** Expects a MWTransformationMapping object */
	public FieldTransformerAssociationsPanel(ValueModel transformationMappingHolder, WorkbenchContextHolder contextHolder) {
		super(transformationMappingHolder, contextHolder);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initializeLayout() {
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createTitledBorder(this.resourceRepository().getString("TRANSFORMATION_MAPPING_FIELD_TRANSFORMER_ASSOCIATIONS_PANEL")));
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		// transformer associations table
		JComponent tablePanel = this.buildFieldTransformerAssociationsTablePanel();
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 4;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 5, 0);
		this.add(tablePanel, constraints);
		
		// add button
		JButton addButton = this.buildAddFieldTransformerAssociationButton();
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 0, 5);
		this.add(addButton, constraints);
		this.addAlignRight(addButton);
		
		// edit button
		JButton editButton = this.buildEditFieldTransformerAssociationButton();
		constraints.gridx		= 1;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 0, 5);
		this.add(editButton, constraints);
		this.addAlignRight(editButton);
		
		// remove button
		JButton removeButton = this.buildRemoveFieldTransformerAssociationsButton();
		constraints.gridx		= 1;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(5, 5, 0, 5);
		this.add(removeButton, constraints);
		this.addAlignRight(removeButton);
		
		addHelpTopicId(this, "mapping.transformation.fieldTransformerAssociations");
	}
	
	private JComponent buildFieldTransformerAssociationsTablePanel() {	
		JTable table = this.buildFieldTransformerAssociationsTable();
		TableColumn firstColumn = table.getColumnModel().getColumn(0);
		TableColumn secondColumn = table.getColumnModel().getColumn(1);
		secondColumn.setPreferredWidth(firstColumn.getPreferredWidth() * 2);

		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getViewport().setBackground(table.getBackground());
		scrollPane.setPreferredSize(new Dimension(10, 10));
		
		return scrollPane;
	}
	
	private JTable buildFieldTransformerAssociationsTable() {
		ListValueModel listValue = this.buildFieldTransformerAssociationsValue();
		TableModel tableModel = this.buildFieldTransformerAssociationsTableModel(listValue);
		this.fieldTransformerAssociationsSelectionModel = this.buildFieldTransformerAssociationsSelectionModel(listValue);
		
		JTable table = SwingComponentFactory.buildTable(tableModel, this.fieldTransformerAssociationsSelectionModel);
		
		TableColumn column;
		
		// field column
		column = table.getColumnModel().getColumn(FieldTransformationAssociationsColumnAdapter.FIELD_COLUMN);
		column.setCellRenderer(this.buildFieldColumnCellRenderer());
		
		// transformer column
		column = table.getColumnModel().getColumn(FieldTransformationAssociationsColumnAdapter.TRANSFORMER_COLUMN);
		column.setCellRenderer(this.buildTransformerTableCellRenderer());
		
		return table;
	}
	
	private ListValueModel buildFieldTransformerAssociationsValue() {
		return new ListAspectAdapter(this.getSubjectHolder(), MWTransformationMapping.FIELD_TRANSFORMER_ASSOCIATIONS_LIST) {
			protected ListIterator getValueFromSubject() {
				return ((MWTransformationMapping) this.subject).fieldTransformerAssociations();
			}
		};
	}
	
	private TableModel buildFieldTransformerAssociationsTableModel(ListValueModel sortedFieldTransformerAssociationsValue) {
		return new TableModelAdapter(sortedFieldTransformerAssociationsValue, this.buildFieldTransformerAssociationsColumnAdapter());
	}
	
	protected ColumnAdapter buildFieldTransformerAssociationsColumnAdapter() {
		return new FieldTransformationAssociationsColumnAdapter(this.resourceRepository());
	}
	
	private ObjectListSelectionModel buildFieldTransformerAssociationsSelectionModel(ListValueModel sortedFieldTransformerAssociationsValue) {
		ObjectListSelectionModel selectionModel = new ObjectListSelectionModel(new ListModelAdapter(sortedFieldTransformerAssociationsValue));
		selectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		return selectionModel;
	}
	
	protected TableCellRenderer buildFieldColumnCellRenderer() {
		return new SimpleTableCellRenderer() {
			protected String buildText(Object value) {
				return value == null ? null : ((MWDataField) value).fieldName();
			}
		};
	}
	
	private TableCellRenderer buildTransformerTableCellRenderer() {
		return new AdaptableTableCellRenderer(new TransformerCellRendererAdapter(this.resourceRepository()));
	}
	
	private JButton buildAddFieldTransformerAssociationButton() {
		JButton button = new JButton(this.resourceRepository().getString("TRANSFORMATION_MAPPING_FIELD_TRANSFORMER_ADD_BUTTON"));
		button.addActionListener(this.buildAddFieldTransformerAssociationAction());
		return button;
	}
	
	protected abstract ActionListener buildAddFieldTransformerAssociationAction();
	
	private JButton buildEditFieldTransformerAssociationButton() {
		JButton button = new JButton(this.resourceRepository().getString("TRANSFORMATION_MAPPING_FIELD_TRANSFORMER_EDIT_BUTTON"));
		button.addActionListener(this.buildEditFieldTransformerAssociationAction());
		this.fieldTransformerAssociationsSelectionModel.addListSelectionListener(this.buildListenerForEditFieldTransformerAssociation(button));
		button.setEnabled(false);
		return button;
	}
	
	protected abstract ActionListener buildEditFieldTransformerAssociationAction();
	
	private ListSelectionListener buildListenerForEditFieldTransformerAssociation(final JButton editButton) {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if ( ! e.getValueIsAdjusting()) {
					boolean ableToEdit = 
						FieldTransformerAssociationsPanel.this.fieldTransformerAssociationsSelectionModel.getSelectedValues().length == 1;
					editButton.setEnabled(ableToEdit);
				}
			}
		};
	}
	
	private JButton buildRemoveFieldTransformerAssociationsButton() {
		JButton button = new JButton(this.resourceRepository().getString("TRANSFORMATION_MAPPING_FIELD_TRANSFORMERS_REMOVE_BUTTON"));
		button.addActionListener(this.buildRemoveFieldTransformerAssociationsAction());
		this.fieldTransformerAssociationsSelectionModel.addListSelectionListener(this.buildListenerForRemoveFieldTransformerAssociations(button));
		button.setEnabled(false);
		return button;
	}
	
	private ActionListener buildRemoveFieldTransformerAssociationsAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				FieldTransformerAssociationsPanel.this.removeSelectedFieldTransformerAssociations();
			}
		};
	}
	
	private ListSelectionListener buildListenerForRemoveFieldTransformerAssociations(final JButton removeButton) {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if ( ! e.getValueIsAdjusting()) {
					boolean ableToRemove = 
						! FieldTransformerAssociationsPanel.this.fieldTransformerAssociationsSelectionModel.isSelectionEmpty();
					removeButton.setEnabled(ableToRemove);
				}
			}
		};
	}
	
	private void removeSelectedFieldTransformerAssociations() {
		for (Iterator stream = CollectionTools.iterator(this.fieldTransformerAssociationsSelectionModel.getSelectedValues()); stream.hasNext(); ) {
			((MWTransformationMapping) this.subject()).removeFieldTransformerAssociation((MWFieldTransformerAssociation) stream.next());
		}
	}
	
	
	// **************** Convenience *******************************************
	
	protected MWFieldTransformerAssociation selectedFieldTransformerAssociation() {
		return (MWFieldTransformerAssociation) this.fieldTransformerAssociationsSelectionModel.getSelectedValue();
	}
	
	
	// **************** Member classes ****************************************
	
	protected static class FieldTransformationAssociationsColumnAdapter
		implements ColumnAdapter
	{
		protected ResourceRepository resourceRepository;
		public static final int COLUMN_COUNT 		= 2;
		
		public static final int FIELD_COLUMN 		= 0;
		public static final int TRANSFORMER_COLUMN 	= 1;
		
		public int getColumnCount() {
			return COLUMN_COUNT;
		}
		
		protected FieldTransformationAssociationsColumnAdapter(ResourceRepository resourceRepository) {
			super();
			this.resourceRepository = resourceRepository;
		}
		
		public String getColumnName(int index) {
			switch (index) {
				case FIELD_COLUMN: 
					return this.resourceRepository.getString("TRANSFORMATION_MAPPING_FIELD_COLUMN_LABEL");
					
				case TRANSFORMER_COLUMN: 
					return this.resourceRepository.getString("TRANSFORMATION_MAPPING_TRANSFORMER_COLUMN_LABEL");
			}
			
			return "";
		}
		
		public Class getColumnClass(int index) {
			switch (index) {
				case FIELD_COLUMN:
					return MWDataField.class;
				case TRANSFORMER_COLUMN:
					return MWTransformer.class;
			}
			
			return null;
		}
		
		public boolean isColumnEditable(int index) {
			return false;
		}
		
		public PropertyValueModel[] cellModels(Object subject) {
			MWFieldTransformerAssociation association = (MWFieldTransformerAssociation) subject;
			PropertyValueModel[] cellModels = new PropertyValueModel[COLUMN_COUNT];
			
			cellModels[FIELD_COLUMN] = this.buildFieldAdapter(association);
			cellModels[TRANSFORMER_COLUMN] = this.buildTransformerAdapter(association);
			
			return cellModels;
		}
		
		protected PropertyValueModel buildFieldAdapter(MWFieldTransformerAssociation association) {
			PropertyValueModel adapter = new PropertyAspectAdapter(MWFieldTransformerAssociation.FIELD_PROPERTY, association) {
				protected Object getValueFromSubject() {
					return ((MWFieldTransformerAssociation) this.subject).getField();
				}
			};
			return new ValuePropertyPropertyValueModelAdapter(adapter, MWDataField.FIELD_NAME_PROPERTY);
		}
		
		private PropertyValueModel buildTransformerAdapter(MWFieldTransformerAssociation association) {
			PropertyValueModel adapter = new PropertyAspectAdapter(MWFieldTransformerAssociation.FIELD_TRANSFORMER_PROPERTY, association) {
				protected Object getValueFromSubject() {
					return ((MWFieldTransformerAssociation) this.subject).getFieldTransformer();
				}
			};
			return new ValuePropertyPropertyValueModelAdapter(adapter, MWTransformer.TRANSFORMER_PROPERTY);
			// TODO we need to figure out a way to listen for the class name to change *or* the method signature to change... :-(
			// that will probably require some refactoring of MWClass and MWMethod to implement some common interface
			// and throw a common event...
		}
	}
}
