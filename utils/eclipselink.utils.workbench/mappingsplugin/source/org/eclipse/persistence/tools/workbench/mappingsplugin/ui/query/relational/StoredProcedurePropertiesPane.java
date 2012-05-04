/*
 * Copyright (c) 2007, 2008, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveTablePanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractProcedureArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWProcedure;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWProcedureNamedInArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWProcedureNamedInOutputArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWProcedureNamedOutputArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWProcedureUnamedInArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWProcedureUnamedInOutputArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWProcedureUnamedOutputArgument;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.SwitcherPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.Transformer;

/**
 * 
 * @see MWProcedure
 * @see StoredProcedurePropertiesPage – The parent container
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Les Davis
 */
final class StoredProcedurePropertiesPane extends AbstractSubjectPanel
{
	private PropertyValueModel selectedArgumentHolder;
	
	private ParameterInArgumentPane namedInPane;
	private ValueInArgumentPane namedInValuePane;
	private ParameterOutArgumentPane namedOutPane;
	private ParameterInOutArgumentPane namedInOutPane;
	private ValueInOutArgumentPane namedInOutValuePane; 
	private ParameterInArgumentPane unnamedInPane;
	private ValueInArgumentPane unnamedInValuePane;
	private ParameterOutArgumentPane unnamedOutPane;
	private ParameterInOutArgumentPane unnamedInOutPane;
	private ValueInOutArgumentPane unnamedInOutValuePane;
	
	private PropertyValueModel useUnnamedOutputCursorHolder;
	
	/**
	 * Creates a new <code>StoredProcedurePropertiesPane</code>.
	 *
	 * @param subjectHolder The holder of <code>MWProcedure</code>
	 * @param workbenchContextHolder The holder of the <code>WorkbenchContext</code>,
	 * used to retrieve the localized string, active window, etc
	 */
	StoredProcedurePropertiesPane(PropertyValueModel procedureHolder,
	                              WorkbenchContextHolder workbenchContextHolder)
	{
		super(procedureHolder, workbenchContextHolder);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		this.selectedArgumentHolder = new SimplePropertyValueModel();

		JComponent nameField = buildLabeledTextField("STORED_PROCEDURE_PROPERTIES_PAGE_PROCEDURE_NAME", buildNameDocument());

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		add(nameField, constraints);
		
		JCheckBox useUnamedOutputCursorCheckbox = buildCheckBox("STORED_PROCEDURE_PROPERTIES_PAGE_USE_UNNAMED_OUPUT_CURSOR", buildUnnamedOuputCursorCheckboxModel());
		
		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);
		
		add(useUnamedOutputCursorCheckbox, constraints);
		
