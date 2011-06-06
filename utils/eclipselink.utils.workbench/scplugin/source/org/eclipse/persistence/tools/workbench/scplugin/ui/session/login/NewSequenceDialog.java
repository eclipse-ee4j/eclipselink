/*
 * Copyright (c) 2008, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.login;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog.Builder;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog.StateObject;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.scplugin.model.SequenceType;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;

/**
 * Here's the layout:
 * 
 * @see SequencingPane - The invoker of this dialog
 * @see SequenceStateObject
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class NewSequenceDialog extends NewNameDialog
{
	private JCheckBox defaultSequenceCheckBox;
	private PropertyValueModel sequenceTypeHolder;
	/** Holds all the settings used by the dialog when editing the name. */
	private Builder builder;

	/**
	 * Creates a new <code>NewSequenceDialog</code>.
	 *
	 * @param workbenchContext The <code>WorkbenchContext</code> used to retrieve
	 * the localized string, active window, etc
	 */
	private NewSequenceDialog(WorkbenchContext workbenchContext,
	                          Builder builder)
	{
		super(workbenchContext, builder);
		this.builder = builder;
	}

	/**
	 * Returns the inside margin of a group box, which is the insets used by the
	 * titled border with a 5 pixels for the left, bottom and right edges.
	 *
	 * @return The insets used to align the inside of a group box
	 */
	public static Insets groupBoxMargin() {
		Border titledBorder = BorderFactory.createTitledBorder("m");
		Insets offset = titledBorder.getBorderInsets(new JPanel());

		offset.left   += 5;
		offset.right  += 5;
		offset.bottom += 5;

		return offset;

	}

	@Override
	protected Component buildMainPanel() {
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel container = new JPanel(new GridBagLayout());

		JLabel descriptionPanel = new JLabel(resourceRepository().getString("NEW_SEQUENCE_DIALOG_DESCRIPTION"));
		
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);

		container.add(descriptionPanel, constraints);
		
		// New Name widgets
		JComponent newNameWidget = this.buildNewNameWidgets();

		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);

		container.add(newNameWidget, constraints);

		// Custom Pane
		Component customPane = buildCustomPane();

		constraints.gridx		= 0;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);

		container.add(customPane, constraints);

		return container;
	}
	
	protected JComponent buildNewNameWidgets() {
		Document document = this.buildDocumentWithStateObject();
		document.addDocumentListener(this.buildDocumentListener());
		this.textField = new JTextField(20);
		JComponent component = buildLabeledTextField(
			"NEW_SEQUENCE_DIALOG_NAME_LABEL",
			document,
			this.textField
		);
		component.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		return component;
	}
	
	@Override
	protected void editName() {
		String text = this.textField.getText();
		
		// empty string might not be allowed
		if (this.builder.emptyNameIsIllegal() && (text.length() == 0)) {
			this.setErrorMessageKey("NEW_NAME_DIALOG.EMPTY_VALUE");
			return;
		}

		boolean nameIsSameAsOriginal = this.namesMatch(text, this.builder.getOriginalName());
		// original name might be "illegal"
		if (this.builder.originalNameIsIllegal() && nameIsSameAsOriginal) {
			this.setErrorMessageKey("NEW_NAME_DIALOG.ORIGINAL_VALUE");
			return;
		}

		// check for "existing" name
		if (this.nameIsAlreadyTaken(text, nameIsSameAsOriginal)) {
			this.setErrorMessageKey("NEW_NAME_DIALOG.DUPLICATE_VALUE");
			return;
		}

		// check for "illegal" name
		if (this.nameIsIllegal(text)) {
			this.setErrorMessageKey("NEW_NAME_DIALOG.ILLEGAL_VALUE");
			return;
		}

		// no problems...
		this.clearErrorMessage();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Component buildCustomPane()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		JComponent container = new JPanel(new GridBagLayout());
		Insets groupBoxMargin = groupBoxMargin();
		sequenceTypeHolder = buildSequenceTypeHolder();

		// Default Sequence check box
		defaultSequenceCheckBox = SwingComponentFactory.buildCheckBox("NEW_SEQUENCE_DIALOG_DEFAULT_SEQUENCE_CHECK_BOX", buildDefaultSequenceCheckBoxModel(), resourceRepository());
		
		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, groupBoxMargin.left, 0, groupBoxMargin.right);

		container.add(defaultSequenceCheckBox, constraints);

		// Sequence Type container
		JComponent sequenceTypeContainer = new JPanel(new GridBagLayout());
		sequenceTypeContainer.setBorder(SwingComponentFactory.buildPaneTitledBorder("NEW_SEQUENCE_DIALOG_SEQUENCE_TYPE_GROUP_BOX", resourceRepository()));

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);

		container.add(sequenceTypeContainer, constraints);

		// Default radio button
		JRadioButton defaultRadioButton = SwingComponentFactory.buildRadioButton("NEW_SEQUENCE_DIALOG_DEFAULT_RADIO_BUTTON", buildDefaultRadioButtonModel(sequenceTypeHolder), resourceRepository());

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 20);

		sequenceTypeContainer.add(defaultRadioButton, constraints);

		// Native radio button
		JRadioButton nativeRadioButton = SwingComponentFactory.buildRadioButton("NEW_SEQUENCE_DIALOG_NATIVE_RADIO_BUTTON", buildNativeRadioButtonModel(sequenceTypeHolder), resourceRepository());

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 20);

		sequenceTypeContainer.add(nativeRadioButton, constraints);

		// Table radio button
		JRadioButton tableRadioButton = SwingComponentFactory.buildRadioButton("NEW_SEQUENCE_DIALOG_TABLE_RADIO_BUTTON", buildTableRadioButtonModel(sequenceTypeHolder), resourceRepository());

		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 20);

		sequenceTypeContainer.add(tableRadioButton, constraints);

		// Unary Table radio button
		JRadioButton unaryTableRadioButton = SwingComponentFactory.buildRadioButton("NEW_SEQUENCE_DIALOG_UNARY_TABLE_RADIO_BUTTON", buildUnaryTableRadioButtonModel(sequenceTypeHolder), resourceRepository());

		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 20);

		sequenceTypeContainer.add(unaryTableRadioButton, constraints);

		return container;
	}

	private ButtonModel buildDefaultRadioButtonModel(PropertyValueModel sequenceTypeHolder)
	{
		return new RadioButtonModelAdapter
		(
			sequenceTypeHolder,
			SequenceType.DEFAULT
		);
	}

	private ButtonModel buildDefaultSequenceCheckBoxModel()
	{
		return new CheckBoxModelAdapter
		(
			buildDefaultSequenceHolder()
		);
	}

	private PropertyValueModel buildDefaultSequenceHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), SequenceStateObject.DEFAULT_SEQUENCE_PROPERTY)
		{
			@Override
			protected Object getValueFromSubject()
			{
				SequenceStateObject subject = (SequenceStateObject) this.subject;
				return subject.isDefaultSequence();
			}

			@Override
			protected void setValueOnSubject(Object value)
			{
				SequenceStateObject subject = (SequenceStateObject) this.subject;
				subject.setDefaultSequence(((Boolean)value).booleanValue());
			}
		};
	}

	private ButtonModel buildNativeRadioButtonModel(PropertyValueModel sequenceTypeHolder)
	{
		return new RadioButtonModelAdapter
		(
			sequenceTypeHolder,
			SequenceType.NATIVE
		);
	}

	private PropertyValueModel buildSequenceTypeHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), SequenceStateObject.SEQUENCE_TYPE_PROPERTY)
		{
			@Override
			protected Object getValueFromSubject()
			{
				SequenceStateObject stateObject = (SequenceStateObject) subject;
				return stateObject.getSequenceType();
			}

			@Override
			protected void setValueOnSubject(Object value)
			{
				SequenceStateObject stateObject = (SequenceStateObject) subject;
				stateObject.setSequenceType((SequenceType)value);
			}
		};
	}

	private ButtonModel buildTableRadioButtonModel(PropertyValueModel sequenceTypeHolder)
	{
		return new RadioButtonModelAdapter
		(
			sequenceTypeHolder,
			SequenceType.TABLE
		);
	}

	private ButtonModel buildUnaryTableRadioButtonModel(PropertyValueModel sequenceTypeHolder)
	{
		return new RadioButtonModelAdapter
		(
			sequenceTypeHolder,
			SequenceType.UNARY_TABLE
		);
	}

	private ButtonModel buildXMLFileRadioButtonModel(PropertyValueModel sequenceTypeHolder)
	{
		return new RadioButtonModelAdapter
		(
			sequenceTypeHolder,
			SequenceType.XML_FILE
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String helpTopicId()
	{
		return "dialog.newSequence";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize()
	{
		super.initialize();
		setResizable(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareToShow()
	{
		super.prepareToShow();
		getOKAction().setEnabled(false);
	}

	static final class NewSequenceBuilder extends Builder
	{

		/**
		 * Creates a new <code>NewSequenceBuilder</code>.
		 *
		 * @param resourceRepository
		 * @param sequencing
		 */
		NewSequenceBuilder(ResourceRepository resourceRepository)
		{
			super();

			setTitle(resourceRepository.getString("NEW_SEQUENCE_DIALOG_TITLE"));
			setTextFieldDescription(resourceRepository.getString("NEW_SEQUENCE_DIALOG_DESCRIPTION"));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NewSequenceDialog buildDialog(WorkbenchContext context)
		{
			return (NewSequenceDialog) super.buildDialog(context);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected NewSequenceDialog buildDialog(WorkbenchContext workbenchContext,
		                                        Builder builder)
		{
			return new NewSequenceDialog(workbenchContext, builder);
		}
	}

	static final class SequenceStateObject extends NewNameDialog.StateObject
	{
		private boolean defaultSequence;
		private SequenceType sequenceType;

		static final String DEFAULT_SEQUENCE_PROPERTY = "defaultSequence";
		static final String SEQUENCE_TYPE_PROPERTY = "sequenceType";

		/**
		 * Creates a new <code>SequenceStateObject</code>.
		 *
		 * @param sequencing
		 * @param builder
		 */
		SequenceStateObject(Builder builder)
		{
			super(builder);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void editName(List<Problem> currentProblems)
		{
			// The default sequence can have an empty name
			if (!defaultSequence)
			{
				super.editName(currentProblems);
			}
		}

		SequenceType getSequenceType()
		{
			return sequenceType;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void initialize()
		{
			super.initialize();

			sequenceType  = SequenceType.DEFAULT;
		}

		boolean isDefaultSequence()
		{
			return defaultSequence;
		}

		void setDefaultSequence(boolean defaultSequence)
		{
			boolean oldDefaultSequence = this.defaultSequence;
			this.defaultSequence = defaultSequence;
			firePropertyChanged(DEFAULT_SEQUENCE_PROPERTY, oldDefaultSequence, defaultSequence);
		}

		void setSequenceType(SequenceType sequenceType)
		{
			SequenceType oldSequenceType = this.sequenceType;
			this.sequenceType = sequenceType;
			firePropertyChanged(SEQUENCE_TYPE_PROPERTY, oldSequenceType, sequenceType);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected StateObject buildStateObject()
	{
		return new SequenceStateObject(getBuilder());
	}
}
