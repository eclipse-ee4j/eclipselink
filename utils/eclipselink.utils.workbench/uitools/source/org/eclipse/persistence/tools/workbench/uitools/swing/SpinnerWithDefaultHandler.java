/*
 * Copyright (c) 2007, 2008, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.uitools.swing;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;

/**
 * This handler is responsible to update the text field by showing a default
 * value if one is defined and when the value holder's value is <code>null</code>.
 * The default value is either removed on focus gained or added on focus lost if
 * and only if the value holder's value is <code>null</code>. The default value
 * is shown as grayed out.
 * <p>
 * <b>Note:</b> <code>JSpinner</code> is poorly designed, which makes it harder
 * to handle the default value. This handler uses a lot of hackery code and
 * some flags.
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class SpinnerWithDefaultHandler
{
	/**
	 * The holder of the default value.
	 */
	private ValueModel defaultValueHolder;

	/**
	 * Flag used to determine how to convert the value, which is only required
	 * when installing our custom formatter.
	 */
	private boolean installingFormatter;

	/**
	 * Flag used to show an empty string when the spinner receives the focus,
	 * this happens by a mouse click on by the up or down buttons being used.
	 */
	private boolean showEmptyString;

	/**
	 * A custom <code>JSpinner</code> is created in order to prevent the model's
	 * value from being changed during the process of either showing the default
	 * value or clearing it.
	 */
	private JSpinner spinner;

	/**
	 * The text field that will be used to show the default value when the value
	 * holder contains <code>null</code>.
	 */
	private JFormattedTextField textField;

	/**
	 * Flag used to convert the number into the right string representation when
	 * the number holder is receiving a new value.
	 */
	private boolean valueBeingChanged;

	/**
	 * Constant used to determine if the value holder holds <code>null</code> In
	 * that case, the actual default value is used and shown accordingly.
	 */
	static final Number DEFAULT_VALUE = -1;

	/**
	 * Creates a new <code>SpinnerWithDefaultHandler</code>.
	 *
	 * @param subjectHolder The holder of the subject, which is used to either
	 * clear the text field or show the default value
	 * @param valueHolder The holder of the spinner's value
	 * @param defaultValueHolder The holder of the default value
	 * @param minimumValue The lower end of the allowed range
	 * @param maximumValue The higher end of the allowed range
	 * @param stepSize The increment used to increase or decrease the current
	 * value
	 */
	public SpinnerWithDefaultHandler(ValueModel subjectHolder,
	                                 PropertyValueModel valueHolder,
	                                 ValueModel defaultValueHolder,
	                                 Comparable<? extends Number> minimumValue,
	                                 Comparable<? extends Number> maximumValue,
	                                 Number stepSize)
	{
		super();

		initialize(subjectHolder,
		           valueHolder,
		           defaultValueHolder,
		           minimumValue,
		           maximumValue,
		           stepSize);
	}

	private PropertyChangeListener buildDefaultValuePropertyChangeListener()
	{
		return new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				spinnerModel().setDefaultValue((Number) e.getNewValue());
			}
		};
	}

	private FocusListener buildFocusListener()
	{
		return new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
				updateTextFieldOnFocusGained();
			}

			public void focusLost(FocusEvent e)
			{
				if (!e.isTemporary())
				{
					updateTextFieldOnFocusLost();
				}
			}
		};
	}

	private NumberFormatter buildFormatter(JSpinner.NumberEditor editor)
	{
		NumberFormatter oldformatter = (NumberFormatter) textField.getFormatter();
		NumberFormatter newFormatter = buildNumberFormatter(editor);

		newFormatter.setMaximum      (oldformatter.getMaximum());
		newFormatter.setMinimum      (oldformatter.getMinimum());
		newFormatter.setValueClass   (oldformatter.getValueClass());
		newFormatter.setOverwriteMode(oldformatter.getOverwriteMode());
		newFormatter.setAllowsInvalid(oldformatter.getAllowsInvalid());
		newFormatter.setCommitsOnValidEdit(true);

		return newFormatter;
	}

	private ChangeListener buildModelChangeListener()
	{
		return new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				updateTextFieldForegroundColor();
			}
		};
	}

	/**
	 * Creates a custom <code>NumberFormatter</code> that will correctly convert
	 * the value from string into a <code>Number</code> or vice versa and by
	 * handling the default value.
	 *
	 * @param editor The spinner's editor
	 * @return A new <code>NumberFormatter</code>
	 */
	private NumberFormatter buildNumberFormatter(JSpinner.NumberEditor editor)
	{
		return new NumberFormatter(editor.getFormat())
		{
			/**
			 * {@inheritDoc}
			 */
			@Override
			public Object stringToValue(String text) throws ParseException
			{
				// If the text is the default value, return DEFAULT_VALUE to not
				// change anything, this can happen when the focus is not moved
				// to the spinner yet and the up/down button was pressed
				if (defaultValue().equals(text))
				{
					return DEFAULT_VALUE;
				}

				// An empty string represent the "null" value so it can be pushed
				// into the number holder, if DEFAULT_VALUE was used, then the
				// number holder wouldn't be updated
				if (text.length() == 0)
				{
					return null;
				}

				// Leave the default implementation to convert
				// the number into a string representation
				return super.stringToValue(text);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String valueToString(Object value) throws ParseException
			{
				// While installing the formatter, we need to convert the
				// numberHolder's value and if the value is null then the default
				// value needs to be returned
				if (installingFormatter)
				{
					value = spinnerModel().getActualNumber();

					if (value == null)
					{
						value = DEFAULT_VALUE;
					}
				}
				// The spinner gained focus and the numberHolder's value is null,
				// we simply need to return an empty string so the user can start
				// typing a new value
				else if (showEmptyString)
				{
					return "";
				}
				// If the text field has the focus, use the text field's value
				// TODO
				// If the value is changed via the up/down buttons, then the value
				// won't be the same as the spinner model's value, which mean the
				// text field's text can't be used, the value needs to be converted
				// into its string reprensation so the text field can reflect the
				// new value
				else if (textField.hasFocus() && !valueBeingChanged)
				{
					return textField.getText();
				}

				// Convert the default value by using the defaultValueHolder's value
				// encapsulated by parenthesis
				if (value == DEFAULT_VALUE)
				{
					return defaultValue();
				}

				return super.valueToString(value);
			}
		};
	}

	private PropertyChangeListener buildPropertyChangeListener()
	{
		return new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				subjectChanged(e.getNewValue() == null);
			}
		};
	}

	/**
	 * Creates the spinner that will be used to show or hide the default value.
	 *
	 * @param model The spinner's model
	 * @return a new <code>JSpinner</code>
	 */
	protected JSpinner buildSpinner(SpinnerNumberModel model)
	{
		return new JSpinner(model);
	}

	/**
	 * Returns the default value stored in the default value holder.
	 *
	 * @return The default value returned by the default value holder,
	 * <code>null</code> is never returned
	 */
	private String defaultValue()
	{
		Number defaultValue = (Number)defaultValueHolder.getValue();

		// No default value specified
		if (defaultValue == null)
		{
			return "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(defaultValue);
		sb.append(')');
		return sb.toString();
	}

	/**
	 * Returns the spinner that was created and initialized to support showing a
	 * default value when the spinner does not have the focus and the value is
	 * <code>null</code>.
	 *
	 * @return The spinner supporting showing a default value
	 */
	public final JSpinner getSpinner()
	{
		return spinner;
	}

	/**
	 * Initializes a new <code>JSpinner</code> and registers the necessary
	 * behavior to allow showing the default value when the current value is
	 * <code>null</code>.
	 *
	 * @param subjectHolder The holder of the subject, which is used to either
	 * clear the text field or show the default value
	 * @param valueHolder The holder of the spinner's value
	 * @param defaultValueHolder The holder of the default value
	 * @param minimumValue The lower end of the allowed range
	 * @param maximumValue The higher end of the allowed range
	 * @param stepSize The increment used to increase or decrease the current
	 * value
	 */
	private void initialize(ValueModel subjectHolder,
	                        PropertyValueModel valueHolder,
	                        ValueModel defaultValueHolder,
	                        Comparable minimumValue,
	                        Comparable maximumValue,
	                        Number stepSize)
	{
		this.defaultValueHolder = defaultValueHolder;

		subjectHolder.addPropertyChangeListener
		(
			ValueModel.VALUE,
			buildPropertyChangeListener()
		);

		defaultValueHolder.addPropertyChangeListener
		(
			ValueModel.VALUE,
			buildDefaultValuePropertyChangeListener()
		);

		SpinnerModel model = new SpinnerModel
		(
			valueHolder,
			defaultValueHolder,
			minimumValue,
			maximumValue,
			stepSize
		);

		spinner = buildSpinner(model);
		model.addChangeListener(buildModelChangeListener());

		JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinner.getEditor();
		textField = editor.getTextField();
		textField.setFocusLostBehavior(JFormattedTextField.PERSIST);
		textField.addFocusListener(buildFocusListener());

		installNumberFormatter(valueHolder, editor);
		updateTextFieldForegroundColor();
	}

	/**
	 * Installs a custom <code>NumberFormatter</code> that will allow to
	 * correctly convert a <code>Number</code> into the appropriate string
	 * representation or from a string value into a <code>Number</code>.
	 *
	 * @param numberHolder The holder of the spinner's value
	 * @param editor The spinner's editor
	 */
	private void installNumberFormatter(ValueModel numberHolder,
	                                    JSpinner.NumberEditor editor)
	{
		try
		{
			installingFormatter = true;
			NumberFormatter newFormatter = buildFormatter(editor);

			// When initializing the spinner, the numberHolder was not hooked yet
			// which means the value set was -1
			if (numberHolder.getValue() != null)
			{
				textField.setValue(numberHolder.getValue());
			}

			textField.setFormatterFactory(new DefaultFormatterFactory(newFormatter));
		}
		finally
		{
			installingFormatter = false;
		}
	}

	private SpinnerModel spinnerModel()
	{
		return (SpinnerModel) spinner.getModel();
	}

	/**
	 * The subject has changed and if it's was nullified, then show the default
	 * value if required.
	 *
	 * @param nullSubject
	 */
	private void subjectChanged(boolean nullSubject)
	{
		if (nullSubject)
		{
			try
			{
				showEmptyString = true;

				textField.setValue(DEFAULT_VALUE);
			}
			finally
			{
				showEmptyString = false;
			}
		}
		else
		{
			spinnerModel().synchronize(spinnerModel().getActualNumber());
		}
	}

	/**
	 * Updates the foreground color based on spinner's value. If the value is the
	 * default value then the foreground color is changed to grey.
	 */
	private void updateTextFieldForegroundColor()
	{
		if (spinnerModel().getActualNumber() == null)
		{
			textField.setForeground(Color.GRAY);
		}
		else
		{
			textField.setForeground(UIManager.getColor("TextField.foreground"));
		}
	}

	/**
	 * Clears the default value if the text field contains it.
	 */
	private void updateTextFieldOnFocusGained()
	{
		if (spinnerModel().getActualNumber() == null)
		{
			try
			{
				showEmptyString = true;

				textField.setValue(DEFAULT_VALUE);
				textField.setForeground(UIManager.getColor("TextField.foreground"));
			}
			finally
			{
				showEmptyString = false;
			}
		}
	}

	/**
	 * Shows the default value if the text field contains an empty entry or the
	 * default value (-1), which was manually entered.
	 */
	private void updateTextFieldOnFocusLost()
	{
		String text = textField.getText();

		// If the current text is either the default value (-1), which was
		// manually entered or it'S blank, then we need to show the default value
		if ((text.length() == 0) ||
		     text.equals(DEFAULT_VALUE.toString()))
		{
			textField.setValue(DEFAULT_VALUE);
			textField.setForeground(Color.GRAY);
		}
	}

	/**
	 * This <code>SpinnerModel</code> adds the support to convert <code>null</code>
	 * to the default value properly and to update the numerous flags required
	 * for supporting the default value displayed as "(default_value)".
	 */
	private class SpinnerModel extends NumberSpinnerModelAdapter
	{
		/**
		 * The current value that is never <code>null</code>
		 */
		private Number value;

		/**
		 * Creates a new <code>SpinnerModel</code>.
		 *
		 * @param valueHolder The holder of the spinner's value
		 * @param defaultValueHolder The holder of the default value
		 * @param minimumValue The lower end of the allowed range
		 * @param maximumValue The higher end of the allowed range
		 * @param stepSize The increment used to increase or decrease the current
		 * value
		 */
		SpinnerModel(PropertyValueModel valueHolder,
		             ValueModel defaultValueHolde,
		             Comparable minimumValue,
		             Comparable maximumValue,
		             Number stepSize)
		{
			super(valueHolder,
			      minimumValue,
			      maximumValue,
			      stepSize,
			      (defaultValueHolder.getValue() != null) ? (Number)defaultValueHolder.getValue() : 0);

			synchronizeValue();
		}

		/**
		 * Returns the actual value, which is stored in the number holder.
		 *
		 * @return The current value, which can be <code>null</code>
		 */
		Object getActualNumber()
		{
			return numberHolder.getValue();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Number getNumber()
		{
			return value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Number getValue()
		{
			return value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void setDefaultValue(Number defaultValue)
		{
			super.setDefaultValue(defaultValue);

			// The current value is null, simply fire a state changed in order to
			// change the spinner's value and the text field's value
			if (value == DEFAULT_VALUE)
			{
				fireStateChanged();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setValue(Object value)
		{
			if ((value != DEFAULT_VALUE) && !installingFormatter)
			{
				try
				{
					valueBeingChanged = (value != null);
					showEmptyString   = (value == null);

					this.value = (value == null) ? DEFAULT_VALUE : (Number) value;
					super.setValue(value);
				}
				finally
				{
					valueBeingChanged = false;
					showEmptyString   = false;
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void synchronize(Object value) 
		{
			// The number holder can hold null but SpinnerModel doesn't handle
			// null, simply use a fake number
			this.value = (value == null) ? DEFAULT_VALUE : (Number)value;
			super.synchronize(this.value);

			// If the value is null and the value stored in SpinnerNumberModel
			// is DEFAULT_VALUE then no event will be fired, fire it ourself so
			// the spinner can be updated and show the default value
			if ((value == null) &&
			    (getSuperValue() == DEFAULT_VALUE))
			{
				fireStateChanged();
			}
		}

		private void synchronizeValue()
		{
			value = (Number)numberHolder.getValue();

			if (value == null)
			{
				value = DEFAULT_VALUE;
			}
		}
	}
}