package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.Pane;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

public final class AddArgumentDialog extends NewNameDialog {
	
	private PropertyValueModel argumentTypeHolder;
	private PropertyValueModel passTypeHolder;
	
	protected AddArgumentDialog(WorkbenchContext workbenchContext, Builder builder) {
		super(workbenchContext, builder);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AddArgumentBuilder builder() {
		return (AddArgumentBuilder) super.builder();
	}

	@Override
	protected Component buildMainPanel() {
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel container = new JPanel(new GridBagLayout());

		JLabel descriptionPanel = new JLabel(resourceRepository().getString("ADD_ARGUMENT_DIALOG_DESCRIPTION"));
		
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

		return container;	}

	protected JComponent buildNewNameWidgets() {
		Document document = this.buildDocumentWithStateObject();
		document.addDocumentListener(this.buildDocumentListener());
		this.textField = new JTextField(20);
		JComponent component = buildLabeledTextField(
			"ENTER_NEW_ARGUMENT_NAME",
			document,
			this.textField
		);
		component.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		new ComponentEnabler(buildIsNamedHolder(), component);
		
		return component;
	}

	protected Component buildCustomPane() {
		JComponent container = new Pane(new BorderLayout());
		container.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		container.add(buildArgumentTypePane(), BorderLayout.PAGE_START);
		container.add(buildPassTypePane(), BorderLayout.PAGE_END);
		return container;
	}


	private PropertyValueModel buildArgumentTypeHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(), ArgumentStateObject.ARGUMENT_TYPE_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				ArgumentStateObject stateObject = (ArgumentStateObject) subject;
				return stateObject.getArgumentType();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				ArgumentStateObject stateObject = (ArgumentStateObject) subject;
				stateObject.setArgumentType((ArgumentType)value);
				if(stateObject.isNamed()) {
					getOKAction().setEnabled(getNameInternal().length() > 0);
				} else {
					clearErrorMessage();
				}
			}
		};
	}
	
	private PropertyValueModel buildPassTypeHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(), ArgumentStateObject.ARGUMENT_PASS_TYPE_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				ArgumentStateObject stateObject = (ArgumentStateObject) subject;
				return stateObject.getArgumentPassType();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				ArgumentStateObject stateObject = (ArgumentStateObject) subject;
				stateObject.setArgumentPassType((ArgumentPassType)value);
			}
		};
	}
	
	private PropertyValueModel buildSupportsValueHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(), ArgumentStateObject.SUPPORTS_VALUE) {
			@Override
			protected Object getValueFromSubject() {
				ArgumentStateObject stateObject = (ArgumentStateObject) subject;
				return new Boolean(stateObject.supportsValue());
			}

			@Override
			protected void setValueOnSubject(Object value) {
				ArgumentStateObject stateObject = (ArgumentStateObject) subject;
				stateObject.setSupportsValue((Boolean)value);
			}
		};
	}
	
	private PropertyValueModel buildIsNamedHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(), ArgumentStateObject.IS_NAMED) {
			@Override
			protected Object getValueFromSubject() {
				ArgumentStateObject stateObject = (ArgumentStateObject) subject;
				return new Boolean(stateObject.isNamed());
			}

			@Override
			protected void setValueOnSubject(Object value) {
				ArgumentStateObject stateObject = (ArgumentStateObject) subject;
				stateObject.setIsNamed((Boolean)value);
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ArgumentStateObject buildStateObject() {
		return new ArgumentStateObject(builder());
	}

	ArgumentType getArgumentType() {
		// TODO: OJC BUG!!!
		return ((AddArgumentDialog.ArgumentStateObject)this.subject()).getArgumentType();
	}
	
	ArgumentPassType getArgumentPassType() {
		return ((AddArgumentDialog.ArgumentStateObject)this.subject()).getArgumentPassType();
	}
	
	boolean isOutType() {
		return ((AddArgumentDialog.ArgumentStateObject)this.subject()).supportsValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String helpTopicId() {
		return "descriptor.queryManager.procedures.arguments";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		//setDescription("ADD_ARGUMENT_DIALOG_DESCRIPTION");
		//setDescriptionTitle("ADD_ARGUMENT_DIALOG_DESCRIPTION_TITLE");
		setTitle(resourceRepository().getString("ADD_ARGUMENT_DIALOG_TITLE"));
		getOKAction().setEnabled(false);
	}
	
	protected final JComponent buildArgumentTypePane() {
		GridBagConstraints constraints = new GridBagConstraints();
		Collection<JComponent> components = new ArrayList<JComponent>(6);
		this.argumentTypeHolder = buildArgumentTypeHolder();
	
		JPanel container = new JPanel(new GridBagLayout());
		container.setBorder(buildTitledBorder("TYPE_BORDER_LABEL_ON_ADD_ARGUMENT_DIALOG"));
	
		// named in argument
		JRadioButton namedInArgumentRadioButton = buildRadioButton("NAMED_IN_RADIOBUTTON_ON_ADD_ARGUMENT_DIALOG", buildNamedInArgumentRadioButtonModel());
	
		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets = 	new Insets(0, 0, 0, 0);
	
		container.add(namedInArgumentRadioButton, constraints);
		components.add(namedInArgumentRadioButton);
	
		// named out
		JRadioButton namedOutRadioButton = buildRadioButton("NAMED_OUT_RADIOBUTTON_ON_ADD_ARGUMENT_DIALOG", buildNamedOutArgumentRadioButtonModel());
	
		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets 	= new Insets(0, 0, 0, 0);
	
		container.add(namedOutRadioButton, constraints);
		components.add(namedOutRadioButton);
	
		// named in out
		JRadioButton namedInOutRadioButton = buildRadioButton("NAMED_IN_OUT_RADIOBUTTON_ON_ADD_ARGUMENT_DIALOG", buildNamedInOutArgumentRadioButtonModel());
	
		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
	
		container.add(namedInOutRadioButton, constraints);
		components.add(namedInOutRadioButton);
	
		// unnamed in argument
		JRadioButton unnamedInArgumentRadioButton = buildRadioButton("UNNAMED_IN_RADIOBUTTON_ON_ADD_ARGUMENT_DIALOG", buildUnNamedInArgumentRadioButtonModel());
	
		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets = 	new Insets(0, 0, 0, 0);
	
		container.add(unnamedInArgumentRadioButton, constraints);
		components.add(unnamedInArgumentRadioButton);
	
		// unnamed out
		JRadioButton unnamedOutRadioButton = buildRadioButton("UNNAMED_OUT_RADIOBUTTON_ON_ADD_ARGUMENT_DIALOG", buildUnNamedOutArgumentRadioButtonModel());
	
		constraints.gridx      = 0;
		constraints.gridy      = 4;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets 	= new Insets(0, 0, 0, 0);
	
		container.add(unnamedOutRadioButton, constraints);
		components.add(unnamedOutRadioButton);
	
		// unnamed in out
		JRadioButton unnamedInOutRadioButton = buildRadioButton("UNNAMED_IN_OUT_RADIOBUTTON_ON_ADD_ARGUMENT_DIALOG", buildUnNamedInOutArgumentRadioButtonModel());
	
		constraints.gridx      = 0;
		constraints.gridy      = 5;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
	
		container.add(unnamedInOutRadioButton, constraints);
		components.add(unnamedInOutRadioButton);
	
		helpManager().addTopicID(container, helpTopicId() + ".type");
		return container;
	}

	private ButtonModel buildNamedInArgumentRadioButtonModel() {
		return new RadioButtonModelAdapter(argumentTypeHolder, ArgumentType.NAMED_IN);
	}
	
	private ButtonModel buildNamedInOutArgumentRadioButtonModel() {
		return new RadioButtonModelAdapter(argumentTypeHolder, ArgumentType.NAMED_IN_OUT);
	}
	
	private ButtonModel buildNamedOutArgumentRadioButtonModel() {
		return new RadioButtonModelAdapter(argumentTypeHolder, ArgumentType.NAMED_OUT);
	}
	
	private ButtonModel buildUnNamedInArgumentRadioButtonModel() {
		return new RadioButtonModelAdapter(argumentTypeHolder, ArgumentType.UNNAMED_IN);
	}
	
	private ButtonModel buildUnNamedInOutArgumentRadioButtonModel() {
		return new RadioButtonModelAdapter(argumentTypeHolder, ArgumentType.UNNAMED_IN_OUT);
	}
	
	private ButtonModel buildUnNamedOutArgumentRadioButtonModel() {
		return new RadioButtonModelAdapter(argumentTypeHolder, ArgumentType.UNNAMED_OUT);
	}
	
	protected final JComponent buildPassTypePane() {
		GridBagConstraints constraints = new GridBagConstraints();
		Collection<JComponent> components = new ArrayList<JComponent>(2);
		passTypeHolder = buildPassTypeHolder();
	
		JPanel container = new JPanel(new GridBagLayout());
		container.setBorder(buildTitledBorder("PASS_TYPE_BORDER_LABEL_ON_ADD_ARGUMENT_DIALOG"));
	
		// parameter
		JRadioButton parameterRadioButton = buildRadioButton("PARAMETER_RADIOBUTTON_ON_ADD_ARGUMENT_DIALOG", buildParameterPassTypeRadioButtonModel());
	
		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets = 	new Insets(0, 0, 0, 0);
	
		container.add(parameterRadioButton, constraints);
		components.add(parameterRadioButton);
	
		// value
		JRadioButton valueRadioButton = buildRadioButton("VALUE_RADIOBUTTON_ON_ADD_ARGUMENT_DIALOG", buildValuePassTypeRadioButtonModel());
	
		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets 	= new Insets(0, 0, 0, 0);
	
		container.add(valueRadioButton, constraints);
		components.add(valueRadioButton);
	
		new ComponentEnabler(buildSupportsValueHolder(), components);
		helpManager().addTopicID(container, helpTopicId() + ".passType");
		return container;
	}
	
	private ButtonModel buildParameterPassTypeRadioButtonModel() {
		return new RadioButtonModelAdapter(passTypeHolder, ArgumentPassType.PARAMETER);
	}

	private ButtonModel buildValuePassTypeRadioButtonModel() {
		return new RadioButtonModelAdapter(passTypeHolder, ArgumentPassType.VALUE);
	}

	static class AddArgumentBuilder extends Builder {

		AddArgumentBuilder() {
			super();
		}

		@Override
		public AddArgumentDialog buildDialog(WorkbenchContext context) {
			return (AddArgumentDialog) super.buildDialog(context);
		}

		@Override
		protected AddArgumentDialog buildDialog(WorkbenchContext context, Builder clone) {
			return new AddArgumentDialog(context, clone);
		}

	}

	class ArgumentStateObject extends NewNameDialog.StateObject {
		
		private ArgumentType argumentType;
			public static final String ARGUMENT_TYPE_PROPERTY = "argumentType";
			
		private ArgumentPassType argumentPassType;
			public static final String ARGUMENT_PASS_TYPE_PROPERTY = "argumentPassType";
			
		private boolean supportsValue;
			public static final String SUPPORTS_VALUE = "supportsValue";
			
		private boolean isNamed;
			public static final String IS_NAMED = "isNamed";
			
		ArgumentStateObject(Builder builder) {
			super(builder);
			this.argumentType = ArgumentType.NAMED_IN;
			this.argumentPassType = ArgumentPassType.PARAMETER;
			this.supportsValue = true;
			this.isNamed = true;
		}
		
		@Override
		protected void editName(List<Problem> currentProblems) {
			String text = getName();

			// empty string is not allowed
			if (getArgumentType() != ArgumentType.UNNAMED_IN 
					&& getArgumentType() != ArgumentType.UNNAMED_IN_OUT 
					&& getArgumentType() != ArgumentType.UNNAMED_OUT 
					&& StringTools.stringIsEmpty(text)) {
				currentProblems.add(buildProblem("NEW_NAME_DIALOG.EMPTY_VALUE"));
				return;
			}

			// no problems...
			 clearErrorMessage();		
		}

		public ArgumentType getArgumentType() {
			return argumentType;
		}

		public void setArgumentType(ArgumentType argumentType) {
			ArgumentType oldArgumentType = this.argumentType;
			this.argumentType = argumentType;
			firePropertyChanged(ARGUMENT_TYPE_PROPERTY, oldArgumentType, argumentType);
			if (this.argumentType == ArgumentType.NAMED_IN
					|| this.argumentType == ArgumentType.NAMED_IN_OUT
					|| this.argumentType == ArgumentType.UNNAMED_IN
					|| this.argumentType == ArgumentType.UNNAMED_IN_OUT) {
				this.setSupportsValue(true);
			} else {
				this.setSupportsValue(false);
			}
			
			if (this.argumentType == ArgumentType.NAMED_IN
					|| this.argumentType == ArgumentType.NAMED_IN_OUT
					|| this.argumentType == ArgumentType.NAMED_OUT) {
				this.setIsNamed(true);
			} else {
				this.setIsNamed(false);
			}
		}
		
		public ArgumentPassType getArgumentPassType() {
			return this.argumentPassType;
		}
		
		public void setArgumentPassType(ArgumentPassType argumentPassType) {
			ArgumentPassType oldArgumentPassType = this.argumentPassType;
			this.argumentPassType = argumentPassType;
			firePropertyChanged(ARGUMENT_PASS_TYPE_PROPERTY, oldArgumentPassType, argumentPassType);
		}
		
		public boolean supportsValue() {
			return supportsValue;
		}
		
		public void setSupportsValue(boolean newValue) {
			boolean oldValue = this.supportsValue;
			this.supportsValue = newValue;
			firePropertyChanged(SUPPORTS_VALUE, oldValue, newValue);
		}
		
		public boolean isNamed() {
			return isNamed;
		}
		
		public void setIsNamed(boolean newValue) {
			boolean oldValue = this.isNamed;
			this.isNamed = newValue;
			firePropertyChanged(IS_NAMED, oldValue, newValue);
		}
		
	}

	enum ArgumentType {NAMED_IN, NAMED_OUT, NAMED_IN_OUT, UNNAMED_IN, UNNAMED_OUT, UNNAMED_IN_OUT};
	
	enum ArgumentPassType {PARAMETER, VALUE};
	
}