package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractProcedureArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractProcedureInOutputArgument;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassRepositoryHolder;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.swing.Combo;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public final class ValueInOutArgumentPane extends AbstractSubjectPanel {

	public ValueInOutArgumentPane(ValueModel argumentHolder, WorkbenchContextHolder workbenchContextHolder) {
		super(argumentHolder, workbenchContextHolder);
	}
		
	@Override
	protected void initializeLayout() {
		
		GridBagConstraints constraints = new GridBagConstraints();


		JButton editButton = new JButton(buildEditAction());
		editButton.setText(resourceRepository().getString("STORED_PROCEDURE_PROPERTIES_PAGE_ARGUMENT_VALUE_EDIT_BUTTON"));
		
		JTextField argumentValueField = new JTextField(buildArgumentValueDocument(), null, 20);
		JComponent	argumentValueWidgets = buildLabeledComponent(
				"STORED_PROCEDURE_PROPERTIES_PAGE_ARGUMENT_VALUE_COLUMN",
				argumentValueField,
				editButton);
		argumentValueField.setEditable(false);
				
		constraints.gridx      	= 0;
		constraints.gridy      	= 0;
		constraints.gridwidth  	= 2;
		constraints.gridheight 	= 1;
		constraints.weightx    	= 1;
		constraints.weighty    	= 0;
		constraints.fill       	= GridBagConstraints.HORIZONTAL;
		constraints.anchor     	= GridBagConstraints.PAGE_START;
		constraints.insets 		= new Insets(10, 0, 0, 0);
		
		add(argumentValueWidgets, constraints);

		JComponent outFieldField = buildLabeledTextField("STORED_PROCEDURE_PROPERTIES_PAGE_OUTFIELD_NAME_COLUMN", buildOutFieldNameDocument());
		
		constraints.gridx      	= 0;
		constraints.gridy      	= 1;
		constraints.gridwidth  	= 2;
		constraints.gridheight 	= 1;
		constraints.weightx    	= 1;
		constraints.weighty    	= 0;
		constraints.fill       	= GridBagConstraints.HORIZONTAL;
		constraints.anchor     	= GridBagConstraints.PAGE_START;
		constraints.insets 		= new Insets(10, 0, 0, 0);
		
		add(outFieldField, constraints);
		
		JLabel javaTypeChooserLabel = buildLabel("STORED_PROCEDURE_PROPERTIES_PAGE_JAVA_CLASS_TYPE_COLUMN");
		
		constraints.gridx      	= 0;
		constraints.gridy      	= 2;
		constraints.gridwidth  	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx    	= 0;
		constraints.weighty    	= 0;
		constraints.fill       	= GridBagConstraints.HORIZONTAL;
		constraints.anchor     	= GridBagConstraints.PAGE_START;
		constraints.insets 		= new Insets(14, 0, 0, 2);

		add(javaTypeChooserLabel, constraints);

		JComponent javaTypeChooser = ClassChooserTools.buildPanel(buildClassTransformer(), 
				buildClassRepositoryHolder(),
				ClassChooserTools.buildDeclarableReferenceFilter(),
				getWorkbenchContextHolder());
		
		constraints.gridx      	= 1;
		constraints.gridy      	= 2;
		constraints.gridwidth  	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx    	= 1;
		constraints.weighty    	= 1;
		constraints.fill       	= GridBagConstraints.HORIZONTAL;
		constraints.anchor     	= GridBagConstraints.PAGE_START;
		constraints.insets 		= new Insets(10, 0, 0, 0);
		javaTypeChooserLabel.setLabelFor(javaTypeChooser);
		
		add(javaTypeChooser, constraints);
		
	}
	
	private Action buildEditAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ArgumentValueDialog dialog = new ArgumentValueDialog(getWorkbenchContext(), getSubjectHolder());

				dialog.show();
				
				if (dialog.wasConfirmed()) {
					((MWAbstractProcedureArgument)subject()).setArgumentValue(((ArgumentValueDialog)dialog).getArgumentValue());								
				}
			}
		};
	}
	
	private Document buildArgumentValueDocument() {
		return new DocumentAdapter(buildArgumentValueHolder());
	}
			
	private PropertyValueModel buildArgumentValueHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWAbstractProcedureArgument.ARGUMENT_VALUE_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((MWAbstractProcedureArgument)subject()).getArgumentValue();
			}
			@Override
			protected void setValueOnSubject(Object value) {
				((MWAbstractProcedureArgument)subject()).setArgumentValue((String)value);
			}
		};
	}
	
	private Document buildOutFieldNameDocument() {
		return new DocumentAdapter(buildArgumentOutFieldNameAdapter());
	}
	

	private PropertyValueModel buildArgumentOutFieldNameAdapter() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWAbstractProcedureInOutputArgument.OUT_FIELD_NAME_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((MWAbstractProcedureInOutputArgument)subject).getOutFieldName();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((MWAbstractProcedureInOutputArgument)subject).setOutFieldName((String)value);
			}
		};
	}

	protected PropertyValueModel buildClassTransformer() {
		return new TransformationPropertyValueModel(buildClassNameHolder()) {
			@Override
			protected Object transform(Object value) {
				if (value == null || "".equals((String)value) || subject() == null) {
					return null;
				}
				return ((MWAbstractProcedureArgument)subject()).typeNamed((String)value);
			}
			@Override
			protected Object reverseTransform(Object value) {
				if (value == null) {
					return null;
				}
				return ((MWClass)value).fullName();
			}
		};
	}
	
	protected PropertyValueModel buildClassNameHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWAbstractProcedureArgument.FIELD_JAVA_CLASS_NAME_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((MWAbstractProcedureArgument)subject).getFieldJavaClassName();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((MWAbstractProcedureArgument)subject).setFieldJavaClassName((String)value);
			}
		};
	}
	
	private ClassRepositoryHolder buildClassRepositoryHolder() {
		return new ClassRepositoryHolder() {
			public MWClassRepository getClassRepository() {
				return ((MWModel) ValueInOutArgumentPane.this.subject()).getRepository();
			}
		};
	}

	private class ArgumentValueDialog extends AbstractDialog {
		
		private JTextField argumentValueTextField;
		private Combo typeChooser;
		private ValueModel argumentHolder;
		
		private ArgumentValueDialog(WorkbenchContext context, ValueModel argumentHolder) {
			super(context);
	        this.argumentHolder = argumentHolder;
		}
	
		@Override
		protected AbstractSubjectPanel buildMainPanel() {
			return new DialogPane(getSubjectHolder(), getApplicationContext());
		}
		
		protected MWAbstractProcedureArgument argument() {
			return (MWAbstractProcedureArgument)this.argumentHolder.getValue();
		}
	
		protected String getInvalidInputMessageKey(Class javaClass, List<String> arguments) {
			String javaClassName = javaClass.getName();
	
			if (javaClassName.equals("java.lang.Boolean"))
				return "BE_EITHER_TRUE_OR_FALSE";
	
			if (javaClassName.equals("java.lang.Byte"))
				return "BE_BETWEEN_0_AND_127";
	
			if (javaClassName.equals("java.lang.Character"))
				return "BE_SINGLE_CHARACTER";
	
			if (javaClassName.equals("java.lang.Double"))
			{
				CollectionTools.addAll(arguments, new String[] { NumberFormat.getInstance().format(Double.MIN_VALUE), NumberFormat.getInstance().format(Double.MAX_VALUE) });
				return "BE_BETWEEN_DOUBLE";
			}
	
			if (javaClassName.equals("java.lang.Float"))
			{
				CollectionTools.addAll(arguments, new String[] { NumberFormat.getInstance().format(Float.MIN_VALUE), NumberFormat.getInstance().format(Float.MAX_VALUE) });
				return "BE_BETWEEN_FLOAT";
			}
	
			if (javaClassName.equals("java.lang.Integer"))
			{
				CollectionTools.addAll(arguments, new String[] { NumberFormat.getInstance().format(Integer.MIN_VALUE), NumberFormat.getInstance().format(Integer.MAX_VALUE) });
				return "BE_BETWEEN_INT";
			}
	
			if (javaClassName.equals("java.lang.Long"))
			{
				CollectionTools.addAll(arguments, new String[] { NumberFormat.getInstance().format(Long.MIN_VALUE), NumberFormat.getInstance().format(Long.MAX_VALUE) });
				return "BE_BETWEEN_LONG";
			}
	
			if (javaClassName.equals("java.lang.Short"))
			{
				CollectionTools.addAll(arguments, new String[] { NumberFormat.getInstance().format(Short.MIN_VALUE), NumberFormat.getInstance().format(Short.MAX_VALUE) });
				return "BE_BETWEEN_SHORT";
			}
	
			if (javaClassName.equals("java.lang.String"))
				return "BE_A_STRING";
	
			if (javaClassName.equals("java.math.BigDecimal"))
				return "CONTAINS_ONLY_DIGITS";
	
			if (javaClassName.equals("java.math.BigInteger"))
				return "CONTAINS_ONLY_DIGITS_AND";
	
			if (javaClassName.equals("java.sql.Date"))
				return "BE_IN_FORMAT1";
	
			if (javaClassName.equals("java.sql.Time"))
				return "BE_IN_FORMAT2";
	
			if (javaClassName.equals("java.sql.Timestamp"))
				return "BE_IN_FORMAT3";
	
			if (javaClassName.equals("java.util.Date"))
				return "BE_IN_FORMAT4";
	
			if (javaClassName.equals("java.util.Calendar"))
				return "BE_IN_FORMAT5";
	
			if (javaClass == byte[].class || javaClass == Byte[].class)
				return "BE_IN_FORMAT_BYTE_ARRAY";
	
			if (javaClass == char[].class || javaClass == Character[].class)
				return "BE_A_STRING";
	
			return "ILLEGAL_FORMAT";
		}
	
		@Override
		protected String helpTopicId() {
			return "storedprocedure.arguments.value.editDialog";
		}
	
		@Override
		public boolean preConfirm() {
			try {
				this.argument().buildValueFromString((String)this.typeChooser.getSelectedItem(), this.argumentValueTextField.getText());
			} catch (ConversionException ce) {
				showInvalidConversionDialog(ce);
				return false;
			}
			return true;
		}
		
		public String getArgumentValue() {
			return this.argumentValueTextField.getText();
		}
	
		private void showInvalidConversionDialog(ConversionException ce) {
			Vector<String> arguments = new Vector<String>();
			arguments.add(ce.getClassToConvertTo().getName());
			
			JOptionPane.showMessageDialog(getWorkbenchContext().getCurrentWindow(),
					resourceRepository().getString(getInvalidInputMessageKey(ce.getClassToConvertTo(), arguments), arguments.toArray()),
					resourceRepository().getString("ILLEGAL_FORMAT"),
					JOptionPane.WARNING_MESSAGE);
		}
	
		private void updateOKButton() {
			boolean enableOKButton = this.argumentValueTextField.getText() != null && !this.argumentValueTextField.getText().equals("");
			getOKAction().setEnabled(enableOKButton);
		}
	
		private class CVDocumentListener implements DocumentListener {
			public void changedUpdate(DocumentEvent de) {
				updateOKButton();
			}
			public void insertUpdate(DocumentEvent de) {
				updateOKButton();
			}
			public void removeUpdate(DocumentEvent de) {
				updateOKButton();
			}
		}
	
		private class DialogPane extends AbstractSubjectPanel {
			DialogPane(ValueModel subjectHolder, ApplicationContext context) {
				super(subjectHolder, context);
			}
			
			private ComboBoxModel buildValueTypeComboboxModel() {
				return new ComboBoxModelAdapter(buildValueTypeChooserValueModel(), buildValueTypeChooserPropertyAdapter());
			}
			
			private ListValueModel buildValueTypeChooserValueModel() {
				return new ListAspectAdapter(argumentHolder) {
					@Override
					protected ListIterator getValueFromSubject() {
						return MWAbstractProcedureArgument.buildBasicTypesList().listIterator();
					}
				};
			}
			
			private PropertyValueModel buildValueTypeChooserPropertyAdapter() {
				return new SimplePropertyValueModel();
			}
			
			@Override
			protected void initializeLayout() {
				setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				setTitle(resourceRepository().getString("STORED_PROCEDURE_PROPERTIES_PAGE_ARGUMENT_VALUE_EDIT_DIALOG_TITLE"));
	
				GridBagConstraints constraints = new GridBagConstraints();
	
				// Create the type chooser
				typeChooser = new Combo(buildValueTypeComboboxModel());
				JComponent argumentValueTypeSelector =  buildLabeledComponent(
														"STORED_PROCEDURE_PROPERTIES_PAGE_ARGUMENT_VALUE_CLASS_COLUMN",
														typeChooser);
				typeChooser.setSelectedIndex(8);
				typeChooser.setEditable(false);
				
				constraints.gridx 		= 0;
				constraints.gridy 		= 0;
				constraints.gridwidth 	= 1;
				constraints.gridheight 	= 1;
				constraints.weightx 	= 0;
				constraints.weighty 	= 0;
				constraints.fill 		= GridBagConstraints.HORIZONTAL;
				constraints.anchor 		= GridBagConstraints.LINE_START;
				constraints.insets 		= new Insets(5, 0, 0, 0);
				
				add(argumentValueTypeSelector, constraints);
				
				argumentValueTextField = new JTextField(20);
				JComponent argumentValueWidgets = buildLabeledComponent("STORED_PROCEDURE_PROPERTIES_PAGE_ARGUMENT_VALUE_COLUMN", argumentValueTextField);
				argumentValueTextField.getDocument().addDocumentListener(new CVDocumentListener());
				argumentValueTextField.setColumns(20);
				
				constraints.gridx 		= 0;
				constraints.gridy 		= 1;
				constraints.gridwidth 	= 1;
				constraints.gridheight 	= 1;
				constraints.weightx 	= 0;
				constraints.weighty 	= 0;
				constraints.fill 		= GridBagConstraints.HORIZONTAL;
				constraints.anchor 		= GridBagConstraints.LINE_START;
				constraints.insets 		= new Insets(5, 0, 0, 0);
	
				add(argumentValueWidgets, constraints);
			
				// Spacer
				Spacer spacer = new Spacer(-1);
	
				constraints.gridx 		= 0;
				constraints.gridy 		= 2;
				constraints.gridwidth 	= 2;
				constraints.gridheight 	= 1;
				constraints.weightx 	= 1;
				constraints.weighty 	= 1;
				constraints.fill 		= GridBagConstraints.VERTICAL;
				constraints.anchor 		= GridBagConstraints.CENTER;
				constraints.insets 		= new Insets(0, 0, 0, 0);
	
				add(spacer, constraints);
	
				updateOKButton();
			}
		}
	}
}