		JComponent outputCursorNameWidgets = buildLabeledTextField("STORED_PROCEDURE_PROPERTIES_PAGE_OUTPUT_CURSOR_NAME", buildOutputCursorNameDocument());
		
		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 10, 0, 0);
		
		add(outputCursorNameWidgets, constraints);
				
		new ComponentEnabler(buildReverseHolder(this.useUnnamedOutputCursorHolder), outputCursorNameWidgets);		
		
		JPanel argumentPanel = new JPanel(new GridBagLayout());
		argumentPanel.setOpaque(false);

		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 15, 0, 0);
		
		add(argumentPanel, constraints);

			AddRemoveListPanel argumentsListPane = buildArgumentsListPanel();
			argumentsListPane.setPreferredSize(new Dimension(175, 200));
	
			constraints.gridx      = 0;
			constraints.gridy      = 0;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 0;
			constraints.weighty    = 1;
			constraints.fill       = GridBagConstraints.VERTICAL;
			constraints.anchor     = GridBagConstraints.PAGE_START;
			constraints.insets     = new Insets(1, 0, 0, 0);
	
			argumentPanel.add(argumentsListPane, constraints);
		
				JComponent container = new JPanel(new GridBagLayout());
				container.setOpaque(false);
		
				constraints.gridx      = 1;
				constraints.gridy      = 0;
				constraints.gridwidth  = 1;
				constraints.gridheight = 2;
				constraints.weightx    = 1;
				constraints.weighty    = 0;
				constraints.fill       = GridBagConstraints.BOTH;
				constraints.anchor     = GridBagConstraints.PAGE_START;
				constraints.insets     = new Insets(0, 10, 0, 0);
		
				argumentPanel.add(container, constraints);
			
				// type widgets
				JTextField typeField = new JTextField(buildArgumentTypeDocument(selectedArgumentHolder), null, 20);
				typeField.setEditable(false);
				JComponent typeWidgets = buildLabeledComponent("ARGUMENT_TYPE_LABEL", typeField);
		
				constraints.gridx      = 0;
				constraints.gridy      = 0;
				constraints.gridwidth  = 1;
				constraints.gridheight = 1;
				constraints.weightx    = 1;
				constraints.weighty    = 0;
				constraints.fill       = GridBagConstraints.HORIZONTAL;
				constraints.anchor     = GridBagConstraints.LINE_START;
				constraints.insets     = new Insets(15, 0, 0, 0);
		
				container.add(typeWidgets, constraints);
		
				initializePanes();
				
				// Properties pane
				SwitcherPanel argumentsPanel = new SwitcherPanel
				(
					selectedArgumentHolder,
					buildArgumentTypeTransformer()
				);
		
				constraints.gridx      = 0;
				constraints.gridy      = 1;
				constraints.gridwidth  = 1;
				constraints.gridheight = 1;
				constraints.weightx    = 1;
				constraints.weighty    = 1;
				constraints.fill       = GridBagConstraints.BOTH;
				constraints.anchor     = GridBagConstraints.LINE_START;
				constraints.insets     = new Insets(10, 0, 0, 0);
		
				container.add(argumentsPanel, constraints);
				
		Spacer endSpace = new Spacer();
		
		constraints.gridx      = 0;
		constraints.gridy      = 4;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 15, 0, 0);

		add(endSpace, constraints);
	}

	protected AddRemoveListPanel buildArgumentsListPanel() {
		AddRemoveListPanel argumentsListPanel = new AddRemoveListPanel(
			getApplicationContext(),
			buildAddRemoveListPanelAdapter(),
			buildArgumentListValueModel(),
			resourceRepository().getString("ARGUMENTS_LIST"));
		argumentsListPanel.setCellRenderer(buildArgumentsListCellRenderer());
		argumentsListPanel.setBorder(buildStandardEmptyBorder());
		argumentsListPanel.addListSelectionListener(buildArgumentListSelectionListener());
				
		return argumentsListPanel;
	}
	
	private AddRemoveListPanel.Adapter buildAddRemoveListPanelAdapter() {
		return new AddRemoveListPanel.Adapter() {
			
			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
				promptToAddAgument(listSelectionModel);
			}

			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
				removeSelectedArguments(listSelectionModel);
			}
		};
	}
	
	protected ListSelectionListener buildArgumentListSelectionListener() {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if ( ! e.getValueIsAdjusting()) {
					ObjectListSelectionModel listSelectionModel = (ObjectListSelectionModel) e.getSource();
					Object[] values = listSelectionModel.getSelectedValues();
					if (values.length == 1) {
						selectedArgumentHolder.setValue(values[0]);
					}
					else {
						selectedArgumentHolder.setValue(null);
					}
				}
			}
		};
	}
	
	private ListCellRenderer buildArgumentsListCellRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				if ("".equals(((MWAbstractProcedureArgument)value).getArgumentName())) {
					return resourceRepository().getString("UNNAMED_ARGUMENT");
				} else {
					return ((MWAbstractProcedureArgument)value).getArgumentName();
				}
			}
		};
	}
	
	private DocumentAdapter buildNameDocument() {
		return new DocumentAdapter(buildNameModel());
	}

	private PropertyValueModel buildNameModel() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWProcedure.NAME_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((MWProcedure)subject).getName();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((MWProcedure)subject).setName((String)value);
			}
		};
	}

	protected void promptToAddAgument(ObjectListSelectionModel listSelectionModel)
	{
		AddArgumentDialog.AddArgumentBuilder builder;

		builder = new AddArgumentDialog.AddArgumentBuilder();

		builder.setTitle(resourceRepository().getString("ADD_ARGUMENT_DIALOG_TITLE"));
		builder.setTextFieldDescription("ENTER_NEW_ARGUMENT_NAME");
		builder.setHelpTopicId("dialog.addNewArgument");
		//builder.setDescriptionTitle("ADD_ARGUMENT_DIALOG_DESCRIPTION_TITLE");
		//builder.setDescription("ADD_ARGUMENT_DIALOG_DESCRIPTION");

		AddArgumentDialog dialog = builder.buildDialog(getWorkbenchContext());
		dialog.show();
		
		if (dialog.wasConfirmed()) {
			AddArgumentDialog.ArgumentType argumentType = dialog.getArgumentType();
			String argumentName = dialog.getNewName();
			MWAbstractProcedureArgument newArgument;

			if (argumentType == AddArgumentDialog.ArgumentType.NAMED_IN) {
				newArgument = ((MWProcedure)subject()).addNamedInArgument(argumentName);
			}
			else if (argumentType == AddArgumentDialog.ArgumentType.NAMED_OUT) {
				newArgument = ((MWProcedure)subject()).addNamedOutputArgument(argumentName);
			}
			else if (argumentType == AddArgumentDialog.ArgumentType.NAMED_IN_OUT) {
				newArgument = ((MWProcedure)subject()).addNamedInOutputArgument(argumentName);
			}
			else if (argumentType == AddArgumentDialog.ArgumentType.UNNAMED_IN) {
				newArgument = ((MWProcedure)subject()).addUnamedInArgument();
			}
			else if (argumentType == AddArgumentDialog.ArgumentType.UNNAMED_OUT) {
				newArgument = ((MWProcedure)subject()).addUnamedOutputArgument();
			}
			else {
				newArgument = ((MWProcedure)subject()).addUnamedInOutputArgument();
			}

			if (dialog.getArgumentPassType() == AddArgumentDialog.ArgumentPassType.VALUE) {
				newArgument.setPassType(MWAbstractProcedureArgument.VALUE_TYPE);
			} else {
				newArgument.setPassType(MWAbstractProcedureArgument.PARAMETER_TYPE);
			}
			
			listSelectionModel.setSelectedValue(newArgument);			
		}
	}
	
	protected void removeSelectedArguments(ObjectListSelectionModel listSelectionModel)
	{
		Iterator arguments = CollectionTools.iterator(listSelectionModel.getSelectedValues());

		while (arguments.hasNext())
		{
			((MWProcedure)subject()).removeArgument((MWAbstractProcedureArgument)arguments.next());
		}
	}

	private ListValueModel buildArgumentListValueModel() {
		return new CollectionListValueModelAdapter(buildArgumentsHolder());
	}
	
	private CollectionValueModel buildArgumentsHolder() {
		return new CollectionAspectAdapter(getSubjectHolder(), MWProcedure.ARGUMENT_COLLECTION) {
			@Override
			protected Iterator getValueFromSubject() {
				return ((MWProcedure)subject).getAllArguments();
			}
			@Override
			protected int sizeFromSubject() {
				return ((MWProcedure)subject).argumentsSize();
			}
		};
	}

	private void initializePanes()
	{
		namedInPane = new ParameterInArgumentPane(
			buildNamedInArgumentHolder(),
			getWorkbenchContextHolder()
		);
		
		namedInValuePane = new ValueInArgumentPane(
			buildNamedInArgumentHolder(),
			getWorkbenchContextHolder()
		);

		namedOutPane = new ParameterOutArgumentPane(
			buildNamedOutArgumentHolder(),
			getWorkbenchContextHolder()
		);

		namedInOutPane = new ParameterInOutArgumentPane(
			buildNamedInOutArgumentHolder(),
			getWorkbenchContextHolder()
		);
		
		namedInOutValuePane = new ValueInOutArgumentPane(
			buildNamedInOutArgumentHolder(),
			getWorkbenchContextHolder()
		);

		unnamedInPane = new ParameterInArgumentPane(
			buildUnnamedInArgumentHolder(),
			getWorkbenchContextHolder()
		);
			
		unnamedInValuePane = new ValueInArgumentPane(
			buildUnnamedInArgumentHolder(),
			getWorkbenchContextHolder()
		);

		unnamedOutPane = new ParameterOutArgumentPane(
			buildUnnamedOutputArgumentHolder(),
			getWorkbenchContextHolder()
		);

		unnamedInOutPane = new ParameterInOutArgumentPane(
			buildUnnamedInOutputArgumentHolder(),
			getWorkbenchContextHolder()
		);
			
		unnamedInOutValuePane = new ValueInOutArgumentPane(
			buildUnnamedInOutputArgumentHolder(),
			getWorkbenchContextHolder()
		);

	}

	private Transformer buildArgumentTypeTransformer() {
		return new Transformer() {
			private JComponent emptyPane;

			private JComponent emptyPane() {
				if (emptyPane == null) {
					emptyPane = buildEmptyPanel();
				}

				return emptyPane;
			}

			private JComponent namedInPane() {
				return namedInPane;
			}
			
			private JComponent namedInValuePane() {
				return namedInValuePane;
			}

			private JComponent namedOutPane() {
				return namedOutPane;
			}

			private JComponent namedInOutPane() {
				return namedInOutPane;
			}

			private JComponent namedInOutValue() {
				return namedInOutValuePane;
			}
			
			private JComponent unnamedInPane() {
				return unnamedInPane;
			}

			private JComponent unnamedInValuePane() {
				return unnamedInValuePane;
			}
			
			private JComponent unnamedOutPane() {
				return unnamedOutPane;
			}

			private JComponent unnamedInOutPane() {
				return unnamedInOutPane;
			}
			
			private JComponent unnamedInOutValuePane() {
				return unnamedInOutValuePane;
			}

			public JComponent transform(Object argument) {
				if (argument == null) {
					return emptyPane();
				}
				MWAbstractProcedureArgument procedureArg = (MWAbstractProcedureArgument)argument;
				if (procedureArg.getPassType().equals(MWAbstractProcedureArgument.VALUE_TYPE)) {
					if (procedureArg.isNamedIn()) {
						return namedInValuePane();
					} else if (procedureArg.isNamedInOut()) {
						return namedInOutValue();
					} else if (procedureArg.isUnnamedIn()) {
						return unnamedInValuePane();
					} else if (procedureArg.isUnnamedInOut()) {
						return unnamedInOutValuePane();
					}
				} else {
					
					if (procedureArg.isNamedIn()) {
						return namedInPane();
					} else if (procedureArg.isNamedOut()) {
						return namedOutPane();
					} else if (procedureArg.isNamedInOut()) {
						return namedInOutPane();
					} else if (procedureArg.isUnnamedIn()) {
						return unnamedInPane();
					} else if (procedureArg.isUnnamedOut()) {
						return unnamedOutPane();
					} else if (procedureArg.isUnnamedInOut()) {
						return unnamedInOutPane();
					}
				}
				return emptyPane();
			}
		};
	}

	private PropertyValueModel buildNamedInArgumentHolder() {
		return new TransformationPropertyValueModel(this.selectedArgumentHolder) {			
			@Override
			protected Object transform(Object value) {
				return (value instanceof MWProcedureNamedInArgument) ? (MWProcedureNamedInArgument) value : null;
			}
		};
	}
	
	private PropertyValueModel buildNamedOutArgumentHolder() {
		return new TransformationPropertyValueModel(this.selectedArgumentHolder) {
			@Override
			protected Object transform(Object value) {
				return (value instanceof MWProcedureNamedOutputArgument) ? (MWProcedureNamedOutputArgument) value : null;
			}
		};
	}
	
	private PropertyValueModel buildNamedInOutArgumentHolder() {
		return new TransformationPropertyValueModel(this.selectedArgumentHolder) {
			@Override
			protected Object transform(Object value) {
				return (value instanceof MWProcedureNamedInOutputArgument) ? (MWProcedureNamedInOutputArgument) value : null;
			}
		};
	}
	
	private PropertyValueModel buildUnnamedInArgumentHolder() {
		return new TransformationPropertyValueModel(this.selectedArgumentHolder) {
			@Override
			protected Object transform(Object value) {
				return (value instanceof MWProcedureUnamedInArgument) ? (MWProcedureUnamedInArgument) value : null;
			}
		};
	}
	
	private PropertyValueModel buildUnnamedOutputArgumentHolder() {
		return new TransformationPropertyValueModel(this.selectedArgumentHolder) {
			@Override
			protected Object transform(Object value) {
				return (value instanceof MWProcedureUnamedOutputArgument) ? (MWProcedureUnamedOutputArgument) value : null;
			}
		};
	}
	
	private PropertyValueModel buildUnnamedInOutputArgumentHolder() {
		return new TransformationPropertyValueModel(this.selectedArgumentHolder) {
			@Override
			protected Object transform(Object value) {
				return (value instanceof MWProcedureUnamedInOutputArgument) ? (MWProcedureUnamedInOutputArgument) value : null;
			}
		};
	}
	
	protected JComponent buildEmptyPanel()
	{
		JPanel container = new JPanel();
		container.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(204, 204, 204)));
		container.setOpaque(false);

		return container;
	}

	protected final Document buildArgumentTypeDocument(PropertyValueModel argumentHolder)
	{
		return new DocumentAdapter(buildArgumentTypeHolder(argumentHolder));
	}

	private PropertyValueModel buildArgumentTypeHolder(PropertyValueModel argumentHolder)
	{
		return new TransformationPropertyValueModel(argumentHolder)
		{
			@Override
			protected String transform(Object argument)
			{
				if ((argument == null) || (subject() == null))
				{
					return resourceRepository().getString("ARGUMENT_NONE_SELECTED");
				}

				if (((MWAbstractProcedureArgument)argument).isNamedIn())
				{
					return resourceRepository().getString("ARGUMENT_NAMED_IN");
				}

				if (((MWAbstractProcedureArgument)argument).isNamedOut())
				{
					return resourceRepository().getString("ARGUMENT_NAMED_OUT");
				}

				if (((MWAbstractProcedureArgument)argument).isNamedInOut())
				{
					return resourceRepository().getString("ARGUMENT_NAMED_IN_OUT");
				}

				if (((MWAbstractProcedureArgument)argument).isUnnamedIn())
				{
					return resourceRepository().getString("ARGUMENT_UNNAMED_IN");
				}

				if (((MWAbstractProcedureArgument)argument).isUnnamedOut())
				{
					return resourceRepository().getString("ARGUMENT_UNNAMED_OUT");
				}

				else
				{
					return resourceRepository().getString("ARGUMENT_UNNAMED_IN_OUT");
				}
				
			}
		};
	}

	private CheckBoxModelAdapter buildUnnamedOuputCursorCheckboxModel() {
		this.useUnnamedOutputCursorHolder = buildUnnamedOutputCursorCheckboxModel();
		return new CheckBoxModelAdapter(this.useUnnamedOutputCursorHolder);
	}
	
	private PropertyValueModel buildUnnamedOutputCursorCheckboxModel() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWProcedure.USE_UNAMED_CURSOR_OUTPUT) {
			@Override
			protected Object getValueFromSubject() {
				return ((MWProcedure)subject).getUseUnamedCursorOutput();
			}
			
			@Override
			protected void setValueOnSubject(Object value) {
				((MWProcedure)subject).setUseUnamedCursorOutput((Boolean)value);
			}
		};
	}
	
	private PropertyValueModel buildReverseHolder(PropertyValueModel holder) {
		return new TransformationPropertyValueModel(holder) {
			@Override
			protected Object transform(Object value) {
				if (value == null) {
					return null;
				}
				if (((Boolean)value).booleanValue()) {
					return Boolean.FALSE;
				} else {
					return Boolean.TRUE;
				}
			}
		};
	}
	
	private DocumentAdapter buildOutputCursorNameDocument() {
		return new DocumentAdapter(buildOutputCursorNameModel());
	}

	private PropertyValueModel buildOutputCursorNameModel() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWProcedure.CURSOR_OUTPUT_NAME) {
			@Override
			protected Object getValueFromSubject() {
				return ((MWProcedure)subject).getCursorOutputName();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((MWProcedure)subject).setCursorOutputName((String)value);
			}
		};
	}

	private void selectNewArgument(final AddRemoveTablePanel tablePane,
	                               final ObjectListSelectionModel listSelectionModel,
	                               final MWAbstractProcedureArgument argument,
	                               final int count)
	{
		listSelectionModel.setSelectedValue(argument);
//		int rowIndex = count - 1;
//		tablePane.startEditing(rowIndex, 1);
	}
	
}