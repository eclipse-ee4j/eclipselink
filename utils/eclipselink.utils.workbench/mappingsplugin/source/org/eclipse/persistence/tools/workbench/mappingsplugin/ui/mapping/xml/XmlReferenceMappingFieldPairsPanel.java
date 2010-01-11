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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.ListIterator;
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

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFieldPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTableCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


final class XmlReferenceMappingFieldPairsPanel 
	extends AbstractSubjectPanel
{
	// **************** Variables *********************************************
	
	private ObjectListSelectionModel fieldPairsSelectionModel;
	
	
	// **************** Constructors ******************************************
	
	XmlReferenceMappingFieldPairsPanel(ValueModel subjectHolder, WorkbenchContextHolder context) {
		super(subjectHolder, context);
		addHelpTopicId(this, "mapping.eis.fieldPairs");
	}
	
	
	// **************** Initialization ****************************************
	
	@Override
	protected void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();

		// Field pairs table
		JComponent tablePanel = this.buildFieldPairsTablePanel();
		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);
		this.add(tablePanel, constraints);

		// Button panel
		JPanel buttonPanel = this.buildButtonPanel();
		constraints.gridx      = 1;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(0, 5, 0, 0);
		this.add(buttonPanel, constraints);
	}

	
	// **************** Field pairs table *************************************
	
	private JComponent buildFieldPairsTablePanel() {
		JTable table = this.buildFieldPairsTable();
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getViewport().setBackground(table.getBackground());
		scrollPane.getViewport().setMinimumSize(new Dimension(10, 10));
		scrollPane.getViewport().setPreferredSize(new Dimension(10, 10));
		
		return scrollPane;
	}
	
	private JTable buildFieldPairsTable() {
		ListValueModel listValue = this.buildFieldPairsValue();
		TableModel tableModel = this.buildFieldPairsTableModel(listValue);
		this.fieldPairsSelectionModel = this.buildFieldPairSelectionModel(listValue);
		
		JTable table = SwingComponentFactory.buildTable(tableModel, this.fieldPairsSelectionModel);
		
		TableColumn column;
		
		// source field column
		column = table.getColumnModel().getColumn(FieldPairsTableColumnAdapter.SOURCE_XPATH_COLUMN);
		column.setCellRenderer(this.buildXpathColumnCellRenderer());
		
		// target field column
		column = table.getColumnModel().getColumn(FieldPairsTableColumnAdapter.TARGET_XPATH_COLUMN);
		column.setCellRenderer(this.buildXpathColumnCellRenderer());
		
		this.addPropertyChangeListener("enabled" /* stupid hard-coded property name */, this.buildTableEnabler(table));
		
		return table;
	}
	
	private ListValueModel buildFieldPairsValue() {
		return new ListAspectAdapter(this.getSubjectHolder(), MWXmlReferenceMapping.XML_FIELD_PAIRS_LIST) {
			@Override
			public Object getItem(int index) {
				return ((MWXmlReferenceMapping) this.subject).xmlFieldPairAt(index);
			}
			
			@Override
			protected ListIterator getValueFromSubject() {
				return ((MWXmlReferenceMapping) subject).xmlFieldPairs();
			}
		};
	}
	
	private TableModel buildFieldPairsTableModel(ListValueModel fieldPairsValue) {
		return new TableModelAdapter(fieldPairsValue, this. buildFieldPairsTableColumnAdapter());
	}
	
	private ColumnAdapter buildFieldPairsTableColumnAdapter() {
		return new FieldPairsTableColumnAdapter(this.resourceRepository());
	}
	
	private ObjectListSelectionModel buildFieldPairSelectionModel(ListValueModel fieldPairsValue) {
		ObjectListSelectionModel selectionModel = new ObjectListSelectionModel(new ListModelAdapter(fieldPairsValue));
		selectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		return selectionModel;
	}
	
	private TableCellRenderer buildXpathColumnCellRenderer() {
		return new SimpleTableCellRenderer() {
			@Override
			protected String buildText(Object value) {
				if ("".equals(value)) {
					return XmlReferenceMappingFieldPairsPanel.this.resourceRepository().getString("NONE_SELECTED");
				}
				else {
					return (String) value;
				}
			}
		};
	}
	
	/** Sets the table enabled/disabled based on this panels enabled/disabled state */
	private PropertyChangeListener buildTableEnabler(final JTable table) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				table.setEnabled(((Boolean) evt.getNewValue()).booleanValue());
			}
		};
	}
	
	private JPanel buildButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 5));

		// Add button
		JButton addButton = this.buildAddFieldPairButton();
		buttonPanel.add(addButton);
		addAlignRight(addButton);

		// Edit button
		JButton editButton = this.buildEditFieldPairButton();
		buttonPanel.add(editButton);
		addAlignRight(editButton);

		// Remove button
		JButton removeButton = this.buildRemoveFieldPairsButton();	
		buttonPanel.add(removeButton);
		addAlignRight(removeButton);

		return buttonPanel;
	}
	
	private JButton buildAddFieldPairButton() {
		JButton addButton = this.buildButton("FIELD_PAIR_ADD_BUTTON");
		addButton.addActionListener(this.buildAddFieldPairAction());
		
		this.addPropertyChangeListener("enabled" /* stupid hard-coded property name */, this.buildAddButtonEnabler(addButton));
		
		return addButton;
	}
	
	private ActionListener buildAddFieldPairAction() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MWXmlReferenceMapping eisReferenceMapping = 
					(MWXmlReferenceMapping) XmlReferenceMappingFieldPairsPanel.this.getSubjectHolder().getValue();
				WorkbenchContext context = 
					XmlReferenceMappingFieldPairsPanel.this.getWorkbenchContext();
				
				XmlReferenceMappingFieldPairEditingDialog.promptToAddFieldPair(eisReferenceMapping, context);
			}
		};
	}
	
	/** Sets the addButton enabled/disabled based on this panels enabled/disabled state */
	private PropertyChangeListener buildAddButtonEnabler(final JButton button) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				button.setEnabled(((Boolean) evt.getNewValue()).booleanValue());
			}
		};
	}
	
	private JButton buildEditFieldPairButton() {
		JButton editButton = this.buildButton("FIELD_PAIR_EDIT_BUTTON");
		editButton.addActionListener(this.buildEditActionListener());
		editButton.setEnabled(false);
		this.buildEditFieldPairButtonEnabler(editButton);
		return editButton;
	}
	
	private ActionListener buildEditActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MWXmlFieldPair fieldPair = 
					(MWXmlFieldPair) XmlReferenceMappingFieldPairsPanel.this.fieldPairsSelectionModel.getSelectedValue();
				WorkbenchContext context = 
					XmlReferenceMappingFieldPairsPanel.this.getWorkbenchContext();
				
				XmlReferenceMappingFieldPairEditingDialog.promptToEditFieldPair(fieldPair, context);
			}
		};
	}
	
	private void buildEditFieldPairButtonEnabler(final JButton editButton) {
		this.fieldPairsSelectionModel.addListSelectionListener(
			new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if ( ! e.getValueIsAdjusting()) {
						editButton.setEnabled(XmlReferenceMappingFieldPairsPanel.this.fieldPairsSelectionModel.getSelectedValues().length == 1);
					}
				}
			}
		);
	}

	private JButton buildRemoveFieldPairsButton() {
		JButton removeButton = this.buildButton("FIELD_PAIR_REMOVE_BUTTON");
		removeButton.addActionListener(buildRemoveFieldPairsActionListener());
		removeButton.setEnabled(false);
		this.buildRemoveFieldPairsButtonEnabler(removeButton);
		return removeButton;
	}
	
	private ActionListener buildRemoveFieldPairsActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				XmlReferenceMappingFieldPairsPanel.this.removeSelectedFieldPairs();
			}
		};
	}
	
	private void removeSelectedFieldPairs() {
		for (Iterator stream = CollectionTools.iterator(this.fieldPairsSelectionModel.getSelectedValues()); stream.hasNext(); ) {
			((MWXmlReferenceMapping) this.subject()).removeXmlFieldPair((MWXmlFieldPair) stream.next());
		}
	}
	
	private void buildRemoveFieldPairsButtonEnabler(final JButton removeButton) {
		this.fieldPairsSelectionModel.addListSelectionListener(
			new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if ( ! e.getValueIsAdjusting()) {
						removeButton.setEnabled(! XmlReferenceMappingFieldPairsPanel.this.fieldPairsSelectionModel.isSelectionEmpty());
					}
				}
			}
		);
	}
	
	
	
	// **************** Member classes ****************************************
	
	private static class FieldPairsTableColumnAdapter 
		implements ColumnAdapter
	{
		private ResourceRepository resourceRepository;
		public static final int COLUMN_COUNT = 2;
		
		public static final int SOURCE_XPATH_COLUMN = 0;
		public static final int TARGET_XPATH_COLUMN = 1;
		
		public int getColumnCount() {
			return COLUMN_COUNT;
		}
		
		FieldPairsTableColumnAdapter(ResourceRepository resourceRepository) {
			super();
			this.resourceRepository = resourceRepository;
		}
		
		public String getColumnName(int index) {
			switch (index) {
				case SOURCE_XPATH_COLUMN: 
					return this.resourceRepository.getString("FIELD_PAIR_SOURCE_FIELD_COLUMN");
					
				case TARGET_XPATH_COLUMN: 
					return this.resourceRepository.getString("FIELD_PAIR_TARGET_FIELD_COLUMN");
			}
			
			return "";
		}
		
		public Class getColumnClass(int index) {
			return String.class;
		}
		
		public boolean isColumnEditable(int index) {
			return false;
		}
		
		public PropertyValueModel[] cellModels(Object subject) {
			MWXmlFieldPair fieldPair = (MWXmlFieldPair) subject;
			PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];
			
			result[SOURCE_XPATH_COLUMN] = this.buildXpathAdapter(fieldPair.getSourceXmlField());
			result[TARGET_XPATH_COLUMN] = this.buildXpathAdapter(fieldPair.getTargetXmlField());
			
			return result;
		}
		
		private PropertyValueModel buildXpathAdapter(MWXmlField xmlField) {
			return new PropertyAspectAdapter(MWXmlField.XPATH_PROPERTY, xmlField) {
				@Override
				protected Object getValueFromSubject() {
					return ((MWXmlField) this.subject).getXpath();
				}
			};
		}
	}
}
