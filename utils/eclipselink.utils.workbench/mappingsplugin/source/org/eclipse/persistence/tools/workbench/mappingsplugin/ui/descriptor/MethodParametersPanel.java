/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.awt.Component;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethodParameter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ClassCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ZeroArgConstructorPreference;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.SpinnerTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.TableCellEditorAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;



final class MethodParametersPanel 
	extends AbstractPanel
{
	private PropertyValueModel methodHolder;
	
	private ObjectListSelectionModel rowSelectionModel;
	private ListValueModel paramatersHolder;
	
	private JButton removeParameterButton;
	
	/**
	 * This action adds a new database field at the end of the table.
	 */
	private class AddParameterAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			MWClass type = ClassChooserTools.promptForType(
					MethodParametersPanel.this.getMethod().getRepository(),
					ClassChooserTools.buildDeclarableNonVoidFilter(),
					MethodParametersPanel.this.workbenchContext()
			);
			// the type will be null if the user pressed cancel or pressed OK but did not select a class
			if (type == null)
				return;
			
			if (getMethod().isZeroArgumentConstructor()) {
				getMethod().addMethodParameter(type);		
				optionallyAddZeroArgumentConstructor();
			}
			else if (type.isVoid()) {
				JOptionPane.showMessageDialog(currentWindow(),
												resourceRepository().getString("METHOD_PARAMETER_TYPE_VOID_WARNING.message"),
												resourceRepository().getString("METHOD_PARAMETER_TYPE_VOID_WARNING.title"),
												JOptionPane.WARNING_MESSAGE);
			}
			else {
				getMethod().addMethodParameter(type);
			}		
		}
		
	}

	// open access for inner class
	WorkbenchContext workbenchContext() {
		return this.getWorkbenchContext();
	}

	private class RemoveParameterAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			removeSelectedMethodParameters();
		}
	}
		
	MethodParametersPanel(PropertyValueModel methodHolder, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		this.methodHolder = methodHolder;
		initialize();
		initializeLayout();
	}
	private void initialize() {
		this.paramatersHolder = buildParametersAdapter();
		this.rowSelectionModel = buildRowSelectionModel();
	}
		
	private ObjectListSelectionModel buildRowSelectionModel() {
		ObjectListSelectionModel rowSelectionModel = new ObjectListSelectionModel(new ListModelAdapter(this.paramatersHolder));
		rowSelectionModel.addListSelectionListener(this.buildRowSelectionListener());
		return rowSelectionModel;
	}

	private ListSelectionListener buildRowSelectionListener() {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if ( ! e.getValueIsAdjusting()) {
					removeParameterButton.setEnabled(rowSelectionModel.getSelectedValue() != null);
				}
			}
		};
	}

	private ListValueModel buildParametersAdapter() {
		return new ListAspectAdapter(this.methodHolder, MWMethod.METHOD_PARAMETERS_LIST) {
			protected ListIterator getValueFromSubject() {
				return ((MWMethod) subject).methodParameters();
			}
			protected int sizeFromSubject() {
				return ((MWMethod) subject).methodParametersSize();
			}
		};
	}

	MWMethod getMethod() {
		return (MWMethod) methodHolder.getValue();
	}
	
	private MWClass getMWClass() {
		return (MWClass) getMethod().getMWParent();
	}

	private Iterator selectedParameters() {
		return CollectionTools.iterator(this.rowSelectionModel.getSelectedValues());
	}

	/**
	 * Initializes this Columns page.
	 */
	protected void initializeLayout() {		
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel mainPanel = this;

		// Create the buttons first
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
			// Add the Add New Database Field button
			JButton addParameterButton = buildButton("ADD_PARAMETER");
			addParameterButton.addActionListener(new AddParameterAction());
			addParameterButton.setEnabled(false);
			this.methodHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildMethodListener(addParameterButton));
			buttonPanel.add(addParameterButton);
	
			// Add the Remove Field button
			removeParameterButton = buildButton("REMOVE_PARAMETER");
			removeParameterButton.addActionListener(new RemoveParameterAction());
			removeParameterButton.setEnabled(false);
			buttonPanel.add(removeParameterButton);

		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_END;
		constraints.insets		= new Insets(5, 5, 0, 0);
			
		mainPanel.add(buttonPanel, constraints);

		// Create the table, listeners needs to have access to the buttons
		JTable table = this.buildParametersTable();
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.getViewport().setBackground(table.getBackground());
		scrollPane.getViewport().setPreferredSize(new Dimension(40, 30));
		table.addPropertyChangeListener("enabled", new PropertyChangeHandler(scrollPane));
		this.methodHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildMethodListener(table));

		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);

		mainPanel.add(scrollPane, constraints);
	}

	// ************ Parameters ************
	private JTable buildParametersTable() {
		JTable table = SwingComponentFactory.buildTable(buildParametersTableModel(), rowSelectionModel);
		int rowHeight = 20;	// start with minimum of 20
	
	
		TableColumn column = table.getColumnModel().getColumn(ParametersColumnAdapter.TYPE_COLUMN);
		column.setCellRenderer(buildTypeRenderer());

		column = table.getColumnModel().getColumn(ParametersColumnAdapter.DIMENSIONALITY_COLUMN);
		SpinnerTableCellRenderer spinnerRenderer = this.buildNumberSpinnerRenderer();
		column.setCellRenderer(spinnerRenderer);
		column.setCellEditor(new TableCellEditorAdapter(this.buildNumberSpinnerRenderer()));
		rowHeight = Math.max(rowHeight, spinnerRenderer.getPreferredHeight());

		table.setRowHeight(rowHeight);
		return table;
	}

	private SpinnerTableCellRenderer buildNumberSpinnerRenderer() {
		return new SpinnerTableCellRenderer(new NumberSpinnerModelAdapter(new SimplePropertyValueModel(), new Integer(0), null, new Integer(1), new Integer(0)));
	}

	private TableCellRenderer buildTypeRenderer() {
		return new AdaptableTableCellRenderer(new ClassCellRendererAdapter(resourceRepository()));
	}
	
	private TableModel buildParametersTableModel() {
		return new TableModelAdapter(this.paramatersHolder, buildParametersTableColumnAdapter());
	}
	
	private ColumnAdapter buildParametersTableColumnAdapter() {
		return new ParametersColumnAdapter(resourceRepository());
	}
	
	private void optionallyAddZeroArgumentConstructor() {
		ZeroArgConstructorPreference.optionallyAddZeroArgumentConstructor(this.getMWClass(), this.getWorkbenchContext());
	}
	
	protected void removeSelectedMethodParameters()  {
		getMethod().removeMethodParameters(selectedParameters());
	}


	// ********** classes **********

	private class PropertyChangeHandler implements PropertyChangeListener {
		private final JScrollPane scrollPane;
		private PropertyChangeHandler(JScrollPane scrollPane) {
			this.scrollPane = scrollPane;
		}
		public void propertyChange(PropertyChangeEvent e) {
			JTable table = (JTable) scrollPane.getViewport().getView();
			if (table.isEnabled())
				scrollPane.getViewport().setBackground(table.getBackground());
			else
				scrollPane.getViewport().setBackground(UIManager.getColor("control"));
		}
	}

	private static class ParametersColumnAdapter implements ColumnAdapter {
		
		private ResourceRepository resourceRepository;
		
		public static final int COLUMN_COUNT = 2;

		public static final int TYPE_COLUMN = 0;
		public static final int DIMENSIONALITY_COLUMN = 1;

		private static final String[] COLUMN_NAME_KEYS = new String[] {
			"TYPE_COLUMN_HEADER",
			"DIMENSIONALITY_COLUMN_HEADER",
		};

		private ParametersColumnAdapter(ResourceRepository repository) {
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
				case TYPE_COLUMN:				return Object.class;
				case DIMENSIONALITY_COLUMN:	return Integer.class;

				default: 						return Object.class;
			}
		}

		public boolean isColumnEditable(int index) {
			return index != TYPE_COLUMN;
		}

		public PropertyValueModel[] cellModels(Object subject) {
			MWMethodParameter methodParameter = (MWMethodParameter) subject;
			PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];

			result[TYPE_COLUMN]				= this.buildTypeAdapter(methodParameter);
			result[DIMENSIONALITY_COLUMN]	= this.buildDimensionalityAdapter(methodParameter);

			return result;
		}

		private PropertyValueModel buildTypeAdapter(MWMethodParameter methodParameter) {
			PropertyValueModel adapter = new PropertyAspectAdapter(MWMethodParameter.TYPE_PROPERTY, methodParameter) {
				protected Object getValueFromSubject() {
					return ((MWMethodParameter) subject).getType();
				}
				protected void setValueOnSubject(Object value) {
					((MWMethodParameter) subject).setType((MWClass) value);
				}
			};
			return new ValuePropertyPropertyValueModelAdapter(adapter, MWClass.NAME_PROPERTY);
		}
		
		private PropertyValueModel buildDimensionalityAdapter(MWMethodParameter methodParameter) {
			return new PropertyAspectAdapter(MWMethodParameter.DIMENSIONALITY_PROPERTY, methodParameter) {
				protected Object getValueFromSubject() {
					return new Integer(((MWMethodParameter) subject).getDimensionality());
				}
				protected void setValueOnSubject(Object value) {
					((MWMethodParameter) subject).setDimensionality(((Integer) value).intValue());
				}
			};
		}
	}
	
	private PropertyChangeListener buildMethodListener(final Component component) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				component.setEnabled(getMethod() != null);	
			}
		};	
	}

}
