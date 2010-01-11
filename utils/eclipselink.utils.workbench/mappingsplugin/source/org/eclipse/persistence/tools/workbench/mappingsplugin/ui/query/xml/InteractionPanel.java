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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.xml;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.Pane;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisInteraction;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;



public final class InteractionPanel extends AbstractPanel {
	
	private PropertyValueModel eisInteractionHolder;
	
	private String helpTopicId;

	private JTable inputArgumentsTable;
	private JTable outputArgumentsTable;
	private JTable propertiesTable;
	
	private JButton addInputArgumentButton;
	private JButton removeInputArgumentButton;

	private JButton addPropertyButton;
	private JButton removePropertyButton;

	private JButton addOutputArgumentButton;
	private JButton removeOutputArgumentButton;

	public InteractionPanel(ApplicationContext context, PropertyValueModel interactionHolder, PropertyValueModel enabledBooleanHolder, String helpTopicId) {
		super(context);
		initialize(interactionHolder, enabledBooleanHolder, helpTopicId);
	}

	public InteractionPanel(ApplicationContext context, PropertyValueModel interactionHolder, String helpTopicId) {
		this(context, interactionHolder, null, helpTopicId);
	}

	public InteractionPanel(ApplicationContext context, PropertyValueModel interactionHolder, PropertyValueModel enabledBooleanHolder) {
		this(context, interactionHolder, enabledBooleanHolder, "descriptor.queryManager.customCalls");
	}

	public InteractionPanel(ApplicationContext context, PropertyValueModel interactionHolder) {
		this(context, interactionHolder, null, "descriptor.queryManager.customCalls");
	}


	protected void initialize(PropertyValueModel interactionModel, PropertyValueModel enabledBooleanHolder, String helpTopicId) {
		this.helpTopicId = helpTopicId;
		this.eisInteractionHolder = interactionModel;
		initializeLayout(enabledBooleanHolder);
	}
	
	public void initializeLayout(PropertyValueModel enabledBooleanHolder){
		Collection components = new ArrayList();
		GridBagConstraints constraints = new GridBagConstraints();
		
		Pane mainPage = new Pane(new GridBagLayout());
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(0, 0, 0, 0);		
		this.add(mainPage, constraints);
				
		JComboBox interactionTypeCombo = new JComboBox(new String[] {resourceRepository().getString("XML_INTERACTION")});
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 0, 5);
		mainPage.add(interactionTypeCombo, constraints);	
					
		JComponent functionNameComponent = buildLabeledTextField("FUNCTION_NAME", buildFunctionNameDocument(buildFunctionNameHolder()));
		CollectionTools.addAll(components, functionNameComponent.getComponents());
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(5, 5, 0, 5);
		mainPage.add(functionNameComponent, constraints);	
				
		JComponent inputRecordNameComponent = buildLabeledTextField("INPUT_RECORD_NAME", buildInputRecordDocument(buildInputRecordNameHolder()));
		CollectionTools.addAll(components, inputRecordNameComponent.getComponents());
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(5, 5, 0, 5);
		mainPage.add(inputRecordNameComponent, constraints);				
						
		JComponent inputRootElementNameComponent = buildLabeledTextField("INPUT_ROOT_ELEMENT_NAME", buildInputRootElementNameDocument(buildInputRootElementNameHolder()));
		CollectionTools.addAll(components, inputRootElementNameComponent.getComponents());
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(5, 5, 0, 5);
		mainPage.add(inputRootElementNameComponent, constraints);
	

		//////////////Input Arg table
		JLabel inputArgumentsLabel = buildLabel("INPUT_ARGUMENTS");
		components.add(inputArgumentsLabel);
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.gridheight = 2;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(5, 5, 0, 0);
		mainPage.add(inputArgumentsLabel, constraints);
		addAlignLeft(inputArgumentsLabel);


		// Create the parameter table
		inputArgumentsTable = buildInputArgumentsTable();
		components.add(inputArgumentsTable);
		constraints.gridx = 1;
		constraints.gridy = 4;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.insets = new Insets(5, 5, 0, 5);
		inputArgumentsTable.setPreferredScrollableViewportSize(new Dimension(25, 50));
		JScrollPane scrollPane = new JScrollPane(inputArgumentsTable);
		scrollPane.getViewport().setBackground(inputArgumentsTable.getBackground());
		mainPage.add(scrollPane, constraints);
		
			// Create the Add button
			this.addInputArgumentButton = buildButton("ADD_ARGUMENT_BUTTON");
			this.addInputArgumentButton.addActionListener(this.buildAddArgumentAction());
			
			// Create the Remove button
			this.removeInputArgumentButton = buildButton("REMOVE_ARGUMENT_BUTTON");
			this.removeInputArgumentButton.addActionListener(this.buildRemoveArgumentAction());
			this.removeInputArgumentButton.setEnabled(false);
			
		// Create the button panel
		Pane inputButtonPanel = new ButtonPane(inputArgumentsTable, addInputArgumentButton, removeInputArgumentButton);
		components.add(inputButtonPanel);

		constraints.gridx = 2;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.insets = new Insets(2, 5, 5, 5);
		mainPage.add(inputButtonPanel, constraints);	
		
		/////////////Output arg table
		JLabel outputArgumentsLabel = buildLabel("OUTPUT_ARGUMENTS");
		components.add(outputArgumentsLabel);
		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.gridwidth = 1;
		constraints.gridheight = 2;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(5, 5, 0, 0);
		mainPage.add(outputArgumentsLabel, constraints);			
		addAlignLeft(outputArgumentsLabel);
		
		
		// Create the parameter table
		outputArgumentsTable = buildOutputArgumentsTable();
		components.add(outputArgumentsTable);
		constraints.gridx = 1;
		constraints.gridy = 6;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 5, 0, 5);
		outputArgumentsTable.setPreferredScrollableViewportSize(new Dimension(25, 50));
		scrollPane = new JScrollPane(outputArgumentsTable);
		scrollPane.getViewport().setBackground(outputArgumentsTable.getBackground());
		mainPage.add(scrollPane, constraints);
		
			// Create the Add button
			this.addOutputArgumentButton = buildButton("ADD_ARGUMENT_BUTTON");
			this.addOutputArgumentButton.addActionListener(this.buildAddArgumentAction());
			
			// Create the Remove button
			this.removeOutputArgumentButton = buildButton("REMOVE_ARGUMENT_BUTTON");
			this.removeOutputArgumentButton.addActionListener(this.buildRemoveArgumentAction());
			this.removeOutputArgumentButton.setEnabled(false);
			
			// Create the button panel
			Pane outputButtonPanel = new ButtonPane(outputArgumentsTable, addOutputArgumentButton, removeOutputArgumentButton);
			components.add(outputButtonPanel);

		constraints.gridx = 2;
		constraints.gridy = 7;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.insets = new Insets(2, 5, 5, 5);
		mainPage.add(outputButtonPanel, constraints);	
					
		JComponent inputResultPathComponent = buildLabeledTextField("INPUT_RESULT_PATH", buildInputResultPathDocument(buildInputResultPathHolder()));
		CollectionTools.addAll(components, inputResultPathComponent.getComponents());
		constraints.gridx = 0;
		constraints.gridy = 8;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(5, 5, 0, 5);
		mainPage.add(inputResultPathComponent, constraints);		
				
		JComponent outputResultPathComponent = buildLabeledTextField("OUTPUT_RESULTS_PATH", buildOutputResultPathDocument(buildOutputResultPathHolder()));
		CollectionTools.addAll(components, outputResultPathComponent.getComponents());
		constraints.gridx = 0;
		constraints.gridy = 9;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(5, 5, 0, 5);
		mainPage.add(outputResultPathComponent, constraints);			

		///////////// properties table
		JLabel propertiesLabel = buildLabel("PROPERTIES");
		components.add(propertiesLabel);
		constraints.gridx = 0;
		constraints.gridy = 10;
		constraints.gridwidth = 1;
		constraints.gridheight = 2;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(5, 5, 0, 0);
		mainPage.add(propertiesLabel, constraints);			
		addAlignLeft(propertiesLabel);
		
		
		// Create the properties table
		this.propertiesTable = buildPropertiesTable();
		components.add(propertiesTable);
		constraints.gridx = 1;
		constraints.gridy = 10;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 5, 0, 5);
		propertiesTable.setPreferredScrollableViewportSize(new Dimension(25, 50));
		scrollPane = new JScrollPane(propertiesTable);
		scrollPane.getViewport().setBackground(propertiesTable.getBackground());
		mainPage.add(scrollPane, constraints);
		
			// Create the Add button
			this.addPropertyButton = buildButton("ADD_ARGUMENT_BUTTON");
			this.addPropertyButton.addActionListener(this.buildAddArgumentAction());
			
			// Create the Remove button
			this.removePropertyButton = buildButton("REMOVE_ARGUMENT_BUTTON");
			this.removePropertyButton.addActionListener(this.buildRemoveArgumentAction());
			this.removePropertyButton.setEnabled(false);
			
			// Create the button panel
			Pane propertiesButtonPanel = new ButtonPane(propertiesTable, addPropertyButton, removePropertyButton);
			components.add(propertiesButtonPanel);

		propertiesButtonPanel.setBorder(BorderFactory.createEmptyBorder());
		constraints.gridx = 2;
		constraints.gridy = 11;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.insets = new Insets(2, 5, 5, 5);
		mainPage.add(propertiesButtonPanel, constraints);	

		addHelpTopicId(mainPage, helpTopicId());
		
		if (enabledBooleanHolder != null) {
			new ComponentEnabler(enabledBooleanHolder, components);
		}
	}


	private static class ArgumentColumnAdapter 
		implements ColumnAdapter 
	{		
		private ResourceRepository resourceRepository;
			
		public final static int COLUMN_COUNT = 2;
		
		public final static int ARGUMENT_NAME_COLUMN = 0;
		public final static int FIELD_NAME_COLUMN = 1;
		
		private String[] COLUMN_NAME_KEYS;

		protected ArgumentColumnAdapter(ResourceRepository resourceRepository, String[] columnNameKeys) {
			super();
			this.resourceRepository = resourceRepository;
			this.COLUMN_NAME_KEYS = columnNameKeys;
		}
		
		public int getColumnCount() {
			return COLUMN_COUNT;
		}

		public String getColumnName(int index) {
			return this.resourceRepository.getString(COLUMN_NAME_KEYS[index]);
		}

		public Class getColumnClass(int index) {
			switch (index) {
				case ARGUMENT_NAME_COLUMN:	return String.class;
				case FIELD_NAME_COLUMN:		return String.class;

				default: 					return Object.class;
			}
		}

		public boolean isColumnEditable(int columnIndex) {
			return true;
		}
		
		public PropertyValueModel[] cellModels(Object subject) {
			MWEisInteraction.ArgumentPair argumentPair = (MWEisInteraction.ArgumentPair)subject;
			PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];

			result[ARGUMENT_NAME_COLUMN]	= this.buildArgumentNameAdapter(argumentPair);
			result[FIELD_NAME_COLUMN]	= this.buildArgumentFieldNameAdapter(argumentPair);

			return result;
		}

		private PropertyValueModel buildArgumentNameAdapter(MWEisInteraction.ArgumentPair argumentPair) {
			return new PropertyAspectAdapter(MWEisInteraction.ArgumentPair.ARGUMENT_NAME_PROPERTY, argumentPair) {
				protected Object getValueFromSubject() {
					return ((MWEisInteraction.ArgumentPair)subject).getArgumentName();
				}
				protected void setValueOnSubject(Object value) {
					((MWEisInteraction.ArgumentPair)subject).setArgumentName((String)value);
				}
			};
		}

		private PropertyValueModel buildArgumentFieldNameAdapter(MWEisInteraction.ArgumentPair argumentPair) {
			return new PropertyAspectAdapter(MWEisInteraction.ArgumentPair.ARGUMENT_FIELD_NAME_PROPERTY, argumentPair) {
				protected Object getValueFromSubject() {
					return ((MWEisInteraction.ArgumentPair)subject).getArgumentFieldName();
				}
				protected void setValueOnSubject(Object value) {
					((MWEisInteraction.ArgumentPair)subject).setArgumentFieldName((String)value);
				}
			};
		}
		
	}
	

	//*******  Function Name
	private PropertyValueModel buildFunctionNameHolder() {
		return new PropertyAspectAdapter(this.eisInteractionHolder, MWEisInteraction.FUNCTION_NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWEisInteraction) this.subject).getFunctionName();
			}

			protected void setValueOnSubject(Object value) {
				((MWEisInteraction) this.subject).setFunctionName((String)value);
			}
		};	
	}
	
	private DocumentAdapter buildFunctionNameDocument(PropertyValueModel functionNameHolder) {
		return new DocumentAdapter(functionNameHolder);	
	}

	//*******  Input Record Name
	private PropertyValueModel buildInputRecordNameHolder() {
		return new PropertyAspectAdapter(this.eisInteractionHolder, MWEisInteraction.INPUT_RECORD_NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWEisInteraction)subject).getInputRecordName();
			}

			protected void setValueOnSubject(Object value) {
				((MWEisInteraction)subject).setInputRecordName((String)value);
			}
		};	
	}
	
	private DocumentAdapter buildInputRecordDocument(PropertyValueModel inputRecordNameHolder) {
		return new DocumentAdapter(inputRecordNameHolder);	
	}

	//*******  Input Root Element Name
	private PropertyValueModel buildInputRootElementNameHolder() {
		return new PropertyAspectAdapter(this.eisInteractionHolder, MWEisInteraction.INPUT_ROOT_ELEMENT_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWEisInteraction)subject).getInputRootElementName();
			}

			protected void setValueOnSubject(Object value) {
				((MWEisInteraction)subject).setInputRootElementName((String)value);
			}
		};	
	}
	
	private DocumentAdapter buildInputRootElementNameDocument(PropertyValueModel inputRootElementNameHolder) {
		return new DocumentAdapter(inputRootElementNameHolder);	
	}

	//*******  Input Result Path
	private PropertyValueModel buildInputResultPathHolder() {
		return new PropertyAspectAdapter(this.eisInteractionHolder, MWEisInteraction.INPUT_RESULT_PATH_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWEisInteraction)subject).getInputResultPath();
			}

			protected void setValueOnSubject(Object value) {
				((MWEisInteraction)subject).setInputResultPath((String)value);
			}
		};	
	}
	
	private DocumentAdapter buildInputResultPathDocument(PropertyValueModel inputResultPathHolder) {
		return new DocumentAdapter(inputResultPathHolder);	
	}
	
	//*******  Output Result Path
	private PropertyValueModel buildOutputResultPathHolder() {
		return new PropertyAspectAdapter(this.eisInteractionHolder, MWEisInteraction.OUTPUT_RESULT_PATH_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWEisInteraction)subject).getOutputResultPath();
			}

			protected void setValueOnSubject(Object value) {
				((MWEisInteraction)subject).setOutputResultPath((String)value);
			}
		};	
	}
	
	private DocumentAdapter buildOutputResultPathDocument(PropertyValueModel outputResultPathHolder) {
		return new DocumentAdapter(outputResultPathHolder);	
	}
		
	protected ListSelectionListener buildListSelectionHandler() 
	{
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if ( ! e.getValueIsAdjusting()) {
					updateRemoveArgumentButton();
				}
			}
		};
	}	

	private void updateRemoveArgumentButton() 
	{
		boolean argsSelected = this.inputArgumentsTable.getSelectedRows().length > 0;
		this.removeInputArgumentButton.setEnabled(argsSelected);
	
		argsSelected = this.outputArgumentsTable.getSelectedRows().length > 0;
		this.removeOutputArgumentButton.setEnabled(argsSelected);
		
		argsSelected = this.propertiesTable.getSelectedRows().length > 0;
		this.removePropertyButton.setEnabled(argsSelected);
	
	}

	protected ActionListener buildAddArgumentAction() 
	{
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(ae.getSource() == addInputArgumentButton) {
					addInputArgument();
				} else if (ae.getSource() == addOutputArgumentButton) {
					addOutputArgument();
				} else {
					addProperty();
				}
				
			}
		};
	}
	
	protected ActionListener buildRemoveArgumentAction()
	 {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(ae.getSource() == removeInputArgumentButton) {
					removeSelectedInputArguments();
				} else if (ae.getSource() == removeOutputArgumentButton) {
					removeSelectedOutputArguments();
				} else {
					removeSelectedProperty();
				}
			}
		};
	}
	
	protected void addInputArgument(){
		

		getEisInteraction().addInputArgument();

		
		int numRows = getEisInteraction().inputArgumentsSize() - 1;
		JTable table = inputArgumentsTable;
		table.setRowSelectionInterval(numRows, numRows);
		table.editCellAt(numRows, ArgumentColumnAdapter.ARGUMENT_NAME_COLUMN);
		((JTextField) table.getEditorComponent()).grabFocus();
		((JTextField) table.getEditorComponent()).selectAll();
	}
	
	protected void addOutputArgument(){

		getEisInteraction().addOutputArgument();

		int numRows = getEisInteraction().outputArgumentsSize() - 1;
		JTable table = outputArgumentsTable;
		table.setRowSelectionInterval(numRows, numRows);
		table.editCellAt(numRows, ArgumentColumnAdapter.ARGUMENT_NAME_COLUMN);
		((JTextField) table.getEditorComponent()).grabFocus();
		((JTextField) table.getEditorComponent()).selectAll();
	}

	protected void addProperty(){

		getEisInteraction().addProperty();

		int numRows = getEisInteraction().propertySize() - 1;
		JTable table = propertiesTable;
		table.setRowSelectionInterval(numRows, numRows);
		table.editCellAt(numRows, ArgumentColumnAdapter.ARGUMENT_NAME_COLUMN);
		((JTextField) table.getEditorComponent()).grabFocus();
		((JTextField) table.getEditorComponent()).selectAll();
	}

	protected void removeSelectedInputArguments() 
	{
		if (inputArgumentsTable.isEditing()) {
			inputArgumentsTable.getCellEditor().stopCellEditing();
		}
					
		// Remove the selected argument pair
		int[] selectedRows = inputArgumentsTable.getSelectedRows();		
		for (int index = selectedRows.length; --index >= 0;) {
			MWEisInteraction.ArgumentPair inputArgumentPair = getEisInteraction().getInputArgumentPair(selectedRows[index]);
			getEisInteraction().removeInputArgument(inputArgumentPair);
		}
	}	
	
	protected void removeSelectedOutputArguments() 
	{
		if (outputArgumentsTable.isEditing())
		outputArgumentsTable.getCellEditor().stopCellEditing();
					
		// Remove the selected argument pair
		int[] selectedRows = outputArgumentsTable.getSelectedRows();		
		for (int index = selectedRows.length; --index >= 0;) {
			MWEisInteraction.ArgumentPair outputArgumentPair = getEisInteraction().getOutputArgumentPair(selectedRows[index]);
			getEisInteraction().removeOutputArgument(outputArgumentPair);
		}
	}

	protected void removeSelectedProperty() 
	{
		if (propertiesTable.isEditing())
			propertiesTable.getCellEditor().stopCellEditing();
					
		// Remove the selected argument pair
		int[] selectedRows = propertiesTable.getSelectedRows();		
		for (int index = selectedRows.length; --index >= 0;) {
			MWEisInteraction.ArgumentPair property = getEisInteraction().getProperty(selectedRows[index]);
			getEisInteraction().removeProperty(property);
		}
	}

	private JTable buildInputArgumentsTable() {
		// Create the table view
		inputArgumentsTable = SwingComponentFactory.buildTable(this.buildInputArgumentsTableModel());
		inputArgumentsTable.getSelectionModel().addListSelectionListener(this.buildListSelectionHandler());
		inputArgumentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				
		// interaction name column
		TableColumn argumentNameColumn = inputArgumentsTable.getColumnModel().getColumn(ArgumentColumnAdapter.ARGUMENT_NAME_COLUMN);
		argumentNameColumn.setMinWidth(122);
		
		// argument name column
		TableColumn fieldNameColumn = inputArgumentsTable.getColumnModel().getColumn(ArgumentColumnAdapter.FIELD_NAME_COLUMN);
		fieldNameColumn.setMinWidth(122);

		return inputArgumentsTable;
	}	
	
	private String[] argumentColumnNameKeys() {
		return new String[] {
			"ARGUMENT_NAME_COLUMN_HEADER",
			"ARGUMENT_FIELD_NAME_COLUMN_HEADER",
		};
	}

	private String[] propertyColumnNameKeys() {
		return new String[] {
			"PROPERTY_NAME",
			"PROPERTY_VALUE",
		};
	}

	private TableModel buildInputArgumentsTableModel() {
		return new TableModelAdapter(buildInputArgumentsAdapter(), buildArgumentTableColumnAdapter(argumentColumnNameKeys()));
	}

	private ColumnAdapter buildArgumentTableColumnAdapter(String[] columnNameKeys) {
		return new ArgumentColumnAdapter(resourceRepository(), columnNameKeys);	
	}
	
	private ListValueModel buildInputArgumentsAdapter() {
		return new ListAspectAdapter(eisInteractionHolder, MWEisInteraction.INPUT_ARGUMENTS_LIST) {
			protected ListIterator getValueFromSubject() {
				return ((MWEisInteraction)subject).inputArguments();
			}
		};
	}

	private JTable buildPropertiesTable() {
		// Create the table view
		propertiesTable = SwingComponentFactory.buildTable(buildPropertiesTableModel());
		propertiesTable.getSelectionModel().addListSelectionListener(this.buildListSelectionHandler());
		propertiesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		return propertiesTable;
	}	

	private TableModel buildPropertiesTableModel() {
		return new TableModelAdapter(buildPropertiesAdapter(), buildArgumentTableColumnAdapter(propertyColumnNameKeys()));
	}
	
	private ListValueModel buildPropertiesAdapter() {
		return new ListAspectAdapter(eisInteractionHolder, MWEisInteraction.PROPERTIES_LIST) {
			protected ListIterator getValueFromSubject() {
				return ((MWEisInteraction)subject).properties();
			}
		};
	}

	private JTable buildOutputArgumentsTable() {
		// Create the table view
		outputArgumentsTable = SwingComponentFactory.buildTable(buildOutputArgumentsTableModel());
		outputArgumentsTable.getSelectionModel().addListSelectionListener(this.buildListSelectionHandler());
		outputArgumentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		return outputArgumentsTable;
	}	

	private TableModel buildOutputArgumentsTableModel() {
		return new TableModelAdapter(buildOutputArgumentsAdapter(), buildArgumentTableColumnAdapter(argumentColumnNameKeys()));
	}
	
	private ListValueModel buildOutputArgumentsAdapter() {
		return new ListAspectAdapter(eisInteractionHolder, MWEisInteraction.OUTPUT_ARGUMENTS_LIST) {
			protected ListIterator getValueFromSubject() {
				return ((MWEisInteraction)subject).outputArguments();
			}
		};
	}

	public MWEisInteraction getEisInteraction(){		
		return (MWEisInteraction)this.eisInteractionHolder.getValue();		
	}
					
	public String helpTopicId() {
		return helpTopicId;
	}

	private class ButtonPane extends Pane {
		private JTable table;

		ButtonPane(JTable table, JButton addButton, JButton removeButton) {
			super(new GridLayout(1, 2, 5, 0));
			initialize(table, addButton, removeButton);
		}

		private void initialize(JTable table, JButton addButton, JButton removeButton) {
			this.table = table;
			this.add(addButton);
			this.add(removeButton);
		}

		protected void updateEnableStateOfChildren(boolean enabled) {
			getComponent(0).setEnabled(enabled);
			getComponent(1).setEnabled(enabled && (table.getSelectedRowCount() > 0));
		}
	}
}
